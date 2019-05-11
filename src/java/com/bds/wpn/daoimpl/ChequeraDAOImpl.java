/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.dao.ChequeraDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.ChequeDTO;
import com.bds.wpn.dto.ChequeraDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.EstadoSolicitudChequeraDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.ChequeraWs;
import com.bds.wpn.ws.services.ChequeraWs_Service;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author robinson.rodriguez
 */
@Named
@Stateless(name = "wpnChequeraDAO")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class ChequeraDAOImpl extends DAOUtil implements ChequeraDAO {

    /**
     * Log de sistema
     */
    private static final Logger LOGGER = Logger.getLogger(CuentaDAOImpl.class.getName());

    private final String URL_WSDL = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/ChequeraWs?wsdl";

    @Override
    public EstadoSolicitudChequeraDTO obtenerEstadoSolicitudChequera(String nroCuenta, String iCodExtCanal) {
        com.bds.wpn.ws.services.EstadoSolicitudChequeraDTO estadoSolicitudChequeraDTOServ;
        com.bds.wpn.dto.EstadoSolicitudChequeraDTO estadoSolicitudChequeraDTO = new EstadoSolicitudChequeraDTO();
        List<com.bds.wpn.dto.EstadoSolicitudChequeraDTO> estadoSolicitudChequeraDTOList = new ArrayList<>();

        try {

            //Invocacion del ws
            ChequeraWs_Service service = new ChequeraWs_Service(new URL(URL_WSDL));
            ChequeraWs port = service.getChequeraWsPort();

            estadoSolicitudChequeraDTOServ = port.obtenerEstadoSolicitudChequera(nroCuenta, iCodExtCanal);

            //Seteo de los valores al DTO
            for (com.bds.wpn.ws.services.EstadoSolicitudChequeraDTO aux : estadoSolicitudChequeraDTOServ.getEstadoSolicitudChequeraDTOList()) {
                estadoSolicitudChequeraDTO.setFechaSolicitud(aux.getFechaSolicitud());
                estadoSolicitudChequeraDTO.setTipoChequera(aux.getTipoChequera());
                estadoSolicitudChequeraDTO.setAgenciaRetiro(aux.getAgenciaRetiro());
                estadoSolicitudChequeraDTO.setEstadoSolicitud(aux.getEstadoSolicitud());
                estadoSolicitudChequeraDTOList.add(estadoSolicitudChequeraDTO);
            }

            estadoSolicitudChequeraDTO.setEstadoSolicitudChequeraDTOList(estadoSolicitudChequeraDTOList);

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN obtenerEstadoSolicitudChequera: ")
                    .append("-CH-").append(iCodExtCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        }
        return estadoSolicitudChequeraDTO;

    }

    @Override
    public CuentaDTO listarChequeraEntregada(String numeroCuenta, String codigoCanal) {
        CuentaDTO cuenta = new CuentaDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            ChequeraWs_Service service = new ChequeraWs_Service(new URL(URL_WSDL));
            ChequeraWs port = service.getChequeraWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.CuentaDTO cuentaWs = port.listarChequeraEntregada(numeroCuenta, codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, cuentaWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(cuenta, cuentaWs);

                ChequeraDTO chequera;
                Collection<ChequeraDTO> chequeras;

                //obtenemos las cuentas corrientes
                if (cuentaWs.getChequeras() != null && !cuentaWs.getChequeras().isEmpty()) {
                    chequeras = new ArrayList<>();
                    for (com.bds.wpn.ws.services.ChequeraDTO chequeraWS : cuentaWs.getChequeras()) {
                        chequera = new ChequeraDTO();

                        chequera.setAgenciaOrigen(chequeraWS.getAgenciaOrigen());
                        chequera.setCantidadCheques(chequeraWS.getCantidadCheques());
                        chequera.setNumeroPrimerCheque(chequeraWS.getNumeroPrimerCheque());
                        chequera.setNumeroUltimoCheque(chequeraWS.getNumeroUltimoCheque());

                        if (chequeraWS.getFechaEmision() != null) {
                            chequera.setFechaEmisionDate(chequeraWS.getFechaEmision().toGregorianCalendar().getTime());
                        } else {
                            chequera.setFechaEmisionDate(new Date());
                        }

                        if (chequeraWS.getFechaEntrega() != null) {
                            chequera.setFechaEntregaDate(chequeraWS.getFechaEntrega().toGregorianCalendar().getTime());
                        }
                        chequeras.add(chequera);
                    }
                    cuenta.setChequerasDTO(chequeras);
                }
            }

        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio listarChequeraEntregada: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN listarChequeraEntregada: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            cuenta.setRespuestaDTO(respuesta);
        }
        return cuenta;

    }

    @Override
    public ChequeraDTO listarChequePorChequeraAct(String numeroCuenta, String numeroPrimerCheque, String codigoCanal) {
        ChequeraDTO chequera = new ChequeraDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            ChequeraWs_Service service = new ChequeraWs_Service(new URL(URL_WSDL));
            ChequeraWs port = service.getChequeraWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.ChequeraDTO chequeraWs = port.listarCheque(numeroCuenta, numeroPrimerCheque, codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, chequeraWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {

                ChequeDTO cheque;
                Collection<ChequeDTO> cheques;

                //obtenemos las cuentas corrientes
                if (chequeraWs.getListCheque() != null && !chequeraWs.getListCheque().isEmpty()) {
                    cheques = new ArrayList<>();
                    for (com.bds.wpn.ws.services.ChequeDTO chequeWS : chequeraWs.getListCheque()) {
                        cheque = new ChequeDTO();

                        BeanUtils.copyProperties(cheque, chequeWS);
                        cheques.add(cheque);
                    }
                    chequera.setChequesDTO(cheques);
                }
            }

        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio listarChequePorChequeraAct: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN listarChequePorChequeraAct: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);

        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            chequera.setRespuestaDTO(respuesta);
        }
        return chequera;

    }

    @Override
    public UtilDTO solicitarChequeraPN(String numeroCuenta, String tipoChequera, String cantidad, String identificacion, String retira, String agenciaRetira, String codigoCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            ChequeraWs_Service service = new ChequeraWs_Service(new URL(URL_WSDL));
            ChequeraWs port = service.getChequeraWsPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            utilWs = port.solicitarChequeraPN(numeroCuenta, tipoChequera, cantidad, identificacion, retira, agenciaRetira, codigoCanal) ;
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put(utilWs.getResulados().getEntry().get(0).getKey(), (utilWs.getResulados().getEntry().get(0).getValue()));
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio solicitarChequeras: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN solicitarChequeras: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            util.setRespuestaDTO(respuesta);
        }
        return util;

    }

    @Override
    public UtilDTO suspenderChequera(String numeroCuenta, String motivoSuspension, String numPrimerCheque, String numUltimoCheque, String listCheques, String nombreCanal, String idCanal) {
        UtilDTO util = new UtilDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        motivoSuspension = "6";// indica que el motivo de suspensión es "OTRO"

        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            ChequeraWs_Service service = new ChequeraWs_Service(new URL(URL_WSDL));
            ChequeraWs port = service.getChequeraWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.UtilDTO chequeraWs = port.suspenderChequera(numeroCuenta, motivoSuspension, numPrimerCheque, numUltimoCheque, listCheques, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, chequeraWs.getRespuesta());
            //validacion de codigo de respuesta            
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {

                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();  
                if(chequeraWs.getResulados() != null){
                    for(int i = 0; i < chequeraWs.getResulados().getEntry().size(); i++){
                        resultados.put(chequeraWs.getResulados().getEntry().get(i).getKey(), (chequeraWs.getResulados().getEntry().get(i).getValue()));                    
                    } 
                }                      
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio suspenderChequeras: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN suspenderChequeras: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
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
