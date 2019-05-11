/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author juan.faneite
 */
public class IbUsuariosDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private String nombre;
    private Character tipoDoc;
    private String documento;
    private String tdd;
    private String email;
    private String celular;
    private Date fechaHoraCreacionDate;
    private Date fechaHoraModificacionDate;
    private String codUsuario;
    private String clave;
    private Collection<IbAcumuladoTransaccionDTO> ibAcumuladoTransaccionDTOCollection;
    private Collection<IbAprobacionesTransaccionesDTO> ibAprobacionesTransaccionesDTOCollection;
    private Collection<IbAfiliacionesDTO> ibAfiliacionesDTOCollection;
    private Collection<IbLogsDTO> ibLogsDTOCollection;
    private Collection<IbTransaccionesPendientesDTO> ibTransaccionesPendientesDTOCollection;
    //private Collection<IbTransUsuariosJuridicosDTO> ibTransUsuariosJuridicosDTOCollection;
    private Collection<IbMensajesUsuariosDTO> ibMensajesUsuariosDTOCollection;
    //private IbRolesJuridicosDTO idRolJuridicoDTO;
    private IbPerfilesDTO idPerfilDTO;
    private Collection<IbUsuariosCanalesDTO> ibUsuariosCanalesDTOCollection;
    private Collection<IbUsuariosPerfilPilotoDTO> ibUsuariosPerfilPilotoDTOCollection;
    private RespuestaDTO respuestaDTO;

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
     * Obtener tipo documento
     *
     * @return String
     */
    public Character getTipoDoc() {
        return tipoDoc;
    }

    /**
     * Asignar tipo documento
     *
     * @param tipoDoc Character
     */
    public void setTipoDoc(Character tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    /**
     * Asignar documento
     *
     * @return String
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * Asignar documento
     *
     * @param documento String
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    /**
     * Obtener tdd
     *
     * @return String
     */
    public String getTdd() {
        return tdd;
    }

    /**
     * Asignar tdd
     *
     * @param tdd String
     */
    public void setTdd(String tdd) {
        this.tdd = tdd;
    }

    /**
     * Obtener email
     *
     * @return String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Asignar email
     *
     * @param email String
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtener celular
     *
     * @return String
     */
    public String getCelular() {
        return celular;
    }

    /**
     * Asignar celular
     *
     * @param celular String
     */
    public void setCelular(String celular) {
        this.celular = celular;
    }

    /**
     * Obtener fecha hora creacion
     *
     * @return String
     */
    public Date getFechaHoraCreacionDate() {
        return fechaHoraCreacionDate;
    }

    /**
     * Asignar fecha hora creacion
     *
     * @param fechaHoraCreacion Date
     */
    public void setFechaHoraCreacionDate(Date fechaHoraCreacion) {
        this.fechaHoraCreacionDate = fechaHoraCreacion;
    }

    /**
     * Obtener fecha hora modificacion
     *
     * @return Date
     */
    public Date getFechaHoraModificacionDate() {
        return fechaHoraModificacionDate;
    }

    /**
     * Asignar fecha hora modificacion
     *
     * @param fechaHoraModificacion Date
     */
    public void setFechaHoraModificacionDate(Date fechaHoraModificacion) {
        this.fechaHoraModificacionDate = fechaHoraModificacion;
    }

    /**
     * Obtener codigo usuario
     *
     * @return String
     */
    public String getCodUsuario() {
        return codUsuario;
    }

    /**
     * Asignar codigo de usuario
     *
     * @param codUsuario String
     */
    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    /**
     * Obtener listado de transaccion acumulado
     *
     * @return Collection<IbAcumuladoTransaccionDTO>
     */
    public Collection<IbAcumuladoTransaccionDTO> getIbAcumuladoTransaccionDTOCollection() {
        return ibAcumuladoTransaccionDTOCollection;
    }

    /**
     * Asignar listado de transaccion acumulado
     *
     * @param ibAcumuladoTransaccionDTOCollection
     * Collection<IbAcumuladoTransaccionDTO>
     */
    public void setIbAcumuladoTransaccionDTOCollection(Collection<IbAcumuladoTransaccionDTO> ibAcumuladoTransaccionDTOCollection) {
        this.ibAcumuladoTransaccionDTOCollection = ibAcumuladoTransaccionDTOCollection;
    }

    /**
     * Obtener transacciones de las aprobaciones
     *
     * @return Collection<IbAprobacionesTransaccionesDTO>
     */
    public Collection<IbAprobacionesTransaccionesDTO> getIbAprobacionesTransaccionesDTOCollection() {
        return ibAprobacionesTransaccionesDTOCollection;
    }

    /**
     * Asignar listado de transacciones de las aprobaciones
     *
     * @param ibAprobacionesTransaccionesDTOCollection
     * Collection<IbAprobacionesTransaccionesDTO>
     */
    public void setIbAprobacionesTransaccionesDTOCollection(Collection<IbAprobacionesTransaccionesDTO> ibAprobacionesTransaccionesDTOCollection) {
        this.ibAprobacionesTransaccionesDTOCollection = ibAprobacionesTransaccionesDTOCollection;
    }

    /**
     * Obtener listado de afiliaciones
     *
     * @return Collection<IbAfiliacionesDTO>
     */
    public Collection<IbAfiliacionesDTO> getIbAfiliacionesDTOCollection() {
        return ibAfiliacionesDTOCollection;
    }

    /**
     * Asignar listado de afiliaciones
     *
     * @param ibAfiliacionesDTOCollection Collection<IbAfiliacionesDTO>
     */
    public void setIbAfiliacionesDTOCollection(Collection<IbAfiliacionesDTO> ibAfiliacionesDTOCollection) {
        this.ibAfiliacionesDTOCollection = ibAfiliacionesDTOCollection;
    }

    /**
     * Asignar listado de los logs
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
     * Obtener listado de transacciones pendientes
     *
     * @return Collection<IbTransaccionesPendientesDTO>
     */
    public Collection<IbTransaccionesPendientesDTO> getIbTransaccionesPendientesDTOCollection() {
        return ibTransaccionesPendientesDTOCollection;
    }

    /**
     * Asignar listado de transacciones pendientes
     *
     * @param ibTransaccionesPendientesDTOCollection
     * Collection<IbTransaccionesPendientesDTO>
     */
    public void setIbTransaccionesPendientesDTOCollection(Collection<IbTransaccionesPendientesDTO> ibTransaccionesPendientesDTOCollection) {
        this.ibTransaccionesPendientesDTOCollection = ibTransaccionesPendientesDTOCollection;
    }

//    /**
//     * Obtener listado de transacciones de usuarios juridicos
//     *
//     * @return Collection<IbTransUsuariosJuridicosDTO>
//     */
//    public Collection<IbTransUsuariosJuridicosDTO> getIbTransUsuariosJuridicosDTOCollection() {
//        return ibTransUsuariosJuridicosDTOCollection;
//    }
//
//    /**
//     * Asignar listado de transacciones de usuarios juridicos
//     *
//     * @param ibTransUsuariosJuridicosDTOCollection
//     * Collection<IbTransUsuariosJuridicosDTO>
//     */
//    public void setIbTransUsuariosJuridicosDTOCollection(Collection<IbTransUsuariosJuridicosDTO> ibTransUsuariosJuridicosDTOCollection) {
//        this.ibTransUsuariosJuridicosDTOCollection = ibTransUsuariosJuridicosDTOCollection;
//    }

    /**
     * Obtener listado de mensajes de usuarios
     *
     * @return Collection<IbMensajesUsuariosDTO>
     */
    public Collection<IbMensajesUsuariosDTO> getIbMensajesUsuariosDTOCollection() {
        return ibMensajesUsuariosDTOCollection;
    }

    /**
     * Asignar listado de mensajes de usuarios
     *
     * @param ibMensajesUsuariosDTOCollection Collection<IbMensajesUsuariosDTO>
     */
    public void setIbMensajesUsuariosDTOCollection(Collection<IbMensajesUsuariosDTO> ibMensajesUsuariosDTOCollection) {
        this.ibMensajesUsuariosDTOCollection = ibMensajesUsuariosDTOCollection;
    }

//    /**
//     * Obtener rol juridico
//     *
//     * @return IbRolesJuridicosDTO
//     */
//    public IbRolesJuridicosDTO getIdRolJuridicoDTO() {
//        return idRolJuridicoDTO;
//    }
//
//    /**
//     * Asignar rol juridico
//     *
//     * @param idRolJuridicoDTO IbRolesJuridicosDTO
//     */
//    public void setIdRolJuridicoDTO(IbRolesJuridicosDTO idRolJuridicoDTO) {
//        this.idRolJuridicoDTO = idRolJuridicoDTO;
//    }

    /**
     * Obtener perfil
     *
     * @return IbPerfilesDTO
     */
    public IbPerfilesDTO getIdPerfilDTO() {
        return idPerfilDTO;
    }

    /**
     * Asignar perfil
     *
     * @param idPerfilDTO IbPerfilesDTO
     */
    public void setIdPerfilDTO(IbPerfilesDTO idPerfilDTO) {
        this.idPerfilDTO = idPerfilDTO;
    }

    /**
     * Obtener listado de canales de usuarios
     *
     * @return Collection<IbUsuariosCanalesDTO>
     */
    public Collection<IbUsuariosCanalesDTO> getIbUsuariosCanalesDTOCollection() {
        return ibUsuariosCanalesDTOCollection;
    }

    /**
     * Asignar canales de usuarios
     *
     * @param ibUsuariosCanalesDTOCollection Collection<IbUsuariosCanalesDTO>
     */
    public void setIbUsuariosCanalesDTOCollection(Collection<IbUsuariosCanalesDTO> ibUsuariosCanalesDTOCollection) {
        this.ibUsuariosCanalesDTOCollection = ibUsuariosCanalesDTOCollection;
    }

    /**
     * Obtener listado de usuarios de perfil piloto
     *
     * @return Collection<IbUsuariosPerfilPilotoDTO>
     */
    public Collection<IbUsuariosPerfilPilotoDTO> getIbUsuariosPerfilPilotoDTOCollection() {
        return ibUsuariosPerfilPilotoDTOCollection;
    }

    /**
     * Asignar listado de usuarios de perfil piloto
     *
     * @param ibUsuariosPerfilPilotoDTOCollection
     * Collection<IbUsuariosPerfilPilotoDTO>
     */
    public void setIbUsuariosPerfilPilotoDTOCollection(Collection<IbUsuariosPerfilPilotoDTO> ibUsuariosPerfilPilotoDTOCollection) {
        this.ibUsuariosPerfilPilotoDTOCollection = ibUsuariosPerfilPilotoDTOCollection;
    }

    /**
     * Obtener respuesta
     *
     * @return RespuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asignar respuesta
     *
     * @param respuestaDTO RespuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }

    /**
     * Obtener clave
     *
     * @return String
     */
    public String getClave() {
        return clave;
    }

    /**
     * Asinar clave
     *
     * @param clave String
     */
    public void setClave(String clave) {
        this.clave = clave;
    }
}
