/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import com.bds.wpn.ws.services.IbUsuarios;
import java.util.Date;

/**
 *
 * @author Ledwin.Belen
 */
public class IbBeneficiariosP2PDTO {
    private Long id;
    private String nroTelefono;
    private String tipoDocumento;
    private String nroDocumento;
    private short codigoBanco;
    private short estatusBeneficiario;
    private String nombreBeneficiario;
    private String aliasBeneficiario;
    private Date fechaHoraCarga;
    private Date fechaHoraModificacion;
    private IbUsuarios codigoUsuarioModifica;
    private IbUsuarios idUsuario;
    private IbUsuarios codigoUsuarioCarga;
    private String nombreBanco;


    public IbBeneficiariosP2PDTO() {
    } 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public short getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(short codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    public short getEstatusBeneficiario() {
        return estatusBeneficiario;
    }

    public void setEstatusBeneficiario(short estatusBeneficiario) {
        this.estatusBeneficiario = estatusBeneficiario;
    }

    public String getNombreBeneficiario() {
        return nombreBeneficiario;
    }

    public void setNombreBeneficiario(String nombreBeneficiario) {
        this.nombreBeneficiario = nombreBeneficiario;
    }

    public String getAliasBeneficiario() {
        return aliasBeneficiario;
    }

    public void setAliasBeneficiario(String aliasBeneficiario) {
        this.aliasBeneficiario = aliasBeneficiario;
    }

    public Date getFechaHoraCarga() {
        return fechaHoraCarga;
    }

    public void setFechaHoraCarga(Date fechaHoraCarga) {
        this.fechaHoraCarga = fechaHoraCarga;
    }

    public Date getFechaHoraModificacion() {
        return fechaHoraModificacion;
    }

    public void setFechaHoraModificacion(Date fechaHoraModificacion) {
        this.fechaHoraModificacion = fechaHoraModificacion;
    }

    public IbUsuarios getCodigoUsuarioModifica() {
        return codigoUsuarioModifica;
    }

    public void setCodigoUsuarioModifica(IbUsuarios codigoUsuarioModifica) {
        this.codigoUsuarioModifica = codigoUsuarioModifica;
    }

    public IbUsuarios getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(IbUsuarios idUsuario) {
        this.idUsuario = idUsuario;
    }

    public IbUsuarios getCodigoUsuarioCarga() {
        return codigoUsuarioCarga;
    }

    public void setCodigoUsuarioCarga(IbUsuarios codigoUsuarioCarga) {
        this.codigoUsuarioCarga = codigoUsuarioCarga;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }
    
    
}
