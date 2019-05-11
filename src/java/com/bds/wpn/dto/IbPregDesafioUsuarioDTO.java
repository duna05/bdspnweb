/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.math.BigDecimal;
import java.util.Collection;

/**
 *
 * @author cesar.mujica
 */
public class IbPregDesafioUsuarioDTO {
    private BigDecimal id;
    private BigDecimal idPregunta;
    private BigDecimal idUsuario;
    private String respuestaPDD;
    private Collection<IbPreguntasDesafioDTO> preguntasDesafioUsr;
    private RespuestaDTO respuestaDTO;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(BigDecimal idPregunta) {
        this.idPregunta = idPregunta;
    }

    public BigDecimal getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(BigDecimal idUsuario) {
        this.idUsuario = idUsuario;
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

    public Collection<IbPreguntasDesafioDTO> getPreguntasDesafioUsr() {
        return preguntasDesafioUsr;
    }

    public void setPreguntasDesafioUsr(Collection<IbPreguntasDesafioDTO> preguntasDesafioUsr) {
        this.preguntasDesafioUsr = preguntasDesafioUsr;
    }
    
    
    
    
}
