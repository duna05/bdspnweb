/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author juan.faneite
 */
public class CabeceraEstadoCtaDTO implements Serializable {
    
    private BigDecimal cantCreditos;
    private BigDecimal cantDebitos;
    private BigDecimal cantDepositos;
    private BigDecimal cantCheques;
    private BigDecimal saldoCreditos;
    private BigDecimal saldoDebitos;
    private BigDecimal saldoDepositos;
    private BigDecimal saldoCheques;
    private BigDecimal saldoInicial;
    private BigDecimal saldoTotal;   
    private BigDecimal codAgencia;
    private String direccion;
    private String Nombre;

    public BigDecimal getCantCreditos() {
        return cantCreditos;
    }

    public void setCantCreditos(BigDecimal cantCreditos) {
        this.cantCreditos = cantCreditos;
    }

    public BigDecimal getCantDebitos() {
        return cantDebitos;
    }

    public void setCantDebitos(BigDecimal cantDebitos) {
        this.cantDebitos = cantDebitos;
    }

    public BigDecimal getCantDepositos() {
        return cantDepositos;
    }

    public void setCantDepositos(BigDecimal cantDepositos) {
        this.cantDepositos = cantDepositos;
    }

    public BigDecimal getCantCheques() {
        return cantCheques;
    }

    public void setCantCheques(BigDecimal cantCheques) {
        this.cantCheques = cantCheques;
    }

    public BigDecimal getSaldoCreditos() {
        return saldoCreditos;
    }

    public void setSaldoCreditos(BigDecimal saldoCreditos) {
        this.saldoCreditos = saldoCreditos;
    }

    public BigDecimal getSaldoDebitos() {
        return saldoDebitos;
    }

    public void setSaldoDebitos(BigDecimal saldoDebitos) {
        this.saldoDebitos = saldoDebitos;
    }

    public BigDecimal getSaldoDepositos() {
        return saldoDepositos;
    }

    public void setSaldoDepositos(BigDecimal saldoDepositos) {
        this.saldoDepositos = saldoDepositos;
    }

    public BigDecimal getSaldoCheques() {
        return saldoCheques;
    }

    public void setSaldoCheques(BigDecimal saldoCheques) {
        this.saldoCheques = saldoCheques;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getCodAgencia() {
        return codAgencia;
    }

    public void setCodAgencia(BigDecimal codAgencia) {
        this.codAgencia = codAgencia;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public BigDecimal getSaldoTotal() {
        return saldoTotal;
    }

    public void setSaldoTotal(BigDecimal saldoTotal) {
        this.saldoTotal = saldoTotal;
    }
    
    
    
    
    
}
