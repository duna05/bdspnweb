/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * @author wilmer.rondon
 */
public class IbUsuariosEventosMediosDTO implements Serializable{
    
    private BigDecimal id;
    private IbUsuariosDTO idUsuario;
    private IbMediosNotificacionDTO idMedio;
    private IbEventosNotificacionDTO idEvento;
    private BigDecimal montoMin;
    private List<IbUsuariosEventosMediosDTO> ibUsuariosEventosMediosDTO;
    private RespuestaDTO respuestaDTO;//todos los DTO deben tener este objeto

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public IbUsuariosDTO getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(IbUsuariosDTO idUsuario) {
        this.idUsuario = idUsuario;
    }

    public IbMediosNotificacionDTO getIdMedio() {
        return idMedio;
    }

    public void setIdMedio(IbMediosNotificacionDTO idMedio) {
        this.idMedio = idMedio;
    }

    public IbEventosNotificacionDTO getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(IbEventosNotificacionDTO idEvento) {
        this.idEvento = idEvento;
    }

    public BigDecimal getMontoMin() {
        return montoMin;
    }

    public void setMontoMin(BigDecimal montoMin) {
        this.montoMin = montoMin;
    }

    public List<IbUsuariosEventosMediosDTO> getIbUsuariosEventosMediosDTO() {
        return ibUsuariosEventosMediosDTO;
    }

    public void setIbUsuariosEventosMediosDTO(List<IbUsuariosEventosMediosDTO> ibUsuariosEventosMediosDTO) {
        this.ibUsuariosEventosMediosDTO = ibUsuariosEventosMediosDTO;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }
    
}
