/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.TarjetasCreditoDTO;
import com.bds.wpn.dto.UtilDTO;

/**
 *
 * @author rony.rodriguez
 */
public interface TarjetaCreditoDAO {

    /**
     * Retorna los movimientos para la TDC seleccionada por parametros
     *
     * @param numeroTarjeta String Numero de Tarjeta a la cual se le van a
     * obtener los movimientos.
     * @param idCanal String id del canal
     * @param nombreCanal String Codigo del canal desde el cual es llamado el
     * procedimiento.
     * @param nroRegistros String Nro maximo de registros que debe contener el
     * listado.
     * @return TarjetasCreditoDTO la TDC con los movimientos encontrados
     */
    public TarjetasCreditoDTO listadoMovimientosTDC(String numeroTarjeta, String idCanal, String nombreCanal, String nroRegistros);

    /**
     * Retorna el listado de tarjetas de credito de un cliente consultando por
     * numero de cedula.
     *
     * @param iNroCedula String numero de cedula del cliente.
     * @param idCanal String id del canal
     * @param nombreCanal String Nombre del canal desde el cual es llamado el
     * procedimiento.
     * @return ClienteDAO objeto cliente con lista de tcd asociadas al mismo.
     */
    public ClienteDTO listadoTdcPorCliente(String iNroCedula, String idCanal, String nombreCanal);

    /**
     * Retorna el detalle de una TDC especifica.
     *
     * @param numeroTarjeta String con el numero de tarjeta
     * @param idCanal String identificador del canal
     * @param nombreCanal String Codigo del canal desde el cual es llamado el
     * procedimiento.
     * @return TarjetasCreditoDTO
     */
    public TarjetasCreditoDTO obtenerDetalleTDC(String numeroTarjeta, String idCanal, String nombreCanal);

    /**
     * Metodo que obtiene el listado de TDC propias por cliente
     *
     * @param nroCedula String Numero de CI del cliente
     * @param idCanal String Id del canal
     * @param nombreCanal String Nombre del canal
     * @return ClienteDTO -> con List< TarjetasCreditoDTO >
     */
    public ClienteDTO listadoTdcPropias(String nroCedula, String idCanal, String nombreCanal);
    
    
      
    /**
     * Metodo que valida que el instrumento pertenece al cliente.
     *
     * @param codigoCliente codigo del cliente Oracle9
     * @param numeroTarjeta numero de tarjeta
     * @param idCanal String id del canal
     * @param nombreCanal String Nombre del canal
     * @return UtilDTO Retorna los datos de la tarjeta del cliente
     */    
     public UtilDTO obtenerClienteTarjeta(String codigoCliente, String numeroTarjeta,String idCanal, String nombreCanal);
     
     /**
     * retorna los movimientos para la TDC seleccionada por parametros
     *
     * @param nroTDC String Numero de Tarjeta a la cual se le van a obtener los
     * movimientos.
     * @param mes mes a consultar
     * @param anno año a consultar
     * @param idCanal String id del canal
     * @param nombreCanal String Codigo del canal desde el cual es llamado el
     * procedimiento.
     * @return TarjetasCreditoDTO la TDC con los movimientos encontrados
     */
    public TarjetasCreditoDTO listadoMovimientosTDCMes(String nroTDC, int mes, int anno, String idCanal, String nombreCanal);

}
