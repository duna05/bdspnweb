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
public class DepositoPlazoDTO implements Serializable {

    private String numeroDeposito;	//Numero de deposito de 20 digitos.
    private String codigoTipoProducto;	//Codigo del tipo de producto (ver Tipos de producto).
    private String nombreProducto;	//Nombre del producto (ver Tipos de producto).
    private String siglasTipoMoneda;	//Siglas nemonicas del tipo de moneda.
    private BigDecimal saldoDisponible;	//Saldo disponible de la cuenta (saldo total en linea).
    private BigDecimal plazo;           //Numero de dias en los que el deposito estara vigente.
    private Date fechaVigencia;         //Fecha de inicio del deposito a plazo a partir de la cual el plazo es considerado.
    private Date fechaVencimiento;      //Fecha de vencimiento del deposito a plazo.
    private Long tasa;                   //Valor total de la tasa a pagar sobre el deposito a plazo.
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    /**
     * Retorna Numero de deposito de 20 digitos.
     *
     * @return String numeroDeposito Numero de cuenta de 20 digitos.
     */
    public String getNumeroDeposito() {
        return numeroDeposito;
    }

    /**
     * Asigna Numero de deposito de 20 digitos.
     *
     * @param String numeroDeposito
     */
    public void setNumeroDeposito(String numeroDeposito) {
        this.numeroDeposito = numeroDeposito;
    }

    /**
     * Retorna Codigo del tipo de producto (ver Tipos de producto).
     *
     * @return String codigoTipoProducto Codigo del tipo de producto (ver Tipos
     * de producto).
     */
    public String getCodigoTipoProducto() {
        return codigoTipoProducto;
    }

    /**
     * Asigna Codigo del tipo de producto (ver Tipos de producto).
     *
     * @param String codigoCliente
     */
    public void setCodigoTipoProducto(String codigoTipoProducto) {
        this.codigoTipoProducto = codigoTipoProducto;
    }

    /**
     * Retorna Nombre del producto (ver Tipos de producto).
     *
     * @return String nombreProducto Nombre del producto (ver Tipos de
     * producto).
     */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /**
     * Asigna Nombre del producto (ver Tipos de producto).
     *
     * @param String nombreProducto
     */
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    /**
     * Retorna Siglas nemonicas del tipo de moneda.
     *
     * @return String siglasTipoMoneda Siglas nemonicas del tipo de moneda.
     */
    public String getSiglasTipoMoneda() {
        return siglasTipoMoneda;
    }

    /**
     * Asigna Siglas nemonicas del tipo de moneda.
     *
     * @param String siglasTipoMoneda
     */
    public void setSiglasTipoMoneda(String siglasTipoMoneda) {
        this.siglasTipoMoneda = siglasTipoMoneda;
    }

    /**
     * Retorna Saldo disponible de la cuenta (saldo total en linea).
     *
     * @return BigDecimal saldoDisponible Saldo disponible de la cuenta (saldo
     * total en linea).
     */
    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    /**
     * Asigna Saldo disponible de la cuenta (saldo total en linea).
     *
     * @param BigDecimal saldoDisponible
     */
    public void setSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    /**
     * Retorna Numero de dias en los que el deposito estara vigente.
     *
     * @return BigDecimal plazo Numero de dias en los que el deposito estara
     * vigente.
     */
    public BigDecimal getPlazo() {
        return plazo;
    }

    /**
     * Asigna Siglas Numero de dias en los que el deposito estara vigente.
     *
     * @param BigDecimal plazo
     */
    public void setPlazo(BigDecimal plazo) {
        this.plazo = plazo;
    }

    /**
     * Retorna Fecha de inicio del deposito a plazo a partir de la cual el plazo
     * es considerado.
     *
     * @return Date fechaVigencia Fecha de inicio del deposito a plazo a partir
     * de la cual el plazo es considerado.
     */
    public Date getFechaVigencia() {
        return fechaVigencia;
    }

    /**
     * Asigna Fecha de inicio del deposito a plazo a partir de la cual el plazo
     * es considerado.
     *
     * @param Date fechaVigencia
     */
    public void setFechaVigencia(Date fechaVigencia) {
        this.fechaVigencia = fechaVigencia;
    }

    /**
     * Retorna Fecha de vencimiento del deposito a plazo.
     *
     * @return Date fechaVencimiento fecha de vencimiento del deposito a plazo.
     */
    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * Asigna Fecha de vencimiento del deposito a plazo.
     *
     * @param Date fechaVencimiento
     */
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * Retorna Valor total de la tasa a pagar sobre el deposito a plazo.
     *
     * @return Long tasa Valor total de la tasa a pagar sobre el deposito a
     * plazo.
     */
    public Long getTasa() {
        return tasa;
    }

    /**
     * Asigna Siglas Valor total de la tasa a pagar sobre el deposito a plazo.
     *
     * @param Long tasa
     */
    public void setTasa(Long tasa) {
        this.tasa = tasa;
    }
}
