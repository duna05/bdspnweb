/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.IbModulosDTO;
import java.util.List;

/**
 *
 * @author renseld.lugo
 */
public interface IbMenuDAO {

    /**
     * Metodo que consulta el menu por usuario.
     *
     * @param idUsuario String id del usuario
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return List<IbModulosDTO>
     */
    public List<IbModulosDTO> consultarModulosPorUsuario(String idUsuario, String idCanal, String nombreCanal);
    
    /**
     * Metodo para obtener el modelo y la transaccion de los que tengan el posee beneficiario activo
     * @param idCanal id del canal
     * @param nombreCanal String nombre del canal
     * @return IbModulosDTO
     */
    public List<IbModulosDTO> obtenerModeloTransBeneficiario(String idCanal, String nombreCanal);
    
    /**
     * Metodo para obtener el modelo y la transaccion de los que tengan el posee
     * beneficiario activo y que no sean propias DelSur
     *
     * @param idCanal id del canal
     * @param nombreCanal String nombre del canal
     * @return IbModulosDTO
     */
    public List<IbModulosDTO> obtenerModeloTransBeneficiarioFiltrada(String idCanal, String nombreCanal);

    /**
     * Método para obtener todos los módulos y transacciones asociadas a un canal en específico
     * esten activos o inactivos, visibles o no visibles
     * @author wilmer.rondon
     * @param idCanal canal del cual se desea obtener los modulos y las transacciones
     * @param nombreCanal nombre del canal para efectos de los logs
     * @return IbModulosDTO
     */
    public List<IbModulosDTO> obtenerTodosModulosYTransacciones(String idCanal, String nombreCanal);
    
}
