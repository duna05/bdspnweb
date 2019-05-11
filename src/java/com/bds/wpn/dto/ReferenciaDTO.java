/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Clase ReferenciaDTO
 * @author rony.rodriguez
 */
public class ReferenciaDTO implements Serializable {
       
    private List<ReferenciaDetalleDTO> referenciasCuentas;
    /*
     *FALTA AGREGAR LAS REFERENCIAS PARA LAS TDC PERO NO SE TIENE EL CURSOR 
     */
    private RespuestaDTO respuestaDTO;

    /**
     * 
     * @return listado de referencias de cuentas
     */
    public List<ReferenciaDetalleDTO> getReferenciasCuentas() {
        return referenciasCuentas;
    }

    /**
     * 
     * @param referenciasCuentas listado de referencias de cuentas
     */
    public void setReferenciasCuentas(List<ReferenciaDetalleDTO> referenciasCuentas) {
        this.referenciasCuentas = referenciasCuentas;
    }    

    /**
     * @return the respuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * @param respuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

}
