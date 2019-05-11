/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;

/**
 *
 * @author juan.faneite
 */
public class BancoDTO implements Serializable {

    private String codigoBanco;
    private String nombreBanco;

    public BancoDTO() {
        //
    }

    /**
     * Obtener codigo banco
     * @return codigo banco
     */
    public String getCodigoBanco() {
        return codigoBanco;
    }

    /**
     * inserta codigo banco
     * @param codigoBanco codigo banco
     */
    public void setCodigoBanco(String codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    /**
     * obtiene nombre banco
     * @return nombre banco
     */
    public String getNombreBanco() {
        return nombreBanco;
    }

    /**
     * inserta nombre banco
     * @param nombreBanco nombre banco
     */
    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }
    
    
}
