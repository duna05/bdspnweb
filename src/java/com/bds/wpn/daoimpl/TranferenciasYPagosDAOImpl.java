/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.controller.TextosController;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dao.TranferenciasYPagosDAO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.TransaccionDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.TransferenciasYPagosWS;
import com.bds.wpn.ws.services.TransferenciasYPagosWS_Service;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author cesar.mujica
 */
@Named
@Stateless(name = "wpnTranferenciasYPagosDAOweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class TranferenciasYPagosDAOImpl extends DAOUtil implements TranferenciasYPagosDAO {

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(TranferenciasYPagosDAOImpl.class.getName());

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;

    //este sera el WSDL del servicio a consumir en este controlador
    private final String wsdlServiciosTransfyPagos = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/TransferenciasYPagosWS?WSDL";

    /**
     * Metodo que se encarga de realizar la transferencia entre dos cuentas del
     * mismo banco
     *
     * @param cuentaOrigen String Numero (de 20 digitos) de la cuenta origen de los
     * fondos.
     * @param cuentaDestino String Numero de cuenta, de 20 digitos, destino de los
     * fondos.
     * @param monto BigDecimal Monto en Bs. de la transferencia.
     * @param descripcion String Descripcion, de la transferencia, suministrada por el
     * cliente que la realiza.
     * @param idCanal String identificador del canal en Oracle 11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es llamado
     * el procedimiento.
     * @return TransaccionDTO objeto de respuesta con el codigo del resultado de
     * la transaccion
     */
    @Override
    public TransaccionDTO transferenciaMismoBanco(String cuentaOrigen, String cuentaDestino, BigDecimal monto, String descripcion, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        TransaccionDTO transaccionDTO = new TransaccionDTO();
        com.bds.wpn.ws.services.TransaccionDTO transaccionWs;
        try {
            //invocacion del WS
            TransferenciasYPagosWS_Service service = new TransferenciasYPagosWS_Service(new URL(wsdlServiciosTransfyPagos));
            TransferenciasYPagosWS port = service.getTransferenciasYPagosWSPort();
            //se obtiene el objeto de salida del WS
            transaccionWs = port.transferenciaMismoBanco(cuentaOrigen, cuentaDestino, monto.setScale(2, BigDecimal.ROUND_HALF_UP), descripcion, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, transaccionWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                BeanUtils.copyProperties(transaccionDTO, transaccionWs);
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR al consumir el servicio transferenciaMismoBanco: ")
                    .append("ORG-").append(this.formatoAsteriscosWeb(cuentaOrigen))
                    .append("DST-").append(this.formatoAsteriscosWeb(cuentaDestino))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR al consumir el servicio transferenciaMismoBanco: ")
                    .append("ORG-").append(this.formatoAsteriscosWeb(cuentaOrigen))
                    .append("DST-").append(this.formatoAsteriscosWeb(cuentaDestino))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            this.notificarErrores(e, idCanal, nombreCanal,
                    parametrosController.getNombreParametro("pnw.services.wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("pnw.delsur.soporte.email", idCanal),
                    textosController.getNombreTexto("pnw.errores.titulo.emailNotificacion", idCanal), parametrosController.getNombreParametro("pnw.delsur.soporte.tlfs", idCanal),
                    textosController.getNombreTexto("pnw.sms.motivo.notificacionError", idCanal));
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            transaccionDTO.setRespuestaDTO(respuesta);
        }
        return transaccionDTO;
    }

    /**
     * Metodo que se encarga de realizar la transferencia entre dos cuentas
     * propias del mismo banco
     *
     * @param cuentaOrigen String Numero (de 20 digitos) de la cuenta origen de los
     * fondos.
     * @param cuentaDestino String Numero de cuenta, de 20 digitos, destino de los
     * fondos.
     * @param monto BigDecimal Monto en Bs. de la transferencia.
     * @param descripcion String Descripcion, de la transferencia, suministrada por el
     * cliente que la realiza.
     * @param idCanal String identificador del canal en Oracle 11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es llamado
     * el procedimiento.
     * @return TransaccionDTO objeto de respuesta con el codigo del resultado de
     * la transaccion
     */
    @Override
    public TransaccionDTO transferenciaPropiasMismoBanco(String cuentaOrigen, String cuentaDestino, BigDecimal monto, String descripcion, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        TransaccionDTO transaccionDTO = new TransaccionDTO();
        com.bds.wpn.ws.services.TransaccionDTO transaccionWs;
        try {
            //invocacion del WS
            TransferenciasYPagosWS_Service service = new TransferenciasYPagosWS_Service(new URL(wsdlServiciosTransfyPagos));
            TransferenciasYPagosWS port = service.getTransferenciasYPagosWSPort();
            //se obtiene el objeto de salida del WS
            transaccionWs = port.transferenciaPropiasMismoBanco(cuentaOrigen, cuentaDestino, monto.setScale(2, BigDecimal.ROUND_HALF_UP), descripcion, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, transaccionWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                BeanUtils.copyProperties(transaccionDTO, transaccionWs);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR al consumir el servicio transferenciaPropiasMismoBanco: ")
                    .append("ORG-").append(this.formatoAsteriscosWeb(cuentaOrigen))
                    .append("DST-").append(this.formatoAsteriscosWeb(cuentaDestino))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR al consumir el servicio transferenciaPropiasMismoBanco: ")
                    .append("ORG-").append(this.formatoAsteriscosWeb(cuentaOrigen))
                    .append("DST-").append(this.formatoAsteriscosWeb(cuentaDestino))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            this.notificarErrores(e, idCanal, nombreCanal, 
                    parametrosController.getNombreParametro("pnw.services.wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("pnw.delsur.soporte.email", idCanal), 
                    textosController.getNombreTexto("pnw.errores.titulo.emailNotificacion", idCanal), parametrosController.getNombreParametro("pnw.delsur.soporte.tlfs", idCanal), 
                    textosController.getNombreTexto("pnw.sms.motivo.notificacionError", idCanal));
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            transaccionDTO.setRespuestaDTO(respuesta);
        }
        return transaccionDTO;
    }

    /**
     * Metodo que se encarga de realizar la transferencia entre dos cuentas
     * hacia otro banco
     *
     * @param cuentaOrigen Numero (de 20 digitos) de la cuenta origen de los
     * fondos.
     * @param cuentaBeneficiario Numero de cuenta, de 20 digitos, destino de los
     * fondos.
     * @param idBeneficiario Identificacion (numero de cedula, RIF, etc.) del
     * beneficiario del pago.
     * @param nombreBeneficiario Nombre y apellido del beneficiario de la
     * transferencia.
     * @param monto Monto en Bs. de la transferencia.
     * @param descripcion Descripcion, de la transferencia, suministrada por el
     * cliente que la realiza.
     * @param idCanal identificador del canal en Oracle 11
     * @param nombreCanal Codigo (extendido) del canal desde el cual es llamado
     * el procedimiento.
     * @return TransaccionDTO objeto de respuesta con el codigo del resultado de
     * la transaccion
     */
    @Override
    public TransaccionDTO transferenciaOtroBanco(String cuentaOrigen, String cuentaBeneficiario, String idBeneficiario, String nombreBeneficiario,
            BigDecimal monto, String descripcion, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        TransaccionDTO transaccionDTO = new TransaccionDTO();
        com.bds.wpn.ws.services.TransaccionDTO transaccionWs;
        try {
            //invocacion del WS
            TransferenciasYPagosWS_Service service = new TransferenciasYPagosWS_Service(new URL(wsdlServiciosTransfyPagos));
            TransferenciasYPagosWS port = service.getTransferenciasYPagosWSPort();
            //se obtiene el objeto de salida del WS
            transaccionWs = port.transferenciaOtroBanco(cuentaOrigen, cuentaBeneficiario, idBeneficiario, nombreBeneficiario, monto.setScale(2, BigDecimal.ROUND_HALF_UP), descripcion, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, transaccionWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                BeanUtils.copyProperties(transaccionDTO, transaccionWs);
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR al consumir el servicio transferenciaOtroBanco: ")
                    .append("ORG-").append(this.formatoAsteriscosWeb(cuentaOrigen))
                    .append("DST-").append(this.formatoAsteriscosWeb(cuentaBeneficiario))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR al consumir el servicio transferenciaOtroBanco: ")
                    .append("ORG-").append(this.formatoAsteriscosWeb(cuentaOrigen))
                    .append("DST-").append(this.formatoAsteriscosWeb(cuentaBeneficiario))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            this.notificarErrores(e, idCanal, nombreCanal, 
                    parametrosController.getNombreParametro("pnw.services.wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("pnw.delsur.soporte.email", idCanal), 
                    textosController.getNombreTexto("pnw.errores.titulo.emailNotificacion", idCanal), parametrosController.getNombreParametro("pnw.delsur.soporte.tlfs", idCanal), 
                    textosController.getNombreTexto("pnw.sms.motivo.notificacionError", idCanal));
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            transaccionDTO.setRespuestaDTO(respuesta);
        }
        return transaccionDTO;
    }

    /**
     * Metodo que se encarga de realizar el pago de un TDC del mismo banco
     *
     * @param cuentaOrigen Numero (de 20 digitos) de la cuenta origen de los
     * fondos.
     * @param tdcBeneficiario Numero de la tarjeta de credito que se desea
     * pagar.
     * @param monto Monto en Bs. de la transferencia.
     * @param descripcion Descripcion, de la transferencia, suministrada por el
     * cliente que la realiza.
     * @param idCanal identificador del canal en Oracle 11
     * @param nombreCanal Codigo (extendido) del canal desde el cual es llamado
     * el procedimiento.
     * @return TransaccionDTO objeto de respuesta con el codigo del resultado de
     * la transaccion
     */
    @Override
    public TransaccionDTO pagoTDCMismoBanco(String cuentaOrigen, String tdcBeneficiario, BigDecimal monto, String descripcion, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        TransaccionDTO transaccionDTO = new TransaccionDTO();
        com.bds.wpn.ws.services.TransaccionDTO transaccionWs;
        try {
            //invocacion del WS
            TransferenciasYPagosWS_Service service = new TransferenciasYPagosWS_Service(new URL(wsdlServiciosTransfyPagos));
            TransferenciasYPagosWS port = service.getTransferenciasYPagosWSPort();
            //se obtiene el objeto de salida del WS
            transaccionWs = port.pagoTDCMismoBanco(cuentaOrigen, tdcBeneficiario, monto.setScale(2, BigDecimal.ROUND_HALF_UP), descripcion, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, transaccionWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                BeanUtils.copyProperties(transaccionDTO, transaccionWs);
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR al consumir el servicio pagoTDCMismoBanco: ")
                    .append("ORG-").append(this.formatoAsteriscosWeb(cuentaOrigen))
                    .append("TDC-").append(this.formatoAsteriscosWeb(tdcBeneficiario))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR al consumir el servicio pagoTDCMismoBanco: ")
                    .append("ORG-").append(this.formatoAsteriscosWeb(cuentaOrigen))
                    .append("TDC-").append(this.formatoAsteriscosWeb(tdcBeneficiario))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            this.notificarErrores(e, idCanal, nombreCanal, 
                    parametrosController.getNombreParametro("pnw.services.wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("pnw.delsur.soporte.email", idCanal), 
                    textosController.getNombreTexto("pnw.errores.titulo.emailNotificacion", idCanal), parametrosController.getNombreParametro("pnw.delsur.soporte.tlfs", idCanal), 
                    textosController.getNombreTexto("pnw.sms.motivo.notificacionError", idCanal));
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            transaccionDTO.setRespuestaDTO(respuesta);
        }
        return transaccionDTO;
    }

    /**
     * Metodo que se encarga de realizar el pago de un TDC Propia del mismo
     * banco
     *
     * @param cuentaOrigen Numero (de 20 digitos) de la cuenta origen de los
     * fondos.
     * @param tdcBeneficiario Numero de la tarjeta de credito que se desea
     * pagar.
     * @param monto Monto en Bs. de la transferencia.
     * @param descripcion Descripcion, de la transferencia, suministrada por el
     * cliente que la realiza.
     * @param idCanal identificador del canal en Oracle 11
     * @param nombreCanal Codigo (extendido) del canal desde el cual es llamado
     * el procedimiento.
     * @return TransaccionDTO objeto de respuesta con el codigo del resultado de
     * la transaccion
     */
    @Override
    public TransaccionDTO pagoTDCPropiaMismoBanco(String cuentaOrigen, String tdcBeneficiario, BigDecimal monto, String descripcion, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        TransaccionDTO transaccionDTO = new TransaccionDTO();
        com.bds.wpn.ws.services.TransaccionDTO transaccionWs;
        try {
            //invocacion del WS
            TransferenciasYPagosWS_Service service = new TransferenciasYPagosWS_Service(new URL(wsdlServiciosTransfyPagos));
            TransferenciasYPagosWS port = service.getTransferenciasYPagosWSPort();
            //se obtiene el objeto de salida del WS
            transaccionWs = port.pagoTDCPropiaMismoBanco(cuentaOrigen, tdcBeneficiario, monto.setScale(2, BigDecimal.ROUND_HALF_UP), descripcion, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, transaccionWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                BeanUtils.copyProperties(transaccionDTO, transaccionWs);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR al consumir el servicio pagoTDCPropiaMismoBanco: ")
                    .append("ORG-").append(this.formatoAsteriscosWeb(cuentaOrigen))
                    .append("TDC-").append(this.formatoAsteriscosWeb(tdcBeneficiario))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR al consumir el servicio pagoTDCPropiaMismoBanco: ")
                    .append("ORG-").append(this.formatoAsteriscosWeb(cuentaOrigen))
                    .append("TDC-").append(this.formatoAsteriscosWeb(tdcBeneficiario))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            this.notificarErrores(e, idCanal, nombreCanal, 
                    parametrosController.getNombreParametro("pnw.services.wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("pnw.delsur.soporte.email", idCanal), 
                    textosController.getNombreTexto("pnw.errores.titulo.emailNotificacion", idCanal), parametrosController.getNombreParametro("pnw.delsur.soporte.tlfs", idCanal), 
                    textosController.getNombreTexto("pnw.sms.motivo.notificacionError", idCanal));
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            transaccionDTO.setRespuestaDTO(respuesta);
        }
        return transaccionDTO;
    }

    /**
     * Metodo que se encarga de realizar la transferencia entre dos cuentas
     * hacia otro banco
     *
     * @param cuentaOrigen Numero (de 20 digitos) de la cuenta origen de los
     * fondos.
     * @param tdcBeneficiario Numero de cuenta, de 20 digitos, destino de los
     * fondos.
     * @param idBeneficiario Identificacion (numero de cedula, RIF, etc.) del
     * beneficiario del pago.
     * @param nombreBeneficiario Nombre y apellido del beneficiario de la
     * transferencia.
     * @param monto Monto en Bs. de la transferencia.
     * @param descripcion Descripcion, de la transferencia, suministrada por el
     * cliente que la realiza.
     * @param idCanal identificador del canal en Oracle 11
     * @param nombreCanal Codigo (extendido) del canal desde el cual es llamado
     * el procedimiento.
     * @return TransaccionDTO objeto de respuesta con el codigo del resultado de
     * la transaccion
     */
    @Override
    public TransaccionDTO pagoTDCOtroBanco(String cuentaOrigen, String tdcBeneficiario, String idBeneficiario, String nombreBeneficiario,
            BigDecimal monto, String descripcion, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        TransaccionDTO transaccionDTO = new TransaccionDTO();
        com.bds.wpn.ws.services.TransaccionDTO transaccionWs;
        try {
            //invocacion del WS
            TransferenciasYPagosWS_Service service = new TransferenciasYPagosWS_Service(new URL(wsdlServiciosTransfyPagos));
            TransferenciasYPagosWS port = service.getTransferenciasYPagosWSPort();
            //se obtiene el objeto de salida del WS
            transaccionWs = port.pagoTDCOtroBanco(cuentaOrigen, tdcBeneficiario, idBeneficiario, nombreBeneficiario, monto.setScale(2, BigDecimal.ROUND_HALF_UP), descripcion, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, transaccionWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                BeanUtils.copyProperties(transaccionDTO, transaccionWs);
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR al consumir el servicio pagoTDCOtroBanco: ")
                    .append("ORG-").append(this.formatoAsteriscosWeb(cuentaOrigen))
                    .append("TDC").append(this.formatoAsteriscosWeb(tdcBeneficiario))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR al consumir el servicio pagoTDCOtroBanco: ")
                    .append("ORG-").append(this.formatoAsteriscosWeb(cuentaOrigen))
                    .append("TDC-").append(this.formatoAsteriscosWeb(tdcBeneficiario))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            this.notificarErrores(e, idCanal, nombreCanal, 
                    parametrosController.getNombreParametro("pnw.services.wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("pnw.delsur.soporte.email", idCanal), 
                    textosController.getNombreTexto("pnw.errores.titulo.emailNotificacion", idCanal), parametrosController.getNombreParametro("pnw.delsur.soporte.tlfs", idCanal), 
                    textosController.getNombreTexto("pnw.sms.motivo.notificacionError", idCanal));
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            transaccionDTO.setRespuestaDTO(respuesta);
        }
        return transaccionDTO;
    }

}
