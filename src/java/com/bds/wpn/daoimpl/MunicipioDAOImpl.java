/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;
import com.bds.wpn.dao.MunicipioDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.MunicipioDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.ClasificacionWs;
import com.bds.wpn.ws.services.ClasificacionWs_Service;
import com.bds.wpn.ws.services.MunicipioWs;
import com.bds.wpn.ws.services.MunicipioWs_Service;
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
@Stateless(name = "wpnMunicipioDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class MunicipioDAOImpl extends DAOUtil implements MunicipioDAO{
    
    private static final Logger logger = Logger.getLogger(MunicipioDAOImpl.class.getName());
    private final String urlWsdlMunicipio = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/MunicipioWs?wsdl";
    // private final String urlWsdlMunicipio = "http://localhost:7001/ibdsws/MunicipioWs?wsdl";


    @Override
    public MunicipioDTO consultarMunicipio() {
       MunicipioDTO  municipioDTO = new MunicipioDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            MunicipioWs_Service service = new MunicipioWs_Service(new URL(urlWsdlMunicipio));
            MunicipioWs port = service.getMunicipioWsPort();
            //se obtiene el objeto de salida del WS
           com.bds.wpn.ws.services.MunicipioDTO municipioWs = port.consultarMunicipio();     
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, municipioWs.getRespuesta());
            
            
            List<MunicipioDTO> listMunicipioDTO = new ArrayList<>();
            List<String> listaMunicipioDTO = new ArrayList<>();
                
          
            for (com.bds.wpn.ws.services.MunicipioDTO municipioD : municipioWs.getIbMunicipioList()) {
                   listaMunicipioDTO.add(municipioD.getCodigoMunicipio()+ ":" +municipioD.getNombre());
            }
           
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {  
                municipioDTO.setIbMunicipioList(listaMunicipioDTO);
            }

        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio consultarDatosCliente: ")
                   
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN consultarDatosCliente: ")
                   
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            municipioDTO.setRespuesta(respuesta);
        }
        return municipioDTO; }
    
}
