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
public class SubClasificacionDTO extends BDSUtil implements Serializable{
    
    private BigDecimal codigoSubClasificacion;
    private String descripcion;
    private RespuestaDTO respuesta;
    private List<String> listSubClasificacionList;
    private List<List<String>> ibClasificacionListBi = new ArrayList<List<String>>();

    public List<List<String>> getIbClasificacionListBi() {
        return ibClasificacionListBi;
    }

    public void setIbClasificacionListBi(List<List<String>> ibClasificacionListBi) {
        this.ibClasificacionListBi = ibClasificacionListBi;
    }
    
    

    public BigDecimal getCodigoSubClasificacion() {
        return codigoSubClasificacion;
    }

    public void setCodigoSubClasificacion(BigDecimal codigoSubClasificacion) {
        this.codigoSubClasificacion = codigoSubClasificacion;
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

    public List<String> getListSubClasificacionList() {
        return listSubClasificacionList;
    }

    public void setListSubClasificacionList(List<String> listSubClasificacionList) {
        this.listSubClasificacionList = listSubClasificacionList;
    }
    
}
