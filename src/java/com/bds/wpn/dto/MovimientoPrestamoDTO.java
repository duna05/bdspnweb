/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author rony.rodriguez
 */
public class MovimientoPrestamoDTO {

    private BigDecimal numeroCuota;     //Numero de cuotas a pagar.
    private Date fechaVencimientoDate;	//Fecha de vencimiento.
    private Date fechaDePagoDate;           //Fecha para relizar los pagos del prestamo.
    private BigDecimal capitalPagado;	//Capital pagado.
    private BigDecimal interesesPagado;	//Intereses pagado
    private BigDecimal moraPagada;	//Mora pagada.
    private BigDecimal seguroPagado;	//Seguro pagado.
    private BigDecimal capital;         //Monto del capital
    private BigDecimal intereses;       //Intereses a pagar.
    private BigDecimal mora;            //Intereses a pagar por mora.
    private BigDecimal seguro;  	//Saldo de seguro a pagar.
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    /**
     * Numero de cuotas a pagar.
     *
     * @return Numero de cuotas a pagar.
     */
    public BigDecimal getNumeroCuota() {
        return numeroCuota;
    }

    /**
     * Numero de cuotas a pagar.
     *
     * @param numeroCuota Numero de cuotas a pagar.
     */
    public void setNumeroCuota(BigDecimal numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    /**
     * Fecha de vencimiento.
     *
     * @return Date Fecha de vencimiento.
     */
    public Date getFechaVencimientoDate() {
        return fechaVencimientoDate;
    }

    /**
     * Fecha de vencimiento.
     *
     * @param fechaVencimientoDate
     */
    public void setFechaVencimientoDate(Date fechaVencimientoDate) {
        this.fechaVencimientoDate = fechaVencimientoDate;
    }

    /**
     * Fecha para relizar los pagos del prestamo.
     *
     * @return Date Fecha para relizar los pagos del prestamo.
     */
    public Date getFechaDePagoDate() {
        return fechaDePagoDate;
    }

    /**
     * Fecha para relizar los pagos del prestamo.
     *
     * @param fechaDePagoDate
     */
    public void setFechaDePagoDate(Date fechaDePagoDate) {
        this.fechaDePagoDate = fechaDePagoDate;
    }

    /**
     * Capital pagado.
     *
     * @return BigDecimal Capital pagado.
     */
    public BigDecimal getCapitalPagado() {
        return capitalPagado;
    }

    /**
     * Capital pagado.
     *
     * @param capitalPagado BigDecimal Capital pagado.
     */
    public void setCapitalPagado(BigDecimal capitalPagado) {
        this.capitalPagado = capitalPagado;
    }

    /**
     * Intereses pagado
     *
     * @return BigDecimal Intereses pagado
     */
    public BigDecimal getInteresesPagado() {
        return interesesPagado;
    }

    /**
     * Intereses pagado
     *
     * @param interesesPagado BigDecimal Intereses pagado
     */
    public void setInteresesPagado(BigDecimal interesesPagado) {
        this.interesesPagado = interesesPagado;
    }

    /**
     * Mora pagada.
     *
     * @return BigDecimal Mora pagada.
     */
    public BigDecimal getMoraPagada() {
        return moraPagada;
    }

    /**
     * Mora pagada.
     *
     * @param moraPagada BigDecimal Mora pagada.
     */
    public void setMoraPagada(BigDecimal moraPagada) {
        this.moraPagada = moraPagada;
    }

    /**
     * Seguro pagado.
     *
     * @return BigDecimal Seguro pagado.
     */
    public BigDecimal getSeguroPagado() {
        return seguroPagado;
    }

    /**
     * Seguro pagado.
     *
     * @param seguroPagado BigDecimal Seguro pagado.
     */
    public void setSeguroPagado(BigDecimal seguroPagado) {
        this.seguroPagado = seguroPagado;
    }

    /**
     * Monto del capital
     *
     * @return BigDecimal Monto del capital
     */
    public BigDecimal getCapital() {
        return capital;
    }

    /**
     * Monto del capital
     *
     * @param capital BigDecimal Monto del capital
     */
    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    /**
     * Intereses a pagar.
     *
     * @return BigDecimal Intereses a pagar.
     */
    public BigDecimal getIntereses() {
        return intereses;
    }

    /**
     * Intereses a pagar.
     *
     * @param intereses BigDecimal Intereses a pagar.
     */
    public void setIntereses(BigDecimal intereses) {
        this.intereses = intereses;
    }

    /**
     * Intereses a pagar por mora.
     *
     * @return BigDecimal Intereses a pagar por mora.
     */
    public BigDecimal getMora() {
        return mora;
    }

    /**
     * Intereses a pagar por mora.
     *
     * @param mora BigDecimal Intereses a pagar por mora.
     */
    public void setMora(BigDecimal mora) {
        this.mora = mora;
    }

    /**
     * Saldo de seguro a pagar.
     *
     * @return BigDecimal Saldo de seguro a pagar.
     */
    public BigDecimal getSeguro() {
        return seguro;
    }

    /**
     * Saldo de seguro a pagar.
     *
     * @param seguro BigDecimal Saldo de seguro a pagar.
     */
    public void setSeguro(BigDecimal seguro) {
        this.seguro = seguro;
    }
}
