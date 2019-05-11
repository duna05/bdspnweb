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
public class IbCanalDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private String codigo;
    private String nombre;
    private Collection<IbLogsDTO> ibLogsDTOCollection;
    private Collection<IbParametrosDTO> ibParametrosDTOCollection;
    private Collection<IbModulosDTO> ibModulosDTOCollection;
    private Collection<IbUsuariosCanalesDTO> ibUsuariosCanalesDTOCollection;
    private Collection<IbCanalDTO> ibCanalDTO;
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
     * Asignar el objeto respuesta
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
     * Obtener id
     *
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Obtener codigo
     *
     * @return String
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Asignar codigo
     *
     * @param codigo String
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Obtener nombre
     *
     * @return String
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asignar nombre
     *
     * @param nombre String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtener listado de logs
     *
     * @return Collection<IbLogsDTO>
     */
    public Collection<IbLogsDTO> getIbLogsDTOCollection() {
        return ibLogsDTOCollection;
    }

    /**
     * Asignar listado de logs
     *
     * @param ibLogsDTOCollection Collection<IbLogsDTO>
     */
    public void setIbLogsDTOCollection(Collection<IbLogsDTO> ibLogsDTOCollection) {
        this.ibLogsDTOCollection = ibLogsDTOCollection;
    }

    /**
     * Obtener listado de parametros
     *
     * @return Collection<IbParametrosDTO>
     */
    public Collection<IbParametrosDTO> getIbParametrosDTOCollection() {
        return ibParametrosDTOCollection;
    }

    /**
     * Asignar listado de parametros
     *
     * @param ibParametrosDTOCollection Collection<IbParametrosDTO>
     */
    public void setIbParametrosDTOCollection(Collection<IbParametrosDTO> ibParametrosDTOCollection) {
        this.ibParametrosDTOCollection = ibParametrosDTOCollection;
    }

    /**
     * Obtener listado de modulos
     *
     * @return Collection<IbModulosDTO>
     */
    public Collection<IbModulosDTO> getIbModulosDTOCollection() {
        return ibModulosDTOCollection;
    }

    /**
     * Asinar listado de modulos
     *
     * @param ibModulosDTOCollection Collection<IbModulosDTO>
     */
    public void setIbModulosDTOCollection(Collection<IbModulosDTO> ibModulosDTOCollection) {
        this.ibModulosDTOCollection = ibModulosDTOCollection;
    }

    /**
     * Obtener listado de usuario canal
     *
     * @return Collection<IbUsuariosCanalesDTO>
     */
    public Collection<IbUsuariosCanalesDTO> getIbUsuariosCanalesDTOCollection() {
        return ibUsuariosCanalesDTOCollection;
    }

    /**
     * Asignar listado de usuario canal
     *
     * @param ibUsuariosCanalesDTOCollection Collection<IbUsuariosCanalesDTO>
     */
    public void setIbUsuariosCanalesDTOCollection(Collection<IbUsuariosCanalesDTO> ibUsuariosCanalesDTOCollection) {
        this.ibUsuariosCanalesDTOCollection = ibUsuariosCanalesDTOCollection;
    }

    public Collection<IbCanalDTO> getIbCanalDTO() {
        return ibCanalDTO;
    }

    public void setIbCanalDTO(Collection<IbCanalDTO> ibCanalDTO) {
        this.ibCanalDTO = ibCanalDTO;
    }   
    
}
