/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.dao.FichaClienteDireccionDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.FichaClienteDireccionDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.ws.services.FichaClienteDireccionWs;
import com.bds.wpn.ws.services.FichaClienteDireccionWs_Service;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.xml.transform.Templates;
import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.controller.TextosController;
import com.bds.wpn.dao.FichaClienteDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.FichaClienteDTO;
import com.bds.wpn.dto.PrestamoDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.SubtipoClienteDTO;
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


/**
 *
 * @author alejandro.flores
 */
@Named
@Stateless(name = "wpnFichaClienteDireccionDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteDireccionDAOImpl extends DAOUtil implements FichaClienteDireccionDAO{
    
     private static final Logger logger = Logger.getLogger(FichaClienteDAOImpl.class.getName());
     private final String urlWsdlFichaClienteDireccion = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteDireccionWs?wsdl";
     //private final String urlWsdlFichaClienteDireccion = "http://localhost:7001/ibdsws/FichaClienteDireccionWs?wsdl";
    @Override
    public FichaClienteDireccionDTO consultarDatosDireccionCliente(String iCodigoCliente) {
       FichaClienteDireccionDTO fichaClienteDireccionDTO = new FichaClienteDireccionDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            FichaClienteDireccionWs_Service service = new FichaClienteDireccionWs_Service(new URL(urlWsdlFichaClienteDireccion));
            FichaClienteDireccionWs port = service.getFichaClienteDireccionWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.FichaClienteDireccionDTO fichaClienteDireccionWs = port.consultarDatosDireccionCliente(iCodigoCliente);     
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, fichaClienteDireccionWs.getRespuesta());
            
            
            List<FichaClienteDireccionDTO> listFichaClienteDireccion = new ArrayList<>();
            List<String> listFichaCliente = new ArrayList<>();
            
         
            
                
          
          for (com.bds.wpn.ws.services.FichaClienteDireccionDTO fichaClienteDirDTO : fichaClienteDireccionWs.getIbFichaClienteDireccionList()) {
                   
              
              
              System.out.println("TAMANO-------------------" + fichaClienteDireccionWs.getIbFichaClienteDireccionList().size());
              
                   /*listFichaCliente.add(fichaClienteDirDTO.getDescDireccion());
                   listFichaCliente.add(fichaClienteDirDTO.getCalle());
                   listFichaCliente.add(fichaClienteDirDTO.getDescSector());
                   listFichaCliente.add(fichaClienteDirDTO.getEdificio());
                   listFichaCliente.add(fichaClienteDirDTO.getNroApartamento());
                   listFichaCliente.add(fichaClienteDirDTO.getNombreDepartamento());
                   listFichaCliente.add(fichaClienteDirDTO.getTenencia());*/
             
                
            }
             
            
            
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
               // BeanUtils.copyProperties(subTipoClienteJuridicoDTO, subtipoClienteWs);
                
               fichaClienteDireccionDTO.setTipoDireccion(listFichaCliente);
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
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            fichaClienteDireccionDTO.setRespuestaDTO(respuesta);
        }
        return fichaClienteDireccionDTO;
      
    }

  
    
}
