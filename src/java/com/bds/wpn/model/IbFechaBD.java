/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author cesar.mujica
 */

@Entity
public class IbFechaBD implements Serializable {
     private Date fecha;

     /**
     * @return la fecha de BD
     */
     @Id
     @Column(name="DATE_VALUE")
     @Temporal(TemporalType.TIMESTAMP)
     public Date getFecha()
     {
          return fecha;
     }

     /**
     * @param fecha the date to set
     */
     public void setFecha(Date fecha)
     {
          this.fecha = fecha;
     }
}
