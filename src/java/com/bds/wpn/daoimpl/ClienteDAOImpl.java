/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.controller.TextosController;
import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dao.NotificationServiceType.ServiceType;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.FideicomisoDTO;
import com.bds.wpn.dto.PosicionConsolidadaDTO;
import com.bds.wpn.dto.PrestamoDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.TarjetasCreditoDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.ClienteWs;
import com.bds.wpn.ws.services.ClienteWs_Service;
import com.bds.wpn.ws.services.CuentaWs;
import com.bds.wpn.ws.services.CuentaWs_Service;
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
 * @author cesar.mujica
 */
@Named
@Stateless(name = "wpnClienteDAOImplweb")
@NotificationServiceType(ServiceType.SERVICES)
public class ClienteDAOImpl extends DAOUtil implements ClienteDAO {

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
    private static final Logger logger = Logger.getLogger(ClienteDAOImpl.class.getName());

    private final String urlWsdlCliente = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/ClienteWs?wsdl";

    private final String urlWsdlCuenta = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/CuentaWs?wsdl";

    /**
     * Realiza la validacion de la clave con la ficha del cliente.
     *
     * @param codigoCliente codigo del cliente
     * @param claveCifrada clave cifrada
     * @param nombreCanal codigo del canal
     * @param idCanal identificador unico del canal
     * @return UtilDTO retorna la validacion de la clave
     */
    @Override
    public UtilDTO validarClaveFondo(String codigoCliente, String claveCifrada, String nombreCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            ClienteWs_Service service = new ClienteWs_Service(new URL(urlWsdlCliente));
            ClienteWs port = service.getClienteWsPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            utilWs = port.validarClaveFondo(codigoCliente, claveCifrada, nombreCanal);
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
                resultados.put("resultado", (utilWs.getResulados().getEntry().get(0).getValue()));
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio validarClaveFondo: ")
                    .append("CODUSR-").append(codigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN validarClaveFondo: ")
                    .append("CODUSR-").append(codigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (util.getResuladosDTO() == null) {
                util.setResuladosDTO(new HashMap());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            util.setRespuestaDTO(respuesta);
        }
        return util;

    }

    /**
     * Retorna los datos de los productos del cliente
     *
     * @param codigoCliente String codigo del cliente
     * @param idCanal String con el id del canal
     * @param canal String canal por el cual se realiza la consulta
     * @return PosicionConsolidadaDTO datos de productos de cliente en core
     * bancario
     */
    @Override
    public PosicionConsolidadaDTO consultarPosicionConsolidadaCliente(String codigoCliente, String idCanal, String canal) {

        PosicionConsolidadaDTO posicion = new PosicionConsolidadaDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        CuentaDTO cuentaDTO;
        PrestamoDTO prestamoDTO;
        TarjetasCreditoDTO tarjeta;
        FideicomisoDTO fideicomisoDTO;

        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            ClienteWs_Service service = new ClienteWs_Service(new URL(urlWsdlCliente));
            ClienteWs port = service.getClienteWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.PosicionConsolidadaDTO posicionWs = port.posicionConsolidadaCliente(codigoCliente, canal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, posicionWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(posicion, posicionWs);

                List<CuentaDTO> listCuenta = new ArrayList<>();
                for (com.bds.wpn.ws.services.CuentaDTO cuenta : posicionWs.getCuentasAhorro()) {
                    cuentaDTO = new CuentaDTO();
                    BeanUtils.copyProperties(cuentaDTO, cuenta);
                    listCuenta.add(cuentaDTO);
                }
                posicion.setCuentasAhorro(listCuenta);

                listCuenta = new ArrayList<>();
                for (com.bds.wpn.ws.services.CuentaDTO cuenta : posicionWs.getCuentasCorriente()) {
                    cuentaDTO = new CuentaDTO();
                    BeanUtils.copyProperties(cuentaDTO, cuenta);
                    listCuenta.add(cuentaDTO);
                }
                posicion.setCuentasCorriente(listCuenta);

                listCuenta = new ArrayList<>();
                for (com.bds.wpn.ws.services.CuentaDTO cuenta : posicionWs.getCuentasME()) {
                    cuentaDTO = new CuentaDTO();
                    BeanUtils.copyProperties(cuentaDTO, cuenta);
                    listCuenta.add(cuentaDTO);
                }
                posicion.setCuentasME(listCuenta);

                List<PrestamoDTO> listPrestamo = new ArrayList<>();
                for (com.bds.wpn.ws.services.PrestamoDTO prestamo : posicionWs.getPrestamos()) {
                    prestamoDTO = new PrestamoDTO();
                    BeanUtils.copyProperties(prestamoDTO, prestamo);
                    listPrestamo.add(prestamoDTO);
                }
                posicion.setPrestamos(listPrestamo);

                List<TarjetasCreditoDTO> listTarjeta = new ArrayList<>();
                for (com.bds.wpn.ws.services.TarjetasCreditoDTO tdc : posicionWs.getTarjetasCredito()) {
                    tarjeta = new TarjetasCreditoDTO();
                    BeanUtils.copyProperties(tarjeta, tdc);
                    listTarjeta.add(tarjeta);
                }
                posicion.setTarjetasCredito(listTarjeta);

                List<FideicomisoDTO> listFideicomiso = new ArrayList<>();
                for (com.bds.wpn.ws.services.FideicomisoDTO fideicomiso : posicionWs.getFideicomisos()) {
                    fideicomisoDTO = new FideicomisoDTO();
                    BeanUtils.copyProperties(fideicomisoDTO, fideicomiso);
                    listFideicomiso.add(fideicomisoDTO);
                }
                posicion.setFideicomisos(listFideicomiso);

            }

        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio consultarPosicionConsolidadaCliente: ")
                    .append("USR-").append(codigoCliente)
                    .append("-CH-").append(canal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN consultarPosicionConsolidadaCliente: ")
                    .append("USR-").append(codigoCliente)
                    .append("-CH-").append(canal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
            this.notificarErrores(e, idCanal, canal,
                    parametrosController.getNombreParametro("wsdl.notificaciones", idCanal), parametrosController.getNombreParametro("delsur.soporte.email", idCanal),
                    textosController.getNombreTexto("notificacion.error.email.titulo", idCanal), parametrosController.getNombreParametro("delsur.soporte.tlfs", idCanal),
                    textosController.getNombreTexto("motivo.sms.notificacion.error", idCanal));
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            posicion.setRespuestaDTO(respuesta);
        }
        return posicion;

    }

    /**
     * Metodo que obtiene el listado de cuentas de ahorro y corrientes que posee
     * un cliente
     *
     * @param codigoCliente String codigo del cliente
     * @param idCanal String con el id del canal
     * @param nombreCanal String codigo de canal
     * @return ClienteDTO listado de cuentas de ahorro y corrientes que posee un
     * cliente
     */
    @Override
    public ClienteDTO listadoCuentasClientes(String codigoCliente, String idCanal, String nombreCanal) {
        ClienteDTO cliente = new ClienteDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.ClienteDTO clienteWs = port.listadoCuentasCliente(codigoCliente, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, clienteWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(cliente, clienteWs);
                CuentaDTO cuentaTemp = null;
                List<CuentaDTO> cuentasTemp = null;

                //obtenemos las cuentas corrientes
                if (clienteWs.getCuentasCorriente() != null && !clienteWs.getCuentasCorriente().isEmpty()) {
                    cuentasTemp = new ArrayList<>();
                    for (com.bds.wpn.ws.services.CuentaDTO cuentaWs : clienteWs.getCuentasCorriente()) {
                        cuentaTemp = new CuentaDTO();
                        BeanUtils.copyProperties(cuentaTemp, cuentaWs);
                        cuentasTemp.add(cuentaTemp);
                    }
                    cliente.setCuentasCorrienteDTO(cuentasTemp);
                }

                if (clienteWs.getCuentasAhorro() != null && !clienteWs.getCuentasAhorro().isEmpty()) {
                    cuentasTemp = new ArrayList<>();
                    for (com.bds.wpn.ws.services.CuentaDTO cuentaWs : clienteWs.getCuentasAhorro()) {
                        cuentaTemp = new CuentaDTO();
                        BeanUtils.copyProperties(cuentaTemp, cuentaWs);
                        cuentasTemp.add(cuentaTemp);
                    }
                    cliente.setCuentasAhorroDTO(cuentasTemp);
                }

                if (clienteWs.getCuentasMonedaExtranjera() != null && !clienteWs.getCuentasMonedaExtranjera().isEmpty()) {
                    cuentasTemp = new ArrayList<>();
                    for (com.bds.wpn.ws.services.CuentaDTO cuentaWs : clienteWs.getCuentasMonedaExtranjera()) {
                        cuentaTemp = new CuentaDTO();
                        BeanUtils.copyProperties(cuentaTemp, cuentaWs);
                        cuentasTemp.add(cuentaTemp);
                    }
                    cliente.setCuentasMonedaExtranjeraDTO(cuentasTemp);
                }
            }

        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio listadoCuentasClientes: ")
                    .append("USR-").append(codigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN listadoCuentasClientes: ")
                    .append("USR-").append(codigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            cliente.setRespuestaDTO(respuesta);
        }
        return cliente;
    }

    /**
     * Método que obtiene el listado de cuentas de ahorro y corriente, activas y
     * canceladas, que posee un cliente, asociadas a una TDD en específico
     *
     * @param codigoCliente String código del cliente
     * @param tdd tarjeta de débito
     * @param idCanal String con el id del canal
     * @param nombreCanal String codigo de canal
     * @return ClienteDTO listado de cuentas de ahorro y corrientes que posee un
     * cliente
     */
    @Override
    public ClienteDTO listCuentasActivasCanceladas(String codigoCliente, String tdd, String idCanal, String nombreCanal) {
        ClienteDTO cliente = new ClienteDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCuenta));
            CuentaWs port = service.getCuentaWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.ClienteDTO clienteWs = port.listCuentasActivasCanceladas(tdd, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, clienteWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(cliente, clienteWs);
                CuentaDTO cuentaTemp = null;
                List<CuentaDTO> cuentasTemp = null;

                //obtenemos las cuentas corrientes
                if (clienteWs.getCuentasCorriente() != null && !clienteWs.getCuentasCorriente().isEmpty()) {
                    cuentasTemp = new ArrayList<>();
                    for (com.bds.wpn.ws.services.CuentaDTO cuentaWs : clienteWs.getCuentasCorriente()) {
                        cuentaTemp = new CuentaDTO();
                        BeanUtils.copyProperties(cuentaTemp, cuentaWs);
                        cuentasTemp.add(cuentaTemp);
                    }
                    cliente.setCuentasCorrienteDTO(cuentasTemp);
                }

                if (clienteWs.getCuentasAhorro() != null && !clienteWs.getCuentasAhorro().isEmpty()) {
                    cuentasTemp = new ArrayList<>();
                    for (com.bds.wpn.ws.services.CuentaDTO cuentaWs : clienteWs.getCuentasAhorro()) {
                        cuentaTemp = new CuentaDTO();
                        BeanUtils.copyProperties(cuentaTemp, cuentaWs);
                        cuentasTemp.add(cuentaTemp);
                    }
                    cliente.setCuentasAhorroDTO(cuentasTemp);
                }

                if (clienteWs.getCuentasMonedaExtranjera() != null && !clienteWs.getCuentasMonedaExtranjera().isEmpty()) {
                    cuentasTemp = new ArrayList<>();
                    for (com.bds.wpn.ws.services.CuentaDTO cuentaWs : clienteWs.getCuentasMonedaExtranjera()) {
                        cuentaTemp = new CuentaDTO();
                        BeanUtils.copyProperties(cuentaTemp, cuentaWs);
                        cuentasTemp.add(cuentaTemp);
                    }
                    cliente.setCuentasMonedaExtranjeraDTO(cuentasTemp);
                }
            }

        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio listCuentasActivasCanceladas: ")
                    .append("USR-").append(codigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN listCuentasActivasCanceladas: ")
                    .append("USR-").append(codigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            cliente.setRespuestaDTO(respuesta);
        }
        return cliente;
    }

    /**
     * Metodo para Valida cliente con Identificacion cliente y No. De cuenta
     *
     * @param identificacion identificacion del cliente se debe incluir letra
     * ej: v123456
     * @param nroCuenta numero de cuenta del cliente
     * @param idCanal id del canal
     * @param codigoCanal nombre del canal
     * @return retorna 'V' para casos validos, 'F' en caso contrario.
     */
    @Override
    public UtilDTO validarIdentificacionCuenta(String identificacion, String nroCuenta, String idCanal, String codigoCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            ClienteWs_Service service = new ClienteWs_Service(new URL(urlWsdlCliente));
            ClienteWs port = service.getClienteWsPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            utilWs = port.validarIdentificacionCuenta(identificacion, nroCuenta, codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put("resultado", (utilWs.getResulados().getEntry().get(0).getValue()));
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio validarClaveFondo: ")
                    .append("DOC-").append(identificacion)
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN validarClaveFondo: ")
                    .append("DOC-").append(identificacion)
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (util.getResuladosDTO() == null) {
                util.setResuladosDTO(new HashMap());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            util.setRespuestaDTO(respuesta);
        }
        return util;
    }

    /**
     * Metodo para validar y obtener un codigo de usuario existente por numero
     * de cedula
     *
     * @param identificacion identificacion del cliente se debe incluir letra
     * ej: v123456
     * @param codigoCanal nombre del canal
     * @param idCanal id del canal
     * @return en caso de existir retorna el parametro codUsuario en el mapa con
     * el valor correspondiente
     */
    @Override
    public UtilDTO obtenerCodCliente(String identificacion, String codigoCanal, String idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            ClienteWs_Service service = new ClienteWs_Service(new URL(urlWsdlCliente));
            ClienteWs port = service.getClienteWsPort();
            //se obtiene el objeto de salida del WS
            //para este caso como se trabaja con la data de oracle9 la clave se maneja vacia
            utilWs = port.obtenerCodCliente(identificacion, codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                util.setRespuestaDTO(respuesta);
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put("codCliente", (utilWs.getResulados().getEntry().get(0).getValue()));
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);
            }
        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN obtenerCodCliente: ")
                    .append("DOC-").append(identificacion)
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (util.getResuladosDTO() == null) {
                util.setResuladosDTO(new HashMap());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            util.setRespuestaDTO(respuesta);
        }
        return util;
    }

    /**
     * Retorna los datos basicos del cliente mediante el codigo y el canal
     *
     * @param iCodigoCliente String codigo del cliente
     * @param idCanal String con el id del canal
     * @param canal String canal por el cual se realiza la consulta
     * @return ClienteDTO datos del cliente en core bancario
     */
    @Override
    public ClienteDTO consultarDatosCliente(String iCodigoCliente, String idCanal, String canal) {
        ClienteDTO cliente = new ClienteDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS
            ClienteWs_Service service = new ClienteWs_Service(new URL(urlWsdlCliente));
            ClienteWs port = service.getClienteWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.ClienteDTO clienteWs = port.datosCliente(iCodigoCliente, canal, idCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, clienteWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(cliente, clienteWs);
            }

        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio consultarDatosCliente: ")
                    .append("USR-").append(iCodigoCliente)
                    .append("-CH-").append(canal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN consultarDatosCliente: ")
                    .append("USR-").append(iCodigoCliente)
                    .append("-CH-").append(canal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            cliente.setRespuestaDTO(respuesta);
        }
        return cliente;

    }

    /**
     * Metodo para validar el rif de un cliente
     *
     * @param rif rif 'v1234567890'
     * @param codigoCanal nombre del canal
     * @return retorna 'S' para casos validos, 'N' en caso contrario.
     */
    public UtilDTO validarRif(String rif, String codigoCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.UtilDTO utilWs;
        UtilDTO util = new UtilDTO();
        try {
            //invocacion del WS
            ClienteWs_Service service = new ClienteWs_Service(new URL(urlWsdlCliente));
            ClienteWs port = service.getClienteWsPort();
            //se obtiene el objeto de salida del WS
            utilWs = port.validarRif(rif, codigoCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, utilWs.getRespuesta());

            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                //cuando es un mapa debemos traer los valores uno a uno
                Map resultados = new HashMap();
                resultados.put("resultado", (utilWs.getResulados().getEntry().get(0).getValue()));
                //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
                util.setResuladosDTO(resultados);
            }

        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio validarRif: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN validarRif: ")
                    .append("-CH-").append(codigoCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if (util.getResuladosDTO() == null) {
                util.setResuladosDTO(new HashMap());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            util.setRespuestaDTO(respuesta);
        }
        return util;

    }

    @Override
    public ClienteDTO listadoCuentasCorrientesCliente(String codigoCliente, String idCanal, String nombreCanal) {
        ClienteDTO cliente = new ClienteDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //invocacion del WS
            CuentaWs_Service service = new CuentaWs_Service(new URL(urlWsdlCliente));
            CuentaWs port = service.getCuentaWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.ClienteDTO clienteWs = port.listadoCuentasCliente(codigoCliente, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, clienteWs.getRespuesta());
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(cliente, clienteWs);
                CuentaDTO cuentaTemp = null;
                List<CuentaDTO> cuentasTemp = null;

                //obtenemos las cuentas corrientes
                if (clienteWs.getCuentasCorriente() != null && !clienteWs.getCuentasCorriente().isEmpty()) {
                    cuentasTemp = new ArrayList<>();
                    for (com.bds.wpn.ws.services.CuentaDTO cuentaWs : clienteWs.getCuentasCorriente()) {
                        cuentaTemp = new CuentaDTO();
                        BeanUtils.copyProperties(cuentaTemp, cuentaWs);
                        cuentasTemp.add(cuentaTemp);
                    }
                    cliente.setCuentasCorrienteDTO(cuentasTemp);
                }

            }

        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio listadoCuentasClientes: ")
                    .append("USR-").append(codigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN listadoCuentasClientes: ")
                    .append("USR-").append(codigoCliente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            cliente.setRespuestaDTO(respuesta);
        }
        return cliente;
    }
}
