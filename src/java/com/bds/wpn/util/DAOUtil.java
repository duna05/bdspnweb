/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.util;

import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.DAOException;
import com.bds.wpn.exception.ServiceException;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import static com.bds.wpn.util.BDSUtil.FORMATO_FECHA_COMPLETA;
import com.bds.wpn.ws.services.NotificacionesWs;
import com.bds.wpn.ws.services.NotificacionesWs_Service;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.log4j.Logger;

/**
 * clase utilitaria para el manejo de las conexiones
 *
 * @author cesar.mujica
 */
public class DAOUtil extends BDSUtil {

    public CallableStatement statement = null;
    private Connection conn = null;
    private ResultSet objResultSet = null;
    public boolean ejecuto = false;    

    /**
     * Log de sistema
     */
    private static final Logger logger = Logger.getLogger(DAOUtil.class.getName());

    /**
     * Metodo que se encarga de crear una conexion en base a un String JNDI
     * correspondiente a un DataSource ubicado en el servidor
     *
     * @param String jndiConexion nombre de JNDI definido
     * @param int canal canla por el que se invoca al SP
     * @return RespuestaDTO clase que contiene el estatus de la operacion
     */
    public RespuestaDTO conectarJNDI(String jndiConexion, String canal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            InitialContext initContext = new InitialContext();
            DataSource ds = (DataSource) initContext.lookup(jndiConexion);
            this.conn = ds.getConnection();
        } catch (NamingException e) {
            logger.error(new StringBuilder("ERROR DAO EN conectarJNDI: ").append("JNDI-").append(jndiConexion)
                    .append("-CH-").append(canal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA).toString());
            respuesta.setCodigo("DAO001");//nombre de jndi no encontrado
        } catch (SQLException e) {
            logger.error(new StringBuilder("ERROR DAO EN conectarJNDI: ").append("JNDI-").append(jndiConexion)
                    .append("-CH-").append(canal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA).append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo("DAO002");//problema de conexion a BD
        }
        return respuesta;
    }

    /**
     * Metodo que se encarga de constuir y preparar el query para invocar un
     * store procedure de Oracle
     *
     * @param paquete String nombre del paquete de BD donde se ubica el query
     * @param nombreProcedimiento String nombre del procedimiento a llamar
     * @param cantParametros int cantidad de parametros que recibe el
     * procedimiento (de entrada y salida)
     * @param canal String canla por el que se invoca al SP
     * @return RespuestaDTO clase que contiene el estatus de la operacion
     */
    public RespuestaDTO generarQuery(String paquete, String nombreProcedimiento, int cantParametros, String canal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        //StringBuilder query = new StringBuilder("BEGIN ").append(paquete.trim()).append(".").append(nombreProcedimiento.trim());
        StringBuilder query = new StringBuilder("CALL ").append(paquete.trim()).append(".").append(nombreProcedimiento.trim());
        try {
            if (cantParametros == 0) {
                //query.append(" (); END; ");
                query.append(" () ");
            } else {
                query.append(" (");
                if (cantParametros < 0) {
                    throw new DAOException();
                } else {
                    int coma = 0;
                    for (int i = 0; i < cantParametros; i++) {
                        coma = i + 1;
                        query.append(" ?");
                        if (coma < cantParametros) {
                            query.append(",");
                        }
                    }
                    //query.append("); END;");
                    query.append(")");
                }
            }
            this.statement = conn.prepareCall(query.toString());
        } catch (DAOException e) {
            logger.error(new StringBuilder("ERROR DAO EN generarQuery: ").append("CANTPARAM-").append(cantParametros)
                    .append("-CH-").append(canal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA).toString());
            respuesta.setCodigo("DAO003");//cantidad de parametros no puede ser negativa
        } catch (SQLException e) {
            logger.error(new StringBuilder("ERROR DAO EN generarQuery: ").append("SP-").append(paquete).append(".").append(nombreProcedimiento)
                    .append("-CH-").append(canal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA).append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo("DAO004");//problema al generar el llamado a BD
        }
        return respuesta;
    }

    /**
     * Metodo que se encarga de cerrar todos los objetos de Oracle
     *
     * @param canal String
     * @return RespuestaDTO clase que contiene el estatus de la operacion
     */
    public RespuestaDTO cerrarConexion(String canal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            if (this.objResultSet != null) {
                this.objResultSet.close();
            }
            if (this.statement != null) {
                this.statement.close();
            }
            if (this.conn != null) {
                this.conn.close();
            }
        } catch (SQLException e) {
            logger.error(new StringBuilder("ERROR DAO EN cerrarConexion: ").append("-CH-").append(canal)
                    .append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA).append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo("DAO005");//problema al intentar cerrar las conexiones
        }
        return respuesta;
    }

    /**
     * Metodo que se encarga de hacer el encoding en Oracle9 para UTF-8
     *
     * @param rs ResultSet Set de datos
     * @param nombreCampo String nombre del campo
     * @return String valor String
     */
    public String getCharSetOracle9(ResultSet rs, String nombreCampo) throws Exception {
        if (rs.getBytes(nombreCampo) == null) {
            return "";
        } else {
            return new String(rs.getBytes(nombreCampo), CHARSET_ORACLE_9);
        }

    }

    /**
     * Metodo que se encarga de hacer el casting de sqlDate a utilDate
     *
     * @param rs ResultSet Set de datos
     * @param nombreCampo String nombre del campo
     * @return Date fecha convertida
     */
    public Date getUtilDate(ResultSet rs, String nombreCampo) throws Exception {
        return new Date((this.objResultSet.getDate(nombreCampo)).getTime());
    }

    /**
     * Metodo que se encarga de hacer el casting de String a sqlDate
     *
     * @param fecha String en formato String eje 31/12/2014
     * @param formato String en que se recibe la fecha eje dd/MM/yyyy
     * @return java.sql.Date fecha convertida
     */
    public java.sql.Date getSQLDate(String fecha, String formato) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(formato);
        Date parsed = format.parse(fecha);
        return new java.sql.Date(parsed.getTime());
    }

    /**
     * Procesa el envio de Email con los parametros especificados
     *
     * @param remitente String Remitente del correo.
     * @param destinatario String Destinatario del correo.
     * @param asunto String Asunto del correo.
     * @param texto String Texto del correo.
     * @param idCanal String indentificador del canal en oracle11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es
     * llamado el procedimiento.
     * @param urlServicios String url del servicio de notificaciones
     * @return EMailDTO el email enviado al cliente
     */
    public EMailDTO enviarEmailUtil(String remitente, String destinatario, String asunto, String texto, String idCanal, String nombreCanal, String urlServicios) {

        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.EMailDTO emailWs;
        EMailDTO emailDTO = new EMailDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

            //invocacion del WS
            NotificacionesWs_Service service = new NotificacionesWs_Service(new URL(urlServicios));//"http://localhost:7001/ibdsws/NotificacionesWs?WSDL"));//
            NotificacionesWs port = service.getNotificacionesWsPort();
            //se obtiene el objeto de salida del WS
            emailWs = port.enviarEmail(remitente, destinatario, asunto, texto, nombreCanal);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, emailWs.getRespuesta());
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            emailDTO.setRespuestaDTO(respuesta);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //obtenemos el resto de valores del objeto
                BeanUtils.copyProperties(emailDTO, emailWs);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio enviarEmailUtil: ")
                    .append("USR-").append(remitente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN enviarEmailUtil: ")
                    .append("USR-").append(remitente)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        }
        return emailDTO;

    }

    /**
     * Procesa el envio de SMS con los parametros especificados
     *
     * @param numeroTlf String Asunto del correo.
     * @param texto String Texto del correo.
     * @param idCanal String indentificador del canal en oracle11
     * @param nombreCanal String Codigo (extendido) del canal desde el cual es
     * llamado el procedimiento.
     * @param motivoSMS String justificacion del envio del SMS
     * @param urlServicios String notificaciones
     * @return EMailDTO el email enviado al cliente
     */
    public RespuestaDTO enviarSMSUtil(String numeroTlf, String texto, String idCanal, String nombreCanal, String motivoSMS, String urlServicios) {

        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            if (numeroTlf != null && !numeroTlf.trim().equalsIgnoreCase("")) {
                //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
                ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);

                //invocacion del WS
                NotificacionesWs_Service service = new NotificacionesWs_Service(new URL(urlServicios));//"http://localhost:7001/ibdsws/NotificacionesWs?WSDL"));//urlServicios));//
                NotificacionesWs port = service.getNotificacionesWsPort();
                //se obtiene el objeto de salida del WS
                respuestaWs = port.enviarSMS(numeroTlf, texto, idCanal);
                //clase para casteo dinamico de atributos
                BeanUtils.copyProperties(respuesta, respuestaWs);
                //validacion de codigo de respuesta
                if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    throw new ServiceException();
                }
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio enviarSMSUtil: ")
                    .append("TLF-").append(numeroTlf)
                    .append("-SMS-").append(motivoSMS)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN enviarSMSUtil: ")
                    .append("TLF-").append(numeroTlf)
                    .append("-SMS-").append(motivoSMS)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        }
        return respuesta;

    }

    /**
     * Metodo que se encarga de notificar a las areas de soporte de cualquier
     * error en la aplicacion
     *
     * @param trazaError string co la traza de error de la excepcion
     * @param idCanal String identificador del canal
     * @param nombreCanal String nombre del canal
     * @param urlServicios String wsdl de notificaciones
     * @param emailSoporte String correo de soporte de BDS
     * @param emailBody String cuerpo para el email
     * @param telfSoporte String telefonos de soporte de BDS
     * @param smsBody String cuerpo de SMS motivo del envio
     */
    public void notificarErrores(Throwable trazaError, String idCanal, String nombreCanal, String urlServicios, String emailSoporte, String emailBody, String telfSoporte, String smsBody) {
        String textoError = "Causa del Error: " + trazaError.toString() + "\n \n Linea del Error: " + trazaError.getStackTrace()[0].toString() + "\n \n Fecha del Error: " + this.formatearFecha(new Date(), FORMATO_FECHA_COMPLETA);
        //validamos si existe algun correo de soporte        
        if (emailSoporte != null && !emailSoporte.trim().equalsIgnoreCase("")) {
            //de ser asi notificamos al grupo configurado
            enviarEmailUtil(emailSoporte, emailSoporte, emailBody, textoError, idCanal, nombreCanal, urlServicios);
            //textosController.getNombreTexto("notificacion.error.email.titulo", idCanal)                       
        }
        //validamos si existn numeros de telf para notificaciones via SMS
        if (telfSoporte != null && !telfSoporte.trim().equalsIgnoreCase("")) {
            //los telefonos vienen separados por el caracter "/"
            String[] telefonos = telfSoporte.split("/");
            if (telefonos.length > 0) {
                for (int i = 0; i < telefonos.length; i++) {
                    if (!telefonos[i].trim().equalsIgnoreCase("")) {
                        enviarSMSUtil(telefonos[i], smsBody + " " + this.formatearFecha(new Date(), FORMATO_FECHA_COMPLETA), idCanal, nombreCanal, smsBody, urlServicios);
                    }
                }
            }
        }
    }

    /**
     * Metodo que recibe un objeto throwable y retorna el stack trace como
     * string
     *
     * @param t Throwable objeto arrojable
     * @return String
     */
    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

}
