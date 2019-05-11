/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.UtilDTO;



/**
 *
 * @author jose.perez
 */
public interface IbClavesOperacionesEspecialesDAO {
    
    /**
     * Valida la Clave de las operaciones especiales para realizar una transaccion donde sea requerida 
     * @param idUsuario
     * @param claveOP
     * @param idCanal
     * @param codigoCanal
     * @return 
     */
    public String validarClaveOperacionesEspeciales(String idUsuario, String claveOP, String idCanal, String codigoCanal);
    
    /**
     * Inserta y actualiza un registro con el valor de la clave de operaciones especiales
     * @param idUsuario
     * @param claveOP
     * @param idUsuarioCarga
     * @param sw
     * @param idCanal
     * @param codigoCanal
     * @return 
     */
    public UtilDTO insertarActualizarClaveOperacionesEspeciales(String idUsuario, String claveOP, String idUsuarioCarga, boolean sw, String idCanal, String codigoCanal);
    
    /**
     * Consulta si existe la clave de operaciones especiales de un usuario determinado
     * @param idUsuario
     * @param idCanal
     * @param codigoCanal
     * @return the boolean 
     */
    public boolean consultarClaveOperacionesEspeciales(String idUsuario, String idCanal, String codigoCanal);
    
}
