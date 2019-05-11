/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.FichaClienteRefTarjetaUpdateDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.ws.services.FichaClienteRefTarjetaUpdateWs;
import com.bds.ws.services.FichaClienteRefTarjetaUpdateWs_Service;
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
@Stateless(name = "wpnFichaClienteRefTarjetaUpdateDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteRefTarjetaUpdateDAOImpl extends DAOUtil implements FichaClienteRefTarjetaUpdateDAO {
    private static final Logger logger = Logger.getLogger(FichaClienteRefTarjetaUpdateDAOImpl.class.getName());
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlFichaClienteRefTarjetaUpdate = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteRefTarjetaUpdateWs?wsdl";
     
    @Override
    public RespuestaDTO actualizarRefTarjeta(String iCodigoCliente) {
         RespuestaDTO respuesta = new RespuestaDTO();
        //com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS            
            FichaClienteRefTarjetaUpdateWs_Service service = new FichaClienteRefTarjetaUpdateWs_Service(new URL(urlWsdlFichaClienteRefTarjetaUpdate));
            FichaClienteRefTarjetaUpdateWs port = service.getFichaClienteRefTarjetaUpdateWsPort();
            //se obtiene el objeto de salida del WS
            port.actualizarRefTarjeta(iCodigoCliente);
            
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
