/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.FichaClienteRefComercialesUpdateDTO;
import com.bds.wpn.dto.RespuestaDTO;

/**
 *
 * @author MGIAccusys
 */
public interface FichaClienteRefComercialesUpdateDAO {
     public RespuestaDTO  actualizarRefComerciales(String iCodigoCliente);

}
