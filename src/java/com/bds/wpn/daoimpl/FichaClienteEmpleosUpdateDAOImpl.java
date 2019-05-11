/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.FichaClienteEmpleosUpdateDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.ws.services.FichaClienteEmpleosUpdateWs;
import com.bds.ws.services.FichaClienteEmpleosUpdateWs_Service;
import java.net.URL;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Logger;

/**
 *
 * @author audra.zapata
 */
@Named
@Stateless(name = "wpnFichaClienteEmpleosUpdateDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteEmpleosUpdateDAOImpl  extends DAOUtil implements FichaClienteEmpleosUpdateDAO{
     private static final Logger logger = Logger.getLogger(FichaClienteUpdateDAOImpl.class.getName());
     
     @Inject
    ParametrosController parametrosController;
     
    private final String urlWsdlFichaClienteEmpleosUpdate = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteEmpleosUpdateWs?wsdl";
     
    @Override
    public RespuestaDTO actualizarDatosEmpleos(String iCodigoCliente, String rif, String ramo, String direccion, String telefono, String secuencia) {
        RespuestaDTO respuesta = new RespuestaDTO();

        try {
            //invocacion del WS            
            FichaClienteEmpleosUpdateWs_Service service = new FichaClienteEmpleosUpdateWs_Service(new URL(urlWsdlFichaClienteEmpleosUpdate));
            FichaClienteEmpleosUpdateWs port = service.getFichaClienteEmpleosUpdateWsPort();
            //se obtiene el objeto de salida del WS
            port.actualizarDatosEmpleos(iCodigoCliente, rif, ramo, direccion, telefono, secuencia);
            
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
