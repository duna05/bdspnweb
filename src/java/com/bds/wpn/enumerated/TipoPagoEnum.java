/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.enumerated;

/**
 *
 * @author José Perez
 */
public enum TipoPagoEnum {
   
    P2P(1, "Pagos a personas"),
    P2C(2, "Pagos a empresas");
    
    private int id;
    private String descripcion;

    private TipoPagoEnum(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
}
