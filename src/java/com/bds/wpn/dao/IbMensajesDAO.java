/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.IbMensajesDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;

/**
 *
 * @author cesar.mujica
 */
public interface IbMensajesDAO {
    
    /**
     * *
     * Metodo que realiza la busqueda un usuario por codigo
     *
     * @param idUsuario codigo del usuario
     * @param nombreCanal nombre del canal
     * @param idCanal identificador unico del canal
     * @return IbUsuarioDTO
     */
    public IbMensajesDTO consultarMensajesUsuarios (String idUsuario , String nombreCanal , String idCanal);
    
    /**
     * *
     * Metodo que consulta la cantidad de mensajes activos(nuevos o leidos) asociados a un usuario
     *
     * @param idUsuario codigo del usuario
     * @param nombreCanal nombre del canal
     * @param idCanal identificador unico del canal
     * @return IbUsuarioDTO
     */
    public UtilDTO cantNuevosMsjsUsuarios (String idUsuario , String nombreCanal , String idCanal);
    
    /**
     * *
     * Metodo que actualiza el estado de un mensaje a leido
     *
     * @param idUsuario codigo del usuario
     * @param idMensaje identificador del mensaje
     * @param nombreCanal nombre del canal
     * @param idCanal identificador unico del canal
     * @return IbUsuarioDTO
     */
    public RespuestaDTO marcarMensajeLeido (String idUsuario , String idMensaje, String nombreCanal , String idCanal);
    
    /**
     * *
     * Metodo que consulta el estado de un mensaje nuevo o leido
     *
     * @param idUsuario codigo del usuario
     * @param idMensaje identificador del mensaje
     * @param nombreCanal nombre del canal
     * @param idCanal identificador unico del canal
     * @return IbUsuarioDTO con parametro estatus y un booleano
     */
    public UtilDTO mensajeUsuarioLeido(String idUsuario, String idMensaje, String nombreCanal, String idCanal);
    
}
