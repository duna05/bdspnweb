/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author juan.faneite
 */
public class IbMensajesDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private Date fechaHoraDate;
    private String texto;
    private Collection<IbMensajesDTO> mensajes;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto

    /**
     * Obtener respuesta
     * @return RespuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asignar respuesta 
     * @param respuestaDTO RespuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    /**
     * Asinar id
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Asignar id
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Obtener fecha hora
     * @return Date
     */
    public Date getFechaHoraDate() {
        return fechaHoraDate;
    }

    /**
     * Asignar fecha hora
     * @param fechaHoraDate Date
     */
    public void setFechaHoraDate(Date fechaHoraDate) {
        this.fechaHoraDate = fechaHoraDate;
    }

    /**
     * Obtener texto
     * @return String
     */
    public String getTexto() {
        return texto;
    }

    /**
     * Asinar texto
     * @param texto String
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     * Obtener listado de mensajes de usuarios
     * @return mensajes
     */
    public Collection<IbMensajesDTO> getMensajes() {
        return mensajes;
    }

    /**
     * Asignar listado de mensajes de usuarios
     * @param mensajes Collection<IbMensajesDTO>
     */
    public void setMensajes(Collection<IbMensajesDTO> mensajes) {
        this.mensajes = mensajes;
    }

}
