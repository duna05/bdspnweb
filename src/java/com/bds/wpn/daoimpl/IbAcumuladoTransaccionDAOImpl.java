/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IBAcumuladoTransaccionDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.IbAcumuladoTransaccionDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbAcumuladoTransaccion;
import com.bds.wpn.ws.services.IbAcumuladoTransaccionWS;
import com.bds.wpn.ws.services.IbAcumuladoTransaccionWS_Service;
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
 * @author juan.faneite
 */
@Named
@Stateless(name = "wpnIbAcumuladoTransaccionDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbAcumuladoTransaccionDAOImpl extends DAOUtil implements IBAcumuladoTransaccionDAO{
    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(IbAcumuladoTransaccionDAOImpl.class.getName());
    
    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlAcumTrans = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbAcumuladoTransaccionWS?WSDL";

    /**
     * Metodo que Obtiene los acumulados del usuario
     *
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return IBAfiliacionesDTO listado de afiliados
     */
    
    @Override
    public IbAcumuladoTransaccionDTO consultarAcumuladoUsuario(String idUsuario, String nombreCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        IbAcumuladoTransaccionDTO ibAcumuladosDTO = new IbAcumuladoTransaccionDTO();

        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class); 
            IbAcumuladoTransaccionWS_Service service = new IbAcumuladoTransaccionWS_Service(new URL(urlWsdlAcumTrans));
            IbAcumuladoTransaccionWS port = service.getIbAcumuladoTransaccionWSPort();
            com.bds.wpn.ws.services.IbAcumuladoTransaccionDTO ibAcumuladosDTOWs = port.consultarAcumuladoTransaccion(idUsuario, nombreCanal);            
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {                
                throw new ServiceException();
            }
                for (IbAcumuladoTransaccion acum : ibAcumuladosDTOWs.getIbAcumuladoTransacciones()) {
                    BeanUtils.copyProperties(ibAcumuladosDTO, acum);
                    if (acum.getFecha() != null) {
                        ibAcumuladosDTO.setFechaDate(acum.getFecha().toGregorianCalendar().getTime());
                    }
                }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio consultarAcumuladoUsuario: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio consultarAcumuladoUsuario: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        } finally {
           
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            ibAcumuladosDTO.setRespuestaDTO(respuesta);
        }
        
        return ibAcumuladosDTO;
    }
        

    @Override
    public RespuestaDTO insertarAcumuladoTransaccion(String idUsuario, String montoTransaccion, String idTransaccion, String nombreCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        
        try {
            //invocacion del WS
           IbAcumuladoTransaccionWS_Service service = new IbAcumuladoTransaccionWS_Service(new URL(urlWsdlAcumTrans));
            IbAcumuladoTransaccionWS port = service.getIbAcumuladoTransaccionWSPort();
            //se obtiene el objeto de salida del WS 
            respuestaWs = port.insertarAcumuladoTransaccion(idUsuario, montoTransaccion, idTransaccion, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio insertarAcumuladoTransaccion: ")
                    .append("CODUSR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN insertarAcumuladoTransaccion: ")
                    .append("CODUSR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return respuesta;
        
    }


}
