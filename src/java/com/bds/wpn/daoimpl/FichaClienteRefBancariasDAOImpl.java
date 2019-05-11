/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;


import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.FichaClienteRefBancariasDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.FichaClienteRefBancariasWs;
import com.bds.wpn.ws.services.FichaClienteRefBancariasWs_Service;
import com.bds.wpn.dao.FichaClienteRefBancariasDAO;
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
@Stateless(name = "wpnFichaClienteRefBancariasDAOImpl")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteRefBancariasDAOImpl extends DAOUtil implements FichaClienteRefBancariasDAO{
    
     private static final Logger logger = Logger.getLogger(FichaClienteRefBancariasDAOImpl.class.getName());

    
     private final String urlWsdlFichaClienteRefBancarias = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteRefBancariasWs?wsdl";
     

    @Override
    public FichaClienteRefBancariasDTO consultarRefBancarias(String iCodigoCliente) {
        FichaClienteRefBancariasDTO referencias = null;
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            FichaClienteRefBancariasWs_Service service = new FichaClienteRefBancariasWs_Service(new URL(urlWsdlFichaClienteRefBancarias));
            FichaClienteRefBancariasWs port = service.getFichaClienteRefBancariasWsPort();
            //se obtiene el objeto de salida del WS
           com.bds.wpn.ws.services.FichaClienteRefBancariasDTO fichaClienteRefBancariasWs = port.consultarRefBancarias(iCodigoCliente);
           
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, fichaClienteRefBancariasWs.getRespuesta());
            
            
            List<FichaClienteRefBancariasDTO> listRefBancaria = new ArrayList<>();
            
            /*List<List<String>> listReferenciaBancaria = new ArrayList<List<String>>();
           
            for(int i =0; i<= 5; i++){
                    listReferenciaBancaria.add(new ArrayList<String>());
            }*/
          
            for (com.bds.wpn.ws.services.FichaClienteRefBancariasDTO refBancaria : fichaClienteRefBancariasWs.getIbReferenciaList()) {
                      referencias = new FichaClienteRefBancariasDTO();
                      
                      referencias.setCodigoFinanciera(refBancaria.getCodigoFinanciera());
                      referencias.setFechaApertura(refBancaria.getFechaApertura());
                      referencias.setNumeroCuentaReferencia(refBancaria.getNumeroCuentaReferencia());
                      referencias.setTipoCuentaFinanciera(refBancaria.getTipoCuentaFinanciera());
                      referencias.setTipoFinanciera(refBancaria.getTipoFinanciera());
                      referencias.setNombreBanco(refBancaria.getNombreBanco());
                      referencias.setDescCuenta(refBancaria.getDescCuenta());
                      referencias.setSaldoPromedio(refBancaria.getSaldoPromedio());
                    listRefBancaria.add(referencias);
            }
             if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {               
               referencias.setIbReferenciaList(listRefBancaria);                
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
            referencias.setRespuesta(respuesta);
        }
        return referencias;
    }
    
}
