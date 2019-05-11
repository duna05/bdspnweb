/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.facade;

import com.bds.wpn.dto.IbTransaccionesDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.model.IbCanal;
import com.bds.wpn.model.IbTransacciones;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import java.math.BigDecimal;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;

/**
 *
 * @author rony.rodriguez
 */
@Named
@Stateless(name = "wpnIbTransaccionesFacade")
public class IbTransaccionesFacade extends AbstractFacade<IbCanal> {

    private static final Logger logger = Logger.getLogger(IbParametrosFacade.class.getName());

    @PersistenceContext(unitName = "bdspnwebPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IbTransaccionesFacade() {
        super(IbCanal.class);
    }

    /**
     * *
     * Metodo que realiza la consulta de una transaccion y devuelve su id
     *
     * @param nombreTransaccion String
     * @return String
     */
    public String consultarIdTransaccion(String nombreTransaccion) {

        IbTransaccionesDTO ibTransaccionesDTO = new IbTransaccionesDTO();
        RespuestaDTO respuestaDTO = new RespuestaDTO();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            IbTransacciones ibTransacciones = (IbTransacciones) em.createNamedQuery("IbTransacciones.findByNombre")
                    .setParameter("nombre", nombreTransaccion)
                    .getSingleResult();

            ibTransaccionesDTO.setId(ibTransacciones.getId());
            ibTransaccionesDTO.setNombre(ibTransacciones.getNombre());

        } catch (NoResultException e) {
            respuestaDTO.setCodigo(BDSUtil.CODIGO_SIN_RESULTADOS_JPA);//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultarIdTransaccion: ")
                    .append("-CH-").append(BDSUtil.CODIGO_CANAL_MOBILE).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (IllegalArgumentException e) {
            respuestaDTO.setCodigo("JPA003");//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultarIdTransaccion: ")
                    .append("-CH-").append(BDSUtil.CODIGO_CANAL_MOBILE).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            respuestaDTO.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return String.valueOf(ibTransaccionesDTO.getId());
    }

    /**
     * Obtener consulta de canal por id
     *
     * @param idTrans String
     * @return IbTransaccionesDTO
     */
    public IbTransaccionesDTO consultaTransaccionporID(String idTrans) {

        IbTransaccionesDTO ibTransaccionDTO = new IbTransaccionesDTO();
        IbTransacciones ibTransaccion = new IbTransacciones();
        RespuestaDTO respuestaDTO = new RespuestaDTO();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            ibTransaccion = (IbTransacciones) em.createQuery("SELECT c FROM IbTransacciones c "
                    + "WHERE c.id = :idTrans"
            )
                    .setParameter("idTrans", new BigDecimal(idTrans))
                    .getSingleResult();

            ibTransaccionDTO.setId(ibTransaccion.getId());
            ibTransaccionDTO.setNombre(ibTransaccion.getNombre());

        } catch (NoResultException e) {
            respuestaDTO.setCodigo(BDSUtil.CODIGO_SIN_RESULTADOS_JPA);//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaTransaccionporID: ")
                    .append("-CH-").append(idTrans).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (IllegalArgumentException e) {
            respuestaDTO.setCodigo("JPA003");//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaTransaccionporID: ")
                    .append("-CH-").append(idTrans).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            //ibParametrosDTO.setIbParametro(ibParametros);
            respuestaDTO.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return ibTransaccionDTO;
    }

    /**
     * Obtener consulta de canal por id CORE
     *
     * @param idTrans String
     * @param canal String
     * @return IbTransaccionesDTO
     */
    public IbTransaccionesDTO consultaTransaccionporIDCORE(String idTrans, String canal) {

        IbTransaccionesDTO ibTransaccionDTO = new IbTransaccionesDTO();
        IbTransacciones transaccion;
        RespuestaDTO respuestaDTO = new RespuestaDTO();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            transaccion = (IbTransacciones) em.createQuery("SELECT c FROM IbTransacciones c "
                    + "WHERE c.idCore = :idTrans "
                    + "and c.idModulo.idCanal.id = :idCanal"
            )
                    .setParameter("idTrans", new BigDecimal(idTrans))
                    .setParameter("idCanal", new BigDecimal(canal))
                    .getSingleResult();

            if (transaccion != null && transaccion.getId() != null) {
                ibTransaccionDTO.setId(transaccion.getId());
                ibTransaccionDTO.setNombre(transaccion.getNombre());
            }else{
                throw new NoResultException();
            }

        } catch (NoResultException e) {
            respuestaDTO.setCodigo(BDSUtil.CODIGO_SIN_RESULTADOS_JPA);//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaTransaccionporIDCORE: ")
                    .append("-CH-").append(idTrans).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (IllegalArgumentException e) {
            respuestaDTO.setCodigo("JPA003");//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaTransaccionporIDCORE: ")
                    .append("-CH-").append(idTrans).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            //ibParametrosDTO.setIbParametro(ibParametros);
            respuestaDTO.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return ibTransaccionDTO;
    }

}
