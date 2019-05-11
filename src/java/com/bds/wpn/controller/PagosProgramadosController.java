/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.DelSurDAO;
import com.bds.wpn.dao.IBAgendaTransaccionesDAO;
import com.bds.wpn.dao.IbMenuDAO;
import com.bds.wpn.dto.BancoDTO;
import com.bds.wpn.dto.DelSurDTO;
import com.bds.wpn.dto.IBAgendaPNDTO;
import com.bds.wpn.dto.IBAgendaTransaccionDTO;
import com.bds.wpn.dto.IbModulosDTO;
import com.bds.wpn.dto.IbTransaccionesDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.fecha2;
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
 * @author cesar.mujica
 */
@Named("wpnPagosProgramadosController")
@SessionScoped
public class PagosProgramadosController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbMenuDAO menuDAO;

    @EJB
    IBAgendaTransaccionesDAO agendaTrasaccionDAO;

    @EJB
    IBAgendaTransaccionesDAO agendaDAO;

    @EJB
    DelSurDAO delSurDAO;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;
    
    private String selectTransSelected;                 //tipo de transaccion seleccionada
    private List<SelectItem> listSelectTrans;           //listado de tipos de transacciones para pagos programados
    private IBAgendaTransaccionDTO agenda;              //objeto con los pagos programados para un cliente
    private List<IBAgendaTransaccionDTO> listSelected;  //lista de agendas seleccionadas
    private IBAgendaPNDTO agendaDTO;
    List<BancoDTO> bancos;
    
    private String valorFrecuencia;
    private String nombreBanco;
    private String codigoBanco;
    private String nombreBeneficiario;
    private boolean botonEliminar;
    private boolean mantenerDatosForm;

    private IBAgendaTransaccionDTO agendaTransaccionesDTO = new IBAgendaTransaccionDTO();

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoListPaso2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_PAGOS_PROGRAMADOS)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    public List<SelectItem> listTrans() {
        listSelectTrans = new ArrayList<>();
        List<IbModulosDTO> modulos = new ArrayList<>(menuDAO.obtenerModeloTransBeneficiario(sesionController.getIdCanal(), sesionController.getNombreCanal()));

        for (IbModulosDTO m : modulos) {
            //llenado del select
            SelectItemGroup selectGroup = new SelectItemGroup(textosController.getNombreTexto(m.getNombre(), sesionController.getIdCanal()) + ":");

            SelectItem[] selectItemTemp = new SelectItem[m.getIbTransaccionesDTOCollection().size()];
            int cont = 0;
            for (IbTransaccionesDTO t : m.getIbTransaccionesDTOCollection()) {
                selectItemTemp[cont++] = new SelectItem(t.getIdCore().toString(), textosController.getNombreTexto(t.getNombre(), sesionController.getIdCanal()));
            }

            selectGroup.setSelectItems(selectItemTemp);

            listSelectTrans.add(selectGroup);
        }

        return listSelectTrans;
    }

    public void regresarPaso1() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.otp.url.validacionOtp", sesionController.getIdCanal()));
    }

    public IBAgendaPNDTO getAgendaDTO() {
        return agendaDTO;
    }

    public void setAgendaDTO(IBAgendaPNDTO agendaDTO) {
        this.agendaDTO = agendaDTO;
    }

    public String getSelectTransSelected() {
        return selectTransSelected;
    }

    public void setSelectTransSelected(String selectTransSelected) {
        this.selectTransSelected = selectTransSelected;
    }

    public List<SelectItem> getListSelectTrans() {
        return listSelectTrans;
    }

    public void setListSelectTrans(List<SelectItem> listSelectTrans) {
        this.listSelectTrans = listSelectTrans;
    }

    public IBAgendaTransaccionDTO getAgenda() {
        return agenda;
    }

    public void setAgenda(IBAgendaTransaccionDTO agenda) {
        this.agenda = agenda;
    }

    public List<IBAgendaTransaccionDTO> getListSelected() {
        return listSelected;
    }

    public void setListSelected(List<IBAgendaTransaccionDTO> listSelected) {
        this.listSelected = listSelected;
    }

    public String getValorFrecuencia() {
        return valorFrecuencia;
    }

    public void setValorFrecuencia(String valorFrecuencia) {
        this.valorFrecuencia = valorFrecuencia;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(String codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    public String getNombreBeneficiario() {
        return nombreBeneficiario;
    }

    public void setNombreBeneficiario(String nombreBeneficiario) {
        this.nombreBeneficiario = nombreBeneficiario;
    }

    public List<BancoDTO> getBancos() {
        return bancos;
    }

    public void setBancos(List<BancoDTO> bancos) {
        this.bancos = bancos;
    }

    public boolean isBotonEliminar() {
        return botonEliminar;
    }

    public void setBotonEliminar(boolean botonEliminar) {
        this.botonEliminar = botonEliminar;
    }        

    public boolean isMantenerDatosForm() {
        return mantenerDatosForm;
    }

    public void setMantenerDatosForm(boolean mantenerDatosForm) {
        this.mantenerDatosForm = mantenerDatosForm;
    }    
    
    public String evaluarFrecuencia(String frecuencia){
        if(frecuencia.equalsIgnoreCase("1")){
            valorFrecuencia = "Semanal";
        }
        if(frecuencia.equalsIgnoreCase("2")){
            valorFrecuencia = "Quincenal";
        }
        if(frecuencia.equalsIgnoreCase("3")){
            valorFrecuencia = "Mensual";
        }
        return valorFrecuencia;
    }
    
    
     /**
     * metodo para redirigir a paso 2
     */
    public void listPaso2() {
        limpiarPagosProgramados();
        sesionController.setValidacionOTP(2);
        sesionController.setValidadorFlujo(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.submenu.servicios.consultaPagosProgramados", sesionController.getIdCanal()));
    }

    /**
     * metodo para redirigir a paso 2
     */
    public void regresarListPaso2() {
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() - 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.submenu.servicios.consultaPagosProgramados", sesionController.getIdCanal()));
    }
    
    /**
     * Método para obtener los Pagos Programados de un usuario
     */
    public void obtenerPagosProgramados() {

        agenda = new IBAgendaTransaccionDTO();

        if (selectTransSelected.equalsIgnoreCase("7")) {//Transferencias Cuentas Propias Otros Bancos
            agenda.setIdTransaccion(Integer.valueOf(ID_TRANS_CTAS_P_OTROS_BCOS));
            agenda.setTipo('1');
        }

        if (selectTransSelected.equalsIgnoreCase("1")) {//Transferencias Terceros DELSUR
            agenda.setIdTransaccion(Integer.valueOf(ID_TRANS_CTAS_TERC_DELSUR));
            agenda.setTipo('1');
        }

        if (selectTransSelected.equalsIgnoreCase("3")) {//Transferencias Terceros Otros Bancos
            agenda.setIdTransaccion(Integer.valueOf(ID_TRANS_CTAS_T_OTROS_BCOS));
            agenda.setTipo('1');
        }

        if (selectTransSelected.equalsIgnoreCase("6")) {//Transferencias Propias DELSUR
            agenda.setIdTransaccion(Integer.valueOf(ID_TRANS_CTAS_PROPIAS_DELSUR));
            agenda.setTipo('1');
        }

        if (selectTransSelected.equalsIgnoreCase("8")) {//Transferencias Terceros Otros Bancos
            agenda.setIdTransaccion(Integer.valueOf(ID_TRANS_TDC_P_OTROS_BCOS));
            agenda.setTipo('2');
        }

        if (selectTransSelected.equalsIgnoreCase("5")) {//Pagos TDC Terceros DELSUR
            agenda.setIdTransaccion(Integer.valueOf(ID_TRANS_TDC_TERC_DELSUR));
            agenda.setTipo('2');
        }

        if (selectTransSelected.equalsIgnoreCase("4")) {//Pagos TDC Terceros Otros Bancos
            agenda.setIdTransaccion(Integer.valueOf(ID_TRANS_TDC_T_OTROS_BCOS));
            agenda.setTipo('2');
        }

        if (selectTransSelected.equalsIgnoreCase("2")) {//Pagos TDC Propias DELSUR
            agenda.setIdTransaccion(Integer.valueOf(ID_TRANS_TDC_PROPIAS_DELSUR));
            agenda.setTipo('2');
        }

        agendaDTO = agendaTrasaccionDAO.consultarIdCabeceraAgendaPP(sesionController.getUsuario().getId(), String.valueOf(agenda.getTipo()), agenda.getIdTransaccion(), sesionController.getNombreCanal(), sesionController.getIdCanal());
        
        if (!agendaDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext.getCurrentInstance().addMessage("formBeneficiarios:selectTipoTransaccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.botonEliminar = false;
        } else {
            if (!agendaDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.botonEliminar = false;
            } else {
                this.botonEliminar = true;
            }
        }
        
        bancos = listBancos();
        for (IBAgendaTransaccionDTO age : agendaDTO.getTransAgendadas()) {
            codigoBanco = age.getCuentaDestino().substring(0, 4);

            for (BancoDTO b : bancos) {
                if (b.getCodigoBanco().equalsIgnoreCase(codigoBanco)) {
                    age.setBancoBeneficiario(b.getNombreBanco());
                    break;
                }else{
                    age.setBancoBeneficiario("DELSUR Banco Universal C.A.");
                }
            }
        }

        if (!agendaDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext.getCurrentInstance().addMessage("formPagosProgramados:selectTipoTransaccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        } else {

        }
        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_PAGOS_PROGRAMADOS, "", "", "Consultar Pagos Programados", "", "", "", "", "", "", "");
    }

    public void borrarPagosProgramados() {
        if (!listSelected.isEmpty()) {
            sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.eliminarPagosProgramados.url.paso1", sesionController.getIdCanal()));
            return;
                    
                    
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formPagosProgramados:divMsjGlobal", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.seleccioneBenef", sesionController.getNombreCanal())));
             this.redirectFacesContext(parametrosController.getNombreParametro("pnw.submenu.servicios.consultaPagosProgramados", sesionController.getIdCanal()));
              return;
        }
    }

    /**
     * metodo para redirigir a paso 2 de desafiliar
     *
     * @return
     */
    public void eliminarPagoProgramadoPaso2() {

        RespuestaDTO respuesta = new RespuestaDTO();

        for (IBAgendaTransaccionDTO i : listSelected) {
            respuesta = agendaDAO.eliminarAgendaProgramada(i.getId(), sesionController.getUsuario().getId(), sesionController.getNombreCanal(), sesionController.getIdCanal());
        }

        if (respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //Guardamos en bitácora la eliminación
            sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_PAGOS_PROGRAMADOS, "", "", "Eliminar Pagos Programados", "", "", "", "", "", "", "");
            sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.eliminarPagosProgramados.url.paso2", sesionController.getIdCanal()));
            return;
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formPagosProgramados:divMsjGlobal", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.eliminarPagosProgramados.url.paso1", sesionController.getIdCanal()));
            return;
        }

    }
         

    public List<BancoDTO> listBancos() {
        DelSurDTO delsurDto = new DelSurDTO();

        delsurDto = delSurDAO.listadoBancos(sesionController.getNombreCanal(), sesionController.getIdCanal());

        if (delsurDto.getRespuestaDTO() != null && !delsurDto.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //ERROR AL INTENTAR ACTUALIZAR EL LISTADO DE PREGUNTAS DE DESAFIO
            FacesContext.getCurrentInstance().addMessage("formAfiliar2:afiliacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        }

        return delsurDto.getBancos();
    }
        
      /**
     * metodo utilitario para reiniciar los valores del formulario
     */
    public void limpiarPagosProgramados() {

        if (sesionController.isReiniciarForm()) {
            this.mantenerDatosForm = false;
            this.agendaDTO = null;
            this.bancos = new ArrayList<>();
            this.listSelected = new ArrayList<>();
            this.valorFrecuencia = "";
            this.listSelectTrans = new ArrayList<>();
            this.selectTransSelected = null;
            this.botonEliminar = false;
            sesionController.setReiniciarForm(false);
        }
    }
    
    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
        public void validarFlujoPagoProgramadoPaso2() {
      
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_PAGOS_PROGRAMADOS)) {
                this.listPaso2();
            }
        
    }
    
    
    
    
    
    public void validarFlujoPagoProgramadoPaso1() {
     
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_PAGOS_PROGRAMADOS)) {
                this.listPaso2();
            }
        
    }
 
    //////////////////////SECCIÓN DE REPORTES//////////////////////
    
     /**
     * metodo encargado de generar el contenido del listado los cheques a suspender
     *
     * @param pdf hoja de trabajo de PDF
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoEliminarPagoProgramadoPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 14, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //Variables para el manejo del monto, Phrase espera siempre un String
            String monto = "";
            String tipoSusp = "";
            String frecuencia = "";

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(PagosProgramadosController.class.getResource("/imgPDF/logoBanner.png"));

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
            //T�tulo que contiene el Nombre y el N�mero del Producto 
            // PdfPCell cell = new PdfPCell();

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas0 = {1.95f, 3.05f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas0);

            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            //Titulo que contiene el Nombre y el Numero del Producto 
            PdfPCell cell = new PdfPCell(new Phrase(textosController.getNombreTexto("pnw.pagosProgramados.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setPaddingBottom(20f);
            table.addCell(cell);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            pdf.add(table);
            
        for ( IBAgendaTransaccionDTO list :listSelected)    
        {   
            
            
            table = new PdfPTable(medidaCeldas0);
   
            
            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);
            table.setWidthPercentage(70f);

            //T�tulos de las Columnas 
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosProgramados.descargaPdf.bancoBeneficiario", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(list.getBancoBeneficiario(), font));

           
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosProgramados.descargaPdf.nombreBeneficiario", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(list.getNombreBeneficiario(), font));
            
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosProgramados.descargaPdf.ctaTdcBeneficiario", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(list.getCuentaDestino(), font));
            
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosProgramados.descargaPdf.monto", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
             table.addCell(new Phrase((list.getMonto() != null ? "Bs. " + formatearMonto(list.getMonto()) : " - "), font));
            
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosProgramados.descargaPdf.frecuencia", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            
        
            table.addCell(new Phrase(evaluarFrecuencia(Character.toString(list.getFrecuencia())), font));
            
           //Contenido de las Columnas, data
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
          //  table.addCell(new Phrase((this.cuentaSeleccionada), font));

            //Agregamos la tabla al PDF
            pdf.add(table);
            pdf.add(new Paragraph(" "));
        }
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
    public void eliminarPagoProgramadoPDF() throws IOException, DocumentException {
       
        try {

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-disposition", "attachment;filename=\"EliminarPagoProgramado.pdf\"");
           // response.addHeader("Content-disposition", "attachment;filename=\"" + tipoPDF + "\"");
            Document document = new Document(PageSize.A4.rotate());//(), 20, 20, 20, 20);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            //Instanciamos la clase utilitaria que realiza la paginaci�n
            PaginarPDF paginar = new PaginarPDF();
            Rectangle headerBox = new Rectangle(22, 45, 1320, 700);

            //Establecemos el tama�o del documento
            writer.setBoxSize("headerBox", headerBox);

            //Pasamos por par�metros los eventos que se producen al generar el PDF
            writer.setPageEvent(paginar);

            document.open();

            //Invocamos el m�todo que genera el PDF
           
                this.cuerpoEliminarPagoProgramadoPDF(document);
          

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
    
}
