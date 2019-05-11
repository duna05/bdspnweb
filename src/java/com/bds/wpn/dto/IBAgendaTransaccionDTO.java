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
 * @author cesar.mujica
 */
public class IBAgendaTransaccionDTO implements Serializable{
    
//****************campo de la cabecera******************//    
private	BigDecimal id;                                      //Identificador de la secuencia
private	BigDecimal idUsuario;                               //Identificador del usuario que crea la agenda
private	char tipo;                                          //tipo de agenda "1" = transferencias, "2" = pago TDC
private	String cuentaOrigen;                                //numero de cuenta de origen
private	String cuentaDestino;                               //numero de cta, tdc o servicio de destino
private	BigDecimal monto;                                   //monto de la transaccion
private	char frecuencia;                                    //frecuencia de ejecucion "1" semanal, "2" quincenal, "3" mensual
private	int diaFrecuencia;                                  //fecha acorde al tipo de frecuencia ()
private	String fechalimite;                                 //fecha hasta la cual perdurara la agenda
//****************campo del detalle******************//  
private	int idTransaccion;                                  //Identificador del CORE del tipo de la transaccion
private	char estatus;                                       //estatus de la agenda "A" activa "I" inactiva "E" ejecutada
private	BigDecimal idAgenda;                                //identificador de la agenda asociada
private	String fechaCreacion;                               //fecha completa de la creacion
private	String fechaAgendada;                               //fecha destinada a ejecutarse la transaccion
private	char tipoDoc;                                       //tipo de documento del beneficiario
private	String documento;                                   //numero de documento del beneficiario
private	String descripcion;                                 //descripcion breve de la transaccion
private	String nombreBeneficiario;                                      //nombre del beneficiario
private	String email;                                       //correo electronico del beneficiario
private	String fechaEjecucion;                              //fecha en la que se ejecuto la transaccion
private List <IBAgendaTransaccionDTO> transAgendadas;       //listado de transacciones agendadas
private RespuestaDTO respuesta;                             //objeto de control de ejecucion

private String bancoBeneficiario;

/*********************inicio GETERS Y SETERS********************/

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getBancoBeneficiario() {
        return bancoBeneficiario;
    }

    public void setBancoBeneficiario(String bancoBeneficiario) {
        this.bancoBeneficiario = bancoBeneficiario;
    }

    public BigDecimal getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(BigDecimal idUsuario) {
        this.idUsuario = idUsuario;
    }

    public char getTipo() {
        return tipo;
    }

    public void setTipo(char tipo) {
        this.tipo = tipo;
    }

    public String getCuentaOrigen() {
        return cuentaOrigen;
    }

    public void setCuentaOrigen(String ctaOrigen) {
        this.cuentaOrigen = ctaOrigen;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String ctaDestino) {
        this.cuentaDestino = ctaDestino;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public char getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(char frecuencia) {
        this.frecuencia = frecuencia;
    }

    public int getDiaFrecuencia() {
        return diaFrecuencia;
    }

    public void setDiaFrecuencia(int fechaFrecuencia) {
        this.diaFrecuencia = fechaFrecuencia;
    }

    public String getFechalimite() {
        return fechalimite;
    }

    public void setFechalimite(String fechalimite) {
        this.fechalimite = fechalimite;
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

    public String getNombreBeneficiario() {
        return nombreBeneficiario;
    }

    public void setNombreBeneficiario(String nombreBeneficiario) {
        this.nombreBeneficiario = nombreBeneficiario;
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

    public RespuestaDTO getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaDTO respuesta) {
        this.respuesta = respuesta;
    }

/*********************FIN GETERS Y SETERS********************/


        
        










    
}
