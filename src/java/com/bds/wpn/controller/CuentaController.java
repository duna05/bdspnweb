/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.CuentaDAO;
import com.bds.wpn.dao.ReclamosDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.MesAnoDTO;
import com.bds.wpn.dto.MovimientoCuentaDTO;
import com.bds.wpn.dto.PosicionConsolidadaDTO;
import com.bds.wpn.dto.ReclamoDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.TransaccionesCuentasDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.fecha2;
import static com.bds.wpn.util.BDSUtil.formatearMonto;
import com.bds.wpn.util.EstadoCuentaHeadFoot;
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
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
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
 * @author wilmer.rondon
 */
/**
 * controlador de todas las transacciones relacionadas con cuentas
 */
@Named("wpnCuentaController")
@SessionScoped
public class CuentaController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;

    //@Inject
    //ErroresController erroresController;
    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    CuentaDAO cuentaDAO;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    ReclamosDAO reclamosDAO;

    @Inject
    TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    private String tipoCuenta = "";                                     //tipo de cuenta del cliente corriente BCC o ahorros BCA
    private String cuentaSeleccionada = "";                             //Cuenta seleccionada en el select
    private CuentaDTO cuentaDTO;                                        //Objeto para almacenar la cuenta seleccionada
    private CuentaDTO cuentaDTOSelect;                                  //objeto para almacenar la cuenta de la lista desplegable
    private PosicionConsolidadaDTO posicionDTO;                         //objeto que almacena todos los productos del cliente
    private Collection<MovimientoCuentaDTO> listMovimientoCuentaDTO;    //collection para movimientos de la cuenta
    private CuentaDTO listSaldoBloqueado;
    private CuentaDTO listSaldoDiferido;
    private ClienteDTO clienteDTO;                                      //objeto para almacenar la informacion del cliente
    private Date txtFechaDesde;                            //input para la fecha desde
    private Date txtFechaHasta;                            //input para la fecha hasta
    private String msjVacio = "";                                           //mensaje predeterminado si no tiene registros de movimientos
    private Date calendarFechaMax = new Date();                                          //fecha maxima para validacion del calendar
    private Date calendarFechaMin;                                          //fecha minima para validacion del calendar
    private MovimientoCuentaDTO movimientoCuentaDTO;                        //objeto que contiene los moviemintos de una cuenta
    private Collection<CuentaDTO> listAhorroCorrienteCuentaDTO;             //collection de la las cuentas ahorro y corrientes de un cliente
    private boolean consultaNueva = true;                                   //indicador para identificar las consultas por defecto de movimientos de cuenta
    private String tipoTransaccionMov;
    private String nroReferencia;
    private String mensaje;
    private String tipoTransaccionFiltroSelected;
    private String mesAnoSelected;
    private String mes;
    private String ano;
    private String fechaTrans;

//indica la transaccion seleccionada en el filtro    
    private boolean detalleMovimiento = false;                                      //indica si se ha accedido al detalle de un movimiento

    ////////////////////SECCION  DE REPORTES//////////////////////////
    /**
     * metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     */
    public void cuerpoDetalleCuentaPDF(Document pdf) {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 14, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //Variables para el manejo de los saldos, Phrase espera siempre un String
            String saldoDiferido;
            String saldoBloqueado;
            String saldoDisponible;
            String saldoTotal;
            String ingreso;
            String egreso;
            String saldo = "0.0";

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(CuentaController.class.getResource("/imgPDF/logoBanner.png"));

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

            table = new PdfPTable(4);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell(new Phrase(cuentaDTO.getNombreProducto() + " " + formatoAsteriscosWeb(cuentaDTO.getNumeroCuenta()), fontTituloBold));
            cell.setPaddingBottom(5);
            cell.setColspan(4);
            cell.setBorder(0);
            table.addCell(cell);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Títulos de las Columnas
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.nombreProducto", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.tipoCta", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.nroCta", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.moneda", sesionController.getIdCanal()), fontBold));

            //Contenido de las Columnas, data
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setBackgroundColor(null);
            table.addCell(new Phrase((cuentaDTO.getNombreProducto() != null ? cuentaDTO.getNombreProducto() : ""), font));
            table.addCell(new Phrase((cuentaDTO.getCodigoTipoProducto() != null ? cuentaDTO.getCodigoTipoProducto() : ""), font));
            table.addCell(new Phrase((cuentaDTO.getNumeroCuenta() != null ? cuentaDTO.getNumeroCuenta() : ""), font));
            table.addCell(new Phrase((cuentaDTO.getSiglasTipoMoneda() != null ? cuentaDTO.getSiglasTipoMoneda() : ""), font));

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

            //Títulos de las columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.saldoDiferido", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.saldoBloqueado", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.saldoDisponible", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.saldoTotal", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(null);

            if (cuentaDTO.getSaldoDiferido() != null) {
                saldoDiferido = formatearMonto(cuentaDTO.getSaldoDiferido());
            } else {
                saldoDiferido = "0.0";
            }
            if (cuentaDTO.getSaldoBloqueado() != null) {
                saldoBloqueado = formatearMonto(cuentaDTO.getSaldoBloqueado());
            } else {
                saldoBloqueado = "0.0";
            }
            if (cuentaDTO.getSaldoDisponible() != null) {
                saldoDisponible = formatearMonto(cuentaDTO.getSaldoDisponible());
            } else {
                saldoDisponible = "0.0";
            }
            if (cuentaDTO.getSaldoTotal() != null) {
                saldoTotal = formatearMonto(cuentaDTO.getSaldoTotal());
            } else {
                saldoTotal = "0.0";
            }

            //Contenido de las columnas
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase(saldoDiferido, font));
            table.addCell(new Phrase(saldoBloqueado, font));
            table.addCell(new Phrase(saldoDisponible, font));
            table.addCell(new Phrase(saldoTotal, font));

            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            //Asignamos celdas a la tabla, 6 columnas
            table = new PdfPTable(6);
            //Le asigne el tama�o a la cada una de las celdas de acuerdo con la cantidad de columnas que especifico arriba
            float[] medidaCeldasSaldos = {0.55f, 0.55f, 1.55f, 0.55f, 0.55f, 0.55f};

            // Asigno las medidas a las tabla (El ancho)
            table.setWidths(medidaCeldasSaldos);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            // PdfPCell tituloMovimientos = new PdfPCell(new Phrase("DETALLES DE MOVIMIENTOS", fontTituloBold));
            //   table.addCell(tituloMovimientos);
            cell.setColspan(3);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

            //Títulos de las columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.fecha", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.nroReferencia", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.descripcion", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.ingreso", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.egreso", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detCta.descargaPdf.saldo", sesionController.getIdCanal()), fontBold));

            //Contenido de las columnas, recorremos los registros recuperados de movimientos
            table.getDefaultCell().setBackgroundColor(null);
            if (listMovimientoCuentaDTO != null && !listMovimientoCuentaDTO.isEmpty()) {
                for (MovimientoCuentaDTO mov : listMovimientoCuentaDTO) {
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(new Phrase((mov.getFechaTransaccionString() != null ? mov.getFechaTransaccionString() : ""), font));
                    table.addCell(new Phrase((mov.getNumeroReferencia() != null ? mov.getNumeroReferencia() : ""), font));
                    table.addCell(new Phrase((mov.getDescripcion() != null ? mov.getDescripcion() : ""), font));

                    if (mov.getIngreso() != null) {
                        ingreso = formatearMonto(mov.getIngreso());
                    } else {
                        ingreso = "0.0";
                    }
                    if (mov.getEgreso() != null) {
                        egreso = formatearMonto(mov.getEgreso());
                    } else {
                        egreso = "0.0";
                    }
                    if (mov.getMonto() != null) {
                        saldo = formatearMonto(mov.getMonto());
                    } else {
                        saldo = "0.0";
                    }
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);

                    table.addCell(new Phrase(ingreso, font));
                    table.addCell(new Phrase(egreso, font));
                    table.addCell(new Phrase(saldo, font));

                }

            } else {
                //Si no hay data recuperada, se muestra un mensaje
                table.getDefaultCell().setColspan(6);
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
    public void detalleCuentaPDF() throws IOException, DocumentException {

        String nombreDocumento;

        try {

            if (this.detalleMovimiento) {
                nombreDocumento = "DetalleMovimientoCuenta.pdf";
            } else {
                nombreDocumento = "DetalleDeCuenta.pdf";
            }

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.reset();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
//            response.addHeader("Content-disposition", "attachment;filename=\"DetalleDeCuenta.pdf\"");
            response.addHeader("Content-disposition", "attachment;filename=\"" + nombreDocumento + "\"");
            Document document = new Document(PageSize.A4.rotate());//(), 20, 20, 20, 20);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            //Instanciamos la clase utilitaria que realiza la paginación
            PaginarPDF paginar = new PaginarPDF();
            //35 representa el bottom
            //1415 representa el left
            //680 representa el top
            //22 representa el right
            Rectangle headerBox = new Rectangle(22, 45, 1320, 700);
            /**/
            //Establecemos el tamaño del documento
            writer.setBoxSize("headerBox", headerBox);
            //Pasamos por parámetros los eventos que se producen al generar el PDF
            writer.setPageEvent(paginar);
            /**/
            document.open();

            //Invocamos el método que genera el PDF
            if (detalleMovimiento) {
                this.cuerpoDetalleMovimientoPDF(document);
            } else {
                this.cuerpoDetalleCuentaPDF(document);
            }

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
     * metodo encargado de generar el contenido del detalle de un movimiento de
     * cuenta Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     * @param writer
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoDetalleMovimientoPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 11, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            String monto;

            //Variables para el manejo de la data
            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(CuentaController.class.getResource("/imgPDF/logoBanner.png"));

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

            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));

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
            table.getDefaultCell().setPaddingLeft(3);
            table.setWidthPercentage(70f);

            //Títulos de las Columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.fecha", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(movimientoCuentaDTO.getFechaTransaccionString(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.tipoOperacion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.movimientoCuentaDTO.getDescripcionTipoOp(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.numReferencia", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.movimientoCuentaDTO.getNumeroReferencia(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.agencia", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.movimientoCuentaDTO.getNombreAgencia(), font));

            //Inicio débitos
            if (this.movimientoCuentaDTO.getDebitoCredito().equalsIgnoreCase("A")) {
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.ctaOrigen", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(formatoAsteriscosWeb(this.movimientoCuentaDTO.getNumeroCuentaOrigen()), font));

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.ctaDestino", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(formatoAsteriscosWeb(this.movimientoCuentaDTO.getNumeroCuentaDestino()), font));

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.nombreEmisor", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(this.movimientoCuentaDTO.getNombreEmisor(), font));

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.nombreBeneficiario", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(this.movimientoCuentaDTO.getNombreBeneficiario(), font));
            }

            if (this.movimientoCuentaDTO.getDebitoCredito().equalsIgnoreCase("D")) {
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.ctaOrigen", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(formatoAsteriscosWeb(this.movimientoCuentaDTO.getNumeroCuentaOrigen()), font));

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.nombreEmisor", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(this.movimientoCuentaDTO.getNombreEmisor(), font));
            }

            if (this.movimientoCuentaDTO.getDebitoCredito().equalsIgnoreCase("O")) {
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.ctaOrigen", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(formatoAsteriscosWeb(this.movimientoCuentaDTO.getNumeroCuentaOrigen()), font));

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.nombreEmisor", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(this.movimientoCuentaDTO.getNombreEmisor(), font));
            }

            if (this.movimientoCuentaDTO.getDebitoCredito().equalsIgnoreCase("C")) {
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.ctaDestino", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(formatoAsteriscosWeb(this.movimientoCuentaDTO.getNumeroCuentaDestino()), font));

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.nombreBeneficiario", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(this.movimientoCuentaDTO.getNombreBeneficiario(), font));
            }

            //fin débitos
            //Inicio tipo transacción
            if (this.tipoTransaccionMov.equalsIgnoreCase("CMT")) {
            }

            if (this.tipoTransaccionMov.equalsIgnoreCase("CTBDS")) {
            }

            if (this.tipoTransaccionMov.equalsIgnoreCase("CTR")) {
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.nombreBcoOrigen", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(this.movimientoCuentaDTO.getNombreBancoOrigen(), font));
            }

            if (this.tipoTransaccionMov.equalsIgnoreCase("CTE")) {
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.nombreBcoDestino", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(this.movimientoCuentaDTO.getNombreBancoDestino(), font));
            }

            if (this.tipoTransaccionMov.equalsIgnoreCase("CMME")) {
                //AGREGAR TIPO DE DEPOSITO CHEQUE O EFECTIVO
            }

            //fin tipo transacción
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.descripcion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.movimientoCuentaDTO.getDescripcion(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.detMovCta.descargaPdf.monto", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));

            if (this.movimientoCuentaDTO.getMonto() != null) {
                monto = formatearMonto(this.movimientoCuentaDTO.getMonto());
            } else {
                monto = "0.0";
            }

            table.addCell(new Phrase("Bs.: " + monto, font));

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
            e.printStackTrace();
        }
    }

    public CuentaDTO getCuentaDTO() {
        return cuentaDTO;
    }
    
    /**
     * Metodo convierte un fecha de formato completo  dd/MM/yyyy hh:mm:ss a un formato simple dd/MM/yyyy dependiendo de la transaccion
    
     */
    public String getFormatFechaTransaccion(){
        Date fechaTransaccion;
        if(tipoTransaccionMov !=COD_TRANSF_P2P){            
            fechaTransaccion = BDSUtil.formatearFechaStringADate(this.movimientoCuentaDTO.getFechaTransaccionString(), FORMATO_FECHA_SIMPLE);
            return  BDSUtil.formatearFecha(fechaTransaccion, FORMATO_FECHA_SIMPLE);
        }else{
            return  this.movimientoCuentaDTO.getFechaTransaccionString();
        } 
    }
    /**
     * Metodo que obtiene el detalle del movimiento seleccionado
     *
     * @param secuenciaExtMovimiento codigo extendido del movimiento a consultar
     */
    public void obtenerDetalleMovimiento(String secuenciaExtMovimiento) {
        tipoTransaccionMov = null;
        movimientoCuentaDTO = cuentaDAO.detalleMovimiento(secuenciaExtMovimiento, sesionController.getIdCanal(), sesionController.getNombreCanal());
        
        detalleMovimiento = true;

        if (!movimientoCuentaDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            this.limipiarMensajesFacesContext();
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.detCta.url.detalle", sesionController.getIdCanal()));
            return;
        } else {
            if (!movimientoCuentaDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", movimientoCuentaDTO.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.detCta.url.detalle", sesionController.getIdCanal()));
                return;
            }
        }

        if (movimientoCuentaDTO.getCodigoAplicacion().equals(CODIGO_PRODUCTO_CA)
                || movimientoCuentaDTO.getCodigoAplicacion().equals(CODIGO_PRODUCTO_CC)) {
            switch (movimientoCuentaDTO.getTipoTransaccion()) {
                case "21": {
                    tipoTransaccionMov = COD_MOV_TAQUILLA;
                    break;
                }
                case "4": {
                    tipoTransaccionMov = COD_MOV_TAQUILLA;
                    break;
                }
                case "52": {
                    tipoTransaccionMov = COD_MOV_TAQUILLA;
                    break;
                }
                case "900": {
                    tipoTransaccionMov = COD_TRANSF_DELSUR;
                    break;
                }
                case "901": {
                    tipoTransaccionMov = COD_TRANSF_DELSUR;
                    break;
                }
                case "395": {
                    tipoTransaccionMov = COD_TRANSF_RECIBIDA;
                    break;
                }
                case "569": {
                    tipoTransaccionMov = COD_TRANSF_RECIBIDA;
                    break;
                }
                case "394": {
                    tipoTransaccionMov = COD_TRANSF_ENVIADA;
                    break;
                }
                case "560": {
                    tipoTransaccionMov = COD_TRANSF_ENVIADA;
                    break;
                }
                case "821": {
                    tipoTransaccionMov = COD_TRANSF_P2P;
                    break;
                }
                case "738": {
                    tipoTransaccionMov = COD_TRANSF_P2P;
                    break;
                }
                case "822": {
                    tipoTransaccionMov = COD_TRANSF_P2P;
                    break;
                }
                case "739": {
                    tipoTransaccionMov = COD_TRANSF_P2P;
                    break;
                }
                case "758": {
                    tipoTransaccionMov = COD_TRANSF_P2P;
                    break;
                }
                case "719": {
                    tipoTransaccionMov = COD_TRANSF_P2P;
                    break;
                }
                case "759": {
                    tipoTransaccionMov = COD_TRANSF_P2P;
                    break;
                }
                case "720": {
                    tipoTransaccionMov = COD_TRANSF_P2P;
                    break;
                }
                default: {
                    tipoTransaccionMov = COD_MOV_TAQUILLA;
                }
            }
        } else {
            if (movimientoCuentaDTO.getCodigoAplicacion().equals(CODIGO_PRODUCTO_ME)) {
                tipoTransaccionMov = COD_MOV_ME;
            }
        }

        //registro en bitacora
        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_DET_MOV_CTA, "", "", "Consulta Detalle de Movimiento", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cuentas.url.detalleMovimiento", sesionController.getIdCanal()));
    }

    /**
     * Metodo para volver atras (detalle de cuenta) de detalle de movimiento
     */
    public void actionAtrasMov() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.detCta.url.detalle", sesionController.getIdCanal()));
    }

    /**
     * Metodo que retorna el detalle de una cuenta de ahorros
     *
     * @param valor
     */
    public void obtenerDetalleCuentaCA(String valor) {
        this.limpiar();
        String numeroCta = valor;
        cuentaDTO = new CuentaDTO(this.sesionController.getSemilla());
        cuentaSeleccionada = numeroCta;
        CuentaDTO cuentaDTOMov = new CuentaDTO(this.sesionController.getSemilla());
        listMovimientoCuentaDTO = new ArrayList<>();
        tipoCuenta = CODIGO_PRODUCTO_CA;
        String fechaDesde = null;
        String fechaHasta = null;
        detalleMovimiento = false;
        boolean validacionesSeguridad = sesionController.productosCliente(cuentaDTO.getDesEnc().desencriptar(cuentaSeleccionada));
        if (validacionesSeguridad) {
            cuentaDTO = cuentaDAO.obtenerDetalleCuenta(tipoCuenta, cuentaDTO.getDesEnc().desencriptar(cuentaSeleccionada), sesionController.getIdCanal(), sesionController.getNombreCanal());
            cuentaDTO.setSemilla(sesionController.getSemilla());

            if (!cuentaDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return;
            } else if (!cuentaDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTO.getRespuestaDTO().getTextoSP()));
                return;
            }

            if (!consultaNueva) {
                fechaDesde = fecha(this.getTxtFechaDesde());
                fechaHasta = fecha(this.getTxtFechaHasta());
            }

            if ((fechaDesde == null || fechaDesde.isEmpty()) && (fechaHasta == null || fechaHasta.isEmpty())) {
                //si las fechas vienen nulas o vacias seteamos la fecha del mes actual
                Calendar date = Calendar.getInstance();
                date.set(Calendar.DAY_OF_MONTH, 1);
                fechaDesde = formatearFecha(date.getTime(), FORMATO_FECHA_SIMPLE);
                //se calculan los mov del mes actual desde el 1 hasta el dia de hoy ya que si esta fecha supera el dia actual el SP arroja un ERROR
                date = Calendar.getInstance();
                fechaHasta = formatearFecha(date.getTime(), FORMATO_FECHA_SIMPLE);

            }

            cuentaDTOMov = cuentaDAO.listarMovimientos(tipoCuenta, cuentaDTO.getDesEnc().desencriptar(cuentaSeleccionada), fechaDesde, fechaHasta,
                    parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.orden.desc", sesionController.getIdCanal()),
                    sesionController.getIdCanal(), sesionController.getNombreCanal());
            cuentaDTOMov.setSemilla(sesionController.getSemilla());

            if (!cuentaDTOMov.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return;
            } else if (!cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO) && !cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_SIN_RESULTADOS_JPA)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTOMov.getRespuestaDTO().getTextoSP()));
                return;
            }

        } else {
            RespuestaDTO respuesta = new RespuestaDTO();
            respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
            respuesta.setTextoSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
            cuentaDTO.setRespuestaDTO(respuesta);

            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTO.getRespuestaDTO().getTextoSP()));
        }

        listMovimientoCuentaDTO = cuentaDTOMov.getMovimientosDTO();

        consultaNueva = false;

        //registro en bitacora
        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_DET_CTA, "", "", "Consulta Detalle de Cuenta Ahorro", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
        cuentaSeleccionada = cuentaDTO.getDesEnc().encriptar(tipoCuenta) + ":" + cuentaSeleccionada;
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.consulta.url.detalleCuenta", sesionController.getIdCanal()));
    }

    /**
     * Metodo que retorna el detalle de una cuenta corriente
     *
     * @param valor
     */
    public void obtenerDetalleCuentaCC(String valor) {

        this.limpiar();
        String numeroCta = valor;
        cuentaDTO = new CuentaDTO(this.sesionController.getSemilla());
        cuentaSeleccionada = numeroCta;
        CuentaDTO cuentaDTOMov = new CuentaDTO(this.sesionController.getSemilla());
        listMovimientoCuentaDTO = new ArrayList<>();
        tipoCuenta = CODIGO_PRODUCTO_CC;
        String fechaDesde = null;
        String fechaHasta = null;
        boolean validacionesSeguridad = sesionController.productosCliente(cuentaDTO.getDesEnc().desencriptar(cuentaSeleccionada));
        detalleMovimiento = false;
        if (validacionesSeguridad) {
            cuentaDTO = cuentaDAO.obtenerDetalleCuenta(tipoCuenta, cuentaDTO.getDesEnc().desencriptar(cuentaSeleccionada), sesionController.getIdCanal(), sesionController.getNombreCanal());
            cuentaDTO.setSemilla(sesionController.getSemilla());
            if (!cuentaDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return;
            } else if (!cuentaDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTO.getRespuestaDTO().getTextoSP()));
                return;
            }

            if (!consultaNueva) {
                fechaDesde = fecha(this.getTxtFechaDesde());
                fechaHasta = fecha(this.getTxtFechaHasta());
            }

            if ((fechaDesde == null || fechaDesde.isEmpty()) && (fechaHasta == null || fechaHasta.isEmpty())) {
                //si las fechas vienen nulas o vacias seteamos la fecha del mes actual
                Calendar date = Calendar.getInstance();
                date.set(Calendar.DAY_OF_MONTH, 1);
                fechaDesde = formatearFecha(date.getTime(), FORMATO_FECHA_SIMPLE);
                //se calculan los mov del mes actual desde el 1 hasta el dia de hoy ya que si esta fecha supera el dia actual el SP arroja un ERROR
                date = Calendar.getInstance();
                fechaHasta = formatearFecha(date.getTime(), FORMATO_FECHA_SIMPLE);

            }

            cuentaDTOMov = cuentaDAO.listarMovimientos(tipoCuenta, cuentaDTO.getDesEnc().desencriptar(cuentaSeleccionada), fechaDesde, fechaHasta,
                    parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.orden.desc", sesionController.getIdCanal()),
                    sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (!cuentaDTOMov.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return;
            } else if (!cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO) && !cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_SIN_RESULTADOS_JPA)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTOMov.getRespuestaDTO().getTextoSP()));
                return;
            }

        } else {
            RespuestaDTO respuesta = new RespuestaDTO();
            respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
            respuesta.setTextoSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
            cuentaDTO.setRespuestaDTO(respuesta);

            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTO.getRespuestaDTO().getTextoSP()));
        }

        listMovimientoCuentaDTO = cuentaDTOMov.getMovimientosDTO();

        //registro en bitacora
        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_DET_CTA, "", "", "Consulta Detalle de Cuenta Corriente", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
        cuentaSeleccionada = cuentaDTO.getDesEnc().encriptar(tipoCuenta) + ":" + cuentaSeleccionada;

        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.consulta.url.detalleCuenta", sesionController.getIdCanal()));
    }

    /**
     * Metodo que retorna el detalle de una cuenta de moneda extranjera
     *
     * @param valor
     */
    public void obtenerDetalleCuentaME(String valor) {

        this.limpiar();
        String numeroCta = valor;
        cuentaDTO = new CuentaDTO(this.sesionController.getSemilla());
        cuentaSeleccionada = numeroCta;
        CuentaDTO cuentaDTOMov = new CuentaDTO(this.sesionController.getSemilla());
        listMovimientoCuentaDTO = new ArrayList<>();
        tipoCuenta = CODIGO_PRODUCTO_ME;
        String fechaDesde = null;
        String fechaHasta = null;
        boolean validacionesSeguridad = sesionController.productosCliente(cuentaDTO.getDesEnc().desencriptar(cuentaSeleccionada));
        if (validacionesSeguridad) {
            cuentaDTO = cuentaDAO.obtenerDetalleCuenta(tipoCuenta, cuentaDTO.getDesEnc().desencriptar(numeroCta), sesionController.getIdCanal(), sesionController.getNombreCanal());
            cuentaDTO.setSemilla(sesionController.getSemilla());
            if (!consultaNueva) {
                fechaDesde = fecha(this.getTxtFechaDesde());
                fechaHasta = fecha(this.getTxtFechaHasta());
            }

            consultaNueva = false;

            cuentaDTOMov = cuentaDAO.listarMovimientos(tipoCuenta, cuentaDTO.getDesEnc().desencriptar(numeroCta), fechaDesde, fechaHasta,
                    parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.orden.desc", sesionController.getIdCanal()),
                    sesionController.getIdCanal(), sesionController.getNombreCanal());

            //registro en bitacora
            sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_DET_CTA, "", "", "Consulta Detalle de Cuenta Moneda Extranjera", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
        } else {
            RespuestaDTO respuesta = new RespuestaDTO();
            respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
            respuesta.setTextoSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
            cuentaDTO.setRespuestaDTO(respuesta);
        }
        if (!cuentaDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !cuentaDTOMov.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        } else if (cuentaDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && !cuentaDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTO.getRespuestaDTO().getTextoSP()));
        }

        listMovimientoCuentaDTO = cuentaDTOMov.getMovimientosDTO();
        cuentaSeleccionada = cuentaDTO.getDesEnc().encriptar(tipoCuenta) + ":" + cuentaSeleccionada;

        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.consulta.url.detalleCuentaME", sesionController.getIdCanal()));

    }

    /*GETTERS Y SETTERS*/
    public boolean isDetalleMovimiento() {
        return detalleMovimiento;
    }

    public void setDetalleMovimiento(boolean detalleMovimiento) {
        this.detalleMovimiento = detalleMovimiento;
    }

    public void setCuentaDTO(CuentaDTO cuentaDTO) {
        this.cuentaDTO = cuentaDTO;
    }

    public Collection<MovimientoCuentaDTO> getListMovimientoCuentaDTO() {
        return listMovimientoCuentaDTO;
    }

    public void setListMovimientoCuentaDTO(Collection<MovimientoCuentaDTO> listMovimientoCuentaDTO) {
        this.listMovimientoCuentaDTO = listMovimientoCuentaDTO;
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

    public MovimientoCuentaDTO getMovimientoCuentaDTO() {
        return movimientoCuentaDTO;
    }

    public void setMovimientoCuentaDTO(MovimientoCuentaDTO movimientoCuentaDTO) {
        this.movimientoCuentaDTO = movimientoCuentaDTO;
    }

    public String getNroReferencia() {
        return nroReferencia;
    }

    public void setNroReferencia(String nroReferencia) {
        this.nroReferencia = nroReferencia;
    }

    public String getTipoTransaccionMov() {
        return tipoTransaccionMov;
    }

    public void setTipoTransaccionMov(String tipoTransaccionMov) {
        this.tipoTransaccionMov = tipoTransaccionMov;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * metodo que obtiene el listado de cuentas de ahorro y corriente de un
     * cliente y la retorna unidas
     *
     * @return listado de cuentas ahorro y corriente
     */
    public Collection<CuentaDTO> getListAhorroCorrienteCuentaDTO() {
        clienteDTO = clienteDAO.listadoCuentasClientes(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());

        if (clienteDTO.getRespuestaDTO() != null) {
            if (!clienteDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            }
        }

        if (clienteDTO.getCuentasAhorroDTO() != null && !clienteDTO.getCuentasAhorroDTO().isEmpty()) {
            for (int i = 0; i < clienteDTO.getCuentasAhorroDTO().size(); i++) {
                clienteDTO.getCuentasAhorroDTO().get(i).setSemilla(sesionController.getSemilla());

            }
        }
        if (clienteDTO.getCuentasCorrienteDTO() != null && !clienteDTO.getCuentasCorrienteDTO().isEmpty()) {
            for (int i = 0; i < clienteDTO.getCuentasCorrienteDTO().size(); i++) {
                clienteDTO.getCuentasCorrienteDTO().get(i).setSemilla(sesionController.getSemilla());

            }
        }
        return listAhorroCorrienteCuentaDTO = clienteDTO.getCuentasAhorroCorrienteDTO();
    }

    public void setListAhorroCorrienteCuentaDTO(Collection<CuentaDTO> listAhorroCorrienteCuentaDTO) {
        this.listAhorroCorrienteCuentaDTO = listAhorroCorrienteCuentaDTO;
    }

    public String getCuentaSeleccionada() {
        return cuentaSeleccionada;
    }

    public void setCuentaSeleccionada(String cuentaSeleccionada) {
        this.cuentaSeleccionada = cuentaSeleccionada;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public boolean isConsultaNueva() {
        return consultaNueva;
    }

    public void setConsultaNueva(boolean consultaNueva) {
        this.consultaNueva = consultaNueva;
    }

    public String getTipoTransaccionFiltroSelected() {
        return tipoTransaccionFiltroSelected;
    }

    public void setTipoTransaccionFiltroSelected(String tipoTransaccionFiltroSelected) {
        this.tipoTransaccionFiltroSelected = tipoTransaccionFiltroSelected;
    }

    public CuentaDTO getListSaldoBloqueado() {
        return listSaldoBloqueado;
    }

    public void setListSaldoBloqueado(CuentaDTO listSaldoBloqueado) {
        this.listSaldoBloqueado = listSaldoBloqueado;
    }

    public CuentaDTO getListSaldoDiferido() {
        return listSaldoDiferido;
    }

    public void setListSaldoDiferido(CuentaDTO listSaldoDiferido) {
        this.listSaldoDiferido = listSaldoDiferido;
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
     * metodo para filtrar los movimientos de la cuenta por fecha, por tipo de
     * transaccion y por nro de referencia
     *
     * @return lista de movimientos filtrada
     */
    public String filtrar() {
        Collection<MovimientoCuentaDTO> listMovimientoCuentaDTOtemp;
        CuentaDTO cuentaDTOMov = new CuentaDTO(this.sesionController.getSemilla());

        if (tipoTransaccionFiltroSelected == null) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.seleccioneUno", sesionController.getNombreCanal())));
            return "";
        } else if ((txtFechaDesde == null) || (txtFechaHasta == null)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.rangoFechaInv", sesionController.getNombreCanal())));
            return "";
        } else {
            if (txtFechaDesde != null && txtFechaHasta != null) {
                //SE VERIFICA SI LA FECHA DESDE ES MAYOR A LA FECHA HASTA
                if (txtFechaDesde.compareTo(txtFechaHasta) > 0) {
                    this.limipiarMensajesFacesContext();
                    FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.rangoFechaInv", sesionController.getNombreCanal())));
                    return "";
                }
            }

            boolean validacionesSeguridad = sesionController.productosCliente(cuentaDTO.getNumeroCuenta());

            if (validacionesSeguridad) {
                /* cuentaDTOMov = cuentaDAO.listarMovimientos(tipoCuenta, cuentaDTO.getNumeroCuenta(), fecha(this.getTxtFechaDesde()), fecha(this.getTxtFechaHasta()),
                        parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                        parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                        parametrosController.getNombreParametro("pnw.global.orden.desc", sesionController.getIdCanal()),
                        sesionController.getIdCanal(), sesionController.getNombreCanal());*/

                cuentaDTOMov = cuentaDAO.listarMovimientosPorTransaccion(tipoCuenta, cuentaDTO.getNumeroCuenta(), fecha(this.getTxtFechaDesde()), fecha(this.getTxtFechaHasta()),
                        parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                        parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                        parametrosController.getNombreParametro("pnw.global.orden.desc", sesionController.getIdCanal()),
                        sesionController.getIdCanal(), sesionController.getNombreCanal(), tipoTransaccionFiltroSelected, sesionController.getIdCanal());

                if (!cuentaDTOMov.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    listMovimientoCuentaDTO = null;
                    this.limipiarMensajesFacesContext();
                    FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    return "";
                } else if (cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_SIN_RESULTADOS_JPA)) {
                    listMovimientoCuentaDTO = null;
                    this.limipiarMensajesFacesContext();
                    return "";
                } else if (!cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    listMovimientoCuentaDTO = null;
                    this.limipiarMensajesFacesContext();
                    FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTOMov.getRespuestaDTO().getTextoSP()));
                    return "";
                }
                listMovimientoCuentaDTO = null;
                listMovimientoCuentaDTO = cuentaDTOMov.getMovimientosDTO();

//              PENDIENTE: tienen que filtrar por transacción. Actualmente está filtrando por tipo de reclamo en el SP
                /* if (tipoTransaccionFiltroSelected != null && !tipoTransaccionFiltroSelected.equalsIgnoreCase("")) {
                    listMovimientoCuentaDTOtemp = new ArrayList<>();
                    for (MovimientoCuentaDTO movTemp : listMovimientoCuentaDTO) {
                        if (movTemp.getTipoTransaccion() != null) {
                            if (movTemp.getTipoTransaccion().equalsIgnoreCase(tipoTransaccionFiltroSelected)) {
                                listMovimientoCuentaDTOtemp.add(movTemp);
                            }
                        }
                    }
                    listMovimientoCuentaDTO = listMovimientoCuentaDTOtemp;
                }*/
                if (nroReferencia != null && !nroReferencia.equalsIgnoreCase("")) {
                    listMovimientoCuentaDTOtemp = new ArrayList<>();
                    for (MovimientoCuentaDTO movTemp : listMovimientoCuentaDTO) {
                        if (movTemp.getNumeroReferencia().equalsIgnoreCase(nroReferencia)) {
                            listMovimientoCuentaDTOtemp.add(movTemp);
                        }
                    }
                    listMovimientoCuentaDTO = listMovimientoCuentaDTOtemp;
                }
            } else {
                RespuestaDTO respuesta = new RespuestaDTO();
                respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
                respuesta.setTextoSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
                cuentaDTOMov.setRespuestaDTO(respuesta);

                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTOMov.getRespuestaDTO().getTextoSP()));
                return "";
            }

        }
        return parametrosController.getNombreParametro("pnw.consulta.url.detalleCuenta", sesionController.getIdCanal());
    }

    /**
     * metodo para filtrar los movimientos de la cuenta por fecha, por tipo de
     * transaccion y por nro de referencia
     *
     * @return lista de movimientos ME filtrada
     */
    public String filtrarME() {
        Collection<MovimientoCuentaDTO> listMovimientoCuentaDTOtemp;
        CuentaDTO cuentaDTOMov = new CuentaDTO(this.sesionController.getSemilla());
        if ((txtFechaDesde == null) || (txtFechaHasta == null)) {
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.rangoFechaInv", sesionController.getNombreCanal())));
            return "";
        } else {
            if (txtFechaDesde != null && txtFechaHasta != null) {
                //SE VERIFICA SI LA FECHA DESDE ES MAYOR A LA FECHA HASTA
                if (txtFechaDesde.compareTo(txtFechaHasta) > 0) {
                    this.limipiarMensajesFacesContext();
                    FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.rangoFechaInv", sesionController.getNombreCanal())));
                    return "";
                }
            }

            boolean validacionesSeguridad = sesionController.productosCliente(cuentaDTO.getNumeroCuenta());

            if (validacionesSeguridad) {
                cuentaDTOMov = cuentaDAO.listarMovimientos(tipoCuenta, cuentaDTO.getNumeroCuenta(), fecha(this.getTxtFechaDesde()), fecha(this.getTxtFechaHasta()),
                        parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                        parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                        parametrosController.getNombreParametro("pnw.global.orden.desc", sesionController.getIdCanal()),
                        sesionController.getIdCanal(), sesionController.getNombreCanal());

                /*cuentaDTOMov = cuentaDAO.listarMovimientosPorTransaccion(tipoCuenta, cuentaDTO.getNumeroCuenta(), fecha(this.getTxtFechaDesde()), fecha(this.getTxtFechaHasta()),
                        parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                        parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                        parametrosController.getNombreParametro("pnw.global.orden.desc", sesionController.getIdCanal()),
                        sesionController.getIdCanal(), sesionController.getNombreCanal(), "", sesionController.getIdCanal());*/
                if (!cuentaDTOMov.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    listMovimientoCuentaDTO = null;
                    this.limipiarMensajesFacesContext();
                    FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    return "";
                } else if (cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_SIN_RESULTADOS_JPA)) {
                    listMovimientoCuentaDTO = null;
                    this.limipiarMensajesFacesContext();
                    return "";
                } else if (!cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    listMovimientoCuentaDTO = null;
                    this.limipiarMensajesFacesContext();
                    FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTOMov.getRespuestaDTO().getTextoSP()));
                    return "";
                }
                listMovimientoCuentaDTO = null;
                listMovimientoCuentaDTO = cuentaDTOMov.getMovimientosDTO();
                if (nroReferencia != null && !nroReferencia.equalsIgnoreCase("")) {
                    listMovimientoCuentaDTOtemp = new ArrayList<>();
                    for (MovimientoCuentaDTO movTemp : listMovimientoCuentaDTO) {
                        if (movTemp.getNumeroReferencia().equalsIgnoreCase(nroReferencia)) {
                            listMovimientoCuentaDTOtemp.add(movTemp);
                        }
                    }
                    listMovimientoCuentaDTO = listMovimientoCuentaDTOtemp;
                }
            } else {
                RespuestaDTO respuesta = new RespuestaDTO();
                respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
                respuesta.setTextoSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
                cuentaDTOMov.setRespuestaDTO(respuesta);

                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTOMov.getRespuestaDTO().getTextoSP()));
                return "";
            }

        }
        return "/sec/cuentas/detalleCuentaME.xhtml";
    }

    /**
     * Metodo para actualizar las cuentas del seleccionar cuentas desplegables
     * con la consulta por defecto enviando las fechas en null
     *
     * @param event
     */
    public void actDetalleCuentaInicial(AjaxBehaviorEvent event) {
        this.limpiarFiltrar();
        this.limpiarFiltrarMeses();
        Collection<MovimientoCuentaDTO> listMovimientoCuentaDTOtemp;
        String[] split = cuentaSeleccionada.split(":");
        cuentaDTO.setSemilla(sesionController.getSemilla());
        tipoCuenta = cuentaDTO.getDesEnc().desencriptar(split[0]);
        CuentaDTO cuentaDTOMov;
        boolean validacionesSeguridad = sesionController.productosCliente(cuentaDTO.getDesEnc().desencriptar(split[1]));
        String fechaDesde;
        String fechaHasta;
        if (validacionesSeguridad) {
            cuentaDTO = cuentaDAO.obtenerDetalleCuenta(tipoCuenta, cuentaDTO.getDesEnc().desencriptar(split[1]), sesionController.getIdCanal(), sesionController.getNombreCanal());
            cuentaDTO.setSemilla(sesionController.getSemilla());

            if (!cuentaDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return;
            } else if (!cuentaDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTO.getRespuestaDTO().getTextoSP()));
                return;
            }

            if (this.getTxtFechaDesde() == null || this.getTxtFechaHasta() == null) {
                //si las fechas vienen nulas o vacias seteamos la fecha del mes actual
                Calendar date = Calendar.getInstance();
                date.set(Calendar.DAY_OF_MONTH, 1);
                fechaDesde = formatearFecha(date.getTime(), FORMATO_FECHA_SIMPLE);
                //se calculan los mov del mes actual desde el 1 hasta el dia de hoy ya que si esta fecha supera el dia actual el SP arroja un ERROR
                date = Calendar.getInstance();
                fechaHasta = formatearFecha(date.getTime(), FORMATO_FECHA_SIMPLE);
            } else {
                fechaDesde = fecha(this.getTxtFechaDesde());
                fechaHasta = fecha(this.getTxtFechaHasta());
            }

            cuentaDTOMov = cuentaDAO.listarMovimientos(tipoCuenta, cuentaDTO.getDesEnc().desencriptar(split[1]), fechaDesde, fechaHasta,
                    parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.orden.desc", sesionController.getIdCanal()),
                    sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (!cuentaDTOMov.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return;
            } else if (cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_SIN_RESULTADOS_JPA)) {
                this.limipiarMensajesFacesContext();
                // return;
            } else if (!cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTOMov.getRespuestaDTO().getTextoSP()));
                return;
            }

            listMovimientoCuentaDTO = cuentaDTOMov.getMovimientosDTO();

//              PENDIENTE: tienen que filtrar por transacción. Actualmente está filtrando por tipo de reclamo en el SP
            if (tipoTransaccionFiltroSelected != null && !tipoTransaccionFiltroSelected.equalsIgnoreCase("")) {
                listMovimientoCuentaDTOtemp = new ArrayList<>();
                for (MovimientoCuentaDTO movTemp : listMovimientoCuentaDTO) {
                    if (movTemp.getTipoTransaccion() != null) {
                        if (movTemp.getTipoTransaccion().equalsIgnoreCase(tipoTransaccionFiltroSelected)) {
                            listMovimientoCuentaDTOtemp.add(movTemp);
                        }
                    }
                }
                listMovimientoCuentaDTO = listMovimientoCuentaDTOtemp;
            }

            if (nroReferencia != null && !nroReferencia.equalsIgnoreCase("")) {
                listMovimientoCuentaDTOtemp = new ArrayList<>();
                for (MovimientoCuentaDTO movTemp : listMovimientoCuentaDTO) {
                    if (movTemp.getNumeroReferencia().equalsIgnoreCase(nroReferencia)) {
                        listMovimientoCuentaDTOtemp.add(movTemp);
                    }
                }
                listMovimientoCuentaDTO = listMovimientoCuentaDTOtemp;
            }

        } else {
            RespuestaDTO respuesta = new RespuestaDTO();
            respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
            respuesta.setTextoSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
            cuentaDTO.setRespuestaDTO(respuesta);

            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTO.getRespuestaDTO().getTextoSP()));
        }

        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.consulta.url.detalleCuenta", sesionController.getIdCanal()));

    }

    /**
     *
     * @return metodo que retorna el listado de los tipo de operacion o listado
     * de reclamos posibles
     */
    public List<ReclamoDTO> listadoTransaccionesMov() {
        ReclamoDTO reclamo;
        reclamo = reclamosDAO.obtenerListadoReclamos(sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (reclamo.getRespuestaDTO() != null && !reclamo.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //ERROR AL INTENTAR ACTUALIZAR EL LISTADO DE PREGUNTAS DE DESAFIO
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        }
        return reclamo.getReclamos();
    }

    /**
     *
     * @return metodo que retorna el listado de los tipo de operacion o listado
     * de reclamos posibles
     */
    public List<TransaccionesCuentasDTO> listadoTransaccionesMovT() {
        TransaccionesCuentasDTO reclamo;
        //reclamo = cuentaDAO.listadoTransaccionesCuentas("BCC", sesionController.getNombreCanal());
        reclamo = cuentaDAO.listadoTransaccionesCuentas("BCA", sesionController.getNombreCanal(), sesionController.getIdCanal());

        if (reclamo.getRespuestaDTO() != null && !reclamo.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //ERROR AL INTENTAR ACTUALIZAR EL LISTADO DE PREGUNTAS DE DESAFIO
            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        }
        return reclamo.getListadoTransacciones();
    }

    /**
     * metodo para el filtro por meses
     */
    public void filtroMeses() {
        CuentaDTO cuentaDTOMov;
        boolean validacionesSeguridad = sesionController.productosCliente(cuentaDTO.getNumeroCuenta());
        if (validacionesSeguridad) {
            sesionController.setMes((String) sesionController.obtenerMesCalendar(sesionController.getMesAnoSelected().getMes()));

            Date fechaInicio = formatearFechaStringADate("01/" + sesionController.getMes() + "/" + sesionController.getMesAnoSelected().getAno(), FORMATO_FECHA_SIMPLE);

            Calendar calendar = Calendar.getInstance();
            //validamos si es el mes actual a consultar tomamos como fecha tope la fecha de hoy
            if (sesionController.getMesAnoSelected().getMes().equalsIgnoreCase(String.valueOf(Calendar.getInstance().get(Calendar.MONTH)))) {
                calendar = Calendar.getInstance();
            } else {
                //si no, se obtiene el ultimo dia del mes seleccionado
                calendar.set(Integer.valueOf(sesionController.getMesAnoSelected().getAno()), Integer.valueOf(sesionController.getMesAnoSelected().getMes()), 1);
                calendar.add(Calendar.MONTH, +1);
                calendar.add(Calendar.DATE, -1);
            }

            cuentaDTOMov = cuentaDAO.listarMovimientos(tipoCuenta, cuentaDTO.getNumeroCuenta(), formatearFecha(fechaInicio, "dd/MM/yyy"), formatearFecha(calendar.getTime(), "dd/MM/yyy"),
                    parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.orden.desc", sesionController.getIdCanal()),
                    sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (!cuentaDTOMov.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return;
            } else if (cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_SIN_RESULTADOS_JPA)) {
                this.limipiarMensajesFacesContext();
                listMovimientoCuentaDTO = new ArrayList<>();
                return;
            } else if (!cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTOMov.getRespuestaDTO().getTextoSP()));
                return;
            }

            if (!cuentaDTOMov.getMovimientosDTO().isEmpty() || cuentaDTOMov.getMovimientosDTO() != null) {
                listMovimientoCuentaDTO = cuentaDTOMov.getMovimientosDTO();
            } else {
                listMovimientoCuentaDTO = new ArrayList<>();
            }

        } else {
            RespuestaDTO respuesta = new RespuestaDTO();
            respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
            respuesta.setTextoSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
            cuentaDTO.setRespuestaDTO(respuesta);

            this.limipiarMensajesFacesContext();
            FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTO.getRespuestaDTO().getTextoSP()));
        }
    }

    /**
     * Método para limpiar los campos del friltrar por transacción, fecha y
     * referencia
     */
    public void limpiarFiltrar() {
        this.txtFechaDesde = null;
        this.txtFechaHasta = null;
        this.tipoTransaccionFiltroSelected = "";
    }

    /**
     * Método para limpiar el combo de seleccion de los meses para realizar el
     * filtrado
     */
    public void limpiarFiltrarMeses() {
        //Se limpia el filtro de meses al seleccionar filtro de fechas
        sesionController.setMesAnoSelected(new MesAnoDTO());
    }

    public void cargarSaldoBloqueado() {
        listSaldoBloqueado = cuentaDAO.listadoSaldoBloqueadoCuenta(tipoCuenta, cuentaDTO.getNumeroCuenta(), sesionController.getNombreCanal(), sesionController.getIdCanal());
    }

    public void cargarSaldoDiferido() {
        listSaldoDiferido = cuentaDAO.listadoSaldoDiferidoCuenta(tipoCuenta, cuentaDTO.getNumeroCuenta(), sesionController.getNombreCanal(), sesionController.getIdCanal());
    }

    /**
     * Metodo que imprime mensaje en el xhtml (para ser usado por el componente
     * message de primefaces)
     *
     * @param mensaje
     */
    public void infoMensaje(String mensaje) {
        this.limipiarMensajesFacesContext();
        FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info", mensaje));
    }

    public void limpiar() {

        sesionController.setMesAnoSelected(new MesAnoDTO());
        sesionController.getMesAnoSelected().setMes(String.valueOf(Calendar.getInstance().get(Calendar.MONTH)));
        this.txtFechaDesde = null;
        this.txtFechaHasta = null;
        this.nroReferencia = null;
        this.tipoTransaccionFiltroSelected = null;
        sesionController.setReiniciarForm(false);
    }

    public void iniciar() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.isReiniciarForm()) {
                sesionController.setMesAnoSelected(new MesAnoDTO());
                sesionController.setReiniciarForm(false);
            }
        }
    }

    public String estadoCuentaPdf() throws IOException, DocumentException {
        boolean error = false;
        try {

            if (this.mesAnoSelected.equalsIgnoreCase("-1") || (this.mesAnoSelected == null)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.rangoFechaInv", sesionController.getNombreCanal())));
                return "";
            } else {
                CuentaDTO cuentaDTOMov;
                CuentaDTO cuentaDTOCab;
                Calendar fecha1 = GregorianCalendar.getInstance();
                fecha1.set(Integer.parseInt(ano), Integer.parseInt(mes), 1);
                fecha1.set(Calendar.DAY_OF_MONTH, fecha1.getActualMinimum(Calendar.DAY_OF_MONTH));
                fecha1.set(Calendar.HOUR, fecha1.getMinimum(Calendar.HOUR_OF_DAY));
                Calendar fecha2 = (Calendar) fecha1.clone();
                fecha2.set(Calendar.DAY_OF_MONTH, fecha1.getActualMaximum(Calendar.DAY_OF_MONTH));

                cuentaDTOCab = cuentaDAO.estadoCuenta(tipoCuenta, cuentaDTO.getNumeroCuenta(), fecha(fecha1.getTime()), fecha(fecha2.getTime()), sesionController.getNombreCanal(), sesionController.getIdCanal());
                cuentaDTOMov = cuentaDAO.listarMovimientos(tipoCuenta, cuentaDTO.getNumeroCuenta(), fecha(fecha1.getTime()), fecha(fecha2.getTime()),
                        parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                        parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                        parametrosController.getNombreParametro("pnw.global.orden.asc", sesionController.getIdCanal()),
                        sesionController.getIdCanal(), sesionController.getNombreCanal());

                if (!cuentaDTOMov.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !cuentaDTOCab.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    this.limipiarMensajesFacesContext();
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    error = true;
                    return "";
                } else if (cuentaDTOCab.getRespuestaDTO().getCodigoSP().equals(CODIGO_SIN_RESULTADOS_JPA)) {
                    this.limipiarMensajesFacesContext();
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    error = true;
                    return "";
                } /*else if (!cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    this.limipiarMensajesFacesContext();
                     FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta",new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "prueba"));
                    error=true;
                    return "";
                }*/ else if (!cuentaDTOCab.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    this.limipiarMensajesFacesContext();
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formDetCta:divDetCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    error = true;
                    return "";
                }

                if (!error) {

                    FacesContext faces = FacesContext.getCurrentInstance();
                    HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
                    response.setHeader("Expires", "0");
                    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
                    response.setHeader("Pragma", "public");
                    response.setContentType("application/octet-stream");
                    response.addHeader("Content-disposition", "attachment;filename=\"EstadoCuenta.pdf\"");

                    Document document = new Document(PageSize.A4, 35, 30, 50, 50);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    PdfWriter writer = PdfWriter.getInstance(document, baos);

                    //Instanciamos la clase utilitaria que realiza la paginación
                    EstadoCuentaHeadFoot paginar = new EstadoCuentaHeadFoot(cuentaDTOCab.getCabecera());
                    paginar.setFootChequesEmi(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.foot.chequesEmi", sesionController.getNombreCanal()));
                    paginar.setFootDepEfc(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.foot.depEfec", sesionController.getNombreCanal()));
                    paginar.setFootFechaEmi(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.foot.FechaEmi", sesionController.getNombreCanal()));
                    paginar.setFootOtrosCre(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.foot.otrosCre", sesionController.getNombreCanal()));
                    paginar.setFootOtrosDeb(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.foot.otrosDeb", sesionController.getNombreCanal()));
                    paginar.setFootSaldoT(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.foot.saldoTotal", sesionController.getNombreCanal()));
                    paginar.setHeadAgencia(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.head.agencia", sesionController.getNombreCanal()));
                    paginar.setHeadAño(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.head.ano", sesionController.getNombreCanal()));
                    paginar.setHeadBienvenida(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.head.bienvenida", sesionController.getNombreCanal()));
                    paginar.setHeadCuenta(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.head.cuenta", sesionController.getNombreCanal()));
                    paginar.setHeadMes(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.head.mes", sesionController.getNombreCanal()));
                    paginar.setHeadPage(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.head.pagina", sesionController.getNombreCanal()));
                    paginar.setHeadSaldo(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.head.saldo", sesionController.getNombreCanal()));
                    paginar.setHeadtitle(textosController.getNombreTexto("pnw.estadoCta.descargaPdf.head.titulo", sesionController.getNombreCanal()));
                    paginar.setCuenta(cuentaDTO.getNumeroCuenta());
                    paginar.setMes(Integer.toString(Integer.parseInt(mes) + 1));
                    paginar.setAno(ano);
                    //Pasamos por parámetros los eventos que se producen al generar el PDF
                    writer.setPageEvent(paginar);

                    document.open();

                    //Invocamos el método que genera el PDF
                    this.cuerpoEstadoCuentaPDF(document, cuentaDTOMov.getMovimientosDTO());

                    //ceramos el documento
                    document.close();

                    response.setContentLength(baos.size());
                    ServletOutputStream out = response.getOutputStream();
                    baos.writeTo(out);
                    baos.flush();

                    faces.responseComplete();
                }
            }
        } catch (Exception e) {
        }
        return "";

    }

    public String getFechaTrans() {
        return fechaTrans;
    }

    public void setFechaTrans(String fechaTrans) {
        this.fechaTrans = fechaTrans;
    }

    public void cuerpoEstadoCuentaPDF(Document pdf, Collection<MovimientoCuentaDTO> lista) throws BadElementException, DocumentException {
        //Estilos de las Fuentes
        try {
            //Estilos de las Fuentes
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font fontsmall = FontFactory.getFont("Open Sans, sans-serif", 8, Font.NORMAL);

            //Instanciacion           
            PdfPCell cell;
            Paragraph paragraph;
            String ingreso;
            String egreso;
            String saldo;

            //Cabecera de movimientos     
            paragraph = new Paragraph();
            float[] columnWidths2 = {0.6f, 2, 1, 1, 1, 1};
            PdfPTable movimientosTable = new PdfPTable(columnWidths2);
            movimientosTable.setWidthPercentage(100);
            movimientosTable.setSpacingBefore(0f);
            movimientosTable.setSpacingAfter(0f);
            cell = new PdfPCell(new Phrase("DIA", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            movimientosTable.addCell(cell);
            cell = new PdfPCell(new Phrase("DESCRIPCION", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            movimientosTable.addCell(cell);
            cell = new PdfPCell(new Phrase("REFERENCIA", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            movimientosTable.addCell(cell);
            cell = new PdfPCell(new Phrase("DEBITO", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            movimientosTable.addCell(cell);
            cell = new PdfPCell(new Phrase("CREDITO", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            movimientosTable.addCell(cell);
            cell = new PdfPCell(new Phrase("SALDO", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            movimientosTable.addCell(cell);

            //Cilo paar el llenado de  movimientos
            int j = 0;
            int i = 1;

            if (lista != null) {
                for (MovimientoCuentaDTO mov : lista) {
                    //for(int i=0;i<=lista.size();i++){

                    cell = new PdfPCell(new Phrase(mov.getFechaTransaccionString() != null ? mov.getFechaTransaccionString() : "", fontsmall));
                    cell.setBorder(PdfPCell.NO_BORDER);
                    movimientosTable.addCell(cell);
                    cell = new PdfPCell(new Phrase(mov.getDescripcion() != null ? mov.getDescripcion() : "", fontsmall));
                    cell.setBorder(PdfPCell.NO_BORDER);
                    movimientosTable.addCell(cell);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell = new PdfPCell(new Phrase(mov.getNumeroReferencia() != null ? mov.getNumeroReferencia() : "", fontsmall));
                    cell.setBorder(PdfPCell.NO_BORDER);
                    movimientosTable.addCell(cell);

                    if (mov.getIngreso() != null) {
                        ingreso = formatearMonto(mov.getIngreso());
                    } else {
                        ingreso = "0.0";
                    }
                    if (mov.getEgreso() != null) {
                        egreso = formatearMonto(mov.getEgreso());
                    } else {
                        egreso = "0.0";
                    }
                    if (mov.getMonto() != null) {
                        saldo = formatearMonto(mov.getMonto());
                    } else {
                        saldo = "0.0";
                    }

                    cell = new PdfPCell(new Phrase(egreso, fontsmall));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    movimientosTable.addCell(cell);
                    cell = new PdfPCell(new Phrase(ingreso, fontsmall));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    movimientosTable.addCell(cell);
                    cell = new PdfPCell(new Phrase(saldo, fontsmall));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    movimientosTable.addCell(cell);

                    // Se limita el numero de Rows a 40 y se genera una paguina nueva
                    if (j == 40 & i < lista.size()) {
                        paragraph.add(movimientosTable);
                        pdf.add(paragraph);
                        pdf.newPage();
                        paragraph = new Paragraph();
                        movimientosTable = new PdfPTable(columnWidths2);
                        movimientosTable.setWidthPercentage(100);
                        movimientosTable.setSpacingBefore(0f);
                        movimientosTable.setSpacingAfter(0f);
                        cell = new PdfPCell(new Phrase("DIA", fontBold));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(Color.LIGHT_GRAY);
                        movimientosTable.addCell(cell);
                        cell = new PdfPCell(new Phrase("DESCRIPCION", fontBold));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(Color.LIGHT_GRAY);
                        movimientosTable.addCell(cell);
                        cell = new PdfPCell(new Phrase("REFERENCIA", fontBold));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(Color.LIGHT_GRAY);
                        movimientosTable.addCell(cell);
                        cell = new PdfPCell(new Phrase("DEBITO", fontBold));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(Color.LIGHT_GRAY);
                        movimientosTable.addCell(cell);
                        cell = new PdfPCell(new Phrase("CREDITO", fontBold));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(Color.LIGHT_GRAY);
                        movimientosTable.addCell(cell);
                        cell = new PdfPCell(new Phrase("SALDO", fontBold));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(Color.LIGHT_GRAY);
                        movimientosTable.addCell(cell);
                        j = 0;
                    }
                    j++;
                    i++;
                }
            } else {

                cell = new PdfPCell(new Phrase("Sin registros que mostrar para este mes", fontBold));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setColspan(6);
                movimientosTable.addCell(cell);

            }
            paragraph.add(movimientosTable);
            pdf.add(paragraph);

        } catch (Exception e) {

        }

    }

}
