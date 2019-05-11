/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import com.bds.wpn.model.IbTextos;
import java.util.List;

/**
 *
 * @author juan.faneite
 */
public class IbtextosDTO {

    private IbTextos ibTextos;
    private List<IbTextos> ListIbTextos;
    private RespuestaDTO respuestaDTO;

    /**
     * Obtener listado de textos
     *
     * @return List<IbTextos>
     */
    public List<IbTextos> getListIbTextos() {
        return ListIbTextos;
    }

    /**
     * Asignar listado de textos
     *
     * @param ListIbTextos List<IbTextos>
     */
    public void setListIbTextos(List<IbTextos> ListIbTextos) {
        this.ListIbTextos = ListIbTextos;
    }

    /**
     * Obtener textos
     *
     * @return IbTextos
     */
    public IbTextos getIbTextos() {
        return ibTextos;
    }

    /**
     * Asignar textos
     *
     * @param ibTextos IbTextos
     */
    public void setIbTextos(IbTextos ibTextos) {
        this.ibTextos = ibTextos;
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
     * @param respuestaDTO RespuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

}
