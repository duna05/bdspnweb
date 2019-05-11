/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dao.TarjetaCreditoDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.MovimientoTDCDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.TarjetasCreditoDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.TdcWS;
import com.bds.wpn.ws.services.TdcWS_Service;
import java.math.BigDecimal;
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
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author rony.rodriguez
 */
@Named
@Stateless(name = "wpnTarjetaCreditoDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class TarjetaCreditoDAOImpl extends DAOUtil implements TarjetaCreditoDAO {

    /**
     * Log de sistema
     */
    private static final Logger logger = Logger.getLogger(CuentaDAOImpl.class.getName());

    private final String urlWsdlTarjetaCredito = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/TdcWS?WSDL";

    @EJB
    IbParametrosFacade parametrosFacade;
    
    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    ParametrosController parametrosController;

    /**
     * retorna los movimientos para la TDC seleccionada por parametros
     *
     * @param nroTDC String Numero de Tarjeta a la cual se le van a obtener los
     * movimientos.
     * @param idCanal String id del canal
     * @param nombreCanal String Codigo del canal desde el cual es llamado el
     * procedimiento.
     * @param nroRegistro String Nro maximo de registros que debe contener el listado.
     * @return TarjetasCreditoDTO la TDC con los movimientos encontrados
     */
    @Override
    public TarjetasCreditoDTO listadoMovimientosTDC(String nroTDC, String idCanal, String nombreCanal, String nroRegistro) {

        RespuestaDTO respuesta = new RespuestaDTO();

        com.bds.wpn.ws.services.TarjetasCreditoDTO tarjetaCreditoWS;
        TarjetasCreditoDTO tarjetaCredito = new TarjetasCreditoDTO();
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            TdcWS_Service service = new TdcWS_Service(new URL(urlWsdlTarjetaCredito));
            TdcWS port = service.getTdcWSPort();
            //se obtiene el objeto de salida del WS 
            tarjetaCreditoWS = port.listadoMovimientosTDC(nroTDC, nombreCanal, nroRegistro);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, tarjetaCreditoWS.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                BeanUtils.copyProperties(tarjetaCredito, tarjetaCreditoWS);

                MovimientoTDCDTO movDTO;
                Collection<MovimientoTDCDTO> listMovimientos;

             
                if (tarjetaCreditoWS.getMovimientos() != null && !tarjetaCreditoWS.getMovimientos().isEmpty()) {
                    listMovimientos = new ArrayList<>();
                    for ( com.bds.wpn.ws.services.MovimientoTDCDTO movimientoWs : tarjetaCreditoWS.getMovimientos()) {
                        movDTO = new MovimientoTDCDTO();

                        BeanUtils.copyProperties(movDTO, movimientoWs);

                        if (movimientoWs.getFechaRegistro() != null) {
                            movDTO.setFechaRegistroDate(movimientoWs.getFechaRegistro().toGregorianCalendar().getTime());
                        }                        
                        if (movimientoWs.getFechaOperacion() != null) {
                            movDTO.setFechaOperacionDate(movimientoWs.getFechaOperacion().toGregorianCalendar().getTime());
                        }
                        listMovimientos.add(movDTO);
                    }
                    tarjetaCredito.setMovimientosDTO(listMovimientos);
                }

            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoMovimientosTDC: ")
                    .append("TDC-").append(formatoAsteriscosWeb(nroTDC))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoMovimientosTDC: ")
                    .append("TDC-").append(formatoAsteriscosWeb(nroTDC))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (tarjetaCredito.getMovimientosDTO() == null) {
                tarjetaCredito.setMovimientosDTO(new ArrayList<MovimientoTDCDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            tarjetaCredito.setRespuestaDTO(respuesta);
        }
        return tarjetaCredito;
    }

    /**
     * Retorna el listado de tarjetas de credito de un cliente consultando por
     * numero de cedula.
     *
     * @param iNroCedula String numero de cedula del cliente.
     * @param idCanal String id del canal
     * @param nombreCanal String Nombre del canal desde el cual es llamado el
     * procedimiento.
     * @return ClienteDAO objeto cliente con lista de tcd asociadas al mismo.
     */
    @Override
    public ClienteDTO listadoTdcPorCliente(String iNroCedula, String idCanal, String nombreCanal) {
        ClienteDTO clienteDTO = new ClienteDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            TdcWS_Service service = new TdcWS_Service(new URL(urlWsdlTarjetaCredito));
            TdcWS port = service.getTdcWSPort();
            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.ClienteDTO clienteWs = port.listadoTdcPorCliente(iNroCedula, nombreCanal);

            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, clienteWs.getRespuesta());;
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa

                BeanUtils.copyProperties(clienteDTO, clienteWs);

                TarjetasCreditoDTO tdc;

                List<TarjetasCreditoDTO> listTDC = new ArrayList<>();

                for ( com.bds.wpn.ws.services.TarjetasCreditoDTO tdcWs : clienteWs.getTdcAsociadasCliente()) {
                    tdc = new TarjetasCreditoDTO();
                    BeanUtils.copyProperties(tdc, tdcWs);

                    if (tdcWs.getFechaCliente() != null) {
                        tdc.setFechaClienteDate(tdcWs.getFechaCliente().toGregorianCalendar().getTime());
                    }
                    if (tdcWs.getFechaExpiracion() != null) {
                        tdc.setFechaExpiracionDate(tdcWs.getFechaExpiracion().toGregorianCalendar().getTime());
                    }
                    listTDC.add(tdc);
                }
                clienteDTO.setTdcAsociadasClienteDTO(listTDC);

            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoTdcPorCliente: ")
                    .append("CI-").append(iNroCedula)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoTdcPorCliente: ")
                    .append("CI-").append(iNroCedula)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (clienteDTO.getTdcAsociadasClienteDTO() == null) {
                clienteDTO.setTdcAsociadasClienteDTO(new ArrayList<TarjetasCreditoDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            clienteDTO.setRespuestaDTO(respuesta);
        }
        return clienteDTO;
    }
    
    
    /**
     * Retorna el detalle de una TDC especifica.
     *
     * @param numeroTarjeta String con el numero de tarjeta
     * @param idCanal String identificador del canal
     * @param nombreCanal String Codigo del canal desde el cual es llamado el
     * procedimiento.
     * @return TarjetasCreditoDTO
     */
    @Override
    public TarjetasCreditoDTO obtenerDetalleTDC(String numeroTarjeta, String idCanal, String nombreCanal){
        TarjetasCreditoDTO tdc = new TarjetasCreditoDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS 
            TdcWS_Service service = new TdcWS_Service(new URL(urlWsdlTarjetaCredito));
            TdcWS port = service.getTdcWSPort();
            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.TarjetasCreditoDTO tdcWs = port.obtenerDetalleTDC(numeroTarjeta, nombreCanal);

            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, tdcWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa

                BeanUtils.copyProperties(tdc, tdcWs);
                if (tdcWs.getFechaCorte() != null) {
                        tdc.setFechaCorteDate(tdcWs.getFechaCorte().toGregorianCalendar().getTime());
                }
                if (tdcWs.getFechaLimite() != null) {
                    tdc.setFechaLimiteDate(tdcWs.getFechaLimite().toGregorianCalendar().getTime());
                }
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerDetalleTDC: ")
                    .append("TDC-").append(formatoAsteriscosWeb(numeroTarjeta))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerDetalleTDC: ")
                    .append("TDC-").append(formatoAsteriscosWeb(numeroTarjeta))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            tdc.setRespuestaDTO(respuesta);
        }
        return tdc;
    }

    
    /**
     * Metodo que obtiene el listado de TDC propias por cliente
     * @param nroCedula String Numero de CI del cliente
     * @param idCanal String Id del canal
     * @param nombreCanal String Nombre del canal
     * @return ClienteDTO -> con List< TarjetasCreditoDTO > 
     */
    @Override
    public ClienteDTO listadoTdcPropias(String nroCedula, String idCanal, String nombreCanal){
        ClienteDTO clienteDTO = new ClienteDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            TdcWS_Service service = new TdcWS_Service(new URL(urlWsdlTarjetaCredito));
            TdcWS port = service.getTdcWSPort();
            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.ClienteDTO clienteWs = port.listadoTdcPropias(nroCedula, nombreCanal);

            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, clienteWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa

                BeanUtils.copyProperties(clienteDTO, clienteWs);

                TarjetasCreditoDTO tdc;

                List<TarjetasCreditoDTO> listTDC = new ArrayList<>();

                for ( com.bds.wpn.ws.services.TarjetasCreditoDTO tdcWs : clienteWs.getTdcAsociadasCliente()) {
                    tdc = new TarjetasCreditoDTO();
                    BeanUtils.copyProperties(tdc, tdcWs);

                    if (tdcWs.getFechaCliente() != null) {
                        tdc.setFechaClienteDate(tdcWs.getFechaCliente().toGregorianCalendar().getTime());
                    }
                    if (tdcWs.getFechaExpiracion() != null) {
                        tdc.setFechaExpiracionDate(tdcWs.getFechaExpiracion().toGregorianCalendar().getTime());
                    }
                    listTDC.add(tdc);
                }
                clienteDTO.setTdcAsociadasClienteDTO(listTDC);

            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoTdcPropias: ")
                    .append("CI-").append(nroCedula)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoTdcPropias: ")
                    .append("CI-").append(nroCedula)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (clienteDTO.getTdcAsociadasClienteDTO() == null) {
                clienteDTO.setTdcAsociadasClienteDTO(new ArrayList<TarjetasCreditoDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            clienteDTO.setRespuestaDTO(respuesta);
        }
        return clienteDTO;
    }
/**
     * Metodo que valida que el instrumento pertenece al cliente.
     *
     * @param codigoCliente codigo del cliente Oracle9
     * @param numeroTarjeta numero de tarjeta
     * @param nombreCanal String Nombre del canal
     * @return UtilDTO Retorna los datos de la tarjeta del cliente
     */    
    @Override
    public UtilDTO obtenerClienteTarjeta(String codigoCliente, String numeroTarjeta,String idCanal, String nombreCanal){
    
    UtilDTO utilDTO = new UtilDTO();
    RespuestaDTO respuesta = new RespuestaDTO();
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            TdcWS_Service service = new TdcWS_Service(new URL(urlWsdlTarjetaCredito));
            TdcWS port = service.getTdcWSPort();
            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.UtilDTO clienteWs = port.obtenerClienteTarjeta(codigoCliente, numeroTarjeta, idCanal);

            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, clienteWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa

                BeanUtils.copyProperties(utilDTO, clienteWs);

               }
            
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put("resultado", (clienteWs.getResulados().getEntry().get(0).getValue()));
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                utilDTO.setResuladosDTO(resultados);                
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoTdcPropias: ")
                    .append("CI-").append(numeroTarjeta)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoTdcPropias: ")
                    .append("CI-").append(numeroTarjeta)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            utilDTO.setRespuestaDTO(respuesta);
        }
    
    return utilDTO;
    }
    
    /**
     * retorna los movimientos para la TDC seleccionada por parametros
     *
     * @param nroTDC String Numero de Tarjeta a la cual se le van a obtener los
     * movimientos.
     * @param mes mes a consultar
     * @param anno año a consultar
     * @param idCanal String id del canal
     * @param nombreCanal String Codigo del canal desde el cual es llamado el
     * procedimiento.
     * @return TarjetasCreditoDTO la TDC con los movimientos encontrados
     */
    @Override
    public TarjetasCreditoDTO listadoMovimientosTDCMes(String nroTDC, int mes, int anno, String idCanal, String nombreCanal) {

        RespuestaDTO respuesta = new RespuestaDTO();

        com.bds.wpn.ws.services.TarjetasCreditoDTO tarjetaCreditoWS;
        TarjetasCreditoDTO tarjetaCredito = new TarjetasCreditoDTO();
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            TdcWS_Service service = new TdcWS_Service(new URL(urlWsdlTarjetaCredito));
            TdcWS port = service.getTdcWSPort();
            //se obtiene el objeto de salida del WS 
            tarjetaCreditoWS = port.listadoMovimientosTDCMes(nroTDC, mes, anno, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, tarjetaCreditoWS.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                BeanUtils.copyProperties(tarjetaCredito, tarjetaCreditoWS);

                MovimientoTDCDTO movDTO;
                Collection<MovimientoTDCDTO> listMovimientos;

             
                if (tarjetaCreditoWS.getMovimientos() != null && !tarjetaCreditoWS.getMovimientos().isEmpty()) {
                    listMovimientos = new ArrayList<>();
                    for ( com.bds.wpn.ws.services.MovimientoTDCDTO movimientoWs : tarjetaCreditoWS.getMovimientos()) {
                        movDTO = new MovimientoTDCDTO();

                        BeanUtils.copyProperties(movDTO, movimientoWs);

                        if (movimientoWs.getFechaRegistro() != null) {
                            movDTO.setFechaRegistroDate(movimientoWs.getFechaRegistro().toGregorianCalendar().getTime());
                        }                        
                        if (movimientoWs.getFechaOperacion() != null) {
                            movDTO.setFechaOperacionDate(movimientoWs.getFechaOperacion().toGregorianCalendar().getTime());
                        }
                        listMovimientos.add(movDTO);
                    }
                    tarjetaCredito.setMovimientosDTO(listMovimientos);
                }

            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoMovimientosTDCMes: ")
                    .append("TDC-").append(formatoAsteriscosWeb(nroTDC))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoMovimientosTDCMes: ")
                    .append("TDC-").append(formatoAsteriscosWeb(nroTDC))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (tarjetaCredito.getMovimientosDTO() == null) {
                tarjetaCredito.setMovimientosDTO(new ArrayList<MovimientoTDCDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            tarjetaCredito.setRespuestaDTO(respuesta);
        }
        return tarjetaCredito;
    }
    
    
}
