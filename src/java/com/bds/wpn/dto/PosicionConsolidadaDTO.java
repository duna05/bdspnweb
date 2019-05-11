/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author cesar.mujica
 */
public class PosicionConsolidadaDTO implements Serializable {

    private List<CuentaDTO> cuentasAhorro;              //listado de cuentas de ahorro
    private List<CuentaDTO> cuentasCorriente;           //listado de cuentas corriente
    private List<CuentaDTO> cuentasME;                  //listado de cuentas en moneda extrajera
    private List<PrestamoDTO> prestamos;                //listado de prestamos
    private List<DepositoPlazoDTO> depositosPlazo;      //listado de depositos a plazo
    private List<TarjetasCreditoDTO> tarjetasCredito;   //listado de TDC
    private List<FideicomisoDTO> fideicomisos;          //listado de fideicomisos de un cliente.
    private BigDecimal totalDispCuentasAhorro;          //total de saldo diponible de cuentas de ahorro    
    private BigDecimal totalDispCuentasCorriente;       //total de saldo diponible de cuentas corriente
    private BigDecimal totalDispCuentasME;              //total de saldo diponible de cuentas en ME
    private BigDecimal totalDispPrestamos;              //total de saldo diponible de prestamos
    private BigDecimal totalDispDepositosPlazo;         //total de saldo diponible de depositos a plazo  
    private BigDecimal totalDispTDC;                    //total de saldo diponible de TDC
    private BigDecimal saldoTotalTDC;                   //Saldo Total de todas las TDC
    private RespuestaDTO respuestaDTO;                  //manejo de respuesta

    /**
     * Retorna listado de cuentas de ahorro
     *
     * @return List<CuentaDTO> cuentasAhorro listado de cuentas de ahorro
     */
    public List<CuentaDTO> getCuentasAhorro() {
        return cuentasAhorro;
    }

    /**
     * Asigna listado de cuentas de ahorro
     *
     * @param List<CuentaDTO> cuentasAhorro
     */
    public void setCuentasAhorro(List<CuentaDTO> cuentasAhorro) {
        this.cuentasAhorro = cuentasAhorro;
    }

    /**
     * Retorna listado de cuentas corriente
     *
     * @return List<CuentaDTO> cuentasCorriente listado de cuentas corriente
     */
    public List<CuentaDTO> getCuentasCorriente() {
        return cuentasCorriente;
    }

    /**
     * Asigna listado de cuentas corriente
     *
     * @param List<CuentaDTO> cuentasCorriente
     */
    public void setCuentasCorriente(List<CuentaDTO> cuentasCorriente) {
        this.cuentasCorriente = cuentasCorriente;
    }

    /**
     * Retorna listado de cuentas en moneda extrajera
     *
     * @return List<CuentaDTO> cuentasME listado de cuentas en moneda extrajera
     */
    public List<CuentaDTO> getCuentasME() {
        return cuentasME;
    }

    /**
     * Asigna listado de cuentas en moneda extrajera
     *
     * @param cuentasME List<CuentaDTO>
     */
    public void setCuentasME(List<CuentaDTO> cuentasME) {
        this.cuentasME = cuentasME;
    }

    /**
     * Retorna listado de prestamos
     *
     * @return List<PrestamoDTO> prestamos listado de prestamos
     */
    public List<PrestamoDTO> getPrestamos() {
        return prestamos;
    }

    /**
     * Asigna listado de prestamos
     *
     * @param List<PrestamoDTO> prestamos
     */
    public void setPrestamos(List<PrestamoDTO> prestamos) {
        this.prestamos = prestamos;
    }

    /**
     * Retorna listado de depositos a Plazo
     *
     * @return List<DepositoPlazoDTO> depositosPlazo listado de depositos a
     * Plazo
     */
    public List<DepositoPlazoDTO> getDepositosPlazo() {
        return depositosPlazo;
    }

    /**
     * Asigna listado de TDC
     *
     * @param List<TarjetasCreditoDTO> tarjetasCredito
     */
    public List<TarjetasCreditoDTO> getTarjetasCredito() {
        return tarjetasCredito;
    }

    /**
     * Retorna listado de TDC
     *
     * @return List<TarjetasCreditoDTO> tarjetasCredito listado de TDC
     */
    public void setTarjetasCredito(List<TarjetasCreditoDTO> tarjetasCredito) {
        this.tarjetasCredito = tarjetasCredito;
    }

    /**
     * Asigna listado de depositos a Plazo
     *
     * @param List<DepositoPlazoDTO> depositosPlazo
     */
    public void setDepositosPlazo(List<DepositoPlazoDTO> depositosPlazo) {
        this.depositosPlazo = depositosPlazo;
    }

    /**
     * Retorna total de saldo diponible de cuentas de ahorro
     *
     * @return BigDecimal totalDispCuentasAhorro total de saldo diponible de
     * cuentas de ahorro
     */
    public BigDecimal getTotalDispCuentasAhorro() {
        return totalDispCuentasAhorro;
    }

    /**
     * Asigna total de saldo diponible de cuentas de ahorro
     *
     * @param BigDecimal totalDispCuentasAhorro
     */
    public void setTotalDispCuentasAhorro(BigDecimal totalDispCuentasAhorro) {
        this.totalDispCuentasAhorro = totalDispCuentasAhorro;
    }

    /**
     * Retorna total de saldo diponible de cuentas corriente
     *
     * @return BigDecimal totalDispCuentasCorriente total de saldo diponible de
     * cuentas corriente
     */
    public BigDecimal getTotalDispCuentasCorriente() {
        return totalDispCuentasCorriente;
    }

    /**
     * Asigna total de saldo diponible de cuentas corriente
     *
     * @param BigDecimal totalDispCuentasCorriente
     */
    public void setTotalDispCuentasCorriente(BigDecimal totalDispCuentasCorriente) {
        this.totalDispCuentasCorriente = totalDispCuentasCorriente;
    }

    /**
     * Retorna total de saldo diponible de cuentas en ME
     *
     * @return BigDecimal totalDispCuentasME total de saldo diponible de cuentas
     * en ME
     */
    public BigDecimal getTotalDispCuentasME() {
        return totalDispCuentasME;
    }

    /**
     * Asigna total de saldo diponible de cuentas en ME
     *
     * @param BigDecimal totalDispCuentasME
     */
    public void setTotalDispCuentasME(BigDecimal totalDispCuentasME) {
        this.totalDispCuentasME = totalDispCuentasME;
    }

    /**
     * Retorna total de saldo diponible de prestamos
     *
     * @return BigDecimal totalDispPrestamos total de saldo diponible de
     * prestamos
     */
    public BigDecimal getTotalDispPrestamos() {
        return totalDispPrestamos;
    }

    /**
     * Asigna total de saldo diponible de prestamos
     *
     * @param BigDecimal totalDispPrestamos
     */
    public void setTotalDispPrestamos(BigDecimal totalDispPrestamos) {
        this.totalDispPrestamos = totalDispPrestamos;
    }

    /**
     * Retorna total de saldo diponible de depositos a plazo
     *
     * @return BigDecimal totalDispDepositosPlazo total de saldo diponible de
     * depositos a plazo
     */
    public BigDecimal getTotalDispDepositosPlazo() {
        return totalDispDepositosPlazo;
    }

    /**
     * Asigna total de saldo diponible de depositos a plazo
     *
     * @param BigDecimal totalDispDepositosPlazo
     */
    public void setTotalDispDepositosPlazo(BigDecimal totalDispDepositosPlazo) {
        this.totalDispDepositosPlazo = totalDispDepositosPlazo;
    }

    /**
     * Retorna total de saldo diponible de TDC
     *
     * @return BigDecimal totalDispTDC total de saldo diponible de TDC
     */
    public BigDecimal getTotalDispTDC() {
        return totalDispTDC;
    }

    /**
     * Asigna total de saldo diponible de TDC
     *
     * @param totalDispTDC
     */
    public void setTotalDispTDC(BigDecimal totalDispTDC) {
        this.totalDispTDC = totalDispTDC;
    }

    public BigDecimal getSaldoTotalTDC() {
        return saldoTotalTDC;
    }

    public void setSaldoTotalTDC(BigDecimal saldoTotalTDC) {
        this.saldoTotalTDC = saldoTotalTDC;
    }
    
    /**
     * Obtener respuesta
     *
     * @return RespuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asignar respuesta
     *
     * @param respuesta
     */
    public void setRespuestaDTO(RespuestaDTO respuesta) {
        this.respuestaDTO = respuesta;
    }

    public List<FideicomisoDTO> getFideicomisos() {
        return fideicomisos;
    }

    public void setFideicomisos(List<FideicomisoDTO> fideicomisos) {
        this.fideicomisos = fideicomisos;
    }

    
}
