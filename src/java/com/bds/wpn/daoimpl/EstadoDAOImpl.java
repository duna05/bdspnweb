/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.dao.EstadoDAO;
import com.bds.wpn.dto.EstadoDTO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.ClasificacionWs;
import com.bds.wpn.ws.services.ClasificacionWs_Service;
import com.bds.wpn.ws.services.EstadoWs;
import com.bds.wpn.ws.services.EstadoWs_Service;
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
@Stateless(name = "wpnEstadoDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class EstadoDAOImpl extends DAOUtil implements EstadoDAO{
    
     private static final Logger logger = Logger.getLogger(EstadoDAOImpl.class.getName());
     private final String urlWsdlEstado = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/EstadoWs?wsdl";
     //private final String urlWsdlEstado = "http://localhost:7001/ibdsws/EstadoWs?wsdl";

    @Override
    public EstadoDTO consultarEstado() {
   EstadoDTO  estadoDTO = new EstadoDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            EstadoWs_Service service = new EstadoWs_Service(new URL(urlWsdlEstado));
            EstadoWs port = service.getEstadoWsPort();
            //se obtiene el objeto de salida del WS
           com.bds.wpn.ws.services.EstadoDTO estadoWs = port.consultarEstado();     
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, estadoWs.getRespuesta());
            
            
            List<EstadoDTO> listEstadoDTO = new ArrayList<>();
            List<String> listaEstado = new ArrayList<>();
                
          
            for (com.bds.wpn.ws.services.EstadoDTO estadoD : estadoWs.getIbEstadoList()) {
                   listaEstado.add(estadoD.getCodigoEstado()+":"+estadoD.getNombre());
           }
           
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {  
                estadoDTO.setIbEstadoList(listaEstado);
            }

        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio consultarDatosCliente: ")
                   
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN consultarDatosCliente: ")
                   
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
             estadoDTO.setRespuesta(respuesta);
        }
        return estadoDTO; }
    
}
