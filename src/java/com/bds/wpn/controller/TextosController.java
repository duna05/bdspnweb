/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.facade.IbTextosFacade;
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
@ManagedBean(name = "wpnTextosController")
@SessionScoped
public class TextosController extends BDSUtil implements Serializable {

    @EJB
    IbTextosFacade textosFacade;

    private Map<String, String> textosMap = new HashMap<>();

    public String getNombreTexto(String key, String idCanal) {
        if (!textosMap.containsKey(key)) {
            String message = getTextosFacade().textoParametro(key, idCanal);

            textosMap.put(key, message);
        }
        return textosMap.get(key);
    }

    public void clearMap() {
        textosMap.clear();
    }

    public IbTextosFacade getTextosFacade() {
        return textosFacade;
    }

    public void setTextosFacade(IbTextosFacade textosFacade) {
        this.textosFacade = textosFacade;
    }
}