/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.AfiliacionServicioDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.ServicioDTO;
import com.bds.wpn.dto.UtilDTO;
import java.math.BigDecimal;


/**
 * Interfaz para Acumulado de Transacciones
 * @author cesar.mujica
 */
public interface GestionServiciosDAO {
    
    /**
     * Metodo se encarga de crear una afiliacion de servicio     *
     * @param afiliacion
     * @param tipoCanal
     * @param nombreCanal
     * @return 
     */
    public RespuestaDTO agregarAfiliacion(AfiliacionServicioDTO afiliacion,String tipoCanal, String nombreCanal);
    
    /**
     * Metodo se encarga de retornar la lista de servicios    
     * @param nombreCanal
     * @return 
     */
    public ServicioDTO obtenerListadoServicios(String nombreCanal);
    
    /**
     * Metodo se encarga de eliminar una afiliacion de servicio     *
     * @param afiliacion
     * @param nombreCanal
     * @return 
     */
    public RespuestaDTO eliminarAfiliacion(AfiliacionServicioDTO afiliacion, String nombreCanal);
    
    /**
     * Metodo se encarga de retornar la lista de afiliaciones a un servicio determinado
     * @param codCliente
     * @param codServicio
     * @param nombreCanal
     * @return 
     */
    public AfiliacionServicioDTO obtenerListadoAfiliaciones(String codCliente, String codServicio, String nombreCanal);
    
    /**
     * Metodo se encarga de crear una afiliacion de servicio     *
     * @param afiliacion
     * @param monto
     * @param ctaDebitar
     * @param tipoCanal
     * @param nombreCanal
     * @return 
     */
    public UtilDTO recargar(AfiliacionServicioDTO afiliacion, BigDecimal monto, String ctaDebitar, String tipoCanal, String nombreCanal);
    
}
