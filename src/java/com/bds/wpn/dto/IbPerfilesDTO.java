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
public class IbPerfilesDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private String nombre;
    private Character estatus;
    private Collection<IbUsuariosDTO> ibUsuariosDTOCollection;
    private Collection<IbTransaccionesPerfilesDTO> ibTransaccionesPerfilesDTOCollection;
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
     * Asinar id
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Obtener nombre
     * @return String
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asignar nombre
     * @param nombre String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtener estatus
     * @return Character
     */
    public Character getEstatus() {
        return estatus;
    }

    /**
     * Asignar estatus
     * @param estatus Character
     */
    public void setEstatus(Character estatus) {
        this.estatus = estatus;
    }

    /**
     * Obtener usuaurio coleccion
     * @return Collection<IbUsuariosDTO>
     */
    public Collection<IbUsuariosDTO> getIbUsuariosDTOCollection() {
        return ibUsuariosDTOCollection;
    }

    /**
     * Asignar listado de usuarios
     * @param ibUsuariosDTOCollection Collection<IbUsuariosDTO>
     */
    public void setIbUsuariosDTOCollection(Collection<IbUsuariosDTO> ibUsuariosDTOCollection) {
        this.ibUsuariosDTOCollection = ibUsuariosDTOCollection;
    }

    /**
     * Obtener listado de transacciones
     * @return Collection<IbTransaccionesPerfilesDTO>
     */
    public Collection<IbTransaccionesPerfilesDTO> getIbTransaccionesPerfilesDTOCollection() {
        return ibTransaccionesPerfilesDTOCollection;
    }

    /**
     * Asignar listado de transaciones de los perfiles
     * @param ibTransaccionesPerfilesDTOCollection Collection<IbTransaccionesPerfilesDTO>
     */
    public void setIbTransaccionesPerfilesDTOCollection(Collection<IbTransaccionesPerfilesDTO> ibTransaccionesPerfilesDTOCollection) {
        this.ibTransaccionesPerfilesDTOCollection = ibTransaccionesPerfilesDTOCollection;
    }

}
