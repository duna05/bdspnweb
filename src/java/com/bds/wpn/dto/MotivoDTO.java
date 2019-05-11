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
public class MotivoDTO extends BDSUtil implements Serializable {

    private BigDecimal codigoMotivo;
    private String nombre;
    private RespuestaDTO respuesta;
    private List<MotivoDTO> ibMotivoList;
    private List<String> iMotivoList;
    private List<MotivoDTO> ibMotivoListSI;

    public List<MotivoDTO> getIbMotivoListSI() {
        return ibMotivoListSI;
    }

    public void setIbMotivoListSI(List<MotivoDTO> ibMotivoListSI) {
        this.ibMotivoListSI = ibMotivoListSI;
    }  
    

    public List<String> getiMotivoList() {
        return iMotivoList;
    }

    public void setiMotivoList(List<String> iMotivoList) {
        this.iMotivoList = iMotivoList;
    }
    
    

    public BigDecimal getCodigoMotivo() {
        return codigoMotivo;
    }

    public void setCodigoMotivo(BigDecimal codigoMotivo) {
        this.codigoMotivo = codigoMotivo;
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

    public List<MotivoDTO> getIbMotivoList() {
        return ibMotivoList;
    }

    public void setIbMotivoList(List<MotivoDTO> ibMotivoList) {
        this.ibMotivoList = ibMotivoList;
    }
    
}
