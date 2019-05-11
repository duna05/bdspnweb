/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dao.P2PDAO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.P2PWS;
import com.bds.wpn.ws.services.P2PWS_Service;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
@Stateless(name = "wpnP2PDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class P2PDAOImpl extends DAOUtil implements P2PDAO {
    private String CERO = "0";

    private static final Logger logger = Logger.getLogger(P2PDAOImpl.class.getName());

    private final String urlWsdlP2P = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/P2PWS?wsdl";

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    ParametrosController parametrosController;

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
     * @param identificacionBeneficiario
     * @param nombreCanal String
     * @return RespuestaDTO
     */
    @Override
    public UtilDTO realizarPagoP2PTercerosDelsur(String codCanalP2P, String nroEmisor, String nroBeneficiario, String identificacionBeneficiario, String monto, String conceptoPago, String identificacionPagador, String url, boolean frecuente, String alias, BigDecimal idUsuario, String tipoDocumento, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        UtilDTO utilDTO = new UtilDTO();

        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            P2PWS_Service service = new P2PWS_Service(new URL(urlWsdlP2P));
            P2PWS port = service.getP2PWSPort();

            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.UtilDTO utilWs = port.realizarPagoP2PTercerosDelsur(codCanalP2P, nroEmisor, nroBeneficiario, tipoDocumento+identificacionBeneficiario, monto, conceptoPago, identificacionPagador, url, idCanal, nombreCanal);
            

            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //YA QUE EL WS DE ROAS SYSTEM RETORNA CODIGOSP "00" EN CASO EXITOSO POR SWICTH7B SE ESTANDARIZA AQUI PARA NO AFECTAR EL MANEJO DE RESPUESTAS
            if (respuesta.getCodigoSP() != null && respuesta.getCodigoSP().equalsIgnoreCase("000")) {
                respuesta.setCodigoSP(CODIGO_RESPUESTA_EXITOSO);
                if(frecuente){
                    com.bds.wpn.ws.services.UtilDTO beneficiarioP2P = port.registrarBeneficiarioP2P(codCanalP2P, nroEmisor, CERO + nroBeneficiario.substring(2), identificacionBeneficiario, monto, conceptoPago, identificacionPagador, DIGITOS_INICIALES_BANCO_DEL_SUR, url, frecuente, alias, idUsuario, tipoDocumento, idCanal, nombreCanal); 
                }
            }
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

            //obtenemos el resto de valores del objeto
            //cuando es un mapa debemos traer los valores uno a uno
            Map resultados = new HashMap();
            resultados.put(utilWs.getResulados().getEntry().get(0).getKey(), utilWs.getResulados().getEntry().get(0).getValue());
            utilDTO.setResuladosDTO(resultados);

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio realizarPagoP2PTercerosDelsur: ")
                    .append("TLFEMISOR-").append(nroEmisor)
                    .append("TLFBENEF-").append(nroBeneficiario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN realizarPagoP2PTercerosDelsur: ")
                    .append("TLFEMISOR-").append(nroEmisor)
                    .append("TLFBENEF-").append(nroBeneficiario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            utilDTO.setRespuestaDTO(respuesta);
        }
        return utilDTO;
    }

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
     * @param identificacionBeneficiario
     * @param nombreCanal String
     * @return RespuestaDTO
     */
    @Override
    public UtilDTO realizarPagoP2PTercerosOtrosBancos(String codCanalP2P, String nroEmisor, String nroBeneficiario, String identificacionBeneficiario, String monto, String conceptoPago, String codBanco, String identificacionPagador, String url, boolean frecuente, String alias, BigDecimal idUsuario, String tipoDocumento, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        UtilDTO utilDTO = new UtilDTO();

        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            P2PWS_Service service = new P2PWS_Service(new URL(urlWsdlP2P));
            P2PWS port = service.getP2PWSPort();

            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.UtilDTO utilWs = port.realizarPagoP2PTercerosOtrosBancos(codCanalP2P, nroEmisor, nroBeneficiario, tipoDocumento+identificacionBeneficiario, monto, conceptoPago, identificacionPagador, codBanco, url, idCanal, nombreCanal);

            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //YA QUE EL WS DE ROAS SYSTEM RETORNA CODIGOSP "00" EN CASO EXITOSO POR SWICTH7B SE ESTANDARIZA AQUI PARA NO AFECTAR EL MANEJO DE RESPUESTAS
            if (respuesta.getCodigoSP() != null && respuesta.getCodigoSP().equalsIgnoreCase("000")) {
                respuesta.setCodigoSP(CODIGO_RESPUESTA_EXITOSO);
                if(frecuente){
                    com.bds.wpn.ws.services.UtilDTO beneficiarioP2P = port.registrarBeneficiarioP2P(codCanalP2P, nroEmisor, CERO + nroBeneficiario.substring(2), identificacionBeneficiario, monto, conceptoPago, identificacionPagador, codBanco, url, frecuente, alias, idUsuario, tipoDocumento, idCanal, nombreCanal);
                }
            }
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

            //obtenemos el resto de valores del objeto
            //cuando es un mapa debemos traer los valores uno a uno
            Map resultados = new HashMap();
            resultados.put(utilWs.getResulados().getEntry().get(0).getKey(), utilWs.getResulados().getEntry().get(0).getValue());
            utilDTO.setResuladosDTO(resultados);

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio realizarPagoP2PTercerosOtrosBancos: ")
                    .append("TLFEMISOR-").append(nroEmisor)
                    .append("TLFBENEF-").append(nroBeneficiario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN realizarPagoP2PTercerosOtrosBancos: ")
                    .append("TLFEMISOR-").append(nroEmisor)
                    .append("TLFBENEF-").append(nroBeneficiario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            utilDTO.setRespuestaDTO(respuesta);
        }
        return utilDTO;
    }

}
