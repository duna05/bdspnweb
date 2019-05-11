/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.IbCanalesDAO;
import com.bds.wpn.dao.IbClaveDAO;
import com.bds.wpn.dao.IbPreguntasDesafioDAO;
import com.bds.wpn.dao.IbUsuarioDAO;
import com.bds.wpn.dao.PreguntasAutenticacionDAO;
import com.bds.wpn.dao.TddDAO;
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
import static com.bds.wpn.util.BDSUtil.FORMATO_FECHA_COMPLETA;
import static com.bds.wpn.util.BDSUtil.FORMATO_FECHA_SIMPLE;
import static com.bds.wpn.util.BDSUtil.formatearFecha;
import com.bds.wpn.util.DESedeEncryption;
import com.bds.wpn.util.SessionAttributesNames;
import java.io.Serializable;
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Logger;

/**
 *
 * @author wilmer.rondon
 */
@Named("wpnCambioClaveController")
@SessionScoped
public class CambioClaveController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbPreguntasDesafioDAO ibPreguntasDesafioDAO;

    @EJB
    PreguntasAutenticacionDAO preguntasAutenticacionDAO;

    @EJB
    IbCanalesDAO canalesDAO;

    @EJB
    IbClaveDAO claveDAO;

    @EJB
    IbUsuarioDAO usuarioDAO;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    TddDAO tddDAO;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;
    
    private FlujoExternaUsuarioDTO flujoExternaUsuarioDTO;
    private String pregunta;
    private String[] respuestasDD;
    private String[] respuestasDDEnc;
    private ArrayList<IbPreguntasDesafioDTO> preguntasDD = null;
    private int cantPreguntasDesafio = 0;
    private String[] respuestasDA;
    private ArrayList<PreguntaAutenticacionDTO> preguntasDA = null;
    private int cantPreguntasAutenticacion = 0;
    private boolean mostrarPreguntas = false;                                               //indicador para visualizar el formulario con las preguntas
    private String txtTituloInformativo;
    private String txtCuerpoInformativo1;
    private String txtCuerpoInformativo2;
    private DESedeEncryption desEnc; 
    private static final Logger logger = Logger.getLogger(CambioClaveController.class.getName());//objeto encargado de encriptar la data

    public String getTxtTituloInformativo() {
        return txtTituloInformativo;
    }

    public void setTxtTituloInformativo(String txtTituloInformativo) {
        this.txtTituloInformativo = txtTituloInformativo;
    }

    public String getTxtCuerpoInformativo1() {
        return txtCuerpoInformativo1;
    }

    public void setTxtCuerpoInformativo1(String txtCuerpoInformativo1) {
        this.txtCuerpoInformativo1 = txtCuerpoInformativo1;
    }

    public String getTxtCuerpoInformativo2() {
        return txtCuerpoInformativo2;
    }

    public void setTxtCuerpoInformativo2(String txtCuerpoInformativo2) {
        this.txtCuerpoInformativo2 = txtCuerpoInformativo2;
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

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

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

    /**
     * Metodo para validar el la composicion del password
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     * @throws java.lang.Exception
     */
    public void validarFormaClave(FacesContext context, UIComponent component, Object value) throws ValidatorException, Exception {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getIdCanal())));
        } //validacion de Longitud   
        if (value.toString().length() < 8 || value.toString().length() > 12) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvLenght", sesionController.getIdCanal())));
        }
        if (!value.toString().equalsIgnoreCase("********") && this.flujoExternaUsuarioDTO.getClaveEnc() == null) {
            //validacion de 3 caracteres iguales y consecutivos
            if (validarRegexParcial("([a-zA-Z\\d])\\1\\1", value.toString())) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clv3Consec", sesionController.getIdCanal())));
            }
            if (!validarRegexParcial("([a-z]+)", value.toString())) {//La clave debe contener al menos una letra minúscula
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvMin", sesionController.getIdCanal())));
            }
            if (!validarRegexParcial("([A-Z]+)", value.toString())) {//La clave debe contener al menos una letra mayúscula
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvMay", sesionController.getIdCanal())));
            }
            if (!validarRegexParcial("([0-9]+)", value.toString())) {//La clave debe contener al menos una letra mayúscula
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvNumero", sesionController.getIdCanal())));
            }
            if (!validarRegexParcial("([.#$/-]+)", value.toString())) { //La clave debe tener al menos 1 caracter especial del ejemplo (.-#$/)
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvEsp", sesionController.getIdCanal())));
            }
            if (validarCantCaracteresConsecutivos(value.toString(), 3) >= 3) { //La clave posee 3 o mas caracteres consecutivos
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvConse", sesionController.getIdCanal())));
            }
        }
    }

    /**
     * Metodo para validar el campo motivo del formulario de transferencias.
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarClavesIgualesCambClv(FacesContext context, UIComponent component, Object value) throws ValidatorException, Exception {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getIdCanal())));
        }
        //validacion de Longitud   
        if (value.toString().length() < 8) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvLenght", sesionController.getIdCanal())));
        }
        UIInput passwordField = (UIInput) context.getViewRoot().findComponent("formCambioClave2:txtClave");

        if (!passwordField.getValue().toString().equalsIgnoreCase("********") && this.flujoExternaUsuarioDTO.getClaveEnc() == null) {
            //validacion de 3 caracteres iguales y consecutivos        
            if (validarRegexParcial("([a-zA-Z\\d])\\1\\1", value.toString())) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clv3Consec", sesionController.getIdCanal())));
            }
            //validacion de claves iguales
            if (passwordField == null) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("No se pudo Ubicar el Componente.", sesionController.getIdCanal())));
            }
            if (passwordField.getSubmittedValue() == null && passwordField.getValue() != null) {
                if (!passwordField.getValue().toString().equalsIgnoreCase(value.toString())) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.claveDesigual", sesionController.getIdCanal())));
                }
            } else {
                if (!passwordField.getSubmittedValue().toString().equalsIgnoreCase(value.toString())) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.claveDesigual", sesionController.getIdCanal())));
                }
            }

            //Validacion de datos personales
            UtilDTO utilDTO = clienteDAO.validarClaveFondo(sesionController.getUsuario().getCodUsuario(), value.toString(), sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (utilDTO == null || utilDTO.getRespuestaDTO() == null || utilDTO.getResuladosDTO() == null || !utilDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
            } else {
                if (utilDTO == null || utilDTO.getRespuestaDTO() == null || utilDTO.getResuladosDTO() == null || !utilDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, "", utilDTO.getRespuestaDTO().getTextoSP()));
                } else {
                    if (!(Boolean) utilDTO.getResuladosDTO().get("resultado")) {
                        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvDatosPers", sesionController.getIdCanal())));
                    }
                }
            }
        }
    }

    /**
     * Metodo para validar el la composicion del password
     *
     * @param name String
     * @param value Object
     * @return
     *
     */
    public boolean validarFormaClave(Object value, String name) {
        boolean valid = true;

        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            FacesContext.getCurrentInstance().addMessage("formCambioClave2:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getIdCanal())));
            valid = false;
        } else {
            if (value.toString().length() < 8 || value.toString().length() > 12) {//validacion de Longitud   
                FacesContext.getCurrentInstance().addMessage(name, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvLenght", sesionController.getIdCanal())));
                valid = false;
            } else {
                if (validarRegexParcial("([a-zA-Z\\d])\\1\\1", value.toString())) {//validacion de 3 caracteres iguales y consecutivos
                    FacesContext.getCurrentInstance().addMessage(name, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clv3Consec", sesionController.getIdCanal())));
                    valid = false;
                } else {
                    if (!validarRegexParcial("([a-z]+)", value.toString())) {//La clave debe contener al menos una letra minúscula
                        FacesContext.getCurrentInstance().addMessage(name, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvMin", sesionController.getIdCanal())));
                        valid = false;
                    } else {
                        if (!validarRegexParcial("([A-Z]+)", value.toString())) {//La clave debe contener al menos una letra mayúscula
                            FacesContext.getCurrentInstance().addMessage(name, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvMay", sesionController.getIdCanal())));
                            valid = false;
                        } else {
                            if (!validarRegexParcial("([0-9]+)", value.toString())) {//La clave debe contener al menos una letra mayúscula
                                FacesContext.getCurrentInstance().addMessage(name, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvNumero", sesionController.getIdCanal())));
                                valid = false;
                            } else {
                                if (!validarRegexParcial("([.#$/-]+)", value.toString())) { //La clave debe tener al menos 1 caracter especial del ejemplo (.-#$/)
                                    FacesContext.getCurrentInstance().addMessage(name, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvEsp", sesionController.getIdCanal())));
                                    valid = false;
                                } else {
                                    if (validarCantCaracteresConsecutivos(value.toString(), 3) >= 3) { //La clave posee 3 o mas caracteres consecutivos
                                        FacesContext.getCurrentInstance().addMessage(name, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvConse", sesionController.getIdCanal())));
                                        valid = false;
                                    } else {
                                        if (!value.toString().matches("(^[a-zA-Z0-9./$#-]{8,12}$)")) { //La clave no cumple con el formato requerido
                                            FacesContext.getCurrentInstance().addMessage(name, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.claveInv", sesionController.getIdCanal())));
                                            valid = false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return valid;
    }

    /**
     * Metodo para validar el campo motivo del formulario de transferencias.
     *
     *
     *
     * @return
     */
    public boolean validarClavesIgualesCambClv() {
        boolean valid = true;
        String value = flujoExternaUsuarioDTO.getClaveConfirm();
        String passwordField = flujoExternaUsuarioDTO.getClave();
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        if (!passwordField.equalsIgnoreCase(value)) {
            FacesContext.getCurrentInstance().addMessage("formCambioClave2:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.claveDesigual", sesionController.getIdCanal())));
            valid = false;
        } else {
            //Validacion de datos personales
            UtilDTO utilDTO = clienteDAO.validarClaveFondo(sesionController.getUsuario().getCodUsuario(), value, sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (utilDTO == null || utilDTO.getRespuestaDTO() == null || utilDTO.getResuladosDTO() == null || !utilDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formCambioClave2:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
                valid = false;
            } else {
                if (utilDTO == null || utilDTO.getRespuestaDTO() == null || utilDTO.getResuladosDTO() == null || !utilDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formCambioClave2:divMensaje", new FacesMessage(FacesMessage.SEVERITY_WARN, utilDTO.getRespuestaDTO().getTextoSP(), utilDTO.getRespuestaDTO().getTextoSP()));
                    valid = false;
                } else {
                    if (!(Boolean) utilDTO.getResuladosDTO().get("resultado")) {
                        FacesContext.getCurrentInstance().addMessage("formCambioClave2:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvDatosPers", sesionController.getIdCanal())));
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }

    /**
     * metodo action para ir a paso1 cambio de clave
     */
    public void cambioClavePaso1() {
        sesionController.setReiniciarForm(true);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso1", sesionController.getIdCanal()));

    }

    /**
     * metodo action para ir al inicio del sistema
     */
    public void cambioClaveInicio() {
        sesionController.setReiniciarForm(true);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.global.url.inicial", sesionController.getIdCanal()) + "?faces-redirect=true");

    }

    /**
     * metodo action para ir a paso2 cambio de clave
     *
     * @return
     */
    public void cambioClavePaso2() {

        UtilDTO utilDTO = new UtilDTO();
        int errores = 0;
        String url = null;
        Calendar calendar = Calendar.getInstance();

        if (this.flujoExternaUsuarioDTO.getTdd() != null && this.flujoExternaUsuarioDTO.getTdd().equalsIgnoreCase(sesionController.getTarjeta())) {

            if (parseInt(sesionController.getMes()) <= calendar.get(Calendar.MONTH)
                    && parseInt(sesionController.getAno()) == calendar.get(Calendar.YEAR)) {

                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formCambioClave1:divFechaVenc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.fechaVenc", sesionController.getIdCanal())));
                errores++;

            } else {

                Date fechaVencimientoTDD = formatearFechaStringADate("01/" + sesionController.getMes() + "/" + sesionController.getAno(), FORMATO_FECHA_SIMPLE);

                this.flujoExternaUsuarioDTO.setFechaVencimiento(fechaVencimientoTDD);

                this.desencriptarPinFase1();
                //OBTENEMOS LA SEMILLA PARA ENCRIPTAR EL PIN DE LAS VARIABLES CONFIGURADAS EN EL AMBIENTE O WEBLOGIC
                String semilla = System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal()));
                this.flujoExternaUsuarioDTO.setPin(this.cadenaBase64(this.encriptarPinDATAPRO(this.flujoExternaUsuarioDTO.getTdd(), this.flujoExternaUsuarioDTO.getPin(), semilla)));
                utilDTO = tddDAO.validarTDD(this.getFlujoExternaUsuarioDTO().getTdd(), this.getFlujoExternaUsuarioDTO().getPin(), this.formatearFecha(this.getFlujoExternaUsuarioDTO().getFechaVencimiento(), this.FORMATO_FECHA_SIMPLE), sesionController.getNombreCanal());

                if (utilDTO != null && utilDTO.getRespuestaDTO() != null && utilDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && utilDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && utilDTO.getResuladosDTO() != null) {
                    if (utilDTO.getResuladosDTO().get("1").toString().equalsIgnoreCase("true")) {
                        //SI LOS DATOS DE LOS PRODUCTOS SON VALIDOS ENTONCES
                        //RETORNA LA URL EXITOSA
                        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
                        url = parametrosController.getNombreParametro("pnw.cambioClave.url.paso2", sesionController.getIdCanal());
                    } else {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formCambioClave1:divPin", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.datosInvalidos", sesionController.getIdCanal())));
                        errores++;
                    }

                } else {
                    if (utilDTO != null && utilDTO.getRespuestaDTO() != null && !utilDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formCambioClave1:divPin", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", utilDTO.getRespuestaDTO().getTextoSP()));
                        errores++;
                    } else if (!utilDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {

                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formCambioClave1:divPin", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getIdCanal())));
                        errores++;
                    } else {

                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formCambioClave1:divPin", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.datosInvalidos", sesionController.getIdCanal())));
                        errores++;
                    }

                }
            }
        } else {

            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formCambioClave1:divPin", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.datosInvalidos", sesionController.getIdCanal())));
            errores++;
        }

        if (errores > 0) {
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso1", sesionController.getIdCanal()));
            return;
        } else {
            sesionController.setValidadorFlujo(2);
            this.redirectFacesContext(url);
            return;
        }

    }

    /**
     * metodo action para ir a paso3 cambio de clave
     *
     * @return
     */
    public void cambioClavePaso3() {

        UtilDTO utilDTO = new UtilDTO();
        boolean valida = false;
        int errores = 0;
        this.desencriptarClaveWeb();

        if ((validarFormaClave(this.flujoExternaUsuarioDTO.getClave(), "formCambioClave2:txtClave")) & (validarFormaClave(this.flujoExternaUsuarioDTO.getClaveConfirm(), "formCambioClave2:txtConfirClave"))) {

            if (!validarClavesIgualesCambClv()) {
                errores++;
            } else {

                this.flujoExternaUsuarioDTO.setClave(this.encSHA256(this.flujoExternaUsuarioDTO.getClave()));
                utilDTO = claveDAO.existeEnUltimasNClaves(sesionController.getUsuario().getId().toString(), this.flujoExternaUsuarioDTO.getClave(), parametrosController.getNombreParametro("pnw.global.validacion.cantClaves", sesionController.getIdCanal()), sesionController.getNombreCanal());

                valida = (Boolean) utilDTO.getResuladosDTO().get(1);

                if (valida) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formCambioClave2:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.clavesDifUlt", sesionController.getIdCanal())));
                    errores++;
                }
            }
        } else {
            errores++;
        }
        if (errores > 0) {
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso2", sesionController.getIdCanal()));
            return;
        }

        sesionController.setValidadorFlujo(3);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
        return;
    }

    /**
     * metodo action para Validar la TDD y generar las preguntas de Seguridad
     */
    public void generarPreguntasSeg() {
        //validacion de la cuenta-- se hace internamente pro problemas de compatibildad entre el validator y el actionListener con ajax
        String binCta = parametrosController.getNombreParametro("pnw.global.bin.nroCta", sesionController.getIdCanal());
        int longBin = binCta.length();

        if (this.flujoExternaUsuarioDTO.getNumeroCuenta() == null || this.flujoExternaUsuarioDTO.getNumeroCuenta().isEmpty() || this.flujoExternaUsuarioDTO.getNumeroCuenta().equals("")) {
            mostrarPreguntas = false;
            cantPreguntasDesafio = 0;
            cantPreguntasAutenticacion = 0;
            respuestasDD = null;
            preguntasDD = null;
            respuestasDA = null;
            preguntasDA = null;
            sesionController.setValidadorFlujo(3);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formCambioClave3:txtNroCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
            return;
        } else if (!this.flujoExternaUsuarioDTO.getNumeroCuenta().matches("^\\d{20}$") || !this.flujoExternaUsuarioDTO.getNumeroCuenta().substring(0, longBin).equalsIgnoreCase(binCta)) {
            mostrarPreguntas = false;
            cantPreguntasDesafio = 0;
            cantPreguntasAutenticacion = 0;
            respuestasDD = null;
            preguntasDD = null;
            respuestasDA = null;
            preguntasDA = null;
            sesionController.setValidadorFlujo(3);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formCambioClave3:txtNroCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.ctaInvalida", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
            return;
        }

        //Validación positiva
        this.mostrarPreguntas = false;
        cantPreguntasDesafio = Integer.parseInt(parametrosController.getValorParametro("pnw.global.validacionPositiva.cantPDD", sesionController.getIdCanal()));
        respuestasDD = new String[cantPreguntasDesafio];
        preguntasDD = new ArrayList<>();
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
            sesionController.setValidadorFlujo(3);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formCambioClave3:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
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
                sesionController.setValidadorFlujo(3);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formCambioClave3:divTDD", new FacesMessage(FacesMessage.SEVERITY_WARN, "", preguntasDesafUsr.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
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
                    sesionController.setValidadorFlujo(3);
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formCambioClave3:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
                    return;
                } else {
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_CAMBIO_CLAVE, "", "", "Generacion de Preguntas Desafio Ora11", "", "", "", "", "", "", "");
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
            sesionController.setValidadorFlujo(3);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formCambioClave3:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
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
                sesionController.setValidadorFlujo(3);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formCambioClave3:divTDD", new FacesMessage(FacesMessage.SEVERITY_WARN, "", preguntasAutUsr.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
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
                    sesionController.setValidadorFlujo(3);
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formCambioClave3:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
                    return;
                } else {
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_CAMBIO_CLAVE, "", "", "Generacion de Preguntas Autenticacion Ora9", "", "", "", "", "", "", "");
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
        sesionController.setValidadorFlujo(3);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
        return;
    }

    public void regresarCambioClavePaso2() {
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() - 1);
        this.preguntasDA = null;
        this.preguntasDD = null;
        this.flujoExternaUsuarioDTO.setNumeroCuenta("");
        this.mostrarPreguntas = false;
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso2", sesionController.getIdCanal()));

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
                FacesContext.getCurrentInstance().addMessage("formCambioClave3:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.AlfaNumEspacio", sesionController.getIdCanal())));
                valid++;
            }
        }
        return valid > 0;
    }

    /**
     * metodo action para ir a paso4 cambio de clave
     *
     *
     */
    public void cambioClavePaso4() {
        int errorValidacion = 0;
        String retorno = "";
        desencriptarRespuestasWeb();
        if (preguntasDD.size() != countResp(respuestasDDEnc) || preguntasDA.size() != countResp(respuestasDA)) {

            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formCambioClave3:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));

        } else if (!validarAlfaNumericoYEspacio(respuestasDDEnc) && !validarAlfaNumericoYEspacio(respuestasDA)) {

            UtilDTO util = ibPreguntasDesafioDAO.validarPreguntaDD(sesionController.getUsuario().getId().toString(), obtenerRespuestasDesafio(), "-", sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (util.getRespuestaDTO() == null || util.getRespuestaDTO().getCodigo() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                    || util.getResuladosDTO() == null || util.getResuladosDTO().isEmpty()) {
                //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formCambioClave3:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
                return;
            } else {
                if (util.getResuladosDTO() != null && (Boolean) util.getResuladosDTO().get("resultado")) {
                    util = preguntasAutenticacionDAO.validarPDAporCliente(this.flujoExternaUsuarioDTO.getTdd(), this.flujoExternaUsuarioDTO.getNumeroCuenta(), obtenerRespuestasAutenticacion(), "-", sesionController.getNombreCanal(), sesionController.getIdCanal());
                    if (util.getRespuestaDTO() == null || util.getRespuestaDTO().getCodigo() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formCambioClave3:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
                        return;
                    } else {
                        if (util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getResuladosDTO() != null && (Boolean) util.getResuladosDTO().get("resultado")) {
                            //SE RESETEA EL CONTADOR DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
                            RespuestaDTO respuesta = usuarioDAO.actualizarPregSegFallidas(sesionController.getUsuario().getId().toString(), 0, sesionController.getNombreCanal(), sesionController.getIdCanal());
                            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                                FacesContext context = FacesContext.getCurrentInstance(); 
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formCambioClave3:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
                                return;
                            }

                            //Se crea la nueva clave
                            // CMUJICA ENCRIPTAR ERROR FALTA
                            respuesta = claveDAO.crearClave(sesionController.getUsuario().getId().toString(), this.flujoExternaUsuarioDTO.getClave(), this.flujoExternaUsuarioDTO.getPeriodoClave(), sesionController.getNombreCanal());

                            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formCambioClave3:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
                                return;
                            }

                            //CONTINUA EL FLUJO DE OLVIDO CLAVES
                            sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_CAMBIO_CLAVE, "", "", "Validación Exitosa de Preguntas de Seguridad", "", "", "", "", "", "", "");

                            sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso4", sesionController.getIdCanal()));
                            return;
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
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_CAMBIO_CLAVE, "", "", "Intento Fallido de Validacion de Preguntas de Seguridad", "", "", "", "", "", "", "");
                retorno = validarIntentosFallidosPregSeg();
                if (sesionController.isUsuarioBloqueado()) {
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_CAMBIO_CLAVE, "", "", "Bloqueo de Usuario por Intentos Fallidos de Preguntas de Seguridad", "", "", "", "", "", "", "");
                    sesionController.mostrarDialogo(textosController.getNombreTexto("pnw.login.texto.bloqueo", sesionController.getNombreCanal()));
                    sesionController.setTextoAlerta(textosController.getNombreTexto("pnw.login.texto.bloqueoPopUp", sesionController.getNombreCanal()));
                    sesionController.setSesionInvalidada(true);
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.global.url.inicio", sesionController.getIdCanal()) + "?faces-redirect=true");
                    return;
                } else {
                    if (retorno.equalsIgnoreCase(CODIGO_EXCEPCION_GENERICA)) {
                        //SI SE GENERO UN ERROR EN LAS CONSULTAS SE ENVIA EL MSJ DE ERROR DEFINIDO EN EL METODO DE VALIDACION
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formCambioClave3:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
                        return;
                    } else {
                        //CASO CONTRARIO SE INDICA QUE SE REALIZO UN INTENTO FALLIDO DE VALIDACION EN LAS PREGUNTAS DE SEGURIDAD
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formCambioClave3:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", textosController.getNombreTexto("pnw.global.texto.respuestasInv", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));
                        return;
                    }
                }
            }
        }
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso3", sesionController.getIdCanal()));

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
        if (util != null && util.getRespuestaDTO() != null && util.getResuladosDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            cantPregSegFallidas = Integer.parseInt(util.getResuladosDTO().get("intentosFallidos").toString());
        } else {
            //ERROR EN DAO AL INTENTAR OBTENER LA CANTIDAD DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
            FacesContext.getCurrentInstance().addMessage("formCambioClave3:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            return CODIGO_EXCEPCION_GENERICA;
        }
        if (cantPregSegFallidas < limitePregSegFallidasPermitidas) {
            cantPregSegFallidas++;
            //SE INCREMENTA EL CONTADOR DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
            respuesta = usuarioDAO.actualizarPregSegFallidas(sesionController.getUsuario().getId().toString(), cantPregSegFallidas, sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formCambioClave3:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
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
            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formCambioClave3:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
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
//            //ESPERANDO CONFIRMACION
//            respuesta = tddDAO.bloquearTDD(sesionController.getUsuario().getTdd(), sesionController.getIdCanal(), sesionController.getNombreCanal());
//            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
//                //ERROR AL INTENTAR BLOQUEAR LA TDD EN EL CORE
//                FacesContext.getCurrentInstance().addMessage("formCambioClave3:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, ", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
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

    public void inicioCambioClave() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (flujoExternaUsuarioDTO == null) {
                flujoExternaUsuarioDTO = new FlujoExternaUsuarioDTO();
            }
            cantPreguntasDesafio = 0;
            respuestasDD = null;
            preguntasDD = null;
            sesionController.setValidadorFlujo(1);

            /**
             * Estas variables pueden ser modificadas en el futuro si se
             * requiere en cada paso
             */
            txtTituloInformativo = textosController.getNombreTexto("pnw.olvidoClave.titulo.olvClav", sesionController.getIdCanal());
            txtCuerpoInformativo1 = textosController.getNombreTexto("pnw.olvidoClave.texto.info1", sesionController.getIdCanal());
            txtCuerpoInformativo2 = textosController.getNombreTexto("pnw.olvidoClave.texto.info2", sesionController.getIdCanal());
            /**
             * Fin variables informativas
             */
        }
        //SE INICIALIZAN VARIABLES DE SESSION INICIALES
        String urlWSL = parametrosController.getValorParametro("pnw.global.url_wsdl", sesionController.getIdCanal());
        if (urlWSL == null || urlWSL.equals("")) {
            logger.error("NO SE INICIARA EL CAMBIO DE CLAVE DEL USUARIO ");
            logger.error("SE DEBE CONFIGURAR LOS PARÁMETROS: pnw.global.url_wsdl");
            redirectFacesContext("/ext/errores/error.xhtml");
        } else {
            getSessionScope().put(SessionAttributesNames.URL_WSDL, urlWSL);
        }
    }

    public void limpiar() {
        if (sesionController.isReiniciarForm()) {
            flujoExternaUsuarioDTO = new FlujoExternaUsuarioDTO();
            cantPreguntasDesafio = 0;
            respuestasDD = null;
            preguntasDD = null;
            mostrarPreguntas = false;
            preguntasDA = null;
            respuestasDA = null;
            sesionController.setValidadorFlujo(1);

            /**
             * Estas variables pueden ser modificadas en el futuro si se
             * requiere en cada paso
             */
            txtTituloInformativo = textosController.getNombreTexto("pnw.olvidoClave.titulo.olvClav", sesionController.getIdCanal());
            txtCuerpoInformativo1 = textosController.getNombreTexto("pnw.olvidoClave.texto.info1", sesionController.getIdCanal());
            txtCuerpoInformativo2 = textosController.getNombreTexto("pnw.olvidoClave.texto.info2", sesionController.getIdCanal());
            sesionController.setReiniciarForm(false);
            /**
             * Fin variables informativas
             */
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

    /**
     * metodo para encriptar el pin en la capa web en 3des con una semilla
     * dinamica
     */
    public void encriptarPinFase1() {
        if (this.flujoExternaUsuarioDTO.getPin() != null && this.flujoExternaUsuarioDTO.getPin().trim().length() == 4) {
            this.desEnc = new DESedeEncryption();
            desEnc.setSemilla(sesionController.getSemilla());
            this.flujoExternaUsuarioDTO.setPinEnc(desEnc.encriptar(this.flujoExternaUsuarioDTO.getPin()));
            this.flujoExternaUsuarioDTO.setPin("****");
        }
    }

    /**
     * metodo para desencriptar el pin en la capa web en 3des con una semilla
     * dinamica
     */
    public void desencriptarPinFase1() {
        this.desEnc = new DESedeEncryption();
        desEnc.setSemilla(sesionController.getSemilla());
        this.flujoExternaUsuarioDTO.setPin(desEnc.desencriptar(this.flujoExternaUsuarioDTO.getPinEnc()));
    }

    /**
     * metodo que redirecciona al paso 1
     */
    public void regresarPaso1() {
        sesionController.setValidadorFlujo(1);
        cantPreguntasDesafio = 0;
        respuestasDD = null;
        preguntasDD = null;
        mostrarPreguntas = false;
        preguntasDA = null;
        respuestasDA = null;
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * metodo que redirecciona al paso 1
     */
    public void regresarPaso2() {
        sesionController.setValidadorFlujo(2);
        cantPreguntasDesafio = 0;
        respuestasDD = null;
        preguntasDD = null;
        mostrarPreguntas = false;
        preguntasDA = null;
        respuestasDA = null;
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioClave.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CAMBIO_CLAVE)) {
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
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CAMBIO_CLAVE)) {
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
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CAMBIO_CLAVE)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo para encriptar texto en la capa web en 3des con una semilla de BD
     * para el ocultamiento de la data en la rafaga del request
     */
    public void encriptarClaveWeb() {
        if (this.flujoExternaUsuarioDTO.getClave() != null) {
            DESedeEncryption desEnc = new DESedeEncryption();
            desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal())));
            this.flujoExternaUsuarioDTO.setClaveEnc(desEnc.encriptar(this.flujoExternaUsuarioDTO.getClave()));
            this.flujoExternaUsuarioDTO.setClaveConfirmEnc(desEnc.encriptar(this.flujoExternaUsuarioDTO.getClaveConfirm()));
            this.flujoExternaUsuarioDTO.setClave("********");
            this.flujoExternaUsuarioDTO.setClaveConfirm("********");
        }
    }

    /**
     * metodo para desencriptar el texto en la capa web en 3des con una semilla
     * de BD
     */
    public void desencriptarClaveWeb() {
        DESedeEncryption desEnc = new DESedeEncryption();
        desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal())));
        this.flujoExternaUsuarioDTO.setClave(desEnc.desencriptar(this.flujoExternaUsuarioDTO.getClaveEnc()));
        this.flujoExternaUsuarioDTO.setClaveConfirm(desEnc.desencriptar(this.flujoExternaUsuarioDTO.getClaveConfirmEnc()));
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

    public void limpiarRespuestas() {
        respuestasDA = new String[cantPreguntasAutenticacion];
        respuestasDD = new String[cantPreguntasDesafio];
    }

}
