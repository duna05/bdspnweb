/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.DelSurDAO;
import com.bds.wpn.dao.IbAfiliacionDAO;
import com.bds.wpn.dao.IbBitacoraDAO;
import com.bds.wpn.dao.IbCanalesDAO;
import com.bds.wpn.dao.IbClaveDAO;
import com.bds.wpn.dao.IbPeriodoClaveDAO;
import com.bds.wpn.dao.IbPrefijosOperadorasDAO;
import com.bds.wpn.dao.IbUsuarioDAO;
import com.bds.wpn.dao.NotificacionesDAO;
import com.bds.wpn.dao.TddDAO;
import com.bds.wpn.dao.TranferenciasYPagosDAO;
import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.IbAfiliacionesDTO;
import com.bds.wpn.dto.IbBitacoraDTO;
import com.bds.wpn.dto.IbCanalDTO;
import com.bds.wpn.dto.IbHistoricoClavesDTO;
import com.bds.wpn.dto.IbParametrosDTO;
import com.bds.wpn.dto.IbPeriodoClaveDTO;
import com.bds.wpn.dto.IbPrefijosOperadorasDTO;
import com.bds.wpn.dto.IbUsuariosCanalesDTO;
import com.bds.wpn.dto.IbUsuariosDTO;
import com.bds.wpn.dto.MesAnoDTO;
import com.bds.wpn.dto.PosicionConsolidadaDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbCanalFacade;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import com.bds.wpn.util.DESedeEncryption;
import com.bds.wpn.util.SessionAttributesNames;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author cesar.mujica
 */
@Named("wpnInicioSesion")
@SessionScoped
public class InicioSesionController extends BDSUtil implements Serializable {

    @EJB
    IbTextosFacade textosFacade;

    @Inject
    TextosController textosController;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbCanalFacade canalFacade;

    @EJB
    NotificacionesDAO notificacionesDAO;

    @EJB
    IbBitacoraDAO ibBitacoraDAO;

    @EJB
    IbUsuarioDAO ibUsuarioDAO;

    @EJB
    IbCanalesDAO ibCanalesDAO;

    @EJB
    TranferenciasYPagosDAO tranferenciasYPagosDAO;

    @EJB
    IbAfiliacionDAO afiliacionDAO;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    TddDAO tddDAO;

    @EJB
    IbPeriodoClaveDAO periodoClaveDAO;

    @EJB
    IbClaveDAO claveDAO;

    @EJB
    IbPrefijosOperadorasDAO prefijosOperadorasDAO;

    @EJB
    DelSurDAO delSurDAO;

    @Inject
    ParametrosController parametrosController;

    @Inject
    CanalController canalController;

    private String tipoLogin = "2";          //tipo de identificador utilizado para el login
    private String tipoDoc = "V";            //tipo de documento para el login
    private String nroDoc = "";             //numero de documento para el login
    private String tarjeta = "";            //numero de TDD del usuario conectado o por ingresar
    private String clave = "";              //clave del usuario a ingresar
    private String claveEnc = "";              //clave encriptada del usuario a ingresar
    private String mensajeNull = "";        //Mensaje por defecto para los campos vacios o en null
    private String idCanal = CODIGO_CANAL_WEB;                   //Codigo del canal en sesion
    private String idSesion = "";                                   //identificador unico de la sesion
    private String nombreCanal;                                     //cigo extendido del canal en sesion
    private boolean mostrarAlerta = false; //indicador de renderizado del popup de mensajes de la pagina login
    private String mensajeAlerta = "";     //titulo del popup de mensajes de la pagina login
    private String textoAlerta = "";        //texto del popup de mensajes de la pagina login
    private boolean usuarioBloqueado = false;                       //indicador para saber si el usuario ha superado sus intentos fallidos
    private IbUsuariosCanalesDTO usuarioCanal;                      //relacion del usuario con el canal de acceso
    private int timeout;                 //tiempo en segundos especificado para la expiracion de la sesion
    private int cantFallasActualesOTP = 0;                          //contador de intentos fallidos de OTP por sesion
    private boolean sesionSimultanea = false;                       //indicador de cierre de sesion por multiples conexiones
    private String textoConfirmarSalida = "";                       //texto para el popup de confirmacion de salida
    private boolean isUltimaConexion = false;
    private String ultimaHoraConexion = "";
    private String codigoCanal;
    private String textoStatus = "";
    private String semilla = "";
    private boolean cerrarSesion = false;
    private IbUsuariosDTO usuario;
    private boolean enTransaccion = false;                  //indicador de transaccion en proceso para no ejecutar la misma transaccion dos veces
    private int validacionOTP = 0;                           //indicador de validacion de codigo OTP por sesion
    private String urlRedireccionOtp;                       //url que indica que modulo se va a accesar
    private String nombreModulo;                            //string para almacenar el nombre del modulo para arbol de menu en la esquina superior izquierda
    private String nombreTransaccion;                       //string para almacenar el nombre de la transaccion para arbol de menu en la esquina superior izquierda    
    private String idTransaccion;
    private EnumSet<tipoIdentificacion> setTipoIdentificacion = EnumSet.allOf(tipoIdentificacion.class);
    private List<tipoIdentificacion> tipoIdent = new ArrayList<>(setTipoIdentificacion);//lista que almacena los tipos de identificacion: V,E,J,G...
    private EnumSet<meses> setMeses = EnumSet.allOf(meses.class);
    private List<meses> listMeses = new ArrayList<>(setMeses);//lista que almacena los meses de un año
    private List<String> listAnos;
    private String mes;//variable temporal de almacenamiento de un mes seleccionado de una lista desplegable
    private String ano;//variable temporal de almacenamiento de un año seleccionado de una lista desplegable
    private List<MesAnoDTO> listMesesAnos;
    private MesAnoDTO mesAnoSelected = new MesAnoDTO();
    private IbPeriodoClaveDTO listPeriodoClaveDTO;
    private boolean claveExpiro = false;                        //indicador de clave expirada
    private String template;                                    //indica el template a usar para el cambio de clave
    private Map parametrosUtil = new HashMap();                 //mapa para almacenar valores varios ocultos de los fomularios para evitar manipulacion de ajax request
    private String idModuloAdm;                                 //el identificador de la opcion del menu
    private int validadorFlujo = 1;
    private IbPrefijosOperadorasDTO listPrefijosOperadoras;
    private boolean reiniciarForm = true;                //bandera para controlar el reinicio del formulario en los prerenders
    private boolean mostrarTimeOutDialog = false;                //bandera para controlar el mensaje de time out por inactividad

    private boolean sesionInvalidada;                           //variable para el redirect en los filtros
    private boolean validarUsuarioPiloto = false;
    private boolean moduloAfiliacionP2P = false;

    private String codigoTransaccion;
    private final String TRANSF_TERCEROS_DEL_SUR = "pnw.submenu.transf.tercdelsur";
    private final String TRANSF_TERCEROS_OTROS_BCOS = "pnw.submenu.transf.tercotrobanc";
    private final String PAGO_TDC_DEL_SUR = "pnw.submenu.pagos.tercdelsur";
    private final String PAGO_TDC_OTROS_BCOS = "pnw.submenu.pagos.tercotrobanc";
    private String horaInicio;
    private String horaFin;

    private static final Logger logger = Logger.getLogger(InicioSesionController.class.getName());

    public void initController() {
        //SE INICIALIZAN VARIABLES DE SESSION INICIALES
        String urlWSL = parametrosController.getValorParametro("pnw.global.url_wsdl", this.getIdCanal());
        String urlP2C = parametrosController.getValorParametro("wsdl.p2c", this.getIdCanal());
        String url_Digitel = parametrosController.getValorParametro("pnw.global.url_digitel_wsdl", this.getIdCanal());
        String codigo_cliente = parametrosController.getValorParametro("codigo.cliente.accusys", "1");        
//if ((urlWSL == null || urlWSL.equals("")) && (url_Digitel == null || url_Digitel.equals("")) || (urlP2C == null || urlP2C.equals(""))) {
       
        if ((urlWSL == null || urlWSL.equals("")) || (urlP2C == null || urlP2C.equals(""))) {
            logger.error("NO SE INICIARA LA APLICACION ");
            logger.error("SE DEBE CONFIGURAR LOS PARÁMETROS: pnw.global.url_wsdl y pnw.global.url_digitel_wsdl");
            redirectFacesContext("/ext/login/acceso_restringido.xhtml");
        } else {
            /*MODIFICACION ACCUSYS urlWSL*/
            /*DIRECION LOCAL*/urlWSL= "http://localhost:7001/ibdsws";
            /*DIRECION PRUEBA*///urlWSL= "http://10.3.1.115:7200/ibdsws";
            getSessionScope().put(SessionAttributesNames.URL_WSDL, urlWSL);
            getSessionScope().put(SessionAttributesNames.URL_P2C, urlP2C);
            getSessionScope().put(SessionAttributesNames.URL_DIGITEL, url_Digitel);
            getSessionScope().put(SessionAttributesNames.CODIGO_CLIENTE, codigo_cliente);
            
        }
        this.clave = "";
        this.nroDoc = "";
        this.tarjeta = "";        
    }

    /**
     * Retorna el indicador para el manejo de los filtros del redirect
     *
     * @return indicador para el manejo de invalidar la sesión
     */
    public boolean isSesionInvalidada() {
        return sesionInvalidada;
    }

    /**
     * Asigna el indicador para el manejo de filtros del redirect
     *
     * @param sesionInvalidada indicador para el manejo de invalidar la sesión
     *
     */
    public void setSesionInvalidada(boolean sesionInvalidada) {
        this.sesionInvalidada = sesionInvalidada;
    }

    /**
     * url al primer paso de olvido de clave
     */
    public void actionOlvidoClave() {
        //reinicio el controller para regenerar la semilla
        reiniciarBeanSesion(parametrosController.getNombreParametro("pnw.olvidoClave.url.paso1", this.getIdCanal()));
        initController();
    }

    /**
     * url al primer paso de desbloqueo de Usuario
     */
    public void desbloqueoUsuario() {
        //reinicio el controller para regenerar la semilla
        reiniciarBeanSesion(parametrosController.getNombreParametro("pnw.desbUsr.url.paso1", this.getIdCanal()));
        initController();
    }

    /**
     * url al primer paso del registro de usuarios
     */
    public void actionRegistro() {
        //reinicio el controller para regenerar la semilla
        reiniciarBeanSesion(parametrosController.getNombreParametro("pnw.registro.url.paso1", this.getIdCanal()));
        initController();
    }

    /**
     * url al primer paso de olvido de reinicio de Usuario
     */
    public void actionReinicioUsr() {
        //reinicio el controller para regenerar la semilla
        reiniciarBeanSesion(parametrosController.getNombreParametro("pnw.reinicio.url.paso1", this.getIdCanal()));
        initController();
    }

    /**
     * url al login
     */
    public void redireccionInicio() {
        try {
            sesionInvalidada = true;
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + (parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()).substring(5) + "?faces-redirect=true"));
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * url al login
     */
    public void redireccionHomeBankingExterno() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("http://www.delsur.com.ve/index.php");
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * url al login
     */
    public void redireccionPosConsolidada() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.posConsolidada.url", this.getIdCanal()));
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * Metodo para validar el campo TDD del formulario de transferencias.
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarTDD(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));
        } else {
            if (!value.toString().matches("^\\d{16}$")) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Formato de Tajerta Invalido", "Formato de Tajerta Invalido"));
            }
        }
    }

    /**
     * Metodo para validar el campo TDD del formulario de transferencias.
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarTDDDelSur(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));
        } else {
            if (!value.toString().matches("^\\d{16}$")) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Formato de Tajerta Invalido", "Formato de Tajerta Invalido"));
            } else {
                if (!value.toString().substring(0, 6).equalsIgnoreCase(parametrosController.getNombreParametro("pnw.global.bin.tdd", this.getIdCanal()))) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Formato de Tajerta Invalido", "Formato de Tajerta Invalido"));
                }
            }
        }
    }

    /**
     * Metodo para validar el campo Nro de Cuenta del formulario de
     * transferencias.
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarCta(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String binCta = parametrosController.getNombreParametro("pnw.global.bin.nroCta", this.getIdCanal());
        int longBin = binCta.length();
        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));
        } else if (!value.toString().matches("^\\d{20}$") || !value.toString().substring(0, longBin).equalsIgnoreCase(binCta)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Formato de Cuenta Invalido", "Formato de Cuenta Invalido"));
        }
    }

    /**
     * Metodo validarAlfaNumerico sirve para validar textos con letras entre
     * ellas acentuadas y Ññ, numeros y los siguientes caracteres especiales:
     * ,.-
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarAlfaNumerico(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (!value.toString().equalsIgnoreCase("********")) {
            if (!value.toString().matches("^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ .,¿?-]*$")) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.AlfaNumInvl", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.AlfaNumInvl", this.getIdCanal())));
            }
        }
    }

    /**
     * Metodo para validar el campo motivo del formulario de transferencias.
     *
     * @param context contexto de la aplicacion
     * @param component componente
     * @param value valor
     * @throws ValidatorException
     */
    public void validarMotivo(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.requerido", this.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.requerido", this.getNombreCanal()).getMensajeUsuario()));
        } else if (!value.toString().matches("^[a-zA-Z0-9 .,-]{0,50}$")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtvInv", this.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.mtvInv", this.getNombreCanal()).getMensajeUsuario()));
        }

    }

    /**
     * Metodo validarAlfaNumericoYEspacio sirve para validar textos con letras
     * entre ellas acentuadas y Ññ, numeros y espacio en blanco. No se aceptan
     * otros tipos de caracteres especiales
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarAlfaNumericoYEspacio(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (!value.toString().equalsIgnoreCase("********")) {
            if (!value.toString().matches("^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ ]*$")) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.AlfaNumEspacio", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.AlfaNumEspacio", this.getIdCanal())));
            }
        }
    }

    /**
     * Metodo validarSoloTexto sirve para validar solo letras entre ellas las
     * acentuadas y ñ
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarSoloTexto(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (!value.toString().matches("^[a-zA-ZÁÉÍÓÚáéíóúÑñ ]*$")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.soloLetrasInvl", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.soloLetrasInvl", this.getIdCanal())));
        }

    }

    public void validarAlias(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (!value.toString().matches("^[a-zA-ZÁÉÍÓÚáéíóúÑñ ]*$")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.soloLetrasInvl", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.soloLetrasInvl", this.getIdCanal())));
        }

    }

    /**
     * Metodo validarSoloNumero sirve para validar solo numeros
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarSoloNumero(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (!value.toString().matches("^[0-9]*$")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "No se permiten letras ni caracteres especiales."));
        }

    }

    /**
     * Metodo para validar OTP solo 8 digitos
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarOTP(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));
        } else if (!value.toString().matches("^\\d{8}$")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Formato de OTP Invalido", "Formato de OTP Invalido"));
        }
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
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));
        }

        UIInput uiInput = (UIInput) component.findComponent("selectTipoDoc");

        if (uiInput.getLocalValue() == null || uiInput.getLocalValue().toString().isEmpty() || uiInput.getLocalValue().toString().equalsIgnoreCase("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));
        }

        String selectTipoDoc = uiInput.getLocalValue().toString();

        if (selectTipoDoc.equalsIgnoreCase("V") || selectTipoDoc.equalsIgnoreCase("E")) {
            if (!value.toString().matches("^[0-9]{5,8}$")) {
                throw new ValidatorException(new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.IdentifInv", this.getIdCanal())));
            }
        } else {
            if (selectTipoDoc.equalsIgnoreCase("P")) {
                if (!value.toString().matches("^[0-9a-zA-Z]{5,9}$")) {
                    throw new ValidatorException(new FacesMessage(
                            FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.IdentifInv", this.getIdCanal())));
                }
            } else {
                if (selectTipoDoc.equalsIgnoreCase("J") || selectTipoDoc.equalsIgnoreCase("G") || selectTipoDoc.equalsIgnoreCase("C")) {

                    if (!value.toString().matches("^[0-9]{5,9}$")) {
                        throw new ValidatorException(new FacesMessage(
                                FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.rifInvalido", this.getIdCanal())));
                    }

                    UtilDTO util = new UtilDTO();

                    util = clienteDAO.validarRif(selectTipoDoc + value, this.getNombreCanal());

                    if (!util.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                        throw new ValidatorException(new FacesMessage(
                                FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", this.getIdCanal())));
                    } else {
                        if (!util.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                            throw new ValidatorException(new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR, "", util.getRespuestaDTO().getTextoSP()));
                        } else {
                            if (util.getResuladosDTO().get("resultado").toString().trim().equalsIgnoreCase("N")) {
                                throw new ValidatorException(new FacesMessage(
                                        FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.rifInvalido", this.getIdCanal())));
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Metodo para validar identificacion solo numeros P2C
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarIdentificacionP2C(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("campoRequerido", this.getIdCanal())));
        }

        UIInput uiInput = (UIInput) component.findComponent("selectTipoDocP2C");

        if (uiInput.getLocalValue() == null || uiInput.getLocalValue().toString().isEmpty() || uiInput.getLocalValue().toString().equalsIgnoreCase("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("campoRequerido", this.getIdCanal())));
        }

        String selectTipoDoc = uiInput.getLocalValue().toString();

        if (selectTipoDoc.equalsIgnoreCase("V") || selectTipoDoc.equalsIgnoreCase("E")) {
            if (!value.toString().matches("^[0-9]{5,8}$")) {
                throw new ValidatorException(new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("global.texto.IdentifInv", this.getIdCanal())));
            }
        } else {
            if (selectTipoDoc.equalsIgnoreCase("P")) {
                if (!value.toString().matches("^[0-9a-zA-Z]{5,9}$")) {
                    throw new ValidatorException(new FacesMessage(
                            FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("global.texto.IdentifInv", this.getIdCanal())));
                }
            } else {
                if (selectTipoDoc.equalsIgnoreCase("J") || selectTipoDoc.equalsIgnoreCase("G") || selectTipoDoc.equalsIgnoreCase("C")) {

                    if (!value.toString().matches("^[0-9]{5,9}$")) {
                        throw new ValidatorException(new FacesMessage(
                                FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("global.error.rifInvalido", this.getIdCanal())));
                    }

                    UtilDTO util = new UtilDTO();

                    util = clienteDAO.validarRif(selectTipoDoc + value, this.getNombreCanal());

                    if (!util.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                        throw new ValidatorException(new FacesMessage(
                                FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("error.generico.msj", this.getIdCanal())));
                    } else {
                        if (!util.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                            throw new ValidatorException(new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR, "", util.getRespuestaDTO().getTextoSP()));
                        } else {
                            if (util.getResuladosDTO().get("resultado").toString().trim().equalsIgnoreCase("N")) {
                                throw new ValidatorException(new FacesMessage(
                                        FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("global.error.rifInvalido", this.getIdCanal())));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Metodo para validar identificacion solo numeros
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarIdentificacionTDS(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));
        }

        UIInput uiInput = (UIInput) component.findComponent("selectTipoDocTDS");

        if (uiInput.getLocalValue() == null || uiInput.getLocalValue().toString().isEmpty() || uiInput.getLocalValue().toString().equalsIgnoreCase("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));
        }

        String selectTipoDocTDS = uiInput.getLocalValue().toString();

        if (selectTipoDocTDS.equalsIgnoreCase("V") || selectTipoDocTDS.equalsIgnoreCase("E")) {
            if (!value.toString().matches("^[0-9]{5,8}$")) {
                throw new ValidatorException(new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.IdentifInv", this.getIdCanal())));
            }
        } else {
            if (selectTipoDocTDS.equalsIgnoreCase("P")) {
                if (!value.toString().matches("^[0-9a-zA-Z]{5,9}$")) {
                    throw new ValidatorException(new FacesMessage(
                            FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.IdentifInv", this.getIdCanal())));
                }
            } else {
                if (selectTipoDocTDS.equalsIgnoreCase("J") || selectTipoDocTDS.equalsIgnoreCase("G") || selectTipoDocTDS.equalsIgnoreCase("C")) {

                    if (!value.toString().matches("^[0-9]{5,9}$")) {
                        throw new ValidatorException(new FacesMessage(
                                FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.rifInvalido", this.getIdCanal())));
                    }

                    UtilDTO util = new UtilDTO();

                    util = clienteDAO.validarRif(selectTipoDocTDS + value, this.getNombreCanal());

                    if (!util.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                        throw new ValidatorException(new FacesMessage(
                                FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", this.getIdCanal())));
                    } else {
                        if (!util.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                            throw new ValidatorException(new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR, "", util.getRespuestaDTO().getTextoSP()));
                        } else {
                            if (util.getResuladosDTO().get("resultado").toString().trim().equalsIgnoreCase("N")) {
                                throw new ValidatorException(new FacesMessage(
                                        FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.rifInvalido", this.getIdCanal())));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Metodo para validar identificacion solo numeros
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarIdentificacionTOB(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));
        }

        UIInput uiInput = (UIInput) component.findComponent("selectTipoDocTOB");

        if (uiInput.getLocalValue() == null || uiInput.getLocalValue().toString().isEmpty() || uiInput.getLocalValue().toString().equalsIgnoreCase("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));
        }

        String selectTipoDocTOB = uiInput.getLocalValue().toString();

        if (selectTipoDocTOB.equalsIgnoreCase("V") || selectTipoDocTOB.equalsIgnoreCase("E")) {
            if (!value.toString().matches("^[0-9]{5,8}$")) {
                throw new ValidatorException(new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.IdentifInv", this.getIdCanal())));
            }
        } else {
            if (selectTipoDocTOB.equalsIgnoreCase("P")) {
                if (!value.toString().matches("^[0-9a-zA-Z]{5,9}$")) {
                    throw new ValidatorException(new FacesMessage(
                            FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.IdentifInv", this.getIdCanal())));
                }
            } else {
                if (selectTipoDocTOB.equalsIgnoreCase("J") || selectTipoDocTOB.equalsIgnoreCase("G") || selectTipoDocTOB.equalsIgnoreCase("C")) {

                    if (!value.toString().matches("^[0-9]{5,9}$")) {
                        throw new ValidatorException(new FacesMessage(
                                FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.rifInvalido", this.getIdCanal())));
                    }

                    UtilDTO util = new UtilDTO();

                    util = clienteDAO.validarRif(selectTipoDocTOB + value, this.getNombreCanal());

                    if (!util.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                        throw new ValidatorException(new FacesMessage(
                                FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", this.getIdCanal())));
                    } else {
                        if (!util.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                            throw new ValidatorException(new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR, "", util.getRespuestaDTO().getTextoSP()));
                        } else {
                            if (util.getResuladosDTO().get("resultado").toString().trim().equalsIgnoreCase("N")) {
                                throw new ValidatorException(new FacesMessage(
                                        FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.rifInvalido", this.getIdCanal())));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Metodo para validar email
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarEmail(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));//textosController.getNombreTexto("tddInv", this.getIdCanal()), textosController.getNombreTexto("tddInv", this.getIdCanal())));
        } else if (!value.toString().matches("^\\S+@\\S+\\.\\S+$")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.emailInvl", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.emailInvl", this.getIdCanal()))); //textosController.getNombreTexto("tddInv", this.getIdCanal()), textosController.getNombreTexto("tddInv", this.getIdCanal())));
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
    public void validarClave(FacesContext context, UIComponent component, Object value) throws ValidatorException, Exception {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));
        } else if (value.toString().length() < 8 || value.toString().length() > 12) { //La clave debe tener un minimo de 8 y maximo de 12 caracteres
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("clvLenght", this.getIdCanal()), textosController.getNombreTexto("clvLenght", this.getIdCanal())));
        }
    }

    /**
     * Metodo para validar el campo de 20 digitos
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validar20Digitos(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));
        } else if (!value.toString().matches("^\\d{20}$")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.ctaInvl", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.ctaInvl", this.getIdCanal())));
        }
    }

    /**
     * Metodo para validar el Pin
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarPin(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal())));
        } else if (value.toString().length() != 4) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.pinInv", this.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.pinInv", this.getIdCanal())));
        }

    }

    /**
     * Metodo para validar el campo monto del formulario de transferencias.
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarMonto(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.campoRequerido", getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.campoRequerido", getNombreCanal()).getMensajeUsuario()));
        } else if (!eliminarformatoSimpleMonto(value.toString()).matches("^\\d{1,}$|^\\d{1,}[.]{1}\\d{1,2}$")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoInv", getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.mtoInv", getNombreCanal()).getMensajeUsuario()));
        }

    }

    /**
     * Metodo para validar un numero de telefono sin codigo
     *
     * @param context
     * @param component
     * @param value
     * @throws ValidatorException
     */
    public void validarNumeroTelefono(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (!value.toString().matches("^\\d{7}$")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.tlfInv", getNombreCanal()).getMensajeUsuario(), textosFacade.findByCodigo("pnw.global.texto.tlfInv", getNombreCanal()).getMensajeUsuario()));
        }
    }

    /**
     * Metodo para validar un numero de telefono con codigo
     *
     * @param context
     * @param component
     * @param value
     * @throws ValidatorException
     */
    public void validarNumeroTelefonoCompleto(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (!value.toString().matches("^\\d{11}$")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.tlfInv", getNombreCanal()).getMensajeUsuario(), textosFacade.findByCodigo("pnw.global.texto.tlfInv", getNombreCanal()).getMensajeUsuario()));
        }
    }

    /**
     * Metodo para validar un numero de telefono con codigo
     *
     * @param context
     * @param component
     * @param value
     * @throws ValidatorException
     */
    public void validarNumeroCelularCompleto(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().trim().equalsIgnoreCase("") || !value.toString().matches("^\\d{11}$")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.tlfInv", getNombreCanal()).getMensajeUsuario(), textosFacade.findByCodigo("pnw.global.texto.tlfInv", getNombreCanal()).getMensajeUsuario()));
        }
        if (!value.toString().trim().equalsIgnoreCase("") && value.toString().trim().length() == 11) {
            String temp = value.toString().substring(0, 4);
            IbPrefijosOperadorasDTO operadoras = this.getListPrefijosOperadoras();
            boolean operadorInvalido = true;
            if (operadoras.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && operadoras.getIbPrefijosOperadorasDTO() != null) {
                for (IbPrefijosOperadorasDTO operadora : operadoras.getIbPrefijosOperadorasDTO()) {
                    if (temp.equalsIgnoreCase(operadora.getOperadora())) {
                        operadorInvalido = false;
                        break;
                    }
                }
                if (operadorInvalido) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.tlfInv", getNombreCanal()).getMensajeUsuario(), textosFacade.findByCodigo("pnw.global.texto.tlfInv", getNombreCanal()).getMensajeUsuario()));
                }
            }
        }
    }

    /**
     * método para validar los componentes SelectOneMenu donde se constata que
     * hayan seleccionado un valor
     *
     * @param context contexto de la aplicacion
     * @param component componente
     * @param value valor
     * @throws ValidatorException
     */
    public void validarSelectOneMenu(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().isEmpty() || value.equals("") || value.equals("-1") || value.equals(-1)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.requerido", this.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.requerido", this.getNombreCanal()).getMensajeUsuario()));
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
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.textoParametro("pnw.global.texto.requerido", this.getIdCanal()), textosFacade.textoParametro("pnw.global.texto.requerido", this.getIdCanal())));//textosFacade.textoParametro("tddInv", this.getIdCanal()), textosFacade.textoParametro("tddInv", this.getIdCanal())));
        } else if (!((Boolean) value)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.textoParametro("pnw.errores.texto.checkBoxInv", this.getIdCanal()), textosFacade.textoParametro("pnw.errores.texto.checkBoxInv", this.getIdCanal()))); //textosFacade.textoParametro("tddInv", this.getIdCanal()), textosFacade.textoParametro("tddInv", this.getIdCanal())));
        }
    }

    //////////////METODOS DE MANEJO DE SESION////////
    /*
     * Metodo que se encarga de hacer las validaciones de acceso y luego redirecciona a la pagina de menu al pasar las validaciones
     * @return string el ruta de la pagina xhtml (login)
     */
    public void validarAcceso() {
        UtilDTO util;                               //objeto generico para obtener datos no agrupados de canales
        IbUsuariosDTO ibUsuariosDTO = null;                //objeto donde se almacena la data relacionada al usuario Oracle11

        //validacion basica de clave 
        //se desencripta el 3DES para segurar la rafaga por el request 
        this.desencriptarClaveWeb();
        if (this.getClave() == null || this.getClave().isEmpty() || this.getClave().equals("")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formLogin:clave", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", this.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()).substring(5) + "?faces-redirect=true");
            return;
        } else {
            desencriptarClaveWeb();
            if (!this.getClave().matches("(^[a-zA-Z0-9./$#-]{8,12}$)")) { //La clave debe tener un minimo de 8 y maximo de 12 caracteres
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formLogin:clave", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.claveInv", this.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()).substring(5) + "?faces-redirect=true");
                return;
            }
        }

        RespuestaDTO respuesta;                     //objeto para almacenar el valor de respuesta de la insercion
        String codUsuario = "";
        textoAlerta = "";                           //se inicializa el texto de mesanje del popup de login
        usuarioBloqueado = false;
        HttpSession session = getSession();
        this.idSesion = session.getId();

        //y luego se hashea para la BD
        this.setClave(this.encSHA256(this.getClave()));

        //se agrega la busqueda de TDD por doc en IB USUARIOS
        if (this.tipoLogin.equals("2")) {
            if (this.getNroDoc() == null || this.getNroDoc().isEmpty() || this.getNroDoc().equals("")) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formLogin:numDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", this.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()).substring(5) + "?faces-redirect=true");
                return;
            }

            UtilDTO utilTdd = this.ibUsuarioDAO.obtenerTDDPorDoc(tipoDoc, nroDoc, this.getNombreCanal(), idCanal);
            //valido que no haya ocurrido alguna excepcion no
            if (!utilTdd.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && !utilTdd.getRespuestaDTO().getCodigo().equals(CODIGO_SIN_RESULTADOS_JPA)) {
                //agregar mensaje excepcion generica intente mas tarde            
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formLogin:numDoc", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", this.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()).substring(5) + "?faces-redirect=true");
                return;
            } else {
                //se validan las excepciones controladas
                if (utilTdd.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && !utilTdd.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    this.mostrarDialogo(textosController.getNombreTexto(utilTdd.getRespuestaDTO().getDescripcionSP(), this.getIdCanal()));
                    sesionInvalidada = true;
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()).substring(5) + "?faces-redirect=true");
                    return;
                } else {
                    if (utilTdd.getRespuestaDTO().getCodigo().equals(CODIGO_SIN_RESULTADOS_JPA) || utilTdd.getResuladosDTO() == null || utilTdd.getResuladosDTO().get("tdd") == null || utilTdd.getResuladosDTO().get("tdd").toString().isEmpty()) {
                        this.mostrarDialogo(textosController.getNombreTexto("pnw.login.texto.usuarioSinReg", this.getIdCanal()));
                        sesionInvalidada = true;
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()).substring(5) + "?faces-redirect=true");
                        return;
                    } else {
                        tarjeta = utilTdd.getResuladosDTO().get("tdd").toString();
                    }
                }
            }
        }

        //valido el login        
        util = ibUsuarioDAO.validarLogin(tarjeta, clave, idCanal, this.getNombreCanal());

        //valido que no haya ocurrido alguna excepcion no
        if (!util.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //agregar mensaje excepcion generica intente mas tarde            
            this.mostrarDialogo(textosController.getNombreTexto("pnw.global.error.generico", this.getIdCanal()));
            sesionInvalidada = true;
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()).substring(5) + "?faces-redirect=true");
            return;
        } else {
            //se validan las excepciones controladas
            if (util.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && (!util.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO) || util.getResuladosDTO() == null)) {
                this.mostrarDialogo(textosController.getNombreTexto(util.getRespuestaDTO().getDescripcionSP(), this.getIdCanal()));
                sesionInvalidada = true;
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()).substring(5) + "?faces-redirect=true");
                return;
            }

            if (util.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && (!util.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO) || util.getResuladosDTO().get("login").equals("false"))) {
                //agregar mensaje de error interno

                //validacion de intentos fallidos
                ibUsuariosDTO = ibUsuarioDAO.obtenerIbUsuarioPorTDD(tarjeta, idCanal, nombreCanal);
                if (ibUsuariosDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    this.agregarIntentoFallido(ibUsuariosDTO);
                    //se registra la bitacora
                    this.usuario = ibUsuariosDTO;
                    this.registroBitacora(this.getUsuario().getId().toString(), BDSUtil.ID_TRANS_INICIAR_SESION, "", "", "Intento Fallido Inicio de Sesión", "", "", "", "", "", "", util.getRespuestaDTO().getCodigoSP());
                    if (usuarioBloqueado) {
                        this.mostrarDialogo(textosController.getNombreTexto("pnw.login.texto.bloqueo", nombreCanal));
                        textoAlerta = textosController.getNombreTexto("pnw.login.texto.bloqueoPopUp", nombreCanal);
                        sesionInvalidada = true;
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()).substring(5) + "?faces-redirect=true");
                        return;
                    }
                }

                this.mostrarDialogo(textosController.getNombreTexto(util.getRespuestaDTO().getDescripcionSP(), this.getIdCanal()));
                sesionInvalidada = true;
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()).substring(5) + "?faces-redirect=true");
                return;
            } else {
                if (util.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && util.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO) && util.getResuladosDTO().get("login").equals("true")) {
                    ibUsuariosDTO = (IbUsuariosDTO) util.getResuladosDTO().get("usuario");
                }
            }
            //seteo los datos del cliente en el objeto que quiero guardar de Oracle11
            if (ibUsuariosDTO == null || ibUsuariosDTO.getId() == null) {
                //valido si el usuario existe en Oracle 11 y ademas posee una relacion con el canal                

                this.mostrarDialogo(textosController.getNombreTexto("pnw.login.texto.usuarioSinReg", this.getIdCanal()));
                sesionInvalidada = true;
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()).substring(5) + "?faces-redirect=true");
                return;

            } else {
                if (ibUsuariosDTO.getId() != null) {
                    //se consulta la asociacion del usuario al canal
                    usuarioCanal = ibCanalesDAO.consultarUsuarioCanalporIdUsuario(ibUsuariosDTO, idCanal);

                    //guardo los datos del usuario conectado en la sesion para el usuario previamente creado
                    this.usuario = ibUsuariosDTO;
                    /**/
                    getSessionScope().put(SessionAttributesNames.CODIGO_CLIENTE, usuario.getCodUsuario());
                    //limpiamos el OBJ util para alamacenar una nueva busqueda
                    util = new UtilDTO();
                    //valido una posible conexion simultanea
                    util = ibCanalesDAO.validarConexionSimultanea(ibUsuariosDTO.getId().toString(), idCanal, this.getNombreCanal());
                    if ((Boolean) util.getResuladosDTO().get(1)) {
                        //agregar mensaje excepcion CONEXION SIMULTANEA 

                        /////////NOTIFICACION VIA SMS////////
                        String textoSMS = textosController.getNombreTexto("pnw.sms.cuerpo.sesionMultiple", nombreCanal);
                        String motivoSMS = textosController.getNombreTexto("pnw.errores.titulo.multiSesion", nombreCanal);
                        HashMap<String, String> parametros = new HashMap();
                        parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                        this.enviarSMS(usuario.getCelular(), textoSMS, parametros, motivoSMS);

                        this.mostrarDialogo(motivoSMS);
                        this.redirectFacesContext(this.cerrarSesion(motivoSMS).substring(5) + "?faces-redirect=true");
                        return;
                    }
                }
            }

            //guardamos la fecha de ultima conexion previa al login actual
            this.armarFechaUltConexion();

            respuesta = ibCanalesDAO.actualizarUsuarioCanal(usuario, idCanal, this.getNombreCanal(), String.valueOf(CODIGO_USUARIO_CONECTADO), 0, this.idSesion,
                    CODIGO_STS_ACTIVO, CODIGO_STS_ACTIVO, usuarioCanal.getLimiteInternas(), usuarioCanal.getLimiteExternas(), usuarioCanal.getLimiteInternasTerceros(), usuarioCanal.getLimiteExternasTerceros());
            respuesta = ibCanalesDAO.actualizarUsuarioCanal(usuario, CODIGO_CANAL_MOBILE, this.getNombreCanal(), "0", 0, "0",
                    CODIGO_STS_ACTIVO, CODIGO_STS_ACTIVO, usuarioCanal.getLimiteInternas(), usuarioCanal.getLimiteExternas(), usuarioCanal.getLimiteInternasTerceros(), usuarioCanal.getLimiteExternasTerceros());
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                //agregar mensaje excepcion PROBLEMAS EN BD
                this.mostrarDialogo(textosController.getNombreTexto("pnw.global.error.generico", this.getIdCanal()));
                sesionInvalidada = true;
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()).substring(5) + "?faces-redirect=true");
                return;
            }

            IbParametrosDTO param = parametrosFacade.consultaParametro(BDSUtil.CODIGO_TIME_OUT, this.getNombreCanal());
            int minutos = Integer.parseInt(param.getIbParametro().getValor());
            int segundos = minutos * 60; //SE CAMBIO DE MILISEGUNDOS A SEGUNDOS PARA PROBAR EL COMPONENTE POLL ANTES -> 60000
            validacionOTP = 0;

            //tiempo de expiracion del http Session
            session.setMaxInactiveInterval(segundos);
            //tiempo de expiracion por JavaScript
            setTimeout((segundos * 1000) - 10000);

            //Registro en Bitacora
            registroBitacora(this.getUsuario().getId().toString(), BDSUtil.ID_TRANS_INICIAR_SESION, "", "", "Inicio de Sesión", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);

            //se inicializa la cantidad de intentos de fallidos de OTP por sesion
            this.cantFallasActualesOTP = 0;
            //Asignar los limites para las transacciones
            asignarLimitesTransf(usuario);
            //creamos la semilla personalizada para encriptar la data sensible
            this.semilla = ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId() + this.usuario.getCodUsuario() + this.getUsuario().getDocumento();

            //Se valida la ultima contrasena y la fecha de vencimiento
            IbHistoricoClavesDTO clavesDTO = claveDAO.obtenerUltimaClave(this.usuario.getId().toString(), nombreCanal);
            if (!clavesDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                //agregar mensaje excepcion PROBLEMAS EN BD
                this.redirectFacesContext(this.cerrarSesion(textosController.getNombreTexto("pnw.global.error.generico", this.getIdCanal())).substring(5) + "?faces-redirect=true");
                return;
            } else {
                if (!clavesDTO.getClave().equalsIgnoreCase(this.usuario.getClave())) {
                    this.redirectFacesContext(this.cerrarSesion(textosController.getNombreTexto("pnw.inicioSesion.texto.datosInvl", this.getIdCanal())).substring(5) + "?faces-redirect=true");
                    return;
                } else {
                    if (clavesDTO.getFechaVencimientoDate().before(new Date())) {
                        this.idTransaccion = BDSUtil.ID_TRANS_CAMBIO_CLAVE;
                        this.claveExpiro = true;
                        this.nombreModulo = "SEGURIDAD";
                        this.nombreTransaccion = "Clave Expirada";
                        this.template = "/templates/internaCambioClave.xhtml";
                        this.redirectFacesContext((parametrosController.getNombreParametro("pnw.submenu.seguridad.cambioClave", this.getIdCanal()) + "?faces-redirect=true"));
                        return;
                    } else {
                        this.template = "/templates/interna.xhtml";
                    }
                }
            }
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.global.url.inicial", this.getIdCanal()));
            return;
        }

    }

    /**
     * Metodo que permite cerrar la sesion en la aplicacion web y registra en
     * bitacora la transaccion
     *
     * @return String
     */
    public String cerrarSesion(String mensaje) {
        if (this.usuario == null) {
            /*try {
             FacesContext.getCurrentInstance().getExternalContext().redirect(parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()));

             } catch (IOException e) {

             }*/
            sesionInvalidada = true;
            return parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal());
        }
        RespuestaDTO respuestaDTO = ibCanalesDAO.actualizarUsuarioCanal(this.usuario, idCanal, this.getNombreCanal(), String.valueOf(CODIGO_USUARIO_DESCONECTADO),
                0, "0", null, null, null, null, null, null);
        if (!respuestaDTO.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //agregar mensaje excepcion generica intente mas tarde
            this.redireccionInicio((mensajeVacio(mensaje) ? parametrosController.getNombreParametro("pnw.global.error.generico", this.getIdCanal()) : mensaje));
        }//se validan las excepciones controladas
        if (respuestaDTO.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && !respuestaDTO.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //agregar mensaje de error interno
            this.redireccionInicio((mensajeVacio(mensaje) ? parametrosController.getNombreParametro("pnw.global.error.generico", this.getIdCanal()) : mensaje));
        }
        if (respuestaDTO.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {

            //Registro en Bitacora
            if (!mensajeVacio(mensaje)) {
                if (mensaje.equalsIgnoreCase(textosController.getNombreTexto("pnw.errores.titulo.multiSesion", nombreCanal))) {
                    this.sesionSimultanea = true;
                    notificarConexionSimultanea();
                    cerrarSesion = false;
                    //actualizamos el cierre de sesion por conexion simultanea
                    ibCanalesDAO.actualizarUsuarioCanal(this.usuario, idCanal, this.getNombreCanal(), String.valueOf(CODIGO_SESION_SIMULTANEA),
                            0, "0", null, null, null, null, null, null);
                    ibCanalesDAO.actualizarUsuarioCanal(this.usuario, CODIGO_CANAL_MOBILE, this.getNombreCanal(), String.valueOf(CODIGO_SESION_SIMULTANEA),
                            0, "0", null, null, null, null, null, null);
                    registroBitacora(this.getUsuario().getId().toString(), BDSUtil.ID_TRANS_MULT_SESS, "", "", "Sesíon Multiple", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
                } else if (mensaje.equalsIgnoreCase(textosController.getNombreTexto("pnw.errores.titulo.timeOut", nombreCanal))) {
                    registroBitacora(this.getUsuario().getId().toString(), BDSUtil.ID_TRANS_TIME_OUT, "", "", "Sesión Expirada", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
                    cerrarSesion = true;
                } else if (mensaje.equalsIgnoreCase(textosController.getNombreTexto("pnw.errores.titulo.errorGenerico", nombreCanal))) {
                    registroBitacora(this.getUsuario().getId().toString(), BDSUtil.ID_TRANS_ERROR_INESP, "", "", "Error Inesperado", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
                    cerrarSesion = true;
                }
            } else {
                registroBitacora(this.getUsuario().getId().toString(), BDSUtil.ID_TRANS_CERRAR_SESION, "", "", "Cierre de Sesión", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
                cerrarSesion = true;
            }
        }
        sesionInvalidada = true;
        return parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal());
    }

    /**
     * Invalida la session
     */
    /*
    public void invalidarSesion() {
        tarjeta = "";
        clave = "";
        tipoDoc = "V";
        nroDoc = "";
        if (cerrarSesion && !mostrarAlerta) {
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        }
        if (sesionInvalidada) {
            cerrarSesion = true;
        }
    }
     */
    /**
     * Metodo para cerrar el pop up del login
     *
     * @return string
     */
    public String salir() {
        //asigna la bandera de cierre de session para el prerender de login
        if (this.cerrarSesion && !this.sesionSimultanea) {
            cerrarSesion = true;
        }
        mostrarAlerta = false;
        sesionInvalidada = true;        
        return (parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()) + "?faces-redirect=true");
        //return "";
    }

    /**
     * Configura la fecha de la ultima conexion
     */
    public void armarFechaUltConexion() {
        Date fecha = null;
        UtilDTO util = ibCanalesDAO.consultarUltimaConexionCanal(this.usuario.getId().toString(), idCanal, nombreCanal);
        if (util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getResuladosDTO() != null) {
            fecha = (Date) util.getResuladosDTO().get(1);
        }
        if (fecha != null) {
            if (fecha.before(Calendar.getInstance().getTime())) {
                isUltimaConexion = true;
                this.ultimaHoraConexion = textosController.getNombreTexto("pnw.global.texto.ultimaConexion", getIdCanal()) + BDSUtil.fecha(fecha)
                        + " a la(s) " + BDSUtil.formatearFecha(fecha, FORMATO_HORA);
            }
        }
    }

    /**
     * Verifica los valores de los limites de las transacciones y en caso de no
     * tener valores, asigna los almacenados en la tabla parametros.
     *
     * @param usuario IbUsuariosDTO
     */
    public void asignarLimitesTransf(IbUsuariosDTO usuario) {
        if (usuarioCanal == null) {
            usuarioCanal = ibCanalesDAO.consultarUsuarioCanalporIdUsuario(usuario, idCanal);
        }
        if (usuarioCanal.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {

            if (usuarioCanal.getLimiteExternas() == null) {

                usuarioCanal.setLimiteExternas(validarLimites(parametrosFacade.consultaParametro("pnw.limite.trans.propiasotrosbancos", idCanal)));
            }

            if (usuarioCanal.getLimiteExternasTerceros() == null) {

                usuarioCanal.setLimiteExternasTerceros(validarLimites(parametrosFacade.consultaParametro("pnw.limite.trans.otrosbancos", idCanal)));
            }

            if (usuarioCanal.getLimiteInternas() == null) {

                usuarioCanal.setLimiteInternas(validarLimites(parametrosFacade.consultaParametro("pnw.limite.trans.propias", idCanal)));
            }

            if (usuarioCanal.getLimiteInternasTerceros() == null) {

                usuarioCanal.setLimiteInternasTerceros(validarLimites(parametrosFacade.consultaParametro("pnw.limite.trans.tercerosdelsur", idCanal)));
            }
        }
    }

    /**
     * Metodo para notificar al area de seguridad el bloqueo por intentos
     * fallidos
     *
     * @param usuarioDTO el objeto con los datos del usuario
     */
    public void notificarEmail(IbUsuariosDTO usuarioDTO) {
//        String asunto = "Notificacion de Bloqueo por Intentos Fallidos, DELSUR";
//        StringBuilder texto = new StringBuilder("");
//        String fechaActual = BDSUtil.formatearFecha(Calendar.getInstance().getTime(), BDSUtil.FORMATO_FECHA_COMPLETA);
//
//        texto.append(parametrosFacade.textoParametro("pnw.global.texto.nombreBanco", this.getIdCanal()));
//        texto.append(" le informa que, se han detectado varios intentos fallidos de acceso a las ");
//        texto.append(fechaActual);
//        texto.append(", para el usuario con el codigo: ");
//        texto.append(usuarioDTO.getCodUsuario());
//        texto.append(NUEVALINEA);
//        texto.append(NUEVALINEA);
//        texto.append("Gracias por su preferencia,");
//        texto.append(NUEVALINEA);
//        texto.append(NUEVALINEA);
//        texto.append(parametrosFacade.textoParametro("pnw.global.texto.nombreBanco", this.getIdCanal()));
//        texto.append(NUEVALINEA);
//        texto.append("Informacion Confidencial.");
//
//        IbCanalDTO canal = canalController.getIbCanalDTO(idCanal);
//        //notificacion al area de seguridad
//        EMailDTO emailDTO = notificacionesDAO.enviarEmail(parametrosFacade.textoParametro("pnw.global.texto.emailBanco", idCanal), parametrosFacade.textoParametro("pnw.global.texto.emailBanco", idCanal), asunto, texto.toString(), String.valueOf(canal.getId()), canal.getCodigo());

    }

    /**
     * Metodo para notificar al usuario el bloqueo por intentos fallidos
     *
     * @param usuarioDTO el objeto con los datos del usuario
     */
    public void notificarEmailBloqueo(IbUsuariosDTO usuarioDTO) {
        String asunto = "Notificacion de Bloqueo por Intentos Fallidos, DELSUR";
        StringBuilder texto = new StringBuilder("");
        String fechaActual = BDSUtil.formatearFecha(Calendar.getInstance().getTime(), BDSUtil.FORMATO_FECHA_COMPLETA);

        texto.append(parametrosController.getNombreParametro("pnw.global.texto.nombreBanco", this.getIdCanal()));
        texto.append(" le informa que, ha superado el limite de intentos fallidos a las, ");
        texto.append(fechaActual);
        texto.append(", por seguridad su usuario sera bloqueado. ");
        texto.append(NUEVALINEA);
        texto.append(NUEVALINEA);
        texto.append("Gracias por su preferencia,");
        texto.append(NUEVALINEA);
        texto.append(NUEVALINEA);
        texto.append(parametrosController.getNombreParametro("pnw.global.texto.nombreBanco", this.getIdCanal()));
        texto.append(NUEVALINEA);
        texto.append("Informacion Confidencial.");

        IbCanalDTO canal = canalController.getIbCanalDTO(idCanal);
        //si el usuario posee correo asociado se le envia el email notificando el bloqueo por intentos fallidos
        if (usuarioDTO != null && usuarioDTO.getEmail() != null && !usuarioDTO.getEmail().trim().equalsIgnoreCase("")) {
            EMailDTO emailDTO = notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("pnw.global.texto.emailBanco", idCanal), usuarioDTO.getEmail(), asunto, texto.toString(), String.valueOf(canal.getId()), canal.getCodigo());
        }
    }

    /**
     * metodo para notificar al usuario que dispone de un intento antes del
     * bloqueo por intentos fallidos
     *
     * @param usuarioDTO el objeto con los datos del usuario
     */
    public void notificarEmailUltimoIntento(IbUsuariosDTO usuarioDTO) {
        String asunto = "Notificacion de Intentos Fallidos, DELSUR";
        StringBuilder texto = new StringBuilder("");

        texto.append(parametrosController.getNombreParametro("pnw.global.texto.nombreBanco", this.getIdCanal()));
        texto.append(" le informa que, solo dispone de un intento para acceder a la aplicacion antes de ser bloqueado.");
        texto.append(NUEVALINEA);
        texto.append(NUEVALINEA);
        texto.append("Gracias por su preferencia,");
        texto.append(NUEVALINEA);
        texto.append(NUEVALINEA);
        texto.append(parametrosController.getNombreParametro("pnw.global.texto.nombreBanco", this.getIdCanal()));
        texto.append(NUEVALINEA);
        texto.append("Informacion Confidencial.");

        IbCanalDTO canal = canalController.getIbCanalDTO(idCanal);
        //notificacion al area de seguridad
        //EMailDTO emailDTO = notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("pnw.global.texto.emailBanco", idCanal), parametrosController.getNombreParametro("pnw.global.texto.emailBanco", idCanal), asunto, texto.toString(), String.valueOf(canal.getId()), canal.getCodigo());
        //si el usuario posee correo asociado se le envia el email notificando el bloqueo por intentos fallidos
        if (usuarioDTO != null && usuarioDTO.getEmail() != null && !usuarioDTO.getEmail().trim().equalsIgnoreCase("")) {
            EMailDTO emailDTO = notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("pnw.global.texto.emailBanco", idCanal), usuarioDTO.getEmail(), asunto, texto.toString(), String.valueOf(canal.getId()), canal.getCodigo());
        }
    }

    /**
     * Metodo para validar y actualizar los intentos fallidos del usuario
     *
     * @param usuarioDTO IbUsuariosDTO
     */
    public void agregarIntentoFallido(IbUsuariosDTO usuarioDTO) {
        IbUsuariosCanalesDTO usuarioCanalDTO;
        int cantFallasActuales = 0;
        int cantFallasPermitidas = 0;
        RespuestaDTO respuestaDTO = new RespuestaDTO();
        //validamos si el usuario existe en  las tablas de oracle 11
        usuarioCanalDTO = this.ibUsuarioDAO.obtenerIbUsuarioPorCanal(usuarioDTO.getId().toString(), idCanal, nombreCanal);
        //si encontramos un registro procedemos a validar la cantidad de intentos fallido
        if (usuarioCanalDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //tomamos los intentos fallidos, lo incrementamos y lo actualizamos
            cantFallasActuales = Integer.parseInt(String.valueOf(usuarioCanalDTO.getIntentosFallidos()));
            cantFallasActuales++;
            ibCanalesDAO.actualizarUsuarioCanal(usuarioDTO, idCanal, nombreCanal, formatearFecha(usuarioCanalDTO.getFechaHoraUltimaInteraccionDate(), FORMATO_FECHA_COMPLETA), cantFallasActuales,
                    usuarioCanalDTO.getIdSesion(), null, null, null, null, null, null);
            ibCanalesDAO.actualizarUsuarioCanal(usuarioDTO, CODIGO_CANAL_MOBILE, nombreCanal, formatearFecha(usuarioCanalDTO.getFechaHoraUltimaInteraccionDate(), FORMATO_FECHA_COMPLETA), cantFallasActuales,
                    usuarioCanalDTO.getIdSesion(), null, null, null, null, null, null);

            //validamos el parametro definido de intentos
            String fallasParam = parametrosController.getValorParametro("pnw.global.login.intentosFallidos", idCanal);
            if (fallasParam != null && !fallasParam.trim().equalsIgnoreCase("")) {
                cantFallasPermitidas = Integer.parseInt(fallasParam);
            }
            if (cantFallasActuales == (cantFallasPermitidas - 1)) {
                /////////NOTIFICACION VIA SMS////////
                String textoSMS = textosController.getNombreTexto("pnw.global.sms.cuerpo.preBloqueo", nombreCanal);
                String motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.preBloqueoLogin", nombreCanal);
                HashMap<String, String> parametros = new HashMap();
                parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                this.enviarSMS(usuarioDTO.getCelular(), textoSMS, parametros, motivoSMS);
                ////////NOTIFICACIÓN VÍA EMAIL AL CORREO DE SEGURIDAD//////
                notificarEmailUltimoIntento(usuarioDTO);
            }
            //si la cantidad de fallas iguala a las permitidas bloquemos al usuario         
            if (cantFallasActuales == cantFallasPermitidas) {
                //se bloquean ambos canales
                respuestaDTO = ibCanalesDAO.bloquearAccesoCanal(usuarioDTO.getId().toString(), idCanal, nombreCanal);
                respuestaDTO = ibCanalesDAO.bloquearAccesoCanal(usuarioDTO.getId().toString(), CODIGO_CANAL_MOBILE, nombreCanal);
                //respuestaDTO = tddDAO.bloquearTDD(usuarioDTO.getTdd(), idCanal, nombreCanal);
                //AGREGAR BLOQUEO DE USUARIO CUANDO SE ELIMINE EL ACCESO POR ORACLE 9
                usuarioBloqueado = true;
                this.textoAlerta = textosController.getNombreTexto("pnw.login.texto.bloqueoPopUp", nombreCanal);
                /////////NOTIFICACION VIA SMS////////
                String textoSMS = textosController.getNombreTexto("pnw.login.texto.bloqueo", nombreCanal);
                String motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.bloqueoLogin", nombreCanal);
                HashMap<String, String> parametros = new HashMap();
                parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                this.enviarSMS(usuarioDTO.getCelular(), textoSMS, parametros, motivoSMS);

                ////////NOTIFICACIÓN VÍA EMAIL AL CORREO DE SEGURIDAD//////
                notificarEmail(usuarioDTO);
                notificarEmailBloqueo(usuarioDTO);
            }
        }

    }

    /**
     * Método que se encarga de obtener los productos pertenecientes al cliente
     * y validar si el mismo pertenece al cliente
     *
     * @author Wilmer Rondón Briceño
     * @param numeroProducto
     * @return
     */
    public boolean productosCliente(String numeroProducto) {
        PosicionConsolidadaDTO posicionDTO = clienteDAO.consultarPosicionConsolidadaCliente(this.getUsuario().getCodUsuario(), this.getIdCanal(), this.getNombreCanal());

        if (posicionDTO.getRespuestaDTO() != null) {
            if (!posicionDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                return false;
            }
        }

        if (posicionDTO.getCuentasAhorro().stream().noneMatch(p -> p.getNumeroCuenta().equals(numeroProducto))) {
            if (posicionDTO.getCuentasCorriente().stream().noneMatch(p -> p.getNumeroCuenta().equals(numeroProducto))) {
                if (posicionDTO.getCuentasME().stream().noneMatch(p -> p.getNumeroCuenta().equals(numeroProducto))) {
                    if (posicionDTO.getPrestamos().stream().noneMatch(p -> p.getNumeroPrestamo().equals(numeroProducto))) {
                        if (posicionDTO.getDepositosPlazo().stream().noneMatch(p -> p.getNumeroDeposito().equals(numeroProducto))) {
                            if (posicionDTO.getTarjetasCredito().stream().noneMatch(p -> p.getNumeroTarjeta().equals(numeroProducto))) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Método que se encarga de verificar si una afiliación es válida para un
     * cliente
     *
     * @param numeroCuenta
     * @param nroIdentidad
     * @param idUsuario
     * @param idTransaccion
     * @param tipoTransf
     * @param idCanal
     * @param nombreCanal
     * @return boolean
     * @author Wilmer Rondón Briceño
     */
    public boolean validarAfiliacion(String numeroCuenta, String nroIdentidad, String idUsuario, String idTransaccion, String tipoTransf, String idCanal, String nombreCanal) {

        boolean afiliacionValida = false;
        IbAfiliacionesDTO ibAfiliacionesDTO = afiliacionDAO.obtenerListadoAfiliadosPorOperacion(idUsuario, idTransaccion, idCanal, nombreCanal);

        if (ibAfiliacionesDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && ibAfiliacionesDTO.getAfiliaciones() != null && !ibAfiliacionesDTO.getAfiliaciones().isEmpty()) {
            for (IbAfiliacionesDTO afiliacion : ibAfiliacionesDTO.getAfiliaciones()) {
                if (afiliacion.getNumeroCuenta().equalsIgnoreCase(numeroCuenta)) {
                    afiliacionValida = true;
                    break;
                }
            }
        }
        return afiliacionValida;
    }

    /**
     * Metodo que muestra el dialogo en la ventana inicio sesion
     *
     * @param mensaje string mensaje que se desea mostrar al cliente
     */
    public void mostrarDialogo(String mensaje) {
        setMostrarAlerta(true);
        setMensajeAlerta(mensaje);
    }

    /**
     * Metodo que redirecciona a la pagina de inicio de sesion
     *
     * @param mensaje string
     */
    public void redireccionInicio(String mensaje) {

        try {
            //se valida si viene algun mensaje predeterminado para mostar el pop up
            if (mensaje != null && !mensaje.isEmpty() && !mensaje.equals("")) {
                this.mostrarDialogo(mensaje);
            }
            //redireccion por contexto de JSF
            sesionInvalidada = true;
            FacesContext.getCurrentInstance().getExternalContext().redirect((parametrosController.getNombreParametro("pnw.inicio.sesion.url", this.getIdCanal()) + "?faces-redirect=true"));
        } catch (IOException e) {

        }
    }

    /**
     * metodo utilitario para validar el contenido de algun mesaje
     *
     * @param mensaje string
     * @return true si el mensaje esta vacio
     */
    public boolean mensajeVacio(String mensaje) {
        if (mensaje == null) {
            return true;
        } else if (mensaje.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Metodo que setea los datos y parametros para el envio del correo luego
     * del proceso de la transferencia
     */
    public void notificarConexionSimultanea() {

        String asunto = textosController.getNombreTexto("pnw.global.texto.emailAsuntoSeguridad", nombreCanal);
        String email = this.usuario.getEmail();
        if (email != null && !email.trim().equalsIgnoreCase("")) {
            StringBuilder texto = new StringBuilder(textosController.getNombreTexto("pnw.global.texto.emailSaludos", nombreCanal));
            texto.append(this.usuario.getNombre());
            texto.append(NUEVALINEA);
            texto.append(NUEVALINEA);
            texto.append(textosController.getNombreTexto("pnw.global.texto.emailMultiSesion", nombreCanal));
            texto.append(NUEVALINEA);
            texto.append(NUEVALINEA);
            texto.append("Gracias por su preferencia,");
            texto.append(NUEVALINEA);
            texto.append(NUEVALINEA);
            texto.append(parametrosController.getNombreParametro("pnw.global.texto.nombreBanco", this.getIdCanal()));
            texto.append(NUEVALINEA);
            texto.append("Informacion Confidencial.");

            if (usuario.getEmail() != null && !usuario.getEmail().trim().equalsIgnoreCase("")) {
                EMailDTO emailDTO = notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("pnw.global.texto.emailBanco", this.idCanal), email, asunto, texto.toString(), this.idCanal, this.nombreCanal);
            }
        } else {
            //no se pudo notificar via email
        }
    }

    /**
     * metodo que se encarga de realizar la sustitucion de parametros y el
     * armado del SMS para posteriormente enviarlo
     *
     * @param numeroTefl numero de telf al cual se enviara el SMS
     * @param codTextoSMS el String correspondiente
     * @param parametros Mapa de parametro que deberia contener en $PARAM key y
     * "texto" como el valor a reemplazarlo
     * @param motivoSMS texto justificativo del envio del SMS
     */
    public void enviarSMS(String numeroTefl, String codTextoSMS, Map<String, String> parametros, String motivoSMS) {
        String notificar = parametrosController.getValorParametro("pnw.global.notificar.sms", idCanal);
        if (numeroTefl == null || numeroTefl.trim().equalsIgnoreCase("")) {
            motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.sinNumero", nombreCanal);
            numeroTefl = "";
        }
        if (!numeroTefl.trim().equalsIgnoreCase("") && notificar != null && notificar.equalsIgnoreCase("true")) {
            if (codTextoSMS != null && !codTextoSMS.trim().equalsIgnoreCase("") && parametros != null && (parametros.size() > 0)) {
                for (Map.Entry<String, String> entry : parametros.entrySet()) {
                    if (entry.getKey() != null && !entry.getKey().trim().equalsIgnoreCase("") && entry.getValue() != null && !entry.getValue().trim().equalsIgnoreCase("")) {
                    }
                    codTextoSMS = codTextoSMS.replaceAll(entry.getKey(), entry.getValue());
                }
            }
        } else {
            motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.inactivos", nombreCanal);
            numeroTefl = "";
        }
        if (codTextoSMS == null || codTextoSMS.trim().equalsIgnoreCase("")) {
            motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.noDefinido", nombreCanal);
            numeroTefl = "";
        }
        //se procede a enviar el SMS si no viene el numero de telf se envia de igual manera
        //internamente el DAO controla el numero de telf y registra la traza con el motivo
        //por el cual se envio o no el SMS
        notificacionesDAO.enviarSMS(numeroTefl, codTextoSMS, idCanal, nombreCanal, motivoSMS);

    }

    /**
     * Registra la bitacora del usuario en el sistema
     *
     * @param idUsuario identificador del usuario
     * @param idTransaccion identificador de la lista de constantes de
     * transaciones
     * @param ctaOrigen cuenta de origen de la transaccion
     * @param ctaDestino cuenta o tarjeta de destino de la transaccion
     * @param descripcion descripcion de la transaccion
     * @param monto monto de la transaccion
     * @param referencia numero de referencia de la transaccion
     * @param nombreBeneficiario nombre del beneficiario
     * @param tipoDoc tipo de documento de identificacion del beneficiario
     * @param nroDoc nro de documento de identificacion del beneficiario
     * @param idAfiliacion identificador de la afiliacion
     * @param codError codigo de error de la lista de errores
     */
    public void registroBitacora(String idUsuario, String idTransaccion, String ctaOrigen, String ctaDestino, String descripcion, String monto, String referencia, String nombreBeneficiario,
            String tipoDoc, String nroDoc, String idAfiliacion, String codError) {

        IbBitacoraDTO bitacoraDTO = new IbBitacoraDTO();

        bitacoraDTO.setIdUsuario(idUsuario);
        bitacoraDTO.setIp(InicioSesionController.getRemoteAddr());

        bitacoraDTO.setCuentaDestino(ctaDestino);
        bitacoraDTO.setNombreBeneficiario(nombreBeneficiario);
        bitacoraDTO.setRifCedula(nroDoc);
        bitacoraDTO.setTipoRif(tipoDoc);
        bitacoraDTO.setCuentaOrigen(ctaOrigen);
        bitacoraDTO.setIdAfiliacion(idAfiliacion);
        bitacoraDTO.setDescripcion(descripcion);
        bitacoraDTO.setMonto(monto);
        bitacoraDTO.setReferencia(referencia);
        bitacoraDTO.setErrorOperacion(codError);
        //se obtiene la informacion del userAgent del cliente OS,browser...
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String userAgent = request.getHeader("user-agent");

        bitacoraDTO.setUserAgent(userAgent);

        bitacoraDTO.setIdTransaccion(idTransaccion);

        bitacoraDTO.setIdCanal(getIdCanal());
        bitacoraDTO.setNombreCanal(getNombreCanal());

        bitacoraDTO.setFechaHoraJob(BDSUtil.formatearFecha(Calendar.getInstance().getTime(), BDSUtil.FORMATO_FECHA_COMPLETA));
        bitacoraDTO.setFechaHoraTx(BDSUtil.formatearFecha(Calendar.getInstance().getTime(), BDSUtil.FORMATO_FECHA_COMPLETA));

        ibBitacoraDAO.registroDeBitacora(
                bitacoraDTO.getCuentaOrigen(), bitacoraDTO.getCuentaDestino(), bitacoraDTO.getMonto(), bitacoraDTO.getReferencia(), bitacoraDTO.getDescripcion(), bitacoraDTO.getIp(),
                bitacoraDTO.getUserAgent(), bitacoraDTO.getErrorOperacion(), bitacoraDTO.getNombreBeneficiario(), bitacoraDTO.getTipoRif(), bitacoraDTO.getRifCedula(),
                bitacoraDTO.getFechaHoraTx(), bitacoraDTO.getFechaHoraJob(), bitacoraDTO.getIdCanal(), bitacoraDTO.getNombreCanal(), bitacoraDTO.getIdUsuario(), bitacoraDTO.getIdTransaccion(), bitacoraDTO.getIdAfiliacion());
    }

    /**
     * Returns the Internet Protocol (IP) address of the client that sent the
     * request. This will first check the <code>X-Forwarded-For</code> request
     * header and if it's present, then return its first IP address, else just
     * return {@link HttpServletRequest#getRemoteAddr()} unmodified.
     *
     * @return The IP address of the client.
     * @see HttpServletRequest#getRemoteAddr()
     * @since 1.2
     */
    public static String getRemoteAddr() {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }

    /**
     * Valida los limites
     *
     * @param param IbParametrosDTO
     * @return BigDecimal
     */
    private BigDecimal validarLimites(IbParametrosDTO param) {
        if (param.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            if (param.getIbParametro().getValor() != null || param.getIbParametro().getValor().trim().equalsIgnoreCase("")) {
                return new BigDecimal(param.getIbParametro().getValor());
            }
        }
        return new BigDecimal(0);
    }

    ////////////////GETTERS Y SETTERS////////////////
    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public boolean isClaveExpiro() {
        return claveExpiro;
    }

    public void setClaveExpiro(boolean claveExpiro) {
        this.claveExpiro = claveExpiro;
    }

    /**
     * Mensaje por defecto para los campos vacios o en null
     *
     * @return String Mensaje por defecto para los campos vacios o en null
     */
    public String getMensajeNull() {
        return mensajeNull = textosController.getNombreTexto("pnw.global.texto.requerido", this.getIdCanal());//textosController.getNombreTexto("campoRequerido", this.getIdCanal());
    }

    /**
     * Mensaje por defecto para los campos vacios o en null
     *
     * @param mensajeNull Mensaje por defecto para los campos vacios o en null
     */
    public void setMensajeNull(String mensajeNull) {
        this.mensajeNull = mensajeNull;
    }

    /**
     * Obtiene el numero de tarjeta
     *
     * @return string
     */
    public String getTarjeta() {
        return tarjeta;
    }

    /**
     * Asigna el numero de tarjeta
     *
     * @param tarjeta string
     */
    public void setTarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }

    /**
     * Obtiene la clave
     *
     * @return string
     */
    public String getClave() {
        return clave;
    }

    /**
     * Asigna la clave
     *
     * @param clave string
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * retorna la clave encriptada
     *
     * @return
     */
    public String getClaveEnc() {
        return claveEnc;
    }

    /**
     * Asigna la clave encriptada
     *
     * @param claveEnc
     */
    public void setClaveEnc(String claveEnc) {
        this.claveEnc = claveEnc;
    }

    /**
     * Obtiene el id del canal
     *
     * @return string
     */
    public String getIdCanal() {
        return idCanal;
    }

    /**
     * Asigna el id del canal
     *
     * @param idCanal string
     */
    public void setIdCanal(String idCanal) {
        this.idCanal = idCanal;
    }

    public String getTextoStatus() {
        textoStatus = textosController.getNombreTexto("pnw.modal.texto.consultandoInf", this.getIdCanal());
        return textoStatus;
    }

    public void setTextoStatus(String textoStatus) {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.textoStatus = textoStatus;
        }
    }

    /**
     * retorna la semilla para encriptar la data sensible
     *
     * @return semilla para encriptar la data sensible
     */
    public String getSemilla() {
        return semilla;
    }

    /**
     * asigna la semilla para encriptar la data sensible
     *
     * @param semilla semilla para encriptar la data sensible
     */
    public void setSemilla(String semilla) {
        this.semilla = semilla;
    }

    /**
     * retorna los datos del usuario conectado
     *
     * @return IbUsuariosDTO
     */
    public IbUsuariosDTO getUsuario() {
        return usuario;
    }

    /**
     * asigna los datos del usuario conectado
     *
     * @param usuario IbUsuariosDTO
     */
    public void setUsuario(IbUsuariosDTO usuario) {
        this.usuario = usuario;
    }

    /**
     * retorna el identificador unico de la sesion
     *
     * @return el identificador unico de la sesion
     */
    public String getIdSesion() {
        return idSesion;
    }

    /**
     * asigna el identificador unico de la sesion
     *
     * @param idSesion identificador unico de la sesion
     */
    public void setIdSesion(String idSesion) {
        this.idSesion = idSesion;
    }

    /**
     * Obtiene el identificador unico del canal (codigo de canal)
     *
     * @return string
     */
    public String getNombreCanal() {
        if (nombreCanal == null || nombreCanal.trim().equalsIgnoreCase("")) {
            return nombreCanal = canalController.getIbCanalDTO(idCanal).getCodigo();
        } else {
            return nombreCanal;
        }
    }

    /**
     * Asigna el nombre del canal
     *
     * @param nombreCanal string
     */
    public void setNombreCanal(String nombreCanal) {
        this.nombreCanal = nombreCanal;
    }

    /**
     * Metodo que devuelve un bolean del la variable mostrar detalle
     *
     * @return mostrarAlerta boolean
     */
    public boolean isMostrarAlerta() {
        return mostrarAlerta;
    }

    /**
     * Metodo que setea el un valor boleano a la variable mostrarAlerta
     *
     * @param mostrarAlerta boolean
     */
    public void setMostrarAlerta(boolean mostrarAlerta) {
        this.mostrarAlerta = mostrarAlerta;
    }

    /**
     * obtiene el mensaje de alerta
     *
     * @return String
     */
    public String getMensajeAlerta() {
        return mensajeAlerta;
    }

    /**
     * Asigna el mensaje de alerta
     *
     * @param mensajeAlerta
     */
    public void setMensajeAlerta(String mensajeAlerta) {
        this.mensajeAlerta = mensajeAlerta;
    }

    /**
     * Bandera para identificar el cerrar sesion por time out
     *
     * @return boolean true si es por time out
     */
    public boolean isCerrarSesion() {
        return cerrarSesion;
    }

    /**
     * bandera para identificar el cerrar sesion por time out
     *
     * @param cerrarSesion boolean
     */
    public void setCerrarSesion(boolean cerrarSesion) {
        this.cerrarSesion = cerrarSesion;
    }

    /**
     *
     * @return indicador de validacion de codigo OTP por sesion "0" no ha
     * realizado otp, "1" otp validado, "2" libre para realizar cualquier
     * operacion
     */
    public int getValidacionOTP() {
        return validacionOTP;
    }

    /**
     *
     * @param validacionOTPin indicador de validacion de codigo OTP por sesion
     * "0" no ha realizado otp, "1" otp validado, "2" libre para realizar
     * cualquier operacion
     */
    public void setValidacionOTP(int validacionOTPin) {
        if (validacionOTPin == 2 && this.validacionOTP == 1) {
            this.validacionOTP = validacionOTPin;
        } else {
            if (validacionOTPin != 2) {
                this.validacionOTP = validacionOTPin;
            }
        }
    }

    /**
     *
     * @return url para redireccionar de otp al siguiente paso del modulo que
     * corresponta
     */
    public String getUrlRedireccionOtp() {
        return urlRedireccionOtp;
    }

    /**
     *
     * @param urlRedireccionOtp url para redireccionar de otp al siguiente paso
     * del modulo que corresponta
     */
    public void setUrlRedireccionOtp(String urlRedireccionOtp) {
        this.urlRedireccionOtp = urlRedireccionOtp;
    }

    /**
     * retorna el indicador para saber si el usuario fue bloqueado al superar la
     * cant de intentos usuarioBloqueado
     *
     * @return el indicador para sabersi el usuario fue bloqueado al superar la
     * cant de intentos
     */
    public boolean isUsuarioBloqueado() {
        return usuarioBloqueado;
    }

    /**
     * asigna el indicador para saber si el usuario fue bloqueado al superar la
     * cant de intentos usuarioBloqueado
     *
     * @param usuarioBloqueado el indicador para saber si el usuario fue
     * bloqueado al superar la cant de intentos
     */
    public void setUsuarioBloqueado(boolean usuarioBloqueado) {
        this.usuarioBloqueado = usuarioBloqueado;
    }

    /**
     * retorna el texto a mostrar el en popup de login
     *
     * @return String
     */
    public String getTextoAlerta() {
        if (this.textoAlerta == null || this.textoAlerta.trim().equalsIgnoreCase("")) {
            this.textoAlerta = textosController.getNombreTexto("pnw.login.texto.loginPopUp", nombreCanal);
        }
        return textoAlerta;
    }

    /**
     * asigna el texto a mostrar el en popup de login
     *
     * @param textoAlerta String
     */
    public void setTextoAlerta(String textoAlerta) {
        this.textoAlerta = textoAlerta;
    }

    /*
     * Metodo que retornar el objeto Sesion
     * @return el objeto httpSessopm
     */
    public HttpSession getSession() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        return request.getSession(false);
    }

    /**
     * retorna el tiempo de inactividad
     *
     * @return string
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Asigna el tiempo de inactividad
     *
     * @param timeout string
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * contador de intentos fallidos de OTP por sesion
     *
     * @return el contador de intentos fallidos de OTP por sesion
     */
    public int getCantFallasActualesOTP() {
        return cantFallasActualesOTP;
    }

    /**
     * asigna el contador de intentos fallidos de OTP por sesion
     *
     * @param cantFallasActualesOTP contador de intentos fallidos de OTP por
     * sesion
     */
    public void setCantFallasActualesOTP(int cantFallasActualesOTP) {
        this.cantFallasActualesOTP = cantFallasActualesOTP;
    }

    /**
     * Retorna el indicador de cierre de sesion por multiples conexiones
     *
     * @return indicador de cierre de sesion por multiples conexiones
     */
    public boolean isSesionSimultanea() {
        return sesionSimultanea;
    }

    /**
     * Asigna el indicador de cierre de sesion por multiples conexiones
     *
     * @param sesionSimultanea indicador de cierre de sesion por multiples
     * conexiones
     */
    public void setSesionSimultanea(boolean sesionSimultanea) {
        this.sesionSimultanea = sesionSimultanea;
    }

    /**
     * Indica si es la ultima conexion
     *
     * @return boolean
     */
    public boolean isIsUltimaConexion() {
        return isUltimaConexion;
    }

    /**
     * Asina si es la ultima conexion
     *
     * @param isUltimaConexion
     */
    public void setIsUltimaConexion(boolean isUltimaConexion) {
        this.isUltimaConexion = isUltimaConexion;
    }

    /**
     * obtiene la ultima hora de conexion
     *
     * @return string
     */
    public String getUltimaHoraConexion() {
        return this.ultimaHoraConexion;
    }

    /**
     * Asigna la ultima hora de conexion
     *
     * @param ultimaHoraConexion string
     */
    public void setUltimaHoraConexion(String ultimaHoraConexion) {
        this.ultimaHoraConexion = ultimaHoraConexion;
    }

    /**
     *
     * @return string para almacenar el nombre del modulo para arbol de menu en
     * la esquina superior izquierda
     */
    public String getNombreModulo() {
        return nombreModulo;
    }

    /**
     *
     * @param nombreModulo string para almacenar el nombre del modulo para arbol
     * de menu en la esquina superior izquierda
     */
    public void setNombreModulo(String nombreModulo) {
        this.nombreModulo = nombreModulo;
    }

    /**
     *
     * @return string para almacenar el nombre de la transaccion para arbol de
     * menu en la esquina superior izquierda
     */
    public String getNombreTransaccion() {
        return nombreTransaccion;
    }

    /**
     *
     * @param nombreTransaccion string para almacenar el nombre de la
     * transaccion para arbol de menu en la esquina superior izquierda
     */
    public void setNombreTransaccion(String nombreTransaccion) {
        this.nombreTransaccion = nombreTransaccion;
    }

    /**
     * Variable para identificar si el usuario acaba de realizar una
     * transaccion, para limpiar la sesion
     *
     * @return true si acaba de realizar una transaccion (pago o transferencia)
     */
    public boolean isEnTransaccion() {
        return enTransaccion;
    }

    /**
     * Inserta tru o falso segun sea el caso
     *
     * @param enTransaccion true si acaba de realizar una transaccion (pago o
     * transferencia)
     */
    public void setEnTransaccion(boolean enTransaccion) {
        this.enTransaccion = enTransaccion;
    }

    /**
     * Retorna el texto para el popup de confirmacion de salida
     *
     * @return texto para el popup de confirmacion de salida
     */
    public String getTextoConfirmarSalida() {
        return textosController.getNombreTexto("pnw.modal.texto.confSalida", nombreCanal);
    }

    /**
     * Asigna texto para el popup de confirmacion de salida
     *
     * @param textoConfirmarSalida texto para el popup de confirmacion de salida
     */
    public void setTextoConfirmarSalida(String textoConfirmarSalida) {
        this.textoConfirmarSalida = textoConfirmarSalida;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public List<tipoIdentificacion> getTipoIdent() {
        return tipoIdent;
    }

    public void setTipoIdent(List<tipoIdentificacion> tipoIdent) {
        this.tipoIdent = tipoIdent;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = this.obtenerMes(mes);
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public List<meses> getListMeses() {
        return listMeses;
    }

    public void setListMeses(List<meses> listMeses) {
        this.listMeses = listMeses;
    }

    /**
     * Obtiene lista de N cantidad de años futuros calculando desde el año
     * actual. N es un parámetro que se obtiene de la tabla de parámetros
     *
     * @author wilmer.rondon
     * @return List<String>
     */
    public List<String> getListAnos() {
        int cantAnos = 0;
        Calendar calendar = Calendar.getInstance();
        int anoActual = calendar.get(Calendar.YEAR);
        int ano;
        listAnos = new ArrayList<>();

        while (cantAnos <= Integer.valueOf(parametrosController.getNombreParametro("pnw.global.validacion.cantAnosFuturo", this.getIdCanal()))) {
            ano = anoActual + cantAnos;
            listAnos.add(Integer.toString(ano));
            cantAnos++;
        }
        return listAnos;
    }

    public void setListAnos(List<String> listAnos) {
        this.listAnos = listAnos;
    }

    /**
     * enum que contiene lista de tipo de identificación. Ejem: V, E, J...
     *
     * @author wilmer.rondon
     */
    public static enum tipoIdentificacion {

        V,
        E,
        P,
        J,
        G,
        C
    }

    /**
     * enum que contiene los nombres de los meses de un año.
     *
     * @author wilmer.rondon
     */
    public static enum meses {

        Enero,
        Febrero,
        Marzo,
        Abril,
        Mayo,
        Junio,
        Julio,
        Agosto,
        Septiembre,
        Octubre,
        Noviembre,
        Diciembre

    }

    /**
     * Método que obtiene mes en número dado el String del nombre del mes. Ejem:
     * in=abril, out=04
     *
     * @author wilmer.rondon
     * @param mes
     * @return String
     */
    public String obtenerMes(String mes) {
        Map listMeses = new HashMap();

        listMeses.put("Enero", "01");
        listMeses.put("Febrero", "02");
        listMeses.put("Marzo", "03");
        listMeses.put("Abril", "04");
        listMeses.put("Mayo", "05");
        listMeses.put("Junio", "06");
        listMeses.put("Julio", "07");
        listMeses.put("Agosto", "08");
        listMeses.put("Septiembre", "09");
        listMeses.put("Octubre", "10");
        listMeses.put("Noviembre", "11");
        listMeses.put("Diciembre", "12");

        return (String) listMeses.get(mes);

    }

    public Object obtenerMesCalendar(String mes) {
        Map listMeses = new HashMap();

        listMeses.put("0", "Enero");
        listMeses.put("1", "Febrero");
        listMeses.put("2", "Marzo");
        listMeses.put("3", "Abril");
        listMeses.put("4", "Mayo");
        listMeses.put("5", "Junio");
        listMeses.put("6", "Julio");
        listMeses.put("7", "Agosto");
        listMeses.put("8", "Septiembre");
        listMeses.put("9", "Octubre");
        listMeses.put("10", "Noviembre");
        listMeses.put("11", "Diciembre");

        return listMeses.get(mes);
    }

    /**
     * Obtiene lista de los últimos N meses respecto al mes actual (inclusive),
     * ordenado en forma descendente, donde N es el número de meses anteriores
     * que se obtendrán. Ejemplo: Julio, Junio, Mayo...
     *
     * @return List<MesAnoDTO>
     */
    public List<MesAnoDTO> getListMesesAnos() {
        listMesesAnos = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = Integer.parseInt(parametrosController.getNombreParametro("pnw.global.filtrar.meses", this.getIdCanal())); i < 0; i++) {
            MesAnoDTO mesAnoDto = new MesAnoDTO();
            mesAnoDto.setAno(String.valueOf(calendar.get(Calendar.YEAR)));
            mesAnoDto.setMes(Integer.toString(calendar.get(Calendar.MONTH)));

            listMesesAnos.add(mesAnoDto);
            calendar.add(Calendar.MONTH, -1);
        }

        return listMesesAnos;
    }

    /**
     * Obtiene lista de los últimos N meses respecto al mes actual (excluyendo
     * al actual), ordenado en forma descendente, donde N es el número de meses
     * anteriores que se obtendrán. Ejemplo: mes act Agosto -> Julio, Junio,
     * Mayo...
     *
     * @return List<MesAnoDTO>
     */
    public List<MesAnoDTO> getListMesesAnosEx() {
        listMesesAnos = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = Integer.parseInt(parametrosController.getNombreParametro("pnw.global.validacion.cantMesesEdoCta", this.getIdCanal())); i < 0; i++) {
            MesAnoDTO mesAnoDto = new MesAnoDTO();

            if (calendar.get(Calendar.MONTH) > 0) {
                mesAnoDto.setMes(Integer.toString(calendar.get(Calendar.MONTH) - 1));
                mesAnoDto.setAno(String.valueOf(calendar.get(Calendar.YEAR)));
            } else {
                mesAnoDto.setMes("11");
                mesAnoDto.setAno(String.valueOf(calendar.get(Calendar.YEAR) - 1));
            }
            listMesesAnos.add(mesAnoDto);
            calendar.add(Calendar.MONTH, -1);
        }

        return listMesesAnos;
    }

    public void setListMesesAnos(List<MesAnoDTO> listMesesAnos) {
        this.listMesesAnos = listMesesAnos;
    }

    public MesAnoDTO getMesAnoSelected() {
        return mesAnoSelected;
    }

    public void setMesAnoSelected(MesAnoDTO mesAnoSelected) {
        this.mesAnoSelected = mesAnoSelected;
    }

    public IbPeriodoClaveDTO getListPeriodoClaveDTO() {
        return periodoClaveDAO.listaPeriodoClave(getNombreCanal());
    }

    public void setListPeriodoClaveDTO(IbPeriodoClaveDTO listPeriodoClaveDTO) {
        this.listPeriodoClaveDTO = listPeriodoClaveDTO;
    }

    public IbPrefijosOperadorasDTO getListPrefijosOperadoras() {
        return prefijosOperadorasDAO.listaPrefijosOperadoras(this.getNombreCanal());
    }

    public void setListPrefijosOperadoras(IbPrefijosOperadorasDTO listPrefijosOperadoras) {
        this.listPrefijosOperadoras = listPrefijosOperadoras;
    }

    /**
     * Asina el Usuario Canal
     *
     * @return IbUsuariosCanalesDTO
     */
    public IbUsuariosCanalesDTO getUsuarioCanal() {
        return usuarioCanal;
    }

    /**
     * Asigna los datos del usuario asociado al canal
     *
     * @param usuarioCanal IbUsuariosCanalesDTO
     */
    public void setUsuarioCanal(IbUsuariosCanalesDTO usuarioCanal) {
        this.usuarioCanal = usuarioCanal;
    }

    /**
     * retorna el identificador de la opcion del menu
     *
     * @return el identificador de la opcion del menu
     */
    public String getIdModuloAdm() {
        return idModuloAdm;
    }

    /**
     * asigna el identificador de la opcion del menu
     *
     * @param idModuloAdm el identificador de la opcion del menu
     */
    public void setIdModuloAdm(String idModuloAdm) {
        this.idModuloAdm = idModuloAdm;
    }

    /**
     * retorna el mapa para almacenar valores varios ocultos de los fomularios
     * para evitar manipulacion de ajax request
     *
     * @return mapa para almacenar valores varios ocultos de los fomularios para
     * evitar manipulacion de ajax request
     */
    public Map getParametrosUtil() {
        return parametrosUtil;
    }

    /**
     * asigna el mapa para almacenar valores varios ocultos de los fomularios
     * para evitar manipulacion de ajax request
     *
     * @param parametrosUtil mapa para almacenar valores varios ocultos de los
     * fomularios para evitar manipulacion de ajax request
     */
    public void setParametrosUtil(Map parametrosUtil) {
        this.parametrosUtil = parametrosUtil;
    }

    /**
     *
     * @param nombre
     * @param valor
     */
    public void almacenarParametro(String nombre, String valor) {
        this.parametrosUtil.put(nombre, valor);
    }

    public int getValidadorFlujo() {
        return validadorFlujo;
    }

    public void setValidadorFlujo(int validadorFlujo) {
        this.validadorFlujo = validadorFlujo;
    }

    public boolean isReiniciarForm() {
        return reiniciarForm;
    }

    public void setReiniciarForm(boolean reiniciarForm) {
        this.reiniciarForm = reiniciarForm;
    }

    public boolean isMostrarTimeOutDialog() {
        return mostrarTimeOutDialog;
    }

    public void setMostrarTimeOutDialog(boolean mostrarTimeOutDialog) {
        this.mostrarTimeOutDialog = mostrarTimeOutDialog;
    }

    public String getTipoLogin() {
        return tipoLogin;
    }

    public void setTipoLogin(String tipoLogin) {
        this.tipoLogin = tipoLogin;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getNroDoc() {
        return nroDoc;
    }

    public void setNroDoc(String nroDoc) {
        this.nroDoc = nroDoc;
    }

    /**
     * Método que valida si el número de la cuenta es igual al numero del banco
     *
     * @param codigoBanco
     * @param codigo
     * @return
     */
    public boolean validarCuentaCodigoBanco(String codigoBanco, String codigo) {
        boolean resultado = false;

        if (codigoBanco.equalsIgnoreCase(codigo.substring(0, 4))) {
            resultado = true;
        }

        return resultado;
    }

    public void cerrarDialog() {
        mostrarAlerta = false;
    }

    /**
     * metodo para encriptar texto en la capa web en 3des con una semilla de BD
     * para el ocultamiento de la data en la rafaga del request
     */
    public void encriptarClaveWeb() {
        //SE DESENCRIPTA LA CLAVE LA CUAL FUE CODIFICADA POR CADA CLIC
        //EN EL inicioSession.xhml configurado en el jqueryfull
        setClave(new String(Base64.getDecoder().decode(this.clave)));
        if (this.clave != null) {
            DESedeEncryption desEnc = new DESedeEncryption();
            desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", this.getIdCanal())));
            this.setClaveEnc(desEnc.encriptar(this.clave));
            this.setClaveEnc(this.clave);
            this.clave = "********";
        }
    }

    /**
     * metodo para desencriptar el texto en la capa web en 3des con una semilla
     * de BD
     */
    public void desencriptarClaveWeb() {
        DESedeEncryption desEnc = new DESedeEncryption();
        desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", this.getIdCanal())));
        this.clave = desEnc.desencriptar(this.getClaveEnc());
         this.clave = this.getClaveEnc();
    }

    public boolean isValidarUsuarioPiloto() {
        return validarUsuarioPiloto;
    }

    public void setValidarUsuarioPiloto(boolean validarUsuarioPiloto) {
        this.validarUsuarioPiloto = validarUsuarioPiloto;
    }

    public boolean isModuloAfiliacionP2P() {
        return moduloAfiliacionP2P;
    }

    public void setModuloAfiliacionP2P(boolean moduloAfiliacionP2P) {
        this.moduloAfiliacionP2P = moduloAfiliacionP2P;
    }

    public String getCodigoTransaccion() {
        return codigoTransaccion;
    }

    public void setCodigoTransaccion(String codigoTransaccion) {
        this.codigoTransaccion = codigoTransaccion;
    }

    public boolean validarTransaccionClaveEspecial() {
        boolean resultados = false;
        if (getCodigoTransaccion().equals(TRANSF_TERCEROS_DEL_SUR)
                || getCodigoTransaccion().equals(TRANSF_TERCEROS_OTROS_BCOS)
                || getCodigoTransaccion().equals(PAGO_TDC_DEL_SUR)
                || getCodigoTransaccion().equals(PAGO_TDC_OTROS_BCOS)) {
            resultados = true;
        }
        return resultados;
    }

    public String getHoraInicio() {
        horaInicio = parametrosController.getValorParametro("pnw.horaInicioInterrupcion.transacciones", this.getNombreCanal());
        return horaInicio;
    }

    public String getHoraFin() {
        horaFin = parametrosController.getValorParametro("pnw.horaFinInterrupcion.transacciones", this.getNombreCanal());
        return horaFin;
    }
    
    
}
