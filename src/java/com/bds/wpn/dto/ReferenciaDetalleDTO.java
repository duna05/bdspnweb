/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author juan.faneite
 */
public class ReferenciaDetalleDTO implements Serializable{
    
    private String nroReferencia;
    private String numeroCuenta;
    private String nombreCliente;
    private String tipoCuenta;
    private String regionPertenece;
    private String nombrePertenece;
    private Date fechaInicioDate;
    private BigDecimal saldo;
    private String cifrasReferencia;
    private byte[] firmaBlob;
    private RespuestaDTO respuestaDTO;

    /**
     * 
     * @return se obtiene la firma
     */
    public byte[] getFirmaBlob() {
        return firmaBlob;
    }

    /**
     * 
     * @param firmaBlob se inserta la firma
     */
    public void setFirmaBlob(byte[] firmaBlob) {
        this.firmaBlob = firmaBlob;
    }


    /**
     * 
     * @return obtiene el numero de referencia
     */
    public String getNroReferencia() {
        return nroReferencia;
    }

    /**
     
     * @param nroReferencia ingresa numero de referencia
     */
    public void setNroReferencia(String nroReferencia) {
        this.nroReferencia = nroReferencia;
    }

    
    /**
     * 
     * @return nombre de la region a la que pertenece
     */
    public String getNombrePertenece() {
        return nombrePertenece;
    }

    /**
     * 
     * @param nombrePertenece nombre de la region a la que pertenece
     */
    public void setNombrePertenece(String nombrePertenece) {
        this.nombrePertenece = nombrePertenece;
    }

    
    /**
     * 
     * @return nombre completo del cliente
     */
    public String getNombreCliente() {
        return nombreCliente;
    }

    /**
     * 
     * @param nombreCliente nombre completo del cliente
     */
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    
    /**
     * 
     * @return numero de la cuenta
     */
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    /**
     * 
     * @param numeroCuenta numero de la cuenta
     */
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    /**
     * 
     * @return tipo de la cuenta
     */
    public String getTipoCuenta() {
        return tipoCuenta;
    }

    /**
     * 
     * @param tipoCuenta tipo de la cuenta
     */
    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    /**
     * 
     * @return region a la que pertenece
     */
    public String getRegionPertenece() {
        return regionPertenece;
    }

    /**
     * 
     * @param regionPertenece region a la que pertenece
     */
    public void setRegionPertenece(String regionPertenece) {
        this.regionPertenece = regionPertenece;
    }

    /**
     * 
     * @return fecha inicio
     */
    public Date getFechaInicioDate() {
        return fechaInicioDate;
    }

    /**
     * 
     * @param fechaInicioDate
     */
    public void setFechaInicioDate(Date fechaInicioDate) {
        this.fechaInicioDate = fechaInicioDate;
    }

    /**
     * 
     * @return saldo
     */
    public BigDecimal getSaldo() {
        return saldo;
    }

    /**
     * 
     * @param saldo saldo
     */
    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    /**
     * 
     * @return cifras
     */
    public String getCifrasReferencia() {
        return cifrasReferencia;
    }

    /**
     * 
     * @param cifrasReferencia cifras
     */
    public void setCifrasReferencia(String cifrasReferencia) {
        this.cifrasReferencia = cifrasReferencia;
    }

    /**
     * @return the respuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * @param respuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }
}