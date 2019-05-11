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
import com.bds.wpn.dao.NotificacionesDAO;
import com.bds.wpn.dao.PreguntasAutenticacionDAO;
import com.bds.wpn.dao.TddDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.FlujoExternaUsuarioDTO;
import com.bds.wpn.dto.IbPregDesafioUsuarioDTO;
import com.bds.wpn.dto.IbPreguntasDesafioDTO;
import com.bds.wpn.dto.IbUsuariosCanalesDTO;
import com.bds.wpn.dto.IbUsuariosDTO;
import com.bds.wpn.dto.PreguntaAutenticacionDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbCanalFacade;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.CODIGO_SIN_RESULTADOS_JPA;
import static com.bds.wpn.util.BDSUtil.FORMATO_FECHA_COMPLETA;
import static com.bds.wpn.util.BDSUtil.FORMATO_FECHA_SIMPLE;
import static com.bds.wpn.util.BDSUtil.NUEVALINEAEMAIL;
import static com.bds.wpn.util.BDSUtil.formatearFecha;
import static com.bds.wpn.util.BDSUtil.formatearFechaStringADate;
import com.bds.wpn.util.DESedeEncryption;
import com.bds.wpn.util.SessionAttributesNames;
import java.io.IOException;
import java.io.Serializable;
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author cesar.mujica
 */
@Named("wpnDesbloquearUsuarioController")
@SessionScoped
public class DesbloquearUsuarioController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbUsuarioDAO usuarioDAO;

    @EJB
    TddDAO tddDAO;

    @EJB
    IbClaveDAO claveDAO;

    @EJB
    IbCanalFacade canalFacade;

    @EJB
    IbCanalesDAO canalesDAO;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    IbPreguntasDesafioDAO ibPreguntasDesafioDAO;

    @EJB
    PreguntasAutenticacionDAO preguntasAutenticacionDAO;

    @EJB
    IbUsuarioDAO ibUsuarioDAO;

    @EJB
    IbCanalesDAO ibCanalesDAO;

    @EJB
    NotificacionesDAO notificacionesDAO;
    
    @Inject
    CanalController canalController;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;

    private ClienteDTO clienteDTO;
    private IbUsuariosDTO ibUsuariosDTO;
    private FlujoExternaUsuarioDTO flujoExternaUsuarioDTO;
    private String[] respuestasDD;
    private String[] respuestasDDEnc;
    private ArrayList<IbPreguntasDesafioDTO> preguntasDD = null;
    private int cantPreguntasDesafio = 0;
    private String[] respuestasDA;
    private ArrayList<PreguntaAutenticacionDTO> preguntasDA = null;
    private int cantPreguntasAutenticacion = 0;
    private boolean mostrarPreguntas = false;                                               //indicador para visualizar el formulario con las preguntas
    private DESedeEncryption desEnc;                                                        //objeto encargado de encriptar la data
    private String semilla = "";
    private int validadorFlujo = 1;
    private static final Logger logger = Logger.getLogger(DesbloquearUsuarioController.class.getName());

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

    public FlujoExternaUsuarioDTO getFlujoExternaUsuarioDTO() {
        return flujoExternaUsuarioDTO;
    }

    public void setFlujoExternaUsuarioDTO(FlujoExternaUsuarioDTO flujoExternaUsuarioDTO) {
        this.flujoExternaUsuarioDTO = flujoExternaUsuarioDTO;
    }

    public DESedeEncryption getDesEnc() {
        return desEnc;
    }

    public void setDesEnc(DESedeEncryption desEnc) {
        this.desEnc = desEnc;
    }

    public String getSemilla() {
        return semilla;
    }

    public void setSemilla(String semilla) {
        this.semilla = semilla;
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

    public int getValidadorFlujo() {
        return validadorFlujo;
    }

    public void setValidadorFlujo(int validadorFlujo) {
        this.validadorFlujo = validadorFlujo;
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo2() {
        if (validadorFlujo != 2) {
            validadorFlujo = 1;
            regresarPaso1();
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo3() {
        if (validadorFlujo != 3) {
            validadorFlujo = 1;
            regresarPaso1();
        }
    }

    /**
     * metodo action para ir a paso2
     *
     * @return String
     */
    public void paso2() {

        ibUsuariosDTO = new IbUsuariosDTO();
        UtilDTO util;
        mostrarPreguntas = false;
        Calendar calendar = Calendar.getInstance();

        if (parseInt(sesionController.getMes()) <= calendar.get(Calendar.MONTH)
                && parseInt(sesionController.getAno()) == calendar.get(Calendar.YEAR)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formDesbUsuario1:divFechaVenc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.fechaVenc", sesionController.getIdCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", sesionController.getIdCanal()));
            return;
        }

        Date fechaVencimientoTDD = formatearFechaStringADate("01/" + sesionController.getMes() + "/" + sesionController.getAno(), FORMATO_FECHA_SIMPLE);
        this.flujoExternaUsuarioDTO.setFechaVencimiento(fechaVencimientoTDD);

        this.desencriptarPinFase1();
        //OBTENEMOS LA SEMILLA PARA ENCRIPTAR EL PIN DE LAS VARIABLES CONFIGURADAS EN EL AMBIENTE O WEBLOGIC
        String semilla = System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal()));
        this.flujoExternaUsuarioDTO.setPin(this.cadenaBase64(this.encriptarPinDATAPRO(this.flujoExternaUsuarioDTO.getTdd(), this.flujoExternaUsuarioDTO.getPin(), semilla)));

        util = tddDAO.validarTDD(this.getFlujoExternaUsuarioDTO().getTdd(), this.getFlujoExternaUsuarioDTO().getPin(), this.formatearFecha(this.getFlujoExternaUsuarioDTO().getFechaVencimiento(), this.FORMATO_FECHA_SIMPLE), sesionController.getNombreCanal());

        if (!util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formDesbUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", sesionController.getIdCanal()));
            return;
        } else {
            if (util.getResuladosDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formDesbUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getIdCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", sesionController.getIdCanal()));
                return;
            }
        }

        if (util.getResuladosDTO().get("1").toString().equalsIgnoreCase("false")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formDesbUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.datosInvalidos", sesionController.getIdCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", sesionController.getIdCanal()));
            return;
        } else {
            //VALIDAMOS QUE EL USUARIO SEA CLIENTE DELSUR 
            util = clienteDAO.obtenerCodCliente(this.flujoExternaUsuarioDTO.getTipoIdentificacion() + this.flujoExternaUsuarioDTO.getIdentificacion(), sesionController.getNombreCanal(), sesionController.getIdCanal());

            if (!util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formDesbUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", sesionController.getIdCanal()));
                return;
            } else {
                if (util.getResuladosDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formDesbUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getIdCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", sesionController.getIdCanal()));
                    return;
                }
            }

            //VALIDAMOS QUE EL USUARIO EXISTA EN LA TABLA IB_USUARIOS 
            ibUsuariosDTO = usuarioDAO.obtenerIbUsuarioPorCodusuario(util.getResuladosDTO().get("codCliente").toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());

            //SI NO EXISTE EL REGISTRO DE USUARIO NO SE PUEDE CONTINUAR EL PROCESO
            if (ibUsuariosDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(this.CODIGO_SIN_RESULTADOS_JPA)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formDesbUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.login.texto.usuarioSinReg", sesionController.getIdCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", sesionController.getIdCanal()));
                return;
            } else if (!ibUsuariosDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) || !ibUsuariosDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(this.CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formDesbUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getIdCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", sesionController.getIdCanal()));
                return;
            }

            //VALIDAMOS QUE EL USUARIO EXISTA EN LA TABLA IB_USUARIOS_CANALES
            IbUsuariosCanalesDTO usuarioCanal = usuarioDAO.obtenerIbUsuarioPorCanal(ibUsuariosDTO.getId().toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (usuarioCanal.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_SIN_RESULTADOS_JPA)) {
                //EL USUARIO NO ESTA REGISTRADO EN EL CANAL NO SE PUEDE CONTINUAR EL PROCESO
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formDesbUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.login.texto.usuarioSinReg", sesionController.getIdCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", sesionController.getIdCanal()));
            } else {
                if (!usuarioCanal.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) || !usuarioCanal.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formDesbUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getIdCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", sesionController.getIdCanal()));
                } else {
                    if (usuarioCanal.getEstatusRegistro().equalsIgnoreCase(CODIGO_BLOQUEO_CANAL)) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formDesbUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.stsRegBloqueado", sesionController.getIdCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", sesionController.getIdCanal()));
                    } else {
                        if (!usuarioCanal.getEstatusAcceso().equalsIgnoreCase(CODIGO_BLOQUEO_CANAL)) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formDesbUsuario1:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.stsAccesoNoBloq", sesionController.getIdCanal())));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", sesionController.getIdCanal()));
                        } else {
                            //TODAS LAS VALIDACIONES SON CORRECTAS
                            this.setValidadorFlujo(2);
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                        }
                    }
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
        cantPreguntasDesafio = Integer.parseInt(parametrosController.getValorParametro("pnw.global.validacionPositiva.cantPDD", sesionController.getIdCanal()));
        respuestasDD = new String[cantPreguntasDesafio];
        preguntasDD = new ArrayList<>();
        IbPregDesafioUsuarioDTO preguntasDesafUsr = ibPreguntasDesafioDAO.listadoPreguntasDesafioUsuario(this.ibUsuariosDTO.getId().toString(), sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (!preguntasDesafUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //MSJ ERROR CONSULTA DE PREGUNTAS DESAFIO
            mostrarPreguntas = false;
            cantPreguntasDesafio = 0;
            cantPreguntasAutenticacion = 0;
            respuestasDD = null;
            preguntasDD = null;
            respuestasDA = null;
            preguntasDA = null;
            this.setValidadorFlujo(2);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
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
                this.setValidadorFlujo(2);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divTDD", new FacesMessage(FacesMessage.SEVERITY_WARN, preguntasDesafUsr.getRespuestaDTO().getTextoSP(), preguntasDesafUsr.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
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
                    this.setValidadorFlujo(2);
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                    return;
                } else {
                    sesionController.registroBitacora(this.ibUsuariosDTO.getId().toString(), BDSUtil.ID_TRANS_DESBL_USR, "", "", "Generacion de Preguntas Desafio Ora11", "", "", "", "", "", "", "");
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
            this.setValidadorFlujo(2);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
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
                this.setValidadorFlujo(2);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divTDD", new FacesMessage(FacesMessage.SEVERITY_WARN, preguntasAutUsr.getRespuestaDTO().getTextoSP(), preguntasAutUsr.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
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
                    this.setValidadorFlujo(2);
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                    return;
                } else {
                    sesionController.registroBitacora(this.ibUsuariosDTO.getId().toString(), BDSUtil.ID_TRANS_DESBL_USR, "", "", "Generacion de Preguntas Autenticacion Ora9", "", "", "", "", "", "", "");
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
        this.setValidadorFlujo(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
        return;
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
                FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.AlfaNumEspacio", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.AlfaNumEspacio", sesionController.getIdCanal())));
                valid++;
            }
        }
        return valid > 0;
    }

    /**
     * url al segundo paso de registro de usuarios
     */
    public void desbloquearPaso3() {
        int errorValidacion = 0;
        String retorno = "";
        desencriptarRespuestasWeb();

        if (preguntasDD.size() != countResp(respuestasDDEnc) || preguntasDA.size() != countResp(respuestasDA)) {

            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));

        } else if (!validarAlfaNumericoYEspacio(respuestasDDEnc) && !validarAlfaNumericoYEspacio(respuestasDA)) {

            UtilDTO util = ibPreguntasDesafioDAO.validarPreguntaDD(this.ibUsuariosDTO.getId().toString(), obtenerRespuestasDesafio(), "-", sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (util.getRespuestaDTO() == null || util.getRespuestaDTO().getCodigo() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                    || util.getResuladosDTO() == null || util.getResuladosDTO().isEmpty()) {
                //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
            } else {
                if (util.getResuladosDTO() != null && (Boolean) util.getResuladosDTO().get("resultado")) {
                    util = preguntasAutenticacionDAO.validarPDAporCliente(this.flujoExternaUsuarioDTO.getTdd(), this.flujoExternaUsuarioDTO.getNumeroCuenta(), obtenerRespuestasAutenticacion(), "-", sesionController.getNombreCanal(), sesionController.getIdCanal());
                    if (util.getRespuestaDTO() == null || util.getRespuestaDTO().getCodigo() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                    } else {
                        if (util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getResuladosDTO() != null && (Boolean) util.getResuladosDTO().get("resultado")) {
                            //SE RESETEA EL CONTADOR DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
                            RespuestaDTO respuesta = usuarioDAO.actualizarPregSegFallidas(this.ibUsuariosDTO.getId().toString(), 0, sesionController.getNombreCanal(), sesionController.getIdCanal());
                            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                            }

                            IbUsuariosCanalesDTO ibUsuariosCanalesDTO = new IbUsuariosCanalesDTO();

                            //SE PROCESA EL CANAL WEB
                            ibUsuariosCanalesDTO = canalesDAO.consultarUsuarioCanalporIdUsuario(ibUsuariosDTO, sesionController.getIdCanal());

                            if (ibUsuariosCanalesDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                if (ibUsuariosCanalesDTO.getId() != null) {
                                    if (!ibUsuariosCanalesDTO.getEstatusRegistro().equalsIgnoreCase(CODIGO_STS_ACTIVO)) {

                                        respuesta = canalesDAO.actualizarUsuarioCanal(ibUsuariosDTO, sesionController.getIdCanal(), sesionController.getNombreCanal(),
                                                null, 0, null, null, CODIGO_STS_ACTIVO, null, null, null, null);

                                        if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                            FacesContext context = FacesContext.getCurrentInstance();
                                            context.getExternalContext().getFlash().setKeepMessages(true);
                                            FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                                        }
                                    }
                                } else {
                                    respuesta = canalesDAO.insertarUsuarioCanal(ibUsuariosDTO, "0", sesionController.getIdCanal(), sesionController.getNombreCanal());

                                    if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                        FacesContext context = FacesContext.getCurrentInstance();
                                        context.getExternalContext().getFlash().setKeepMessages(true);
                                        FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                                    }
                                }
                            } else {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                            }

                            //SE PROCESA EL CANAL MOBILE
                            ibUsuariosCanalesDTO = canalesDAO.consultarUsuarioCanalporIdUsuario(ibUsuariosDTO, CODIGO_CANAL_MOBILE);

                            if (ibUsuariosCanalesDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                if (ibUsuariosCanalesDTO.getId() != null) {
                                    if (!ibUsuariosCanalesDTO.getEstatusRegistro().equalsIgnoreCase(CODIGO_STS_ACTIVO)) {

                                        respuesta = canalesDAO.actualizarUsuarioCanal(ibUsuariosDTO, CODIGO_CANAL_MOBILE, canalFacade.textoCanal(CODIGO_CANAL_MOBILE),
                                                null, 0, null, null, CODIGO_STS_ACTIVO, null, null, null, null);

                                        if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                            FacesContext context = FacesContext.getCurrentInstance();
                                            context.getExternalContext().getFlash().setKeepMessages(true);
                                            FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                                        }
                                    }
                                } else {
                                    respuesta = canalesDAO.insertarUsuarioCanal(ibUsuariosDTO, "0", CODIGO_CANAL_MOBILE, canalFacade.textoCanal(CODIGO_CANAL_MOBILE));

                                    if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                        FacesContext context = FacesContext.getCurrentInstance();
                                        context.getExternalContext().getFlash().setKeepMessages(true);
                                        FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                                    }
                                }
                            } else {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                            }

                            //validacion de intentos fallidos
                            ibUsuariosDTO = ibUsuarioDAO.obtenerIbUsuarioPorTDD(this.flujoExternaUsuarioDTO.getTdd(), sesionController.getIdCanal(), sesionController.getNombreCanal());

                            //se consulta la asociacion del usuario al canal
                            IbUsuariosCanalesDTO usuarioCanal = ibCanalesDAO.consultarUsuarioCanalporIdUsuario(ibUsuariosDTO, sesionController.getIdCanal());

                            //desbloqueamos el canal WEB
                            RespuestaDTO respuestaTemp = ibCanalesDAO.actualizarUsuarioCanal(ibUsuariosDTO, sesionController.getIdCanal(), sesionController.getNombreCanal(), String.valueOf(CODIGO_USUARIO_DESCONECTADO), 0, "0",
                                    CODIGO_STS_ACTIVO, CODIGO_STS_ACTIVO, usuarioCanal.getLimiteInternas(), usuarioCanal.getLimiteExternas(), usuarioCanal.getLimiteInternasTerceros(), usuarioCanal.getLimiteExternasTerceros());
                            if (!respuestaTemp.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                            }
                            //desbloqueamos el canal MOVIL
                            respuestaTemp = ibCanalesDAO.actualizarUsuarioCanal(ibUsuariosDTO, CODIGO_CANAL_MOBILE, sesionController.getNombreCanal(), String.valueOf(CODIGO_USUARIO_DESCONECTADO), 0, "0",
                                    CODIGO_STS_ACTIVO, CODIGO_STS_ACTIVO, usuarioCanal.getLimiteInternas(), usuarioCanal.getLimiteExternas(), usuarioCanal.getLimiteInternasTerceros(), usuarioCanal.getLimiteExternasTerceros());
                            if (!respuestaTemp.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                            }

                            //Se crea la nueva clave
                            //respuesta = claveDAO.crearClave(ibUsuariosDTO.getCodUsuario(), this.flujoExternaUsuarioDTO.getClave(), this.flujoExternaUsuarioDTO.getPeriodoClave(), sesionController.getNombreCanal());
                            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                            }

                            /////////NOTIFICACION VIA SMS////////
                            String textoSMS = textosController.getNombreTexto("pnw.global.sms.cuerpo.desblqUsuario", sesionController.getNombreCanal());
                            String motivoSMS = textosController.getNombreTexto("pnw.global.sms.motivo.desblqUsuario", sesionController.getNombreCanal());
                            sesionController.enviarSMS(this.ibUsuariosDTO.getCelular(), textoSMS, null, motivoSMS);
                            ///////NOTIFICACIÓN VÍA EMAIL//////////////
                            this.notificarDesbloqueo(this.ibUsuariosDTO);

                            //CONTINUA EL FLUJO DE DESBLOQUEO DE USUARIO CLAVES
                            sesionController.registroBitacora(this.ibUsuariosDTO.getId().toString(), BDSUtil.ID_TRANS_DESBL_USR, "", "", "Validación Exitosa de Preguntas de Seguridad", "", "", "", "", "", "", "");
                            this.setValidadorFlujo(3);
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso3", sesionController.getIdCanal()));
                        } else {
                            //INDICAMOS QUE SE ENCONTRO UNA INCOSISTENCIA EN LAS RESPUESTAS
                            errorValidacion++;
                        }
                    }
                } else {
                    //INDICAMOS QUE SE ENCONTRO UNA INCONSISTENCIA EN LAS RESPUESTAS
                    errorValidacion++;
                }
            }
            if (errorValidacion > 0) {
                sesionController.registroBitacora(this.ibUsuariosDTO.getId().toString(), BDSUtil.ID_TRANS_DESBL_USR, "", "", "Intento Fallido de Validacion de Preguntas de Seguridad", "", "", "", "", "", "", "");
                retorno = validarIntentosFallidosPregSeg();
                if (sesionController.isUsuarioBloqueado()) {
                    sesionController.registroBitacora(this.ibUsuariosDTO.getId().toString(), BDSUtil.ID_TRANS_DESBL_USR, "", "", "Bloqueo de Usuario por Intentos Fallidos de Preguntas de Seguridad", "", "", "", "", "", "", "");
                    sesionController.mostrarDialogo(textosController.getNombreTexto("pnw.login.texto.bloqueo", sesionController.getNombreCanal()));
                    sesionController.setTextoAlerta(textosController.getNombreTexto("pnw.login.texto.bloqueoPopUp", sesionController.getNombreCanal()));
                    sesionController.setSesionInvalidada(true);
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.global.url.inicio", sesionController.getIdCanal()));
                } else {
                    if (retorno.equalsIgnoreCase(CODIGO_EXCEPCION_GENERICA)) {
                        //SI SE GENERO UN ERROR EN LAS CONSULTAS SE ENVIA EL MSJ DE ERROR DEFINIDO EN EL METODO DE VALIDACION
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioPdd.url.paso2", sesionController.getIdCanal()));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                    } else {
                        //CASO CONTRARIO SE INDICA QUE SE REALIZO UN INTENTO FALLIDO DE VALIDACION EN LAS PREGUNTAS DE SEGURIDAD
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, textosController.getNombreTexto("pnw.global.texto.respuestasInv", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.respuestasInv", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
                    }
                }
            }
        }
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso2", sesionController.getIdCanal()));
    }

    public String obtenerRespuestasDesafio() {
        StringBuilder rafagaDesafio = new StringBuilder();

        for (int i = 0; i < cantPreguntasDesafio; i++) {
            if (!rafagaDesafio.toString().equalsIgnoreCase("")) {
                rafagaDesafio.append("-");
            }
            rafagaDesafio.append(preguntasDD.get(i).getId()).append("-").append(this.encSHA256(respuestasDDEnc[i].toLowerCase()));
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
            FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            return CODIGO_EXCEPCION_GENERICA;
        }
        if (cantPregSegFallidas < limitePregSegFallidasPermitidas) {
            cantPregSegFallidas++;
            //SE INCREMENTA EL CONTADOR DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
            respuesta = usuarioDAO.actualizarPregSegFallidas(this.ibUsuariosDTO.getId().toString(), cantPregSegFallidas, sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
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
            respuesta = canalesDAO.bloquearAccesoCanal(this.ibUsuariosDTO.getId().toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return CODIGO_EXCEPCION_GENERICA;
            }
            respuesta = canalesDAO.bloquearAccesoCanal(this.ibUsuariosDTO.getId().toString(), CODIGO_CANAL_MOBILE, sesionController.getNombreCanal());

            if (respuesta == null || respuesta.getCodigo() == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else if (respuesta.getCodigoSP() != null && respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formActualizacionDatosPaso1:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getTextoSP()));
                return CODIGO_EXCEPCION_GENERICA;
            }
            //ESPERANDO CONFIRMACION
//            respuesta = tddDAO.bloquearTDD(this.ibUsuariosDTO.getTdd(), sesionController.getIdCanal(), sesionController.getNombreCanal());
//            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
//                //ERROR AL INTENTAR BLOQUEAR LA TDD EN EL CORE
//                FacesContext.getCurrentInstance().addMessage("formDesbUsuario2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
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

    public void regresarPaso1() {
        this.setValidadorFlujo(1);
        this.preguntasDA = null;
        this.preguntasDD = null;
        this.cantPreguntasDesafio = 0;
        this.cantPreguntasDesafio = 0;
        this.respuestasDA = null;
        this.respuestasDD = null;
        this.mostrarPreguntas = false;
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * url al inicio de sesion
     */
    public void inicio() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestContextPath() + parametrosController.getNombreParametro("pnw.global.url.inicio", sesionController.getIdCanal()));
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    public void iniciarDesbUsuario() {
        this.validadorFlujo = 1;
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
        
        //SE INICIALIZAN VARIABLES DE SESSION INICIALES
        String urlWSL = parametrosController.getValorParametro("pnw.global.url_wsdl", sesionController.getIdCanal());
        if (urlWSL == null || urlWSL.equals("")) {
            logger.error( "NO SE INICIARA EL DESBLOQUEO USUARIO ");
            logger.error( "SE DEBE CONFIGURAR LOS PARÁMETROS: pnw.global.url_wsdl");
            redirectFacesContext("/ext/errores/error.xhtml");
        } else {
            getSessionScope().put(SessionAttributesNames.URL_WSDL, urlWSL);
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

    /**
     * Notificación realizada vía email Método que setea los datos y parametros
     * para el envio del correo luego de la ejecución del proceso
     *
     * @param ibUsuariosDTO
     */
    public void notificarDesbloqueo(IbUsuariosDTO ibUsuariosDTO) {

        String asunto = "Notificación Desbloqueo de Usuario";
        StringBuilder texto = new StringBuilder("");

        texto = new StringBuilder("Estimado(a) ");
        texto.append(ibUsuariosDTO.getNombre());
        texto.append(NUEVALINEAEMAIL);
        texto.append(NUEVALINEAEMAIL);
        texto.append(parametrosController.getNombreParametro("banco.nombre", sesionController.getIdCanal()));
        texto.append(" le informa que se ha realizado el desbloqueo de su usuario de Internet Banking Persona Natural de manera exitosa ");
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

        EMailDTO emailDTO = notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("banco.email", sesionController.getIdCanal()), ibUsuariosDTO.getEmail(), asunto, texto.toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());

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
