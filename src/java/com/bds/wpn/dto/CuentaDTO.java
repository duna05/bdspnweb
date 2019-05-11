/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import static com.bds.wpn.util.BDSUtil.formatearMonto;
import static com.bds.wpn.util.BDSUtil.formatoAsteriscosWeb;
import com.bds.wpn.util.DESedeEncryption;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

/**
 *
 * @author cesar.mujica
 */
public class CuentaDTO implements Serializable{

    private String numeroCuenta;                    //Numero de cuenta de 20 digitos.
    private String codigoTipoProducto;              //Codigo del tipo de producto (ver Tipos de producto).
    private String nombreProducto;                  //Nombre del producto (ver Tipos de producto).
    private String siglasTipoMoneda;                //Siglas nemonicas del tipo de moneda.
    private BigDecimal saldoDisponible;             //Saldo disponible de la cuenta (saldo total en linea).
    private BigDecimal saldoDiferido;               //Monto de los valores que se ecuentran en reserva y son afectados durante el dia antes del proceso de fin de dia.
    private BigDecimal saldoBloqueado;              //Saldo embargado (bloqueado) en linea de la cuenta.
    private BigDecimal saldoTotal;                  //Saldo total de la cuenta.
    private String codAgencia;                      //codigo de la agencia donde se aperturo la cuenta
    private Collection<MovimientoCuentaDTO> movimientosDTO;  //movimientos parauna cuenta    
    private Collection<MovimientoCuentaDTO> listSaldoDiferido;  //collection de movimientos de saldo diferido de una cuenta
    private Collection<MovimientoCuentaDTO> listSaldoBloqueado;  //collection de movimientos de saldo bloqueado de una cuenta
    private Collection<ChequeraDTO> chequerasDTO;       //collection de chequeras asociadas a la cta corriente
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto
    private DESedeEncryption desEnc;                //objeto encargado de encriptar la data
    private String semilla;                         //semilla para encriptar data sesnsible
    private CabeceraEstadoCtaDTO cabecera;  
    
    public CuentaDTO (String semillaDes){
        super();
        semilla = semillaDes;
        this.desEnc = new DESedeEncryption();
        desEnc.setSemilla(semilla);
    }
    
    public CuentaDTO (){
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
     * @return String numeroCuenta Numero de cuenta de 20 digitos.
     */
    public String getNumeroCuenta() {        
        return numeroCuenta;
    }
    
    /**
     * Retorna Numero de cuenta encriptado de 20 digitos.
     *
     * @return String numeroCuenta encriptado Numero de cuenta de 20 digitos.
     */
    public String getNumeroCuentaEnc() {
        return this.desEnc.encriptar(numeroCuenta);
    }

    /**
     * Asigna Numero de cuenta de 20 digitos.
     *
     * @param numeroCuenta
     */
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
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
     * Retorna Codigo del tipo de producto (ver Tipos de producto). encriptado
     *
     * @return String codigoTipoProducto Codigo del tipo de producto (ver Tipos
     * de producto).
     */
    public String getCodigoTipoProductoEnc() {
        return desEnc.encriptar(codigoTipoProducto);
    }

    /**
     * Asigna Codigo del tipo de producto (ver Tipos de producto).
     *
     * @param codigoTipoProducto
     */
    public void setCodigoTipoProducto(String codigoTipoProducto) {
        this.codigoTipoProducto = codigoTipoProducto;
    }

    /**
     * Retorna Nombre del producto (ver Tipos de producto).
     *
     * @return String Nombre del producto (ver Tipos de producto).
     */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /**
     * Asigna Nombre del producto (ver Tipos de producto).
     *
     * @param nombreProducto
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
     * @param siglasTipoMoneda
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
     * @param saldoDisponible
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
     * @param saldoDiferido
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
     * @param saldoBloqueado
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
     * @param saldoTotal
     */
    public void setSaldoTotal(BigDecimal saldoTotal) {
        this.saldoTotal = saldoTotal;
    }

    /**
     * Retorna movimientos de cuenta
     *
     * @return List<MovimientoCuentaDTO> movimientos movimientos de cuenta
     */
    public Collection<MovimientoCuentaDTO> getMovimientosDTO() {
        return movimientosDTO;
    }

    /**
     * Asigna movimientos de cuenta
     *
     * @param movimientos
     */
    public void setMovimientosDTO(Collection<MovimientoCuentaDTO> movimientos) {
        this.movimientosDTO = movimientos;
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

    public Collection<MovimientoCuentaDTO> getListSaldoDiferido() {
        return listSaldoDiferido;
    }

    public void setListSaldoDiferido(Collection<MovimientoCuentaDTO> listSaldoDiferido) {
        this.listSaldoDiferido = listSaldoDiferido;
    }

    public Collection<MovimientoCuentaDTO> getListSaldoBloqueado() {
        return listSaldoBloqueado;
    }

    public void setListSaldoBloqueado(Collection<MovimientoCuentaDTO> listSaldoBloqueado) {
        this.listSaldoBloqueado = listSaldoBloqueado;
    }

    public String getCodAgencia() {
        return codAgencia;
    }

    public void setCodAgencia(String codAgencia) {
        this.codAgencia = codAgencia;
    }

    public Collection<ChequeraDTO> getChequerasDTO() {
        return chequerasDTO;
    }

    public void setChequerasDTO(Collection<ChequeraDTO> chequerasDTO) {
        this.chequerasDTO = chequerasDTO;
    }

    public CabeceraEstadoCtaDTO getCabecera() {
        return cabecera;
    }

    public void setCabecera(CabeceraEstadoCtaDTO cabecera) {
        this.cabecera = cabecera;
    }
    
    public String getDetalleProdLista(){
        StringBuilder detalle = new StringBuilder();
        //detalle.append(completarEspacios(capitalizarTexto(this.getNombreProducto()),50));
        detalle.append(this.getCodigoTipoProducto());
        detalle.append(" - ");
        detalle.append(formatoAsteriscosWeb(getNumeroCuenta()));
        detalle.append(" - ");
        detalle.append(" Bs. "); 
        detalle.append(formatearMonto(this.getSaldoDisponible()));
        return detalle.toString();
    }
    
    
}
