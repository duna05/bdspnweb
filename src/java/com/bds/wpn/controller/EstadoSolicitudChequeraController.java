/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ChequeraDAO;
import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.EstadoSolicitudChequeraDTO;
import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author robinson.rodriguez
 */
@Named("wpnEstadoSolicitudChequeraController")
@SessionScoped
public class EstadoSolicitudChequeraController extends BDSUtil implements Serializable {

    @EJB
    ChequeraDAO chequeraDAO;

    @EJB
    ClienteDAO clienteDAO;

    @Inject
    TextosController textosController;

    @Inject
    InicioSesionController sesionController;

    @Inject
    ParametrosController parametrosController;

    private String ctaCte = "-1";
    private String auxComp = "-1";
    private CuentaDTO ctaCteSeleccionada = null;
    private Collection<CuentaDTO> cuentasCliente;
    private String itemValue = "-1";
    private boolean estadoSolicitud = false;
    private EstadoSolicitudChequeraDTO estadoSolicitudChequeraDTO = new EstadoSolicitudChequeraDTO();

    public void consultaInicial() {
        reinciarSolChequeras();
        consultarCuentasPropias();
    }

    public void reinciarSolChequeras() {
        if (sesionController.isReiniciarForm()) {
            this.cuentasCliente = new ArrayList<>();
            this.ctaCte = "-1";
            this.cuentasCliente = null;
            this.ctaCteSeleccionada = null;
            this.itemValue = "-1";
            this.estadoSolicitud = false;
            sesionController.setReiniciarForm(false);
        }
    }

    public void setCtaCte(String ctaCte) {
        this.ctaCte = ctaCte;
    }

    /**
     * metodo utilitario para cargar dinamicamente las chequeras asociadas a una
     * cuenta corriente
     */
    public void consultarEstadoSolicitudChequera() {

        this.setCtaCte(ctaCte);

        estadoSolicitudChequeraDTO = chequeraDAO.obtenerEstadoSolicitudChequera(ctaCte.trim(), sesionController.getIdCanal().trim());

        if ((!estadoSolicitudChequeraDTO.equals(null)) && (!ctaCte.equalsIgnoreCase(itemValue))) {
            this.setEstadoSolicitud(true);
        } else {
            this.setEstadoSolicitud(false);
        }

    }

    /**
     * Metodo para consultar las cuentas propias
     */
    public void consultarCuentasPropias() {
        if (this.cuentasCliente == null) {
            ClienteDTO datosCliente = clienteDAO.listadoCuentasCorrientesCliente(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());
            if (!datosCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formEstadoSolicitudCheques:txtCtaCte", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
            } else {
                if (datosCliente.getCuentasCorrienteDTO() != null && datosCliente.getCuentasCorrienteDTO().size() > 0) {
                    if (!datosCliente.getCuentasCorrienteDTO().isEmpty()) {
                        for (int i = 0; i < datosCliente.getCuentasCorrienteDTO().size(); i++) {
                            datosCliente.getCuentasCorrienteDTO().get(i).setSemilla(sesionController.getSemilla());
                        }
                    }
                    this.cuentasCliente = datosCliente.getCuentasCorrienteDTO();
                } else {
                    FacesContext.getCurrentInstance().addMessage("formEstadoSolicitudCheques:txtCtaCte", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.ctaCteRequerida", sesionController.getIdCanal())));
                }
            }
        }
    }

    //Setters and getters
    public ChequeraDAO getCuentaDAO() {
        return chequeraDAO;
    }

    public void setCuentaDAO(ChequeraDAO chequeraDAO) {
        this.chequeraDAO = chequeraDAO;
    }

    public TextosController getTextosController() {
        return textosController;
    }

    public void setTextosController(TextosController textosController) {
        this.textosController = textosController;
    }

    public InicioSesionController getSesionController() {
        return sesionController;
    }

    public void setSesionController(InicioSesionController sesionController) {
        this.sesionController = sesionController;
    }

    public ParametrosController getParametrosController() {
        return parametrosController;
    }

    public void setParametrosController(ParametrosController parametrosController) {
        this.parametrosController = parametrosController;
    }

    public String getAuxComp() {
        return auxComp;
    }

    public void setAuxComp(String auxComp) {
        this.auxComp = auxComp;
    }

    public CuentaDTO getCtaCteSeleccionada() {
        return ctaCteSeleccionada;
    }

    public void setCtaCteSeleccionada(CuentaDTO ctaCteSeleccionada) {
        this.ctaCteSeleccionada = ctaCteSeleccionada;
    }

    public Collection<CuentaDTO> getCuentasCliente() {
        return cuentasCliente;
    }

    public void setCuentasCliente(Collection<CuentaDTO> cuentasCliente) {
        this.cuentasCliente = cuentasCliente;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public EstadoSolicitudChequeraDTO getEstadoSolicitudChequeraDTO() {
        return estadoSolicitudChequeraDTO;
    }

    public void setEstadoSolicitudChequeraDTO(EstadoSolicitudChequeraDTO estadoSolicitudChequeraDTO) {
        this.estadoSolicitudChequeraDTO = estadoSolicitudChequeraDTO;
    }

    public boolean isEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(boolean estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }

    public ClienteDAO getClienteDAO() {
        return clienteDAO;
    }

    public String getCtaCte() {
        return ctaCte;
    }

}
