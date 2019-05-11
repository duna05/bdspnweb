/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dao.SubSubClasificacionDao;
import com.bds.wpn.dto.SubSubClasificacionDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.SubSubClasificacionWs;
import com.bds.wpn.ws.services.SubSubClasificacionWs_Service;
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
@Stateless(name = "wpnSubSubClasificacionDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)

public class SubSubClasificacionDaoImpl extends DAOUtil implements SubSubClasificacionDao{
    
     private static final Logger logger = Logger.getLogger(SubSubClasificacionDaoImpl.class.getName());
      private final String urlWsdlSubSubClasificacion = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/SubSubClasificacionWs?wsdl";
     //private final String urlWsdlSubSubClasificacion = "http://localhost:7001/ibdsws/SubSubClasificacionWs?wsdl";

    @Override
    public SubSubClasificacionDTO consultarSubSubClasificacion() {
    SubSubClasificacionDTO subSubClasificacionDTO  = new SubSubClasificacionDTO();
       RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            SubSubClasificacionWs_Service service = new SubSubClasificacionWs_Service(new URL(urlWsdlSubSubClasificacion));
            SubSubClasificacionWs port = service.getSubSubClasificacionWsPort();
            //se obtiene el objeto de salida del WS
           com.bds.wpn.ws.services.SubSubClasificacionDTO subSubclasificacionWs = port.consultarSubSubClasificacion();     
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, subSubclasificacionWs.getRespuesta());
            
            
            List<SubSubClasificacionDTO> listSubClasificacionDTO = new ArrayList<>();
            //List<String> listaSubSubClasificacion = new ArrayList<>();
            List<List<String>> listaSubSubClasificacion = new ArrayList<List<String>>();
             for(int i =0; i<= 1; i++){
                 listaSubSubClasificacion.add(new ArrayList<String>()); 
             }
                
          
            for (com.bds.wpn.ws.services.SubSubClasificacionDTO subSuclasificacion : subSubclasificacionWs.getIbSubSubClasificacionList()) {
                   //listaSubSubClasificacion.add(subSuclasificacion.getCodigoSubSubClasificacion()+ ":" +subSuclasificacion.getDescripcion());
                      listaSubSubClasificacion.get(0).add(subSuclasificacion.getCodigoSubSubClasificacion().toString());
                      listaSubSubClasificacion.get(1).add(subSuclasificacion.getDescripcion());
            }
            
           
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {  
                //subSubClasificacionDTO.setSubSubClasificacionList(listaSubSubClasificacion);
                subSubClasificacionDTO.setSubSubClasificacionListBi(listaSubSubClasificacion);
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
             subSubClasificacionDTO.setRespuesta(respuesta);
        }
        return subSubClasificacionDTO; }
    
}
