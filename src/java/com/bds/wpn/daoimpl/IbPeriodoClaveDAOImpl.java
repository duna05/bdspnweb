/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbPeriodoClaveDAO;
import com.bds.wpn.dto.IbPeriodoClaveDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbPeriodoClaveWS;
import com.bds.wpn.ws.services.IbPeriodoClaveWS_Service;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

/**
 * Clase que implementa la interfaz IbPeriodoClaveDAO
 * @author wilmer.rondon
 */
@Named
@Stateless(name = "wpnIbPeriodoClaveDAO")
@com.bds.wpn.dao.NotificationServiceType(com.bds.wpn.dao.NotificationServiceType.ServiceType.SERVICES)
public class IbPeriodoClaveDAOImpl extends DAOUtil implements IbPeriodoClaveDAO{

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(IbPeriodoClaveDAOImpl.class.getName());
    
    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;
    
    private final String urlWsdlPeriodoClave = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbPeriodoClaveWS?WSDL";
    
    /**
     * Metodo que obtiene el listado de periodos de clave
     * @param nombreCanal nombre del canal
     * @return IbPeriodoClaveDTO listado de claves
     */
    @Override
    public IbPeriodoClaveDTO listaPeriodoClave(String nombreCanal) {

        RespuestaDTO respuesta = new RespuestaDTO();
        IbPeriodoClaveDTO ibPeriodoClaveDTO = new IbPeriodoClaveDTO();

        try {

            IbPeriodoClaveWS_Service service = new IbPeriodoClaveWS_Service(new URL(urlWsdlPeriodoClave));
            IbPeriodoClaveWS port = service.getIbPeriodoClaveWSPort();
            com.bds.wpn.ws.services.IbPeriodoClaveDTO ibPeriodoClaveDTOWs = port.listaPeriodoClave(nombreCanal);
            BeanUtils.copyProperties(respuesta, ibPeriodoClaveDTOWs.getRespuesta());
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }
            
            List<IbPeriodoClaveDTO> listPeriodoClaveDTO = new ArrayList<>();
                for (com.bds.wpn.ws.services.IbPeriodoClave perido : ibPeriodoClaveDTOWs.getPeriodosClave()) {
                    IbPeriodoClaveDTO periodoClaveTemp = new IbPeriodoClaveDTO();
                    BeanUtils.copyProperties(periodoClaveTemp, perido);
                    listPeriodoClaveDTO.add(periodoClaveTemp);
                }
                ibPeriodoClaveDTO.setIbPeriodoClaveDTO(listPeriodoClaveDTO);

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listaPeriodoClave: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listaPeriodoClave: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if(ibPeriodoClaveDTO.getIbPeriodoClaveDTO() == null){
                ibPeriodoClaveDTO.setIbPeriodoClaveDTO(new ArrayList<IbPeriodoClaveDTO>());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            ibPeriodoClaveDTO.setRespuestaDTO(respuesta);
        }
        //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
        return ibPeriodoClaveDTO;
    }
    
}
