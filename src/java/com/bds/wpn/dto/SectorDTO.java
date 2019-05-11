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
public class SectorDTO extends BDSUtil implements Serializable{
    
    private BigDecimal codigoSector;
    private String descripcion;
    private RespuestaDTO respuesta;
    private List<String> ibSectorList;

    public BigDecimal getCodigoSector() {
        return codigoSector;
    }

    public void setCodigoSector(BigDecimal codigoSector) {
        this.codigoSector = codigoSector;
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

    public List<String> getIbSectorList() {
        return ibSectorList;
    }

    public void setIbSectorList(List<String> ibSectorList) {
        this.ibSectorList = ibSectorList;
    }
    
    
}
