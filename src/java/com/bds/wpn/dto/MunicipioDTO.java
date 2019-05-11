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
public class MunicipioDTO extends BDSUtil implements Serializable{
    
    private BigDecimal codigoMunicipio;
    private String nombre;
    private RespuestaDTO respuesta;
    private List<String> ibMunicipioList;

    public BigDecimal getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(BigDecimal codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
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

    public List<String> getIbMunicipioList() {
        return ibMunicipioList;
    }

    public void setIbMunicipioList(List<String> ibMunicipioList) {
        this.ibMunicipioList = ibMunicipioList;
    }
}
