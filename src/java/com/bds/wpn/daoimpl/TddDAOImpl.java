/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dao.TddDAO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.TarjetaDebitoDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.TddWS;
import com.bds.wpn.ws.services.TddWS_Service;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * @author rony.rodriguez
 */
@Named
@Stateless(name = "wpnTddDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class TddDAOImpl extends DAOUtil implements TddDAO {

    /**
     * Log de sistema
     */
    private static final Logger logger = Logger.getLogger(TddDAOImpl.class.getName());

    @EJB
    IbParametrosFacade parametrosFacade;

    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlTDD = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/TddWS?wsdl";

    /**
     * Bloquea una tarjeta de debito por intentos fallidos ya sean de acceso o
     * de validacion de OTP.
     *
     * @param tarjetaDebito String
     * @param idCanal String
     * @param nombreCanal String
     * @return RespuestaDTO
     */
    @Override
    public RespuestaDTO bloquearTDD(String tarjetaDebito, String idCanal, String nombreCanal) {

        RespuestaDTO respuesta = new RespuestaDTO();

        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            TddWS_Service service = new TddWS_Service(new URL(urlWsdlTDD));
            TddWS port = service.getTddWSPort();

            //clase para casteo dinamico de atributos y se obtiene el objeto de salida del WS
            BeanUtils.copyProperties(respuesta, port.bloquearTDD(tarjetaDebito, nombreCanal));

            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException d) {
            logger.error(new StringBuilder("ERROR DAO  EN bloquearTDD: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(tarjetaDebito))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(d.toString()).toString());
        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO GENERICO EN bloquearTDD: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(tarjetaDebito))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log            
        }

        return respuesta;
    }

    /**
     * Valida que los instrumentos TDD, Fecha Vcto y PIN pertenece al cliente.
     *
     * @param numeroTarjeta numero de la tarjeta de debito del cliente
     * @param pinCifrado PIN cifrado de la Tarjeta.
     * @param fechaVencimiento Fecha de Vencimiento de la Tarjeta.
     * @param codigoCanal codigo del canal
     * @return UtilDTO
     */
    @Override
    public UtilDTO validarTDD(String numeroTarjeta, String pinCifrado, String fechaVencimiento, String codigoCanal) {

        RespuestaDTO respuesta = new RespuestaDTO();
        UtilDTO utilDTO = new UtilDTO();

        try {

            //invocacion del WS
            TddWS_Service service = new TddWS_Service(new URL(urlWsdlTDD));
            TddWS port = service.getTddWSPort();
            com.bds.wpn.ws.services.UtilDTO utilWs = port.validarTDD(numeroTarjeta, pinCifrado, fechaVencimiento, codigoCanal);
            //clase para casteo dinamico de atributos y se obtiene el objeto de salida del WS
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());

            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

            //obtenemos el resto de valores del objeto
            //cuando es un mapa debemos traer los valores uno a uno
            Map resultados = new HashMap();
            resultados.put("1", (utilWs.getResulados().getEntry().get(0).getValue()));
            utilDTO.setResuladosDTO(resultados);

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO  EN validarTDD: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(numeroTarjeta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO GENERICO EN validarTDD: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(numeroTarjeta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log            
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            utilDTO.setRespuestaDTO(respuesta);
        }
        return utilDTO;
    }

    /**
     * Metodo que obtiene el listado de TDC propias por cliente
     *
     * @param codigoCliente
     * @param codigoCanal
     * @return ClienteDTO -> con List< TarjetasCreditoDTO >
     */
    @Override
    public TarjetaDebitoDTO obtenerListadoTDDCliente(String codigoCliente, String codigoCanal) {
        TarjetaDebitoDTO tddDTO = new TarjetaDebitoDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            TddWS_Service service = new TddWS_Service(new URL(urlWsdlTDD));
            TddWS port = service.getTddWSPort();
            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.TarjetaDebitoDTO tddWs = port.obtenerListadoTDDCliente(codigoCliente, codigoCanal);

            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, tddWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa

                BeanUtils.copyProperties(tddDTO, tddWs);

                TarjetaDebitoDTO tdd = null;

                List<TarjetaDebitoDTO> listTDD = new ArrayList<>();

                for (com.bds.wpn.ws.services.TarjetaDebitoDTO tarjeta : tddWs.getTddsDTO()) {
                    tdd = new TarjetaDebitoDTO();
                    BeanUtils.copyProperties(tdd, tarjeta);
                    listTDD.add(tdd);
                }
                tddDTO.setTddsDTO(listTDD);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerListadoTDDCliente: ")
                    .append("USR-").append(codigoCliente)
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerListadoTDDCliente: ")
                    .append("USR-").append(codigoCliente)
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (tddDTO.getTddsDTO() == null) {
                tddDTO.setTddsDTO(new ArrayList<TarjetaDebitoDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            tddDTO.setRespuestaDTO(respuesta);
        }
        return tddDTO;
    }

}
