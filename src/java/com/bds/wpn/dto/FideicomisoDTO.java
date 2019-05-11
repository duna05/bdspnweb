/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.math.BigDecimal;

/**
 *
 * @author cesar.mujica
 */
public class FideicomisoDTO {
    private String codigoPlan;          //codigo de plan de fideicomiso
    private String nombrePlan;          //nombre del plan de fideicomiso
    private String tipoPlan;            //tipo de plan de fideicomiso
    private BigDecimal saldoDisponible; //saldo disponible del fideicomiso
    private BigDecimal totalAporte;     //total de aporte de fideicomiso
    private BigDecimal totalRetiro;     //total retirado de fideicomiso
    private RespuestaDTO respuesta;                 //manejo de respuestas

    public String getCodigoPlan() {
        return codigoPlan;
    }

    public void setCodigoPlan(String codigoPlan) {
        this.codigoPlan = codigoPlan;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    public String getTipoPlan() {
        return tipoPlan;
    }

    public void setTipoPlan(String tipoPlan) {
        this.tipoPlan = tipoPlan;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    public void setSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    public BigDecimal getTotalAporte() {
        return totalAporte;
    }

    public void setTotalAporte(BigDecimal totalAporte) {
        this.totalAporte = totalAporte;
    }

    public BigDecimal getTotalRetiro() {
        return totalRetiro;
    }

    public void setTotalRetiro(BigDecimal totalRetiro) {
        this.totalRetiro = totalRetiro;
    }

    public RespuestaDTO getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaDTO respuesta) {
        this.respuesta = respuesta;
    }
    
}
