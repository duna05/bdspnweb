/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.dao.FichaClienteRefPersonalDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.FichaClienteRefPersonalDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.ClasificacionWs_Service;
import com.bds.wpn.ws.services.FichaClienteRefPersonalWs;
import com.bds.wpn.ws.services.FichaClienteRefPersonalWs_Service;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author alejandro.flores
 */
@Named
@Stateless(name = "wpnFichaClienteRefPersonalDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteRefPersonalDAOImpl extends DAOUtil implements FichaClienteRefPersonalDAO{
    
    private static final Logger logger = Logger.getLogger(ClasificacionDAOImpl.class.getName());
    private final String urlWsdlFichaClienteRefPersonal = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "//FichaClienteRefPersonalWs?wsdl";
    //private final String urlWsdlFichaClienteRefPersonal = "http://localhost:7001/ibdsws/FichaClienteRefPersonalWs?wsdl";

    @Override
    public FichaClienteRefPersonalDTO consultarRefPersonal(String iCodigoCliente) {
        FichaClienteRefPersonalDTO fichaClienteRefPersonalDTO = new FichaClienteRefPersonalDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
       
  
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            FichaClienteRefPersonalWs_Service service = new FichaClienteRefPersonalWs_Service(new URL(urlWsdlFichaClienteRefPersonal));
            FichaClienteRefPersonalWs port = service.getFichaClienteRefPersonalWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.FichaClienteRefPersonalDTO fichaClienteRefPersonalWs = port.consultarRefPersonal(iCodigoCliente);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, fichaClienteRefPersonalWs.getRespuesta());

            List<FichaClienteRefPersonalDTO> listFichaClienteRefPersonalDTO = new ArrayList<>();
            List<String> listFichaClienteRefPersonal = new ArrayList<>();
           
            for (com.bds.wpn.ws.services.FichaClienteRefPersonalDTO ficha : fichaClienteRefPersonalWs.getIbFichaClienteRefPersonalList()) {
                    fichaClienteRefPersonalDTO = new FichaClienteRefPersonalDTO();
                    fichaClienteRefPersonalDTO.setNombre(ficha.getNombre());
                    fichaClienteRefPersonalDTO.setParentesco(ficha.getParentesco());
                    fichaClienteRefPersonalDTO.setNumeroIdentificacion(ficha.getNumeroIdentificacion());
                    fichaClienteRefPersonalDTO.setPrimerApellido(ficha.getPrimerApellido());
                    fichaClienteRefPersonalDTO.setSegundoApellido(ficha.getSegundoApellido());
                    fichaClienteRefPersonalDTO.setTelefono(ficha.getTelefono());
                    fichaClienteRefPersonalDTO.setTelefono2(ficha.getTelefono2());
                    listFichaClienteRefPersonalDTO.add(fichaClienteRefPersonalDTO);
            }

            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                  fichaClienteRefPersonalDTO.setIbFichaClienteRefPersonalList(listFichaClienteRefPersonalDTO);
            }

        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio consultarDatosCliente: ")
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN consultarDatosCliente: ")
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            fichaClienteRefPersonalDTO.setRespuesta(respuesta);
        }
        
        return fichaClienteRefPersonalDTO;
    }
    
}
