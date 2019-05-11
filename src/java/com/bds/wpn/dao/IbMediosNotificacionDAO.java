/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.IbMediosNotificacionDTO;


/**
 *
 * @author juan.faneite
 */
public interface IbMediosNotificacionDAO {
    
    /**
     * Metodo que obtiene el listado de medios
     * @param codigoCanal codigo del canal para su registro en el log
     * @return IbMediosNotificacionDTO con listado
     */
    public IbMediosNotificacionDTO listaMedios (String codigoCanal);
}
