/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.IbHistoricoClavesDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;

/**
 *
 * @author wilmer.rondon
 */
public interface IbClaveDAO {
    
    /**
     * Método que se encarga de consultar si la nueva clave ya se encuentra entre las
     * últimas N claves que el cliente ha tenido
     * @param idUsuario
     * @param clave
     * @param cantClaves
     * @param nombreCanal
     * @return UtilDTO
     */
    public UtilDTO existeEnUltimasNClaves(String idUsuario, String clave, String cantClaves, String nombreCanal);
    
    /**
     * Método que se encarga de actualizar la nueva clave en IB_USUARIOS y de insertar
     * en la tabla IB_HISTORICO_CLAVES la última clave actualizada por el usuario así como el
     * período de validez de la misma.
     * @param idUsuario
     * @param clave
     * @param periodoClave
     * @param nombreCanal
     * @return RespuestaDTO
     */
    public RespuestaDTO crearClave(String idUsuario, String clave, String periodoClave, String nombreCanal);
    
    /**
     * Método que se encarga de retornar la última clave que tiene el cliente
     * activa     
     * @param idUsuario
     * @param nombreCanal
     * @return IbHistoricoClavesDTO
     */
    public IbHistoricoClavesDTO obtenerUltimaClave(String idUsuario, String nombreCanal);
    
}
