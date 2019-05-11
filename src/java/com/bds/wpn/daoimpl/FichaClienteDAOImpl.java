/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

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
import com.bds.wpn.ws.services.FichaClienteWs;
import com.bds.wpn.ws.services.FichaClienteWs_Service;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author humberto.rojas
 */
@Named
@Stateless(name = "wpnFichaClienteDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteDAOImpl extends DAOUtil implements FichaClienteDAO {
    
    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;

    @Inject
    TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(FichaClienteDAOImpl.class.getName());

    private final String urlWsdlFichaCliente = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteWs?wsdl";
    //private final String urlWsdlFichaCliente = "http://localhost:7001/ibdsws/FichaClienteWs?wsdl";
     //private final String urlWsdlSubTipoClienteJuridico = "http://localhost:7001/ibdsws/SubTipoClienteWs?wsdl";

    @Override
    public FichaClienteDTO consultarDatosCliente(String iCodigoCliente) {
        SubtipoClienteDTO subTipoClienteJuridicoDTO = new SubtipoClienteDTO();
        FichaClienteDTO Fichacliente = new FichaClienteDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            FichaClienteWs_Service service = new FichaClienteWs_Service(new URL(urlWsdlFichaCliente));
            FichaClienteWs port = service.getFichaClienteWsPort();
            //se obtiene el objeto de salida del WS
           com.bds.wpn.ws.services.FichaClienteDTO FichaclienteWs = port.consultarDatosCliente(iCodigoCliente);     
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, FichaclienteWs.getRespuesta());
            
            List<FichaClienteDTO> listCliente = new ArrayList<>();
          
            Fichacliente.setNombreComercial(FichaclienteWs.getNombreComercial());
            Fichacliente.setRazonSocial(FichaclienteWs.getRazonSocial());
            Fichacliente.setDescCargoR(FichaclienteWs.getDescCargoR());
            Fichacliente.setTipoCliente(FichaclienteWs.getTipoCliente());
            Fichacliente.setSexo(FichaclienteWs.getSexo());
            
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(Fichacliente, FichaclienteWs);
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
            Fichacliente.setRespuestaDTO(respuesta);
        }
        return Fichacliente;
    
    
    
    
    
    }

    
   
    
    
    
}











/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
package com.bds.wpn.daoimpl;

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
import com.bds.wpn.dto.SubTipoClienteJuridicoDTO;
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
import com.bds.wpn.ws.services.FichaClienteWs;
import com.bds.wpn.ws.services.FichaClienteWs_Service;
import java.util.ArrayList;
import java.util.List;


@Named
@Stateless(name = "wpnFichaClienteDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteDAOImpl extends DAOUtil implements FichaClienteDAO {
    
    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;

    @Inject
    TextosController textosController;

    @Inject
    ParametrosController parametrosController;

   
    private static final Logger logger = Logger.getLogger(FichaClienteDAOImpl.class.getName());

    //private final String urlWsdlFichaCliente = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteWs?wsdl";
    private final String urlWsdlFichaCliente = "http://localhost:7001/ibdsws/FichaClienteWs?wsdl";
     private final String urlWsdlSubTipoClienteJuridico = "http://localhost:7001/ibdsws/SubTipoClienteWs?wsdl";

    @Override
    public FichaClienteDTO consultarDatosCliente(String iCodigoCliente) {
        SubTipoClienteJuridicoDTO subTipoClienteJuridicoDTO = new SubTipoClienteJuridicoDTO();
        FichaClienteDTO Fichacliente = new FichaClienteDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            FichaClienteWs_Service service = new FichaClienteWs_Service(new URL(urlWsdlFichaCliente));
            FichaClienteWs port = service.getFichaClienteWsPort();
            //se obtiene el objeto de salida del WS
           com.bds.wpn.ws.services.FichaClienteDTO FichaclienteWs = port.consultarDatosCliente(iCodigoCliente);     
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, FichaclienteWs.getRespuesta());
            
            List<FichaClienteDTO> listCliente = new ArrayList<>();
            
           
            Fichacliente.setNombreComercial(FichaclienteWs.getNombreComercial());
            Fichacliente.setRazonSocial(FichaclienteWs.getRazonSocial());
            Fichacliente.setDescCargoR(FichaclienteWs.getDescCargoR());
            
            
            
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(Fichacliente, FichaclienteWs);
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
            Fichacliente.setRespuestaDTO(respuesta);
        }
        return Fichacliente;
    
    
    
    
    
    }

    

    public FichaClienteDTO consultarDireccionCliente(String iCodigoCliente) {
        SubTipoClienteJuridicoDTO subTipoClienteJuridicoDTO = new SubTipoClienteJuridicoDTO();
        FichaClienteDTO Fichacliente = new FichaClienteDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            FichaClienteWs_Service service = new FichaClienteWs_Service(new URL(urlWsdlFichaCliente));
            FichaClienteWs port = service.getFichaClienteWsPort();
            //se obtiene el objeto de salida del WS
           com.bds.wpn.ws.services.FichaClienteDTO FichaclienteWs = port.consultarDatosCliente(iCodigoCliente);     
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, FichaclienteWs.getRespuesta());
            
            List<FichaClienteDTO> listCliente = new ArrayList<>();
            
           
            Fichacliente.setNombreComercial(FichaclienteWs.getNombreComercial());
            Fichacliente.setRazonSocial(FichaclienteWs.getRazonSocial());
            Fichacliente.setDescCargoR(FichaclienteWs.getDescCargoR());
            
            
            
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(Fichacliente, FichaclienteWs);
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
            Fichacliente.setRespuestaDTO(respuesta);
        }
        return Fichacliente;
    
    
    
    
    
    }

    
    
    
}*/
