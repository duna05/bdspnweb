/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dao.SectorDAO;
import com.bds.wpn.dto.MunicipioDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.SectorDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.ClasificacionWs;
import com.bds.wpn.ws.services.ClasificacionWs_Service;
import com.bds.wpn.ws.services.MunicipioWs;
import com.bds.wpn.ws.services.MunicipioWs_Service;
import com.bds.wpn.ws.services.SectorWs;
import com.bds.wpn.ws.services.SectorWs_Service;
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
@Stateless(name = "wpnSectorDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class SectorDAOImpl extends DAOUtil implements SectorDAO{
    
    private static final Logger logger = Logger.getLogger(SectorDAOImpl.class.getName());
     private final String urlWsdlSector = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/SectorWs?wsdl";
     //private final String urlWsdlSector = "http://localhost:7001/ibdsws/SectorWs?wsdl";

    @Override
    public SectorDTO consultarSector() {
      SectorDTO  sectorDTO = new SectorDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            SectorWs_Service service = new SectorWs_Service(new URL(urlWsdlSector));
            SectorWs port = service.getSectorWsPort();
            //se obtiene el objeto de salida del WS
           com.bds.wpn.ws.services.SectorDTO sectorWs = port.consultarSector();     
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, sectorWs.getRespuesta());
            
            
            List<SectorDTO> listSectorDTO = new ArrayList<>();
            List<String> listaSector = new ArrayList<>();
                
          
            for (com.bds.wpn.ws.services.SectorDTO sectorD : sectorWs.getIbSectorList()) {
                   listaSector.add(sectorD.getCodigoSector()+ ":" +sectorD.getDescripcion());
            }
           
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {  
                sectorDTO.setIbSectorList(listaSector);
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
             sectorDTO.setRespuesta(respuesta);
        }
        return sectorDTO; }
    
}
