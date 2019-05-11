/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.TarjetaDebitoDTO;
import com.bds.wpn.dto.UtilDTO;

/**
 *
 * @author rony.rodriguez
 */
public interface TddDAO {

    /**
     * Bloquea una tarjeta de debito por intentos fallidos ya sean de acceso o
     * de validacion de OTP.
     *
     * @param tarjetaDebito String
     * @param idCanal String
     * @param nombreCanal String
     * @return RespuestaDTO
     */
    public RespuestaDTO bloquearTDD(String tarjetaDebito, String idCanal, String nombreCanal);
    
    /**
     * Valida que los instrumentos TDD, Fecha Vcto y PIN pertenece al cliente.
     * @param numeroTarjeta numero de la tarjeta de debito del cliente  
     * @param pinCifrado PIN cifrado de la Tarjeta.
     * @param fechaVencimiento Fecha de Vencimiento de la Tarjeta.
     * @param codigoCanal codigo del canal
     * @return UtilDTO
     */
    public UtilDTO validarTDD (String numeroTarjeta,String pinCifrado,String fechaVencimiento,String codigoCanal);
    
    /**
     * Metodo que obtiene el listado de TDC propias por cliente
     * @param codigoCliente
     * @param codigoCanal
     * @return ClienteDTO -> con List< TarjetasCreditoDTO > 
     */
    public TarjetaDebitoDTO obtenerListadoTDDCliente(String codigoCliente, String codigoCanal);

}
