/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.IbUsuariosP2PDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;

/**
 *
 * @author cesar.mujica
 */
public interface IbUsuariosP2PDAO {
    
    /**
     * inserta la afiliacion de un usuario al servicio P2P
     * @param usuarioP2P datos del usuario afiliado al servicio P2P
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param codigoCanal String codigo por el cual se ejecuta la transaccion.
     * @return RespuestaDTO indica si el metodo se realizo de manera exitosa o no
     */        
    public RespuestaDTO insertarUsuarioP2P(IbUsuariosP2PDTO usuarioP2P, String idCanal, String codigoCanal);
    
    /**
     * Metodo que realiza la consulta de afiliaciones Activas para el serv P2P por usuario.
     * @param idUsuario identificador del usuario en Ora11
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la transaccion.
     * @return RespuestaDTO indica si el metodo se realizo de manera exitosa o no
     */       
    public IbUsuariosP2PDTO consultarUsuarioP2P(String idUsuario, String idCanal, String nombreCanal);
    
    
    /**
     * *
     * Metodo que realiza la validacion de los datos a afiliar al servicio P2P
     *
     * @param nroTelf
     * @param nroCta
     * @param idUsuario id del usuario
     * @param codigoCanal codigo del canal
     * @return UtilDTO
     */
    public UtilDTO validarAfiliacionP2P(String nroTelf, String nroCta, String idUsuario, String codigoCanal);
    
    /**
     * *
     * Metodo que realiza la validacion de los datos a editar una afiliacion al servicio P2P
     *
     * @param idAfiliacion identificador de la afiliacion a modificar
     * @param nroTelf numero de telf a afiliar
     * @param nroCta numero de cta a afiliar
     * @param idUsuario id del usuario
     * @param nombreCanal codigo del canal
     * @return UtilDTO
     */
    public UtilDTO validarEdicionAfiliacionP2P(String idAfiliacion, String nroTelf, String nroCta, String idUsuario, String nombreCanal);
    
    /**
     * edita la afiliacion de un usuario al servicio P2P
     * @param usuarioP2P datos del usuario afiliado al servicio P2P
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la transaccion.
     * @return RespuestaDTO indica si el metodo se realizo de manera exitosa o no
     */        
    public RespuestaDTO editarUsuarioP2P(IbUsuariosP2PDTO usuarioP2P , String idCanal, String nombreCanal);
    
    /**
     * elimina logicamente la afiliacion de un usuario al servicio P2P
     * @param idAfiliacion identificador de la afiliacion a desafiliar
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la transaccion.
     * @return RespuestaDTO indica si el metodo se realizo de manera exitosa o no
     */        
    public RespuestaDTO desafiliarUsuarioP2P(String idAfiliacion , String idCanal, String nombreCanal);
    
}
