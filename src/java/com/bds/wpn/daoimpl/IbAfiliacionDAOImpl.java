/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbAfiliacionDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.IbAfiliacionesDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbAfiliacionWS;
import com.bds.wpn.ws.services.IbAfiliacionWS_Service;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author juan.faneite
 */
@Named
@Stateless(name = "wpnIbAfiliacionDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbAfiliacionDAOImpl extends DAOUtil implements IbAfiliacionDAO{
    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(IbAfiliacionDAOImpl.class.getName());
    
    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlAfiliaciones = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbAfiliacionWS?WSDL";

    /**
     * Metodo que Obtiene el listado de afiliados de un cliente por operacion.
     *
     * @param usuario String Referencia foranea al usuario dueno de la
     * afiliacion.
     * @param idTransaccion String ID de el tipo de transaccion.
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return IBAfiliacionesDTO listado de afiliados
     */
    @Override
    public IbAfiliacionesDTO obtenerListadoAfiliadosPorOperacion(String usuario, String idTransaccion, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        IbAfiliacionesDTO ibAfiliacionesDTO = new IbAfiliacionesDTO();

        try {
            IbAfiliacionWS_Service service = new IbAfiliacionWS_Service(new URL(urlWsdlAfiliaciones));
            IbAfiliacionWS port = service.getIbAfiliacionWSPort();
            com.bds.wpn.ws.services.IbAfiliacionesDTO ibAfiliacionesDTOWs = port.obtenerListadoAfiliadosPorOperacion(usuario, idTransaccion, idCanal, nombreCanal);
            BeanUtils.copyProperties(respuesta, ibAfiliacionesDTOWs.getRespuesta());
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {                
                throw new ServiceException();
            }
            
            List<IbAfiliacionesDTO> listAfiliacionesDTO = new ArrayList<>();
                for (com.bds.wpn.ws.services.IbAfiliaciones afiliacion : ibAfiliacionesDTOWs.getIbAfiliaciones()) {
                    IbAfiliacionesDTO afiliacionTemp = new IbAfiliacionesDTO();
                    BeanUtils.copyProperties(afiliacionTemp, afiliacion);
                    listAfiliacionesDTO.add(afiliacionTemp);
                }
                ibAfiliacionesDTO.setAfiliaciones(listAfiliacionesDTO);

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerListadoAfiliadosPorOperacion: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerListadoAfiliadosPorOperacion: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (ibAfiliacionesDTO.getAfiliaciones() == null) {
                ibAfiliacionesDTO.setAfiliaciones(new ArrayList<IbAfiliacionesDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            ibAfiliacionesDTO.setRespuestaDTO(respuesta);
        }
        
        return ibAfiliacionesDTO;
    }

    /**
     * Metodo que Obtiene el listado de afiliados de un cliente por operacion.
     *
     * @param nroIdentidad String Numero de CI del usuario dueno. (Ordenante)
     * @param codUsuario String Codigo del
     * @param idTransaccion String ID de el tipo de transaccion.
     * @param tipoTransf Indica el tipo de transferencia
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return IBAfiliacionesDTO listado de afiliados
     */
    @Override
    public IbAfiliacionesDTO afiliacionesOperacionCodUsuario(String nroIdentidad, String codUsuario, String idTransaccion, String tipoTransf, String nombreCanal) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Metodo para obtener el afiliado por codigo de usuario y Id de Usuario
     * @param idAfiliacion String id de la afiliacion
     * @param nombreCanal String ID del canal
     * @return IBAfiliacionesDTO -> afiliacion Seleccionada
     */
    @Override
    public IbAfiliacionesDTO obtenerAfiliacionPorId(String idAfiliacion, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        IbAfiliacionesDTO ibAfiliacionesDTO = new IbAfiliacionesDTO();
        try {
        
        IbAfiliacionWS_Service service = new IbAfiliacionWS_Service(new URL(urlWsdlAfiliaciones));
        IbAfiliacionWS port = service.getIbAfiliacionWSPort();
        com.bds.wpn.ws.services.IbAfiliacionesDTO ibAfiliacionesDTOWs = port.obtenerAfiliacionPorId(idAfiliacion, nombreCanal);
        BeanUtils.copyProperties(respuesta, ibAfiliacionesDTOWs.getRespuesta()); 
        if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {                
                throw new ServiceException();
            }
        
         BeanUtils.copyProperties(ibAfiliacionesDTO, ibAfiliacionesDTOWs.getIbAfiliacion()); 
        
        
        
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerAfiliacionPorId: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerAfiliacionPorId: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }  finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION            
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            ibAfiliacionesDTO.setRespuestaDTO(respuesta);
        }
       return ibAfiliacionesDTO; 
    }

    /**
     * Metodo para deshabilitar el afiliado por codigo de usuario y Id de
     * Usuario
     *
     * @param rafaga
     * @param separador
     * @param nombreCanal String ID del canal
     * @return RespuestaDTO
     */
    @Override
    public RespuestaDTO deshabilitarAfiliacion(String rafaga, String separador, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        
        try {
                       
            //invocacion del WS
            IbAfiliacionWS_Service service = new IbAfiliacionWS_Service(new URL(urlWsdlAfiliaciones));
            IbAfiliacionWS port = service.getIbAfiliacionWSPort();
            //se obtiene el objeto de salida del WS 
            respuestaWs = port.deshabilitarAfiliacion(rafaga, separador, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio deshabilitarAfiliacion: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN deshabilitarAfiliacion: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return respuesta;
    }

    /**
     * Metodo para actualizar una afiliacion
     *
     * @param idAfiliacion id de la afiliacion
     * @param alias alias de la afiliacion
     * @param email email del beneficiario
     * @param montoMax monto maximo
     * @param nombreBenef nombre del beneficiario
     * @param idCanal id del canal
     * @param nombreCanal nombre del canal
     * @return RespuestaDTO indica si la operacion se realizo de manera exitosa o no
     */
    @Override
    public RespuestaDTO actualizarAfiliacion(String idAfiliacion, String alias, char tipoDoc, String documento, String email, String montoMax, String nombreBenef, String nombreBanco,String nroCtaTDC, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS
            IbAfiliacionWS_Service service = new IbAfiliacionWS_Service(new URL(urlWsdlAfiliaciones));
            IbAfiliacionWS port = service.getIbAfiliacionWSPort();
            //se obtiene el objeto de salida del WS 
            respuestaWs = port.actualizarAfiliacion(idAfiliacion, alias, String.valueOf(tipoDoc), documento, email, montoMax, nombreBenef, nombreBanco, nroCtaTDC, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio actualizarAfiliacion: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN actualizarAfiliacion: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return respuesta;
    }

    /**
     * Metodo para verificar que no ingrese un alias existente
     * @param codUsuario codigo del usuario
     * @param alias alias del beneficiario
     * @param idCanal id del canal
     * @param nombreCanal nombre del canal
     * @return UtilDTO true si el alias existe false en caso contrario
     */
    @Override
    public UtilDTO verificarAlias(String codUsuario, String alias, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            IbAfiliacionWS_Service service = new IbAfiliacionWS_Service(new URL(urlWsdlAfiliaciones));
            IbAfiliacionWS port = service.getIbAfiliacionWSPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            utilWs = port.verificarAlias(codUsuario, alias, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            util.setRespuestaDTO(respuesta);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put("resultado", (utilWs.getResulados().getEntry().get(0).getValue()));
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);                
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio verificarAlias: ")
                    .append("CODUSR-").append(codUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN verificarAlias: ")
                    .append("CODUSR-").append(codUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION            
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            util.setRespuestaDTO(respuesta);
        }
        return util;
    }

    /**
     * Metodo para insertar una afiliacion
     * @param codUsuario codigo del usuario
     * @param alias alias del beneficiario
     * @param tipoDoc tipo de documento V o J
     * @param documento cedula o rif
     * @param email email del beneficiario
     * @param idTran id de la transaccion
     * @param montoMax monto maximo
     * @param nombreBanco nombre del banco
     * @param nombreBenef nombre del beneficiario
     * @param nroCtaTDC numero de cuenta o tdc
     * @param idCanal id del canal
     * @param nombreCanal nombre del canal
     * @return RespuestaDTO indica si la operacion se realizo de manera exitosa o no
     */
    @Override
    public RespuestaDTO insertarAfiliacion(String codUsuario, String alias, char tipoDoc, String documento, String email, String idTran, String montoMax, String nombreBanco, String nombreBenef, String nroCtaTDC, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS
            IbAfiliacionWS_Service service = new IbAfiliacionWS_Service(new URL(urlWsdlAfiliaciones));
            IbAfiliacionWS port = service.getIbAfiliacionWSPort();
            //se obtiene el objeto de salida del WS 
            respuestaWs = port.insertarAfiliacion(codUsuario, alias, String.valueOf(tipoDoc), documento, email,idTran, montoMax, nombreBanco, nombreBenef, nroCtaTDC, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio insertarAfiliacion: ")
                    .append("CODUSR-").append(codUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN insertarAfiliacion: ")
                    .append("CODUSR-").append(codUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return respuesta;
    }

    @Override
    public UtilDTO verificarProducto(String codUsuario, String producto,String idCanal, String nombreCanal) {
        
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            IbAfiliacionWS_Service service = new IbAfiliacionWS_Service(new URL(urlWsdlAfiliaciones));
            IbAfiliacionWS port = service.getIbAfiliacionWSPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            utilWs = port.verificarProducto(codUsuario, producto, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            util.setRespuestaDTO(respuesta);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put("resultado", (utilWs.getResulados().getEntry().get(0).getValue()));
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);                
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio verificarProducto: ")
                    .append("CODUSR-").append(codUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN verificarProducto: ")
                    .append("CODUSR-").append(codUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION            
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            util.setRespuestaDTO(respuesta);
        }
        return util;
        
        
    }

}
