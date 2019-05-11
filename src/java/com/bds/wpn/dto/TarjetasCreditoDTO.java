/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import com.bds.wpn.util.DESedeEncryption;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author cesar.mujica
 */
public class TarjetasCreditoDTO implements Serializable {

    private String numeroTarjeta;	//Numero de Tarjeta de 20 digitos.
    private String codigoTipoProducto;	//Codigo del tipo de producto (ver Tipos de producto).
    private String nombreProducto;	//Nombre del producto (ver Tipos de producto).
    private String siglasTipoMoneda;	//Siglas nemonicas del tipo de moneda.
    private BigDecimal saldoDisponible;	//Saldo disponible de la cuenta (saldo total en linea).
    private BigDecimal saldoDiferido;	//Monto de los valores que se ecuentran en reserva y son afectados durante el dia antes del proceso de fin de dia.
    private BigDecimal saldoBloqueado;	//Saldo embargado (bloqueado) en linea de la cuenta.
    private BigDecimal saldoTotal;	//Saldo total de la cuenta.

    private String codigoTdc;         // codigo de 8 digitos de la TDC.
    private Date fechaClienteDate;      // Fecha desde la cual el cliente es usuario (Visa/Mater).
    private Date fechaExpiracionDate;   // fecha de venciminto de la tarjeta.
    private String codigoValidacion;  // Ultimos tres digitos de la parte tresera de la TDC.
    private String nombreCliente;     // Nombre del usuario de la TDC.
    private String estatus;           // Estatus de la tdc.
    private Collection<MovimientoTDCDTO> movimientosDTO; //lista de movimientos de tarjetas    

    //Detalle Encabezado TDC    
    private BigDecimal saldoAlCorte;	//Saldo al corte de la TDC.
    private BigDecimal montoDisponible;	//Monto disponible.
    private BigDecimal pagoMinimo; //Pago minimo de la TDC.
    private BigDecimal limiteCredito;//límite de crédito
    private Date fechaLimiteDate; 	//Fecha limite de pago    
    private Date fechaCorteDate; //Fecha de corte.

    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto
    private DESedeEncryption desEnc;                //objeto encargado de encriptar la data
    private String semilla;                         //semilla para encriptar data sesnsible
    
    public TarjetasCreditoDTO (String semillaDes){
        super();
        semilla = semillaDes;
        this.desEnc = new DESedeEncryption();
        desEnc.setSemilla(semilla);
    }
    
    public TarjetasCreditoDTO (){
        super();
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    /**
     * Retorna Numero de cuenta de 20 digitos.
     *
     * @return String
     */
    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    /**
     * Asigna Numero de cuenta de 20 digitos.
     *
     * @param numeroTarjeta String
     */
    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }
    
    /**
     * Retorna Numero de cuenta de 20 digitos encriptado
     *
     * @return String
     */
    public String getNumeroTarjetaEnc() {
        return this.desEnc.encriptar(numeroTarjeta);
    }

    /**
     * Retorna Codigo del tipo de producto (ver Tipos de producto).
     *
     * @return String
     */
    public String getCodigoTipoProducto() {
        return codigoTipoProducto;
    }

    /**
     * signa Codigo del tipo de producto (ver Tipos de producto).
     *
     * @param codigoTipoProducto String
     */
    public void setCodigoTipoProducto(String codigoTipoProducto) {
        this.codigoTipoProducto = codigoTipoProducto;
    }

    /**
     * Retorna Nombre del producto (ver Tipos de producto).
     *
     * @return String
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
     * @return String
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
     * @param saldoDisponible BigDecimal
     */
    public void setSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    /**
     * Retorna Monto de los valores que se ecuentran en reserva y son afectados
     * durante el dia antes del proceso de fin de dia.
     *
     * @return BigDecimal saldoDiferido Monto de los valores que se ecuentran en
     * reserva y son afectados durante el dia antes del proceso de fin de dia.
     */
    public BigDecimal getSaldoDiferido() {
        return saldoDiferido;
    }

    /**
     * Asigna Monto de los valores que se ecuentran en reserva y son afectados
     * durante el dia antes del proceso de fin de dia.
     *
     * @param saldoDiferido BigDecimal
     */
    public void setSaldoDiferido(BigDecimal saldoDiferido) {
        this.saldoDiferido = saldoDiferido;
    }

    /**
     * Retorna Saldo embargado (bloqueado) en linea de la cuenta.
     *
     * @return BigDecimal saldoBloqueado Saldo embargado (bloqueado) en linea de
     * la cuenta.
     */
    public BigDecimal getSaldoBloqueado() {
        return saldoBloqueado;
    }

    /**
     * Asigna Saldo embargado (bloqueado) en linea de la cuenta.
     *
     * @param saldoBloqueado BigDecimal
     */
    public void setSaldoBloqueado(BigDecimal saldoBloqueado) {
        this.saldoBloqueado = saldoBloqueado;
    }

    /**
     * Retorna Saldo total de la cuenta.
     *
     * @return BigDecimal saldoTotal Saldo total de la cuenta.
     */
    public BigDecimal getSaldoTotal() {
        return saldoTotal;
    }

    /**
     * Asigna Saldo total de la cuenta.
     *
     * @param saldoTotal BigDecimal
     */
    public void setSaldoTotal(BigDecimal saldoTotal) {
        this.saldoTotal = saldoTotal;
    }

    /**
     * Obtiene el identificdor unico del codigo de 8 digitos de la TDC.
     *
     * @return String identificdor unico del codigo de 8 digitos de la TDC.
     */
    public String getCodigoTdc() {
        return codigoTdc;
    }

    /**
     * Ingresa Identificdor unico del codigo de 8 digitos de la TDC.
     *
     * @param codigoTdc codigo de 8 digitos de la TDC.
     */
    public void setCodigoTdc(String codigoTdc) {
        this.codigoTdc = codigoTdc;
    }

    /**
     * Obtiene la fecha desde la cual el cliente es usuario (Visa/Mater).
     *
     * @return Date fecha desde la cual el cliente es usuario (Visa/Mater).
     */
    public Date getFechaClienteDate() {
        return fechaClienteDate;
    }

    /**
     * Ingresa la fecha desde la cual el cliente es usuario (Visa/Mater).
     *
     * @param fechaCliente Fecha desde la cual el cliente es usuario
     * (Visa/Mater).
     */
    public void setFechaClienteDate(Date fechaCliente) {
        this.fechaClienteDate = fechaCliente;
    }

    /**
     * Obtiene la fecha de venciminto de la tarjeta.
     *
     * @return Date fecha de venciminto de la tarjeta.
     */
    public Date getFechaExpiracionDate() {
        return fechaExpiracionDate;
    }

    /**
     * Ingresa la fecha de venciminto de la tarjeta.
     *
     * @param fechaExpiracion fecha de venciminto de la tarjeta.
     */
    public void setFechaExpiracionDate(Date fechaExpiracion) {
        this.fechaExpiracionDate = fechaExpiracion;
    }

    /**
     * Obtiene los Ultimos tres digitos de la parte tresera de la TDC.
     *
     * @return String Ultimos tres digitos de la parte tresera de la TDC.
     */
    public String getCodigoValidacion() {
        return codigoValidacion;
    }

    /**
     * Ingresa los ultimos tres digitos de la parte tresera de la TDC.
     *
     * @param codigoValidacion Ultimos tres digitos de la parte tresera de la
     * TDC.
     */
    public void setCodigoValidacion(String codigoValidacion) {
        this.codigoValidacion = codigoValidacion;
    }

    /**
     * Obtiene el nombre del usuario de la TDC.
     *
     * @return String nombre del usuario de la TDC.
     */
    public String getNombreCliente() {
        return nombreCliente;
    }

    /**
     * Ingresa el nombre del usuario de la TDC.
     *
     * @param nombreCliente Nombre del usuario de la TDC.
     */
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    /**
     * Obtiene el estatus de la tdc.
     *
     * @return String Estatus de la tdc.
     */
    public String isEstatus() {
        return estatus;
    }

    /**
     * Ingresa el estaus de la tdc.
     *
     * @param estatus Estaus de la tdc.
     */
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    /**
     * Lista de movimientos de tarjetas
     *
     * @return lista de movimientos de tarjetas
     */
    public Collection<MovimientoTDCDTO> getMovimientosDTO() {
        return movimientosDTO;
    }

    /**
     * Lista de movimientos de tarjetas
     *
     * @param movimientos lista de movimientos de tarjetas
     */
    public void setMovimientosDTO(Collection<MovimientoTDCDTO> movimientos) {
        this.movimientosDTO = movimientos;
    }
    
    /**
     * Obtener saldo al corte
     *
     * @return BigDecimal
     */
    public BigDecimal getSaldoAlCorte() {
        return saldoAlCorte;
    }

    /**
     * Asignar saldo al corte
     *
     * @param saldoAlCorte BigDecimal
     */
    public void setSaldoAlCorte(BigDecimal saldoAlCorte) {
        this.saldoAlCorte = saldoAlCorte;
    }

    /**
     * Obtener monto disponible
     *
     * @return BigDecimal
     */
    public BigDecimal getMontoDisponible() {
        return montoDisponible;
    }

    /**
     * Asignar monto disponible
     *
     * @param montoDisponible BigDecimal
     */
    public void setMontoDisponible(BigDecimal montoDisponible) {
        this.montoDisponible = montoDisponible;
    }

    /**
     * Obtener pago minimo
     *
     * @return BigDecimal
     */
    public BigDecimal getPagoMinimo() {
        return pagoMinimo;
    }

    /**
     * Asignar pago minimo
     *
     * @param pagoMinimo BigDecimal
     */
    public void setPagoMinimo(BigDecimal pagoMinimo) {
        this.pagoMinimo = pagoMinimo;
    }

    /**
     * Obtener fecha limite
     *
     * @return Date
     */
    public Date getFechaLimiteDate() {
        return fechaLimiteDate;
    }

    /**
     * Asignar fecha limite
     *
     * @param fechaLimiteDate Date
     */
    public void setFechaLimiteDate(Date fechaLimiteDate) {
        this.fechaLimiteDate = fechaLimiteDate;
    }

    /**
     * Obtener fecha de corte
     *
     * @return Date
     */
    public Date getFechaCorteDate() {
        return fechaCorteDate;
    }

    /**
     * Asignar fecha de corte
     *
     * @param fechaCorteDate Date
     */
    public void setFechaCorteDate(Date fechaCorteDate) {
        this.fechaCorteDate = fechaCorteDate;
    }
    
    /**
     * retorna la semilla para encriptar la data sensible
     * @return semilla para encriptar la data sensible
     */
    public String getSemilla() {
        return semilla;
    }

    /**
     * asigna la semilla para encriptar la data sensible
     * @param semilla semilla para encriptar la data sensible
     */
    public void setSemilla(String semilla) {
        this.semilla = semilla;
        this.desEnc = new DESedeEncryption();
        desEnc.setSemilla(semilla);
    }

    /**
     * retorna el objeto para manejo de encriptado
     * @return el objeto para manejo de encriptado
     */
    public DESedeEncryption getDesEnc() {
        return desEnc;
    }

    /**
     * asigna el objeto para manejo de encriptado
     * @param desEnc el objeto para manejo de encriptado
     */
    public void setDesEnc(DESedeEncryption desEnc) {
        this.desEnc = desEnc;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }    
}
