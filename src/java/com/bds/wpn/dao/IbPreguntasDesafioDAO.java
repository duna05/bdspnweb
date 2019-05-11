/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.IbPregDesafioUsuarioDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import java.util.List;

/**
 *
 * @author cesar.mujica
 */
public interface IbPreguntasDesafioDAO {
    
    /**
     * Metodo para obtener el listado de preguntas de desafio del banco
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return IbPreguntasDesafioDTO listado de las preguntas de desafio
     */
    public IbPregDesafioUsuarioDTO listadoPreguntasDesafioBanco (String nombreCanal, String idCanal);
    
    /**
     * Metodo para validar una rafaga de preguntas de desafio
     *
     * @param idUsuario id del usuario
     * @param rafaga cadena con las preguntas y respuestas a validar ej: < codigoPregunta >< separador
     * > < codigoPregunta >< separador >< codigoPregunta >< separador >
     * @param separador separador que utilizara en la rafaga, si este es null se tomará guion "-" como separador
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return UtilDTO -> 'true' en caso que la respuesta sea corresta 'false'
     * en caso contrario
     */
    public UtilDTO validarPreguntaDD(String idUsuario, String rafaga, String separador, String nombreCanal, String idCanal);
    
    /**
     * Metodo que devuelve un listado de preguntas de desafio de un cliente
     *
     * @param idUsuario id del usuario
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return retorna un listado de preguntas de desafio de un cliente
     */
    public IbPregDesafioUsuarioDTO listadoPreguntasDesafioUsuario(String idUsuario, String nombreCanal, String idCanal);
    
    /**
     * Metodo que sustituye las preguntas de desafio viejas por las nuevas con sus respuestas
     * @param listPDDUsuarioRespuestas listado de preguntas y respuestas
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return RespuestaDTO
     */
    public RespuestaDTO agregarPDDUsuario(List<IbPregDesafioUsuarioDTO> listPDDUsuarioRespuestas, String nombreCanal, String idCanal);
    
}
