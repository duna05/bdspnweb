/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author juan.faneite
 */
public class FlujoExternaUsuarioDTO implements Serializable{
    private String tdd;
    private String pin;
    private String pinEnc;
    private Date fechaVencimiento;
    private String tipoIdentificacion;
    private String identificacion;
    private String contrato;
    private boolean checkContrato;
    private String clave;
    private String claveEnc;
    private String claveConfirm;
    private String claveConfirmEnc;
    private String periodoClave;
    private String numeroCuenta;
    private String claveOP;
    private String confirmacionClaveOP;

    public FlujoExternaUsuarioDTO() {
        this.checkContrato = false;
    }

    public String getClaveConfirmEnc() {
        return claveConfirmEnc;
    }

    public void setClaveConfirmEnc(String claveConfirmEnc) {
        this.claveConfirmEnc = claveConfirmEnc;
    }
    
    
    /**
     * 
     * @return obtiene el numero de cuenta
     */
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    /**
     * 
     * @param numeroCuenta inserta el numero de cuenta
     */
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }
    
    /**
     * 
     * @return obtiene la clave del usuario
     */
    public String getClave() {
        return clave;
    }

    /**
     * 
     * @param clave inserta la clave del usuario
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * retorna la clave encriptada del usuario
     * @return 
     */
    public String getClaveEnc() {
        return claveEnc;
    }

    /**
     * asigna la clave encriptada del usuario
     * @param claveEnc 
     */
    public void setClaveEnc(String claveEnc) {
        this.claveEnc = claveEnc;
    }
    
    /**
     * 
     * @return obtiene la confirmacion de clave del usuario
     */
    public String getClaveConfirm() {
        return claveConfirm;
    }

    /**
     * 
     * @param claveConfirm inserta la confirmacion de clave del usuario
     */
    public void setClaveConfirm(String claveConfirm) {
        this.claveConfirm = claveConfirm;
    }

    /**
     * 
     * @return obtiene el periodo de vencimiento de la clave
     */
    public String getPeriodoClave() {
        return periodoClave;
    }

    /**
     * 
     * @param periodoClave inserta el periodo de vencimiento de la clave
     */
    public void setPeriodoClave(String periodoClave) {
        this.periodoClave = periodoClave;
    }
        
    /**
     * 
     * @return obtiene el contrato que el cliente debe aceptar para poder ingresar a la nueva banca en linea
     */
    public String getContrato() {
        return contrato;
    }

    /**
     * 
     * @param contrato inserta el contrato que el cliente debe aceptar para poder ingresar a la nueva banca en linea
     */
    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    /**
     * 
     * @return obtiene check para el contrato true en caso de aceptar, false en caso contrario
     */
    public boolean isCheckContrato() {
        return checkContrato;
    }

    /**
     * 
     * @param checkContrato insertar check para el contrato true en caso de aceptar, false en caso contrario
     */
    public void setCheckContrato(boolean checkContrato) {
        this.checkContrato = checkContrato;
    }    

    /**
     * 
     * @return obtiene el numero de tarjeta de debito
     */
    public String getTdd() {
        return tdd;
    }

    /**
     * 
     * @param tdd inserta el numero de tarjeta de debito
     */
    public void setTdd(String tdd) {
        this.tdd = tdd;
    }

    /**
     * 
     * @return obtiene el pin de la tarjeta de debito
     */
    public String getPin() {
        return pin;
    }

    /**
     * 
     * @param pin inserta el pin de la tarjeta de debito
     */
    public void setPin(String pin) {
        this.pin = pin;
    }

    /**
     * 
     * @return el pin de la tarjeta de debito encriptado
     */
    public String getPinEnc() {
        return pinEnc;
    }

    /**
     * 
     * @param pinEnc inserta el pin de la tarjeta de debito encriptado
     */
    public void setPinEnc(String pinEnc) {
        this.pinEnc = pinEnc;
    }    

    /**
     * 
     * @return obtiene la fecha de vencimiento de la tarjeta de debito
     */
    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * 
     * @param fechaVencimiento inserta la fecha de vencimiento de la tarjeta de debito
     */
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * 
     * @return obtiene el tipo de identificacion (V - E - J - P) del cliente
     */
    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    /**
     * 
     * @param tipoIdentificacion inserta el tipo de identificacion (V - E - J - P) del cliente
     */
    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    /**
     * 
     * @return obtiene el numero de identificacion del cliente
     */
    public String getIdentificacion() {
        return identificacion;
    }

    /**
     * 
     * @param identificacion inserta el numero de identificacion del cliente
     */
    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getClaveOP() {
        return claveOP;
    }

    public void setClaveOP(String claveOP) {
        this.claveOP = claveOP;
    }

    public String getConfirmacionClaveOP() {
        return confirmacionClaveOP;
    }

    public void setConfirmacionClaveOP(String confirmacionClaveOP) {
        this.confirmacionClaveOP = confirmacionClaveOP;
    }

}
