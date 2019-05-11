/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.dao.MotivoDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.MotivoDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.MotivoWs;
import com.bds.wpn.ws.services.MotivoWs_Service;
import org.apache.log4j.Logger;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;


/**
 *
 * @author alejandro.flores
 */
@Named
@Stateless(name = "wpnMotivoDAOImpl")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class MotivoDAOImpl extends DAOUtil implements MotivoDAO{
    
    private static final Logger logger = Logger.getLogger(IbPaisDAOImpl.class.getName());
     private final String urlWsdlMotivo = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/MotivoWs?wsdl";
     
    @Override
    public MotivoDTO consultarMotivo() {
        MotivoDTO motivos = null;              
        MotivoDTO motivoDTO = new MotivoDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            MotivoWs_Service service = new MotivoWs_Service(new URL(urlWsdlMotivo));
            MotivoWs port = service.getMotivoWsPort();
            //se obtiene el objeto de salida del WS
           com.bds.wpn.ws.services.MotivoDTO motivoWs = port.consultarMotivo();
           
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, motivoWs.getRespuesta());
            
            
            
            
            List<MotivoDTO> listMotivo = new ArrayList<>();
            
            List<String> list=new ArrayList<>();
           
            for (com.bds.wpn.ws.services.MotivoDTO mot : motivoWs.getIbMotivoList()) {
               
                //list.add(mot.getNombre());
                motivos = new MotivoDTO();
                      motivos.setCodigoMotivo(mot.getCodigoMotivo());
                      motivos.setNombre(mot.getNombre());
                
                 listMotivo.add(motivos);
            }
             if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else { 
               //motivoDTO.setiMotivoList(list);
                 motivoDTO.setIbMotivoListSI(listMotivo);
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
            motivoDTO.setRespuesta(respuesta);
        }
        return motivoDTO;
    }
    
}
