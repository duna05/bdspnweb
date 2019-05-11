/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author juan.faneite
 */
public class IbDetallePagoProveedorDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private String tipoDetalle;
    private String referencia;
    private BigDecimal monto;
    private IbTransaccionesBeneficiariosDTO idTransaccionBeneficiarioDTO;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto

    /**
     * Obtener objeto respuesta
     *
     * @return RespuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asignar objeto respuesta
     *
     * @param respuestaDTO RespuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    /**
     * Obtener id
     *
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Asignar id
     *
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Obtener tipo detalle
     *
     * @return String
     */
    public String getTipoDetalle() {
        return tipoDetalle;
    }

    /**
     * Asinar tipo detalle
     *
     * @param tipoDetalle String
     */
    public void setTipoDetalle(String tipoDetalle) {
        this.tipoDetalle = tipoDetalle;
    }

    /**
     * Obtener referencia
     *
     * @return String
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * Asignar referencia
     *
     * @param referencia String
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    /**
     * Obtener monto
     *
     * @return BigDecimal
     */
    public BigDecimal getMonto() {
        return monto;
    }

    /**
     * Asignar monto
     *
     * @param monto BigDecimal
     */
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    /**
     * Obtener Transaccion beneficiario
     *
     * @return
     */
    public IbTransaccionesBeneficiariosDTO getIdTransaccionBeneficiarioDTO() {
        return idTransaccionBeneficiarioDTO;
    }

    /**
     * Asinar transaccion beneficiario
     *
     * @param idTransaccionBeneficiarioDTO
     */
    public void setIdTransaccionBeneficiarioDTO(IbTransaccionesBeneficiariosDTO idTransaccionBeneficiarioDTO) {
        this.idTransaccionBeneficiarioDTO = idTransaccionBeneficiarioDTO;
    }

}
