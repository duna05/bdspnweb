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
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.FlujoExternaUsuarioDTO;
import com.bds.wpn.dto.IbPregDesafioUsuarioDTO;
import com.bds.wpn.dto.IbPreguntasDesafioDTO;
import com.bds.wpn.dto.IbUsuariosCanalesDTO;
import com.bds.wpn.dto.IbUsuariosDTO;
import com.bds.wpn.dto.PreguntaAutenticacionDTO;
import com.bds.wpn.dto.PreguntasDesafioSelectedDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbCanalFacade;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.FORMATO_FECHA_COMPLETA;
import static com.bds.wpn.util.BDSUtil.formatearFecha;
import com.bds.wpn.util.DESedeEncryption;
import com.bds.wpn.util.SessionAttributesNames;
import java.io.IOException;
import java.io.Serializable;
import static java.lang.Integer.parseInt;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author juan.faneite
 */
@Named("wpnRegistroUsuariosController")
@SessionScoped
public class RegistroUsuariosController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbCanalFacade canalFacade;

    @EJB
    IbPreguntasDesafioDAO ibPreguntasDesafioDAO;

    @EJB
    PreguntasAutenticacionDAO preguntasAutenticacionDAO;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    IbClaveDAO claveDAO;

    @EJB
    IbUsuarioDAO usuarioDAO;

    @EJB
    IbCanalesDAO canalesDAO;

    @EJB
    TddDAO tddDAO;

    @Inject
    TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    private ClienteDTO clienteDTO;
    private FlujoExternaUsuarioDTO flujoExternaUsuarioDTO;
    private String pregunta;
    private String[] respuestasDD;
    private ArrayList<IbPreguntasDesafioDTO> preguntasDD = null;
    private int cantPreguntasDesafio = 0;
    private String[] respuestasDA;
    private ArrayList<PreguntaAutenticacionDTO> preguntasDA = null;
    private int cantPreguntasAutenticacion = 0;
    private List<PreguntasDesafioSelectedDTO> listPDDSelectedDTO = null;                   //preguntas seleccionadas con respuestas en claro
    private List<PreguntasDesafioSelectedDTO> listPDDSelectedEncDTO = null;                //preguntas seleccionadas con respuestas encriptadas
    private List<BigDecimal> listIdPdd;
    private IbUsuariosDTO ibUsuariosDTO;
    private boolean mostrarPreguntas = false;                                               //indicador para visualizar el formulario con las preguntas
    private DESedeEncryption desEnc;                                                        //objeto encargado de encriptar la data
    private String semilla = "";
    private int validadorFlujo = 1;
    private List<IbPregDesafioUsuarioDTO> listPDRUDTO = null;
    private static final Logger logger = Logger.getLogger(RegistroUsuariosController.class.getName());

    public List<IbPregDesafioUsuarioDTO> getListPDRUDTO() {
        return listPDRUDTO;
    }

    public void setListPDRUDTO(List<IbPregDesafioUsuarioDTO> listPDRUDTO) {
        this.listPDRUDTO = listPDRUDTO;
    }

    public String[] getRespuestasDD() {
        return respuestasDD;
    }

    public void setRespuestasDD(String[] respuestasDD) {
        this.respuestasDD = respuestasDD;
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

    public List<BigDecimal> getListIdPdd() {
        return listIdPdd;
    }

    public void setListIdPdd(List<BigDecimal> listIdPdd) {
        this.listIdPdd = listIdPdd;
    }

    public List<PreguntasDesafioSelectedDTO> getListPDDSelectedDTO() {
        return listPDDSelectedDTO;
    }

    public void setListPDDSelectedDTO(List<PreguntasDesafioSelectedDTO> listPDDSelectedDTO) {
        this.listPDDSelectedDTO = listPDDSelectedDTO;
    }

    public List<PreguntasDesafioSelectedDTO> getListPDDSelectedEncDTO() {
        return listPDDSelectedEncDTO;
    }

    public void setListPDDSelectedEncDTO(List<PreguntasDesafioSelectedDTO> listPDDSelectedEncDTO) {
        this.listPDDSelectedEncDTO = listPDDSelectedEncDTO;
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

    public IbUsuariosDTO getIbUsuariosDTO() {
        return ibUsuariosDTO;
    }

    public void setIbUsuariosDTO(IbUsuariosDTO ibUsuariosDTO) {
        this.ibUsuariosDTO = ibUsuariosDTO;
    }

    public boolean isMostrarPreguntas() {
        return mostrarPreguntas;
    }

    public void setMostrarPreguntas(boolean mostrarPreguntas) {
        this.mostrarPreguntas = mostrarPreguntas;
    }

    public String getSemilla() {
        return semilla;
    }

    public void setSemilla(String semilla) {
        this.semilla = semilla;
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
     *
     * @return obtiene todos los datos del cliente
     */
    public ClienteDTO getClienteDTO() {
        return clienteDTO;
    }

    /**
     *
     * @param clienteDTO inserta todos los datos del cliente
     */
    public void setClienteDTO(ClienteDTO clienteDTO) {
        this.clienteDTO = clienteDTO;
    }

    public int getValidadorFlujo() {
        return validadorFlujo;
    }

    public void setValidadorFlujo(int validadorFlujo) {
        this.validadorFlujo = validadorFlujo;
    }

    /**
     * Metodo para validar el la composicion del password
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
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
            if (validarRegexParcial("([a-z\\d])\\1\\1", value.toString())) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clv3Consec", sesionController.getIdCanal())));
            }
            if (!validarRegexParcial("([a-z]+)", value.toString())) {//La clave debe contener al menos una letra minúscula
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvMin", sesionController.getIdCanal())));
            }
            if (!validarRegexParcial("([A-Z]+)", value.toString())) {//La clave debe contener al menos una letra mayúscula
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvMay", sesionController.getIdCanal())));
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
    public void validarClavesIgualesOlvClv(FacesContext context, UIComponent component, Object value) throws ValidatorException, Exception {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getIdCanal())));
        }
        //validacion de Longitud   
        if (value.toString().length() < 8) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvLenght", sesionController.getIdCanal())));
        }
        UIInput passwordField = (UIInput) context.getViewRoot().findComponent("formRegistroUsuario3:txtClave");

        if (!passwordField.getValue().toString().equalsIgnoreCase("********") && this.flujoExternaUsuarioDTO.getClaveEnc() == null) {
            //validacion de 3 caracteres iguales y consecutivos
            if (validarRegexParcial("([a-z\\d])\\1\\1", value.toString())) {
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
        }
    }

    /**
     * url al primer paso de registro de usuarios
     */
    public void paso1() {
        inicioRegistro();
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * url al segundo paso de registro de usuarios
     *
     * @return String
     */
    public void paso2() {

        ibUsuariosDTO = new IbUsuariosDTO();
        RespuestaDTO respuesta;
        UtilDTO util;
        mostrarPreguntas = false;
        Calendar calendar = Calendar.getInstance();

        if (parseInt(sesionController.getMes()) <= calendar.get(Calendar.MONTH)
                && parseInt(sesionController.getAno()) == calendar.get(Calendar.YEAR)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divFechaVenc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.fechaVenc", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
        } else {
            Date fechaVencimientoTDD = formatearFechaStringADate("01/" + sesionController.getMes() + "/" + sesionController.getAno(), FORMATO_FECHA_SIMPLE);
            this.flujoExternaUsuarioDTO.setFechaVencimiento(fechaVencimientoTDD);

            //VALIDAMOS QUE EL USUARIO EN EFECTO SEA CLIENTE DELSUR 
            util = clienteDAO.obtenerCodCliente(this.flujoExternaUsuarioDTO.getTipoIdentificacion() + this.flujoExternaUsuarioDTO.getIdentificacion(), sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (util.getRespuestaDTO() != null
                    && !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                    && util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_SIN_RESULTADOS_SP)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.datosInvalidos", sesionController.getNombreCanal())));  
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                return;
            } else if (util != null && util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getResuladosDTO() != null) {

                //VALIDAMOS QUE LA TDD CON LA QUE INTENTA INGRESAR SE ENCUENTRA ACTIVA EN EL CORE BANCARIO ORA9
                UtilDTO utilTdd = usuarioDAO.validarTDDActivaCore(util.getResuladosDTO().get("codCliente").toString(), this.flujoExternaUsuarioDTO.getTdd(), sesionController.getIdCanal(), sesionController.getNombreCanal());
                if (utilTdd != null && utilTdd.getRespuestaDTO() != null && utilTdd.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    if (!utilTdd.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) || !((Boolean) utilTdd.getResuladosDTO().get("resultado"))) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto(utilTdd.getRespuestaDTO().getDescripcionSP(), sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                        return;
                    }
                } else {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                    return;
                }

                //VALIDAMOS QUE EL USUARIO NO EXISTA EN LA TABLA USUARIO SI EXISTE ACTUALIZAMOS LOS DATOS SI NO LO INSERTAMOS
                ibUsuariosDTO = usuarioDAO.obtenerIbUsuarioPorCodusuario(util.getResuladosDTO().get("codCliente").toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());

                if (ibUsuariosDTO != null && ibUsuariosDTO.getRespuestaDTO() != null && ibUsuariosDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    //OBTENGO LOS DATOS DEL CORE PARA CREAR O ACTUALIZAR EL REGISTRO DE IBSUARIO
                    ClienteDTO datosCliente = clienteDAO.consultarDatosCliente(util.getResuladosDTO().get("codCliente").toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());

                    if (datosCliente == null || datosCliente.getRespuestaDTO() == null) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                        return;
                    } else if (!datosCliente.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", datosCliente.getRespuestaDTO().getTextoSP()));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                        return;
                    } else if (!datosCliente.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                        return;
                    }

                    //SI NO EXISTE EL REGISTRO DE USUARIO SE CREA
                    if (ibUsuariosDTO != null && ibUsuariosDTO.getRespuestaDTO() != null && ibUsuariosDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_SIN_RESULTADOS_JPA)) {
                        respuesta = usuarioDAO.insertarDatosIbUsuario(datosCliente, this.flujoExternaUsuarioDTO.getTdd(), BigDecimal.ONE, sesionController.getIdCanal(), sesionController.getNombreCanal());
                        if (respuesta == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                            return;
                        }
                    } else {
                        //SI EXISTE EL REGISTRO VALIDO QUE NO EXISTA EN IBUSUARIOCANAL Y SI ES ASI SE ACTUALIZA LA DATA DE IBUSUARIO
                        if (ibUsuariosDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            //VALIDAMOS QUE EL USUARIO NO EXISTA EN LA TABLA USUARIO CANALES YA QUE ESTARIA REGISTRADO
                            IbUsuariosCanalesDTO usuarioCanal = usuarioDAO.obtenerIbUsuarioPorCanal(ibUsuariosDTO.getId().toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
                            if (usuarioCanal != null && usuarioCanal.getRespuestaDTO() != null && usuarioCanal.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && usuarioCanal.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_SIN_RESULTADOS_JPA)) {
                                //EL USUARIO NO ESTA REGISTRADO EN EL CANAL PROCEDEMOS A ACTUALIZARLO YA QUE ES UN USUARIO NUEVO
                                respuesta = usuarioDAO.actualizarDatosUsuario(datosCliente, this.flujoExternaUsuarioDTO.getTdd(), sesionController.getIdCanal(), sesionController.getNombreCanal());
                                if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                    FacesContext context = FacesContext.getCurrentInstance();
                                    context.getExternalContext().getFlash().setKeepMessages(true);
                                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                                    return;
                                }
                            } else {
                                if (usuarioCanal != null && usuarioCanal.getRespuestaDTO() != null && !usuarioCanal.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                    FacesContext context = FacesContext.getCurrentInstance();
                                    context.getExternalContext().getFlash().setKeepMessages(true);
                                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                                    return;
                                } else {
                                    if (usuarioCanal != null && usuarioCanal.getRespuestaDTO() != null && usuarioCanal.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && usuarioCanal.getEstatusRegistro().equalsIgnoreCase(CODIGO_BLOQUEO_CANAL)) {
                                        FacesContext context = FacesContext.getCurrentInstance();
                                        context.getExternalContext().getFlash().setKeepMessages(true);
                                        FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.stsRegBloqueado", sesionController.getNombreCanal())));
                                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                                        return;
                                    } else {
                                        //NOTIFICAMOS QUE NO SE PUDO REALIZAR EL REGISTRO YA QUE EL USUARIO YA ESTA REGISTRADO
                                        FacesContext context = FacesContext.getCurrentInstance();
                                        context.getExternalContext().getFlash().setKeepMessages(true);
                                        FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.registro.texto.usuarioYaRegistrado", sesionController.getNombreCanal())));
                                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    //VOLVEMOS A REALIZAR LA CONSULTA CON LOS DATOS ACTAULIZADOS
                    ibUsuariosDTO = usuarioDAO.obtenerIbUsuarioPorCodusuario(util.getResuladosDTO().get("codCliente").toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
                    if (ibUsuariosDTO != null && ibUsuariosDTO.getRespuestaDTO() != null && ibUsuariosDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        //YA QUE EL USUARIO EXISTE EN EL CORE Y ESTA REGISTRADO EN ORA11 PERO NO ESTA AFILIADO AL CANAL PROCEDEMOS A VALIDAR SUS PRODUCTOS
                        //SE ELIMINA EL 3DES DE LA TDD 
                        this.desencriptarPinFase1();
                        //OBTENEMOS LA SEMILLA PARA ENCRIPTAR EL PIN DE LAS VARIABLES CONFIGURADAS EN EL AMBIENTE O WEBLOGIC
                        String semilla = System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal()));
                        this.flujoExternaUsuarioDTO.setPin(this.cadenaBase64(this.encriptarPinDATAPRO(this.flujoExternaUsuarioDTO.getTdd(), this.flujoExternaUsuarioDTO.getPin(), semilla)));
                        util = tddDAO.validarTDD(this.getFlujoExternaUsuarioDTO().getTdd(), this.getFlujoExternaUsuarioDTO().getPin(), formatearFecha(this.getFlujoExternaUsuarioDTO().getFechaVencimiento(), this.FORMATO_FECHA_SIMPLE), sesionController.getNombreCanal());
                        if (util != null && util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getResuladosDTO() != null) {
                            if (util.getResuladosDTO().get("1").toString().equalsIgnoreCase("true")) {
                                //SI LOS DATOS DE LOS PRODUCTOS SON VALIDOS ENTONCES
                                //RETORNA LA URL EXITOSA
                                this.setValidadorFlujo(2);
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso2", sesionController.getIdCanal()));
                            } else {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.datosInvalidos", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                            }

                        } else {
                            if (util != null && util.getRespuestaDTO() != null && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", util.getRespuestaDTO().getTextoSP()));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                            } else if (!util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                            } else {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.datosInvalidos", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                            }

                        }
                    } else {
                        if (ibUsuariosDTO != null && ibUsuariosDTO.getRespuestaDTO() != null && !ibUsuariosDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                        } else {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.datosInvalidos", sesionController.getNombreCanal())));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                        }
                    }
                } else {
                    if (ibUsuariosDTO != null && ibUsuariosDTO.getRespuestaDTO() != null && !ibUsuariosDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                    } else {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.datosInvalidos", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                    }
                }
            } else {
                if (util != null && util.getRespuestaDTO() != null && !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                } else {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.datosInvalidos", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
                }
            }
        }
    }

    /**
     * metodo action para Validar la TDD y generar las preguntas de Seguridad
     */
    public void generarPreguntasSeg() {
        //Validación positiva
        this.mostrarPreguntas = false;

        //PARA EL RESGISTRO SOLO SE VALIDAN LAS PREGUNTAS DE AUTENTICACION YA QUE LAS DE SEGURIDAD SE GENERAN EN EL SIGUIENTE PASO
        cantPreguntasAutenticacion = Integer.parseInt(parametrosController.getValorParametro("pnw.global.validacionPositiva.cantPDA", sesionController.getIdCanal()));
        respuestasDA = new String[cantPreguntasAutenticacion];
        preguntasDA = new ArrayList<>();
        //SE COLOCAN 0 PREGUNTAS PARA QUE EL SERVICIO RETORNE TODO EL POOL DE PDA
        PreguntaAutenticacionDTO preguntasAutUsr = preguntasAutenticacionDAO.listPDAporCliente(this.flujoExternaUsuarioDTO.getTdd(), this.flujoExternaUsuarioDTO.getNumeroCuenta(), cantPreguntasAutenticacion, sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (!preguntasAutUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //MSJ ERROR CONSULTA DE PREGUNTAS DESAFIO
            mostrarPreguntas = false;
            cantPreguntasDesafio = 0;
            cantPreguntasAutenticacion = 0;
            respuestasDD = null;
            preguntasDD = null;
            respuestasDA = null;
            preguntasDA = null;
            this.setValidadorFlujo(4);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso4", sesionController.getIdCanal()));
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
                this.setValidadorFlujo(4);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divTDD", new FacesMessage(FacesMessage.SEVERITY_WARN, "", preguntasAutUsr.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso4", sesionController.getIdCanal()));
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
                    this.setValidadorFlujo(4);
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso4", sesionController.getIdCanal()));
                    return;
                } else {
                    sesionController.registroBitacora(this.ibUsuariosDTO.getId().toString(), BDSUtil.ID_TRANS_REGISTRO_USUARIO, "", "", "Generacion de Preguntas Autenticacion Ora9", "", "", "", "", "", "", "");
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
        this.setValidadorFlujo(4);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso4", sesionController.getIdCanal()));
        return;
    }

    public void regresarPaso3() {
        this.setValidadorFlujo(3);
        this.preguntasDA = null;
        this.preguntasDD = null;
        this.mostrarPreguntas = false;
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso3", sesionController.getIdCanal()));
    }

    public void regresarPaso4() {
        this.setValidadorFlujo(4);
        this.preguntasDA = null;
        this.preguntasDD = null;
        this.mostrarPreguntas = false;
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso4", sesionController.getIdCanal()));
    }

    /**
     * url al segundo paso de registro de usuarios
     *
     * @return String
     */
    public void paso3() {

        if (this.flujoExternaUsuarioDTO.isCheckContrato()) {
            this.setValidadorFlujo(3);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso3", sesionController.getIdCanal()));
            return;

        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRegistroUsuario2:checkContrato", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.checkBoxInv", sesionController.getIdCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso2", sesionController.getIdCanal()));
            return;
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
            FacesContext.getCurrentInstance().addMessage("formRegistroUsuario3:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getIdCanal())));
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
            FacesContext.getCurrentInstance().addMessage("formRegistroUsuario3:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.claveDesigual", sesionController.getIdCanal())));
            valid = false;
        } else {
            //Validacion de datos personales
            UtilDTO utilDTO = clienteDAO.validarClaveFondo(ibUsuariosDTO.getCodUsuario(), value, sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (utilDTO == null || utilDTO.getRespuestaDTO() == null || utilDTO.getResuladosDTO() == null || !utilDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario3:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
                valid = false;
            } else {
                if (utilDTO == null || utilDTO.getRespuestaDTO() == null || utilDTO.getResuladosDTO() == null || !utilDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario3:divMensaje", new FacesMessage(FacesMessage.SEVERITY_WARN, "", utilDTO.getRespuestaDTO().getTextoSP()));
                    valid = false;
                } else {
                    if (!(Boolean) utilDTO.getResuladosDTO().get("resultado")) {
                        FacesContext.getCurrentInstance().addMessage("formRegistroUsuario3:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvDatosPers", sesionController.getIdCanal())));
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }

    /**
     * metodo action para ir a paso4
     *
     * @return
     */
    public void paso4() {

        UtilDTO utilDTO = new UtilDTO();
        boolean valida = false;
        int errores = 0;
        this.desencriptarClaveWeb();
        if ((validarFormaClave(this.flujoExternaUsuarioDTO.getClave(), "formRegistroUsuario3:txtClave")) & (validarFormaClave(this.flujoExternaUsuarioDTO.getClaveConfirm(), "formRegistroUsuario3:txtConfirClave"))) {

            if (!validarClavesIgualesCambClv()) {
                errores++;
            } else {

                this.flujoExternaUsuarioDTO.setClave(this.encSHA256(this.flujoExternaUsuarioDTO.getClave()));
                utilDTO = claveDAO.existeEnUltimasNClaves(ibUsuariosDTO.getId().toString(), this.flujoExternaUsuarioDTO.getClave(), parametrosController.getNombreParametro("pnw.global.validacion.cantClaves", sesionController.getIdCanal()), sesionController.getNombreCanal());

                valida = (Boolean) utilDTO.getResuladosDTO().get(1);

                if (valida) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario3:txtConfirClave", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.clavesDifUlt", sesionController.getIdCanal())));
                    errores++;
                }
            }
        } else {
            errores++;
        }
        if (errores > 0) {
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso3", sesionController.getIdCanal()));
            return;
        }

        this.setValidadorFlujo(4);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso4", sesionController.getIdCanal()));
        return;
    }

    /**
     * url al quinto paso de registro de usuarios
     *
     * @throws java.io.IOException
     */
    public void paso5() throws IOException {
        int errorValidacion = 0;
        String retorno = "";
        UtilDTO util;

        util = preguntasAutenticacionDAO.validarPDAporCliente(this.flujoExternaUsuarioDTO.getTdd(), this.flujoExternaUsuarioDTO.getNumeroCuenta(), obtenerRespuestasAutenticacion(), "-", sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (util.getRespuestaDTO() == null || util.getRespuestaDTO().getCodigo() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso4", sesionController.getIdCanal()));
            return;
        } else {
            if (util.getResuladosDTO() != null && (Boolean) util.getResuladosDTO().get("resultado")) {
                //SE RESETEA EL CONTADOR DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
                RespuestaDTO respuesta = usuarioDAO.actualizarPregSegFallidas(this.ibUsuariosDTO.getId().toString(), 0, sesionController.getNombreCanal(), sesionController.getIdCanal());
                if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso4", sesionController.getIdCanal()));
                    return;
                }

                if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso4", sesionController.getIdCanal()));
                    return;
                }

                //CONTINUA EL FLUJO DE Registro de usuarios
                sesionController.registroBitacora(this.ibUsuariosDTO.getId().toString(), BDSUtil.ID_TRANS_REGISTRO_USUARIO, "", "", "Validación Exitosa de Preguntas de Seguridad", "", "", "", "", "", "", "");

                listIdPdd = new ArrayList<>();
                listPDDSelectedDTO = new ArrayList<>();
                IbPregDesafioUsuarioDTO preguntasDesafUsr = ibPreguntasDesafioDAO.listadoPreguntasDesafioBanco(sesionController.getNombreCanal(), sesionController.getIdCanal().toString());
                if (!preguntasDesafUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    //MSJ ERROR CONSULTA DE PREGUNTAS DESAFIO
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso4", sesionController.getIdCanal()));
                    return;
                }
                cantPreguntasDesafio = Integer.parseInt(parametrosController.getValorParametro("pnw.global.registro.cantPDD", sesionController.getIdCanal()));
                for (int i = 0; i < cantPreguntasDesafio; i++) {
                    PreguntasDesafioSelectedDTO pddSelectedDTO = new PreguntasDesafioSelectedDTO();
                    pddSelectedDTO.setListPreguntaDD(new ArrayList<>(preguntasDesafUsr.getPreguntasDesafioUsr()));

                    listPDDSelectedDTO.add(pddSelectedDTO);
                }

                this.setValidadorFlujo(5);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso5", sesionController.getIdCanal()));
                return;
            } else {
                //INDICAMOS QUE SE ENCONTRO UNA INCOSISTENCIA EN LAS RESPUESTAS
                errorValidacion++;
            }
        }

        if (errorValidacion > 0) {
            sesionController.registroBitacora(this.ibUsuariosDTO.getId().toString(), BDSUtil.ID_TRANS_REGISTRO_USUARIO, "", "", "Intento Fallido de Validacion de Preguntas de Seguridad", "", "", "", "", "", "", "");
            retorno = validarIntentosFallidosPregSeg();
            if (sesionController.isUsuarioBloqueado()) {
                sesionController.registroBitacora(this.ibUsuariosDTO.getId().toString(), BDSUtil.ID_TRANS_REGISTRO_USUARIO, "", "", "Bloqueo de Registro de Usuario por Intentos Fallidos de Preguntas de Seguridad", "", "", "", "", "", "", "");
                sesionController.mostrarDialogo(textosController.getNombreTexto("pnw.errores.texto.stsRegBloqueado", sesionController.getNombreCanal()));
                sesionController.setTextoAlerta(textosController.getNombreTexto("pnw.login.texto.bloqueoPopUp", sesionController.getNombreCanal()));
                sesionController.setSesionInvalidada(true);
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.global.url.inicio", sesionController.getIdCanal()));
                return;
            } else {
                if (retorno.equalsIgnoreCase(CODIGO_EXCEPCION_GENERICA)) {
                    //SI SE GENERO UN ERROR EN LAS CONSULTAS SE ENVIA EL MSJ DE ERROR DEFINIDO EN EL METODO DE VALIDACION
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso4", sesionController.getIdCanal()));
                    return;
                } else {
                    //CASO CONTRARIO SE INDICA QUE SE REALIZO UN INTENTO FALLIDO DE VALIDACION EN LAS PREGUNTAS DE SEGURIDAD
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", textosController.getNombreTexto("pnw.global.texto.respuestasInv", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso4", sesionController.getIdCanal()));
                    return;
                }
            }
        }
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso4", sesionController.getIdCanal()));
        return;
    }

    private int countResp(List<PreguntasDesafioSelectedDTO> args) {
        int count = 0;
        for (PreguntasDesafioSelectedDTO s : args) {
            if (s.getRespuestaPDD() != null) {
                if (!s.getRespuestaPDD().isEmpty()) {
                    count++;
                }
            }

        }
        return count;
    }

    private boolean validarAlfaNumericoYEspacio(List<PreguntasDesafioSelectedDTO> value) {
        int valid = 0;
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        for (PreguntasDesafioSelectedDTO s : value) {
            if (!s.getRespuestaPDD().matches("^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ ]*$")) {
                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario5:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.AlfaNumEspacio", sesionController.getIdCanal())));
                valid++;
            }
        }
        return valid > 0;
    }

    /**
     * metodo action para ir a paso6
     */
    public void paso6() {
        RespuestaDTO respuesta;
        desencriptarRespuestasWeb();

        if (listPDDSelectedEncDTO.size() != countResp(listPDDSelectedEncDTO)) {

            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRegistroUsuario5:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso5", sesionController.getIdCanal()));

        } else if (!validarAlfaNumericoYEspacio(listPDDSelectedEncDTO)) {

            listPDRUDTO = new ArrayList<>();
            for (PreguntasDesafioSelectedDTO pr : listPDDSelectedEncDTO) {
                IbPregDesafioUsuarioDTO pdu = new IbPregDesafioUsuarioDTO();
                pdu.setId(BigDecimal.ZERO);
                pdu.setIdPregunta(pr.getPreguntaDD());
                pdu.setIdUsuario(this.ibUsuariosDTO.getId());
                pdu.setRespuestaPDD(this.encSHA256(pr.getRespuestaPDD().toLowerCase()));

                listPDRUDTO.add(pdu);
            }

            IbUsuariosCanalesDTO ibUsuariosCanalesDTO = new IbUsuariosCanalesDTO();

            //SE PROCESA EL CANAL WEB
            ibUsuariosCanalesDTO = canalesDAO.consultarUsuarioCanalporIdUsuario(this.ibUsuariosDTO, sesionController.getIdCanal());

            if (ibUsuariosCanalesDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                if (ibUsuariosCanalesDTO.getId() != null) {
                    if (!ibUsuariosCanalesDTO.getEstatusRegistro().equalsIgnoreCase(CODIGO_STS_ACTIVO)) {

                        respuesta = canalesDAO.actualizarUsuarioCanal(this.ibUsuariosDTO, sesionController.getIdCanal(), sesionController.getNombreCanal(),
                                null, 0, null, null, CODIGO_STS_ACTIVO, null, null, null, null);

                        if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formRegistroUsuario5:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso5", sesionController.getIdCanal()));
                            return;
                        }
                    }
                } else {
                    respuesta = canalesDAO.insertarUsuarioCanal(this.ibUsuariosDTO, "0", sesionController.getIdCanal(), sesionController.getNombreCanal());

                    if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formRegistroUsuario5:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso5", sesionController.getIdCanal()));
                        return;
                    }
                }
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario5:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso5", sesionController.getIdCanal()));
                return;
            }

            //SE PROCESA EL CANAL MOBILE
            ibUsuariosCanalesDTO = canalesDAO.consultarUsuarioCanalporIdUsuario(this.ibUsuariosDTO, CODIGO_CANAL_MOBILE);

            if (ibUsuariosCanalesDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                if (ibUsuariosCanalesDTO.getId() != null) {
                    if (!ibUsuariosCanalesDTO.getEstatusRegistro().equalsIgnoreCase(CODIGO_STS_ACTIVO)) {

                        respuesta = canalesDAO.actualizarUsuarioCanal(this.ibUsuariosDTO, CODIGO_CANAL_MOBILE, canalFacade.textoCanal(CODIGO_CANAL_MOBILE),
                                null, 0, null, null, CODIGO_STS_ACTIVO, null, null, null, null);

                        if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formRegistroUsuario5:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso5", sesionController.getIdCanal()));
                            return;
                        }
                    }
                } else {
                    respuesta = canalesDAO.insertarUsuarioCanal(this.ibUsuariosDTO, "0", CODIGO_CANAL_MOBILE, canalFacade.textoCanal(CODIGO_CANAL_MOBILE));

                    if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formRegistroUsuario5:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso5", sesionController.getIdCanal()));
                        return;
                    }
                }
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario5:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso5", sesionController.getIdCanal()));
                return;
            }

            //Se crea la nueva clave
            respuesta = claveDAO.crearClave(this.ibUsuariosDTO.getId().toString(), this.flujoExternaUsuarioDTO.getClave(), this.flujoExternaUsuarioDTO.getPeriodoClave(), sesionController.getNombreCanal());

            if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario5:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso5", sesionController.getIdCanal()));
                return;
            }

            respuesta = ibPreguntasDesafioDAO.agregarPDDUsuario(listPDRUDTO, sesionController.getNombreCanal(), sesionController.getIdCanal());

            if (respuesta == null || respuesta.getCodigo() == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL LISTADO DE PREGUNTAS DE DESAFIO
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario5:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso5", sesionController.getIdCanal()));
                return;
            } else {
                if (respuesta.getCodigoSP() != null && respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario5:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", respuesta.getTextoSP()));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso5", sesionController.getIdCanal()));
                    return;
                } else {
                    //REGISTRAMOS LA BITACORA Y CONTINUAMOS EL FLUJO 
                    sesionController.registroBitacora(this.ibUsuariosDTO.getId().toString(), BDSUtil.ID_TRANS_REGISTRO_USUARIO, "", "", "Registro Exitoso de Usuario", "", "", "", "", "", "", "");
                    this.setValidadorFlujo(6);
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso6", sesionController.getIdCanal()));
                    return;
                }
            }
        }
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso5", sesionController.getIdCanal()));
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

    public void regresarPaso2() {
        this.setValidadorFlujo(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo que se encarga de valida la cantidad de intentos fallidos de un
     * usuario al modulo de preguntas de seguridad en OLVIDO CLAVE (PDA Y PDD)
     *
     * @return indicador de error en consulta o si viene vacio el proceso se
     * concreto sin problemas
     */
    public String validarIntentosFallidosPregSeg() {
        int limitePregSegFallidasPermitidas = Integer.parseInt(parametrosController.getNombreParametro("pnw.global.validacionPositiva.cantIntentosFallidos", sesionController.getIdCanal()));
        int cantPregSegFallidas = 0;
        RespuestaDTO respuesta;
        UtilDTO util = usuarioDAO.cantidadPregSegFallidas(this.ibUsuariosDTO.getId().toString(), this.ibUsuariosDTO.getTdd(), sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (util != null && util.getRespuestaDTO() != null && util.getResuladosDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            cantPregSegFallidas = Integer.parseInt(util.getResuladosDTO().get("intentosFallidos").toString());
        } else {
            //ERROR EN DAO AL INTENTAR OBTENER LA CANTIDAD DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
            FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            return CODIGO_EXCEPCION_GENERICA;
        }
        if (cantPregSegFallidas < limitePregSegFallidasPermitidas) {
            cantPregSegFallidas++;
            //SE INCREMENTA EL CONTADOR DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
            respuesta = usuarioDAO.actualizarPregSegFallidas(this.ibUsuariosDTO.getId().toString(), cantPregSegFallidas, sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return CODIGO_EXCEPCION_GENERICA;
            }
        }
        /////////NOTIFICACION VIA SMS ULTIMO INTENTO RESTANTE////////
        if (cantPregSegFallidas == (limitePregSegFallidasPermitidas - 1)) {
            String textoSMS = textosController.getNombreTexto("pnw.global.sms.cuerpo.preBloqueo", sesionController.getNombreCanal());
            String motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.preBloqueoLogin", sesionController.getNombreCanal());
            HashMap<String, String> parametros = new HashMap();
            parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
            sesionController.enviarSMS(this.ibUsuariosDTO.getCelular(), textoSMS, parametros, motivoSMS);
            ////////NOTIFICACIÓN VÍA EMAIL AL CORREO DE SEGURIDAD//////
            sesionController.notificarEmailUltimoIntento(this.ibUsuariosDTO);
        }
        //BLOQUEO DEL USUARIO POR LIMITE DE INTENTOS FALLIDOS SUPERADOS
        if (cantPregSegFallidas >= limitePregSegFallidasPermitidas) {

            //////////////////////////////////////////////////////////
            //VALIDAMOS QUE EL USUARIO EXISTA EN EL CANAL PARA PODER REGISTRAR EL BLOQUEO
            IbUsuariosCanalesDTO ibUsuariosCanalesDTO = canalesDAO.consultarUsuarioCanalporIdUsuario(this.ibUsuariosDTO, sesionController.getIdCanal());

            if (ibUsuariosCanalesDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && ibUsuariosCanalesDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_SIN_RESULTADOS_JPA)) {
                //INSERTAMOS EN EL CANAL WEB
                respuesta = canalesDAO.insertarUsuarioCanal(this.ibUsuariosDTO, "0", sesionController.getIdCanal(), canalFacade.textoCanal(sesionController.getIdCanal()));

                if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                    return "";
                }

                respuesta = canalesDAO.actualizarUsuarioCanal(this.ibUsuariosDTO, sesionController.getIdCanal(), canalFacade.textoCanal(sesionController.getIdCanal()),
                        null, 0, null, CODIGO_BLOQUEO_CANAL, CODIGO_BLOQUEO_CANAL, null, null, null, null);

                if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                    return "";
                }

                //INSERTAMOS EN EL CANAL MOBILE
                respuesta = canalesDAO.insertarUsuarioCanal(this.ibUsuariosDTO, "0", CODIGO_CANAL_MOBILE, canalFacade.textoCanal(CODIGO_CANAL_MOBILE));

                if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                    return "";
                }

                respuesta = canalesDAO.actualizarUsuarioCanal(this.ibUsuariosDTO, CODIGO_CANAL_MOBILE, canalFacade.textoCanal(CODIGO_CANAL_MOBILE),
                        null, 0, null, CODIGO_BLOQUEO_CANAL, CODIGO_BLOQUEO_CANAL, null, null, null, null);

                if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                    return "";
                }

            }

            /////////////////////////////////////////////////////////////
            //ESPERANDO CONFIRMACION
//            respuesta = tddDAO.bloquearTDD(this.ibUsuariosDTO.getTdd(), sesionController.getIdCanal(), sesionController.getNombreCanal());
//            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
//                //ERROR AL INTENTAR BLOQUEAR LA TDD EN EL CORE
//                FacesContext.getCurrentInstance().addMessage("formRegistroUsuario4:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
//                return CODIGO_EXCEPCION_GENERICA;
//            }
            sesionController.setUsuarioBloqueado(true);
            /////////NOTIFICACION VIA SMS BLOQUEO DE USUARIO POR INTENTOS FALLIDOS////////
            String textoSMS = textosController.getNombreTexto("pnw.login.texto.bloqueo", sesionController.getNombreCanal());
            String motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.bloqueoLogin", sesionController.getNombreCanal());
            HashMap<String, String> parametros = new HashMap();
            parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
            sesionController.enviarSMS(this.ibUsuariosDTO.getCelular(), textoSMS, parametros, motivoSMS);

            ////////NOTIFICACIÓN VÍA EMAIL AL CORREO DE SEGURIDAD//////
            sesionController.notificarEmail(this.ibUsuariosDTO);
            sesionController.notificarEmailBloqueo(this.ibUsuariosDTO);
            sesionController.setTextoAlerta(textosController.getNombreTexto("pnw.login.texto.bloqueoPopUp", sesionController.getNombreCanal()));
        }
        return "";
    }

    /**
     * Metodo para validar select de preguntas
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarSelectPreg(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || new BigDecimal("-1").compareTo((BigDecimal) value) == 0) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getIdCanal())));
        }
    }

    public void eliminarPDDSelect(int valor) {
        listIdPdd = new ArrayList<>();
        for (int i = 0; i < cantPreguntasDesafio; i++) {
            if (listPDDSelectedDTO.get(i).getPreguntaDD() != null) {
                listIdPdd.add(listPDDSelectedDTO.get(i).getPreguntaDD());
            }
        }

        for (int i = 0; i < cantPreguntasDesafio; i++) {
            if (valor != i) {
                listPDDSelectedDTO.get(i).getListPreguntaDD().clear();
                IbPregDesafioUsuarioDTO preguntasDesafUsr = ibPreguntasDesafioDAO.listadoPreguntasDesafioBanco(sesionController.getNombreCanal(), sesionController.getIdCanal());

                if (preguntasDesafUsr == null || preguntasDesafUsr.getRespuestaDTO().getCodigo() == null || !preguntasDesafUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    //MSJ ERROR CONSULTA DE PREGUNTAS DESAFIO
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario5:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioPdd.url.paso2", sesionController.getIdCanal()));
                } else if (preguntasDesafUsr.getRespuestaDTO().getCodigoSP() != null && preguntasDesafUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !preguntasDesafUsr.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formRegistroUsuario5:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", preguntasDesafUsr.getRespuestaDTO().getTextoSP()));

                }

                listPDDSelectedDTO.get(i).setListPreguntaDD(new ArrayList<>(preguntasDesafUsr.getPreguntasDesafioUsr()));

                if (listPDDSelectedDTO.get(i).getPreguntaDD() == null) {
                    for (BigDecimal bd : listIdPdd) {
                        for (int x = 0; x < listPDDSelectedDTO.get(i).getListPreguntaDD().size(); x++) {
                            if (listPDDSelectedDTO.get(i).getListPreguntaDD().get(x).getId().compareTo(bd) == 0) {
                                listPDDSelectedDTO.get(i).getListPreguntaDD().remove(x);
                                x--;
                            }
                        }
                    }

                } else {
                    for (BigDecimal bd : listIdPdd) {
                        for (int x = 0; x < listPDDSelectedDTO.get(i).getListPreguntaDD().size(); x++) {
                            if (listPDDSelectedDTO.get(i).getListPreguntaDD().get(x).getId().compareTo(bd) == 0 && bd.compareTo(listPDDSelectedDTO.get(i).getPreguntaDD()) != 0) {
                                listPDDSelectedDTO.get(i).getListPreguntaDD().remove(x);
                                x--;
                            }
                        }
                    }
                }
            } else {
                listPDDSelectedDTO.get(valor).getRespuestaPDD();
                listPDDSelectedDTO.get(valor).setRespuestaPDD("");
            }
        }
    }

    /**
     * url al inicio de sesion
     */
    /*
     * Limpiar respuestas
     */
    public void limpiarRespuestasPDD() {

        for (PreguntasDesafioSelectedDTO pre : listPDDSelectedDTO) {

            if (pre.getRespuestaPDD() != null) {
                pre.setRespuestaPDD("");
            }
        }
    }

    public void inicio() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestContextPath() + parametrosController.getNombreParametro("pnw.global.url.inicio", sesionController.getIdCanal()));
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    public void inicioRegistro() {
        this.semilla = ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId() + (new Date().getTime());
        if (clienteDTO == null) {
            clienteDTO = new ClienteDTO();
        }
        if (flujoExternaUsuarioDTO == null) {
            flujoExternaUsuarioDTO = new FlujoExternaUsuarioDTO();
        }
        cantPreguntasDesafio = 0;
        respuestasDD = null;
        preguntasDD = null;
        cantPreguntasAutenticacion = 0;
        respuestasDA = null;
        preguntasDA = null;
        validadorFlujo = 1;
        
        //SE INICIALIZAN VARIABLES DE SESSION INICIALES
        String urlWSL = parametrosController.getValorParametro("pnw.global.url_wsdl", sesionController.getIdCanal());
        if (urlWSL == null || urlWSL.equals("")) {
            logger.error("NO SE INICIARA EL REGISTRO DEL USUARIO ");
            logger.error("SE DEBE CONFIGURAR LOS PARÁMETROS: pnw.global.url_wsdl");
            redirectFacesContext("/ext/errores/error.xhtml");
        } else {
            getSessionScope().put(SessionAttributesNames.URL_WSDL, urlWSL);
        }
    }

    /**
     * Metodo para validar checkBox.
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarCheckBox(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getIdCanal())));//textosController.getNombreTexto("tddInv", this.getIdCanal()), textosController.getNombreTexto("tddInv", this.getIdCanal())));
        } else if (!((Boolean) value)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.checkBoxInv", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.errores.texto.checkBoxInv", sesionController.getIdCanal()))); //textosController.getNombreTexto("tddInv", this.getIdCanal()), textosController.getNombreTexto("tddInv", this.getIdCanal())));
        }
    }

    /**
     * metodo para encriptar el pin en la capa web en 3des con una semilla
     * dinamica
     */
    public void encriptarPinFase1() {
        if (this.flujoExternaUsuarioDTO.getPin() != null && this.flujoExternaUsuarioDTO.getPin().trim().length() == 4) {
            this.desEnc = new DESedeEncryption();
            desEnc.setSemilla(semilla);
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
        desEnc.setSemilla(semilla);
        this.flujoExternaUsuarioDTO.setPin(desEnc.desencriptar(this.flujoExternaUsuarioDTO.getPinEnc()));
    }

    public void regresarPaso1() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.registro.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo2() {
        if (validadorFlujo != 2) {
            validadorFlujo = 1;
            this.regresarPaso1();
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo3() {
        if (validadorFlujo != 3) {
            validadorFlujo = 1;
            this.regresarPaso1();
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo4() {
        if (validadorFlujo != 4) {
            validadorFlujo = 1;
            this.regresarPaso1();
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo5() {
        if (validadorFlujo != 5) {
            validadorFlujo = 1;
            this.regresarPaso1();
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo6() {
        if (validadorFlujo != 6) {
            validadorFlujo = 1;
            this.regresarPaso1();
        }
    }

    /**
     * metodo para encriptar texto en la capa web en 3des con una semilla de BD
     * para el ocultamiento de la data en la rafaga del request
     */
    public void encriptarClaveWeb() {
        if (this.flujoExternaUsuarioDTO.getClave() != null & this.flujoExternaUsuarioDTO.getClaveConfirm() != null) {
            DESedeEncryption desEnc = new DESedeEncryption();
            desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal())));
            this.flujoExternaUsuarioDTO.setClaveEnc(desEnc.encriptar(this.flujoExternaUsuarioDTO.getClave()));
            this.flujoExternaUsuarioDTO.setClaveConfirmEnc(desEnc.encriptar(this.flujoExternaUsuarioDTO.getClaveConfirm()));
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
        if (this.listPDDSelectedDTO != null && this.listPDDSelectedDTO.size() > 0) {
            listPDDSelectedEncDTO = new ArrayList<>();
            DESedeEncryption desEnc = new DESedeEncryption();
            desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal())));
            PreguntasDesafioSelectedDTO tempResp = null;
            for (int i = 0; i < this.listPDDSelectedDTO.size(); i++) {
                tempResp = new PreguntasDesafioSelectedDTO();
                tempResp.setPreguntaDD(this.listPDDSelectedDTO.get(i).getPreguntaDD());
                tempResp.setRespuestaPDD(desEnc.encriptar(listPDDSelectedDTO.get(i).getRespuestaPDD()));
                listPDDSelectedEncDTO.add(tempResp);
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
        for (int i = 0; i < this.listPDDSelectedEncDTO.size(); i++) {
            this.listPDDSelectedEncDTO.get(i).setRespuestaPDD(desEnc.desencriptar(this.listPDDSelectedEncDTO.get(i).getRespuestaPDD()));
        }
    }
}
