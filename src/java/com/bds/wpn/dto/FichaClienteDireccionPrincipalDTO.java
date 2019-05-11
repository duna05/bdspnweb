/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author alejandro.flores
 */
public class FichaClienteDireccionPrincipalDTO extends BDSUtil implements Serializable{
    
    private BigDecimal codigoTipoDireccion; // listo
    private String descDireccion; //listo
    private String dirPrincipal; // Direccion principal  es  un chekc
    private String dirValida; // Direccion Valida  es  un chekc
    private BigDecimal codigoDepartamento; // Estado listo
    private String nombreDepartamento;  // estado listo
    private BigDecimal codigoSector; // cod parroquia
    private String descSector;  //nombre parroquia
    private String urbanizacion; //     urbanizacion
    private String calle; //
    private String edificio;
    private String manzana;
    private String nroApartamento;
    private String tenencia;
    private BigDecimal zona;
    private String telefonos;
    private String telex; /*HAY QUE VALIDAR SI ESTE ES OTRO TELEFONO*/
    private String email;
    private String es_de_trabajo;
    private BigDecimal codigoMunicipio;
    private RespuestaDTO respuesta; 
    private String ciudad;
    private String ciudadPN;
    private String codigoPostal;
    private BigDecimal codigoDireccion;

    public String getCiudadPN() {
        return ciudadPN;
    }

    public void setCiudadPN(String ciudadPN) {
        this.ciudadPN = ciudadPN;
    }
    
    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public BigDecimal getCodigoDireccion() {
        return codigoDireccion;
    }

    public void setCodigoDireccion(BigDecimal codigoDireccion) {
        this.codigoDireccion = codigoDireccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public RespuestaDTO getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaDTO respuesta) {
        this.respuesta = respuesta;
    }

    public BigDecimal getCodigoTipoDireccion() {
        return codigoTipoDireccion;
    }

    public void setCodigoTipoDireccion(BigDecimal codigoTipoDireccion) {
        this.codigoTipoDireccion = codigoTipoDireccion;
    }

    public String getDescDireccion() {
        return descDireccion;
    }

    public void setDescDireccion(String descDireccion) {
        this.descDireccion = descDireccion;
    }

    public String getDirPrincipal() {
        return dirPrincipal;
    }

    public void setDirPrincipal(String dirPrincipal) {
        this.dirPrincipal = dirPrincipal;
    }

    public String getDirValida() {
        return dirValida;
    }

    public void setDirValida(String dirValida) {
        this.dirValida = dirValida;
    }

    public BigDecimal getCodigoDepartamento() {
        return codigoDepartamento;
    }

    public void setCodigoDepartamento(BigDecimal codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
    }

    public String getNombreDepartamento() {
        return nombreDepartamento;
    }

    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }

    public BigDecimal getCodigoSector() {
        return codigoSector;
    }

    public void setCodigoSector(BigDecimal codigoSector) {
        this.codigoSector = codigoSector;
    }

    public String getDescSector() {
        return descSector;
    }

    public void setDescSector(String descSector) {
        this.descSector = descSector;
    }

    public String getUrbanizacion() {
        return urbanizacion;
    }

    public void setUrbanizacion(String urbanizacion) {
        this.urbanizacion = urbanizacion;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

    public String getManzana() {
        return manzana;
    }

    public void setManzana(String manzana) {
        this.manzana = manzana;
    }

    public String getNroApartamento() {
        return nroApartamento;
    }

    public void setNroApartamento(String nroApartamento) {
        this.nroApartamento = nroApartamento;
    }

    public String getTenencia() {
        return tenencia;
    }

    public void setTenencia(String tenencia) {
        this.tenencia = tenencia;
    }

    public BigDecimal getZona() {
        return zona;
    }

    public void setZona(BigDecimal zona) {
        this.zona = zona;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getTelex() {
        return telex;
    }

    public void setTelex(String telex) {
        this.telex = telex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEs_de_trabajo() {
        return es_de_trabajo;
    }

    public void setEs_de_trabajo(String es_de_trabajo) {
        this.es_de_trabajo = es_de_trabajo;
    }

    public BigDecimal getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(BigDecimal codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    
    
}
