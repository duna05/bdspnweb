/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.CuentaDAO;
import com.bds.wpn.dao.IBAcumuladoTransaccionDAO;
import com.bds.wpn.dao.IBAgendaTransaccionesDAO;
import com.bds.wpn.dao.IbAfiliacionDAO;
import com.bds.wpn.dao.NotificacionesDAO;
import com.bds.wpn.dao.TranferenciasYPagosDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.IBAgendaTransaccionDTO;
import com.bds.wpn.dto.IbAcumuladoTransaccionDTO;
import com.bds.wpn.dto.IbAfiliacionesDTO;
import com.bds.wpn.dto.IbCanalDTO;
import com.bds.wpn.dto.IbParametrosDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.TransaccionDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbCanalFacade;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
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
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author juan.faneite
 */
@Named("wpnTransferenciaController")
@SessionScoped
public class TransferenciaController extends BDSUtil implements Serializable {

    @EJB
    IbCanalFacade canalFacade;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbAfiliacionDAO afiliacionDAO;

    @EJB
    IBAcumuladoTransaccionDAO acumuladoDAO;

    @EJB
    TranferenciasYPagosDAO tranferenciasYPagosDAO;

    @EJB
    IBAgendaTransaccionesDAO agendaTransaccionesDAO;

    @EJB
    CuentaDAO cuentaDAO;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    NotificacionesDAO notificacionesDAO;

    @Inject
    InicioSesionController sesionController;

    @Inject
    private TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    @Inject
    CanalController canalController;

    private String paramCodOTP;

    private String monto = "";                                                  //Monto extraido del formulario de transferencias
    private String motivo = "";                                                 //Motivo extraido del formulario de transferencias
    private String cuentaSeleccionada = "";                                     //Cuenta del Titular a Debitar Seleccionada extraido del formulario de transferencias
    private String cuentaDestinoSeleccionada = "";                              //Cuenta Destino a Acreditar extraido del formulario de transferencias
    private String numConfirmacion = "";                                        //Numero de confirmacion de la operacion realizada
    private String mensajeNull = "";                                            //Mensaje por defecto para los campos vacios o en null
    private String tipoTransf = "";                                             //Atributo para identificar el tipo de transferencia P - propias, TOB - terceros otros bancos, TBDS - terceros banco del sur
    private Collection<IbAfiliacionesDTO> afiliaciones;                         //objeto que contiene las afiliaciones para el cliente
    private Collection<CuentaDTO> cuentasCliente;                               //objeto que contiene las cuentas propias
    private CuentaDTO datosCuentaSeleccionadaLista = null;                      //objeto para almacenar los datos de la cuenta seleccionada de la lista
    private CuentaDTO datosCuentaSeleccionadaActual = null;                     //objeto para almacenar la coonsulta de los datos de la cuenta seleccionada
    private IbAfiliacionesDTO datosAfiliacionSeleccionada = null;               //objeto para almacenar los datos de la afiliacion seleccionada de la lista
    private String codigoOTP = "";                                              //codigo de validacion OTP    
    private String tituloTransferencias = "";
    private Date fechaTransaccion;
    private String emailBeneficiario = "";
    private String errorConfirmacion = "";
    private String formTransferencias = "";                                     //indicador del formulario actual segun la transaccion ejecutada
    private boolean tieneAfiliaciones = false;                                  //indicador de posesion de afiliaciones para un tipo determinado
    private boolean otpGenerado = false;                                        //indicador para validar que si el OTP fue solicitado    
    private Date fechaHasta;                                                    //fecha tope de agenda de transferencia
    private boolean mostrarFechaHasta;                                          //bandera para identificar si se muestra el calendario de la fecha hasta de agendar transf
    private Date calendarFechaMin;                                              //fecha minima para validacion del calendar
    private Date calendarFechaMax;                                              //fecha maxima para validacion del calendar
    private String tipoEjecTransf = "U";                                        //tipo de ejecucion de transferencia U=unico, D=diario, A=agendado
    private int frecSeleccionada;                                               //1=semanal, 2= quincenal, 3=mensual
    private int fechaFrecSeleccionada;                                          //fecha de la frecuencia (este valor depende de la frecSeleccionada)
    private String fechaSeleccionadaString = "";                                //descripcion de la fecha seleccionada
    private String frecSeleccionadaString = "";                                 //descripcion de la frecuencia seleccionada
    private String[] frecuencias = new String[]{"Seleccione"};                  //tipos posibles para la frecuencia de las transf
    private String[] fechaFrecuencia = new String[]{"Seleccione"};              //valores posibles para los tipos de frecuencia de las transf
    private boolean mostrarDiasQuincena;                                        //bandera para identificar si se muestra el texto de los dias de quincena a transferir
    private int segundoDiaQuincena;                                             //indicador del segundo dia de la quincena a programar
    private boolean mantenerDatosForm = false;  
    private boolean horarioHabilitado = true;
    private boolean validaCierreOperaciones = true;
    private boolean mostrarMensajeCierre = false;
    
    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(OTPController.class.getName());

    /**
     * metodo para validar la cuenta propia seleccionada
     *
     * @param context contexto de la aplicacion
     * @param component componente
     * @param value valor
     * @throws ValidatorException
     */
    public void validarCtaPropia(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().isEmpty() || value.equals("") || value.equals("-1")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.requerido", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.requerido", sesionController.getNombreCanal()).getMensajeUsuario()));
        }
    }

    /**
     * metodo para validar la cuenta propia seleccionada
     *
     * @param context contexto de la aplicacion
     * @param component componente
     * @param value valor
     * @throws ValidatorException
     */
    public void validarCtaDestino(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().isEmpty() || value.equals("") || value.equals("-1")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.requerido", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.requerido", sesionController.getNombreCanal()).getMensajeUsuario()));
        }
    }

    /**
     * Metodo de apoyo que inserta el nombre del formulario dependiendo del tipo
     * de transferencias
     */
    public void setearForm() {
        switch (this.tipoTransf) {
            case T_TRANSF_PROPIAS: {
                formTransferencias = "formPDS";
                break;
            }
            case T_TRANSF_OTROSBANCOS: {
                formTransferencias = "formTOB";
                break;
            }
            case T_TRANSF_3ROSDELSUR: {
                formTransferencias = "formTDS";
                break;
            }
            case T_TRANSF_PROPOTROSBANCOS: {
                formTransferencias = "formPOB";
                break;
            }
        }
    }

    /**
     * Metodo para consultar las cuentas propias
     */
    public void consultarCuentasPropias() {
        this.tieneAfiliaciones = false;
        ClienteDTO datosCliente = clienteDAO.listadoCuentasClientes(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (!datosCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //manejar mensaje de error 
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtCtaDebitar", fMsg);
        } else {
            //se consultan las afiliaciones para dicha transaccion
            if (datosCliente.getCuentasAhorroCorrienteDTO().size() > 0) {
                if (tipoTransf.equalsIgnoreCase(T_TRANSF_PROPIAS)) {
                    this.tieneAfiliaciones = true;
                }
                if (datosCliente.getCuentasAhorroDTO() != null && !datosCliente.getCuentasAhorroDTO().isEmpty()) {
                    for (int i = 0; i < datosCliente.getCuentasAhorroDTO().size(); i++) {
                        datosCliente.getCuentasAhorroDTO().get(i).setSemilla(sesionController.getSemilla());
                    }
                }
                if (datosCliente.getCuentasCorrienteDTO() != null && !datosCliente.getCuentasCorrienteDTO().isEmpty()) {
                    for (int i = 0; i < datosCliente.getCuentasCorrienteDTO().size(); i++) {
                        datosCliente.getCuentasCorrienteDTO().get(i).setSemilla(sesionController.getSemilla());
                    }
                }
                this.cuentasCliente = datosCliente.getCuentasAhorroCorrienteDTO();
            }
        }
    }

    /**
     * metodo para cargar las listas iniciales para Propias
     */
    public void consultaInicialPropias() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.limpiarAfiliaciones();
            this.tieneAfiliaciones = false;
            tipoTransf = T_TRANSF_PROPIAS;
            setearForm();
            consultarCuentasPropias();
            if (this.cuentasCliente == null || this.cuentasCliente.isEmpty() || this.cuentasCliente.size() > 0) {
                this.tieneAfiliaciones = true;
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage(formTransferencias + ":divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.sinAfiliaciones", sesionController.getIdCanal())));
            }
        }
    }

    /**
     * metodo para cargar las listas iniciales para TOB
     */
    public void consultaInicialTOB() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.limpiarAfiliaciones();
            this.tieneAfiliaciones = false;
            tipoTransf = T_TRANSF_OTROSBANCOS;
            setearForm();
            consultarCuentasPropias();
            consultarAfiliacionesTercOtrosBcos();
            if (afiliaciones.isEmpty()) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage(formTransferencias + ":divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.sinAfiliaciones", sesionController.getIdCanal())));
            }
            if (this.afiliaciones.size() > 0) {
                this.tieneAfiliaciones = true;
            }
        }
    }

    /**
     * metodo para cargar las listas iniciales para TDS
     */
    public void consultaInicialTDS() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.limpiarAfiliaciones();
            this.tieneAfiliaciones = false;
            tipoTransf = T_TRANSF_3ROSDELSUR;
            setearForm();
            consultarCuentasPropias();
            consultarAfiliacionesTercDelSur();
            if (afiliaciones.isEmpty()) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage(formTransferencias + ":divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.sinAfiliaciones", sesionController.getIdCanal())));
            }
            if (this.afiliaciones.size() > 0) {
                this.tieneAfiliaciones = true;
            }
        }
    }

    /**
     * metodo para cargar las listas iniciales para POB
     */
    public void consultaInicialPOB() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.limpiarAfiliaciones();
            this.tieneAfiliaciones = false;
            tipoTransf = T_TRANSF_PROPOTROSBANCOS;
            setearForm();
            consultarCuentasPropias();
            consultarAfiliacionesPOB();
            FacesMessage fMsg;
            if (afiliaciones.isEmpty()) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage(formTransferencias + ":divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.sinAfiliaciones", sesionController.getIdCanal())));
            }
            if (this.afiliaciones.size() > 0) {
                this.tieneAfiliaciones = true;
            }
        }
    }

    /**
     * metodo que se encarga de consultar las afiliaciones por el tipo de
     * transaccion definido
     */
    public void consultarAfiliacionesTercOtrosBcos() {
        tipoTransf = T_TRANSF_OTROSBANCOS;
        this.tieneAfiliaciones = false;
        //se limpian las afiliaciones
        this.afiliaciones = new ArrayList<>();
        IbAfiliacionesDTO afiliacionCliente = afiliacionDAO.obtenerListadoAfiliadosPorOperacion(sesionController.getUsuario().getId().toString(),
                ID_TRANS_CTAS_T_OTROS_BCOS,
                sesionController.getIdCanal(),
                sesionController.getNombreCanal());
        if (!afiliacionCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //manejar mensaje de error     
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtCtaAbonar", fMsg);
        } else {
            //se consultan las tdc propias para dicha transaccion
            if (afiliacionCliente.getAfiliaciones().size() > 0) {
                this.tieneAfiliaciones = true;
                if (!afiliacionCliente.getAfiliaciones().isEmpty()) {
                    for (int i = 0; i < afiliacionCliente.getAfiliaciones().size(); i++) {
                        afiliacionCliente.getAfiliaciones().get(i).setSemilla(sesionController.getSemilla());
                    }
                }
                this.afiliaciones = afiliacionCliente.getAfiliaciones();
            }
        }
    }

    /**
     * metodo que se encarga de consultar las afiliaciones por el tipo de
     * transaccion definido
     */
    public void consultarAfiliacionesTercDelSur() {
        tipoTransf = T_TRANSF_3ROSDELSUR;
        this.tieneAfiliaciones = false;
        //se limpian las afiliaciones
        this.afiliaciones = new ArrayList<>();
        IbAfiliacionesDTO afiliacionCliente = afiliacionDAO.obtenerListadoAfiliadosPorOperacion(sesionController.getUsuario().getId().toString(),
                ID_TRANS_CTAS_TERC_DELSUR,
                sesionController.getIdCanal(),
                sesionController.getNombreCanal());
        if (!afiliacionCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //manejar mensaje de error 
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtCtaAbonar", fMsg);
        } else {
            //se consultan las tdc propias para dicha transaccion
            if (afiliacionCliente.getAfiliaciones().size() > 0) {
                this.tieneAfiliaciones = true;
                if (!afiliacionCliente.getAfiliaciones().isEmpty()) {
                    for (int i = 0; i < afiliacionCliente.getAfiliaciones().size(); i++) {
                        afiliacionCliente.getAfiliaciones().get(i).setSemilla(sesionController.getSemilla());
                    }
                }
                this.afiliaciones = afiliacionCliente.getAfiliaciones();
            }
        }
    }

    /**
     * metodo que se encarga de consultar las afiliaciones por el tipo de
     * transaccion definido
     */
    public void consultarAfiliacionesPOB() {
        tipoTransf = T_TRANSF_PROPOTROSBANCOS;
        this.tieneAfiliaciones = false;
        //se limpian las afiliaciones
        this.afiliaciones = new ArrayList<>();
        IbAfiliacionesDTO afiliacionCliente = afiliacionDAO.obtenerListadoAfiliadosPorOperacion(sesionController.getUsuario().getId().toString(),
                ID_TRANS_CTAS_P_OTROS_BCOS,
                sesionController.getIdCanal(),
                sesionController.getNombreCanal());
        if (!afiliacionCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //manejar mensaje de error 
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtCtaAbonar", fMsg);
        } else {
            //se consultan las tdc propias para dicha transaccion
            if (afiliacionCliente.getAfiliaciones().size() > 0) {
                this.tieneAfiliaciones = true;
                if (!afiliacionCliente.getAfiliaciones().isEmpty()) {
                    for (int i = 0; i < afiliacionCliente.getAfiliaciones().size(); i++) {
                        afiliacionCliente.getAfiliaciones().get(i).setSemilla(sesionController.getSemilla());
                    }
                }
                this.afiliaciones = afiliacionCliente.getAfiliaciones();
            }
        }
    }

    /**
     * metodo encargado de limpiar el formulario de transferencias desde el menu
     */
    public void iniciarTransferencia() {
        if (sesionController.isReiniciarForm()) {
            if (!FacesContext.getCurrentInstance().isPostback()) {
                this.mantenerDatosForm = false;
                this.limpiarAfiliaciones();
                sesionController.setReiniciarForm(false);
            }
        }
    }

    /**
     * metodo utilitario para reiniciar los valores de transferencias
     */
    public void limpiarAfiliaciones() {
        if (!this.mantenerDatosForm) {
            this.cuentasCliente = new ArrayList<>();
            this.cuentaDestinoSeleccionada = "-1";
            this.setDatosAfiliacionSeleccionada(null);
            this.cuentaSeleccionada = "-1";
            this.monto = "";
            this.motivo = "";
            this.codigoOTP = "";
            this.emailBeneficiario = "";
            this.afiliaciones = new ArrayList<>();
            this.sesionController.setEnTransaccion(false);
            this.tieneAfiliaciones = false;
            this.otpGenerado = false;
            this.tipoEjecTransf = "U";
            this.seleccionarTipoEjecTransf();
        }
    }

    /**
     * metodo que se encarga de realizar las validaciones de negocio de las
     * transferencias entre cuentas propias
     *
     * @return int
     */
    public int validarTransferencias() {
        FacesMessage fMsg;
        int errores = 0;
        //se consultan los acumulados para el dia
        IbAcumuladoTransaccionDTO acumuladoUsuario = acumuladoDAO.consultarAcumuladoUsuario(sesionController.getUsuario().getId().toString(), sesionController.getNombreCanal(), sesionController.getIdCanal());
        //se obtiene el parametro configurado como tope maximo
        BigDecimal acumMaxDelsur = new BigDecimal(parametrosController.getValorParametro("pnw.acumulado.trans.delsur", sesionController.getNombreCanal()));
        //validaciones de negocio 
        //validacion de limites
        switch (this.tipoTransf) {
            case T_TRANSF_PROPIAS: {
                //Propias del Sur, Límite del Usuario por Canal

                if (sesionController.getUsuarioCanal().getLimiteInternas().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                            textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                    FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                    errores++;
                }

                //Se consulta el límite del banco
                IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.propias", sesionController.getIdCanal());

                //Propias del Sur, Límite del banco
                if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPDS", sesionController.getNombreCanal()).getMensajeTecnico(),
                            textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPDS", sesionController.getNombreCanal()).getMensajeUsuario());
                    FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);//LÍMITE DEL BANCO
                    errores++;
                }

                break;
            }
            case T_TRANSF_OTROSBANCOS: {
                //se almacena el total de la operacion actual sumada a los acumulados
                BigDecimal acumulado = acumuladoUsuario.getAcumuladoTotalTransf().add(new BigDecimal(eliminarformatoSimpleMonto(this.monto)));
                if (acumulado.compareTo(acumMaxDelsur) > 0) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.montoAcumulado", sesionController.getNombreCanal()).getMensajeTecnico(),
                            textosFacade.findByCodigo("pnw.global.texto.montoAcumulado", sesionController.getNombreCanal()).getMensajeUsuario());
                    FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                    errores++;
                } else {
                    //Terceros Otros Bancos, Límite de Afiliación
                    if (datosAfiliacionSeleccionada.getMontoMaximo() != null && datosAfiliacionSeleccionada.getMontoMaximo().compareTo(BigDecimal.ZERO) > 0) {
                        if (datosAfiliacionSeleccionada.getMontoMaximo().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteAfiliacionInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteAfiliacionInv", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                            errores++;
                        }
                        //Terceros Otros Bancos, Límite del Usuario por Canal
                        if (sesionController.getUsuarioCanal().getLimiteExternasTerceros().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                            errores++;
                        }

                        //Se consulta el límite del banco
                        IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.otrosbancos", sesionController.getIdCanal());
                        //Terceros Otros Bancos, Límite del Banco
                        if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTOB", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTOB", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);//LÍMITE DEL BANCO
                            errores++;
                        }
                    } else {
                        //Terceros Otros Bancos, Límite de Afiliación
                        if (datosAfiliacionSeleccionada.getMontoMaximo() != null && datosAfiliacionSeleccionada.getMontoMaximo().compareTo(BigDecimal.ZERO) == 0) {
//                        COMENTADO TEMPORALMENTE MIENTRAS SE LE INDICAN A LOS NUEVOS CLIENTES 
//                        QUE DEBEN SETEAR LOS LIMITES DE LAS AFILIACIONES MIGRADAS MIRNA 14-07-2017
                            //Terceros Otros Bancos, Límite del Usuario por Canal
                            if (sesionController.getUsuarioCanal().getLimiteExternasTerceros().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                        textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                                FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                                errores++;
                            }

                            //Se consulta el límite del banco
                            IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.otrosbancos", sesionController.getIdCanal());
                            //Terceros Otros Bancos, Límite del Banco
                            if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTOB", sesionController.getNombreCanal()).getMensajeTecnico(),
                                        textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTOB", sesionController.getNombreCanal()).getMensajeUsuario());
                                FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);//LÍMITE DEL BANCO
                                errores++;
                            }
                        }
                    }
                }
                break;
            }
            case T_TRANSF_3ROSDELSUR: {
                //se almacena el total de la operacion actual sumada a los acumulados
                BigDecimal acumulado = acumuladoUsuario.getAcumuladoTotalTransf().add(new BigDecimal(eliminarformatoSimpleMonto(this.monto)));
                if (acumulado.compareTo(acumMaxDelsur) > 0) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.montoAcumulado", sesionController.getNombreCanal()).getMensajeTecnico(),
                            textosFacade.findByCodigo("pnw.global.texto.montoAcumulado", sesionController.getNombreCanal()).getMensajeUsuario());
                    FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                    errores++;
                } else {
                    if (datosAfiliacionSeleccionada.getMontoMaximo() != null && datosAfiliacionSeleccionada.getMontoMaximo().compareTo(BigDecimal.ZERO) > 0) {
                        if (datosAfiliacionSeleccionada.getMontoMaximo().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteAfiliacionInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteAfiliacionInv", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                            errores++;
                        }
                        if (sesionController.getUsuarioCanal().getLimiteInternasTerceros().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                            errores++;
                        }

                        //Se consulta el límite del banco
                        IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.tercerosdelsur", sesionController.getIdCanal());

                        //Terceros del Sur, Límite del Banco
                        if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTDS", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTDS", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);//LÍMITE DEL BANCO
                            errores++;
                        }
                        break;
                    } else {
                        if (datosAfiliacionSeleccionada.getMontoMaximo() != null && datosAfiliacionSeleccionada.getMontoMaximo().compareTo(BigDecimal.ZERO) == 0) {
                            //                        COMENTADO TEMPORALMENTE MIENTRAS SE LE INDICAN A LOS NUEVOS CLIENTES 
//                        QUE DEBEN SETEAR LOS LIMITES DE LAS AFILIACIONES MIGRADAS MIRNA 14-07-2017
                            if (sesionController.getUsuarioCanal().getLimiteInternasTerceros().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                        textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                                FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                                errores++;
                            }

                            //Se consulta el límite del banco
                            IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.tercerosdelsur", sesionController.getIdCanal());

                            //Terceros del Sur, Límite del Banco
                            if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTDS", sesionController.getNombreCanal()).getMensajeTecnico(),
                                        textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTDS", sesionController.getNombreCanal()).getMensajeUsuario());
                                FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);//LÍMITE DEL BANCO
                                errores++;
                            }
                            break;
                        }
                    }
                }
            }
            case T_TRANSF_PROPOTROSBANCOS: {
                //se almacena el total de la operacion actual sumada a los acumulados
                BigDecimal acumulado = acumuladoUsuario.getAcumuladoTotalTransf().add(new BigDecimal(eliminarformatoSimpleMonto(this.monto)));
                if (acumulado.compareTo(acumMaxDelsur) > 0) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.montoAcumulado", sesionController.getNombreCanal()).getMensajeTecnico(),
                            textosFacade.findByCodigo("pnw.global.texto.montoAcumulado", sesionController.getNombreCanal()).getMensajeUsuario());
                    FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                    errores++;
                } else {
                    if (datosAfiliacionSeleccionada.getMontoMaximo() != null && datosAfiliacionSeleccionada.getMontoMaximo().compareTo(BigDecimal.ZERO) > 0) {
                        if (datosAfiliacionSeleccionada.getMontoMaximo().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteAfiliacionInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteAfiliacionInv", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                            errores++;
                        }
                        if (sesionController.getUsuarioCanal().getLimiteExternas().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                            errores++;
                        }

                        //Se consulta el límita del banco
                        IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.propiasotrosbancos", sesionController.getIdCanal());

                        //Propias Otros Bancos, Límite del Banco
                        if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPOB", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPOB", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);//LÍMITE DEL BANCO
                            errores++;
                        }
                        break;
                    } else {
                        if (datosAfiliacionSeleccionada.getMontoMaximo() != null && datosAfiliacionSeleccionada.getMontoMaximo().compareTo(BigDecimal.ZERO) == 0) {
//                        COMENTADO TEMPORALMENTE MIENTRAS SE LE INDICAN A LOS NUEVOS CLIENTES 
//                        QUE DEBEN SETEAR LOS LIMITES DE LAS AFILIACIONES MIGRADAS MIRNA 14-07-2017
                            if (sesionController.getUsuarioCanal().getLimiteExternas().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                        textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                                FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                                errores++;
                            }

                            //Se consulta el límita del banco
                            IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.propiasotrosbancos", sesionController.getIdCanal());

                            //Propias Otros Bancos, Límite del Banco
                            if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                                FacesContext context = FacesContext.getCurrentInstance();
                                context.getExternalContext().getFlash().setKeepMessages(true);
                                fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPOB", sesionController.getNombreCanal()).getMensajeTecnico(),
                                        textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPOB", sesionController.getNombreCanal()).getMensajeUsuario());
                                FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);//LÍMITE DEL BANCO
                                errores++;
                            }
                            break;
                        }
                    }
                }
            }
        }

        //monto superior a 1 Bolivar
        if (Double.valueOf(eliminarformatoSimpleMonto(this.monto)) < Double.valueOf("1.01")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.mtoInv", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
            errores++;
        }
        //monto menor que saldo disponible se valida contra el saldo en tiempo real  
        this.datosCuentaSeleccionadaActual = cuentaDAO.obtenerDetalleCuenta(datosCuentaSeleccionadaLista.getCodigoTipoProducto(),
                datosCuentaSeleccionadaLista.getNumeroCuenta(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (this.datosCuentaSeleccionadaActual.getRespuestaDTO() == null || this.datosCuentaSeleccionadaActual.getRespuestaDTO().getCodigo() == null || this.datosCuentaSeleccionadaActual.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_EXCEPCION_GENERICA)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            errores++;
        } else {
            if (this.datosCuentaSeleccionadaActual.getSaldoDisponible().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.monto))) < 0) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.saldoInsuficiente", sesionController.getNombreCanal()).getMensajeTecnico(),
                        textosFacade.findByCodigo("pnw.global.texto.saldoInsuficiente", sesionController.getNombreCanal()).getMensajeUsuario());
                FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtMonto", fMsg);
                errores++;
            }
        }

        //cuentas diferentes
        if (cuentaSeleccionada.equals(cuentaDestinoSeleccionada)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.ctasDiferentes", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.ctasDiferentes", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtCtaDebitar", fMsg);
            FacesContext.getCurrentInstance().addMessage(formTransferencias + ":txtCtaAbonar", fMsg);
            errores++;
        }
        return errores;
    }

    /**
     * metodo para redirigir a paso 1 de tob
     */
    public void tobPaso1() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.ttob.url.paso1", sesionController.getIdCanal()));
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * metodo para redirigir a paso de llenado de formulario del transferencias
     * segun sea el tipo de transferencia seleccionada
     *
     * @return
     */
    public String transferenciaPaso2() {
        if (sesionController.isEnTransaccion()) {
            sesionController.setEnTransaccion(false);
            limpiarAfiliaciones();
        }
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(2);
        try {
            switch (tipoTransf) {
                case T_TRANSF_PROPIAS: {
                    consultarCuentasPropias();
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.transf.propias", sesionController.getIdCanal()));
                    return "";
                }
                case T_TRANSF_OTROSBANCOS: {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.transf.tercotrobanc", sesionController.getIdCanal()));
                    return "";
                }
                case T_TRANSF_3ROSDELSUR: {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.transf.tercdelsur", sesionController.getIdCanal()));
                    return "";
                }
                case T_TRANSF_PROPOTROSBANCOS: {
                    consultarCuentasPropias();
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.transf.propotrobanc", sesionController.getIdCanal()));
                    return "";
                }
                default: {
                    sesionController.setValidadorFlujo(1);
                    return parametrosController.getNombreParametro("pnw.global.url.inicial", sesionController.getIdCanal());
                }
            }
        } catch (IOException e) {

        }
        return "";
    }

    /**
     * metodo para redirigir a paso 1 de afiliar
     */
    public void confirPaso3() {
        sesionController.setTextoStatus("pnw.modal.texto.esperaTransaccion");
        int validaciones = this.validarTransferencias();
        if (validaciones == 0) {
            if (tipoEjecTransf.equalsIgnoreCase("A")) {
                if (this.frecSeleccionada == 0 || this.fechaFrecSeleccionada == 0 || this.fechaHasta == null) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage(formTransferencias + ":rowAgendarTransf", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.transf.msj.agendarTrnsf", sesionController.getIdCanal())));
                    redireccionarTransferencia();
                }
            }
            sesionController.setValidadorFlujo(3);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.transferencias.url.confirPaso3", sesionController.getIdCanal()));

        } else {
            redireccionarTransferencia();
        }
    }

    /**
     * metodo que identifica la transaccion y realiza el registro de bitacora
     */
    private void registrarBitacoraTransf(String tipoError) {

        //Registro en Bitacora
        switch (this.tipoTransf) {
            case T_TRANSF_PROPIAS: {
                //registro en bitacora
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_CTAS_PROPIAS_DELSUR, this.getCtaOrigenMascara(), this.getCtaDestinoMascara(), "Transferencias PDS - " + motivo, monto, numConfirmacion,
                        sesionController.getUsuario().getNombre(), sesionController.getUsuario().getTipoDoc().toString(), sesionController.getUsuario().getDocumento(), "", tipoError);
                break;
            }
            case T_TRANSF_OTROSBANCOS: {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_CTAS_T_OTROS_BCOS, this.getCtaOrigenMascara(), this.getCtaDestinoMascara(), "Transferencias TOB - " + motivo, monto, numConfirmacion,
                        datosAfiliacionSeleccionada.getNombreBeneficiario(), datosAfiliacionSeleccionada.getTipoDoc().toString(), datosAfiliacionSeleccionada.getDocumento(),
                        datosAfiliacionSeleccionada.getId().toString(), tipoError);
                break;
            }
            case T_TRANSF_3ROSDELSUR: {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_CTAS_TERC_DELSUR, this.getCtaOrigenMascara(), this.getCtaDestinoMascara(), "Transferencias TDS - " + motivo, monto, numConfirmacion,
                        datosAfiliacionSeleccionada.getNombreBeneficiario(), datosAfiliacionSeleccionada.getTipoDoc().toString(), datosAfiliacionSeleccionada.getDocumento(),
                        datosAfiliacionSeleccionada.getId().toString(), tipoError);
                break;
            }
            case T_TRANSF_PROPOTROSBANCOS: {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_CTAS_P_OTROS_BCOS, this.getCtaOrigenMascara(), this.getCtaDestinoMascara(), "Transferencias POB - " + motivo, monto, numConfirmacion,
                        datosAfiliacionSeleccionada.getNombreBeneficiario(), datosAfiliacionSeleccionada.getTipoDoc().toString(), datosAfiliacionSeleccionada.getDocumento(),
                        datosAfiliacionSeleccionada.getId().toString(), tipoError);
                break;
            }
        }
    }

    /**
     * metodo que identifica la transaccion y realiza el registro de bitacora
     * para las transferencias agendadas
     */
    private void registrarBitacoraAgendaTransf(String tipoError) {

        //Registro en Bitacora
        switch (this.tipoTransf) {
            case T_TRANSF_PROPIAS: {
                //registro en bitacora
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_AGENDA_TPDS, this.getCtaOrigenMascara(), this.getCtaDestinoMascara(), "Agenda de Transferencias PDS - " + motivo, monto, numConfirmacion,
                        sesionController.getUsuario().getNombre(), sesionController.getUsuario().getTipoDoc().toString(), sesionController.getUsuario().getDocumento(), "", tipoError);
                break;
            }
            case T_TRANSF_OTROSBANCOS: {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_AGENDA_TTOB, this.getCtaOrigenMascara(), this.getCtaDestinoMascara(), "Agenda de Transferencias TOB - " + motivo, monto, numConfirmacion,
                        datosAfiliacionSeleccionada.getNombreBeneficiario(), datosAfiliacionSeleccionada.getTipoDoc().toString(), datosAfiliacionSeleccionada.getDocumento(),
                        datosAfiliacionSeleccionada.getId().toString(), tipoError);
                break;
            }
            case T_TRANSF_3ROSDELSUR: {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_AGENDA_TTDS, this.getCtaOrigenMascara(), this.getCtaDestinoMascara(), "Agenda de Transferencias TDS - " + motivo, monto, numConfirmacion,
                        datosAfiliacionSeleccionada.getNombreBeneficiario(), datosAfiliacionSeleccionada.getTipoDoc().toString(), datosAfiliacionSeleccionada.getDocumento(),
                        datosAfiliacionSeleccionada.getId().toString(), tipoError);
                break;
            }
            case T_TRANSF_PROPOTROSBANCOS: {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_AGENDA_TPOB, this.getCtaOrigenMascara(), this.getCtaDestinoMascara(), "Agenda de Transferencias POB - " + motivo, monto, numConfirmacion,
                        datosAfiliacionSeleccionada.getNombreBeneficiario(), datosAfiliacionSeleccionada.getTipoDoc().toString(), datosAfiliacionSeleccionada.getDocumento(),
                        datosAfiliacionSeleccionada.getId().toString(), tipoError);
                break;
            }
        }
    }

    /**
     * Metodo que setea los datos y parametros para el envio del correo luego
     * del proceso de la transferencia
     */
    public void notificarTransferencia() {

        String nombreBeneficiario = "";
        StringBuilder textoE = new StringBuilder(""); //EMISOR
        StringBuilder textoB = new StringBuilder(""); //BENEFICIARIO

        if (this.tipoTransf.equalsIgnoreCase(T_TRANSF_PROPIAS) || this.tipoTransf.equalsIgnoreCase(T_TRANSF_PROPOTROSBANCOS)) {
            nombreBeneficiario = sesionController.getUsuario().getNombre();
        } else {
            nombreBeneficiario = datosAfiliacionSeleccionada.getNombreBeneficiario();
        }

        String documentoBenef;
        if (tipoTransf.equalsIgnoreCase(T_TRANSF_PROPIAS)) {
            documentoBenef = sesionController.getUsuario().getDocumento();
        } else {
            documentoBenef = datosAfiliacionSeleccionada.getDocumento();
        }

        textoE.append(sesionController.getUsuario().getNombre())
                .append("~")
                .append(nombreBeneficiario)
                .append("~")
                .append(documentoBenef)
                .append("~")
                .append(formatoAsteriscosWeb(this.getCtaDestinoMascara()))
                .append("~")
                .append(formatoAsteriscosWeb(this.getCtaOrigenMascara()))
                .append("~")
                .append(monto)
                .append("~")
                .append(getFechaTransaccion())
                .append("~")
                .append(getNumConfirmacion())
                .append("~")
                .append(getMotivo());

        textoB.append(sesionController.getUsuario().getNombre())
                .append("~")
                .append(nombreBeneficiario)
                .append("~")
                .append(documentoBenef)
                .append("~")
                .append(formatoAsteriscosWeb(this.getCtaDestinoMascara()))
                .append("~")
                .append(formatoAsteriscosWeb(this.getCtaOrigenMascara()))
                .append("~")
                .append(monto)
                .append("~")
                .append(getFechaTransaccion())
                .append("~")
                .append(getNumConfirmacion())
                .append("~")
                .append(getMotivo());

        //Envio de Email del Emisor
        notificacionesDAO.enviarEmailPN(CODIGO_EMAIL_TRANSF, sesionController.getUsuario().getEmail(), textoE.toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        //Envio de Email del Beneficiario
        notificacionesDAO.enviarEmailPN(CODIGO_EMAIL_TRANSF, getEmailBeneficiario(), textoB.toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
    }

    /**
     * Metodo que setea los datos y parametros para el envio del correo luego
     * del proceso de la Agenda de la transferencia
     */
    public void notificarAgendaTransferencia() {

        String asunto = "";
        String nombreEmisor;
        String nombreBeneficiario = "";
        String identificacionBenef = "";
        StringBuilder texto = new StringBuilder("");
        StringBuilder textoE = new StringBuilder(""); //EMISOR

        //setFechaTransaccion(BDSUtil.formatearFecha(Calendar.getInstance().getTime(), BDSUtil.FORMATO_FECHA_COMPLETA));
        setFechaTransaccion(new Date());

        switch (this.tipoTransf) {
            case T_TRANSF_PROPIAS: {
                asunto = "Agenda de Transferencia entre Cuentas Propias, DELSUR";
                nombreEmisor = sesionController.getUsuario().getNombre();
                nombreBeneficiario = nombreEmisor;
                identificacionBenef = sesionController.getUsuario().getDocumento();

                textoE = new StringBuilder("Estimado(a) ");
                textoE.append(nombreEmisor);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append("Le informamos que su solicitud de Agenda de Transferencia de fondos se registró satisfactoriamente según el siguiente detalle:");
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);

                break;
            }
            case T_TRANSF_OTROSBANCOS: {
                asunto = "Agenda de Transferencia entre Cuentas en Otros Bancos, DELSUR";
                nombreEmisor = sesionController.getUsuario().getNombre();
                nombreBeneficiario = datosAfiliacionSeleccionada.getNombreBeneficiario();
                setEmailBeneficiario(datosAfiliacionSeleccionada.getEmail());
                identificacionBenef = datosAfiliacionSeleccionada.getDocumento();

                textoE = new StringBuilder("Estimado(a) ");
                textoE.append(nombreEmisor);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append("Le informamos que su solicitud de Agenda de Transferencia de fondos se registró satisfactoriamente según el siguiente detalle:");
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);

                break;
            }
            case T_TRANSF_3ROSDELSUR: {
                asunto = "Agenda de Transferencia entre Cuentas de Terceros, DELSUR";
                nombreEmisor = sesionController.getUsuario().getNombre();
                nombreBeneficiario = datosAfiliacionSeleccionada.getNombreBeneficiario();
                setEmailBeneficiario(datosAfiliacionSeleccionada.getEmail());
                identificacionBenef = datosAfiliacionSeleccionada.getDocumento();

                textoE = new StringBuilder("Estimado(a) ");
                textoE.append(nombreEmisor);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append("Le informamos que su solicitud de Agenda de Transferencia de fondos se registró satisfactoriamente según el siguiente detalle:");
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);

                break;
            }

            case T_TRANSF_PROPOTROSBANCOS: {
                asunto = "Agenda de Transferencia entre Cuentas Propias de Otros Bancos, DELSUR";
                nombreEmisor = sesionController.getUsuario().getNombre();
                nombreBeneficiario = nombreEmisor;
                identificacionBenef = datosAfiliacionSeleccionada.getDocumento();

                textoE = new StringBuilder("Estimado(a) ");
                textoE.append(nombreEmisor);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append("Le informamos que su solicitud de Agenda de Transferencia de fondos se registro satisfactoriamente según el siguiente detalle:");
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);

                break;
            }
        }

        texto.append("Registro de Agenda de Transferencia ");
        texto.append(NUEVALINEAEMAIL);
        texto.append("Nombre de Beneficiario: ");
        texto.append(nombreBeneficiario);
        texto.append(NUEVALINEAEMAIL);
        texto.append("Nro. Cédula / Rif Beneficiario: ");
        texto.append(identificacionBenef);
        texto.append(NUEVALINEAEMAIL);
        texto.append("Cuenta de Destino: ");
        texto.append(formatoAsteriscosWeb(this.getCtaDestinoMascara()));
        texto.append(NUEVALINEAEMAIL);
        texto.append("Cuenta de Origen: ");
        texto.append(formatoAsteriscosWeb(this.getCtaOrigenMascara()));
        texto.append(NUEVALINEAEMAIL);
        texto.append("Monto del Transferencia: ");
        texto.append(monto);
        texto.append(" Bs.");
        texto.append(NUEVALINEAEMAIL);
        texto.append("Fecha de Validez de la Agenda de Transacción: ");
        texto.append(formatearFecha(getFechaHasta(), FORMATO_FECHA_SIMPLE));
        texto.append(NUEVALINEAEMAIL);
        texto.append("Observación: ");
        texto.append(getMotivo());
        texto.append(NUEVALINEAEMAIL);
        texto.append(NUEVALINEAEMAIL);
        texto.append("Este correo electrónico se genera automáticamente cada vez que se realiza una Agenda de Transferencia o Pago a su persona. Por su seguridad no responda al mismo y en caso que desconozca esta transacción, comuníquese inmediatamente con nosotros a través de los teléfonos 0212-208.73.44 / 0212-208.74.45");
        //texto.append(NUEVALINEAEMAIL);
        //texto.append("Si desea disfrutar de los beneficios y las ventajas de DELSUR Banco Universal, conéctese con nosotros a través de www.delsur.com.ve.");
        texto.append(NUEVALINEAEMAIL);
        texto.append(NUEVALINEAEMAIL);
        texto.append("Atentamente,");
        texto.append(NUEVALINEAEMAIL);
        texto.append("DELSUR Banco Universal www.delsur.com.ve.");
        texto.append(NUEVALINEAEMAIL);
        texto.append("Un pequeño Grande en Detalles.");

        IbCanalDTO canal = canalController.getIbCanalDTO(sesionController.getIdCanal());

        EMailDTO emailDTOOrdenante = notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("pnw.global.texto.emailBanco", sesionController.getIdCanal()), sesionController.getUsuario().getEmail(), asunto, textoE.append(texto).toString(), String.valueOf(canal.getId()), canal.getCodigo());

    }

    /**
     * Action de confirmar datos para retornar a editar los mismos
     *
     */
    public void reiniciarTransferencia() {
        this.mantenerDatosForm = false;
        limpiarAfiliaciones();
        this.tieneAfiliaciones = true;
        sesionController.setValidacionOTP(2);
        sesionController.setValidadorFlujo(2);
        switch (tipoTransf) {
            case T_TRANSF_PROPIAS: {
                consultarCuentasPropias();
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.transf.propias", sesionController.getIdCanal()));

                } catch (IOException e) {

                }
                return;
            }
            case T_TRANSF_OTROSBANCOS: {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.transf.tercotrobanc", sesionController.getIdCanal()));

                } catch (IOException e) {

                }
                return;
            }
            case T_TRANSF_3ROSDELSUR: {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.transf.tercdelsur", sesionController.getIdCanal()));

                } catch (IOException e) {

                }
                return;
            }
            case T_TRANSF_PROPOTROSBANCOS: {
                consultarCuentasPropias();
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.transf.propotrobanc", sesionController.getIdCanal()));

                } catch (IOException e) {

                }
                return;
            }
            default: {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.global.url.inicial", sesionController.getIdCanal()));
                } catch (IOException e) {

                }
            }
        }
    }

    /**
     * Action de redireccionar las paginas de tranferencia
     *
     *
     */
    public void redireccionarTransferencia() {
        this.mantenerDatosForm = true;
        limpiarAfiliaciones();
        this.tieneAfiliaciones = true;
        sesionController.setValidacionOTP(2);
        sesionController.setValidadorFlujo(2);
        switch (tipoTransf) {
            case T_TRANSF_PROPIAS: {
                consultarCuentasPropias();
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.transf.propias", sesionController.getIdCanal()));

                } catch (IOException e) {

                }
                //return "";
            }
            case T_TRANSF_OTROSBANCOS: {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.transf.tercotrobanc", sesionController.getIdCanal()));

                } catch (IOException e) {

                }
                //return "";
            }
            case T_TRANSF_3ROSDELSUR: {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.transf.tercdelsur", sesionController.getIdCanal()));

                } catch (IOException e) {

                }
                //return "";
            }
            case T_TRANSF_PROPOTROSBANCOS: {
                consultarCuentasPropias();
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.transf.propotrobanc", sesionController.getIdCanal()));

                } catch (IOException e) {

                }
                //return "";
            }
        }
    }

    /**
     * metodo que se encarga de realizar las validaciones de negocio de las
     * transferencias entre cuentas propias
     *
     * @return TransaccionDTO
     */
    public TransaccionDTO ejecutarTransferencia() {
        boolean validacionesSeguridad;
        TransaccionDTO transaccionDTO = new TransaccionDTO();
        RespuestaDTO acumuladoDTO;

        //si el tipó de transaccion no es unica procedemos a agendarla
        if (!this.tipoEjecTransf.equalsIgnoreCase("U")) {
            /**
             * **************AGENDAR TRANSACCION *******************
             */
            RespuestaDTO respuestaAgenda = new RespuestaDTO();
            IBAgendaTransaccionDTO agenda = new IBAgendaTransaccionDTO();
            if (tipoTransf.equalsIgnoreCase(T_TRANSF_PROPIAS)) {

                this.datosAfiliacionSeleccionada = new IbAfiliacionesDTO(sesionController.getSemilla());

                //se llenan los datos del usuario
                agenda.setCuentaDestino(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaDestinoSeleccionada));
                agenda.setEmail(sesionController.getUsuario().getEmail());
                agenda.setTipoDoc(sesionController.getUsuario().getTipoDoc());
                agenda.setDocumento(sesionController.getUsuario().getDocumento());
                agenda.setNombreBeneficiario(sesionController.getUsuario().getNombre());
            } else {
                //se llenan los datos del beneficiario
                agenda.setCuentaDestino(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaDestinoSeleccionada));
                agenda.setEmail(this.datosAfiliacionSeleccionada.getEmail());
                agenda.setTipoDoc(this.datosAfiliacionSeleccionada.getTipoDoc());
                agenda.setDocumento(this.datosAfiliacionSeleccionada.getDocumento());
                agenda.setNombreBeneficiario(this.datosAfiliacionSeleccionada.getNombreBeneficiario());
            }
            //se llenan los datos genericos
            agenda.setCuentaOrigen(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaSeleccionada));
            agenda.setMonto(BigDecimal.valueOf(Double.parseDouble(eliminarformatoSimpleMonto(this.monto))));
            agenda.setIdUsuario(sesionController.getUsuario().getId());
            agenda.setFrecuencia(String.valueOf(this.frecSeleccionada).charAt(0));
            agenda.setDiaFrecuencia(this.fechaFrecSeleccionada);
            agenda.setDescripcion(this.motivo);
            agenda.setTipo(TIPO_AGENDA_TRANF);
            agenda.setFechalimite(formatearFecha(fechaHasta, FORMATO_FECHA_SIMPLE));
            //se asigna el id de transaccion
            switch (tipoTransf) {
                case (T_TRANSF_PROPIAS): {
                    agenda.setIdTransaccion(Integer.valueOf(ID_TRANS_CTAS_PROPIAS_DELSUR));
                    break;
                }
                case (T_TRANSF_PROPOTROSBANCOS): {
                    agenda.setIdTransaccion(Integer.valueOf(ID_TRANS_CTAS_P_OTROS_BCOS));
                    break;
                }
                case (T_TRANSF_3ROSDELSUR): {
                    agenda.setIdTransaccion(Integer.valueOf(ID_TRANS_CTAS_TERC_DELSUR));
                    break;
                }
                case (T_TRANSF_OTROSBANCOS): {
                    agenda.setIdTransaccion(Integer.valueOf(ID_TRANS_CTAS_T_OTROS_BCOS));
                    break;
                }
            }
            //validamos que no exista un registro previo con los mismos valores
            UtilDTO util = agendaTransaccionesDAO.consultarIdCabeceraAgenda(agenda, sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (util != null && util.getRespuestaDTO() != null && !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                if (util.getResuladosDTO() != null && util.getResuladosDTO().get("id") != null && !util.getResuladosDTO().get("id").toString().isEmpty()) {
                    respuestaAgenda.setCodigo(CODIGO_RESPUESTA_EXITOSO);
                    respuestaAgenda.setCodigoSP(CODIGO_EXCEPCION_GENERICA);
                    respuestaAgenda.setDescripcionSP(CODIGO_EXCEPCION_GENERICA);
                    //notificamos que el registro ya existe
                    respuestaAgenda.setTextoSP(textosController.getNombreTexto("pnw.global.texto.transDuplicada", sesionController.getNombreCanal()));
                } else {
                    //añadimos la cabecera
                    respuestaAgenda = agendaTransaccionesDAO.agregarCabeceraAgenda(agenda, sesionController.getNombreCanal(), sesionController.getIdCanal());
                    //si la cabecera se agrego con exito añadimos el detalle
                    if (respuestaAgenda.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        util = agendaTransaccionesDAO.consultarIdCabeceraAgenda(agenda, sesionController.getNombreCanal(), sesionController.getIdCanal());
                        agenda.setIdAgenda(BigDecimal.valueOf(Double.parseDouble(util.getResuladosDTO().get("id").toString())));
                        respuestaAgenda = agendaTransaccionesDAO.agregarDetalleAgenda(agenda, sesionController.getNombreCanal(), sesionController.getIdCanal());
                    }
                }
            }
            transaccionDTO.setRespuestaDTO(respuestaAgenda);

        } else {
            /**
             * ***********EJECUTAR TRANSACCION***********
             */
            if (!tipoTransf.equalsIgnoreCase(T_TRANSF_PROPIAS)) {
                this.datosAfiliacionSeleccionada = this.afiliacionSeleccionadaCompleta(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaDestinoSeleccionada));
                datosAfiliacionSeleccionada.setSemilla(sesionController.getSemilla());
                validacionesSeguridad = sesionController.productosCliente(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaSeleccionada));
            } else {
                datosCuentaSeleccionadaActual.setSemilla(sesionController.getSemilla());
                validacionesSeguridad = sesionController.productosCliente(datosCuentaSeleccionadaActual.getDesEnc().desencriptar(cuentaSeleccionada));
            }
            if (validacionesSeguridad) {
                switch (tipoTransf) {
                    case T_TRANSF_PROPIAS: {
                        validacionesSeguridad = sesionController.productosCliente(datosCuentaSeleccionadaActual.getDesEnc().desencriptar(cuentaDestinoSeleccionada));
                        if (validacionesSeguridad) {
                            transaccionDTO = tranferenciasYPagosDAO.transferenciaPropiasMismoBanco(datosCuentaSeleccionadaActual.getDesEnc().desencriptar(cuentaSeleccionada),
                                    datosCuentaSeleccionadaActual.getDesEnc().desencriptar(cuentaDestinoSeleccionada), new BigDecimal(eliminarformatoSimpleMonto(this.monto)),
                                    motivo, sesionController.getIdCanal(), sesionController.getNombreCanal());
                        }
                        break;
                    }
                    case T_TRANSF_OTROSBANCOS: {
                        validacionesSeguridad = sesionController.validarAfiliacion(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaDestinoSeleccionada),
                                sesionController.getUsuario().getDocumento(),
                                sesionController.getUsuario().getId().toString(),
                                ID_TRANS_CTAS_T_OTROS_BCOS,
                                tipoTransf,
                                sesionController.getIdCanal(),
                                sesionController.getNombreCanal());
                        if (validacionesSeguridad) {
                            transaccionDTO = tranferenciasYPagosDAO.transferenciaOtroBanco(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaSeleccionada),
                                    datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaDestinoSeleccionada), datosAfiliacionSeleccionada.getTipoDoc() + datosAfiliacionSeleccionada.getDocumento(), datosAfiliacionSeleccionada.getNombreBeneficiario(),
                                    new BigDecimal(eliminarformatoSimpleMonto(this.monto)), motivo, sesionController.getIdCanal(), sesionController.getNombreCanal());
                            if (transaccionDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                acumuladoDTO = acumuladoDAO.insertarAcumuladoTransaccion(sesionController.getUsuario().getId().toString(), this.monto, ID_TRANS_CTAS_T_OTROS_BCOS, sesionController.getNombreCanal(), sesionController.getIdCanal());
                            }
                        }
                        break;
                    }
                    case T_TRANSF_3ROSDELSUR: {
                        validacionesSeguridad = sesionController.validarAfiliacion(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaDestinoSeleccionada),
                                sesionController.getUsuario().getDocumento(),
                                sesionController.getUsuario().getId().toString(),
                                ID_TRANS_CTAS_TERC_DELSUR,
                                tipoTransf,
                                sesionController.getIdCanal(),
                                sesionController.getNombreCanal());
                        if (validacionesSeguridad) {
                            transaccionDTO = tranferenciasYPagosDAO.transferenciaMismoBanco(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaSeleccionada),
                                    datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaDestinoSeleccionada), new BigDecimal(eliminarformatoSimpleMonto(this.monto)),
                                    motivo, sesionController.getIdCanal(), sesionController.getNombreCanal());
                            if (transaccionDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                acumuladoDTO = acumuladoDAO.insertarAcumuladoTransaccion(sesionController.getUsuario().getId().toString(), this.monto, ID_TRANS_CTAS_TERC_DELSUR, sesionController.getNombreCanal(), sesionController.getIdCanal());
                            }
                        }
                        break;
                    }
                    case T_TRANSF_PROPOTROSBANCOS: {
                        validacionesSeguridad = sesionController.validarAfiliacion(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaDestinoSeleccionada),
                                sesionController.getUsuario().getDocumento(),
                                sesionController.getUsuario().getId().toString(),
                                ID_TRANS_CTAS_P_OTROS_BCOS,
                                tipoTransf,
                                sesionController.getIdCanal(),
                                sesionController.getNombreCanal());
                        if (validacionesSeguridad) {
                            transaccionDTO = tranferenciasYPagosDAO.transferenciaOtroBanco(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaSeleccionada),
                                    datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaDestinoSeleccionada), datosAfiliacionSeleccionada.getTipoDoc() + datosAfiliacionSeleccionada.getDocumento(), datosAfiliacionSeleccionada.getNombreBeneficiario(),
                                    new BigDecimal(eliminarformatoSimpleMonto(this.monto)), motivo, sesionController.getIdCanal(), sesionController.getNombreCanal());
                            if (transaccionDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                acumuladoDTO = acumuladoDAO.insertarAcumuladoTransaccion(sesionController.getUsuario().getId().toString(), this.monto, ID_TRANS_CTAS_P_OTROS_BCOS, sesionController.getNombreCanal(), sesionController.getIdCanal());
                            }
                        }
                        break;
                    }
                }
            }
            if (!validacionesSeguridad) {
                RespuestaDTO respuesta = new RespuestaDTO();
                respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
                respuesta.setDescripcionSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
                respuesta.setTextoSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
                transaccionDTO.setRespuestaDTO(respuesta);
            }
        }

        return transaccionDTO;
    }

    /**
     * metodo para redirigir a paso 1 de afiliar
     *
     */
    public void ejecutarPaso4() {

        String codError = CODIGO_RESPUESTA_EXITOSO;
        TransaccionDTO transaccionDTO;
        if (sesionController.isEnTransaccion()) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formConfirmTrans:divCabeceraDetalle", new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Esta Transacción ya fue ejecutada"));
            redireccionarTransferencia();
        } else {
            int validaciones = this.validarTransferencias();
            setErrorConfirmacion("");
            if (validaciones == 0) {
                //realizamos la transaccion y validamos
                transaccionDTO = this.ejecutarTransferencia();
                if (transaccionDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && transaccionDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    sesionController.setEnTransaccion(true);
                    //notificamos en caso de realizar la transferencia
                    if (this.tipoEjecTransf.equalsIgnoreCase("U")) {
                        this.numConfirmacion = transaccionDTO.getNroReferencia();
                        //mensaje transaccion exitosa NOTA: en la nueva plataforma solo se maneja el mensaje exitoso en el paso final
                        //FacesContext.getCurrentInstance().addMessage("formDetalleTrans:divCabeceraDetalle", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("msj.operacion.exitosa", sesionController.getNombreCanal())));
                        //notificacion via email o SMS de transaccion
                        notificarTransferencia();
                        //se registra la bitacora
                        registrarBitacoraTransf(codError);
                        if (!this.tipoTransf.equalsIgnoreCase(T_TRANSF_PROPIAS)) {
                            /////////NOTIFICACION VIA SMS////////
                            String textoSMS = textosController.getNombreTexto("pnw.global.texto.smsTransf", sesionController.getNombreCanal());
                            String motivoSMS = textosController.getNombreTexto("pnw.menu.modulo.transf", sesionController.getNombreCanal());
                            HashMap<String, String> parametros = new HashMap();
                            parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                            parametros.put("\\$PMTO", monto);
                            parametros.put("\\$PCTAORIG", this.getCtaOrigenMascara().substring(this.getCtaOrigenMascara().length() - 4));
                            parametros.put("\\$PCTADEST", this.getCtaDestinoMascara().substring(this.getCtaDestinoMascara().length() - 4));
//                            sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);
                        }
                    } else {
                        //notificacion via email o SMS de transaccion de agenda de transferencia
                        notificarAgendaTransferencia();
                        //se registra la bitacora
                        registrarBitacoraAgendaTransf(codError);

                    }
                } else if (!transaccionDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    //mensaje error grave
                    codError = CODIGO_EXCEPCION_GENERICA;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formDetalleTrans:divCabeceraDetalle", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.msjOperacion", sesionController.getNombreCanal())));
                    setErrorConfirmacion(codError);
                } else if (transaccionDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && !transaccionDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    //mensaje error controlado
                    codError = transaccionDTO.getRespuestaDTO().getCodigoSP();
                    //se registra la bitacora
                    registrarBitacoraTransf(codError);
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formDetalleTrans:divCabeceraDetalle", new FacesMessage(FacesMessage.SEVERITY_WARN, "", transaccionDTO.getRespuestaDTO().getTextoSP()));
                    setErrorConfirmacion(codError);
                }
                sesionController.setTextoStatus(textosController.getNombreTexto("pnw.modal.texto.consultandoInf", sesionController.getIdCanal()));
                sesionController.setValidadorFlujo(4);

                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.transferencias.url.ejecutarPaso4", sesionController.getIdCanal()));
            } else {
                reiniciarTransferencia();
            }
        }
    }

    /**
     * Atributo para identificar si el usuario tiene afiliaciones o no al
     * momento de ingresar en otp
     *
     * @return true si tiene afiliaciones, false en caso contrario
     */
    public boolean isTieneAfiliaciones() {
        return tieneAfiliaciones;
    }

    /**
     * Atributo para identificar si el usuario tiene afiliaciones o no al
     * momento de ingresar en otp
     *
     * @param tieneAfiliaciones true si tiene afiliaciones, false en caso
     * contrario
     */
    public void setTieneAfiliaciones(boolean tieneAfiliaciones) {
        this.tieneAfiliaciones = tieneAfiliaciones;
    }

    /**
     * metodo que retorna un boolean identificando si se ejecuto la transaccion
     * mediante el numero de confirmacion
     *
     * @return boolean
     */
    public boolean transaccionExitosa() {
        if (numConfirmacion == null || numConfirmacion.trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    ////////////////////////////////GETTERS Y SETTERS//////////////////////////////////////////
    /**
     * obtener errorConfirmacion
     *
     * @return String
     */
    public String getErrorConfirmacion() {
        return errorConfirmacion;
    }

    /**
     * insertar errorConfirmacion
     *
     * @param errorConfirmacion String
     */
    public void setErrorConfirmacion(String errorConfirmacion) {
        this.errorConfirmacion = errorConfirmacion;
    }

    /**
     * metodo que se encarga retornar las afiliaciones consultadas por el tipo
     * de transaccion
     *
     * @return Collection < IbAfiliacionesDTO >
     */
    public Collection<IbAfiliacionesDTO> getAfiliaciones() {
        return afiliaciones;
    }

    /**
     * metodo que se encarga de asignar las afiliaciones consultadas por el tipo
     * de transaccion
     *
     * @param afiliaciones Collection < IbAfiliacionesDTO >
     */
    public void setAfiliaciones(Collection<IbAfiliacionesDTO> afiliaciones) {
        this.afiliaciones = afiliaciones;
    }

    /**
     * Obtiene Atributo para identificar el tipo de transferencia P - propias,
     * TOB - terceros otros bancos, TBDS - terceros banco del sur
     *
     * @return String P - propias, TOB - terceros otros bancos, TBDS - terceros
     * banco del sur
     */
    public String getTipoTransf() {
        return tipoTransf;
    }

    /**
     * Inserta Atributo para identificar el tipo de transferencia P - propias,
     * TOB - terceros otros bancos, TBDS - terceros banco del sur
     *
     * @param tipoTransf String P - propias, TOB - terceros otros bancos, TBDS -
     * terceros banco del sur
     */
    public void setTipoTransf(String tipoTransf) {
        this.tipoTransf = tipoTransf;
    }

    /**
     * Obtener Mensaje por defecto para los campos vacios o en null
     *
     * @return mensajeNull String Mensaje por defecto para los campos vacios o
     * en null
     */
    public String getMensajeNull() {
        return mensajeNull = textosFacade.findByCodigo("campoRequerido", sesionController.getNombreCanal()).getMensajeUsuario();
    }

    /**
     * Insertar Mensaje por defecto para los campos vacios o en null
     *
     * @param mensajeNull Mensaje por defecto para los campos vacios o en null
     */
    public void setMensajeNull(String mensajeNull) {
        this.mensajeNull = mensajeNull;
    }

    /**
     * Obtener Numero de confirmacion de la operacion realizada
     *
     * @return String Numero de confirmacion de la operacion realizada
     */
    public String getNumConfirmacion() {
        return numConfirmacion;
    }

    /**
     * Insertar Numero de confirmacion de la operacion realizada
     *
     * @param numConfirmacion Numero de confirmacion de la operacion realizada
     */
    public void setNumConfirmacion(String numConfirmacion) {
        this.numConfirmacion = numConfirmacion;
    }

    /**
     * Obtener Cuenta Destino a Acreditar extraido del formulario de
     * transferencias
     *
     * @return String Cuenta Destino a Acreditar extraido del formulario de
     * transferencias
     */
    public String getCuentaDestinoSeleccionada() {
        return cuentaDestinoSeleccionada;
    }

    /**
     * Insertar Cuenta Destino a Acreditar extraido del formulario de
     * transferencias
     *
     * @param cuentaDestinoSeleccionada Cuenta Destino a Acreditar extraido del
     * formulario de transferencias
     */
    public void setCuentaDestinoSeleccionada(String cuentaDestinoSeleccionada) {
        this.cuentaDestinoSeleccionada = cuentaDestinoSeleccionada;

        if (!tipoTransf.equalsIgnoreCase(T_TRANSF_PROPIAS)) {
            if (datosAfiliacionSeleccionada == null || datosAfiliacionSeleccionada.getSemilla() == null || datosAfiliacionSeleccionada.getSemilla().trim().equalsIgnoreCase("")) {
                this.datosAfiliacionSeleccionada = new IbAfiliacionesDTO(sesionController.getSemilla());
            }
            this.datosAfiliacionSeleccionada = this.afiliacionSeleccionadaCompleta(this.datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaDestinoSeleccionada));
        } else {
            if (datosCuentaSeleccionadaActual == null || datosCuentaSeleccionadaActual.getSemilla() == null || datosCuentaSeleccionadaActual.getSemilla().trim().equalsIgnoreCase("")) {
                this.datosCuentaSeleccionadaActual = new CuentaDTO(sesionController.getSemilla());
            }
            this.datosCuentaSeleccionadaActual = this.cuentaDestinoSeleccionadaCompleta(this.datosCuentaSeleccionadaActual.getDesEnc().desencriptar(cuentaDestinoSeleccionada));
        }
    }

    /**
     * metodo que se sencarga de retornar los datos completos de una cuenta
     * propia
     *
     * @param nroCuenta numero de la cuenta
     * @return CuentaDTO
     */
    public CuentaDTO cuentaPropiaSeleccionadaCompleta(String nroCuenta) {
        CuentaDTO cuentaTemp = new CuentaDTO(this.sesionController.getSemilla());
        for (CuentaDTO cuenta : cuentasCliente) {
            if (cuenta.getNumeroCuenta().equals(nroCuenta)) {
                cuentaTemp = cuenta;
                break;
            }
        }
        return cuentaTemp;
    }

    /**
     * metodo que se sencarga de retornar la afiliacion seleccionada
     *
     * @param nroCuenta numero de la cuenta
     * @return IbAfiliacionesDTO
     */
    public IbAfiliacionesDTO afiliacionSeleccionadaCompleta(String nroCuenta) {
        IbAfiliacionesDTO afiliacionTemp = new IbAfiliacionesDTO();
        for (IbAfiliacionesDTO afiliacion : afiliaciones) {
            if (afiliacion.getNumeroCuenta().equals(nroCuenta)) {
                afiliacionTemp = afiliacion;
                break;
            }
        }
        return afiliacionTemp;
    }

    /**
     * metodo que se sencarga de retornar los datos completos de una cuenta
     * destino
     *
     * @param nroCuenta numero de la cuenta
     * @return CuentaDTO
     */
    public CuentaDTO cuentaDestinoSeleccionadaCompleta(String nroCuenta) {
        CuentaDTO cuentaTemp = new CuentaDTO(this.sesionController.getSemilla());
        for (CuentaDTO cuenta : cuentasCliente) {
            if (cuenta.getNumeroCuenta().equals(nroCuenta)) {
                cuentaTemp = cuenta;
                break;
            }
        }
        return cuentaTemp;
    }

    /**
     * Obtener Cuenta del Titular a Debitar Seleccionada extraido del formulario
     * de transferencias
     *
     * @return String Cuenta del Titular a Debitar Seleccionada extraido del
     * formulario de transferencias
     */
    public String getCuentaSeleccionada() {
        return cuentaSeleccionada;
    }

    /**
     * Insertar Cuenta del Titular a Debitar Seleccionada extraido del
     * formulario de transferencias
     *
     * @param cuentaSeleccionada Cuenta del Titular a Debitar Seleccionada
     * extraido del formulario de transferencias
     */
    public void setCuentaSeleccionada(String cuentaSeleccionada) {
        this.cuentaSeleccionada = cuentaSeleccionada;
        if (datosCuentaSeleccionadaLista == null || datosCuentaSeleccionadaLista.getSemilla() == null || datosCuentaSeleccionadaLista.getSemilla().trim().equalsIgnoreCase("")) {
            this.datosCuentaSeleccionadaLista = new CuentaDTO(sesionController.getSemilla());
        }
        this.datosCuentaSeleccionadaLista = this.cuentaPropiaSeleccionadaCompleta(datosCuentaSeleccionadaLista.getDesEnc().desencriptar(cuentaSeleccionada));
    }

    /**
     * Obtener Monto extraido del formulario de transferencias
     *
     * @return String Monto extraido del formulario de transferencias
     */
    public String getMonto() {
        return monto;
    }

    /**
     * Insertar Monto extraido del formulario de transferencias
     *
     * @param monto Monto extraido del formulario de transferencias
     */
    public void setMonto(String monto) {
        this.monto = monto;
    }

    /**
     * Obtener Motivo extraido del formulario de transferencias
     *
     * @return String Motivo extraido del formulario de transferencias
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Insertar Motivo extraido del formulario de transferencias
     *
     * @param motivo Motivo extraido del formulario de transferencias
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * obtiene cuentas del cliente
     *
     * @return objeto que contiene las cuentas propias
     */
    public Collection<CuentaDTO> getCuentasCliente() {
        return cuentasCliente;
    }

    /**
     * asigna objeto que contiene las cuentas propias
     *
     * @param cuentasCliente
     */
    public void setCuentasCliente(Collection<CuentaDTO> cuentasCliente) {
        this.cuentasCliente = cuentasCliente;
    }

    /**
     * obtiene datos de Cuenta Seleccionada de la Lista
     *
     * @return objeto para almacenar los datos de la cuenta seleccionada de la
     * lista
     */
    public CuentaDTO getDatosCuentaSeleccionadaLista() {
        return datosCuentaSeleccionadaLista;
    }

    /**
     * asigna objeto para almacenar los datos de la cuenta seleccionada de la
     * lista
     *
     * @param datosCuentaSeleccionadaLista CuentaDTO
     */
    public void setDatosCuentaSeleccionadaLista(CuentaDTO datosCuentaSeleccionadaLista) {
        this.datosCuentaSeleccionadaLista = datosCuentaSeleccionadaLista;
    }

    /**
     * obtener datos de Cuenta Seleccionada Actual
     *
     * @return objeto para almacenar la coonsulta de los datos de la cuenta
     * seleccionada
     */
    public CuentaDTO getDatosCuentaSeleccionadaActual() {
        return datosCuentaSeleccionadaActual;
    }

    /**
     * asigna objeto para almacenar la coonsulta de los datos de la cuenta
     * seleccionada
     *
     * @param datosCuentaSeleccionadaActual CuentaDTO
     */
    public void setDatosCuentaSeleccionadaActual(CuentaDTO datosCuentaSeleccionadaActual) {
        this.datosCuentaSeleccionadaActual = datosCuentaSeleccionadaActual;
    }

    /**
     * obtiene datos de Afiliacion Seleccionada
     *
     * @return objeto para almacenar los datos de la afiliacion seleccionada de
     * la lista
     */
    public IbAfiliacionesDTO getDatosAfiliacionSeleccionada() {
        return datosAfiliacionSeleccionada;
    }

    /**
     * asigna objeto para almacenar los datos de la afiliacion seleccionada de
     * la lista
     *
     * @param datosAfiliacionSeleccionada IbAfiliacionesDTO
     */
    public void setDatosAfiliacionSeleccionada(IbAfiliacionesDTO datosAfiliacionSeleccionada) {
        this.datosAfiliacionSeleccionada = datosAfiliacionSeleccionada;
    }

    /**
     * obtiene titulo de Transferencias
     *
     * @return el titulo de Transferencias
     */
    public String getTituloTransferencias() {
        return tituloTransferencias;
    }

    /**
     * inserta el tituloTransferencias
     *
     * @param tituloTransferencias el titulo de Transferencias
     */
    public void setTituloTransferencias(String tituloTransferencias) {
        this.tituloTransferencias = tituloTransferencias;
    }

    /**
     * obtiene codigo de validacion OTP
     *
     * @return el codigo de validacion OTP
     */
    public String getCodigoOTP() {
        return codigoOTP;
    }

    /**
     * asigna el codigo de validacion OTP
     *
     * @param codigoOTP String
     */
    public void setCodigoOTP(String codigoOTP) {
        this.codigoOTP = codigoOTP;
    }

    /**
     * obtiene la fecha de Transaccion
     *
     * @return String la fecha de Transaccion
     */
    public Date getFechaTransaccion() {
        return new Date();
    }

    /**
     * asigna la fecha de Transaccion
     *
     * @param fechaTransaccion la fecha de Transaccion
     */
    public void setFechaTransaccion(Date fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    /**
     * obtiene el email del Beneficiario
     *
     * @return el email del Beneficiario
     */
    public String getEmailBeneficiario() {
        return emailBeneficiario;
    }

    /**
     * asigna el email del Beneficiario
     *
     * @param emailBeneficiario el email del Beneficiario
     */
    public void setEmailBeneficiario(String emailBeneficiario) {
        this.emailBeneficiario = emailBeneficiario;
    }

    /**
     * Indica sin el flujo de la transaccion incluye el paso de generar y
     * validar el codigo OTP
     *
     * @return boolean
     */
    public boolean flujoSinOTP() {
        if (tipoTransf.equalsIgnoreCase(T_TRANSF_PROPIAS) || tipoTransf.equalsIgnoreCase(T_TRANSF_PROPOTROSBANCOS)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * obtiene el paramCodOTP cuando viene de la pagina de redireccion
     *
     * @return el paramCodOTP cuando viene de la pagina de redireccion
     */
    public String getParamCodOTP() {
        return paramCodOTP;
    }

    /**
     * asigna el paramCodOTP cuando viene de la pagina de redireccion
     *
     * @param paramCodOTP el paramCodOTP cuando viene de la pagina de
     * redireccion
     */
    public void setParamCodOTP(String paramCodOTP) {
        this.paramCodOTP = paramCodOTP;
    }

    /**
     * retorna el indicador para validar que si el OTP fue solicitado
     *
     * @return indicador para validar que si el OTP fue solicitado
     */
    public boolean isOtpGenerado() {
        return otpGenerado;
    }

    /**
     * asigna el indicador para validar que si el OTP fue solicitado
     *
     * @param otpGenerado indicador para validar que si el OTP fue solicitado
     */
    public void setOtpGenerado(boolean otpGenerado) {
        this.otpGenerado = otpGenerado;
    }

    /**
     * metodo que retorna la cuenta de destino de pago enmascarada
     *
     * @return la cuenta de destino de pago enmascarada
     */
    public String getCtaDestinoMascara() {
        if (!tipoTransf.equalsIgnoreCase(T_TRANSF_PROPIAS)) {
            if (datosAfiliacionSeleccionada == null || datosAfiliacionSeleccionada.getSemilla() == null || datosAfiliacionSeleccionada.getSemilla().trim().equalsIgnoreCase("")) {
                this.datosAfiliacionSeleccionada = new IbAfiliacionesDTO(sesionController.getSemilla());
            }
            return formatoAsteriscosWeb(this.datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaDestinoSeleccionada));
        } else {
            if (datosCuentaSeleccionadaActual == null || datosCuentaSeleccionadaActual.getSemilla() == null || datosCuentaSeleccionadaActual.getSemilla().trim().equalsIgnoreCase("")) {
                this.datosCuentaSeleccionadaActual = new CuentaDTO(sesionController.getSemilla());
            }
            return formatoAsteriscosWeb(this.datosCuentaSeleccionadaActual.getDesEnc().desencriptar(cuentaDestinoSeleccionada));
        }
    }

    /**
     * metodo que retorna la cuenta de origen de pago enmascarada
     *
     * @return la cuenta de origen de pago enmascarada
     */
    public String getCtaOrigenMascara() {
        if (datosCuentaSeleccionadaLista == null || datosCuentaSeleccionadaLista.getSemilla() == null || datosCuentaSeleccionadaLista.getSemilla().trim().equalsIgnoreCase("")) {
            this.datosCuentaSeleccionadaLista = new CuentaDTO(sesionController.getSemilla());
        }
        return formatoAsteriscosWeb(this.datosCuentaSeleccionadaLista.getDesEnc().desencriptar(cuentaSeleccionada));
    }

    /**
     *
     * @return obtiene fecha maxima para validacion del calendar
     */
    public Date getCalendarFechaMax() {

        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, 12);
        calendarFechaMax = c.getTime();
        return calendarFechaMax;
    }

    /**
     *
     * @param calendarFechaMax inserta fecha maxima para validacion del calendar
     */
    public void setCalendarFechaMax(Date calendarFechaMax) {
        this.calendarFechaMax = calendarFechaMax;
    }

    /**
     *
     * @return obtiene fecha minima para validacion del calendar
     */
    public Date getCalendarFechaMin() {

        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.DAY_OF_YEAR, 1);
        calendarFechaMin = c.getTime();
        return calendarFechaMin;
    }

    /**
     *
     * @param calendarFechaMin inserta fecha minima para validacion del calendar
     */
    public void setCalendarFechaMin(Date calendarFechaMin) {
        this.calendarFechaMin = calendarFechaMin;
    }

    /**
     *
     * @return obtiene el filtro de fecha Hasta
     */
    public Date getFechaHasta() {
        return fechaHasta;
    }

    /**
     *
     * @param fechaHasta inserta el filtro de fecha Hasta
     */
    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    /**
     * retorna tipo de ejecucion de transferencia U=unico, D=diario, A=agendado
     *
     * @return tipo de ejecucion de transferencia U=unico, D=diario, A=agendado
     */
    public String getTipoEjecTransf() {
        return tipoEjecTransf;
    }

    /**
     * asigna tipo de ejecucion de transferencia U=unico, D=diario, A=agendado
     *
     * @param tipoEjecTransf tipo de ejecucion de transferencia U=unico,
     * D=diario, A=agendado
     */
    public void setTipoEjecTransf(String tipoEjecTransf) {
        this.tipoEjecTransf = tipoEjecTransf;
    }

    public void seleccionarTipoEjecTransf() {
        this.mantenerDatosForm = true;
        this.fechaSeleccionadaString = "";
        if (this.tipoEjecTransf == null || this.tipoEjecTransf.isEmpty()) {

        } else {
            switch (tipoEjecTransf) {
                case ("U"): {
                    this.fechaHasta = null;
                    this.frecSeleccionada = 0;
                    this.fechaFrecSeleccionada = 0;
                    this.fechaFrecuencia = new String[]{"Seleccione"};
                    this.frecuencias = new String[]{"Seleccione"};
                    this.mostrarDiasQuincena = false;
                    this.segundoDiaQuincena = 0;
                    this.mostrarFechaHasta = false;
                    break;
                }
                case ("A"): {
                    this.fechaHasta = null;
                    this.frecSeleccionada = 0;
                    this.fechaFrecSeleccionada = 0;
                    this.fechaFrecuencia = new String[]{"Seleccione una frecuencia"};
                    this.frecuencias = new String[]{"Seleccione una frecuencia", "Semana", "Quincena", "Mes"};
                    this.mostrarFechaHasta = true;
                    break;
                }
            }
        }
    }

    public void seleccionarFechaEjecTransf() {
        this.fechaSeleccionadaString = "";
        if (this.tipoEjecTransf == null || this.tipoEjecTransf.isEmpty()) {
            this.fechaFrecuencia = new String[]{"Seleccione"};
            this.frecuencias = new String[]{"Seleccione"};
        } else {
            switch (tipoEjecTransf) {
                case ("U"): {
                    this.fechaHasta = null;
                    this.frecSeleccionada = 0;
                    this.fechaFrecSeleccionada = 0;
                    this.fechaFrecuencia = new String[]{"Seleccione"};
                    this.frecuencias = new String[]{"Seleccione"};
                    this.mostrarDiasQuincena = false;
                    this.segundoDiaQuincena = 0;
                    this.mostrarFechaHasta = false;
                    break;
                }
                case ("A"): {
                    this.frecSeleccionadaString = this.frecuencias[frecSeleccionada];
                    this.mostrarFechaHasta = true;
                    switch (frecSeleccionada) {
                        case (0): {
                            this.fechaHasta = null;
                            this.frecSeleccionada = 0;
                            this.fechaFrecSeleccionada = 0;
                            this.mostrarDiasQuincena = false;
                            this.segundoDiaQuincena = 0;
                            this.fechaFrecuencia = new String[]{"Seleccione una frecuencia"};
                            break;
                        }
                        case (1): {
                            this.fechaHasta = null;
                            this.fechaFrecSeleccionada = 0;
                            this.mostrarDiasQuincena = false;
                            this.segundoDiaQuincena = 0;
                            this.fechaFrecuencia = new String[]{"Seleccione una fecha", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
                            break;
                        }
                        case (2): {
                            this.fechaHasta = null;
                            this.fechaFrecSeleccionada = 0;
                            this.fechaFrecuencia = new String[]{"Seleccione una fecha", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
                            break;
                        }
                        case (3): {
                            this.fechaHasta = null;
                            this.fechaFrecSeleccionada = 0;
                            this.mostrarDiasQuincena = false;
                            this.segundoDiaQuincena = 0;
                            this.fechaFrecuencia = new String[]{"Seleccione una fecha", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "Último Día"};
                            break;
                        }
                    }

                    break;
                }
            }
        }
    }

    public void seleccionarDiaQuincena() {
        this.fechaSeleccionadaString = fechaFrecuencia[fechaFrecSeleccionada];
        if (this.tipoEjecTransf.equalsIgnoreCase("A") && this.frecSeleccionada == 2) {
            this.mostrarDiasQuincena = true;
            this.segundoDiaQuincena = 15 + this.fechaFrecSeleccionada;
        } else {
            this.mostrarDiasQuincena = false;
            this.segundoDiaQuincena = 0;
        }

    }

    /**
     * retorna los tipos posibles para la frecuencia de las transf
     *
     * @return tipos posibles para la frecuencia de las transf
     */
    public String[] getFrecuencias() {
        return frecuencias;
    }

    /**
     * asigna los tipos posibles para la frecuencia de las transf
     *
     * @param frecuencias tipos posibles para la frecuencia de las transf
     */
    public void setFrecuencias(String[] frecuencias) {
        this.frecuencias = frecuencias;
    }

    /**
     * retorna los valores posibles para los tipos de frecuencia de las transf
     *
     * @return valores posibles para los tipos de frecuencia de las transf
     */
    public String[] getFechaFrecuencia() {
        return fechaFrecuencia;
    }

    /**
     * asigna los valores posibles para los tipos de frecuencia de las transf
     *
     * @param fechaFrecuencia valores posibles para los tipos de frecuencia de
     * las transf
     */
    public void setFechaFrecuencia(String[] fechaFrecuencia) {
        this.fechaFrecuencia = fechaFrecuencia;
    }

    /**
     * retorna la frecSeleccionada 1=semanal, 2= quincenal, 3=mensual
     *
     * @return frecSeleccionada 1=semanal, 2= quincenal, 3=mensual
     */
    public int getFrecSeleccionada() {
        return frecSeleccionada;
    }

    /**
     * asigna la frecSeleccionada 1=semanal, 2= quincenal, 3=mensual
     *
     * @param frecSeleccionada 1=semanal, 2= quincenal, 3=mensual
     */
    public void setFrecSeleccionada(int frecSeleccionada) {
        this.frecSeleccionada = frecSeleccionada;
    }

    /**
     * retorna la fecha de la frecuencia (este valor depende de la
     * frecSeleccionada)
     *
     * @return fecha de la frecuencia (este valor depende de la
     * frecSeleccionada)
     */
    public int getFechaFrecSeleccionada() {
        return fechaFrecSeleccionada;
    }

    /**
     * asigna la fecha de la frecuencia (este valor depende de la
     * frecSeleccionada)
     *
     * @param fechaFrecSeleccionada fecha de la frecuencia (este valor depende
     * de la frecSeleccionada)
     */
    public void setFechaFrecSeleccionada(int fechaFrecSeleccionada) {
        this.fechaFrecSeleccionada = fechaFrecSeleccionada;
    }

    /**
     * retorna la bandera para identificar si se muestra el texto de los dias de
     * quincena a transferir
     *
     * @return bandera para identificar si se muestra el texto de los dias de
     * quincena a transferir
     */
    public boolean isMostrarDiasQuincena() {
        return mostrarDiasQuincena;
    }

    /**
     * asigna la bandera para identificar si se muestra el texto de los dias de
     * quincena a transferir
     *
     * @param mostrarDiasQuincena bandera para identificar si se muestra el
     * texto de los dias de quincena a transferir
     */
    public void setMostrarDiasQuincena(boolean mostrarDiasQuincena) {
        this.mostrarDiasQuincena = mostrarDiasQuincena;
    }

    /**
     * retorna el indicador del segundo dia de la quincena a programar
     *
     * @return el indicador del segundo dia de la quincena a programar
     */
    public int getSegundoDiaQuincena() {
        return segundoDiaQuincena;
    }

    /**
     * asigna la el indicador del segundo dia de la quincena a programar
     *
     * @param segundoDiaQuincena el indicador del segundo dia de la quincena a
     * programar
     */
    public void setSegundoDiaQuincena(int segundoDiaQuincena) {
        this.segundoDiaQuincena = segundoDiaQuincena;
    }

    /**
     * retorna la bandera para identificar si se muestra el calendario de la
     * fecha hasta de agendar transf
     *
     * @return bandera para identificar si se muestra el calendario de la fecha
     * hasta de agendar transf
     */
    public boolean isMostrarFechaHasta() {
        return mostrarFechaHasta;
    }

    /**
     * asigna la bandera para identificar si se muestra el calendario de la
     * fecha hasta de agendar transf
     *
     * @param mostrarFechaHasta bandera para identificar si se muestra el
     * calendario de la fecha hasta de agendar transf
     */
    public void setMostrarFechaHasta(boolean mostrarFechaHasta) {
        this.mostrarFechaHasta = mostrarFechaHasta;
    }

    /**
     * retorna la descripcion de la fecha seleccionada
     *
     * @return descripcion de la fecha seleccionada
     */
    public String getFechaSeleccionadaString() {
        return fechaSeleccionadaString;
    }

    /**
     * asigna la descripcion de la fecha seleccionada
     *
     * @param fechaSeleccionadaString descripcion de la fecha seleccionada
     */
    public void setFechaSeleccionadaString(String fechaSeleccionadaString) {
        this.fechaSeleccionadaString = fechaSeleccionadaString;
    }

    /**
     * retorna la descripcion de la frecuencia seleccionada
     *
     * @return descripcion de la frecuencia seleccionada
     */
    public String getFrecSeleccionadaString() {
        return frecSeleccionadaString;
    }

    /**
     * asigna la descripcion de la frecuencia seleccionada
     *
     * @param frecSeleccionadaString descripcion de la frecuencia seleccionada
     */
    public void setFrecSeleccionadaString(String frecSeleccionadaString) {
        this.frecSeleccionadaString = frecSeleccionadaString;
    }

    /**
     * retorna el indicador para mantener campos en formulario de transferencias
     *
     * @return el indicador para mantener campos en formulario de transferencias
     */
    public boolean isMantenerDatosForm() {
        return mantenerDatosForm;
    }

    /**
     * asigna el indicador para mantener campos en formulario de transferencias
     *
     * @param mantenerDatosForm el indicador para mantener campos en formulario
     * de transferencias
     */
    public void setMantenerDatosForm(boolean mantenerDatosForm) {
        this.mantenerDatosForm = mantenerDatosForm;
    }

    public void regresarPaso1() {
        this.redirectFacesContext("/sec/noticias/noticias.xhtml?faces-redirect=true");
        //this.redirectFacesContext(parametrosController.getNombreParametro("pnw.otp.url.validacionOtp", sesionController.getIdCanal()));
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoPaso2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() == 1
                    && (sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_PROPIAS_DELSUR)
                    || sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_P_OTROS_BCOS))) {
                //incrementamos el validador de flujo ya que estos modulos no tienen OTP
                sesionController.setValidadorFlujo(2);
            }
            if (sesionController.getValidadorFlujo() != 2
                    || (!sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_PROPIAS_DELSUR)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_T_OTROS_BCOS)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_TERC_DELSUR)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_P_OTROS_BCOS))) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoConfirPaso3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3
                    || (!sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_PROPIAS_DELSUR)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_T_OTROS_BCOS)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_TERC_DELSUR)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_P_OTROS_BCOS))) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoEjecutarPaso4() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 4
                    || (!sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_PROPIAS_DELSUR)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_T_OTROS_BCOS)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_TERC_DELSUR)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CTAS_P_OTROS_BCOS))) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    ////////////////////SECCION  DE REPORTES//////////////////////////
    /**
     * metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoTransferenciasPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 11, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //Variables para el manejo de la data
            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            String nombreBeneficiario;
            String nombreBanco;
            String emailBeneficiarioPDF;
            String ctaAbonar;

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(TransferenciaController.class.getResource("/imgPDF/logoBanner.png"));

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

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas0 = {1.95f, 3.05f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas0);
            //    table.getDefaultCell().setBorder(0);

            //    cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));
            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.transferencia.descargaPdf.titulo", sesionController.getIdCanal()) + " " + sesionController.getNombreTransaccion(), fontTituloBold)));

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setPaddingBottom(20f);
            table.addCell(cell);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            pdf.add(new Paragraph(" "));

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(null);
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);
            table.setWidthPercentage(70f);

            //Títulos de las Columnas
            if (this.tipoEjecTransf.equalsIgnoreCase("U") && this.transaccionExitosa()) {
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.transferencia.descargaPdf.nroConfirmacion", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(this.numConfirmacion, font));
            }

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            if (this.tipoEjecTransf.equalsIgnoreCase("U")) {
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.transferencia.descargaPdf.fecTransaccion", sesionController.getIdCanal()), fontBold));
            } else {
                if (this.tipoEjecTransf.equalsIgnoreCase("A")) {
                    table.addCell(new Phrase(textosController.getNombreTexto("pnw.transferencia.descargaPdf.fechaAgendada", sesionController.getIdCanal()), fontBold));
                }
            }
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(fecha2(new Date()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.transferencia.descargaPdf.ctaDebitar", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(formatoAsteriscosWeb(this.datosCuentaSeleccionadaLista.getDesEnc().desencriptar(cuentaSeleccionada)), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.transferencia.descargaPdf.ctaAbonar", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));

            if (tipoTransf.equalsIgnoreCase("TPDS")) {
                ctaAbonar = this.datosCuentaSeleccionadaActual.getDesEnc().desencriptar(cuentaDestinoSeleccionada);
            } else {
                ctaAbonar = this.datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaDestinoSeleccionada);
            }

            table.addCell(new Phrase(formatoAsteriscosWeb(ctaAbonar), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.transferencia.descargaPdf.nomBeneficiario", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));

            if (tipoTransf.equalsIgnoreCase("TPDS")) {
                nombreBeneficiario = sesionController.getUsuario().getNombre();
            } else {
                nombreBeneficiario = this.datosAfiliacionSeleccionada.getNombreBeneficiario();
            }
            table.addCell(new Phrase(nombreBeneficiario, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.transferencia.descargaPdf.bancoDestino", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));

            if (tipoTransf.equalsIgnoreCase("TPDS")) {
                nombreBanco = textosController.getNombreTexto("pnw.global.texto.banco", sesionController.getIdCanal());
            } else {
                nombreBanco = this.datosAfiliacionSeleccionada.getNombreBanco();
            }

            table.addCell(new Phrase(nombreBanco, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.transferencia.descargaPdf.monto", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase((this.monto != null ? parametrosController.getNombreParametro("pnw.global.tipoMoneda.Bs", sesionController.getIdCanal()) + " " + this.monto : " - "), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.transferencia.descargaPdf.motivo", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.motivo, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.transferencia.descargaPdf.email", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));

            if (tipoTransf.equalsIgnoreCase("TPDS")) {
                emailBeneficiarioPDF = sesionController.getUsuario().getEmail();
            } else {
                emailBeneficiarioPDF = this.datosAfiliacionSeleccionada.getEmail();
            }

            table.addCell(new Phrase(emailBeneficiarioPDF, font));

            if (this.tipoEjecTransf.equalsIgnoreCase("A")) {

                table.getDefaultCell().setColspan(2);
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.transferencia.descargaPdf.tipoEjec", sesionController.getIdCanal()), fontBold));

                String instruccion = textosController.getNombreTexto("pnw.transferencia.descargaPdf.agendaPeriodica", sesionController.getIdCanal())
                        + fechaSeleccionadaString;

                if (frecSeleccionada == 2) {
                    instruccion = instruccion + " y el día " + this.segundoDiaQuincena;
                }

                instruccion = instruccion + ", Hasta el " + formatearFecha(this.fechaHasta, FORMATO_FECHA_SIMPLE);

                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(instruccion, font));

                table.getDefaultCell().setColspan(0);
            }

            //Agregamos la tabla al PDF
            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            //Añadimos la tabla al PDF
            // pdf.setMargins(150f, 20f, 50f, 10f);
            pdf.setMargins(50f, 3f, 22f, 18f);
            pdf.add(new Paragraph(" "));
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
    public void detalleTransferenciasPDF() throws IOException, DocumentException {

        try {

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-disposition", "attachment;filename=\"Transferencias.pdf\"");
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
            this.cuerpoTransferenciasPDF(document);

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
    
    public boolean isValidaCierreOperaciones() {
        
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date horaActualSistema = new Date();
        Calendar calendarHoraInicio = Calendar.getInstance();
        Calendar calendarHoraCierre = Calendar.getInstance();
        Date horaInicioCierre, horaFinCierre;
        
        try {
            horaInicioCierre = dateFormat.parse(sesionController.getHoraInicio());
            horaFinCierre = dateFormat.parse(sesionController.getHoraFin());

            calendarHoraInicio.setTime(horaInicioCierre);
            calendarHoraCierre.setTime(horaFinCierre);

            if (calendarHoraInicio.DAY_OF_YEAR != calendarHoraCierre.DAY_OF_YEAR) {
                calendarHoraCierre.add(calendarHoraCierre.DAY_OF_YEAR, 1);
            }

            String horaActualFormateada = dateFormat.format(horaActualSistema);

            if ((calendarHoraInicio.getTime().compareTo(dateFormat.parse(horaActualFormateada)) <= 0)
                    && (calendarHoraCierre.getTime().compareTo(dateFormat.parse(horaActualFormateada)) >= 0)) {
                validaCierreOperaciones = false;
            } else {
                validaCierreOperaciones = true;
            }
        } catch (ParseException parseException) {

        }
        return validaCierreOperaciones;
    }

    public boolean isMostrarMensajeCierre() {
        if(validaCierreOperaciones == false){
            mostrarMensajeCierre = true;
        }
        return mostrarMensajeCierre;
    }
}
