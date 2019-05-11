/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.IbBitacoraDAO;
import com.bds.wpn.dao.PrestamoDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.MovimientoPrestamoDTO;
import com.bds.wpn.dto.PrestamoDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.fecha;
import static com.bds.wpn.util.BDSUtil.fecha2;
import static com.bds.wpn.util.BDSUtil.formatearMonto;
import com.bds.wpn.util.PaginarPDF;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * Controlador de prestamos
 *
 * @author rony.rodriguez
 */
@Named("wpnPrestamoController")
@SessionScoped
public class PrestamoController extends BDSUtil implements Serializable {

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbParametrosFacade parametrosFacade;

    @Inject
    InicioSesionController sesionController;

    @EJB
    PrestamoDAO prestamoDAO;

    @EJB
    IbBitacoraDAO ibBitacoraDAO;

    @EJB
    ClienteDAO clienteDAO;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;

    private PrestamoDTO prestamoDTO;
    private String prestamoSeleccionado;
    private String msjVacio = "";
    private Collection<PrestamoDTO> listPrestamoDTO;
    private Collection<CuentaDTO> cuentasCliente;                   //objeto que contiene las cuentas propias
    private String cuentaSeleccionada = "";                                     //Cuenta del Titular a Debitar Seleccionada extraido del formulario de prestamos
    private CuentaDTO datosCuentaSeleccionadaLista = null;          //objeto para almacenar los datos de la cuenta seleccionada de la lista

    /**
     * Obtiene el listado de los prestamos
     *
     * @return Collection<PrestamoDTO>
     */
    public Collection<PrestamoDTO> getListPrestamoDTO() {
        ClienteDTO cliente = prestamoDAO.listadoPrestamoPorCliente(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());

        if (cliente.getRespuestaDTO() != null) {
            if (!cliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formPrestamo:divDetPres", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else if (cliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && !cliente.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                infoMensaje(cliente.getRespuestaDTO().getTextoSP());
            }
        }

        if (cliente.getPrestamosClienteDTO() != null && !cliente.getPrestamosClienteDTO().isEmpty()) {
            for (int i = 0; i < cliente.getPrestamosClienteDTO().size(); i++) {
                cliente.getPrestamosClienteDTO().get(i).setSemilla(sesionController.getSemilla());

            }
        }
        return listPrestamoDTO = cliente.getPrestamosClienteDTO();
    }

    /**
     * Asigna el listado de los prestamos
     *
     * @param listPrestamoDTO Collection<PrestamoDTO>
     */
    public void setListPrestamoDTO(Collection<PrestamoDTO> listPrestamoDTO) {
        this.listPrestamoDTO = listPrestamoDTO;
    }

    /**
     * Obtiene el prestamo seleccionado
     *
     * @return String
     */
    public String getPrestamoSeleccionado() {
        return prestamoSeleccionado;
    }

    /**
     * Asigna el prestamo seleccionado
     *
     * @param prestamoSeleccionado String
     */
    public void setPrestamoSeleccionado(String prestamoSeleccionado) {
        this.prestamoSeleccionado = prestamoSeleccionado;
    }

    /**
     * Obtiene prestamoDTO
     *
     * @return prestamoDTO
     */
    public PrestamoDTO getPrestamoDTO() {
        return prestamoDTO;
    }

    /**
     * Obtener cuentas de clientes
     *
     * @return Collection<CuentaDTO> objeto que contiene las cuentas propias
     */
    public Collection<CuentaDTO> getCuentasCliente() {
        return cuentasCliente;
    }

    /**
     * Asigna objeto que contiene las cuentas propias
     *
     * @param cuentasCliente
     */
    public void setCuentasCliente(Collection<CuentaDTO> cuentasCliente) {
        this.cuentasCliente = cuentasCliente;
    }

    /**
     * Obtener Cuenta del Titular a Debitar Seleccionada extraido del formulario
     * del pago tdc
     *
     * @return String Cuenta del Titular a Debitar Seleccionada extraido del
     * formulario del pago tdc
     */
    public String getCuentaSeleccionada() {
        return cuentaSeleccionada;
    }

    /**
     * Insertar Cuenta del Titular a Debitar Seleccionada extraido del
     * formulario del pago tdc
     *
     * @param cuentaSeleccionada Cuenta del Titular a Debitar Seleccionada
     * extraido del formulario del pago tdc
     */
    public void setCuentaSeleccionada(String cuentaSeleccionada) {
        this.cuentaSeleccionada = cuentaSeleccionada;
        this.datosCuentaSeleccionadaLista = this.cuentaPropiaSeleccionadaCompleta(cuentaSeleccionada);
    }

    /**
     * Obtiene los datos de cuenta seleccionada
     *
     * @return CuentaDTO para almacenar los datos de la cuenta seleccionada de
     * la lista
     */
    public CuentaDTO getDatosCuentaSeleccionadaLista() {
        return datosCuentaSeleccionadaLista;
    }

    /**
     * Asigna objeto para almacenar los datos de la cuenta seleccionada de la
     * lista
     *
     * @param datosCuentaSeleccionadaLista CuentaDTO
     */
    public void setDatosCuentaSeleccionadaLista(CuentaDTO datosCuentaSeleccionadaLista) {
        this.datosCuentaSeleccionadaLista = datosCuentaSeleccionadaLista;
    }

    /**
     * metodo que se sencarga de retornar los datos completos de una cuenta
     * propia
     *
     * @param nroCuenta numero de la cuenta
     * @return CuentaDTO
     */
    public CuentaDTO cuentaPropiaSeleccionadaCompleta(String nroCuenta) {
        CuentaDTO cuentaTemp = new CuentaDTO(this.sesionController.getSemilla());
        for (CuentaDTO cuenta : cuentasCliente) {
            if (cuenta.getNumeroCuenta().equals(nroCuenta)) {
                cuentaTemp = cuenta;
                break;
            }
        }
        return cuentaTemp;
    }

    /**
     * metodo para validar la cuenta propia seleccionada
     *
     * @param context contexto de la aplicacion
     * @param component componente
     * @param value valor
     * @throws ValidatorException
     */
    public void validarCtaPropia(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().isEmpty() || value.equals("") || value.equals("-1")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.requerido", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.requerido", sesionController.getNombreCanal()).getMensajeUsuario()));
        }
    }

    /**
     * Metodo que obtiene el Detalle del Prestamo
     *
     * @param nroPrestamo
     * @throws IOException
     */
    public void obtenerDetallePrestamo(String nroPrestamo) throws IOException {

        setPrestamoSeleccionado(nroPrestamo);
        PrestamoDTO prestamoTemp = new PrestamoDTO();
        prestamoTemp.setSemilla(sesionController.getSemilla());
        boolean validacionesSeguridad = sesionController.productosCliente(prestamoTemp.getDesEnc().desencriptar(nroPrestamo));
        if (validacionesSeguridad) {
            //obtenemos el detalle del prestamo
            prestamoDTO = prestamoDAO.obtenerDetallePrestamo(prestamoTemp.getDesEnc().desencriptar(nroPrestamo), sesionController.getIdCanal(), sesionController.getNombreCanal());

            //ajustar la tarjeta que viene de entrada
            prestamoTemp = prestamoDAO.listadoPagosPrestamo(prestamoTemp.getDesEnc().desencriptar(nroPrestamo), null, null,
                    parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.orden.desc", sesionController.getIdCanal()),
                    sesionController.getNombreCanal(),
                    sesionController.getIdCanal());
            prestamoTemp.setSemilla(sesionController.getSemilla());
            //mensajes
            if (prestamoTemp.getMovimientos() == null) {
                prestamoDTO.setMovimientos(new ArrayList<MovimientoPrestamoDTO>());
            } else {
                prestamoDTO.setMovimientos(prestamoTemp.getMovimientos());
            }

        } else {
            RespuestaDTO respuesta = new RespuestaDTO();
            respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
            respuesta.setDescripcionSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
            prestamoDTO.setRespuestaDTO(respuesta);
        }

        if (!prestamoDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !prestamoDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formPrestamo:divDetPres", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        } else if (prestamoDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && !prestamoDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formPrestamo:divDetPres", new FacesMessage(FacesMessage.SEVERITY_WARN, "", prestamoDTO.getRespuestaDTO().getTextoSP()));
        }

        if (!prestamoDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formPrestamo:divDetPres", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        }

        //Registro en Bitacora
        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_DET_PRESTAMO, "", "", "Consulta Detalle de Prestamo", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);

        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.prestamo.detalle.url", sesionController.getIdCanal()));

    }

    /* Asigna el prestamo
     * @param prestamoDTO PrestamoDTO
     */
    public void setPrestamoDTO(PrestamoDTO prestamoDTO) {
        this.prestamoDTO = prestamoDTO;
    }

    /**
     * Metodo que imprime mensaje en el xhtml (para ser usado por el componente
     * message de primefaces)
     *
     * @param mensaje String
     */
    public void infoMensaje(String mensaje) {
        this.limipiarMensajesFacesContext();
        FacesContext.getCurrentInstance().addMessage("formPrestamo:divDetPres", new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mensaje));
    }

    /**
     * Metodo para actualizar los prestamos del seleccionar alguno
     *
     * @param event AjaxBehaviorEvent
     * @throws java.io.IOException
     */
    public void actDetallePrestamo(AjaxBehaviorEvent event) throws IOException {

        PrestamoDTO prestamoTemp = new PrestamoDTO();
        prestamoTemp.setSemilla(sesionController.getSemilla());
        //obtenemos el detalle del prestamo
        prestamoDTO.setSemilla(sesionController.getSemilla());
        boolean validacionesSeguridad = sesionController.productosCliente(prestamoSeleccionado);
        if (validacionesSeguridad) {
            prestamoDTO = prestamoDAO.obtenerDetallePrestamo(getPrestamoSeleccionado(), sesionController.getIdCanal(), sesionController.getNombreCanal());
            prestamoDTO.setSemilla(sesionController.getSemilla());

            //ajustar la tarjeta que viene de entrada
            prestamoTemp = prestamoDAO.listadoPagosPrestamo(getPrestamoSeleccionado(), null, null,
                    parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.orden.desc", sesionController.getIdCanal()),
                    sesionController.getNombreCanal(),
                    sesionController.getIdCanal());
            prestamoTemp.setSemilla(sesionController.getSemilla());
            //mensajes
            if (prestamoTemp.getMovimientos() == null) {
                prestamoDTO.setMovimientos(new ArrayList<MovimientoPrestamoDTO>());
            } else {
                prestamoDTO.setMovimientos(prestamoTemp.getMovimientos());
            }

        } else {
            RespuestaDTO respuesta = new RespuestaDTO();
            respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
            respuesta.setDescripcionSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
            prestamoDTO.setRespuestaDTO(respuesta);
        }

        if (!prestamoDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formPrestamo:divDetPres", new FacesMessage(FacesMessage.SEVERITY_WARN, "", prestamoDTO.getRespuestaDTO().getTextoSP()));
        } else if (!prestamoDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext.getCurrentInstance().addMessage("formPrestamo:divDetPres", new FacesMessage(FacesMessage.SEVERITY_WARN, "", prestamoDTO.getRespuestaDTO().getTextoSP()));
        }

        //Registro en Bitacora
        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_DET_PRESTAMO, "", "", "Consulta Detalle de Prestamo", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.prestamo.detalle.url", sesionController.getIdCanal()));

    }

    /**
     * Metodo para consultar las cuentas propias
     */
    public void consultarCuentasPropias() {

        ClienteDTO datosCliente = clienteDAO.listadoCuentasClientes(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (!datosCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //manejar mensaje de error 
        } else {
            if (datosCliente.getCuentasAhorroCorrienteDTO().size() > 0) {
                if (datosCliente.getCuentasAhorroDTO() != null && !datosCliente.getCuentasAhorroDTO().isEmpty()) {
                    for (int i = 0; i < datosCliente.getCuentasAhorroDTO().size(); i++) {
                        datosCliente.getCuentasAhorroDTO().get(i).setSemilla(sesionController.getSemilla());
                    }
                }
                if (datosCliente.getCuentasCorrienteDTO() != null && !datosCliente.getCuentasCorrienteDTO().isEmpty()) {
                    for (int i = 0; i < datosCliente.getCuentasCorrienteDTO().size(); i++) {
                        datosCliente.getCuentasCorrienteDTO().get(i).setSemilla(sesionController.getSemilla());
                    }
                }
                this.cuentasCliente = datosCliente.getCuentasAhorroCorrienteDTO();
            }
        }
    }

    /**
     * metodo para cargar las listas iniciales para Propias
     */
    public void consultaInicialPropias() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            consultarCuentasPropias();
        }
    }

    ////////////////////SECCION  DE REPORTES//////////////////////////
    /**
     * metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     */
    public void cuerpoDetallePrestamoPDF(Document pdf) {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 14, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            PrestamoDTO prestamoTemp = new PrestamoDTO(sesionController.getSemilla());
            //Variables para el manejo de los Saldos
            String saldoCapital;
            String montoCuota;
            String montoOperacion = "0.0";
            String capital = "0.0";
            String intereses = "0.0";
            String gastos = "0.0";
            String mora = "0.0";
            String seguros = "0.0";

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(PrestamoController.class.getResource("/imgPDF/logoBanner.png"));

            //Transparencia de la imagen
            image.setTransparency(new int[]{0x00, 0x10});
            image.scaleAbsolute(140f, 40f);
            PdfPCell cellImage = new PdfPCell();
            cellImage.setRowspan(2);
            cellImage.setBorder(0);
            cellImage.addElement(new Chunk(image, 2, -2));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas = {0.97f, 2.75f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas);
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setBorderColor(Color.WHITE);
            table.addCell(cellImage);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            //Alineamos fecha y usuario a la derecha
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell fecha = new PdfPCell(new Phrase(fecha2(new Date()), fontTituloBold2));
            PdfPCell usuario = new PdfPCell(new Phrase(sesionController.getUsuario().getNombre(), fontTituloBold2));

            //Establecemos espaciado a la derecha
            fecha.enableBorderSide(Rectangle.RIGHT);
            usuario.enableBorderSide(Rectangle.RIGHT);
            fecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
            usuario.setHorizontalAlignment(Element.ALIGN_RIGHT);
            fecha.setBorder(0);
            usuario.setBorder(0);
            table.addCell(fecha);
            table.addCell(usuario);

            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            table = new PdfPTable(5);

            //Le asigno el tamanio a la cada una de las celdas de acuerdo con la cantidad de columnas que especifico arriba
            float[] medidaCeldasTitulos = {0.55f, 0.95f, 0.55f, 0.55f, 0.55f};

            // Asigno las medidas a las tabla (El ancho)
            table.setWidths(medidaCeldasTitulos);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            //Título que contiene el Nombre y el Número del Producto
            String titulo;
            if (prestamoDTO.getMovimientos() == null || prestamoDTO.getMovimientos().isEmpty()) {
                titulo = "Detalle de Préstamo";
            } else {
                titulo = prestamoDTO.getNombreProducto() + " " + formatoAsteriscosWeb(prestamoDTO.getNumeroPrestamo());
            }

            PdfPCell cell = new PdfPCell(new Phrase(titulo, fontTituloBold));
            cell.setPaddingBottom(5);
            cell.setColspan(5);
            cell.setBorder(0);
            table.addCell(cell);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Título de las columnas
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.tipoPrestamo", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.nroPrestamo", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.fechaLiquidacion", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.tasa", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.fechaVencimiento", sesionController.getIdCanal()), fontBold));

            //Contenido de las columnas, data
            table.getDefaultCell().setBackgroundColor(null);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase((prestamoDTO.getCodigoTipoProducto() != null ? prestamoDTO.getCodigoTipoProducto() : ""), font));
            table.addCell(new Phrase((prestamoDTO.getNumeroPrestamo() != null ? prestamoDTO.getNumeroPrestamo() : ""), font));
            table.addCell(new Phrase((prestamoDTO.getFechaLiquidacionDate() != null ? fecha(prestamoDTO.getFechaLiquidacionDate()) : ""), font));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase((prestamoDTO.getTasa() != null ? prestamoDTO.getTasa().toString() : ""), font));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase((prestamoDTO.getFechaVencimientoDate() != null ? fecha(prestamoDTO.getFechaVencimientoDate()) : ""), font));

            //Agregamos la tabla al PDF
            pdf.add(table);

            table = new PdfPTable(5);

            float[] medidaCeldasTitulos2 = {0.55f, 0.95f, 0.55f, 0.55f, 0.55f};

            // Asigno las medidas a las tabla (El ancho)
            table.setWidths(medidaCeldasTitulos2);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            cell.setColspan(4);
            cell.setBorder(0);

            //título de las columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.fechaProxPago", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.montoAprobado", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.saldoActual", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.saldoCapital", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.montoCuota", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(null);

            table.addCell(new Phrase((prestamoDTO.getFechaProximoPagoDate() != null ? fecha(prestamoDTO.getFechaProximoPagoDate()) : ""), font));
            table.addCell(new Phrase((prestamoDTO.getFechaProximoPagoDate() != null ? fecha(prestamoDTO.getFechaProximoPagoDate()) : ""), font));
            table.addCell(new Phrase((prestamoDTO.getFechaProximoPagoDate() != null ? fecha(prestamoDTO.getFechaProximoPagoDate()) : ""), font));

            if (prestamoDTO.getSaldoActual() != null) {
                saldoCapital = formatearMonto(prestamoDTO.getSaldoActual());
            } else {
                saldoCapital = "0.0";
            }

            if (prestamoDTO.getMontoCuota() != null) {
                montoCuota = formatearMonto(prestamoDTO.getMontoCuota());
            } else {
                montoCuota = "0.0";
            }

            //Contenido de las columnas
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase(saldoCapital, font));
            table.addCell(new Phrase(montoCuota, font));

            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            //Asignamos celdas a la tabla, 8 columnas
            table = new PdfPTable(8);
            //Le asigne el tamanio a la cada una de las celdas de acuerdo con la cantidad de columnas que especifico arriba
            float[] medidaCeldasSaldos = {0.35f, 0.55f, 0.35f, 0.35f, 0.35f, 0.35f, 0.35f, 0.35f};

            // Asigno las medidas a las tabla (El ancho)
            table.setWidths(medidaCeldasSaldos);

            //Aplicamos espaciado entre el texto y la celda
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            // PdfPCell tituloMovimientos = new PdfPCell(new Phrase("DETALLES DE MOVIMIENTOS", fontTituloBold));
            //   table.addCell(tituloMovimientos);
            //   cell.setColspan(3);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

            //Títulos de las columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.nroCuota", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.fechaPago", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.montoOperacion", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.capital", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.intereses", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.gastos", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.mora", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detPrestamo.descargaPdf.seguro", sesionController.getIdCanal()), fontBold));

            //Contenido de las columnas, recorremos los registros recuperados de movimientos
            table.getDefaultCell().setBackgroundColor(null);

           
            prestamoTemp = prestamoDTO;

            //Validamos que existan movimientos para mostrar
            if (prestamoTemp.getMovimientos() == null || prestamoTemp.getMovimientos().isEmpty()) {
                table.getDefaultCell().setColspan(8);
                table.getDefaultCell().setPaddingTop(3);
                table.getDefaultCell().setPaddingBottom(3);
                table.getDefaultCell().setPaddingRight(3);
                table.getDefaultCell().setPaddingLeft(3);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.dataTable.texto.vacio", sesionController.getIdCanal()), font));

            } else {
                //Se recorren los movimientos recuperados para armar la data
                for (MovimientoPrestamoDTO mov : prestamoTemp.getMovimientos()) {
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(new Phrase((mov.getNumeroCuota() != null ? mov.getNumeroCuota().toString() : ""), font));
                    table.addCell(new Phrase((mov.getFechaDePagoDate() != null ? fecha(mov.getFechaDePagoDate()) : ""), font));

                    if (mov.getMoraPagada()!= null) {
                        montoOperacion = formatearMonto(mov.getMoraPagada());
                    } else {
                        montoOperacion = "0.0";
                    }
                    if (mov.getCapital() != null) {
                        capital = formatearMonto(mov.getCapital());
                    } else {
                        capital = "0.0";
                    }
                    if (mov.getInteresesPagado()!= null) {
                        intereses = formatearMonto(mov.getInteresesPagado());
                    } else {
                        intereses = "0.0";
                    }
                    if (mov.getIntereses()!= null) {
                        gastos = formatearMonto(mov.getIntereses());
                    } else {
                        gastos = "0.0";
                    }
                    if (mov.getMora()!= null) {
                        mora = formatearMonto(mov.getMora());
                    } else {
                        mora = "0.0";
                    }                    if (mov.getSeguro()!= null) {
                        seguros = formatearMonto(mov.getSeguro());
                    } else {
                       seguros = "0.0";
                    }
                    
                    
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);

                    table.addCell(new Phrase(montoOperacion, font));
                    table.addCell(new Phrase(capital, font));
                   table.addCell(new Phrase(intereses, font));
                    table.addCell(new Phrase(gastos, font));
                   table.addCell(new Phrase(mora, font));
                   table.addCell(new Phrase(seguros, font));
                }
            }

            //Añadimos la tabla 
            // pdf.setMargins(150f, 20f, 50f, 10f);
            pdf.add(table);
            pdf.setMargins(25f, 3f, 30f, 25f);
            pdf.add(new Paragraph(" "));
        } catch (Exception e) {
            //manejar excepciones de reportes
        }

    }

    /**
     * metodo encargado de armar el reportes de listado del Perfil
     * PerfilBKUsuarios en PDF
     *
     * @throws java.io.IOException
     * @throws com.lowagie.text.DocumentException
     */
    public void detallePrestamoPDF() throws IOException, DocumentException {

        try {

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-disposition", "attachment;filename=\"DetalleDePrestamo.pdf\"");
            Document document = new Document(PageSize.A4.rotate());//(), 20, 20, 20, 20);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            //Instanciamos la clase utilitaria que realiza la paginacion
            PaginarPDF paginar = new PaginarPDF();
            Rectangle headerBox = new Rectangle(22, 45, 1320, 700);
            /**/
            //Establecemos el tamanio del documento
            writer.setBoxSize("headerBox", headerBox);
            //Pasamos por parametros los eventos que se producen al generar el PDF
            writer.setPageEvent(paginar);
            /**/
            document.open();
            //Invocamos el metodo que genera el PDF
            this.cuerpoDetallePrestamoPDF(document);

            document.close();

            response.setContentLength(baos.size());
            ServletOutputStream out = response.getOutputStream();
            baos.writeTo(out);
            baos.flush();
            faces.responseComplete();

        } catch (Exception e) {
        }
    }

}
