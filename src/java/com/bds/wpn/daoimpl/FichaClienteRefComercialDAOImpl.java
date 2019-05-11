/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.dao.FichaClienteRefComercialDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.FichaClienteRefComercialesDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.FichaClienteRefComercialesWs;
import com.bds.wpn.ws.services.FichaClienteRefComercialesWs_Service;
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
@Stateless(name = "wpnFichaClienteRefComercialDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteRefComercialDAOImpl extends DAOUtil implements FichaClienteRefComercialDAO{
    
    private static final Logger logger = Logger.getLogger(ClasificacionDAOImpl.class.getName());
    private final String urlWsdlFichaClienteRefComercial = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "//FichaClienteRefComercialesWs?wsdl";
    //private final String urlWsdlFichaClienteRefComercial = "localhost:7001/ibdsws/FichaClienteRefComercialesWs?wsdl";

    @Override
    public FichaClienteRefComercialesDTO consultarRefComerciales(String iCodigoCliente) {
        FichaClienteRefComercialesDTO fichaClienteRefComercialDTO = new FichaClienteRefComercialesDTO();
		FichaClienteRefComercialesDTO referencias = null;
        RespuestaDTO respuesta = new RespuestaDTO();
       
  
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            FichaClienteRefComercialesWs_Service service = new FichaClienteRefComercialesWs_Service(new URL(urlWsdlFichaClienteRefComercial));
            FichaClienteRefComercialesWs port = service.getFichaClienteRefComercialesWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.FichaClienteRefComercialesDTO fichaClienteRefComercialesWs = port.consultarRefComerciales(iCodigoCliente);
			
			
			
			
			
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, fichaClienteRefComercialesWs.getRespuesta());
            
            
	     List<FichaClienteRefComercialesDTO> listRefComercial = new ArrayList<>();
           
                        
            for (com.bds.wpn.ws.services.FichaClienteRefComercialesDTO refComercial : fichaClienteRefComercialesWs.getIbReferenciaList()) {
                    
					referencias = new FichaClienteRefComercialesDTO();
                    referencias.setCasaComercial(refComercial.getCasaComercial());
                    referencias.setFechaConcesion(refComercial.getFechaConcesion());
                    referencias.setTelefonos(refComercial.getTelefonos());
                    listRefComercial.add(referencias);
            }

            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                  
				  referencias.setIbReferenciaComercialList(listRefComercial);
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
            referencias.setRespuesta(respuesta);
        }
        
        return referencias; 
		}
    
}
