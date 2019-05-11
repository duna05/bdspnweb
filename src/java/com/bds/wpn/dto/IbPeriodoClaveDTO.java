/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Clase IbPeriodoClaveDTO
 * @author wilmer.rondon
 */
public class IbPeriodoClaveDTO implements Serializable{
    private BigDecimal id;
    private Integer cantidad;
    private String descripcion;
    private List<IbPeriodoClaveDTO> ibPeriodoClaveDTO;
    private RespuestaDTO respuestaDTO;     //objeto para manejar el estatus de las respuestas

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<IbPeriodoClaveDTO> getIbPeriodoClaveDTO() {
        return ibPeriodoClaveDTO;
    }

    public void setIbPeriodoClaveDTO(List<IbPeriodoClaveDTO> ibPeriodoClaveDTO) {
        this.ibPeriodoClaveDTO = ibPeriodoClaveDTO;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

}
