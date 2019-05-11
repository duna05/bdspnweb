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
public class EMailDTO implements Serializable {

    private String remitente;       //Remitente del correo.
    private String destinatario;    //Destinatario del correo.
    private String asunto;          //Asunto del correo.
    private String texto;           //Texto del correo.
    private String canal;           //Codigo (extendido) del canal desde el cual es llamado el procedimiento.
    RespuestaDTO respuestaDTO;         //manejo de respuesta

    /**
     * Retorna Remitente del correo.
     *
     * @return String Remitente del correo.
     */
    public String getRemitente() {
        return remitente;
    }

    /**
     * Asigna Remitente del correo.
     *
     * @param String Remitente del correo.
     */
    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    /**
     * Retorna Destinatario del correo.
     *
     * @return String Destinatario del correo.
     */
    public String getDestinatario() {
        return destinatario;
    }

    /**
     * Asigna Destinatario del correo.
     *
     * @param String Destinatario del correo.
     */
    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    /**
     * Retorna Asunto del correo.
     *
     * @return String Asunto del correo.
     */
    public String getAsunto() {
        return asunto;
    }

    /**
     * signa Asunto del correo.
     *
     * @param String Asunto del correo.
     */
    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    /**
     * retorna Texto del correo.
     *
     * @return String Texto del correo.
     */
    public String getTexto() {
        return texto;
    }

    /**
     * asigna Texto del correo.
     *
     * @param String Texto del correo.
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     * retorna Codigo (extendido) del canal desde el cual es llamado el
     * procedimiento.
     *
     * @return String Codigo (extendido) del canal desde el cual es llamado el
     * procedimiento.
     */
    public String getCanal() {
        return canal;
    }

    /**
     * Asigna Codigo (extendido) del canal desde el cual es llamado el
     * procedimiento.
     *
     * @param String Codigo (extendido) del canal desde el cual es llamado el
     * procedimiento.
     */
    public void setCanal(String canal) {
        this.canal = canal;
    }

    /**
     * Retorna el objeto de respuesta
     *
     * @return RespuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asigna el objeto respueta
     *
     * @param respuesta RespuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuesta) {
        this.respuestaDTO = respuesta;
    }

}
