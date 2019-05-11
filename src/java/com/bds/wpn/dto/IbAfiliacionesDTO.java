/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import com.bds.wpn.util.DESedeEncryption;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * @author juan.faneite
 */
public class IbAfiliacionesDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private String alias;
    private String numeroCuenta;
    private String nombreBeneficiario;
    private Character tipoDoc;
    private String documento;
    private String email;
    private BigDecimal montoMaximo;
    private String codUsuario;
    private Date fechaRegistroDate;
    private Date fechaModificacionDate;
    private IbUsuariosDTO idUsuarioDTO;
    private IbTransaccionesDTO idTransaccionDTO;
    private Collection<IbLogsDTO> ibLogsDTOCollection;
    private List<IbAfiliacionesDTO> afiliaciones;//lista de afiliaciones para el manejo en caso de consulta multiple
    private Character estatus;
    private String nombreBanco;
    private RespuestaDTO respuestaDTO;              //todos los DTO deben tener este objeto
    private DESedeEncryption desEnc;                //objeto encargado de encriptar la data
    private String semilla;                         //semilla para encriptar data sesnsible
    private int idInt;                          /*********se agrego para pruebas hablar con cesar (juan)*****/

    public int getIdInt() {
        return idInt = Integer.parseInt(id.toString());
    }

    public void setIdInt(int idInt) {
        this.idInt = idInt;
    }
    
    
    
    public IbAfiliacionesDTO (String semillaDes){
        super();
        semilla = semillaDes;
        this.desEnc = new DESedeEncryption();
        desEnc.setSemilla(semilla);
    }
    
    public IbAfiliacionesDTO (){
        super();
    }

    /**
     * Obtener estatus
     *
     * @return Character
     */
    public Character getEstatus() {
        return estatus;
    }

    /**
     * Asignar estatus
     *
     * @param estatus Character
     */
    public void setEstatus(Character estatus) {
        this.estatus = estatus;
    }

    /**
     * Obtener objeto respuesta
     *
     * @return RespuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asignar objeto respuesta
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
     * Asignar id
     *
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Obtener alias
     *
     * @return String
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Asignar alias
     *
     * @param alias String
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Obtener numero de cuenta
     *
     * @return String
     */
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    /**
     * Asignar numero de cuenta
     *
     * @param numeroCuenta String
     */
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }
    
    /**
     * Obtener numero de cuenta
     *
     * @return String
     */
    public String getNumeroCuentaEnc() {
        return this.getDesEnc().encriptar(numeroCuenta);
    }

    /**
     * Obtener nombre de beneficiario
     *
     * @return String
     */
    public String getNombreBeneficiario() {
        return nombreBeneficiario;
    }

    /**
     * Asignar nombre del beneficiario
     *
     * @param nombreBeneficiario String
     */
    public void setNombreBeneficiario(String nombreBeneficiario) {
        this.nombreBeneficiario = nombreBeneficiario;
    }

    /**
     * Obtener tipo de doc
     *
     * @return Character
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
     * Obtener documento
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
     * Obtener email
     *
     * @return String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Asignar el email
     *
     * @param email String
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtener el monto maximo
     *
     * @return BigDecimal
     */
    public BigDecimal getMontoMaximo() {
        return montoMaximo;
    }

    /**
     * Asignar monto maximo
     *
     * @param montoMaximo BigDecimal
     */
    public void setMontoMaximo(BigDecimal montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    /**
     * Asignar codigo usuario
     *
     * @return String
     */
    public String getCodUsuario() {
        return codUsuario;
    }

    /**
     * Asignar codigo del usuario
     *
     * @param codUsuario String
     */
    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    /**
     * Obtener fecha de reistro
     *
     * @return Date
     */
    public Date getFechaRegistroDate() {
        return fechaRegistroDate;
    }

    /**
     * Asinar fecha de registro
     *
     * @param fechaRegistro Date
     */
    public void setFechaRegistroDate(Date fechaRegistro) {
        this.fechaRegistroDate = fechaRegistro;
    }

    /**
     * Obtener fecha de modificacion
     *
     * @return Date
     */
    public Date getFechaModificacionDate() {
        return fechaModificacionDate;
    }

    /**
     * Asignar fecha de modificacion
     *
     * @param fechaModificacion Date
     */
    public void setFechaModificacionDate(Date fechaModificacion) {
        this.fechaModificacionDate = fechaModificacion;
    }

    /**
     * Obtener id del usuario
     *
     * @return IbUsuariosDTO
     */
    public IbUsuariosDTO getIdUsuarioDTO() {
        return idUsuarioDTO;
    }

    /**
     * Asignar el usuario
     *
     * @param idUsuarioDTO IbUsuariosDTO
     */
    public void setIdUsuarioDTO(IbUsuariosDTO idUsuarioDTO) {
        this.idUsuarioDTO = idUsuarioDTO;
    }

    /**
     * Obtener el id de la transaccion
     *
     * @return IbTransaccionesDTO
     */
    public IbTransaccionesDTO getIdTransaccionDTO() {
        return idTransaccionDTO;
    }

    /**
     * Asignar objeto transaccion
     *
     * @param idTransaccionDTO IbTransaccionesDTO
     */
    public void setIdTransaccionDTO(IbTransaccionesDTO idTransaccionDTO) {
        this.idTransaccionDTO = idTransaccionDTO;
    }

    /**
     * Obtener un listado de logs
     *
     * @return Collection<IbLogsDTO>
     */
    public Collection<IbLogsDTO> getIbLogsDTOCollection() {
        return ibLogsDTOCollection;
    }

    /**
     * Asignar un listado de logs
     *
     * @param ibLogsDTOCollection Collection<IbLogsDTO>
     */
    public void setIbLogsDTOCollection(Collection<IbLogsDTO> ibLogsDTOCollection) {
        this.ibLogsDTOCollection = ibLogsDTOCollection;
    }

    /**
     * Obtiene un listado de afiliaciones
     *
     * @return List<IbAfiliacionesDTO>
     */
    public List<IbAfiliacionesDTO> getAfiliaciones() {
        return afiliaciones;
    }

    /**
     * Asigna un listado de afiliaciones
     *
     * @param afiliaciones List<IbAfiliacionesDTO>
     */
    public void setAfiliaciones(List<IbAfiliacionesDTO> afiliaciones) {
        this.afiliaciones = afiliaciones;
    }

    /**
     * Obtener el nombre del banco
     *
     * @return nombreBanco String
     */
    public String getNombreBanco() {
        if (nombreBanco != null && nombreBanco.contains("--")){
            this.nombreBanco = nombreBanco.split("--")[0];
        }
        return nombreBanco;
    }
    /**
     * Obtener el nombre del banco
     *
     * @return nombreBanco String
     */
    public String getNombreBancoCompleto() {
        return nombreBanco;
    }
    
    
       /**
     * Obtener el nombre del banco
     *
     * @param nombreBanco
     * 
     */
    public void setNombreBancoCompleto(String nombreBanco) {
        this.nombreBanco=nombreBanco;
    }
     
    
    
    
    /**
     * Obtener el nombre del banco
     *
     * @return nombreBanco String
     */
    public String getCodBanco() {
        String cod = "";
        if (nombreBanco != null && nombreBanco.contains("--")){
            cod = nombreBanco.split("--")[1];
        }
        return cod;
    }

    /**
     * Asignar el nombre del banco
     *
     * @param nombreBanco String
     */
    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }    
    
    /**
     * retorna la semilla para encriptar la data sensible
     * @return semilla para encriptar la data sensible
     */
    public String getSemilla() {
        return semilla;
    }

    /**
     * asigna la semilla para encriptar la data sensible
     * @param semilla semilla para encriptar la data sensible
     */
    public void setSemilla(String semilla) {
        this.semilla = semilla;
        this.desEnc = new DESedeEncryption();
        desEnc.setSemilla(semilla);
    }

    /**
     * retorna el objeto para manejo de encriptado
     * @return el objeto para manejo de encriptado
     */
    public DESedeEncryption getDesEnc() {
        return desEnc;
    }

    /**
     * asigna el objeto para manejo de encriptado
     * @param desEnc el objeto para manejo de encriptado
     */
    public void setDesEnc(DESedeEncryption desEnc) {
        this.desEnc = desEnc;
    }
}
