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
public class FichaClienteComplementarioDTO extends BDSUtil implements Serializable{   
    
    private BigDecimal codigoNacionalidad;    
    private BigDecimal cantidadSucursales;
    private BigDecimal cantidadEmpleados;
    private BigDecimal codigoActividadEconomica;    
    private String facturacionMensual;
    private String facturacioUltima;
    private String personaContactar;
    private BigDecimal cargoPersonaContacto;
    private BigDecimal codigoSegmento;   
    private String inicioSociedad;      
    private String finSociedad;
    private BigDecimal codigoPaisLegal;
    private BigDecimal codigoDepartamento;
    private BigDecimal codigoMunicipio;
    private BigDecimal capitalSocial;
    private BigDecimal capitalPagado;
    private BigDecimal capitalSuscrito;
    private BigDecimal reservas;
    private String objetivoSocial;
    private String limitacionEstatutaria;
    private String fechaAumentoCapital;
    private BigDecimal valorAumentoCapital;
    private String tipoCompania;
    private BigDecimal numeroRegistro;
    private String letraTomo;
    private BigDecimal tomo;
    private String protocolo;
    private String fechaRegistro;
    private String ciudad;
    private String motivo;
    private String mov_dep;
    private String mov_ret;
    private String env_transf;
    private String rec_transf;
    private BigDecimal dep_efect;
    private BigDecimal dep_cheq_per;
    private BigDecimal dep_cheq_ger;
    private BigDecimal dep_transf;
    private BigDecimal ret_efec;
    private BigDecimal ret_cheq_per;
    private BigDecimal ret_cheq_ger;
    private BigDecimal ret_transf;
    private String uso_cta_prestamo;
    private String uso_cta_ahorro;
    private String uso_divisa;
    private String uso_transferencia;
    private String uso_otros;
    private String pais_recibir;
    private String pais_enviar; //HACE FALTA CREAR ESTE CAMPO EN EL SERVICIO****    
    private String descNacionalidad;
    private String actividadEconomica;
    private String segmento;
    private String calificacionCliente;
    private String tiposCalificacion;
    private String duracion; 
    private String comentariosOtros;
    private String nombreEstado;
    private String nombreMunicipio;

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    public void setNombreMunicipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }
    
    
   
    private RespuestaDTO respuesta;

    public BigDecimal getCodigoNacionalidad() {
        return codigoNacionalidad;
    }

    public String getComentariosOtros() {
        return comentariosOtros;
    }

    public void setComentariosOtros(String comentariosOtros) {
        this.comentariosOtros = comentariosOtros;
    }
    
    

    public void setCodigoNacionalidad(BigDecimal codigoNacionalidad) {
        this.codigoNacionalidad = codigoNacionalidad;
    }

    public BigDecimal getCantidadSucursales() {
        return cantidadSucursales;
    }

    public void setCantidadSucursales(BigDecimal cantidadSucursales) {
        this.cantidadSucursales = cantidadSucursales;
    }

    public BigDecimal getCantidadEmpleados() {
        return cantidadEmpleados;
    }

    public void setCantidadEmpleados(BigDecimal cantidadEmpleados) {
        this.cantidadEmpleados = cantidadEmpleados;
    }

    public BigDecimal getCodigoActividadEconomica() {
        return codigoActividadEconomica;
    }

    public void setCodigoActividadEconomica(BigDecimal codigoActividadEconomica) {
        this.codigoActividadEconomica = codigoActividadEconomica;
    }

    public String getFacturacionMensual() {
        return facturacionMensual;
    }

    public void setFacturacionMensual(String facturacionMensual) {
        this.facturacionMensual = facturacionMensual;
    }

    public String getFacturacioUltima() {
        return facturacioUltima;
    }

    public void setFacturacioUltima(String facturacioUltima) {
        this.facturacioUltima = facturacioUltima;
    }

    public String getPersonaContactar() {
        return personaContactar;
    }

    public void setPersonaContactar(String personaContactar) {
        this.personaContactar = personaContactar;
    }

    public BigDecimal getCargoPersonaContacto() {
        return cargoPersonaContacto;
    }

    public void setCargoPersonaContacto(BigDecimal cargoPersonaContacto) {
        this.cargoPersonaContacto = cargoPersonaContacto;
    }

    public BigDecimal getCodigoSegmento() {
        return codigoSegmento;
    }

    public void setCodigoSegmento(BigDecimal codigoSegmento) {
        this.codigoSegmento = codigoSegmento;
    }

    public String getInicioSociedad() {
        return inicioSociedad;
    }

    public void setInicioSociedad(String inicioSociedad) {
        this.inicioSociedad = inicioSociedad;
    }

    public String getFinSociedad() {
        return finSociedad;
    }

    public void setFinSociedad(String finSociedad) {
        this.finSociedad = finSociedad;
    }

    public BigDecimal getCodigoPaisLegal() {
        return codigoPaisLegal;
    }

    public void setCodigoPaisLegal(BigDecimal codigoPaisLegal) {
        this.codigoPaisLegal = codigoPaisLegal;
    }

    public BigDecimal getCodigoDepartamento() {
        return codigoDepartamento;
    }

    public void setCodigoDepartamento(BigDecimal codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
    }

    public BigDecimal getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(BigDecimal codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public BigDecimal getCapitalSocial() {
        return capitalSocial;
    }

    public void setCapitalSocial(BigDecimal capitalSocial) {
        this.capitalSocial = capitalSocial;
    }

    public BigDecimal getCapitalPagado() {
        return capitalPagado;
    }

    public void setCapitalPagado(BigDecimal capitalPagado) {
        this.capitalPagado = capitalPagado;
    }

    public BigDecimal getCapitalSuscrito() {
        return capitalSuscrito;
    }

    public void setCapitalSuscrito(BigDecimal capitalSuscrito) {
        this.capitalSuscrito = capitalSuscrito;
    }

    public BigDecimal getReservas() {
        return reservas;
    }

    public void setReservas(BigDecimal reservas) {
        this.reservas = reservas;
    }

    public String getObjetivoSocial() {
        return objetivoSocial;
    }

    public void setObjetivoSocial(String objetivoSocial) {
        this.objetivoSocial = objetivoSocial;
    }

    public String getLimitacionEstatutaria() {
        return limitacionEstatutaria;
    }

    public void setLimitacionEstatutaria(String limitacionEstatutaria) {
        this.limitacionEstatutaria = limitacionEstatutaria;
    }

    public String getFechaAumentoCapital() {
        return fechaAumentoCapital;
    }

    public void setFechaAumentoCapital(String fechaAumentoCapital) {
        this.fechaAumentoCapital = fechaAumentoCapital;
    }

    public BigDecimal getValorAumentoCapital() {
        return valorAumentoCapital;
    }

    public void setValorAumentoCapital(BigDecimal valorAumentoCapital) {
        this.valorAumentoCapital = valorAumentoCapital;
    }

    public String getTipoCompania() {
        return tipoCompania;
    }

    public void setTipoCompania(String tipoCompania) {
        this.tipoCompania = tipoCompania;
    }

    public BigDecimal getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(BigDecimal numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getLetraTomo() {
        return letraTomo;
    }

    public void setLetraTomo(String letraTomo) {
        this.letraTomo = letraTomo;
    }

    public BigDecimal getTomo() {
        return tomo;
    }

    public void setTomo(BigDecimal tomo) {
        this.tomo = tomo;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getMov_dep() {
        return mov_dep;
    }

    public void setMov_dep(String mov_dep) {
        this.mov_dep = mov_dep;
    }

    public String getMov_ret() {
        return mov_ret;
    }

    public void setMov_ret(String mov_ret) {
        this.mov_ret = mov_ret;
    }

    public String getEnv_transf() {
        return env_transf;
    }

    public void setEnv_transf(String env_transf) {
        this.env_transf = env_transf;
    }

    public String getRec_transf() {
        return rec_transf;
    }

    public void setRec_transf(String rec_transf) {
        this.rec_transf = rec_transf;
    }

    public BigDecimal getDep_efect() {
        return dep_efect;
    }

    public void setDep_efect(BigDecimal dep_efect) {
        this.dep_efect = dep_efect;
    }

    public BigDecimal getDep_cheq_per() {
        return dep_cheq_per;
    }

    public void setDep_cheq_per(BigDecimal dep_cheq_per) {
        this.dep_cheq_per = dep_cheq_per;
    }

    public BigDecimal getDep_cheq_ger() {
        return dep_cheq_ger;
    }

    public void setDep_cheq_ger(BigDecimal dep_cheq_ger) {
        this.dep_cheq_ger = dep_cheq_ger;
    }

    public BigDecimal getDep_transf() {
        return dep_transf;
    }

    public void setDep_transf(BigDecimal dep_transf) {
        this.dep_transf = dep_transf;
    }

    public BigDecimal getRet_efec() {
        return ret_efec;
    }

    public void setRet_efec(BigDecimal ret_efec) {
        this.ret_efec = ret_efec;
    }

    public BigDecimal getRet_cheq_per() {
        return ret_cheq_per;
    }

    public void setRet_cheq_per(BigDecimal ret_cheq_per) {
        this.ret_cheq_per = ret_cheq_per;
    }

    public BigDecimal getRet_cheq_ger() {
        return ret_cheq_ger;
    }

    public void setRet_cheq_ger(BigDecimal ret_cheq_ger) {
        this.ret_cheq_ger = ret_cheq_ger;
    }

    public BigDecimal getRet_transf() {
        return ret_transf;
    }

    public void setRet_transf(BigDecimal ret_transf) {
        this.ret_transf = ret_transf;
    }

    public String getUso_cta_prestamo() {
        return uso_cta_prestamo;
    }

    public void setUso_cta_prestamo(String uso_cta_prestamo) {
        this.uso_cta_prestamo = uso_cta_prestamo;
    }

    public String getUso_cta_ahorro() {
        return uso_cta_ahorro;
    }

    public void setUso_cta_ahorro(String uso_cta_ahorro) {
        this.uso_cta_ahorro = uso_cta_ahorro;
    }

    public String getUso_divisa() {
        return uso_divisa;
    }

    public void setUso_divisa(String uso_divisa) {
        this.uso_divisa = uso_divisa;
    }

    public String getUso_transferencia() {
        return uso_transferencia;
    }

    public void setUso_transferencia(String uso_transferencia) {
        this.uso_transferencia = uso_transferencia;
    }

    public String getUso_otros() {
        return uso_otros;
    }

    public void setUso_otros(String uso_otros) {
        this.uso_otros = uso_otros;
    }

    public String getPais_recibir() {
        return pais_recibir;
    }

    public void setPais_recibir(String pais_recibir) {
        this.pais_recibir = pais_recibir;
    }

    public String getPais_enviar() {
        return pais_enviar;
    }

    public void setPais_enviar(String pais_enviar) {
        this.pais_enviar = pais_enviar;
    }

    public String getDescNacionalidad() {
        return descNacionalidad;
    }

    public void setDescNacionalidad(String descNacionalidad) {
        this.descNacionalidad = descNacionalidad;
    }

    public String getActividadEconomica() {
        return actividadEconomica;
    }

    public void setActividadEconomica(String actividadEconomica) {
        this.actividadEconomica = actividadEconomica;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public String getCalificacionCliente() {
        return calificacionCliente;
    }

    public void setCalificacionCliente(String calificacionCliente) {
        this.calificacionCliente = calificacionCliente;
    }

    public String getTiposCalificacion() {
        return tiposCalificacion;
    }

    public void setTiposCalificacion(String tiposCalificacion) {
        this.tiposCalificacion = tiposCalificacion;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public RespuestaDTO getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaDTO respuesta) {
        this.respuesta = respuesta;
    }
    
    
}
