/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.controller.TextosController;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dao.ReclamosDAO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.MovimientoCuentaDTO;
import com.bds.wpn.dto.ReclamoDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.ReclamosWS;
import com.bds.wpn.ws.services.ReclamosWS_Service;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
 *
 * @author juan.faneite
 */
@Named
@Stateless(name = "wpnReclamosDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class ReclamosDAOImpl extends DAOUtil implements ReclamosDAO {

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(ReclamosDAOImpl.class.getName());

    private final String urlWsdlReclamos = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/ReclamosWS?WSDL";

    /**
     * Obtiene el listado de reclamos
     *
     * @param idCanal id del canal
     * @param codigoCanal codigo del canal
     * @return ClienteDTO  obtiene el listado de reclamos
     */
    @Override
    public ReclamoDTO obtenerListadoReclamos(String idCanal, String codigoCanal) {
        ReclamoDTO reclamo = new ReclamoDTO();
        List<ReclamoDTO> listReclamo = new ArrayList<>();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            ReclamosWS_Service service = new ReclamosWS_Service(new URL(urlWsdlReclamos));
            ReclamosWS port = service.getReclamosWSPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.ReclamoDTO reclamoWs = port.obtenerListadoReclamosTDD(codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, reclamoWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {

                for (com.bds.wpn.ws.services.ReclamoDTO rec : reclamoWs.getReclamos()) {
                    ReclamoDTO reclamoTemp = new ReclamoDTO();

                    //si es positivo procedemos a obtener la data completa
                    BeanUtils.copyProperties(reclamoTemp, rec);
                    
                    listReclamo.add(reclamoTemp);
                }                
                reclamo.setReclamos(listReclamo);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerListadoReclamos: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerListadoReclamos: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (reclamo.getReclamos() == null) {
                reclamo.setReclamos(new ArrayList<ReclamoDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            reclamo.setRespuestaDTO(respuesta);
        }
        return reclamo;
    }

    @Override
    public ReclamoDTO insertarReclamoCliente(String codigoCliente, String codigoReclamo, String secuenciaExtMovimiento, String observacion, String codigoCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        ReclamoDTO reclamos = new ReclamoDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        com.bds.wpn.ws.services.ReclamoDTO reclamoWs;
        try {
            //invocacion del WS
            ReclamosWS_Service service = new ReclamosWS_Service(new URL(urlWsdlReclamos));
            ReclamosWS port = service.getReclamosWSPort();
            //se obtiene el objeto de salida del WS 
            reclamoWs = port.insertarReclamoClienteTDD(codigoCliente, codigoReclamo, secuenciaExtMovimiento, observacion, codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, reclamoWs.getRespuesta());
            BeanUtils.copyProperties(reclamos, reclamoWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio insertarReclamoCliente: ")
                    .append("CODUSR-").append(codigoCliente)
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN insertarReclamoCliente: ")
                    .append("CODUSR-").append(codigoCliente)
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        } finally{
    reclamos.setRespuestaDTO(respuesta);
        }
        return reclamos;
    }
    
    @Override
    public ReclamoDTO listadoReclamosPorCliente(String codigoCliente, String idCanal, String codigoCanal) {

        ReclamoDTO reclamo = new ReclamoDTO();
        List<ReclamoDTO> listReclamo = new ArrayList<>();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            ReclamosWS_Service service = new ReclamosWS_Service(new URL(urlWsdlReclamos));
            ReclamosWS port = service.getReclamosWSPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.ReclamoDTO reclamoWs = port.listadoReclamosPorCliente(codigoCliente, codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, reclamoWs.getRespuesta()); 
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {

                for (com.bds.wpn.ws.services.ReclamoDTO rec : reclamoWs.getReclamos()) {
                    ReclamoDTO reclamoTemp = new ReclamoDTO();

                    //si es positivo procedemos a obtener la data completa
                    BeanUtils.copyProperties(reclamoTemp, rec);

                    if (rec.getFechaSolicitud() != null) {
                        reclamoTemp.setFechaSolicitudDate(rec.getFechaSolicitud().toGregorianCalendar().getTime());
                    }

                    if (rec.getFechaSolucion() != null) {
                        reclamoTemp.setFechaSolucionDate(rec.getFechaSolucion().toGregorianCalendar().getTime());
                    }

                    listReclamo.add(reclamoTemp);
                }

                reclamo.setReclamos(listReclamo);

            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoReclamosPorCliente: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoReclamosPorCliente: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }  finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (reclamo.getReclamos() == null) {
                reclamo.setReclamos(new ArrayList<ReclamoDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            reclamo.setRespuestaDTO(respuesta);
        }
        return reclamo;
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
            ReclamosWS_Service service = new ReclamosWS_Service(new URL(urlWsdlReclamos));
            ReclamosWS port = service.getReclamosWSPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.CuentaDTO reclamoWs = port.listadoMovimientosCuenta(tipoCuenta, numeroCuenta, fechaIni, fechaFin, regIni, regFin, tipoOrdenFecha, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, reclamoWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(cuenta, reclamoWs);

                MovimientoCuentaDTO movimientoTemp = null;
                Collection<MovimientoCuentaDTO> movimientosTemp = null;

                //obtenemos las cuentas corrientes
                if (reclamoWs.getMovimientos() != null && !reclamoWs.getMovimientos().isEmpty()) {
                    movimientosTemp = new ArrayList<>();
                    for (com.bds.wpn.ws.services.MovimientoCuentaDTO movimientoWS : reclamoWs.getMovimientos()) {
                        movimientoTemp = new MovimientoCuentaDTO();

                        BeanUtils.copyProperties(movimientoTemp, movimientoWS);

                        movimientosTemp.add(movimientoTemp);
                    }
                    cuenta.setMovimientosDTO(movimientosTemp);
                }
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listarMovimientos: ")
                    .append("CTA-").append(this.formatoAsteriscosWeb(numeroCuenta))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listarMovimientos: ")
                    .append("CTA-").append(this.formatoAsteriscosWeb(numeroCuenta))
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

}
