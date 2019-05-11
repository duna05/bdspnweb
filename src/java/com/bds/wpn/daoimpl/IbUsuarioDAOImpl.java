/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbUsuarioDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.IbPerfilesDTO;
import com.bds.wpn.dto.IbUsuariosCanalesDTO;
import com.bds.wpn.dto.IbUsuariosDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbUsuariosCanalesWs;
import com.bds.wpn.ws.services.IbUsuariosCanalesWs_Service;
import java.math.BigDecimal;
import java.net.URL;
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
@Stateless(name = "wpnIbUsuarioDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbUsuarioDAOImpl extends DAOUtil implements IbUsuarioDAO {

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(IbUsuarioDAOImpl.class.getName());

    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlUsuariosCanales = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbUsuariosCanalesWs?WSDL";

    /**
     * Metodo que obtiene un Usuario por canal
     *
     * @param idUsuario String id del usuario.
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return IbUsuariosCanalesDTO
     */
    @Override
    public IbUsuariosCanalesDTO obtenerIbUsuarioPorCanal(String idUsuario, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.IbUsuariosCanalesDTO usuarioWs;
        IbUsuariosCanalesDTO usuario = new IbUsuariosCanalesDTO();
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS
            usuarioWs = port.obtenerIbUsuarioPorCanal(idUsuario, idCanal, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, usuarioWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                BeanUtils.copyProperties(usuario, usuarioWs.getIbUsuarioCanal());
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerIbUsuarioPorCanal: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerIbUsuarioPorCanal: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            usuario.setRespuestaDTO(respuesta);
        }
        return usuario;
    }

    /**
     * Metodo que se encarga de registrar los datos de un cliente en la BD
     * Oracle 11
     *
     * @param cliente ClienteDTO objeto con los datos del cliente en oracle9
     * @param nroTDD String numero de TDD afiliada
     * @param idPerfil BigDecimal identificador del perfil
     * @param idCanal String String idCanal por el cual se ejecuta la
     * transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return Objeto de Respuesta con el resultado de la transaccion.
     */
    @Override
    public RespuestaDTO insertarDatosIbUsuario(ClienteDTO cliente, String nroTDD, BigDecimal idPerfil, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS
            //para este caso como es migracion de oracle 9 la clave se inserta vacia ya que se valida por otro SP
            respuestaWs = port.insertarDatosIbUsuario(cliente.getCodigoCliente(), nroTDD, cliente.getIdentificacion().substring(0, 1),
                    cliente.getIdentificacion().substring(1), cliente.getEmailCorreo(), cliente.getNombres() + " " + cliente.getApellidos(),
                    cliente.getCodigoTlfCell() + cliente.getTelefonoCell(), idPerfil, "", idCanal, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio insertarDatosIbUsuario: ")
                    .append("USR-").append(cliente.getCodigoCliente())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN insertarDatosIbUsuario: ")
                    .append("USR-").append(cliente.getCodigoCliente())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }
        return respuesta;

    }

    /**
     * Metodo que obtiene un Usuario por codigo de cliente
     *
     * @param codUsuario String Codigo del usuario.
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return IbUsuarioDTO
     */
    @Override
    public IbUsuariosDTO obtenerIbUsuarioPorCodusuario(String codUsuario, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.IbUsuarioDTO usuarioWs;
        IbUsuariosDTO usuario = new IbUsuariosDTO();
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS
            usuarioWs = port.obtenerIbUsuarioPorCodUsuario(codUsuario, idCanal, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, usuarioWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                BeanUtils.copyProperties(usuario, usuarioWs.getUsuario());
                IbPerfilesDTO perfil = new IbPerfilesDTO();
                BeanUtils.copyProperties(perfil, usuarioWs.getUsuario().getIdPerfil());
                usuario.setIdPerfilDTO(perfil);
//                if (usuarioWs.getUsuario().getIdRolJuridico() != null) {
//                    IbRolesJuridicosDTO rolJuridico = new IbRolesJuridicosDTO();
//                    BeanUtils.copyProperties(rolJuridico, usuarioWs.getUsuario().getIdRolJuridico());
//                    usuario.setIdRolJuridicoDTO(rolJuridico);
//                }
                //seteo de parametros no disponibles
                usuario.setFechaHoraCreacionDate(usuarioWs.getUsuario().getFechaHoraCreacion().toGregorianCalendar().getTime());
                usuario.setFechaHoraModificacionDate(usuarioWs.getUsuario().getFechaHoraModificacion().toGregorianCalendar().getTime());
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerIbUsuarioPorCodusuario: ")
                    .append("USR-").append(codUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerIbUsuarioPorCodusuario: ")
                    .append("USR-").append(codUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            usuario.setRespuestaDTO(respuesta);
        }
        return usuario;
    }

    /**
     * Metodo que obtiene un Usuario por TDD
     *
     * @param tdd String numero de tarjeta de debito
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return IbUsuarioDTO
     */
    @Override
    public IbUsuariosDTO obtenerIbUsuarioPorTDD(String tdd, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.IbUsuarioDTO usuarioWs;
        IbUsuariosDTO usuario = new IbUsuariosDTO();
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS
            usuarioWs = port.obtenerIbUsuarioPorTDD(tdd, idCanal, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, usuarioWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                BeanUtils.copyProperties(usuario, usuarioWs.getUsuario());
                IbPerfilesDTO perfil = new IbPerfilesDTO();
                BeanUtils.copyProperties(perfil, usuarioWs.getUsuario().getIdPerfil());
                usuario.setIdPerfilDTO(perfil);
//                if (usuarioWs.getUsuario().getIdRolJuridico() != null) {
//                    IbRolesJuridicosDTO rolJuridico = new IbRolesJuridicosDTO();
//                    BeanUtils.copyProperties(rolJuridico, usuarioWs.getUsuario().getIdRolJuridico());
//                    usuario.setIdRolJuridicoDTO(rolJuridico);
//                }
                //seteo de parametros no disponibles
                usuario.setFechaHoraCreacionDate(usuarioWs.getUsuario().getFechaHoraCreacion().toGregorianCalendar().getTime());
                usuario.setFechaHoraModificacionDate(usuarioWs.getUsuario().getFechaHoraModificacion().toGregorianCalendar().getTime());
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerIbUsuarioPorTDD: ")
                    .append("TDD-").append(this.formatoAsteriscosWeb(tdd))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerIbUsuarioPorTDD: ")
                    .append("TDD-").append(formatoAsteriscosWeb(tdd))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            usuario.setRespuestaDTO(respuesta);
        }
        return usuario;
    }

    /**
     * Metodo para actualizar los datos del usuario del core a la bd del IB
     *
     * @param cliente
     * @param nroTDD
     * @param idCanal
     * @param nombreCanal
     * @return RespuestaDTO
     */
    @Override
    public RespuestaDTO actualizarDatosUsuario(ClienteDTO cliente, String nroTDD, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        String tipoDoc;
        String nroDocumento;
        String nombreCompleto;
        
        try {
            
            if(cliente.getIdentificacion() != null && !cliente.getIdentificacion().isEmpty()){
               tipoDoc = cliente.getIdentificacion().substring(0, 1);
               nroDocumento = cliente.getIdentificacion().substring(1);
            }else{
                tipoDoc = null;
                nroDocumento = null;
            }       
            
            if ((cliente.getNombres() == null || cliente.getNombres().isEmpty()) 
                    && (cliente.getApellidos() == null || cliente.getApellidos().isEmpty())){
                nombreCompleto = null;
            }else if ((cliente.getNombres() == null || cliente.getNombres().isEmpty()) 
                    && (cliente.getApellidos() != null && !cliente.getApellidos().isEmpty())){
                nombreCompleto = cliente.getApellidos();
            }else if ((cliente.getNombres() != null && !cliente.getNombres().isEmpty()) 
                    && (cliente.getApellidos() == null || cliente.getApellidos().isEmpty())){
                nombreCompleto = cliente.getNombres();
            }else{
                nombreCompleto = cliente.getNombres() + " " + cliente.getApellidos();
            }
            
            
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            respuestaWs = port.actualizarDatosUsuario(cliente.getCodigoCliente(), nroTDD, tipoDoc,
                    nroDocumento, cliente.getEmailCorreo(), nombreCompleto,
                    cliente.getCodigoTlfCell() + cliente.getTelefonoCell(), "", nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio actualizarDatosUsuario: ")
                    .append("USR-").append(cliente.getCodigoCliente())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN actualizarDatosUsuario: ")
                    .append("USR-").append(cliente.getCodigoCliente())
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }
        return respuesta;
    }

    /**
     * Metodo para ingresar al internet banking con manejo de encriptamiento de
     * datos
     *
     * @param nroTDD
     * @param clave
     * @param idCanal
     * @param nombreCanal
     * @return RespuestaDTO
     */
    @Override
    public UtilDTO validarLogin(String nroTDD, String clave, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        com.bds.wpn.ws.services.UtilDTO utilWsTDD;
        IbUsuariosDTO usuario = new IbUsuariosDTO();
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            utilWs = port.validarLogin(nroTDD, clave, idCanal, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put("login", (utilWs.getResulados().getEntry().get(1).getValue()));

                if (resultados.get("login").toString().equals("true") && utilWs.getResulados().getEntry().get(0).getValue() != null) {
                    //clase para casteo dinamico de atributos
                    BeanUtils.copyProperties(usuario, ((com.bds.wpn.ws.services.IbUsuarioDTO) utilWs.getResulados().getEntry().get(0).getValue()).getUsuario());
                    resultados.put("usuario", usuario);
                    //Validamos el status de la TDD se hace por separado del login ya que esta informacion viene de Ora9
                    //y al combinar las transaciones con Ora11 el Obj de Conn cierra ambos DS en el .close                  
                 
                    utilWsTDD = port.validarTDDActivaCore(usuario.getCodUsuario(), nombreCanal,nroTDD);
                    //clase para casteo dinamico de atributos
                    BeanUtils.copyProperties(respuesta, utilWsTDD.getRespuesta());
                    if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                        throw new ServiceException();
                    } else {
                        if (utilWsTDD.getResulados() != null && utilWsTDD.getResulados().getEntry() != null && utilWsTDD.getResulados().getEntry().get(0).getValue() != null) {
                            if (!nroTDD.substring(6).equalsIgnoreCase(utilWsTDD.getResulados().getEntry().get(0).getValue().toString())) {
                                resultados.put("login", "false");
                                resultados.put("usuario", null);
                                respuesta.setDescripcionSP("pnw.error.usuario.bloqueado.tdd");
                            }
                        }
                    }
                } else {
                    resultados.put("usuario", null);
                }
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio validarLogin: ")
                    .append("TDD-").append(formatoAsteriscosWeb(nroTDD))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN validarLogin: ")
                    .append("TDD-").append(formatoAsteriscosWeb(nroTDD))
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
     * Metodo para consultar la cantidad de intentos fallidos de preguntas de
     * seguridad
     *
     * @param idUsuario identificador del usuario
     * @param nroTDD nro de TDD del usuario
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return RespuestaDTO
     */
    @Override
    public UtilDTO cantidadPregSegFallidas(String idUsuario, String nroTDD, String nombreCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            utilWs = port.cantidadPregSegFallidas(idUsuario, nroTDD, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put("intentosFallidos", (utilWs.getResulados().getEntry().get(0).getValue()));

                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio cantidadPregSegFallidas: ")
                    .append("TDD-").append(formatoAsteriscosWeb(nroTDD))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN cantidadPregSegFallidas: ")
                    .append("TDD-").append(formatoAsteriscosWeb(nroTDD))
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
     * Metodo actualiza la cantidad de intentos fallidos de las preguntas de
     * seguridad
     *
     * @param idUsuario identificador del usuario
     * @param cantIntentos cantidad de intentos fallidos a actualizar
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return RespuestaDTO
     */
    @Override
    public RespuestaDTO actualizarPregSegFallidas(String idUsuario, int cantIntentos, String nombreCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS
IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            respuestaWs = port.actualizarPregSegFallidas(idUsuario, cantIntentos, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio actualizarPregSegFallidas: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN actualizarPregSegFallidas: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }
        return respuesta;
    }
    
    /**
     * Metodo para validar si la TDD ingresada esta activa en el core bancario Ora9
     *
     * @param codUsuario
     * @param nroTDD
     * @param idCanal
     * @param nombreCanal
     * @return RespuestaDTO
     */
    @Override
    public UtilDTO validarTDDActivaCore(String codUsuario, String nroTDD, String idCanal, String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        com.bds.wpn.ws.services.UtilDTO utilWsTDD;
        IbUsuariosDTO usuario = new IbUsuariosDTO();
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS
            
                    utilWsTDD = port.validarTDDActivaCore(codUsuario, nombreCanal,nroTDD);
                    //clase para casteo dinamico de atributos
                    BeanUtils.copyProperties(respuesta, utilWsTDD.getRespuesta());
                    if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                        throw new ServiceException();
                    } else {
                        Map resultados = new HashMap(); 
                        resultados.put("resultado", false);
                        respuesta.setDescripcionSP("pnw.errores.tipo.datosInvalidos");
                        if (utilWsTDD.getResulados() != null && utilWsTDD.getResulados().getEntry() != null && utilWsTDD.getResulados().getEntry().get(0).getValue() != null) {
                            if (nroTDD.substring(6).equalsIgnoreCase(utilWsTDD.getResulados().getEntry().get(0).getValue().toString())) {
                                resultados.put("resultado", true);
                                respuesta.setDescripcionSP("OK");
                            }
                        }
                        util.setResuladosDTO(resultados);
                    }
            
            

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio validarTDDActivaCore: ")
                    .append("TDD-").append(formatoAsteriscosWeb(nroTDD))
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN validarTDDActivaCore: ")
                    .append("TDD-").append(formatoAsteriscosWeb(nroTDD))
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
     * Metodo para consultar la la TDD de un usuario por su Documento de Identidad
     *
     * @param tipoDoc tipo de documento
     * @param nroDoc nro de TDD del usuario
     * @param nombreCanal nombre del canal
     * @param idCanal identificador del canal
     * @return RespuestaDTO
     */
    @Override
    public UtilDTO obtenerTDDPorDoc(String tipoDoc, String nroDoc, String nombreCanal, String idCanal){
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        com.bds.wpn.ws.services.UtilDTO utilWsTDD;
        IbUsuariosDTO usuario = new IbUsuariosDTO();
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            IbUsuariosCanalesWs_Service service = new IbUsuariosCanalesWs_Service(new URL(urlWsdlUsuariosCanales));
            IbUsuariosCanalesWs port = service.getIbUsuariosCanalesWsPort();
            //se obtiene el objeto de salida del WS
            
                    utilWsTDD = port.obtenerTDDPorDoc(tipoDoc, nroDoc, nombreCanal);
                    //clase para casteo dinamico de atributos
                    BeanUtils.copyProperties(respuesta, utilWsTDD.getRespuesta());
                    if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put("tdd", (utilWsTDD.getResulados().getEntry().get(0).getValue()));

                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);
            }
            

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerTDDPorDoc: ")
                    .append("TDOC-").append(tipoDoc)
                    .append("-NDOC-").append(nroDoc)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerTDDPorDoc: ")
                    .append("TDOC-").append(tipoDoc)
                    .append("-NDOC-").append(nroDoc)
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
