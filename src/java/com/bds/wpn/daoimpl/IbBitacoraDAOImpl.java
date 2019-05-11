/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.InicioSesionController;
import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbBitacoraDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbBitacoraWS;
import com.bds.wpn.ws.services.IbBitacoraWS_Service;
import java.net.URL;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author rony.rodriguez
 */
@Named
@Stateless(name = "wpnIbBitacoraDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbBitacoraDAOImpl extends DAOUtil implements IbBitacoraDAO {

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(IbBitacoraDAOImpl.class.getName());

    private final String urlWsdlBitacora = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbBitacoraWS?WSDL";

    //Registro de bitacora Todos los campos son obligatorios.
    /**
     * Registro de bitacora Todos los campos son obligatorios.
     *
     * @param cuentaOrigen String Numero de cuenta origen
     * @param cuentaDestino String Numero de cuenta/TDC/Servicio destino
     * @param monto String Monto de la transaccion
     * @param referencia String Numero de referencia de la transaccion
     * @param descripcion String Descripcion dada por el usuario a la
     * transaccion
     * @param ip String Direccion ip del usuario
     * @param userAgent String Identificacion del navegador utilizado por el
     * usuario
     * @param errorOperacion String Canal de ejecucion de la transaccion
     * @param nombreBeneficiario String Nombre del beneficiario
     * @param tipoRif String Tipo de RIF de una empresa
     * @param rifCedula String Numero de RIF de una empresa
     * @param fechaHoraTx String Fecha y hora para ejecutar la transaccion
     * @param fechaHoraJob String Fecha y hora de ejecucion del job
     * @param idUsuario String Referencia foranea al usuario creador de la
     * transaccion
     * @param idTransaccion String Identificador de la transaccion realizada
     * @param idAfiliacion String Referencia foranea al beneficiario
     * @param canal String Canal por el cual se ejecuta la transaccion.
     * @param idCanal String idCanal por el se ejecuta la transaccion.
     * @return RespuestaDTO
     */
    @Override
    public RespuestaDTO registroDeBitacora(String cuentaOrigen, String cuentaDestino, String monto, String referencia, String descripcion, String ip,
            String userAgent, String errorOperacion, String nombreBeneficiario, String tipoRif, String rifCedula,
            String fechaHoraTx, String fechaHoraJob, String idCanal, String canal, String idUsuario, String idTransaccion, String idAfiliacion) {

        RespuestaDTO respuesta = new RespuestaDTO();
        com.bds.wpn.ws.services.RespuestaDTO respuestaWs;
        try {
            //invocacion del WS 
            IbBitacoraWS_Service service = new IbBitacoraWS_Service(new URL(urlWsdlBitacora));

            IbBitacoraWS port = service.getIbBitacoraWSPort();
            //se obtiene el objeto de salida del WS  
            respuestaWs = port.registroDeBitacora(
                    cuentaOrigen, cuentaDestino, monto, referencia, descripcion, ip,
                    userAgent, errorOperacion, nombreBeneficiario, tipoRif, rifCedula,
                    fechaHoraTx, fechaHoraJob, idCanal, canal, idUsuario, idTransaccion, idAfiliacion);

            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, respuestaWs);
            //validacion de codigo de respuesta
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio registroDeBitacora: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(canal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN registroDeBitacora: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(canal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }
        return respuesta;
    }

}
