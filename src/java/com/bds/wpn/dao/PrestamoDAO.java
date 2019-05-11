/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.PrestamoDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;

/**
 *
 * @author rony.rodriguez
 */
public interface PrestamoDAO {

    /**
     * Obtiene el detalle de un prestamo
     *
     * @param numeroPrestamo String
     * @param idCanal String
     * @param nombreCanal String
     * @return PrestamoDTO
     */
    public PrestamoDTO obtenerDetallePrestamo(String numeroPrestamo, String idCanal, String nombreCanal);

    /**
     * Retorna el listado de prestamo de un cliente consultando su codigo.
     *
     * @param codigoCliente String
     * @param idCanal String
     * @param nombreCanal String
     * @return ClienteDTO
     */
    public ClienteDTO listadoPrestamoPorCliente(String codigoCliente, String idCanal, String nombreCanal);
    
    /**
     * Retorna el listado de prestamo de un cliente consultando su codigo, Amortización de Préstamos
     *
     * @param codigoCliente String
     * @param idCanal String
     * @param nombreCanal String
     * @return ClienteDTO
     */
    public ClienteDTO listadoPrestamoPorClienteAP(String codigoCliente, String idCanal, String nombreCanal);

    /**
     * retorna los pagos para el prestamo seleccionado
     *
     * @param iNumeroPrestamo String numero de cuenta
     * @param fechaIni String fecha de incio del filtro
     * @param fechaFin String fecha de fin del filtro
     * @param regIni String registro de Inicio para la paginacion
     * @param regFin String registro de fin para la paginacion
     * @param tipoOrdenFecha String tipo de Orden por Fecha: ASC(por defecto),
     * DESC
     * @param nombreCanal
     * @param idCanal
     * @return CuentaDTO la cuenta con los movimientos de la cuenta
     * seleccionada(solo vienen los datos de los movimientos)
     */
    public PrestamoDTO listadoPagosPrestamo(String iNumeroPrestamo, String fechaIni, String fechaFin,
            String regIni, String regFin, String tipoOrdenFecha, String nombreCanal, String idCanal);
    
    
     /**
     * Realiza el pago del prestamo.
     *
     * @param iNumeroPrestamo numero del prestamo
     * @param ivNumeroCuenta numero de la cuenta
     * @param inValorTransaccion monto de la transaccion
     * @param ivCodigoUsuario codigo del usuario
     * @param ivDescripcionMovimiento descripcion del movimiento
     * @param codigoCanal codigo del canal
     * @param idCanal id del canal
     * @return PrestamoDTO Los datos de un prestamos especifico
     */
    public UtilDTO aplicarPagoPrestamo(String iNumeroPrestamo, String ivNumeroCuenta, String inValorTransaccion, String ivCodigoUsuario, String ivDescripcionMovimiento, String codigoCanal, String idCanal);
}
