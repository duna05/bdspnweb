/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.SubtipoClienteDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.ws.services.SubTipoClienteWs;
import com.bds.wpn.ws.services.SubTipoClienteWs_Service;

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
import com.bds.wpn.dao.SubtipoClienteDAO;
import com.bds.wpn.util.SessionAttributesNames;

/**
 *
 * @author alejandro.flores y  humberto rojas
 */
@Named
@Stateless(name = "wpnSubTipoClienteJuridicoDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class SubtipoClienteDAOImpl extends DAOUtil implements SubtipoClienteDAO {

    private static final Logger logger = Logger.getLogger(FichaClienteDAOImpl.class.getName());
    private final String urlWsdlSubTipoClienteJuridico = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/SubTipoClienteWs?wsdl";
    //private final String urlWsdlSubTipoClienteJuridico = "http://localhost:7001/ibdsws/SubTipoClienteWs?wsdl";

    @Override
    public SubtipoClienteDTO consultarSubtipoCliente() {
        SubtipoClienteDTO subTipoClienteJuridicoDTO = new SubtipoClienteDTO();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            SubTipoClienteWs_Service service = new SubTipoClienteWs_Service(new URL(urlWsdlSubTipoClienteJuridico));
            SubTipoClienteWs port = service.getSubTipoClienteWsPort();
            com.bds.wpn.ws.services.SubtipoClienteDTO subtipoClienteWs = port.consultarSubtipoCliente();
            BeanUtils.copyProperties(respuesta, subtipoClienteWs.getRespuesta());
            List<String> subtipoClientes = new ArrayList<>();
            //System.out.println(subtipoClienteWs.getIbSubtipoClienteJuridicoList());
            for (com.bds.wpn.ws.services.SubtipoClienteDTO subtipoClienteDTO : subtipoClienteWs.getIbSubtipoClienteJuridicoList()) {
                subtipoClientes.add(subtipoClienteDTO.getCodigoSubtipo() + ":" + subtipoClienteDTO.getDescripcion());
            }
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                subTipoClienteJuridicoDTO.setListTipo(subtipoClientes);
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
            subTipoClienteJuridicoDTO.setRespuestaDTO(respuesta);
        }
        return subTipoClienteJuridicoDTO;
    }

}
