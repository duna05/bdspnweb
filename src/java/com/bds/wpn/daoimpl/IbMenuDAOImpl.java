/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.daoimpl;

import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.dao.IbMenuDAO;
import com.bds.wpn.dao.NotificationServiceType;
import com.bds.wpn.dto.IbCanalDTO;
import com.bds.wpn.dto.IbModulosDTO;
import com.bds.wpn.dto.IbTransaccionesDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.exception.ServiceException;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.util.DAOUtil;
import com.bds.wpn.util.SessionAttributesNames;
import com.bds.wpn.ws.services.IbAfiliacionWS;
import com.bds.wpn.ws.services.IbAfiliacionWS_Service;
import com.bds.wpn.ws.services.IbMenuWS;
import com.bds.wpn.ws.services.IbMenuWS_Service;
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
 *
 * @author renseld.lugo
 */
@Named
@Stateless(name = "wpnIbMenuDAOImplweb")
@NotificationServiceType(NotificationServiceType.ServiceType.SERVICES)
public class IbMenuDAOImpl extends DAOUtil implements IbMenuDAO {

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(IbMenuDAOImpl.class.getName());

    @EJB
    IbParametrosFacade parametrosFacade;
    
    @Inject
    ParametrosController parametrosController;

    private final String urlWsdlMenu = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbMenuWS?wsdl";
    private final String urlWsdlAfiliaciones = getSessionScope().get(SessionAttributesNames.URL_WSDL) + "/IbAfiliacionWS?WSDL";

    /**
     * *
     * Metodo que retorna el menu (Modulos y Transacciones) validando si el
     * usuario es "piloto" o no renseld.lugo
     *
     * @param idUsuario String Id del usuario en Inter Banking
     * @param idCanal String idCanal por el cual se ejecuta la transaccion.
     * @param nombreCanal String nombreCanal por el cual se ejecuta la
     * transaccion.
     * @return listModulosDTO List<IbModulosDTO> lista con menu y sub-menu
     */
    @Override
    public List<IbModulosDTO> consultarModulosPorUsuario(String idUsuario, String idCanal, String nombreCanal) {
        List<IbModulosDTO> listModulosDTO = new ArrayList<>();
        IbModulosDTO modulosDTO = null;
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            IbMenuWS_Service service = new IbMenuWS_Service(new URL(urlWsdlMenu));
            IbMenuWS port = service.getIbMenuWSPort();
            com.bds.wpn.ws.services.IbModulosDTO moduloWs = port.consultarModulosPorUsuario(idUsuario, idCanal, nombreCanal);
            BeanUtils.copyProperties(respuesta, moduloWs.getRespuesta());
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                IbCanalDTO canalDTO = null;
                List<IbTransaccionesDTO> listTransDto = null;
                IbTransaccionesDTO transaccionesDTO = null;

                for (com.bds.wpn.ws.services.IbModulos m : moduloWs.getIbModulos()) {
                    modulosDTO = new IbModulosDTO();
                    BeanUtils.copyProperties(modulosDTO, m);
                    canalDTO = new IbCanalDTO();
                    BeanUtils.copyProperties(canalDTO, m.getIdCanal());
                    listTransDto = new ArrayList<>();
                    for (com.bds.wpn.ws.services.IbTransacciones t : m.getIbTransaccionesCollection()) {
                        transaccionesDTO = new IbTransaccionesDTO();
                        BeanUtils.copyProperties(transaccionesDTO, t);
                        listTransDto.add(transaccionesDTO);
                    }
                    modulosDTO.setIdCanalDTO(canalDTO);
                    modulosDTO.setIbTransaccionesDTOCollection(listTransDto);
                    listModulosDTO.add(modulosDTO);
                }
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio consultarModulosPorUsuario: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN consultarModulosPorUsuario: ")
                    .append("USR-").append(idUsuario)
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }
        return listModulosDTO;
    }

    /**
     * Metodo para obtener el modelo y la transaccion de los que tengan el posee
     * beneficiario activo
     *
     * @param idCanal id del canal
     * @param nombreCanal String nombre del canal
     * @return IbModulosDTO
     */
    @Override
    public List<IbModulosDTO> obtenerModeloTransBeneficiario(String idCanal, String nombreCanal) {
        List<IbModulosDTO> listModulosDTO = new ArrayList<>();
        IbModulosDTO modulosDTO = null;
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            IbAfiliacionWS_Service service = new IbAfiliacionWS_Service(new URL(urlWsdlAfiliaciones));
            IbAfiliacionWS port = service.getIbAfiliacionWSPort();
            com.bds.wpn.ws.services.IbModulosDTO moduloWs = port.modeloTransBeneficiario(nombreCanal);
            BeanUtils.copyProperties(respuesta, moduloWs.getRespuesta());
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                IbCanalDTO canalDTO = null;
                List<IbTransaccionesDTO> listTransDto = null;
                IbTransaccionesDTO transaccionesDTO = null;

                for (com.bds.wpn.ws.services.IbModulos m : moduloWs.getIbModulos()) {
                    modulosDTO = new IbModulosDTO();
                    BeanUtils.copyProperties(modulosDTO, m);
                    canalDTO = new IbCanalDTO();
                    BeanUtils.copyProperties(canalDTO, m.getIdCanal());
                    listTransDto = new ArrayList<>();
                    for (com.bds.wpn.ws.services.IbTransacciones t : m.getIbTransaccionesCollection()) {
                        transaccionesDTO = new IbTransaccionesDTO();
                        BeanUtils.copyProperties(transaccionesDTO, t);
                        listTransDto.add(transaccionesDTO);
                    }
                    modulosDTO.setIdCanalDTO(canalDTO);
                    modulosDTO.setIbTransaccionesDTOCollection(listTransDto);
                    listModulosDTO.add(modulosDTO);
                }
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerModeloTransBeneficiario: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerModeloTransBeneficiario: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }

        return listModulosDTO;
    }

    /**
     * Metodo para obtener el modelo y la transaccion de los que tengan el posee
     * beneficiario activo y que no sean propias DelSur
     *
     * @param idCanal id del canal
     * @param nombreCanal String nombre del canal
     * @return IbModulosDTO
     */
    @Override
    public List<IbModulosDTO> obtenerModeloTransBeneficiarioFiltrada(String idCanal, String nombreCanal) {
        List<IbModulosDTO> listModulosDTO = new ArrayList<>();
        IbModulosDTO modulosDTO = null;
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            IbAfiliacionWS_Service service = new IbAfiliacionWS_Service(new URL(urlWsdlAfiliaciones));
            IbAfiliacionWS port = service.getIbAfiliacionWSPort();
            com.bds.wpn.ws.services.IbModulosDTO moduloWs = port.modeloTransBeneficiario(nombreCanal);
            BeanUtils.copyProperties(respuesta, moduloWs.getRespuesta());
            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                IbCanalDTO canalDTO = null;
                List<IbTransaccionesDTO> listTransDto = null;
                IbTransaccionesDTO transaccionesDTO = null;

                for (com.bds.wpn.ws.services.IbModulos m : moduloWs.getIbModulos()) {
                    modulosDTO = new IbModulosDTO();
                    BeanUtils.copyProperties(modulosDTO, m);
                    canalDTO = new IbCanalDTO();
                    BeanUtils.copyProperties(canalDTO, m.getIdCanal());
                    listTransDto = new ArrayList<>();
                    for (com.bds.wpn.ws.services.IbTransacciones t : m.getIbTransaccionesCollection()) {
                        //obviamos las transacciones de pagos de tdc propias del sur y transf propias del sur ya que no requieren afiliacion
                        if (!t.getIdCore().toString().equalsIgnoreCase(ID_TRANS_CTAS_PROPIAS_DELSUR) && !t.getIdCore().toString().equalsIgnoreCase(ID_TRANS_TDC_PROPIAS_DELSUR)) {
                            transaccionesDTO = new IbTransaccionesDTO();
                            BeanUtils.copyProperties(transaccionesDTO, t);
                            listTransDto.add(transaccionesDTO);
                        }
                    }
                    modulosDTO.setIdCanalDTO(canalDTO);
                    modulosDTO.setIbTransaccionesDTOCollection(listTransDto);
                    listModulosDTO.add(modulosDTO);
                }
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerModeloTransBeneficiario: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerModeloTransBeneficiario: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }

        return listModulosDTO;
    }

    /**
     * Método para obtener todos los módulos y transacciones asociadas a un
     * canal en específico esten activos o inactivos, visibles o no visibles
     *
     * @author wilmer.rondon
     * @param idCanal canal del cual se desea obtener los modulos y las
     * transacciones
     * @param nombreCanal nombre del canal para efectos de los logs
     * @return IbModulosDTO
     */
    @Override
    public List<IbModulosDTO> obtenerTodosModulosYTransacciones(String idCanal, String nombreCanal) {
        List<IbModulosDTO> listModulosDTO = new ArrayList<>();
        IbModulosDTO modulosDTO = null;
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            IbMenuWS_Service service = new IbMenuWS_Service(new URL(urlWsdlMenu));
            IbMenuWS port = service.getIbMenuWSPort();
            com.bds.wpn.ws.services.IbModulosDTO moduloWs = port.obtenerTodosModulosYTransacciones(idCanal, nombreCanal);
            BeanUtils.copyProperties(respuesta, moduloWs.getRespuesta());

            if (!respuesta.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !respuesta.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                throw new ServiceException();
            } else {
                IbCanalDTO canalDTO = null;
                List<IbTransaccionesDTO> listTransDto = null;
                IbTransaccionesDTO transaccionesDTO = null;

                for (com.bds.wpn.ws.services.IbModulos m : moduloWs.getIbModulos()) {
                    modulosDTO = new IbModulosDTO();
                    BeanUtils.copyProperties(modulosDTO, m);
                    canalDTO = new IbCanalDTO();
                    BeanUtils.copyProperties(canalDTO, m.getIdCanal());
                    listTransDto = new ArrayList<>();
                    for (com.bds.wpn.ws.services.IbTransacciones t : m.getIbTransaccionesCollection()) {
                        transaccionesDTO = new IbTransaccionesDTO();
                        BeanUtils.copyProperties(transaccionesDTO, t);
                        listTransDto.add(transaccionesDTO);
                    }
                    modulosDTO.setIdCanalDTO(canalDTO);
                    modulosDTO.setIbTransaccionesDTOCollection(listTransDto);
                    listModulosDTO.add(modulosDTO);
                }
            }
        } catch (ServiceException e) {
            logger.error(new StringBuilder("ERROR DAO al consumir el servicio obtenerTodosModulosYTransacciones: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());

        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR DAO EN obtenerTodosModulosYTransacciones: ")
                    .append("-CH-").append(nombreCanal)
                    .append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            respuesta.setCodigo(CODIGO_EXCEPCION_GENERICA);
        }

        return listModulosDTO;
    }

}
