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
public class IbUsuariosPerfilPilotoDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private IbUsuariosDTO idUsuarioDTO;
    private IbPerfilesPilotoDTO idPerfilPilotoDTO;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto

    /**
     * Obtener respuesta
     *
     * @return RespuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asignar respuesta
     *
     * @param respuestaDTO RespuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    /**
     * Obtener id
     *
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Asignar id
     *
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Obtener usuario
     *
     * @return IbUsuariosDTO
     */
    public IbUsuariosDTO getIdUsuarioDTO() {
        return idUsuarioDTO;
    }

    /**
     * Asignar usuarios
     *
     * @param idUsuarioDTO IbUsuariosDTO
     */
    public void setIdUsuarioDTO(IbUsuariosDTO idUsuarioDTO) {
        this.idUsuarioDTO = idUsuarioDTO;
    }

    /**
     * Obtener perfil piloto
     *
     * @return IbPerfilesPilotoDTO
     */
    public IbPerfilesPilotoDTO getIdPerfilPilotoDTO() {
        return idPerfilPilotoDTO;
    }

    /**
     * Asignar perfil piloto
     *
     * @param idPerfilPilotoDTO IbPerfilesPilotoDTO
     */
    public void setIdPerfilPilotoDTO(IbPerfilesPilotoDTO idPerfilPilotoDTO) {
        this.idPerfilPilotoDTO = idPerfilPilotoDTO;
    }

}
