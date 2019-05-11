/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.util.Collection;

/**
 * Clase PreguntaAutenticacionDTO
 * @author juan.faneite
 */
public class PreguntaAutenticacionDTO implements Serializable{
    private String nroOrdenamiento;
    private String codigoPregunta;
    private String descripcion;
    private String codigoTipoPregunta;
    private String tipoDato;
    private String longitudDato;
    
    private Collection<PreguntaAutenticacionDTO> preguntasAutenticacion;
    
    private RespuestaDTO respuestaDTO;

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuesta) {
        this.respuestaDTO = respuesta;
    }
    
    /**
     * 
     * @return obtiene el listado de preguntas
     */
    public Collection<PreguntaAutenticacionDTO> getPreguntasAutenticacion() {
        return preguntasAutenticacion;
    }

    /**
     * 
     * @param preguntasAutenticacion inserta el listado de preguntas
     */
    public void setPreguntasAutenticacion(Collection<PreguntaAutenticacionDTO> preguntasAutenticacion) {
        this.preguntasAutenticacion = preguntasAutenticacion;
    }
    
    /**
     * 
     * @return obtiene el numero de ordenamiento de las preguntas
     */
    public String getNroOrdenamiento() {
        return nroOrdenamiento;
    }

    /**
     * 
     * @param nroOrdenamiento inserta el numero de ordenamiento de las preguntas
     */
    public void setNroOrdenamiento(String nroOrdenamiento) {
        this.nroOrdenamiento = nroOrdenamiento;
    }

    /**
     * 
     * @return obtiene el codigo de la pregunta
     */
    public String getCodigoPregunta() {
        return codigoPregunta;
    }

    /**
     * 
     * @param codigoPregunta inserta el codigo de la pregunta
     */
    public void setCodigoPregunta(String codigoPregunta) {
        this.codigoPregunta = codigoPregunta;
    }

    /**
     * 
     * @return obtiene la descripcion de la pregunta
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * 
     * @param descripcion inserta la descripcion de la pregunta
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * 
     * @return obtiene el codigo del tipo de pregunta
     */
    public String getCodigoTipoPregunta() {
        return codigoTipoPregunta;
    }

    /**
     * 
     * @param codigoTipoPregunta inserta el codigo del tipo de pregunta
     */
    public void setCodigoTipoPregunta(String codigoTipoPregunta) {
        this.codigoTipoPregunta = codigoTipoPregunta;
    }

    /**
     * 
     * @return obtiene el tipo de dato de la respuesta
     */
    public String getTipoDato() {
        return tipoDato;
    }

    /**
     * 
     * @param tipoDato inserta el tipo de dato de la respuesta
     */
    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    /**
     * 
     * @return obtiene la longitud de la respuesta
     */
    public String getLongitudDato() {
        return longitudDato;
    }

    /**
     * 
     * @param longitudDato inserta la longitud de la respuesta
     */
    public void setLongitudDato(String longitudDato) {
        this.longitudDato = longitudDato;
    }
    
    
}
