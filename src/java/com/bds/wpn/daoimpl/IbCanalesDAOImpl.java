/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.CanalController;
import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.controller.TextosController;
import com.bds.wpn.dao.IbCanalesDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.IbCanalDTO;
import com.bds.wpn.dto.IbUsuariosCanalesDTO;
import com.bds.wpn.dto.IbUsuariosDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbCanalFacade;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbUsuariosCanalesWs;
import com.bds.wpn.ws.services.IbUsuariosCanalesWs_Service;
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
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author cesar.mujica
 */
@Named
@Stateless(name = "wpnIbCanalesDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbCanalesDAOImpl extends DAOUtil implements IbCanalesDAO {

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(IbCanalesDAOImpl.class.getName());

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbCanalFacade canaFacade;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;
    
    @Inject
    CanalController canalController;

    private final String urlWsdlUsuariosCanales = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbUsuariosCanalesWs?WSDL";

    /**
     * Obtiene la fecha de la ultima conexion realizada por el cliente al canal.
     *
     * @param idUsuario String id del cliente.
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return UtilDTO
     */
    @Override
    public UtilDTO consultarUltimaConexionCanal(String idUsuario, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS 
            utilWs = port.consultarUltimaConexionCanal(idUsuario, idCanal, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put(1, ((XMLGregorianCalendar) utilWs.getResulados().getEntry().get(0).getValue()).toGregorianCalendar().getTime());
                util.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio consultarUltimaConexionCanal: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN consultarUltimaConexionCanal: ")
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
     * Realiza el bloqueo de acceso al canal .
     *
     * @param idUsuario String id del cliente.
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return RespuestaDTO
     */
    @Override
    public RespuestaDTO bloquearAccesoCanal(String idUsuario, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS 
            respuestaWs = port.bloquearAccesoCanal(idUsuario, idCanal, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio bloquearAccesoCanal: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN bloquearAccesoCanal: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            this.notificarErrores(e, idCanal, nombreCanal,
                    parametrosController.getNombreParametro("wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("delsur.soporte.email", idCanal),
                    textosController.getNombreTexto("notificacion.error.email.titulo", idCanal), parametrosController.getNombreParametro("delsur.soporte.tlfs", idCanal),
                    textosController.getNombreTexto("motivo.sms.notificacion.error", idCanal));
        }
        return respuesta;
    }

    /**
     * Valida que no exista una conexion antes de iniciar session.
     * @param idUsuario String id del cliente.
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return UtilDTO
     */
    @Override
    public UtilDTO validarConexionSimultanea(String idUsuario, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS
            utilWs = port.validarConexionSimultanea(idUsuario, nombreCanal, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            util.setRespuestaDTO(respuesta);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno 
                Map resultados = new HashMap();
                resultados.put(1, ((Boolean) utilWs.getResulados().getEntry().get(0).getValue()));
                util.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio validarConexionSimultanea: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN validarConexionSimultanea: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        }
        return util;
    }

    /**
     * Metodo para validar el acceso a la banca movil via app mobile NOTA
     * estemetodo es temporal
     *
     * @param numeroTarjeta String numero de tarjeta de 20 digitos
     * @param password String password de acceso al IB
     * @param idCanal String String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return UtilDTO con el codigo del usuario y la fecha dela ultima conexion
     * si el acceso es correcto
     */
    @Override
    public UtilDTO loginIB(String numeroTarjeta, String password, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS
            utilWs = port.loginIB(numeroTarjeta, password, idCanal, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //Manejo de errores controlados
                if (respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    //obtenemos el resto de valores del objeto
                    //cuando es un mapa debemos traer los valores uno a uno
                    Map resultados = new HashMap();
                    resultados.put("codCliente", ((String) utilWs.getResulados().getEntry().get(0).getValue()));
                    util.setResuladosDTO(resultados);
                } 
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio loginIB: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(numeroTarjeta))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN loginIB: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(numeroTarjeta))
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
            util.setRespuestaDTO(respuesta);
        }
        return util;
    }

    /**
     * asocia un usuario a un canal y lo crea como activo y conectado
     *
     * @param usuario String el objeto usuario a asociar
     * @param idSesion id de la sesion
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return RespuestaDTO
     */
    @Override
    public RespuestaDTO insertarUsuarioCanal(IbUsuariosDTO usuario, String idSesion, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS 
            respuestaWs = port.insertarUsuarioCanal(usuario.getCodUsuario(), idSesion, idCanal, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio insertarUsuarioCanal: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(usuario.getTdd()))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN insertarUsuarioCanal: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(usuario.getTdd()))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }
        return respuesta;
    }

    /**
     * Obtiene la fecha de la ultima conexion realizada por el cliente al canal.
     *
     * @param usuario IbUsuariosDTO String id del cliente.
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @param estatusConeccion String
     * @param intentosFallidos int cantidad de intentos fallidos de acceso
     * @param idSesion identificador unico de la sesion
     * @param estatusAcceso indentificador del estado del acceso al canal
     * @param estatusRegistro indentificador del estado del registro al canal
     * @param limiteInternas limite de monto de operaciones propias en DELSUR
     * @param limiteExternas limite de monto de operaciones propias en otros bancos
     * @param limiteInternasTerc limite de monto de operaciones terceros en DELSUR
     * @param limiteExternasTerc limite de monto de operaciones terceros en otros bancos
     * @return UtilDTO
     */
    @Override
    public RespuestaDTO actualizarUsuarioCanal(IbUsuariosDTO usuario, String idCanal, String nombreCanal, String estatusConeccion, 
            int intentosFallidos, String idSesion, String estatusAcceso, String estatusRegistro, BigDecimal limiteInternas, 
            BigDecimal limiteExternas, BigDecimal limiteInternasTerc, BigDecimal limiteExternasTerc) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS 
            respuestaWs = port.actualizarUsuarioCanal(usuario.getId().toString(), idCanal, nombreCanal, estatusConeccion, intentosFallidos, idSesion, estatusAcceso, 
                    estatusRegistro, limiteInternas, limiteExternas, limiteInternasTerc, limiteExternasTerc);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }
            
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio actualizarUsuarioCanal: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(usuario.getTdd()))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN actualizarUsuarioCanal: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(usuario.getTdd()))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }
        return respuesta;
    }

    /**
     * *
     * Devuelve un objeto IbUsuariosCanalesDTO asociado al id del usuario y el
     * id del canal
     *
     * @param usuario IbUsuariosDTO
     * @param idCanal String
     * @return IbUsuariosCanalesDTO
     */
    @Override
    public IbUsuariosCanalesDTO consultarUsuarioCanalporIdUsuario(IbUsuariosDTO usuario, String idCanal) {
        IbUsuariosCanalesDTO usuariosCanales = new IbUsuariosCanalesDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.IbUsuariosCanalesDTO usuariosCanalesWS;
        IbCanalDTO canal = new IbCanalDTO();

        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS 
            usuariosCanalesWS = port.usuarioCanalporIdUsuario(String.valueOf(usuario.getId()), idCanal);
            
            BeanUtils.copyProperties(respuesta, usuariosCanalesWS.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }else{
                //clase para casteo dinamico de atributos
                BeanUtils.copyProperties(usuariosCanales, usuariosCanalesWS.getIbUsuarioCanal());
                if(usuariosCanalesWS.getIbUsuarioCanal().getFechaHoraUltimaInteraccion() != null){ 
                    usuariosCanales.setFechaHoraUltimaInteraccionDate(usuariosCanalesWS.getIbUsuarioCanal().getFechaHoraUltimaInteraccion().toGregorianCalendar().getTime());
                }
            }

            canal = canalController.getIbCanalDTO(idCanal);

            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio consultarUsuarioCanalporIdUsuario: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(usuario.getTdd()))
                    .append("-CH-").append(canal.getCodigo())
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN consultarUsuarioCanalporIdUsuario: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(usuario.getTdd()))
                    .append("-CH-").append(canal.getCodigo())
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            usuariosCanales.setRespuestaDTO(respuesta);
        }
        return usuariosCanales;
    }

    /**
     * Metodo para validar el estatus de registro y de acceso del usuario en
     * IB_USUARIOS_CANALES
     *
     * @param codUsuario String
     * @param idCanal String
     * @param nombreCanal String
     * @return RespuestaDTO Si esta bloqueado en alguno de los dos retorna el codigoSP y
     * textoSP
     */
    @Override
    public RespuestaDTO validarUsuarioAccesoRegistro(String codUsuario, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS 
            respuestaWs = port.validarUsuarioAccesoRegistro(codUsuario, idCanal, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio validarUsuarioAccesoRegistro: ")
                    .append("USR-").append(codUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN validarUsuarioAccesoRegistro: ")
                    .append("USR-").append(codUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }
        return respuesta;
    }
    
    /**
     * Metodo para obtener el listado de canales
     *
     * @param codUsuario codigo del usuario para efectos de registro en el log
     * @param nombreCanal nombre del canal para efectos de registro en el log
     * @param idCanal
     * @return IbCanalDTO Listado de canales para un usuario
     */
    @Override
    public IbCanalDTO listadoCanales(String codUsuario, String nombreCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        IbCanalDTO CanalDTO = new IbCanalDTO();
        //List<IbCanalDTO> listCanales = new ArrayList<>();
        
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.IbCanalDTO IbCanalWs= port.listadoCanales(codUsuario, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, IbCanalWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }
             
                List<IbCanalDTO> listCanalesDTO = new ArrayList<>();
                for (com.bds.wpn.ws.services.IbCanal canal : IbCanalWs.getCanales()) {
                    IbCanalDTO canalTemp = new IbCanalDTO();
                    BeanUtils.copyProperties(canalTemp, canal);
                    listCanalesDTO.add(canalTemp);
                }

                CanalDTO.setIbCanalDTO(listCanalesDTO);
                
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoCanales: ")
                    .append("USR-").append(codUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoCanales: ")
                    .append("USR-").append(codUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            CanalDTO.setRespuestaDTO(respuesta);
        }
        return CanalDTO;
    }

    @Override
    public IbCanalDTO consultaCanalesPorUsuario(String idUsuario, String idcanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        IbCanalDTO CanalDTO = new IbCanalDTO(); 
        
         try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS 
            com.bds.wpn.ws.services.IbCanalDTO IbCanalWs= port.consultaCanalesPorUsuario(idUsuario, idcanal, nombreCanal);
             //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, IbCanalWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }
            
              List<IbCanalDTO> listCanalesDTO = new ArrayList<>();
                for (com.bds.wpn.ws.services.IbCanal canal : IbCanalWs.getCanales()) {
                    IbCanalDTO canalTemp = new IbCanalDTO();
                    BeanUtils.copyProperties(canalTemp, canal);
                    listCanalesDTO.add(canalTemp);
                }

                CanalDTO.setIbCanalDTO(listCanalesDTO);
                
            } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoCanales: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoCanales: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            CanalDTO.setRespuestaDTO(respuesta);
        }
        return CanalDTO;
            
    }
}
