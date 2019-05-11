/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author alejandro.flores
 */
public class FichaClienteEmpleosDTO extends BDSUtil implements Serializable{
    
         private String nombreEmpresa; 
         private String cargo; 
         private BigDecimal ultimoSueldo; 
         private String fechaEntrada; 
         private String rif; 
         private String ramo; 
         private String direccion; 
         private String telefono;
         private RespuestaDTO respuesta;
         private List<FichaClienteEmpleosDTO> ibEmpleoList;
        

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public BigDecimal getUltimoSueldo() {
        return ultimoSueldo;
    }

    public void setUltimoSueldo(BigDecimal ultimoSueldo) {
        this.ultimoSueldo = ultimoSueldo;
    }

    public String getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(String fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public String getRif() {
        return rif;
    }

    public void setRif(String rif) {
        this.rif = rif;
    }

    public String getRamo() {
        return ramo;
    }

    public void setRamo(String ramo) {
        this.ramo = ramo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public RespuestaDTO getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaDTO respuesta) {
        this.respuesta = respuesta;
    }

    public List<FichaClienteEmpleosDTO> getIbEmpleoList() {
        return ibEmpleoList;
    }

    public void setIbEmpleoList(List<FichaClienteEmpleosDTO> ibEmpleoList) {
        this.ibEmpleoList = ibEmpleoList;
    }
         
         
    
}
