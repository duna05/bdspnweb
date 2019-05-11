/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;


import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbUsuariosEventosMediosDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.IbUsuariosEventosMediosDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbUsuariosEventosMediosWS;
import com.bds.wpn.ws.services.IbUsuariosEventosMediosWS_Service;
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
@Stateless(name = "wpnIbUsuariosEventosMediosDAO")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbUsuariosEventosMediosDAOImpl extends DAOUtil implements IbUsuariosEventosMediosDAO{
    
    
    
    private static final Logger logger = Logger.getLogger(IbUsuariosEventosMediosDAOImpl.class.getName());

    private final String urlWsdlUsuariosEventosMedios = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbUsuariosEventosMediosWS?WSDL";

    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;
    
    /**
     * Metodo que sustituye el listado de Eventos y medios asociados a un usuario
     * @param listUsuariosEventosMediosDTO
     * @param codigoCanal codigo del canal
     * @return RespuestaDTO
     */
    @Override
    public RespuestaDTO agregarUsuarioEventosMedios(IbUsuariosEventosMediosDTO listUsuariosEventosMediosDTO, String codigoCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            IbUsuariosEventosMediosWS_Service service = new IbUsuariosEventosMediosWS_Service(new URL(urlWsdlUsuariosEventosMedios));
            IbUsuariosEventosMediosWS port = service.getIbUsuariosEventosMediosWSPort();

            for (IbUsuariosEventosMediosDTO i : listUsuariosEventosMediosDTO.getIbUsuariosEventosMediosDTO()) {
                
                String montoMin = null;

                if (i.getMontoMin() != null) {
                    montoMin = i.getMontoMin().toString();
                }

                com.bds.wpn.ws.services.RespuestaDTO eventosNotificacionWs = port.agregarUsuarioEventosMedios(i.getIdUsuario().getId().toString(), i.getIdEvento().getId(), i.getIdMedio().getId().toString(), montoMin, codigoCanal);
                BeanUtils.copyProperties(respuesta, eventosNotificacionWs);

                if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    throw new ServiceException();
                }
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio agregarUsuarioEventosMedios: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN agregarUsuarioEventosMedios: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        }

        return respuesta;
    }

}
