/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;

/**
 * Clase AgenciaDTO
 * @author juan.faneite
 */
public class AgenciaDTO implements Serializable {

    private String codigoAgencia;
    private String nombreAgencia;
    private String nombreEstado;

    public AgenciaDTO() {
        //
    }

    /**
     * @return the codigoAgencia
     */
    public String getCodigoAgencia() {
        return codigoAgencia;
    }

    /**
     * @param codigoAgencia the codigoAgencia to set
     */
    public void setCodigoAgencia(String codigoAgencia) {
        this.codigoAgencia = codigoAgencia;
    }

    /**
     * @return the nombreAgencia
     */
    public String getNombreAgencia() {
        return nombreAgencia;
    }

    /**
     * @param nombreAgencia the nombreAgencia to set
     */
    public void setNombreAgencia(String nombreAgencia) {
        this.nombreAgencia = nombreAgencia;
    }

    /**
     * @return the nombreEstado
     */
    public String getNombreEstado() {
        return nombreEstado;
    }

    /**
     * @param nombreEstado the nombreEstado to set
     */
    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

}
