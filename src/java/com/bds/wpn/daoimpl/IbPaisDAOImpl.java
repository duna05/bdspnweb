/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;


import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.FichaClienteRefBancariasDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.FichaClienteRefBancariasWs;
import com.bds.wpn.ws.services.FichaClienteRefBancariasWs_Service;
import com.bds.wpn.dao.FichaClienteRefBancariasDAO;
import com.bds.wpn.dao.IbPaisDAO;
import com.bds.wpn.dto.PaisDTO;
import com.bds.wpn.ws.services.PaisWs;
import com.bds.wpn.ws.services.PaisWs_Service;
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
@Stateless(name = "wpnPaisDAOImpl")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbPaisDAOImpl extends DAOUtil implements IbPaisDAO{
    
     private static final Logger logger = Logger.getLogger(IbPaisDAOImpl.class.getName());

    
     private final String urlWsdlPais = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/PaisWs?wsdl";
     

    @Override
    public PaisDTO consultarPais() {
        PaisDTO paises = null;
        PaisDTO paisDTO = new PaisDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            PaisWs_Service service = new PaisWs_Service(new URL(urlWsdlPais));
            PaisWs port = service.getPaisWsPort();
            //se obtiene el objeto de salida del WS
           com.bds.wpn.ws.services.PaisDTO paisWs = port.paisEstado();
           
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, paisWs.getRespuesta());
            
            
            List<PaisDTO> listPais = new ArrayList<>();
            
           
          
            for (com.bds.wpn.ws.services.PaisDTO pais : paisWs.getIbPaisList()) {
               
                      paises = new PaisDTO();
                      paises.setCodigoPais(pais.getCodigoPais());
                      paises.setNombre(pais.getNombre());
                      
                    listPais.add(paises);
            }
             if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else { 
                 paisDTO.setPaisList(listPais);
               //paises.setPaisList(listPais);
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
            paisDTO.setRespuesta(respuesta);
        }
        return paisDTO;
    }
    
}
