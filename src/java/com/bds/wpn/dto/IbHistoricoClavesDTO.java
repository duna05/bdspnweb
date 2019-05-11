/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author wilmer.rondon
 */
public class IbHistoricoClavesDTO {
    private BigDecimal id;
    private String clave;
    private Date fechaCreacionDate;
    private Date fechaVencimientoDate;
    private IbUsuariosDTO idUsuario;
    private List<IbHistoricoClavesDTO> ibHistoricosClavesDTO;
    private RespuestaDTO respuestaDTO;     //objeto para manejar el estatus de las respuestas

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Date getFechaCreacionDate() {
        return fechaCreacionDate;
    }

    public void setFechaCreacionDate(Date fechaCreacionDate) {
        this.fechaCreacionDate = fechaCreacionDate;
    }

    public Date getFechaVencimientoDate() {
        return fechaVencimientoDate;
    }

    public void setFechaVencimientoDate(Date fechaVencimientoDate) {
        this.fechaVencimientoDate = fechaVencimientoDate;
    }

    public IbUsuariosDTO getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(IbUsuariosDTO idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<IbHistoricoClavesDTO> getIbHistoricosClavesDTO() {
        return ibHistoricosClavesDTO;
    }

    public void setIbHistoricosClavesDTO(List<IbHistoricoClavesDTO> ibHistoricosClavesDTO) {
        this.ibHistoricosClavesDTO = ibHistoricosClavesDTO;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    
}
