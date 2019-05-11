/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbMensajesDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.IbMensajesDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbMensajesWS;
import com.bds.wpn.ws.services.IbMensajesWS_Service;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
@Stateless(name = "wpnIbMensajesDAO")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbMensajesDAOImpl extends DAOUtil implements IbMensajesDAO {

    @EJB
    IbParametrosFacade parametrosFacade;
    
    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    ParametrosController parametrosController;
    
    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(IbMensajesDAOImpl.class.getName());


    private final String urlWsdlMensajes = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbMensajesWS?WSDL";

    /**
     * *
     * Metodo que realiza la busqueda un usuario por codigo
     *
     * @param idUsuario codigo del usuario
     * @param nombreCanal nombre del canal
     * @param idCanal identificador unico del canal
     * @return IbUsuarioDTO
     */
    @Override
    public IbMensajesDTO consultarMensajesUsuarios(String idUsuario, String nombreCanal, String idCanal) {
        IbMensajesDTO mensajes = new IbMensajesDTO();
         IbMensajesDTO mensajeTemp ;
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            IbMensajesWS_Service service = new IbMensajesWS_Service(new URL(urlWsdlMensajes));
            IbMensajesWS port = service.getIbMensajesWSPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.IbMensajesDTO mensajesWs = port.consultarMensajesUsuarios(idUsuario, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, mensajesWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(mensajes, mensajesWs);
                
                Collection <IbMensajesDTO> mensajesTemp = new ArrayList<>();
                for (com.bds.wpn.ws.services.IbMensajes m : mensajesWs.getIbMensajes()) {
                    mensajeTemp = new IbMensajesDTO();
                    BeanUtils.copyProperties(mensajeTemp, m);
                    if(m.getFechaHora() != null){
                        mensajeTemp.setFechaHoraDate(m.getFechaHora().toGregorianCalendar().getTime());
                    }
                    mensajesTemp.add(mensajeTemp);
                }
                mensajes.setMensajes(mensajesTemp);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio consultarMensajesUsuarios: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN consultarMensajesUsuarios: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (mensajes.getMensajes() == null) {
                mensajes.setMensajes(new ArrayList<IbMensajesDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            mensajes.setRespuestaDTO(respuesta);
        }
        return mensajes;        
    }
    
    /**
     * *
     * Metodo que consulta la cantidad de mensajes activos(nuevos o leidos) asociados a un usuario
     *
     * @param idUsuario codigo del usuario
     * @param nombreCanal nombre del canal
     * @param idCanal identificador unico del canal
     * @return IbUsuarioDTO
     */
    @Override
    public UtilDTO cantNuevosMsjsUsuarios (String idUsuario , String nombreCanal , String idCanal){
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            IbMensajesWS_Service service = new IbMensajesWS_Service(new URL(urlWsdlMensajes));
            IbMensajesWS port = service.getIbMensajesWSPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            utilWs = port.cantNuevosMsjsUsuarios(idUsuario, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put("resultado", (utilWs.getResulados().getEntry().get(0).getValue()));
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio cantNuevosMsjsUsuarios: ")
                    .append("IDUSR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN cantNuevosMsjsUsuarios: ")
                    .append("IDUSR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            util.setRespuestaDTO(respuesta);
        }
        return util;
    }
    
    /**
     * *
     * Metodo que actualiza el estado de un mensaje a leido
     *
     * @param idUsuario codigo del usuario
     * @param idMensaje identificador del mensaje
     * @param nombreCanal nombre del canal
     * @param idCanal identificador unico del canal
     * @return IbUsuarioDTO
     */
    @Override
    public RespuestaDTO marcarMensajeLeido (String idUsuario , String idMensaje, String nombreCanal , String idCanal){        
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //invocacion del WS
            IbMensajesWS_Service service = new IbMensajesWS_Service(new URL(urlWsdlMensajes));
            IbMensajesWS port = service.getIbMensajesWSPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.RespuestaDTO respuestaWs = port.marcarMensajeLeido(idUsuario, idMensaje, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } 

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio marcarMensajeLeido: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN marcarMensajeLeido: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }
        return respuesta;
    }
    
    /**
     * *
     * Metodo que consulta el estado de un mensaje nuevo o leido
     *
     * @param idUsuario codigo del usuario
     * @param idMensaje identificador del mensaje
     * @param nombreCanal nombre del canal
     * @param idCanal identificador unico del canal
     * @return IbUsuarioDTO con parametro estatus y un booleano
     */
    @Override
    public UtilDTO mensajeUsuarioLeido(String idUsuario, String idMensaje, String nombreCanal, String idCanal){
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            IbMensajesWS_Service service = new IbMensajesWS_Service(new URL(urlWsdlMensajes));
            IbMensajesWS port = service.getIbMensajesWSPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            utilWs = port.mensajeUsuarioLeido(idUsuario, idMensaje, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put("resultado", (utilWs.getResulados().getEntry().get(0).getValue()));
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio mensajeUsuarioLeido: ")
                    .append("IDUSR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN mensajeUsuarioLeido: ")
                    .append("IDUSR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            util.setRespuestaDTO(respuesta);
        }
        return util;
    }
}
