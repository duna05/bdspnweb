/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.enumerated;

/**
 *
 * @author jose.perez
 */
public enum ClavesOperacionesEspecialesEnum {

    ACTIVO("1", "ACTIVO"),
    BLOQUEADO("2", "BLOQUEADO"),
    NO_REGISTRADO("3", "NO_REGISTRADO"),
    INCORRECTO("4", "INCORRECTO"),
    YA_REGISTRADO("5", "YA_REGISTRADO");

    String id;
    String descripcion;

    private ClavesOperacionesEspecialesEnum(String id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
