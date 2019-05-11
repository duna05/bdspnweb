/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbClavesOperacionesEspecialesDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.enumerated.ClavesOperacionesEspecialesEnum;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade; 
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbClavesOperacionesEspecialesWs;
import com.bds.wpn.ws.services.IbClavesOperacionesEspecialesWs_Service;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 *
 * @author jose.perez
 */
@Named
@Stateless(name = "wpnIbClavesOperacionesEspecialesDAOImpl")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbClavesOperacionesEspecialesDAOImpl extends DAOUtil implements IbClavesOperacionesEspecialesDAO {

    private static final Logger logger = Logger.getLogger(IbClavesOperacionesEspecialesDAOImpl.class.getName());
    
    private final String urlWsdlclaveOP = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbClavesOperacionesEspecialesWs?wsdl";

    @EJB
    IbParametrosFacade parametrosFacade;

    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlUsuariosCanales = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbClavesOperacionesEspeciales?WSDL";

    @Override
    public String validarClaveOperacionesEspeciales(String idUsuario, String claveOP, String idCanal, String codigoCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        String resultadoUtil = "";
        try {            
            //invocacion del WS
            IbClavesOperacionesEspecialesWs_Service service = new IbClavesOperacionesEspecialesWs_Service(new URL(urlWsdlclaveOP));
            IbClavesOperacionesEspecialesWs port = service.getIbClavesOperacionesEspecialesWsPort();

            utilWs = port.validarClaveOP(idUsuario, claveOP, idCanal, codigoCanal);
            
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                Map resultados = new HashMap();

                for (com.bds.wpn.ws.services.UtilDTO.Resulados.Entry aEntry : utilWs.getResulados().getEntry()) {
                    resultados.put(aEntry.getKey(), aEntry.getValue());
                }
                
                if (resultados.get(BDSUtil.ESTADO_CLAVE_OP).equals(ClavesOperacionesEspecialesEnum.ACTIVO.getDescripcion())) {
                    resultadoUtil = ClavesOperacionesEspecialesEnum.ACTIVO.getDescripcion();
                }else if(resultados.get(BDSUtil.ESTADO_CLAVE_OP).equals(ClavesOperacionesEspecialesEnum.BLOQUEADO.getDescripcion())){
                    resultadoUtil = ClavesOperacionesEspecialesEnum.BLOQUEADO.getDescripcion();
                }else if(resultados.get(BDSUtil.ESTADO_CLAVE_OP).equals(ClavesOperacionesEspecialesEnum.INCORRECTO.getDescripcion())){
                    resultadoUtil = ClavesOperacionesEspecialesEnum.INCORRECTO.getDescripcion();
                }else if(resultados.get(BDSUtil.ESTADO_CLAVE_OP).equals(ClavesOperacionesEspecialesEnum.NO_REGISTRADO.getDescripcion())){
                    resultadoUtil = ClavesOperacionesEspecialesEnum.NO_REGISTRADO.getDescripcion();
                }else if(resultados.get(BDSUtil.ESTADO_CLAVE_OP).equals(ClavesOperacionesEspecialesEnum.YA_REGISTRADO.getDescripcion())){
                    resultadoUtil = ClavesOperacionesEspecialesEnum.YA_REGISTRADO.getDescripcion();
                }
            }

        } catch (Exception e) {
            logger.log(Level.ERROR, new StringBuilder("ERROR DAO EN ConsultarClaveOperacionesEspeciales: ")
                    .append("USR-").append(idUsuario)
                    .append("-CNL-").append(idCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        }
        return resultadoUtil;
    }

    @Override
    public UtilDTO insertarActualizarClaveOperacionesEspeciales(String idUsuario, String claveOP, String idUsuarioCarga, boolean sw, String idCanal, String codigoCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        UtilDTO utilDTO = new UtilDTO();
        Map resultados = new HashMap();
        try {
            //invocacion del WS
            IbClavesOperacionesEspecialesWs_Service service = new IbClavesOperacionesEspecialesWs_Service(new URL(urlWsdlclaveOP));
            IbClavesOperacionesEspecialesWs port = service.getIbClavesOperacionesEspecialesWsPort();

            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.UtilDTO utilWs = port.insertarActualizarClaveOP(idUsuario, claveOP, idUsuarioCarga, sw, idCanal, codigoCanal);

            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());

            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                resultados.put(utilWs.getResulados().getEntry().get(0).getKey(), utilWs.getResulados().getEntry().get(0).getValue());
                throw new ServiceException();
            }

            //obtenemos el resto de valores del objeto
            //cuando es un mapa debemos traer los valores uno a uno
            resultados.put(utilWs.getResulados().getEntry().get(0).getKey(), utilWs.getResulados().getEntry().get(0).getValue());

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio insertarActualizarClaveOperacionesEspeciales: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN insertarActualizarClaveOperacionesEspeciales: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            utilDTO.setRespuestaDTO(respuesta);
            utilDTO.setResuladosDTO(resultados);
        }
        return utilDTO;
    }

    @Override
    public boolean consultarClaveOperacionesEspeciales(String idUsuario, String idCanal, String codigoCanal) {
        boolean usuarioValido = false;
        try {
            //invocacion del WS
            IbClavesOperacionesEspecialesWs_Service service = new IbClavesOperacionesEspecialesWs_Service(new URL(urlWsdlclaveOP));
            IbClavesOperacionesEspecialesWs port = service.getIbClavesOperacionesEspecialesWsPort();

            usuarioValido = port.validarExisteClaveOperacionesEspeciales(idUsuario, idCanal, codigoCanal);

        } catch (Exception e) {
            logger.log(Level.ERROR, new StringBuilder("ERROR DAO EN ConsultarClaveOperacionesEspeciales: ")
                    .append("USR-").append(idUsuario)
                    .append("-CNL-").append(idCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        }
        return usuarioValido;
    }
}
