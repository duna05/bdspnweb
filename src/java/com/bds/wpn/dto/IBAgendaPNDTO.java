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
 * Clase IBAfiliacionesDTO
 * @author juan.faneite
 */
public class IBAgendaPNDTO implements Serializable {

    private int idTransaccion;                                  //Identificador del CORE del tipo de la transaccion
    private char estatus;                                       //estatus de la agenda "A" activa "I" inactiva "E" ejecutada
    private BigDecimal idAgenda;                                //identificador de la agenda asociada
    private String fechaCreacion;                               //fecha completa de la creacion
    private String fechaAgendada;                               //fecha destinada a ejecutarse la transaccion
    private char tipoDoc;                                       //tipo de documento del beneficiario
    private String documento;                                   //numero de documento del beneficiario
    private String descripcion;                                 //descripcion breve de la transaccion
    private String nombre;                                      //nombre del beneficiario
    private String email;                                       //correo electronico del beneficiario
    private String fechaEjecucion;                              //fecha en la que se ejecuto la transaccion
    private List<IBAgendaTransaccionDTO> transAgendadas;       //listado de transacciones agendadas
    private RespuestaDTO respuestaDTO;                             //objeto de control de ejecucion


    public IBAgendaPNDTO() {
    }

    public int getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(int idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public char getEstatus() {
        return estatus;
    }

    public void setEstatus(char estatus) {
        this.estatus = estatus;
    }

    public BigDecimal getIdAgenda() {
        return idAgenda;
    }

    public void setIdAgenda(BigDecimal idAgenda) {
        this.idAgenda = idAgenda;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaAgendada() {
        return fechaAgendada;
    }

    public void setFechaAgendada(String fechaAgendada) {
        this.fechaAgendada = fechaAgendada;
    }

    public char getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(char tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(String fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public List<IBAgendaTransaccionDTO> getTransAgendadas() {
        return transAgendadas;
    }

    public void setTransAgendadas(List<IBAgendaTransaccionDTO> transAgendadas) {
        this.transAgendadas = transAgendadas;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

   
    
    
    

}
