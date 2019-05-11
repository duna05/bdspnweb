/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;

/**
 *
 * @author rony.rodriguez
 */
public class IbBitacoraDTO implements Serializable {

    private String cuentaOrigen = "";
    private String cuentaDestino = "";
    private String monto = "";
    private String referencia = "";
    private String descripcion = "";
    private String ip = "";
    private String userAgent = "";
    private String errorOperacion = "";
    private String nombreBeneficiario = "";
    private String tipoRif = "";
    private String rifCedula = "";
    private String fechaHoraTx = "";
    private String fechaHoraJob = "";
    private String idCanal = "";
    private String nombreCanal = "";
    private String idUsuario = "";
    private String idTransaccion = "";
    private String idAfiliacion = "";

    /**
     * Obtener la cuenta de origen
     *
     * @return String
     */
    public String getCuentaOrigen() {
        return cuentaOrigen;
    }

    /**
     * Asinar la cuenta de origen
     *
     * @param cuentaOrigen String
     */
    public void setCuentaOrigen(String cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }

    /**
     * Obtener la cuenta de destino
     *
     * @return String
     */
    public String getCuentaDestino() {
        return cuentaDestino;
    }

    /**
     * Asignar la cuenta de destino
     *
     * @param cuentaDestino String
     */
    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    /**
     * Obtener el monto
     *
     * @return String
     */
    public String getMonto() {
        return monto;
    }

    /**
     * Asignar el monto
     *
     * @param monto String
     */
    public void setMonto(String monto) {
        this.monto = monto;
    }

    /**
     * Obtener la referencia
     *
     * @return String
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * Asignar la referencia
     *
     * @param referencia String
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    /**
     * Obtener la descripcion
     *
     * @return String
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Asignar la descripcion
     *
     * @param descripcion String
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtener el ip
     *
     * @return String
     */
    public String getIp() {
        return ip;
    }

    /**
     * Asignar el ip
     *
     * @param ip String
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Obtener el agente
     *
     * @return String
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Asignar el agente
     *
     * @param userAgent String
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Obtener el error de la operacion
     *
     * @return String
     */
    public String getErrorOperacion() {
        return errorOperacion;
    }

    /**
     * Asiganr el error de la operacion
     *
     * @param errorOperacion String
     */
    public void setErrorOperacion(String errorOperacion) {
        this.errorOperacion = errorOperacion;
    }

    /**
     * Obtener nombre del beneficiario
     *
     * @return the nombreBeneficiario
     */
    public String getNombreBeneficiario() {
        return nombreBeneficiario;
    }

    /**
     * Asignar nombre del beneficiario
     *
     * @param nombreBeneficiario String
     */
    public void setNombreBeneficiario(String nombreBeneficiario) {
        this.nombreBeneficiario = nombreBeneficiario;
    }

    /**
     * Obetener tipo rif
     *
     * @return String
     */
    public String getTipoRif() {
        return tipoRif;
    }

    /**
     * Asignar el tipo de rif
     *
     * @param tipoRif String
     */
    public void setTipoRif(String tipoRif) {
        this.tipoRif = tipoRif;
    }

    /**
     * Obtener rif o cedular
     *
     * @return String
     */
    public String getRifCedula() {
        return rifCedula;
    }

    /**
     * Asignar rif o cedula
     *
     * @param rifCedula String
     */
    public void setRifCedula(String rifCedula) {
        this.rifCedula = rifCedula;
    }

    /**
     * Obtener fecha hora tx
     *
     * @return String
     */
    public String getFechaHoraTx() {
        return fechaHoraTx;
    }

    /**
     * Asignar fecha hora tx
     *
     * @param fechaHoraTx String
     */
    public void setFechaHoraTx(String fechaHoraTx) {
        this.fechaHoraTx = fechaHoraTx;
    }

    /**
     * @return the fechaHoraJob
     */
    public String getFechaHoraJob() {
        return fechaHoraJob;
    }

    /**
     * @param fechaHoraJob the fechaHoraJob to set
     */
    public void setFechaHoraJob(String fechaHoraJob) {
        this.fechaHoraJob = fechaHoraJob;
    }

    /**
     * @return the idUsuario
     */
    public String getIdUsuario() {
        return idUsuario;
    }

    /**
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * @return the idTransaccion
     */
    public String getIdTransaccion() {
        return idTransaccion;
    }

    /**
     * @param idTransaccion the idTransaccion to set
     */
    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    /**
     * @return the idAfiliacion
     */
    public String getIdAfiliacion() {
        return idAfiliacion;
    }

    /**
     * @param idAfiliacion the idAfiliacion to set
     */
    public void setIdAfiliacion(String idAfiliacion) {
        this.idAfiliacion = idAfiliacion;
    }

    /**
     * @return the idCanal
     */
    public String getIdCanal() {
        return idCanal;
    }

    /**
     * @param idCanal the idCanal to set
     */
    public void setIdCanal(String idCanal) {
        this.idCanal = idCanal;
    }

    /**
     * @return the nombreCanal
     */
    public String getNombreCanal() {
        return nombreCanal;
    }

    /**
     * @param nombreCanal the nombreCanal to set
     */
    public void setNombreCanal(String nombreCanal) {
        this.nombreCanal = nombreCanal;
    }

}
