/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;


import com.bds.wpn.dto.FichaClienteEmpleosDTO;
/**
 *
 * @author humberto.rojas
 */
public interface FichaClienteEmpleosDAO {
    
    public FichaClienteEmpleosDTO consultarEmpleos(String iCodigoCliente);
    
}
