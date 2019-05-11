/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.ChequeraDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.MovimientoCuentaDTO;
import com.bds.wpn.dto.TransaccionesCuentasDTO;
import com.bds.wpn.dto.UtilDTO;

/**
 *
 * @author cesar.mujica
 */
public interface CuentaDAO {

    /**
     * Retorna los datos basicos de la cuenta mediante la cedula y el canal
     *
     * @param tipoCuenta String tipo de cuenta (Ahorro, Corriente)
     * @param numeroCuenta String numero de cuenta
     * @param idCanal String con el id del canal
     * @param nombreCanal String canal por el cual se realiza la consulta
     * @return CuentaDTO datos de la cuenta en core bancario
     */
    public CuentaDTO obtenerDetalleCuenta(String tipoCuenta, String numeroCuenta, String idCanal, String nombreCanal);

    /**
     * Retorna los movimientos para la cuenta seleccionada
     *
     * @param tipoCuenta String tipo de cuenta (BCC, BCA, BME) -> ver constantes
     * de tipos producto
     * @param numeroCuenta String numero de cuenta
     * @param fechaIni String fecha de incio del filtro
     * @param fechaFin String fecha de fin del filtro
     * @param regIni String registro de Inicio para la paginacion
     * @param regFin String registro de fin para la paginacion
     * @param tipoOrdenFecha String tipo de Orden por Fecha: ASC(por defecto),
     * DESC
     * @param idCanal String con el id del canal
     * @param nombreCanal canal por el cual se realiza la consulta
     * @return CuentaDTO la cuenta con los movimientos de la cuenta
     * seleccionada(solo vienen los datos de los movimientos)
     */
    public CuentaDTO listarMovimientos(String tipoCuenta, String numeroCuenta, String fechaIni, String fechaFin,
            String regIni, String regFin, String tipoOrdenFecha, String idCanal, String nombreCanal);

    /**
     * Metodo que consulta el detalle para el movimiento de una cuenta
     *
     * @param secuenciamovimiento String secuencia del movimiento de la cuenta
     * @param idCanal String con el id del canal
     * @param nombreCanal codigo de canal
     * @return MovimientoCuentaDTO el movimiento con la informacion detallada
     */
    public MovimientoCuentaDTO detalleMovimiento(String secuenciamovimiento, String idCanal, String nombreCanal);
    
     /**
     * Obtiene los saldos bloqueados de una cuenta
     * @param tipoCuenta tipo de cuenta BCA o BCC
     * @param numeroCuenta numero de la cuenta
     * @param codigoCanal codigo del canal
     * @param idCanal
     * @return listado de Saldos Bloqueados de una Cuenta
     */
    public CuentaDTO listadoSaldoBloqueadoCuenta(String tipoCuenta, String numeroCuenta, String codigoCanal, String idCanal);
    
    /**
     * Obtiene los saldos diferidos de una cuenta
     * @param tipoCuenta tipo de cuenta BCA o BCC
     * @param numeroCuenta numero de la cuenta
     * @param codigoCanal codigo del canal
     * @param idCanal
     * @return listado de Saldos diferidos de una Cuenta
     */
    public CuentaDTO listadoSaldoDiferidoCuenta(String tipoCuenta, String numeroCuenta, String codigoCanal, String idCanal);
    
    /**
     * Realiza la solicitud de chequeras de una cuenta corriente.
     *
     * @param numeroCuenta numero de la cuenta
     * @param tipoChequera tipo de chequera seleccionada por cantidad de cheques
     * @param cantidad cantidad de chequeras solicitadas
     * @param codigoCanal codigo del canal
     * @param retira nombre de la persona que retira las chequeras puede ser null para el titular
     * @param identificacion nro de cedula de la persona que retira las chequeras puede ser null para el titular
     * @param agenciaRetira codigo de agencia en la cual se retiran las chequeras
     * @param idCanal identificador del canal 
     * @return ChequeraDTO Listado de chequeras por cliente
     */
    public UtilDTO solicitarChequeras(String numeroCuenta, String tipoChequera, String cantidad, String identificacion, String retira, String agenciaRetira, String codigoCanal, String idCanal);
    
    /**
     * Obtiene las chequeras entregadas de una cuenta.
     *
     * @param numeroCuenta numero de cuenta
     * @param codigoCanal codigo del canal
     * @param idCanal identificador del canal 
     * @return CuentaDTO Listado de las chequeras entregadas
     */
    public CuentaDTO listarChequerasEntregadas(String numeroCuenta, String codigoCanal, String idCanal);
    
    /**
     * Obtiene el listado de cheques activos de una chequera
     *
     * @param numeroCuenta numero de cuenta
     * @param numeroPrimerCheque numero del primer cheque de una chequera
     * @param codigoCanal codigo del canal
     * @param idCanal identificador del canal 
     * @return CuentaDTO Listado de las chequeras entregadas
     */
    public ChequeraDTO listarChequesPorChequeraAct(String numeroCuenta,String numeroPrimerCheque, String codigoCanal, String idCanal);
    
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
    public UtilDTO suspenderChequeras(String numeroCuenta, String motivoSuspension, String numPrimerCheque, String numUltimoCheque, String listCheques, String nombreCanal, String idCanal);


    /**
     * Obtiene el estado de cuenta.
     * @param tipoCuenta tipo de cuenta BCA o BCC
     * @param numeroCuenta numero de la cuenta
     * @param fechaIni fecha inicial
     * @param fechaFin fecha final
     * @param codigoCanal codigo del canal
     * @param idCanal
     * @return estado de cuenta.
     */ 
 
    public CuentaDTO estadoCuenta(String tipoCuenta,String numeroCuenta,String fechaIni, String fechaFin, String codigoCanal, String idCanal);


/**
     * retorna los movimientos para la cuenta seleccionada
     *
     * @param tipoCuenta String tipo de cuenta (BCC, BCA, BME) -> ver constantes
     * de tipos producto
     * @param numeroCuenta String numero de cuenta
     * @param fechaIni String fecha de incio del filtro
     * @param fechaFin String fecha de fin del filtro
     * @param regIni String registro de Inicio para la paginacion
     * @param regFin String registro de fin para la paginacion
     * @param tipoOrdenFecha String tipo de Orden por Fecha: ASC(por defecto),
     * DESC
     * @param codigoCanal codigo canal por el cual se realiza la consulta
     * @param nombreCanal canal por el cual se realiza la consulta
     * @param codTransaccion Codigo de transaccion
     * @param idCanal
     * @return CuentaDTO la cuenta con los movimientos de la cuenta
     * seleccionada(solo vienen los datos de los movimientos)
     */
    public CuentaDTO listarMovimientosPorTransaccion(String tipoCuenta, String numeroCuenta, String fechaIni, String fechaFin,
            String regIni, String regFin, String tipoOrdenFecha, String codigoCanal, String nombreCanal,String codTransaccion, String idCanal);

    

    /**
     * Método para recuperar el liatod movimientos de las cuentas
     * @param tipoCuenta tipo de cuenta, Corriente, Ahorro
     * @param nombreCanal nombre del Canal
     * @param idCanal
     * @return 
     */
    public TransaccionesCuentasDTO listadoTransaccionesCuentas(String tipoCuenta, String nombreCanal, String idCanal);
        
    /**
     * Método retorna el saldo disponible de una cta
     *
     * @param nroCuenta numero de cta de 20 digitos
     * @param idCanal
     * @param codigoCanal
     *
     * @return UtilDTO
     */
    public UtilDTO obtenerSaldoDisponibleCta(String nroCuenta, String idCanal, String codigoCanal);
    
    /**
     * Obtiene el listado de cheques utilizados de una chequera
     *
     * @param numeroCuenta numero de cuenta
     * @param numeroPrimerCheque numero del primer cheque de una chequera
     * @param codigoCanal codigo del canal
     * @param idCanal identificador del canal 
     * @return CuentaDTO Listado de las chequeras entregadas
     */
    public ChequeraDTO listarChequesPorChequera(String numeroCuenta,String numeroPrimerCheque, String codigoCanal, String idCanal);
    

}
