/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.util.Map;

/**
 *
 * @author renseld.lugo
 */
public class UtilDTO {

    private Map resultadosDTO;              // resultados de consulta a datos especificos.
    private RespuestaDTO respuestaDTO;  // Maneja la respuesta de la operacion que se realiza

    /**
     * Retorna mapa con resultados de la consulta de un dato en especifico.
     *
     * @return Map resultados de consulta a datos especificos.
     */
    public Map getResuladosDTO() {
        return resultadosDTO;
    }

    /**
     * Asigna Mapa con resultados de la consulta de un dato en especifico.
     *
     * @param resulados Map resultados de consulta a datos especificos.
     */
    public void setResuladosDTO(Map resulados) {
        this.resultadosDTO = resulados;
    }

    /**
     * Retorna la respuesta de la operacion que se realiza.
     *
     * @return RespuestaDTO respuesta de la operacion que se realiza.
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asigna objeto RespuestaDTO con el estatus de la transaccion.
     *
     * @param respuestaDTO objeto respuestaDTO. RespuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

}
