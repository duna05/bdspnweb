/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.IbClavesOperacionesEspecialesDAO;
import com.bds.wpn.dao.IbMensajesDAO;
import com.bds.wpn.dto.IbMensajesDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author cesar.mujica
 */
@Named("wpnNoticiasController")
@SessionScoped
public class NoticiasController extends BDSUtil implements Serializable {

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbMensajesDAO mensajesDAO;

    @Inject
    InicioSesionController sesionController;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;
    
    @EJB
    IbClavesOperacionesEspecialesDAO ibClavesOperacionesEspecialesDAO;

    private String cantNoticiasNuevas = "";     //contador de noticias nuevas
    private IbMensajesDTO mensaje = null;       //objeto de mensajes asociados al usuario
    private int pagina;                         //indicador de la pag/elemento de la noticia
    private String text = null;

    ///////////////////GETTERS Y SETTERS////////////////////////////
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCantNoticiasNuevas() {
        return cantNoticiasNuevas;
    }

    public void setCantNoticiasNuevas(String cantNoticiasNuevas) {
        this.cantNoticiasNuevas = cantNoticiasNuevas;
    }

    public IbMensajesDTO getMensaje() {
        return mensaje;
    }

    public void setMensaje(IbMensajesDTO mensaje) {
        this.mensaje = mensaje;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    ////////////////////METODOS UTILITARIOS///////////////////// 
    /**
     * obtiene el texto de la cantidad de noticias nuevas para un usuario ej: 9
     * -> "nueve (9)"
     */
    /*
    public void obtenerCantMsjsNuevos() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            UtilDTO util = mensajesDAO.cantNuevosMsjsUsuarios(sesionController.getUsuario().getId().toString(), sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (util != null && util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                int cant = (int) util.getResuladosDTO().get("resultado");
                if (cant > 0) {
                    this.cantNoticiasNuevas = convierteNumeroAPalabras(cant);
                } else {
                    this.cantNoticiasNuevas = "";
                }
            } else {
                //VALIDAR EL MESSAGE SALEN UNOS PUNTOS AL CREAR EL MENU
                if (util == null || util.getRespuestaDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    //FacesContext.getCurrentInstance().addMessage("formPrestamo:msjPrestamos", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                } else {
                    if (util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                        //FacesContext.getCurrentInstance().addMessage("formPrestamo:msjPrestamos", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
                    }
                }
            }
        }
    }
    */

    /**
     * metodo de redireccion al la zona de noticias
     *
     * @return la url de las noticias
     */
    public String redireccionNoticias() {
        return (parametrosController.getNombreParametro("pnw.global.url.inicial", sesionController.getIdCanal()) + "?faces-redirect=true");
    }

    /**
     * metodo encargado de buscar las noticias asociadas al usuario
     */
    /*
    public void consultarNoticias() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.mensaje = mensajesDAO.consultarMensajesUsuarios(sesionController.getUsuario().getId().toString(), sesionController.getNombreCanal(), sesionController.getIdCanal());
            if (mensaje == null || mensaje.getRespuestaDTO() == null || !mensaje.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formNoticias:divDataTable", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else {
                if (mensaje != null && mensaje.getRespuestaDTO() != null && !mensaje.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && mensaje.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext.getCurrentInstance().addMessage("formNoticias:divDataTable", new FacesMessage(FacesMessage.SEVERITY_WARN, "", mensaje.getRespuestaDTO().getTextoSP()));
                }
            }
        }
    }
    */

    /**
     * metodo que se encarga de validar y actualiza el estatus de una noticia en
     * caso que sea una Nueva
     */
    /*
    public void validarEstsLectura() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            UtilDTO util;
            RespuestaDTO respuesta = null;
            if (this.mensaje != null && this.getMensaje().getMensajes() != null && !this.getMensaje().getMensajes().isEmpty()) {
                for (IbMensajesDTO mensajeTemp : mensaje.getMensajes()) {
                    //preguntamos si el mensaje ya fue leido
                    util = this.mensajesDAO.mensajeUsuarioLeido(sesionController.getUsuario().getId().toString(), mensajeTemp.getId().toString(), sesionController.getNombreCanal(), sesionController.getIdCanal());

                    if (util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getResuladosDTO() != null) {
                        //si el mensaje no ha sido leido entonces actualizamos el estatus
                        if (!(Boolean) util.getResuladosDTO().get("resultado")) {
                            respuesta = this.mensajesDAO.marcarMensajeLeido(sesionController.getUsuario().getId().toString(), mensajeTemp.getId().toString(), sesionController.getNombreCanal(), sesionController.getIdCanal());
                        }
                        if (respuesta == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            //error generico al intentar actualizar la noticia
                        } else {
                            if (!respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                //error controlado al intentar actualizar la noticia
                            }
                        }
                    } else {
                        if (util.getRespuestaDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) || util.getResuladosDTO() != null) {
                            //error generico al intentar consultar el estatus de la noticia
                        } else {
                            if (!util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                //error controlado al intentar consultar el estatus de la noticia
                            }
                        }
                    }
                }
            }
        }
    }
    */
    
    public boolean consultaExisteClaveOP(){
        boolean resultado = false;        
        resultado = ibClavesOperacionesEspecialesDAO.consultarClaveOperacionesEspeciales(
                sesionController.getUsuario().getId().toString(), 
                sesionController.getIdCanal(), 
                sesionController.getNombreCanal()); 
        return resultado;
    }
    
    public void redirecCrearClaveOP(){
        sesionController.setNombreModulo(textosController.getNombreTexto("pnw.menu.modulo.seguridad", sesionController.getNombreCanal()));
        sesionController.setNombreTransaccion(textosController.getNombreTexto("pnw.submenu.seguridad.crearClaveOpeEsp", sesionController.getNombreCanal()));
        this.redirectFacesContext("/sec/seguridad/clave_especial_cre_vali_posi.xhtml");
    }
}
