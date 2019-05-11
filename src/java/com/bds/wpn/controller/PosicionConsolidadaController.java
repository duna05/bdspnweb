/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.PosicionConsolidadaDTO;
import com.bds.wpn.dto.PrestamoDTO;
import com.bds.wpn.dto.TarjetasCreditoDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author cesar.mujica
 */
@Named("wpnPosicionConsolidadaController")
@RequestScoped
public class PosicionConsolidadaController extends BDSUtil implements Serializable {

    @EJB
    ClienteDAO clienteDAO;

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;
    
    private PosicionConsolidadaDTO posicionDTO;
    private int regBita = 0;
    private boolean inicioPosCon = true;             //indicador para recargar la consulta de la posicion consolidada

    public boolean isInicioPosCon() {
        return inicioPosCon;
    }

    public void setInicioPosCon(boolean inicioPosCon) {
        this.inicioPosCon = inicioPosCon;
    }

    /**
     * Obtener la posicion consolidada
     *
     * @return PosicionConsolidadaDTO
     */
    public PosicionConsolidadaDTO getPosicionDTO() {

        if (inicioPosCon) {
            regBitacora(1);
            posicionDTO = clienteDAO.consultarPosicionConsolidadaCliente(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());
            if (posicionDTO.getRespuestaDTO() != null) {
                if (!posicionDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formPosCons:msjPosCons", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("error.generico.msj", sesionController.getNombreCanal())));
                }
            }
            inicioPosCon = false;
            agregarSemillasEncriptado();
        }
        return posicionDTO;
    }

    /**
     * Asigna la posicion consoldada
     *
     * @param posicionDTO PosicionConsolidadaDTO
     */
    public void setPosicionDTO(PosicionConsolidadaDTO posicionDTO) {
        this.posicionDTO = posicionDTO;
    }

    /**
     * Asignar el listado cuentas de ahorro
     *
     * @return List<CuentaDTO>
     */
    public List<CuentaDTO> getListCuentaAhorro() {
        if (posicionDTO == null) {
            regBitacora(1);
            posicionDTO = clienteDAO.consultarPosicionConsolidadaCliente(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (posicionDTO.getRespuestaDTO() != null) {
                if (!posicionDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formPosCons:msjPosCons", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("error.generico.msj", sesionController.getNombreCanal())));
                }
            }
            agregarSemillasEncriptado();
        }
        return posicionDTO.getCuentasAhorro();
    }

    /**
     * Obtener el listado de cuentas corrientes
     *
     * @return List<CuentaDTO>
     */
    public List<CuentaDTO> getListCuentasCorriente() {
        if (posicionDTO == null) {
            regBitacora(1);
            posicionDTO = clienteDAO.consultarPosicionConsolidadaCliente(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (posicionDTO.getRespuestaDTO() != null) {
                if (!posicionDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formPosCons:msjPosCons", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("error.generico.msj", sesionController.getNombreCanal())));
                }
            }
            agregarSemillasEncriptado();
        }
        return posicionDTO.getCuentasCorriente();
    }

    /**
     * Obtener el listado cuentas de moneda extrajera
     *
     * @return List<CuentaDTO>
     */
    public List<CuentaDTO> getListCuentasME() {
        if (posicionDTO == null) {
            regBitacora(1);
            posicionDTO = clienteDAO.consultarPosicionConsolidadaCliente(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (posicionDTO.getRespuestaDTO() != null) {
                if (!posicionDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formPosCons:msjPosCons", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("error.generico.msj", sesionController.getNombreCanal())));
                }
            }
            agregarSemillasEncriptado();
        }
        return posicionDTO.getCuentasME();
    }

    /**
     * Obtener el listado de prestamo
     *
     * @return List<PrestamoDTO>
     */
    public List<PrestamoDTO> getPrestamos() {
        if (posicionDTO == null) {
            regBitacora(1);
            posicionDTO = clienteDAO.consultarPosicionConsolidadaCliente(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (posicionDTO.getRespuestaDTO() != null) {
                if (!posicionDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formPosCons:msjPosCons", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("error.generico.msj", sesionController.getNombreCanal())));
                }
            }
            agregarSemillasEncriptado();
        }
        return posicionDTO.getPrestamos();
    }

    /**
     * Obtener el listado de tarjetas de credito
     *
     * @return the tarjetasCredito
     */
    public List<TarjetasCreditoDTO> getTarjetasCredito() {
        if (posicionDTO == null) {
            regBitacora(1);
            posicionDTO = clienteDAO.consultarPosicionConsolidadaCliente(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (posicionDTO.getRespuestaDTO() != null) {
                if (!posicionDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formPosCons:msjPosCons", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("error.generico.msj", sesionController.getNombreCanal())));
                }
            }
            agregarSemillasEncriptado();
        }
        return posicionDTO.getTarjetasCredito();
    }

    /**
     * Metodo que dirige a la pagina de detalle de la cuenta y retora a la
     * pagina
     *
     * @return String
     */
    public String actionDetalleCuenta() {
        return parametrosController.getNombreParametro("detalle.cuenta.url", sesionController.getIdCanal());
    }

    /**
     * Registra en la tabla bitacora
     *
     * @param var int
     */
    public void regBitacora(int var) {
        regBita += var;
        if (regBita == 1) {
            if (!FacesContext.getCurrentInstance().isPostback()) {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), ID_TRANS_POS_CONSOLIDADA, "", "", "Consulta Posicion Consolidad", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
            }
        }
    }

    /**
     * metodo para inicializar la consulta de posicion consolidada esto forza la
     * consulta de los datos al invocar a la pagina nuevamente
     */
    public void iniciarPosCons() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.setInicioPosCon(true);
            sesionController.setReiniciarForm(true);
        }
    }

    /**
     * metodo utilitario para agregar las semillas al encriptar los datos
     * sensibles
     */
    public void agregarSemillasEncriptado() {
        if (posicionDTO.getCuentasAhorro() != null && !posicionDTO.getCuentasAhorro().isEmpty()) {
            for (int i = 0; i < posicionDTO.getCuentasAhorro().size(); i++) {
                posicionDTO.getCuentasAhorro().get(i).setSemilla(sesionController.getSemilla());

            }
        }
        if (posicionDTO.getCuentasCorriente() != null && !posicionDTO.getCuentasCorriente().isEmpty()) {
            for (int i = 0; i < posicionDTO.getCuentasCorriente().size(); i++) {
                posicionDTO.getCuentasCorriente().get(i).setSemilla(sesionController.getSemilla());

            }
        }
        if (posicionDTO.getCuentasME() != null && !posicionDTO.getCuentasME().isEmpty()) {
            for (int i = 0; i < posicionDTO.getCuentasME().size(); i++) {
                posicionDTO.getCuentasME().get(i).setSemilla(sesionController.getSemilla());

            }
        }
        if (posicionDTO.getPrestamos() != null && !posicionDTO.getPrestamos().isEmpty()) {
            for (int i = 0; i < posicionDTO.getPrestamos().size(); i++) {
                posicionDTO.getPrestamos().get(i).setSemilla(sesionController.getSemilla());

            }
        }
        if (posicionDTO.getTarjetasCredito() != null && !posicionDTO.getTarjetasCredito().isEmpty()) {
            for (int i = 0; i < posicionDTO.getTarjetasCredito().size(); i++) {
                posicionDTO.getTarjetasCredito().get(i).setSemilla(sesionController.getSemilla());

            }
        }
    }
}
