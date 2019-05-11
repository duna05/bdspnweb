/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.ReferenciaDAO;
import com.bds.wpn.dao.TarjetaCreditoDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.ReferenciaDTO;
import com.bds.wpn.dto.ReferenciaDetalleDTO;
import com.bds.wpn.dto.TarjetasCreditoDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import com.bds.wpn.util.PaginarPDF;
import com.lowagie.text.BadElementException;
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
import java.util.Calendar;
import java.util.Collection;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wilmer.rondon
 */
@Named("wpnReferenciaBancariaController")
@SessionScoped
public class ReferenciaBancariaController extends BDSUtil implements Serializable {

    @EJB
    IbParametrosFacade parametrosFacade;

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    TarjetaCreditoDAO tarjetaCreditoDAO;

    @EJB
    ReferenciaDAO referenciaDAO;

    @EJB
    IbTextosFacade ibTextosFacade;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;

    private String tipoRef = "U";
    private String tipoDest = "1";
    private String cuentaSeleccionada;
    private CuentaDTO cuentaSeleccionadaDTO;                         //objeto para almacenar los datos de la cuenta seleccionada de la lista
    private String TDCSeleccionada;
    private TarjetasCreditoDTO TDCSeleccionadaDTO;                  //objeto para almacenar los datos de la TDC seleccionada de la lista
    private String nombreDest;
    private boolean tipoRefIndividual = true;
    private boolean tipoDestNombreDest = false;
    private boolean refCuenta;
    private boolean RefTdc;
    private Collection<CuentaDTO> cuentasCliente;                   //objeto que contiene las cuentas propias
    private Collection<TarjetasCreditoDTO> tdcCliente;              //objeto que contiene las TDC propias
    private String nroCuenta;
    private String nroTarjeta;
    private ReferenciaDTO referencia;

    public ReferenciaDTO getReferencia() {
        return referencia;
    }

    public void setReferencia(ReferenciaDTO referencia) {
        this.referencia = referencia;
    }

    public String getTipoRef() {
        return tipoRef;
    }

    public void setTipoRef(String tipoRef) {
        this.tipoRef = tipoRef;
    }

    public String getTipoDest() {
        return tipoDest;
    }

    public void setTipoDest(String tipoDest) {
        this.tipoDest = tipoDest;
    }

    public String getCuentaSeleccionada() {
        return cuentaSeleccionada;
    }

    public void setCuentaSeleccionada(String cuentaSeleccionada) {

        this.cuentaSeleccionada = cuentaSeleccionada;
        if (cuentaSeleccionada != null) {
            if (cuentaSeleccionadaDTO == null || cuentaSeleccionadaDTO.getSemilla() == null || cuentaSeleccionadaDTO.getSemilla().trim().equalsIgnoreCase("")) {
                this.cuentaSeleccionadaDTO = new CuentaDTO(sesionController.getSemilla());
            }
            this.cuentaSeleccionadaDTO = this.cuentaPropiaSeleccionadaCompleta(this.cuentaSeleccionadaDTO.getDesEnc().desencriptar(cuentaSeleccionada));
        }
    }

    public String getTDCSeleccionada() {
        return TDCSeleccionada;
    }

    public void setTDCSeleccionada(String TDCSeleccionada) {
        this.TDCSeleccionada = TDCSeleccionada;

        if (TDCSeleccionada != null) {
            if (TDCSeleccionadaDTO == null || TDCSeleccionadaDTO.getSemilla() == null || TDCSeleccionadaDTO.getSemilla().trim().equalsIgnoreCase("")) {
                this.TDCSeleccionadaDTO = new TarjetasCreditoDTO(sesionController.getSemilla());
            }
            this.TDCSeleccionadaDTO = this.tdcPropiaSeleccionadaCompleta(this.TDCSeleccionadaDTO.getDesEnc().desencriptar(TDCSeleccionada));
        }

    }

    public String getNombreDest() {
        return nombreDest;
    }

    public void setNombreDest(String nombreDest) {
        this.nombreDest = nombreDest;
    }

    public boolean isTipoRefIndividual() {
        return tipoRefIndividual;
    }

    public void setTipoRefIndividual(boolean tipoRefIndividual) {
        this.tipoRefIndividual = tipoRefIndividual;
    }

    public boolean isTipoDestNombreDest() {
        return tipoDestNombreDest;
    }

    public void setTipoDestNombreDest(boolean tipoDestNombreDest) {
        this.tipoDestNombreDest = tipoDestNombreDest;
    }

    public boolean isRefCuenta() {
        return refCuenta;
    }

    public void setRefCuenta(boolean refCuenta) {
        this.refCuenta = refCuenta;
    }

    public boolean isRefTdc() {
        return RefTdc;
    }

    public void setRefTdc(boolean RefTdc) {
        this.RefTdc = RefTdc;
    }

    public Collection<CuentaDTO> getCuentasCliente() {
        this.consultarCuentasPropias();
        return cuentasCliente;
    }

    public void setCuentasCliente(Collection<CuentaDTO> cuentasCliente) {
        this.cuentasCliente = cuentasCliente;
    }

    /**
     * Obtener el listado de las TDC del cliente
     *
     * @return Collection<TarjetasCreditoDTO>
     */
    public Collection<TarjetasCreditoDTO> getTdcCliente() {
        this.consultarTDCPropias();
        return tdcCliente;
    }

    /**
     * Asignar la TDC del cliente
     *
     * @param tdcCliente
     */
    public void setTdcCliente(Collection<TarjetasCreditoDTO> tdcCliente) {
        this.tdcCliente = tdcCliente;
    }

    public CuentaDTO getCuentaSeleccionadaDTO() {
        return cuentaSeleccionadaDTO;
    }

    public void setCuentaSeleccionadaDTO(CuentaDTO cuentaSeleccionadaDTO) {
        this.cuentaSeleccionadaDTO = cuentaSeleccionadaDTO;
    }

    public TarjetasCreditoDTO getTDCSeleccionadaDTO() {
        return TDCSeleccionadaDTO;
    }

    public void setTDCSeleccionadaDTO(TarjetasCreditoDTO TDCSeleccionadaDTO) {
        this.TDCSeleccionadaDTO = TDCSeleccionadaDTO;
    }

    public String getNroCuenta() {
        return nroCuenta;
    }

    public void setNroCuenta(String nroCuenta) {
        this.nroCuenta = nroCuenta;
    }

    public String getNroTarjeta() {
        return nroTarjeta;
    }

    public void setNroTarjeta(String nroTarjeta) {
        this.nroTarjeta = nroTarjeta;
    }

    /**
     * Metodo ejecutado desde el ajax de los radio de tipo de referencia el cual
     * activa variables que son usadas desde el rendered de la vista
     */
    public void accionTipoReferencia() {
        if (tipoRef.equalsIgnoreCase("U")) {
            tipoRefIndividual = true;
            cuentaSeleccionada = "";
            TDCSeleccionada = "";
        } else if (tipoRef.equalsIgnoreCase("T")) {
            tipoRefIndividual = false;
        }
    }

    /**
     * Metodo ejecutado desde el ajax de los radio de tipo de destinatario el
     * cual activa variables que son usadas desde el rendered de la vista
     */
    public void accionTipoDestinatario() {
        if (tipoDest.equalsIgnoreCase("1")) {
            tipoDestNombreDest = false;
        } else if (tipoDest.equalsIgnoreCase("2")) {
            tipoDestNombreDest = true;
            nombreDest = "";
        }
    }

    /**
     * Metodo para consultar las cuentas propias
     */
    public void consultarCuentasPropias() {

        if (cuentasCliente == null) {

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
    }

    /**
     * Consulta TDC propias
     */
    public void consultarTDCPropias() {
        //se limpian las afiliaciones               
        ClienteDTO datosCliente = tarjetaCreditoDAO.listadoTdcPropias(sesionController.getUsuario().getDocumento(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        //se valida el objeto de respuesta
        if (!datosCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //manejar mensaje de error 
        } else {
            //se consultan las tdc propias para dicha transaccion
            if (datosCliente.getTdcAsociadasClienteDTO().size() > 0) {

                if (!datosCliente.getTdcAsociadasClienteDTO().isEmpty()) {
                    for (int i = 0; i < datosCliente.getTdcAsociadasClienteDTO().size(); i++) {

                        //por cada TDC valida del cliente se consulta su detalle 
                        datosCliente.getTdcAsociadasClienteDTO().set(i, tarjetaCreditoDAO.obtenerDetalleTDC(datosCliente.getTdcAsociadasClienteDTO().get(i).getNumeroTarjeta(), sesionController.getIdCanal(), sesionController.getNombreCanal()));
                        datosCliente.getTdcAsociadasClienteDTO().get(i).setSemilla(sesionController.getSemilla());
                    }
                }
                this.tdcCliente = datosCliente.getTdcAsociadasClienteDTO();
            }
            //manejar un mensaje para el caso de no poseer afiliaciones registradas
        }

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
     * Metodo que se sencarga de retornar los datos completos de una TDC PROPIA
     * destino
     *
     * @param nroTDC String
     * @return TarjetasCreditoDTO
     */
    public TarjetasCreditoDTO tdcPropiaSeleccionadaCompleta(String nroTDC) {
        TarjetasCreditoDTO tdcTemp = new TarjetasCreditoDTO();
        for (TarjetasCreditoDTO tdc : tdcCliente) {
            if (tdc.getNumeroTarjeta().equals(nroTDC)) {
                tdcTemp = tdc;
                break;
            }
        }
        return tdcTemp;
    }

    public void referenciaBancariaPaso1() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.referenciaBancaria.url.paso1", sesionController.getIdCanal()));
    }

    public void referenciaBancariaPaso2() {

        this.nroCuenta = null;
        this.nroTarjeta = null;

        if (tipoRefIndividual) {

            if (cuentaSeleccionada == null && TDCSeleccionada == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formReferenciaBancariaPaso1:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.tipoRefIndiv", sesionController.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.referenciaBancaria.url.paso1", sesionController.getIdCanal()));
                return;

            } else if (cuentaSeleccionada != null && TDCSeleccionada != null) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formReferenciaBancariaPaso1:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.tipoRefIndiv", sesionController.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.referenciaBancaria.url.paso1", sesionController.getIdCanal()));
                return;
            } else {
                refCuenta = false;
                RefTdc = false;
                CuentaDTO cuentaDTO = new CuentaDTO();
                cuentaDTO.setSemilla(sesionController.getSemilla());
                String producto = null;

                if (cuentaSeleccionada != null) {
                    refCuenta = true;
                    producto = cuentaSeleccionada;
                    this.nroCuenta = this.cuentaSeleccionadaDTO.getNumeroCuenta();
                } else if (TDCSeleccionada != null) {
                    RefTdc = true;
                    producto = TDCSeleccionada;
                    this.nroTarjeta = TDCSeleccionadaDTO.getNumeroTarjeta();
                }

                boolean validacionesSeguridad = sesionController.productosCliente(cuentaDTO.getDesEnc().desencriptar(producto));

                if (!validacionesSeguridad) {
                    FacesContext.getCurrentInstance().addMessage("formReferenciaBancariaPaso1:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.referenciaBancaria.url.paso1", sesionController.getIdCanal()));
                    return;
                }
            }
        }

        if (tipoDest.equalsIgnoreCase("1")) {
            nombreDest = textosController.getNombreTexto("pnw.referenciaBancaria.campo.tipoDest.todos", sesionController.getNombreCanal());
        }

        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.referenciaBancaria.url.paso2", sesionController.getIdCanal()));
        return;
    }

    public void referenciaBancariaPaso3() {

        if (TDCSeleccionada != null) {
            referencia = referenciaDAO.obtenerReferenciaTDC(sesionController.getUsuario().getCodUsuario(), this.nroTarjeta, nombreDest, sesionController.getNombreCanal());
        } else {
            referencia = referenciaDAO.obtenerDatosReferencias(sesionController.getUsuario().getCodUsuario(), tipoRef, this.nroCuenta, nombreDest, sesionController.getNombreCanal());
        }

        if (!referencia.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formReferenciaBancariaPaso2:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.referenciaBancaria.url.paso2", sesionController.getIdCanal()));
            return;
        } else if (!referencia.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formReferenciaBancariaPaso2:divMensaje", new FacesMessage(FacesMessage.SEVERITY_WARN, referencia.getRespuestaDTO().getTextoSP(), referencia.getRespuestaDTO().getTextoSP()));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.referenciaBancaria.url.paso2", sesionController.getIdCanal()));
            return;
        }

        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_REF_BANCARIA, "", "", "Generación de Referencia Bancaria", "", "", "", "", "", "", "");
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);

        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.referenciaBancaria.url.paso3", sesionController.getIdCanal()));
        return;

    }

    ////////////////////SECCION  DE REPORTES//////////////////////////
    /**
     * metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     * @param writer
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoReferenciaBancariaTDCPDF2(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilos de Fuente
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(1);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(ReferenciaBancariaController.class.getResource("/imgPDF/logoBanner.png"));

            //Transparencia de la imagen
            image.setTransparency(new int[]{0x00, 0x10});
            image.scaleAbsolute(140f, 40f);
            PdfPCell cellImage = new PdfPCell();
            cellImage.setRowspan(2);
            cellImage.setBorder(0);
            cellImage.addElement(new Chunk(image, 2, -2));

            ///////////////////Imagen Firma///////////////////////
            Image imagefirma = null;
            String nroReferencia = null;
            String fechaInicio = null;
            String tdc = null;
            String cifras = null;
            String tipoCuenta = null;
            for (ReferenciaDetalleDTO i : referencia.getReferenciasCuentas()) {
                imagefirma = Image.getInstance(i.getFirmaBlob());
                nroReferencia = i.getNroReferencia();
                fechaInicio = formatearFecha(i.getFechaInicioDate(), FORMATO_FECHA_SIMPLE);
                tdc = i.getNumeroCuenta();
                cifras = i.getCifrasReferencia();
                tipoCuenta = i.getTipoCuenta();
                break;
            }

            //Transparencia de la imagen
            imagefirma.setTransparency(new int[]{0x00, 0x10});
            imagefirma.scaleAbsolute(160f, 60f);
            PdfPCell cellImageFirma = new PdfPCell();
            cellImageFirma.setRowspan(2);
            cellImageFirma.setBorder(0);
            cellImageFirma.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellImageFirma.setPaddingLeft(125f);
            cellImageFirma.addElement(new Chunk(imagefirma, 2, -2));

            float[] medidaCeldas = {6.75f};

            //Asigno las medidas a la tabla (El ancho)
            //   table.setWidths(medidaCeldas);
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
            PdfPCell cell1 = new PdfPCell(new Phrase(" ", font));
            PdfPCell cell2 = new PdfPCell(new Phrase(" ", font));
            cell1.setBorder(0);
            cell2.setBorder(0);
            table.addCell(cell1);
            table.addCell(cell2);

            pdf.add(table);

            table = new PdfPTable(1);

            if (tipoDest.equalsIgnoreCase("1")) {
                PdfPCell interesar = new PdfPCell(new Phrase("A QUIEN PUEDA INTERESAR", font));
                //Establecemos espaciado a la derecha
                interesar.setPaddingLeft(165f);
                interesar.setPaddingTop(65f);
                interesar.setBorder(0);
                table.addCell(interesar);

            } else if (tipoDest.equalsIgnoreCase("2")) {

                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                PdfPCell titulo1 = new PdfPCell(new Phrase("SEÑORES:", font));
                PdfPCell destinatario = new PdfPCell(new Phrase(nombreDest, font));
                PdfPCell titulo2 = new PdfPCell(new Phrase("Presente.-", font));

                //Establecemos espaciado a la derecha
                titulo1.setPaddingLeft(5f);
                titulo2.setPaddingLeft(5f);
                destinatario.setPaddingLeft(25f);
                titulo1.setPaddingTop(55f);
                titulo2.setPaddingTop(15f);
                destinatario.setPaddingTop(10f);
                titulo1.setBorder(0);
                destinatario.setBorder(0);
                titulo2.setBorder(0);
                table.addCell(titulo1);
                table.addCell(destinatario);
                table.addCell(titulo2);
            }

            pdf.add(table);

            table = new PdfPTable(1);

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas);

            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            table.getDefaultCell().setFixedHeight(50f);
            PdfPCell parrafo1 = new PdfPCell(new Phrase("Por medio de la presente se hace constar que el(la) Sr(a)."
                    + " " + sesionController.getUsuario().getNombre() + ", portador(a) de la cédula de identidad número "
                    + sesionController.getUsuario().getTipoDoc() + "-" + sesionController.getUsuario().getDocumento() + ", es "
                    + "titular de la " + tipoCuenta + " número " + tdc + " con una línea de Crédito de " + cifras
                    + " con buena experiencia crediticia.", font));
            parrafo1.setPaddingLeft(5f);
            parrafo1.setPaddingRight(5f);
            parrafo1.setColspan(2);
            parrafo1.setBorder(0);
            parrafo1.setPaddingTop(-95f);
            parrafo1.setLeading(2f, 2f);//Espacio entre lineas
            parrafo1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);

            table.getDefaultCell().setMinimumHeight(10f);
            table.addCell(parrafo1);

            //Titulo que contiene el Nombre y el Numero del Producto 
            Calendar miCalendario = Calendar.getInstance();
            int diaHoy = miCalendario.get(Calendar.DAY_OF_MONTH);
            int anio = miCalendario.get(Calendar.YEAR);
            int mes = miCalendario.get(Calendar.MONTH);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            PdfPCell parrafo2 = new PdfPCell(new Phrase("Constancia que se expide en Caracas a los " + diaHoy + " días del mes " + dateMonth(mes) + " del año " + anio + ".", font));
            parrafo2.setPaddingLeft(10f);
            parrafo2.setColspan(2);
            parrafo2.setBorder(0);

            PdfPCell cell3 = new PdfPCell(new Phrase(" ", font));
            cell3.setBorder(0);
            pdf.add(new Paragraph(" "));
            parrafo2.setPaddingTop(-15f);
            table.addCell(cell3);
            table.addCell(parrafo2);

            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell parrafo3 = new PdfPCell(new Phrase("Por DEL SUR BANCO UNIVERSAL, C.A.", font));
            parrafo3.setPaddingLeft(120f);
            parrafo3.setColspan(2);
            parrafo3.setBorder(0);
            pdf.add(new Paragraph(" "));
            parrafo3.setPaddingTop(40f);
            parrafo3.setPaddingBottom(40f);
            table.addCell(parrafo3);

            table.addCell(cellImageFirma);

            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell parrafo6 = new PdfPCell(new Phrase("________________________________________", font));
            parrafo6.setBorder(0);
            parrafo6.setPaddingLeft(100f);
            parrafo6.setColspan(2);
            // parrafo4.setBorder(0);

            PdfPCell cell6 = new PdfPCell(new Phrase(" ", font));
            cell6.setBorder(0);
            pdf.add(new Paragraph(" "));
            parrafo6.setPaddingTop(2f);
            table.addCell(parrafo6);

            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell parrafo4 = new PdfPCell(new Phrase("AGENCIA", font));
            //   parrafo4.setBorder(Rectangle.TOP);
            parrafo4.setPaddingLeft(170f);
            parrafo4.setColspan(2);
            parrafo4.setBorder(0);

            PdfPCell cell5 = new PdfPCell(new Phrase(" ", font));
            cell5.setBorder(0);
            pdf.add(new Paragraph(" "));
            parrafo4.setPaddingTop(10f);
            table.addCell(parrafo4);

            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell parrafo5 = new PdfPCell(new Phrase("Esta referencia tiene una validez de treinta (30) días  a partir de la fecha de emisión.", font));
            parrafo5.setPaddingLeft(20f);
            parrafo5.setColspan(2);
            parrafo5.setBorder(0);

            pdf.add(new Paragraph(" "));
            pdf.add(new Paragraph(" "));
            parrafo5.setPaddingTop(40f);
            table.addCell(parrafo5);

            //Agregamos la tabla al PDF
            //pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            // pdf.setMargins(150f, 20f, 50f, 10f);
            pdf.setMargins(50f, 3f, 22f, 18f);
        } catch (Exception e) {
            //manejar excepciones de reportes
        }
    }

    /**
     * metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     * @param writer
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoReferenciaBancariaTDCPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilo de Fuente
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);
            Font fontBoldPequena = FontFactory.getFont("Open Sans, sans-serif", 7, Font.BOLD);
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 9, Font.BOLD);
            Font fontBoldGrande = FontFactory.getFont("Open Sans, sans-serif", 12, Font.BOLD);

            PdfPTable contenedor = new PdfPTable(1);
            PdfPCell contendorCuerpo = new PdfPCell();
            contendorCuerpo.setPaddingTop(-50);

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(1);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(ReferenciaBancariaController.class.getResource("/imgPDF/logoBanner.png"));

            //Transparencia de la imagen
            image.setTransparency(new int[]{0x00, 0x10});

            image.scaleAbsolute(140f, 40f);

            PdfPCell cellImage = new PdfPCell();
            cellImage.setBorder(0);
            cellImage.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellImage.addElement(new Chunk(image, 2, -2));

            ///////////////////Imagen Firma///////////////////////
            Image imagefirma = null;
            String nroReferencia = null;
            String fechaInicio = null;
            String tdc = null;
            String cifras = null;
            String tipoCuenta = null;
            for (ReferenciaDetalleDTO i : referencia.getReferenciasCuentas()) {
                imagefirma = Image.getInstance(i.getFirmaBlob());
                nroReferencia = i.getNroReferencia();
                fechaInicio = formatearFecha(i.getFechaInicioDate(), FORMATO_FECHA_SIMPLE);
                tdc = i.getNumeroCuenta();
                cifras = i.getCifrasReferencia();
                tipoCuenta = i.getTipoCuenta();
                break;
            }

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas = {6.75f};

            //Asigno las medidas a la tabla (El ancho)
            //   table.setWidths(medidaCeldas);
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setBorderColor(Color.WHITE);
            table.addCell(cellImage);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            //   float[] medidaCeldas = { 2.75f};
            //     table.setWidths(medidaCeldas);
            //Alineamos titulo1 y usuario a la derecha
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPCell titulo1 = new PdfPCell(new Phrase("REFERENCIA BANCARIA DE CUENTAS", fontBoldGrande));
            PdfPCell titulo2 = new PdfPCell(new Phrase("Nro. Control de Referencia:" + nroReferencia, fontBoldGrande));

            //Establecemos espaciado a la derecha
            titulo1.setHorizontalAlignment(Element.ALIGN_RIGHT);//titulo1.setPaddingLeft(165f);//antes 220f
            titulo2.setHorizontalAlignment(Element.ALIGN_RIGHT);//titulo2.setPaddingLeft(165f);//antes 220f
            titulo1.setPaddingTop(20f);
            titulo2.setPaddingTop(20f);
            titulo1.setBorder(0);
            titulo2.setBorder(0);
            table.addCell(titulo1);
            table.addCell(titulo2);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            if (tipoDest.equalsIgnoreCase("1")) {
                PdfPCell interesar = new PdfPCell(new Phrase("A QUIEN PUEDA INTERESAR", fontBoldGrande));
                //Establecemos espaciado a la derecha
                interesar.setHorizontalAlignment(Element.ALIGN_CENTER);//interesar.setPaddingLeft(135f);//antes 165f
                interesar.setPaddingTop(20f);//antes 65f
                interesar.setBorder(0);
                table.addCell(interesar);

            } else if (tipoDest.equalsIgnoreCase("2")) {

                PdfPCell t1 = new PdfPCell(new Phrase("SEÑORES:", fontBoldGrande));
                PdfPCell destinatario = new PdfPCell(new Phrase(nombreDest, font));
                PdfPCell t2 = new PdfPCell(new Phrase("Presente.-", font));

                //Establecemos espaciado a la derecha
                t1.setPaddingLeft(5f);
                t2.setPaddingLeft(5f);
                t1.setPaddingTop(20f);
                t2.setPaddingTop(15f);
                t1.setBorder(0);
                t2.setBorder(0);

                destinatario.setPaddingLeft(25f);
                destinatario.setPaddingTop(10f);
                destinatario.setBorder(0);

                table.addCell(t1);
                table.addCell(destinatario);
                table.addCell(t2);
            }

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas);

            Calendar date = Calendar.getInstance();
            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            table.getDefaultCell().setFixedHeight(50f);
            PdfPCell parrafo1 = new PdfPCell(new Phrase("Por medio de la presente se hace constar que el(la) Sr(a)."
                    + " " + sesionController.getUsuario().getNombre() + ", portador(a) de la cédula de identidad número "
                    + sesionController.getUsuario().getTipoDoc() + "-" + sesionController.getUsuario().getDocumento() + ", es "
                    + "titular de la " + tipoCuenta + " número " + tdc + " con una línea de Crédito de " + cifras
                    + " con buena experiencia crediticia.", font));
            parrafo1.setPaddingLeft(10f);
            parrafo1.setBorder(0);
            parrafo1.setPaddingTop(20f);
            parrafo1.setLeading(1.5f, 1.5f);//Espacio entre lineas
            parrafo1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);

            // table.addCell(parrafo1);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

            table.getDefaultCell().setMinimumHeight(10f);
            table.addCell(parrafo1);

            //Titulo que contiene el Nombre y el Numero del Producto 
            Calendar miCalendario = Calendar.getInstance();
            int diaHoy = miCalendario.get(Calendar.DAY_OF_MONTH);
            int anio = miCalendario.get(Calendar.YEAR);
            int mes = miCalendario.get(Calendar.MONTH);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            PdfPCell parrafo2 = new PdfPCell(new Phrase("Constancia que se expide en Caracas a los " + diaHoy + " días del mes " + dateMonth(mes) + " del año " + anio + ".", font));
            parrafo2.setHorizontalAlignment(Element.ALIGN_CENTER);
            parrafo2.setColspan(2);
            parrafo2.setBorder(0);

            PdfPCell cell3 = new PdfPCell(new Phrase(" ", font));
            cell3.setBorder(0);
            parrafo2.setPaddingTop(-15f);
            table.addCell(cell3);
            table.addCell(parrafo2);

            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell parrafo3 = new PdfPCell(new Phrase("Por DEL SUR BANCO UNIVERSAL, C.A.", font));
            parrafo3.setHorizontalAlignment(Element.ALIGN_CENTER);
            parrafo3.setColspan(2);
            parrafo3.setBorder(0);
            parrafo3.setPaddingTop(40f);
            parrafo3.setPaddingBottom(40f);
            table.addCell(parrafo3);

            //Transparencia de la imagen
            imagefirma.setTransparency(new int[]{0x00, 0x10});
            imagefirma.scaleAbsolute(160f, 60f);
            imagefirma.setAlignment(Image.ALIGN_CENTER);
            PdfPCell cellImageFirma = new PdfPCell();
            cellImageFirma.setBorder(0);
            cellImageFirma.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellImageFirma.setPaddingLeft(150f);
            cellImageFirma.setPaddingTop(15f);
            cellImageFirma.addElement(new Chunk(imagefirma, 2, -2));

            table.addCell(cellImageFirma);

            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell parrafo6 = new PdfPCell(new Phrase("________________________________________", font));
            parrafo6.setBorder(0);
            parrafo6.setHorizontalAlignment(Element.ALIGN_CENTER);
            parrafo6.setColspan(2);
            // parrafo4.setBorder(0);

            PdfPCell cell6 = new PdfPCell(new Phrase(" ", font));
            cell6.setBorder(0);
            parrafo6.setPaddingTop(2f);
            table.addCell(parrafo6);

            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell parrafo4 = new PdfPCell(new Phrase("AGENCIA", font));
            //   parrafo4.setBorder(Rectangle.TOP);
            parrafo4.setHorizontalAlignment(Element.ALIGN_CENTER);
            parrafo4.setColspan(2);
            parrafo4.setBorder(0);

            PdfPCell cell5 = new PdfPCell(new Phrase(" ", font));
            cell5.setBorder(0);
            parrafo4.setPaddingTop(10f);
            table.addCell(parrafo4);

            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell parrafo5 = new PdfPCell(new Phrase("Esta referencia tiene una validez de treinta (30) días  a partir de la fecha de emisión.", font));
            parrafo5.setHorizontalAlignment(Element.ALIGN_CENTER);
            parrafo5.setColspan(2);
            parrafo5.setBorder(0);

            pdf.add(new Paragraph(" "));
            pdf.add(new Paragraph(" "));
            parrafo5.setPaddingTop(40f);
            table.addCell(parrafo5);

            //Agregamos la tabla al PDF
            //pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            // pdf.setMargins(150f, 20f, 50f, 10f);
        } catch (Exception e) {
            //manejar excepciones de reportes
        }
    }

    /**
     * metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     * @param writer
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoReferenciaBancariaCuentasPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilo de Fuente
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);
            Font fontBoldPequena = FontFactory.getFont("Open Sans, sans-serif", 7, Font.BOLD);
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 9, Font.BOLD);
            Font fontBoldGrande = FontFactory.getFont("Open Sans, sans-serif", 12, Font.BOLD);
            String tipoCuenta = "";

            PdfPTable contenedor = new PdfPTable(1);
            PdfPCell contendorCuerpo = new PdfPCell();
            contendorCuerpo.setPaddingTop(-50);

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(1);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(ReferenciaBancariaController.class.getResource("/imgPDF/logoBanner.png"));

            //Transparencia de la imagen
            image.setTransparency(new int[]{0x00, 0x10});
            image.scaleAbsolute(160f, 40f);
            PdfPCell cellImage = new PdfPCell();
            cellImage.setBorder(0);
            cellImage.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellImage.addElement(new Chunk(image, 2, -2));

            ///////////////////Imagen Firma///////////////////////
            Image imagefirma = null;
            String nroReferencia = null;
            String fechaInicio = null;
            for (ReferenciaDetalleDTO i : referencia.getReferenciasCuentas()) {
                imagefirma = Image.getInstance(i.getFirmaBlob());
                nroReferencia = i.getNroReferencia();
                fechaInicio = formatearFecha(i.getFechaInicioDate(), FORMATO_FECHA_SIMPLE);
                break;
            }

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas = {6.75f};

            //Asigno las medidas a la tabla (El ancho)
            //   table.setWidths(medidaCeldas);
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setBorderColor(Color.WHITE);
            table.addCell(cellImage);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            //   float[] medidaCeldas = { 2.75f};
            //     table.setWidths(medidaCeldas);
            //Alineamos titulo1 y usuario a la derecha
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPCell titulo1 = new PdfPCell(new Phrase("REFERENCIA BANCARIA DE CUENTAS", fontBoldGrande));
            PdfPCell titulo2 = new PdfPCell(new Phrase("Nro. Control de Referencia:" + nroReferencia, fontBoldGrande));

            //Establecemos espaciado a la derecha
            titulo1.setHorizontalAlignment(Element.ALIGN_RIGHT);//titulo1.setPaddingLeft(165f);//antes 220f
            titulo2.setHorizontalAlignment(Element.ALIGN_RIGHT);//titulo2.setPaddingLeft(165f);//antes 220f
            titulo1.setPaddingTop(20f);
            titulo2.setPaddingTop(20f);
            titulo1.setBorder(0);
            titulo2.setBorder(0);
            table.addCell(titulo1);
            table.addCell(titulo2);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            if (tipoDest.equalsIgnoreCase("1")) {
                PdfPCell interesar = new PdfPCell(new Phrase("A QUIEN PUEDA INTERESAR", fontBoldGrande));
                //Establecemos espaciado a la derecha
                interesar.setHorizontalAlignment(Element.ALIGN_CENTER);//interesar.setPaddingLeft(135f);//antes 165f
                interesar.setPaddingTop(20f);//antes 65f
                interesar.setBorder(0);
                table.addCell(interesar);

            } else if (tipoDest.equalsIgnoreCase("2")) {

                PdfPCell t1 = new PdfPCell(new Phrase("SEÑORES:", fontBoldGrande));
                PdfPCell destinatario = new PdfPCell(new Phrase(nombreDest, font));
                PdfPCell t2 = new PdfPCell(new Phrase("Presente.-", font));

                //Establecemos espaciado a la derecha
                t1.setPaddingLeft(5f);
                t2.setPaddingLeft(5f);
                t1.setPaddingTop(20f);
                t2.setPaddingTop(15f);
                t1.setBorder(0);
                t2.setBorder(0);

                destinatario.setPaddingLeft(25f);
                destinatario.setPaddingTop(10f);
                destinatario.setBorder(0);

                table.addCell(t1);
                table.addCell(destinatario);
                table.addCell(t2);
            }

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas);

            Calendar date = Calendar.getInstance();
            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            table.getDefaultCell().setFixedHeight(50f);
            PdfPCell parrafo1 = new PdfPCell(new Phrase("Por medio de la presente se hace constar que el(la) Sr(a)."
                    + " " + sesionController.getUsuario().getNombre() + ", portador(a) de la cédula de identidad número "
                    + sesionController.getUsuario().getTipoDoc() + "-" + sesionController.getUsuario().getDocumento() + ", es"
                    + " cliente de nuestra institución financiera desde el " + fechaInicio + " "
                    + "y mantiene el(los) siguiente(s) producto(s):", font));
            parrafo1.setPaddingLeft(10f);
            parrafo1.setBorder(0);
            parrafo1.setPaddingTop(20f);
            parrafo1.setLeading(1.5f, 1.5f);//Espacio entre lineas
            parrafo1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);

            table.addCell(parrafo1);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

            for (ReferenciaDetalleDTO i : referencia.getReferenciasCuentas()) {

                PdfPCell producto1 = new PdfPCell(new Phrase("(*) " + i.getTipoCuenta() + ": " + this.formatoAsteriscosWeb(i.getNumeroCuenta()) + " con un saldo promedio mensual de " + i.getCifrasReferencia(), font));
                producto1.setPaddingLeft(50f);
                producto1.setPaddingRight(40f);
                producto1.setBorder(0);

                PdfPCell cellP = new PdfPCell(new Phrase(" ", font));
                cellP.setBorder(0);
                producto1.setLeading(1.5f, 1.5f);//Espacio entre lineas
                table.addCell(cellP);
                table.addCell(producto1);
            }

            //Titulo que contiene el Nombre y el Numero del Producto 
            Calendar miCalendario = Calendar.getInstance();
            int diaHoy = miCalendario.get(Calendar.DAY_OF_MONTH);
            int mes = miCalendario.get(Calendar.MONTH);
            int anio = miCalendario.get(Calendar.YEAR);
            int hora = miCalendario.get(Calendar.HOUR);
            int min = miCalendario.get(Calendar.MINUTE);
            int seg = miCalendario.get(Calendar.SECOND);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            PdfPCell parrafo2 = new PdfPCell(new Phrase("Constancia que se expide a petición de la parte interesada a los " + diaHoy + " días del mes " + dateMonth(mes) + " del año " + anio + " y tiene una vigencia de 30 días continuos a partir de la fecha de emisión.", font));
            parrafo2.setPaddingLeft(10f);
            parrafo2.setBorder(0);

            PdfPCell cell3 = new PdfPCell(new Phrase(" ", font));
            cell3.setBorder(0);
            parrafo2.setPaddingTop(5f);
            parrafo2.setLeading(1.5f, 1.5f);//Espacio entre lineas
            table.addCell(cell3);
            table.getDefaultCell().setMinimumHeight(10f);
            table.addCell(parrafo2);

            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell parrafo3 = new PdfPCell(new Phrase("DEL SUR Banco Universal", font));
            parrafo3.setHorizontalAlignment(Element.ALIGN_CENTER);//parrafo3.setPaddingLeft(150f);
            parrafo3.setBorder(0);

            PdfPCell cell4 = new PdfPCell(new Phrase(" ", font));
            cell4.setBorder(0);
            parrafo3.setPaddingTop(15f);
            table.addCell(cell4);
            table.addCell(parrafo3);

            //Transparencia de la imagen
            imagefirma.setTransparency(new int[]{0x00, 0x10});
            imagefirma.scaleAbsolute(160f, 60f);
            imagefirma.setAlignment(Image.ALIGN_CENTER);
            PdfPCell cellImageFirma = new PdfPCell();
            cellImageFirma.setBorder(0);
            cellImageFirma.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellImageFirma.setPaddingLeft(150f);
            cellImageFirma.setPaddingTop(15f);
            cellImageFirma.addElement(new Chunk(imagefirma, 2, -2));

            table.addCell(cellImageFirma);

            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell parrafo4 = new PdfPCell(new Phrase("Funcionario Autorizado", font));
            parrafo4.setHorizontalAlignment(Element.ALIGN_CENTER);//parrafo4.setPaddingLeft(170f);
            parrafo4.setBorder(0);

            PdfPCell cell5 = new PdfPCell(new Phrase(" ", font));
            cell5.setBorder(0);
            parrafo4.setPaddingTop(5f);
            table.addCell(parrafo4);

            //Titulo que contiene el Nombre y el Numero del Producto 
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            table.getDefaultCell().setFixedHeight(50f);
            PdfPCell parrafo5 = new PdfPCell(new Phrase("Esta Referencia ha sido generada por el cliente a través del servicio de banca "
                    + "electrónica, DELSUR ONLINE el " + diaHoy + " de " + dateMonth(mes) + " de " + anio + " a las " + hora + ":" + min + ":" + seg + ". De ser necesaria su confirmación, comuníquese "
                    + "por los números telefónicos (0286)-920.35.69/35.33 de lunes a viernes de 8:30 am a 12:00 pm y de 2:00 pm a 5:00 pm.", fontBoldPequena));
            parrafo5.setPaddingLeft(10f);
            parrafo5.setBorder(0);

            parrafo5.setPaddingTop(15f);
            parrafo5.setLeading(1.5f, 1.5f);//Espacio entre lineas
            table.getDefaultCell().setMinimumHeight(10f);
            table.addCell(parrafo5);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell parrafo6 = new PdfPCell(new Phrase("*Información Confidencial*", fontBoldPequena));
            parrafo6.setPaddingLeft(170f);
            parrafo6.setBorder(0);

            PdfPCell cell6 = new PdfPCell(new Phrase(" ", font));
            cell6.setBorder(0);
            parrafo6.setPaddingTop(15f);
            table.addCell(cell6);
            table.addCell(parrafo6);

            //Agregamos la tabla al PDF
            pdf.add(table);

            // pdf.setMargins(150f, 20f, 50f, 10f);
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
    public void detalleReferenciaBancariaPDF() throws IOException, DocumentException {

        try {

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-disposition", "attachment;filename=\"ReferenciaBancaria.pdf\"");//Preguntar si se agrega la identificaciÃ³n del usuario
            //response.addHeader("Content-disposition", "attachment;filename=\"" + tipoPDF + "\"");
            Document document = new Document(PageSize.A4, 30, 30, 30, 30);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            //Instanciamos la clase utilitaria que realiza la paginación
            PaginarPDF paginar = new PaginarPDF();
            Rectangle headerBox = new Rectangle(22, 45, 1320, 700);

            //Establecemos el tamaño del documento
            writer.setBoxSize("headerBox", headerBox);

            //Pasamos por parametros los eventos que se producen al generar el PDF
            writer.setPageEvent(paginar);

            //document.setMargins(left, right, top, bottom);
            document.setMargins(-10f, -10f, 50f, 40f);

            document.open();

            if (TDCSeleccionada != null) {
                //Invocamos el metodo que genera el PDF
                this.cuerpoReferenciaBancariaTDCPDF(document);
            } else {
                //Invocamos el metodo que genera el PDF
                this.cuerpoReferenciaBancariaCuentasPDF(document);
            }

            //ceramos el documento
            document.close();

            response.setContentLength(baos.size());
            ServletOutputStream out = response.getOutputStream();
            baos.writeTo(out);
            baos.flush();
            faces.responseComplete();

        } catch (Exception e) {
        }
    }

    public void inicio() {
        if (sesionController.isReiniciarForm()) {
            if (!FacesContext.getCurrentInstance().isPostback()) {
                cuentaSeleccionada = null;
                cuentaSeleccionadaDTO = new CuentaDTO();
                TDCSeleccionada = null;
                TDCSeleccionadaDTO = new TarjetasCreditoDTO();
                nombreDest = null;
                refCuenta = false;
                RefTdc = false;
                nroCuenta = null;
                nroTarjeta = null;
                tipoRef = "U";
                tipoDest = "1";
                tipoRefIndividual = true;
                tipoDestNombreDest = false;
                nombreDest = null;
                sesionController.setReiniciarForm(false);
                sesionController.setValidadorFlujo(1);
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_REF_BANCARIA)) {
                sesionController.setValidadorFlujo(1);
                this.referenciaBancariaPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_REF_BANCARIA)) {
                sesionController.setValidadorFlujo(1);
                this.referenciaBancariaPaso1();
            }
        }
    }

}
