/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.PosicionConsolidadaDTO;
import com.bds.wpn.dto.UtilDTO;

/**
 *
 * @author cesar.mujica
 */
public interface ClienteDAO {

    /**
     * Realiza la validacion de la clave con la ficha del cliente.
     *
     * @param codigoCliente codigo del cliente
     * @param claveCifrada clave cifrada
     * @param nombreCanal codigo del canal
     * @param idCanal identificador unico del canal
     * @return UtilDTO retorna la validacion de la clave
     */
    public UtilDTO validarClaveFondo(String codigoCliente, String claveCifrada, String nombreCanal, String idCanal);

    /**
     * Retorna los datos de los productos del cliente
     *
     * @param codigoCliente codigo del cliente
     * @param idCanal String con el id del canal
     * @param canal canal por el cual se realiza la consulta
     * @return PosicionConsolidadaDTO datos de productos de cliente en core
     * bancario
     */
    public PosicionConsolidadaDTO consultarPosicionConsolidadaCliente(String codigoCliente, String idCanal, String canal);

    /**
     * Metodo que obtiene el listado de cuentas de ahorro y corrientes que posee
     * un cliente
     *
     * @param codigoCliente codigo del cliente
     * @param idCanal String con el id del canal
     * @param nombreCanal codigo de canal
     * @return ClienteDTO listado de cuentas de ahorro y corrientes que posee un
     * cliente
     */
    public ClienteDTO listadoCuentasClientes(String codigoCliente, String idCanal, String nombreCanal);

    /**
     * Método que obtiene el listado de cuentas de ahorro y corriente, activas y
     * canceladas, que posee un cliente, asociadas a una TDD en específico
     *
     * @param codigoCliente codigo del cliente
     * @param tdd tarjeta de débito
     * @param idCanal String con el id del canal
     * @param nombreCanal codigo de canal
     * @return ClienteDTO listado de cuentas de ahorro y corrientes que posee un
     * cliente
     */
    public ClienteDTO listCuentasActivasCanceladas(String codigoCliente, String tdd, String idCanal, String nombreCanal);

    /**
     * Metodo para Valida cliente con Identificacion cliente y No. De cuenta
     *
     * @param identificacion identificacion del cliente se debe incluir letra
     * ej: v123456
     * @param nroCuenta numero de cuenta del cliente
     * @param idCanal id del canal
     * @param codigoCanal nombre del canal
     * @return retorna 'V' para casos validos, 'F' en caso contrario.
     */
    public UtilDTO validarIdentificacionCuenta(String identificacion, String nroCuenta, String idCanal, String codigoCanal);

    /**
     * Metodo para validar y obtener un codigo de usuario existente por numero
     * de cedula
     *
     * @param identificacion identificacion del cliente se debe incluir letra
     * ej: v123456
     * @param codigoCanal nombre del canal
     * @param idCanal id del canal
     * @return en caso de existir retorna el parametro codUsuario en el mapa con
     * el valor correspondiente
     */
    public UtilDTO obtenerCodCliente(String identificacion, String codigoCanal, String idCanal);

    /**
     * Retorna los datos basicos del cliente mediante el codigo y el canal
     *
     * @param iCodigoCliente codigo del cliente
     * @param idCanal String con el id del canal
     * @param canal canal por el cual se realiza la consulta
     * @return ClienteDTO datos del cliente en core bancario
     */
    public ClienteDTO consultarDatosCliente(String iCodigoCliente, String idCanal, String canal);

    /**
     * Metodo para validar el rif de un cliente
     *
     * @param rif rif 'v1234567890'
     * @param codigoCanal nombre del canal
     * @return retorna 'S' para casos validos, 'N' en caso contrario.
     */
    public UtilDTO validarRif(String rif, String codigoCanal);

    /**
     * Metodo que obtiene el listado de cuentas corrientes que posee un cliente
     *
     * @param codigoCliente codigo del cliente
     * @param idCanal String con el id del canal
     * @param nombreCanal codigo de canal
     * @return ClienteDTO listado de cuentas de ahorro y corrientes que posee un
     * cliente
     */
    public ClienteDTO listadoCuentasCorrientesCliente(String codigoCliente, String idCanal, String nombreCanal);

}
