
package com.bds.wpn.dao;

import com.bds.wpn.dto.IbAcumuladoTransaccionDTO;
import com.bds.wpn.dto.RespuestaDTO;

/**
 * Interfaz para Acumulado de Transacciones
 * @author mdiaz
 */
public interface IBAcumuladoTransaccionDAO {
 
     /**
     * Metodo para insertar una afiliacion
     * @param idUsuario    
     * @param montoTransaccion  
     * @param idTransaccion  
     * @param nombreCanal    
     * @param idCanal    
     
     * @return RespuestaDTO indica si la operacion se realizo de manera exitosa o no
     */
    public RespuestaDTO insertarAcumuladoTransaccion(String idUsuario, String montoTransaccion, String idTransaccion, String nombreCanal, String idCanal);

    
    /**
     * Metodo para obtener el afiliado por codigo de usuario y Id de Usuario
     * @param idUsuario
     * @param nombreCanal
     * @param idCanal
     * @return IBAfiliacionesDTO -> afiliacion Seleccionada
     */
    public IbAcumuladoTransaccionDTO consultarAcumuladoUsuario(String idUsuario, String nombreCanal, String idCanal);   
    
}
