/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbEventosNotificacionDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.IbEventosNotificacionDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbUsuariosEventosMediosWS;
import com.bds.wpn.ws.services.IbUsuariosEventosMediosWS_Service;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
@Stateless(name = "wpnIbEventosNotificacionDAO")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbEventosNotificacionDAOImpl extends DAOUtil implements IbEventosNotificacionDAO {

    private static final Logger logger = Logger.getLogger(IbEventosNotificacionDAOImpl.class.getName());

    private final String urlWsdlUsuariosEventosMedios = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbUsuariosEventosMediosWS?WSDL";

    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;

    /**
     * Metodo que obtiene el listado de eventos
     *
     * @param codigoCanal codigo del canal para su registro en el log
     * @return IbEventosNotificacionDTO con listado
     */
    @Override
    public IbEventosNotificacionDTO listaEventos(String codigoCanal) {
        IbEventosNotificacionDTO ibEventosNotificacionDTO = new IbEventosNotificacionDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            IbUsuariosEventosMediosWS_Service service = new IbUsuariosEventosMediosWS_Service(new URL(urlWsdlUsuariosEventosMedios));
            IbUsuariosEventosMediosWS port = service.getIbUsuariosEventosMediosWSPort();
            com.bds.wpn.ws.services.IbEventosNotificacionDTO eventosNotificacionWs = port.listaEventos(codigoCanal);
            BeanUtils.copyProperties(respuesta, eventosNotificacionWs.getRespuesta());

            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {

                List<IbEventosNotificacionDTO> listEventosNotificacion = new ArrayList<>();
                for (com.bds.wpn.ws.services.IbEventosNotificacion eventos : eventosNotificacionWs.getEventosNotificacion()) {
                    IbEventosNotificacionDTO eventosTemp = new IbEventosNotificacionDTO();
                    BeanUtils.copyProperties(eventosTemp, eventos);

                    listEventosNotificacion.add(eventosTemp);
                }

                ibEventosNotificacionDTO.setIbEventosNotificacionDTO(listEventosNotificacion);                

            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listaEventos: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listaEventos: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            ibEventosNotificacionDTO.setRespuestaDTO(respuesta);
        }

        return ibEventosNotificacionDTO;

    }
}
