/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

/**
 *
 * @author juan.faneite
 */
public class IbPerfilesPilotoDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private String nombre;
    private Collection<IbTransaccionesPerfilesDTO> ibTransaccionesPerfilesDTOCollection;
    private Collection<IbUsuariosPerfilPilotoDTO> ibUsuariosPerfilPilotoDTOCollection;
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
     * @param respuestaDTO
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
     * Obtener nombre
     *
     * @return String
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asignar nombre
     *
     * @param nombre String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtener listado de las transacciones de los perfiles
     *
     * @return Collection<IbTransaccionesPerfilesDTO>
     */
    public Collection<IbTransaccionesPerfilesDTO> getIbTransaccionesPerfilesDTOCollection() {
        return ibTransaccionesPerfilesDTOCollection;
    }

    /**
     * Asignar listado de las transacciones de los perfiles
     *
     * @param ibTransaccionesPerfilesDTOCollection
     * Collection<IbTransaccionesPerfilesDTO>
     */
    public void setIbTransaccionesPerfilesDTOCollection(Collection<IbTransaccionesPerfilesDTO> ibTransaccionesPerfilesDTOCollection) {
        this.ibTransaccionesPerfilesDTOCollection = ibTransaccionesPerfilesDTOCollection;
    }

    /**
     * Obtener listado de los usuarios
     *
     * @return Collection<IbUsuariosPerfilPilotoDTO>
     */
    public Collection<IbUsuariosPerfilPilotoDTO> getIbUsuariosPerfilPilotoDTOCollection() {
        return ibUsuariosPerfilPilotoDTOCollection;
    }

    /**
     * Asignar listado de usuarios
     *
     * @param ibUsuariosPerfilPilotoDTOCollection
     */
    public void setIbUsuariosPerfilPilotoDTOCollection(Collection<IbUsuariosPerfilPilotoDTO> ibUsuariosPerfilPilotoDTOCollection) {
        this.ibUsuariosPerfilPilotoDTOCollection = ibUsuariosPerfilPilotoDTOCollection;
    }

}
