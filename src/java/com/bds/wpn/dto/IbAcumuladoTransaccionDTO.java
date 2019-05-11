/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 *
 * @author juan.faneite
 */
public class IbAcumuladoTransaccionDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private Date fechaDate;
    private BigDecimal acumuladoInternas;
    private BigDecimal acumuladoInternasTerceros;
    private BigDecimal acumuladoExternas;
    private BigDecimal acumuladoExternasTerceros;
    private BigDecimal acumuladoDigitel;
    private BigDecimal acumuladoP2P;
    private BigDecimal acumuladoP2C;
    private IbUsuariosDTO idUsuarioDTO;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto
    private List<IbAcumuladoTransaccionDTO> acumulados;//lista de afiliaciones para el manejo en caso de consulta multiple

    /**
     * Obtiene el objeto respuesta
     *
     * @return RespuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asignacion de objeto respuesta
     *
     * @param respuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    /**
     * Obtener el id
     *
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Asinar el id
     *
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Obtener la fecha
     *
     * @return
     */
    public Date getFechaDate() {
        return fechaDate;
    }

    /**
     * Asinar la fecha
     *
     * @param fecha Date
     */
    public void setFechaDate(Date fechaDate) {
        this.fechaDate = fechaDate;
    }

    /**
     * Obtener el valor de acumulado Internas
     *
     * @return BigDecimal
     */
    public BigDecimal getAcumuladoInternas() {
        return acumuladoInternas;
    }

    /**
     * Asignar acumulado Internas
     *
     * @param acumuladoInternas BigDecimal
     */
    public void setAcumuladoInternas(BigDecimal acumuladoInternas) {
        this.acumuladoInternas = acumuladoInternas;
    }

    /**
     * Obtener Acumulado Internas Terceros
     *
     * @return BigDecimal
     */
    public BigDecimal getAcumuladoInternasTerceros() {
        return acumuladoInternasTerceros;
    }

    /**
     * Asignar acumulado internas terceros
     *
     * @param acumuladoInternasTerceros BigDecimal
     */
    public void setAcumuladoInternasTerceros(BigDecimal acumuladoInternasTerceros) {
        this.acumuladoInternasTerceros = acumuladoInternasTerceros;
    }

    /**
     * Asignar acumulado externas
     *
     * @return BigDecimal
     */
    public BigDecimal getAcumuladoExternas() {
        return acumuladoExternas;
    }

    /**
     * Asignar acumulado externas
     *
     * @param acumuladoExternas BigDecimal
     */
    public void setAcumuladoExternas(BigDecimal acumuladoExternas) {
        this.acumuladoExternas = acumuladoExternas;
    }

    /**
     * Obtener acumulado externas terceros
     *
     * @return BigDecimal
     */
    public BigDecimal getAcumuladoExternasTerceros() {
        return acumuladoExternasTerceros;
    }

    /**
     * Asignar acumulador externas terceros
     *
     * @param acumuladoExternasTerceros BigDecimal
     */
    public void setAcumuladoExternasTerceros(BigDecimal acumuladoExternasTerceros) {
        this.acumuladoExternasTerceros = acumuladoExternasTerceros;
    }

    /**
     * Obtener id usuario
     *
     * @return IbUsuariosDTO
     */
    public IbUsuariosDTO getIdUsuarioDTO() {
        return idUsuarioDTO;
    }

    /**
     * Asignar id de usuario
     *
     * @param idUsuarioDTO IbUsuariosDTO
     */
    public void setIdUsuarioDTO(IbUsuariosDTO idUsuarioDTO) {
        this.idUsuarioDTO = idUsuarioDTO;
    }

    /**
     * 
     * @return 
     */
    public List<IbAcumuladoTransaccionDTO> getAcumulados() {
        return acumulados;
    }

    /**
     * 
     * @param acumulados 
     */
    public void setAcumulados(List<IbAcumuladoTransaccionDTO> acumulados) {
        this.acumulados = acumulados;
    }

    /**
     * 
     * @return 
     */
    public BigDecimal getAcumuladoDigitel() {
        return acumuladoDigitel;
    }

    /**
     * 
     * @param acumuladoDigitel 
     */
    public void setAcumuladoDigitel(BigDecimal acumuladoDigitel) {
        this.acumuladoDigitel = acumuladoDigitel;
    }

    /**
     * 
     * @return 
     */
    public BigDecimal getAcumuladoP2P() {
        return acumuladoP2P;
    }

    /**
     * 
     * @param acumuladoP2p 
     */
    public void setAcumuladoP2P(BigDecimal acumuladoP2p) {
        this.acumuladoP2P = acumuladoP2p;
    }

    public BigDecimal getAcumuladoP2C() {
        return acumuladoP2C;
    }

    public void setAcumuladoP2C(BigDecimal acumuladoP2C) {
        this.acumuladoP2C = acumuladoP2C;
    }

    
    /**
     * metodo que calcula y retorna el total de acumulados de transferencias (menos las propias delsur)
     * @return 
     */
    public BigDecimal getAcumuladoTotalTransf() {
        BigDecimal acumTotalTransf = new BigDecimal(BigInteger.ZERO);
        if(this.acumuladoExternas != null){
            acumTotalTransf = acumTotalTransf.add(this.acumuladoExternas);
        }
        if(this.acumuladoExternasTerceros != null){
            acumTotalTransf = acumTotalTransf.add(this.acumuladoExternasTerceros);
        }
        if(this.acumuladoInternasTerceros != null){
            acumTotalTransf = acumTotalTransf.add(this.acumuladoInternasTerceros);
        }
        return acumTotalTransf;
    }
    
}
