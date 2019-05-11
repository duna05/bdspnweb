/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author juan.faneite
 */
public class MesAnoDTO implements Serializable {
    private String ano;
    private String mes;

    public String getAno() {
        int anio;
        Calendar calendar = Calendar.getInstance();        
        if(calendar.get(Calendar.MONTH) >= 5){
            anio = calendar.get(Calendar.YEAR);
        } else if (Integer.parseInt(mes) < 6){
            anio = calendar.get(Calendar.YEAR);
        } else {
            anio = calendar.get(Calendar.YEAR) - 1;
        }
        ano = String.valueOf(anio);
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }
    
    public String getAnoString() {
        return ano;
    }
    
    
}
