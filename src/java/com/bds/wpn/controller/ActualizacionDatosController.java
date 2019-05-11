/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.IbCanalesDAO;
import com.bds.wpn.dao.IbPreguntasDesafioDAO;
import com.bds.wpn.dao.IbUsuarioDAO;
import com.bds.wpn.dao.NotificacionesDAO;
import com.bds.wpn.dao.PreguntasAutenticacionDAO;
import com.bds.wpn.dao.TddDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.FlujoExternaUsuarioDTO;
import com.bds.wpn.dto.IbPregDesafioUsuarioDTO;
import com.bds.wpn.dto.IbPreguntasDesafioDTO;
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
import static com.bds.wpn.util.BDSUtil.NUEVALINEAEMAIL;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
@Named("wpnActualizacionDatosController")
@SessionScoped
public class ActualizacionDatosController extends BDSUtil implements Serializable {

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
    IbUsuarioDAO usuarioDAO;

    @EJB
    IbCanalesDAO canalesDAO;

    @EJB
    TddDAO tddDAO;

    @EJB
    NotificacionesDAO notificacionesDAO;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;
    
    private String pregunta;
    private String[] respuestasDD;                                          //Respuestas de Desafio en claro... al hacer submit se enmascaran
    private String[] respuestasDDEnc;                                       //Respuestas de Desafio encriptadas
    private ArrayList<IbPreguntasDesafioDTO> preguntasDD = null;
    private int cantPreguntasDesafio = 0;
    private String[] respuestasDA;
    private ArrayList<PreguntaAutenticacionDTO> preguntasDA = null;
    private int cantPreguntasAutenticacion = 0;
    private String email;
    private String celular;
    private String prefijoOperadora;
    private String numeroCel;
    private Date fechaTransaccion;
    private boolean mostrarPreguntas = false;
    private FlujoExternaUsuarioDTO flujoExternaUsuarioDTO;
    
    private String seleccion;           //guarda el valor seleccionado en el radio    
        
    public String getSeleccion() {        
        return seleccion;
    }

    public void setSeleccion(String seleccion) {        
        this.seleccion = seleccion;
    }

    public boolean isMostrarPreguntas() {
        return mostrarPreguntas;
    }

    public void setMostrarPreguntas(boolean mostrarPreguntas) {
        this.mostrarPreguntas = mostrarPreguntas;
    }

    public FlujoExternaUsuarioDTO getFlujoExternaUsuarioDTO() {
        return flujoExternaUsuarioDTO;
    }

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

    public String getEmail() {                                                            
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {                     
            this.celular = prefijoOperadora + numeroCel;            
        return celular;
    }

    public void setCelular(String celular) {         
        this.celular = celular;
    }

    public String getPrefijoOperadora() {  
        celular = sesionController.getUsuario().getCelular();
        if(celular != null){
            String cadena;             
            cadena = celular.substring(0,4);
            prefijoOperadora = cadena;        
        } else {
            prefijoOperadora = "";
        }
        return prefijoOperadora;
    }

    public void setPrefijoOperadora(String prefijoOperadora) {        
        this.prefijoOperadora = prefijoOperadora;
    }

    public String getNumeroCel() {    
        this.celular = sesionController.getUsuario().getCelular();
        if(celular != null){
            String cadena1;            
            cadena1 = celular.substring(4,11);
            numeroCel = cadena1;        
        } else {
            numeroCel = "";
        }
        return numeroCel;
    }

    public void setNumeroCel(String numeroCel) {
        this.numeroCel = numeroCel;
    }

    public Date getFechaTransaccion() {
        fechaTransaccion = new Date();
        return fechaTransaccion;
    }

    public void setFechaTransaccion(Date fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public void actualizacionDatosPaso1() {
        sesionController.setReiniciarForm(true);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
    }

    public void actualizacionDatosPaso2() {

        int errorValidacion = 0;
        String retorno = "";
        desencriptarRespuestasWeb();        
        
        if (preguntasDD.size() != countResp(respuestasDDEnc) || preguntasDA.size() != countResp(respuestasDA)) {

            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
                        
        } else if (!validarAlfaNumericoYEspacio(respuestasDDEnc) && !validarAlfaNumericoYEspacio(respuestasDA)) {

            UtilDTO util = ibPreguntasDesafioDAO.validarPreguntaDD(sesionController.getUsuario().getId().toString(), obtenerRespuestasDesafio(), "-", sesionController.getNombreCanal(), sesionController.getIdCanal());

            if (util == null || util.getRespuestaDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
                respuestasDD = null;
                respuestasDA = null;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
                return;
            } else if (util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                respuestasDD = null;
                respuestasDA = null;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
                return;
            } else {
                if (util.getResuladosDTO() != null && (Boolean) util.getResuladosDTO().get("resultado")) {
                    util = preguntasAutenticacionDAO.validarPDAporCliente(sesionController.getUsuario().getTdd(), this.flujoExternaUsuarioDTO.getNumeroCuenta(), obtenerRespuestasAutenticacion(), "-", sesionController.getNombreCanal(), sesionController.getIdCanal());

                    if (util == null || util.getRespuestaDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
                        respuestasDD = null;
                        respuestasDA = null;
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
                        return;
                    } else if (util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        respuestasDD = null;
                        respuestasDA = null;
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
                        return;
                    } else {
                        if ((Boolean) util.getResuladosDTO().get("resultado")) {
                            //SE RESETEA EL CONTADOR DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
                            RespuestaDTO respuesta = usuarioDAO.actualizarPregSegFallidas(sesionController.getUsuario().getId().toString(), 0, sesionController.getNombreCanal(), sesionController.getIdCanal());

                            if (respuesta == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
                                respuestasDD = null;
                                respuestasDA = null;
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
                                return;
                            } else if (respuesta != null && respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                respuestasDD = null;
                                respuestasDA = null;
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getTextoSP()));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
                                return;
                            }

                            //////////////////////////////////////////
                            //EN ESTE RENGLÓN SE PROCEDE A REALIZAR LA CARGA DE LA PÁGINA SIGUIENTE
                            sesionController.setValidadorFlujo(2);
                            sesionController.setReiniciarForm(true);
                            this.email = sesionController.getUsuario().getEmail();                            
                            this.celular = sesionController.getUsuario().getCelular();
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso2", sesionController.getIdCanal()));
                            return;
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
                    return;
                } else {
                    if (retorno.equalsIgnoreCase(CODIGO_EXCEPCION_GENERICA)) {
                        //SI SE GENERO UN ERROR EN LAS CONSULTAS SE ENVIA EL MSJ DE ERROR DEFINIDO EN EL METODO DE VALIDACION
                        respuestasDD = null;
                        respuestasDA = null;
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
                        return;
                    } else {
                        //CASO CONTRARIO SE INDICA QUE SE REALIZO UN INTENTO FALLIDO DE VALIDACION EN LAS PREGUNTAS DE SEGURIDAD
                        respuestasDD = null;
                        respuestasDA = null;
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, textosController.getNombreTexto("pnw.global.texto.respuestasInv", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.respuestasInv", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
                        return;
                    }
                }
            }
        }
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));        

    }

    public void actualizacionDatosPaso3() {
        sesionController.setValidadorFlujo(3);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso3", sesionController.getIdCanal()));
    }

    public void actualizacionDatosPaso4() {

        ClienteDTO datosCliente = new ClienteDTO();
        RespuestaDTO respuesta;

        datosCliente.setCodigoCliente(sesionController.getUsuario().getCodUsuario());
        datosCliente.setEmailCorreo(this.email);
        datosCliente.setCodigoTlfCell(this.prefijoOperadora);
        datosCliente.setTelefonoCell(this.numeroCel);
        
        //aqui actualizo el objeto que contiene la información mapeada.
        sesionController.getUsuario().setCelular(celular);
        sesionController.getUsuario().setEmail(email);
        
        respuesta = usuarioDAO.actualizarDatosUsuario(datosCliente, null, sesionController.getIdCanal(), sesionController.getNombreCanal());

        if (respuesta == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso3:divMjs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        } else if (respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso3:divMjs", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getTextoSP()));
        } else {

            sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_ACTUALIZACIONDAT, "", "", "Actualización de Datos", "", "", "", "", "", "", "");

            sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);

            /////////NOTIFICACION VIA SMS////////
            String textoSMS = textosController.getNombreTexto("pnw.global.sms.cuerpo.actDatCli", sesionController.getNombreCanal());
            String motivoSMS = textosController.getNombreTexto("pnw.global.sms.motivo.actDatCli", sesionController.getNombreCanal());
            HashMap<String, String> parametros = new HashMap();
            parametros.put("\\$PFEC", this.formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
            sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);

            //notificaActualizacionDatosEmail();
        }
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso4", sesionController.getIdCanal()));
        return;
    }

    public void regresarActualizacionDatosPaso1() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
    }

    public void regresarActualizacionDatosPaso2() {
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() - 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso2", sesionController.getIdCanal()));
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
            FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
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
                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divTDD", new FacesMessage(FacesMessage.SEVERITY_WARN, preguntasDesafUsr.getRespuestaDTO().getTextoSP(), preguntasDesafUsr.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
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
                    FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
                    return;
                } else {
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_ACTUALIZACIONDAT, "", "", "Generacion de Preguntas Desafio Ora11", "", "", "", "", "", "", "");
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
        //CAMBIAR EL 10 POR NULL CUANDO HAGAN EL CAMBIO SOLICITADO
        PreguntaAutenticacionDTO preguntasAutUsr = preguntasAutenticacionDAO.listPDAporCliente(sesionController.getUsuario().getTdd(), this.flujoExternaUsuarioDTO.getNumeroCuenta(), cantPreguntasAutenticacion, sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (!preguntasAutUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
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
            FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
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
                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divTDD", new FacesMessage(FacesMessage.SEVERITY_WARN, preguntasAutUsr.getRespuestaDTO().getTextoSP(), preguntasAutUsr.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
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
                    FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
                    return;
                } else {
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_ACTUALIZACIONDAT, "", "", "Generacion de Preguntas Autenticacion Ora9", "", "", "", "", "", "", "");
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
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
        return;
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

    /**
     * metodo que se encarga de valida la cantidad de intentos fallidos de un
     * usuario al modulo de preguntas de seguridad en CAMBIO DE CLAVE (PDA Y
     * PDD)
     *
     * @return indicador de error en consulta o si viene vacio el proceso se
     * concreto sin problemas
     */
    public String validarIntentosFallidosPregSeg() {
        int limitePregSegFallidasPermitidas = Integer.parseInt(parametrosController.getNombreParametro("pnw.global.validacionPositiva.cantIntentosFallidos", sesionController.getIdCanal()));
        int cantPregSegFallidas = 0;
        RespuestaDTO respuesta;
        UtilDTO util = usuarioDAO.cantidadPregSegFallidas(sesionController.getUsuario().getId().toString(), sesionController.getUsuario().getTdd(), sesionController.getNombreCanal(), sesionController.getIdCanal());

        if (util == null || util.getRespuestaDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        } else if (util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
        } else if (util.getRespuestaDTO() != null && util.getResuladosDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            cantPregSegFallidas = Integer.parseInt(util.getResuladosDTO().get("intentosFallidos").toString());
        } else {
            //ERROR EN DAO AL INTENTAR OBTENER LA CANTIDAD DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
            FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            return CODIGO_EXCEPCION_GENERICA;
        }
        if (cantPregSegFallidas < limitePregSegFallidasPermitidas) {
            cantPregSegFallidas++;
            //SE INCREMENTA EL CONTADOR DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
            respuesta = usuarioDAO.actualizarPregSegFallidas(sesionController.getUsuario().getId().toString(), cantPregSegFallidas, sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
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
                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else if (respuesta.getCodigoSP() != null && respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getTextoSP()));
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

    public void inicio() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (flujoExternaUsuarioDTO == null) {
                flujoExternaUsuarioDTO = new FlujoExternaUsuarioDTO();
            }
            this.flujoExternaUsuarioDTO.setNumeroCuenta("");
            mostrarPreguntas = false;
            cantPreguntasDesafio = 0;
            respuestasDD = null;
            preguntasDD = null;
            sesionController.setValidadorFlujo(1);
            sesionController.setReiniciarForm(false);
            this.email = "";
            this.numeroCel = "";
            this.celular = "";   
            this.seleccion = "";
        }
    }
    
    public void cargaObjetos(){
        this.email = sesionController.getUsuario().getEmail();
    }
    
    public void limpiarRespuestas() {
        respuestasDA = new String[cantPreguntasAutenticacion];
        respuestasDD = new String[cantPreguntasDesafio];
    }

    public void limpiar() {
        if (sesionController.isReiniciarForm()) {
            flujoExternaUsuarioDTO = new FlujoExternaUsuarioDTO();
            this.flujoExternaUsuarioDTO.setNumeroCuenta("");
            mostrarPreguntas = false;
            cantPreguntasDesafio = 0;
            respuestasDD = null;
            preguntasDD = null;
            sesionController.setValidadorFlujo(1);
            sesionController.setReiniciarForm(false);
            this.email = "";
            this.numeroCel = "";
            this.celular = "";             
        }
    }

    public void limpiar2() {
        flujoExternaUsuarioDTO = new FlujoExternaUsuarioDTO();
        mostrarPreguntas = false;
        cantPreguntasDesafio = 0;
        respuestasDD = null;
        preguntasDD = null;
        this.email = "";
        this.numeroCel = "";
        this.celular = "";          
    }

    public void regresarPaso1() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_ACTUALIZACIONDAT)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }        
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_ACTUALIZACIONDAT)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo4() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_ACTUALIZACIONDAT)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    //Seccion Notificaciones y Reportes
    /**
     * Método encargado de generar el contenido para el registro de reclamos
     *
     * @param pdf
     * @throws BadElementException
     * @throws DocumentException
     */
    public void actualizarDatosClientePDF(Document pdf) throws BadElementException, DocumentException {
        try {

            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 11, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(ActualizacionDatosController.class.getResource("/imgPDF/logoBanner.png"));

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
            pdf.add(new Paragraph(" "));
            
            table = new PdfPTable(2);

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas0 = {1.95f, 3.05f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas0);

            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.actDatCli.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));

            cell.setColspan(2);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            pdf.add(table);
            table = new PdfPTable(medidaCeldas0);
            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(null);
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);
            table.setWidthPercentage(70f);

            //Títulos de las Columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.actDatCli.descargaPdf.fecEjecucion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(fecha2(new Date()), font));

            if(this.seleccion.equals("opcorreo")){
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.actDatCli.descargaPdf.email", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(this.email, font));
            } else if(this.seleccion.equals("opcelular")){
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.actDatCli.descargaPdf.nroCelular", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(this.celular, font));
            }
            
            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            //Añadimos la tabla al PDF
            // pdf.setMargins(150f, 20f, 50f, 10f);
            pdf.setMargins(50f, 3f, 22f, 18f);
            pdf.add(new Paragraph(" "));
            seleccion = "";
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
    public void detalleActualizarDatosClientePDF() throws IOException, DocumentException {

        try {

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/force-download");
            response.addHeader("Content-disposition", "attachment;filename=\"ActualizacionDatosCliente.pdf\"");
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

            this.actualizarDatosClientePDF(document);

            //ceramos el documento
            document.close();

            response.setContentLength(baos.size());
            ServletOutputStream out = response.getOutputStream();
            baos.writeTo(out);
            baos.flush();
            faces.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notificación realizada vía email Método que setea los datos y parametros
     * para el envio del correo luego de la ejecución del proceso
     */
    public void notificaActualizacionDatosEmail() {

        String asunto = "Notificación Actualización de Datos";
        StringBuilder texto = new StringBuilder("");

        texto = new StringBuilder("Estimado(a) ");
        texto.append(sesionController.getUsuario().getNombre());
        texto.append(NUEVALINEAEMAIL);
        texto.append(NUEVALINEAEMAIL);
        texto.append(parametrosController.getNombreParametro("banco.nombre", sesionController.getIdCanal()));
        texto.append(" le informa que se ha realizado la modificación de sus datos exitosamente");
        texto.append(" el día: ");
        texto.append(BDSUtil.formatearFecha(new Date(), BDSUtil.FORMATO_FECHA_COMPLETA));
        texto.append(".");
        texto.append(".");
        texto.append(NUEVALINEAEMAIL);
        texto.append(NUEVALINEAEMAIL);
        texto.append("Gracias por su preferencia,");
        texto.append(NUEVALINEAEMAIL);
        texto.append(NUEVALINEAEMAIL);
        texto.append(parametrosController.getNombreParametro("banco.nombre", sesionController.getIdCanal()));
        texto.append(NUEVALINEAEMAIL);
        texto.append("Informacion Confidencial.");

        EMailDTO emailDTO = notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("banco.email", sesionController.getIdCanal()), sesionController.getUsuario().getEmail(), asunto, texto.toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
    }

    /**
     * Método para validar cuentas Del sur
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
            FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:txtNroCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }
        if (!flujoExternaUsuarioDTO.getNumeroCuenta().matches("^\\d{20}$") || !flujoExternaUsuarioDTO.getNumeroCuenta().substring(0, longBin).equalsIgnoreCase(binCta)) {
            errores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:txtNroCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getNombreCanal())));
        }

        if (errores > 0) {
            sesionController.setValidadorFlujo(1);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.actualizacionDatos.url.paso1", sesionController.getIdCanal()));
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
    
    /*
    * Metodo para contar las cantidad de respuestas que no se encuentran vacias
     */
    private int countResp(String[] args) {
        int count = 0;
        for (String s : args) {
            if (s != null) {
                if (!s.isEmpty()) {
                    count++;
                }
            }
        }
        return count;
    }
    
    private boolean validarAlfaNumericoYEspacio(String[] value) {
        int valid = 0;
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        for (String s : value) {
            if (!s.matches("^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ ]*$")) {
                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.AlfaNumEspacio", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.AlfaNumEspacio", sesionController.getIdCanal())));
                valid++;
            }
        }
        return valid > 0;
    }
}