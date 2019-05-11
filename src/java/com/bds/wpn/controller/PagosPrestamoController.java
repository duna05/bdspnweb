/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.CuentaDAO;
import com.bds.wpn.dao.NotificacionesDAO;
import com.bds.wpn.dao.PrestamoDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.IbCanalDTO;
import com.bds.wpn.dto.MovimientoPrestamoDTO;
import com.bds.wpn.dto.PrestamoDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbCanalFacade;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_DATA_INVALIDA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.NUEVALINEAEMAIL;
import static com.bds.wpn.util.BDSUtil.eliminarformatoSimpleMonto;
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
import java.util.Date;
import java.util.List;
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

/**
 *
 * @author cesar.mujica
 */
@Named("wpnPagosPrestamoController")
@SessionScoped
public class PagosPrestamoController extends BDSUtil implements Serializable {

    @EJB
    IbCanalFacade canalFacade;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    CuentaDAO cuentaDAO;

    @EJB
    PrestamoDAO prestamoDAO;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    NotificacionesDAO notificacionesDAO;

    @Inject
    InicioSesionController sesionController;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;
    
    @Inject
    CanalController canalController;
   
    private boolean tienePrestamos = false;                 //identifica si el cliente posee prestamos
    private boolean mostrarDatosPrestamo = false;           //identifica si se muestran los datos del prestamo seleccionado
    private boolean mantenerDatosForm = false;              //indicador para mantener los datos del formulario
    private List<CuentaDTO> cuentasCliente = null;          //collection de cuentas propias
    private List<PrestamoDTO> prestamos = null;             //collection de prestamos del cliente
    private String prestamoSeleccionado = "-1";             //numero de prestamo seleccionado
    private String cuentaSeleccionada = "-1";               //numero de cuenta seleccionada
    private String monto = "0,00";                          //monto a abonar
    private CuentaDTO datosCuentaSeleccionada = null;       //datos de la cuenta seleccionada para debitar
    private PrestamoDTO datosPrestamoSeleccionado = null;   //datos del presetamo seleccionado para abonar
    
    private boolean horarioHabilitado = true;
    private boolean validaCierreOperaciones = true;
    private boolean mostrarMensajeCierre = false;

    private String temporalMonto;

    private String errorConfirmacion = "";
    private BigDecimal montoAbsoluto = new BigDecimal(BigInteger.ZERO);
    private String formPagosPrestamo = "";

    private CuentaDTO datosCuentaSeleccionadaLista = null;          //objeto para almacenar los datos de la cuenta seleccionada de la lista
    private CuentaDTO datosCuentaSeleccionadaActual = null;         //objeto para almacenar la coonsulta de los datos de la Cuenta seleccionada

    private Date fechaEjecucionPago;
    private String secMovimiento;

    ////////////////////GETTERS Y SETTERS/////////////////
    public void setErrorConfirmacion(String errorConfirmacion) {
        this.errorConfirmacion = errorConfirmacion;
    }

    public String getErrorConfirmacion() {
        return errorConfirmacion;
    }

    public String getTemporalMonto() {
        return temporalMonto;
    }

    public void setTemporalMonto(String temporalMonto) {
        this.temporalMonto = temporalMonto;
    }

    public boolean isMostrarDatosPrestamo() {
        return mostrarDatosPrestamo;
    }

    public void setMostrarDatosPrestamo(boolean mostrarDatosPrestamo) {
        this.mostrarDatosPrestamo = mostrarDatosPrestamo;
    }

    public boolean isTienePrestamos() {
        return tienePrestamos;
    }

    public void setTienePrestamos(boolean tienePrestamos) {
        this.tienePrestamos = tienePrestamos;
    }

    public boolean isMantenerDatosForm() {
        return mantenerDatosForm;
    }

    public void setMantenerDatosForm(boolean mantenerDatosForm) {
        this.mantenerDatosForm = mantenerDatosForm;
    }

    public List<CuentaDTO> getCuentasCliente() {
        return cuentasCliente;
    }

    public void setCuentasCliente(List<CuentaDTO> cuentasCliente) {
        this.cuentasCliente = cuentasCliente;
    }

    public List<PrestamoDTO> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(List<PrestamoDTO> prestamos) {
        this.prestamos = prestamos;
    }

    public CuentaDTO getDatosCuentaSeleccionada() {
        return datosCuentaSeleccionada;
    }

    public void setDatosCuentaSeleccionada(CuentaDTO datosCuentaSeleccionada) {
        this.datosCuentaSeleccionada = datosCuentaSeleccionada;
    }

    public String getPrestamoSeleccionado() {
        return prestamoSeleccionado;
    }

    public void setPrestamoSeleccionado(String prestamoSeleccionado) {
        this.prestamoSeleccionado = prestamoSeleccionado;
    }

    public Date getFechaEjecucionPago() {
        fechaEjecucionPago = new Date();
        return fechaEjecucionPago;
    }

    public void setFechaEjecucionPago(Date fechaEjecucionPago) {
        this.fechaEjecucionPago = fechaEjecucionPago;
    }

    public String getCuentaSeleccionada() {
        return cuentaSeleccionada;
    }

    public void setCuentaSeleccionada(String cuentaSeleccionada) {
        this.cuentaSeleccionada = cuentaSeleccionada;
        if (datosCuentaSeleccionada == null || datosCuentaSeleccionada.getSemilla() == null || datosCuentaSeleccionada.getSemilla().trim().equalsIgnoreCase("")) {
            this.datosCuentaSeleccionada = new CuentaDTO(sesionController.getSemilla());
        }
        this.datosCuentaSeleccionada = this.cuentaSeleccionadaCompleta(this.datosCuentaSeleccionada.getDesEnc().desencriptar(cuentaSeleccionada));
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public PrestamoDTO getDatosPrestamoSeleccionado() {
        return datosPrestamoSeleccionado;
    }

    public void setDatosPrestamoSeleccionado(PrestamoDTO datosPrestamoSeleccionado) {
        this.datosPrestamoSeleccionado = datosPrestamoSeleccionado;
    }

    public String getSecMovimiento() {
        return secMovimiento;
    }

    public void setSecMovimiento(String secMovimiento) {
        this.secMovimiento = secMovimiento;
    }

    ////////////////////VALIDATORS///////////////////////////
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
     * metodo para validar el prestamo seleccionado
     *
     * @param context contexto de la aplicacion
     * @param component componente
     * @param value valor
     * @throws ValidatorException
     */
    public void validarPrestamo(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().isEmpty() || value.equals("") || value.equals("-1")) {
            this.mantenerDatosForm = false;
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.requerido", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.requerido", sesionController.getNombreCanal()).getMensajeUsuario()));
        }
    }

    /**
     * metodo para validar el monto a abonar
     *
     * @param context contexto de la aplicacion
     * @param component componente
     * @param value valor
     * @throws ValidatorException
     */
    public void validarMonto(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.campoRequerido", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.campoRequerido", sesionController.getNombreCanal()).getMensajeUsuario()));
        } else if (!eliminarformatoSimpleMonto(value.toString()).matches("^\\d{1,}$|^\\d{1,}[.]{1}\\d{1,2}$")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.mtoInv", sesionController.getNombreCanal()).getMensajeUsuario()));
        }
    }

    //////////////////METODOS FUNCIONALES////////////////////
    /**
     * metodo utilitario para consultar el detalle del prestamo en el on change
     * de la lista
     */
    public void obtenerDetallePrestamo() {
        this.mantenerDatosForm = true;
        this.mostrarDatosPrestamo = false;
        datosPrestamoSeleccionado = new PrestamoDTO(sesionController.getSemilla());
        this.datosPrestamoSeleccionado = prestamoDAO.obtenerDetallePrestamo(datosPrestamoSeleccionado.getDesEnc().desencriptar(prestamoSeleccionado), sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (datosPrestamoSeleccionado.getRespuestaDTO() == null || !datosPrestamoSeleccionado.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || datosPrestamoSeleccionado.getRespuestaDTO().getCodigoSP().equals(CODIGO_SIN_RESULTADOS_JPA)) {
            FacesContext.getCurrentInstance().addMessage("formPagosPrestamo:txtPestamoAbonar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        } else {
            if (datosPrestamoSeleccionado.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && !datosPrestamoSeleccionado.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formPagosPrestamo:txtPestamoAbonar", new FacesMessage(FacesMessage.SEVERITY_WARN, "", datosPrestamoSeleccionado.getRespuestaDTO().getTextoSP()));
            }
        }

        //si la consulta del detalle del prestamo es exitosa se carga el resto de la data
        if (datosPrestamoSeleccionado.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && datosPrestamoSeleccionado.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
            this.datosPrestamoSeleccionado.setSemilla(sesionController.getSemilla());
            this.datosPrestamoSeleccionado.setMovimientos(prestamoDAO.listadoPagosPrestamo(datosPrestamoSeleccionado.getDesEnc().desencriptar(prestamoSeleccionado), null, null, "1", "1000", parametrosController.getNombreParametro("pnw.global.orden.asc", sesionController.getIdCanal()), sesionController.getNombreCanal(), sesionController.getIdCanal()).getMovimientos());
            if (this.datosPrestamoSeleccionado.getRespuestaDTO() != null && this.datosPrestamoSeleccionado.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                this.mostrarDatosPrestamo = true;
            }

        }

    }

    /**
     * metodo que se sencarga de retornar los datos completos de una cuenta
     * propia
     *
     * @param nroCuenta numero de la cuenta
     * @return CuentaDTO
     */
    public CuentaDTO cuentaSeleccionadaCompleta(String nroCuenta) {
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
     * Metodo para cargar las listas iniciales para Propias
     */
    public void consultaInicialPagPrestamo() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            sesionController.setValidadorFlujo(1);
            this.limpiarPagosPrestamo();
            this.tienePrestamos = false;
            consultarCuentasPropias();
            consultarPrestamos();
            if (prestamos == null || prestamos.isEmpty()) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formPagosPrestamo:divMsjSinPrestamos", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.sinPrestamos", sesionController.getIdCanal())));
            } else {
                if (prestamos.size() > 0) {
                    this.tienePrestamos = true;
                }
            }
        }
    }

    /**
     * metodo utilitario para reiniciar los valores de transferencias
     */
    public void limpiarPagosPrestamo() {
        if (!this.mantenerDatosForm || sesionController.isReiniciarForm()) {
            this.mostrarDatosPrestamo = false;
            this.cuentasCliente = new ArrayList<>();
            this.prestamoSeleccionado = "-1";
            this.setDatosPrestamoSeleccionado(null);
            this.cuentaSeleccionada = "-1";
            this.monto = "";
            this.prestamos = new ArrayList<>();
            this.sesionController.setEnTransaccion(false);
            this.tienePrestamos = false;
            this.sesionController.setReiniciarForm(false);
        }
    }

    /**
     * Metodo para consultar las cuentas propias
     */
    public void consultarCuentasPropias() {

        ClienteDTO datosCliente = clienteDAO.listadoCuentasClientes(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());
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

    /**
     * Obtiene el listado de los prestamos
     *
     */
    public void consultarPrestamos() {
        ClienteDTO cliente = prestamoDAO.listadoPrestamoPorClienteAP(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());

        if (cliente.getRespuestaDTO() != null) {
            if (!cliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                this.limipiarMensajesFacesContext();
                FacesContext.getCurrentInstance().addMessage("formPrestamo:msjPrestamos", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            }
        }

        if (cliente.getPrestamosClienteDTO() != null && !cliente.getPrestamosClienteDTO().isEmpty()) {
            for (int i = 0; i < cliente.getPrestamosClienteDTO().size(); i++) {
                cliente.getPrestamosClienteDTO().get(i).setSemilla(sesionController.getSemilla());
            }
        }
        prestamos = cliente.getPrestamosClienteDTO();
    }

    /**
     * Metodo que obtiene el Detalle del Prestamo
     *
     * @param nroPrestamo
     * @throws IOException
     */
    public void obtenerDetallePrestamo(String nroPrestamo) throws IOException {

        setPrestamoSeleccionado(nroPrestamo);
        PrestamoDTO prestamoTemp = new PrestamoDTO();
        prestamoTemp.setSemilla(sesionController.getSemilla());
        boolean validacionesSeguridad = sesionController.productosCliente(prestamoTemp.getDesEnc().desencriptar(nroPrestamo));
        if (validacionesSeguridad) {
            //obtenemos el detalle del prestamo
            datosPrestamoSeleccionado = prestamoDAO.obtenerDetallePrestamo(prestamoTemp.getDesEnc().desencriptar(nroPrestamo), sesionController.getIdCanal(), sesionController.getNombreCanal());

            //ajustar la tarjeta que viene de entrada
            prestamoTemp = prestamoDAO.listadoPagosPrestamo(prestamoTemp.getDesEnc().desencriptar(nroPrestamo), null, null,
                    parametrosController.getNombreParametro("pnw.global.pagSP.ini", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.pagSP.fin", sesionController.getIdCanal()),
                    parametrosController.getNombreParametro("pnw.global.orden.desc", sesionController.getIdCanal()),
                    sesionController.getNombreCanal(),
                    sesionController.getIdCanal());
            prestamoTemp.setSemilla(sesionController.getSemilla());
            //mensajes
            if (prestamoTemp.getMovimientos() == null) {
                datosPrestamoSeleccionado.setMovimientos(new ArrayList<MovimientoPrestamoDTO>());
            } else {
                datosPrestamoSeleccionado.setMovimientos(prestamoTemp.getMovimientos());
            }

        } else {
            RespuestaDTO respuesta = new RespuestaDTO();
            respuesta.setCodigoSP(CODIGO_DATA_INVALIDA);
            respuesta.setDescripcionSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
            datosPrestamoSeleccionado.setRespuestaDTO(respuesta);
        }

        if (datosPrestamoSeleccionado == null || datosPrestamoSeleccionado.getRespuestaDTO() == null || !datosPrestamoSeleccionado.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext.getCurrentInstance().addMessage("formPagosPrestamo:txtPestamoAbonar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        } else {
            if (datosPrestamoSeleccionado.getRespuestaDTO() != null && datosPrestamoSeleccionado.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !datosPrestamoSeleccionado.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formPagosPrestamo:txtPestamoAbonar", new FacesMessage(FacesMessage.SEVERITY_WARN, "", datosPrestamoSeleccionado.getRespuestaDTO().getTextoSP()));
            }
        }

        //Registro en Bitacora
        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_DET_PRESTAMO, "", "", "Consulta Detalle de Prestamo", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoPagPrestamoPaso2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_SERVICIOS_PAGOSPRESTAMO)) {
                sesionController.setValidadorFlujo(1);
                this.paso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoPagPrestamoPaso3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_SERVICIOS_PAGOSPRESTAMO)) {
                sesionController.setValidadorFlujo(1);
                this.paso1();
            }
        }
    }

    /**
     * metodo action para ir a paso1 de pago de prestamo
     */
    public void paso1() {
        this.mantenerDatosForm = false;
        limpiarPagosPrestamo();
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagosPrestamo.url.paso1", sesionController.getIdCanal()));
    }

    public void regresarPosCos() {
        this.redirectFacesContext("/sec/noticias/noticias.xhtml?faces-redirect=true");
        //this.redirectFacesContext(parametrosController.getNombreParametro("pnw.otp.url.validacionOtp", sesionController.getIdCanal()));
    }
    
    /**
     * metodo action para ir a paso1
     */
    public void regresarPaso1() {
        this.mantenerDatosForm = true;
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagosPrestamo.url.paso1", sesionController.getIdCanal()));
    }

    /**
     *
     * @return
     */
    public void confirPaso2() {
        BigDecimal montoBD = new BigDecimal(this.eliminarformatoSimpleMonto(monto));
        if (montoBD.compareTo(this.datosCuentaSeleccionada.getSaldoDisponible()) > 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formPagosPrestamo:txtMonto", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.saldoInsuficiente", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagosPrestamo.url.paso1", sesionController.getIdCanal()));
        }
        if (montoBD.compareTo(this.datosPrestamoSeleccionado.getSaldoActual()) > 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formPagosPrestamo:txtMonto", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoMayorDeuda", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagosPrestamo.url.paso1", sesionController.getIdCanal()));
        }

        if (getMonto() == null || getMonto().isEmpty() || (new BigDecimal(eliminarformatoSimpleMonto(getMonto())).compareTo(BigDecimal.ZERO) == 0)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formPagosPrestamo:txtMonto", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoInv", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagosPrestamo.url.paso1", sesionController.getIdCanal()));
            return;
        }

        //Se redirecciona a la pantalla de confirmacion
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagosPrestamo.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo para redirigir a paso 1 de pago de préstamos
     *
     * @return
     */
    public void ejecutarPaso3() {

        String codError = CODIGO_RESPUESTA_EXITOSO;
        RespuestaDTO respuestaDTO = null;

        setErrorConfirmacion("");
        //se validan las reglas de negocio para el pago
        int validaciones = this.validarPagosPrestamo();
        if (validaciones == 0) {
            //realizamos la transaccion y validamos
            respuestaDTO = this.ejecutarPagoPrestamo();
            if (respuestaDTO != null && respuestaDTO.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && respuestaDTO.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {

                //notificamos en caso de realizar el pago
                //registramos en bitacora 
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_SERVICIOS_PAGOSPRESTAMO, "", "", "Pago de Préstamo Exitoso", this.monto, "", "", "", "", "", "");
                //redireccionamos al paso final

                sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
                this.mantenerDatosForm = false;
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagosPrestamo.url.paso3", sesionController.getIdCanal()));
            } else {
                if (!respuestaDTO.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    //mensaje error grave     
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formPagosPrestamo2:divPagPrestamo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    setErrorConfirmacion(codError);
                    this.mantenerDatosForm = false;
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagosPrestamo.url.paso2", sesionController.getIdCanal()));
                } else {
                    if (respuestaDTO.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && !respuestaDTO.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                        //mensaje error controlado
                        codError = respuestaDTO.getCodigoSP();
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formPagosPrestamo2:divPagPrestamo", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuestaDTO.getTextoSP()));
                        setErrorConfirmacion(codError);
                        this.mantenerDatosForm = false;
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagosPrestamo.url.paso2", sesionController.getIdCanal()));
                    }
                }
            }
        } else {
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagosPrestamo.url.paso2", sesionController.getIdCanal()));
        }
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagosPrestamo.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo que se encarga de realizar las validaciones de negocio de las
     * transferencias entre cuentas propias
     *
     * @return TransaccionDTO
     */
    public RespuestaDTO ejecutarPagoPrestamo() {
        boolean validacionesSeguridad = false;
        RespuestaDTO respPago = new RespuestaDTO();
        UtilDTO utilDTO = new UtilDTO();

        datosCuentaSeleccionada.setSemilla(sesionController.getSemilla());
        //se validan que tanto la cuenta como el prestamo pertenezcan al cliente
        validacionesSeguridad = sesionController.productosCliente(datosCuentaSeleccionada.getDesEnc().desencriptar(cuentaSeleccionada));
        if (validacionesSeguridad) {
            validacionesSeguridad = sesionController.productosCliente(datosCuentaSeleccionada.getDesEnc().desencriptar(prestamoSeleccionado));
            if (validacionesSeguridad) {
                utilDTO = prestamoDAO.aplicarPagoPrestamo(datosCuentaSeleccionada.getDesEnc().desencriptar(prestamoSeleccionado), datosCuentaSeleccionada.getDesEnc().desencriptar(cuentaSeleccionada), this.eliminarformatoSimpleMonto(this.monto), sesionController.getUsuario().getCodUsuario(), textosController.getNombreTexto("pnw.submenu.servicios.pagoPrestamo", sesionController.getNombreCanal()), sesionController.getNombreCanal(), sesionController.getIdCanal());
                if (utilDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && utilDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    if (utilDTO.getResuladosDTO().get("secMovimiento") != null && !utilDTO.getResuladosDTO().get("secMovimiento").equals("")) {
                        secMovimiento = utilDTO.getResuladosDTO().get("secMovimiento").toString();
                    }
                    notificarPagoPrestamo();
                } else {
                    respPago = utilDTO.getRespuestaDTO();
                }
            } else {
                respPago.setCodigoSP(CODIGO_DATA_INVALIDA);
                respPago.setDescripcionSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
                respPago.setTextoSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
            }

        } else {
            respPago.setCodigoSP(CODIGO_DATA_INVALIDA);
            respPago.setDescripcionSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
            respPago.setTextoSP(textosController.getNombreTexto("pnw.errores.texto.dataInvalida", sesionController.getNombreCanal()));
        }
        return respPago;
    }

    /**
     * Metodo que se encarga de realizar las validaciones de negocio de los
     * pagos de Prestamo
     *
     * @return int
     */
    public int validarPagosPrestamo() {

        FacesMessage fMsg;
        int errores = 0;

        if (new BigDecimal(eliminarformatoSimpleMonto(this.monto)).signum() < 0) {
            montoAbsoluto = new BigDecimal(eliminarformatoSimpleMonto(this.monto)).negate();
        } else {
            montoAbsoluto = new BigDecimal(eliminarformatoSimpleMonto(this.monto));
        }

        //monto superior a 1 Bolivar
        if (Double.valueOf(montoAbsoluto.toString()) < Double.valueOf("1.01")) {
            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.mtoInv", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage(formPagosPrestamo + ":txtMonto", fMsg);
            errores++;
        }
        //monto menor que saldo disponible se valida contra el saldo en tiempo real 
        this.datosCuentaSeleccionada = cuentaDAO.obtenerDetalleCuenta(datosCuentaSeleccionada.getCodigoTipoProducto(),
                datosCuentaSeleccionada.getNumeroCuenta(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (this.datosCuentaSeleccionada.getSaldoDisponible() != null) {
            if (this.datosCuentaSeleccionada.getSaldoDisponible().compareTo(montoAbsoluto) < 0) {
                fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.saldoInsuficiente", sesionController.getNombreCanal()).getMensajeTecnico(),
                        textosFacade.findByCodigo("pnw.global.texto.saldoInsuficiente", sesionController.getNombreCanal()).getMensajeUsuario());
                FacesContext.getCurrentInstance().addMessage(formPagosPrestamo + ":txtMonto", fMsg);
                errores++;
            }
        }

        return errores;
    }

    //Notificación de Pago de Préstamo realizado vía email
    /**
     * Metodo que setea los datos y parametros para el envio del correo luego
     * del proceso del Pago de Préstamo
     */
    public void notificarPagoPrestamo() {

        String asunto = "Notificacion Pago de Préstamo DELSUR";
        StringBuilder texto = new StringBuilder("");

        setFechaEjecucionPago(new Date(BDSUtil.formatearFecha(Calendar.getInstance().getTime(), BDSUtil.FORMATO_FECHA_COMPLETA)));

        texto = new StringBuilder("Estimado(a) ");
        texto.append(sesionController.getUsuario().getNombre());
        texto.append(NUEVALINEAEMAIL);
        texto.append(NUEVALINEAEMAIL);
        texto.append(parametrosController.getNombreParametro("banco.nombre", sesionController.getIdCanal()));
        texto.append(" le informa que se ha realizado el Pago del Préstamo: ");
        texto.append(this.formatoAsteriscosWeb(datosPrestamoSeleccionado.getNumeroPrestamo()));
        texto.append(" el día: ");
        texto.append(this.getFechaEjecucionPago());
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

    }

    ////////////////////SECCION  DE REPORTES//////////////////////////
    /**
     * metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     */
    public void cuerpoPagoPrestamosPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 11, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //Variables para el manejo del monto, Phrase espera siempre un String
            String monto = null;

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(PagosPrestamoController.class.getResource("/imgPDF/logoBanner.png"));

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
            PdfPCell cell = new PdfPCell(new Phrase("Comprobante de Pago de Préstamo", fontTituloBold));

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
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosPrestamo.descargaPdf.nroConfirmacion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.secMovimiento, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosPrestamo.descargaPdf.fecha", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(fecha2(new Date()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosPrestamo.descargaPdf.ctaDebitar", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase((this.formatoAsteriscosWeb(datosCuentaSeleccionada.getNumeroCuenta())), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosPrestamo.descargaPdf.nroPrestamo", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase((this.formatoAsteriscosWeb(datosPrestamoSeleccionado.getNumeroPrestamo())), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagosPrestamo.descargaPdf.monto", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            String moneda;
            if (datosPrestamoSeleccionado.getSiglasTipoMoneda() == null) {
                moneda = "Bs.";
            } else {
                moneda = datosPrestamoSeleccionado.getSiglasTipoMoneda();
            }
            table.addCell(new Phrase(moneda + " " + this.monto, font));

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
    public void detallePagosPrestamosPDF() throws IOException, DocumentException {

        try {

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-disposition", "attachment;filename=\"PagoPrestamo.pdf\"");
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
            this.cuerpoPagoPrestamosPDF(document);

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
