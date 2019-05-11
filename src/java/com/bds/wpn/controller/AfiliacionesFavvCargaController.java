/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;


import com.bds.wpn.util.BDSUtil;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author jose.perez
 *
 */
@ManagedBean(name = "wpnAfiliacionesFavvCargaController")
@ViewScoped
public class AfiliacionesFavvCargaController extends BDSUtil {

    @Inject
    private TextosController textosController;
    @Inject
    private InicioSesionController sesionController;
    @Inject
    ParametrosController parametrosController;

    @PostConstruct
    public void initController() {        
        sesionController.setReiniciarForm(true);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliacionesFavv.url.paso1", sesionController.getIdCanal()));
    }
    
    public void doContinuarCommandButtonAction() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliacionesFavv.url.paso2", sesionController.getIdCanal()));
    }
    
    public InicioSesionController getSesionController() {
        return sesionController;
    }

    public void setSesionController(InicioSesionController sesionController) {
        this.sesionController = sesionController;
    }

    public TextosController getTextosController() {
        return textosController;
    }

    public void setTextosController(TextosController textosController) {
        this.textosController = textosController;
    }

    /**
     * Metodo que devuelve un bolean del la variable mostrar detalle
     *
     * @return mostrarAlerta boolean
     */
//    public boolean isMostrarAlertaGenerica() {
//        if (!getSessionScope().containsKey(SessionAttributesNames.MOSTRAR_ALERTA_GENERICA)) {
//            return false;
//        }
//
//        return (Boolean) getSessionScope().get(SessionAttributesNames.MOSTRAR_ALERTA_GENERICA);
//    }

    /**
     * retorna el texto a mostrar el en dialogo de alerta generica
     *
     * @return String
     */
//    public String getTextoAlertaGenerica() {
//
//        return (String) getSessionScope().get(SessionAttributesNames.TEXTO_ALERTA_GENERICO);
//    }

    /**
     * obtiene el mensaje de alerta Generica
     *
     * @return String
     */
//    public String getMensajeAlertaGenerica() {
//        return (String) getSessionScope().get(SessionAttributesNames.MENSAJE_ALERTA_GENERICO);
//    }

//    public void setMostrarAlertaGenerica(Boolean mostrar) {
//        getSessionScope().put(SessionAttributesNames.MOSTRAR_ALERTA_GENERICA, mostrar);
//    }

//    public void setMensajeAlertaGenerica(String texto) {
//        getSessionScope().put(SessionAttributesNames.MENSAJE_ALERTA_GENERICO, texto);
//    }

//    public void doCerrarDialogoGenericoCommandButtonAction() {
//        getSessionScope().remove(SessionAttributesNames.TEXTO_ALERTA_GENERICO);
//        getSessionScope().remove(SessionAttributesNames.MENSAJE_ALERTA_GENERICO);
//        getSessionScope().remove(SessionAttributesNames.MOSTRAR_ALERTA_GENERICA);
//    }

}
