/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.NotificacionesDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.OtpDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.NotificacionesWs;
import com.bds.wpn.ws.services.NotificacionesWs_Service;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author cesar.mujica
 */
@Named
@Stateless(name = "wpnNotificacionesDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class NotificacionesDAOImpl extends DAOUtil implements NotificacionesDAO {

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(NotificacionesDAOImpl.class.getName());

    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlNotificaciones = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/NotificacionesWs?WSDL";

    /**
     * Procesa el envio de Email con los parametros especificados
     *
     * @param remitente String Remitente del correo.
     * @param destinatario String Destinatario del correo.
     * @param asunto String Asunto del correo.
     * @param texto String Texto del correo.
     * @param idCanal String indentificador del canal en oracle11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es llamado
     * el procedimiento.
     * @return EMailDTO el email enviado al cliente
     */
    @Override
    public EMailDTO enviarEmail(String remitente, String destinatario, String asunto, String texto, String idCanal, String nombreCanal) {

        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.EMailDTO emailWs;
        EMailDTO emailDTO = new EMailDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            NotificacionesWs_Service service = new NotificacionesWs_Service(new URL(urlWsdlNotificaciones));
            NotificacionesWs port = service.getNotificacionesWsPort();
            //se obtiene el objeto de salida del WS
            emailWs = port.enviarEmail(remitente, destinatario, asunto, texto, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, emailWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                BeanUtils.copyProperties(emailDTO, emailWs);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio enviarEmail: ")
                    .append("USR-").append(remitente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN enviarEmail: ")
                    .append("USR-").append(remitente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            emailDTO.setRespuestaDTO(respuesta);
        }
        return emailDTO;

    }
    
    
    /**
     * Procesa el envio de Email con los parametros especificados
     *
     * @param remitente String Remitente del correo.
     * @param destinatario String Destinatario del correo.
     * @param asunto String Asunto del correo.
     * @param texto String Texto del correo.
     * @param idCanal String indentificador del canal en oracle11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es llamado
     * el procedimiento.
     * @return EMailDTO el email enviado al cliente
     */
    @Override
    public EMailDTO enviarEmailOTP(String codCliente, String idCanal,String nombreCanal, String codOtp) {

        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.EMailDTO emailWs;
        EMailDTO emailDTO = new EMailDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            NotificacionesWs_Service service = new NotificacionesWs_Service(new URL(urlWsdlNotificaciones));
            NotificacionesWs port = service.getNotificacionesWsPort();
            //se obtiene el objeto de salida del WS
            emailWs = port.enviarEmailOTP(codCliente, nombreCanal, codOtp);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, emailWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                BeanUtils.copyProperties(emailDTO, emailWs);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio enviarEmail: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN enviarEmail: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            emailDTO.setRespuestaDTO(respuesta);
        }
        return emailDTO;

    }

    /* Genera un nuevo OTP el cual es asignado al cliente asociado a una tarjeta de debito.
     * Adicionalmente, el OTP es enviado por correo electronico al cliente, solo en caso de que, 
     * tanto el canal, como el cliente esten configurados para dicho envio.
     * @param inCodigoCliente String codigo de cliente al cual se le va a generar el OTP.
     * @param nombreCanal String canal Codigo (extendido) del canal desde el cual es llamado el procedimiento.
     * @return OtpDTO   
     */
    @Override
    public OtpDTO generarOTP(String inCodigoCliente, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.OtpDTO otpWs;
        OtpDTO otpDTO = new OtpDTO();
        try {
            //invocacion del WS
            NotificacionesWs_Service service = new NotificacionesWs_Service(new URL(urlWsdlNotificaciones));
            NotificacionesWs port = service.getNotificacionesWsPort();
            //se obtiene el objeto de salida del WS
            otpWs = port.generarOTP(inCodigoCliente, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, otpWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                BeanUtils.copyProperties(otpDTO, otpWs);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio generarOTP: ")
                    .append("USR-").append(inCodigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN generarOTP: ")
                    .append("USR-").append(inCodigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            otpDTO.setRespuestaDTO(respuesta);
        }
        return otpDTO;
    }
    
    
    /* Genera un nuevo OTP el cual es asignado al cliente asociado a una tarjeta de debito.
     * Adicionalmente, el OTP es enviado por correo electronico al cliente, solo en caso de que, 
     * tanto el canal, como el cliente esten configurados para dicho envio.
     * @param inCodigoCliente String codigo de cliente al cual se le va a generar el OTP.
     * @param nombreCanal String canal Codigo (extendido) del canal desde el cual es llamado el procedimiento.
     * @return OtpDTO
     */
    @Override
    public OtpDTO generarOTPSinEmail(String inCodigoCliente, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.OtpDTO otpWs;
        OtpDTO otpDTO = new OtpDTO();
        try {
            //invocacion del WS
            NotificacionesWs_Service service = new NotificacionesWs_Service(new URL(urlWsdlNotificaciones));
            NotificacionesWs port = service.getNotificacionesWsPort();
            //se obtiene el objeto de salida del WS
            otpWs = port.generarOTPSinMail(inCodigoCliente, nombreCanal);
            
            
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, otpWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                BeanUtils.copyProperties(otpDTO, otpWs);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio generarOTP: ")
                    .append("USR-").append(inCodigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN generarOTP: ")
                    .append("USR-").append(inCodigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            otpDTO.setRespuestaDTO(respuesta);
        }
        return otpDTO;
    }

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
    @Override
    public OtpDTO validarOTP(String inCodigoCliente, String ivOTP, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.OtpDTO otpWs;
        OtpDTO otpDTO = new OtpDTO();
        try {
            //invocacion del WS
            NotificacionesWs_Service service = new NotificacionesWs_Service(new URL(urlWsdlNotificaciones));
            NotificacionesWs port = service.getNotificacionesWsPort();
            //se obtiene el objeto de salida del WS
            otpWs = port.validarOTP(inCodigoCliente, ivOTP, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, otpWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                BeanUtils.copyProperties(otpDTO, otpWs);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio validarOTP: ")
                    .append("USR-").append(inCodigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN validarOTP: ")
                    .append("USR-").append(inCodigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            otpDTO.setRespuestaDTO(respuesta);
        }
        return otpDTO;
    }
    
    
    /**
     * Procesa el envio de SMS con los parametros especificados
     *    
     * @param numeroTlf String Asunto del correo.
     * @param texto String Texto del correo.
     * @param idCanal String indentificador del canal en oracle11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es llamado
     * el procedimiento.
     * @param motivoSMS String justificacion del envio del SMS
     * @return EMailDTO el email enviado al cliente
     */
    @Override
    public RespuestaDTO enviarSMS(String numeroTlf, String texto, String idCanal, String nombreCanal, String motivoSMS) {

        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            if(numeroTlf != null && !numeroTlf.trim().equalsIgnoreCase("")){
                //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
                ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

                //invocacion del WS
                NotificacionesWs_Service service = new NotificacionesWs_Service(new URL(urlWsdlNotificaciones));
                NotificacionesWs port = service.getNotificacionesWsPort();
                //se obtiene el objeto de salida del WS
                respuestaWs = port.enviarSMS(numeroTlf, texto, idCanal);
                //clase para casteo dinamico de atributos
                BeanUtils.copyProperties(respuesta, respuestaWs);            
                //validacion de codigo de respuesta
                if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    throw new ServiceException();
                } 
            }else{
                throw new Exception();
            }            

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio enviarSMS: ")
                    .append("TLF-").append(numeroTlf)
                    .append("-SMS-").append(motivoSMS)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN enviarSMS: ")
                    .append("TLF-").append(numeroTlf)
                    .append("-SMS-").append(motivoSMS)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }
        return respuesta;

    }
    
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
    @Override
    public RespuestaDTO enviarEmailPN(String codigoPlantilla, String destinatario, String parametrosCorreo, String idCanal, String codigoCanal) {

        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            NotificacionesWs_Service service = new NotificacionesWs_Service(new URL(urlWsdlNotificaciones));
            NotificacionesWs port = service.getNotificacionesWsPort();
            //se obtiene el objeto de salida del WS
            respuestaWs = port.enviarEmailPN(codigoPlantilla, destinatario, parametrosCorreo, idCanal, codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } 

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio enviarEmailPN: ")
                    .append("DEST-").append(destinatario)
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN enviarEmailPN: ")
                    .append("DEST-").append(destinatario)
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } 
        return respuesta;

    }

}
