/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;


import java.io.Serializable;

/**
 * Maneja todo lo relacionado con las claves OTP (One Time Password).
 *
 * @author wilmer.rondon
 */
public class OtpDTO implements Serializable {

    private String ovOTP;           //Valor del OTP generado.
    private String onCodSalida;     //Codigo de salida indicando si el OTP fue generado.
    private RespuestaDTO respuestaDTO;

    /**
     * Retorna Valor del OTP generado.
     *
     * @return String ovOTP Valor del OTP generado.
     */
    public String getOvOTP() {
        return ovOTP;
    }

    /**
     * Ingresa Valor del OTP generado.
     *
     * @param String ovOTP Valor del OTP generado.
     */
    public void setOvOTP(String ovOTP) {
        this.ovOTP = ovOTP;
    }

    /**
     * Retorna Codigo de salida indicando si el OTP fue generado.
     *
     * @return String onCodSalida Codigo de salida indicando si el OTP fue
     * generado.
     */
    public String getOnCodSalida() {
        return onCodSalida;
    }

    /**
     * Ingresa Codigo de salida indicando si el OTP fue generado.
     *
     * @param String onCodSalida Codigo de salida indicando si el OTP fue
     * generado.
     */
    public void setOnCodSalida(String onCodSalida) {
        this.onCodSalida = onCodSalida;
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
     * @param respuesta RespuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuesta) {
        this.respuestaDTO = respuesta;
    }

}
