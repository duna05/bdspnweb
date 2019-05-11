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
public class SubSubClasificacionDTO extends BDSUtil implements Serializable {
    
    private BigDecimal codigoSubSubClasificacion;
    private String descripcion;
    private RespuestaDTO respuesta;
    private List<String> SubSubClasificacionList;
    private List<List<String>> SubSubClasificacionListBi = new ArrayList<List<String>>();

    public List<List<String>> getSubSubClasificacionListBi() {
        return SubSubClasificacionListBi;
    }

    public void setSubSubClasificacionListBi(List<List<String>> SubSubClasificacionListBi) {
        this.SubSubClasificacionListBi = SubSubClasificacionListBi;
    }
    
    

    public BigDecimal getCodigoSubSubClasificacion() {
        return codigoSubSubClasificacion;
    }

    public void setCodigoSubSubClasificacion(BigDecimal codigoSubSubClasificacion) {
        this.codigoSubSubClasificacion = codigoSubSubClasificacion;
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

    public List<String> getSubSubClasificacionList() {
        return SubSubClasificacionList;
    }

    public void setSubSubClasificacionList(List<String> SubSubClasificacionList) {
        this.SubSubClasificacionList = SubSubClasificacionList;
    }
}
