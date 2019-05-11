/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.UtilDTO;
import java.math.BigDecimal;

/**
 *
 * @author cesar.mujica
 */
public interface P2PDAO {
    
    /**
     * invoca al WS de pagos P2P en DelSur.
     *
     * @param codCanalP2P
     * @param nroEmisor
     * @param idCanal String
     * @param nroBeneficiario
     * @param monto
     * @param conceptoPago
     * @param identificacionPagador
     * @param url
     * @param frecuente
     * @param alias
     * @param idUsuario
     * @param identificacionBeneficiario
     * @param nombreCanal String
     * @param tipoDocumento
     * @return RespuestaDTO
     */
    public UtilDTO realizarPagoP2PTercerosDelsur(String codCanalP2P, String nroEmisor, String nroBeneficiario, String identificacionBeneficiario, String monto, String conceptoPago, String identificacionPagador, String url, boolean frecuente, String alias, BigDecimal idUsuario, String tipoDocumento, String idCanal, String nombreCanal);
    
    /**
     * invoca al WS de pagos P2P en Otros Bancos.
     *
     * @param codCanalP2P
     * @param nroEmisor
     * @param idCanal String
     * @param nroBeneficiario
     * @param monto
     * @param conceptoPago
     * @param codBanco
     * @param identificacionPagador
     * @param url
     * @param frecuente
     * @param alias
     * @param identificacionBeneficiario
     * @param tipoDocumento
     * @param idUsuario
     * @param nombreCanal String
     * @return RespuestaDTO
     */
    public UtilDTO realizarPagoP2PTercerosOtrosBancos(String codCanalP2P, String nroEmisor, String nroBeneficiario, String identificacionBeneficiario, String monto, String conceptoPago, String codBanco, String identificacionPagador, String url, boolean frecuente, String alias, BigDecimal idUsuario, String tipoDocumento, String idCanal, String nombreCanal);
    
    
}
