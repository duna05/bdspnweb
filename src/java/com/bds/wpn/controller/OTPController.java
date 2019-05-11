/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.DelSurDAO;
import com.bds.wpn.dao.IbCanalesDAO;
import com.bds.wpn.dao.NotificacionesDAO;
import com.bds.wpn.dao.TddDAO;
import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.IbCanalDTO;
import com.bds.wpn.dto.OtpDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.facade.IbCanalFacade;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Logger;

/**
 *
 * @author juan.faneite
 */
@Named("wpnOTPController")
@SessionScoped
public class OTPController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    NotificacionesDAO notificacionesDAO;

    @EJB
    TddDAO tddDAO;

    @EJB
    IbCanalFacade canalFacade;

    @EJB
    IbCanalesDAO canalesDAO;

    @EJB
    DelSurDAO delSurDAO;

    @Inject
    TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    @Inject
    CanalController canalController;

    private String codigoOTP;
    private String fechaTransaccion = "";
    private boolean otpGenerado = false;
    private boolean usuarioHabilitadoP2P = false;
    private boolean moduloAfiliacionP2P;

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(OTPController.class.getName());

    public void initController() {
        if (sesionController.isValidarUsuarioPiloto()) {
            FacesContext.getCurrentInstance().
                    addMessage("formOTPController:SinPermisoP2P",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
                                    textosController.getNombreTexto("pnw.global.texto.sinPermisoP2P",
                                            sesionController.getIdCanal())));

            usuarioHabilitadoP2P = delSurDAO.validarUsuarioP2P(sesionController.getUsuario().getTipoDoc() + sesionController.getUsuario().getDocumento(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        } else {
            usuarioHabilitadoP2P = true;
        }
        
        if(sesionController.isModuloAfiliacionP2P()){
            moduloAfiliacionP2P = true;
        }else {
            moduloAfiliacionP2P = false;
        }
    }

    public boolean isOtpGenerado() {
        return otpGenerado;
    }

    public void setOtpGenerado(boolean otpGenerado) {
        this.otpGenerado = otpGenerado;
    }

    public String getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(String fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public String getCodigoOTP() {
        return codigoOTP;
    }

    public void setCodigoOTP(String codigoOTP) {
        this.codigoOTP = codigoOTP;
    }
    
    /**
     * Metodo que se encarga de generar un codigo de validacion para
     * transferencias
     */
    public void generarOTP() {
        OtpDTO otpDTO = new OtpDTO();
     
        otpDTO.setRespuestaDTO(new RespuestaDTO());
        try {
            otpDTO = notificacionesDAO.generarOTPSinEmail(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR EN CONTROLADOR OTPCONTROLLER: ")
                    .append("USR-").append(sesionController.getUsuario().getCodUsuario())
                    .append("-CH-").append(sesionController.getNombreCanal())
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } finally {
            this.otpGenerado = true;
        }

        if (!otpDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) ) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formOtp:txtOTP", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));           
        } else {
            if (!otpDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formOtp:txtOTP", new FacesMessage(FacesMessage.SEVERITY_WARN, otpDTO.getRespuestaDTO().getDescripcionSP(), otpDTO.getRespuestaDTO().getDescripcionSP()));
            }else{
                try {
                        /////////NOTIFICACION VIA SMS////////
                    HashMap<String, String> parametros = new HashMap();
                    parametros.put("\\$POTP", otpDTO.getOvOTP());
                    
                    String textoSMS   = textosController.getNombreTexto("pnw.global.sms.cuerpo", sesionController.getNombreCanal());
                    String motivoSMS  = textosController.getNombreTexto("pnw.global.sms.motivo", sesionController.getNombreCanal());
                    String numeroTefl = sesionController.getUsuario().getCelular();
                    sesionController.enviarSMS(numeroTefl, textoSMS, parametros, motivoSMS);
                    
                    /////////NOTIFICACION VIA CORREO ELECTRONICO////////
                    String codUsuario  = sesionController.getUsuario().getCodUsuario();
                    String idCanal     = sesionController.getIdCanal();
                    String nombreCanal = sesionController.getNombreCanal();

                    EMailDTO eMailDTO = new EMailDTO();
                    eMailDTO = notificacionesDAO.enviarEmailOTP(codUsuario, idCanal, nombreCanal, otpDTO.getOvOTP());

                    if (!eMailDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                        logger.error( new StringBuilder("ERROR DAO al consumir el servicio enviarEmailOTP: ")
                                .append("USR-").append(codUsuario)
                                .append("-CH-").append(nombreCanal)
                                .append("-DT-").append(new Date())
                                .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                                .append("-EXCP-").append(eMailDTO.getRespuestaDTO().getDescripcionSP()).toString());
                    }
                } catch (Exception e) {
                    logger.error( new StringBuilder("ERROR AL ENVIAR EMAIL OTP O SMS: ")
                            .append("USR-").append(sesionController.getUsuario().getCodUsuario())
                            .append("-CH-").append(sesionController.getNombreCanal())
                            .append("-DT-").append(new Date())
                            .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                            .append("-EXCP-").append(e.toString()).toString(), e);
                } finally {
                    this.otpGenerado = true;
                }
            }
        }
    }
    /**
     * metodo que se encarga de validar el codigo OTP de TOB y redirigir el
     * flujo
     *
     */
    public void validarOTPTOB() {
        OtpDTO otpDTO;
        int cantFallasPermitidas = 0;
        otpDTO = notificacionesDAO.validarOTP(sesionController.getUsuario().getCodUsuario(), codigoOTP, sesionController.getIdCanal(), sesionController.getNombreCanal());

        if (!otpDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //mostrar mensaje error generico
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            sesionController.setReiniciarForm(false);
            FacesContext.getCurrentInstance().addMessage("formOtp:txtOTP", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.otp.url.validacionOtp", sesionController.getIdCanal()));
            return;
        }
        //DESCOMENTAR
        if (otpDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !otpDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //if (false) {
            sesionController.setCantFallasActualesOTP(sesionController.getCantFallasActualesOTP() + 1);
            //validamos el parametro definido de intentos
            String fallasParam = parametrosController.getValorParametro("pnw.global.otp.intentosFallidos", sesionController.getIdCanal());
            if (fallasParam != null && !fallasParam.trim().equalsIgnoreCase("")) {
                cantFallasPermitidas = Integer.parseInt(fallasParam);
            }
            if (sesionController.getCantFallasActualesOTP() == (cantFallasPermitidas - 1)) {
                /////////NOTIFICACION VIA SMS////////
                String textoSMS = textosController.getNombreTexto("pnw.global.sms.cuerpo.preBloqueo", sesionController.getNombreCanal());
                String motivoSMS = textosController.getNombreTexto("pnw.global.sms.motivo.preBloqueo", sesionController.getNombreCanal());
                HashMap<String, String> parametros = new HashMap();
                parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);
            }

            //se bloquea la TDD y se cierra la sesion
            if (otpDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_TRES_INTENTOS_FALLIDOS_OTP)) {
                //tddDAO.bloquearTDD(sesionController.getTarjeta(), sesionController.getIdCanal(), sesionController.getNombreCanal());
                //agregar actualizar usuarios canales
                this.codigoOTP = "";
                this.otpGenerado = false;
                sesionController.setUrlRedireccionOtp("");

                //Bloqueo canal WEB
                RespuestaDTO respuesta = canalesDAO.actualizarUsuarioCanal(sesionController.getUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal(),
                        String.valueOf(CODIGO_USUARIO_DESCONECTADO), 0, "0", CODIGO_BLOQUEO_CANAL, null, sesionController.getUsuarioCanal().getLimiteInternas(), sesionController.getUsuarioCanal().getLimiteExternas(), sesionController.getUsuarioCanal().getLimiteInternasTerceros(), sesionController.getUsuarioCanal().getLimiteExternasTerceros());

                if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formOtp:txtOTP", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                }

                //Bloqueo canal MOVIL
                RespuestaDTO respuesta2 = canalesDAO.actualizarUsuarioCanal(sesionController.getUsuario(), CODIGO_CANAL_MOBILE, sesionController.getNombreCanal(),
                        String.valueOf(CODIGO_USUARIO_DESCONECTADO), 0, "0", CODIGO_BLOQUEO_CANAL, null, sesionController.getUsuarioCanal().getLimiteInternas(), sesionController.getUsuarioCanal().getLimiteExternas(), sesionController.getUsuarioCanal().getLimiteInternasTerceros(), sesionController.getUsuarioCanal().getLimiteExternasTerceros());

                if (!respuesta2.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formOtp:txtOTP", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                }

                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), ID_TRANS_CERRAR_SESION, "", "", otpDTO.getRespuestaDTO().getTextoSP(), "", "", "", "", "", "", CODIGO_EXCEPCION_GENERICA);
                notificarBloqueoOTP();

                /////////NOTIFICACION VIA SMS////////
                String textoSMS = textosController.getNombreTexto("pnw.global.sms.cuerpo.bloqueo", sesionController.getNombreCanal());
                String motivoSMS = textosController.getNombreTexto("pnw.global.sms.motivo.bloqueo", sesionController.getNombreCanal());
                HashMap<String, String> parametros = new HashMap();
                parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);

                sesionController.redireccionInicio(otpDTO.getRespuestaDTO().getTextoSP());
            }
            //mostrar mensaje error controlado
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            sesionController.setReiniciarForm(false);
            FacesContext.getCurrentInstance().addMessage("formOtp:txtOTP", new FacesMessage(FacesMessage.SEVERITY_WARN, otpDTO.getRespuestaDTO().getTextoSP(), otpDTO.getRespuestaDTO().getTextoSP()));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.otp.url.validacionOtp", sesionController.getIdCanal()));
            return;
        }
        //if(true){
        //DESCOMENTAR
        if (otpDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && otpDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            sesionController.setValidadorFlujo(2);
            sesionController.setValidacionOTP(1);
            sesionController.setReiniciarForm(true);
            this.redirectFacesContext(sesionController.getUrlRedireccionOtp());
        }
    }

    /**
     * Metodo que setea los datos y parametros para el envio del correo luego
     * del proceso de la transferencia
     */
    public void notificarBloqueoOTP() {

        String asunto = "Notificacion de Seguridad, DELSUR";
        String nombreBeneficiario = "";
        StringBuilder texto;

        setFechaTransaccion(BDSUtil.formatearFecha(Calendar.getInstance().getTime(), BDSUtil.FORMATO_FECHA_COMPLETA));

        texto = new StringBuilder("Estimado(a) ");
        texto.append(sesionController.getUsuario().getNombre());
        texto.append(NUEVALINEAEMAIL);
        texto.append(NUEVALINEAEMAIL);
        texto.append(parametrosController.getNombreParametro("banco.nombre", sesionController.getIdCanal()));
        texto.append(" le informa que su Tarjeta de Debito No: ");
        texto.append(formatoAsteriscosWeb(sesionController.getTarjeta()));
        texto.append(" fue bloqueada por su seguridad al registrarse tres intentos fallidos del Codigo de OTP para sus transacciones. Fecha de Bloqueo: ");
        texto.append(this.getFechaTransaccion());
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

        IbCanalDTO canal = canalController.getIbCanalDTO(sesionController.getIdCanal());

        EMailDTO emailDTO = notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("banco.email", sesionController.getIdCanal()), sesionController.getUsuario().getEmail(), asunto, texto.toString(), String.valueOf(canal.getId()), canal.getCodigo());

        //////////////////NOTIFICACIÓN AL CORREO DE SEGURIRDAD///////////////////
        String asuntoS = "Notificacion de Seguridad Bloqueo OTP, DELSUR";
        StringBuilder textoS = new StringBuilder("");
        String fechaActual = BDSUtil.formatearFecha(Calendar.getInstance().getTime(), BDSUtil.FORMATO_FECHA_COMPLETA);

        textoS.append(parametrosController.getNombreParametro("banco.nombre", sesionController.getIdCanal()));
        textoS.append(" le informa que, se han detectado varios intentos fallidos de OTP a las ");
        textoS.append(fechaActual);
        textoS.append(", para el usuario con el codigo: ");
        textoS.append(sesionController.getUsuario().getCodUsuario());
        textoS.append(NUEVALINEAEMAIL);
        textoS.append(NUEVALINEAEMAIL);
        textoS.append("Gracias por su preferencia,");
        textoS.append(NUEVALINEAEMAIL);
        textoS.append(NUEVALINEAEMAIL);
        textoS.append(parametrosController.getNombreParametro("banco.nombre", sesionController.getIdCanal()));
        textoS.append(NUEVALINEAEMAIL);
        textoS.append("Informacion Confidencial.");

        EMailDTO emailDTOS = notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("banco.email", sesionController.getIdCanal()), sesionController.getUsuario().getEmail(), asuntoS, texto.toString(), String.valueOf(canal.getId()), canal.getCodigo());

    }

    /**
     * metodo para limpiar algunas variables relacionadas con el otp
     */
    public void limpiarOtp() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.codigoOTP = "";
            if (sesionController.isReiniciarForm()) {
                this.otpGenerado = false;
            }
        }
    }

    public boolean isUsuarioHabilitadoP2P() {
        return usuarioHabilitadoP2P;
    }

    public void setUsuarioHabilitadoP2P(boolean usuarioHabilitadoP2P) {
        this.usuarioHabilitadoP2P = usuarioHabilitadoP2P;
    }

    public void doRegresar() {
        this.redirectFacesContext("/sec/noticias/noticias.xhtml");
    }

    public boolean isModuloAfiliacionP2P() {
        return moduloAfiliacionP2P;
    }

    public void setModuloAfiliacionP2P(boolean moduloAfiliacionP2P) {
        this.moduloAfiliacionP2P = moduloAfiliacionP2P;
    }
}
