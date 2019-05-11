/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dto;

import com.bds.wpn.model.IbParametros;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author rony.rodriguez
 */
public class IbParametrosDTO implements Serializable {

    private IbParametros ibParametro;   //Objeto de tipo IbParametros para retornar un solo parametro
    private List<IbParametros> ibParametros;  //Lisa de objetos IbParametros para retornar varios parametros 
    private RespuestaDTO respuestaDTO;  //objeto de tipo RespuestaDTO para retornar el resultado de la transaccion

    /**
     * Fetorna Objeto de tipo IbParametros para retornar un solo parametro.
     *
     * @return IbParametros
     */
    public IbParametros getIbParametro() {
        return ibParametro;
    }

    /**
     * Asigna Objeto de tipo IbParametros
     *
     * @param IbParametros IbParametros
     */
    public void setIbParametro(IbParametros ibParametro) {
        this.ibParametro = ibParametro;
    }

    /**
     * Retorna Lista de objetos IbParametros
     *
     * @return List<IbParametros>
     */
    public List<IbParametros> getIbParametros() {
        return ibParametros;
    }

    /**
     * Asigna Lista de objetos IbParametros.
     *
     * @param IbParametros.
     */
    public void setIbParametros(List<IbParametros> ibParametros) {
        this.ibParametros = ibParametros;
    }

    /**
     * Obtener respuesta
     * @return RespuestaDTO
     */
    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    /**
     * Asignar respuesta
     * @param respuestaDTO RespuestaDTO
     */
    public void setRespuestaDTO(RespuestaDTO respuestaDTO) {
        this.respuestaDTO = respuestaDTO;
    }
}
