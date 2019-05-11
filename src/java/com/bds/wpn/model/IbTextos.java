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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author cesar.mujica
 */
@Entity
@Table(name = "IB_TEXTOS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IbTextos.findAll", query = "SELECT i FROM IbTextos i"),
    @NamedQuery(name = "IbTextos.findById", query = "SELECT i FROM IbTextos i WHERE i.id = :id"),
    @NamedQuery(name = "IbTextos.findByCodigo", query = "SELECT i FROM IbTextos i WHERE i.codigo = :codigo"),
    @NamedQuery(name = "IbTextos.findByMensajeUsuario", query = "SELECT i FROM IbTextos i WHERE i.mensajeUsuario = :mensajeUsuario"),
    @NamedQuery(name = "IbTextos.findByMensajeTecnico", query = "SELECT i FROM IbTextos i WHERE i.mensajeTecnico = :mensajeTecnico"),
    @NamedQuery(name = "IbTextos.findByParametros", query = "SELECT i FROM IbTextos i WHERE i.parametros = :parametros")})
public class IbTextos implements Serializable {
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private BigDecimal id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "CODIGO")
    private String codigo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "MENSAJE_USUARIO")
    private String mensajeUsuario;
    @Size(max = 255)
    @Column(name = "MENSAJE_TECNICO")
    private String mensajeTecnico;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PARAMETROS")
    private BigInteger parametros;

    public IbTextos() {
    }

    public IbTextos(BigDecimal id) {
        this.id = id;
    }

    public IbTextos(BigDecimal id, String codigo, String mensajeUsuario, BigInteger parametros) {
        this.id = id;
        this.codigo = codigo;
        this.mensajeUsuario = mensajeUsuario;
        this.parametros = parametros;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensajeUsuario() {
        return mensajeUsuario;
    }

    public void setMensajeUsuario(String mensajeUsuario) {
        this.mensajeUsuario = mensajeUsuario;
    }

    public String getMensajeTecnico() {
        return mensajeTecnico;
    }

    public void setMensajeTecnico(String mensajeTecnico) {
        this.mensajeTecnico = mensajeTecnico;
    }

    public BigInteger getParametros() {
        return parametros;
    }

    public void setParametros(BigInteger parametros) {
        this.parametros = parametros;
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
        if (!(object instanceof IbTextos)) {
            return false;
        }
        IbTextos other = (IbTextos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.bds.wpn.model.IbTextos[ id=" + id + " ]";
    }
    
}
