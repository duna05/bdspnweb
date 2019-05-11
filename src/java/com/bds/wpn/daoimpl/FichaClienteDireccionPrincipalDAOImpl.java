/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.controller.TextosController;
import com.bds.wpn.dao.FichaClienteDAO;
import com.bds.wpn.dao.FichaClienteDireccionPrincipalDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.FichaClienteDTO;
import com.bds.wpn.dto.FichaClienteDireccionPrincipalDTO;
import com.bds.wpn.dto.PrestamoDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.SubtipoClienteDTO;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.ws.services.FichaClienteDireccionPrincipalWs;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.log4j.Logger;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import com.bds.wpn.ws.services.FichaClienteDireccionPrincipalWs_Service;
import com.bds.wpn.ws.services.FichaClienteWs_Service;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author humberto.rojas
 */
@Named
@Stateless(name = "wpnFichaClienteDireccionPrincipalDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteDireccionPrincipalDAOImpl extends DAOUtil implements FichaClienteDireccionPrincipalDAO {
  
    private static final Logger logger = Logger.getLogger(FichaClienteDAOImpl.class.getName());
    
    private final String urlWsdlFichaClienteDireccionPrincipal = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteDireccionPrincipalWs?wsdl";
    //private final String urlWsdlFichaClienteDireccionPrincipal = "http://localhost:7001/ibdsws/FichaClienteDireccionPrincipalWs?wsdl";
  
    @Override
    public FichaClienteDireccionPrincipalDTO consultarDatosDirPpalCliente(String iCodigoCliente) {
        //SubTipoClienteJuridicoDTO subTipoClienteJuridicoDTO = new SubTipoClienteJuridicoDTO();
        FichaClienteDireccionPrincipalDTO fichaclienteDireccionPrincipal = new FichaClienteDireccionPrincipalDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            FichaClienteDireccionPrincipalWs_Service service = new FichaClienteDireccionPrincipalWs_Service(new URL(urlWsdlFichaClienteDireccionPrincipal));
            FichaClienteDireccionPrincipalWs port = service.getFichaClienteDireccionPrincipalWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.FichaClienteDireccionPrincipalDTO fichaClienteDireccionPrincipalWs = port.consultarDatosDireccionCliente(iCodigoCliente);     
            //clase para casteo dinamico de atributos
        
            BeanUtils.copyProperties(respuesta, fichaClienteDireccionPrincipalWs.getRespuesta());  
            
            fichaclienteDireccionPrincipal.setUrbanizacion(fichaClienteDireccionPrincipalWs.getUrbanizacion());
            fichaclienteDireccionPrincipal.setTelefonos(fichaClienteDireccionPrincipalWs.getTelefonos());
            fichaclienteDireccionPrincipal.setTelex(fichaClienteDireccionPrincipalWs.getTelex());
            fichaclienteDireccionPrincipal.setTenencia(fichaClienteDireccionPrincipalWs.getTenencia());
            fichaclienteDireccionPrincipal.setCodigoPostal(fichaClienteDireccionPrincipalWs.getCodigoPostal());
            fichaclienteDireccionPrincipal.setCiudadPN(fichaClienteDireccionPrincipalWs.getCiudadPN());
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(fichaclienteDireccionPrincipal, fichaClienteDireccionPrincipalWs);
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
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            fichaclienteDireccionPrincipal.setRespuesta(respuesta);
        }
        return fichaclienteDireccionPrincipal;
    
    
    
    
    
    }
    
}
