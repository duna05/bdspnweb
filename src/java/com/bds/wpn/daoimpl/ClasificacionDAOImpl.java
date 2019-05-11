/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.dao.ClasificacionDAO;
import com.bds.wpn.dto.ClasificacionDTO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.ClasificacionWs;
import com.bds.wpn.ws.services.ClasificacionWs_Service;
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
@Stateless(name = "wpnClasificacionDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class ClasificacionDAOImpl extends DAOUtil implements ClasificacionDAO {

    private static final Logger logger = Logger.getLogger(ClasificacionDAOImpl.class.getName());
    private final String urlWsdlClasificacion = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/ClasificacionWs?wsdl";
    //private final String urlWsdlClasificacion = "http://localhost:7001/ibdsws/ClasificacionWs?wsdl";

    @Override
    public ClasificacionDTO consultarClasificacion() {
        ClasificacionDTO clasificacionDTO = new ClasificacionDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            ClasificacionWs_Service service = new ClasificacionWs_Service(new URL(urlWsdlClasificacion));
            ClasificacionWs port = service.getClasificacionWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.ClasificacionDTO clasificacionWs = port.consultarClasificacion();
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, clasificacionWs.getRespuesta());

            List<ClasificacionDTO> listClasificacionDTO = new ArrayList<>();
            //List<String> listaClasificacion = new ArrayList<>();
            List<List<String>> listaClasificacion = new ArrayList<List<String>>();
             for(int i =0; i<= 1; i++){
                 listaClasificacion.add(new ArrayList<String>()); 
             }
                   
              
            //ibClasificacionListBi

            for (com.bds.wpn.ws.services.ClasificacionDTO clasificacion : clasificacionWs.getIbClasificacionList()) {

                 //listaClasificacion.add(clasificacion.getCodigoClasificacion() + ":" + clasificacion.getDescripcion());
                  listaClasificacion.get(0).add(clasificacion.getCodigoClasificacion().toString());
                  listaClasificacion.get(1).add(clasificacion.getDescripcion());
            }

            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //clasificacionDTO.setClasificacionList(listaClasificacion);
                  clasificacionDTO.setIbClasificacionListBi(listaClasificacion);                 
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
            clasificacionDTO.setRespuesta(respuesta);
        }
        return clasificacionDTO;
    }

}
