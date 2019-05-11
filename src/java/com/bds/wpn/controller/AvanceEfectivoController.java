/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.NotificacionesDAO;
import com.bds.wpn.dao.TarjetaCreditoDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.TarjetasCreditoDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.FORMATO_FECHA_COMPLETA;
import static com.bds.wpn.util.BDSUtil.NUEVALINEAEMAIL;
import static com.bds.wpn.util.BDSUtil.fecha2;
import static com.bds.wpn.util.BDSUtil.formatearFecha;
import static com.bds.wpn.util.BDSUtil.formatoAsteriscosWeb;
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
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author cesar.mujica
 */
@Named("wpnAvanceEfectivoController")
@SessionScoped
public class AvanceEfectivoController extends BDSUtil implements Serializable {

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbParametrosFacade parametrosFacade;

    @Inject
    InicioSesionController sesionController;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    TarjetaCreditoDAO tarjetaCreditoDAO;

    @EJB
    NotificacionesDAO notificacionesDAO;

    @Inject
    TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    private String monto = "";
    private String codTDC = "";
    private String ctaSeleccionada;
    private String tdcSeleccionada;
    private TarjetasCreditoDTO datosTDCSeleccionada;
    private CuentaDTO datosCtaSeleccionada;
    private Collection<CuentaDTO> cuentasCliente;                   //objeto que contiene las cuentas propias
    private Collection<TarjetasCreditoDTO> tdcCliente;              //objeto que contiene las TDC propias
    private String nroReferencia = "";
    private boolean horarioHabilitado = true;
    private boolean validaCierreOperaciones = true;
    private boolean mostrarMensajeCierre = false;

    /**
     * ****************GETTERS Y
     * SETTERS*****************************************
     */
    public Collection<CuentaDTO> getCuentasCliente() {
        return cuentasCliente;
    }

    public void setCuentasCliente(Collection<CuentaDTO> cuentasCliente) {
        this.cuentasCliente = cuentasCliente;
    }

    public Collection<TarjetasCreditoDTO> getTdcCliente() {
        return tdcCliente;
    }

    public void setTdcCliente(Collection<TarjetasCreditoDTO> tdcCliente) {
        this.tdcCliente = tdcCliente;
    }

    public String getCtaSeleccionada() {
        return ctaSeleccionada;
    }

    public void setCtaSeleccionada(String ctaSeleccionada) {
        this.ctaSeleccionada = ctaSeleccionada;
        if (this.ctaSeleccionada != null && !this.ctaSeleccionada.trim().equalsIgnoreCase("") && !this.ctaSeleccionada.trim().equalsIgnoreCase("-1")) {
            for (CuentaDTO ctaTemp : this.cuentasCliente) {
                if (ctaTemp.getNumeroCuentaEnc().equalsIgnoreCase(this.ctaSeleccionada)) {
                    this.datosCtaSeleccionada = ctaTemp;
                    break;
                }
            }
        } else {
            this.datosCtaSeleccionada = new CuentaDTO();
        }
    }

    public String getTdcSeleccionada() {
        return tdcSeleccionada;
    }

    public void setTdcSeleccionada(String tdcSeleccionada) {
        this.tdcSeleccionada = tdcSeleccionada;
    }

    public TarjetasCreditoDTO getDatosTDCSeleccionada() {
        if (datosTDCSeleccionada == null) {
            datosTDCSeleccionada = new TarjetasCreditoDTO(sesionController.getSemilla());
        }
        return datosTDCSeleccionada;
    }

    public void setDatosTDCSeleccionada(TarjetasCreditoDTO datosTDCSeleccionada) {
        this.datosTDCSeleccionada = datosTDCSeleccionada;
    }

    public CuentaDTO getDatosCtaSeleccionada() {
        return datosCtaSeleccionada;
    }

    public void setDatosCtaSeleccionada(CuentaDTO datosCtaSeleccionada) {
        this.datosCtaSeleccionada = datosCtaSeleccionada;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getCodTDC() {
        return codTDC;
    }

    public void setCodTDC(String codTDC) {
        this.codTDC = codTDC;
    }

    public String getNroReferencia() {
        return nroReferencia;
    }

    public void setNroReferencia(String nroReferencia) {
        this.nroReferencia = nroReferencia;
    }

    /**
     * *************************METODOS
     * FUNCIONALES*******************************
     */
    /**
     * metodo generico que se encarga de limpiar el formulario
     */
    public void limpiar() {
        if (sesionController.isReiniciarForm()) {
            datosTDCSeleccionada = new TarjetasCreditoDTO(sesionController.getSemilla());

            ctaSeleccionada = "-1";
            tdcSeleccionada = "-1";
            datosCtaSeleccionada = new CuentaDTO();
            datosTDCSeleccionada = new TarjetasCreditoDTO();
            monto = "";
            codTDC = "";
            nroReferencia = "";
            consultarCuentasPropias();
            consultarTDCPropias();

            sesionController.setReiniciarForm(false);
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
            FacesContext.getCurrentInstance().addMessage("formAvanceEfect:selectCtaDestino", fMsg);
        } else {
            //se consultan las afiliaciones para dicha transaccion
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
            FacesContext.getCurrentInstance().addMessage("formAvanceEfect:txtCtaAbonar", fMsg);
        } else {
            //se consultan las tdc propias para dicha transaccion
            if (datosCliente.getTdcAsociadasClienteDTO().size() > 0) {
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

    public void consultarDetalleTDC() {
        if (tdcSeleccionada != null && !tdcSeleccionada.equalsIgnoreCase("-1")) {
            this.datosTDCSeleccionada = null;
            boolean validacionesSeguridad = sesionController.productosCliente(getDatosTDCSeleccionada().getDesEnc().desencriptar(tdcSeleccionada));
            if (validacionesSeguridad) {
                this.datosTDCSeleccionada = tarjetaCreditoDAO.obtenerDetalleTDC(datosTDCSeleccionada.getDesEnc().desencriptar(tdcSeleccionada), sesionController.getIdCanal(), sesionController.getNombreCanal());
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeTecnico(),
                        textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeUsuario());
                FacesContext.getCurrentInstance().addMessage("formAvanceEfect:avanceEfectDiv", fMsg);
            }
        } else {
            this.limpiar();
        }
    }

    /**
     * metodo utilitario que redirecciona al OTP
     */
    public void regresarAvcEfectPaso1() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.otp.url.validacionOtp", sesionController.getIdCanal()));
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoAvcEfect2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AVC_EFECT)) {
                sesionController.setValidadorFlujo(1);
                this.regresarAvcEfectPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoAvcEfect3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AVC_EFECT)) {
                sesionController.setValidadorFlujo(1);
                this.regresarAvcEfectPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoAvcEfect4() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AVC_EFECT)) {
                sesionController.setValidadorFlujo(1);
                this.regresarAvcEfectPaso1();
            }
        }
    }

    /**
     * metodo para redirigir a paso 2 de avance de efectivo
     */
    public void regresarAvcEfectPaso2() {
        sesionController.setValidadorFlujo(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.avanceEfectivo.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo valida los datos ingresado y envia al paso de confirmacion
     */
    public void avcEfectPaso3() {
        int cantErrores = 0;
        if (tdcSeleccionada == null || tdcSeleccionada.trim().equalsIgnoreCase("-1")) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAvanceEfect:selectTDC", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.seleccioneTDC", sesionController.getNombreCanal())));
        } else {
            BigDecimal mtoAvc = new BigDecimal(eliminarformatoSimpleMonto(this.getMonto()));
            if (mtoAvc.compareTo(datosTDCSeleccionada.getMontoDisponible()) == 1) {
                cantErrores++;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formAvanceEfect:txtMonto", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.saldoInsuficiente", sesionController.getNombreCanal())));
            }
            if (codTDC == null || codTDC.isEmpty() || codTDC.trim().length() != 3) {
                cantErrores++;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formAvanceEfect:txtCodTDC", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.codTDCInv", sesionController.getNombreCanal())));
            }
        }

        if (cantErrores > 0) {
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.avanceEfectivo.url.paso2", sesionController.getIdCanal()));
        } else {
            sesionController.setValidadorFlujo(3);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.avanceEfectivo.url.paso3", sesionController.getIdCanal()));
        }
    }

    public void avcEfectPaso4() {
        UtilDTO utilP2P = new UtilDTO();
        utilP2P.setRespuestaDTO(new RespuestaDTO());
        /*utilP2P = p2pDAO.realizarPagoP2PTercerosOtrosBancos(CODIGO_CANAL_P2P, ajustarTelfP2P(this.usuarioP2P.getNroTelefono()), ajustarTelfP2P(this.codTelfDestino + this.telfDestino),
         soloNumeros(this.nroDocBeneficiario), eliminarformatoSimpleMonto(this.montoPago), this.descripcionPago, "000" + this.codBanco, soloNumeros(sesionController.getUsuario().getDocumento()),
         parametrosController.getNombreParametro("roas.wsdl.p2p", sesionController.getIdCanal()), sesionController.getIdCanal(), sesionController.getNombreCanal());*/

        //INVOCAR AL WS DE AVC DE EFECT
        if (utilP2P.getRespuestaDTO() != null && utilP2P.getRespuestaDTO().getCodigo() != null && utilP2P.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            if (utilP2P.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {

                //OJO
                this.nroReferencia = "123123";
                //this.nroReferencia = utilP2P.getResuladosDTO().get("referencia").toString();
                sesionController.setValidadorFlujo(4);

                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), sesionController.getIdTransaccion(), formatoAsteriscosWeb(this.datosTDCSeleccionada.getNumeroTarjeta()),
                        formatoAsteriscosWeb(this.datosCtaSeleccionada.getNumeroCuenta()), "Avance de Efectivo TDC", this.monto, this.nroReferencia, "", "", "", "", "");
                String textoSMS = textosController.getNombreTexto("pnw.sms.cuerpo.avcEfect", sesionController.getNombreCanal());
                String motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.avcEfect", sesionController.getNombreCanal());
                HashMap<String, String> parametros = new HashMap();
                parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                parametros.put("\\$PMTO", this.monto);
                parametros.put("\\$PCTADEST", this.numeroCuentaFormato(this.datosCtaSeleccionada.getNumeroCuenta()));
                parametros.put("\\$PCTAORIG", this.numeroCuentaFormato(this.datosTDCSeleccionada.getNumeroTarjeta()));
                sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);
                this.notificarAvanceEfectivoEmail();
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.avanceEfectivo.url.paso4", sesionController.getIdCanal()));

            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formAvanceEfect:avanceEfectDiv", new FacesMessage(FacesMessage.SEVERITY_WARN, "", utilP2P.getRespuestaDTO().getDescripcionSP()));
                sesionController.setValidadorFlujo(3);
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.avanceEfectivo.url.paso3", sesionController.getIdCanal()));
            }
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAvanceEfect:avanceEfectDiv", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            sesionController.setValidadorFlujo(3);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.avanceEfectivo.url.paso3", sesionController.getIdCanal()));
        }
    }

    /**
     * Metodo que setea los datos y parametros para el envio del correo luego
     * del proceso de Avance de Efectivo
     */
    public void notificarAvanceEfectivoEmail() {

        String asunto = "";
        String nombreEmisor;
        StringBuilder texto = new StringBuilder("");
        StringBuilder textoE; //EMISOR

        asunto = "Avance de Efectivo, DELSUR";
        nombreEmisor = sesionController.getUsuario().getNombre();

        textoE = new StringBuilder("Estimado(a) ");
        textoE.append(nombreEmisor);
        textoE.append(NUEVALINEAEMAIL);
        textoE.append(NUEVALINEAEMAIL);
        textoE.append("Le informamos que su solicitud de Avance de Efectivo se realizó satisfactoriamente según el siguiente detalle:");
        textoE.append(NUEVALINEAEMAIL);
        textoE.append(NUEVALINEAEMAIL);

        texto.append("Recibo del Avance de Efectivo ");
        texto.append(NUEVALINEAEMAIL);
        texto.append("Depositado en Cuenta: ");
        texto.append(formatoAsteriscosWeb(this.datosCtaSeleccionada.getNumeroCuenta()));
        texto.append(NUEVALINEAEMAIL);
        texto.append("Tarjeta de Crédito Origen: ");
        texto.append(formatoAsteriscosWeb(this.datosTDCSeleccionada.getNumeroTarjeta()));
        texto.append(NUEVALINEAEMAIL);
        texto.append("Monto del Avance de Efectivo: ");
        texto.append(monto);
        texto.append(" Bs.");
        texto.append(NUEVALINEAEMAIL);
        texto.append("Fecha de la Transacción: ");
        texto.append(new Date());
        texto.append(NUEVALINEAEMAIL);
        texto.append("Número de Referencia: ");
        texto.append(this.nroReferencia);
        texto.append(NUEVALINEAEMAIL);
        texto.append(NUEVALINEAEMAIL);
        texto.append(NUEVALINEAEMAIL);
        texto.append("Este correo electrónico se genera automáticamente cada vez que se realiza un Avance de Efectivo. Por su seguridad no responda al mismo y en caso que desconozca esta transacción, comuníquese inmediatamente con nosotros a través de los teléfonos 0212-208.73.44 / 0212-208.74.45");
        //texto.append(NUEVALINEAEMAIL);
        //texto.append("Si desea disfrutar de los beneficios y las ventajas de DELSUR Banco Universal, conéctese con nosotros a través de www.delsur.com.ve.");
        texto.append(NUEVALINEAEMAIL);
        texto.append(NUEVALINEAEMAIL);
        texto.append("Atentamente,");
        texto.append(NUEVALINEAEMAIL);
        texto.append("DELSUR Banco Universal www.delsur.com.ve.");
        texto.append(NUEVALINEAEMAIL);
        texto.append("Un pequeño Grande en Detalles.");

        notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("pnw.global.texto.emailBanco", sesionController.getIdCanal()), sesionController.getUsuario().getEmail(), asunto, textoE.append(texto).toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());

    }

    /**
     * metodo encargado de armar el reportes de avance de efectivo en PDF
     *
     * @throws java.io.IOException
     * @throws com.lowagie.text.DocumentException
     */
    public void detalleAvcEfectivoPDF() throws IOException, DocumentException {

        try {

            String nombreDocumento;

            nombreDocumento = "AvanceDeEfectivo.pdf";

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
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
            this.cuerpoAvcEfectPDF(document);

            //ceramos el documento
            document.close();

            response.setContentLength(baos.size());
            ServletOutputStream out = response.getOutputStream();
            baos.writeTo(out);
            baos.flush();
            faces.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * metodo encargado de generar el contenido del avance de efectivo en PDF
     *
     * @param pdf hoja de trabajo de PDF
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoAvcEfectPDF(Document pdf) throws BadElementException, DocumentException {
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
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.avcEfect.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));

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
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.avcEfect.descargaPdf.tdc", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(formatoAsteriscosWeb(this.datosTDCSeleccionada.getNumeroTarjeta()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.avcEfect.descargaPdf.ctaDestino", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(formatoAsteriscosWeb(this.datosCtaSeleccionada.getNumeroCuenta()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.avcEfect.descargaPdf.monto", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.monto, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.avcEfect.descargaPdf.nroRef", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.nroReferencia, font));

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
