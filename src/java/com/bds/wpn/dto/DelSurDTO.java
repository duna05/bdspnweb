/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Clase DelSurDTO
 * @author juan.faneite
 */
public class DelSurDTO implements Serializable {

    private List<AgenciaDTO> agencias;
    private List<BancoDTO> bancos;
    private RespuestaDTO respuestaDTO;                 //manejo de respuestas

    public DelSurDTO() {
        //
    }

    /**
     * obtiene listado de bancos
     * @return  listado de bancos
     */
    public List<BancoDTO> getBancos() {
        return bancos;
    }

    /**
     * inserta listado de bancos
     * @param bancos listado de bancos
     */
    public void setBancos(List<BancoDTO> bancos) {
        this.bancos = bancos;
    }
    
    /**
     * @return the agencias
     */
    public List<AgenciaDTO> getAgencias() {
        return agencias;
    }

    /**
     * @param agencias the agencias to set
     */
    public void setAgencias(List<AgenciaDTO> agencias) {
        this.agencias = agencias;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }


}
