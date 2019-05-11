/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author cesar.mujica
 */
public class ServicioDTO implements Serializable {
    private int codigoServicio;
    private String descripcionServicio;
    private String proveedorServicio;
    private List<ServicioDTO> servicios;
    private RespuestaDTO respuestaDTO;

    public int getCodigoServicio() {
        return codigoServicio;
    }

    public void setCodigoServicio(int codigoServicio) {
        this.codigoServicio = codigoServicio;
    }

    public String getDescripcionServicio() {
        return descripcionServicio;
    }

    public void setDescripcionServicio(String descripcionServicio) {
        this.descripcionServicio = descripcionServicio;
    }

    public String getProveedorServicio() {
        return proveedorServicio;
    }

    public void setProveedorServicio(String proveedorServicio) {
        this.proveedorServicio = proveedorServicio;
    }

    public List<ServicioDTO> getServicios() {
        return servicios;
    }

    public void setServicios(List<ServicioDTO> servicios) {
        this.servicios = servicios;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }
    
    
    
}
