/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.GestionServiciosDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.AfiliacionServicioDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.ServicioDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.delSur.wpn.ws.services.Afiliacion;
import com.delSur.wpn.ws.services.Beneficiario;
import com.delSur.wpn.ws.services.DELSURException_Exception;
import com.delSur.wpn.ws.services.ListadoAfiliaciones;
import com.delSur.wpn.ws.services.ListadoServicios;
import com.delSur.wpn.ws.services.PAfiliarAbonadoService;
import com.delSur.wpn.ws.services.PEliminarAfiliacionService;
import com.delSur.wpn.ws.services.PObtenerListadoAfiliacionesService;
import com.delSur.wpn.ws.services.PObtenerListadoServiciosSistemaService;
import com.delSur.wpn.ws.services.PRecargar;
import com.delSur.wpn.ws.services.PRecargarService;
import com.delSur.wpn.ws.services.Respuesta;
import com.delSur.wpn.ws.services.Servicio;
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
import org.apache.log4j.Logger;

/**
 *
 * @author cesar.mujica
 */
@Named
@Stateless(name = "wpnGestionServiciosDAOweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class GestionServiciosDAOImpl extends DAOUtil implements GestionServiciosDAO {

    private static final Logger logger = Logger.getLogger(GestionServiciosDAOImpl.class.getName());

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlAfilServ = getSessionScope().get(SessionAttributesNames.URL_DIGITEL) + "/p_afiliarAbonado_WS/P_AfiliarAbonadoService?wsdl";
    private final String urlWsdldesafilServ = getSessionScope().get(SessionAttributesNames.URL_DIGITEL) + "/p_eliminarAfiliacionAbonado_WS/P_EliminarAfiliacionService?wsdl";
    private final String urlWsdlListarServ = getSessionScope().get(SessionAttributesNames.URL_DIGITEL) + "/p_obtenerListadoServiciosSistema_WS/P_ObtenerListadoServiciosSistemaService?wsdl";
    private final String urlWsdlListarAfilServ = getSessionScope().get(SessionAttributesNames.URL_DIGITEL) + "/p_obtenerListadoAfiliaciones_WS/P_ObtenerListadoAfiliacionesService?wsdl";
    private final String urlWsdlModifAfilServ = getSessionScope().get(SessionAttributesNames.URL_DIGITEL) + "/p_modificarAfiliacionAbonado_WS/P_ModificarAfiliacionAbonadoService?wsdl";
    private final String urlWsdlRecargarServ = getSessionScope().get(SessionAttributesNames.URL_DIGITEL) + "/p_recargar_WS/P_RecargarService?wsdl";

    /**
     * Metodo se encarga de crear una afiliacion de servicio
     *
     *
     * @param afiliacion
     * @param tipoCanal
     * @param nombreCanal
     * @return
     */
    @Override
    public RespuestaDTO agregarAfiliacion(AfiliacionServicioDTO afiliacion, String tipoCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Respuesta respuestaWs;
        try {
            //invocacion del WS
            PAfiliarAbonadoService service = new PAfiliarAbonadoService(new URL(urlWsdlAfilServ));
            com.delSur.wpn.ws.services.AfiliarAbonado port = service.getAfiliarAbonadoPort();

            Afiliacion afiliacionWS = new Afiliacion();
            Servicio servicioWS = new Servicio();
            Beneficiario beneficiarioWS = new Beneficiario();
            servicioWS.setCodigoServicio(afiliacion.getServicio().getCodigoServicio());
            servicioWS.setDescripcionServicio(afiliacion.getServicio().getDescripcionServicio());
            servicioWS.setProveedorServicio(afiliacion.getServicio().getProveedorServicio());
            beneficiarioWS.setIdentificacion(afiliacion.getBeneficiario().getIdentificacion());
            beneficiarioWS.setNombre(afiliacion.getBeneficiario().getNombre());
            beneficiarioWS.setTipoIdentificacion(afiliacion.getBeneficiario().getTipoIdentificacion());
            afiliacionWS.setCodigoAbonado(afiliacion.getCodigoAbonado());
            afiliacionWS.setCodigoCliente(afiliacion.getCodigoCliente());
            afiliacionWS.setServicio(servicioWS);
            afiliacionWS.setBeneficiario(beneficiarioWS);

            //se obtiene el objeto de salida del WS 
            respuestaWs = port.afiliarAbonado(afiliacionWS, tipoCanal);

            //manejo de codigos de error diferentes de exitoso para el WS de DIGITEL
            if (respuestaWs != null && respuestaWs.getCodigoSalida() != 1 && respuestaWs.getCodigoSalida() != 283) {
                respuesta.setCodigoSP(String.valueOf(respuestaWs.getCodigoSalida()));
                respuesta.setDescripcionSP(String.valueOf(respuestaWs.getDescripcion()));
            }

        } catch (DELSURException_Exception e) {
            if (e.getMessage().contains("ORA-")) {
                respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
            } else {
                respuesta.setCodigoSP(CODIGO_EXCEPCION_GENERICA);//revisar el log
                if(e.getMessage() != null && !e.getMessage().trim().equalsIgnoreCase("")){
                    String[] errores = e.getMessage().split(": ");
                    if(errores.length > 0){
                        respuesta.setDescripcionSP(errores[(errores.length-1)]);//revisar el log
                    }
                }
            }
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio agregarAfiliacion: ")
                    .append("-SRV-").append(afiliacion.getServicio().getDescripcionServicio())
                    .append("-NRO-").append(afiliacion.getCodigoAbonado())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN agregarAfiliacion: ")
                    .append("-SRV-").append(afiliacion.getServicio().getDescripcionServicio())
                    .append("-NRO-").append(afiliacion.getCodigoAbonado())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return respuesta;
    }

    /**
     * Metodo se encarga de eliminar una afiliacion de servicio
     *
     *
     * @param afiliacion
     * @param nombreCanal
     * @return
     */
    @Override
    public RespuestaDTO eliminarAfiliacion(AfiliacionServicioDTO afiliacion, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Respuesta respuestaWs;
        try {
            //invocacion del WS
            PEliminarAfiliacionService service = new PEliminarAfiliacionService(new URL(urlWsdldesafilServ));
            com.delSur.wpn.ws.services.EliminarAfiliacion port = service.getEliminarAfiliacionPort();

            Afiliacion afiliacionWS = new Afiliacion();
            Servicio servicioWS = new Servicio();
            Beneficiario beneficiarioWS = new Beneficiario();
            servicioWS.setCodigoServicio(afiliacion.getServicio().getCodigoServicio());
            servicioWS.setDescripcionServicio(afiliacion.getServicio().getDescripcionServicio());
            servicioWS.setProveedorServicio(afiliacion.getServicio().getProveedorServicio());
            beneficiarioWS.setIdentificacion(afiliacion.getBeneficiario().getIdentificacion());
            beneficiarioWS.setNombre(afiliacion.getBeneficiario().getNombre());
            beneficiarioWS.setTipoIdentificacion(afiliacion.getBeneficiario().getTipoIdentificacion());
            afiliacionWS.setCodigoAbonado(afiliacion.getCodigoAbonado());
            afiliacionWS.setCodigoCliente(afiliacion.getCodigoCliente());
            afiliacionWS.setServicio(servicioWS);
            afiliacionWS.setBeneficiario(beneficiarioWS);
            afiliacionWS.setAfiliacionID(afiliacion.getAfiliacionID());
            afiliacionWS.setEstado(afiliacion.getEstado());

            //se obtiene el objeto de salida del WS 
            respuestaWs = port.eliminarAfiliacion(afiliacionWS);
            //manejo de codigos de error diferentes de exitoso para el WS de DIGITEL
            if (respuestaWs != null && respuestaWs.getCodigoSalida() != 1 && respuestaWs.getCodigoSalida() != 283) {
                respuesta.setCodigoSP(String.valueOf(respuestaWs.getCodigoSalida()));
                respuesta.setDescripcionSP(String.valueOf(respuestaWs.getDescripcion()));
            }

        } catch (DELSURException_Exception e) {
            if (e.getMessage().contains("ORA-")) {
                respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
            } else {
                respuesta.setCodigoSP(CODIGO_EXCEPCION_GENERICA);//revisar el log
                if(e.getMessage() != null && !e.getMessage().trim().equalsIgnoreCase("")){
                    String[] errores = e.getMessage().split(": ");
                    if(errores.length > 0){
                        respuesta.setDescripcionSP(errores[(errores.length-1)]);//revisar el log
                    }
                }
            }
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio eliminarAfiliacion: ")
                    .append("-SRV-").append(afiliacion.getServicio().getDescripcionServicio())
                    .append("-NRO-").append(afiliacion.getCodigoAbonado())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            e.getMessage();

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN eliminarAfiliacion: ")
                    .append("-SRV-").append(afiliacion.getServicio().getDescripcionServicio())
                    .append("-NRO-").append(afiliacion.getCodigoAbonado())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return respuesta;
    }

    /**
     * Metodo se encarga de retornar la lista de servicios
     *
     * @param nombreCanal
     * @return
     */
    @Override
    public ServicioDTO obtenerListadoServicios(String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        ServicioDTO servicios = new ServicioDTO();
        try {
            //invocacion del WS
            PObtenerListadoServiciosSistemaService service = new PObtenerListadoServiciosSistemaService(new URL(urlWsdlListarServ));
            ListadoServicios port = service.getListadoServiciosPort();
            List<Servicio> serviciosWS;
            servicios.setServicios(new ArrayList<ServicioDTO>());

            //se obtiene el objeto de salida del WS 
            serviciosWS = port.obtenerListadoServiciosSistema();

            if (serviciosWS != null && !serviciosWS.isEmpty()) {
                ServicioDTO servicio;
                for (Servicio servicioWS : serviciosWS) {
                    servicio = new ServicioDTO();
                    servicio.setCodigoServicio(servicioWS.getCodigoServicio());
                    servicio.setDescripcionServicio(servicioWS.getDescripcionServicio());
                    servicio.setProveedorServicio(servicioWS.getProveedorServicio());
                    servicios.getServicios().add(servicio);
                }
            } else {
                respuesta.setCodigo(CODIGO_SIN_RESULTADOS_JPA);
            }

        } catch (DELSURException_Exception e) {
            if (e.getMessage().contains("ORA-")) {
                respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
            } else {
                respuesta.setCodigoSP(CODIGO_EXCEPCION_GENERICA);//revisar el log
                if(e.getMessage() != null && !e.getMessage().trim().equalsIgnoreCase("")){
                    String[] errores = e.getMessage().split(": ");
                    if(errores.length > 0){
                        respuesta.setDescripcionSP(errores[(errores.length-1)]);//revisar el log
                    }
                }
            }
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerListadoServicios: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerListadoServicios: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        } finally {
            servicios.setRespuestaDTO(respuesta);
        }
        return servicios;
    }

    /**
     * Metodo se encarga de retornar la lista de afiliaciones a un servicio
     * determinado
     *
     * @param codCliente
     * @param codServicio
     * @param nombreCanal
     * @return
     */
    @Override
    public AfiliacionServicioDTO obtenerListadoAfiliaciones(String codCliente, String codServicio, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        AfiliacionServicioDTO afiliaciones = new AfiliacionServicioDTO();
        try {
            //invocacion del WS
            PObtenerListadoAfiliacionesService service = new PObtenerListadoAfiliacionesService(new URL(urlWsdlListarAfilServ));
            ListadoAfiliaciones port = service.getListadoAfiliacionesPort();
            List<Afiliacion> afiliacionesWS;
            afiliaciones.setAfiliacionesServicio(new ArrayList<AfiliacionServicioDTO>());

            //se obtiene el objeto de salida del WS 
            afiliacionesWS = port.obtenerListadoAfiliaciones(Integer.parseInt(codCliente), codServicio);

            if (afiliacionesWS != null && !afiliacionesWS.isEmpty()) {
                AfiliacionServicioDTO afiliacion;
                for (Afiliacion afiliacionWS : afiliacionesWS) {
                    afiliacion = new AfiliacionServicioDTO();
                    afiliacion.setAfiliacionID(afiliacionWS.getAfiliacionID());
                    afiliacion.setCodigoAbonado(afiliacionWS.getCodigoAbonado());
                    afiliacion.setEstado(afiliacionWS.getEstado());
                    afiliacion.setCodigoCliente(afiliacionWS.getCodigoCliente());
                    afiliacion.getBeneficiario().setIdentificacion(afiliacionWS.getBeneficiario().getIdentificacion());
                    afiliacion.getBeneficiario().setTipoIdentificacion(afiliacionWS.getBeneficiario().getTipoIdentificacion());
                    afiliacion.getBeneficiario().setNombre(afiliacionWS.getBeneficiario().getNombre());
                    afiliacion.getServicio().setCodigoServicio(afiliacionWS.getServicio().getCodigoServicio());
                    afiliacion.getServicio().setDescripcionServicio(afiliacionWS.getServicio().getDescripcionServicio());
                    afiliacion.getServicio().setProveedorServicio(afiliacionWS.getServicio().getProveedorServicio());
                    afiliaciones.getAfiliacionesServicio().add(afiliacion);
                }
            } else {
                respuesta.setCodigo(CODIGO_SIN_RESULTADOS_JPA);
            }

        } catch (DELSURException_Exception e) {
            if (e.getMessage().contains("ORA-")) {
                respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
            } else {
                respuesta.setCodigoSP(CODIGO_EXCEPCION_GENERICA);//revisar el log
                if(e.getMessage() != null && !e.getMessage().trim().equalsIgnoreCase("")){
                    String[] errores = e.getMessage().split(": ");
                    if(errores.length > 0){
                        respuesta.setDescripcionSP(errores[(errores.length-1)]);//revisar el log
                    }
                }
            }
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerListadoAfiliaciones: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerListadoAfiliaciones: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        } finally {
            afiliaciones.setRespuestaDTO(respuesta);
        }
        return afiliaciones;
    }

    /**
     * Metodo se encarga de crear una afiliacion de servicio
     *
     *
     * @param afiliacion
     * @param monto
     * @param ctaDebitar
     * @param tipoCanal
     * @param nombreCanal
     * @return
     */
    @Override
    public UtilDTO recargar(AfiliacionServicioDTO afiliacion, BigDecimal monto, String ctaDebitar, String tipoCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Respuesta respuestaWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            //NOTA: ES IMPORTANTE QUE EL CLIENTE DE RECARGA ESTE AL FINAL PARA QUE EL OBJETO RESPUESTA NO SE PIERDA YA QUE NO SE REUTILIZA EL MISMO
            PRecargarService service = new PRecargarService(new URL(urlWsdlRecargarServ));
            PRecargar port = service.getPRecargarPort();

            Afiliacion afiliacionWS = new Afiliacion();
            Servicio servicioWS = new Servicio();
            Beneficiario beneficiarioWS = new Beneficiario();
            servicioWS.setCodigoServicio(afiliacion.getServicio().getCodigoServicio());
            servicioWS.setDescripcionServicio(afiliacion.getServicio().getDescripcionServicio());
            servicioWS.setProveedorServicio(afiliacion.getServicio().getProveedorServicio());
            beneficiarioWS.setIdentificacion(afiliacion.getBeneficiario().getIdentificacion());
            beneficiarioWS.setNombre(afiliacion.getBeneficiario().getNombre());
            beneficiarioWS.setTipoIdentificacion(afiliacion.getBeneficiario().getTipoIdentificacion());
            afiliacionWS.setAfiliacionID(afiliacion.getAfiliacionID());
            afiliacionWS.setCodigoAbonado(afiliacion.getCodigoAbonado());
            afiliacionWS.setCodigoCliente(afiliacion.getCodigoCliente());
            afiliacionWS.setServicio(servicioWS);
            afiliacionWS.setBeneficiario(beneficiarioWS);

            //se obtiene el objeto de salida del WS 
            respuestaWs = port.recargar(afiliacionWS, monto.doubleValue(), ctaDebitar, tipoCanal);
            //manejo de codigos de error diferentes de exitoso para el WS de DIGITEL
            if (respuestaWs != null && respuestaWs.getCodigoSalida() != 1 && respuestaWs.getCodigoSalida() != 283) {
                respuesta.setCodigoSP(String.valueOf(respuestaWs.getCodigoSalida()));
                respuesta.setDescripcionSP(String.valueOf(respuestaWs.getDescripcion()));
            } else {
                if (respuestaWs != null && respuestaWs.getTraceId() != 0) {
                    Map resultados = new HashMap();
                    resultados.put("id", respuestaWs.getTraceId());
                    util.setResuladosDTO(resultados);
                }
            }

        } catch (DELSURException_Exception e) {
            if (e.getMessage().contains("ORA-")) {
                respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
            } else {
                respuesta.setCodigoSP(CODIGO_EXCEPCION_GENERICA);//revisar el log
                if(e.getMessage() != null && !e.getMessage().trim().equalsIgnoreCase("")){
                    String[] errores = e.getMessage().split(": ");
                    if(errores.length > 0){
                        respuesta.setDescripcionSP(errores[(errores.length-1)]);//revisar el log
                    }
                }
            }
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio recargar: ")
                    .append("-SRV-").append(afiliacion.getServicio().getDescripcionServicio())
                    .append("-NRO-").append(afiliacion.getCodigoAbonado())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            e.getMessage();

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN recargar: ")
                    .append("-SRV-").append(afiliacion.getServicio().getDescripcionServicio())
                    .append("-NRO-").append(afiliacion.getCodigoAbonado())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        } finally {
            util.setRespuestaDTO(respuesta);
        }
        return util;
    }

}
