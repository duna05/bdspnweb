/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.facade;

import com.bds.wpn.dto.IbParametrosDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.model.IbFechaBD;
import com.bds.wpn.model.IbParametros;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_EXCEPCION_GENERICA;
import static com.bds.wpn.util.BDSUtil.DESCRIPCION_RESPUESTA_FALLIDA;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author cesar.mujica
 */
@Named
@Stateless(name = "wpnIbParametrosFacade")
public class IbParametrosFacade extends AbstractFacade<IbParametros> {

    @PersistenceContext(unitName = "bdspnwebPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    private Map<String, String> textosMap = new HashMap<>();

    private static final Logger logger = Logger.getLogger(IbParametrosFacade.class.getName());

    public IbParametrosFacade() {
        super(IbParametros.class);
    }

    /**
     * Metodo que realiza la consulta de un parametro en la tabla IB_PARAETROS
     *
     * @param codigoParametro String
     * @param idcanal String
     * @return IbParametrosDTO
     */
    public IbParametrosDTO consultaParametro(String codigoParametro, String idcanal) {

        IbParametrosDTO ibParametrosDTO = new IbParametrosDTO();
        IbParametros ibParametros = new IbParametros();
        RespuestaDTO respuestaDTO = new RespuestaDTO();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            ibParametros = (IbParametros) em.createQuery("SELECT c FROM IbParametros c "
                    + "WHERE c.codigo = :codParametro"
            )
                    .setParameter("codParametro", codigoParametro)
                    .getSingleResult();

            ibParametrosDTO.setIbParametro(ibParametros);

        } catch (NoResultException e) {
            respuestaDTO.setCodigo(BDSUtil.CODIGO_SIN_RESULTADOS_JPA);//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaParametro: ").append("PARAMETRO-").append(codigoParametro)
                    .append("-CH-").append(idcanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (IllegalArgumentException e) {
            respuestaDTO.setCodigo("JPA003");//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN consultaParametro: ").append("PARAMETRO-").append(codigoParametro)
                    .append("-CH-").append(idcanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            ibParametrosDTO.setIbParametro(ibParametros);
            respuestaDTO.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }
        ibParametrosDTO.setRespuestaDTO(respuestaDTO);
        return ibParametrosDTO;
    }

    /**
     * Metodo que realiza la consulta de un parametro en la tabla IB_PARAETROS y
     * retorna su descripcion
     *
     * @param codigoParametro String
     * @param idcanal String
     * @return String
     */
    public String textoParametro(String codigoParametro, String idcanal) {

        IbParametrosDTO ibParametrosDTO = new IbParametrosDTO();
        IbParametros ibParametros = new IbParametros();
        RespuestaDTO respuestaDTO = new RespuestaDTO();

        try {
            em.getEntityManagerFactory().getCache().evictAll();
            ibParametros = (IbParametros) em.createNamedQuery("IbParametros.findByCodigo")
                    .setParameter("codigo", codigoParametro)
                    .getSingleResult();

            ibParametrosDTO.setIbParametro(ibParametros);

            if (ibParametrosDTO.getIbParametro().getNombre().isEmpty() || ibParametrosDTO.getIbParametro().getNombre() == null
                    || ibParametrosDTO.getIbParametro().getNombre().equals("")) {
                throw new NoResultException();
            }

        } catch (NoResultException e) {
            respuestaDTO.setCodigo(BDSUtil.CODIGO_SIN_RESULTADOS_JPA);//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN textoParametro: ").append("PARAMETRO-").append(codigoParametro)
                    .append("-CH-").append(idcanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            return "";
        } catch (IllegalArgumentException e) {
            respuestaDTO.setCodigo("JPA003");//revisar el log     
            logger.error(new StringBuilder("ERROR JPA EN textoParametro: ").append("PARAMETRO-").append(codigoParametro)
                    .append("-CH-").append(idcanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        } catch (Exception e) {
            ibParametrosDTO.setIbParametro(ibParametros);
            respuestaDTO.setCodigo(CODIGO_EXCEPCION_GENERICA);//revisar el log
        }

        return ibParametrosDTO.getIbParametro().getNombre();
    }

    /**
     * @param idcanal
     * @return la hora de la BD
     */
    public Date obtenerFechaBD(String idcanal) {

        try {
            em.getEntityManagerFactory().getCache().evictAll();
            Query query = em.createNativeQuery("SELECT sysdate AS date_value FROM dual", IbFechaBD.class);
            IbFechaBD fechaBD = (IbFechaBD) query.getSingleResult();
            return fechaBD.getFecha();
        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR JPA EN obtenerFechaBD: ")
                    .append("-CH-").append(idcanal).append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
            throw e;
        } 
    }
}
