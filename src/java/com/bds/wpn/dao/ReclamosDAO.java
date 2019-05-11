/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.ReclamoDTO;
import com.bds.wpn.dto.RespuestaDTO;

/**
 *
 * @author juan.faneite
 */
public interface ReclamosDAO {
   
    /**
     * Obtiene el listado de reclamos
     *
     * @param idCanal id del canal
     * @param codigoCanal codigo del canal
     * @return ClienteDTO  obtiene el listado de reclamos
     */
    public ReclamoDTO obtenerListadoReclamos(String idCanal, String codigoCanal);
    
    /**
     * Graba en la tabla de solicitud los campos del reclamo.
     * @param codigoCliente codigo del cliente
     * @param codigoReclamo codigo del reclamo
     * @param secuenciaExtMovimiento Este valor es devuelto en el campo secuenciaExtMovimiento de algunos cursores de movimientos.
     * @param observacion Observación del reclamo suministrada por el cliente que la realiza.
     * @param codigoCanal codigo del canal 
     * @return RespuestaDTO
     */
    public ReclamoDTO insertarReclamoCliente (String codigoCliente,String codigoReclamo,String secuenciaExtMovimiento,String observacion,String codigoCanal);
    
    /**
     * Listado De Reclamos X Cliente
     * @param codigoCliente codigo del cliente
     * @param idCanal id del canal
     * @param codigoCanal codigo del canal 
     * @return RespuestaDTO
     */
    public ReclamoDTO listadoReclamosPorCliente (String codigoCliente, String idCanal, String codigoCanal);
    
    /**
     * Retorna los movimientos para la cuenta seleccionada
     *
     * @param tipoCuenta String tipo de cuenta (BCC, BCA, BME) -> ver constantes
     * de tipos producto
     * @param numeroCuenta String numero de cuenta
     * @param fechaIni String fecha de incio del filtro
     * @param fechaFin String fecha de fin del filtro
     * @param regIni String registro de Inicio para la paginacion
     * @param regFin String registro de fin para la paginacion
     * @param tipoOrdenFecha String tipo de Orden por Fecha: ASC(por defecto),
     * DESC
     * @param idCanal String con el id del canal
     * @param nombreCanal canal por el cual se realiza la consulta
     * @return CuentaDTO la cuenta con los movimientos de la cuenta
     * seleccionada(solo vienen los datos de los movimientos)
     */
    public CuentaDTO listarMovimientos(String tipoCuenta, String numeroCuenta, String fechaIni, String fechaFin,
            String regIni, String regFin, String tipoOrdenFecha, String idCanal, String nombreCanal);
    
}

