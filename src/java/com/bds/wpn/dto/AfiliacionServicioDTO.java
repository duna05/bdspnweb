/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cesar.mujica
 */
public class AfiliacionServicioDTO implements Serializable {
    private int afiliacionID;
    private BeneficiarioServicioDTO beneficiario;
    private String codigoAbonado;
    private int codigoCliente;
    private String estado;
    private ServicioDTO servicio;
    private List<AfiliacionServicioDTO> afiliacionesServicio;
    private RespuestaDTO respuestaDTO;

    public AfiliacionServicioDTO() {
        this.beneficiario = new BeneficiarioServicioDTO();
        this.servicio = new ServicioDTO();
        this.afiliacionesServicio = new ArrayList<>();
    }
    
    

    public int getAfiliacionID() {
        return afiliacionID;
    }

    public void setAfiliacionID(int afiliacionID) {
        this.afiliacionID = afiliacionID;
    }

    public BeneficiarioServicioDTO getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(BeneficiarioServicioDTO beneficiario) {
        this.beneficiario = beneficiario;
    }

    public String getCodigoAbonado() {
        return codigoAbonado;
    }

    public void setCodigoAbonado(String codigoAbonado) {
        this.codigoAbonado = codigoAbonado;
    }

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ServicioDTO getServicio() {
        return servicio;
    }

    public void setServicio(ServicioDTO servicio) {
        this.servicio = servicio;
    }

    public List<AfiliacionServicioDTO> getAfiliacionesServicio() {
        return afiliacionesServicio;
    }

    public void setAfiliacionesServicio(List<AfiliacionServicioDTO> afiliacionesServicio) {
        this.afiliacionesServicio = afiliacionesServicio;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }
    
    
    
    
}
