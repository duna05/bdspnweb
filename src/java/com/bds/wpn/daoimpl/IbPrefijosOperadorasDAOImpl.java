/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbPrefijosOperadorasDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.IbPrefijosOperadorasDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbPrefijosOperadorasWS;
import com.bds.wpn.ws.services.IbPrefijosOperadorasWS_Service;
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
 * Clase que implementa IbPrefijosOperadorasDAO
 * @author juan.faneite
 */
@Named
@Stateless(name = "wpnIbPrefijosOperadorasDAO")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbPrefijosOperadorasDAOImpl extends DAOUtil implements IbPrefijosOperadorasDAO{
    
    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlPrefijosOperadoras = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbPrefijosOperadorasWS?WSDL";
    
    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(IbPrefijosOperadorasDAOImpl.class.getName());
    
    /**
     * Metodo para obtener el listado de prefijos de operadoras
     * @param nombreCanal nombre del canal
     * @return IbPrefijosOperadorasDTO Listado de prefijos de operadoras
     */
    @Override
    public IbPrefijosOperadorasDTO listaPrefijosOperadoras(String nombreCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        IbPrefijosOperadorasDTO ibPrefijosOperadorasDTO = new IbPrefijosOperadorasDTO();

        try {

            IbPrefijosOperadorasWS_Service service = new IbPrefijosOperadorasWS_Service(new URL(urlWsdlPrefijosOperadoras));
            IbPrefijosOperadorasWS port = service.getIbPrefijosOperadorasWSPort();
            com.bds.wpn.ws.services.IbPrefijosOperadorasDTO ibPrefijosOperadorasDTOWs = port.listaPrefijosOperadoras(nombreCanal);
            BeanUtils.copyProperties(respuesta, ibPrefijosOperadorasDTOWs.getRespuesta());
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            }
            
            List<IbPrefijosOperadorasDTO> listPrefijosOperadorasDTO = new ArrayList<>();
                for (com.bds.wpn.ws.services.IbPrefijosOperadoras prefijos : ibPrefijosOperadorasDTOWs.getPrefijosOperadoras()) {
                    IbPrefijosOperadorasDTO prefijosOperadorasTemp = new IbPrefijosOperadorasDTO();
                    BeanUtils.copyProperties(prefijosOperadorasTemp, prefijos);
                    listPrefijosOperadorasDTO.add(prefijosOperadorasTemp);
                }
                ibPrefijosOperadorasDTO.setIbPrefijosOperadorasDTO(listPrefijosOperadorasDTO);

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listaPrefijosOperadoras: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio listaPrefijosOperadoras: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            if(ibPrefijosOperadorasDTO.getIbPrefijosOperadorasDTO() == null){
                ibPrefijosOperadorasDTO.setIbPrefijosOperadorasDTO(new ArrayList<IbPrefijosOperadorasDTO> ());
            }
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            ibPrefijosOperadorasDTO.setRespuestaDTO(respuesta);
        }
        //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
        return ibPrefijosOperadorasDTO;
    }
    
}
