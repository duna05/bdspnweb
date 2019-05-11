/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.IbLogsDTO;

/**
 * Interfaz DAO para logs
 * @author juan.faneite
 */
public interface IbLogsDAO {
    
    /**
     * Metodo para obtener el listado de moviemientos del log del usuario
     *
     * @param idUsuario id del usuario
     * @param fechaDesde fecha desde si viene null se tomaran los ultimos registros
     * @param fechaHasta fecha hasta si viene null se tomaran los ultimos registros
     * @param nroMesesAtras cantidad de meses atras a colsultar
     * @param idTransaccion número de la transacción por la que se realizará el filtro
     * @param idCanal id del canal a filtrar
     * @param nombreCanal nombre del canal
     * @return IbLogsDTO -> List < IbLogs > listado de moviemientos del log
     */
    public IbLogsDTO listadoHistoricoCliente (String idUsuario, String fechaDesde, String fechaHasta, int nroMesesAtras, String idTransaccion, String idCanal, String nombreCanal);
}
