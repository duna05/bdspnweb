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
import java.util.Date;

/**
 *
 * @author juan.faneite
 */
public class IbTransaccionesPendientesDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private Character tipo;
    private Character estatus;
    private BigInteger lote;
    private Date fechaHoraRegistro;
    private Date fechaHoraValor;
    private BigDecimal monto;
    private BigInteger aprobacionesRequeridas;
    private Character tipoDoc;
    private String documento;
    private String cuentaOrigen;
    private String cuentaDestino;
    private String descripcion;
    private Character tipoIdBeneficiario;
    private BigInteger idBeneficiario;
    private String nombreBeneficiario;
    private String emailBeneficiario;
    private Character tipoCarga;
    private Date fechaEjecucion;
    private Collection<IbAprobacionesTransaccionesDTO> ibAprobacionesTransaccionesDTOCollection;
    private IbUsuariosDTO idUsuarioDTO;
    private Collection<IbTransaccionesBeneficiariosDTO> ibTransaccionesBeneficiariosDTOCollection;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto

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
     * Retornar id
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
     * Obtener lote
     * @return BigInteger
     */
    public BigInteger getLote() {
        return lote;
    }

    /**
     * Asignar lote
     * @param lote BigInteger
     */
    public void setLote(BigInteger lote) {
        this.lote = lote;
    }

    /**
     * Obtener fecha hora registro
     * @return Date
     */
    public Date getFechaHoraRegistro() {
        return fechaHoraRegistro;
    }

    /**
     * Asignar fecha hora de registro
     * @param fechaHoraRegistro Date
     */
    public void setFechaHoraRegistro(Date fechaHoraRegistro) {
        this.fechaHoraRegistro = fechaHoraRegistro;
    }

    /**
     * Obtener fecha hora valor
     * @return Date
     */
    public Date getFechaHoraValor() {
        return fechaHoraValor;
    }

    /**
     * Asignar fecha hora valor
     * @param fechaHoraValor Date
     */
    public void setFechaHoraValor(Date fechaHoraValor) {
        this.fechaHoraValor = fechaHoraValor;
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
     * Obtener aprobaciones requeridas
     * @return BigInteger
     */
    public BigInteger getAprobacionesRequeridas() {
        return aprobacionesRequeridas;
    }

    /**
     * Asignar  aprobaciones requeridas
     * @param aprobacionesRequeridas BigInteger
     */
    public void setAprobacionesRequeridas(BigInteger aprobacionesRequeridas) {
        this.aprobacionesRequeridas = aprobacionesRequeridas;
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
     * Obtener cuenta de origen
     * @return String
     */
    public String getCuentaOrigen() {
        return cuentaOrigen;
    }

    /**
     * Asignar cuenta de origen
     * @param cuentaOrigen String
     */
    public void setCuentaOrigen(String cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }

    /**
     * Obtener cuenta destino
     * @return String
     */
    public String getCuentaDestino() {
        return cuentaDestino;
    }

    /**
     * Asignar cuenta destino
     * @param cuentaDestino String
     */
    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    /**
     * Obtener descripcion
     * @return String
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Asignar descripcion
     * @param descripcion String
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtener tipo beneficiario
     * @return Character
     */
    public Character getTipoIdBeneficiario() {
        return tipoIdBeneficiario;
    }

    /**
     * Asignar asignar tipo beneficiario
     * @param tipoIdBeneficiario Character
     */
    public void setTipoIdBeneficiario(Character tipoIdBeneficiario) {
        this.tipoIdBeneficiario = tipoIdBeneficiario;
    }

    /**
     * Obtener beneficiario
     * @return BigInteger
     */
    public BigInteger getIdBeneficiario() {
        return idBeneficiario;
    }

    /**
     * Asignar beneficiario
     * @param idBeneficiario BigInteger
     */
    public void setIdBeneficiario(BigInteger idBeneficiario) {
        this.idBeneficiario = idBeneficiario;
    }

    /**
     * Obtener nombre beneficiario
     * @return String
     */
    public String getNombreBeneficiario() {
        return nombreBeneficiario;
    }

    /**
     *Asignar nombre beneficiario
     * @param nombreBeneficiario String
     */
    public void setNombreBeneficiario(String nombreBeneficiario) {
        this.nombreBeneficiario = nombreBeneficiario;
    }

    /**
     * Obtener email beneficiario
     * @return String
     */
    public String getEmailBeneficiario() {
        return emailBeneficiario;
    }

    /**
     * Asignar email de beneficiario
     * @param emailBeneficiario String
     */
    public void setEmailBeneficiario(String emailBeneficiario) {
        this.emailBeneficiario = emailBeneficiario;
    }

    /**
     * Obtener tipo carga
     * @return Character
     */
    public Character getTipoCarga() {
        return tipoCarga;
    }

    /**
     * Asignar tipo carga
     * @param tipoCarga Character
     */
    public void setTipoCarga(Character tipoCarga) {
        this.tipoCarga = tipoCarga;
    }

    /**
     * Obtener fecha de ejecucion
     * @return Date
     */
    public Date getFechaEjecucion() {
        return fechaEjecucion;
    }

    /**
     * Asignar fecha de ejecucion
     * @param fechaEjecucion Date
     */
    public void setFechaEjecucion(Date fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    /**
     * Obtener listado de transacciones de aprobaciones
     * @return Collection<IbAprobacionesTransaccionesDTO>
     */
    public Collection<IbAprobacionesTransaccionesDTO> getIbAprobacionesTransaccionesDTOCollection() {
        return ibAprobacionesTransaccionesDTOCollection;
    }

    /**
     * Asignar listado de transacciones de aprobaciones
     * @param ibAprobacionesTransaccionesDTOCollection
     * Collection<IbAprobacionesTransaccionesDTO>
     */
    public void setIbAprobacionesTransaccionesDTOCollection(Collection<IbAprobacionesTransaccionesDTO> ibAprobacionesTransaccionesDTOCollection) {
        this.ibAprobacionesTransaccionesDTOCollection = ibAprobacionesTransaccionesDTOCollection;
    }

    /**
     * Obtener usuario
     * @return IbUsuariosDTO
     */
    public IbUsuariosDTO getIdUsuarioDTO() {
        return idUsuarioDTO;
    }

    /**
     * Asignar usuario
     * @param idUsuarioDTO IbUsuariosDTO
     */
    public void setIdUsuarioDTO(IbUsuariosDTO idUsuarioDTO) {
        this.idUsuarioDTO = idUsuarioDTO;
    }

    /**
     * Obtener listado de transacciones de los beneficiarios
     * @return Collection<IbTransaccionesBeneficiariosDTO>
     */
    public Collection<IbTransaccionesBeneficiariosDTO> getIbTransaccionesBeneficiariosDTOCollection() {
        return ibTransaccionesBeneficiariosDTOCollection;
    }

    /**
     * Asignar listado de transacciones de los beneficiarios
     * @param ibTransaccionesBeneficiariosDTOCollection
     * Collection<IbTransaccionesBeneficiariosDTO>
     */
    public void setIbTransaccionesBeneficiariosDTOCollection(Collection<IbTransaccionesBeneficiariosDTO> ibTransaccionesBeneficiariosDTOCollection) {
        this.ibTransaccionesBeneficiariosDTOCollection = ibTransaccionesBeneficiariosDTOCollection;
    }

}
