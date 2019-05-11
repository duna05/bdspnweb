/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.RespuestaDTO;

/**
 *
 * @author audra.zapata
 */
public interface FichaClienteRefTarjetaUpdateDAO {
    
    public RespuestaDTO actualizarRefTarjeta(String iCodigoCliente);
}
