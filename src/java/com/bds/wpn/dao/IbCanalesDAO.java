/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.IbCanalDTO;
import com.bds.wpn.dto.IbUsuariosCanalesDTO;
import com.bds.wpn.dto.IbUsuariosDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author renseld.lugo
 */
public interface IbCanalesDAO {

    /**
     * Obtiene la fecha de la ultima conexion realizada por el cliente al canal.
     *
     * @param idUsuario String id del cliente.
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return UtilDTO
     */
    public UtilDTO consultarUltimaConexionCanal(String idUsuario, String idCanal, String nombreCanal);

    /**
     * Realiza el bloqueo de acceso al canal .
     *
     * @param idUsuario String id del cliente.
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return RespuestaDTO
     */
    public RespuestaDTO bloquearAccesoCanal(String idUsuario, String idCanal, String nombreCanal);

    /**
     * Valida que no exista una conexion antes de iniciar session.
     *
     * @param idUsuario String id del cliente.
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return UtilDTO
     */
    public UtilDTO validarConexionSimultanea(String idUsuario, String idCanal, String nombreCanal);

    /**
     * Metodo para validar el acceso a la banca movil via app mobile NOTA
     * estemetodo es temporal
     *
     * @param numeroTarjeta numero de tarjeta de 20 digitos
     * @param password password de acceso al IB
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return UtilDTO con el nombre del usuario y la fecha dela ultima conexion
     * si el acceso es correcto
     */
    public UtilDTO loginIB(String numeroTarjeta, String password, String idCanal, String nombreCanal);

    /**
     * Inserta la realcion de usuario con canal
     *
     * @param usuario IbUsuariosDTO
     * @param idSesion id de la sesion
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return RespuestaDTO
     */
    public RespuestaDTO insertarUsuarioCanal(IbUsuariosDTO usuario, String idSesion, String idCanal, String nombreCanal);

    /**
     * Obtiene la fecha de la ultima conexion realizada por el cliente al canal.
     *
     * @param usuario IbUsuariosDTO String id del cliente.
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @param estatusConeccion String
     * @param intentosFallidos cantidad de intentos fallidos de acceso
     * @param idSesion identificador unico de la sesion
     * @param estatusAcceso indentificador del estado del acceso al canal
     * @param estatusRegistro indentificador del estado del registro al canal
     * @param limiteInternas limite de monto de operaciones propias en DELSUR
     * @param limiteExternas limite de monto de operaciones propias en otros bancos
     * @param limiteInternasTerc limite de monto de operaciones terceros en DELSUR
     * @param limiteExternasTerc limite de monto de operaciones terceros en otros bancos
     * @return RespuestaDTO
     */
    public RespuestaDTO actualizarUsuarioCanal(IbUsuariosDTO usuario, String idCanal, String nombreCanal, String estatusConeccion, 
            int intentosFallidos, String idSesion,String estatusAcceso, String estatusRegistro, BigDecimal limiteInternas, BigDecimal limiteExternas, 
            BigDecimal limiteInternasTerc, BigDecimal limiteExternasTerc);

    /**
     * *
     * Obtiene los datos del canal y del usuario a traves del id de usuario
     *
     * @param idUsuario IbUsuariosDTO
     * @param idCanal String
     * @return IbUsuariosCanalesDTO
     */
    public IbUsuariosCanalesDTO consultarUsuarioCanalporIdUsuario(IbUsuariosDTO idUsuario, String idCanal);

    /**
     * Metodo para validar el estatus de registro y de acceso del usuario en
     * IB_USUARIOS_CANALES
     *
     * @param codUsuario String
     * @param idCanal String
     * @param nombreCanal String
     * @return RespuestaDTO Si esta bloqueado en alguno de los dos retorna el
     * codigoSP y textoSP
     */
    public RespuestaDTO validarUsuarioAccesoRegistro(String codUsuario, String idCanal, String nombreCanal);
    
    /**
     * Metodo para obtener el listado de canales
     *
     * @param codUsuario codigo del usuario para efectos de registro en el log
     * @param nombreCanal nombre del canal para efectos de registro en el log
     * @param idCanal
     * @return IbCanalDTO Listado de canales para un usuario
     */
     public IbCanalDTO listadoCanales(String codUsuario, String nombreCanal, String idCanal);

     
      /**
     * Metodo para obtener el listado de canales por usuario
     * @param idUsuario id del usuario
     * @param idcanal id del canal
     * @param nombreCanal nombre del canal
     * @return  IbCanalDTO listado de canales
     */
    public IbCanalDTO consultaCanalesPorUsuario (String idUsuario,String idcanal,String nombreCanal);
     
     
     
}
