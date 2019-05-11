/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.util;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

/**
 * Clase utilitaria para agregar la paginación a los archivos PDF
 *
 * @author Maury Díaz <Adverweb C. A.>
 */
public class PaginarPDF extends PdfPageEventHelper {

    private String encabezado;
    PdfTemplate total;

    /**
     * Crea el objecto PdfTemplate el cual contiene el numero total de paginas
     * en el documento
     *
     * @param writer
     * @param document
     */
    public void onOpenDocument(PdfWriter writer, Document document) {
        total = writer.getDirectContent().createTemplate(30, 16);
    }

    /**
     * ESTE ES Metodo a llamar cuando ocurra el evento <b>onEndPage</b>, es en
     * este evento donde crearemos la paginación del pdf con los elementos
     * indicados.
     *
     * @param writer
     * @param document
     */
    public void onEndPage(PdfWriter writer, Document document) {
        Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);
        PdfPTable table = new PdfPTable(2);

        try {
            table.setWidths(new int[]{1, 1});
            table.setTotalWidth(50);
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setBorderColor(Color.WHITE);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            Phrase footer = new Phrase(String.format("%d /", writer.getPageNumber()), font);
            table.addCell(footer);
            PdfPCell cell = new PdfPCell(Image.getInstance(total));
            Image image = Image.getInstance(total);
             //Transparencia de la imagen
            image.setTransparency(new int[]{0x00, 0x10});
            image.scaleAbsolute(18f, 12f);
            cell.addElement(new Chunk(image, 2, -2));
            cell.setBorder(0);
            cell.setBorderColor(Color.WHITE);

            cell.setPaddingLeft(-12f);
            cell.setPaddingTop(-6f);
            cell.setPaddingLeft(-8f);
            

            table.addCell(cell);
            table.writeSelectedRows(0, -1, 685, 37, writer.getDirectContent());
        } catch (Exception de) {
            throw new ExceptionConverter(de);
        }
    }
    private Font font;

    /**
     * Realiza el conteo de paginas al momento de cerrar el documento
     *
     * @param writer
     * @param document
     */
    public void onCloseDocument(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(String.valueOf(writer.getPageNumber() - 1)), 10, 4, 0);
    }

}
