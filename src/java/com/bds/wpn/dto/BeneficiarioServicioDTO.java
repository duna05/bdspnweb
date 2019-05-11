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
public class BeneficiarioServicioDTO implements Serializable {
    private String identificacion;
    private String nombre;
    private String tipoIdentificacion;
    private List<BeneficiarioServicioDTO> beneficiariosServicio;
    private RespuestaDTO respuestaDTO;

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public List<BeneficiarioServicioDTO> getBeneficiariosServicio() {
        return beneficiariosServicio;
    }

    public void setBeneficiariosServicio(List<BeneficiarioServicioDTO> beneficiariosServicio) {
        this.beneficiariosServicio = beneficiariosServicio;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }
    
    
    
    
}
