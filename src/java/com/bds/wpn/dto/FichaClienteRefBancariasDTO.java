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
public class FichaClienteRefBancariasDTO extends BDSUtil implements Serializable {
    
    private BigDecimal codigoFinanciera;
    private BigDecimal tipoFinanciera;
    private String tipoCuentaFinanciera;
    private String numeroCuentaReferencia;
    private String fechaApertura; 
    private String  nombreBanco;
    private String descCuenta;
    private String saldoPromedio;
    
    
    private List<List<String>> fichaClienteRefBancarias = new ArrayList<List<String>>(); 
    private List<FichaClienteRefBancariasDTO> ibReferenciaList;
    private FichaClienteRefBancariasDTO fichaClienteRefBancariasDTO;

    public FichaClienteRefBancariasDTO getFichaClienteRefBancariasDTO() {
        return fichaClienteRefBancariasDTO;
    }

    public void setFichaClienteRefBancariasDTO(FichaClienteRefBancariasDTO fichaClienteRefBancariasDTO) {
        this.fichaClienteRefBancariasDTO = fichaClienteRefBancariasDTO;
    }
    
    private RespuestaDTO respuesta;

    public List<FichaClienteRefBancariasDTO> getIbReferenciaList() {
        return ibReferenciaList;
    }

    public void setIbReferenciaList(List<FichaClienteRefBancariasDTO> ibReferenciaList) {
        this.ibReferenciaList = ibReferenciaList;
    } 

    public List<List<String>> getFichaClienteRefBancarias() {
        return fichaClienteRefBancarias;
    }

    public void setFichaClienteRefBancarias(List<List<String>> fichaClienteRefBancarias) {
        this.fichaClienteRefBancarias = fichaClienteRefBancarias;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getDescCuenta() {
        return descCuenta;
    }

    public void setDescCuenta(String descCuenta) {
        this.descCuenta = descCuenta;
    }

    public String getSaldoPromedio() {
        return saldoPromedio;
    }

    public void setSaldoPromedio(String saldoPromedio) {
        this.saldoPromedio = saldoPromedio;
    }
    
    
    
    public BigDecimal getCodigoFinanciera() {
        return codigoFinanciera;
    }

    public void setCodigoFinanciera(BigDecimal codigoFinanciera) {
        this.codigoFinanciera = codigoFinanciera;
    }

    public BigDecimal getTipoFinanciera() {
        return tipoFinanciera;
    }

    public void setTipoFinanciera(BigDecimal tipoFinanciera) {
        this.tipoFinanciera = tipoFinanciera;
    }

    public String getTipoCuentaFinanciera() {
        return tipoCuentaFinanciera;
    }

    public void setTipoCuentaFinanciera(String tipoCuentaFinanciera) {
        this.tipoCuentaFinanciera = tipoCuentaFinanciera;
    }

    public String getNumeroCuentaReferencia() {
        return numeroCuentaReferencia;
    }

    public void setNumeroCuentaReferencia(String numeroCuentaReferencia) {
        this.numeroCuentaReferencia = numeroCuentaReferencia;
    }

    public String getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(String fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public RespuestaDTO getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaDTO respuesta) {
        this.respuesta = respuesta;
    }
}
