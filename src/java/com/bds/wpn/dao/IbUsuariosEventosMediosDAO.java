/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.IbUsuariosEventosMediosDTO;
import com.bds.wpn.dto.RespuestaDTO;



/**
 *
 * @author juan.faneite
 */
public interface IbUsuariosEventosMediosDAO {
    
    /**
     * Metodo que sustituye el listado de Eventos y medios asociados a un usuario
     * @param listUsuariosEventosMediosDTO
     * @param codigoCanal codigo del canal
     * @return RespuestaDTO
     */
    public RespuestaDTO agregarUsuarioEventosMedios (IbUsuariosEventosMediosDTO listUsuariosEventosMediosDTO, String codigoCanal);
    
}
