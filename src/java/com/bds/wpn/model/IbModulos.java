/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author cesar.mujica
 */
@Entity
@Table(name = "IB_MODULOS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IbModulos.findAll", query = "SELECT i FROM IbModulos i"),
    @NamedQuery(name = "IbModulos.findById", query = "SELECT i FROM IbModulos i WHERE i.id = :id"),
    @NamedQuery(name = "IbModulos.findByNombre", query = "SELECT i FROM IbModulos i WHERE i.nombre = :nombre"),
    @NamedQuery(name = "IbModulos.findByPosicion", query = "SELECT i FROM IbModulos i WHERE i.posicion = :posicion"),
    @NamedQuery(name = "IbModulos.findByEstatus", query = "SELECT i FROM IbModulos i WHERE i.estatus = :estatus"),
    @NamedQuery(name = "IbModulos.findByVisible", query = "SELECT i FROM IbModulos i WHERE i.visible = :visible")})
public class IbModulos implements Serializable {
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private BigDecimal id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 140)
    @Column(name = "NOMBRE")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "POSICION")
    private BigInteger posicion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ESTATUS")
    private Character estatus;
    @Basic(optional = false)
    @NotNull
    @Column(name = "VISIBLE")
    private short visible;
    @JoinColumn(name = "ID_CANAL", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private IbCanal idCanal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idModulo")
    private List<IbTransacciones> ibTransaccionesList;

    public IbModulos() {
    }

    public IbModulos(BigDecimal id) {
        this.id = id;
    }

    public IbModulos(BigDecimal id, String nombre, BigInteger posicion, Character estatus, short visible) {
        this.id = id;
        this.nombre = nombre;
        this.posicion = posicion;
        this.estatus = estatus;
        this.visible = visible;
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

    public Character getEstatus() {
        return estatus;
    }

    public void setEstatus(Character estatus) {
        this.estatus = estatus;
    }

    public short getVisible() {
        return visible;
    }

    public void setVisible(short visible) {
        this.visible = visible;
    }

    public IbCanal getIdCanal() {
        return idCanal;
    }

    public void setIdCanal(IbCanal idCanal) {
        this.idCanal = idCanal;
    }

    @XmlTransient
    public List<IbTransacciones> getIbTransaccionesList() {
        return ibTransaccionesList;
    }

    public void setIbTransaccionesList(List<IbTransacciones> ibTransaccionesList) {
        this.ibTransaccionesList = ibTransaccionesList;
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
        if (!(object instanceof IbModulos)) {
            return false;
        }
        IbModulos other = (IbModulos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.bds.wpn.model.IbModulos[ id=" + id + " ]";
    }
    
}
