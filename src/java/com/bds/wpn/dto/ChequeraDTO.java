package com.bds.wpn.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Clase ChequeraDTO
 *
 * @author rony.rodriguez
 */
public class ChequeraDTO implements Serializable {

    private String activa;
    private String agenciaOrigen;
    private String cantidadCheques;
    private String chequeFinal;
    private String chequePago;
    private String agencia;
    private String aplicacion;
    private String empresa;
    private String subAplicacion;
    private String codigoUsuario;
    private Date fechaEmisionDate;
    private Date fechaEntregaDate;
    private String numeroCuenta;
    private String numeroPrimerCheque;
    private String numeroUltimoCheque;
    private String secuenciaSolicitud;
    private String valorChequera;
    private Collection<ChequeDTO> chequesDTO;
    
    private RespuestaDTO respuestaDTO;

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuesta) {
        this.respuestaDTO = respuesta;
    }

    /**
     * 
     * @return Listado de cheques
     */
    public Collection<ChequeDTO> getChequesDTO() {
        return chequesDTO;
    }

    /**
     * 
     * @param cheques Listado de cheques
     */
    public void setChequesDTO(Collection<ChequeDTO> cheques) {
        this.chequesDTO = cheques;
    }
    
    /**
     * 
     * @return Estatus de Chequera
     */
    public String getActiva() {
        return activa;
    }

    /**
     * 
     * @param activa Estatus de Chequera
     */
    public void setActiva(String activa) {
        this.activa = activa;
    }

    /**
     * 
     * @return Agencia Origen
     */
    public String getAgenciaOrigen() {
        return agenciaOrigen;
    }

    /**
     * 
     * @param agenciaOrigen Agencia Origen
     */
    public void setAgenciaOrigen(String agenciaOrigen) {
        this.agenciaOrigen = agenciaOrigen;
    }

    /**
     * 
     * @return Cantidad de Cheques
     */
    public String getCantidadCheques() {
        return cantidadCheques;
    }

    /**
     * 
     * @param cantidadCheques Cantidad de Cheques
     */
    public void setCantidadCheques(String cantidadCheques) {
        this.cantidadCheques = cantidadCheques;
    }

    /**
     * 
     * @return Numero de Cheque Final
     */
    public String getChequeFinal() {
        return chequeFinal;
    }

    /**
     * 
     * @param chequeFinal Numero de Cheque Final
     */
    public void setChequeFinal(String chequeFinal) {
        this.chequeFinal = chequeFinal;
    }

    /**
     * 
     * @return Numero de Cheque Pago
     */
    public String getChequePago() {
        return chequePago;
    }

    /**
     * 
     * @param chequePago Cheque Pago
     */
    public void setChequePago(String chequePago) {
        this.chequePago = chequePago;
    }
    
    /**
     * 
     * @return Agencia
     */
    public String getAgencia() {
        return agencia;
    }

    /**
     * 
     * @param agencia Agencia
     */
    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    /**
     * 
     * @return Aplicacion
     */
    public String getAplicacion() {
        return aplicacion;
    }

    /**
     * 
     * @param aplicacion Aplicacion
     */
    public void setAplicacion(String aplicacion) {
        this.aplicacion = aplicacion;
    }

    /**
     * 
     * @return Empresa
     */
    public String getEmpresa() {
        return empresa;
    }

    /**
     * 
     * @param empresa Empresa
     */
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    /**
     * 
     * @return SubAplicacion
     */
    public String getSubAplicacion() {
        return subAplicacion;
    }

    /**
     * 
     * @param subAplicacion SubAplicacion
     */
    public void setSubAplicacion(String subAplicacion) {
        this.subAplicacion = subAplicacion;
    }

    /**
     * 
     * @return Codigo de Usuario
     */
    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    /**
     * 
     * @param codigoUsuario Codigo de Usuario
     */
    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    /**
     * 
     * @return Fecha de Emision
     */
    public Date getFechaEmisionDate() {
        return fechaEmisionDate;
    }

    /**
     * 
     * @param fechaEmision Fecha de Emision
     */
    public void setFechaEmisionDate(Date fechaEmision) {
        this.fechaEmisionDate = fechaEmision;
    }

    /**
     * 
     * @return Fecha de Entrega
     */
    public Date getFechaEntregaDate() {
        return fechaEntregaDate;
    }

    /**
     * 
     * @param fechaEntrega Fecha de Entrega
     */
    public void setFechaEntregaDate(Date fechaEntrega) {
        this.fechaEntregaDate = fechaEntrega;
    }

    /**
     * 
     * @return Numero de Cuenta
     */
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    /**
     * 
     * @param numeroCuenta Numero de Cuenta
     */
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    /**
     * 
     * @return Numero del Primer Cheque
     */
    public String getNumeroPrimerCheque() {
        return numeroPrimerCheque;
    }

    /**
     * 
     * @param numeroPrimerCheque Numero del Primer Cheque
     */
    public void setNumeroPrimerCheque(String numeroPrimerCheque) {
        this.numeroPrimerCheque = numeroPrimerCheque;
    }

    /**
     * 
     * @return Numero del Ultimo Cheque
     */
    public String getNumeroUltimoCheque() {
        return numeroUltimoCheque;
    }

    /**
     * 
     * @param numeroUltimoCheque Numero del Ultimo Cheque
     */
    public void setNumeroUltimoCheque(String numeroUltimoCheque) {
        this.numeroUltimoCheque = numeroUltimoCheque;
    }

    /**
     * 
     * @return Numero de la Scuencia de la Solicitud
     */
    public String getSecuenciaSolicitud() {
        return secuenciaSolicitud;
    }

    /**
     * 
     * @param secuenciaSolicitud Numero de la Scuencia de la Solicitud
     */
    public void setSecuenciaSolicitud(String secuenciaSolicitud) {
        this.secuenciaSolicitud = secuenciaSolicitud;
    }

    /**
     * 
     * @return Valor de Chequera
     */
    public String getValorChequera() {
        return valorChequera;
    }

    /**
     * 
     * @param valorChequera Valor de Chequera
     */
    public void setValorChequera(String valorChequera) {
        this.valorChequera = valorChequera;
    }
    
    

}