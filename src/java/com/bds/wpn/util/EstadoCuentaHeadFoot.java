package com.bds.wpn.util;


import com.bds.wpn.dto.CabeceraEstadoCtaDTO;
import static com.bds.wpn.util.BDSUtil.fecha;
import static com.bds.wpn.util.BDSUtil.formatearMonto;
import static com.bds.wpn.util.BDSUtil.formatearMontoPdf;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.util.Date;
import org.apache.log4j.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author juan.faneite
 */
public class EstadoCuentaHeadFoot extends PdfPageEventHelper{
    
    final Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 14, Font.NORMAL, new Color(15, 158, 197));
    final Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
    final Font titleBold = FontFactory.getFont("Open Sans, sans-serif", 10, Font.BOLD);
    final Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
    final Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);
    final Font fontsmall = FontFactory.getFont("Open Sans, sans-serif", 8, Font.NORMAL);
    final int FIRST_ROW = 0;
    final int LAST_ROW = -1;
    private PdfTemplate total;
    private Date fecha = new Date();
    private String footChequesEmi;
    private String footDepEfc;
    private String footSaldoT;
    private String footOtrosDeb;
    private String footOtrosCre;
    private String footFechaEmi;
    private String headBienvenida;
    private String headtitle;
    private String headAgencia;
    private String headCuenta;
    private String headMes;
    private String headAño;
    private String headPage;
    private String headSaldo;
    private String mes;
    private String ano;
    private String cuenta;
    
    private static final Logger logger = Logger.getLogger(EstadoCuentaHeadFoot.class.getName());
    
    private CabeceraEstadoCtaDTO cabecera;
    
    public EstadoCuentaHeadFoot(CabeceraEstadoCtaDTO cabecera) {
        this.cabecera=cabecera;
    }

    public EstadoCuentaHeadFoot() {
    }

     public void setMes(String mes) {
        this.mes = mes;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    
    public void setFootChequesEmi(String footChequesEmi) {
        this.footChequesEmi = footChequesEmi;
    }

    public void setFootDepEfc(String footDebEfc) {
        this.footDepEfc = footDebEfc;
    }

    public void setFootSaldoT(String footSaldoT) {
        this.footSaldoT = footSaldoT;
    }

    public void setFootOtrosDeb(String footOtrosDeb) {
        this.footOtrosDeb = footOtrosDeb;
    }

    public void setFootOtrosCre(String footOtrosCre) {
        this.footOtrosCre = footOtrosCre;
    }

    public void setFootFechaEmi(String footFechaEmi) {
        this.footFechaEmi = footFechaEmi;
    }

    public void setHeadBienvenida(String headBienvenida) {
        this.headBienvenida = headBienvenida;
    }

    public void setHeadtitle(String headtitle) {
        this.headtitle = headtitle;
    }

    public void setHeadAgencia(String headAgencia) {
        this.headAgencia = headAgencia;
    }

    public void setHeadCuenta(String headCuenta) {
        this.headCuenta = headCuenta;
    }

    public void setHeadMes(String headMes) {
        this.headMes = headMes;
    }

    public void setHeadAño(String headAño) {
        this.headAño = headAño;
    }

    public void setHeadPage(String headPage) {
        this.headPage = headPage;
    }

    public void setHeadSaldo(String headSaldo) {
        this.headSaldo = headSaldo;
    }

    @Override
      public void onOpenDocument(PdfWriter writer, Document document) {
        total = writer.getDirectContent().createTemplate(30, 16);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Paragraph  paragraph = new Paragraph();
        PdfPCell cell = null;
        float[] columnWidths3 = {1, 1, 1, 1, 1, 1};
        PdfPTable footerTable = new PdfPTable(columnWidths3);
        footerTable.setWidthPercentage(100);
        footerTable.setSpacingBefore(0f);
        footerTable.setSpacingAfter(0f);
        cell = new PdfPCell(new Phrase(this.footChequesEmi,fontBold));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase(this.footDepEfc,fontBold));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase(this.footSaldoT,fontBold));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase((this.cabecera.getCantCheques() != null ? cabecera.getCantCheques().toString() : "0"),fontsmall));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase(formatearMontoPdf(cabecera.getSaldoCheques()),fontsmall));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase((this.cabecera.getCantDepositos()!= null ? cabecera.getCantDepositos().toString() : "0"),fontsmall));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase(formatearMontoPdf(cabecera.getSaldoDepositos()),fontsmall));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase(formatearMontoPdf(cabecera.getSaldoTotal()),fontsmall));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase(this.footOtrosDeb,fontBold));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase(this.footOtrosCre,fontBold));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase(" ",fontBold));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase((this.cabecera.getCantDebitos()!= null ? cabecera.getCantDebitos().toString() : "0"),fontsmall));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase(formatearMontoPdf(cabecera.getSaldoDebitos()),fontsmall));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase((this.cabecera.getCantCreditos()!= null ? cabecera.getCantCreditos().toString() : "0"),fontsmall));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase(formatearMontoPdf(cabecera.getSaldoCreditos()),fontsmall));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        footerTable.addCell(cell);
        cell = new PdfPCell(new Phrase(this.footFechaEmi+":"+fecha(fecha),fontsmall));
        cell.setColspan(2);
        footerTable.addCell(cell);
            if(footerTable.getTotalWidth()==0)
                 footerTable.setTotalWidth((document.right()-document.left())*footerTable.getWidthPercentage()/100f);
            footerTable.writeSelectedRows(FIRST_ROW, LAST_ROW, document.left(), document.bottom()+footerTable.getTotalHeight(),writer.getDirectContent());

    }
 
    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        
        super.onStartPage(writer, document);
        try {
            Image image = null;
            PdfPCell cell = null;
            // Obtenemos el logo de datojava
            float[] headercolumnWidths = {5, 3.5f};
            image = Image.getInstance(EstadoCuentaHeadFoot.class.getResource("/imgPDF/logoBanner.png"));
            image.scaleAbsolute(140f, 40f);
            
            // Crear las fuentes para el contenido y los titulos
            
            // Creacion de una tabla
            PdfPTable tableimage = new PdfPTable(headercolumnWidths);
            tableimage.setWidthPercentage(100);
            tableimage.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            // Agregar la imagen anterior a una celda de la tabla
            cell = new PdfPCell(image);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            tableimage.addCell(cell);
            cell = new PdfPCell(new Phrase(this.headtitle,titleBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorder(PdfPCell.NO_BORDER);
            tableimage.addCell(cell);
          
           // Agregar la tabla al documento
           document.add(tableimage);
           
            //cantidad de celdas de la tabla, tabla del encabezado
            float[] columnWidths = {5, 3.5f};
            Paragraph paragraph = new Paragraph();
            //   Tabla principal contenedora    
            PdfPTable mainTable = new PdfPTable(columnWidths);
            mainTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(0f);
            mainTable.setSpacingAfter(0f);
            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);
            table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

            // Primera tabla para Nombre y direccion
            PdfPCell firstTableCell = new PdfPCell();
            firstTableCell.setBorder(PdfPCell.NO_BORDER);
            PdfPTable firstTable = new PdfPTable(1);
            firstTable.setWidthPercentage(100.0f);
            cell = new PdfPCell(new Phrase(this.headBienvenida, titleBold));
            cell.setBorder(PdfPCell.NO_BORDER);
            firstTable.addCell(cell);
            cell = new PdfPCell(new Phrase((this.cabecera.getNombre()!= null ? cabecera.getNombre() : " "),titleBold));
            cell.setBorder(PdfPCell.NO_BORDER);
            firstTable.addCell(cell);
            cell = new PdfPCell(new Phrase((this.cabecera.getDireccion()!= null ? cabecera.getDireccion() : " "),fontsmall));
            cell.setBorder(PdfPCell.NO_BORDER);
            firstTable.addCell(cell);
            firstTableCell.addElement(firstTable);
            
            mainTable.addCell(firstTableCell);

            // Segunda tabla para Informaciond de banco.
            float[] bancocolumnWidths = {2.5f , 5};
            PdfPCell secondTableCell = new PdfPCell();
            PdfPTable secondTable = new PdfPTable(bancocolumnWidths);
            secondTable.setWidthPercentage(100f);
            secondTableCell.setBorder(PdfPCell.NO_BORDER);
            cell = new PdfPCell(new Phrase(this.headAgencia,fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            secondTable.addCell(cell);
            cell = new PdfPCell(new Phrase(this.headCuenta,fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            secondTable.addCell(cell);
            cell = new PdfPCell(new Phrase((this.cabecera.getCodAgencia()!= null ? cabecera.getCodAgencia().toString() : " "),font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            secondTable.addCell(cell);
            cell = new PdfPCell(new Phrase(this.cuenta,font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            secondTable.addCell(cell);
            secondTableCell.addElement(secondTable);
            
            
            PdfPTable datetable = new PdfPTable(3);
            datetable.getDefaultCell().setBorder(2);
            datetable.setWidthPercentage(100f);
            cell = new PdfPCell(new Phrase(this.headMes,fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            datetable.addCell(cell);
            cell = new PdfPCell(new Phrase(this.headAño,fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            datetable.addCell(cell);
            cell = new PdfPCell(new Phrase(this.headPage,fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            datetable.addCell(cell);
            cell = new PdfPCell(new Phrase(this.mes,font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            datetable.addCell(cell);
            cell = new PdfPCell(new Phrase(this.ano,font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            datetable.addCell(cell);
            
            //Paguinacion y total de paguinas
            image = Image.getInstance(total);
            image.scaleAbsolute(22f, 12f);
            Phrase phrase = new Phrase();
            phrase.add(new Phrase(writer.getPageNumber()+"/",font));
            phrase.add(new Chunk(image, 0, 0));
            cell.setPaddingTop(-8f);
            cell.setPaddingLeft(28f);
            cell.addElement(phrase);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            datetable.addCell(cell);
         
            cell = new PdfPCell(new Phrase(this.headSaldo+":" +formatearMonto(cabecera.getSaldoInicial()),font));
            cell.setColspan(3);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            datetable.addCell(cell);
            secondTableCell.addElement(datetable);
            mainTable.addCell(secondTableCell);

            paragraph.add(mainTable);
            document.add(paragraph);
            document.add(new Paragraph(" "));
            
        } catch (BadElementException  ex) {
            logger.error(ex.toString());
        } catch (IOException | DocumentException ex) {
            logger.error(ex.toString());
        }
    }
    
    @Override
     public void onCloseDocument(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(String.valueOf(writer.getPageNumber() - 1)), 0, 0, 0);
    } 
}
