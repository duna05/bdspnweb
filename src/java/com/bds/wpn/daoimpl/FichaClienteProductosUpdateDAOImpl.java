/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.FichaClienteProductosUpdateDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.ws.services.FichaClienteProductosUpdateWs;
import com.bds.ws.services.FichaClienteProductosUpdateWs_Service;
import java.net.URL;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Logger;

/**
 *
 * @author MGIAccusys
 */
@Named
@Stateless(name = "wpnFichaClienteProductosUpdateDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteProductosUpdateDAOImpl extends DAOUtil implements FichaClienteProductosUpdateDAO{
  
     private static final Logger logger = Logger.getLogger(FichaClienteProductosUpdateDAOImpl.class.getName());
     
     
    @Inject
    ParametrosController parametrosController;
     
     
     private final String urlWsdlFichaClienteProductosUpdate = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteProductosUpdateWs?wsdl";
                                                                                                                        
     @Override
    public RespuestaDTO actualizarClienteProductos(String iCodigoCliente, String motivoSolicitud) {
        //FichaClienteUpdateDTO Fichacliente = new FichaClienteUpdateDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        //com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS            
            FichaClienteProductosUpdateWs_Service service = new FichaClienteProductosUpdateWs_Service(new URL(urlWsdlFichaClienteProductosUpdate));
            FichaClienteProductosUpdateWs port = service.getFichaClienteProductosUpdateWsPort();
            //se obtiene el objeto de salida del WS            
            port.actualizarClienteProductos(iCodigoCliente, motivoSolicitud);
            
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
