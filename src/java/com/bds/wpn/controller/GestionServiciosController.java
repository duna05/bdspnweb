/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.GestionServiciosDAO;
import com.bds.wpn.dao.IBAcumuladoTransaccionDAO;
import com.bds.wpn.dto.AfiliacionServicioDTO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.ServicioDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
 * @author cesar.mujica
 */
@Named("wpnGestionServiciosController")
@SessionScoped
public class GestionServiciosController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;
    @EJB
    IbParametrosFacade parametrosFacade;
    @EJB
    IbTextosFacade textosFacade;
    @EJB
    GestionServiciosDAO serviciosDAO;
    @EJB
    ClienteDAO clienteDAO;
    @EJB
    IBAcumuladoTransaccionDAO acumuladoDAO;

    @Inject
    TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    private AfiliacionServicioDTO afiliacion = new AfiliacionServicioDTO();
    private List<ServicioDTO> servicios;
    private int servicioSeleccionado;
    private int servicioSeleccionadoRecarga;
    private ServicioDTO servSeleccionadoCompleto;
    private List<AfiliacionServicioDTO> afiliaciones;
    private List<AfiliacionServicioDTO> afiliacionesDesafiliar;
    private int afiliacionSeleccionada;
    private boolean mantenerDatosForm = false;              //indicador para mantener los datos del formulario
    private boolean insertarBeneficiario;
    private List<CuentaDTO> cuentasCliente;
    private String montoRecarga;
    private String ctaSeleccionada;
    private CuentaDTO datosCuentaSeleccionada;
    private Date fechaEjecucion;
    private String numeroRecibo;
    private boolean mostrarBotonDesafiliar = false;

    private boolean horarioHabilitado = true;
    private boolean validaCierreOperaciones = true;
    private boolean mostrarMensajeCierre = false;

    //////////////GETTERS Y SETTERS////////
    public AfiliacionServicioDTO getAfiliacion() {
        return afiliacion;
    }

    public void setAfiliacion(AfiliacionServicioDTO afiliacion) {
        this.afiliacion = afiliacion;
    }

    public List<ServicioDTO> getServicios() {
        return servicios;
    }

    public void setServicios(List<ServicioDTO> servicios) {
        this.servicios = servicios;
    }

    public int getServicioSeleccionado() {
        return servicioSeleccionado;
    }

    public void setServicioSeleccionado(int servicioSeleccionado) {
        this.servicioSeleccionado = servicioSeleccionado;
        for (ServicioDTO serv : this.servicios) {
            if (serv.getCodigoServicio() == this.servicioSeleccionado) {
                this.servSeleccionadoCompleto = serv;
            }
        }
    }

    public int getServicioSeleccionadoRecarga() {
        return servicioSeleccionadoRecarga;
    }

    public void setServicioSeleccionadoRecarga(int servicioSeleccionadoRecarga) {
        this.servicioSeleccionadoRecarga = servicioSeleccionadoRecarga;
        for (ServicioDTO serv : this.servicios) {
            if (serv.getCodigoServicio() == this.servicioSeleccionadoRecarga) {
                this.servSeleccionadoCompleto = serv;
            }
        }
    }

    public boolean isMantenerDatosForm() {
        return mantenerDatosForm;
    }

    public void setMantenerDatosForm(boolean mantenerDatosForm) {
        this.mantenerDatosForm = mantenerDatosForm;
    }

    public boolean isInsertarBeneficiario() {
        return insertarBeneficiario;
    }

    public void setInsertarBeneficiario(boolean insertarBeneficiario) {
        this.insertarBeneficiario = insertarBeneficiario;
    }

    public Date getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(Date fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public ServicioDTO getServSeleccionadoCompleto() {
        return servSeleccionadoCompleto;
    }

    public void setServSeleccionadoCompleto(ServicioDTO servSeleccionadoCompleto) {
        this.servSeleccionadoCompleto = servSeleccionadoCompleto;
    }

    public List<AfiliacionServicioDTO> getAfiliaciones() {
        return afiliaciones;
    }

    public void setAfiliaciones(List<AfiliacionServicioDTO> afiliaciones) {
        this.afiliaciones = afiliaciones;
    }

    public List<AfiliacionServicioDTO> getAfiliacionesDesafiliar() {
        return afiliacionesDesafiliar;
    }

    public void setAfiliacionesDesafiliar(List<AfiliacionServicioDTO> afiliacionesDesafiliar) {
        this.afiliacionesDesafiliar = afiliacionesDesafiliar;
    }

    public int getAfiliacionSeleccionada() {
        return afiliacionSeleccionada;
    }

    public void setAfiliacionSeleccionada(int afiliacionSeleccionada) {
        this.afiliacionSeleccionada = afiliacionSeleccionada;
        if (this.afiliacionSeleccionada == -1) {
            this.afiliacion = new AfiliacionServicioDTO();
        } else {
            for (AfiliacionServicioDTO afi : afiliaciones) {
                if (afi.getAfiliacionID() == afiliacionSeleccionada) {
                    afiliacion = afi;
                    return;
                }
            }
        }
    }

    public String getMontoRecarga() {
        return montoRecarga;
    }

    public void setMontoRecarga(String montoRecarga) {
        this.montoRecarga = montoRecarga;
    }

    public List<CuentaDTO> getCuentasCliente() {
        ClienteDTO datosCliente = clienteDAO.listadoCuentasClientes(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (!datosCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //manejar mensaje de error 
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage("formRecargarServ1:selectCtaDebitar", fMsg);
        } else {
            //se consultan las afiliaciones para dicha transaccion
            if (datosCliente.getCuentasAhorroCorrienteDTO().size() > 0) {
                if (datosCliente.getCuentasAhorroDTO() != null) {
                    for (int i = 0; i < datosCliente.getCuentasAhorroDTO().size(); i++) {
                        datosCliente.getCuentasAhorroDTO().get(i).setSemilla(sesionController.getSemilla());
                    }
                }
                if (datosCliente.getCuentasCorrienteDTO() != null) {
                    for (int i = 0; i < datosCliente.getCuentasCorrienteDTO().size(); i++) {
                        datosCliente.getCuentasCorrienteDTO().get(i).setSemilla(sesionController.getSemilla());
                    }
                }
                this.cuentasCliente = datosCliente.getCuentasAhorroCorrienteDTO();
            }
        }
        return cuentasCliente;
    }

    public void setCuentasCliente(List<CuentaDTO> cuentasCliente) {
        this.cuentasCliente = cuentasCliente;
    }

    public String getCtaSeleccionada() {
        return ctaSeleccionada;
    }

    public void setCtaSeleccionada(String ctaSeleccionada) {
        this.ctaSeleccionada = ctaSeleccionada;
        if (this.ctaSeleccionada.equalsIgnoreCase("-1")) {
            datosCuentaSeleccionada = new CuentaDTO();
        } else {
            for (CuentaDTO cuenta : cuentasCliente) {
                if (cuenta.getNumeroCuentaEnc().equals(ctaSeleccionada)) {
                    datosCuentaSeleccionada = cuenta;
                    break;
                }
            }
        }
    }

    public CuentaDTO getDatosCuentaSeleccionada() {
        return datosCuentaSeleccionada;
    }

    public void setDatosCuentaSeleccionada(CuentaDTO datosCuentaSeleccionada) {
        this.datosCuentaSeleccionada = datosCuentaSeleccionada;
    }

    public String getNumeroRecibo() {
        return numeroRecibo;
    }

    public void setNumeroRecibo(String numeroRecibo) {
        this.numeroRecibo = numeroRecibo;
    }

    public boolean isMostrarBotonDesafiliar() {
        return mostrarBotonDesafiliar;
    }

    public void setMostrarBotonDesafiliar(boolean mostrarBotonDesafiliar) {
        this.mostrarBotonDesafiliar = mostrarBotonDesafiliar;
    }

    //////////METODOS FUNCIONALES///////////
    /**
     * metodo generico que se encarga de limpiar el formulario
     */
    public void limpiar() {
        if (sesionController.isReiniciarForm()) {
            ServicioDTO listaServ = serviciosDAO.obtenerListadoServicios(GestionServiciosController.CODIGO_CANAL_WEB_WSDESLSUR);
            if (listaServ != null && listaServ.getServicios() != null && !listaServ.getServicios().isEmpty()
                    && listaServ.getRespuestaDTO() != null && listaServ.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                this.servicios = listaServ.getServicios();
            } else {

            }
            this.afiliacion = new AfiliacionServicioDTO();
            this.servSeleccionadoCompleto = null;
            this.mantenerDatosForm = false;
            sesionController.setReiniciarForm(false);
            this.servicioSeleccionado = -1;
        }
    }

    /**
     * metodo generico que se encarga de limpiar el formulario
     */
    public void limpiarRecarga() {
        if (sesionController.isReiniciarForm()) {
            ServicioDTO listaServ = serviciosDAO.obtenerListadoServicios(GestionServiciosController.CODIGO_CANAL_WEB_WSDESLSUR);
            if (listaServ != null && listaServ.getServicios() != null && !listaServ.getServicios().isEmpty()
                    && listaServ.getRespuestaDTO() != null && listaServ.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                this.servicios = listaServ.getServicios();
            } else {

            }
            this.servicioSeleccionadoRecarga = -1;
            this.mantenerDatosForm = false;
            sesionController.setReiniciarForm(false);
            this.ctaSeleccionada = "-1";
            this.afiliacionSeleccionada = -1;
            this.montoRecarga = null;
            this.afiliacion = new AfiliacionServicioDTO();
        }
    }

    /**
     * metodo generico que se encarga de limpiar el formulario
     */
    public void limpiarDesafiliacion() {
        if (sesionController.isReiniciarForm()) {
            ServicioDTO listaServ = serviciosDAO.obtenerListadoServicios(GestionServiciosController.CODIGO_CANAL_WEB_WSDESLSUR);
            if (listaServ != null && listaServ.getServicios() != null && !listaServ.getServicios().isEmpty()
                    && listaServ.getRespuestaDTO() != null && listaServ.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                this.servicios = listaServ.getServicios();
            } else {

            }
            this.servSeleccionadoCompleto = null;
            this.mantenerDatosForm = false;
            this.mostrarBotonDesafiliar = false;
            this.afiliaciones = null;
            this.afiliacionesDesafiliar = null;
            sesionController.setReiniciarForm(false);
            this.servicioSeleccionado = -1;
        }
    }

    /**
     * metodo generico que valida el flujo del pasos de recarga y si no se
     * cumple redireeciona al paso 1
     */
    public void validarFlujoRecargar2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_PAGOS_SERVICIOS)) {
                sesionController.setValidadorFlujo(1);
                this.regresarRecargarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos de recarga y si no se
     * cumple redireeciona al paso 1
     */
    public void validarFlujoRecargar3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_PAGOS_SERVICIOS)) {
                sesionController.setValidadorFlujo(1);
                this.regresarRecargarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoAfiliar2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AFILIARSERV)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoAfiliar3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AFILIARSERV)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoAfiliar4() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AFILIARSERV)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoDesafiliar2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_DESAFILIARSERV)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoDesafiliar3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_DESAFILIARSERV)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoDesafiliar4() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_DESAFILIARSERV)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo utilitario que redirecciona al OTP
     */
    public void regresarPaso1() {
        this.redirectFacesContext("/sec/noticias/noticias.xhtml?faces-redirect=true");
        //this.redirectFacesContext(parametrosController.getNombreParametro("pnw.otp.url.validacionOtp", sesionController.getIdCanal()));
    }

    /**
     * metodo para redirigir a paso 2 de afiliar
     */
    public void regresarAfiliarPaso2() {
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarServ.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo para redirigir a paso 2 de afiliar
     */
    public void regresarDesafiliarPaso2() {
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliarServ.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo que se encargar de redireccionar al paso 3 de la afiliacion de
     * servicios
     */
    public void afiliarPaso3() {
        if (this.servicioSeleccionado == -1) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:selectServAfiliar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarServ.url.paso2", sesionController.getIdCanal()));
        } else {
            sesionController.setValidadorFlujo(3);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarServ.url.paso3", sesionController.getIdCanal()));
        }
    }

    /**
     * metodo que se encarga de redireccionar al paso 4 de la afiliacion de
     * servicios
     */
    public void afiliarPaso4() {
        this.afiliacion.setCodigoCliente(Integer.parseInt(sesionController.getUsuario().getCodUsuario()));
        this.afiliacion.setServicio(servSeleccionadoCompleto);
        RespuestaDTO respuesta = serviciosDAO.agregarAfiliacion(afiliacion, CODIGO_CANAL_WEB_WSDESLSUR, sesionController.getNombreCanal());
        if (respuesta == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ3:afiliacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarServ.url.paso3", sesionController.getIdCanal()));
        } else {
            if (respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ3:afiliacion", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getDescripcionSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarServ.url.paso3", sesionController.getIdCanal()));
            } else {
                sesionController.setValidadorFlujo(4);
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), sesionController.getIdTransaccion(), "", "", "Afiliación de Servicio " + servSeleccionadoCompleto.getDescripcionServicio() + " Nro: " + afiliacion.getCodigoAbonado(),
                        "", "", "", "", "", "", "");
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarServ.url.paso4", sesionController.getIdCanal()));
            }
        }
    }

    /**
     * metodo utilitario para actualizar los datos de las afiliaciones de
     * servicios
     */
    public void actualizarAfiliaciones() {
        this.afiliacion = new AfiliacionServicioDTO();
        this.afiliaciones = new ArrayList<>();
        //si el servicio se selecciona vacio se limpian las afiliaciones 
        if (servicioSeleccionadoRecarga == -1) {
            this.servSeleccionadoCompleto = null;
            this.afiliacionSeleccionada = -1;
        } else {
            //si no se buscan las afiliaciones asociadas al servicio
            for (ServicioDTO serv : this.servicios) {
                if (serv.getCodigoServicio() == this.servicioSeleccionadoRecarga) {
                    this.servSeleccionadoCompleto = serv;
                }
            }
            AfiliacionServicioDTO afiliacionTemp = serviciosDAO.obtenerListadoAfiliaciones(sesionController.getUsuario().getCodUsuario(), this.servSeleccionadoCompleto.getProveedorServicio(), sesionController.getNombreCanal());
            if (afiliacionTemp.getRespuestaDTO() == null || !afiliacionTemp.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formRecargarServ1:selectAfiRecargar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else {
                if (afiliacionTemp.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !afiliacionTemp.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formRecargarServ1:selectAfiRecargar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", afiliacionTemp.getRespuestaDTO().getDescripcionSP()));

                } else {
                    this.afiliaciones = afiliacionTemp.getAfiliacionesServicio();
                }
            }
        }
    }

    /**
     * metodo utilitario para listar los datos de las afiliaciones de un
     * servicio
     */
    public void listarAfiliaciones() {
        this.afiliaciones = new ArrayList<>();
        //si el servicio se selecciona vacio se limpian las afiliaciones 
        if (servicioSeleccionado == -1) {
            this.servSeleccionadoCompleto = null;
            this.mostrarBotonDesafiliar = false;
        } else {
            //si no se buscan las afiliaciones asociadas al servicio
            for (ServicioDTO serv : this.servicios) {
                if (serv.getCodigoServicio() == this.servicioSeleccionado) {
                    this.servSeleccionadoCompleto = serv;
                }
            }
            AfiliacionServicioDTO afiliacionTemp = serviciosDAO.obtenerListadoAfiliaciones(sesionController.getUsuario().getCodUsuario(), this.servSeleccionadoCompleto.getProveedorServicio(), sesionController.getNombreCanal());
            if (afiliacionTemp.getRespuestaDTO() == null || !afiliacionTemp.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formDesafiliarServ2:datalist", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else {
                if (afiliacionTemp.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !afiliacionTemp.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formDesafiliarServ2:datalist", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", afiliacionTemp.getRespuestaDTO().getDescripcionSP()));

                } else {
                    this.afiliaciones = afiliacionTemp.getAfiliacionesServicio();
                    if (!this.afiliaciones.isEmpty()) {
                        this.mostrarBotonDesafiliar = true;
                    }
                }
            }
        }
    }

    /**
     * metodo utilitario para listar los datos de las afiliaciones de un
     * servicio
     */
    public void desafiliarPaso2() {
        if (this.servicioSeleccionado == 0 || this.servicioSeleccionado == -1 || this.afiliacionesDesafiliar == null || afiliacionesDesafiliar.isEmpty()) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formDesafiliarServ2:datalist", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.desafAfiServ", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliarServ.url.paso2", sesionController.getIdCanal()));
        } else {
            sesionController.setValidadorFlujo(3);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliarServ.url.paso3", sesionController.getIdCanal()));
        }
    }

    /**
     * metodo que se encargar de redireccionar al paso 3 de la afiliacion de
     * servicios
     */
    public void desafiliarPaso3() {
        int cantErrores = 0;
        StringBuilder afiliacionesFallidas = new StringBuilder();
        List<AfiliacionServicioDTO> afiTemp = new ArrayList<>();
        RespuestaDTO respuesta;
        for (AfiliacionServicioDTO afiDesa : afiliacionesDesafiliar) {
            respuesta = serviciosDAO.eliminarAfiliacion(afiDesa, sesionController.getNombreCanal());
            if (respuesta == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                cantErrores++;
                if (afiliacionesFallidas.toString().trim().equalsIgnoreCase("")) {
                    afiliacionesFallidas.append(" ");
                    afiliacionesFallidas.append(afiDesa.getBeneficiario());
                    afiliacionesFallidas.append("/");
                    afiliacionesFallidas.append(afiDesa.getCodigoAbonado());
                } else {
                    afiliacionesFallidas.append(", ");
                    afiliacionesFallidas.append(afiDesa.getBeneficiario());
                    afiliacionesFallidas.append("/");
                    afiliacionesFallidas.append(afiDesa.getCodigoAbonado());
                }
            } else {
                afiTemp.add(afiDesa);
            }
        }
        if (cantErrores == afiliaciones.size()) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formDesafiliarServ3:afiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.desafAfiServFinal", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliarServ.url.paso3", sesionController.getIdCanal()));
        } else {
            this.fechaEjecucion = new Date();
            this.afiliaciones.clear();
            this.afiliaciones.addAll(afiTemp);
            if (cantErrores > 0) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formDesafiliarServ4:divMsjGlobal", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.cantDesaFallidas", sesionController.getNombreCanal()) + afiliacionesFallidas.toString()));
            }
            sesionController.setValidadorFlujo(4);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliarServ.url.paso4", sesionController.getIdCanal()));
        }
    }

    /**
     * metodo que se encargar de redireccionar al paso 2 de la recarga de
     * servicios
     */
    public void recargarPaso2() {
        int cantErrores = 0;
        //validacion de servicio vacio
        if (this.servicioSeleccionadoRecarga == -1) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRecargarServ1:selectServRecargar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }
        //validacion de afiliacion vacia
        if (this.afiliacionSeleccionada == -1) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRecargarServ1:selectAfiRecargar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }
        //validacion de cuenta debito vacia
        if (this.ctaSeleccionada.equalsIgnoreCase("-1")) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRecargarServ1:selectCtaDebitar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }
        //validacion de productos del cliente para evitar alteracion de request
        if (!sesionController.productosCliente(datosCuentaSeleccionada.getNumeroCuenta())) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRecargarServ1:selectCtaDebitar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal())));
        }
        //validacion de monto de recarga vacio
        if (this.montoRecarga == null || this.montoRecarga.trim().equalsIgnoreCase("")) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRecargarServ1:txtMtoRecarga", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        } else {
            //monto superior al minimo establecido
            if (Double.valueOf(eliminarformatoSimpleMonto(this.montoRecarga)) < Double.valueOf(parametrosController.getNombreParametro("pnw.recargarServ.digitel.mtoMin", sesionController.getNombreCanal()))) {
                cantErrores++;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formRecargarServ1:txtMtoRecarga", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.recargarServ.digitel.mtoMin", sesionController.getNombreCanal())));
            }
            //monto inferior al maximo establecido
            if (Double.valueOf(eliminarformatoSimpleMonto(this.montoRecarga)) > Double.valueOf(parametrosController.getNombreParametro("pnw.recargarServ.digitel.mtoMax", sesionController.getNombreCanal()))) {
                cantErrores++;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formRecargarServ1:txtMtoRecarga", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.recargarServ.digitel.mtoMax", sesionController.getNombreCanal())));
            }
            //monto no corresponde con el multiplo establecido
            if (!this.esMultiploDe(Double.valueOf(eliminarformatoSimpleMonto(this.montoRecarga)), Double.valueOf(parametrosController.getNombreParametro("pnw.recargarServ.digitel.multipoRec", sesionController.getNombreCanal())))) {
                cantErrores++;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formRecargarServ1:txtMtoRecarga", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.recargarServ.digitel.multipoRec", sesionController.getNombreCanal())));
            }
        }
        //monto menor que saldo disponible se valida contra el saldo en tiempo real 
        if (this.datosCuentaSeleccionada.getSaldoDisponible().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.montoRecarga))) < 0) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRecargarServ1:txtMtoRecarga", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.saldoInsuficiente", sesionController.getNombreCanal())));
        }

        if (cantErrores > 0) {
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.recargarServ.url.paso1", sesionController.getIdCanal()));
        } else {
            sesionController.setValidadorFlujo(2);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.recargarServ.url.paso2", sesionController.getIdCanal()));
        }
    }

    /**
     * metodo para redirigir a paso 2 de recargar
     */
    public void regresarRecargarPaso1() {
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.recargarServ.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * metodo para redigir al paso 3 de recargar
     */
    public void recargarPaso3() {
        UtilDTO util = serviciosDAO.recargar(afiliacion, BigDecimal.valueOf(Double.parseDouble(this.eliminarformatoSimpleMonto(montoRecarga))), datosCuentaSeleccionada.getNumeroCuenta(), this.CODIGO_CANAL_WEB_WSDESLSUR, sesionController.getNombreCanal());
        if (util.getRespuestaDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formRecargarServ2:divRecarga", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.recargarServ.url.paso2", sesionController.getIdCanal()));
        } else {
            if (util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formRecargarServ2:divRecarga", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getDescripcionSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.recargarServ.url.paso2", sesionController.getIdCanal()));
            } else {
                sesionController.setValidadorFlujo(3);
                numeroRecibo = util.getResuladosDTO().get("id").toString();
                this.fechaEjecucion = new Date();
                //Acumulados de transacciones
                acumuladoDAO.insertarAcumuladoTransaccion(sesionController.getUsuario().getId().toString(), this.montoRecarga, ID_TRANS_REC_DIGITEL, sesionController.getNombreCanal(), sesionController.getIdCanal());
                //registro de bitacora
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), this.ID_TRANS_PAGOS_SERVICIOS, this.formatoAsteriscosWeb(datosCuentaSeleccionada.getNumeroCuenta()), "", "Recarga de Servicio " + servSeleccionadoCompleto.getDescripcionServicio() + " Nro: " + afiliacion.getCodigoAbonado(),
                        montoRecarga, numeroRecibo, afiliacion.getBeneficiario().getNombre(), afiliacion.getBeneficiario().getTipoIdentificacion(), afiliacion.getBeneficiario().getIdentificacion(), String.valueOf(afiliacion.getAfiliacionID()), "");
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.recargarServ.url.paso3", sesionController.getIdCanal()));
            }
        }

    }

    //////////////////////GENERACION DE PDF////////////////////////////////////
    /**
     * metodo encargado de armar el reportes de listado del Perfil
     * PerfilBKUsuarios en PDF
     *
     * @throws java.io.IOException
     * @throws com.lowagie.text.DocumentException
     */
    public void detalleRecargaPDF() throws IOException, DocumentException {

        try {

            String nombreDocumento;

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
            //response.addHeader("Content-disposition", "attachment;filename=\"AfiliacionBeneficiario.pdf\"");
            response.addHeader("Content-disposition", "attachment;filename=\"reciboRecarga.pdf\"");
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
            this.cuerpoRecargaPDF(document);

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

    /**
     * metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoRecargaPDF(Document pdf) throws BadElementException, DocumentException {
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
            Image image = Image.getInstance(BeneficiarioController.class.getResource("/imgPDF/logoBanner.png"));

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

            table = new PdfPTable(2);

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas0 = {1.95f, 3.05f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas0);
            //    table.getDefaultCell().setBorder(0);

            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.recargarServ.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));

            cell.setColspan(2);
            cell.setBorder(0);

            pdf.add(new Paragraph(" "));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
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
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.recargarServ.descargaPdf.servicio", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.servSeleccionadoCompleto.getDescripcionServicio(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.recargarServ.descargaPdf.numeroRecibo", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.numeroRecibo, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.recargarServ.descargaPdf.fechaRecarga", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(fecha2(this.getFechaEjecucion()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.recargarServ.descargaPdf.ctaDebitar", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.formatoAsteriscosWeb(this.datosCuentaSeleccionada.getNumeroCuenta()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.recargarServ.descargaPdf.cedulaBeneficiario", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.afiliacion.getBeneficiario().getTipoIdentificacion() + this.afiliacion.getBeneficiario().getIdentificacion(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.recargarServ.descargaPdf.nombreBeneficiario", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.afiliacion.getBeneficiario().getNombre(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.recargarServ.descargaPdf.monto", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase("Bs. " + this.montoRecarga, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.recargarServ.descargaPdf.numeroRecargar", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.afiliacion.getCodigoAbonado(), font));

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
