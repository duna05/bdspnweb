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
public class FichaClienteRefTarjetaDTO extends BDSUtil implements Serializable{
        private BigDecimal codTipoTarjeta;
        private String desTipoTarjeta;
        private BigDecimal codBancoTarjeta;        
        private String nomBancoTarjeta;
        private BigDecimal numeroTarjeta; 
        private String fechaEmision;
        private BigDecimal limiteTarjeta;
        
        private RespuestaDTO respuesta;
    
        private List<FichaClienteRefTarjetaDTO> ibFichaClienteRefTarjetaList;
        
        private List<FichaClienteRefTarjetaDTO> ibRefTarjetaList;

    public List<FichaClienteRefTarjetaDTO> getIbFichaClienteRefTarjetaList() {
        return ibFichaClienteRefTarjetaList;
    }

    public void setIbFichaClienteRefTarjetaList(List<FichaClienteRefTarjetaDTO> ibFichaClienteRefTarjetaList) {
        this.ibFichaClienteRefTarjetaList = ibFichaClienteRefTarjetaList;
    }

    public List<FichaClienteRefTarjetaDTO> getIbRefTarjetaList() {
        return ibRefTarjetaList;
    }

    public void setIbRefTarjetaList(List<FichaClienteRefTarjetaDTO> ibRefTarjetaList) {
        this.ibRefTarjetaList = ibRefTarjetaList;
    }
        
        
        
        

    public BigDecimal getCodTipoTarjeta() {
        return codTipoTarjeta;
    }

    public void setCodTipoTarjeta(BigDecimal codTipoTarjeta) {
        this.codTipoTarjeta = codTipoTarjeta;
    }

    public String getDesTipoTarjeta() {
        return desTipoTarjeta;
    }

    public void setDesTipoTarjeta(String desTipoTarjeta) {
        this.desTipoTarjeta = desTipoTarjeta;
    }

    public BigDecimal getCodBancoTarjeta() {
        return codBancoTarjeta;
    }

    public void setCodBancoTarjeta(BigDecimal codBancoTarjeta) {
        this.codBancoTarjeta = codBancoTarjeta;
    }

    public String getNomBancoTarjeta() {
        return nomBancoTarjeta;
    }

    public void setNomBancoTarjeta(String nomBancoTarjeta) {
        this.nomBancoTarjeta = nomBancoTarjeta;
    }

    public BigDecimal getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(BigDecimal numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public BigDecimal getLimiteTarjeta() {
        return limiteTarjeta;
    }

    public void setLimiteTarjeta(BigDecimal limiteTarjeta) {
        this.limiteTarjeta = limiteTarjeta;
    }

    public RespuestaDTO getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaDTO respuesta) {
        this.respuesta = respuesta;
    }

    /*public List<FichaClienteRefTarjetaDTO> getIbFichaClienteRefTarjetaList() {
        return ibFichaClienteRefTarjetaList;
    }

    public void setIbFichaClienteRefTarjetaList(List<FichaClienteRefTarjetaDTO> ibFichaClienteRefTarjetaList) {
        this.ibFichaClienteRefTarjetaList = ibFichaClienteRefTarjetaList;
    }*/
    
}
