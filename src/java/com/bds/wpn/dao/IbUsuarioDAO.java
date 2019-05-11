/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.IbUsuariosDTO;
import com.bds.wpn.dto.IbUsuariosCanalesDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import java.math.BigDecimal;

/**
 *
 * @author juan.faneite
 */
public interface IbUsuarioDAO {

    /**
     * Metodo que obtiene un Usuario por canal
     *
     * @param idUsuario String Codigo del usuario.
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return IbUsuariosCanalesDTO
     */
    public IbUsuariosCanalesDTO obtenerIbUsuarioPorCanal(String idUsuario, String idCanal, String nombreCanal);

    /**
     * Metodo que se encarga de registrar los datos de un cliente en la BD
     * Oracle 11
     *
     * @param cliente ClienteDTO objeto con los datos del cliente en oracle9
     * @param nroTDD String numero de TDD afiliada
     * @param idPerfil BigDecimal identificador del perfil
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return RespuestaDTO Objeto de Respuesta con el resultado de la
     * transaccion.
     */
    public RespuestaDTO insertarDatosIbUsuario(ClienteDTO cliente, String nroTDD, BigDecimal idPerfil, String idCanal, String nombreCanal);

    /**
     * Metodo que obtiene un Usuario por codigo de cliente
     *
     * @param codUsuario String Codigo del usuario.
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return IbUsuariosDTO
     */
    public IbUsuariosDTO obtenerIbUsuarioPorCodusuario(String codUsuario, String idCanal, String nombreCanal);

    /**
     * Metodo que obtiene un Usuario por TDD
     *
     * @param tdd String numero de tarjeta de debito
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return IbUsuarioDTO
     */
    public IbUsuariosDTO obtenerIbUsuarioPorTDD(String tdd, String idCanal, String nombreCanal);

    /**
     * Metodo para actualizar los datos del usuario del core a la bd del IB
     *
     * @param cliente ClienteDTO
     * @param nroTDD String
     * @param idCanal String
     * @param nombreCanal String
     * @return RespuestaDTO
     */
    public RespuestaDTO actualizarDatosUsuario(ClienteDTO cliente, String nroTDD, String idCanal, String nombreCanal);
    
    /**
     * Metodo para ingresar al internet banking con manejo de encriptamiento de datos     
     * @param nroTDD
     * @param clave
     * @param idCanal
     * @param nombreCanal
     * @return RespuestaDTO
     */
    public UtilDTO validarLogin(String nroTDD, String clave, String idCanal, String nombreCanal);
    
    /**
     * Metodo para consultar la cantidad de intentos fallidos de preguntas de
     * seguridad
     *
     * @param idUsuario identificador del usuario
     * @param nroTDD nro de TDD del usuario
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return RespuestaDTO
     */
    public UtilDTO cantidadPregSegFallidas(String idUsuario, String nroTDD, String nombreCanal, String idCanal);

    /**
     * Metodo actualiza la cantidad de intentos fallidos de las preguntas de
     * seguridad
     *
     * @param idUsuario identificador del usuario
     * @param cantIntentos cantidad de intentos fallidos a actualizar
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return RespuestaDTO
     */
    public RespuestaDTO actualizarPregSegFallidas(String idUsuario, int cantIntentos, String nombreCanal, String idCanal);
    
    
    /**
     * Metodo para validar si la TDD ingresada esta activa en el core bancario Ora9
     *
     * @param codUsuario
     * @param nroTDD
     * @param idCanal
     * @param nombreCanal
     * @return RespuestaDTO
     */
    public UtilDTO validarTDDActivaCore(String codUsuario, String nroTDD, String idCanal, String nombreCanal);
    
     /**
     * Metodo para consultar la la TDD de un usuario por su Documento de Identidad
     *
     * @param tipoDoc tipo de documento
     * @param nroDoc nro de TDD del usuario
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return RespuestaDTO
     */
    public UtilDTO obtenerTDDPorDoc(String tipoDoc, String nroDoc, String nombreCanal, String idCanal);
    
}
