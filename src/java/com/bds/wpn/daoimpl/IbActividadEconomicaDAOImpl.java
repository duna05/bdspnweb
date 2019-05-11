/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.dao.IbActividadEconomicaDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.ActividadEconomicaDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.ActividadEconomicaWs;
import com.bds.wpn.ws.services.ActividadEconomicaWs_Service;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author alejandro.flores
 */
@Named
@Stateless(name = "wpnIbActividadEconomicaDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbActividadEconomicaDAOImpl extends DAOUtil implements IbActividadEconomicaDAO {

    private static final Logger logger = Logger.getLogger(IbActividadEconomicaDAOImpl.class.getName());

    //private final String urlWsdlFichaCliente = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteWs?wsdl";
    private final String urlWsdlIbActividadEconomica = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/ActividadEconomicaWs?wsdl";
    //private final String urlWsdlIbActividadEconomica = "http://localhost:7001/ibdsws/ActividadEconomicaWs?wsdl";

    @Override
    public ActividadEconomicaDTO consultarActividadEconomica() {
        ActividadEconomicaDTO actividad = new ActividadEconomicaDTO();
        ActividadEconomicaDTO actividadEconomicaDTO = new ActividadEconomicaDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            ActividadEconomicaWs_Service service = new ActividadEconomicaWs_Service(new URL(urlWsdlIbActividadEconomica));
            ActividadEconomicaWs port = service.getActividadEconomicaWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.ActividadEconomicaDTO actividadEconomicaWs = port.consultarActividadEconomica();
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, actividadEconomicaWs.getRespuesta());

            List<ActividadEconomicaDTO> listActividad = new ArrayList<>();

            for (com.bds.wpn.ws.services.ActividadEconomicaDTO actEconomica : actividadEconomicaWs.getIbActividadEconomicaList()) {
                actividad = new ActividadEconomicaDTO();
                actividad.setCodigoActividadEconomica(actEconomica.getCodigoActividadEconomica());
                actividad.setDescripcion(actEconomica.getDescripcion());
                listActividad.add(actividad);
            }
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                actividadEconomicaDTO.setActividadEconomicaList(listActividad);
             }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio consultarDatosCliente: ")
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN consultarDatosCliente: ")
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            actividadEconomicaDTO.setRespuesta(respuesta);
        }
        return actividadEconomicaDTO;
    }

}
