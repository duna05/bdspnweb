/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Clase para el manejo de las transacciones recuperadas desde la base de datos en consulta de mvimientos.
 * @author mdiaz
 */
public class TransaccionesCuentasDTO implements Serializable{
    
    private String codigoTransaccion;
    private String descripcionTransaccion;
    private RespuestaDTO respuestaDTO;                 //manejo de respuestas

    private List<TransaccionesCuentasDTO> listadoTransacciones;

    public String getCodigoTransaccion() {
        return codigoTransaccion;
    }

    public void setCodigoTransaccion(String codigoTransaccion) {
        this.codigoTransaccion = codigoTransaccion;
    }

    public String getDescripcionTransaccion() {
        return descripcionTransaccion;
    }

    public void setDescripcionTransaccion(String descripcionTransaccion) {
        this.descripcionTransaccion = descripcionTransaccion;
    }

    public List<TransaccionesCuentasDTO> getListadoTransacciones() {
        return listadoTransacciones;
    }

    public void setListadoTransacciones(List<TransaccionesCuentasDTO> listadoTransacciones) {
        this.listadoTransacciones = listadoTransacciones;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }
   
   
    
}
