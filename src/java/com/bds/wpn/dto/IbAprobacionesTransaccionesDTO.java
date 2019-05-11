/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author juan.faneite
 */
public class IbAprobacionesTransaccionesDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private Date fechaHora;
    private IbUsuariosDTO idUsuarioDTO;
    private IbTransaccionesPendientesDTO idTransaccionPendienteDTO;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto

    /**
     * Obtener el objeto respuesta
     *
     * @return RespuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asinar el objeto respuesta
     *
     * @param respuestaDTO RespuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    /**
     * Obtener el id
     *
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Asignar el id
     *
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Obtener la fecha y hora
     *
     * @return Date
     */
    public Date getFechaHora() {
        return fechaHora;
    }

    /**
     * Asignar la fecha y hora
     *
     * @param fechaHora Date
     */
    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    /**
     * Obtener el usuario
     *
     * @return IbUsuariosDTO
     */
    public IbUsuariosDTO getIdUsuarioDTO() {
        return idUsuarioDTO;
    }

    /**
     * Asignar el usuario
     *
     * @param idUsuarioDTO IbUsuariosDTO
     */
    public void setIdUsuarioDTO(IbUsuariosDTO idUsuarioDTO) {
        this.idUsuarioDTO = idUsuarioDTO;
    }

    /**
     * Obtener la transaccion pendiente
     *
     * @return IbTransaccionesPendientesDTO
     */
    public IbTransaccionesPendientesDTO getIdTransaccionPendienteDTO() {
        return idTransaccionPendienteDTO;
    }

    /**
     * Asignar la transaccion pendiente
     *
     * @param idTransaccionPendienteDTO IbTransaccionesPendientesDTO
     */
    public void setIdTransaccionPendienteDTO(IbTransaccionesPendientesDTO idTransaccionPendienteDTO) {
        this.idTransaccionPendienteDTO = idTransaccionPendienteDTO;
    }

}
