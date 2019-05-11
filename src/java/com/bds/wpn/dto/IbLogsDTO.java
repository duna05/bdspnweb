/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import static com.bds.wpn.util.BDSUtil.formatearMonto;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 *
 * @author juan.faneite
 */
public class IbLogsDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private Date fechaHoraDate;
    private String cuentaOrigen;
    private String cuentaDestino;
    private BigDecimal monto;
    private String referencia;
    private String descripcion;
    private String ip;
    private String userAgent;
    private BigInteger errorOperacion;
    private String nombreBeneficiario;
    private Character tipoDoc;
    private String documento;
    private Date fechaHoraTxDate;
    private Date fechaHoraJobDate;
    private IbUsuariosDTO idUsuarioDTO;
    private BigDecimal idTransaccion;
    private IbTransaccionesDTO idTransaccionDTO;
    private IbCanalDTO idCanalDTO;
    private IbAfiliacionesDTO idAfiliacionDTO;
    private List<IbLogsDTO> ibLogsDTO;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto
    
    private String montoTransaccion;

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
     * Asinar id
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Obtener fecha hora
     * @return 
     */
    public Date getFechaHoraDate() {
        return fechaHoraDate;
    }

    /**
     * Asignar fecha hora
     * @param fechaHora 
     */
    public void setFechaHoraDate(Date fechaHoraDate) {
        this.fechaHoraDate = fechaHoraDate;
    }

    /**
     * Asginar cuenta de origgen
     * @return String
     */
    public String getCuentaOrigen() {
        return cuentaOrigen;
    }

    /**
     * Asignar cuenta origen
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
     * @param cuentaDestino 
     */
    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    /**
     * Obtener monto
     * @return BigDecimal
     */
    public BigDecimal getMonto() {
        return monto;
    }

    /**
     * Asinar monto
     * @param monto BigDecimal
     */
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    /**
     * Obtener referencia
     * @return String
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * Asignar referencia
     * @param referencia String
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    /**
     * Obtener descripcion
     * @return String
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Asinar descripcion
     * @param descripcion String
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtener ip
     * @return String
     */
    public String getIp() {
        return ip;
    }

    /**
     * Asignar IP
     * @param ip String
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Obtener user agent
     * @return String
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Asinar user agent
     * @param userAgent String
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Obtener error de operacion
     * @return 
     */
    public BigInteger getErrorOperacion() {
        return errorOperacion;
    }

    /**
     * Asinar error de operacion
     * @param errorOperacion BigInteger
     */
    public void setErrorOperacion(BigInteger errorOperacion) {
        this.errorOperacion = errorOperacion;
    }

    /**
     * Obtener nombre del beneficiario
     * @return 
     */
    public String getNombreBeneficiario() {
        return nombreBeneficiario;
    }

    /**
     * Asignar nombre del beneficiario
     * @param nombreBeneficiario 
     */
    public void setNombreBeneficiario(String nombreBeneficiario) {
        this.nombreBeneficiario = nombreBeneficiario;
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
     * Obtener fecha hora tx
     * @return Date
     */
    public Date getFechaHoraTxDate() {
        return fechaHoraTxDate;
    }

    /**
     * Asignar fecha hora tx
     * @param fechaHoraTxDate Date
     */
    public void setFechaHoraTxDate(Date fechaHoraTxDate) {
        this.fechaHoraTxDate = fechaHoraTxDate;
    }

    /**
     * Asinar fecha hora job
     * @return Date
     */
    public Date getFechaHoraJobDate() {
        return fechaHoraJobDate;
    }

    /**
     * Asignar fecha hora
     * @param fechaHoraJobDate Date
     */
    public void setFechaHoraJobDate(Date fechaHoraJobDate) {
        this.fechaHoraJobDate = fechaHoraJobDate;
    }

    /**
     * Asinar el usuario
     * @return IbUsuariosDTO
     */
    public IbUsuariosDTO getIdUsuarioDTO() {
        return idUsuarioDTO;
    }

    /**
     * Asignar el usuario
     * @param idUsuarioDTO IbUsuariosDTO
     */
    public void setIdUsuarioDTO(IbUsuariosDTO idUsuarioDTO) {
        this.idUsuarioDTO = idUsuarioDTO;
    }

    /**
     * Obtener la transaccion
     * @return IbTransaccionesDTO
     */
    public IbTransaccionesDTO getIdTransaccionDTO() {
        return idTransaccionDTO;
    }

    /**
     * Asignar transaccion
     * @param idTransaccionDTO IbTransaccionesDTO
     */
    public void setIdTransaccionDTO(IbTransaccionesDTO idTransaccionDTO) {
        this.idTransaccionDTO = idTransaccionDTO;
    }

    /**
     * Obtener canal
     * @return IbCanalDTO
     */
    public IbCanalDTO getIdCanalDTO() {
        return idCanalDTO;
    }

    /**
     * asinar canal
     * @param idCanalDTO 
     */
    public void setIdCanalDTO(IbCanalDTO idCanalDTO) {
        this.idCanalDTO = idCanalDTO;
    }

    /**
     * Obtener afiliacion
     * @return IbAfiliacionesDTO
     */
    public IbAfiliacionesDTO getIdAfiliacionDTO() {
        return idAfiliacionDTO;
    }

    /**
     * Asignar afiliacion
     * @param idAfiliacionDTO IbAfiliacionesDTO
     */
    public void setIdAfiliacionDTO(IbAfiliacionesDTO idAfiliacionDTO) {
        this.idAfiliacionDTO = idAfiliacionDTO;
    }

    public List<IbLogsDTO> getIbLogsDTO() {
        return ibLogsDTO;
    }

    public void setIbLogsDTO(List<IbLogsDTO> ibLogsDTO) {
        this.ibLogsDTO = ibLogsDTO;
    }

    public String getMontoTransaccion() {
        
         if (monto != null) {
                montoTransaccion = formatearMonto(monto);
            } else {
                montoTransaccion = " - ";
            }
        
        return montoTransaccion;
    }

    public void setMontoTransaccion(String montoTransaccion) {
        this.montoTransaccion = montoTransaccion;
    }

    public BigDecimal getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(BigDecimal idTransaccion) {
        this.idTransaccion = idTransaccion;
    }    
    
}
