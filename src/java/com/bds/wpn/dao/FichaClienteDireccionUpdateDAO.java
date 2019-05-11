/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.FichaClienteDireccionUpdateDTO;
import com.bds.wpn.dto.RespuestaDTO;

/**
 *
 * @author humberto.rojas
 */
public interface FichaClienteDireccionUpdateDAO {
    
    public RespuestaDTO insertarDatosDireccion(String iCodigoCliente, String tipoVivienda);
    
}
