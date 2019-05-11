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
 * @author alejandro.flores
 */
public class FichaClienteInsertarDTO {
    
    private BigDecimal tipoIdentificacion;   
    private String NumeroIdentificacion; 
    private BigDecimal    codSubTipoCliente;    
    private String tipoCliente;
    private BigDecimal codigoCliente; 
    private String codigoOnt;
    private String nombreComercial;
    private String razonSocial;    
    private BigDecimal codGrupoEconomico;    
    private BigDecimal codClasificacion;   
    private BigDecimal codSubClasificacion;    
    private BigDecimal codSubSubClasificacion;    
    private BigDecimal tipoIdentificacionR;    
    private String NumeroIdentificacionr; 
    private String nombreRepresentante;
    private BigDecimal codCargoRepresentante;

    public BigDecimal getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(BigDecimal tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getNumeroIdentificacion() {
        return NumeroIdentificacion;
    }

    public void setNumeroIdentificacion(String NumeroIdentificacion) {
        this.NumeroIdentificacion = NumeroIdentificacion;
    }

    public BigDecimal getCodSubTipoCliente() {
        return codSubTipoCliente;
    }

    public void setCodSubTipoCliente(BigDecimal codSubTipoCliente) {
        this.codSubTipoCliente = codSubTipoCliente;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public BigDecimal getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(BigDecimal codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getCodigoOnt() {
        return codigoOnt;
    }

    public void setCodigoOnt(String codigoOnt) {
        this.codigoOnt = codigoOnt;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public BigDecimal getCodGrupoEconomico() {
        return codGrupoEconomico;
    }

    public void setCodGrupoEconomico(BigDecimal codGrupoEconomico) {
        this.codGrupoEconomico = codGrupoEconomico;
    }

    public BigDecimal getCodClasificacion() {
        return codClasificacion;
    }

    public void setCodClasificacion(BigDecimal codClasificacion) {
        this.codClasificacion = codClasificacion;
    }

    public BigDecimal getCodSubClasificacion() {
        return codSubClasificacion;
    }

    public void setCodSubClasificacion(BigDecimal codSubClasificacion) {
        this.codSubClasificacion = codSubClasificacion;
    }

    public BigDecimal getCodSubSubClasificacion() {
        return codSubSubClasificacion;
    }

    public void setCodSubSubClasificacion(BigDecimal codSubSubClasificacion) {
        this.codSubSubClasificacion = codSubSubClasificacion;
    }

    public BigDecimal getTipoIdentificacionR() {
        return tipoIdentificacionR;
    }

    public void setTipoIdentificacionR(BigDecimal tipoIdentificacionR) {
        this.tipoIdentificacionR = tipoIdentificacionR;
    }

    public String getNumeroIdentificacionr() {
        return NumeroIdentificacionr;
    }

    public void setNumeroIdentificacionr(String NumeroIdentificacionr) {
        this.NumeroIdentificacionr = NumeroIdentificacionr;
    }

    public String getNombreRepresentante() {
        return nombreRepresentante;
    }

    public void setNombreRepresentante(String nombreRepresentante) {
        this.nombreRepresentante = nombreRepresentante;
    }

    public BigDecimal getCodCargoRepresentante() {
        return codCargoRepresentante;
    }

    public void setCodCargoRepresentante(BigDecimal codCargoRepresentante) {
        this.codCargoRepresentante = codCargoRepresentante;
    }
    
    
      
    
}
