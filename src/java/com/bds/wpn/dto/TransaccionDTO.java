/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;

/**
 *
 * @author cesar.mujica
 */
public class TransaccionDTO implements Serializable {

    private String nroReferencia; //numero de referencia de la transaccion
    private RespuestaDTO respuestaDTO; //objeto para almacenar la respuesta de la transaccion

    /**
     * Asignar numero de referencia
     *
     * @return numero de referencia de la transaccion
     */
    public String getNroReferencia() {
        return nroReferencia;
    }

    /**
     * Asigna numero de referencia de la transaccion
     *
     * @param nroReferencia
     */
    public void setNroReferencia(String nroReferencia) {
        this.nroReferencia = nroReferencia;
    }

    /**
     * Obtener respuesta
     *
     * @return objeto para almacenar la respuesta de la transaccion
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asigna objeto para almacenar la respuesta de la transaccion
     *
     * @param respuesta
     */
    public void setRespuestaDTO(RespuestaDTO respuesta) {
        this.respuestaDTO = respuesta;
    }

}
