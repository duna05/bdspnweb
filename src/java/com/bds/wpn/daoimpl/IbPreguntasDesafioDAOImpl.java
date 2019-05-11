/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbPreguntasDesafioDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.IbPregDesafioUsuarioDTO;
import com.bds.wpn.dto.IbPreguntasDesafioDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbPreguntasDesafioWS;
import com.bds.wpn.ws.services.IbPreguntasDesafioWS_Service;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
 * @author cesar.mujica
 */
@Named
@Stateless(name = "wpnIbPreguntasDesafioDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbPreguntasDesafioDAOImpl extends DAOUtil implements IbPreguntasDesafioDAO {

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(IbPreguntasDesafioDAOImpl.class.getName());

    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlPreguntasDesafio = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbPreguntasDesafioWS?WSDL";

    /**
     * Metodo para obtener el listado de preguntas de desafio del banco
     *
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return IbPreguntasDesafioDTO listado de las preguntas de desafio
     */
    @Override
    public IbPregDesafioUsuarioDTO listadoPreguntasDesafioBanco(String nombreCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.IbPreguntasDesafioDTO preguntasDesafioWs;
        IbPreguntasDesafioDTO preguntasDesafio;
        IbPregDesafioUsuarioDTO preguntasDesafioBanco = new IbPregDesafioUsuarioDTO();
        try {
            //invocacion del WS
            IbPreguntasDesafioWS_Service service = new IbPreguntasDesafioWS_Service(new URL(urlWsdlPreguntasDesafio));
            IbPreguntasDesafioWS port = service.getIbPreguntasDesafioWSPort();
            //se obtiene el objeto de salida del WS
            preguntasDesafioWs = port.listadoPreguntasDesafioBanco(nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, preguntasDesafioWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                if (preguntasDesafioWs.getPreguntasDesafio() != null && !preguntasDesafioWs.getPreguntasDesafio().isEmpty()) {
                    Collection<IbPreguntasDesafioDTO> preguntas = new ArrayList<>();
                    for (com.bds.wpn.ws.services.IbPreguntasDesafio pregDesafioWs : preguntasDesafioWs.getPreguntasDesafio()) {
                        preguntasDesafio = new IbPreguntasDesafioDTO();
                        BeanUtils.copyProperties(preguntasDesafio, pregDesafioWs);
                        preguntas.add(preguntasDesafio);
                    }
                    preguntasDesafioBanco.setPreguntasDesafioUsr(preguntas);
                }
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoPreguntasDesafioBanco: ")
                    .append("IDCH-").append(idCanal)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoPreguntasDesafioBanco: ")
                    .append("IDCH-").append(idCanal)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            preguntasDesafioBanco.setRespuestaDTO(respuesta);
        }
        return preguntasDesafioBanco;
    }

    ;
    
    /**
     * Metodo para validar una rafaga de preguntas de desafio
     *
     * @param idUsuario id del usuario
     * @param rafaga cadena con las preguntas y respuestas a validar ej: < codigoPregunta >< separador
     * > < codigoPregunta >< separador >< codigoPregunta >< separador >
     * @param separador separador que utilizara en la rafaga, si este es null se tomará guion "-" como separador
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return UtilDTO -> 'true' en caso que la respuesta sea corresta 'false'
     * en caso contrario
     */
    @Override
    public UtilDTO validarPreguntaDD(String idUsuario, String rafaga, String separador, String nombreCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            IbPreguntasDesafioWS_Service service = new IbPreguntasDesafioWS_Service(new URL(urlWsdlPreguntasDesafio));
            IbPreguntasDesafioWS port = service.getIbPreguntasDesafioWSPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            utilWs = port.validarPreguntaDD(idUsuario, rafaga, separador, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put("resultado", (utilWs.getResulados().getEntry().get(0).getValue()));
                util.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio validarPreguntaDD: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN validarPreguntaDD: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            util.setRespuestaDTO(respuesta);
        }
        return util;
    }

    /**
     * Metodo que devuelve un listado de preguntas de desafio de un cliente
     *
     * @param idUsuario id del usuario
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return retorna un listado de preguntas de desafio de un cliente
     */
    @Override
    public IbPregDesafioUsuarioDTO listadoPreguntasDesafioUsuario(String idUsuario, String nombreCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        IbPreguntasDesafioDTO preguntasDesafio;
        com.bds.wpn.ws.services.IbPregDesafioUsuarioDTO preguntasDesafioUsrWs;
        IbPregDesafioUsuarioDTO preguntasDesafioUsr = new IbPregDesafioUsuarioDTO();
        try {
            //invocacion del WS
            IbPreguntasDesafioWS_Service service = new IbPreguntasDesafioWS_Service(new URL(urlWsdlPreguntasDesafio));
            IbPreguntasDesafioWS port = service.getIbPreguntasDesafioWSPort();
            //se obtiene el objeto de salida del WS
            preguntasDesafioUsrWs = port.listadoPreguntasDesafioUsuario(idUsuario, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, preguntasDesafioUsrWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                if (preguntasDesafioUsrWs.getIbPregDesafioUsuarios() != null && !preguntasDesafioUsrWs.getIbPregDesafioUsuarios().isEmpty()) {
                    Collection<IbPreguntasDesafioDTO> preguntas = new ArrayList<>();
                    for (com.bds.wpn.ws.services.IbPregDesafioUsuario pregDesafioUsrWs : preguntasDesafioUsrWs.getIbPregDesafioUsuarios()) {
                        if (pregDesafioUsrWs != null && pregDesafioUsrWs.getIdPreguntaDesafio() != null) {
                            preguntasDesafio = new IbPreguntasDesafioDTO();
                            BeanUtils.copyProperties(preguntasDesafio, pregDesafioUsrWs.getIdPreguntaDesafio());
                            preguntas.add(preguntasDesafio);
                        }
                    }
                    preguntasDesafioUsr.setPreguntasDesafioUsr(preguntas);
                }
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoPreguntasDesafioUsuario: ")
                    .append("IDCH-").append(idCanal)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoPreguntasDesafioUsuario: ")
                    .append("IDCH-").append(idCanal)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (preguntasDesafioUsr.getPreguntasDesafioUsr() == null) {
                preguntasDesafioUsr.setPreguntasDesafioUsr(new ArrayList<IbPreguntasDesafioDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            preguntasDesafioUsr.setRespuestaDTO(respuesta);
        }
        return preguntasDesafioUsr;
    }

    ;
    
    /**
     * Metodo que sustituye las preguntas de desafio viejas por las nuevas con sus respuestas
     * @param listPDDUsuarioRespuestas listado de preguntas y respuestas
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return RespuestaDTO
     */
    @Override
    public RespuestaDTO agregarPDDUsuario(List<IbPregDesafioUsuarioDTO> listPDDUsuarioRespuestas, String nombreCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        com.bds.wpn.ws.services.PreguntaRespuestaUsuarioDTO listPregRespWS = new com.bds.wpn.ws.services.PreguntaRespuestaUsuarioDTO();
        try {
            //invocacion del WS
            IbPreguntasDesafioWS_Service service = new IbPreguntasDesafioWS_Service(new URL(urlWsdlPreguntasDesafio));
            IbPreguntasDesafioWS port = service.getIbPreguntasDesafioWSPort();
            //se castea la lista de preguntas del proyecto web al ws
            for (IbPregDesafioUsuarioDTO pruWeb : listPDDUsuarioRespuestas) {
                com.bds.wpn.ws.services.PreguntaRespuestaUsuarioDTO pregRespTempWS = new com.bds.wpn.ws.services.PreguntaRespuestaUsuarioDTO();
                BeanUtils.copyProperties(pregRespTempWS, pruWeb);
                listPregRespWS.getPreguntasRespuestasUsuarios().add(pregRespTempWS);
            }

            respuestaWs = port.agregarPDDUsuario(listPregRespWS, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio agregarPDDUsuario: ")
                    .append("USR-").append(listPDDUsuarioRespuestas.iterator().next().getIdUsuario())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN agregarPDDUsuario: ")
                    .append("USR-").append(listPDDUsuarioRespuestas.iterator().next().getIdUsuario())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }
        return respuesta;
    }
;

}
