/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dao.SubClasificacionDAO;
import com.bds.wpn.dto.SubClasificacionDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.SubClasificacionWs;
import com.bds.wpn.ws.services.SubClasificacionWs_Service;
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
@Stateless(name = "wpnSubClasificacionDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)

public class SubClasificacionDAOImpl extends DAOUtil implements SubClasificacionDAO{
    
     private static final Logger logger = Logger.getLogger(SubClasificacionDAOImpl.class.getName());
      private final String urlWsdlSubClasificacion = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/SubClasificacionWs?wsdl";
     //private final String urlWsdlSubClasificacion = "http://localhost:7001/ibdsws/SubClasificacionWs?wsdl";

    @Override
    public SubClasificacionDTO consultarSubClasificacion() {
       SubClasificacionDTO subClasificacionDTO  = new SubClasificacionDTO();
       RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            SubClasificacionWs_Service service = new SubClasificacionWs_Service(new URL(urlWsdlSubClasificacion));
            SubClasificacionWs port = service.getSubClasificacionWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.SubClasificacionDTO subclasificacionWs = port.consultarSubClasificacion();     
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, subclasificacionWs.getRespuesta());
            
            
            List<SubClasificacionDTO> listSubClasificacionDTO = new ArrayList<>();
            //List<String> listaSubClasificacion = new ArrayList<>();
            List<List<String>> listaSubClasificacion = new ArrayList<List<String>>(); 
            for(int i =0; i<= 1; i++){
                 listaSubClasificacion.add(new ArrayList<String>()); 
             }
          
            for (com.bds.wpn.ws.services.SubClasificacionDTO subclasificacion : subclasificacionWs.getIbSubClasificacionList()) {
                   //listaSubClasificacion.add(subclasificacion.getCodigoSubClasificacion()+ ":" +subclasificacion.getDescripcion());
                   listaSubClasificacion.get(0).add(subclasificacion.getCodigoSubClasificacion().toString());
                  listaSubClasificacion.get(1).add(subclasificacion.getDescripcion());
            
            }
           
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {  
                subClasificacionDTO.setIbClasificacionListBi(listaSubClasificacion);
                 
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
             subClasificacionDTO.setRespuesta(respuesta);
        }
        return subClasificacionDTO; }
    
}
