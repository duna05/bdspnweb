/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.dao.FichaClienteComplementarioDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dao.NotificationServiceType.ServiceType;
import com.bds.wpn.dto.FichaClienteComplementarioDTO;
import com.bds.wpn.util.DAOUtil;
import javax.ejb.Stateless;
import javax.inject.Named;
import org.apache.log4j.Logger;
import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.controller.TextosController;
import com.bds.wpn.dao.FichaClienteDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.FichaClienteDTO;
import com.bds.wpn.dto.PrestamoDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.SubtipoClienteDTO;
import com.bds.wpn.ws.services.FichaClienteComplementarioWs;
import com.bds.wpn.ws.services.FichaClienteComplementarioWs_Service;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.log4j.Logger;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import com.bds.wpn.ws.services.FichaClienteWs;
import com.bds.wpn.ws.services.FichaClienteWs_Service;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author alejandro.flores
 */
@Named
@Stateless(name = "wpnFichaClienteComplementarioDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class FichaClienteComplementarioDAOImpl extends DAOUtil implements FichaClienteComplementarioDAO{
    
    private static final Logger logger = Logger.getLogger(FichaClienteComplementarioDAOImpl.class.getName());
    private final String urlWsdlFichaClienteComplementario = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/FichaClienteComplementarioWs?wsdl";
   
    @Override
    public FichaClienteComplementarioDTO consultarDatosComplementariosCliente(String iCodigoCliente) {
     FichaClienteComplementarioDTO fichaClienteComplementarioDTO = new FichaClienteComplementarioDTO();
            RespuestaDTO respuesta = new RespuestaDTO();
            try {
            //se agrega esta valiadacion para el casteo correcto de los bigDecimal que vengan nulos
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            //invocacion del WS            
            FichaClienteComplementarioWs_Service service = new FichaClienteComplementarioWs_Service(new URL(urlWsdlFichaClienteComplementario));
            FichaClienteComplementarioWs port = service.getFichaClienteComplementarioWsPort();
            //se obtiene el objeto de salida del WS
           com.bds.wpn.ws.services.FichaClienteComplementarioDTO FichaclienteComplementarioWs = port.consultarDatosComplementariosCliente(iCodigoCliente);     
            //clase para casteo dinamico de atributos
            BeanUtils.copyProperties(respuesta, FichaclienteComplementarioWs.getRespuesta());
            
            //List<FichaClienteComplementarioDTO> listFichaClienteComplementario = new ArrayList<>();
            
                fichaClienteComplementarioDTO.setCantidadEmpleados(FichaclienteComplementarioWs.getCantidadEmpleados());
                fichaClienteComplementarioDTO.setCantidadSucursales(FichaclienteComplementarioWs.getCantidadSucursales());
                fichaClienteComplementarioDTO.setTipoCompania(FichaclienteComplementarioWs.getTipoCompania());
                fichaClienteComplementarioDTO.setDep_efect(FichaclienteComplementarioWs.getDepEfect());
                fichaClienteComplementarioDTO.setDep_cheq_ger(FichaclienteComplementarioWs.getDepCheqGer());
                fichaClienteComplementarioDTO.setDep_transf(FichaclienteComplementarioWs.getDepTransf());
                fichaClienteComplementarioDTO.setDep_cheq_per(FichaclienteComplementarioWs.getDepCheqPer());
                fichaClienteComplementarioDTO.setRet_cheq_ger(FichaclienteComplementarioWs.getRetCheqGer());
                fichaClienteComplementarioDTO.setRet_cheq_per(FichaclienteComplementarioWs.getRetCheqPer());
                fichaClienteComplementarioDTO.setRet_efec(FichaclienteComplementarioWs.getRetEfec());
                fichaClienteComplementarioDTO.setRet_transf(FichaclienteComplementarioWs.getRetTransf());
                fichaClienteComplementarioDTO.setSegmento(FichaclienteComplementarioWs.getSegmento());
                fichaClienteComplementarioDTO.setTiposCalificacion(FichaclienteComplementarioWs.getTiposCalificacion());
                fichaClienteComplementarioDTO.setValorAumentoCapital(FichaclienteComplementarioWs.getValorAumentoCapital());
                fichaClienteComplementarioDTO.setTomo(FichaclienteComplementarioWs.getTomo());
                fichaClienteComplementarioDTO.setPais_recibir(FichaclienteComplementarioWs.getPaisRecibir());
                fichaClienteComplementarioDTO.setPais_enviar(FichaclienteComplementarioWs.getPaisEnviar());
                fichaClienteComplementarioDTO.setComentariosOtros(FichaclienteComplementarioWs.getComentariosOtros());
                fichaClienteComplementarioDTO.setMov_dep(FichaclienteComplementarioWs.getMovDep());
                fichaClienteComplementarioDTO.setMov_ret(FichaclienteComplementarioWs.getMovRet());
                fichaClienteComplementarioDTO.setNombreEstado(FichaclienteComplementarioWs.getNombreEstado());
                fichaClienteComplementarioDTO.setNombreMunicipio(FichaclienteComplementarioWs.getNombreMunicipio());
                String usoOtros = FichaclienteComplementarioWs.getUsoOtros().toString();
                String usoAhorro = FichaclienteComplementarioWs.getUsoCtaAhorro().toString();
                String usoTransferencia = FichaclienteComplementarioWs.getUsoTransferencia().toString();
                String usoPrestamo = FichaclienteComplementarioWs.getUsoCtaPrestamo().toString();
                String usoDivisa = FichaclienteComplementarioWs.getUsoDivisa().toString();
                if (usoOtros.equals("S")){
                   fichaClienteComplementarioDTO.setUso_otros("TRUE"); 
                }else{
                   fichaClienteComplementarioDTO.setUso_otros("FALSE"); 
                }
                
                if (usoAhorro.equals("S")){
                   fichaClienteComplementarioDTO.setUso_cta_ahorro("TRUE"); 
                }else{
                   fichaClienteComplementarioDTO.setUso_cta_ahorro("FALSE"); 
                }
                
                if (usoTransferencia.equals("S")){
                   fichaClienteComplementarioDTO.setUso_transferencia("TRUE"); 
                }else{
                   fichaClienteComplementarioDTO.setUso_transferencia("FALSE"); 
                }
                
                if (usoPrestamo.equals("S")){
                   fichaClienteComplementarioDTO.setUso_cta_prestamo("TRUE"); 
                }else{
                   fichaClienteComplementarioDTO.setUso_cta_prestamo("FALSE"); 
                }
                
                if (usoDivisa.equals("S")){
                   fichaClienteComplementarioDTO.setUso_divisa("TRUE"); 
                }else{
                   fichaClienteComplementarioDTO.setUso_divisa("FALSE"); 
                }
                
                if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                //si es positivo procedemos a obtener la data completa
                BeanUtils.copyProperties(fichaClienteComplementarioDTO, FichaclienteComplementarioWs);
            }

        } catch (ServiceException e) {
            logger.error( new StringBuilder("ERROR DAO al consumir el servicio consultarDatosCliente: ")
                    .append("USR-").append(iCodigoCliente)
                    
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error( new StringBuilder("ERROR DAO EN consultarDatosCliente: ")
                    .append("USR-").append(iCodigoCliente)
                    
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        } finally {
            //validacion de objetos null: el objeto a consultar siempre debe estar instanciado para evitar NULLPOINTERSEXCEPTION
            //seteamos la respuesta sea cual sea el caso para manejar la salida en los controladores
            fichaClienteComplementarioDTO.setRespuesta(respuesta);
        }
            return fichaClienteComplementarioDTO;
    }
    
}
