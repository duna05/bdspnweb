/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dao.PreguntasAutenticacionDAO;
import com.bds.wpn.dto.PreguntaAutenticacionDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.PreguntasAutenticacionWS;
import com.bds.wpn.ws.services.PreguntasAutenticacionWS_Service;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
@Stateless(name = "wpnPreguntasAutenticacionDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class PreguntasAutenticacionDAOImpl extends DAOUtil implements PreguntasAutenticacionDAO {
    
     /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(PreguntasAutenticacionDAOImpl.class.getName());
    
    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;
    
    private final String urlWsdlPreguntasAutenticacion = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/PreguntasAutenticacionWS?WSDL";
    
    /**
     * Metodo para obtener las preguntas de autenticacion de un cliente (core bancario)
     * @param tdd tarjeta de debito del cliente
     * @param nroCta numero de cuenta del cliente
     * @param nroPreguntas numero de preguntas a mostrar
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return PreguntaAutenticacionDTO -> con listado de preguntas de autenticacion
     */
    @Override
    public PreguntaAutenticacionDTO listPDAporCliente(String tdd, String nroCta, int nroPreguntas, String nombreCanal, String idCanal){
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.PreguntaAutenticacionDTO preguntaAutenticacionWs;
        PreguntaAutenticacionDTO preguntaAutenticacion = new PreguntaAutenticacionDTO();
        PreguntaAutenticacionDTO preguntaTemp;
        try {
            //invocacion del WS
            PreguntasAutenticacionWS_Service service = new PreguntasAutenticacionWS_Service(new URL(urlWsdlPreguntasAutenticacion));
            PreguntasAutenticacionWS port = service.getPreguntasAutenticacionWSPort();
            //se obtiene el objeto de salida del WS
            preguntaAutenticacionWs = port.listPDAporCliente(tdd, nroCta, nroPreguntas, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, preguntaAutenticacionWs.getRespuesta());
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                if(preguntaAutenticacionWs.getPreguntasAutenticacion()!= null && !preguntaAutenticacionWs.getPreguntasAutenticacion().isEmpty()){
                    Collection<PreguntaAutenticacionDTO> preguntas = new ArrayList<>();
                    for(com.bds.wpn.ws.services.PreguntaAutenticacionDTO pregAutWs : preguntaAutenticacionWs.getPreguntasAutenticacion()){
                        if(pregAutWs != null && pregAutWs.getCodigoPregunta() != null){
                            preguntaTemp = new PreguntaAutenticacionDTO();
                            BeanUtils.copyProperties(preguntaTemp, pregAutWs);
                            preguntas.add(preguntaTemp);
                        }
                    }
                    preguntaAutenticacion.setPreguntasAutenticacion(preguntas);
                }
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listPDAporCliente: ")
                    .append("IDCH-").append(idCanal)
                    .append("-TDD-").append(this.formatoAsteriscosWeb(tdd))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listPDAporCliente: ")
                    .append("IDCH-").append(idCanal)
                    .append("-TDD-").append(this.formatoAsteriscosWeb(tdd))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (preguntaAutenticacion.getPreguntasAutenticacion() == null) {
                preguntaAutenticacion.setPreguntasAutenticacion(new ArrayList<PreguntaAutenticacionDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            preguntaAutenticacion.setRespuestaDTO(respuesta);
        }
        return preguntaAutenticacion;
    }
    
    /**
     * Metodo para verificar las respuestas de autenticacion 
     * @param tdd tarjeta de debito del cliente
     * @param nroCta numero de cuenta del cliente
     * @param rafaga cadena con las preguntas y respuestas a validar ej: < codigoPregunta >< separador > < codigoPregunta >< separador >< codigoPregunta >< separador >
     * @param separador separador que utilizara en la rafaga, si este es null se tomará guion "-" como separador
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return UtilDTO con respuesta valida o no para las respuestas
     */
    @Override
    public UtilDTO validarPDAporCliente(String tdd, String nroCta, String rafaga, String separador, String nombreCanal, String idCanal){
    RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            PreguntasAutenticacionWS_Service service = new PreguntasAutenticacionWS_Service(new URL(urlWsdlPreguntasAutenticacion));
            PreguntasAutenticacionWS port = service.getPreguntasAutenticacionWSPort();
            //se obtiene el objeto de salida del WS
            utilWs = port.validarPDAporCliente(tdd, nroCta, rafaga, separador, nombreCanal);
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
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio validarPDAporCliente: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(tdd))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN validarPDAporCliente: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(tdd))
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
    
}
