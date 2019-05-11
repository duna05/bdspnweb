/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

/**
 *
 * @author juan.faneite
 */
public class IbTransaccionesBeneficiariosDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private Character tipoCarga;
    private Character tipo;
    private String nombre;
    private Character tipoDoc;
    private String documento;
    private String numeroCuenta;
    private String email;
    private String celular;
    private BigDecimal monto;
    private String referencia;
    private String concepto;
    private Character reversada;
    private IbTransaccionesPendientesDTO idTransaccionPendienteDTO;
    private Collection<IbDetallePagoProveedorDTO> ibDetallePagoProveedorDTOCollection;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto

    /**
     * Asignar respuesta
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
     * Obtener tipo carga
     * @return Character
     */
    public Character getTipoCarga() {
        return tipoCarga;
    }

    /**
     * Asinar tipo de carga
     * @param tipoCarga Character
     */
    public void setTipoCarga(Character tipoCarga) {
        this.tipoCarga = tipoCarga;
    }

    /**
     * Obtener tipo
     * @return Character
     */
    public Character getTipo() {
        return tipo;
    }

    /**
     * Asignar tipo
     * @param tipo Character
     */
    public void setTipo(Character tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtener nombre
     * @return String
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asignar nombre
     * @param nombre String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtener tipo documento
     * @return Character
     */
    public Character getTipoDoc() {
        return tipoDoc;
    }

    /**
     * Asignar tipo documento
     * @param tipoDoc Character
     */
    public void setTipoDoc(Character tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    /**
     * Obtener documento
     * @return String
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * Asignar documento
     * @param documento String
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    /**
     * Obtener numero de cuenta
     * @return String
     */
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    /**
     * Asignar numero de cuenta
     * @param numeroCuenta String
     */
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    /**
     * Obtener email
     * @return String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Asignar email
     * @param email String
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtener celular
     * @return String
     */
    public String getCelular() {
        return celular;
    }

    /**
     * Asignar celular
     * @param celular String
     */
    public void setCelular(String celular) {
        this.celular = celular;
    }

    /**
     * Obtener monto
     * @return BigDecimal
     */
    public BigDecimal getMonto() {
        return monto;
    }

    /**
     * Asignar monto
     * @param monto BigDecimal
     */
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    /**
     * Obtener referencia
     * @return String
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * Asinar referencia
     * @param referencia String
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    /**
     * Obtener concepto
     * @return String
     */
    public String getConcepto() {
        return concepto;
    }    
    
    /**
     * Asignar concepto
     * @param concepto String
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
    
    
    /**
     * Obtener reversada
     * @return Character
     */
    public Character getReversada() {
        return reversada;
    }

    /**
     * Asiganr reversada
     * @param reversada Character
     */
    public void setReversada(Character reversada) {
        this.reversada = reversada;
    }

    /**
     * Obtener transacciones pendientes
     * @return IbTransaccionesPendientesDTO
     */
    public IbTransaccionesPendientesDTO getIdTransaccionPendienteDTO() {
        return idTransaccionPendienteDTO;
    }

    /**
     * Asignar transacciones pendientes
     * @param idTransaccionPendienteDTO IbTransaccionesPendientesDTO
     */
    public void setIdTransaccionPendienteDTO(IbTransaccionesPendientesDTO idTransaccionPendienteDTO) {
        this.idTransaccionPendienteDTO = idTransaccionPendienteDTO;
    }

    /**
     * Obtener detalle pago de proveedores
     * @return IbDetallePagoProveedorDTO
     */
    public Collection<IbDetallePagoProveedorDTO> getIbDetallePagoProveedorDTOCollection() {
        return ibDetallePagoProveedorDTOCollection;
    }

    /**
     * Asignar detalle pago de proveedores
     * @param ibDetallePagoProveedorDTOCollection Collection<IbDetallePagoProveedorDTO>
     */
    public void setIbDetallePagoProveedorDTOCollection(Collection<IbDetallePagoProveedorDTO> ibDetallePagoProveedorDTOCollection) {
        this.ibDetallePagoProveedorDTOCollection = ibDetallePagoProveedorDTOCollection;
    }

}
