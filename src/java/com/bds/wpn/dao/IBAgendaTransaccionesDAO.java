/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.IBAgendaPNDTO;
import com.bds.wpn.dto.IBAgendaTransaccionDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import java.math.BigDecimal;

/**
 *
 * @author cesar.mujica
 */
public interface IBAgendaTransaccionesDAO {
    
    /**
     * Metodo se encarga de almacenar la cabecera de una transaccion agendada
     * @param agenda Objeto con los datos de la cabecera de transaccion a agendar
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return RespuestaDTO
     */
    public RespuestaDTO agregarCabeceraAgenda( IBAgendaTransaccionDTO agenda, String nombreCanal, String idCanal);
    
     /**
     * Metodo se encarga de almacenar el detalle de una transaccion agendada
     * @param agenda Objeto con los datos de la cabecera de transaccion a agendar
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return RespuestaDTO
     */
    public RespuestaDTO agregarDetalleAgenda(IBAgendaTransaccionDTO agenda, String nombreCanal, String idCanal);
    
    /**
     * Metodo se encarga de obtener el id de la cabecera de una transaccion agendada
     *
     * @param agenda Objeto con los datos de la cabecera de transaccion a
     * agendar
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return UtilDTO
     */
    public UtilDTO consultarIdCabeceraAgenda(IBAgendaTransaccionDTO agenda, String nombreCanal, String idCanal);
    
    /**
     * Metodo se encarga de obtener el id de la cabecera de una transaccion agendada
     *
     * @param id
     * @param tipo
     * @param idTransaccion
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return UtilDTO
     */
    public IBAgendaPNDTO consultarIdCabeceraAgendaPP(BigDecimal id, String tipo, int idTransaccion, String nombreCanal, String idCanal);

  
     /**
     * Metodo se encarga de obtener el id de la cabecera de una transaccion agendada por el usuario, id de transacción
     *
     * @param idAgenda
     * @param idUsuario
     * @param nombreCanal nombre del canal
     * @return UtilDTO
     */
    public RespuestaDTO eliminarAgendaProgramada(BigDecimal idAgenda, BigDecimal idUsuario, String nombreCanal, String idCanal);
    
    
}
