/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.TransaccionDTO;
import java.math.BigDecimal;

/**
 *
 * @author cesar.mujica
 */
public interface TranferenciasYPagosDAO {

    /**
     * Metodo que se encarga de realizar la transferencia entre dos cuentas del
     * mismo banco
     *
     * @param cuentaOrigen String Numero (de 20 digitos) de la cuenta origen de
     * los fondos.
     * @param cuentaDestino String Numero de cuenta, de 20 digitos, destino de
     * los fondos.
     * @param monto BigDecimal Monto en Bs. de la transferencia.
     * @param descripcion String Descripcion, de la transferencia, suministrada
     * por el cliente que la realiza.
     * @param idCanal String identificador del canal en Oracle 11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es
     * llamado el procedimiento.
     * @return TransaccionDTO objeto de respuesta con el codigo del resultado de
     * la transaccion
     */
    public TransaccionDTO transferenciaMismoBanco(String cuentaOrigen, String cuentaDestino, BigDecimal monto, String descripcion, String idCanal, String nombreCanal);

    /**
     * Metodo que se encarga de realizar la transferencia entre dos cuentas
     * propias del mismo banco
     *
     * @param cuentaOrigen String Numero (de 20 digitos) de la cuenta origen de
     * los fondos.
     * @param cuentaDestino String Numero de cuenta, de 20 digitos, destino de
     * los fondos.
     * @param monto String Monto en Bs. de la transferencia.
     * @param descripcion String Descripcion, de la transferencia, suministrada
     * por el cliente que la realiza.
     * @param idCanal String identificador del canal en Oracle 11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es
     * llamado el
     * @return TransaccionDTO objeto de respuesta con el codigo del resultado de
     * la transaccion
     */
    public TransaccionDTO transferenciaPropiasMismoBanco(String cuentaOrigen, String cuentaDestino, BigDecimal monto, String descripcion, String idCanal, String nombreCanal);

    /**
     * Metodo que se encarga de realizar la transferencia entre dos cuentas
     * hacia otro banco
     *
     * @param cuentaOrigen String Numero (de 20 digitos) de la cuenta origen de
     * los fondos.
     * @param cuentaBeneficiario String Numero de cuenta, de 20 digitos, destino
     * de los fondos.
     * @param idBeneficiario String Identificacion (numero de cedula, RIF, etc.)
     * del beneficiario del pago.
     * @param nombreBeneficiario String Nombre y apellido del beneficiario de la
     * transferencia.
     * @param monto BigDecimal Monto en Bs. de la transferencia.
     * @param descripcion String Descripcion, de la transferencia, suministrada
     * por el cliente que la realiza.
     * @param idCanal String identificador del canal en Oracle 11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es
     * llamado el
     * @return TransaccionDTO objeto de respuesta con el codigo del resultado de
     * la transaccion
     */
    public TransaccionDTO transferenciaOtroBanco(String cuentaOrigen, String cuentaBeneficiario, String idBeneficiario, String nombreBeneficiario, BigDecimal monto, String descripcion, String idCanal, String nombreCanal);

    /**
     * Metodo que se encarga de realizar el pago de un TDC del mismo banco
     *
     * @param cuentaOrigen String Numero (de 20 digitos) de la cuenta origen de
     * los fondos.
     * @param tdcBeneficiario String Numero de la tarjeta de credito que se
     * desea pagar.
     * @param monto Monto en Bs. de la transferencia.
     * @param descripcion String Descripcion, de la transferencia, suministrada
     * por el cliente que la realiza.
     * @param idCanal String identificador del canal en Oracle 11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es
     * llamado el
     * @return TransaccionDTO objeto de respuesta con el codigo del resultado de
     * la transaccion
     */
    public TransaccionDTO pagoTDCMismoBanco(String cuentaOrigen, String tdcBeneficiario, BigDecimal monto, String descripcion, String idCanal, String nombreCanal);

    /**
     * Metodo que se encarga de realizar el pago de un TDC Propia del mismo
     * banco
     *
     * @param cuentaOrigen String Numero (de 20 digitos) de la cuenta origen de
     * los fondos.
     * @param tdcBeneficiario String Numero de la tarjeta de credito que se
     * desea pagar.
     * @param monto BigDecimal Monto en Bs. de la transferencia.
     * @param descripcion String Descripcion, de la transferencia, suministrada
     * por el cliente que la realiza.
     * @param idCanal String identificador del canal en Oracle 11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es
     * llamado el
     * @return TransaccionDTO objeto de respuesta con el codigo del resultado de
     * la transaccion
     */
    public TransaccionDTO pagoTDCPropiaMismoBanco(String cuentaOrigen, String tdcBeneficiario, BigDecimal monto, String descripcion, String idCanal, String nombreCanal);

    /**
     * Metodo que se encarga de realizar la transferencia entre dos cuentas
     * hacia otro banco
     *
     * @param cuentaOrigen String Numero (de 20 digitos) de la cuenta origen de
     * los fondos.
     * @param tdcBeneficiario String Numero de cuenta, de 20 digitos, destino de
     * los fondos.
     * @param idBeneficiario String Identificacion (numero de cedula, RIF, etc.)
     * del beneficiario del pago.
     * @param nombreBeneficiario String Nombre y apellido del beneficiario de la
     * transferencia.
     * @param monto BigDecimal Monto en Bs. de la transferencia.
     * @param descripcion String Descripcion, de la transferencia, suministrada
     * por el cliente que la realiza.
     * @param idCanal String identificador del canal en Oracle 11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es
     * llamado el
     * @return TransaccionDTO objeto de respuesta con el codigo del resultado de
     * la transaccion
     */
    public TransaccionDTO pagoTDCOtroBanco(String cuentaOrigen, String tdcBeneficiario, String idBeneficiario, String nombreBeneficiario, BigDecimal monto, String descripcion, String idCanal, String nombreCanal);

}
