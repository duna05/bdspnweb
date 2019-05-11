/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.facade;

import com.bds.wpn.dto.IbCanalDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.model.IbCanal;
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
 * @author cesar.mujica
 */
@Named
@Stateless(name = "wpnIbCanalFacade")
public class IbCanalFacade extends AbstractFacade<IbCanal> {

    @PersistenceContext(unitName = "bdspnwebPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Instanciar IbCanalFacade
     */
    public IbCanalFacade() {
        super(IbCanal.class);
    }

    private static final Logger logger = Logger.getLogger(IbParametrosFacade.class.getName());

    /**
     * Obtener canal
     *
     * @param idCanal String
     * @return IbCanalDTO
     */
    public IbCanalDTO consultaCanal(String idCanal) {

        IbCanalDTO ibCanalDTO = new IbCanalDTO();
        IbCanal ibCanal = new IbCanal();
        RespuestaDTO respuestaDTO = new RespuestaDTO();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            ibCanal = (IbCanal) em.createQuery("SELECT c FROM IbCanal c "
                    + "WHERE c.id = :idCanal"
            )
                    .setParameter("idCanal", new BigDecimal(idCanal))
                    .getSingleResult();

            ibCanalDTO.setCodigo(ibCanal.getCodigo());
            ibCanalDTO.setNombre(ibCanal.getNombre());

        } catch (NoResultException e) {
            respuestaDTO.setCodigo(BDSUtil.CODIGO_SIN_RESULTADOS_JPA);//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaCanal: ")
                    .append("-CH-").append(idCanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (IllegalArgumentException e) {
            respuestaDTO.setCodigo("JPA003");//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaParametro: ")
                    .append("-CH-").append(idCanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            //ibParametrosDTO.setIbParametro(ibParametros);
            respuestaDTO.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return ibCanalDTO;
    }

    /**
     * Obtener consulta de canal por id
     *
     * @param idCanal String
     * @return String
     */
    
   
    public IbCanalDTO consultaCanalporID(String idCanal) {

        IbCanalDTO ibCanalDTO = new IbCanalDTO();
        IbCanal ibCanal = new IbCanal();
        RespuestaDTO respuestaDTO = new RespuestaDTO();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            ibCanal = (IbCanal) em.createQuery("SELECT c FROM IbCanal c "
                    + "WHERE c.id = :idCanal"
            )
                    .setParameter("idCanal", new BigDecimal(idCanal))
                    .getSingleResult();

            ibCanalDTO.setCodigo(ibCanal.getCodigo());
            ibCanalDTO.setNombre(ibCanal.getNombre());
            ibCanalDTO.setId(ibCanal.getId());

        } catch (NoResultException e) {
            respuestaDTO.setCodigo(BDSUtil.CODIGO_SIN_RESULTADOS_JPA);//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaCanal: ")
                    .append("-CH-").append(idCanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (IllegalArgumentException e) {
            respuestaDTO.setCodigo("JPA003");//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaParametro: ")
                    .append("-CH-").append(idCanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            //ibParametrosDTO.setIbParametro(ibParametros);
            respuestaDTO.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return ibCanalDTO;
    }

    /**
     * Metodo que realiza la consulta de un canal y devuelve su descripcion
     * IB_CANAL
     *
     * @param idCanal String
     * @return String
     */
    public String textoCanal(String idCanal) {

        IbCanalDTO ibCanalDTO = new IbCanalDTO();
        IbCanal ibCanal = new IbCanal();
        RespuestaDTO respuestaDTO = new RespuestaDTO();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            ibCanal = (IbCanal) em.createQuery("SELECT c FROM IbCanal c "
                    + "WHERE c.id = :idCanal"
            )
                    .setParameter("idCanal", new BigDecimal(idCanal))
                    .getSingleResult();

            ibCanalDTO.setCodigo(ibCanal.getCodigo());
            ibCanalDTO.setNombre(ibCanal.getNombre());

        } catch (NoResultException e) {
            respuestaDTO.setCodigo(BDSUtil.CODIGO_SIN_RESULTADOS_JPA);//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaCanal: ")
                    .append("-CH-").append(idCanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (IllegalArgumentException e) {
            respuestaDTO.setCodigo("JPA003");//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaParametro: ")
                    .append("-CH-").append(idCanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            //ibParametrosDTO.setIbParametro(ibParametros);
            respuestaDTO.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return ibCanalDTO.getCodigo();
    }

    /**
     * Consulta la hora del sistema en base de datos y retorna un valor string
     * que puede ser: dia, tarde o noche.
     *
     * @param codigoCanal String
     * @return String 
     */
    public String consultaHorarioSistema(String codigoCanal) {
        String dia_desc = "";
        RespuestaDTO respuestaDTO = new RespuestaDTO();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            dia_desc = (String) em.createNativeQuery("select SALUDO_INTERNETBANKING from dual").getSingleResult();

        } catch (NoResultException e) {
            respuestaDTO.setCodigo(BDSUtil.CODIGO_SIN_RESULTADOS_JPA);//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaHoraSistema: ").append("CANAL-").append(codigoCanal)
                    .append("-CH-").append(codigoCanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (IllegalArgumentException e) {
            respuestaDTO.setCodigo("JPA003");//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaHoraSistema: ")
                    .append("-CH-").append(codigoCanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            //ibParametrosDTO.setIbParametro(ibParametros);
            respuestaDTO.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        return dia_desc;
    }

}
