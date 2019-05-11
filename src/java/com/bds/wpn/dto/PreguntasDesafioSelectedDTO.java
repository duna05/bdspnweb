/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author juan.faneite
 */
public class PreguntasDesafioSelectedDTO implements Serializable{
    private List<IbPreguntasDesafioDTO> listPreguntaDD;
    private BigDecimal preguntaDD;
    private String respuestaPDD;
    private RespuestaDTO respuestaDTO;

    public List<IbPreguntasDesafioDTO> getListPreguntaDD() {
        return listPreguntaDD;
    }

    public void setListPreguntaDD(List<IbPreguntasDesafioDTO> listPreguntaDD) {
        this.listPreguntaDD = listPreguntaDD;
    }

    public BigDecimal getPreguntaDD() {
        return preguntaDD;
    }

    public void setPreguntaDD(BigDecimal preguntaDD) {
        this.preguntaDD = preguntaDD;
    }    

    public String getRespuestaPDD() {
        return respuestaPDD;
    }

    public void setRespuestaPDD(String respuestaPDD) {
        this.respuestaPDD = respuestaPDD;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }
    
    
}
