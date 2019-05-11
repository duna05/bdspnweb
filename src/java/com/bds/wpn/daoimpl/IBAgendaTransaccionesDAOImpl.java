/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IBAgendaTransaccionesDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.IBAgendaPNDTO;
import com.bds.wpn.dto.IBAgendaTransaccionDTO;
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
import com.bds.wpn.ws.services.IBAgendaTransWS;
import com.bds.wpn.ws.services.IBAgendaTransWS_Service;
import java.math.BigDecimal;
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
 * @author cesar.mujica
 */
@Named
@Stateless(name = "wpnIBAgendaTransaccionesDAOweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IBAgendaTransaccionesDAOImpl extends DAOUtil implements IBAgendaTransaccionesDAO {

    private static final Logger logger = Logger.getLogger(IBAgendaTransaccionesDAOImpl.class.getName());
    
    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlAgenda = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IBAgendaTransWS?WSDL";


    /**
     * Metodo se encarga de almacenar la cabecera de una transaccion agendada
     *
     * @param agenda Objeto con los datos de la cabecera de transaccion a
     * agendar
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return RespuestaDTO
     */
    @Override
    public RespuestaDTO agregarCabeceraAgenda(IBAgendaTransaccionDTO agenda, String nombreCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS
            IBAgendaTransWS_Service service = new IBAgendaTransWS_Service(new URL(urlWsdlAgenda));
            IBAgendaTransWS port = service.getIBAgendaTransWSPort();
            //se obtiene el objeto de salida del WS 
            respuestaWs = port.agregarCabeceraAgenda(agenda.getIdUsuario(), 
                    agenda.getCuentaDestino(), 
                    agenda.getCuentaOrigen(), 
                    agenda.getDiaFrecuencia(), 
                    agenda.getMonto(), 
                    agenda.getFechalimite(), 
                    String.valueOf(agenda.getFrecuencia()), 
                    String.valueOf(agenda.getTipo()), 
                    nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio agregarCabeceraAgenda: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN agregarCabeceraAgenda: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return respuesta;
    }

    /**
     * Metodo se encarga de almacenar el detalle de una transaccion agendada
     *
     * @param agenda Objeto con los datos de la cabecera de transaccion a
     * agendar
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return RespuestaDTO
     */
    @Override
    public RespuestaDTO agregarDetalleAgenda(IBAgendaTransaccionDTO agenda, String nombreCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS
            IBAgendaTransWS_Service service = new IBAgendaTransWS_Service(new URL(urlWsdlAgenda));
            IBAgendaTransWS port = service.getIBAgendaTransWSPort();
            //se obtiene el objeto de salida del WS 
            respuestaWs = port.agregarDetalleAgenda(agenda.getIdUsuario(), 
                    agenda.getCuentaDestino(), 
                    agenda.getCuentaOrigen(), 
                    agenda.getDiaFrecuencia(), 
                    agenda.getMonto(), 
                    agenda.getFechalimite(), 
                    String.valueOf(agenda.getFrecuencia()), 
                    agenda.getDescripcion(), 
                    String.valueOf(agenda.getTipoDoc()), 
                    agenda.getDocumento(), 
                    agenda.getEmail(), 
                    agenda.getIdAgenda(), 
                    agenda.getIdTransaccion(), 
                    agenda.getNombreBeneficiario(), 
                    nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio agregarDetalleAgenda: ")
                    .append("USR-").append(agenda.getIdUsuario())
                    .append("CTAORG-").append(this.formatoAsteriscosWeb(agenda.getCuentaOrigen()))
                    .append("CTADST-").append(this.formatoAsteriscosWeb(agenda.getCuentaDestino()))
                    .append("MTO-").append(agenda.getMonto())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN agregarDetalleAgenda: ")
                    .append("USR-").append(agenda.getIdUsuario())
                    .append("CTAORG-").append(this.formatoAsteriscosWeb(agenda.getCuentaOrigen()))
                    .append("CTADST-").append(this.formatoAsteriscosWeb(agenda.getCuentaDestino()))
                    .append("MTO-").append(agenda.getMonto())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return respuesta;        
    }

    /**
     * Metodo se encarga de obtener el id de la cabecera de una transaccion
     * agendada
     *
     * @param agenda Objeto con los datos de la cabecera de transaccion a
     * agendar
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return UtilDTO
     */
    @Override
    public UtilDTO consultarIdCabeceraAgenda(IBAgendaTransaccionDTO agenda, String nombreCanal, String idCanal) {
        UtilDTO utilDTO = new UtilDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //invocacion del WS
            IBAgendaTransWS_Service service = new IBAgendaTransWS_Service(new URL(urlWsdlAgenda));
            IBAgendaTransWS port = service.getIBAgendaTransWSPort();
            //se obtiene el objeto de salida del WS 
            utilWs = port.consultarIdCabeceraAgenda(agenda.getIdUsuario(), 
                    agenda.getCuentaDestino(), 
                    agenda.getCuentaOrigen(), 
                    agenda.getDiaFrecuencia(), 
                    agenda.getMonto(), 
                    agenda.getFechalimite(), 
                    String.valueOf(agenda.getFrecuencia()), 
                    String.valueOf(agenda.getTipo()), 
                    nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put("id", (utilWs.getResulados().getEntry().get(0).getValue()));
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                utilDTO.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO EN consultarIdCabeceraAgenda: ")
                    .append("USR-").append(agenda.getIdUsuario())
                    .append("CTAORG-").append(this.formatoAsteriscosWeb(agenda.getCuentaOrigen()))
                    .append("CTADST-").append(this.formatoAsteriscosWeb(agenda.getCuentaDestino()))
                    .append("MTO-").append(agenda.getMonto())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN consultarIdCabeceraAgenda: ")
                    .append("USR-").append(agenda.getIdUsuario())
                    .append("CTAORG-").append(this.formatoAsteriscosWeb(agenda.getCuentaOrigen()))
                    .append("CTADST-").append(this.formatoAsteriscosWeb(agenda.getCuentaDestino()))
                    .append("MTO-").append(agenda.getMonto())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            utilDTO.setRespuestaDTO(respuesta);
        }
        return utilDTO;
    }
    
    /**
     * Metodo se encarga de obtener el id de la cabecera de una transaccion
     * agendada por el usuario, id de transaccion
     *
     * @param agenda Objeto con los datos de la cabecera de transaccion a
     * agendar
     * @param tipo
     * @param idTransaccion
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return UtilDTO
     */       
    @Override
    public IBAgendaPNDTO consultarIdCabeceraAgendaPP(BigDecimal id, String tipo, int idTransaccion, String nombreCanal, String idCanal) {
       com.bds.wpn.ws.services.IbAgendaPNDTO agendaWs;
        RespuestaDTO respuesta = new RespuestaDTO();
          IBAgendaPNDTO agendaDTO = new IBAgendaPNDTO();
        try {
            //invocacion del WS
            IBAgendaTransWS_Service service = new IBAgendaTransWS_Service(new URL(urlWsdlAgenda));
            IBAgendaTransWS port = service.getIBAgendaTransWSPort();
            
            //se obtiene el objeto de salida del WS 
            agendaWs = port.consultarIdCabeceraAgendaPP(id, tipo,idTransaccion,nombreCanal, idCanal);
            
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, agendaWs.getRespuesta());
            
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
               
                List<IBAgendaTransaccionDTO> listPagosProgramadosDTO = new ArrayList<>();
                for (com.bds.wpn.ws.services.IbAgendaTransaccionesPn agenda : agendaWs.getIbAgendas()) {
                    IBAgendaTransaccionDTO agendaTemp = new IBAgendaTransaccionDTO();
                     

                    BeanUtils.copyProperties(agendaTemp, agenda);
                    if (agenda.getIbDetalleAgendaTransPnCollection().size()>0)
                        agendaTemp.setNombreBeneficiario(agenda.getIbDetalleAgendaTransPnCollection().get(0).getNombre());
                    listPagosProgramadosDTO.add(agendaTemp);
                  
                    
                }
                
                agendaDTO.setTransAgendadas(listPagosProgramadosDTO);
                
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO EN consultarIdCabeceraAgendaPP: ")
                    .append("USR-").append(id)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN consultarIdCabeceraAgendaPP: ")
                    .append("USR-").append(id)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (agendaDTO.getTransAgendadas()== null) {
                agendaDTO.setTransAgendadas(new ArrayList<IBAgendaTransaccionDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            agendaDTO.setRespuestaDTO(respuesta);
        }
        return agendaDTO;
    }

    @Override
    public RespuestaDTO eliminarAgendaProgramada(BigDecimal idAgenda, BigDecimal idUsuario, String nombreCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        
        try { 
                       
            //invocacion del WS
            IBAgendaTransWS_Service service = new IBAgendaTransWS_Service(new URL(urlWsdlAgenda));
            IBAgendaTransWS port = service.getIBAgendaTransWSPort();
            //se obtiene el objeto de salida del WS 
            respuestaWs = port.eliminarAgendaProgramada(idAgenda, idUsuario, nombreCanal,idCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio eliminarAgendaProgramada: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN eliminarAgendaProgramada: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return respuesta;
    }

    }

