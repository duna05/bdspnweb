/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author alejandro.flores
 */
public class PaisDTO extends BDSUtil implements Serializable{
    
    private BigDecimal codigoPais;
    private String nombre;
    private RespuestaDTO respuesta;
    private RespuestaDTO respuestaDTO;
    private List<String> listaFPais;
    private List<PaisDTO> paisList;

    public List<PaisDTO> getPaisList() {
        return paisList;
    }

    public void setPaisList(List<PaisDTO> paisList) {
        this.paisList = paisList;
    }
        
    public List<String> getListaFPais() {
        return listaFPais;
    }

    public void setListaFPais(List<String> listaFPais) {
        this.listaFPais = listaFPais;
    }

   

    public BigDecimal getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(BigDecimal codigoPais) {
        this.codigoPais = codigoPais;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public RespuestaDTO getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaDTO respuesta) {
        this.respuesta = respuesta;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    
}
