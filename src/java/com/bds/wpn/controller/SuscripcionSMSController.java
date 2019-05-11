/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.IbCanalesDAO;
import com.bds.wpn.dao.IbEventosNotificacionDAO;
import com.bds.wpn.dao.IbPreguntasDesafioDAO;
import com.bds.wpn.dao.IbUsuarioDAO;
import com.bds.wpn.dao.IbUsuariosEventosMediosDAO;
import com.bds.wpn.dao.PreguntasAutenticacionDAO;
import com.bds.wpn.dto.FlujoExternaUsuarioDTO;
import com.bds.wpn.dto.IbEventosNotificacionDTO;
import com.bds.wpn.dto.IbMediosNotificacionDTO;
import com.bds.wpn.dto.IbPregDesafioUsuarioDTO;
import com.bds.wpn.dto.IbPreguntasDesafioDTO;
import com.bds.wpn.dto.IbUsuariosDTO;
import com.bds.wpn.dto.IbUsuariosEventosMediosDTO;
import com.bds.wpn.dto.PreguntaAutenticacionDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.CODIGO_SIN_RESULTADOS_JPA;
import static com.bds.wpn.util.BDSUtil.FORMATO_FECHA_COMPLETA;
import static com.bds.wpn.util.BDSUtil.fecha2;
import static com.bds.wpn.util.BDSUtil.formatearFecha;
import com.bds.wpn.util.DESedeEncryption;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
@Named("wpnSuscripcionSMSController")
@SessionScoped
public class SuscripcionSMSController extends BDSUtil implements Serializable {

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbParametrosFacade parametrosFacade;

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbPreguntasDesafioDAO ibPreguntasDesafioDAO;

    @EJB
    PreguntasAutenticacionDAO preguntasAutenticacionDAO;

    @EJB
    IbEventosNotificacionDAO eventosNotificacionDAO;

    @EJB
    IbUsuariosEventosMediosDAO usuariosEventosMediosDAO;

    @EJB
    IbUsuarioDAO usuarioDAO;

    @EJB
    IbCanalesDAO canalesDAO;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;

    private String pregunta;
    private String[] respuestasDD;
    private String[] respuestasDDEnc;
    private ArrayList<IbPreguntasDesafioDTO> preguntasDD = null;
    private int cantPreguntasDesafio = 0;
    private String[] respuestasDA;
    private ArrayList<PreguntaAutenticacionDTO> preguntasDA = null;
    private int cantPreguntasAutenticacion = 0;
    private Date fechaTransaccion;
    private String celular;
    private IbEventosNotificacionDTO listEventosNotificacionDTO;
    private IbUsuariosEventosMediosDTO listUsuariosEventosMediosDTO;
    private boolean mostrarPreguntas = false;                                               //indicador para visualizar el formulario con las preguntas
    private FlujoExternaUsuarioDTO flujoExternaUsuarioDTO;
    private boolean mantenerDatosForm;

    public boolean isMostrarPreguntas() {
        return mostrarPreguntas;
    }

    public void setMostrarPreguntas(boolean mostrarPreguntas) {
        this.mostrarPreguntas = mostrarPreguntas;
    }

    /**
     *
     * @return obtiene el objeto de registro usuario
     */
    public FlujoExternaUsuarioDTO getFlujoExternaUsuarioDTO() {
        return flujoExternaUsuarioDTO;
    }

    /**
     *
     * @param flujoExternaUsuarioDTO inserta el objeto de registro usuario
     */
    public void setFlujoExternaUsuarioDTO(FlujoExternaUsuarioDTO flujoExternaUsuarioDTO) {
        this.flujoExternaUsuarioDTO = flujoExternaUsuarioDTO;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String[] getRespuestasDD() {
        return respuestasDD;
    }

    public void setRespuestasDD(String[] respuestasDD) {
        this.respuestasDD = respuestasDD;
    }

    public String[] getRespuestasDDEnc() {
        return respuestasDDEnc;
    }

    public void setRespuestasDDEnc(String[] respuestasDDEnc) {
        this.respuestasDDEnc = respuestasDDEnc;
    }

    public ArrayList<IbPreguntasDesafioDTO> getPreguntasDD() {
        return preguntasDD;
    }

    public void setPreguntasDD(ArrayList<IbPreguntasDesafioDTO> preguntasDD) {
        this.preguntasDD = preguntasDD;
    }

    public int getCantPreguntasDesafio() {
        return cantPreguntasDesafio;
    }

    public void setCantPreguntasDesafio(int cantPreguntasDesafio) {
        this.cantPreguntasDesafio = cantPreguntasDesafio;
    }

    public String[] getRespuestasDA() {
        return respuestasDA;
    }

    public void setRespuestasDA(String[] respuestasDA) {
        this.respuestasDA = respuestasDA;
    }

    public ArrayList<PreguntaAutenticacionDTO> getPreguntasDA() {
        return preguntasDA;
    }

    public void setPreguntasDA(ArrayList<PreguntaAutenticacionDTO> preguntasDA) {
        this.preguntasDA = preguntasDA;
    }

    public int getCantPreguntasAutenticacion() {
        return cantPreguntasAutenticacion;
    }

    public void setCantPreguntasAutenticacion(int cantPreguntasAutenticacion) {
        this.cantPreguntasAutenticacion = cantPreguntasAutenticacion;
    }

    public Date getFechaTransaccion() {
        fechaTransaccion = new Date();
        return fechaTransaccion;
    }

    public void setFechaTransaccion(Date fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public String getCelular() {
        return sesionController.getUsuario().getCelular();
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public IbEventosNotificacionDTO getListEventosNotificacionDTO() {
        if (listEventosNotificacionDTO == null) {
            listEventosNotificacionDTO = eventosNotificacionDAO.listaEventos(sesionController.getNombreCanal());

            if (!listEventosNotificacionDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso2:msjEventos", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else if (!listEventosNotificacionDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso2:msjEventos", new FacesMessage(FacesMessage.SEVERITY_WARN, "", listEventosNotificacionDTO.getRespuestaDTO().getTextoSP()));
            }
        }
        return listEventosNotificacionDTO;
    }

    public void setListEventosNotificacionDTO(IbEventosNotificacionDTO listEventosNotificacionDTO) {
        this.listEventosNotificacionDTO = listEventosNotificacionDTO;
    }

    public IbUsuariosEventosMediosDTO getListUsuariosEventosMediosDTO() {
        return listUsuariosEventosMediosDTO;
    }

    public void setListUsuariosEventosMediosDTO(IbUsuariosEventosMediosDTO listUsuariosEventosMediosDTO) {
        this.listUsuariosEventosMediosDTO = listUsuariosEventosMediosDTO;
    }

    public boolean isMantenerDatosForm() {
        return mantenerDatosForm;
    }

    public void setMantenerDatosForm(boolean mantenerDatosForm) {
        this.mantenerDatosForm = mantenerDatosForm;
    }

    public void suscripcionSMSPaso1() {
        sesionController.setReiniciarForm(true);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
    }
    
    
     /*
    * Metodo para contar las cantidad de respuestas que no se encuentran vacias
    */
    
     private int countResp(String[] args){
        int count=0;
        for (String s:args){
           if(s!=null) 
            if (!s.isEmpty())
                count++; 
            
        }
    return count;
    }
    
    
    private boolean validarAlfaNumericoYEspacio(String[] value){
    int valid=0;
    FacesContext context = FacesContext.getCurrentInstance();
    context.getExternalContext().getFlash().setKeepMessages(true);
       for (String s:value)
        if (!s.matches("^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ ]*$")) {
                FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas",new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.AlfaNumEspacio", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.AlfaNumEspacio", sesionController.getIdCanal())));
                valid++;
        }
    return valid>0;
    }
    

    public void suscripcionSMSPaso2() {

        int errorValidacion = 0;
        String retorno = "";
         desencriptarRespuestasWeb();
        
        if (preguntasDD.size()!=countResp(respuestasDDEnc)||preguntasDA.size()!=countResp(respuestasDA))
    {
  
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
        
    }else if (!validarAlfaNumericoYEspacio(respuestasDDEnc) && !validarAlfaNumericoYEspacio(respuestasDA)){
        
        UtilDTO util = ibPreguntasDesafioDAO.validarPreguntaDD(sesionController.getUsuario().getId().toString(), obtenerRespuestasDesafio(), "-", sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (util == null || util.getRespuestaDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            respuestasDD = null;
            respuestasDA = null;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
            return;
        } else if (util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            respuestasDD = null;
            respuestasDA = null;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
            return;
        } else {
            if (util.getResuladosDTO() != null && (Boolean) util.getResuladosDTO().get("resultado")) {
                util = preguntasAutenticacionDAO.validarPDAporCliente(sesionController.getUsuario().getTdd(), this.flujoExternaUsuarioDTO.getNumeroCuenta(), obtenerRespuestasAutenticacion(), "-", sesionController.getNombreCanal(), sesionController.getIdCanal());
                if (util == null || util.getRespuestaDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    respuestasDD = null;
                    respuestasDA = null;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
                    return;
                } else if (util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    respuestasDD = null;
                    respuestasDA = null;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
                    return;
                } else {
                    if (util.getResuladosDTO() != null && (Boolean) util.getResuladosDTO().get("resultado")) {
                        //SE RESETEA EL CONTADOR DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
                        RespuestaDTO respuesta = usuarioDAO.actualizarPregSegFallidas(sesionController.getUsuario().getId().toString(), 0, sesionController.getNombreCanal(), sesionController.getIdCanal());
                        if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                            respuestasDD = null;
                            respuestasDA = null;
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
                            return;
                        }

                        if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                            respuestasDD = null;
                            respuestasDA = null;            
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
                            return;
                        }

                        //////////////////////////////////////////
                        //EN ESTE RENGLÓN SE PROCEDE A REALIZAR LA CARGA DE LA PÁGINA SIGUIENTE
                        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
                        sesionController.setReiniciarForm(true);
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso2", sesionController.getIdCanal()));
                        //return parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso2", sesionController.getIdCanal());
                        //////////////////////////////////////////
                    } else {
                        //INDICAMOS QUE SE ENCONTRO UNA INCOSISTENCIA EN LAS RESPUESTAS
                        errorValidacion++;
                    }
                }
            } else {
                //INDICAMOS QUE SE ENCONTRO UNA INCOSISTENCIA EN LAS RESPUESTAS
                errorValidacion++;
            }

        }
            if (errorValidacion > 0) {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_ACTUALIZACIONDAT, "", "", "Intento Fallido de Validacion de Preguntas de Seguridad", "", "", "", "", "", "", "");
                retorno = validarIntentosFallidosPregSeg();
                if (sesionController.isUsuarioBloqueado()) {
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_ACTUALIZACIONDAT, "", "", "Bloqueo de Usuario por Intentos Fallidos de Preguntas de Seguridad", "", "", "", "", "", "", "");
                    sesionController.mostrarDialogo(textosController.getNombreTexto("pnw.login.texto.bloqueo", sesionController.getNombreCanal()));
                    sesionController.setTextoAlerta(textosController.getNombreTexto("pnw.login.texto.bloqueoPopUp", sesionController.getNombreCanal()));
                    sesionController.setSesionInvalidada(true);
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.global.url.inicio", sesionController.getIdCanal()) + "?faces-redirect=true");
                } else {
                    if (retorno.equalsIgnoreCase(CODIGO_EXCEPCION_GENERICA)) {
                        //SI SE GENERO UN ERROR EN LAS CONSULTAS SE ENVIA EL MSJ DE ERROR DEFINIDO EN EL METODO DE VALIDACION
                        respuestasDD = null;
                        respuestasDA = null;   
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
                        return;
                    } else {
                        //CASO CONTRARIO SE INDICA QUE SE REALIZO UN INTENTO FALLIDO DE VALIDACION EN LAS PREGUNTAS DE SEGURIDAD
                        respuestasDD = null;
                        respuestasDA = null;    
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, textosController.getNombreTexto("pnw.global.texto.respuestasInv", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.respuestasInv", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
                        return;
                    }
                }
            }
    }
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
        
      
    }

    public void regresarSuscripcionSMSPaso2() {
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() - 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso2", sesionController.getIdCanal()));
    }

    public void suscripcionSMSPaso3() {
        int cont = 0;//cuenta si ha sido seleccionado aunque sea un checkbox
        int indexId = 0;
        listUsuariosEventosMediosDTO = new IbUsuariosEventosMediosDTO();
        List<IbUsuariosEventosMediosDTO> listUsuariosTemp = new ArrayList<>();

        for (IbEventosNotificacionDTO i : listEventosNotificacionDTO.getIbEventosNotificacionDTO()) {
            if (i.isCheckEvento()) {
                cont = ++cont;
                if (i.getPoseeMonto().toString().equalsIgnoreCase("1")) {
                    if (i.getMontoMin() == null || i.getMontoMin().isEmpty() || (new BigDecimal(eliminarformatoSimpleMonto(i.getMontoMin())).compareTo(BigDecimal.ZERO) == 0)) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso2:txtMontoMin" + indexId, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", i.getNombreEvento() + ": " + textosController.getNombreTexto("pnw.suscripcionSMS.msj2", sesionController.getIdCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso2", sesionController.getIdCanal()));
                        return;
                    }
                }

                IbUsuariosEventosMediosDTO usuariosEventosMediosTemp = new IbUsuariosEventosMediosDTO();
                IbEventosNotificacionDTO ibEventosNotificacionDTO = new IbEventosNotificacionDTO();
                IbMediosNotificacionDTO ibMediosNotificacionDTO = new IbMediosNotificacionDTO();
                IbUsuariosDTO ibUsuariosDTO = new IbUsuariosDTO();

                ibEventosNotificacionDTO.setId(i.getId());
                ibMediosNotificacionDTO.setId(new BigDecimal(2));
                ibUsuariosDTO.setId(sesionController.getUsuario().getId());

                usuariosEventosMediosTemp.setId(BigDecimal.ZERO);
                usuariosEventosMediosTemp.setIdEvento(ibEventosNotificacionDTO);
                usuariosEventosMediosTemp.setIdMedio(ibMediosNotificacionDTO);
                usuariosEventosMediosTemp.setIdUsuario(ibUsuariosDTO);
                if (i.getMontoMin() != null) {
                    usuariosEventosMediosTemp.setMontoMin(new BigDecimal(eliminarformatoSimpleMonto(i.getMontoMin())));
                }
                listUsuariosTemp.add(usuariosEventosMediosTemp);

            } else {
                if (i.getPoseeMonto().toString().equalsIgnoreCase("1") && i.getMontoMin() != null && !i.getMontoMin().isEmpty()) {
                    if (new BigDecimal(eliminarformatoSimpleMonto(i.getMontoMin())).compareTo(BigDecimal.ZERO) > 0) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso2:txtMontoMin" + indexId, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", i.getNombreEvento() + ": " + textosController.getNombreTexto("pnw.suscripcionSMS.msj2", sesionController.getIdCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso2", sesionController.getIdCanal()));
                        return;
                    }
                }
            }

            indexId++;
        }

        if (cont == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso2:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.suscripcionSMS.msj1", sesionController.getIdCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso2", sesionController.getIdCanal()));
            return;

        }

        listUsuariosEventosMediosDTO.setIbUsuariosEventosMediosDTO(listUsuariosTemp);

        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso3", sesionController.getIdCanal()));
        return;
    }

    public void suscripcionSMSPaso4() {
        RespuestaDTO respuesta = new RespuestaDTO();

        respuesta = usuariosEventosMediosDAO.agregarUsuarioEventosMedios(listUsuariosEventosMediosDTO, sesionController.getNombreCanal());

        if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso3:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso3", sesionController.getIdCanal()));
            return;
        } else if (!respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso3:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, respuesta.getCodigoSP(), respuesta.getCodigoSP()));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso3", sesionController.getIdCanal()));
            return;
        }

        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_SUSCRIP_SMS, "", "", "Suscripción de SMS", "", "", "", "", "", "", "");

        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso4", sesionController.getIdCanal()));
        return;

    }

    /**
     * metodo action para Validar la TDD y generar las preguntas de Seguridad
     */
    public void generarPreguntasSeg() {
        //Validación positiva
        this.mostrarPreguntas = false;
        cantPreguntasDesafio = Integer.parseInt(parametrosController.getValorParametro("pnw.global.validacionPositiva.cantPDD", sesionController.getIdCanal()));
        respuestasDD = new String[cantPreguntasDesafio];
        preguntasDD = new ArrayList<>();
        validarCuentaDS();

        IbPregDesafioUsuarioDTO preguntasDesafUsr = ibPreguntasDesafioDAO.listadoPreguntasDesafioUsuario(sesionController.getUsuario().getId().toString(), sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (!preguntasDesafUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) || preguntasDesafUsr.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_SIN_RESULTADOS_JPA)) {
            //MSJ ERROR CONSULTA DE PREGUNTAS DESAFIO
            mostrarPreguntas = false;
            cantPreguntasDesafio = 0;
            cantPreguntasAutenticacion = 0;
            respuestasDD = null;
            preguntasDD = null;
            respuestasDA = null;
            preguntasDA = null;
            sesionController.setValidadorFlujo(1);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
            return;
        } else {
            if (preguntasDesafUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !preguntasDesafUsr.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //MSJ ERROR CONTROLADO CONSULTA DE PREGUNTAS
                mostrarPreguntas = false;
                cantPreguntasDesafio = 0;
                cantPreguntasAutenticacion = 0;
                respuestasDD = null;
                preguntasDD = null;
                respuestasDA = null;
                preguntasDA = null;
                sesionController.setValidadorFlujo(1);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divTDD", new FacesMessage(FacesMessage.SEVERITY_WARN, preguntasDesafUsr.getRespuestaDTO().getTextoSP(), preguntasDesafUsr.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
                return;
            } else {
                if (preguntasDesafUsr.getPreguntasDesafioUsr() == null || preguntasDesafUsr.getPreguntasDesafioUsr().isEmpty() || preguntasDesafUsr.getPreguntasDesafioUsr().size() < cantPreguntasDesafio) {
                    //MSJ DE ERROR CANT DE PREGUNTAS 0 ó MENOR A LA CANT REQUERIDA
                    mostrarPreguntas = false;
                    cantPreguntasDesafio = 0;
                    cantPreguntasAutenticacion = 0;
                    respuestasDD = null;
                    preguntasDD = null;
                    respuestasDA = null;
                    preguntasDA = null;
                    sesionController.setValidadorFlujo(1);
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
                    return;
                } else {
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_SUSCRIP_SMS, "", "", "Generacion de Preguntas Desafio Ora11", "", "", "", "", "", "", "");
                    ArrayList<IbPreguntasDesafioDTO> preguntasDesaTotales = new ArrayList<>(preguntasDesafUsr.getPreguntasDesafioUsr());
                    int posicionPregAleatoria = 0;
                    for (int i = 0; i < cantPreguntasDesafio; i++) {
                        posicionPregAleatoria = numeroAleatorio(preguntasDesaTotales.size());
                        preguntasDD.add(preguntasDesaTotales.get(posicionPregAleatoria));
                        preguntasDesaTotales.remove(posicionPregAleatoria);
                    }
                }
            }
        }

        cantPreguntasAutenticacion = Integer.parseInt(parametrosController.getValorParametro("pnw.global.validacionPositiva.cantPDA", sesionController.getIdCanal()));
        respuestasDA = new String[cantPreguntasAutenticacion];
        preguntasDA = new ArrayList<>();
        //SE COLOCAN 0 PREGUNTAS PARA QUE EL SERVICIO RETORNE TODO EL POOL DE PDA
        PreguntaAutenticacionDTO preguntasAutUsr = preguntasAutenticacionDAO.listPDAporCliente(sesionController.getUsuario().getTdd(), this.flujoExternaUsuarioDTO.getNumeroCuenta(), cantPreguntasAutenticacion, sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (!preguntasAutUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) || preguntasDesafUsr.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_SIN_RESULTADOS_JPA)) {
            //MSJ ERROR CONSULTA DE PREGUNTAS DESAFIO
            mostrarPreguntas = false;
            cantPreguntasDesafio = 0;
            cantPreguntasAutenticacion = 0;
            respuestasDD = null;
            preguntasDD = null;
            respuestasDA = null;
            preguntasDA = null;
            sesionController.setValidadorFlujo(1);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
            return;
        } else {
            if (preguntasAutUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !preguntasAutUsr.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //MSJ ERROR CONTROLADO CONSULTA DE PREGUNTAS
                mostrarPreguntas = false;
                cantPreguntasDesafio = 0;
                cantPreguntasAutenticacion = 0;
                respuestasDD = null;
                preguntasDD = null;
                respuestasDA = null;
                preguntasDA = null;
                sesionController.setValidadorFlujo(1);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divTDD", new FacesMessage(FacesMessage.SEVERITY_WARN, preguntasAutUsr.getRespuestaDTO().getTextoSP(), preguntasAutUsr.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
                return;
            } else {
                if (preguntasAutUsr.getPreguntasAutenticacion() == null || preguntasAutUsr.getPreguntasAutenticacion().isEmpty() || preguntasAutUsr.getPreguntasAutenticacion().size() < cantPreguntasAutenticacion) {
                    //MSJ DE ERROR CANT DE PREGUNTAS 0 ó MENOR A LA CANT REQUERIDA
                    mostrarPreguntas = false;
                    cantPreguntasDesafio = 0;
                    cantPreguntasAutenticacion = 0;
                    respuestasDD = null;
                    preguntasDD = null;
                    respuestasDA = null;
                    preguntasDA = null;
                    sesionController.setValidadorFlujo(1);
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
                    return;
                } else {
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_SUSCRIP_SMS, "", "", "Generacion de Preguntas Autenticacion Ora9", "", "", "", "", "", "", "");
                    ArrayList<PreguntaAutenticacionDTO> preguntasAutTotales = new ArrayList<>(preguntasAutUsr.getPreguntasAutenticacion());
                    int posicionPregAleatoria = 0;
                    for (int i = 0; i < cantPreguntasAutenticacion; i++) {
                        posicionPregAleatoria = numeroAleatorio(preguntasAutTotales.size());
                        preguntasDA.add(preguntasAutTotales.get(posicionPregAleatoria));
                        preguntasAutTotales.remove(posicionPregAleatoria);
                    }
                }
            }
        }

        this.mostrarPreguntas = true;

        sesionController.setValidadorFlujo(1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));

    }

    public void cargarEventos() {
        // if (!FacesContext.getCurrentInstance().isPostback()) {
        listEventosNotificacionDTO = eventosNotificacionDAO.listaEventos(sesionController.getNombreCanal());
        //  }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_SUSCRIP_SMS)) {
                sesionController.setValidadorFlujo(1);
                this.suscripcionSMSPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_SUSCRIP_SMS)) {
                sesionController.setValidadorFlujo(1);
                this.suscripcionSMSPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo4() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_SUSCRIP_SMS)) {
                sesionController.setValidadorFlujo(1);
                this.suscripcionSMSPaso1();
            }
        }
    }

    public void limpiar() {
        if (sesionController.isReiniciarForm()) {
            if (!FacesContext.getCurrentInstance().isPostback()) {
                flujoExternaUsuarioDTO = new FlujoExternaUsuarioDTO();
                mostrarPreguntas = false;
                cantPreguntasDesafio = 0;
                cantPreguntasAutenticacion = 0;
                respuestasDD = null;
                respuestasDA = null;
                preguntasDD = null;
                preguntasDA = null;
                listEventosNotificacionDTO = null;
                this.mantenerDatosForm = false;
                sesionController.setValidadorFlujo(1);
                sesionController.setReiniciarForm(false);
            }
        }
    }

    public void inicio() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (!this.mantenerDatosForm) {

                listEventosNotificacionDTO = eventosNotificacionDAO.listaEventos(sesionController.getNombreCanal());

                if (!listEventosNotificacionDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso2:msjEventos", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                } else if (!listEventosNotificacionDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso2:msjEventos", new FacesMessage(FacesMessage.SEVERITY_WARN, "", listEventosNotificacionDTO.getRespuestaDTO().getTextoSP()));
                }
            }
        }
    }

    //SECCIÓN DE REPORTES
    /**
     * Método encargado de generar el contenido para el registro de reclamos
     *
     * @param pdf
     * @throws BadElementException
     * @throws DocumentException
     */
    public void cuerpoSuscripcionSMSDF(Document pdf) throws BadElementException, DocumentException {
        try {

            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 11, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //Variables para el manejo de la data
            String montoMov;

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(SuscripcionSMSController.class.getResource("/imgPDF/logoBanner.png"));

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

            pdf.add(table);
            pdf.add(new Paragraph(" "));

            table = new PdfPTable(2);

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas0 = {1.95f, 3.05f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas0);

            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.suscripcionSMS.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setPaddingBottom(20f);
            table.addCell(cell);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(null);
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);
            table.setWidthPercentage(70f);

            //Títulos de las Columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.suscripcionSMS.descargaPdf.fechaEjec", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(fecha2(new Date()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.suscripcionSMS.descargaPdf.nroCelular", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(sesionController.getUsuario().getCelular(), font));

            if (listEventosNotificacionDTO.getIbEventosNotificacionDTO().size() > 0) {
                table.getDefaultCell().setColspan(2);
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.suscripcionSMS.descargaPdf.eventos", sesionController.getIdCanal()) + ": ", fontBold));
//                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
//                table.addCell(new Phrase("", fontBold));

                for (IbEventosNotificacionDTO lista : listEventosNotificacionDTO.getIbEventosNotificacionDTO()) {
                    if (lista.isCheckEvento()) {
                        table.getDefaultCell().setColspan(2);
                        table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                        table.addCell(new Phrase(lista.getNombreEvento(), font));

                        if (lista.getMontoMin() != null) {
                            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                            table.addCell(new Phrase(textosController.getNombreTexto("pnw.suscripcionSMS.descargaPdf.mtoMinimo", sesionController.getIdCanal()) + " Bs.: " + lista.getMontoMin(), font));
                        } else {
                            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                            table.addCell(new Phrase("", fontBold));
                        }
                    }
                }
            }

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
    public void detalleSuscripcionSMSPDF() throws IOException, DocumentException {

        try {

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-disposition", "attachment;filename=\"suscripcionSMS.pdf\"");
            // response.addHeader("Content-disposition", "attachment;filename=\"" + nombreDocumento + "\"");
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
            this.cuerpoSuscripcionSMSDF(document);

            //ceramos el documento
            document.close();

            response.setContentLength(baos.size());
            ServletOutputStream out = response.getOutputStream();
            baos.writeTo(out);
            //  baos.flush();
            faces.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String obtenerRespuestasDesafio() {
        StringBuilder rafagaDesafio = new StringBuilder();
       
        for (int i = 0; i < cantPreguntasDesafio; i++) {
            if (!rafagaDesafio.toString().equalsIgnoreCase("")) {
                rafagaDesafio.append("-");
            }
            rafagaDesafio.append(preguntasDD.get(i).getId()).append("-").append(this.encSHA256(respuestasDDEnc[i]));
        }
        return rafagaDesafio.toString();
    }

    public String obtenerRespuestasAutenticacion() { 
        StringBuilder rafagaAutenticacion = new StringBuilder();
        for (int i = 0; i < cantPreguntasAutenticacion; i++) {
            if (!rafagaAutenticacion.toString().equalsIgnoreCase("")) {
                rafagaAutenticacion.append("-");
            }
            rafagaAutenticacion.append(preguntasDA.get(i).getCodigoPregunta()).append("-").append(respuestasDA[i]);
        }
        return rafagaAutenticacion.toString();
    }

    public String validarIntentosFallidosPregSeg() {
        int limitePregSegFallidasPermitidas = Integer.parseInt(parametrosController.getNombreParametro("pnw.global.validacionPositiva.cantIntentosFallidos", sesionController.getIdCanal()));
        int cantPregSegFallidas = 0;
        RespuestaDTO respuesta;
        UtilDTO util = usuarioDAO.cantidadPregSegFallidas(sesionController.getUsuario().getId().toString(), sesionController.getUsuario().getTdd(), sesionController.getNombreCanal(), sesionController.getIdCanal());

        if (util == null || util.getRespuestaDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        } else if (util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
        } else if (util.getRespuestaDTO() != null && util.getResuladosDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            cantPregSegFallidas = Integer.parseInt(util.getResuladosDTO().get("intentosFallidos").toString());
        } else {
            //ERROR EN DAO AL INTENTAR OBTENER LA CANTIDAD DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            return CODIGO_EXCEPCION_GENERICA;
        }
        if (cantPregSegFallidas < limitePregSegFallidasPermitidas) {
            cantPregSegFallidas++;
            //SE INCREMENTA EL CONTADOR DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
            respuesta = usuarioDAO.actualizarPregSegFallidas(sesionController.getUsuario().getId().toString(), cantPregSegFallidas, sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return CODIGO_EXCEPCION_GENERICA;
            }
        }
        /////////NOTIFICACION VIA SMS ULTIMO INTENTO RESTANTE////////
        if (cantPregSegFallidas == (limitePregSegFallidasPermitidas - 1)) {
            String textoSMS = textosController.getNombreTexto("pnw.global.sms.cuerpo.preBloqueo", sesionController.getNombreCanal());
            String motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.preBloqueoLogin", sesionController.getNombreCanal());
            HashMap<String, String> parametros = new HashMap();
            parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
            sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);
            ////////NOTIFICACIÓN VÍA EMAIL AL CORREO DE SEGURIDAD//////
            sesionController.notificarEmailUltimoIntento(sesionController.getUsuario());
        }
        //BLOQUEO DEL USUARIO POR LIMITE DE INTENTOS FALLIDOS SUPERADOS
        if (cantPregSegFallidas >= limitePregSegFallidasPermitidas) {
            respuesta = canalesDAO.bloquearAccesoCanal(sesionController.getUsuario().getId().toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (respuesta == null || respuesta.getCodigo() == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else if (respuesta.getCodigoSP() != null && respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getTextoSP()));
                return CODIGO_EXCEPCION_GENERICA;
            }
            respuesta = canalesDAO.bloquearAccesoCanal(sesionController.getUsuario().getId().toString(), CODIGO_CANAL_MOBILE, sesionController.getNombreCanal());

            if (respuesta == null || respuesta.getCodigo() == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else if (respuesta.getCodigoSP() != null && respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getTextoSP()));
                return CODIGO_EXCEPCION_GENERICA;
            }
            //ESPERANDO CONFIRMACION
//            respuesta = tddDAO.bloquearTDD(sesionController.getUsuario().getTdd(), sesionController.getIdCanal(), sesionController.getNombreCanal());
//
//            if (respuesta == null || respuesta.getCodigo() == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
//                //ERROR AL INTENTAR BLOQUEAR LA TDD EN EL CORE
//                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
//            } else if (respuesta.getCodigoSP() != null && respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
//                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
//                return CODIGO_EXCEPCION_GENERICA;
//            }

            sesionController.setUsuarioBloqueado(true);
            /////////NOTIFICACION VIA SMS BLOQUEO DE USUARIO POR INTENTOS FALLIDOS////////
            String textoSMS = textosController.getNombreTexto("pnw.login.texto.bloqueo", sesionController.getNombreCanal());
            String motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.bloqueoLogin", sesionController.getNombreCanal());
            HashMap<String, String> parametros = new HashMap();
            parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
            sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);

            ////////NOTIFICACIÓN VÍA EMAIL AL CORREO DE SEGURIDAD//////
            sesionController.notificarEmail(sesionController.getUsuario());
            sesionController.notificarEmailBloqueo(sesionController.getUsuario());
            sesionController.setTextoAlerta(textosController.getNombreTexto("pnw.login.texto.bloqueoPopUp", sesionController.getNombreCanal()));
        }
        return "";
    }

    /**
     * Método para validar cuentas DELSUR
     */
    public void validarCuentaDS() {

        String binCta = parametrosController.getNombreParametro("pnw.global.bin.nroCta", sesionController.getIdCanal());
        int longBin = binCta.length();

        int errores = 0;
        //se realizan las validaciones del formulario aqui para permitir los ajax onChange de TDD y cuentas 
        //  this.limipiarMensajesFacesContext();

        if (flujoExternaUsuarioDTO.getNumeroCuenta() == null || flujoExternaUsuarioDTO.getNumeroCuenta().isEmpty() || flujoExternaUsuarioDTO.getNumeroCuenta().equals("")) {
            errores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:txtNroCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }
        if (!flujoExternaUsuarioDTO.getNumeroCuenta().matches("^\\d{20}$") || !flujoExternaUsuarioDTO.getNumeroCuenta().substring(0, longBin).equalsIgnoreCase(binCta)) {
            errores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formSuscripcionSMSPaso1:txtNroCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getNombreCanal())));
        }

        if (errores > 0) {
            sesionController.setValidadorFlujo(1);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suscripcionSMS.url.paso1", sesionController.getIdCanal()));
            return;
        }

    }

    /**
     * metodo para encriptar texto en la capa web en 3des con una semilla de BD
     * para el ocultamiento de la data en la rafaga del request
     */
    public void encriptarRespuestasWeb() {
        if (this.respuestasDD != null && this.respuestasDD.length > 0) {
            respuestasDDEnc = new String[cantPreguntasDesafio];
            DESedeEncryption desEnc = new DESedeEncryption();
            desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal())));
            for (int i = 0; i < cantPreguntasDesafio; i++) {
                respuestasDDEnc[i] = desEnc.encriptar(this.respuestasDD[i]);
            }
        }
    }
    
    /**
     * metodo para desencriptar el texto en la capa web en 3des con una semilla
     * de BD
     */
    public void desencriptarRespuestasWeb() {
        DESedeEncryption desEnc = new DESedeEncryption();
        desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal())));
        for (int i = 0; i < cantPreguntasDesafio; i++) {
            respuestasDDEnc[i] = desEnc.desencriptar(this.respuestasDDEnc[i]);
        }
    }
    
    public void limpiarRespuestas(){
      respuestasDA = new String[cantPreguntasAutenticacion];
      respuestasDD = new String[cantPreguntasDesafio];
    }
    
    
}
