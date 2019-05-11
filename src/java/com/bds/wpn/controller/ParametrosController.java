/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author luis.perez
 */
@ManagedBean(name = "wpnParametrosController")
@SessionScoped
public class ParametrosController extends BDSUtil implements Serializable {

    @EJB
    IbParametrosFacade ibParametrosFacade;
    private Map<String, String> parametrosNombreMap = new HashMap<>();
    private Map<String, String> parametrosValorMap = new HashMap<>();

    public String getNombreParametro(String key, String idCanal) {
        if (!parametrosNombreMap.containsKey(key)) {
            String message = getIbParametrosFacade().consultaParametro(key, idCanal).getIbParametro().getNombre();
            parametrosNombreMap.put(key, message);
        }
        return parametrosNombreMap.get(key);
    }
    
    public String getValorParametro(String key, String idCanal) {
        if (!parametrosValorMap.containsKey(key)) {
            String message = getIbParametrosFacade().consultaParametro(key, idCanal).getIbParametro().getValor();
            parametrosValorMap.put(key, message);
        }
        return parametrosValorMap.get(key);
    }

    public void clearMap() {
        parametrosValorMap.clear();
        parametrosNombreMap.clear();
    }

    public IbParametrosFacade getIbParametrosFacade() {
        return ibParametrosFacade;
    }

    public void setIbParametrosFacade(IbParametrosFacade ibParametrosFacade) {
        this.ibParametrosFacade = ibParametrosFacade;
    }
}