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
public interface FichaClienteRefPersonalesUpdateDAO {
    
    public RespuestaDTO actualizarRefPersonales(String iCodigoCliente, String codTipoIdentif, String nroIdentif, String nombreIn, String primerApellido, String segundoApellido, String codReferenciaPersonal, String telefonoIn, String telefono2In);
}
