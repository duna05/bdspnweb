/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.FichaClienteUpdateDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.FichaClienteUpdateWs;
import com.bds.wpn.ws.services.FichaClienteUpdateWs_Service;
import com.bds.wpn.dto.RespuestaDTO;
import java.net.URL;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Logger;

/**
 *
 * @author humberto.rojas
 */
@Named
@Stateless(name = "wpnFichaClienteUpdateDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteUpdateDAOImpl extends DAOUtil implements FichaClienteUpdateDAO{
    
     private static final Logger logger = Logger.getLogger(FichaClienteUpdateDAOImpl.class.getName());
     
     
    @Inject
    ParametrosController parametrosController;
     
     
     private final String urlWsdlFichaClienteUpdate = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteUpdateWs?wsdl";
     
     /*---------------------------------------------------------------------------------------------------*/
    @Override
    public RespuestaDTO insertarDatosCliente(String iCodigoCliente) {
        //FichaClienteUpdateDTO Fichacliente = new FichaClienteUpdateDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        //com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS            
            FichaClienteUpdateWs_Service service = new FichaClienteUpdateWs_Service(new URL(urlWsdlFichaClienteUpdate));
            FichaClienteUpdateWs port = service.getFichaClienteUpdateWsPort();
            //se obtiene el objeto de salida del WS
            port.insertUpdateFichaCliente(iCodigoCliente); 
            
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();           
            }

        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio consultarDatosCliente: ")
                    .append("USR-").append(iCodigoCliente)
                    
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN consultarDatosCliente: ")
                    .append("USR-").append(iCodigoCliente)
                    
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);       
        }
        return respuesta;
     }
  
    
}
