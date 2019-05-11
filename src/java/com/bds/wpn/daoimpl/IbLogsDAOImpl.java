/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbLogsDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.IbCanalDTO;
import com.bds.wpn.dto.IbLogsDTO;
import com.bds.wpn.dto.IbTransaccionesDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbLogsWS;
import com.bds.wpn.ws.services.IbLogsWS_Service;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.log4j.Logger;

/**
 * Clase que implementa la interfaz IbLogsDAO
 *
 * @author juan.faneite
 */
@Named
@Stateless(name = "wpnIbLogsDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbLogsDAOImpl extends DAOUtil implements IbLogsDAO {

    /**
     * Log de sistema
     */
    private static final Logger logger = Logger.getLogger(IbLogsDAOImpl.class.getName());

    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlLogs = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbLogsWS?WSDL";

    /**
     * Metodo para obtener el listado de moviemientos del log del usuario
     *
     * @author wilmer.rondon
     * @param idUsuario id del usuario
     * @param fechaDesde fecha desde si viene null se tomaran los ultimos
     * registros
     * @param fechaHasta fecha hasta si viene null se tomaran los ultimos
     * registros
     * @param nroMesesAtras cantidad de meses atras a colsultar
     * @param idTransaccion número de la transacción por la que se realizará el
     * filtro
     * @param idCanal id del canal a filtrar
     * @param nombreCanal nombre del canal
     * @return IbLogsDTO -> List<IbLogs>
     */
    @Override
    public IbLogsDTO listadoHistoricoCliente(String idUsuario, String fechaDesde, String fechaHasta, int nroMesesAtras, String idTransaccion, String idCanal, String nombreCanal) {

        IbLogsDTO ibLogsDTO = new IbLogsDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        IbCanalDTO canal;
        IbTransaccionesDTO transaccion;
        try {

            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //se agrega esta valiadacion para el casteo correcto de los BigInteger que vengan nulos
            ConvertUtils.register(new BigIntegerConverter(null), BigInteger.class);
            IbLogsWS_Service service = new IbLogsWS_Service(new URL(urlWsdlLogs));
            IbLogsWS port = service.getIbLogsWSPort();
            com.bds.wpn.ws.services.IbLogsDTO logsWs = port.listadoHistoricoCliente(idUsuario, fechaDesde, fechaHasta, nroMesesAtras, idTransaccion, idCanal, nombreCanal);
            BeanUtils.copyProperties(respuesta, logsWs.getRespuesta());

            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {

                List<IbLogsDTO> listLogsDto = new ArrayList<>();
                for (com.bds.wpn.ws.services.IbLogs log : logsWs.getLogs()) {
                    transaccion = new IbTransaccionesDTO();
                    canal = new IbCanalDTO();
                    IbLogsDTO logsTemp = new IbLogsDTO();
                    BeanUtils.copyProperties(logsTemp, log);
                    if (log.getFechaHora() != null) {
                        logsTemp.setFechaHoraDate(log.getFechaHora().toGregorianCalendar().getTime());
                    }
                    if (log.getFechaHoraJob() != null) {
                        logsTemp.setFechaHoraJobDate(log.getFechaHoraJob().toGregorianCalendar().getTime());
                    }
                    if (log.getFechaHoraTx() != null) {
                        logsTemp.setFechaHoraTxDate(log.getFechaHoraTx().toGregorianCalendar().getTime());
                    }

                    BeanUtils.copyProperties(canal, log.getIdCanal());
                    logsTemp.setIdCanalDTO(canal);
                     
                    listLogsDto.add(logsTemp);
                }

                ibLogsDTO.setIbLogsDTO(listLogsDto);
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listadoHistoricoCliente: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN listadoHistoricoCliente: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            ibLogsDTO.setRespuestaDTO(respuesta);
        }

        return ibLogsDTO;
    }
}
