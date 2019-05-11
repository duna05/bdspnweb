/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author luis.perez
 */
@Named("wpnClaveEspecialModificarEjecutarProcesoController")
@SessionScoped
public class ClaveEspecialModificarEjecutarProcesoController extends BDSUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public void doContinuar() {
        redirectFacesContext("/sec/noticias/noticias.xhtml");
    }
}
