/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author alejandro.flores
 */
public class EstadoDTO extends BDSUtil implements Serializable{
    private BigDecimal codigoEstado;
    private String nombre;
    private RespuestaDTO respuesta;

    public BigDecimal getCodigoEstado() {
        return codigoEstado;
    }

    public void setCodigoEstado(BigDecimal codigoEstado) {
        this.codigoEstado = codigoEstado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public RespuestaDTO getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaDTO respuesta) {
        this.respuesta = respuesta;
    }

    public List<String> getIbEstadoList() {
        return ibEstadoList;
    }

    public void setIbEstadoList(List<String> ibEstadoList) {
        this.ibEstadoList = ibEstadoList;
    }
    private List<String> ibEstadoList;
    
}
