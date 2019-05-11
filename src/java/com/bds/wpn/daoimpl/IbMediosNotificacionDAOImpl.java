/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbMediosNotificacionDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.IbMediosNotificacionDTO;
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
@Stateless(name = "wpnIbMediosNotificacionDAO")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbMediosNotificacionDAOImpl extends DAOUtil implements IbMediosNotificacionDAO{
    
    private static final Logger logger = Logger.getLogger(IbMediosNotificacionDAOImpl.class.getName());        

    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;
    
    private final String urlWsdlUsuariosEventosMedios = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbUsuariosEventosMediosWS?WSDL";

    /**
     * Metodo que obtiene el listado de medios
     * @param codigoCanal codigo del canal para su registro en el log
     * @return IbMediosNotificacionDTO con listado
     */
    @Override
    public IbMediosNotificacionDTO listaMedios(String codigoCanal) {
        
        IbMediosNotificacionDTO ibMediosNotificacionDTO = new IbMediosNotificacionDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            IbUsuariosEventosMediosWS_Service service = new IbUsuariosEventosMediosWS_Service(new URL(urlWsdlUsuariosEventosMedios));
            IbUsuariosEventosMediosWS port = service.getIbUsuariosEventosMediosWSPort();
            com.bds.wpn.ws.services.IbMediosNotificacionDTO mediosNotificacionWs = port.listaMedios(codigoCanal);
            BeanUtils.copyProperties(respuesta, mediosNotificacionWs.getRespuesta());

            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {

                List<IbMediosNotificacionDTO> listMediosNotificacion = new ArrayList<>();
                for (com.bds.wpn.ws.services.IbMediosNotificacion medios : mediosNotificacionWs.getMediosNotificacion()) {
                    IbMediosNotificacionDTO mediosTemp = new IbMediosNotificacionDTO();
                    BeanUtils.copyProperties(mediosTemp, medios);

                    listMediosNotificacion.add(mediosTemp);
                }

                ibMediosNotificacionDTO.setIbMediosNotificacionDTO(listMediosNotificacion);                

            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listaMedios: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listaMedios: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            ibMediosNotificacionDTO.setRespuestaDTO(respuesta);
        }

        return ibMediosNotificacionDTO;
    }
}
