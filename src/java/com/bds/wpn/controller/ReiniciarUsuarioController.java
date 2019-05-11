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
import com.bds.wpn.dao.TddDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.FlujoExternaUsuarioDTO;
import com.bds.wpn.dto.IbCanalDTO;
import com.bds.wpn.dto.IbPregDesafioUsuarioDTO;
import com.bds.wpn.dto.IbPreguntasDesafioDTO;
import com.bds.wpn.dto.IbUsuariosDTO;
import com.bds.wpn.dto.OtpDTO;
import com.bds.wpn.dto.PreguntaAutenticacionDTO;
import com.bds.wpn.dto.PreguntasDesafioSelectedDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbCanalFacade;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_BLOQUEO_CANAL;
import static com.bds.wpn.util.BDSUtil.CODIGO_CANAL_MOBILE;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.CODIGO_SIN_RESULTADOS_JPA;
import static com.bds.wpn.util.BDSUtil.CODIGO_TRES_INTENTOS_FALLIDOS_OTP;
import static com.bds.wpn.util.BDSUtil.CODIGO_USUARIO_DESCONECTADO;
import static com.bds.wpn.util.BDSUtil.FORMATO_FECHA_COMPLETA;
import static com.bds.wpn.util.BDSUtil.FORMATO_FECHA_SIMPLE;
import static com.bds.wpn.util.BDSUtil.ID_TRANS_CERRAR_SESION;
import static com.bds.wpn.util.BDSUtil.NUEVALINEAEMAIL;
import static com.bds.wpn.util.BDSUtil.formatearFechaStringADate;
import static com.bds.wpn.util.BDSUtil.formatoAsteriscosWeb;
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
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author cesar.mujica
 */
@Named("wpnReiniciarUsuarioController")
@SessionScoped
public class ReiniciarUsuarioController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    IbUsuarioDAO usuarioDAO;

    @EJB
    NotificacionesDAO notificacionesDAO;

    @EJB
    IbPreguntasDesafioDAO ibPreguntasDesafioDAO;

    @EJB
    TddDAO tddDAO;

    @EJB
    IbCanalFacade canalFacade;

    @EJB
    IbCanalesDAO canalesDAO;

    @EJB
    IbClaveDAO claveDAO;

    @Inject
    TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    @Inject
    CanalController canalController;

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
    private boolean mostrarPreguntas = false;                                               //indicador para visualizar el formulario con las preguntas
    private DESedeEncryption desEnc;                                                        //objeto encargado de encriptar la data
    private IbUsuariosDTO ibUsuariosDTO;
    private String semilla = "";
    private int validadorFlujo = 1;
    private List<IbPregDesafioUsuarioDTO> listPDRUDTO = null;
    private String codigoOTP;
    private String fechaTransaccion = "";
    private boolean otpGenerado = false;
    private static final Logger logger = Logger.getLogger(ReiniciarUsuarioController.class.getName());

    /**
     * **************GETTERS Y SETTERS*********************
     */
    public ClienteDTO getClienteDTO() {
        return clienteDTO;
    }

    public void setClienteDTO(ClienteDTO clienteDTO) {
        this.clienteDTO = clienteDTO;
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

    public List<BigDecimal> getListIdPdd() {
        return listIdPdd;
    }

    public void setListIdPdd(List<BigDecimal> listIdPdd) {
        this.listIdPdd = listIdPdd;
    }

    public boolean isMostrarPreguntas() {
        return mostrarPreguntas;
    }

    public void setMostrarPreguntas(boolean mostrarPreguntas) {
        this.mostrarPreguntas = mostrarPreguntas;
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

    public int getValidadorFlujo() {
        return validadorFlujo;
    }

    public void setValidadorFlujo(int validadorFlujo) {
        this.validadorFlujo = validadorFlujo;
    }

    public List<IbPregDesafioUsuarioDTO> getListPDRUDTO() {
        return listPDRUDTO;
    }

    public void setListPDRUDTO(List<IbPregDesafioUsuarioDTO> listPDRUDTO) {
        this.listPDRUDTO = listPDRUDTO;
    }

    public IbUsuariosDTO getIbUsuariosDTO() {
        return ibUsuariosDTO;
    }

    public void setIbUsuariosDTO(IbUsuariosDTO ibUsuariosDTO) {
        this.ibUsuariosDTO = ibUsuariosDTO;
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
     * **************METODOS FUNCIONALES*********************
     */
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
            logger.error("NO SE INICIARA EL REINICIO DEL USUARIO ");
            logger.error("SE DEBE CONFIGURAR LOS PARÁMETROS: pnw.global.url_wsdl");
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

    public void regresarPaso1() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
    }

    public void regresarPaso3() {
        this.setValidadorFlujo(3);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso3", sesionController.getIdCanal()));
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
     * url al segundo paso de registro de usuarios
     *
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
            FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divFechaVenc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.fechaVenc", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
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
                        FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto(utilTdd.getRespuestaDTO().getDescripcionSP(), sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
                        return;
                    }
                } else {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
                    return;
                }

                //VALIDAMOS QUE EL USUARIO EXISTA EN LA TABLA USUARIO 
                ibUsuariosDTO = usuarioDAO.obtenerIbUsuarioPorCodusuario(util.getResuladosDTO().get("codCliente").toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());

                if (ibUsuariosDTO != null && ibUsuariosDTO.getRespuestaDTO() != null && ibUsuariosDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {

                    //SI NO EXISTE EL REGISTRO DE USUARIO SE NOTIFICA QUE DEBE ESTAR REGISTRADO
                    if (ibUsuariosDTO != null && ibUsuariosDTO.getRespuestaDTO() != null && ibUsuariosDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_SIN_RESULTADOS_JPA)) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.usuarioNoReg", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
                        return;
                    } else {
                        //SI EXISTE EL REGISTRO VALIDO QUE EXISTA EN IBUSUARIOCANAL
                        //TEMPORALMENTE ELIMINADA ESTA VALIDACION YA QUE SE PERMITIRA REINICIAR RESGISTROS SIN IMPORTAR EL STATUS
                        //24-08-2017 POR CONVENIO CON MIRNA Y APARICIO
//                        if (ibUsuariosDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
//                            //VALIDAMOS QUE EL USUARIO NO EXISTA EN LA TABLA USUARIO CANALES YA QUE ESTARIA REGISTRADO
//                            IbUsuariosCanalesDTO usuarioCanal = usuarioDAO.obtenerIbUsuarioPorCanal(ibUsuariosDTO.getId().toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
//                            if (usuarioCanal != null && usuarioCanal.getRespuestaDTO() != null && usuarioCanal.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && usuarioCanal.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_SIN_RESULTADOS_JPA)) {
//                                //EL USUARIO NO ESTA REGISTRADO EN EL CANAL PROCEDEMOS A NOTIFICAR QUE DEBE ESTAR REGISTRADO
//                                FacesContext context = FacesContext.getCurrentInstance();
//                                context.getExternalContext().getFlash().setKeepMessages(true);
//                                FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.usuarioNoReg", sesionController.getNombreCanal())));
//                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
//                                return;
//                            } else {
//                                if (usuarioCanal != null && usuarioCanal.getRespuestaDTO() != null && !usuarioCanal.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
//                                    FacesContext context = FacesContext.getCurrentInstance();
//                                    context.getExternalContext().getFlash().setKeepMessages(true);
//                                    FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
//                                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
//                                    return;
//                                } else {
//                                    if (usuarioCanal != null && usuarioCanal.getRespuestaDTO() != null && usuarioCanal.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && usuarioCanal.getEstatusRegistro().equalsIgnoreCase(CODIGO_BLOQUEO_CANAL)) {
//                                        FacesContext context = FacesContext.getCurrentInstance();
//                                        context.getExternalContext().getFlash().setKeepMessages(true);
//                                        FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.login.texto.bloqueo", sesionController.getNombreCanal())));
//                                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
//                                        return;
//                                    }
//                                }
//                            }
//                        }
                    }
                    //YA QUE EL USUARIO EXISTE EN EL CORE Y ESTA REGISTRADO EN ORA11 PERO NO ESTA AFILIADO AL CANAL PROCEDEMOS A VALIDAR SUS PRODUCTOS
                    //SE ELIMINA EL 3DES DE LA TDD 
                    this.desencriptarPinFase1();
                    //OBTENEMOS LA SEMILLA PARA ENCRIPTAR EL PIN DE LAS VARIABLES CONFIGURADAS EN EL AMBIENTE O WEBLOGIC
                    String semilla = System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal()));
                    this.flujoExternaUsuarioDTO.setPin(this.cadenaBase64(this.encriptarPinDATAPRO(this.flujoExternaUsuarioDTO.getTdd(), this.flujoExternaUsuarioDTO.getPin(), semilla)));
                    util = tddDAO.validarTDD(this.getFlujoExternaUsuarioDTO().getTdd(), this.getFlujoExternaUsuarioDTO().getPin(), formatearFecha(this.getFlujoExternaUsuarioDTO().getFechaVencimiento(), FORMATO_FECHA_SIMPLE), sesionController.getNombreCanal());
                    if (util != null && util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getResuladosDTO() != null) {
                        if (util.getResuladosDTO().get("1").toString().equalsIgnoreCase("true")) {
                            //SI LOS DATOS DE LOS PRODUCTOS SON VALIDOS ENTONCES
                            //RETORNA LA URL EXITOSA
                            this.setValidadorFlujo(2);
                            sesionController.setReiniciarForm(true);
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso2", sesionController.getIdCanal()));
                        } else {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.datosInvalidos", sesionController.getNombreCanal())));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
                        }
                    } else {
                        if (util != null && util.getRespuestaDTO() != null && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", util.getRespuestaDTO().getTextoSP()));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
                        } else {
                            if (util != null && !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
                            } else {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.datosInvalidos", sesionController.getNombreCanal())));
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
                            }
                        }
                    }
                } else {
                    if (ibUsuariosDTO != null && ibUsuariosDTO.getRespuestaDTO() != null && !ibUsuariosDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
                    } else {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.datosInvalidos", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
                    }
                }
            } else {
                if (util != null && util.getRespuestaDTO() != null && !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
                } else {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divTipoDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.datosInvalidos", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", sesionController.getIdCanal()));
                }
            }
        }
    }

    /**
     * Metodo que se encarga de generar un codigo de validacion para
     * transferencias
     */
    public void generarOTP() {
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setRespuestaDTO(new RespuestaDTO());

        try {
            otpDTO = notificacionesDAO.generarOTP(ibUsuariosDTO.getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        } catch (Exception e) {
        } finally {
            this.otpGenerado = true;
        }

        if (!otpDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:txtOTP", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            //sesionController.redireccionInicio("");//agregar mensaje de error
        } else {
            if (!otpDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:txtOTP", new FacesMessage(FacesMessage.SEVERITY_WARN, otpDTO.getRespuestaDTO().getDescripcionSP(), otpDTO.getRespuestaDTO().getDescripcionSP()));
            }
            if (otpDTO.getOvOTP() != null && !otpDTO.getOvOTP().trim().equalsIgnoreCase("") && ibUsuariosDTO.getCelular() != null
                    && !ibUsuariosDTO.getCelular().trim().equalsIgnoreCase("")) {
                /////////NOTIFICACION VIA SMS////////
                String textoSMS = textosController.getNombreTexto("pnw.global.sms.cuerpo", sesionController.getNombreCanal());
                String motivoSMS = textosController.getNombreTexto("pnw.global.sms.motivo", sesionController.getNombreCanal());
                HashMap<String, String> parametros = new HashMap();
                parametros.put("\\$POTP", otpDTO.getOvOTP());
                sesionController.enviarSMS(ibUsuariosDTO.getCelular(), textoSMS, parametros, motivoSMS);
            }
            this.otpGenerado = true;
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
        otpDTO = notificacionesDAO.validarOTP(ibUsuariosDTO.getCodUsuario(), codigoOTP, sesionController.getIdCanal(), sesionController.getNombreCanal());

        if (!otpDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //mostrar mensaje error generico
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:txtOTP", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso2", sesionController.getIdCanal()));
            return;
        }
        if (otpDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !otpDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
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
                sesionController.enviarSMS(ibUsuariosDTO.getCelular(), textoSMS, parametros, motivoSMS);
            }

            //se bloquea la TDD y se cierra la sesion
            if (otpDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_TRES_INTENTOS_FALLIDOS_OTP)) {
                //tddDAO.bloquearTDD(sesionController.getTarjeta(), sesionController.getIdCanal(), sesionController.getNombreCanal());
                //agregar actualizar usuarios canales
                this.codigoOTP = "";
                this.otpGenerado = false;
                sesionController.setUrlRedireccionOtp("");

                //Bloqueo canal WEB
                RespuestaDTO respuesta = canalesDAO.actualizarUsuarioCanal(ibUsuariosDTO, sesionController.getIdCanal(), sesionController.getNombreCanal(),
                        String.valueOf(CODIGO_USUARIO_DESCONECTADO), 0, "0", CODIGO_BLOQUEO_CANAL, null, sesionController.getUsuarioCanal().getLimiteInternas(), sesionController.getUsuarioCanal().getLimiteExternas(), sesionController.getUsuarioCanal().getLimiteInternasTerceros(), sesionController.getUsuarioCanal().getLimiteExternasTerceros());

                if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:txtOTP", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                }

                //Bloqueo canal MOVIL
                RespuestaDTO respuesta2 = canalesDAO.actualizarUsuarioCanal(ibUsuariosDTO, CODIGO_CANAL_MOBILE, sesionController.getNombreCanal(),
                        String.valueOf(CODIGO_USUARIO_DESCONECTADO), 0, "0", CODIGO_BLOQUEO_CANAL, null, sesionController.getUsuarioCanal().getLimiteInternas(), sesionController.getUsuarioCanal().getLimiteExternas(), sesionController.getUsuarioCanal().getLimiteInternasTerceros(), sesionController.getUsuarioCanal().getLimiteExternasTerceros());

                if (!respuesta2.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:txtOTP", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                }

                sesionController.registroBitacora(ibUsuariosDTO.getId().toString(), ID_TRANS_CERRAR_SESION, "", "", otpDTO.getRespuestaDTO().getTextoSP(), "", "", "", "", "", "", CODIGO_EXCEPCION_GENERICA);
                notificarBloqueoOTP();

                /////////NOTIFICACION VIA SMS////////
                String textoSMS = textosController.getNombreTexto("pnw.global.sms.cuerpo.bloqueo", sesionController.getNombreCanal());
                String motivoSMS = textosController.getNombreTexto("pnw.global.sms.motivo.bloqueo", sesionController.getNombreCanal());
                HashMap<String, String> parametros = new HashMap();
                parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                sesionController.enviarSMS(ibUsuariosDTO.getCelular(), textoSMS, parametros, motivoSMS);

                sesionController.redireccionInicio(otpDTO.getRespuestaDTO().getTextoSP());
            }
            //mostrar mensaje error controlado
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:txtOTP", new FacesMessage(FacesMessage.SEVERITY_WARN, otpDTO.getRespuestaDTO().getTextoSP(), otpDTO.getRespuestaDTO().getTextoSP()));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso2", sesionController.getIdCanal()));
            return;
        }
        if (otpDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && otpDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            this.setValidadorFlujo(3);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso3", sesionController.getIdCanal()));
        }
    }

    /**
     * Metodo que setea los datos y parametros para el envio del correo luego
     * del proceso de la transferencia
     */
    public void notificarBloqueoOTP() {

        String asunto = "Notificacion de Seguridad, DELSUR";
        StringBuilder texto;

        setFechaTransaccion(BDSUtil.formatearFecha(Calendar.getInstance().getTime(), BDSUtil.FORMATO_FECHA_COMPLETA));

        texto = new StringBuilder("Estimado(a) ");
        texto.append(ibUsuariosDTO.getNombre());
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

        EMailDTO emailDTO = notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("banco.email", sesionController.getIdCanal()), ibUsuariosDTO.getEmail(), asunto, texto.toString(), String.valueOf(canal.getId()), canal.getCodigo());

        //////////////////NOTIFICACIÓN AL CORREO DE SEGURIRDAD///////////////////
        String asuntoS = "Notificacion de Seguridad Bloqueo OTP, DELSUR";
        StringBuilder textoS = new StringBuilder("");
        String fechaActual = BDSUtil.formatearFecha(Calendar.getInstance().getTime(), BDSUtil.FORMATO_FECHA_COMPLETA);

        textoS.append(parametrosController.getNombreParametro("banco.nombre", sesionController.getIdCanal()));
        textoS.append(" le informa que, se han detectado varios intentos fallidos de OTP a las ");
        textoS.append(fechaActual);
        textoS.append(", para el usuario con el codigo: ");
        textoS.append(ibUsuariosDTO.getCodUsuario());
        textoS.append(NUEVALINEAEMAIL);
        textoS.append(NUEVALINEAEMAIL);
        textoS.append("Gracias por su preferencia,");
        textoS.append(NUEVALINEAEMAIL);
        textoS.append(NUEVALINEAEMAIL);
        textoS.append(parametrosController.getNombreParametro("banco.nombre", sesionController.getIdCanal()));
        textoS.append(NUEVALINEAEMAIL);
        textoS.append("Informacion Confidencial.");

        EMailDTO emailDTOS = notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("banco.email", sesionController.getIdCanal()), ibUsuariosDTO.getEmail(), asuntoS, texto.toString(), String.valueOf(canal.getId()), canal.getCodigo());

    }

    /**
     * metodo para limpiar algunas variables relacionadas con el otp
     */
    public void limpiarOtp() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.codigoOTP = "";
            if (sesionController.isReiniciarForm()) {
                this.otpGenerado = false;
                sesionController.setReiniciarForm(false);
            }
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
            FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getIdCanal())));
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
            FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.claveDesigual", sesionController.getIdCanal())));
            valid = false;
        } else {
            //Validacion de datos personales
            UtilDTO utilDTO = clienteDAO.validarClaveFondo(ibUsuariosDTO.getCodUsuario(), value, sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (utilDTO == null || utilDTO.getRespuestaDTO() == null || utilDTO.getResuladosDTO() == null || !utilDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
                valid = false;
            } else {
                if (utilDTO.getRespuestaDTO() == null || utilDTO.getResuladosDTO() == null || !utilDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divMensaje", new FacesMessage(FacesMessage.SEVERITY_WARN, "", utilDTO.getRespuestaDTO().getTextoSP()));
                    valid = false;
                } else {
                    if (!(Boolean) utilDTO.getResuladosDTO().get("resultado")) {
                        FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvDatosPers", sesionController.getIdCanal())));
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
     */
    public void paso4() {

        UtilDTO utilDTO;
        boolean valida;
        int errores = 0;
        this.desencriptarClaveWeb();
        if ((validarFormaClave(this.flujoExternaUsuarioDTO.getClave(), "formReinicioUsuario:txtClave")) & (validarFormaClave(this.flujoExternaUsuarioDTO.getClaveConfirm(), "formReinicioUsuario:txtConfirClave"))) {

            if (!validarClavesIgualesCambClv()) {
                errores++;
            } else {

                this.flujoExternaUsuarioDTO.setClave(this.encSHA256(this.flujoExternaUsuarioDTO.getClave()));
                utilDTO = claveDAO.existeEnUltimasNClaves(ibUsuariosDTO.getId().toString(), this.flujoExternaUsuarioDTO.getClave(), parametrosController.getNombreParametro("pnw.global.validacion.cantClaves", sesionController.getIdCanal()), sesionController.getNombreCanal());

                valida = (Boolean) utilDTO.getResuladosDTO().get(1);

                if (valida) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:txtConfirClave", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.clavesDifUlt", sesionController.getIdCanal())));
                    errores++;
                }
            }
        } else {
            errores++;
        }
        if (errores > 0) {
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso3", sesionController.getIdCanal()));
            return;
        }
        listIdPdd = new ArrayList<>();
        listPDDSelectedDTO = new ArrayList<>();
        IbPregDesafioUsuarioDTO preguntasDesafUsr = ibPreguntasDesafioDAO.listadoPreguntasDesafioBanco(sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (!preguntasDesafUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //MSJ ERROR CONSULTA DE PREGUNTAS DESAFIO
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso3", sesionController.getIdCanal()));
            return;
        }
        cantPreguntasDesafio = Integer.parseInt(parametrosController.getValorParametro("pnw.global.registro.cantPDD", sesionController.getIdCanal()));
        for (int i = 0; i < cantPreguntasDesafio; i++) {
            PreguntasDesafioSelectedDTO pddSelectedDTO = new PreguntasDesafioSelectedDTO();
            pddSelectedDTO.setListPreguntaDD(new ArrayList<>(preguntasDesafUsr.getPreguntasDesafioUsr()));

            listPDDSelectedDTO.add(pddSelectedDTO);
        }

        this.setValidadorFlujo(4);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso4", sesionController.getIdCanal()));
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
                    FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.cambioPdd.url.paso2", sesionController.getIdCanal()));
                } else if (preguntasDesafUsr.getRespuestaDTO().getCodigoSP() != null && preguntasDesafUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !preguntasDesafUsr.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", preguntasDesafUsr.getRespuestaDTO().getTextoSP()));

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
                FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.AlfaNumEspacio", sesionController.getIdCanal())));
                valid++;
            }
        }
        return valid > 0;
    }

    /**
     * metodo action para ir a paso5
     */
    public void paso5() {
        RespuestaDTO respuesta;
        RespuestaDTO respuestaMobile;
        desencriptarRespuestasWeb();

        if (listPDDSelectedEncDTO.size() != countResp(listPDDSelectedEncDTO)) {

            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso4", sesionController.getIdCanal()));

        } else {
            if (!validarAlfaNumericoYEspacio(listPDDSelectedEncDTO)) {

                listPDRUDTO = new ArrayList<>();
                for (PreguntasDesafioSelectedDTO pr : listPDDSelectedEncDTO) {
                    IbPregDesafioUsuarioDTO pdu = new IbPregDesafioUsuarioDTO();
                    pdu.setId(BigDecimal.ZERO);
                    pdu.setIdPregunta(pr.getPreguntaDD());
                    pdu.setIdUsuario(this.ibUsuariosDTO.getId());
                    pdu.setRespuestaPDD(this.encSHA256(pr.getRespuestaPDD().toLowerCase()));
                    listPDRUDTO.add(pdu);
                }

                //Se crea la nueva clave
                respuesta = claveDAO.crearClave(this.ibUsuariosDTO.getId().toString(), this.flujoExternaUsuarioDTO.getClave(), this.flujoExternaUsuarioDTO.getPeriodoClave(), sesionController.getNombreCanal());

                if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.registroIncomp", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso4", sesionController.getIdCanal()));
                    return;
                }

                respuesta = ibPreguntasDesafioDAO.agregarPDDUsuario(listPDRUDTO, sesionController.getNombreCanal(), sesionController.getIdCanal());

                if (respuesta == null || respuesta.getCodigo() == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    //ERROR AL INTENTAR ACTUALIZAR EL LISTADO DE PREGUNTAS DE DESAFIO
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso4", sesionController.getIdCanal()));
                    return;
                } else {
                    if (respuesta.getCodigoSP() != null && respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", respuesta.getTextoSP()));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso4", sesionController.getIdCanal()));
                        return;
                    } else {
                        // Actualiza el registro por el canal web
                        respuesta = canalesDAO.actualizarUsuarioCanal(this.ibUsuariosDTO, sesionController.getIdCanal(), sesionController.getNombreCanal(), String.valueOf(CODIGO_USUARIO_DESCONECTADO), 0, "",
                                CODIGO_STS_ACTIVO, CODIGO_STS_ACTIVO, null, null, null, null);

                        // Actualiza el registro por el canal mobile
                        respuestaMobile = canalesDAO.actualizarUsuarioCanal(this.ibUsuariosDTO, CODIGO_CANAL_MOBILE, sesionController.getNombreCanal(), String.valueOf(CODIGO_USUARIO_DESCONECTADO), 0, "",
                                CODIGO_STS_ACTIVO, CODIGO_STS_ACTIVO, null, null, null, null);

                        if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuestaMobile.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formReinicioUsuario:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso4", sesionController.getIdCanal()));
                            return;
                        }

                        //REGISTRAMOS LA BITACORA Y CONTINUAMOS EL FLUJO 
                        sesionController.registroBitacora(this.ibUsuariosDTO.getId().toString(), BDSUtil.ID_TRANS_REINICIO_USUARIO, "", "", "Reinicio Exitoso de Usuario", "", "", "", "", "", "", "");
                        this.setValidadorFlujo(5);
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso5", sesionController.getIdCanal()));
                        return;
                    }
                }
            }
        }
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.reinicio.url.paso4", sesionController.getIdCanal()));
    }

}
