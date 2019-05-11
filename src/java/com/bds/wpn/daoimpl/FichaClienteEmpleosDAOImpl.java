/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.dao.FichaClienteEmpleosDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.FichaClienteEmpleosDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.FichaClienteEmpleosWs;
import com.bds.wpn.ws.services.FichaClienteEmpleosWs_Service;
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
 * @author humberto.rojas
 */
@Named
@Stateless(name = "wpnFichaClienteEmpleosDAOImpl")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteEmpleosDAOImpl extends DAOUtil implements FichaClienteEmpleosDAO{
    
    private static final Logger logger = Logger.getLogger(FichaClienteEmpleosDAOImpl.class.getName());    
    private final String urlWsdlFichaClienteEmpleos = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteEmpleosWs?wsdl";
    
    
    
    @Override
    public FichaClienteEmpleosDTO consultarEmpleos(String iCodigoCliente) {
        FichaClienteEmpleosDTO empleos = null;
        RespuestaDTO respuesta = new RespuestaDTO();
        try {            
           ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);                      
           FichaClienteEmpleosWs_Service service = new FichaClienteEmpleosWs_Service(new URL(urlWsdlFichaClienteEmpleos));
           FichaClienteEmpleosWs port = service.getFichaClienteEmpleosWsPort();
           com.bds.wpn.ws.services.FichaClienteEmpleosDTO fichaClienteEmpleosWs = port.consultarEmpleos(iCodigoCliente);
           BeanUtils.copyProperties(respuesta, fichaClienteEmpleosWs.getRespuesta());
           List<FichaClienteEmpleosDTO> listEmpleos = new ArrayList<>();
            for (com.bds.wpn.ws.services.FichaClienteEmpleosDTO empleo : fichaClienteEmpleosWs.getIbEmpleoList()) {
                       empleos = new FichaClienteEmpleosDTO();
                       empleos.setNombreEmpresa(empleo.getNombreEmpresa());
                       empleos.setCargo(empleo.getCargo());
                       empleos.setUltimoSueldo(empleo.getUltimoSueldo());
                       empleos.setFechaEntrada(empleo.getFechaEntrada());
                       empleos.setRif(empleo.getRif());
                       empleos.setRamo(empleo.getRamo());
                       empleos.setDireccion(empleo.getDireccion());
                       empleos.setTelefono(empleo.getTelefono());
                       listEmpleos.add(empleos);
            }
             if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {   
               empleos.setIbEmpleoList(listEmpleos);                             
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
            empleos.setRespuesta(respuesta);
        }
        return empleos;
    }
    
    
}
