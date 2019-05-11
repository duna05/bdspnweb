/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author juan.faneite
 */
public class IbMensajesUsuariosDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private Character estatus;
    private IbUsuariosDTO idUsuarioDTO;
    private IbMensajesDTO idMensajeDTO;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto

    /**
     * Obtener respuesta
     * @return RespuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asignar respuesta
     * @param respuestaDTO RespuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    /**
     * Obtener id
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Asignar id
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Obtener estatus
     * @return Character
     */
    public Character getEstatus() {
        return estatus;
    }

    /**
     * Asinar estatus
     * @param estatus Character
     */
    public void setEstatus(Character estatus) {
        this.estatus = estatus;
    }

    /**
     * Asignar usuario
     * @return IbUsuariosDTO
     */
    public IbUsuariosDTO getIdUsuarioDTO() {
        return idUsuarioDTO;
    }

    /**
     * Asignar usuario
     * @param idUsuarioDTO IbUsuariosDTO
     */
    public void setIdUsuarioDTO(IbUsuariosDTO idUsuarioDTO) {
        this.idUsuarioDTO = idUsuarioDTO;
    }

    /**
     * Obtener mensaje
     * @return IbMensajesDTO
     */
    public IbMensajesDTO getIdMensajeDTO() {
        return idMensajeDTO;
    }

    /**
     * Asignar mensaje
     * @param idMensajeDTO IbMensajesDTO
     */
    public void setIdMensajeDTO(IbMensajesDTO idMensajeDTO) {
        this.idMensajeDTO = idMensajeDTO;
    }

}
