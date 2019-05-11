/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ChequeraDAO;
import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.CuentaDAO;
import com.bds.wpn.dao.DelSurDAO;
import com.bds.wpn.dao.NotificacionesDAO;
import com.bds.wpn.dto.AgenciaDTO;
import com.bds.wpn.dto.ChequeraDTO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.DelSurDTO;
import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.NUEVALINEAEMAIL;
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
import java.util.ArrayList;
import java.util.Collection;
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
 * @author juan.faneite
 */
@Named("wpnChequeraController")
@SessionScoped
public class ChequeraController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    DelSurDAO delSurDAO;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    CuentaDAO cuentaDAO;

    @EJB
    NotificacionesDAO notificacionesDAO;
    
    @EJB
    ChequeraDAO chequeraDAO;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;

    private String ctaCte = "-1";
    private CuentaDTO ctaCteSeleccionada = null;                    //detalle de la cuenta seleccionada
    private ChequeraDTO chequeraSeleccionada;                //detalle de la chequera seleccionada
    private AgenciaDTO agenciaSeleccionada = null;                  //detalle de la agencia seleccionada
    private Collection<CuentaDTO> cuentasCliente;                   //objeto que contiene las cuentas propias
    private Collection<AgenciaDTO> agencias;                        //objeto que contiene las agencias de DelSur
    private String tipoChequera;
    private String cantChequeras = "1";                             //temporalmente se deja 1 por el ahorro de papel
    private String agencia = null;
    private Date fechaEjecucion;
    private String chequera;
    private String tipoSuspension = "T";
    private List<String> chequesSelected;
    private boolean renderedCheques = false;
    private boolean mantenerDatosForm = false;                      //indicador para mantener campos en formulario de solicitud de Chequeras
    private String chequesSuspendidos;
    private String listCheques = null;
    private boolean renderedTipoSuspension = false;
    private String numSolicitud;

    private String tipoPDF;

    /////////////////GETTERS Y SETTERS//////////////////////////////
    public boolean isRenderedCheques() {
        return renderedCheques;
    }

    public void setRenderedCheques(boolean renderedCheques) {
        this.renderedCheques = renderedCheques;
    }

    public String getChequera() {
        return chequera;
    }

    public void setChequera(String chequera) {
        this.chequera = chequera;
    }

    public String getTipoSuspension() {
        return tipoSuspension;
    }

    public void setTipoSuspension(String tipoSuspension) {
        this.tipoSuspension = tipoSuspension;
    }

    public List<String> getChequesSelected() {
        return chequesSelected;
    }

    public void setChequesSelected(List<String> chequesSelected) {
        this.chequesSelected = chequesSelected;
    }

    public String getCtaCte() {
        return ctaCte;
    }

    public String getChequesSuspendidos() {
        return chequesSuspendidos;
    }

    public void setChequesSuspendidos(String chequesSuspendidos) {
        this.chequesSuspendidos = chequesSuspendidos;
    }

    public boolean isRenderedTipoSuspension() {
        return renderedTipoSuspension;
    }

    public void setRenderedTipoSuspension(boolean renderedTipoSuspension) {
        this.renderedTipoSuspension = renderedTipoSuspension;
    }

    public String getNumSolicitud() {
        return numSolicitud;
    }

    public void setNumSolicitud(String numSolicitud) {
        this.numSolicitud = numSolicitud;
    }

    public void setCtaCte(String ctaCte) {
        this.ctaCte = ctaCte;
        if (!ctaCte.equalsIgnoreCase("-1")) {
            if (ctaCteSeleccionada == null || ctaCteSeleccionada.getSemilla() == null || ctaCteSeleccionada.getSemilla().trim().equalsIgnoreCase("")) {
                this.ctaCteSeleccionada = new CuentaDTO(sesionController.getSemilla());
            }
            this.ctaCteSeleccionada = this.cuentaPropiaSeleccionadaCompleta(this.ctaCteSeleccionada.getDesEnc().desencriptar(ctaCte));
            if (sesionController.getIdTransaccion().equalsIgnoreCase(ID_TRANS_SOL_CHEQUERAS)) {
                this.setAgencia(ctaCteSeleccionada.getCodAgencia());
            }
        } else {
            if (sesionController.getIdTransaccion().equalsIgnoreCase(ID_TRANS_SOL_CHEQUERAS)) {
                this.setAgencia("-1");
            }
        }

    }

    public String getTipoChequera() {
        return "1";
    }

    public void setTipoChequera(String tipoChequera) {
        this.tipoChequera = tipoChequera;
    }

    public String getCantChequeras() {
        return cantChequeras;
    }

    public void setCantChequeras(String cantChequeras) {
        this.cantChequeras = cantChequeras;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
        this.agenciaSeleccionada = agenciaSeleccionadaCompleta(agencia);
    }

    public Date getFechaEjecucion() {
        return new Date();
    }

    public void setFechaEjecucion(Date fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public Collection<CuentaDTO> getCuentasCliente() {
        return cuentasCliente;
    }

    public void setCuentasCliente(Collection<CuentaDTO> cuentasCliente) {
        this.cuentasCliente = cuentasCliente;
    }

    public Collection<AgenciaDTO> getAgencias() {
        return agencias;
    }

    public void setAgencias(Collection<AgenciaDTO> agencias) {
        this.agencias = agencias;
    }

    public boolean isMantenerDatosForm() {
        return mantenerDatosForm;
    }

    public void setMantenerDatosForm(boolean mantenerDatosForm) {
        this.mantenerDatosForm = mantenerDatosForm;
    }

    public CuentaDTO getCtaCteSeleccionada() {
        return ctaCteSeleccionada;
    }

    public void setCtaCteSeleccionada(CuentaDTO ctaCteSeleccionada) {
        this.ctaCteSeleccionada = ctaCteSeleccionada;
    }

    public AgenciaDTO getAgenciaSeleccionada() {
        return agenciaSeleccionada;
    }

    public void setAgenciaSeleccionada(AgenciaDTO agenciaSeleccionada) {
        this.agenciaSeleccionada = agenciaSeleccionada;
    }

    public ChequeraDTO getChequeraSeleccionada() {
        return chequeraSeleccionada;
    }

    public void setChequeraSeleccionada(ChequeraDTO chequeraSeleccionada) {
        this.chequeraSeleccionada = chequeraSeleccionada;
    }

    public String getTipoPDF() {
        return tipoPDF;
    }

    public void setTipoPDF(String tipoPDF) {
        this.tipoPDF = tipoPDF;
    }

    public String getListCheques() {
        return listCheques;
    }

    public void setListCheques(String listCheques) {
        this.listCheques = listCheques;
    }

    ///////////////////////VALIDATORS/////////////////////////////////////    
    /**
     * metodo para validar la cuenta propia seleccionada
     *
     * @param context contexto de la aplicacion
     * @param component componente
     * @param value valor
     * @throws ValidatorException
     */
    public void validarAgencias(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().isEmpty() || value.equals("") || value.equals("-1")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.requerido", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.texto.requerido", sesionController.getNombreCanal()).getMensajeUsuario()));
        }
    }

    public void validarCantidadChequeras(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (!value.toString().matches("[1-9]{1}")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosFacade.findByCodigo("pnw.solicitudChequera.texto.cantidadCheq", sesionController.getNombreCanal()).getMensajeUsuario()));
        }
    }

    /////////////////////////////METODOS UTILITARIOS Y FUNCIONALES///////////////////////////////////////////
    /**
     * metodo utilitario para cargar dinamicamente las chequeras asociadas a una
     * cuenta corriente
     */
    public void consultarChequerasCuenta() {
        this.setMantenerDatosForm(true);
        this.ctaCteSeleccionada.setChequerasDTO(null);
        this.tipoSuspension = "T";
        this.renderedCheques = false;
        this.chequesSelected = null;
        this.renderedTipoSuspension = false;

        this.setCtaCte(ctaCte);
        if (sesionController.getIdTransaccion().equalsIgnoreCase(ID_TRANS_SUSP_CHEQUERAS)) {
//            CuentaDTO cuentaTemp = cuentaDAO.listarChequerasEntregadas(ctaCteSeleccionada.getNumeroCuenta(), sesionController.getNombreCanal(), sesionController.getIdCanal());
            CuentaDTO cuentaTemp = chequeraDAO.listarChequeraEntregada(ctaCteSeleccionada.getNumeroCuenta(), sesionController.getNombreCanal());
            if (cuentaTemp != null && cuentaTemp.getRespuestaDTO() != null && cuentaTemp.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                    && cuentaTemp.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && cuentaTemp.getChequerasDTO() != null) {
                ctaCteSeleccionada.setChequerasDTO(cuentaTemp.getChequerasDTO());
            } else {
                if (cuentaTemp == null || cuentaTemp.getRespuestaDTO() == null || !cuentaTemp.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formSuspCheques1:txtChequeras", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
                } else {
                    if (!cuentaTemp.getRespuestaDTO().getTextoSP().equalsIgnoreCase("")) {
                        FacesContext.getCurrentInstance().addMessage("formSuspCheques1:txtCheques", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaTemp.getRespuestaDTO().getTextoSP()));
                    }
                }
            }
        }
    }
    
    /**
     * metodo utilitario para cargar dinamicamente las chequeras asociadas a una
     * cuenta corriente
     */
    public void consultarChequerasCuentaSts() {
        this.setMantenerDatosForm(true);
        this.ctaCteSeleccionada.setChequerasDTO(null);
        this.tipoSuspension = "T";
        this.renderedCheques = false;
        this.chequesSelected = null;
        this.renderedTipoSuspension = false;

        this.setCtaCte(ctaCte);
//            CuentaDTO cuentaTemp = cuentaDAO.listarChequerasEntregadas(ctaCteSeleccionada.getNumeroCuenta(), sesionController.getNombreCanal(), sesionController.getIdCanal());
            CuentaDTO cuentaTemp = chequeraDAO.listarChequeraEntregada(ctaCteSeleccionada.getNumeroCuenta(), sesionController.getNombreCanal());
            if (cuentaTemp != null && cuentaTemp.getRespuestaDTO() != null && cuentaTemp.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                    && cuentaTemp.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && cuentaTemp.getChequerasDTO() != null) {
                ctaCteSeleccionada.setChequerasDTO(cuentaTemp.getChequerasDTO());
            } else {
                if (cuentaTemp == null || cuentaTemp.getRespuestaDTO() == null || !cuentaTemp.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formStsCheques:txtChequeras", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
                } else {
                    if (!cuentaTemp.getRespuestaDTO().getTextoSP().equalsIgnoreCase("")) {
                        FacesContext.getCurrentInstance().addMessage("formStsCheques:txtCheques", new FacesMessage(FacesMessage.SEVERITY_WARN, "", cuentaTemp.getRespuestaDTO().getTextoSP()));
                    }
                }
            }
    }

    /**
     *
     */
    public void seleccionarChequera() {
        this.tipoSuspension = "T";
        this.chequesSelected = null;
        this.renderedCheques = false;
        if (!this.chequera.equalsIgnoreCase("-1")) {
            this.renderedTipoSuspension = true;
            for (ChequeraDTO chequeraTemp : ctaCteSeleccionada.getChequerasDTO()) {
                if (chequeraTemp.getNumeroPrimerCheque().equalsIgnoreCase(chequera)) {
                    this.chequeraSeleccionada = chequeraTemp;
                    this.consultarChequesAct();
                    break;
                }
            }
        } else {
            this.renderedTipoSuspension = false;
        }
    }
    
    /**
     *
     */
    public void seleccionarChequeraStsCheques() {
        this.tipoSuspension = "T";
        this.chequesSelected = null;
        this.renderedCheques = false;
        if (!this.chequera.equalsIgnoreCase("-1")) {
            this.renderedTipoSuspension = true;
            for (ChequeraDTO chequeraTemp : ctaCteSeleccionada.getChequerasDTO()) {
                if (chequeraTemp.getNumeroPrimerCheque().equalsIgnoreCase(chequera)) {
                    this.chequeraSeleccionada = chequeraTemp;
                    this.consultarChequesUtilizados();
                    break;
                }
            }
        } else {
            this.renderedTipoSuspension = false;
        }
    }

    /**
     * metodo utilitario para consultar los cheques activos asociados a una chequera
     */
    public void consultarChequesAct() {
        if (!ctaCte.equalsIgnoreCase("-1") && ctaCteSeleccionada != null && !chequera.equalsIgnoreCase("-1")) {
//            ChequeraDTO chequeratemp = cuentaDAO.listarChequesPorChequeraAct(ctaCteSeleccionada.getNumeroCuenta(), chequeraSeleccionada.getNumeroPrimerCheque(), sesionController.getNombreCanal(), sesionController.getIdCanal());
            ChequeraDTO chequeratemp = chequeraDAO.listarChequePorChequeraAct(ctaCteSeleccionada.getNumeroCuenta(), chequeraSeleccionada.getNumeroPrimerCheque(), sesionController.getNombreCanal());
            if (chequeratemp != null && chequeratemp.getRespuestaDTO() != null && chequeratemp.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                    && chequeratemp.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && chequeratemp.getChequesDTO() != null && !chequeratemp.getChequesDTO().isEmpty()) {
                this.chequeraSeleccionada.setChequesDTO(chequeratemp.getChequesDTO());
                this.renderedCheques = true;
            } else {
                if (!chequeratemp.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formSuspCheques1:txtChequeras", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
                } else {
                    if (chequeratemp.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_SIN_RESULTADOS_JPA)) {
                        FacesContext.getCurrentInstance().addMessage("formSuspCheques1:txtChequeras", new FacesMessage(FacesMessage.SEVERITY_WARN, "", textosController.getNombreTexto("pnw.errores.texto.SinCheqActivos", sesionController.getIdCanal())));
                    } else {
                        if (!chequeratemp.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            FacesContext.getCurrentInstance().addMessage("formSuspCheques1:txtChequeras", new FacesMessage(FacesMessage.SEVERITY_WARN, "", chequeratemp.getRespuestaDTO().getTextoSP()));
                        }
                    }
                }
            }
        }
    }
    
    /**
     * metodo utilitario para consultar los cheques asociados a una chequera
     */
    public void consultarChequesUtilizados() {
        if (!ctaCte.equalsIgnoreCase("-1") && ctaCteSeleccionada != null && !chequera.equalsIgnoreCase("-1")) {
//            ChequeraDTO chequeratemp = cuentaDAO.listarChequesPorChequeraAct(ctaCteSeleccionada.getNumeroCuenta(), chequeraSeleccionada.getNumeroPrimerCheque(), sesionController.getNombreCanal(), sesionController.getIdCanal());
            ChequeraDTO chequeratemp = chequeraDAO.listarChequePorChequeraAct(ctaCteSeleccionada.getNumeroCuenta(), chequeraSeleccionada.getNumeroPrimerCheque(), sesionController.getNombreCanal());
            if (chequeratemp != null && chequeratemp.getRespuestaDTO() != null && chequeratemp.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                    && chequeratemp.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && chequeratemp.getChequesDTO() != null && !chequeratemp.getChequesDTO().isEmpty()) {
                this.chequeraSeleccionada.setChequesDTO(chequeratemp.getChequesDTO());
            } else {
                if (!chequeratemp.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formStsCheques:txtChequeras", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
                } else {
                    if (chequeratemp.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_SIN_RESULTADOS_JPA)) {
                        FacesContext.getCurrentInstance().addMessage("formStsCheques:txtChequeras", new FacesMessage(FacesMessage.SEVERITY_WARN, "", textosController.getNombreTexto("pnw.errores.texto.SinCheqActivos", sesionController.getIdCanal())));
                    } else {
                        if (!chequeratemp.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            FacesContext.getCurrentInstance().addMessage("formStsCheques:txtChequeras", new FacesMessage(FacesMessage.SEVERITY_WARN, "", chequeratemp.getRespuestaDTO().getTextoSP()));
                        }
                    }
                }
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
     * metodo que se encarga de retornar los datos completos de una agencia
     * seleccionada
     *
     * @param codAgencia codigo de la agencia seleccionada
     * @return AgenciaDTO
     */
    public AgenciaDTO agenciaSeleccionadaCompleta(String codAgencia) {
        AgenciaDTO AgenciaTemp = new AgenciaDTO();
        for (AgenciaDTO agenciaCol : agencias) {
            if (agenciaCol.getCodigoAgencia().equals(codAgencia)) {
                AgenciaTemp = agenciaCol;
                break;
            }
        }
        return AgenciaTemp;
    }

    /**
     * metodo action para ir a paso 1 de registro de solicitud de chequera
     */
    public void solicitudChequeraPaso1() {
        mantenerDatosForm = false;//se mantienen los datos del formulario para evitar la recarga de las cuentas y las agencias
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.solicitudChequera.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * metodo action para ir a paso 1 de registro de solicitud de chequera
     */
    public void regresarSolChequeraPaso1() {
        mantenerDatosForm = true;//se mantienen los datos del formulario para evitar la recarga de las cuentas y las agencias
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.solicitudChequera.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * metodo action para ir a paso 2 de registro de solicitud de chequera
     */
    public void solicitudChequeraPaso2() {
        mantenerDatosForm = true;//se mantienen los datos del formulario para evitar la recarga de las cuentas y las agencias
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.solicitudChequera.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo action para ir a paso 3 de registro de solicitud de chequera
     */
    public void solicitudChequeraPaso3() {
        mantenerDatosForm = true;//se mantienen los datos del formulario para evitar la recarga de las cuentas y las agencias
        UtilDTO util = chequeraDAO.solicitarChequeraPN(ctaCteSeleccionada.getNumeroCuenta(), this.tipoChequera, this.cantChequeras, null, null, this.agenciaSeleccionada.getCodigoAgencia(), sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (util != null && util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            if (util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formSolChequera2:divMsjsForm", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.solicitudChequera.url.paso2", sesionController.getIdCanal()));
            } else {
                this.numSolicitud = util.getResuladosDTO().get("numSolicitud").toString();
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_SOL_CHEQUERAS, "", "", "Solicitud de Chequera Exitosa", "", "", "", "", "", "", "");
                sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
                this.notificarSolicitudChequera();
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.solicitudChequera.url.paso3", sesionController.getIdCanal()));
            }
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formSolChequera2:divMsjsForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.solicitudChequera.url.paso2", sesionController.getIdCanal()));
        }

    }

    /**
     * metodo action para ir a paso 1 de registro de suspender de chequera
     */
    public void suspenderChequesPaso1() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suspenderCheques.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * metodo action para ir a paso 1 de registro de suspender de chequera
     */
    public void regresarSuspenderChequesPaso1() {
        sesionController.setValidadorFlujo(1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suspenderCheques.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * metodo action para ir a paso 2 de registro de suspender de chequera
     *
     * @return String
     */
    public void suspenderChequesPaso2() {
        if (chequesSelected != null) {
            listCheques = this.convierteListStringEnString(chequesSelected, ",");
        }

        if (chequeraSeleccionada.getChequesDTO() == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formSuspCheques1:txtChequeras", new FacesMessage(FacesMessage.SEVERITY_WARN, "", textosController.getNombreTexto("pnw.errores.texto.SinCheqActivos", sesionController.getIdCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suspenderCheques.url.paso1", sesionController.getIdCanal()));
        }
        sesionController.setValidadorFlujo(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suspenderCheques.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo action para ir a paso 3 de registro de suspender de chequera
     *
     * @return
     */
    public void suspenderChequesPaso3() {

        UtilDTO utilDTO = new UtilDTO();
        int nroSuspensiones;
        int nroReferencia;

        utilDTO = chequeraDAO.suspenderChequera(ctaCteSeleccionada.getNumeroCuenta(), null, chequeraSeleccionada.getNumeroPrimerCheque(), chequeraSeleccionada.getNumeroUltimoCheque(), listCheques, sesionController.getNombreCanal(), sesionController.getIdCanal());

        if (!utilDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            if (chequesSelected != null) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formSuspCheques2:divMsj", new FacesMessage(FacesMessage.SEVERITY_WARN, "", textosController.getNombreTexto("pnw.errores.texto.suspChequesParcial", sesionController.getNombreCanal()) + utilDTO.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suspenderCheques.url.paso2", sesionController.getIdCanal()));
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formSuspCheques2:divMsj", new FacesMessage(FacesMessage.SEVERITY_WARN, "", utilDTO.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suspenderCheques.url.paso2", sesionController.getIdCanal()));
            }
        } else {
            if (!utilDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formSuspCheques2:divMsj", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suspenderCheques.url.paso2", sesionController.getIdCanal()));
            } else {
                chequesSuspendidos = ((utilDTO.getResuladosDTO().get("chequesSuspendidos")) != null && (!utilDTO.getResuladosDTO().get("chequesSuspendidos").toString().equalsIgnoreCase("")) ? utilDTO.getResuladosDTO().get("chequesSuspendidos").toString() : "0");

                if (utilDTO.getResuladosDTO().get("errorCheques") != null && !utilDTO.getResuladosDTO().get("errorCheques").equals("")) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formSuspCheques2:divMsj", new FacesMessage(FacesMessage.SEVERITY_WARN, "", textosController.getNombreTexto("pnw.errores.texto.suspChequesParcial", sesionController.getNombreCanal()) + utilDTO.getRespuestaDTO().getTextoSP()));
                } else {
                    sesionController.setValidadorFlujo(3);
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_SUSP_CHEQUERAS, "", "", "Suspensión de Cheques y Chequeras Exitosa", "", "", "", "", "", "", "");
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.suspenderCheques.url.paso3", sesionController.getIdCanal()));
                }
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoSolCheqPaso2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_SOL_CHEQUERAS)) {
                sesionController.setValidadorFlujo(1);
                this.solicitudChequeraPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoSolCheqPaso3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_SOL_CHEQUERAS)) {
                sesionController.setValidadorFlujo(1);
                this.solicitudChequeraPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoSuspCheqPaso2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_SUSP_CHEQUERAS)) {
                sesionController.setValidadorFlujo(1);
                this.suspenderChequesPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoSuspCheqPaso3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_SUSP_CHEQUERAS)) {
                sesionController.setValidadorFlujo(1);
                this.suspenderChequesPaso1();
            }
        }
    }

    /**
     * metodo para mostrar o ocultar cheques
     */
    public void accionRenderedCheques() {
        if (tipoSuspension.equalsIgnoreCase("U")) {
            renderedCheques = true;
        } else {
            renderedCheques = false;
            this.chequesSelected = null;
        }
    }

    /**
     * Metodo para cargar las listas iniciales de solicitud de chequeras
     */
    public void consultaInicial() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            sesionController.setValidadorFlujo(1);
            this.limpiarSolChequeras();
            this.consultarCuentasPropias();
            this.consultarAgencias();
        }
    }

    /**
     * metodo utilitario para limpiar los valores de solicitud de chequeras
     */
    public void limpiarSolChequeras() {
        if (!this.mantenerDatosForm) {
            this.reinciarSolChequeras();
        }
    }

    /**
     * de solicitud de chequeras de de solicitud de chequeras
     */
    public void reinciarSolChequeras() {
        if (sesionController.isReiniciarForm()) {
            this.cuentasCliente = new ArrayList<>();
            this.ctaCte = "-1";
            this.agencia = "-1";
            this.tipoChequera = null;
            this.cantChequeras = parametrosController.getNombreParametro("pnw.solicitudChequera.cantChequeras", sesionController.getIdCanal());
            this.cuentasCliente = null;
            this.agencias = null;
            this.ctaCteSeleccionada = null;
            this.agenciaSeleccionada = null;
            sesionController.setReiniciarForm(false);
        }
    }

    /**
     * Metodo para cargar las listas iniciales de suspencion de chequeras
     */
    public void consultaInicialSuspChequeras() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            sesionController.setValidadorFlujo(1);
            this.limpiarSuspChequeras();
            this.consultarCuentasPropias();
            listCheques = null;
        }
    }

    /**
     * metodo utilitario para limpiar los valores de suspencion de chequeras
     */
    public void limpiarSuspChequeras() {
        if (!this.mantenerDatosForm) {
            this.reinciarSuspChequeras();
        }
    }

    /**
     * metodo utilitario para reiniciar los valores de suspencion de chequeras
     */
    public void reinciarSuspChequeras() {
        this.cuentasCliente = new ArrayList<>();
        this.ctaCte = "-1";
        this.chequera = "-1";
        this.cuentasCliente = null;
        this.ctaCteSeleccionada = null;
    }

    /**
     * Metodo para consultar las cuentas propias
     */
    public void consultarCuentasPropias() {
        if (this.cuentasCliente == null) {
            ClienteDTO datosCliente = clienteDAO.listadoCuentasClientes(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());
            if (!datosCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                if (sesionController.getIdTransaccion().equalsIgnoreCase(ID_TRANS_SOL_CHEQUERAS)) {
                    FacesContext.getCurrentInstance().addMessage("formSolChequera1:txtCtaCte", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
                }
                if (sesionController.getIdTransaccion().equalsIgnoreCase(ID_TRANS_SUSP_CHEQUERAS)) {
                    FacesContext.getCurrentInstance().addMessage("formSuspCheques1:txtCtaCte", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
                }

            } else {
                if (datosCliente.getCuentasCorrienteDTO() != null && datosCliente.getCuentasCorrienteDTO().size() > 0) {
                    if (!datosCliente.getCuentasCorrienteDTO().isEmpty()) {
                        for (int i = 0; i < datosCliente.getCuentasCorrienteDTO().size(); i++) {
                            datosCliente.getCuentasCorrienteDTO().get(i).setSemilla(sesionController.getSemilla());
                        }
                    }
                    this.cuentasCliente = datosCliente.getCuentasCorrienteDTO();
                }else{
                    FacesContext.getCurrentInstance().addMessage("formSolChequera1:divMsjSinCtas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.ctaCteRequerida", sesionController.getIdCanal())));
                }
            }
        }

    }

    /**
     * Metodo para consultar las cuentas propias
     */
    public void consultarAgencias() {
        if (this.agencias == null) {
            DelSurDTO delSur = delSurDAO.obtenerAgencias(sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (delSur != null && delSur.getRespuestaDTO() != null && delSur.getRespuestaDTO().getCodigo().equalsIgnoreCase(this.CODIGO_RESPUESTA_EXITOSO) && delSur.getAgencias() != null) {
                this.agencias = delSur.getAgencias();
            }
        }
    }

    /**
     * metodo para validar la cuenta propia seleccionada
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarCtaPropia(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().isEmpty() || value.equals("") || value.equals("-1")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("campoRequerido", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("campoRequerido", sesionController.getNombreCanal()).getMensajeUsuario()));
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
    public void cuerpoChequeraPDF(Document pdf) throws BadElementException, DocumentException {
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
            Image image = Image.getInstance(ChequeraController.class.getResource("/imgPDF/logoBanner.png"));

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
            //T�tulo que contiene el Nombre y el N�mero del Producto 
            // PdfPCell cell = new PdfPCell();

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas0 = {1.95f, 3.05f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas0);

            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            //T�tulo que contiene el Nombre y el N�mero del Producto 
            PdfPCell cell = new PdfPCell(new Phrase(textosController.getNombreTexto("pnw.solicitudChequera.descargaPdf.tituloPDF", sesionController.getIdCanal()), fontTituloBold));

            cell.setColspan(2);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(20f);
            table.addCell(cell);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            pdf.add(new Paragraph(" "));

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);
            table.setWidthPercentage(70f);

            //T�tulos de las Columnas 
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.solicitudChequera.descargaPdf.fecEjecucion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(fecha2(new Date()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.solicitudChequera.descargaPdf.nroCuenta", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.ctaCteSeleccionada.getNombreProducto() + " - " + this.formatoAsteriscosWeb(this.ctaCteSeleccionada.getNumeroCuenta()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.solicitudChequera.descargaPdf.tipoChequera", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase("25 cheques", font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.solicitudChequera.descargaPdf.cantChequera", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.cantChequeras, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.solicitudChequera.descargaPdf.agenciaRetiro", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.agenciaSeleccionada.getNombreAgencia() + " - " + this.agenciaSeleccionada.getNombreEstado(), font));

            //Contenido de las Columnas, data
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
          //  table.addCell(new Phrase((this.cuentaSeleccionada), font));

            //Agregamos la tabla al PDF
            pdf.add(table);

            // pdf.setMargins(150f, 20f, 50f, 10f);
            pdf.setMargins(50f, 3f, 22f, 18f);
        } catch (Exception e) {
            //manejar excepciones de reportes
        }
    }

    /**
     * metodo encargado de generar el contenido del listado los cheques a
     * suspender
     *
     * @param pdf hoja de trabajo de PDF
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoChequesPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 14, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //Variables para el manejo del monto, Phrase espera siempre un String
            String monto = null;
            String tipoSusp = null;

            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(ChequeraController.class.getResource("/imgPDF/logoBanner.png"));

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
            //T�tulo que contiene el Nombre y el N�mero del Producto 
            // PdfPCell cell = new PdfPCell();

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas0 = {1.95f, 3.05f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas0);

            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            //Titulo que contiene el Nombre y el Numero del Producto 
            PdfPCell cell = new PdfPCell(new Phrase(textosController.getNombreTexto("pnw.suspensionCheques.descargaPdf.tituloPDF", sesionController.getIdCanal()), fontTituloBold));

            cell.setColspan(2);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(20f);
            table.addCell(cell);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            pdf.add(new Paragraph(" "));

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);
            table.setWidthPercentage(70f);

            //T�tulos de las Columnas 
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.suspensionCheques.descargaPdf.fecEjecucion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(fecha2(new Date()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.suspensionCheques.descargaPdf.nroCuenta", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            //table.addCell(new Phrase(this.ctaCte, font));
            table.addCell(new Phrase(this.ctaCteSeleccionada.getNombreProducto() + " - " + formatoAsteriscosWeb(this.ctaCteSeleccionada.getNumeroCuenta()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.suspensionCheques.descargaPdf.chequera", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase("Serie de Cheques: " + this.chequeraSeleccionada.getNumeroPrimerCheque() + " al " + chequeraSeleccionada.getNumeroUltimoCheque(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.suspensionCheques.descargaPdf.tipoSuspension", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));

            if (this.tipoSuspension.equalsIgnoreCase("T")) {
                tipoSusp = "Suspensión de Chequera";
            } else {
                if (this.tipoSuspension.equalsIgnoreCase("U")) {
                    tipoSusp = "Suspensión de Cheques";
                }
            }

            table.addCell(new Phrase(tipoSusp, font));
            if (this.tipoSuspension.equalsIgnoreCase("U")) {
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.suspensionCheques.descargaPdf.cheques", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(chequesSuspendidos, font));
            }

            //Contenido de las Columnas, data
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
          //  table.addCell(new Phrase((this.cuentaSeleccionada), font));

            //Agregamos la tabla al PDF
            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

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
    public void detalleSolicitudChequeraPDF() throws IOException, DocumentException {

        if (sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_SUSP_CHEQUERAS)) {
            if (tipoSuspension.equalsIgnoreCase("U")) {
                tipoPDF = "SuspensionCheques.pdf";
            } else {
                if (tipoSuspension.equalsIgnoreCase("T")) {
                    tipoPDF = "SuspensionChequera.pdf";
                }
            }
        } else if (sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_SOL_CHEQUERAS)) {
            if (agenciaSeleccionada != null && !agenciaSeleccionada.getNombreAgencia().equalsIgnoreCase("")) {
                tipoPDF = "SolicitudChequera.pdf";
            }
        }

        try {

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/octet-stream");
            //response.addHeader("Content-disposition", "attachment;filename=\"SolicitudChequera.pdf\"");
            response.addHeader("Content-disposition", "attachment;filename=\"" + tipoPDF + "\"");
            Document document = new Document(PageSize.A4.rotate());//(), 20, 20, 20, 20);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            //Instanciamos la clase utilitaria que realiza la paginaci�n
            PaginarPDF paginar = new PaginarPDF();
            Rectangle headerBox = new Rectangle(22, 45, 1320, 700);

            //Establecemos el tama�o del documento
            writer.setBoxSize("headerBox", headerBox);

            //Pasamos por par�metros los eventos que se producen al generar el PDF
            writer.setPageEvent(paginar);

            document.open();

            //Invocamos el método que genera el PDF            
            if (sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_SUSP_CHEQUERAS)) {
                this.cuerpoChequesPDF(document);
            } else if (sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_SOL_CHEQUERAS)) {
                this.cuerpoChequeraPDF(document);
            }

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

    public void inicioSuspchequeras() {
        if (sesionController.isReiniciarForm()) {
            if (!FacesContext.getCurrentInstance().isPostback()) {
                sesionController.setValidadorFlujo(1);
                this.cuentasCliente = new ArrayList<>();
                this.ctaCte = "-1";
                this.chequera = "-1";
                this.cuentasCliente = null;
                this.ctaCteSeleccionada = null;
                this.listCheques = null;
                this.renderedTipoSuspension = false;
                this.renderedCheques = false;
                this.chequesSelected = null;
                sesionController.setReiniciarForm(false);
                this.consultarCuentasPropias();
            }
        }
    }
    
    public void inicioStsCheques() {
        if (sesionController.isReiniciarForm()) {
            if (!FacesContext.getCurrentInstance().isPostback()) {
                sesionController.setValidadorFlujo(1);
                this.cuentasCliente = new ArrayList<>();
                this.ctaCte = "-1";
                this.chequera = "-1";
                this.cuentasCliente = null;
                this.ctaCteSeleccionada = null;
                this.listCheques = null;
                this.renderedTipoSuspension = false;
                this.renderedCheques = false;
                this.chequesSelected = null;
                sesionController.setReiniciarForm(false);
                this.consultarCuentasPropias();
            }
        }
    }

    //
    /**
     * Notificación realizada vía email Método que setea los datos y parametros
     * para el envio del correo luego de la ejecución del proceso
     */
    public void notificarSolicitudChequera() {

        String asunto = "Notificación Solicitud de Chequera DELSUR";
        StringBuilder texto = new StringBuilder("");

        texto = new StringBuilder("Estimado(a) ");
        texto.append(sesionController.getUsuario().getNombre());
        texto.append(NUEVALINEAEMAIL);
        texto.append(NUEVALINEAEMAIL);
        texto.append(parametrosController.getNombreParametro("banco.nombre", sesionController.getIdCanal()));
        texto.append(" le informa que se ha realizado la Solicitud de Chequera: ");
        texto.append(this.getNumSolicitud());
        texto.append(" el día: ");
        texto.append(BDSUtil.formatearFecha(this.getFechaEjecucion(), BDSUtil.FORMATO_FECHA_COMPLETA));
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
