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
 * @author alejandro.flores
 */
public class SubtipoClienteDTO implements Serializable{
    
    private BigDecimal codigoSubtipo;
    private String descripcion;
    private RespuestaDTO respuesta;
    private RespuestaDTO respuestaDTO;
    private List<String> listTipo;

    public List<String> getListTipo() {
        return listTipo;
    }

    public void setListTipo(List<String> listTipo) {
        this.listTipo = listTipo;
    }
    
    

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }
    
    public BigDecimal getCodigoSubtipo() {
        return codigoSubtipo;
    }

    public void setCodigoSubtipo(BigDecimal codigoSubtipo) {
        this.codigoSubtipo = codigoSubtipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public RespuestaDTO getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaDTO respuesta) {
        this.respuesta = respuesta;
    }
  
}
