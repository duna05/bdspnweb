/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.IbCanalesDAO;
import com.bds.wpn.dao.IbClavesOperacionesEspecialesDAO;
import com.bds.wpn.dao.IbPreguntasDesafioDAO;
import com.bds.wpn.dao.IbUsuarioDAO;
import com.bds.wpn.dao.NotificacionesDAO;
import com.bds.wpn.dao.PreguntasAutenticacionDAO;
import com.bds.wpn.dao.TddDAO;
import com.bds.wpn.dto.IbPregDesafioUsuarioDTO;
import com.bds.wpn.dto.IbPreguntasDesafioDTO;
import com.bds.wpn.dto.IbUsuariosDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import com.bds.wpn.util.DESedeEncryption;
import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
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
 * @author luis.perez
 */
@Named("wpnClaveEspecialCrearValidacionPositivaController")
@SessionScoped
public class ClaveEspecialCrearValidacionPositivaController extends BDSUtil implements Serializable {

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

    @EJB
    ClienteDAO clienteDAO;
    
    @EJB
    IbClavesOperacionesEspecialesDAO ibClavesOperacionesEspecialesDAO;

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
    private Character tipoDoc;
    private String documento;
    private IbUsuariosDTO ibUsuariosDTO;
    
    public void initController(){
        sesionController.setValidadorFlujo(1);
        this.documento = "";
    }
    
    private static final Logger logger = Logger.getLogger(ClaveEspecialCrearValidacionPositivaController.class.getName());
    
    /**
     * metodo action para Validar la TDD y generar las preguntas de Seguridad
     */
    @PostConstruct
    public void generarPreguntasSeg() {
        cantPreguntasDesafio = Integer.parseInt(parametrosController.getValorParametro("pnw.global.validacionPositiva.cantPDDClaveEsp", sesionController.getIdCanal()));
        respuestasDD = new String[cantPreguntasDesafio];
        preguntasDD = new ArrayList<>();

        IbPregDesafioUsuarioDTO preguntasDesafUsr = ibPreguntasDesafioDAO.listadoPreguntasDesafioUsuario(sesionController.getUsuario().getId().toString(), sesionController.getNombreCanal(), sesionController.getIdCanal());

        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_ACTUALIZACIONDAT, "", "", "Generacion de Preguntas Desafio Ora11", "", "", "", "", "", "", "");
        ArrayList<IbPreguntasDesafioDTO> preguntasDesaTotales = new ArrayList<>(preguntasDesafUsr.getPreguntasDesafioUsr());
        int posicionPregAleatoria = 0;
        for (int i = 0; i < cantPreguntasDesafio; i++) {
            posicionPregAleatoria = numeroAleatorio(preguntasDesaTotales.size());
            preguntasDD.add(preguntasDesaTotales.get(posicionPregAleatoria));
            preguntasDesaTotales.remove(posicionPregAleatoria);
        }
    }

    /**
     * Valida las preguntas de desafio de un usuario
     *
     * @return true cuando se valido correctamente false cuando la validacion ha
     * sido errada
     */
    public boolean validarPreguntasDesafio() {
        boolean valido;
        desencriptarRespuestasWeb();
        UtilDTO util = ibPreguntasDesafioDAO.validarPreguntaDD(sesionController.getUsuario().getId().toString(), obtenerRespuestasDesafio(), "-", sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (util.getRespuestaDTO() == null || util.getRespuestaDTO().getCodigo() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                || util.getResuladosDTO() == null || util.getResuladosDTO().isEmpty()) {
            valido = false;
        } else {
            valido = (boolean) util.getResuladosDTO().get("resultado");
        }
        if (!valido) {
            logger.error("********** TRAZA PREGUNTAS DE DESAFIO ********** |"+obtenerRespuestasDesafio()+"| Id de Usuario: "+sesionController.getUsuario().getId());
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formValidacionPositiva:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, textosController.getNombreTexto("pnw.global.texto.respuestasInv", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.respuestasInv", sesionController.getNombreCanal())));
        }
        return valido;
    }

    public String obtenerRespuestasDesafio() {
        StringBuilder rafagaDesafio = new StringBuilder();
        for (int i = 0; i < cantPreguntasDesafio; i++) {
            if (!rafagaDesafio.toString().equalsIgnoreCase("")) {
                rafagaDesafio.append("-");
            }
            String respuesta = respuestasDDEnc[i];
            rafagaDesafio.append(preguntasDD.get(i).getId()).append("-").append(this.encSHA256(respuesta.toLowerCase()));
        }
        return rafagaDesafio.toString();
    }

    /**
     * metodo que se encarga de valida la cantidad de intentos fallidos de un
     * usuario al modulo de preguntas de seguridad en CAMBIO DE CLAVE ( PDD)
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
            FacesContext.getCurrentInstance().addMessage("formValidacionPositiva:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        } else if (util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext.getCurrentInstance().addMessage("formValidacionPositiva:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
        } else if (util.getRespuestaDTO() != null && util.getResuladosDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            cantPregSegFallidas = Integer.parseInt(util.getResuladosDTO().get("intentosFallidos").toString());
        } else {
            //ERROR EN DAO AL INTENTAR OBTENER LA CANTIDAD DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
            FacesContext.getCurrentInstance().addMessage("formValidacionPositiva:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            return CODIGO_EXCEPCION_GENERICA;
        }
        if (cantPregSegFallidas < limitePregSegFallidasPermitidas) {
            cantPregSegFallidas++;
            //SE INCREMENTA EL CONTADOR DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
            respuesta = usuarioDAO.actualizarPregSegFallidas(sesionController.getUsuario().getId().toString(), cantPregSegFallidas, sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formValidacionPositiva:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return CODIGO_EXCEPCION_GENERICA;
            }
        }

        //BLOQUEO DEL USUARIO POR LIMITE DE INTENTOS FALLIDOS SUPERADOS
        if (cantPregSegFallidas >= limitePregSegFallidasPermitidas) {
            respuesta = canalesDAO.bloquearAccesoCanal(sesionController.getUsuario().getId().toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (respuesta == null || respuesta.getCodigo() == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formValidacionPositiva:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else if (respuesta.getCodigoSP() != null && respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formValidacionPositiva:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getTextoSP()));
                return CODIGO_EXCEPCION_GENERICA;
            }
            respuesta = canalesDAO.bloquearAccesoCanal(sesionController.getUsuario().getId().toString(), CODIGO_CANAL_MOBILE, sesionController.getNombreCanal());

            if (respuesta == null || respuesta.getCodigo() == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formValidacionPositiva:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else if (respuesta.getCodigoSP() != null && respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formValidacionPositiva:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getTextoSP()));
                return CODIGO_EXCEPCION_GENERICA;
            }

            sesionController.setUsuarioBloqueado(true);

            if (sesionController.isUsuarioBloqueado()) {
                sesionController.registroBitacora(String.valueOf(sesionController.getUsuario().getId()), BDSUtil.ID_TRANS_DESBL_USR, "", "", "Bloqueo de Usuario por Intentos Fallidos de Preguntas de Seguridad", "", "", "", "", "", "", "");
                sesionController.mostrarDialogo(textosController.getNombreTexto("pnw.login.texto.bloqueo", sesionController.getNombreCanal()));
                sesionController.setTextoAlerta(textosController.getNombreTexto("pnw.login.texto.bloqueoPopUp", sesionController.getNombreCanal()));
                sesionController.setSesionInvalidada(true);
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.global.url.inicio", sesionController.getIdCanal()));
            }
        }
        return "";
    }

    /**
     * Metodo para validar identificacion solo numeros
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarIdentificacion(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", BDSUtil.CODIGO_CANAL_WEB_PN)));
        }

        UIInput uiInput = (UIInput) component.findComponent("selectTipoDoc");

        if (uiInput.getLocalValue() == null || uiInput.getLocalValue().toString().isEmpty() || uiInput.getLocalValue().toString().equalsIgnoreCase("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", BDSUtil.CODIGO_CANAL_WEB_PN)));
        }

        String selectTipoDoc = uiInput.getLocalValue().toString();

        if (selectTipoDoc.equalsIgnoreCase("V") || selectTipoDoc.equalsIgnoreCase("E")) {
            if (!value.toString().matches("^[0-9]{5,9}$")) {
                throw new ValidatorException(new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.IdentifInv", BDSUtil.CODIGO_CANAL_WEB_PN)));
            }
        } else {
            if (selectTipoDoc.equalsIgnoreCase("P")) {
                if (!value.toString().matches("^[0-9a-zA-Z]{5,9}$")) {
                    throw new ValidatorException(new FacesMessage(
                            FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.IdentifInv", BDSUtil.CODIGO_CANAL_WEB_PN)));
                }
            }
        }
        
        //SE CAPTURA EL NUMERO DE IDENTIFICACION DEL USUARIO QUE SE ENCUENTRA EN LA SESSION
        String numeroIdentificacionSession   = (sesionController.getTipoDoc() + sesionController.getNroDoc()).trim();
        String numeroIdentificacionIngresado = (selectTipoDoc + value).trim();
        
        if (!numeroIdentificacionSession.equals(numeroIdentificacionIngresado)) {
            logger.error("********** TRAZA PARA NUMERO DE IDENTIFICACION SESSION CRE **********| Numero de Identificacion ingresado: |"+numeroIdentificacionIngresado+"| Numero de Identificacion session: |" + numeroIdentificacionSession + "| CodCliente Abanks |"+ sesionController.getUsuario().getCodUsuario()+"|");
            
            //SE VALIDA CONTRA LA BD EN EL CASO QUE LA SESSION ESTE GUARDANDO DE MANERA ERRADA EL VALOR
            //VALIDAMOS QUE EL USUARIO EXISTA EN LA TABLA USUARIO 
            ibUsuariosDTO = usuarioDAO.obtenerIbUsuarioPorCodusuario(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());
            String numeroIdentificacionBD = (ibUsuariosDTO.getTipoDoc() + ibUsuariosDTO.getDocumento()).trim();
            
            if (!numeroIdentificacionBD.equals(numeroIdentificacionIngresado)) {
                logger.error("********** TRAZA PARA NUMERO DE IDENTIFICACION BD CRE **********| Numero de Identificacion ingresado: |" + numeroIdentificacionIngresado + "| Numero de Identificacion bd: |" + numeroIdentificacionBD + "| CodCliente Abanks |" + sesionController.getUsuario().getCodUsuario() + "|");
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.seguridad.crear.operacionesEspeciales.cedulaErrada", BDSUtil.CODIGO_CANAL_WEB_PN)));
            }
        }
    }

    public void doContinuar() {
        if (validarPreguntasDesafio()) {
            sesionController.setValidadorFlujo(2);
            //SE VALIDARON LAS PREGUNTAS CORRECTAMENTE
            redirectFacesContext("/sec/seguridad/clave_especial_cre_ingreso_datos.xhtml");
        } else {
            //SE INCREMENTAN LOS INTENTOS FALLIDOS DE PREGUNTAS
            validarIntentosFallidosPregSeg();
            //OCURRIO UN ERROR EN LA VALIDACION DE LAS PREGUNTAS
            redirectFacesContext("/sec/seguridad/clave_especial_cre_vali_posi.xhtml");
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
    
    public boolean consultaExisteClaveOP(){
        boolean resultado = false;        
        resultado = ibClavesOperacionesEspecialesDAO.consultarClaveOperacionesEspeciales(
                sesionController.getUsuario().getId().toString(), 
                sesionController.getIdCanal(), 
                sesionController.getNombreCanal());
        return resultado;
    }

    public ArrayList<IbPreguntasDesafioDTO> getPreguntasDD() {
        return preguntasDD;
    }

    public void setPreguntasDD(ArrayList<IbPreguntasDesafioDTO> preguntasDD) {
        this.preguntasDD = preguntasDD;
    }

    public String[] getRespuestasDD() {
        return respuestasDD;
    }

    public void setRespuestasDD(String[] respuestasDD) {
        this.respuestasDD = respuestasDD;
    }

    public String[] getRespuestasDA() {
        return respuestasDA;
    }

    public void setRespuestasDA(String[] respuestasDA) {
        this.respuestasDA = respuestasDA;
    }

    public Character getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(Character tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }
}
