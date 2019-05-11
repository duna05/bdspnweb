/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.facade;

import com.bds.wpn.dto.IbtextosDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.model.IbTextos;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;

/**
 *
 * @author cesar.mujica
 */
@Named
@Stateless(name = "wpnIbTextosFacade")
public class IbTextosFacade extends AbstractFacade<IbTextos> {

    @PersistenceContext(unitName = "bdspnwebPU")
    private EntityManager em;

    private static final Logger logger = Logger.getLogger(IbTextosFacade.class.getName());

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IbTextosFacade() {
        super(IbTextos.class);
    }

    /**
     * Metodo que realiza la consulta de un texto en la tabla IB_TEXTOS y
     * retorna su descripcion
     *
     * @param codigoParametro String
     * @param nombreCanal String
     * @return String
     */
    public String textoParametro(String codigoParametro, String nombreCanal) {
        IbtextosDTO ibtextosDTO = new IbtextosDTO();
        IbTextos ibTextos = new IbTextos();
        RespuestaDTO respuestaDTO = new RespuestaDTO();

        try {            
            em.getEntityManagerFactory().getCache().evictAll();
            ibTextos = (IbTextos) em.createQuery("SELECT i FROM IbTextos i WHERE i.codigo = :codigo")
                    .setParameter("codigo", codigoParametro)
                    .getSingleResult();

            ibtextosDTO.setIbTextos(ibTextos);

            if (ibtextosDTO.getIbTextos().getMensajeUsuario().isEmpty() || ibtextosDTO.getIbTextos().getMensajeUsuario() == null
                    || ibtextosDTO.getIbTextos().getMensajeUsuario().equals("")) {
                throw new NoResultException();
            }

        } catch (NoResultException e) {
            respuestaDTO.setCodigo(BDSUtil.CODIGO_SIN_RESULTADOS_JPA);//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN textoParametro: ").append("PARAMETRO-").append(codigoParametro)
                    .append("-CH-").append(nombreCanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            return "";
        } catch (IllegalArgumentException e) {
            respuestaDTO.setCodigo("JPA003");//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN textoParametro: ").append("PARAMETRO-").append(codigoParametro)
                    .append("-CH-").append(nombreCanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            respuestaDTO.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
            ibtextosDTO.setRespuestaDTO(respuestaDTO);
        }

        return ibtextosDTO.getIbTextos().getMensajeUsuario();
    }

    /**
     * Metodo que realiza la consulta de un objeto texto en la tabla IB_TEXTOS
     *
     * @param codigoParametro String
     * @param nombreCanal String
     * @return String
     */
    public IbTextos findByCodigo(String codigoParametro, String nombreCanal) {
        IbtextosDTO ibtextosDTO = new IbtextosDTO();
        IbTextos ibTextos = new IbTextos();
        RespuestaDTO respuestaDTO = new RespuestaDTO();

        try {            
            em.getEntityManagerFactory().getCache().evictAll();
            ibTextos = (IbTextos) em.createNamedQuery("IbTextos.findByCodigo")
                    .setParameter("codigo", codigoParametro)
                    .getSingleResult();

            ibtextosDTO.setIbTextos(ibTextos);

            if (ibtextosDTO.getIbTextos().getMensajeUsuario().isEmpty() || ibtextosDTO.getIbTextos().getMensajeUsuario() == null
                    || ibtextosDTO.getIbTextos().getMensajeUsuario().equals("")) {
                throw new NoResultException();
            }

        } catch (NoResultException e) {
            respuestaDTO.setCodigo(BDSUtil.CODIGO_SIN_RESULTADOS_JPA);//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN findByCodigo: ").append("PARAMETRO-").append(codigoParametro)
                    .append("-CH-").append(nombreCanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            return ibTextos;
        } catch (IllegalArgumentException e) {
            respuestaDTO.setCodigo("JPA003");//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN findByCodigo: ").append("PARAMETRO-").append(codigoParametro)
                    .append("-CH-").append(nombreCanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            respuestaDTO.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
            ibtextosDTO.setRespuestaDTO(respuestaDTO);
        }

        return ibTextos;
    }

}
