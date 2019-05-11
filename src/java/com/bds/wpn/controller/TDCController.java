/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ReclamosDAO;
import com.bds.wpn.dao.TarjetaCreditoDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.MesAnoDTO;
import com.bds.wpn.dto.MovimientoTDCDTO;
import com.bds.wpn.dto.ReclamoDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.TarjetasCreditoDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author juan.faneite
 */
@Named("wpnTDCController")
@SessionScoped
public class TDCController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;
    @EJB
    IbParametrosFacade parametrosFacade;
    @EJB
    IbTextosFacade textosFacade;

    @EJB
    TarjetaCreditoDAO tarjetaDAO;

    @EJB
    ReclamosDAO reclamosDAO;

    @EJB
    TarjetaCreditoDAO tarjetaCreditoDAO;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;

    private TarjetasCreditoDTO tdcDTO;
    private String tdcSeleccionada;
    private Date txtFechaDesde = new Date();                            //input para la fecha desde
    private Date txtFechaHasta = new Date();                            //input para la fecha hasta
    private Date calendarFechaMax = new Date();                         //fecha maxima para validacion del calendar
    private Date calendarFechaMin;                                      //fecha minima para validacion del calendar
    private String tipoTransaccionFiltroSelected;                       //indica la transaccion seleccionada en el filtro
    private Collection<MovimientoTDCDTO> listMovimientosDTO;            //lista de movimientos de tarjetas  

    private Collection<TarjetasCreditoDTO> tdcCliente;              //objeto que contiene las TDC propias

    private String tdc = "";
    private String mesAnoSelected;
    private String mes;
    private String ano;
    private boolean mesCerrado = false;                                 //indicador de movimientos de TDC mes cerrado(true) o movimientos por facturar (false)

    /**
     *
     * @return obtiene dto de tdc
     */
    public TarjetasCreditoDTO getTdcDTO() {
        return tdcDTO;
    }

    /**
     *
     * @param tdcDTO inserta dto de tdc
     */
    public void setTdcDTO(TarjetasCreditoDTO tdcDTO) {
        this.tdcDTO = tdcDTO;
    }

    public Collection<MovimientoTDCDTO> getListMovimientosDTO() {
        return listMovimientosDTO;
    }

    public void setListMovimientosDTO(Collection<MovimientoTDCDTO> movimientosDTO) {
        this.listMovimientosDTO = movimientosDTO;
    }

    public String getTdcSeleccionada() {
        return tdcSeleccionada;
    }

    public void setTdcSeleccionada(String tdcSeleccionada) {
        this.tdcSeleccionada = tdcSeleccionada;
    }

    public Date getTxtFechaDesde() {
        return txtFechaDesde;
    }

    public void setTxtFechaDesde(Date txtFechaDesde) {
        this.txtFechaDesde = txtFechaDesde;
    }

    public Date getTxtFechaHasta() {
        return txtFechaHasta;
    }

    public void setTxtFechaHasta(Date txtFechaHasta) {
        this.txtFechaHasta = txtFechaHasta;
    }

    public Date getCalendarFechaMax() {
        return calendarFechaMax;
    }

    public void setCalendarFechaMax(Date calendarFechaMax) {
        this.calendarFechaMax = calendarFechaMax;
    }

    public boolean isMesCerrado() {
        return mesCerrado;
    }

    public void setMesCerrado(boolean mesCerrado) {
        this.mesCerrado = mesCerrado;
    }
    
    public void iniciar() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.isReiniciarForm()) {
                sesionController.setMesAnoSelected(new MesAnoDTO());
                sesionController.setReiniciarForm(false);
            }
        }
    }

    /**
     * get que obtiene la fecha minima para la seleccion del calendario
     *
     * @return fecha minima
     */
    public Date getCalendarFechaMin() {

        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, Integer.parseInt(parametrosController.getNombreParametro("pnw.global.filtrar.meses", sesionController.getIdCanal())));
        calendarFechaMin = c.getTime();
        return calendarFechaMin;
    }

    public void setCalendarFechaMin(Date calendarFechaMin) {
        this.calendarFechaMin = calendarFechaMin;
    }

    public String getTipoTransaccionFiltroSelected() {
        return tipoTransaccionFiltroSelected;
    }

    public void setTipoTransaccionFiltroSelected(String tipoTransaccionFiltroSelected) {
        this.tipoTransaccionFiltroSelected = tipoTransaccionFiltroSelected;
    }

    public String getMesAnoSelected() {
        return mesAnoSelected;
    }

    public void setMesAnoSelected(String mesAnoSelected) {

        if (!mesAnoSelected.equals("-1")) {
            this.mes = mesAnoSelected.split("--")[0];
            this.ano = mesAnoSelected.split("--")[1];
        }

        this.mesAnoSelected = mesAnoSelected;
    }

    public String getMes() {
        return mes;
    }

    public String getAno() {
        return ano;
    }

    /**
     * Metodo que obtiene el detalle de la tarjeta de credito
     *
     * @param nroTDD
     * @throws IOException
     */
    public void obtenerDetalleTarjetaCredito(String nroTDD) throws IOException {

        tdcSeleccionada = nroTDD;
        tdcDTO = new TarjetasCreditoDTO(sesionController.getSemilla());
        TarjetasCreditoDTO tarjetaTemp = new TarjetasCreditoDTO(sesionController.getSemilla());
        //obtenemos el detalle de la TDC
        boolean validacionesSeguridad = sesionController.productosCliente(tarjetaTemp.getDesEnc().desencriptar(tdcSeleccionada));
        if (validacionesSeguridad) {
            tdcDTO = tarjetaDAO.obtenerDetalleTDC(tarjetaTemp.getDesEnc().desencriptar(tdcSeleccionada), sesionController.getIdCanal(), sesionController.getNombreCanal());
            tdcDTO.setSemilla(sesionController.getSemilla());

            //ajustar la tarjeta que viene de entrada
            tarjetaTemp = tarjetaDAO.listadoMovimientosTDC(tdcDTO.getDesEnc().desencriptar(tdcSeleccionada), sesionController.getIdCanal(), sesionController.getNombreCanal(), numeroRegistro());
            tarjetaTemp.setSemilla(sesionController.getSemilla());
            //mensajes
            if (tarjetaTemp.getMovimientosDTO() == null) {
                tdcDTO.setMovimientosDTO(new ArrayList<MovimientoTDCDTO>());
            } else {
                tdcDTO.setMovimientosDTO(tarjetaTemp.getMovimientosDTO());
            }
        } else {
            RespuestaDTO respuesta = new RespuestaDTO();
            respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
            respuesta.setDescripcionSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
            tdcDTO.setRespuestaDTO(respuesta);
        }

        if (!tdcDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !tdcDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetalleTDC:divDetTDC", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        } else if (tdcDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && !tdcDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetalleTDC:divDetTDC", new FacesMessage(FacesMessage.SEVERITY_WARN, "", tdcDTO.getRespuestaDTO().getTextoSP()));
        }

        if (!tdcDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetalleTDC:divDetTDC", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        }
        //Registro en Bitacora
        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_DET_TDC, "", "", "Consulta Detalle de TDC", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);

        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.detTdc.url", sesionController.getIdCanal()));
    }

    /**
     * Cantidad de numero de registros a mostrar
     *
     * @return String
     */
    public String numeroRegistro() {
        return parametrosController.getValorParametro("pnw.detTdc.num.registros", sesionController.getIdCanal());
    }

    /**
     *
     * @return metodo que retorna el listado de los tipo de operacion o listado
     * de reclamos posibles
     */
    public List<ReclamoDTO> listadoTransaccionesMov() {
        ReclamoDTO reclamo = new ReclamoDTO();
        reclamo = reclamosDAO.obtenerListadoReclamos(sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (reclamo.getRespuestaDTO() != null && !reclamo.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetalleTDC:divDetTDC", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        }
        return reclamo.getReclamos();
    }

    /**
     * metodo para el filtro por meses no se utiliza por problemas de Credicard
     */
    public void filtroMeses() {
        TarjetasCreditoDTO tdcDTOMov;
        tdcDTO.setMovimientosDTO(null);
        boolean validacionesSeguridad = sesionController.productosCliente(tdcDTO.getNumeroTarjeta());
        if (validacionesSeguridad) {
            if((Integer.parseInt(sesionController.getMesAnoSelected().getMes())+1) < 10 ){
                mes = "0" + (Integer.parseInt(sesionController.getMesAnoSelected().getMes())+1);
            }else{
                mes = "" + (Integer.parseInt(sesionController.getMesAnoSelected().getMes())+1);
            }
            
            ano = sesionController.getMesAnoSelected().getAno();
            if (formatearFecha(new Date(), FORMATO_FECHA_SIMPLE).split("/")[1].equalsIgnoreCase(mes)) {
                mesCerrado = false;
                tdcDTOMov = tarjetaDAO.listadoMovimientosTDC(tdcDTO.getDesEnc().desencriptar(tdcSeleccionada), sesionController.getIdCanal(), sesionController.getNombreCanal(), numeroRegistro());
            } else {
                mesCerrado = true;
                tdcDTOMov = tarjetaDAO.listadoMovimientosTDCMes(tdcDTO.getDesEnc().desencriptar(tdcSeleccionada), Integer.valueOf(mes), Integer.valueOf(ano), sesionController.getIdCanal(), sesionController.getNombreCanal());
            }

            if (!tdcDTOMov.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetalleTDC:divDetTDC", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return;
            } else {
                if (tdcDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_SIN_RESULTADOS_JPA)) {
                    this.limipiarMensajesFacesContext();
                    return;
                } else {
                    if (!tdcDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                        this.limipiarMensajesFacesContext();
                        FacesContext.getCurrentInstance().addMessage("formDetalleTDC:divDetTDC", new FacesMessage(FacesMessage.SEVERITY_WARN, "", tdcDTOMov.getRespuestaDTO().getTextoSP()));
                        return;
                    }
                }
            }

            if (!tdcDTOMov.getMovimientosDTO().isEmpty() || tdcDTOMov.getMovimientosDTO() != null) {
                tdcDTO.setMovimientosDTO(tdcDTOMov.getMovimientosDTO());
            }

        } else {
            RespuestaDTO respuesta = new RespuestaDTO();
            respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
            respuesta.setTextoSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
            tdcDTO.setRespuestaDTO(respuesta);
        }

    }

    /**
     * Consulta TDC propias
     *
     * @return
     */
    public List<TarjetasCreditoDTO> getTDCPropias() {
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
        return datosCliente.getTdcAsociadasClienteDTO();
    }

    /**
     * Metodo para actualizar las tdc del seleccionar tdc desplegables con la
     * consulta por defecto enviando las fechas en null
     *
     * @param event
     */
    public void actDetalleTDCInicial(AjaxBehaviorEvent event) {
        // String[] split = tdcSeleccionada.split(":");
        tdcDTO.setSemilla(sesionController.getSemilla());
        TarjetasCreditoDTO tdcDTOMov = new TarjetasCreditoDTO(this.sesionController.getSemilla());
        // tdc= tdcDTO.getDesEnc().desencriptar(tdcSeleccionada);System.err.println("TARJETA: " + tdc);
        boolean validacionesSeguridad = sesionController.productosCliente(tdcDTO.getDesEnc().desencriptar(tdcSeleccionada));
        String fechaDesde = "";
        String fechaHasta = "";
        if (validacionesSeguridad) {
            tdcDTO = tarjetaDAO.obtenerDetalleTDC(tdcDTO.getDesEnc().desencriptar(tdcSeleccionada), sesionController.getIdCanal(), sesionController.getNombreCanal());
            tdcDTO.setSemilla(sesionController.getSemilla());

            //seteamos la fecha del mes actual
            Calendar date = Calendar.getInstance();
            date.set(Calendar.DAY_OF_MONTH, 1);
            fechaDesde = this.formatearFecha(date.getTime(), this.FORMATO_FECHA_SIMPLE);
            //se calculan los mov del mes actual desde el 1 hasta el dia de hoy ya que si esta fecha supera el dia actual el SP arroja un ERROR
            date = Calendar.getInstance();
            fechaHasta = this.formatearFecha(date.getTime(), this.FORMATO_FECHA_SIMPLE);

            tdcDTOMov = tarjetaDAO.listadoMovimientosTDC(tdcDTO.getNumeroTarjeta(), sesionController.getIdCanal(), sesionController.getNombreCanal(), numeroRegistro());
        } else {
            RespuestaDTO respuesta = new RespuestaDTO();
            respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
            respuesta.setTextoSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
            tdcDTO.setRespuestaDTO(respuesta);
        }
//        if (!tdcDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !tdcDTOMov.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
//            this.limipiarMensajesFacesContext();
//            FacesContext.getCurrentInstance().addMessage("formDetalleTDC:divDetTDC", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
//        } else if (tdcDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && !tdcDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
//            this.limipiarMensajesFacesContext();
//            FacesContext.getCurrentInstance().addMessage("formDetalleTDC:divDetTDC", new FacesMessage(FacesMessage.SEVERITY_WARN, "", tdcDTO.getRespuestaDTO().getTextoSP()));
//        }

        if (!tdcDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetalleTDC:divDetTDC", new FacesMessage(FacesMessage.SEVERITY_WARN, "", tdcDTO.getRespuestaDTO().getTextoSP()));
        } else if (!tdcDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetalleTDC:divDetTDC", new FacesMessage(FacesMessage.SEVERITY_WARN, "", tdcDTO.getRespuestaDTO().getTextoSP()));
        }

        listMovimientosDTO = tdcDTOMov.getMovimientosDTO();

    }

    ////////////////////SECCION  DE REPORTES//////////////////////////
    /**
     * Metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     */
    public void cuerpoDetalleTDCPDF(Document pdf) {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 14, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            TarjetasCreditoDTO tarjetaTemp = new TarjetasCreditoDTO(sesionController.getSemilla());

            //Variables para el manejo de los Saldos
            String limiteCredito;
            String saldoBloqueado;
            String montoDisponible;
            String saldoTotal;
            String monto = "0.0";
            String monto2 = "0.0";
            String inter = "0.0";

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(TDCController.class.getResource("/imgPDF/logoBanner.png"));

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

            //Le asigne el tama?o a la cada una de las celdas de acuerdo con la cantidad de columnas que especifico arriba
            float[] medidaCeldasTitulos = {0.95f, 0.5f, 0.55f, 0.55f, 0.55f};

            // Asigno las medidas a las tabla (El ancho)
            table.setWidths(medidaCeldasTitulos);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            //Título que contiene el Nombre y el Número del Producto
            PdfPCell cell = new PdfPCell(new Phrase(tdcDTO.getNombreProducto() + " " + formatoAsteriscosWeb(tdcDTO.getNumeroTarjeta()), fontTituloBold));
            cell.setPaddingBottom(5);
            cell.setColspan(5);
            cell.setBorder(0);
            table.addCell(cell);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Título de las columnas
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.nombreProducto", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.tipo", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.nroTarjeta", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.moneda", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.fechaCorte", sesionController.getIdCanal()), fontBold));

            //COntenido de las columnas, data
            table.getDefaultCell().setBackgroundColor(null);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase((tdcDTO.getNombreProducto() != null ? tdcDTO.getNombreProducto() : ""), font));
            table.addCell(new Phrase((tdcDTO.getCodigoTipoProducto() != null ? tdcDTO.getCodigoTipoProducto() : ""), font));
            table.addCell(new Phrase((tdcDTO.getNumeroTarjeta() != null ? tdcDTO.getNumeroTarjeta() : ""), font));
            table.addCell(new Phrase((tdcDTO.getSiglasTipoMoneda() != null ? tdcDTO.getSiglasTipoMoneda() : ""), font));
            table.addCell(new Phrase((tdcDTO.getFechaCorteDate() != null ? fecha(tdcDTO.getFechaCorteDate()) : ""), font));

            //Agregamos la tabla al PDF
            pdf.add(table);

            table = new PdfPTable(4);

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
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.saldoTotal", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.saldoBloqueado", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.saldoDisponible", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.limiteCredito", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(null);

            if (tdcDTO.getSaldoTotal() != null) {
                saldoTotal = formatearMonto(tdcDTO.getSaldoTotal());
            } else {
                saldoTotal = "0.0";
            }

            if (tdcDTO.getMontoDisponible() != null) {
                montoDisponible = formatearMonto(tdcDTO.getMontoDisponible());
            } else {
                montoDisponible = "0.0";
            }

            if (tdcDTO.getLimiteCredito() != null) {
                limiteCredito = formatearMonto(tdcDTO.getLimiteCredito());
            } else {
                limiteCredito = "0.0";
            }
            if (tdcDTO.getSaldoBloqueado() != null) {
                saldoBloqueado = formatearMonto(tdcDTO.getSaldoBloqueado());
            } else {
                saldoBloqueado = "0.0";
            }

            //Contenido de las columnas
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase(saldoTotal, font));                       
            table.addCell(new Phrase(saldoBloqueado, font));
            table.addCell(new Phrase(montoDisponible, font));
            table.addCell(new Phrase(limiteCredito, font));

            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            

            if(mesCerrado){
                //Asignamos celdas a la tabla, 7 columnas
            table = new PdfPTable(7);
            //Le asigne el tamanio a la cada una de las celdas de acuerdo con la cantidad de columnas que especifico arriba
            float[] medidaCeldasSaldos = {0.55f, 0.55f, 0.55f, 0.55f, 0.55f, 0.55f, 0.55f};

            // Asigno las medidas a las tabla (El ancho)
            table.setWidths(medidaCeldasSaldos);

            //Aplicamos espaciado entre el texto y la celda
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            // PdfPCell tituloMovimientos = new PdfPCell(new Phrase("DETALLES DE MOVIMIENTOS", fontTituloBold));
            //   table.addCell(tituloMovimientos);
            //   cell.setColspan(7);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                
                //Títulos de las columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.fecha", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.fechaReg", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.descripcion", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.montoDiv", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.montoBs", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.intereses", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.referencia", sesionController.getIdCanal()), fontBold));

            //Contenido de las columnas, recorremos los registros recuperados de movimientos
            table.getDefaultCell().setBackgroundColor(null);

            tdcDTO = tarjetaDAO.obtenerDetalleTDC(tarjetaTemp.getDesEnc().desencriptar(tdcSeleccionada), sesionController.getIdCanal(), sesionController.getNombreCanal());
            tdcDTO.setSemilla(sesionController.getSemilla());
            
            /*************NUEVO****************/
            if((Integer.parseInt(sesionController.getMesAnoSelected().getMes())+1) < 10 ){
                mes = "0" + (Integer.parseInt(sesionController.getMesAnoSelected().getMes())+1);
            }else{
                mes = "" + (Integer.parseInt(sesionController.getMesAnoSelected().getMes())+1);
            }
            
            ano = sesionController.getMesAnoSelected().getAno();
            tarjetaTemp = tarjetaDAO.listadoMovimientosTDCMes(tdcDTO.getDesEnc().desencriptar(tdcSeleccionada), Integer.valueOf(mes), Integer.valueOf(ano), sesionController.getIdCanal(), sesionController.getNombreCanal());
            
            tarjetaTemp.setSemilla(sesionController.getSemilla());

            //Validamos que existan movimientos para mostrar
            if (tarjetaTemp.getMovimientosDTO() == null || tarjetaTemp.getMovimientosDTO().isEmpty()) {
                table.getDefaultCell().setColspan(7);
                table.getDefaultCell().setPaddingTop(3);
                table.getDefaultCell().setPaddingBottom(3);
                table.getDefaultCell().setPaddingRight(3);
                table.getDefaultCell().setPaddingLeft(3);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.dataTable.texto.vacio", sesionController.getIdCanal()), font));
            } else {
                //Se recorren los movimientos recuperados para armar la data
                for (MovimientoTDCDTO mov : tarjetaTemp.getMovimientosDTO()) {
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(new Phrase((mov.getFechaString() != null ? mov.getFechaString() : ""), font));
                    table.addCell(new Phrase((mov.getFechaRegString() != null ? mov.getFechaRegString() : ""), font));
                    table.addCell(new Phrase((mov.getDescripcion() != null ? mov.getDescripcion() : ""), font));

                    if (mov.getMontoDivisa()!= null) {
                        monto = formatearMonto(mov.getMontoDivisa());
                    } else {
                        monto = "0.0";
                    }
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);

                    table.addCell(new Phrase(monto, font));
                    
                    if (mov.getMonto()!= null) {
                        monto2 = formatearMonto(mov.getMonto());
                    } else {
                        monto2 = "0.0";
                    }
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);

                    table.addCell(new Phrase(monto2, font));
                    
                    if (mov.getIntereses()!= null) {
                        inter = formatearMonto(mov.getIntereses());
                    } else {
                        inter = "0.0";
                    }
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);

                    table.addCell(new Phrase(inter, font));
                    
                    table.addCell(new Phrase((mov.getReferencia() != null ? mov.getReferencia() : ""), font));
                }
            }
            
            }else{
                //Asignamos celdas a la tabla, 3 columnas
            table = new PdfPTable(3);
            //Le asigne el tamanio a la cada una de las celdas de acuerdo con la cantidad de columnas que especifico arriba
            float[] medidaCeldasSaldos = {0.55f, 0.55f, 0.55f};

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
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.fecha", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.descripcion", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detTDC.descargaPdf.monto", sesionController.getIdCanal()), fontBold));

            //Contenido de las columnas, recorremos los registros recuperados de movimientos
            table.getDefaultCell().setBackgroundColor(null);

            tdcDTO = tarjetaDAO.obtenerDetalleTDC(tarjetaTemp.getDesEnc().desencriptar(tdcSeleccionada), sesionController.getIdCanal(), sesionController.getNombreCanal());
            tdcDTO.setSemilla(sesionController.getSemilla());
            tarjetaTemp = tarjetaDAO.listadoMovimientosTDC(tdcDTO.getDesEnc().desencriptar(tdcSeleccionada), sesionController.getIdCanal(), sesionController.getNombreCanal(), numeroRegistro());
            tarjetaTemp.setSemilla(sesionController.getSemilla());

            //Validamos que existan movimientos para mostrar
            if (tarjetaTemp.getMovimientosDTO() == null || tarjetaTemp.getMovimientosDTO().isEmpty()) {
                table.getDefaultCell().setColspan(4);
                table.getDefaultCell().setPaddingTop(3);
                table.getDefaultCell().setPaddingBottom(3);
                table.getDefaultCell().setPaddingRight(3);
                table.getDefaultCell().setPaddingLeft(3);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.dataTable.texto.vacio", sesionController.getIdCanal()), font));
            } else {
                //Se recorren los movimientos recuperados para armar la data
                for (MovimientoTDCDTO mov : tarjetaTemp.getMovimientosDTO()) {
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(new Phrase((mov.getFechaOperacionDate() != null ? fecha(mov.getFechaOperacionDate()) : ""), font));
                    table.addCell(new Phrase((mov.getDescripcion() != null ? mov.getDescripcion() : ""), font));

                    if (mov.getMonto() != null) {
                        monto = formatearMonto(mov.getMonto());
                    } else {
                        monto = "0.0";
                    }
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);

                    table.addCell(new Phrase(monto, font));
                }
            }
                
                
            }
            
            

            //Añadimos la tabla 
            // pdf.setMargins(150f, 20f, 50f, 10f);
            pdf.add(table);
            pdf.setMargins(20f, 3f, 25f, 20f);
            pdf.add(new Paragraph(" "));
        } catch (Exception e) {
            //manejar excepciones de reportes
            e.printStackTrace();
        }

    }

    /**
     * metodo encargado de armar el reportes de listado del Perfil
     * PerfilBKUsuarios en PDF
     *
     * @throws java.io.IOException
     * @throws com.lowagie.text.DocumentException
     */
    public void detalleTDCPDF() throws IOException, DocumentException {

        try {

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-disposition", "attachment;filename=\"DetalleDeTDC.pdf\"");
            Document document = new Document(PageSize.A4.rotate());//(), 20, 20, 20, 20);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            //Instanciamos la clase utilitaria que realiza la paginaci�n
            PaginarPDF paginar = new PaginarPDF();
            Rectangle headerBox = new Rectangle(22, 45, 1320, 700);
            /**/
            //Establecemos el tamanio del documento
            writer.setBoxSize("headerBox", headerBox);
            //Pasamos por parametros los eventos que se producen al generar el PDF
            writer.setPageEvent(paginar);
            /**/
            document.open();
            //Invocamos el m�todo que genera el PDF
            this.cuerpoDetalleTDCPDF(document);

            document.close();

            response.setContentLength(baos.size());
            ServletOutputStream out = response.getOutputStream();
            baos.writeTo(out);
            baos.flush();
            faces.responseComplete();

        } catch (Exception e) {
        }
    }

    /**/
    /**
     * M�todo que permite imprimir la versi�n que se encuentra desplegada
     *
     * @return
     */
//    public String printVersion() {
//        String version;
//        String date;
//
//        try {
//            ServletContext servletContext = (ServletContext) FacesContext
//                    .getCurrentInstance().getExternalContext().getContext();
//            // ServletContext servletContext = getServletConfig().getServletContext();
//            InputStream inputStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF");
//            Manifest manifest = new Manifest(inputStream);
//            Attributes attributes = manifest.getMainAttributes();
//            String wlsAppVersion = attributes.getValue("Weblogic-Application-Version");
//            if (null == wlsAppVersion || wlsAppVersion.isEmpty()) {
//                String appVersion = attributes.getValue("Implementation-Version");
//                if (null == appVersion || appVersion.isEmpty()) {
//                    // version = "VERSION DESPLEGADA : DESCONOCIDA";
//                    version = "DESCONOCIDA";
//                } else {
//                    // version = "VERSION DESPLEGADA (APP) : " + appVersion;
//                    version = appVersion;
//                }
//            } else {
//                // version = "VERSION DESPLEGADA (WLS): " + wlsAppVersion;
//                version = wlsAppVersion;
//            }
//
//            String deliveryDate = attributes.getValue("Delivery-Date");
//            // date = "ENTREGA FECHA : " + deliveryDate;
//            date = deliveryDate;
//
//        } catch (Exception e) {
//            version = "VERSION DESPLEGADA : ERROR OBTENIENDO EL VALOR";
//            date = "ENTREGA FECHA : ERROR OBTENIENDO EL VALOR";
//            logger.error("Error leyendo los datos de la version de la aplicacion.", e);
//        }
//
//        return "ABL v. " + version + " (" + date + ")";
//    }
//    
    /**/
}
