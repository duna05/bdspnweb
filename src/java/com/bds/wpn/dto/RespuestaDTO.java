/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;


import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;

/**
 * Esta clase va a servir como referencia para el manejo de errores
 *
 * @author cesar.mujica
 */
/**
 * clase para validar los estatus de respuestas de los servicios y/o
 * procedimientos almacenados
 *
 * @author cesar.mujica
 */
public class RespuestaDTO extends BDSUtil implements Serializable {

    //Atributos que funcionan para el manejo de errores interno.
    //codigo de respuesta de status, 000 representa estatus exitoso
    private String codigo;

    //breve descripcion relativa al codigo
    private String descripcion;

    //Atributos que describen la respuesta de determinados SP    
    private String codigoSP;        //Codigo arrojado por SP invocado.
    private String descripcionSP;   //Siglas del codigo devuelto.
    private String textoSP;         //Descripcion del codigo de salida. 

    public RespuestaDTO() {
        this.codigo = CODIGO_RESPUESTA_EXITOSO;
        this.descripcion = DESCRIPCION_RESPUESTA_EXITOSO;
        this.codigoSP = CODIGO_RESPUESTA_EXITOSO;
        this.descripcionSP = DESCRIPCION_RESPUESTA_EXITOSO_SP;
    }

    /**
     * Descripcion del codigo de salida.
     *
     * @return String Descripcion del codigo de salida.
     */
    public String getTextoSP() {
        return textoSP;
    }

    /**
     * Descripcion del codigo de salida.
     *
     * @param textoSP String
     */
    public void setTextoSP(String textoSP) {
        this.textoSP = textoSP;
    }

    /**
     * Obtiene Codigo arrojado por SP invocado
     *
     * @return String
     */
    public String getCodigoSP() {
        return codigoSP;
    }

    /**
     * Ingresa Codigo arrojado por SP invocado
     *
     * @param codigoSP String
     */
    public void setCodigoSP(String codigoSP) {
        this.codigoSP = codigoSP;
    }

    /**
     * Obtiene Siglas del codigo devuelto.
     *
     * @return String
     */
    public String getDescripcionSP() {
        return descripcionSP;
    }

    /**
     * Ingresa Siglas del codigo devuelto.
     *
     * @param descripcionSP String
     */
    public void setDescripcionSP(String descripcionSP) {
        this.descripcionSP = descripcionSP;
    }

    /**
     * Retorna el codigo de respuesta de status, 000 representa estatus exitoso
     *
     * @return String
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Asignar codigo de resuesta
     *
     * @param codigo String
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
        if (!this.codigo.equals(CODIGO_RESPUESTA_EXITOSO)) {
            this.descripcion = DESCRIPCION_RESPUESTA_FALLIDA;
        }
    }

    /**
     * Retorna una breve descripcion relativa al codigo
     *
     * @return String descripcion de respuesta de status
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Asignar descripcion
     *
     * @param descripcion String
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
