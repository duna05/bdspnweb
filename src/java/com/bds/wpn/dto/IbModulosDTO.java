/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

/**
 *
 * @author juan.faneite
 */
public class IbModulosDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private String nombre;
    private BigInteger posicion;
    private Character estatus;
    private IbCanalDTO idCanalDTO;
    private Collection<IbTransaccionesDTO> ibTransaccionesDTOCollection;
    private RespuestaDTO respuestaDTO;

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
     * Obtener posicion
     * @return BigInteger
     */
    public BigInteger getPosicion() {
        return posicion;
    }

    /**
     * Asignar posicion
     * @param posicion BigInteger
     */
    public void setPosicion(BigInteger posicion) {
        this.posicion = posicion;
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
     * Obtener canal
     * @return IbCanalDTO
     */
    public IbCanalDTO getIdCanalDTO() {
        return idCanalDTO;
    }

    /**
     * Asignar canal
     * @param idCanalDTO IbCanalDTO
     */
    public void setIdCanalDTO(IbCanalDTO idCanalDTO) {
        this.idCanalDTO = idCanalDTO;
    }

    /**
     * Obtener listado de transacciones 
     * @return Collection<IbTransaccionesDTO>
     */
    public Collection<IbTransaccionesDTO> getIbTransaccionesDTOCollection() {
        return ibTransaccionesDTOCollection;
    }

    /**
     * Asinar listado de transacciones
     * @param ibTransaccionesDTOCollection Collection<IbTransaccionesDTO>
     */
    public void setIbTransaccionesDTOCollection(Collection<IbTransaccionesDTO> ibTransaccionesDTOCollection) {
        this.ibTransaccionesDTOCollection = ibTransaccionesDTOCollection;
    }
    
    /**
     * metodo que retorna el id conun caracter especial para agregarlo al identificador del menu
     * @return 
     */
    public String getIdMenu() {
       return ("menu_"+id.toString());
   }

}
