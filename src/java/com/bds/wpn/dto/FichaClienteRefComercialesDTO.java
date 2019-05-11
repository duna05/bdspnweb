/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author alejandro.flores
 */
public class FichaClienteRefComercialesDTO extends BDSUtil implements Serializable{
    
    private String casaComercial;
    private String telefonos;
    private String fechaConcesion;    
    private RespuestaDTO respuesta;
    private List<FichaClienteRefComercialesDTO> ibReferenciaComercialList;

    public String getCasaComercial() {
        return casaComercial;
    }

    public void setCasaComercial(String casaComercial) {
        this.casaComercial = casaComercial;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getFechaConcesion() {
        return fechaConcesion;
    }

    public void setFechaConcesion(String fechaConcesion) {
        this.fechaConcesion = fechaConcesion;
    }

    public RespuestaDTO getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaDTO respuesta) {
        this.respuesta = respuesta;
    }

    public List<FichaClienteRefComercialesDTO> getIbReferenciaComercialList() {
        return ibReferenciaComercialList;
    }

    public void setIbReferenciaComercialList(List<FichaClienteRefComercialesDTO> ibReferenciaComercialList) {
        this.ibReferenciaComercialList = ibReferenciaComercialList;
    }

  
    
    
    
}
