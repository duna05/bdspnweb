/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.CuentaDAO;
import com.bds.wpn.dao.IBAgendaTransaccionesDAO;
import com.bds.wpn.dao.IbAfiliacionDAO;
import com.bds.wpn.dao.NotificacionesDAO;
import com.bds.wpn.dao.TarjetaCreditoDAO;
import com.bds.wpn.dao.TranferenciasYPagosDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.IBAgendaTransaccionDTO;
import com.bds.wpn.dto.IbAfiliacionesDTO;
import com.bds.wpn.dto.IbCanalDTO;
import com.bds.wpn.dto.IbParametrosDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.TarjetasCreditoDTO;
import com.bds.wpn.dto.TransaccionDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbCanalFacade;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_DATA_INVALIDA;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.FORMATO_FECHA_COMPLETA;
import static com.bds.wpn.util.BDSUtil.NUEVALINEAEMAIL;
import static com.bds.wpn.util.BDSUtil.eliminarformatoSimpleMonto;
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
import java.math.BigInteger;
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
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wilmer.rondon
 */
@Named("wpnPagosTDCController")
@SessionScoped
public class PagosTDCController extends BDSUtil implements Serializable {

    @EJB
    IbCanalFacade canalFacade;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbAfiliacionDAO afiliacionDAO;

    @EJB
    CuentaDAO cuentaDAO;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    TarjetaCreditoDAO tarjetaCreditoDAO;

    @EJB
    NotificacionesDAO notificacionesDAO;

    @EJB
    TranferenciasYPagosDAO tranferenciasYPagosDAO;

    @EJB
    IBAgendaTransaccionesDAO agendaTransaccionesDAO;

    @Inject
    InicioSesionController sesionController;

    @Inject
    private TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    @Inject
    CanalController canalController;

    private String paramCodOTP;

    private boolean mostrarMontoCuotaPago = false;           //Atributo que indica si se muestra el elemento de montos de cuotas de pago
    private String montoMin = "";                        //Monto minimo a pagar extraido del formulario de pagos
    private String montoTotal = "";                      //Monto total a pagar extraido del formulario de pagos    
    private String radioMontoaPagar = "";                    //Monto a pagar extraido del formulario de pagos
    private boolean mostrarRadioMonto = false;               //Atributo que indica si se muestra el elemento de otro monto    
    private String monto = "";                      //Monto extraido del formulario de transferencias
    private String motivo = "";                         //Motivo extraido del formulario de transferencias
    private String cuentaSeleccionada = "";             //Cuenta del Titular a Debitar Seleccionada extraido del formulario de transferencias
    private String tdcDestinoSeleccionada = "";      //TDC Destino a Acreditar extraido del formulario de transferencias
    private String numConfirmacion = "";     //Numero de confirmacion de la operacion realizada
    private String mensajeNull = "";                    //Mensaje por defecto para los campos vacios o en null
    private String tipoPago = "";                     //Atributo para identificar el tipo de transferencia P - propias, TOB - terceros otros bancos, TBDS - terceros banco del sur
    private Collection<IbAfiliacionesDTO> afiliaciones;             //objeto que contiene las afiliaciones para el cliente
    private Collection<CuentaDTO> cuentasCliente;                   //objeto que contiene las cuentas propias
    private Collection<TarjetasCreditoDTO> tdcCliente;              //objeto que contiene las TDC propias
    private CuentaDTO datosCuentaSeleccionadaLista = null;          //objeto para almacenar los datos de la cuenta seleccionada de la lista
    private CuentaDTO datosCuentaSeleccionadaActual = null;         //objeto para almacenar la coonsulta de los datos de la Cuenta seleccionada
    private IbAfiliacionesDTO datosAfiliacionSeleccionada = null;   //objeto para almacenar los datos de la afiliacion seleccionada de la lista
    private TarjetasCreditoDTO datosTDCSeleccionada = null;         //objeto para almacenar los datos de la TDC seleccionada de la lista
    private String codigoOTP = "";                                  //codigo de validacion OTP    
    private Date fechaTransaccion;
    private String emailBeneficiario = "";
    private String errorConfirmacion = "";
    private String formPagosTDC = "";
    private BigDecimal montoAbsoluto = new BigDecimal(BigInteger.ZERO);
    private BigDecimal montoAbsolutoTotal = new BigDecimal(BigInteger.ZERO);
    private BigDecimal montoAbsolutoMinimo = new BigDecimal(BigInteger.ZERO);
    private boolean tieneAfiliaciones = false;                      //indicador de posesion de afiliaciones para un tipo determinado
    private boolean otpGenerado = false;                            //indicador para validar que si el OTP fue solicitado
    private Date fechaHasta;                                                    //fecha tope de agenda de transferencia
    private boolean mostrarFechaHasta;                                          //bandera para identificar si se muestra el calendario de la fecha hasta de agendar transf
    private Date calendarFechaMin;                                              //fecha minima para validacion del calendar
    private Date calendarFechaMax;                                              //fecha maxima para validacion del calendar
    private String tipoEjecPagTDC = "U";                                        //tipo de ejecucion de transferencia U=unico, D=diario, A=agendado
    private int frecSeleccionada;                                               //1=semanal, 2= quincenal, 3=mensual
    private int fechaFrecSeleccionada;                                          //fecha de la frecuencia (este valor depende de la frecSeleccionada)
    private String fechaSeleccionadaString = "";                                //descripcion de la fecha seleccionada
    private String frecSeleccionadaString = "";                                 //descripcion de la frecuencia seleccionada
    private String[] frecuencias = new String[]{"Seleccione"};                  //tipos posibles para la frecuencia de las transf
    private String[] fechaFrecuencia = new String[]{"Seleccione"};              //valores posibles para los tipos de frecuencia de las transf
    private boolean mostrarDiasQuincena;                                        //bandera para identificar si se muestra el texto de los dias de quincena a transferir
    private int segundoDiaQuincena;                                             //indicador del segundo dia de la quincena a programar
    private boolean mantenerDatosForm = false;                                  //indicador para mantener campos en formulario de transferencias
    private boolean horarioHabilitado = true;
    private boolean validaCierreOperaciones = true;
    private boolean mostrarMensajeCierre = false;

    /**
     * metodo encargado de limpiar el formulario de Pagos desde el menu
     */
    public void iniciarPagos() {
        if (sesionController.isReiniciarForm()) {
            if (!FacesContext.getCurrentInstance().isPostback()) {
                this.mantenerDatosForm = false;
                this.limpiarAfiliaciones();
                sesionController.setReiniciarForm(false);
            }
        }
    }

    /**
     * Asigna el formulario para cada transferencia
     */
    public void setearFormulario() {
        switch (this.tipoPago) {
            case T_PAG_TDC_PROPIAS: {
                formPagosTDC = "formPDS";
                break;
            }
            case T_PAG_TDC_OTROSBANCOS: {
                formPagosTDC = "formTOB";
                break;
            }
            case T_PAG_TDC_3ROSDELSUR: {
                formPagosTDC = "formTDS";
                break;
            }
            case T_PAG_TDC_PROPOTROSBANCOS: {
                formPagosTDC = "formPOB";
                break;
            }
        }
    }

    /**
     * Metodo para cargar las listas iniciales para TOB
     */
    public void consultaInicialTOB() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.limpiarAfiliaciones();
            this.tieneAfiliaciones = false;
            tipoPago = T_PAG_TDC_OTROSBANCOS;
            setearFormulario();
            consultarCuentasPropias();
            consultarAfiliacionesTercOtrosBcos();
            if (afiliaciones.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.sinAfiliaciones", sesionController.getIdCanal())));
            }
        }
    }

    /**
     * Metodo para cargar las listas iniciales para TDS
     */
    public void consultaInicialTDS() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.limpiarAfiliaciones();
            this.tieneAfiliaciones = false;
            tipoPago = T_PAG_TDC_3ROSDELSUR;
            setearFormulario();
            consultarCuentasPropias();
            consultarAfiliacionesTercDelSur();
            if (afiliaciones.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.sinAfiliaciones", sesionController.getIdCanal())));
            }
        }
    }

    /**
     * Metodo para cargar las listas iniciales para POB
     */
    public void consultaInicialPOB() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.limpiarAfiliaciones();
            this.tieneAfiliaciones = false;
            this.tieneAfiliaciones = false;
            tipoPago = T_PAG_TDC_PROPOTROSBANCOS;
            setearFormulario();
            consultarCuentasPropias();
            consultarAfiliacionesPOB();
            if (afiliaciones.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.sinAfiliaciones", sesionController.getIdCanal())));
            }
            if (this.afiliaciones.size() > 0) {
                this.tieneAfiliaciones = true;
            }
        }
    }

    /**
     * Metodo para cargar las listas iniciales para Propias
     */
    public void consultaInicialPropias() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.limpiarAfiliaciones();
            this.tieneAfiliaciones = false;
            tipoPago = T_PAG_TDC_PROPIAS;
            setearFormulario();
            consultarCuentasPropias();
            consultarTDCPropias();
            if (tdcCliente.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.sinAfiliaciones", sesionController.getIdCanal())));
            }
            if (this.tdcCliente.size() > 0) {
                this.tieneAfiliaciones = true;
            }
        }
    }

    /**
     * Consulta TDC propias
     */
    public void consultarTDCPropias() {
        //se limpian las afiliaciones 
        this.tdcCliente = new ArrayList<>();
        ClienteDTO datosCliente = tarjetaCreditoDAO.listadoTdcPropias(sesionController.getUsuario().getDocumento(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        //se valida el objeto de respuesta
        if (!datosCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //manejar mensaje de error 
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtCtaAbonar", fMsg);
        } else {
            //se consultan las tdc propias para dicha transaccion
            if (datosCliente.getTdcAsociadasClienteDTO().size() > 0) {
                this.tieneAfiliaciones = true;
                if (!datosCliente.getTdcAsociadasClienteDTO().isEmpty()) {
                    for (int i = 0; i < datosCliente.getTdcAsociadasClienteDTO().size(); i++) {

                        //por cada TDC valida del cliente se consulta su detalle 
                        datosCliente.getTdcAsociadasClienteDTO().set(i, tarjetaCreditoDAO.obtenerDetalleTDC(datosCliente.getTdcAsociadasClienteDTO().get(i).getNumeroTarjeta(), sesionController.getIdCanal(), sesionController.getNombreCanal()));
                        datosCliente.getTdcAsociadasClienteDTO().get(i).setSemilla(sesionController.getSemilla());
                    }
                }
                this.tdcCliente = datosCliente.getTdcAsociadasClienteDTO();
            }
            //manejar un mensaje para el caso de no poseer afiliaciones registradas
        }

    }

    /**
     * metodo que se encarga de consultar las afiliaciones por el tipo de
     * transaccion definido
     */
    public void consultarAfiliacionesTercOtrosBcos() {
        tipoPago = T_PAG_TDC_OTROSBANCOS;
        this.tieneAfiliaciones = false;
        //se limpian las afiliaciones
        this.afiliaciones = new ArrayList<>();
        IbAfiliacionesDTO afiliacionCliente = afiliacionDAO.obtenerListadoAfiliadosPorOperacion(sesionController.getUsuario().getId().toString(),
                ID_TRANS_TDC_T_OTROS_BCOS,
                sesionController.getIdCanal(),
                sesionController.getNombreCanal());
        if (!afiliacionCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //manejar mensaje de error     
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtCtaAbonar", fMsg);
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
        tipoPago = T_PAG_TDC_3ROSDELSUR;
        this.tieneAfiliaciones = false;
        //se limpian las afiliaciones
        this.afiliaciones = new ArrayList<>();
        IbAfiliacionesDTO afiliacionCliente = afiliacionDAO.obtenerListadoAfiliadosPorOperacion(sesionController.getUsuario().getId().toString(),
                ID_TRANS_TDC_TERC_DELSUR,
                sesionController.getIdCanal(),
                sesionController.getNombreCanal());
        if (!afiliacionCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //manejar mensaje de error 
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtCtaAbonar", fMsg);
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
        tipoPago = T_PAG_TDC_PROPOTROSBANCOS;
        this.tieneAfiliaciones = false;
        //se limpian las afiliaciones
        this.afiliaciones = new ArrayList<>();
        IbAfiliacionesDTO afiliacionCliente = afiliacionDAO.obtenerListadoAfiliadosPorOperacion(sesionController.getUsuario().getId().toString(),
                ID_TRANS_TDC_P_OTROS_BCOS,
                sesionController.getIdCanal(),
                sesionController.getNombreCanal());
        if (!afiliacionCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //manejar mensaje de error 
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtCtaAbonar", fMsg);
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
     * metodo utilitario para reiniciar los valores de transferencias
     */
    public void limpiarAfiliaciones() {
        if (!this.mantenerDatosForm) {
            this.cuentasCliente = new ArrayList<>();
            this.tdcCliente = new ArrayList<>();
            this.tdcDestinoSeleccionada = "-1";
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
            this.tipoEjecPagTDC = "U";
            this.mostrarMontoCuotaPago = false;
            this.mostrarRadioMonto = false;
            this.radioMontoaPagar = null;
            this.seleccionarTipoEjecPagTDC();
        }
    }

    /**
     * Metodo para consultar las cuentas propias
     */
    public void consultarCuentasPropias() {

        ClienteDTO datosCliente = clienteDAO.listadoCuentasClientes(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (!datosCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //manejar mensaje de error 
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtCtaDebitar", fMsg);
        } else {
            if (datosCliente.getCuentasAhorroCorrienteDTO().size() > 0) {
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
     * metodo para redirigir a paso 1 de afiliar
     */
    public String confirPaso3() {
        sesionController.setTextoStatus("pnw.modal.texto.esperaTransaccion");
        int validaciones = this.validarPagosTDC();
        if (validaciones == 0) {
            if (tipoEjecPagTDC.equalsIgnoreCase("A")) {
                if (this.frecSeleccionada == 0 || this.fechaFrecSeleccionada == 0 || this.fechaHasta == null) {
                    FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":rowAgendarTransf", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.transf.msj.agendarTrnsf", sesionController.getIdCanal())));
                    return "";
                }
            }
            sesionController.setValidadorFlujo(3);
            return "/sec/pagosTDC/confirPaso3.xhtml?faces-redirect=true";
            //return parametrosController.getNombreParametro("pnw.pagosTDC.url.confirPaso3", sesionController.getIdCanal());
        } else {
            return "";
        }
    }

    /**
     * Metodo que se encarga de realizar las validaciones de negocio de los
     * pagos de TDC
     *
     * @return int
     */
    public int validarPagosTDC() {

        FacesMessage fMsg;
        int errores = 0;

        if (new BigDecimal(eliminarformatoSimpleMonto(this.monto)).signum() < 0) {
            montoAbsoluto = new BigDecimal(eliminarformatoSimpleMonto(this.monto)).negate();
        } else {
            montoAbsoluto = new BigDecimal(eliminarformatoSimpleMonto(this.monto));
        }

        //validacion de limites
        switch (this.tipoPago) {
            case T_PAG_TDC_PROPIAS: {
                //Propias del Sur, Límite del Usuario por Canal
                if (sesionController.getUsuarioCanal().getLimiteInternas().compareTo(montoAbsoluto) < 0) {
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                            textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                    FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":radioMonto", fMsg);
                    errores++;
                }

                //Se consulta el límite del banco
                IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.propias", sesionController.getIdCanal());

                //Propias del Sur, Límite del banco
                if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(montoAbsoluto) < 0) {
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPDS", sesionController.getNombreCanal()).getMensajeTecnico(),
                            textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPDS", sesionController.getNombreCanal()).getMensajeUsuario());
                    FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":radioMonto", fMsg);//LÍMITE DEL BANCO
                    errores++;
                }

                // Monto a pagar mayor a deuda total de las tdc
                if (Double.parseDouble(getMontoTotal().replace(".", "").replace(",", ".")) < Double.valueOf(montoAbsoluto.toString())) {
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("mtosuperior", sesionController.getNombreCanal()).getMensajeTecnico(),
                            textosFacade.findByCodigo("mtosuperior", sesionController.getNombreCanal()).getMensajeUsuario());
                    FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":radioMonto", fMsg);
                    errores++;
                }
                break;
            }
            case T_PAG_TDC_OTROSBANCOS: {//Tipo de pago tdc terceros a otros bancos
                //Terceros Otros Bancos, Límite de Afiliación
                if (datosAfiliacionSeleccionada.getMontoMaximo() != null && datosAfiliacionSeleccionada.getMontoMaximo().compareTo(BigDecimal.ZERO) > 0) {
                    if (datosAfiliacionSeleccionada.getMontoMaximo().compareTo(montoAbsoluto) < 0) {
                        fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteAfiliacionInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                textosFacade.findByCodigo("pnw.global.texto.mtoLimiteAfiliacionInv", sesionController.getNombreCanal()).getMensajeUsuario());
                        FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);//El monto ingresado supera el límite configurado para esta afiliación
                        errores++;
                    }

                    //Terceros Otros Bancos, Límites del Usuario por Canal
                    if (sesionController.getUsuarioCanal().getLimiteExternasTerceros().compareTo(montoAbsoluto) < 0) {
                        fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                        FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                        errores++;
                    }

                    //Se consulta el límite del banco  
                    IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.otrosbancos", sesionController.getIdCanal());

                    //Terceros Otros Bancos, Límite del Banco
                    if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(montoAbsoluto) < 0) {
                        fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTOB", sesionController.getNombreCanal()).getMensajeTecnico(),
                                textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTOB", sesionController.getNombreCanal()).getMensajeUsuario());
                        FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);//LÍMITE DEL BANCO
                        errores++;
                    }
                } else {
                    if (datosAfiliacionSeleccionada.getMontoMaximo() != null && datosAfiliacionSeleccionada.getMontoMaximo().compareTo(BigDecimal.ZERO) == 0) {
//                        COMENTADO TEMPORALMENTE MIENTRAS SE LE INDICAN A LOS NUEVOS CLIENTES 
//                        QUE DEBEN SETEAR LOS LIMITES DE LAS AFILIACIONES MIGRADAS MIRNA 14-07-2017

                        //Terceros Otros Bancos, Límites del Usuario por Canal
                        if (sesionController.getUsuarioCanal().getLimiteExternasTerceros().compareTo(montoAbsoluto) < 0) {
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                            errores++;
                        }

                        //Se consulta el límite del banco  
                        IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.otrosbancos", sesionController.getIdCanal());

                        //Terceros Otros Bancos, Límite del Banco
                        if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(montoAbsoluto) < 0) {
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTOB", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTOB", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);//LÍMITE DEL BANCO
                            errores++;
                        }
                    }
                }
                break;

            }
            case T_PAG_TDC_3ROSDELSUR: {//Tipo de pago tdc a terceros del banco del sur
                //Terceros del Sur, Límite de Afiliación
                if (datosAfiliacionSeleccionada.getMontoMaximo() != null && datosAfiliacionSeleccionada.getMontoMaximo().compareTo(BigDecimal.ZERO) > 0) {
                    if (datosAfiliacionSeleccionada.getMontoMaximo().compareTo(montoAbsoluto) < 0) {
                        fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteAfiliacionInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                textosFacade.findByCodigo("pnw.global.texto.mtoLimiteAfiliacionInv", sesionController.getNombreCanal()).getMensajeUsuario());
                        FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);
                        errores++;
                    }

                    //Terceros del Sur, Límite del Usuario por Canal
                    if (sesionController.getUsuarioCanal().getLimiteInternasTerceros().compareTo(montoAbsoluto) < 0) {
                        fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                        FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);
                        errores++;
                    }

                    //Se consulta el límite del banco
                    IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.tercerosdelsur", sesionController.getIdCanal());

                    //Terceros del Sur, Límite del Banco
                    if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(montoAbsoluto) < 0) {
                        fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTDS", sesionController.getNombreCanal()).getMensajeTecnico(),
                                textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTDS", sesionController.getNombreCanal()).getMensajeUsuario());
                        FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);//LÍMITE DEL BANCO
                        errores++;
                    }
                } else {
                    if (datosAfiliacionSeleccionada.getMontoMaximo() != null && datosAfiliacionSeleccionada.getMontoMaximo().compareTo(BigDecimal.ZERO) == 0) {
                        //                        COMENTADO TEMPORALMENTE MIENTRAS SE LE INDICAN A LOS NUEVOS CLIENTES 
//                        QUE DEBEN SETEAR LOS LIMITES DE LAS AFILIACIONES MIGRADAS MIRNA 14-07-2017

                        //Terceros del Sur, Límite del Usuario por Canal
                        if (sesionController.getUsuarioCanal().getLimiteInternasTerceros().compareTo(montoAbsoluto) < 0) {
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);
                            errores++;
                        }

                        //Se consulta el límite del banco
                        IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.tercerosdelsur", sesionController.getIdCanal());

                        //Terceros del Sur, Límite del Banco
                        if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(montoAbsoluto) < 0) {
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTDS", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTDS", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);//LÍMITE DEL BANCO
                            errores++;
                        }
                    }
                }

                break;

            }
            case T_PAG_TDC_PROPOTROSBANCOS: {
                //Propias Otros Bancos, Límite Afiliación
                if (datosAfiliacionSeleccionada.getMontoMaximo() != null && datosAfiliacionSeleccionada.getMontoMaximo().compareTo(BigDecimal.ZERO) > 0) {
                    if (datosAfiliacionSeleccionada.getMontoMaximo().compareTo(montoAbsoluto) < 0) {
                        fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteAfiliacionInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                textosFacade.findByCodigo("pnw.global.texto.mtoLimiteAfiliacionInv", sesionController.getNombreCanal()).getMensajeUsuario());
                        FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);
                        errores++;
                    }

                    //Propias Otros Bancos, Límite del Usuario por Canal
                    if (sesionController.getUsuarioCanal().getLimiteExternas().compareTo(montoAbsoluto) < 0) {
                        fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                        FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);
                        errores++;
                    }

                    //Se consulta el límite del banco
                    IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.propiasotrosbancos", sesionController.getIdCanal());

                    //Propias Otros Bancos, Límite del Banco
                    if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(montoAbsoluto) < 0) {
                        fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPOB", sesionController.getNombreCanal()).getMensajeTecnico(),
                                textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPOB", sesionController.getNombreCanal()).getMensajeUsuario());
                        FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);//LÍMITE DEL BANCO
                        errores++;
                    }
                } else {
                    if (datosAfiliacionSeleccionada.getMontoMaximo() != null && datosAfiliacionSeleccionada.getMontoMaximo().compareTo(BigDecimal.ZERO) == 0) {
                        //                        COMENTADO TEMPORALMENTE MIENTRAS SE LE INDICAN A LOS NUEVOS CLIENTES 
//                        QUE DEBEN SETEAR LOS LIMITES DE LAS AFILIACIONES MIGRADAS MIRNA 14-07-2017

                        //Propias Otros Bancos, Límite del Usuario por Canal
                        if (sesionController.getUsuarioCanal().getLimiteExternas().compareTo(montoAbsoluto) < 0) {
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);
                            errores++;
                        }

                        //Se consulta el límite del banco
                        IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.propiasotrosbancos", sesionController.getIdCanal());

                        //Propias Otros Bancos, Límite del Banco
                        if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(montoAbsoluto) < 0) {
                            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPOB", sesionController.getNombreCanal()).getMensajeTecnico(),
                                    textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPOB", sesionController.getNombreCanal()).getMensajeUsuario());
                            FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);//LÍMITE DEL BANCO
                            errores++;
                        }
                    }
                }
                break;

            }
        }

        //monto superior a 1 Bolivar
        if (Double.valueOf(montoAbsoluto.toString()) < Double.valueOf("1.01")) {
            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.mtoInv", sesionController.getNombreCanal()).getMensajeUsuario());
            if (this.tipoPago.equalsIgnoreCase(T_PAG_TDC_PROPIAS)) {
                FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":radioMonto", fMsg);
            } else {
                FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);
            }
            errores++;
        }
        //monto menor que saldo disponible se valida contra el saldo en tiempo real 
        this.datosCuentaSeleccionadaActual = cuentaDAO.obtenerDetalleCuenta(datosCuentaSeleccionadaLista.getCodigoTipoProducto(),
                datosCuentaSeleccionadaLista.getNumeroCuenta(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (this.datosCuentaSeleccionadaActual.getRespuestaDTO() == null || this.datosCuentaSeleccionadaActual.getRespuestaDTO().getCodigo() == null || this.datosCuentaSeleccionadaActual.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_EXCEPCION_GENERICA)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            errores++;
        } else {
            if (this.datosCuentaSeleccionadaActual.getSaldoDisponible().compareTo(montoAbsoluto) < 0) {
                fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.saldoInsuficiente", sesionController.getNombreCanal()).getMensajeTecnico(),
                        textosFacade.findByCodigo("pnw.global.texto.saldoInsuficiente", sesionController.getNombreCanal()).getMensajeUsuario());
                if (this.tipoPago.equalsIgnoreCase(T_PAG_TDC_PROPIAS)) {
                    FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":radioMonto", fMsg);
                } else {
                    FacesContext.getCurrentInstance().addMessage(formPagosTDC + ":txtMonto", fMsg);
                }
                errores++;
            }
        }
        return errores;
    }

    /**
     * Metodo para validar el campo tipo de pago del formulario de pagos.
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarRadio(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (datosTDCSeleccionada.getSaldoAlCorte() != null) {
            if (datosTDCSeleccionada.getSaldoAlCorte().signum() < 0) {
                montoAbsolutoTotal = datosTDCSeleccionada.getSaldoAlCorte().negate();
            } else {
                montoAbsolutoTotal = datosTDCSeleccionada.getSaldoAlCorte();
            }
        }

        if (datosTDCSeleccionada.getPagoMinimo() != null) {
            if (datosTDCSeleccionada.getPagoMinimo().signum() < 0) {
                montoAbsolutoMinimo = datosTDCSeleccionada.getPagoMinimo().negate();
            } else {
                montoAbsolutoMinimo = datosTDCSeleccionada.getPagoMinimo();
            }
        }

        if (String.valueOf(value).equalsIgnoreCase("pagoTotal")) {
            monto = this.formatearMonto(montoAbsolutoTotal);
        } else if (String.valueOf(value).equalsIgnoreCase("pagoMin")) {
            monto = this.formatearMonto(montoAbsolutoMinimo);
        }
        if (value == null || value.toString().isEmpty() || value.toString().equalsIgnoreCase("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("campoRequerido", sesionController.getIdCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("campoRequerido", sesionController.getIdCanal()).getMensajeUsuario()));
        }
    }

    /**
     * Metodo que se sencarga de retornar los datos completos de una cuenta
     * destino
     *
     * @param nroCuenta String
     * @return CuentaDTO
     */
    public CuentaDTO tdcDestinoSeleccionadaCompleta(String nroCuenta) {
        CuentaDTO cuentaTemp = new CuentaDTO();
        for (CuentaDTO cuenta : cuentasCliente) {
            if (cuenta.getNumeroCuenta().equals(nroCuenta)) {
                cuentaTemp = cuenta;
                break;
            }
        }
        return cuentaTemp;
    }

    /**
     * Metodo que se sencarga de retornar los datos completos de una TDC PROPIA
     * destino
     *
     * @param nroTDC String
     * @return TarjetasCreditoDTO
     */
    public TarjetasCreditoDTO tdcPropiaSeleccionadaCompleta(String nroTDC) {
        TarjetasCreditoDTO tdcTemp = new TarjetasCreditoDTO();
        for (TarjetasCreditoDTO tdc : tdcCliente) {
            if (tdc.getNumeroTarjeta().equals(nroTDC)) {
                tdcTemp = tdc;
                break;
            }
        }
        return tdcTemp;
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
     * metodo para validar la cuenta propia seleccionada
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarTDCDestino(FacesContext context, UIComponent component, Object value) throws ValidatorException {
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
    public void validarCtaPropia(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().isEmpty() || value.equals("") || value.equals("-1")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.requerido", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.requerido", sesionController.getNombreCanal()).getMensajeUsuario()));
        }
    }

    /**
     * metodo que retorna la TDC de destino de pago enmascarada
     *
     * @return la cuenta de destino de pago enmascarada
     */
    public String getTDCDestinoMascara() {
        if (!tipoPago.equalsIgnoreCase(T_PAG_TDC_PROPIAS)) {
            if (datosAfiliacionSeleccionada == null || datosAfiliacionSeleccionada.getSemilla() == null || datosAfiliacionSeleccionada.getSemilla().trim().equalsIgnoreCase("")) {
                this.datosAfiliacionSeleccionada = new IbAfiliacionesDTO(sesionController.getSemilla());
            }
            return this.formatoAsteriscosWeb(this.datosAfiliacionSeleccionada.getDesEnc().desencriptar(tdcDestinoSeleccionada));
        } else {
            if (datosCuentaSeleccionadaActual == null || datosCuentaSeleccionadaActual.getSemilla() == null || datosCuentaSeleccionadaActual.getSemilla().trim().equalsIgnoreCase("")) {
                this.datosCuentaSeleccionadaActual = new CuentaDTO(sesionController.getSemilla());
            }
            return this.formatoAsteriscosWeb(this.datosCuentaSeleccionadaActual.getDesEnc().desencriptar(tdcDestinoSeleccionada));
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
        return this.formatoAsteriscosWeb(this.datosCuentaSeleccionadaLista.getDesEnc().desencriptar(cuentaSeleccionada));
    }

    /**
     * metodo para redirigir a paso de llenado de formulario del transferencias
     * segun sea el tipo de transferencia seleccionada
     */
    public String pagoTDCPaso2() {
        if (sesionController.isEnTransaccion()) {
            sesionController.setEnTransaccion(false);
            limpiarAfiliaciones();
        }
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(2);
        try {
            switch (tipoPago) {
                case T_PAG_TDC_PROPIAS: {
                    consultarCuentasPropias();
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.pagos.propias", sesionController.getIdCanal()));
                    return "";
                }
                case T_PAG_TDC_OTROSBANCOS: {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.pagos.tercotrobanc", sesionController.getIdCanal()));
                    return "";
                }
                case T_PAG_TDC_3ROSDELSUR: {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.pagos.tercdelsur", sesionController.getIdCanal()));
                    return "";
                }
                case T_PAG_TDC_PROPOTROSBANCOS: {
                    consultarCuentasPropias();
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.pagos.propotrobanc", sesionController.getIdCanal()));
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
     *
     * @return
     */
    public String ejecutarPaso4() {

        String codError = CODIGO_RESPUESTA_EXITOSO;
        TransaccionDTO transaccionDTO;
        if (sesionController.isEnTransaccion()) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formConfirmPagTDC:divCabeceraDetalle", new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Esta Transacción ya fue ejecutada"));
            return "";
        } else {
            int validaciones = this.validarPagosTDC();
            setErrorConfirmacion("");
            if (validaciones == 0) {
                //realizamos la transaccion y validamos
                transaccionDTO = this.ejecutarPagoTDC();
                if (transaccionDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && transaccionDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    sesionController.setEnTransaccion(true);
                    //notificamos en caso de realizar la transferencia
                    if (this.tipoEjecPagTDC.equalsIgnoreCase("U")) {
                        this.numConfirmacion = transaccionDTO.getNroReferencia();
                        //mensaje transaccion exitosa NOTA: en la nueva plataforma solo se maneja el mensaje exitoso en el paso final
                        //FacesContext.getCurrentInstance().addMessage("formDetalleTrans:divCabeceraDetalle", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("msj.operacion.exitosa", sesionController.getNombreCanal())));
                        //notificacion via email o SMS de transaccion
                        notificarPagoTDC();
                        //se registra la bitacora
                        registrarBitacoraPagTDC(codError);
                        if (!this.tipoPago.equalsIgnoreCase(T_PAG_TDC_PROPIAS)) {
                            /////////NOTIFICACION VIA SMS////////
                            String textoSMS = textosController.getNombreTexto("pnw.global.texto.smsPagTDC", sesionController.getNombreCanal());
                            String motivoSMS = textosController.getNombreTexto("pnw.menu.modulo.pagos", sesionController.getNombreCanal());
                            HashMap<String, String> parametros = new HashMap();
                            parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                            parametros.put("\\$PMTO", monto);
                            parametros.put("\\$PCTAORIG", this.getCtaOrigenMascara().substring(this.getCtaOrigenMascara().length() - 4));
                            parametros.put("\\$PCTADEST", this.getTDCDestinoMascara().substring(this.getTDCDestinoMascara().length() - 4));
//                            sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);
                        }
                    } else {
                        //notificacion via email o SMS de transaccion de agenda de transferencia
                        notificarAgendaPagoTDc();
                        //se registra la bitacora
                        registrarBitacoraAgendaPagTDC(codError);

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
                    registrarBitacoraPagTDC(codError);
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formDetalleTrans:divCabeceraDetalle", new FacesMessage(FacesMessage.SEVERITY_WARN, "", transaccionDTO.getRespuestaDTO().getTextoSP()));
                    setErrorConfirmacion(codError);

                }
                sesionController.setTextoStatus(textosController.getNombreTexto("pnw.modal.texto.consultandoInf", sesionController.getIdCanal()));
                sesionController.setValidadorFlujo(4);
                return "/sec/pagosTDC/ejecutarPaso4.xhtml?faces-redirect=true";
//return parametrosController.getNombreParametro("pnw.pagosTDC.url.ejecutarPaso4", sesionController.getIdCanal());
            } else {
                return reiniciarPagosTDC();
            }
        }
    }

    /**
     * metodo que se encarga de realizar las validaciones de negocio de las
     * transferencias entre cuentas propias
     *
     * @return TransaccionDTO
     */
    public TransaccionDTO ejecutarPagoTDC() {
        boolean validacionesSeguridad = false;
        TransaccionDTO transaccionDTO = new TransaccionDTO();

        //si el tipó de transaccion no es unica procedemos a agendarla
        if (!this.tipoEjecPagTDC.equalsIgnoreCase("U")) {
            /**
             * **************AGENDAR TRANSACCION *******************
             */
            RespuestaDTO respuestaAgenda = new RespuestaDTO();
            IBAgendaTransaccionDTO agenda = new IBAgendaTransaccionDTO();
            if (tipoPago.equalsIgnoreCase(T_PAG_TDC_PROPIAS)) {

                this.datosAfiliacionSeleccionada = new IbAfiliacionesDTO(sesionController.getSemilla());

                //se llenan los datos del usuario
                agenda.setCuentaDestino(datosAfiliacionSeleccionada.getDesEnc().desencriptar(tdcDestinoSeleccionada));
                agenda.setEmail(sesionController.getUsuario().getEmail());
                agenda.setTipoDoc(sesionController.getUsuario().getTipoDoc());
                agenda.setDocumento(sesionController.getUsuario().getDocumento());
                agenda.setNombreBeneficiario(sesionController.getUsuario().getNombre());
            } else {
                //se llenan los datos del beneficiario
                agenda.setCuentaDestino(datosAfiliacionSeleccionada.getDesEnc().desencriptar(tdcDestinoSeleccionada));
                agenda.setEmail(this.datosAfiliacionSeleccionada.getEmail());
                agenda.setTipoDoc(this.datosAfiliacionSeleccionada.getTipoDoc());
                agenda.setDocumento(this.datosAfiliacionSeleccionada.getDocumento());
                agenda.setNombreBeneficiario(this.datosAfiliacionSeleccionada.getNombreBeneficiario());
            }
            //se llenan los datos genericos
            agenda.setCuentaOrigen(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaSeleccionada));
            agenda.setMonto(BigDecimal.valueOf(Double.parseDouble(this.eliminarformatoSimpleMonto(this.monto))));
            agenda.setIdUsuario(sesionController.getUsuario().getId());
            agenda.setFrecuencia(String.valueOf(this.frecSeleccionada).charAt(0));
            agenda.setDiaFrecuencia(this.fechaFrecSeleccionada);
            agenda.setDescripcion(this.motivo);
            agenda.setTipo(TIPO_AGENDA_PAGTDC);
            agenda.setFechalimite(this.formatearFecha(this.fechaHasta, this.FORMATO_FECHA_SIMPLE));
            //se asigna el id de transaccion
            switch (tipoPago) {
                case (T_PAG_TDC_PROPIAS): {
                    agenda.setIdTransaccion(Integer.valueOf(this.ID_TRANS_TDC_PROPIAS_DELSUR));
                    break;
                }
                case (T_PAG_TDC_PROPOTROSBANCOS): {
                    agenda.setIdTransaccion(Integer.valueOf(this.ID_TRANS_TDC_P_OTROS_BCOS));
                    break;
                }
                case (T_PAG_TDC_3ROSDELSUR): {
                    agenda.setIdTransaccion(Integer.valueOf(this.ID_TRANS_TDC_TERC_DELSUR));
                    break;
                }
                case (T_PAG_TDC_OTROSBANCOS): {
                    agenda.setIdTransaccion(Integer.valueOf(this.ID_TRANS_TDC_T_OTROS_BCOS));
                    break;
                }
            }
            //validamos que no exista un registro previo con los mismos valores
            UtilDTO util = agendaTransaccionesDAO.consultarIdCabeceraAgenda(agenda, sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (util != null && util.getRespuestaDTO() != null && !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(this.CODIGO_RESPUESTA_EXITOSO)) {
                if (util.getResuladosDTO() != null && util.getResuladosDTO().get("id") != null && !util.getResuladosDTO().get("id").toString().isEmpty()) {
                    respuestaAgenda.setCodigo(this.CODIGO_RESPUESTA_EXITOSO);
                    respuestaAgenda.setCodigoSP(this.CODIGO_EXCEPCION_GENERICA);
                    respuestaAgenda.setDescripcionSP(this.CODIGO_EXCEPCION_GENERICA);
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
            if (!tipoPago.equalsIgnoreCase(T_PAG_TDC_PROPIAS)) {
                this.datosAfiliacionSeleccionada = this.afiliacionSeleccionadaCompleta(datosAfiliacionSeleccionada.getDesEnc().desencriptar(tdcDestinoSeleccionada));
                datosAfiliacionSeleccionada.setSemilla(sesionController.getSemilla());
                validacionesSeguridad = sesionController.productosCliente(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaSeleccionada));
            } else {
                datosCuentaSeleccionadaActual.setSemilla(sesionController.getSemilla());
                validacionesSeguridad = sesionController.productosCliente(datosCuentaSeleccionadaActual.getDesEnc().desencriptar(cuentaSeleccionada));
            }
            if (validacionesSeguridad) {
                switch (tipoPago) {
                    case T_PAG_TDC_PROPIAS: {
                        validacionesSeguridad = sesionController.productosCliente(datosCuentaSeleccionadaActual.getDesEnc().desencriptar(tdcDestinoSeleccionada));
                        if (validacionesSeguridad) {
                            transaccionDTO = tranferenciasYPagosDAO.pagoTDCPropiaMismoBanco(datosCuentaSeleccionadaActual.getDesEnc().desencriptar(cuentaSeleccionada),
                                    datosCuentaSeleccionadaActual.getDesEnc().desencriptar(tdcDestinoSeleccionada), new BigDecimal(eliminarformatoSimpleMonto(this.monto)),
                                    motivo, sesionController.getIdCanal(), sesionController.getNombreCanal());
                        }
                        break;
                    }
                    case T_PAG_TDC_OTROSBANCOS: {
                        validacionesSeguridad = sesionController.validarAfiliacion(datosAfiliacionSeleccionada.getDesEnc().desencriptar(tdcDestinoSeleccionada),
                                sesionController.getUsuario().getDocumento(),
                                sesionController.getUsuario().getId().toString(),
                                ID_TRANS_TDC_T_OTROS_BCOS,
                                tipoPago,
                                sesionController.getIdCanal(),
                                sesionController.getNombreCanal());
                        if (validacionesSeguridad) {
                            transaccionDTO = tranferenciasYPagosDAO.pagoTDCOtroBanco(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaSeleccionada),
                                    datosAfiliacionSeleccionada.getDesEnc().desencriptar(tdcDestinoSeleccionada), datosAfiliacionSeleccionada.getTipoDoc() + datosAfiliacionSeleccionada.getDocumento(), datosAfiliacionSeleccionada.getNombreBeneficiario(),
                                    new BigDecimal(eliminarformatoSimpleMonto(this.monto)), motivo, sesionController.getIdCanal(), sesionController.getNombreCanal());
                        }
                        break;
                    }
                    case T_PAG_TDC_3ROSDELSUR: {
                        validacionesSeguridad = sesionController.validarAfiliacion(datosAfiliacionSeleccionada.getDesEnc().desencriptar(tdcDestinoSeleccionada),
                                sesionController.getUsuario().getDocumento(),
                                sesionController.getUsuario().getId().toString(),
                                ID_TRANS_TDC_TERC_DELSUR,
                                tipoPago,
                                sesionController.getIdCanal(),
                                sesionController.getNombreCanal());
                        if (validacionesSeguridad) {
                            transaccionDTO = tranferenciasYPagosDAO.pagoTDCMismoBanco(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaSeleccionada),
                                    datosAfiliacionSeleccionada.getDesEnc().desencriptar(tdcDestinoSeleccionada), new BigDecimal(eliminarformatoSimpleMonto(this.monto)),
                                    motivo, sesionController.getIdCanal(), sesionController.getNombreCanal());
                        }
                        break;
                    }
                    case T_PAG_TDC_PROPOTROSBANCOS: {
                        validacionesSeguridad = sesionController.validarAfiliacion(datosAfiliacionSeleccionada.getDesEnc().desencriptar(tdcDestinoSeleccionada),
                                sesionController.getUsuario().getDocumento(),
                                sesionController.getUsuario().getId().toString(),
                                ID_TRANS_TDC_P_OTROS_BCOS,
                                tipoPago,
                                sesionController.getIdCanal(),
                                sesionController.getNombreCanal());
                        if (validacionesSeguridad) {
                            transaccionDTO = tranferenciasYPagosDAO.pagoTDCOtroBanco(datosAfiliacionSeleccionada.getDesEnc().desencriptar(cuentaSeleccionada),
                                    datosAfiliacionSeleccionada.getDesEnc().desencriptar(tdcDestinoSeleccionada), datosAfiliacionSeleccionada.getTipoDoc() + datosAfiliacionSeleccionada.getDocumento(), datosAfiliacionSeleccionada.getNombreBeneficiario(),
                                    new BigDecimal(eliminarformatoSimpleMonto(this.monto)), motivo, sesionController.getIdCanal(), sesionController.getNombreCanal());
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
     * metodo que identifica la transaccion y realiza el registro de bitacora
     */
    private void registrarBitacoraPagTDC(String tipoError) {

        //Registro en Bitacora
        switch (this.tipoPago) {
            case T_PAG_TDC_PROPIAS: {
                //registro en bitacora
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_TDC_PROPIAS_DELSUR, this.getCtaOrigenMascara(), this.getTDCDestinoMascara(), "Pago de TDC PDS - " + motivo, monto, numConfirmacion,
                        sesionController.getUsuario().getNombre(), sesionController.getUsuario().getTipoDoc().toString(), sesionController.getUsuario().getDocumento(), "", tipoError);
                break;
            }
            case T_PAG_TDC_OTROSBANCOS: {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_TDC_T_OTROS_BCOS, this.getCtaOrigenMascara(), this.getTDCDestinoMascara(), "Pago de TDC TOB - " + motivo, monto, numConfirmacion,
                        datosAfiliacionSeleccionada.getNombreBeneficiario(), datosAfiliacionSeleccionada.getTipoDoc().toString(), datosAfiliacionSeleccionada.getDocumento(),
                        datosAfiliacionSeleccionada.getId().toString(), tipoError);
                break;
            }
            case T_PAG_TDC_3ROSDELSUR: {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_TDC_TERC_DELSUR, this.getCtaOrigenMascara(), this.getTDCDestinoMascara(), "Pago de TDC TDS - " + motivo, monto, numConfirmacion,
                        datosAfiliacionSeleccionada.getNombreBeneficiario(), datosAfiliacionSeleccionada.getTipoDoc().toString(), datosAfiliacionSeleccionada.getDocumento(),
                        datosAfiliacionSeleccionada.getId().toString(), tipoError);
                break;
            }
            case T_PAG_TDC_PROPOTROSBANCOS: {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_TDC_P_OTROS_BCOS, this.getCtaOrigenMascara(), this.getTDCDestinoMascara(), "Pago de TDC POB - " + motivo, monto, numConfirmacion,
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
    private void registrarBitacoraAgendaPagTDC(String tipoError) {

        //Registro en Bitacora
        switch (this.tipoPago) {
            case T_PAG_TDC_PROPIAS: {
                //registro en bitacora
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_AGENDA_PPDS, this.getCtaOrigenMascara(), this.getTDCDestinoMascara(), "Agenda de Pago de TDC PDS - " + motivo, monto, numConfirmacion,
                        sesionController.getUsuario().getNombre(), sesionController.getUsuario().getTipoDoc().toString(), sesionController.getUsuario().getDocumento(), "", tipoError);
                break;
            }
            case T_PAG_TDC_OTROSBANCOS: {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_AGENDA_PTOB, this.getCtaOrigenMascara(), this.getTDCDestinoMascara(), "Agenda de Pago de TOB PDS - " + motivo, monto, numConfirmacion,
                        datosAfiliacionSeleccionada.getNombreBeneficiario(), datosAfiliacionSeleccionada.getTipoDoc().toString(), datosAfiliacionSeleccionada.getDocumento(),
                        datosAfiliacionSeleccionada.getId().toString(), tipoError);
                break;
            }
            case T_PAG_TDC_3ROSDELSUR: {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_AGENDA_PTDS, this.getCtaOrigenMascara(), this.getTDCDestinoMascara(), "Agenda de Pago de TOB TDS - " + motivo, monto, numConfirmacion,
                        datosAfiliacionSeleccionada.getNombreBeneficiario(), datosAfiliacionSeleccionada.getTipoDoc().toString(), datosAfiliacionSeleccionada.getDocumento(),
                        datosAfiliacionSeleccionada.getId().toString(), tipoError);
                break;
            }
            case T_PAG_TDC_PROPOTROSBANCOS: {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_AGENDA_PPOB, this.getCtaOrigenMascara(), this.getTDCDestinoMascara(), "Agenda de Pago de TOB POB - " + motivo, monto, numConfirmacion,
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
    public void notificarPagoTDC() {

        String nombreBeneficiario = "";
        StringBuilder textoE = new StringBuilder(""); //EMISOR
        StringBuilder textoB = new StringBuilder(""); //BENEFICIARIO

        if (this.tipoPago.equalsIgnoreCase(T_PAG_TDC_PROPIAS) || this.tipoPago.equalsIgnoreCase(T_PAG_TDC_PROPOTROSBANCOS)) {
            nombreBeneficiario = sesionController.getUsuario().getNombre();
        } else {
            nombreBeneficiario = datosAfiliacionSeleccionada.getNombreBeneficiario();
        }

        String documentoBenef;
        if (tipoPago.equalsIgnoreCase(T_PAG_TDC_PROPIAS)) {
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
                .append(formatoAsteriscosWeb(this.getTDCDestinoMascara()))
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
                .append(formatoAsteriscosWeb(this.getTDCDestinoMascara()))
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
        notificacionesDAO.enviarEmailPN(CODIGO_EMAIL_PAG_TDC, sesionController.getUsuario().getEmail(), textoE.toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        //Envio de Email del Beneficiario
        notificacionesDAO.enviarEmailPN(CODIGO_EMAIL_PAG_TDC, getEmailBeneficiario(), textoB.toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
    }

    /**
     * Metodo que setea los datos y parametros para el envio del correo luego
     * del proceso de la Agenda del pago TDC
     */
    public void notificarAgendaPagoTDc() {

        String asunto = "";
        String nombreEmisor = "";
        String nombreBeneficiario = "";
        String identificacionBenef = "";
        StringBuilder texto = new StringBuilder("");
        StringBuilder textoE = new StringBuilder(""); //EMISOR

        //setFechaTransaccion(BDSUtil.formatearFecha(Calendar.getInstance().getTime(), BDSUtil.FORMATO_FECHA_COMPLETA));
        setFechaTransaccion(new Date());

        switch (this.tipoPago) {
            case T_PAG_TDC_PROPIAS: {
                asunto = "Agenda de Pago de TDC Propias, DELSUR";
                nombreEmisor = sesionController.getUsuario().getNombre();
                nombreBeneficiario = nombreEmisor;
                identificacionBenef = sesionController.getUsuario().getDocumento();

                textoE = new StringBuilder("Estimado(a) ");
                textoE.append(nombreEmisor);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append("Le informamos que su solicitud de Agenda de Pago de TDC se registró satisfactoriamente según el siguiente detalle:");
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);

                break;
            }
            case T_PAG_TDC_OTROSBANCOS: {
                asunto = "Agenda de Pago de TDC en Otros Bancos, DELSUR";
                nombreEmisor = sesionController.getUsuario().getNombre();
                nombreBeneficiario = datosAfiliacionSeleccionada.getNombreBeneficiario();
                setEmailBeneficiario(datosAfiliacionSeleccionada.getEmail());
                identificacionBenef = datosAfiliacionSeleccionada.getDocumento();

                textoE = new StringBuilder("Estimado(a) ");
                textoE.append(nombreEmisor);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append("Le informamos que su solicitud de Agenda de Pago de TDC se registró satisfactoriamente según el siguiente detalle:");
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);

                break;
            }
            case T_PAG_TDC_3ROSDELSUR: {
                asunto = "Agenda de Pago de TDC de Terceros, DELSUR";
                nombreEmisor = sesionController.getUsuario().getNombre();
                nombreBeneficiario = datosAfiliacionSeleccionada.getNombreBeneficiario();
                setEmailBeneficiario(datosAfiliacionSeleccionada.getEmail());
                identificacionBenef = datosAfiliacionSeleccionada.getDocumento();

                textoE = new StringBuilder("Estimado(a) ");
                textoE.append(nombreEmisor);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append("Le informamos que su solicitud de Agenda de Pago de TDC se registró satisfactoriamente según el siguiente detalle:");
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);

                break;
            }

            case T_PAG_TDC_PROPOTROSBANCOS: {
                asunto = "Agenda de Pago de TDC Propias de Otros Bancos, DELSUR";
                nombreEmisor = sesionController.getUsuario().getNombre();
                nombreBeneficiario = nombreEmisor;
                identificacionBenef = datosAfiliacionSeleccionada.getDocumento();

                textoE = new StringBuilder("Estimado(a) ");
                textoE.append(nombreEmisor);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);
                textoE.append("Le informamos que su solicitud de Agenda de Pago de TDC se registro satisfactoriamente según el siguiente detalle:");
                textoE.append(NUEVALINEAEMAIL);
                textoE.append(NUEVALINEAEMAIL);

                break;
            }
        }

        texto.append("Registro de Agenda de Pago de TDC ");
        texto.append(NUEVALINEAEMAIL);
        texto.append("Nombre de Beneficiario: ");
        texto.append(nombreBeneficiario);
        texto.append(NUEVALINEAEMAIL);
        texto.append("Nro. Cédula / Rif Beneficiario: ");
        texto.append(identificacionBenef);
        texto.append(NUEVALINEAEMAIL);
        texto.append("TDC de Destino: ");
        texto.append(formatoAsteriscosWeb(this.getTDCDestinoMascara()));
        texto.append(NUEVALINEAEMAIL);
        texto.append("Cuenta de Origen: ");
        texto.append(formatoAsteriscosWeb(this.getCtaOrigenMascara()));
        texto.append(NUEVALINEAEMAIL);
        texto.append("Monto del Pago TDC: ");
        texto.append(monto);
        texto.append(" Bs.");
        texto.append(NUEVALINEAEMAIL);
        texto.append("Fecha de Validez de la Agenda de Transacción: ");
        texto.append(this.formatearFecha(getFechaHasta(), this.FORMATO_FECHA_SIMPLE));
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
     * @return String Action de confirmar datos para retornar a editar los
     * mismos
     */
    public String reiniciarPagosTDC() {
        this.mantenerDatosForm = false;
        limpiarAfiliaciones();
        this.tieneAfiliaciones = true;
        sesionController.setValidacionOTP(2);
        sesionController.setValidadorFlujo(2);
        switch (tipoPago) {
            case T_PAG_TDC_PROPIAS: {
                consultarCuentasPropias();
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.pagos.propias", sesionController.getIdCanal()));

                } catch (IOException e) {

                }
                return "";
            }
            case T_PAG_TDC_OTROSBANCOS: {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.pagos.tercotrobanc", sesionController.getIdCanal()));

                } catch (IOException e) {

                }
                return "";
            }
            case T_PAG_TDC_3ROSDELSUR: {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.pagos.tercdelsur", sesionController.getIdCanal()));

                } catch (IOException e) {

                }
                return "";
            }
            case T_PAG_TDC_PROPOTROSBANCOS: {
                consultarCuentasPropias();
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.submenu.pagos.propotrobanc", sesionController.getIdCanal()));

                } catch (IOException e) {

                }
                return "";
            }
            default: {
                return parametrosController.getNombreParametro("pnw.global.url.inicial", sesionController.getIdCanal());
            }
        }
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
     * Metodo que se encarga retornar las afiliaciones consultadas por el tipo
     * de transaccion
     *
     * @return Collection<IbAfiliacionesDTO>
     */
    public Collection<IbAfiliacionesDTO> getAfiliaciones() {
        return afiliaciones;
    }

    /**
     * Metodo que se encarga de asignar las afiliaciones consultadas por el tipo
     * de transaccion
     *
     * @param afiliaciones Collection<IbAfiliacionesDTO>
     */
    public void setAfiliaciones(Collection<IbAfiliacionesDTO> afiliaciones) {
        this.afiliaciones = afiliaciones;
    }

    /**
     * Obtiene Atributo para identificar el tipo del pago tdc P - propias, TOB -
     * terceros otros bancos, TBDS - terceros banco del sur
     *
     * @return String P - propias, TOB - terceros otros bancos, TBDS - terceros
     * banco del sur
     */
    public String getTipoPago() {
        return tipoPago;
    }

    /**
     * Inserta Atributo para identificar el tipo del pago tdc P - propias, TOB -
     * terceros otros bancos, TBDS - terceros banco del sur
     *
     * @param tipoPago String P - propias, TOB - terceros otros bancos, TBDS -
     * terceros banco del sur
     */
    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    /**
     * Obtener Mensaje por defecto para los campos vacios o en null
     *
     * @return mensajeNull String Mensaje por defecto para los campos vacios o
     * en null
     */
    public String getMensajeNull() {
        return mensajeNull = textosFacade.findByCodigo("campoRequerido", sesionController.getIdCanal()).getMensajeUsuario();
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
     * Obtener TDC Destino a Acreditar extraido del formulario del pago tdc
     *
     * @return String TDC Destino a Acreditar extraido del formulario del pago
     * tdc
     */
    public String getTdcDestinoSeleccionada() {
        return tdcDestinoSeleccionada;
    }

    /**
     * Insertar TDC Destino a Acreditar extraido del formulario del pago tdc
     *
     * @param tdcDestinoSeleccionada TDC Destino a Acreditar extraido del
     * formulario del pago tdc
     */
    public void setTdcDestinoSeleccionada(String tdcDestinoSeleccionada) {
        this.tdcDestinoSeleccionada = tdcDestinoSeleccionada;

        if (!tipoPago.equalsIgnoreCase(T_PAG_TDC_PROPIAS)) {
            if (datosAfiliacionSeleccionada == null || datosAfiliacionSeleccionada.getSemilla() == null || datosAfiliacionSeleccionada.getSemilla().trim().equalsIgnoreCase("")) {
                this.datosAfiliacionSeleccionada = new IbAfiliacionesDTO(sesionController.getSemilla());
            }
            this.datosAfiliacionSeleccionada = this.afiliacionSeleccionadaCompleta(this.datosAfiliacionSeleccionada.getDesEnc().desencriptar(tdcDestinoSeleccionada));
        } else {
            if (datosTDCSeleccionada == null || datosTDCSeleccionada.getSemilla() == null || datosTDCSeleccionada.getSemilla().trim().equalsIgnoreCase("")) {
                this.datosTDCSeleccionada = new TarjetasCreditoDTO(sesionController.getSemilla());
            }
            this.datosTDCSeleccionada = this.tdcPropiaSeleccionadaCompleta(this.datosTDCSeleccionada.getDesEnc().desencriptar(tdcDestinoSeleccionada));
        }
    }

    /**
     * Obtener Cuenta del Titular a Debitar Seleccionada extraido del formulario
     * del pago tdc
     *
     * @return String Cuenta del Titular a Debitar Seleccionada extraido del
     * formulario del pago tdc
     */
    public String getCuentaSeleccionada() {
        return cuentaSeleccionada;
    }

    /**
     * Insertar Cuenta del Titular a Debitar Seleccionada extraido del
     * formulario del pago tdc
     *
     * @param cuentaSeleccionada Cuenta del Titular a Debitar Seleccionada
     * extraido del formulario del pago tdc
     */
    public void setCuentaSeleccionada(String cuentaSeleccionada) {
        this.cuentaSeleccionada = cuentaSeleccionada;
        if (datosCuentaSeleccionadaLista == null || datosCuentaSeleccionadaLista.getSemilla() == null || datosCuentaSeleccionadaLista.getSemilla().trim().equalsIgnoreCase("")) {
            this.datosCuentaSeleccionadaLista = new CuentaDTO(sesionController.getSemilla());
        }
        this.datosCuentaSeleccionadaLista = this.cuentaPropiaSeleccionadaCompleta(this.datosCuentaSeleccionadaLista.getDesEnc().desencriptar(cuentaSeleccionada));
    }

    /**
     * Obtener Monto extraido del formulario del pago tdc
     *
     * @return String Monto extraido del formulario del pago tdc
     */
    public String getMonto() {
        return monto;
    }

    /**
     * Insertar Monto extraido del formulario del pago tdc
     *
     * @param monto Monto extraido del formulario del pago tdc
     */
    public void setMonto(String monto) {
        this.monto = monto;
    }

    /**
     * Obtener Motivo extraido del formulario del pago tdc
     *
     * @return String Motivo extraido del formulario del pago tdc
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Insertar Motivo extraido del formulario del pago tdc
     *
     * @param motivo Motivo extraido del formulario del pago tdc
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * Obtener cuentas de clientes
     *
     * @return Collection<CuentaDTO> objeto que contiene las cuentas propias
     */
    public Collection<CuentaDTO> getCuentasCliente() {
        return cuentasCliente;
    }

    /**
     * Asigna objeto que contiene las cuentas propias
     *
     * @param cuentasCliente
     */
    public void setCuentasCliente(Collection<CuentaDTO> cuentasCliente) {
        this.cuentasCliente = cuentasCliente;
    }

    /**
     * Obtiene los datos de cuenta seleccionada
     *
     * @return CuentaDTO para almacenar los datos de la cuenta seleccionada de
     * la lista
     */
    public CuentaDTO getDatosCuentaSeleccionadaLista() {
        return datosCuentaSeleccionadaLista;
    }

    /**
     * Asigna objeto para almacenar los datos de la cuenta seleccionada de la
     * lista
     *
     * @param datosCuentaSeleccionadaLista CuentaDTO
     */
    public void setDatosCuentaSeleccionadaLista(CuentaDTO datosCuentaSeleccionadaLista) {
        this.datosCuentaSeleccionadaLista = datosCuentaSeleccionadaLista;
    }

    /**
     * Obtiene datos de cuenta seleccionada
     *
     * @return objeto para almacenar la coonsulta de los datos de la cuenta
     * seleccionada
     */
    public CuentaDTO getDatosCuentaSeleccionadaActual() {
        return datosCuentaSeleccionadaActual;
    }

    /**
     * Asigna objeto para almacenar la coonsulta de los datos de la cuenta
     * seleccionada
     *
     * @param datosCuentaSeleccionadaActual CuentaDTO
     */
    public void setDatosCuentaSeleccionadaActual(CuentaDTO datosCuentaSeleccionadaActual) {
        this.datosCuentaSeleccionadaActual = datosCuentaSeleccionadaActual;
    }

    /**
     * Obtiene los datos de la afiliacion seleccionada
     *
     * @return objeto para almacenar los datos de la afiliacion seleccionada de
     * la lista
     */
    public IbAfiliacionesDTO getDatosAfiliacionSeleccionada() {
        return datosAfiliacionSeleccionada;
    }

    /**
     * Asigna objeto para almacenar los datos de la afiliacion seleccionada de
     * la lista
     *
     * @param datosAfiliacionSeleccionada IbAfiliacionesDTO
     */
    public void setDatosAfiliacionSeleccionada(IbAfiliacionesDTO datosAfiliacionSeleccionada) {
        this.datosAfiliacionSeleccionada = datosAfiliacionSeleccionada;
    }

    /**
     * Obteneer el codigo de validacio de OTP
     *
     * @return el codigo de validacion OTP
     */
    public String getCodigoOTP() {
        return codigoOTP;
    }

    /**
     * Asigna el codigo de validacion OTP
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
     * Obtener el email del beneficiario
     *
     * @return the emailBeneficiario
     */
    public String getEmailBeneficiario() {
        return emailBeneficiario;
    }

    /**
     * Asignar el email del beneficiario
     *
     * @param emailBeneficiario el email del beneficiario
     */
    public void setEmailBeneficiario(String emailBeneficiario) {
        this.emailBeneficiario = emailBeneficiario;
    }

    /**
     * Obtener el listado de las TDC del cliente
     *
     * @return Collection<TarjetasCreditoDTO>
     */
    public Collection<TarjetasCreditoDTO> getTdcCliente() {
        return tdcCliente;
    }

    /**
     * Asignar la TDC del cliente
     *
     * @param tdcCliente
     */
    public void setTdcCliente(Collection<TarjetasCreditoDTO> tdcCliente) {
        this.tdcCliente = tdcCliente;
    }

    /**
     * Indica se se muestra el mono de la cuota de pago
     *
     * @return
     */
    public boolean isMostrarMontoCuotaPago() {
        return mostrarMontoCuotaPago;
    }

    /**
     * Asigna mostrar monto de cuota de pago
     *
     * @param mostrarMontoCuotaPago
     */
    public void setMostrarMontoCuotaPago(boolean mostrarMontoCuotaPago) {
        this.mostrarMontoCuotaPago = mostrarMontoCuotaPago;
    }

    /**
     * Obtener monto minimo
     *
     * @return
     */
    public String getMontoMin() {
        return montoMin;
    }

    /**
     * Asignar monto minimo
     *
     * @param montoMin String
     */
    public void setMontoMin(String montoMin) {
        this.montoMin = montoMin;
    }

    /**
     * Obtener monto total
     *
     * @return String
     */
    public String getMontoTotal() {
        return montoTotal;
    }

    /**
     * Asignar el monto total
     *
     * @param montoTotal
     */
    public void setMontoTotal(String montoTotal) {
        this.montoTotal = montoTotal;
    }

    /**
     * Asignacion del tipo de pago (total, minimo, otro)
     *
     * @return String
     */
    public String getRadioMontoaPagar() {
        return radioMontoaPagar;
    }

    /**
     * Asignacion del tipo de pago (total, minimo, otro)
     *
     * @param radioMontoaPagar (total, minimo, otro)
     */
    public void setRadioMontoaPagar(String radioMontoaPagar) {
        this.radioMontoaPagar = radioMontoaPagar;
    }

    /**
     * retorna el Atributo que indica si se muestra el elemento de otro monto
     *
     * @return Atributo que indica si se muestra el elemento de otro monto
     */
    public boolean isMostrarRadioMonto() {
        return mostrarRadioMonto;
    }

    /**
     * asigna el Atributo que indica si se muestra el elemento de otro monto
     *
     * @param mostrarRadioMonto Atributo que indica si se muestra el elemento de
     * otro monto
     */
    public void setMostrarRadioMonto(boolean mostrarRadioMonto) {
        this.mostrarRadioMonto = mostrarRadioMonto;
    }

    /**
     * retorna el objeto que almacena los datos de la TDC seleccionada de la
     * lista
     *
     * @return el objeto que almacena los datos de la TDC seleccionada de la
     * lista
     */
    public TarjetasCreditoDTO getDatosTDCSeleccionada() {
        return datosTDCSeleccionada;
    }

    /**
     * retorna el codigo OTP cuando viene de la pagina de redireccion
     *
     * @return paramCodOTP el codigo OTP cuando viene de la pagina de
     * redireccion
     */
    public String getParamCodOTP() {
        return paramCodOTP;
    }

    /**
     * asigna el codigo OTP cuando viene de la pagina de redireccion
     *
     * @param paramCodOTP el codigo OTP cuando viene de la pagina de redireccion
     */
    public void setParamCodOTP(String paramCodOTP) {
        this.paramCodOTP = paramCodOTP;
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
     * Monto Absoluto de pago
     *
     * @return BigDecimal
     */
    public BigDecimal getMontoAbsoluto() {
        return montoAbsoluto;
    }

    /**
     * inserta Monto Absoluto de pago
     *
     * @param montoAbsoluto BigDecimal
     */
    public void setMontoAbsoluto(BigDecimal montoAbsoluto) {
        this.montoAbsoluto = montoAbsoluto;
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
     * retorna tipo de ejecucion del pago tdc U=unico, D=diario, A=agendado
     *
     * @return tipo de ejecucion del pago tdc U=unico, D=diario, A=agendado
     */
    public String getTipoEjecPagTDC() {
        return tipoEjecPagTDC;
    }

    /**
     * asigna tipo de ejecucion del pago tdc U=unico, D=diario, A=agendado
     *
     * @param tipoEjecPagTDC tipo de ejecucion del pago tdc U=unico, D=diario,
     * A=agendado
     */
    public void setTipoEjecPagTDC(String tipoEjecPagTDC) {
        this.tipoEjecPagTDC = tipoEjecPagTDC;
    }

    public void seleccionarTipoEjecPagTDC() {
        this.mantenerDatosForm = true;
        this.fechaSeleccionadaString = "";
        if (this.tipoEjecPagTDC == null || this.tipoEjecPagTDC.isEmpty()) {

        } else {
            switch (tipoEjecPagTDC) {
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

    public void seleccionarFechaEjecPagTDC() {
        this.fechaSeleccionadaString = "";
        if (this.tipoEjecPagTDC == null || this.tipoEjecPagTDC.isEmpty()) {
            this.fechaFrecuencia = new String[]{"Seleccione"};
            this.frecuencias = new String[]{"Seleccione"};
        } else {
            switch (tipoEjecPagTDC) {
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
        if (this.tipoEjecPagTDC.equalsIgnoreCase("A") && this.frecSeleccionada == 2) {
            this.mostrarDiasQuincena = true;
            this.segundoDiaQuincena = 15 + this.fechaFrecSeleccionada;
        } else {
            this.mostrarDiasQuincena = false;
            this.segundoDiaQuincena = 0;
        }

    }

    /**
     * Metodo para activar el imput de otro monto cuando se selecciona en el
     * select de montos (otro monto)
     *
     * @param event AjaxBehaviorEvent
     */
    public void activarOtroMonto(AjaxBehaviorEvent event) {
        if (radioMontoaPagar.equals("pagoOtro")) {
            mostrarRadioMonto = true;
            monto = "0,00";
        } else {
            mostrarRadioMonto = false;
        }
    }

    /**
     * Metodo para consultar los tipos de pagos para la TDC
     *
     * @param event AjaxBehaviorEvent
     */
    public void consultarPagosTarjeta(AjaxBehaviorEvent event) {
        if (datosTDCSeleccionada != null) {
            mostrarMontoCuotaPago = true;
            mostrarRadioMonto = false;
            radioMontoaPagar = null;
            montoMin = this.formatearMonto((datosTDCSeleccionada.getPagoMinimo() != null ? datosTDCSeleccionada.getPagoMinimo() : BigDecimal.ZERO));
            montoTotal = this.formatearMonto((datosTDCSeleccionada.getSaldoTotal() != null ? datosTDCSeleccionada.getSaldoTotal() : BigDecimal.ZERO));
            monto = "0,00";
        } else {
            mostrarMontoCuotaPago = false;
            mostrarRadioMonto = false;
            radioMontoaPagar = null;
            montoMin = "0,00";
            montoTotal = "0,00";
            monto = "0,00";
        }
    }

    /**
     * retorna los tipos posibles para la frecuencia de Pago TDC
     *
     * @return tipos posibles para la frecuencia de Pago TDC
     */
    public String[] getFrecuencias() {
        return frecuencias;
    }

    /**
     * asigna los tipos posibles para la frecuencia de Pago TDC
     *
     * @param frecuencias tipos posibles para la frecuencia de Pago TDC
     */
    public void setFrecuencias(String[] frecuencias) {
        this.frecuencias = frecuencias;
    }

    /**
     * retorna los valores posibles para los tipos de frecuencia de Pago TDC
     *
     * @return valores posibles para los tipos de frecuencia de Pago TDC
     */
    public String[] getFechaFrecuencia() {
        return fechaFrecuencia;
    }

    /**
     * asigna los valores posibles para los tipos de frecuencia de Pago TDC
     *
     * @param fechaFrecuencia valores posibles para los tipos de frecuencia de
     * Pago TDC
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
     * quincena a pagar la tdc
     *
     * @return bandera para identificar si se muestra el texto de los dias de
     * quincena a pagar la tdc
     */
    public boolean isMostrarDiasQuincena() {
        return mostrarDiasQuincena;
    }

    /**
     * asigna la bandera para identificar si se muestra el texto de los dias de
     * quincena a pagar la tdc
     *
     * @param mostrarDiasQuincena bandera para identificar si se muestra el
     * texto de los dias de quincena a pagar la tdc
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
     * fecha hasta de agendar Pago TDC
     *
     * @return bandera para identificar si se muestra el calendario de la fecha
     * hasta de agendar Pago TDC
     */
    public boolean isMostrarFechaHasta() {
        return mostrarFechaHasta;
    }

    /**
     * asigna la bandera para identificar si se muestra el calendario de la
     * fecha hasta de agendar Pago TDC
     *
     * @param mostrarFechaHasta bandera para identificar si se muestra el
     * calendario de la fecha hasta de agendar Pago TDC
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
     * retorna el indicador para mantener campos en formulario de Pago TDC
     *
     * @return el indicador para mantener campos en formulario de Pago TDC
     */
    public boolean isMantenerDatosForm() {
        return mantenerDatosForm;
    }

    /**
     * asigna el indicador para mantener campos en formulario de Pago TDC
     *
     * @param mantenerDatosForm el indicador para mantener campos en formulario
     * de Pago TDC
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
                    && (sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_PROPIAS_DELSUR)
                    || sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_P_OTROS_BCOS))) {
                //incrementamos el validador de flujo ya que estos modulos no tienen OTP
                sesionController.setValidadorFlujo(2);
            }
            if (sesionController.getValidadorFlujo() != 2
                    || (!sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_PROPIAS_DELSUR)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_T_OTROS_BCOS)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_TERC_DELSUR)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_P_OTROS_BCOS))) {
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
                    || (!sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_PROPIAS_DELSUR)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_T_OTROS_BCOS)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_TERC_DELSUR)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_P_OTROS_BCOS))) {
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
                    || (!sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_PROPIAS_DELSUR)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_T_OTROS_BCOS)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_TERC_DELSUR)
                    && !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_TDC_P_OTROS_BCOS))) {
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
     * @param writer
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoPagosTDCPDF(Document pdf) throws BadElementException, DocumentException {
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
            String numeroTDC;

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(PagosTDCController.class.getResource("/imgPDF/logoBanner.png"));

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
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.pagosTDC.descargaPdf.titulo", sesionController.getIdCanal()) + " " + sesionController.getNombreTransaccion(), fontTituloBold)));

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
            if (this.tipoEjecPagTDC.equalsIgnoreCase("U") && this.transaccionExitosa()) {
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosTDC.descargaPdf.nroRef", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(this.numConfirmacion, font));
            }

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            if (this.tipoEjecPagTDC.equalsIgnoreCase("U")) {
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosTDC.descargaPdf.fechaEjec", sesionController.getIdCanal()), fontBold));
            } else {
                if (this.tipoEjecPagTDC.equalsIgnoreCase("A")) {
                    table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosTDC.descargaPdf.fechaAgendada", sesionController.getIdCanal()), fontBold));
                }
            }
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(fecha2(new Date()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosTDC.descargaPdf.ctaDebitada", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.formatoAsteriscosWeb(datosCuentaSeleccionadaLista.getNumeroCuenta()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosTDC.descargaPdf.tdcAbonar", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));

            if (tipoPago.equalsIgnoreCase("PPDS")) {
                numeroTDC = this.formatoAsteriscosWeb(this.datosCuentaSeleccionadaActual.getDesEnc().desencriptar(this.tdcDestinoSeleccionada));
            } else {
                numeroTDC = this.formatoAsteriscosWeb(this.datosAfiliacionSeleccionada.getDesEnc().desencriptar(tdcDestinoSeleccionada));
            }

            table.addCell(new Phrase(numeroTDC, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosTDC.descargaPdf.nombreBeneficiario", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));

            if (tipoPago.equalsIgnoreCase("PPDS")) {
                nombreBeneficiario = sesionController.getUsuario().getNombre();
            } else {
                nombreBeneficiario = this.datosAfiliacionSeleccionada.getNombreBeneficiario();
            }

            table.addCell(new Phrase(nombreBeneficiario, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosTDC.descargaPdf.bancoDestino", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));

            if (tipoPago.equalsIgnoreCase("PPDS")) {
                nombreBanco = textosController.getNombreTexto("pnw.global.texto.banco", sesionController.getIdCanal());
            } else {
                nombreBanco = this.datosAfiliacionSeleccionada.getNombreBanco();
            }

            table.addCell(new Phrase(nombreBanco, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosTDC.descargaPdf.monto", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase((this.monto != null ? parametrosController.getNombreParametro("pnw.global.tipoMoneda.Bs", sesionController.getIdCanal()) + " " + this.monto : " - "), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosTDC.descargaPdf.motivo", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.motivo, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosTDC.descargaPdf.email", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            if (tipoPago.equalsIgnoreCase("PPDS")) {
                emailBeneficiarioPDF = sesionController.getUsuario().getEmail();
            } else {
                emailBeneficiarioPDF = this.datosAfiliacionSeleccionada.getEmail();
            }
            table.addCell(new Phrase(emailBeneficiarioPDF, font));

            if (this.tipoEjecPagTDC.equalsIgnoreCase("A")) {

                table.getDefaultCell().setColspan(2);
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosTDC.descargaPdf.tipoEjec", sesionController.getIdCanal()), fontBold));

                String instruccion = textosController.getNombreTexto("pnw.pagosTDC.descargaPdf.agendaPeriodica", sesionController.getIdCanal())
                        + fechaSeleccionadaString;

                if (frecSeleccionada == 2) {
                    instruccion = instruccion + " y el día " + this.segundoDiaQuincena;
                }

                instruccion = instruccion + ", Hasta el " + this.formatearFecha(this.fechaHasta, FORMATO_FECHA_SIMPLE);

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
        }
    }

    /**
     * metodo encargado de armar el reportes de listado del Perfil
     * PerfilBKUsuarios en PDF
     *
     * @throws java.io.IOException
     * @throws com.lowagie.text.DocumentException
     */
    public void detallePagosTDCPDF() throws IOException, DocumentException {

        try {

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-disposition", "attachment;filename=\"PagoTDC.pdf\"");
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
            this.cuerpoPagosTDCPDF(document);

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
        if (validaCierreOperaciones == false) {
            mostrarMensajeCierre = true;
        }
        return mostrarMensajeCierre;
    }

}
