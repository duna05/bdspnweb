/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.DelSurDTO;
import com.bds.wpn.dto.UtilDTO;

/**
 * Interfaz de metodos especificos del banco del sur
 * @author juan.faneite
 */
public interface DelSurDAO {

    /**
     * Metodo que obtiene el detalle de los nombres de las agencias y estados.
     *
     * @param codigoCanal codigo canal
     * @param idCanal identificador del canal
     * @return DelSurDTO listado de las agencias
     */
    public DelSurDTO obtenerAgencias(String codigoCanal, String idCanal);

    /**
     * Metodo para obtener el listado de bancos
     * @param codigoCanal codigo del canal
     * @param idCanal id del canal
     * @return listado de bancos
     */
    public DelSurDTO listadoBancos(String codigoCanal, String idCanal);
    
    /**
     * Metodo para verificar que el banco no posee restricciones internas
     * @param codbanco codigo del banco
     * @param nombreCanal nombre del canal
     * @param idCanal id del canal
     * @return RespuestaDTO
     */
    public UtilDTO verificarBloqueBanco (String codbanco, String nombreCanal, String idCanal);
    
    /**
     * Metodo para obtener el listado de bancos afiliados a Switch7B
     * @param codigoCanal codigo del canal
     * @param idCanal id del canal
     * @return listado de bancos
     */
    public DelSurDTO listadoBancosSwitch7B(String codigoCanal, String idCanal);
    
    /**
     * Metodo que valida si el usuario está habilitado para hacer transacciones P2P
     * @param cedula
     * @param codigoCanal codigo del canal
     * @param idCanal id del canal
     * @return listado de bancos
     */
    public boolean validarUsuarioP2P(String cedula, String idCanal, String codigoCanal);
}
