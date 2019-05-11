/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.CuentaDAO;
import com.bds.wpn.dao.IbMediosNotificacionDAO;
import com.bds.wpn.dao.NotificacionesDAO;
import com.bds.wpn.dao.ReclamosDAO;
import com.bds.wpn.dao.TddDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.IbMediosNotificacionDTO;
import com.bds.wpn.dto.MovimientoCuentaDTO;
import com.bds.wpn.dto.ReclamoDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.TarjetaDebitoDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_DATA_INVALIDA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.NUEVALINEAEMAIL;
import static com.bds.wpn.util.BDSUtil.fecha;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
 * @author juan.faneite
 */
@Named("wpnReclamoController")
@SessionScoped
public class ReclamoController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;
    @EJB
    IbParametrosFacade parametrosFacade;
    @EJB
    IbTextosFacade textosFacade;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    ReclamosDAO reclamosDAO;

    @EJB
    IbMediosNotificacionDAO mediosNotificacionDAO;

    @EJB
    CuentaDAO cuentaDAO;

    @EJB
    TddDAO tddDAO;
    
    @EJB
    NotificacionesDAO notificacionesDAO;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;

    private String email;
    private String prefijoOperadora;
    private String nroTlf;
    private Date fechaEjecucion;
    private ReclamoDTO reclamoSelected;
    private ReclamoDTO reclamoDTO;
    private Collection<ReclamoDTO> listTipoReclamos;
    private String idtipoReclamoSeleccionado = "-1";
    private String nombretipoReclamoSeleccionado;
    private String mediosNotificacionSelected = "-1";
    private String temporalIDSelectedReclamo = "-1";
    private IbMediosNotificacionDTO listMediosNotificacionDTO;
    private String TDDSelected = "-1";
    private TarjetaDebitoDTO listTDD;                   //objeto que contiene las cuentas propias
    private Collection<CuentaDTO> cuentasCliente;                   //objeto que contiene las cuentas propias
    private String movimientosAsociados;
    private boolean activaMovimientosCuenta;
    private Date txtFechaDesde;                            //input para la fecha desde
    private Date txtFechaHasta;                            //input para la fecha hasta
    private CuentaDTO cuentaDTO;                                        //Objeto para almacenar la cuenta seleccionada
    private Collection<MovimientoCuentaDTO> listMovimientoCuentaDTO;    //collection para movimientos de la cuenta
    private ArrayList<MovimientoCuentaDTO> listMovimientoSeleccionado;    //collection para movimientos de la cuenta
    private Date calendarFechaMax = new Date();                                          //fecha maxima para validacion del calendar
    private Date calendarFechaMin;                                          //fecha minima para validacion del calendar
    private String cuentaSeleccionada = "-1";                             //Cuenta seleccionada en el select
    private CuentaDTO datosCuentaSeleccionadaLista = null;                      //objeto para almacenar los datos de la cuenta seleccionada de la lista
    private String observacion;
    private String estatusRegistro = "En trámite";
    private boolean registro;
    private boolean listadoReclamos;
    private boolean mantenerDatosForm = false;              //indicador para mantener los datos del formulario
    private int reclamosMovProcesados;
    private Collection<MovimientoCuentaDTO> movSatisfactorios;
    private Collection<MovimientoCuentaDTO> movError;
    private boolean tieneMovSatisfactorios;
    private boolean tieneMovError;

    public boolean isMantenerDatosForm() {
        return mantenerDatosForm;
    }

    public void setMantenerDatosForm(boolean mantenerDatosForm) {
        this.mantenerDatosForm = mantenerDatosForm;
    }

    public ReclamoDTO getReclamoSelected() {
        return reclamoSelected;
    }

    public void setReclamoSelected(ReclamoDTO reclamoSelected) {
        this.reclamoSelected = reclamoSelected;
    }

    public ReclamoDTO getReclamoDTO() {
        return reclamoDTO;
    }

    public void setReclamoDTO(ReclamoDTO reclamoDTO) {
        this.reclamoDTO = reclamoDTO;
    }

    public String getTemporalIDSelectedReclamo() {
        return temporalIDSelectedReclamo;
    }

    public void setTemporalIDSelectedReclamo(String temporalIDSelectedReclamo) {
        this.temporalIDSelectedReclamo = temporalIDSelectedReclamo;
    }

    public void obtenerReclamosCliente() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.mantenerDatosForm = true;
            if (reclamoDTO == null) {
                reclamoDTO = reclamosDAO.listadoReclamosPorCliente(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());

                if (!reclamoDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !reclamoDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    this.limipiarMensajesFacesContext();

                    FacesContext.getCurrentInstance().addMessage("formConsultaReclamo:datalist", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));                        
                    reclamoDTO = null;

                } else if (reclamoDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && !reclamoDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    this.limipiarMensajesFacesContext();
                }
            }
            listadoReclamos = true;
            sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_CONSULTA_RECLAMOS, "", "", "Consulta General de Reclamos", "", "", "", "", "", "", "");
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrefijoOperadora() {
        return prefijoOperadora;
    }

    public void setPrefijoOperadora(String prefijoOperadora) {
        this.prefijoOperadora = prefijoOperadora;
    }

    public String getNroTlf() {
        return nroTlf;
    }

    public void setNroTlf(String nroTlf) {
        this.nroTlf = nroTlf;
    }

    public Date getFechaEjecucion() {
        return new Date();
    }

    public void setFechaEjecucion(Date fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public String getMediosNotificacionSelected() {
        return mediosNotificacionSelected;
    }

    public void setMediosNotificacionSelected(String mediosNotificacionSelected) {
        this.mediosNotificacionSelected = mediosNotificacionSelected;
    }

    public IbMediosNotificacionDTO getListMediosNotificacionDTO() {
        return listMediosNotificacionDTO;
    }

    public void setListMediosNotificacionDTO(IbMediosNotificacionDTO listMediosNotificacionDTO) {
        this.listMediosNotificacionDTO = listMediosNotificacionDTO;
    }

    public String getTDDSelected() {
        return TDDSelected;
    }

    public void setTDDSelected(String TDDSelected) {
        this.TDDSelected = TDDSelected;
    }

    public Collection<CuentaDTO> getCuentasCliente() {
        return cuentasCliente;
    }

    public void setCuentasCliente(Collection<CuentaDTO> cuentasCliente) {
        this.cuentasCliente = cuentasCliente;
    }

    public Collection<ReclamoDTO> getListTipoReclamos() {
        if (listTipoReclamos == null) {
            listTipoReclamos = new ArrayList<>(reclamosDAO.obtenerListadoReclamos(sesionController.getIdCanal(), sesionController.getNombreCanal()).getReclamos());
        }
        
        
        return listTipoReclamos;
    }

    public void setListTipoReclamos(Collection<ReclamoDTO> listTipoReclamos) {
        this.listTipoReclamos = listTipoReclamos;
    }

    public String getIdtipoReclamoSeleccionado() {
        if(temporalIDSelectedReclamo != null){
            idtipoReclamoSeleccionado = temporalIDSelectedReclamo;
        }
        return idtipoReclamoSeleccionado;
    }

    public void setIdtipoReclamoSeleccionado(String idtipoReclamoSeleccionado) {
        if (idtipoReclamoSeleccionado != null && !idtipoReclamoSeleccionado.equalsIgnoreCase("-1") && idtipoReclamoSeleccionado.contains(":")) {
            temporalIDSelectedReclamo = idtipoReclamoSeleccionado;
            String[] split = idtipoReclamoSeleccionado.split(":");
            this.setNombretipoReclamoSeleccionado(split[1]);
            this.idtipoReclamoSeleccionado = split[0];
        } else {
            this.idtipoReclamoSeleccionado = idtipoReclamoSeleccionado;
        }
    }

    public String getMovimientosAsociados() {
        return movimientosAsociados;
    }

    public void setMovimientosAsociados(String movimientosAsociados) {
        this.movimientosAsociados = movimientosAsociados;
    }

    public boolean isActivaMovimientosCuenta() {
        return activaMovimientosCuenta;
    }

    public void setActivaMovimientosCuenta(boolean activaMovimientosCuenta) {
        this.activaMovimientosCuenta = activaMovimientosCuenta;
    }

    public Date getTxtFechaDesde() {
        return txtFechaDesde;
    }

    public void setTxtFechaDesde(Date txtFechaDesde) {
        this.txtFechaDesde = txtFechaDesde;
    }

    public Date getTxtFechaHasta() {
        return txtFechaHasta;
    }

    public void setTxtFechaHasta(Date txtFechaHasta) {
        this.txtFechaHasta = txtFechaHasta;
    }

    public CuentaDTO getCuentaDTO() {
        return cuentaDTO;
    }

    public void setCuentaDTO(CuentaDTO cuentaDTO) {
        this.cuentaDTO = cuentaDTO;
    }

    public Collection<MovimientoCuentaDTO> getListMovimientoCuentaDTO() {
        return listMovimientoCuentaDTO;
    }

    public void setListMovimientoCuentaDTO(Collection<MovimientoCuentaDTO> listMovimientoCuentaDTO) {
        this.listMovimientoCuentaDTO = listMovimientoCuentaDTO;
    }

    public Date getCalendarFechaMax() {
        return calendarFechaMax;
    }

    public void setCalendarFechaMax(Date calendarFechaMax) {
        this.calendarFechaMax = calendarFechaMax;
    }

    /**
     * get que obtiene la fecha minima para la seleccion del calendario
     *
     * @return fecha minima
     */
    public Date getCalendarFechaMin() {

        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, Integer.parseInt(parametrosController.getNombreParametro("pnw.global.filtrar.meses", sesionController.getIdCanal())));
        calendarFechaMin = c.getTime();
        return calendarFechaMin;
    }

    public void setCalendarFechaMin(Date calendarFechaMin) {
        this.calendarFechaMin = calendarFechaMin;
    }

    public String getCuentaSeleccionada() {
        return cuentaSeleccionada;
    }

    public void setCuentaSeleccionada(String cuentaSeleccionada) {
        this.cuentaSeleccionada = cuentaSeleccionada;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public ArrayList<MovimientoCuentaDTO> getListMovimientoSeleccionado() {
        return listMovimientoSeleccionado;
    }

    public void setListMovimientoSeleccionado(ArrayList<MovimientoCuentaDTO> listMovimientoSeleccionado) {
        this.listMovimientoSeleccionado = listMovimientoSeleccionado;
    }

    public TarjetaDebitoDTO getListTDD() {
        return listTDD;
    }

    public void setListTDD(TarjetaDebitoDTO listTDD) {
        this.listTDD = listTDD;
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
     * metodo que retorna la cuenta de origen de pago enmascarada
     *
     * @return la cuenta de origen de pago enmascarada
     */
    public String getCtaMascara() {
        if (datosCuentaSeleccionadaLista == null || datosCuentaSeleccionadaLista.getSemilla() == null || datosCuentaSeleccionadaLista.getSemilla().trim().equalsIgnoreCase("")) {
            this.datosCuentaSeleccionadaLista = new CuentaDTO(sesionController.getSemilla());
        }
        String[] split = cuentaSeleccionada.split(":");
        return this.datosCuentaSeleccionadaLista.getDesEnc().desencriptar(split[1]);
    }

    public String getNombretipoReclamoSeleccionado() {
        return nombretipoReclamoSeleccionado;
    }

    public void setNombretipoReclamoSeleccionado(String nombretipoReclamoSeleccionado) {
        this.nombretipoReclamoSeleccionado = nombretipoReclamoSeleccionado;
    }

    public String getEstatusRegistro() {
        return estatusRegistro;
    }

    public void setEstatusRegistro(String estatusRegistro) {
        this.estatusRegistro = estatusRegistro;
    }

    public boolean isRegistro() {
        return registro;
    }

    public void setRegistro(boolean registro) {
        this.registro = registro;
    }

    public boolean isListadoReclamos() {
        return listadoReclamos;
    }

    public void setListadoReclamos(boolean listadoReclamos) {
        this.listadoReclamos = listadoReclamos;
    }

    public int getReclamosMovProcesados() {
        return reclamosMovProcesados;
    }

    public void setReclamosMovProcesados(int reclamosMovProcesados) {
        this.reclamosMovProcesados = reclamosMovProcesados;
    }

    public Collection<MovimientoCuentaDTO> getMovSatisfactorios() {
        return movSatisfactorios;
    }

    public void setMovSatisfactorios(Collection<MovimientoCuentaDTO> movSatisfactorios) {
        this.movSatisfactorios = movSatisfactorios;
    }

    public Collection<MovimientoCuentaDTO> getMovError() {
        return movError;
    }

    public void setMovError(Collection<MovimientoCuentaDTO> movError) {
        this.movError = movError;
    }

    public boolean isTieneMovSatisfactorios() {
        return tieneMovSatisfactorios;
    }

    public void setTieneMovSatisfactorios(boolean tieneMovSatisfactorios) {
        this.tieneMovSatisfactorios = tieneMovSatisfactorios;
    }

    public boolean isTieneMovError() {
        return tieneMovError;
    }

    public void setTieneMovError(boolean tieneMovError) {
        this.tieneMovError = tieneMovError;
    }

    /**
     * Método que recibe 2 montos BigDecimal y retorna el que posea monto mayor
     * a cero. El monto es formateado y retornado como un String
     *
     * @param ingreso
     * @param egreso
     * @return BigDecimal
     */
    public String obtieneMontoValido(BigDecimal ingreso, BigDecimal egreso) {

        BigDecimal value;

        if (ingreso == null && egreso == null) {
            return "";
        }

        if (ingreso.compareTo(egreso) > 0) {
            value = ingreso;
            DecimalFormatSymbols mySymbols = new DecimalFormatSymbols();
            mySymbols.setDecimalSeparator(',');
            mySymbols.setGroupingSeparator('.');
            DecimalFormat newFormat = new DecimalFormat("#,##0.00", mySymbols);
            return newFormat.format(value.doubleValue());
        } else {
            value = egreso;
            DecimalFormatSymbols mySymbols = new DecimalFormatSymbols();
            mySymbols.setDecimalSeparator(',');
            mySymbols.setGroupingSeparator('.');
            DecimalFormat newFormat = new DecimalFormat("#,##0.00", mySymbols);
            String valor = newFormat.format(value.doubleValue());
            if (!valor.equalsIgnoreCase("0,00") && !valor.contains("-")) {
                valor = "-" + valor;
            }
            return valor;
        }
    }

    /**
     * metodo action para ir a paso 1 de registro de reclamo
     */
    public void registroPaso1() {
        this.mantenerDatosForm = false;
        limpiarRegReclamo();
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.regReclamo.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * metodo action para ir a paso 1 de registro de reclamo
     */
    public void regresarRegistroPaso1() {
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.regReclamo.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * metodo action para ir a paso 2 de registro de reclamo
     */
    public void registroPaso2() {
        this.activaMovimientosCuenta = true;
        int errores = 0;
        //se realizan las validaciones del formulario aqui para permitir los ajax onChange de TDD y cuentas 
        this.limipiarMensajesFacesContext();

        if (this.observacion == null || this.observacion.trim().equalsIgnoreCase("")) {
            errores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRegReclamo1:txtAreaObservacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        } else {
            if (!observacion.matches("^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ .,¿?-]*$")) {
                errores++;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formRegReclamo1:txtAreaObservacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.AlfaNumInvl", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.AlfaNumInvl", sesionController.getNombreCanal())));
            }
        }

        if (this.mediosNotificacionSelected == null || this.mediosNotificacionSelected.equalsIgnoreCase("-1")) {
            errores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRegReclamo1:mediosNotificacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }

        if (this.idtipoReclamoSeleccionado == null || this.idtipoReclamoSeleccionado.equalsIgnoreCase("-1")) {
            errores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRegReclamo1:tipoReclamo", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }

        if (this.TDDSelected == null || this.TDDSelected.equalsIgnoreCase("-1")) {
            errores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRegReclamo1:TDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }

        if (this.cuentaSeleccionada == null || this.cuentaSeleccionada.equalsIgnoreCase("-1")) {
            errores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRegReclamo1:cuentaSeleccionada", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }

        if (movimientosAsociados.equalsIgnoreCase(textosController.getNombreTexto("pnw.global.label.si", sesionController.getNombreCanal()))
                && (listMovimientoSeleccionado == null || listMovimientoSeleccionado.isEmpty())) {
            errores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRegReclamo1:divFiltroFecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.sinMovimientos", sesionController.getNombreCanal())));
        }

        if (errores > 0) {
            this.mantenerDatosForm = true;
            sesionController.setValidadorFlujo(1);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.regReclamo.url.paso1", sesionController.getIdCanal()));
            return;
        }

        this.movSatisfactorios = new ArrayList<>();
        this.movError = new ArrayList<>();
        this.tieneMovSatisfactorios = false;
        this.tieneMovError = false;

        sesionController.setValidadorFlujo(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.regReclamo.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo action para ir a paso 3 de registro de reclamo
     *
     */
    public void registroPaso3() {

        if (this.movimientosAsociados.equalsIgnoreCase(textosController.getNombreTexto("pnw.global.label.no", sesionController.getIdCanal()))) {
            RespuestaDTO respuesta = new RespuestaDTO();
            ReclamoDTO reclamo = new ReclamoDTO();
            reclamo = reclamosDAO.insertarReclamoCliente(sesionController.getUsuario().getCodUsuario(), this.idtipoReclamoSeleccionado, null, this.observacion, sesionController.getNombreCanal());
            respuesta=reclamo.getRespuestaDTO();
            if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                this.registro = false;
                this.listadoReclamos = false;
                this.mantenerDatosForm = false;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formRegReclamo2:divMsj", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.regReclamo.url.paso2", sesionController.getIdCanal()));
                return;
            } else {
                if (!respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    this.registro = false;
                    this.listadoReclamos = false;
                    this.mantenerDatosForm = false;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formRegReclamo2:divMsj", new FacesMessage(FacesMessage.SEVERITY_WARN, respuesta.getTextoSP(), respuesta.getTextoSP()));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.regReclamo.url.paso2", sesionController.getIdCanal()));
                    return;
                } else {
                    registro = true;
                    listadoReclamos = false;
                    this.mantenerDatosForm = false;
                }
                
                /////////NOTIFICACION VIA SMS////////
                String textoSMS = textosController.getNombreTexto("pnw.global.sms.cuerpo.registroReclamo", sesionController.getNombreCanal());
                String motivoSMS = textosController.getNombreTexto("pnw.global.sms.motivo.registroReclamo", sesionController.getNombreCanal());
                 HashMap<String, String> parametros = new HashMap();
                parametros.put("\\$PNREC",reclamo.getNumeroSolicitud());
                sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);

                //Envío email
                notificarRegistroReclamo(reclamo.getNumeroSolicitud());
            }
        } else {
            this.movSatisfactorios = new ArrayList<>();
            this.movError = new ArrayList<>();
            this.tieneMovSatisfactorios = false;
            this.tieneMovError = false;
            this.reclamosMovProcesados = 0;
            RespuestaDTO respuesta = new RespuestaDTO();
            ReclamoDTO reclamo = new ReclamoDTO();
             StringBuilder reclamos = new StringBuilder();
            for (MovimientoCuentaDTO i : this.listMovimientoSeleccionado) {

                reclamo = reclamosDAO.insertarReclamoCliente(sesionController.getUsuario().getCodUsuario(), this.idtipoReclamoSeleccionado, i.getSecuenciaExtMovimiento(), this.observacion, sesionController.getNombreCanal());
                respuesta=reclamo.getRespuestaDTO();
                
                if (respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    this.movSatisfactorios.add(i);
                    this.tieneMovSatisfactorios = true;
                    reclamos.append(",").append(reclamo.getNumeroSolicitud());
                } else {
                    this.movError.add(i);
                    this.tieneMovError = true;
                }
                this.reclamosMovProcesados++;
            }
            if (movSatisfactorios.isEmpty()) {
                if (this.listMovimientoSeleccionado.size() == 1) {
                    if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        this.registro = false;
                        this.listadoReclamos = false;
                        this.mantenerDatosForm = false;
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formRegReclamo2:divMsj",new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.regReclamo.url.paso2", sesionController.getIdCanal()));
                        return;
                    } else {
                        if (!respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            this.registro = false;
                            this.listadoReclamos = false;
                            this.mantenerDatosForm = false;
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formRegReclamo2:divMsj",
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getTextoSP()));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.regReclamo.url.paso2", sesionController.getIdCanal()));
                            return;
                        }
                    }
                } else if (this.listMovimientoSeleccionado.size() > 1) {
                    if (!respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        this.registro = false;
                        this.listadoReclamos = false;
                        this.mantenerDatosForm = false;
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.regReclamo.url.paso2", sesionController.getIdCanal()));
                        return;
                    } else {
                        if (!respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            this.registro = false;
                            this.listadoReclamos = false;
                            this.mantenerDatosForm = false;
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.regReclamo.url.paso2", sesionController.getIdCanal()));
                            return;
                        }
                    }
                }
            } else {
                this.registro = true;
                this.listadoReclamos = false;
                this.mantenerDatosForm = false;
                
                /////////NOTIFICACION VIA SMS////////
                String textoSMS = textosController.getNombreTexto("pnw.global.sms.cuerpo.registroReclamo", sesionController.getNombreCanal());
                String motivoSMS = textosController.getNombreTexto("pnw.global.sms.motivo.registroReclamo", sesionController.getNombreCanal());
                  HashMap<String, String> parametros = new HashMap();
                parametros.put("\\$PNREC",reclamos.toString());
                sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);

                //Envío email
                notificarRegistroReclamo(reclamos.toString());
            }
        }

       
        
        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_REG_RECLAMOS, "", "", "Registro de Reclamo Exitoso", "", "", "", "", "", "", "");
        sesionController.setValidadorFlujo(3);
        this.mantenerDatosForm = false;

        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.regReclamo.url.paso3", sesionController.getIdCanal()));
    }

    /**
     * metodo action para ir a consulta de registro
     */
    public void consultaReclamos() {
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() - 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.consultaReclamos.url", sesionController.getIdCanal()));
    }

    /**
     * metodo action para ir a consulta de registro
     *
     * @param reclamoSelected
     */
    public void detalleReclamos(ReclamoDTO reclamoSelected) {
        this.reclamoSelected = reclamoSelected;
        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_CONSULTA_RECLAMOS, "", "", "Consulta Detalle de Reclamos", "", "", "", "", "", "", "");
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.consultaReclamos.url.detalle", sesionController.getIdCanal()));
    }

    public void inicio() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            sesionController.setValidadorFlujo(1);
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujo2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_CONSULTA_RECLAMOS)) {
                sesionController.setValidadorFlujo(1);
                this.consultaReclamos();
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
    public void cuerpoConsultaReclamosPDF(Document pdf) throws BadElementException, DocumentException {
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

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(ReclamoController.class.getResource("/imgPDF/logoBanner.png"));

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

            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.consultaReclamo.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));

            cell.setPaddingLeft(180f);
            cell.setColspan(2);
            cell.setBorder(0);

            pdf.add(new Paragraph(" "));
            cell.setPaddingBottom(30f);
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
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaReclamo.descargaPdf.nroSolicitud", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(reclamoSelected.getNumeroSolicitud(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaReclamo.descargaPdf.fechaReclamo", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(fecha2(this.reclamoSelected.getFechaSolicitudDate()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaReclamo.descargaPdf.descripcion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.reclamoSelected.getNombreReclamo(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaReclamo.descargaPdf.estatus", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.reclamoSelected.getEstatus(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaReclamo.descargaPdf.monto", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase("Bs. " + formatearMonto(this.reclamoSelected.getMontoReclamo()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.consultaReclamo.descargaPdf.fechaSolucion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase((this.reclamoSelected.getFechaSolucionDate() != null ? fecha2(this.reclamoSelected.getFechaSolucionDate()) : ""), font));

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
     * Método encargado de generar el contenido para el registro de reclamos
     *
     * @param pdf
     * @throws BadElementException
     * @throws DocumentException
     */
    public void cuerpoRegistrarRecamoPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            cuentaDTO = new CuentaDTO(this.sesionController.getSemilla());

            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 11, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //Variables para el manejo de la data
            String montoMov;

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(ReclamoController.class.getResource("/imgPDF/logoBanner.png"));

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

            pdf.add(table);
            pdf.add(new Paragraph(" "));

            table = new PdfPTable(2);

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas0 = {1.95f, 3.05f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas0);

            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));

            cell.setPaddingLeft(180f);
            cell.setColspan(2);
            cell.setBorder(0);

            pdf.add(new Paragraph(" "));
            cell.setPaddingBottom(30f);
            table.addCell(cell);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(null);
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);
            table.setWidthPercentage(70f);

            //Títulos de las Columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.fechaEjecucion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(fecha2(this.getFechaEjecucion()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.estatusRegistro", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.estatusRegistro, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.email", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.email, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.nroCelular", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.nroTlf, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.medioNotif", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.mediosNotificacionSelected, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.tdd", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.TDDSelected, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.cuentas", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(formatoAsteriscosWeb(this.getCtaMascara()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.tipoReclamo", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.nombretipoReclamoSeleccionado, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.observacion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.observacion, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.pregunta", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.movimientosAsociados, font));

            if (this.activaMovimientosCuenta) {
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.reclamosMovProcesados", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(String.valueOf(this.reclamosMovProcesados), font));
            }

            pdf.add(new Paragraph(" "));
            pdf.add(table);

            if (this.tieneMovSatisfactorios) {
                table = new PdfPTable(1);
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                pdf.add(new Paragraph(" "));
                //Movimiento sobre la cuenta/reclamo
                PdfPCell cell2 = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.movSatisfactorios", sesionController.getIdCanal()), fontTituloBold)));

                cell2.setPaddingLeft(-2f);
                cell2.setColspan(3);
                cell2.setBorder(0);

                cell2.setPaddingBottom(2f);
                cell2.setPaddingRight(1);
                table.addCell(cell2);

                pdf.add(table);

                table = new PdfPTable(4);
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.fechaMov", sesionController.getIdCanal()), fontBold));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.numeroReferencia", sesionController.getIdCanal()), fontBold));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.descripcionMov", sesionController.getIdCanal()), fontBold));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.montoMov", sesionController.getIdCanal()), fontBold));

                for (MovimientoCuentaDTO movi : movSatisfactorios) {

                    table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                    table.addCell(new Phrase(movi.getFechaTransaccionString(), font));
                    table.addCell(new Phrase(movi.getNumeroReferencia(), font));
                    table.addCell(new Phrase(movi.getDescripcion(), font));

                    montoMov = obtieneMontoValido(movi.getIngreso(), movi.getEgreso());

                    table.addCell(new Phrase((montoMov != null ? "Bs. " + montoMov : " - "), font));

                }
                pdf.add(table);
            }

            if (this.tieneMovError) {
                table = new PdfPTable(1);
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                pdf.add(new Paragraph(" "));
                //Movimiento sobre la cuenta/reclamo
                PdfPCell cell2 = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.movError", sesionController.getIdCanal()), fontTituloBold)));

                cell2.setPaddingLeft(-2f);
                cell2.setColspan(3);
                cell2.setBorder(0);

                cell2.setPaddingBottom(2f);
                cell2.setPaddingRight(1);
                table.addCell(cell2);

                pdf.add(table);

                table = new PdfPTable(4);
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.fechaMov", sesionController.getIdCanal()), fontBold));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.numeroReferencia", sesionController.getIdCanal()), fontBold));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.descripcionMov", sesionController.getIdCanal()), fontBold));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.registroReclamo.descargaPdf.montoMov", sesionController.getIdCanal()), fontBold));

                for (MovimientoCuentaDTO movi : movError) {

                    table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                    table.addCell(new Phrase(movi.getFechaTransaccionString(), font));
                    table.addCell(new Phrase(movi.getNumeroReferencia(), font));
                    table.addCell(new Phrase(movi.getDescripcion(), font));

                    montoMov = obtieneMontoValido(movi.getIngreso(), movi.getEgreso());

                    table.addCell(new Phrase((montoMov != null ? "Bs. " + montoMov : " - "), font));

                }
                pdf.add(table);
            }

            //Añadimos la tabla al PDF
            // pdf.setMargins(150f, 20f, 50f, 10f);
            pdf.setMargins(50f, 3f, 22f, 18f);
            pdf.add(new Paragraph(" "));
        } catch (Exception e) {
            //manejar excepciones de reportes
        }
    }

    /**
     * metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     */
    public void cuerpoListadoReclamosPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 11, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //Variables para el manejo del monto, Phrase espera siempre un String
            String monto;

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(HistoricoTransaccionesController.class.getResource("/imgPDF/logoBanner.png"));

            //Transparencia de la imagen
            image.setTransparency(new int[]{0x00, 0x10});
            image.scaleAbsolute(160f, 60f);
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
            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell();
            PdfPCell cell1 = new PdfPCell();
            PdfPCell cell2 = new PdfPCell();
            PdfPCell cell3 = new PdfPCell();

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas0 = {1.95f, 3.05f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas0);
        //    table.getDefaultCell().setBorder(0);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(null);
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);
            table.getDefaultCell().setBorder(0);

            cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.listadoReclamo.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));
            table.getDefaultCell().setRowspan(3);
            cell.setRowspan(3);
            cell.setBorder(0);
            cell.setMinimumHeight(30f);
            cell.setBackgroundColor(Color.white);
            table.addCell(cell);

            //Agregamos la tabla al PDF
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            table = new PdfPTable(4);

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas2 = {0.55f, 0.95f, 0.95f, 0.55f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas2);
        //    table.getDefaultCell().setBorder(0);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            //Títulos de las columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.listadoReclamo.descargaPdf.nroSolicitud", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.listadoReclamo.descargaPdf.fechaReclamo", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.listadoReclamo.descargaPdf.descripcion", sesionController.getIdCanal()), fontBold));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.listadoReclamo.descargaPdf.estatus", sesionController.getIdCanal()), fontBold));

            table.getDefaultCell().setBackgroundColor(null);
            if (!reclamoDTO.getReclamos().isEmpty()) {
                for (ReclamoDTO reclamo : reclamoDTO.getReclamos()) {
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(new Phrase((String) (reclamo.getNumeroSolicitud() != null ? reclamo.getNumeroSolicitud() : ""), font));
                    table.addCell(new Phrase((reclamo.getFechaSolicitudDate() != null ? formatearFecha(reclamo.getFechaSolicitudDate(), FORMATO_FECHA_HORA_MINUTO) : ""), font));
                    table.addCell(new Phrase((String) (reclamo.getNombreReclamo() != null ? reclamo.getNombreReclamo() : ""), font));
                    table.addCell(new Phrase((reclamo.getEstatus() != null ? reclamo.getEstatus() : ""), font));

                }

            } else {
                //Si no hay data recuperada, se muestra un mensaje
                table.getDefaultCell().setColspan(5);
                table.getDefaultCell().setPaddingTop(3);
                table.getDefaultCell().setPaddingBottom(3);
                table.getDefaultCell().setPaddingRight(3);
                table.getDefaultCell().setPaddingLeft(3);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.dataTable.texto.vacio", sesionController.getIdCanal()), font));

            }

            //Añadimos la tabla al PDF
            // pdf.setMargins(150f, 20f, 50f, 10f);
            pdf.add(table);
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
    public void detalleConsultaReclamosPDF() throws IOException, DocumentException {

        try {
            String nombreDocumento;

            if (registro) {
                nombreDocumento = "RegistroReclamo.pdf";
            } else {
                if (listadoReclamos) {
                    nombreDocumento = "ListadoReclamos.pdf";
                } else {
                    nombreDocumento = "ConsultaReclamo.pdf";
                }
            }

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
            //    response.addHeader("Content-disposition", "attachment;filename=\"ConsultaReclamo.pdf\"");
            response.addHeader("Content-disposition", "attachment;filename=\"" + nombreDocumento + "\"");
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
            if (registro) {
                this.cuerpoRegistrarRecamoPDF(document);
            } else {
                if (listadoReclamos) {
                    this.cuerpoListadoReclamosPDF(document);
                } else {
                    this.cuerpoConsultaReclamosPDF(document);
                }
            }
            //ceramos el documento
            document.close();

            response.setContentLength(baos.size());
            ServletOutputStream out = response.getOutputStream();
            baos.writeTo(out);
            //  baos.flush();
            faces.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo para consultar las cuentas propias
     */
    public void consultarCuentasPropiasActivasCanceladas() {

        this.cuentasCliente = null;
        this.movimientosAsociados = textosController.getNombreTexto("pnw.global.label.si", sesionController.getIdCanal());
        this.listMovimientoCuentaDTO = null;
        this.listMovimientoSeleccionado = null;
        this.activaMovimientosCuenta = true;
        this.txtFechaDesde = null;
        this.txtFechaHasta = null;
        this.cuentaSeleccionada = "-1";

        //si no selecciona ninguna TDD se limpian las cuentas y todo lo asociado a ella
        if (this.TDDSelected == null || this.TDDSelected.equalsIgnoreCase("-1")) {
            return;
        }

        ClienteDTO datosCliente = clienteDAO.listCuentasActivasCanceladas(sesionController.getUsuario().getCodUsuario(), TDDSelected, sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (!datosCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //manejar mensaje de error 
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

    public void inicioRegReclamo() {
        if (sesionController.isReiniciarForm()) {
            if (!FacesContext.getCurrentInstance().isPostback()) {
                this.email = sesionController.getUsuario().getEmail();
                this.nroTlf = sesionController.getUsuario().getCelular();
                this.listMediosNotificacionDTO = mediosNotificacionDAO.listaMedios(sesionController.getNombreCanal());
                this.listTDD = tddDAO.obtenerListadoTDDCliente(sesionController.getUsuario().getCodUsuario(), sesionController.getNombreCanal());
                this.cuentaSeleccionada = "-1";
                this.TDDSelected = "-1";
                this.mediosNotificacionSelected = "-1";
                this.cuentasCliente = null;
                this.idtipoReclamoSeleccionado = "-1";
                this.observacion = null;
                this.movimientosAsociados = textosController.getNombreTexto("pnw.global.label.si", sesionController.getIdCanal());
                this.listMovimientoCuentaDTO = null;
                this.listMovimientoSeleccionado = null;
                this.activaMovimientosCuenta = true;
                this.txtFechaDesde = null;
                this.txtFechaHasta = null;
                this.registro = false;
                this.listadoReclamos = false;
                this.movSatisfactorios = new ArrayList<>();
                this.movError = new ArrayList<>();
                this.tieneMovSatisfactorios = false;
                this.tieneMovError = false;
                sesionController.setReiniciarForm(false);
                sesionController.setValidadorFlujo(1);
                this.temporalIDSelectedReclamo = "-1";
            }
        }
    }

    /**
     * metodo utilitario para reiniciar los valores del registro de reclamos
     */
    public void limpiarRegReclamo() {
        if (!this.mantenerDatosForm) {
            this.email = sesionController.getUsuario().getEmail();
            this.nroTlf = sesionController.getUsuario().getCelular();
            this.listMediosNotificacionDTO = null;
            this.listTDD = null;
            this.cuentasCliente = null;
            this.idtipoReclamoSeleccionado = "-1";
            this.observacion = null;
            this.movimientosAsociados = null;
            this.listMovimientoCuentaDTO = null;
            this.listMovimientoSeleccionado = null;
            this.activaMovimientosCuenta = false;
            this.txtFechaDesde = null;
            this.txtFechaHasta = null;
            this.registro = false;
            this.listadoReclamos = false;
            sesionController.setValidadorFlujo(1);
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoRegistro2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_REG_RECLAMOS)) {
                sesionController.setValidadorFlujo(1);
                this.registroPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoRegistro3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_REG_RECLAMOS)) {
                sesionController.setValidadorFlujo(1);
                this.registroPaso1();
            }
        }
    }

    public void cuentasInicializar() {
        this.movimientosAsociados = textosController.getNombreTexto("pnw.global.label.si", sesionController.getIdCanal());
        this.listMovimientoCuentaDTO = null;
        this.listMovimientoSeleccionado = null;
        this.activaMovimientosCuenta = true;
        this.txtFechaDesde = null;
        this.txtFechaHasta = null;
        this.registro = false;
        this.limipiarMensajesFacesContext();
    }

    public void movimientosAsociadosReclamos() {
        if (movimientosAsociados.equalsIgnoreCase("Si")) {
            this.activaMovimientosCuenta = true;
        } else {
            this.activaMovimientosCuenta = false;
        }
    }

    /**
     * metodo para filtrar los movimientos de la cuenta por fecha, por tipo de
     * transaccion y por nro de referencia
     *
     * @return lista de movimientos filtrada
     */
    public String filtrar() {

        cuentaDTO = new CuentaDTO(this.sesionController.getSemilla());
        CuentaDTO cuentaDTOMov = new CuentaDTO(this.sesionController.getSemilla());
        String[] split = cuentaSeleccionada.split(":");

        if (this.cuentaSeleccionada == null || this.cuentaSeleccionada.equalsIgnoreCase("-1")) {
            FacesContext.getCurrentInstance().addMessage("formRegReclamo1:cuentaSeleccionada", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getIdCanal())));
            return "";
        } else if (txtFechaDesde == null && txtFechaHasta == null) {
            FacesContext.getCurrentInstance().addMessage("formRegReclamo1:divFiltroFecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.seleccioneUno", sesionController.getIdCanal())));
            return "";
        } else if ((txtFechaDesde != null && txtFechaHasta == null) || (txtFechaDesde == null && txtFechaHasta != null)) {
            FacesContext.getCurrentInstance().addMessage("formRegReclamo1:divFiltroFecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.rangoFechaInv", sesionController.getIdCanal())));
            return "";
        } else {
            if (txtFechaDesde != null && txtFechaHasta != null) {
                //SE VERIFICA SI LA FECHA DESDE ES MAYOR A LA FECHA HASTA
                if (txtFechaDesde.compareTo(txtFechaHasta) > 0) {
                    FacesContext.getCurrentInstance().addMessage("formRegReclamo1:divFiltroFecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.rangoFechaInv", sesionController.getIdCanal())));
                    return "";
                }
            }

            boolean validacionesSeguridad = sesionController.productosCliente(cuentaDTO.getDesEnc().desencriptar(split[1]));

            if (validacionesSeguridad) {
                cuentaDTOMov = reclamosDAO.listarMovimientos(cuentaDTO.getDesEnc().desencriptar(split[0]), cuentaDTO.getDesEnc().desencriptar(split[1]), fecha(this.getTxtFechaDesde()), fecha(this.getTxtFechaHasta()),
                        parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                        parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                        parametrosController.getNombreParametro("pnw.global.orden.desc", sesionController.getIdCanal()),
                        sesionController.getIdCanal(), sesionController.getNombreCanal());

                if (!cuentaDTOMov.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formRegReclamo1:divFiltroFecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    return "";
                } else if (cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_SIN_RESULTADOS_JPA)) {
                    return "";
                } else if (!cuentaDTOMov.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formRegReclamo1:divFiltroFecha", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTOMov.getRespuestaDTO().getTextoSP()));
                    return "";
                }

                listMovimientoCuentaDTO = cuentaDTOMov.getMovimientosDTO();

            } else {
                RespuestaDTO respuesta = new RespuestaDTO();
                respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
                respuesta.setTextoSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
                cuentaDTOMov.setRespuestaDTO(respuesta);

                FacesContext.getCurrentInstance().addMessage("formRegReclamo1:divFiltroFecha", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaDTOMov.getRespuestaDTO().getTextoSP()));
                return "";
            }

        }
        return "";
    }

    
    //Notificación de Pago de Préstamo realizado vía email
    /**
     * Metodo que setea los datos y parametros para el envio del correo luego
     * del proceso del Pago de Préstamo
     */
    public void notificarRegistroReclamo(String NumeroReclamo) {

        String asunto = "Notificación Registro Reclamo";
        StringBuilder texto = new StringBuilder("");

        texto = new StringBuilder("Estimado(a) ");
        texto.append(sesionController.getUsuario().getNombre());
        texto.append(NUEVALINEAEMAIL);
        texto.append(NUEVALINEAEMAIL);
        texto.append(parametrosController.getNombreParametro("banco.nombre", sesionController.getIdCanal()));
        texto.append(" le informa que se ha realizado un Registro de Reclamo nro: ");
        texto.append(NumeroReclamo);
        texto.append(" desde su Internet Banking Persona Natural de manera exitosa ");
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

    
}
