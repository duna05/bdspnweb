/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.IbCanalesDAO;
import com.bds.wpn.dao.IbLogsDAO;
import com.bds.wpn.dao.IbMenuDAO;
import com.bds.wpn.dto.IbCanalDTO;
import com.bds.wpn.dto.IbLogsDTO;
import com.bds.wpn.dto.IbModulosDTO;
import com.bds.wpn.dto.IbTransaccionesDTO;
import com.bds.wpn.facade.IbCanalFacade;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.facade.IbTransaccionesFacade;
import com.bds.wpn.util.BDSUtil;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author juan.faneite
 */
@Named("wpnHistTransController")
@SessionScoped
public class HistoricoTransaccionesController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;
    @EJB
    IbParametrosFacade parametrosFacade;
    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbCanalesDAO canalesDAO;

    @EJB
    IbMenuDAO menuDAO;

    @EJB
    IbLogsDAO logsDAO;

    @EJB
    IbCanalFacade canalFacade;

    @EJB
    IbTransaccionesFacade transaccionFacade;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;
    
    @Inject
    CanalController canalController;
    
    private Date txtFechaDesde;                                             //input para la fecha desde
    private Date txtFechaHasta;                                             //input para la fecha hasta
    private Date calendarFechaMin;                                          //fecha minima para validacion del calendar
    private Date calendarFechaMax = new Date();                             //fecha maxima para validacion del calendar
    private String canalSelected;                                           //guarda el canal que es seleccionado en la lista desplegable
    private List<IbCanalDTO> listCanales;                                   //almacena la lista de canales
    private List<SelectItem> listSelectTrans;                               //lista de tipos de transacciones
    private String selectTransSelected;                                     //indica la transaccion seleccionada en el filtro
    private IbLogsDTO ibLogsDTO;                                            //objeto que almacenará el histórico de transacciones
    private IbLogsDTO transaccionSeleccionada;

    /**
     * método que limpia las variables que almacena data de la vista author
     * wilmer.rondon
     */
    public void limpiarHistTransacciones() {
        if (sesionController.isReiniciarForm()) {
            if (!FacesContext.getCurrentInstance().isPostback()) {
                txtFechaDesde = null;
                txtFechaHasta = null;
                canalSelected = null;
                selectTransSelected = null;
                ibLogsDTO = null;
                listSelectTrans = null;
                sesionController.setValidadorFlujo(1);
                sesionController.setReiniciarForm(false);
            }
        }
    }

    /**
     *
     * @return obtiene fecha maxima para validacion del calendar
     */
    public Date getCalendarFechaMax() {
        return calendarFechaMax;
    }

    /**
     *
     * @param calendarFechaMax inserta fecha maxima para validacion del calendar
     */
    public void setCalendarFechaMax(Date calendarFechaMax) {
        this.calendarFechaMax = calendarFechaMax;
    }

    /**
     *
     * @return obtiene fecha minima para validacion del calendar
     */
    public Date getCalendarFechaMin() {

        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, Integer.parseInt(parametrosController.getNombreParametro("pnw.logs.filtro.meses", sesionController.getIdCanal())));
        calendarFechaMin = c.getTime();
        return calendarFechaMin;
    }

    /**
     *
     * @param calendarFechaMin inserta fecha minima para validacion del calendar
     */
    public void setCalendarFechaMin(Date calendarFechaMin) {
        this.calendarFechaMin = calendarFechaMin;
    }

    /**
     *
     * @return obtiene el filtro de fecha desde
     */
    public Date getTxtFechaDesde() {
        return txtFechaDesde;
    }

    /**
     *
     * @param txtFechaDesde
     */
    public void setTxtFechaDesde(Date txtFechaDesde) {
        this.txtFechaDesde = txtFechaDesde;
    }

    /**
     *
     * @return obtiene el filtro de fecha hasta
     */
    public Date getTxtFechaHasta() {
        return txtFechaHasta;
    }

    /**
     *
     * @param txtFechaHasta
     */
    public void setTxtFechaHasta(Date txtFechaHasta) {
        this.txtFechaHasta = txtFechaHasta;
    }

    /**
     *
     * @return obtiene el filtro para canal
     */
    public String getCanalSelected() {
        return canalSelected;
    }

    /**
     *
     * @param canalSelected
     */
    public void setCanalSelected(String canalSelected) {
        this.canalSelected = canalSelected;
    }

    public IbCanalDTO getListCanales() {
        return canalesDAO.listadoCanales(sesionController.getUsuario().getCodUsuario(), sesionController.getNombreCanal(), sesionController.getIdCanal());
    }

    public void setListCanales(List<IbCanalDTO> listCanales) {
        this.listCanales = listCanales;
    }

    public List<SelectItem> getListSelectTrans() {
        return listSelectTrans;
    }

    public void setListSelectTrans(List<SelectItem> listSelectTrans) {
        this.listSelectTrans = listSelectTrans;
    }

    public String getSelectTransSelected() {
        return selectTransSelected;
    }

    public void setSelectTransSelected(String selectTransSelected) {
        this.selectTransSelected = selectTransSelected;
    }

    public IbLogsDTO getIbLogsDTO() {
        return ibLogsDTO;
    }

    public void setIbLogsDTO(IbLogsDTO ibLogsDTO) {
        this.ibLogsDTO = ibLogsDTO;
    }

    public IbLogsDTO getTransaccionSeleccionada() {
        return transaccionSeleccionada;
    }

    public void setTransaccionSeleccionada(IbLogsDTO transaccionSeleccionada) {
        this.transaccionSeleccionada = transaccionSeleccionada;
    }

    /**
     * Método que realiza la carga del Histórico de Transacciones cuando es
     * seleccionado desde el menú author wilmer.rondon
     */
    public void cargaGeneralHistTransacciones() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (ibLogsDTO == null) {
                ibLogsDTO = logsDAO.listadoHistoricoCliente(sesionController.getUsuario().getId().toString(), null, null, Integer.parseInt(parametrosController.getNombreParametro("pnw.logs.filtro.meses", sesionController.getIdCanal())), null, null, sesionController.getNombreCanal());

                if (!ibLogsDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    this.limipiarMensajesFacesContext();
                    FacesContext.getCurrentInstance().addMessage("formHistTrans:divDataTable", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                } else if (!ibLogsDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO) && !ibLogsDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_SIN_RESULTADOS_JPA)) {
                    this.limipiarMensajesFacesContext();
                    FacesContext.getCurrentInstance().addMessage("formHistTrans:divDataTable", new FacesMessage(FacesMessage.SEVERITY_WARN, "", ibLogsDTO.getRespuestaDTO().getTextoSP()));
                }
            }
        }
    }

    /**
     * Método que se encarga de realizar el filtro del histórico de
     * transacciones author wilmer.rondon
     *
     * @return String
     */
    public String filtrar() {

        if ((txtFechaDesde == null && txtFechaHasta == null && canalSelected == null && selectTransSelected == null)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formHistTrans:divDataTable",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.seleccioneUno", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.seleccioneUno", sesionController.getNombreCanal())));
            return "";
        } else if ((txtFechaDesde != null && txtFechaHasta == null) || (txtFechaDesde == null && txtFechaHasta != null)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formHistTrans:divDataTable",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.rangoFechaInv", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.rangoFechaInv", sesionController.getNombreCanal())));
            return "";
        } else if ((canalSelected == null && selectTransSelected != null)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formHistTrans:divDataTable",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.seleccioneCanal", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.seleccioneCanal", sesionController.getNombreCanal())));
            return "";
        } else {
            if (txtFechaDesde != null && txtFechaHasta != null) {
                //SE VERIFICA SI LA FECHA DESDE ES MAYOR A LA FECHA HASTA
                if (txtFechaDesde.compareTo(txtFechaHasta) > 0) {
                    this.limipiarMensajesFacesContext();
                    FacesContext.getCurrentInstance().addMessage("formHistTrans:divDataTable",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.rangoFechaInv", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.rangoFechaInv", sesionController.getNombreCanal())));
                    return "";
                }
            }

            String fechaDesde;
            String fechaHasta;
            if (txtFechaDesde == null) {
                fechaDesde = null;
            } else {
                fechaDesde = this.formatearFecha(txtFechaDesde, this.FORMATO_FECHA_SIMPLE);
            }

            if (txtFechaHasta == null) {
                fechaHasta = null;
            } else {
                fechaHasta = this.formatearFecha(txtFechaHasta, FORMATO_FECHA_SIMPLE);
            }

            ibLogsDTO = new IbLogsDTO();
            IbCanalDTO canal;
            String idTransaccion = null;
            String idCanal = null;

            if (canalSelected != null) {
                //Consultamos el canal en Base de Datos con el Valor que seleccionamos en la vista
                canal = canalController.getIbCanalDTO(canalSelected);
                idCanal = canal.getId().toString();
            }

            if (selectTransSelected != null) {
                idTransaccion = selectTransSelected;
            }

            ibLogsDTO = logsDAO.listadoHistoricoCliente(sesionController.getUsuario().getId().toString(), fechaDesde, fechaHasta, Integer.parseInt(parametrosController.getNombreParametro("pnw.logs.filtro.meses", sesionController.getIdCanal())), idTransaccion, idCanal, sesionController.getNombreCanal());

            if (!ibLogsDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formHistTrans:divDataTable", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return "";
            } else if (ibLogsDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_SIN_RESULTADOS_JPA)) {
                this.limipiarMensajesFacesContext();
                return "";
            } else if (!ibLogsDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formHistTrans:divDataTable", new FacesMessage(FacesMessage.SEVERITY_WARN, "", ibLogsDTO.getRespuestaDTO().getTextoSP()));
                return "";
            }
        }

        return parametrosController.getNombreParametro("pnw.consultas.url.histTransacciones", sesionController.getIdCanal());
    }

    public List<SelectItem> listTrans() {

        if (this.getCanalSelected() != null) {

            listSelectTrans = new ArrayList<>();

            //Consultamos el canal en Base de Datos con el Valor que seleccionamos en la vista
            IbCanalDTO canal = canalController.getIbCanalDTO(canalSelected);

            List<IbModulosDTO> modulos = new ArrayList<>(menuDAO.obtenerTodosModulosYTransacciones(canal.getId().toString(), sesionController.getNombreCanal()));

            for (IbModulosDTO m : modulos) {
                //llenado del select
                SelectItemGroup selectGroup = new SelectItemGroup(textosController.getNombreTexto(m.getNombre(), sesionController.getIdCanal()) + ":");

                SelectItem[] selectItemTemp = new SelectItem[m.getIbTransaccionesDTOCollection().size()];
                int cont = 0;
                for (IbTransaccionesDTO t : m.getIbTransaccionesDTOCollection()) {
                    String trans;
                    if (t.getIdCore().intValue() == 0) {
                        trans = t.getId().toString();
                    } else {
                        trans = t.getIdCore().toString();
                    }
                    selectItemTemp[cont++] = new SelectItem(trans, textosController.getNombreTexto(t.getNombre(), sesionController.getIdCanal()));
                }

                selectGroup.setSelectItems(selectItemTemp);

                listSelectTrans.add(selectGroup);
            }

        } else {
            listSelectTrans = new ArrayList<>();
        }

        return listSelectTrans;
    }

    /**
     * método usado para cargar los tipos de trasacciones a través del ajax
     * cuando en el filtro es seleccionado un canal author wilmer.rondon
     */
    public void cambiarListTrans() {
        this.listTrans();
    }

    /**
     * metodo action para ir a consulta de registro
     *
     * @param transaccionSeleccionada
     */
    public void verDetalleHistorico(IbLogsDTO transaccionSeleccionada) {
        this.transaccionSeleccionada = transaccionSeleccionada;
        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_HISTTRANSACCIONES, "", "", "Ver Detalle Transacción", "", "", "", "", "", "", "");
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.consultas.detalleMovimientoHistorico", sesionController.getIdCanal()));
    }

    /**
     * metodo action para ir a consulta de registro
     */
    public void consultaHistoricoTransacciones() {
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() - 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.consultas.url.histTransacciones", sesionController.getIdCanal()));
    }

    /**
     *
     * @param item
     * @return
     */
    public String obtieneDescripcion(IbLogsDTO item) {

        if (item.getDescripcion() != null && item.getIdTransaccionDTO() != null) {
            return textosController.getNombreTexto(item.getIdTransaccionDTO().getNombre(), sesionController.getIdCanal())
                    + " " + item.getDescripcion();
        } else {
            if (item.getIdTransaccionDTO() != null) {
                return textosController.getNombreTexto(item.getIdTransaccionDTO().getNombre(), sesionController.getIdCanal());
            } else {
                return "";
            }
        }
    }

    ////////////////////SECCION  DE REPORTES//////////////////////////
    /**
     * metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     */
    public void cuerpoHistoricoTransaccionesPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 11, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //Variables para el manejo del monto, Phrase espera siempre un String
            String monto;

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(HistoricoTransaccionesController.class.getResource("/imgPDF/logoBanner.png"));

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
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell fecha = new PdfPCell(new Phrase(fecha2(new Date()), fontTituloBold2));
            PdfPCell usuario = new PdfPCell(new Phrase(sesionController.getUsuario().getNombre(), fontTituloBold2));

            //Establecemos espaciado a la derecha
            fecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
            usuario.setHorizontalAlignment(Element.ALIGN_RIGHT);
            fecha.setBorder(0);
            usuario.setBorder(0);
            table.addCell(fecha);
            table.addCell(usuario);

            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            table = new PdfPTable(2);
            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell();
            PdfPCell cell1 = new PdfPCell();
            PdfPCell cell2 = new PdfPCell();
            PdfPCell cell3 = new PdfPCell();

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas0 = {1.95f, 3.05f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas0);
        //    table.getDefaultCell().setBorder(0);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(null);
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);
            table.getDefaultCell().setBorder(0);

            String fechaDesde;
            String fechaHasta;
            String nombreTrans = null;

            IbCanalDTO canal = canalController.getIbCanalDTO(canalSelected);

            //Consultamos la transacción en Base de Datos con el Valor que seleccionamos en la vista
            IbTransaccionesDTO transaccion = transaccionFacade.consultaTransaccionporID(selectTransSelected);

            nombreTrans = textosController.getNombreTexto(transaccion.getNombre(), sesionController.getIdCanal());

            if (txtFechaDesde == null || txtFechaHasta == null) {
                fechaDesde = null;
                table.addCell(new Phrase(""));
                table.addCell(new Phrase(""));
                fechaHasta = null;
                table.addCell(new Phrase(""));
                table.addCell(new Phrase(""));
            } else {
                fechaDesde = this.formatearFecha(txtFechaDesde, this.FORMATO_FECHA_SIMPLE);
                fechaHasta = this.formatearFecha(txtFechaHasta, this.FORMATO_FECHA_SIMPLE);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell1 = new PdfPCell(new Phrase(textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.fechDesde", sesionController.getIdCanal()) + ": "
                        + fechaDesde + "   " + textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.fechHasta", sesionController.getIdCanal()) + ": "
                        + fechaHasta, fontBold));

            }

            if (canal.getNombre() == null || transaccion.getNombre() == null) {

                table.addCell(new Phrase(""));
                table.addCell(new Phrase(""));
            } else {

                cell2 = new PdfPCell(new Phrase(textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.canal", sesionController.getIdCanal()) + ": " + canal.getNombre(), fontBold));
                cell3 = new PdfPCell(new Phrase(textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.transaccion", sesionController.getIdCanal()) + ": " + nombreTrans, fontBold));

            }
            cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));
            table.getDefaultCell().setRowspan(3);
            cell.setRowspan(3);
            cell.setBorder(0);
            cell.setMinimumHeight(30f);
            cell.setBackgroundColor(Color.white);
            table.addCell(cell);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell1.setBorder(0);
            cell1.setBackgroundColor(Color.white);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell1);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setBorder(0);
            cell2.setBackgroundColor(Color.white);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell2);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorder(0);
            cell3.setBackgroundColor(Color.white);
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell3);

            //Agregamos la tabla al PDF
            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            table = new PdfPTable(5);

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas2 = {0.55f, 0.55f, 0.55f, 0.95f, 0.55f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas2);
        //    table.getDefaultCell().setBorder(0);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            //Títulos de las columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.fecTransaccion", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.canal", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.referencia", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.descripcion", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.monto", sesionController.getIdCanal()), fontBold));

            table.getDefaultCell().setBackgroundColor(null);
            if (!ibLogsDTO.getIbLogsDTO().isEmpty()) {
                for (IbLogsDTO historico : ibLogsDTO.getIbLogsDTO()) {
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(new Phrase((historico.getFechaHoraDate() != null ? formatearFecha(historico.getFechaHoraDate(), FORMATO_FECHA_HORA_MINUTO) : ""), font));
                    table.addCell(new Phrase((String) (historico.getIdCanalDTO().getNombre() != null ? historico.getIdCanalDTO().getNombre() : ""), font));
                    table.addCell(new Phrase((historico.getReferencia() != null ? historico.getReferencia() : ""), font));
                    table.addCell(new Phrase((String) (historico.getDescripcion() != null ? historico.getDescripcion() : ""), font));

                    if (historico.getMonto() != null) {
                        monto = formatearMonto(historico.getMonto());
                    } else {
                        monto = " - ";
                    }
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(new Phrase(monto, font));

                }

            } else {
                //Si no hay data recuperada, se muestra un mensaje
                table.getDefaultCell().setColspan(5);
                table.getDefaultCell().setPaddingTop(3);
                table.getDefaultCell().setPaddingBottom(3);
                table.getDefaultCell().setPaddingRight(3);
                table.getDefaultCell().setPaddingLeft(3);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.dataTable.texto.vacio", sesionController.getIdCanal()), font));

            }

            //Añadimos la tabla al PDF
            // pdf.setMargins(150f, 20f, 50f, 10f);
            pdf.add(table);
            pdf.setMargins(50f, 3f, 22f, 18f);
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
    public void detalleHistoricoTransaccionesPDF() throws IOException, DocumentException {

        try {

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-disposition", "attachment;filename=\"HistoricoTransacciones.pdf\"");
            Document document = new Document(PageSize.A4.rotate());//(), 20, 20, 20, 20);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            //Instanciamos la clase utilitaria que realiza la paginación
            PaginarPDF paginar = new PaginarPDF();
            Rectangle headerBox = new Rectangle(22, 45, 1320, 700);

            //Establecemos el tamaño del documento
            writer.setBoxSize("headerBox", headerBox);

            //Pasamos por parámetros los eventos que se producen al generar el PDF
            writer.setPageEvent(paginar);

            document.open();

            //Invocamos el método que genera el PDF
            this.cuerpoHistoricoTransaccionesPDF(document);

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

    //Detalle de la transacción
    public void cuerpoDetalleHistoricoTransaccionPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 11, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //Variables para el manejo de la data
            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(HistoricoTransaccionesController.class.getResource("/imgPDF/logoBanner.png"));

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
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell fecha = new PdfPCell(new Phrase(fecha2(new Date()), fontTituloBold2));
            PdfPCell usuario = new PdfPCell(new Phrase(sesionController.getUsuario().getNombre(), fontTituloBold2));

            //Establecemos espaciado a la derecha
            fecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
            usuario.setHorizontalAlignment(Element.ALIGN_RIGHT);
            fecha.setBorder(0);
            usuario.setBorder(0);
            table.addCell(fecha);
            table.addCell(usuario);

            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            table = new PdfPTable(2);

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas0 = {1.95f, 3.05f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas0);
        //    table.getDefaultCell().setBorder(0);

            //    cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));
            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.consultaHistTran.descargarPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));

            cell.setColspan(2);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(20f);
            table.addCell(cell);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            pdf.add(new Paragraph(" "));

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(null);
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            //  table.getDefaultCell().setLeading(1.5f, 1.5f);//Espacio entre lineas
            table.setWidthPercentage(70f);

            //Títulos de las Columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaHistTran.descargarPdf.fechaHora", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(fecha2(transaccionSeleccionada.getFechaHoraDate()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaHistTran.descargarPdf.ctaOrigen", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(transaccionSeleccionada.getCuentaOrigen(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaHistTran.descargarPdf.nomBeneficiario", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(transaccionSeleccionada.getNombreBeneficiario(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaHistTran.descargarPdf.identificacion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(transaccionSeleccionada.getTipoDoc() + " - " + transaccionSeleccionada.getDocumento(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaHistTran.descargarPdf.ctaDestino", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(transaccionSeleccionada.getCuentaDestino(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaHistTran.descargarPdf.monto", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase("Bs. " + formatearMonto(transaccionSeleccionada.getMonto()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaHistTran.descargarPdf.referencia", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(transaccionSeleccionada.getReferencia(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaHistTran.descargarPdf.descripcion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(transaccionSeleccionada.getDescripcion(), font));

            //Agregamos la tabla al PDF
            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            //Añadimos la tabla al PDF
            // pdf.setMargins(150f, 20f, 50f, 10f);
            pdf.setMargins(50f, 3f, 22f, 18f);
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
    public void detalleHistoricoTransaccionPDF() throws IOException, DocumentException {

        try {

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-disposition", "attachment;filename=\"DetalleHistoricoTransaccion.pdf\"");
            Document document = new Document(PageSize.A4.rotate());//(), 20, 20, 20, 20);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            //Instanciamos la clase utilitaria que realiza la paginación
            PaginarPDF paginar = new PaginarPDF();
            Rectangle headerBox = new Rectangle(22, 45, 1320, 700);

            //Establecemos el tamaño del documento
            writer.setBoxSize("headerBox", headerBox);

            //Pasamos por parámetros los eventos que se producen al generar el PDF
            writer.setPageEvent(paginar);

            document.open();

            //Invocamos el método que genera el PDF
            this.cuerpoDetalleHistoricoTransaccionPDF(document);

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

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_HISTTRANSACCIONES)) {
                sesionController.setValidadorFlujo(1);
                this.consultaHistoricoTransacciones();
            }
        }
    }
}
