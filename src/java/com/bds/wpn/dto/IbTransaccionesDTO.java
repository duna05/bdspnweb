/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

/**
 *
 * @author juan.faneite
 */
public class IbTransaccionesDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private String nombre;
    private BigInteger posicion;
    private Character opcional;
    private Character estatus;
    private Collection<IbAfiliacionesDTO> ibAfiliacionesDTOCollection;
    private Collection<IbLogsDTO> ibLogsDTOCollection;
    //private Collection<IbTransUsuariosJuridicosDTO> ibTransUsuariosJuridicosDTOCollection;
    private Collection<IbTransaccionesPerfilesDTO> ibTransaccionesPerfilesDTOCollection;
    private IbModulosDTO idModuloDTO;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto
    private Character poseeOtp;
    private Character poseeBeneficiario;
    private BigDecimal idCore;

    /**
     * 
     * @return indica el id de la transaccion en el core
     */
    public BigDecimal getIdCore() {
        return idCore;
    }

    /**
     * 
     * @param idCore indica el id de la transaccion en el core
     */
    public void setIdCore(BigDecimal idCore) {
        this.idCore = idCore;
    }    
    

    /**
     * 
     * @return indica si el modulo posee otp "1" si, "0" no
     */
    public Character getPoseeOtp() {
        return poseeOtp;
    }

    /**
     * 
     * @param poseeOtp indica si el modulo posee otp "1" si, "0" no
     */
    public void setPoseeOtp(Character poseeOtp) {
        this.poseeOtp = poseeOtp;
    }

    public Character getPoseeBeneficiario() {
        return poseeBeneficiario;
    }

    public void setPoseeBeneficiario(Character poseeBeneficiario) {
        this.poseeBeneficiario = poseeBeneficiario;
    }

    /**
     * Obtener respuesta
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
     * @param id 
     */
    public void setId(BigDecimal id) {
        this.id = id;
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
     * Obtener posicion
     * @return BigInteger
     */
    public BigInteger getPosicion() {
        return posicion;
    }

    /**
     * Asignar posicion
     * @param posicion BigInteger
     */
    public void setPosicion(BigInteger posicion) {
        this.posicion = posicion;
    }

    /**
     * Obtener opcional
     * @return Character
     */
    public Character getOpcional() {
        return opcional;
    }

    /**
     * Asignar opcional
     * @param opcional Character
     */
    public void setOpcional(Character opcional) {
        this.opcional = opcional;
    }

    /**
     * Obtener estatus
     * @return Character
     */
    public Character getEstatus() {
        return estatus;
    }

    /**
     * Asignar estatus
     * @param estatus Character
     */
    public void setEstatus(Character estatus) {
        this.estatus = estatus;
    }

    /**
     * Obtener listado de afiliaciones
     * @return Collection<IbAfiliacionesDTO>
     */
    public Collection<IbAfiliacionesDTO> getIbAfiliacionesDTOCollection() {
        return ibAfiliacionesDTOCollection;
    }

    /**
     * Asignar listado de afiliaciones
     * @param ibAfiliacionesDTOCollection Collection<IbAfiliacionesDTO>
     */
    public void setIbAfiliacionesDTOCollection(Collection<IbAfiliacionesDTO> ibAfiliacionesDTOCollection) {
        this.ibAfiliacionesDTOCollection = ibAfiliacionesDTOCollection;
    }

    /**
     * Obtener listado de logs
     * @return Collection<IbLogsDTO>
     */
    public Collection<IbLogsDTO> getIbLogsDTOCollection() {
        return ibLogsDTOCollection;
    }

    /**
     * Asignar listado de logs
     * @param ibLogsDTOCollection Collection<IbLogsDTO>
     */
    public void setIbLogsDTOCollection(Collection<IbLogsDTO> ibLogsDTOCollection) {
        this.ibLogsDTOCollection = ibLogsDTOCollection;
    }

//    /**
//     * Obtener listado de transacciones de usuarios juridicos
//     * @return Collection<IbTransUsuariosJuridicosDTO>
//     */
//    public Collection<IbTransUsuariosJuridicosDTO> getIbTransUsuariosJuridicosDTOCollection() {
//        return ibTransUsuariosJuridicosDTOCollection;
//    }
//
//    /**
//     * Asignar listado de transacciones de usuarios juridicos
//     * @param ibTransUsuariosJuridicosDTOCollection Collection<IbTransUsuariosJuridicosDTO>
//     */
//    public void setIbTransUsuariosJuridicosDTOCollection(Collection<IbTransUsuariosJuridicosDTO> ibTransUsuariosJuridicosDTOCollection) {
//        this.ibTransUsuariosJuridicosDTOCollection = ibTransUsuariosJuridicosDTOCollection;
//    }

    /**
     * Obtener listado de transacciones de perfiles
     * @return Collection<IbTransaccionesPerfilesDTO>
     */
    public Collection<IbTransaccionesPerfilesDTO> getIbTransaccionesPerfilesDTOCollection() {
        return ibTransaccionesPerfilesDTOCollection;
    }

    /**
     * Asignar listado de transacciones de perfiles
     * @param ibTransaccionesPerfilesDTOCollection Collection<IbTransaccionesPerfilesDTO>
     */
    public void setIbTransaccionesPerfilesDTOCollection(Collection<IbTransaccionesPerfilesDTO> ibTransaccionesPerfilesDTOCollection) {
        this.ibTransaccionesPerfilesDTOCollection = ibTransaccionesPerfilesDTOCollection;
    }

    /**
     * Obtener modulo
     * @return IbModulosDTO
     */
    public IbModulosDTO getIdModuloDTO() {
        return idModuloDTO;
    }

    /**
     * Asignar modulo
     * @param idModuloDTO IbModulosDTO
     */
    public void setIdModuloDTO(IbModulosDTO idModuloDTO) {
        this.idModuloDTO = idModuloDTO;
    }
    
    /**
     * metodo que retorna el id conun caracter especial para agregarlo al identificador del menu
     * @return 
     */
    public String getIdMenu() {
       return ("_"+id.toString());
   }

}
