/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author cesar.mujica
 */
public class IbUsuariosP2PDTO {
    private BigDecimal id;                      //identificador del registro
    private BigDecimal idUsuario;               //identificador del usuario en Ora11
    private String nroTelefono;                 //numero de telefono asociado al servicio
    private String tipoDocumento;               //tipo de doc del usuario asociado al servicio
    private String nroDocumento;                //numero de doc del usuario asociado al servicio
    private String email;                       //correo electronico del usuario asociado al servicio
    private String nroCuenta;                   //numero de cuenta asociada al servicio
    private String mtoMaxTransaccion;       //monto maximo de pago por operacion
    private String mtoMaxDiario;            //monto maximo acumlado diarios de pagos
    private List<IbUsuariosP2PDTO> usuariosP2p;//lista de afiliaciones del mismo usuario p2p
    private List<IbBeneficiariosP2PDTO> beneficiariosP2P;//lista de beneficiarios del usuario p2p
    private List<IbBeneficiariosP2CDTO> beneficiariosP2C;//lista de beneficiarios del usuario p2c
    private RespuestaDTO respuestaDTO;          //objeto de respuestas

    public IbUsuariosP2PDTO() {
    } 

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(BigDecimal idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNroTelefono() {
        return nroTelefono;
    }

    public void setNroTelefono(String nroTelefono) {
        this.nroTelefono = nroTelefono;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNroCuenta() {
        return nroCuenta;
    }

    public void setNroCuenta(String nroCuenta) {
        this.nroCuenta = nroCuenta;
    }

    public String getMtoMaxTransaccion() {
        return mtoMaxTransaccion;
    }

    public void setMtoMaxTransaccion(String mtoMaxTransaccion) {
        this.mtoMaxTransaccion = mtoMaxTransaccion;
    }

    public String getMtoMaxDiario() {
        return mtoMaxDiario;
    }

    public void setMtoMaxDiario(String mtoMaxDiario) {
        this.mtoMaxDiario = mtoMaxDiario;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    public List<IbUsuariosP2PDTO> getUsuariosP2p() {
        return usuariosP2p;
    }

    public void setUsuariosP2p(List<IbUsuariosP2PDTO> usuariosP2p) {
        this.usuariosP2p = usuariosP2p;
    }

    public List<IbBeneficiariosP2PDTO> getBeneficiariosP2P() {
        return beneficiariosP2P;
    }

    public void setBeneficiariosP2P(List<IbBeneficiariosP2PDTO> beneficiariosP2P) {
        this.beneficiariosP2P = beneficiariosP2P;
    }

    public List<IbBeneficiariosP2CDTO> getBeneficiariosP2C() {
        return beneficiariosP2C;
    }

    public void setBeneficiariosP2C(List<IbBeneficiariosP2CDTO> beneficiariosP2C) {
        this.beneficiariosP2C = beneficiariosP2C;
    }
    
}
