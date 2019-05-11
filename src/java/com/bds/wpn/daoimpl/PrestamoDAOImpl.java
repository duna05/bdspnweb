/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.controller.TextosController;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dao.PrestamoDAO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.MovimientoPrestamoDTO;
import com.bds.wpn.dto.PrestamoDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.PrestamoWS;
import com.bds.wpn.ws.services.PrestamoWS_Service;
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
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.log4j.Logger;
/**
 *
 * @author rony.rodriguez
 */
@Named
@Stateless(name = "wpnPrestamoDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class PrestamoDAOImpl extends DAOUtil implements PrestamoDAO {

    private static final Logger logger = Logger.getLogger(PrestamoDAOImpl.class.getName());

    private final String urlWsdlPrestamo = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/PrestamoWS?WSDL";

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;

    /**
     * Obtiene el detalle de un prestamo
     *
     * @param numeroPrestamo String
     * @param idCanal String
     * @param nombreCanal String
     * @return PrestamoDTO
     */
    @Override
    public PrestamoDTO obtenerDetallePrestamo(String numeroPrestamo, String idCanal, String nombreCanal) {
        PrestamoDTO prestamo = new PrestamoDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            PrestamoWS_Service service = new PrestamoWS_Service(new URL(urlWsdlPrestamo));
            PrestamoWS port = service.getPrestamoWSPort();
            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.PrestamoDTO prestamoWs = port.detallePrestamo(numeroPrestamo, nombreCanal);

            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, prestamoWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa

                BeanUtils.copyProperties(prestamo, prestamoWs);

                if (prestamoWs.getFechaLiquidacion() != null) {
                    prestamo.setFechaLiquidacionDate(prestamoWs.getFechaLiquidacion().toGregorianCalendar().getTime());
                }
                if (prestamoWs.getFechaProximoPago() != null) {
                    prestamo.setFechaProximoPagoDate(prestamoWs.getFechaProximoPago().toGregorianCalendar().getTime());
                }

                if (prestamoWs.getFechaVencimiento() != null) {
                    prestamo.setFechaVencimientoDate(prestamoWs.getFechaVencimiento().toGregorianCalendar().getTime());
                }

                //
                if (prestamoWs.getFechaCuotaAnteriorInteres() != null) {
                    prestamo.setFechaCuotaAnteriorInteresDate(prestamoWs.getFechaCuotaAnteriorInteres().toGregorianCalendar().getTime());
                }

                if (prestamoWs.getFechaPagoInteres() != null) {
                    prestamo.setFechaPagoInteresDate(prestamoWs.getFechaPagoInteres().toGregorianCalendar().getTime());
                }

                if (prestamoWs.getFechaPrimerPagoInteres() != null) {
                    prestamo.setFechaPrimerPagoInteresDate(prestamoWs.getFechaPrimerPagoInteres().toGregorianCalendar().getTime());
                }

                if (prestamoWs.getFechaProximoPagoInteres() != null) {
                    prestamo.setFechaProximoPagoDate(prestamoWs.getFechaProximoPagoInteres().toGregorianCalendar().getTime());
                }

            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerDetallePrestamo: ")
                    .append("PR-").append(this.formatoAsteriscosWeb(numeroPrestamo))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerDetallePrestamo: ")
                    .append("PR-").append(this.formatoAsteriscosWeb(numeroPrestamo))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            prestamo.setRespuestaDTO(respuesta);
        }
        return prestamo;
    }

    /**
     * Retorna el listado de prestamo de un cliente consultando su codigo.
     *
     * @param codigoCliente String
     * @param idCanal String
     * @param nombreCanal String
     * @return ClienteDTO
     */
    @Override
    public ClienteDTO listadoPrestamoPorCliente(String codigoCliente, String idCanal, String nombreCanal) {
        ClienteDTO clienteDTO = new ClienteDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            PrestamoWS_Service service = new PrestamoWS_Service(new URL(urlWsdlPrestamo));
            PrestamoWS port = service.getPrestamoWSPort();
            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.ClienteDTO clienteWs = port.listadoPrestamosCliente(codigoCliente, nombreCanal);

            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, clienteWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa

                BeanUtils.copyProperties(clienteDTO, clienteWs);

                PrestamoDTO prestamo = null;

                List<PrestamoDTO> listPrestamos = new ArrayList<>();

                for (com.bds.wpn.ws.services.PrestamoDTO prestamoWs : clienteWs.getPrestamosCliente()) {
                    prestamo = new PrestamoDTO();
                    BeanUtils.copyProperties(prestamo, prestamoWs);

                    if (prestamoWs.getFechaLiquidacion() != null) {
                        prestamo.setFechaLiquidacionDate(prestamoWs.getFechaLiquidacion().toGregorianCalendar().getTime());
                    }
                    if (prestamoWs.getFechaProximoPago() != null) {
                        prestamo.setFechaProximoPagoDate(prestamoWs.getFechaProximoPago().toGregorianCalendar().getTime());
                    }

                    if (prestamoWs.getFechaVencimiento() != null) {
                        prestamo.setFechaVencimientoDate(prestamoWs.getFechaVencimiento().toGregorianCalendar().getTime());
                    }

                    //
                    if (prestamoWs.getFechaCuotaAnteriorInteres() != null) {
                        prestamo.setFechaCuotaAnteriorInteresDate(prestamoWs.getFechaCuotaAnteriorInteres().toGregorianCalendar().getTime());
                    }

                    if (prestamoWs.getFechaPagoInteres() != null) {
                        prestamo.setFechaPagoInteresDate(prestamoWs.getFechaPagoInteres().toGregorianCalendar().getTime());
                    }

                    if (prestamoWs.getFechaPrimerPagoInteres() != null) {
                        prestamo.setFechaPrimerPagoInteresDate(prestamoWs.getFechaPrimerPagoInteres().toGregorianCalendar().getTime());
                    }

                    if (prestamoWs.getFechaProximoPagoInteres() != null) {
                        prestamo.setFechaProximoPagoDate(prestamoWs.getFechaProximoPagoInteres().toGregorianCalendar().getTime());
                    }

                    listPrestamos.add(prestamo);
                }
                clienteDTO.setPrestamosClienteDTO(listPrestamos);

            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoPrestamoPorCliente: ")
                    .append("CC-").append(codigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoPrestamoPorCliente: ")
                    .append("CC-").append(codigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (clienteDTO.getPrestamosClienteDTO() == null) {
                clienteDTO.setPrestamosClienteDTO(new ArrayList<PrestamoDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            clienteDTO.setRespuestaDTO(respuesta);
        }
        return clienteDTO;
    }
    
    /**
     * Retorna el listado de prestamo de un cliente consultando su codigo.
     *
     * @param codigoCliente String
     * @param idCanal String
     * @param nombreCanal String
     * @return ClienteDTO
     */
    @Override
    public ClienteDTO listadoPrestamoPorClienteAP(String codigoCliente, String idCanal, String nombreCanal) {
        ClienteDTO clienteDTO = new ClienteDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            PrestamoWS_Service service = new PrestamoWS_Service(new URL(urlWsdlPrestamo));
            PrestamoWS port = service.getPrestamoWSPort();
            //se obtiene el objeto de salida del WS 
            //com.bds.wpn.ws.services.ClienteDTO clienteWs = port.listadoPrestamosClienteAP(codigoCliente, nombreCanal);
            com.bds.wpn.ws.services.ClienteDTO clienteWs = port.listadoPrestamosClienteAP(codigoCliente, nombreCanal);

            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, clienteWs.getRespuesta());
            //se asigna elobjeto de respuesta para validar el resultado
            clienteDTO.setRespuestaDTO(respuesta);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa

                BeanUtils.copyProperties(clienteDTO, clienteWs);

                PrestamoDTO prestamo = null;

                List<PrestamoDTO> listPrestamos = new ArrayList<>();

                for (com.bds.wpn.ws.services.PrestamoDTO prestamoWs : clienteWs.getPrestamosCliente()) {
                    prestamo = new PrestamoDTO();
                    BeanUtils.copyProperties(prestamo, prestamoWs);

                    if (prestamoWs.getFechaLiquidacion() != null) {
                        prestamo.setFechaLiquidacionDate(prestamoWs.getFechaLiquidacion().toGregorianCalendar().getTime());
                    }
                    if (prestamoWs.getFechaProximoPago() != null) {
                        prestamo.setFechaProximoPagoDate(prestamoWs.getFechaProximoPago().toGregorianCalendar().getTime());
                    }

                    if (prestamoWs.getFechaVencimiento() != null) {
                        prestamo.setFechaVencimientoDate(prestamoWs.getFechaVencimiento().toGregorianCalendar().getTime());
                    }

                    //
                    if (prestamoWs.getFechaCuotaAnteriorInteres() != null) {
                        prestamo.setFechaCuotaAnteriorInteresDate(prestamoWs.getFechaCuotaAnteriorInteres().toGregorianCalendar().getTime());
                    }

                    if (prestamoWs.getFechaPagoInteres() != null) {
                        prestamo.setFechaPagoInteresDate(prestamoWs.getFechaPagoInteres().toGregorianCalendar().getTime());
                    }

                    if (prestamoWs.getFechaPrimerPagoInteres() != null) {
                        prestamo.setFechaPrimerPagoInteresDate(prestamoWs.getFechaPrimerPagoInteres().toGregorianCalendar().getTime());
                    }

                    if (prestamoWs.getFechaProximoPagoInteres() != null) {
                        prestamo.setFechaProximoPagoDate(prestamoWs.getFechaProximoPagoInteres().toGregorianCalendar().getTime());
                    }

                    listPrestamos.add(prestamo);
                }
                clienteDTO.setPrestamosClienteDTO(listPrestamos);

            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoPrestamoPorClienteAP: ")
                    .append("CC-").append(codigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoPrestamoPorClienteAP: ")
                    .append("CC-").append(codigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            this.notificarErrores(e, idCanal, nombreCanal,
                    parametrosController.getNombreParametro("wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("delsur.soporte.email", idCanal),
                    textosController.getNombreTexto("notificacion.error.email.titulo", idCanal), parametrosController.getNombreParametro("delsur.soporte.tlfs", idCanal),
                    textosController.getNombreTexto("motivo.sms.notificacion.error", idCanal));
        }
        return clienteDTO;
    }

    /**
     * retorna los pagos para el prestamo seleccionado
     *
     * @param iNumeroPrestamo String numero de cuenta
     * @param fechaIni String fecha de incio del filtro
     * @param fechaFin String fecha de fin del filtro
     * @param regIni String registro de Inicio para la paginacion
     * @param regFin String registro de fin para la paginacion
     * @param tipoOrdenFecha String tipo de Orden por Fecha: ASC(por defecto),
     * DESC
     * @param nombreCanal
     * @param idCanal
     * @return Prestamo el prestamo con los pagos (solo vienen los datos de los
     * pagos)
     */
    @Override
    public PrestamoDTO listadoPagosPrestamo(String iNumeroPrestamo, String fechaIni, String fechaFin,
            String regIni, String regFin, String tipoOrdenFecha, String nombreCanal, String idCanal) {

        PrestamoDTO prestamo = new PrestamoDTO();
        RespuestaDTO respuesta = new RespuestaDTO();

        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            PrestamoWS_Service service = new PrestamoWS_Service(new URL(urlWsdlPrestamo));
            PrestamoWS port = service.getPrestamoWSPort();
            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.PrestamoDTO prestamoWs = port.listadoPagosPrestamo(iNumeroPrestamo, fechaIni, fechaFin, regIni, regFin, tipoOrdenFecha, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, prestamoWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                BeanUtils.copyProperties(prestamo, prestamoWs);

                MovimientoPrestamoDTO movDTO = null;
                List<MovimientoPrestamoDTO> listMovimientos = null;

                if (prestamoWs.getMovimientos() != null && !prestamoWs.getMovimientos().isEmpty()) {
                    listMovimientos = new ArrayList<>();
                    for (com.bds.wpn.ws.services.MovimientoPrestamoDTO movimientoWs : prestamoWs.getMovimientos()) {
                        movDTO = new MovimientoPrestamoDTO();

                        BeanUtils.copyProperties(movDTO, movimientoWs);

                        if (movimientoWs.getFechaVencimiento() != null) {
                            movDTO.setFechaVencimientoDate(movimientoWs.getFechaVencimiento().toGregorianCalendar().getTime());
                        }
                        if (movimientoWs.getFechaDePago() != null) {
                            movDTO.setFechaDePagoDate(movimientoWs.getFechaDePago().toGregorianCalendar().getTime());
                        }
                        listMovimientos.add(movDTO);
                    }
                    prestamo.setMovimientos(listMovimientos);
                }

            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoPagosPrestamo: ")
                    .append("Prestamo-").append(iNumeroPrestamo)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoPagosPrestamo: ")
                    .append("Prestamo-").append(iNumeroPrestamo)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (prestamo.getMovimientos() == null) {
                prestamo.setMovimientos(new ArrayList<MovimientoPrestamoDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            prestamo.setRespuestaDTO(respuesta);
        }
        return prestamo;
    }

    /**
     * Realiza el pago del prestamo.
     *
     * @param iNumeroPrestamo numero de prestamo
     * @param ivNumeroCuenta numero de cuenta
     * @param inValorTransaccion numero de valor
     * @param ivCodigoUsuario numero de codigo
     * @param ivDescripcionMovimiento numero de descripcion
     * @param codigoCanal
     * @param idCanal codigo de canal
     * 
     * @return PrestamoDTO Contiene los datos para apliacar el pago.
     */
    @Override
    public UtilDTO aplicarPagoPrestamo(String iNumeroPrestamo, String ivNumeroCuenta, String inValorTransaccion, String ivCodigoUsuario, String ivDescripcionMovimiento, String codigoCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        UtilDTO utilDTO = new UtilDTO();

        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            PrestamoWS_Service service = new PrestamoWS_Service(new URL(urlWsdlPrestamo));
            PrestamoWS port = service.getPrestamoWSPort();
            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.UtilDTO utilWs = port.aplicarPagoPrestamo(iNumeroPrestamo, ivNumeroCuenta, inValorTransaccion, ivCodigoUsuario, ivDescripcionMovimiento, codigoCanal);

            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

            //obtenemos el resto de valores del objeto
            //cuando es un mapa debemos traer los valores uno a uno
            Map resultados = new HashMap();
            resultados.put(utilWs.getResulados().getEntry().get(0).getKey(), utilWs.getResulados().getEntry().get(0).getValue());
            utilDTO.setResuladosDTO(resultados);

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio aplicarPagoPrestamo: ")
                    .append("Prestamo-").append(iNumeroPrestamo)
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN aplicarPagoPrestamo: ")
                    .append("Prestamo-").append(iNumeroPrestamo)
                    .append("-CH-").append(codigoCanal)
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

}
