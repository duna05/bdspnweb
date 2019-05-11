/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dto.IbCanalDTO;
import com.bds.wpn.facade.IbCanalFacade;
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
@ManagedBean(name = "wpnCanalController")
@SessionScoped
public class CanalController extends BDSUtil implements Serializable {

    @EJB
    IbCanalFacade ibCanalFacade;
    
    IbCanalDTO ibCanalDTO;

    private Map<String, String> textosMap = new HashMap<>();

    public IbCanalDTO getIbCanalDTO(String idCanal) {
        if(ibCanalDTO == null){
            ibCanalDTO = ibCanalFacade.consultaCanalporID(idCanal);
        }
        return ibCanalDTO;
    }

    public void clearMap() {
        textosMap.clear();
    }
}