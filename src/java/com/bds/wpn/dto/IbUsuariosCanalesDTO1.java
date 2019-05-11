/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author juan.faneite
 */
public class IbUsuariosCanalesDTO1 implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private String idSesion;                      //identificador unico de la sesion
    private String estatusRegistro;
    private String estatusAcceso;
    private BigDecimal limiteInternas;
    private BigDecimal limiteInternasTerceros;
    private BigDecimal limiteExternas;
    private BigDecimal limiteExternasTerceros;
    private Character inicioSesion;
    private Date fechaHoraUltimaInteraccionDate;
    private IbUsuariosDTO idUsuarioDTO;
    private IbCanalDTO idCanalDTO;
    private int intentosFallidos;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto    

    /**
     * Obtener respuesta
     * @return RespuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asignar respuesta
     * @param respuestaDTO RespuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    /**
     * Obtener id
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Asignar id
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Obtener estatus registro
     * @return String
     */
    public String getEstatusRegistro() {
        return estatusRegistro;
    }

    /**
     * Asignar estatus registro
     * @param estatusRegistro String
     */
    public void setEstatusRegistro(String estatusRegistro) {
        this.estatusRegistro = estatusRegistro;
    }

    /**
     * Obtener estatus acceso
     * @return String
     */
    public String getEstatusAcceso() {
        return estatusAcceso;
    }

    /**
     * Asignar estatus acceso
     * @param estatusAcceso String
     */
    public void setEstatusAcceso(String estatusAcceso) {
        this.estatusAcceso = estatusAcceso;
    }

    /**
     * Obtener limite internas
     * @return BigDecimal
     */
    public BigDecimal getLimiteInternas() {
        return limiteInternas;
    }

    /**
     * Asignar limites internas
     * @param limiteInternas BigDecimal
     */
    public void setLimiteInternas(BigDecimal limiteInternas) {
        this.limiteInternas = limiteInternas;
    }

    /**
     * Obtener limite internas terceros
     * @return BigDecimal
     */
    public BigDecimal getLimiteInternasTerceros() {
        return limiteInternasTerceros;
    }

    /**
     * Asignar limite internas terceros
     * @param limiteInternasTerceros BigDecimal
     */
    public void setLimiteInternasTerceros(BigDecimal limiteInternasTerceros) {
        this.limiteInternasTerceros = limiteInternasTerceros;
    }

    /**
     * Obtener limite de externas
     * @return BigDecimal
     */
    public BigDecimal getLimiteExternas() {
        return limiteExternas;
    }

    /**
     * Asignar limite de extenas
     * @param limiteExternas BigDecimal
     */
    public void setLimiteExternas(BigDecimal limiteExternas) {
        this.limiteExternas = limiteExternas;
    }

    /**
     * Obtener limite externas terceros
     * @return BigDecimal
     */
    
    public BigDecimal getLimiteExternasTerceros() {
        return limiteExternasTerceros;
    }

    /**
     * Asignar limite externas
     * @param limiteExternasTerceros BigDecimal
     */
    public void setLimiteExternasTerceros(BigDecimal limiteExternasTerceros) {
        this.limiteExternasTerceros = limiteExternasTerceros;
    }

    /**
     * Obtener inicio sesion
     * @return Character
     */
    public Character getInicioSesion() {
        return inicioSesion;
    }

    /**
     * Asignar inicio sesion
     * @param inicioSesion Character
     */
    public void setInicioSesion(Character inicioSesion) {
        this.inicioSesion = inicioSesion;
    }

    /**
     * Obtener fecha hora ultima interaccion
     * @return Date
     */
    public Date getFechaHoraUltimaInteraccionDate() {
        return fechaHoraUltimaInteraccionDate;
    }

    /**
     * Asignar fecha hora ultima interaccion
     * @param fechaHoraUltimaInteraccion Date
     */
    public void setFechaHoraUltimaInteraccionDate(Date fechaHoraUltimaInteraccion) {
        this.fechaHoraUltimaInteraccionDate = fechaHoraUltimaInteraccion;
    }

    /**
     * Obtener usuario
     * @return IbUsuariosDTO
     */
    public IbUsuariosDTO getIdUsuarioDTO() {
        return idUsuarioDTO;
    }

    /**
     * Asignar usuario
     * @param idUsuarioDTO IbUsuariosDTO
     */
    public void setIdUsuarioDTO(IbUsuariosDTO idUsuarioDTO) {
        this.idUsuarioDTO = idUsuarioDTO;
    }

    /**
     * Obtener canal
     * @return IbCanalDTO
     */
    public IbCanalDTO getIdCanalDTO() {
        return idCanalDTO;
    }

    /**
     * Asignar canal
     * @param idCanalDTO IbCanalDTO
     */
    public void setIdCanalDTO(IbCanalDTO idCanalDTO) {
        this.idCanalDTO = idCanalDTO;
    }
    
    /**
     * Obtener intentos fallidos
     * @return int
     */
     public int getIntentosFallidos() {
        return intentosFallidos;
    }

     /**
      * Asignar intentos falidos
      * @param intentosFallidos int
      */
    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    /**
     * 
     * @return el identificador unico de la sesion
     */
    public String getIdSesion() {
        return idSesion;
    }

    /**
     * asigna el identificador unico de la sesion
     * @param idSesion identificador unico de la sesion
     */
    public void setIdSesion(String idSesion) {
        this.idSesion = idSesion;
    }
    
    

}
