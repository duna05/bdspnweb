/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbClaveDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.IbHistoricoClavesDTO;
import com.bds.wpn.dto.IbUsuariosDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbUsuariosCanalesWs;
import com.bds.wpn.ws.services.IbUsuariosCanalesWs_Service;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author wilmer.rondon
 */
@Named
@Stateless(name = "wpnIbClaveDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbClaveDAOImpl extends DAOUtil implements IbClaveDAO {

    private static final Logger logger = Logger.getLogger(IbClaveDAOImpl.class.getName());

    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;
    
    private final String urlWsdlUsuariosCanales = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbUsuariosCanalesWs?WSDL";

    /**
     * Método que se encarga de consultar si la nueva clave ya se encuentra
     * entre las últimas N claves que el cliente ha tenido
     *
     * @param idUsuario
     * @param clave
     * @param cantClaves
     * @param nombreCanal
     * @return UtilDTO
     */
    @Override
    public UtilDTO existeEnUltimasNClaves(String idUsuario, String clave, String cantClaves, String nombreCanal) {

        UtilDTO utilDTO = new UtilDTO();
        RespuestaDTO respuesta = new RespuestaDTO();

        try {

            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            com.bds.wpn.ws.services.UtilDTO utilWs = port.existeEnUltimasNClaves(idUsuario, clave, cantClaves, nombreCanal);
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

            //obtenemos el resto de valores del objeto
            //cuando es un mapa debemos traer los valores uno a uno
            Map resultados = new HashMap();
            resultados.put(1, (utilWs.getResulados().getEntry().get(0).getValue()));
            utilDTO.setResuladosDTO(resultados);
            
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio existeEnUltimasNClaves: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio existeEnUltimasNClaves: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            utilDTO.setRespuestaDTO(respuesta);
        }
        return utilDTO;
    }
    
    /**
     * Método que se encarga de actualizar la nueva clave en IB_USUARIOS y de insertar
     * en la tabla IB_HISTORICO_CLAVES la última clave actualizada por el usuario así como el
     * período de validez de la misma.
     * @param idUsuario
     * @param clave
     * @param periodoClave
     * @param nombreCanal
     * @return RespuestaDTO
     */
    @Override
    public RespuestaDTO crearClave(String idUsuario, String clave, String periodoClave, String nombreCanal){

        RespuestaDTO respuesta = new RespuestaDTO();

        try {

            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            com.bds.wpn.ws.services.RespuestaDTO respuestaWs = port.crearClave(idUsuario, clave, periodoClave, nombreCanal);
            BeanUtils.copyProperties(respuesta, respuestaWs);
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

            
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio crearClave: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio crearClave: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return respuesta;
    }

    /**
     * Método que se encarga de retornar la última clave que tiene el cliente
     * activa
     *
     * @param idUsuario
     * @return IbHistoricoClavesDTO
     */
    @Override
    public IbHistoricoClavesDTO obtenerUltimaClave(String idUsuario, String nombreCanal) {

        IbHistoricoClavesDTO ibHistoricoClavesDTO = new IbHistoricoClavesDTO();        
        RespuestaDTO respuesta = new RespuestaDTO();
        
        try {

            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            com.bds.wpn.ws.services.IbHistoricoClavesDTO historicoClavesWs = port.obtenerUltimaClave(idUsuario, nombreCanal);
            BeanUtils.copyProperties(respuesta, historicoClavesWs.getRespuesta());
            
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }
            if (historicoClavesWs.getIbHistoricoClave().getClave() != null){
                ibHistoricoClavesDTO.setClave(historicoClavesWs.getIbHistoricoClave().getClave());
            }
            
            if (historicoClavesWs.getIbHistoricoClave().getIdUsuario() != null && historicoClavesWs.getIbHistoricoClave().getIdUsuario().getId() != null){
                ibHistoricoClavesDTO.setIdUsuario(new IbUsuariosDTO());
                ibHistoricoClavesDTO.getIdUsuario().setId(historicoClavesWs.getIbHistoricoClave().getIdUsuario().getId());
            }
            
            if (historicoClavesWs.getIbHistoricoClave().getFechaCreacion() != null){
                ibHistoricoClavesDTO.setFechaCreacionDate(historicoClavesWs.getIbHistoricoClave().getFechaCreacion().toGregorianCalendar().getTime());
            }
            
            if (historicoClavesWs.getIbHistoricoClave().getFechaVencimiento() != null){
                ibHistoricoClavesDTO.setFechaVencimientoDate(historicoClavesWs.getIbHistoricoClave().getFechaVencimiento().toGregorianCalendar().getTime());
            }
            
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerUltimaClave: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerUltimaClave: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            ibHistoricoClavesDTO.setRespuestaDTO(respuesta);
        }
        return ibHistoricoClavesDTO;

    }

}
