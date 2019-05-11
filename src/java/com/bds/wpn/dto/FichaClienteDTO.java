/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
/**
 *
 * @author humberto.rojas
 */

public class FichaClienteDTO  implements Serializable{    
      
    private BigDecimal tipoIdentificacion;
    private String descTipoIdentificacion;
    private String NumeroIdentificacion; 
    private BigDecimal    codSubTipoCliente;
    private List<SubtipoClienteDTO> listSubTipoClienteJuridico;
    private String tipoCliente;
    private BigDecimal    codigoCliente; 
    private String codigoOnt;
    private String nombreComercial;
    private String razonSocial;
    private RespuestaDTO respuesta;
    private BigDecimal    codGrupoEconomico; 
    private String descGrupoEconomico;
    private BigDecimal    codClasificacion;
    private String desClasificacion;
    private BigDecimal    codSubClasificacion;
    private String desSubClasificacion;
    private BigDecimal    codSubSubClasificacion;
    private String desSubSubClasificacion;
    private BigDecimal    tipoIdentificacionR;
    private String descTipoIdentificacionR;
    private String NumeroIdentificacionr; 
    private String nombreRepresentante;
    private BigDecimal    codCargoRepresentante;
    private String descCargoR;
    //Persona Natural
    
    private String nombre;
    private String primerApellido;
    private String apellidoCasada;
    private String fechaNacimiento;
    private String nacionalidad;
    private String lugarNacimiento;
    private String sexo;
    private String estadoCivil;
    private String actEconomicaPpal;
    private String nivelEducativo;
    private BigDecimal codActEconomica;
    private String descripcionOcupacion;
    private String calificacion;
    private String numeroDependientes;
    private String cantidadHijos;
    private String idetConyugue;
    private String numeroIdentConyugue;
    private String nombreConyugue;
    private String segundoApellido;
    private String edad;
    private String desCalificacion;
    private BigDecimal codSegmento;
    private String desSegmento;
    private RespuestaDTO respuestaDTO;

    public BigDecimal getCodSegmento() {
        return codSegmento;
    }

    public void setCodSegmento(BigDecimal codSegmento) {
        this.codSegmento = codSegmento;
    }

    public String getDesSegmento() {
        return desSegmento;
    }

    public void setDesSegmento(String desSegmento) {
        this.desSegmento = desSegmento;
    }
    
    

    public RespuestaDTO getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaDTO respuesta) {
        this.respuesta = respuesta;
    }

    public String getDesCalificacion() {
        return desCalificacion;
    }

    public void setDesCalificacion(String desCalificacion) {
        this.desCalificacion = desCalificacion;
    }
   
    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getApellidoCasada() {
        return apellidoCasada;
    }

    public void setApellidoCasada(String apellidoCasada) {
        this.apellidoCasada = apellidoCasada;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getLugarNacimiento() {
        return lugarNacimiento;
    }

    public void setLugarNacimiento(String lugarNacimiento) {
        this.lugarNacimiento = lugarNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getActEconomicaPpal() {
        return actEconomicaPpal;
    }

    public void setActEconomicaPpal(String actEconomicaPpal) {
        this.actEconomicaPpal = actEconomicaPpal;
    }

    public String getNivelEducativo() {
        return nivelEducativo;
    }

    public void setNivelEducativo(String nivelEducativo) {
        this.nivelEducativo = nivelEducativo;
    }

    public BigDecimal getCodActEconomica() {
        return codActEconomica;
    }

    public void setCodActEconomica(BigDecimal codActEconomica) {
        this.codActEconomica = codActEconomica;
    }

    public String getDescripcionOcupacion() {
        return descripcionOcupacion;
    }

    public void setDescripcionOcupacion(String descripcionOcupacion) {
        this.descripcionOcupacion = descripcionOcupacion;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public String getNumeroDependientes() {
        return numeroDependientes;
    }

    public void setNumeroDependientes(String numeroDependientes) {
        this.numeroDependientes = numeroDependientes;
    }

    public String getCantidadHijos() {
        return cantidadHijos;
    }

    public void setCantidadHijos(String cantidadHijos) {
        this.cantidadHijos = cantidadHijos;
    }

    public String getIdetConyugue() {
        return idetConyugue;
    }

    public void setIdetConyugue(String idetConyugue) {
        this.idetConyugue = idetConyugue;
    }

    public String getNumeroIdentConyugue() {
        return numeroIdentConyugue;
    }

    public void setNumeroIdentConyugue(String numeroIdentConyugue) {
        this.numeroIdentConyugue = numeroIdentConyugue;
    }

    public String getNombreConyugue() {
        return nombreConyugue;
    }

    public void setNombreConyugue(String nombreConyugue) {
        this.nombreConyugue = nombreConyugue;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }
    
    public List<SubtipoClienteDTO> getListSubTipoClienteJuridico() {
        return listSubTipoClienteJuridico;
    }

    public void setListSubTipoClienteJuridico(List<SubtipoClienteDTO> listSubTipoClienteJuridico) {
        this.listSubTipoClienteJuridico = listSubTipoClienteJuridico;
    }
    

    public BigDecimal getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(BigDecimal tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getDescTipoIdentificacion() {
        return descTipoIdentificacion;
    }

    public void setDescTipoIdentificacion(String descTipoIdentificacion) {
        this.descTipoIdentificacion = descTipoIdentificacion;
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

    public String getDescGrupoEconomico() {
        return descGrupoEconomico;
    }

    public void setDescGrupoEconomico(String descGrupoEconomico) {
        this.descGrupoEconomico = descGrupoEconomico;
    }

    public BigDecimal getCodClasificacion() {
        return codClasificacion;
    }

    public void setCodClasificacion(BigDecimal codClasificacion) {
        this.codClasificacion = codClasificacion;
    }

    public String getDesClasificacion() {
        return desClasificacion;
    }

    public void setDesClasificacion(String desClasificacion) {
        this.desClasificacion = desClasificacion;
    }

    public BigDecimal getCodSubClasificacion() {
        return codSubClasificacion;
    }

    public void setCodSubClasificacion(BigDecimal codSubClasificacion) {
        this.codSubClasificacion = codSubClasificacion;
    }

    public String getDesSubClasificacion() {
        return desSubClasificacion;
    }

    public void setDesSubClasificacion(String desSubClasificacion) {
        this.desSubClasificacion = desSubClasificacion;
    }

    public BigDecimal getCodSubSubClasificacion() {
        return codSubSubClasificacion;
    }

    public void setCodSubSubClasificacion(BigDecimal codSubSubClasificacion) {
        this.codSubSubClasificacion = codSubSubClasificacion;
    }

    public String getDesSubSubClasificacion() {
        return desSubSubClasificacion;
    }

    public void setDesSubSubClasificacion(String desSubSubClasificacion) {
        this.desSubSubClasificacion = desSubSubClasificacion;
    }

    public BigDecimal getTipoIdentificacionR() {
        return tipoIdentificacionR;
    }

    public void setTipoIdentificacionR(BigDecimal tipoIdentificacionR) {
        this.tipoIdentificacionR = tipoIdentificacionR;
    }

    public String getDescTipoIdentificacionR() {
        return descTipoIdentificacionR;
    }

    public void setDescTipoIdentificacionR(String descTipoIdentificacionR) {
        this.descTipoIdentificacionR = descTipoIdentificacionR;
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

    public String getDescCargoR() {
        return descCargoR;
    }

    public void setDescCargoR(String descCargoR) {
        this.descCargoR = descCargoR;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }    
    
}
