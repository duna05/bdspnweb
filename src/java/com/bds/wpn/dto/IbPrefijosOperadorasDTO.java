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
 * Clase IbPrefijosOperadorasDTO
 * @author wilmer.rondon
 */
public class IbPrefijosOperadorasDTO implements Serializable{
    
    private BigDecimal id;
    private String operadora;
    private List<IbPrefijosOperadorasDTO> ibPrefijosOperadorasDTO;
    private RespuestaDTO respuestaDTO;     //objeto para manejar el estatus de las respuestas

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getOperadora() {
        return operadora;
    }

    public void setOperadora(String operadora) {
        this.operadora = operadora;
    }

    public List<IbPrefijosOperadorasDTO> getIbPrefijosOperadorasDTO() {
        return ibPrefijosOperadorasDTO;
    }

    public void setIbPrefijosOperadorasDTO(List<IbPrefijosOperadorasDTO> ibPrefijosOperadorasDTO) {
        this.ibPrefijosOperadorasDTO = ibPrefijosOperadorasDTO;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }    
    
}
