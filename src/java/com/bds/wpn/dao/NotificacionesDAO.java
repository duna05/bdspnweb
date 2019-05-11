/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.OtpDTO;
import com.bds.wpn.dto.RespuestaDTO;

/**
 *
 * @author cesar.mujica
 */
public interface NotificacionesDAO {

    /**
     * Procesa el envio de Email con los parametros especificados
     *
     * @param remitente String Remitente del correo.
     * @param destinatario String Destinatario del correo.
     * @param asunto String Asunto del correo.
     * @param texto String Texto del correo.
     * @param idCanal String indentificador del canal en oracle11
     * @param nombreCanal Codigo (extendido) del canal desde el cual es llamado
     * el procedimiento.
     * @return EMailDTO el email enviado al cliente
     */
    public EMailDTO enviarEmail(String remitente, String destinatario, String asunto, String texto, String idCanal, String nombreCanal);
     /**
     * Procesa el envio de Email con los parametros especificados
     *
     * @param codCliente String codigo de cliente del sur.
     * @param codOtp String codigo que se va a enviar via correo electronico 
     * @param nombreCanal Codigo (extendido) del canal desde el cual es llamado
     * @param idCanal String indentificador del canal en oracle11
     * el procedimiento.
     * @return EMailDTO el email enviado al cliente
     */
    public EMailDTO enviarEmailOTP(String codCliente, String idCanal,String nombreCanal, String codOtp);
    
    /* Genera un nuevo OTP el cual es asignado al cliente asociado a una tarjeta de debito.
     * Adicionalmente, el OTP es enviado por correo electronico al cliente, solo en caso de que, 
     * tanto el canal, como el cliente esten configurados para dicho envio.
     * @param inCodigoCliente String codigo de cliente al cual se le va a generar el OTP.
     * @param idCanal indentificador del canal en oracle11
     * @param nombreCanal String canal Codigo (extendido) del canal desde el cual es llamado el procedimiento.
     * @return OtpDTO
     */
    public OtpDTO generarOTP(String inCodigoCliente, String idCanal, String nombreCanal);
    
      /* Genera un nuevo OTP el cual es asignado al cliente asociado a una tarjeta de debito.
     * Adicionalmente, el OTP es enviado por correo electronico al cliente, solo en caso de que, 
     * tanto el canal, como el cliente esten configurados para dicho envio.
     * @param inCodigoCliente String codigo de cliente al cual se le va a generar el OTP.
     * @param idCanal indentificador del canal en oracle11
     * @param nombreCanal String canal Codigo (extendido) del canal desde el cual es llamado el procedimiento.
     * @return OtpDTO
     */
    public OtpDTO generarOTPSinEmail(String inCodigoCliente, String idCanal, String nombreCanal);

    /**
     * Valida un OTP. El OTP es valido solo si coincide con el OTP generado
     * previamente. Nota: Se tomara siempre el ultimo OTP generado.
     *
     * @param inCodigoCliente String codigo de cliente al cual se le va a
     * generar el OTP.
     * @param ivOTP String OTP que va a ser validado.
     * @param idCanal String indentificador del canal en oracle11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es
     * llamado el procedimiento.
     * @return OtpDTO
     */
    public OtpDTO validarOTP(String inCodigoCliente, String ivOTP, String idCanal, String nombreCanal);

    /**
     * Procesa el envio de SMS con los parametros especificados
     *
     * @param numeroTlf String Asunto del correo.
     * @param texto String Texto del correo.
     * @param idCanal String indentificador del canal en oracle11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es llamado
     * el procedimiento.
     * @param motivoSMS String justificacion del motivo del SMS
     * @return EMailDTO el email enviado al cliente
     */
    public RespuestaDTO enviarSMS(String numeroTlf, String texto, String idCanal, String nombreCanal, String motivoSMS);
    
    /**
     * Procesa el envio de Email con los parametros especificados
     *
     * @param codigoPlantilla codigo de la plantilla
     * @param destinatario String Destinatario del correo.
     * @param parametrosCorreo parametros requeridos para la plantilla
     * @param idCanal codigo del canal interno en ib
     * @param codigoCanal codigo del canal interno en el CORE
     * @return EMailDTO la cuenta con los movimientos de la cuenta
     * seleccionada(solo vienen los datos de los movimientos)
     */
    public RespuestaDTO enviarEmailPN(String codigoPlantilla, String destinatario, String parametrosCorreo, String idCanal, String codigoCanal);

}
