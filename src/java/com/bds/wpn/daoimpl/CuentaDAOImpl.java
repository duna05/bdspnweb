/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.controller.TextosController;
import com.bds.wpn.dao.CuentaDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.CabeceraEstadoCtaDTO;
import com.bds.wpn.dto.ChequeDTO;
import com.bds.wpn.dto.ChequeraDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.MovimientoCuentaDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.TransaccionesCuentasDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.CuentaWs;
import com.bds.wpn.ws.services.CuentaWs_Service;
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
 * @author cesar.mujica
 */
@Named
@Stateless(name = "wpnCuentaDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class CuentaDAOImpl extends DAOUtil implements CuentaDAO {

    /**
     * Log de sistema
     */
    private static final Logger LOGGER = Logger.getLogger(CuentaDAOImpl.class.getName());

    @EJB
    IbParametrosFacade parametrosFacade;
    
    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlCuenta = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/CuentaWs?wsdl";

    /**
     * retorna los datos basicos de la cuenta mediante la cedula y el canal
     *
     * @param tipoCuenta String tipo de cuenta (Ahorro, Corriente)
     * @param numeroCuenta String numero de cuenta
     * @param idCanal String con el id del canal
     * @param nombreCanal String canal por el cual se realiza la consulta
     * @return CuentaDTO datos de la cuenta en core bancario
     */
    @Override
    public CuentaDTO obtenerDetalleCuenta(String tipoCuenta, String numeroCuenta, String idCanal, String nombreCanal) {
        CuentaDTO cuenta = new CuentaDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.CuentaDTO cuentaWs = port.detalleCuenta(tipoCuenta, numeroCuenta, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, cuentaWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(cuenta, cuentaWs);
            }

        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio obtenerDetalleCuenta: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN obtenerDetalleCuenta: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            this.notificarErrores(e, idCanal, nombreCanal, 
                    parametrosController.getNombreParametro("wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("delsur.soporte.email", idCanal), 
                    textosController.getNombreTexto("notificacion.error.email.titulo", idCanal), parametrosController.getNombreParametro("delsur.soporte.tlfs", idCanal), 
                    textosController.getNombreTexto("motivo.sms.notificacion.error", idCanal));
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            cuenta.setRespuestaDTO(respuesta);
        }
        return cuenta;
    }

    /**
     * retorna los movimientos para la cuenta seleccionada
     *
     * @param tipoCuenta String tipo de cuenta (BCC, BCA, BME) -> ver constantes
     * de tipos producto
     * @param numeroCuenta String numero de cuenta
     * @param fechaIni String fecha de incio del filtro
     * @param fechaFin String fecha de fin del filtro
     * @param regIni String registro de Inicio para la paginacion
     * @param regFin String registro de fin para la paginacion
     * @param tipoOrdenFecha String tipo de Orden por Fecha: ASC(por defecto),
     * DESC
     * @param idCanal String con el id del canal
     * @param nombreCanal String canal por el cual se realiza la consulta
     * @return CuentaDTO la cuenta con los movimientos de la cuenta
     * seleccionada(solo vienen los datos de los movimientos)
     */
    @Override
    public CuentaDTO listarMovimientos(String tipoCuenta, String numeroCuenta, String fechaIni, String fechaFin,
            String regIni, String regFin, String tipoOrdenFecha, String idCanal, String nombreCanal) {
        CuentaDTO cuenta = new CuentaDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.CuentaDTO cuentaWs = port.listadoMovimientosCuenta(tipoCuenta, numeroCuenta, fechaIni, fechaFin, regIni, regFin, tipoOrdenFecha, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, cuentaWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(cuenta, cuentaWs);

                MovimientoCuentaDTO movimientoTemp;
                Collection<MovimientoCuentaDTO> movimientosTemp;

                //obtenemos las cuentas corrientes
                if (cuentaWs.getMovimientos() != null && !cuentaWs.getMovimientos().isEmpty()) {
                    movimientosTemp = new ArrayList<>();
                    for (com.bds.wpn.ws.services.MovimientoCuentaDTO movimientoWS : cuentaWs.getMovimientos()) {
                        movimientoTemp = new MovimientoCuentaDTO();

                        BeanUtils.copyProperties(movimientoTemp, movimientoWS);

                        movimientosTemp.add(movimientoTemp);
                    }
                    cuenta.setMovimientosDTO(movimientosTemp);
                }
            }

        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio listarMovimientos: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN listarMovimientos: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            this.notificarErrores(e, idCanal, nombreCanal, 
                    parametrosController.getNombreParametro("wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("delsur.soporte.email", idCanal), 
                    textosController.getNombreTexto("notificacion.error.email.titulo", idCanal), parametrosController.getNombreParametro("delsur.soporte.tlfs", idCanal), 
                    textosController.getNombreTexto("motivo.sms.notificacion.error", idCanal));
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            cuenta.setRespuestaDTO(respuesta);
        }
        return cuenta;
    }

    /**
     * Metodo que consulta el detalle para el movimiento de una cuenta
     *
     * @param secuenciamovimiento String secuencia del movimiento de la cuenta
     * @param idCanal String con el id del canal
     * @param nombreCanal String Nombre del canal
     * @return MovimientoCuentaDTO el movimiento con la informacion detallada
     */
    @Override
    public MovimientoCuentaDTO detalleMovimiento(String secuenciamovimiento, String idCanal, String nombreCanal) {
        MovimientoCuentaDTO movimientoCuentaDTO = new MovimientoCuentaDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.MovimientoCuentaDTO movimientoWs = port.detalleMovimientoCuenta(secuenciamovimiento, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, movimientoWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(movimientoCuentaDTO, movimientoWs);
                
            }

        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio detalleMovimiento: ")
                    .append("SECEXTMOV-").append(secuenciamovimiento)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN detalleMovimiento: ")
                    .append("SECEXTMOV-").append(secuenciamovimiento)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            this.notificarErrores(e, idCanal, nombreCanal, 
                    parametrosController.getNombreParametro("wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("delsur.soporte.email", idCanal), 
                    textosController.getNombreTexto("notificacion.error.email.titulo", idCanal), parametrosController.getNombreParametro("delsur.soporte.tlfs", idCanal), 
                    textosController.getNombreTexto("motivo.sms.notificacion.error", idCanal));
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            movimientoCuentaDTO.setRespuestaDTO(respuesta);
        }
        return movimientoCuentaDTO;
    }
    
     /**
     * Obtiene los saldos bloqueados de una cuenta
     * @param tipoCuenta tipo de cuenta BCA o BCC
     * @param numeroCuenta numero de la cuenta
     * @param codigoCanal codigo del canal
     * @param idCanal
     * @return listado de Saldos Bloqueados de una Cuenta
     */
    @Override
    public CuentaDTO listadoSaldoBloqueadoCuenta(String tipoCuenta, String numeroCuenta, String codigoCanal, String idCanal) {
        CuentaDTO cuenta = new CuentaDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        BigDecimal totalBloqueado = new BigDecimal(0);
        
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.CuentaDTO cuentaWs = port.listadoSaldoBloqueadoCuenta(tipoCuenta, numeroCuenta, codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, cuentaWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(cuenta, cuentaWs);

                MovimientoCuentaDTO movimientoTemp;
                Collection<MovimientoCuentaDTO> movimientosTemp;

                //obtenemos las cuentas corrientes
                if (cuentaWs.getListSaldoBloqueado() != null && !cuentaWs.getListSaldoBloqueado().isEmpty()) {
                    movimientosTemp = new ArrayList<>();
                    for (com.bds.wpn.ws.services.MovimientoCuentaDTO movimientoWS : cuentaWs.getListSaldoBloqueado()) {
                        movimientoTemp = new MovimientoCuentaDTO();

                        BeanUtils.copyProperties(movimientoTemp, movimientoWS);
                        
                        if (movimientoWS.getMonto() != null){
                            //se calcula el saldo bloqueado total
                            totalBloqueado = totalBloqueado.add(movimientoWS.getMonto());
                        }
                        
                        movimientosTemp.add(movimientoTemp);
                    }
                    
                    cuenta.setSaldoDiferido(totalBloqueado);
                    cuenta.setListSaldoBloqueado(movimientosTemp);
                }
            }

        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio listadoSaldoBloqueadoCuenta: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN listadoSaldoBloqueadoCuenta: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            this.notificarErrores(e, idCanal, codigoCanal, 
                    parametrosController.getNombreParametro("wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("delsur.soporte.email", idCanal), 
                    textosController.getNombreTexto("notificacion.error.email.titulo", idCanal), parametrosController.getNombreParametro("delsur.soporte.tlfs", idCanal), 
                    textosController.getNombreTexto("motivo.sms.notificacion.error", idCanal));
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            cuenta.setRespuestaDTO(respuesta);
        }
        return cuenta;
    }
    
    /**
     * Obtiene los saldos diferidos de una cuenta
     * @param tipoCuenta tipo de cuenta BCA o BCC
     * @param numeroCuenta numero de la cuenta
     * @param codigoCanal codigo del canal
     * @param idCanal
     * @return listado de Saldos diferidos de una Cuenta
     */
    @Override
    public CuentaDTO listadoSaldoDiferidoCuenta(String tipoCuenta, String numeroCuenta, String codigoCanal, String idCanal) {
        CuentaDTO cuenta = new CuentaDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        BigDecimal totalDiferido = new BigDecimal(0);
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.CuentaDTO cuentaWs = port.listadoSaldoDiferidoCuenta(tipoCuenta, numeroCuenta, codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, cuentaWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(cuenta, cuentaWs);

                MovimientoCuentaDTO movimientoTemp;
                Collection<MovimientoCuentaDTO> movimientosTemp;

                //obtenemos las cuentas corrientes
                if (cuentaWs.getListSaldoDiferido()!= null && !cuentaWs.getListSaldoDiferido().isEmpty()) {
                    movimientosTemp = new ArrayList<>();
                    for (com.bds.wpn.ws.services.MovimientoCuentaDTO movimientoWS : cuentaWs.getListSaldoDiferido()) {
                        movimientoTemp = new MovimientoCuentaDTO();

                        BeanUtils.copyProperties(movimientoTemp, movimientoWS);
                        
                        if (movimientoWS.getMonto() != null){
                            //se calcula el saldo diferido total
                            totalDiferido = totalDiferido.add(movimientoWS.getMonto());
                        }
                        
                        movimientosTemp.add(movimientoTemp);
                    }
                    
                    
                    cuenta.setSaldoDiferido(totalDiferido);
                    cuenta.setListSaldoDiferido(movimientosTemp);
                }
            }

        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio listadoSaldoDiferidoCuenta: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN listadoSaldoDiferidoCuenta: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            this.notificarErrores(e, idCanal, codigoCanal, 
                    parametrosController.getNombreParametro("wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("delsur.soporte.email", idCanal), 
                    textosController.getNombreTexto("notificacion.error.email.titulo", idCanal), parametrosController.getNombreParametro("delsur.soporte.tlfs", idCanal), 
                    textosController.getNombreTexto("motivo.sms.notificacion.error", idCanal));
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            cuenta.setRespuestaDTO(respuesta);
        }
        return cuenta;
    }
    
   /**
     * Realiza la solicitud de chequeras de una cuenta corriente.
     *
     * @param numeroCuenta numero de la cuenta
     * @param tipoChequera tipo de chequera seleccionada por cantidad de cheques
     * @param cantidad cantidad de chequeras solicitadas
     * @param codigoCanal codigo del canal
     * @param retira nombre de la persona que retira las chequeras puede ser null para el titular
     * @param identificacion nro de cedula de la persona que retira las chequeras puede ser null para el titular
     * @param agenciaRetira codigo de agencia en la cual se retiran las chequeras
     * @param idCanal identificador del canal 
     * @return ChequeraDTO Listado de chequeras por cliente
     */
    @Override
    public UtilDTO solicitarChequeras(String numeroCuenta, String tipoChequera, String cantidad, String identificacion, String retira, String agenciaRetira, String codigoCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            utilWs = port.solicitarChequeras(numeroCuenta, tipoChequera, cantidad, identificacion, retira, agenciaRetira, codigoCanal) ;
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
    
    
    
    /**
     * Obtiene las chequeras entregadas de una cuenta.
     *
     * @param numeroCuenta numero de cuenta
     * @param codigoCanal codigo del canal
     * @param idCanal identificador del canal 
     * @return CuentaDTO Listado de las chequeras entregadas
     */
    @Override
    public CuentaDTO listarChequerasEntregadas(String numeroCuenta, String codigoCanal, String idCanal) {
        CuentaDTO cuenta = new CuentaDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.CuentaDTO cuentaWs = port.listarChequerasEntregadas(numeroCuenta, codigoCanal);
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

                        BeanUtils.copyProperties(chequera, chequeraWS); 
                        if(chequeraWS.getFechaEmision() != null){
                            chequera.setFechaEmisionDate(chequeraWS.getFechaEmision().toGregorianCalendar().getTime());
                        }
                        if(chequeraWS.getFechaEntrega() != null){
                            chequera.setFechaEntregaDate(chequeraWS.getFechaEntrega().toGregorianCalendar().getTime());
                        }
                        chequeras.add(chequera);
                    }
                    cuenta.setChequerasDTO(chequeras);
                }
            }

        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio listarChequerasEntregadas: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN listarChequerasEntregadas: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            cuenta.setRespuestaDTO(respuesta);
        }
        return cuenta;
    
    }
    
    /**
     * Obtiene el listado de cheques activos de una chequera
     *
     * @param numeroCuenta numero de cuenta
     * @param numeroPrimerCheque numero del primer cheque de una chequera
     * @param codigoCanal codigo del canal
     * @param idCanal identificador del canal 
     * @return CuentaDTO Listado de las chequeras entregadas
     */
    @Override
    public ChequeraDTO listarChequesPorChequeraAct(String numeroCuenta,String numeroPrimerCheque, String codigoCanal, String idCanal) {
        ChequeraDTO chequera = new ChequeraDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.ChequeraDTO chequeraWs = port.listarCheques(numeroCuenta, numeroPrimerCheque, codigoCanal);
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
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio listarChequesPorChequeraAct: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN listarChequesPorChequeraAct: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            chequera.setRespuestaDTO(respuesta);
        }
        return chequera;
    
    }
    
    /**
     * Realiza la suspension de un cheque o varios cheque de una cuenta
     * corriente.
     *
     * @param numeroCuenta numero de la cuenta
     * @param motivoSuspension motivo de la suspension
     * @param numPrimerCheque numero del primer cheque
     * @param numUltimoCheque numero del ultimo cheque
     * @param listCheques lista de cheques a ser suspendidos
     * @param nombreCanal nombre del canal
     * @param idCanal
     * @return Número de cheques que se lograron suspender, Numero de referencia
     * de la suspensión, Código de estatus de la operación indicando si la
     * solicitud fue realizada correctamente.
     */
    @Override
    public UtilDTO suspenderChequeras(String numeroCuenta, String motivoSuspension, String numPrimerCheque, String numUltimoCheque, String listCheques, String nombreCanal, String idCanal) {
        UtilDTO util = new UtilDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        motivoSuspension = "6";// indica que el motivo de suspensión es "OTRO"

        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort(); 
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.UtilDTO chequeraWs = port.suspenderChequeras(numeroCuenta, motivoSuspension, numPrimerCheque, numUltimoCheque, listCheques, nombreCanal);
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

    /**
     * 
     * @param tipoCuenta
     * @param numeroCuenta
     * @param fechaIni
     * @param fechaFin
     * @param codigoCanal
     * @param idCanal
     * @return 
     */
    @Override
    public CuentaDTO estadoCuenta(String tipoCuenta, String numeroCuenta, String fechaIni, String fechaFin, String codigoCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.CuentaDTO cuentaWs;
        
        CuentaDTO cuenta = new CuentaDTO();
        CabeceraEstadoCtaDTO cabecera = new CabeceraEstadoCtaDTO();
                
           try { 
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            cuentaWs = port.estadoCuenta(tipoCuenta, numeroCuenta, fechaIni, fechaFin, codigoCanal);
            BeanUtils.copyProperties(respuesta, cuentaWs.getRespuesta());
           
            
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
             BeanUtils.copyProperties(cabecera, cuentaWs.getCabecera()); 
             cuenta.setCabecera(cabecera);
            }
            
            
            
        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio estadoCuenta: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN estadoCuenta: ")
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

    /**
     * 
     * @param tipoCuenta
     * @param nombreCanal
     * @param idCanal
     * @return 
     */
    @Override
    public TransaccionesCuentasDTO listadoTransaccionesCuentas(String tipoCuenta, String nombreCanal, String idCanal) {
         TransaccionesCuentasDTO transaccion = new TransaccionesCuentasDTO();
        List<TransaccionesCuentasDTO> listTransacciones = new ArrayList<>();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.TransaccionesCuentasDTO transaccionWs = port.listadoTransaccionesCuentas(tipoCuenta, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, transaccionWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {

                for (com.bds.wpn.ws.services.TransaccionesCuentasDTO rec : transaccionWs.getListadoTransacciones()) {
                    TransaccionesCuentasDTO transaccionTemp = new TransaccionesCuentasDTO();

                    //si es positivo procedemos a obtener la data completa
                    BeanUtils.copyProperties(transaccionTemp, rec);
                    
                    listTransacciones.add(transaccionTemp);
                }                
                transaccion.setListadoTransacciones(listTransacciones);
            }

        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio listadoTransaccionesCuentas: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN listadoTransaccionesCuentas: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (transaccion.getListadoTransacciones() == null) {
                transaccion.setListadoTransacciones(new ArrayList<TransaccionesCuentasDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            transaccion.setRespuestaDTO(respuesta);
        }
        return transaccion;
    }


    /**
     * 
     * @param tipoCuenta
     * @param numeroCuenta
     * @param fechaIni
     * @param fechaFin
     * @param regIni
     * @param regFin
     * @param tipoOrdenFecha
     * @param codigoCanal
     * @param nombreCanal
     * @param codTransaccion
     * @return 
     */
    @Override
    public CuentaDTO listarMovimientosPorTransaccion(String tipoCuenta, String numeroCuenta, String fechaIni, String fechaFin, String regIni, String regFin, String tipoOrdenFecha,String codigoCanal, String nombreCanal, String codTransaccion, String idCanal) {
         RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.CuentaDTO cuentaWs;
        
        CuentaDTO cuenta = new CuentaDTO();
        
        try { 
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            cuentaWs = port.listarMovimientosPorTransaccion(tipoCuenta, numeroCuenta, fechaIni, fechaFin, regIni, regFin, tipoOrdenFecha, nombreCanal, codTransaccion);
            BeanUtils.copyProperties(respuesta, cuentaWs.getRespuesta());
           
            
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                 BeanUtils.copyProperties(cuenta, cuentaWs);

                 MovimientoCuentaDTO movimientoTemp = null;
                 Collection<MovimientoCuentaDTO> movimientosTemp = null;

                //obtenemos las cuentas 
                if (cuentaWs.getMovimientos() != null && !cuentaWs.getMovimientos().isEmpty()) {
                    movimientosTemp = new ArrayList<>();
                    for (com.bds.wpn.ws.services.MovimientoCuentaDTO movimientoWS : cuentaWs.getMovimientos()) {
                        movimientoTemp = new MovimientoCuentaDTO();

                        BeanUtils.copyProperties(movimientoTemp, movimientoWS);

                        movimientosTemp.add(movimientoTemp);
                    }
                    cuenta.setMovimientosDTO(movimientosTemp);
                }
            }
            
            
            
        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio listarMovimientosPorTransaccion: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN listarMovimientosPorTransaccion: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(nombreCanal)
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
    
    
    
    /**
     * Método retorna el saldo disponible de una cta
     *
     * @param nroCuenta numero de cta de 20 digitos
     * @param idCanal
     * @param codigoCanal
     *
     * @return UtilDTO
     */
    @Override
    public UtilDTO obtenerSaldoDisponibleCta(String nroCuenta, String idCanal, String codigoCanal) {
        UtilDTO util = new UtilDTO();
        RespuestaDTO respuesta = new RespuestaDTO();

        try {
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();  
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.UtilDTO infoCtaWs = port.obtenerSaldoDisponibleCta(nroCuenta, idCanal, codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, infoCtaWs.getRespuesta());
            //validacion de codigo de respuesta            
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {

                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();  
                if(infoCtaWs.getResulados() != null){
                    for(int i = 0; i < infoCtaWs.getResulados().getEntry().size(); i++){
                        resultados.put(infoCtaWs.getResulados().getEntry().get(i).getKey(), (infoCtaWs.getResulados().getEntry().get(i).getValue()));                    
                    } 
                }                      
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio obtenerSaldoDisponibleCta: ")
                    .append("CTA-").append(formatoAsteriscosWeb(nroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN obtenerSaldoDisponibleCta: ")
                    .append("CTA-").append(formatoAsteriscosWeb(nroCuenta))
                    .append("-CH-").append(codigoCanal)
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
     * Obtiene el listado de cheques utilizados de una chequera
     *
     * @param numeroCuenta numero de cuenta
     * @param numeroPrimerCheque numero del primer cheque de una chequera
     * @param codigoCanal codigo del canal
     * @param idCanal identificador del canal 
     * @return CuentaDTO Listado de las chequeras entregadas
     */
    @Override
    public ChequeraDTO listarChequesPorChequera(String numeroCuenta,String numeroPrimerCheque, String codigoCanal, String idCanal) {
        ChequeraDTO chequera = new ChequeraDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.ChequeraDTO chequeraWs = port.listarChequesChequera(numeroCuenta, numeroPrimerCheque, codigoCanal);
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
            LOGGER.error( new StringBuilder("ERROR DAO al consumir el servicio listarChequesPorChequera: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            LOGGER.error( new StringBuilder("ERROR DAO EN listarChequesPorChequera: ")
                    .append("CTA-").append(formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            chequera.setRespuestaDTO(respuesta);
        }
        return chequera;
    
    }
}
