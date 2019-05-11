/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Clase usada para almacenar datos referentes a
 * los eventos de las notificaciones, además, del estatus de la respuesta 
 * de la transacción realizada para obtener los datos almacenados en el DTO
 * @author wilmer.rondon
 */
public class IbEventosNotificacionDTO {
    
    private String id;
    private boolean checkEvento;//checkbox que son seleccionados en la vista
    private String nombreEvento;
    private String montoMin;
    private Character poseeMonto;//indica si el evento necesita tener asociado un monto mínimo
    private BigDecimal idCore;
    private List <IbEventosNotificacionDTO> ibEventosNotificacionDTO;
    private RespuestaDTO respuestaDTO;//todos los DTO deben tener este objeto

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCheckEvento() {
        return checkEvento;
    }

    public void setCheckEvento(boolean checkEvento) {
        this.checkEvento = checkEvento;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public String getMontoMin() {
        return montoMin;
    }

    public void setMontoMin(String montoMin) {
        this.montoMin = montoMin;
    }
    
    public Character getPoseeMonto() {
        return poseeMonto;
    }

    public void setPoseeMonto(Character poseeMonto) {
        this.poseeMonto = poseeMonto;
    }

    public BigDecimal getIdCore() {
        return idCore;
    }

    public void setIdCore(BigDecimal idCore) {
        this.idCore = idCore;
    }

    public List<IbEventosNotificacionDTO> getIbEventosNotificacionDTO() {
        return ibEventosNotificacionDTO;
    }

    public void setIbEventosNotificacionDTO(List<IbEventosNotificacionDTO> ibEventosNotificacionDTO) {
        this.ibEventosNotificacionDTO = ibEventosNotificacionDTO;
    }
    
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }    
}
