/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.FichaClienteRefBancariasUpdateDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.ws.services.FichaClienteRefBancariasUpdateWs;
import com.bds.ws.services.FichaClienteRefBancariasUpdateWs_Service;
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
@Stateless(name = "wpnFichaClienteRefBancariasUpdateDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteRefBancariasUpdateDAOImpl  extends DAOUtil implements FichaClienteRefBancariasUpdateDAO{
    private static final Logger logger = Logger.getLogger(FichaClienteRefTarjetaUpdateDAOImpl.class.getName());
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlFichaClienteRefBancariasUpdate = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteRefBancariasUpdateWs?wsdl";

    @Override
    public RespuestaDTO actualizarDatosRefBancarias(String iCodigoCliente) {
        RespuestaDTO respuesta = new RespuestaDTO();
        //com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS            
            FichaClienteRefBancariasUpdateWs_Service service = new FichaClienteRefBancariasUpdateWs_Service(new URL(urlWsdlFichaClienteRefBancariasUpdate));
            FichaClienteRefBancariasUpdateWs port = service.getFichaClienteRefBancariasUpdateWsPort();
            //se obtiene el objeto de salida del WS
            port.actualizarDatosRefBancarias(iCodigoCliente);
            
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
