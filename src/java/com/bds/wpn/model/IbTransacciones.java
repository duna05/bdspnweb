/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rony.rodriguez
 */
@Entity
@Table(name = "IB_TRANSACCIONES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IbTransacciones.findAll", query = "SELECT i FROM IbTransacciones i"),
    @NamedQuery(name = "IbTransacciones.findById", query = "SELECT i FROM IbTransacciones i WHERE i.id = :id"),
    @NamedQuery(name = "IbTransacciones.findByNombre", query = "SELECT i FROM IbTransacciones i WHERE i.nombre = :nombre"),
    @NamedQuery(name = "IbTransacciones.findByPosicion", query = "SELECT i FROM IbTransacciones i WHERE i.posicion = :posicion"),
    @NamedQuery(name = "IbTransacciones.findByOpcional", query = "SELECT i FROM IbTransacciones i WHERE i.opcional = :opcional"),
    @NamedQuery(name = "IbTransacciones.findByEstatus", query = "SELECT i FROM IbTransacciones i WHERE i.estatus = :estatus")})
public class IbTransacciones implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_CORE")
    private BigInteger idCore;
    
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private BigDecimal id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "NOMBRE")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "POSICION")
    private BigInteger posicion;
    @Column(name = "OPCIONAL")
    private Character opcional;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ESTATUS")
    private Character estatus;
    @JoinColumn(name = "ID_MODULO", referencedColumnName = "ID")
    @ManyToOne
    private IbModulos idModulo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "POSEE_BENEFICIARIO")
    private short poseeBeneficiario;
    @Basic(optional = false)
    @NotNull
    @Column(name = "POSEE_OTP")
    private Character poseeOtp;

    public IbTransacciones() {
    }

    public IbTransacciones(BigDecimal id) {
        this.id = id;
    }

    public IbTransacciones(BigDecimal id, String nombre, BigInteger posicion, Character estatus) {
        this.id = id;
        this.nombre = nombre;
        this.posicion = posicion;
        this.estatus = estatus;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigInteger getPosicion() {
        return posicion;
    }

    public void setPosicion(BigInteger posicion) {
        this.posicion = posicion;
    }

    public Character getOpcional() {
        return opcional;
    }

    public void setOpcional(Character opcional) {
        this.opcional = opcional;
    }

    public Character getEstatus() {
        return estatus;
    }

    public void setEstatus(Character estatus) {
        this.estatus = estatus;
    }

    public IbModulos getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(IbModulos idModulo) {
        this.idModulo = idModulo;
    }

    public short getPoseeBeneficiario() {
        return poseeBeneficiario;
    }

    public void setPoseeBeneficiario(short poseeBeneficiario) {
        this.poseeBeneficiario = poseeBeneficiario;
    }

    public Character getPoseeOtp() {
        return poseeOtp;
    }

    public void setPoseeOtp(Character poseeOtp) {
        this.poseeOtp = poseeOtp;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IbTransacciones)) {
            return false;
        }
        IbTransacciones other = (IbTransacciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.bds.model.IbTransacciones[ id=" + id + " ]";
    }

    public BigInteger getIdCore() {
        return idCore;
    }

    public void setIdCore(BigInteger idCore) {
        this.idCore = idCore;
    }

}
