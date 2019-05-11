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
 * @author wilmer.rondon
 */
public class IbMediosNotificacionDTO implements Serializable{
    
    private BigDecimal id;
    private String nombreMedio;
    private List <IbMediosNotificacionDTO> ibMediosNotificacionDTO;
    private RespuestaDTO respuestaDTO;//todos los DTO deben tener este objeto

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getNombreMedio() {
        return nombreMedio;
    }

    public void setNombreMedio(String nombreMedio) {
        this.nombreMedio = nombreMedio;
    }

    public List<IbMediosNotificacionDTO> getIbMediosNotificacionDTO() {
        return ibMediosNotificacionDTO;
    }

    public void setIbMediosNotificacionDTO(List<IbMediosNotificacionDTO> ibMediosNotificacionDTO) {
        this.ibMediosNotificacionDTO = ibMediosNotificacionDTO;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }
}
