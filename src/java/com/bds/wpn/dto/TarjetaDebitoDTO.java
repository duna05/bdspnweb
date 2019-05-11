/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Clase TarjetaDebitoDTO
 * @author rony.rodriguez
 */
public class TarjetaDebitoDTO implements Serializable {

    private String numeroTarjeta;	//Numero de Tarjeta de 20 digitos.
    private String estado;	//Estatus de la Tarjeta de debito.
    private ClienteDTO clienteDTO;  //cliente asociado a la tdd
    private String codigoAgencia;
    private String codigoSubaplicacion;
    private List<ReclamoDTO> reclamoDTO;
    private List<TarjetaDebitoDTO> tddsDTO;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto

    /**
     * 
     * @return listado de resclamos asociados a las TDD
     */
    public List<ReclamoDTO> getReclamoDTO() {
        return reclamoDTO;
    }

    /**
     * 
     * @param reclamoDTO listado de resclamos asociados a las TDD
     */
    public void setReclamoDTO(List<ReclamoDTO> reclamoDTO) {
        this.reclamoDTO = reclamoDTO;
    }    
    
    /**
     * obtiene codigo de agencia
     * @return codigo de agencia
     */
    public String getCodigoAgencia() {
        return codigoAgencia;
    }

    /**
     * inserta codigo de agencia
     * @param codigoAgencia codigo de agencia
     */
    public void setCodigoAgencia(String codigoAgencia) {
        this.codigoAgencia = codigoAgencia;
    }

    /**
     * obtiene codigo de subaplicacion
     * @return codigo de subaplicacion
     */
    public String getCodigoSubaplicacion() {
        return codigoSubaplicacion;
    }

    /**
     * inserta codigo de subaplicacion
     * @param codigoSubaplicacion codigo de subaplicacion
     */
    public void setCodigoSubaplicacion(String codigoSubaplicacion) {
        this.codigoSubaplicacion = codigoSubaplicacion;
    }
    
    /**
     * obtiene cliente asociado a la tdd
     * @return cliente
     */
    public ClienteDTO getClienteDTO() {
        return clienteDTO;
    }

    /**
     * inserta cliente asociado a la tdd
     * @param clienteDTO
     */
    public void setClienteDTO(ClienteDTO clienteDTO) {
        this.clienteDTO = clienteDTO;
    }
    
    /**
     * @return the numeroTarjeta
     */
    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    /**
     * @param numeroTarjeta the numeroTarjeta to set
     */
    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    /**
     * @return the estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }
 
    public List<TarjetaDebitoDTO> getTddsDTO() {
        return tddsDTO;
    }

    public void setTddsDTO(List<TarjetaDebitoDTO> tddsDTO) {
        this.tddsDTO = tddsDTO;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }
        
}
