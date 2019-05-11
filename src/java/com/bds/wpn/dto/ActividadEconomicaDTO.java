/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alejandro.flores
 */
public class ActividadEconomicaDTO extends BDSUtil implements Serializable{
    
    private BigDecimal codigoActividadEconomica;
    private String descripcion;
    private RespuestaDTO respuesta;
    private List<ActividadEconomicaDTO> actividadEconomicaList;

    public List<ActividadEconomicaDTO> getActividadEconomicaList() {
        return actividadEconomicaList;
    }

    public void setActividadEconomicaList(List<ActividadEconomicaDTO> actividadEconomicaList) {
        this.actividadEconomicaList = actividadEconomicaList;
    }
    
    public BigDecimal getCodigoActividadEconomica() {
        return codigoActividadEconomica;
    }

    public void setCodigoActividadEconomica(BigDecimal codigoActividadEconomica) {
        this.codigoActividadEconomica = codigoActividadEconomica;
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
