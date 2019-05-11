
package com.bds.wpn.dao;

import com.bds.wpn.dto.RespuestaDTO;

/**
 *
 * @author MGIAccusys
 */
public interface FichaClienteProductosUpdateDAO {
    public  RespuestaDTO actualizarClienteProductos(String iCodigoCliente, String motivoSolicitud);
}
