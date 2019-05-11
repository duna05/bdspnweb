/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dao.NotificationServiceType.ServiceType;
import com.bds.wpn.dao.ReferenciaDAO;
import com.bds.wpn.dto.ReferenciaDTO;
import com.bds.wpn.dto.ReferenciaDetalleDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.ReferenciaWS;
import com.bds.wpn.ws.services.ReferenciaWS_Service;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.log4j.Logger;

/**
 * Clase que implementa la interfaz ReferenciaDAO
 *
 * @author juan.faneite
 */
@Named
@Stateless(name = "wpnReferenciaDAO")
@NotificationServiceType(ServiceType.SERVICES)
public class ReferenciaDAOImpl extends DAOUtil implements ReferenciaDAO {

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(ReferenciaDAOImpl.class.getName());
    
    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlReferencia = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/ReferenciaWS?WSDL";
    
    /**
     * Obtiene los datos de las referencias bancarias.
     *
     * @param codigoCliente codigo del cliente
     * @param tipoRef tipo de referencia
     * @param nroCuenta numero de cuenta
     * @param destinatario
     * @param nombreCanal codigo de canal
     * @return ReferenciaDTO Datos de la referencia
     */
    @Override
    public ReferenciaDTO obtenerDatosReferencias(String codigoCliente, String tipoRef, String nroCuenta, String destinatario, String nombreCanal) {
        
        RespuestaDTO respuesta = new RespuestaDTO();
        ReferenciaDTO referenciaDTO = new ReferenciaDTO();

        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            ReferenciaWS_Service service = new ReferenciaWS_Service(new URL(urlWsdlReferencia));
            ReferenciaWS port = service.getReferenciaWSPort();
            com.bds.wpn.ws.services.ReferenciaDTO referenciaWs = port.obtenerDatosReferencias(codigoCliente, tipoRef, nroCuenta, destinatario, nombreCanal);
            BeanUtils.copyProperties(respuesta, referenciaWs.getRespuesta());

            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {

                List<ReferenciaDetalleDTO> listRefDetalleDto = new ArrayList<>();
                for (com.bds.wpn.ws.services.ReferenciaDetalleDTO ref : referenciaWs.getReferenciasCuentas()) {

                    ReferenciaDetalleDTO refTemp = new ReferenciaDetalleDTO();
                    BeanUtils.copyProperties(refTemp, ref);
                    
                    if (ref.getFechaInicio() != null) {
                        refTemp.setFechaInicioDate(ref.getFechaInicio().toGregorianCalendar().getTime());
                    }
                    
                    if (ref.getFirma() != null) {
                        refTemp.setFirmaBlob(ref.getFirma());
                    }
                    
                    listRefDetalleDto.add(refTemp);
                }
                referenciaDTO.setReferenciasCuentas(listRefDetalleDto);
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerDatosReferencias: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerDatosReferencias: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            referenciaDTO.setRespuestaDTO(respuesta);
        }

        return referenciaDTO;
    }

    /**
     * Obtiene las referencias de una TDC
     *
     * @param codigoCliente codigo del cliente
     * @param nroTarjeta
     * @param destinatario destinatario a quien va dirigida la tdc
     * @param nombreCanal codigo del canal
     * @return ReferenciaDTO retorna los datos de la refeneia
     */
    @Override
    public ReferenciaDTO obtenerReferenciaTDC(String codigoCliente, String nroTarjeta, String destinatario, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        ReferenciaDTO referenciaDTO = new ReferenciaDTO();

        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            ReferenciaWS_Service service = new ReferenciaWS_Service(new URL(urlWsdlReferencia));
            ReferenciaWS port = service.getReferenciaWSPort();
            com.bds.wpn.ws.services.ReferenciaDTO referenciaWs = port.obtenerReferenciaTDC(codigoCliente, nroTarjeta, destinatario, nombreCanal);
            BeanUtils.copyProperties(respuesta, referenciaWs.getRespuesta());

            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {

                List<ReferenciaDetalleDTO> listRefDetalleDto = new ArrayList<>();
                for (com.bds.wpn.ws.services.ReferenciaDetalleDTO ref : referenciaWs.getReferenciasCuentas()) {

                    ReferenciaDetalleDTO refTemp = new ReferenciaDetalleDTO();
                    BeanUtils.copyProperties(refTemp, ref);
                    
                    if (ref.getFechaInicio() != null) {
                        refTemp.setFechaInicioDate(ref.getFechaInicio().toGregorianCalendar().getTime());
                    }
                    
                    if (ref.getFirma() != null) {
                        //refTemp.setFirmaBlob(ref.getFirma().toString().getBytes());
                        refTemp.setFirmaBlob(ref.getFirma());
                    }
                     
                    listRefDetalleDto.add(refTemp);
                }
                referenciaDTO.setReferenciasCuentas(listRefDetalleDto);
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerReferenciaTDC: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());            
        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerReferenciaTDC: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            referenciaDTO.setRespuestaDTO(respuesta);
        }
        return referenciaDTO;
    }

}
