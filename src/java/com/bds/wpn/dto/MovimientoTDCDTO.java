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
 * @author cesar.mujica
 */
public class MovimientoTDCDTO implements Serializable {

    private Date fechaCorteDate;        //Fecha del corte
    private Date fechaRegistroDate;     //Fecha cuando se registra en la tarjeta del cliente.
    private Date fechaOperacionDate;    //Fecha en la que se realizo la transaccion.
    private BigDecimal monto;       //Monto de la transaccion.
    private String descripcion;     //Descripcion de la transaccion.
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto
    
    private String fechaString;
    private String fechaRegString;
    private String fechaTransString;
    private BigDecimal montoDivisa;
    private BigDecimal intereses;
    private String referencia;

    /**
     * Obtener respuesta
     * @return RespuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asignar respuesta
     * @param respuestaDTO 
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    /**
     * Fecha del corte
     *
     * @return Fecha del corte
     */
    public Date getFechaCorteDate() {
        return fechaCorteDate;
    }

    /**
     * Fecha del corte
     *
     * @param fechaCorte Fecha del corte
     */
    public void setFechaCorteDate(Date fechaCorte) {
        this.fechaCorteDate = fechaCorte;
    }

    /**
     * Fecha cuando se registra en la tarjeta del cliente.
     *
     * @return Fecha cuando se registra en la tarjeta del cliente.
     */
    public Date getFechaRegistroDate() {
        return fechaRegistroDate;
    }

    /**
     * Fecha cuando se registra en la tarjeta del cliente.
     *
     * @param fechaRegistro Fecha cuando se registra en la tarjeta del cliente.
     */
    public void setFechaRegistroDate(Date fechaRegistro) {
        this.fechaRegistroDate = fechaRegistro;
    }

    /**
     * Fecha en la que se realizo la transaccion.
     *
     * @return Fecha en la que se realizo la transaccion.
     */
    public Date getFechaOperacionDate() {
        return fechaOperacionDate;
    }

    /**
     * Fecha en la que se realizo la transaccion.
     *
     * @param fechaOperacion Fecha en la que se realizo la transaccion.
     */
    public void setFechaOperacionDate(Date fechaOperacion) {
        this.fechaOperacionDate = fechaOperacion;
    }

    /**
     * Monto de la transaccion.
     *
     * @return Monto de la transaccion.
     */
    public BigDecimal getMonto() {
        return monto;
    }

    /**
     * Monto de la transaccion.
     *
     * @param monto Monto de la transaccion.
     */
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    /**
     * Descripcion de la transaccion.
     *
     * @return Descripcion de la transaccion.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Descripcion de la transaccion.
     *
     * @param descripcion Descripcion de la transaccion.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaString() {        
        return (fechaString != null && !fechaString.trim().equalsIgnoreCase("") && !fechaString.startsWith("0/0")?fechaString:"");
    }

    public void setFechaString(String fechaString) {
        this.fechaString = fechaString;
    }

    public String getFechaRegString() {
        return (fechaRegString != null && !fechaRegString.trim().equalsIgnoreCase("") && !fechaRegString.startsWith("0/0")?fechaRegString:"");
    }

    public void setFechaRegString(String fechaRegString) {
        this.fechaRegString = fechaRegString;
    }

    public String getFechaTransString() {
        return fechaTransString;
    }

    public void setFechaTransString(String fechaTransString) {
        this.fechaTransString = fechaTransString;
    }

    public BigDecimal getMontoDivisa() {
        return montoDivisa;
    }

    public void setMontoDivisa(BigDecimal montoDivisa) {
        this.montoDivisa = montoDivisa;
    }

    public BigDecimal getIntereses() {
        return intereses;
    }

    public void setIntereses(BigDecimal intereses) {
        this.intereses = intereses;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
    
    
}
