/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import com.bds.wpn.util.DESedeEncryption;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 *
 * @author cesar.mujica
 */
public class PrestamoDTO implements Serializable {

    private String numeroPrestamo;	//Numero de prestamo de 20 di­gitos.
    private String codigoTipoProducto;  //Codigo del tipo de producto (ver Tipos de producto).
    private String nombreProducto;	//Nombre del producto (ver Tipos de producto).
    private String siglasTipoMoneda;	//Siglas nemonicas del tipo de moneda.
    private BigDecimal saldoPrestamo;	//Saldo incial del prestamo.
    private Date fechaLiquidacionDate;      //Fecha en la que se liquida el contrato.
    private Date fechaVencimientoDate;      //Fecha en la que se vence el prestamo.
    private Date fechaProximoPagoDate;      //Fecha del proximo pago del prestamo.
    private Date fechaCuotaAnteriorInteresDate;
    private Date fechaPagoInteresDate;
    private Date fechaPrimerPagoInteresDate;
    private BigDecimal montoAprobado;   //Monto aprobado del prestamo.
    private BigDecimal tasa;            //Valor de la tasa del prestamo.
    private BigDecimal montoCuota;      //Monto de las coutas a pagar.
    private BigDecimal saldoActual;     //Saldo actual del prestamo.
    private List<MovimientoPrestamoDTO> movimientos;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto
    private DESedeEncryption desEnc;                //objeto encargado de encriptar la data
    private String semilla;                         //semilla para encriptar data sesnsible
    private BigDecimal cantidadCuotasPagadas;

    public PrestamoDTO(String semillaDes) {
        super();
        semilla = semillaDes;
        this.desEnc = new DESedeEncryption();
        desEnc.setSemilla(semilla);
    }

    public PrestamoDTO() {
        super();
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    /**
     * Obtiene Fecha en la que se liquida el contrato.
     *
     * @return Date fechaLiquidacion Fecha en la que se liquida el contrato.
     */
    public Date getFechaLiquidacionDate() {
        return fechaLiquidacionDate;
    }

    /**
     * Ingresa Fecha en la que se liquida el contrato.
     *
     * @param fechaLiquidacion Fecha en la que se liquida el contrato.
     */
    public void setFechaLiquidacionDate(Date fechaLiquidacion) {
        this.fechaLiquidacionDate = fechaLiquidacion;
    }

    /**
     * Obtiene Fecha en la que se vence el prestamo.
     *
     * @return Date fechaVencimiento Fecha en la que se vence el prestamo.
     */
    public Date getFechaVencimientoDate() {
        return fechaVencimientoDate;
    }

    /**
     * Ingresa Fecha en la que se vence el prestamo.
     *
     * @param fechaVencimiento Fecha en la que se vence el prestamo.
     */
    public void setFechaVencimientoDate(Date fechaVencimiento) {
        this.fechaVencimientoDate = fechaVencimiento;
    }

    /**
     * Obtiene Fecha del proximo pago del prestamo.
     *
     * @return Date fechaProximoPago Fecha del proximo pago del prestamo.
     */
    public Date getFechaProximoPagoDate() {
        return fechaProximoPagoDate;
    }

    /**
     * Ingresa Fecha del proximo pago del prestamo.
     *
     * @param fechaProximoPago Fecha del proximo pago del prestamo.
     */
    public void setFechaProximoPagoDate(Date fechaProximoPago) {
        this.fechaProximoPagoDate = fechaProximoPago;
    }

    /**
     * Obtiene Monto aprobado del prestamo.
     *
     * @return BigDecimal montoAprobado Monto aprobado del prestamo.
     */
    public BigDecimal getMontoAprobado() {
        return montoAprobado;
    }

    /**
     * Ingresa Monto aprobado del prestamo.
     *
     * @param montoAprobado Monto aprobado del prestamo.
     */
    public void setMontoAprobado(BigDecimal montoAprobado) {
        this.montoAprobado = montoAprobado;
    }

    /**
     * Obtiene Valor de la tasa del prestamo.
     *
     * @return BigDecimal Valor de la tasa del prestamo.
     */
    public BigDecimal getTasa() {
        return tasa;
    }

    /**
     * Ingresa Valor de la tasa del prestamo.
     *
     * @param tasa Valor de la tasa del prestamo.
     */
    public void setTasa(BigDecimal tasa) {
        this.tasa = tasa;
    }

    /**
     * Obtiene Monto de las coutas a pagar.
     *
     * @return BigDecimal Monto de las coutas a pagar.
     */
    public BigDecimal getMontoCuota() {
        return montoCuota;
    }

    /**
     * Ingresa Monto de las coutas a pagar.
     *
     * @param montoCuota Monto de las coutas a pagar.
     */
    public void setMontoCuota(BigDecimal montoCuota) {
        this.montoCuota = montoCuota;
    }

    /**
     * Saldo actual del prestamo.
     *
     * @return BigDecimal Saldo actual del prestamo.
     */
    public BigDecimal getSaldoActual() {
        return saldoActual;
    }

    /**
     * Ingresa Saldo actual del prestamo.
     *
     * @param saldoActual Saldo actual del prestamo.
     */
    public void setSaldoActual(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
    }

    /**
     * Retorna Numero de prestamo de 20 di­gitos.
     *
     * @return String numeroPrestamo Numero de prestamo de 20 di­gitos.
     */
    public String getNumeroPrestamo() {
        return numeroPrestamo;
    }

    /**
     * Asigna Numero de prestamo de 20 di­gitos.
     *
     * @param numeroPrestamo String
     */
    public void setNumeroPrestamo(String numeroPrestamo) {
        this.numeroPrestamo = numeroPrestamo;
    }

    /**
     * Retorna Numero de prestamo de 20 di­gitos.
     *
     * @return String numeroPrestamo Numero de prestamo de 20 di­gitos.
     * encriptado
     */
    public String getNumeroPrestamoEnc() {
        return this.desEnc.encriptar(numeroPrestamo);
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
     * @param codigoTipoProducto String
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
     * @param nombreProducto String
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
     * @param siglasTipoMoneda String
     */
    public void setSiglasTipoMoneda(String siglasTipoMoneda) {
        this.siglasTipoMoneda = siglasTipoMoneda;
    }

    /**
     * Retorna Saldo incial del prestamo.
     *
     * @return BigDecimal saldoPrestamo Saldo incial del prestamo.
     */
    public BigDecimal getSaldoPrestamo() {
        return saldoPrestamo;
    }

    /**
     * Asigna Saldo incial del prestamo.
     *
     * @param saldoPrestamo BigDecimal
     */
    public void setSaldoPrestamo(BigDecimal saldoPrestamo) {
        this.saldoPrestamo = saldoPrestamo;
    }

    /**
     * Obtener listado de movimiento
     *
     * @return List<MovimientoPrestamoDTO>
     */
    public List<MovimientoPrestamoDTO> getMovimientos() {
        return movimientos;
    }

    /**
     * Asignar listado de movimientos
     *
     * @param movimientos List<MovimientoPrestamoDTO>
     */
    public void setMovimientos(List<MovimientoPrestamoDTO> movimientos) {
        this.movimientos = movimientos;
    }

    /**
     * Obtener fecha de cuota anterior
     *
     * @return Date
     */
    public Date getFechaCuotaAnteriorInteresDate() {
        return fechaCuotaAnteriorInteresDate;
    }

    /**
     * Asignar la fecha cuota anterior interes
     *
     * @param fechaCuotaAnteriorInteresDate Date
     */
    public void setFechaCuotaAnteriorInteresDate(Date fechaCuotaAnteriorInteresDate) {
        this.fechaCuotaAnteriorInteresDate = fechaCuotaAnteriorInteresDate;
    }

    /**
     * Obtener fecha de pago ineteres
     *
     * @return Date
     */
    public Date getFechaPagoInteresDate() {
        return fechaPagoInteresDate;
    }

    /**
     * Asignar fecha de pago interes
     *
     * @param fechaPagoInteresDate Date
     */
    public void setFechaPagoInteresDate(Date fechaPagoInteresDate) {
        this.fechaPagoInteresDate = fechaPagoInteresDate;
    }

    /**
     * Obtener fecha de primer pago del interes
     *
     * @return Date
     */
    public Date getFechaPrimerPagoInteresDate() {
        return fechaPrimerPagoInteresDate;
    }

    /**
     * Asinar fecha de primer pago del interes
     *
     * @param fechaPrimerPagoInteresDate Date
     */
    public void setFechaPrimerPagoInteresDate(Date fechaPrimerPagoInteresDate) {
        this.fechaPrimerPagoInteresDate = fechaPrimerPagoInteresDate;
    }

    /**
     * retorna la semilla para encriptar la data sensible
     *
     * @return semilla para encriptar la data sensible
     */
    public String getSemilla() {
        return semilla;
    }

    /**
     * asigna la semilla para encriptar la data sensible
     *
     * @param semilla semilla para encriptar la data sensible
     */
    public void setSemilla(String semilla) {
        this.semilla = semilla;
        this.desEnc = new DESedeEncryption();
        desEnc.setSemilla(semilla);
    }

    /**
     * retorna el objeto para manejo de encriptado
     *
     * @return el objeto para manejo de encriptado
     */
    public DESedeEncryption getDesEnc() {
        return desEnc;
    }

    /**
     * asigna el objeto para manejo de encriptado
     *
     * @param desEnc el objeto para manejo de encriptado
     */
    public void setDesEnc(DESedeEncryption desEnc) {
        this.desEnc = desEnc;
    }

    public BigDecimal getCantidadCuotasPagadas() {
        return cantidadCuotasPagadas;
    }
    
    public String getCantidadCuotasPagadasString() {
        if(cantidadCuotasPagadas != null){
            return cantidadCuotasPagadas.toString();
        }else{
            return "No Disponible";
        }
    }

    public void setCantidadCuotasPagadas(BigDecimal cantidadCuotasPagadas) {
        this.cantidadCuotasPagadas = cantidadCuotasPagadas;
    }    
}
