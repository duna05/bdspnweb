/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.util.List;

/**
 *
 * @author robinson.rodriguez
 */
public class EstadoSolicitudChequeraDTO {

    private String fechaSolicitud;
    private String tipoChequera;
    private String estadoSolicitud;
    private String agenciaRetiro;
    private List<EstadoSolicitudChequeraDTO> estadoSolicitudChequeraDTOList;

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getTipoChequera() {
        return tipoChequera;
    }

    public void setTipoChequera(String tipoChequera) {
        this.tipoChequera = tipoChequera;
    }

    public String getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(String estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }

    public String getAgenciaRetiro() {
        return agenciaRetiro;
    }

    public void setAgenciaRetiro(String agenciaRetiro) {
        this.agenciaRetiro = agenciaRetiro;
    }

    public List<EstadoSolicitudChequeraDTO> getEstadoSolicitudChequeraDTOList() {
        return estadoSolicitudChequeraDTOList;
    }

    public void setEstadoSolicitudChequeraDTOList(List<EstadoSolicitudChequeraDTO> estadoSolicitudChequeraDTOList) {
        this.estadoSolicitudChequeraDTOList = estadoSolicitudChequeraDTOList;
    }

}
