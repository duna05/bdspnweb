/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.ChequeraDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.EstadoSolicitudChequeraDTO;
import com.bds.wpn.dto.UtilDTO;

/**
 *
 * @author robinson.rodriguez
 */
public interface ChequeraDAO {

    /**
     *
     * @param nroCuenta numero de cuenta a ser consultado
     * @param iCodExtCanal canal por el cual se realiza la consulta
     * @return EstadoSolicitudChequeraDTO
     */
    public EstadoSolicitudChequeraDTO obtenerEstadoSolicitudChequera(String nroCuenta, String iCodExtCanal);

    /**
     * Obtiene las chequeras entregadas de una cuenta.
     *
     * @param numeroCuenta numero de la cuenta
     * @param codigoCanal codigo del canal
     * @return CuentaDTO listado de chequeras entraadas a una cuenta
     */
    public CuentaDTO listarChequeraEntregada(String numeroCuenta, String codigoCanal);

    /**
     * Obtiene el estado de los cheques de una chequera.
     *
     * @param numeroCuenta numero de la cuenta
     * @param numeroPrimerCheque numero del primer cheque
     * @param codigoCanal codigo del canal
     * @return ChequeraDTO lista los cheques de una chequera
     */
    public ChequeraDTO listarChequePorChequeraAct(String numeroCuenta, String numeroPrimerCheque, String codigoCanal);

    /**
     * Realiza la solicitud de chequeras de una cuenta corriente.
     *
     * @param numeroCuenta numero de la cuenta
     * @param tipoChequera tipo de chequera seleccionada por cantidad de cheques
     * @param cantidad cantidad de chequeras solicitadas
     * @param codigoCanal codigo del canal
     * @param retira nombre de la persona que retira las chequeras puede ser
     * null para el titular
     * @param identificacion nro de cedula de la persona que retira las
     * chequeras puede ser null para el titular
     * @param agenciaRetira codigo de agencia en la cual se retiran las
     * chequeras
     * @param idCanal identificador del canal
     * @return ChequeraDTO Listado de chequeras por cliente
     */
    public UtilDTO solicitarChequeraPN(String numeroCuenta, String tipoChequera, String cantidad, String identificacion, String retira, String agenciaRetira, String codigoCanal, String idCanal);

    /**
     * Realiza la suspension de un cheque o varios cheque de una cuenta
     * corriente.
     *
     * @param numeroCuenta numero de la cuenta
     * @param motivoSuspension motivo de la suspension
     * @param numPrimerCheque numero del primer cheque
     * @param numUltimoCheque numero del ultimo cheque
     * @param listCheques
     * @param nombreCanal nombre del canal
     * @param idCanal
     * @return Número de cheques que se lograron suspender, Numero de referencia
     * de la suspensión, Código de estatus de la operación indicando si la
     * solicitud fue realizada correctamente.
     */
    public UtilDTO suspenderChequera(String numeroCuenta, String motivoSuspension, String numPrimerCheque, String numUltimoCheque, String listCheques, String nombreCanal, String idCanal);

}
