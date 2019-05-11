/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.DelSurDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.AgenciaDTO;
import com.bds.wpn.dto.BancoDTO;
import com.bds.wpn.dto.DelSurDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.DelSurWS;
import com.bds.wpn.ws.services.DelSurWS_Service;
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
@Stateless(name = "wpnDelSurDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class DelSurDAOImpl extends DAOUtil implements DelSurDAO {

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    ParametrosController parametrosController;

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(DelSurDAOImpl.class.getName());

    private final String urlWsdlDelsur = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/DelSurWS?WSDL";

    /**
     * Metodo que obtiene el detalle de los nombres de las agencias y estados.
     *
     * @param codigoCanal codigo canal
     * @param idCanal identificador del canal
     * @return DelSurDTO listado de las agencias
     */
    @Override
    public DelSurDTO obtenerAgencias(String codigoCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        DelSurDTO delsur = new DelSurDTO();
        List<AgenciaDTO> listAgencias = new ArrayList<>();
        try {
            //invocacion del WS
            DelSurWS_Service service = new DelSurWS_Service(new URL(urlWsdlDelsur));
            DelSurWS port = service.getDelSurWSPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.DelSurDTO delsurWs = port.obtenerDatosEstadoAgencias(codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, delsurWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                for (com.bds.wpn.ws.services.AgenciaDTO agenciaWS : delsurWs.getAgencias()) {
                    AgenciaDTO agencia = new AgenciaDTO();
                    
                    BeanUtils.copyProperties(agencia, agenciaWS);
                    
                    listAgencias.add(agencia);
                }
                
                delsur.setAgencias(listAgencias);
                
            }
        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio obtenerAgencias: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN obtenerAgencias: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(this.CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (delsur.getAgencias() == null) {
                delsur.setAgencias(new ArrayList<AgenciaDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            delsur.setRespuestaDTO(respuesta);
        }
        return delsur;
    }

    /**
     * Metodo para obtener el listado de bancos
     *
     * @param codigoCanal codigo del canal
     * @param idCanal id del canal
     * @return listado de bancos
     */
    @Override
    public DelSurDTO listadoBancos(String codigoCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        DelSurDTO delsur = new DelSurDTO();
        List<BancoDTO> listBancos = new ArrayList<>();
        try {
            //invocacion del WS
            DelSurWS_Service service = new DelSurWS_Service(new URL(urlWsdlDelsur));
            DelSurWS port = service.getDelSurWSPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.DelSurDTO delsurWs = port.listadoBancos(codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, delsurWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                for (com.bds.wpn.ws.services.BancoDTO bancoWS : delsurWs.getBancos()) {
                    BancoDTO banco = new BancoDTO();
                    
                    BeanUtils.copyProperties(banco, bancoWS);
                    
                    listBancos.add(banco);
                }
                
                delsur.setBancos(listBancos);
                
            }
        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio listadoBancos: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN listadoBancos: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (delsur.getBancos() == null) {
                delsur.setBancos(new ArrayList<BancoDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            delsur.setRespuestaDTO(respuesta);
        }
        return delsur;
    }
    
    /**
     * Metodo para obtener el listado de bancos afiliados a Switch7B
     *
     * @param codigoCanal codigo del canal
     * @param idCanal id del canal
     * @return listado de bancos
     */
    @Override
    public DelSurDTO listadoBancosSwitch7B(String codigoCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        DelSurDTO delsur = new DelSurDTO();
        List<BancoDTO> listBancos = new ArrayList<>();
        try {
            //invocacion del WS
            DelSurWS_Service service = new DelSurWS_Service(new URL(urlWsdlDelsur));
            DelSurWS port = service.getDelSurWSPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.DelSurDTO delsurWs = port.listadoBancosSwitch7B(codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, delsurWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                for (com.bds.wpn.ws.services.BancoDTO bancoWS : delsurWs.getBancos()) {
                    BancoDTO banco = new BancoDTO();
                    
                    BeanUtils.copyProperties(banco, bancoWS);
                    
                    listBancos.add(banco);
                }
                
                delsur.setBancos(listBancos);
                
            }
        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio listadoBancosSwitch7B: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN listadoBancosSwitch7B: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (delsur.getBancos() == null) {
                delsur.setBancos(new ArrayList<BancoDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            delsur.setRespuestaDTO(respuesta);
        }
        return delsur;
    }

    /**
     * Metodo para verificar que el banco no posee restricciones internas
     * @param codbanco codigo del banco
     * @param nombreCanal nombre del canal
     * @param idCanal id del canal
     * @return RespuestaDTO
     */
    @Override
    public UtilDTO verificarBloqueBanco (String codbanco, String nombreCanal, String idCanal){
        
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            DelSurWS_Service service = new DelSurWS_Service(new URL(urlWsdlDelsur));
            DelSurWS port = service.getDelSurWSPort();
            //se obtiene el objeto de salida del WS
            utilWs = port.verificarBloqueBanco(codbanco, nombreCanal);
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
                resultados.put("bancoBloqueado", (utilWs.getResulados().getEntry().get(0).getValue()));
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);                
            }

        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio verificarBloqueBanco: ")
                    .append("BCO-").append(codbanco)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN verificarBloqueBanco: ")
                    .append("BCO-").append(codbanco)
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
     * Metodo que valida si el usuario está habilitado para hacer transacciones P2P
     * @param cedula
     * @param codigoCanal codigo del canal
     * @param idCanal id del canal
     * @return listado de bancos
     */
    @Override
    public boolean validarUsuarioP2P(String cedula, String idCanal, String codigoCanal) {
//        RespuestaDTO respuesta = new RespuestaDTO();
//        try {
//            //invocacion del WS
//            DelSurWS_Service service = new DelSurWS_Service(new URL(urlWsdlDelsur));
//            DelSurWS port = service.getDelSurWSPort();
//            //se obtiene el objeto de salida del WS
//            com.bds.wpn.ws.services.UtilDTO util = port.validarUsuarioPilotoP2P(cedula, idCanal, codigoCanal);
//            //clase para casteo dinamico de atributos
//            BeanUtils.copyProperties(respuesta, util.getRespuesta());
//            //validacion de codigo de respuesta
//            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
//                return false;
//            } else {
//                Map resultados = new HashMap();
//                for (com.bds.wpn.ws.services.UtilDTO.Resulados.Entry aEntry : util.getResulados().getEntry()) {
//                    resultados.put(aEntry.getKey(), aEntry.getValue());
//                }
//                return (boolean) resultados.get(VALIDO);
//            }
//        } catch (Exception e) {
//            logger.error( new StringBuilder("ERROR DAO EN validarUsuarioP2P: ")
//                    .append("-CH-").append(codigoCanal)
//                    .append("-DT-").append(new Date())
//                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
//                    .append("-EXCP-").append(e.toString()).toString(),e);
//            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
//        }
        return true;
    }
    
}
