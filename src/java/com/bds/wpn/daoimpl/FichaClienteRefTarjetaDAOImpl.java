/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.dao.FichaClienteRefTarjetaDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.FichaClienteRefTarjetaDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.FichaClienteRefTarjetaWs;
import com.bds.wpn.ws.services.FichaClienteRefTarjetaWs_Service;
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
@Stateless(name = "wpnFichaClienteRefTarjetaDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteRefTarjetaDAOImpl extends DAOUtil implements FichaClienteRefTarjetaDAO{
    
    private static final Logger logger = Logger.getLogger(ClasificacionDAOImpl.class.getName());
    private final String urlWsdlFichaClienteRefTarjeta = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteRefTarjetaWs?wsdl";
    //private final String urlWsdlFichaClienteRefTarjeta = "http://localhost:7001/ibdsws/http://localhost:7001/ibdsws/FichaClienteRefTarjetaWs?wsdl";

    @Override
    public FichaClienteRefTarjetaDTO consultarRefTarjeta(String iCodigoCliente) {
        //FichaClienteRefTarjetaDTO fichaClienteRefTarjetaDTO = new FichaClienteRefTarjetaDTO();
        FichaClienteRefTarjetaDTO referencias = null;
        RespuestaDTO respuesta = new RespuestaDTO();
       
  
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            FichaClienteRefTarjetaWs_Service service = new FichaClienteRefTarjetaWs_Service(new URL(urlWsdlFichaClienteRefTarjeta));
            FichaClienteRefTarjetaWs port = service.getFichaClienteRefTarjetaWsPort();
            //se obtiene el objeto de salida del WS
            com.bds.wpn.ws.services.FichaClienteRefTarjetaDTO fichaClienteRefTarjetaWs = port.consultarRefTarjeta(iCodigoCliente);
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, fichaClienteRefTarjetaWs.getRespuesta());

            List<FichaClienteRefTarjetaDTO> listRefTarjeta = new ArrayList<>();
            //List<String> listFichaClienteRefTarjeta = new ArrayList<>();
           
            for (com.bds.wpn.ws.services.FichaClienteRefTarjetaDTO ficha : fichaClienteRefTarjetaWs.getIbFichaClienteRefTarjetaList()) {
                    referencias = new FichaClienteRefTarjetaDTO();
                    //fichaClienteRefTarjetaDTO.setCodBancoTarjeta(ficha.getCodBancoTarjeta());
                    //fichaClienteRefTarjetaDTO.setCodTipoTarjeta(ficha.getCodTipoTarjeta());
                    
                    referencias.setNomBancoTarjeta(ficha.getNomBancoTarjeta());
                    referencias.setDesTipoTarjeta(ficha.getDesTipoTarjeta());
                    referencias.setNumeroTarjeta(ficha.getNumeroTarjeta());
                    referencias.setFechaEmision(ficha.getFechaEmision());
                    referencias.setLimiteTarjeta(ficha.getLimiteTarjeta());
                    
                    
                    listRefTarjeta.add(referencias);
            }

            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {   
                  //fichaClienteRefTarjetaDTO.setIbFichaClienteRefTarjetaList(listFichaClienteRefTarjetaDTO);
                  referencias.setIbFichaClienteRefTarjetaList(listRefTarjeta);
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
            referencias.setRespuesta(respuesta);
        }
        
        return referencias;    }
    
}
