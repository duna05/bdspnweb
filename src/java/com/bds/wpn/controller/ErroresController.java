/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.IbCanalesDAO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.CODIGO_USUARIO_DESCONECTADO;
import java.io.IOException;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author cesar.mujica
 */
@Named("wpnErroresController")
@SessionScoped
public class ErroresController extends BDSUtil implements Serializable {

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbParametrosFacade parametrosFacade;

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbCanalesDAO ibCanalesDAO;

    @Inject
    TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    private String tipoError = ""; //tipo de error por defecto
    private String tituloError = "";//titulo del pop up por defecto
    private String msjError = "";//mensaje del pop up por defecto

    /**
     * Redirecciona a la pagina de login despues de inactividad
     *
     * @param res objeto respuesta http
     * @param req objeto solicitud http
     */
    public void redireccionTimeOut(HttpServletResponse res, HttpServletRequest req) {
        try {
            tipoError = textosController.getNombreTexto("pnw.errores.tipo.timeOut", BDSUtil.CODIGO_CANAL_MOBILE);
            tituloError = textosController.getNombreTexto("pnw.errores.titulo.timeOut", BDSUtil.CODIGO_CANAL_MOBILE);
            msjError = textosController.getNombreTexto("pnw.errores.texto.timeOut", BDSUtil.CODIGO_CANAL_MOBILE);
            //res.sendRedirect(req.getContextPath() + parametrosController.getNombreParametro("pnw.global.url.error", BDSUtil.CODIGO_CANAL_MOBILE)+"?faces-redirect=true");
            ibCanalesDAO.actualizarUsuarioCanal(sesionController.getUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal(), String.valueOf(CODIGO_USUARIO_DESCONECTADO),
            0, "0", null, null, null, null, null, null);
            res.sendRedirect("http://www.delsur.com.ve/index.php" + "?faces-redirect=true");
        } catch (Exception e) {
            //this.redireccionGenerica(res, req);
        }
    }

    /**
     * Redirecciona a la pagina de inicio de sesion
     *
     * @param res objeto respuesta http
     * @param req objeto solicitud http
     */
    public void redireccionSesionMultiple(HttpServletResponse res, HttpServletRequest req) {

        try {
            tipoError = textosController.getNombreTexto("pnw.errores.tipo.multiSesion", BDSUtil.CODIGO_CANAL_MOBILE);
            tituloError = textosController.getNombreTexto("pnw.errores.titulo.multiSesion", BDSUtil.CODIGO_CANAL_MOBILE);
            msjError = textosController.getNombreTexto("pnw.errores.texto.multiSesion", BDSUtil.CODIGO_CANAL_MOBILE);
            res.sendRedirect(req.getContextPath() + parametrosController.getNombreParametro("pnw.global.url.error", BDSUtil.CODIGO_CANAL_MOBILE) + "?faces-redirect=true");
        } catch (Exception e) {
            this.redireccionGenerica(res, req);
        }

    }

    /**
     * Redireccion generica
     *
     * @param res objeto respuesta http
     * @param req objeto solicitud http
     */
    public void redireccionGenerica(HttpServletResponse res, HttpServletRequest req) {
        try {
            tipoError = textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", BDSUtil.CODIGO_CANAL_MOBILE);
            tituloError = textosController.getNombreTexto("pnw.errores.titulo.errorGenerico", BDSUtil.CODIGO_CANAL_MOBILE);
            msjError = textosController.getNombreTexto("pnw.global.error.generico", BDSUtil.CODIGO_CANAL_MOBILE);
            res.sendRedirect(req.getContextPath() + parametrosController.getNombreParametro("pnw.global.url.error", BDSUtil.CODIGO_CANAL_MOBILE) + "?faces-redirect=true");
        } catch (IOException e) {
            //la excepcion no puede ser controlada e inicaria error de URL en BD 
            //la misma se manejaria por la pagina de error mapeada en el web.xml
        }
    }

    /**
     * Asina los errores generados
     */
    public void iniciarErrores() {
        if (tipoError.equalsIgnoreCase("")) {
            tipoError = textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", BDSUtil.CODIGO_CANAL_MOBILE);
            tituloError = textosController.getNombreTexto("pnw.errores.titulo.errorGenerico", BDSUtil.CODIGO_CANAL_MOBILE);
            msjError = textosController.getNombreTexto("pnw.global.error.generico", BDSUtil.CODIGO_CANAL_MOBILE);
        }
    }

    /**
     * Metodo que permite cerrar la sesion en la aplicacion web y registra en
     * bitacora la transaccion
     *
     * @param mensaje mensaje para mostrar en el pop up
     * @return String
     */
    public String cerrarSesion(String mensaje) {
        if (sesionController.getUsuario() == null) {
            /*try {
                FacesContext.getCurrentInstance().getExternalContext().redirect((parametrosController.getNombreParametro("pnw.inicio.sesion.url", sesionController.getIdCanal())+"?faces-redirect=true"));

            } catch (IOException e) {

            }*/
            sesionController.setSesionInvalidada(true);
            return (parametrosController.getNombreParametro("pnw.inicio.sesion.url", sesionController.getIdCanal()) + "?faces-redirect=true");
        }
        RespuestaDTO respuestaDTO = ibCanalesDAO.actualizarUsuarioCanal(sesionController.getUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal(), String.valueOf(CODIGO_USUARIO_DESCONECTADO),
                0, "0", null, null, null, null, null, null);
        if (!respuestaDTO.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //agregar mensaje excepcion generica intente mas tarde
            sesionController.redireccionInicio((sesionController.mensajeVacio(mensaje) ? parametrosController.getNombreParametro("pnw.errores.texto.msjOperacion", sesionController.getIdCanal()) : mensaje));
        }//se validan las excepciones controladas
        if (respuestaDTO.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) && !respuestaDTO.getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
            //agregar mensaje de error interno
            sesionController.redireccionInicio((sesionController.mensajeVacio(mensaje) ? parametrosController.getNombreParametro("pnw.errores.texto.msjOperacion", sesionController.getNombreCanal()) : mensaje));
        }
        if (respuestaDTO.getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {

            //Registro en Bitacora
            if (!sesionController.mensajeVacio(mensaje)) {
                if (mensaje.equalsIgnoreCase(textosController.getNombreTexto("pnw.errores.titulo.multiSesion", sesionController.getNombreCanal()))) {
                    sesionController.notificarConexionSimultanea();
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_MULT_SESS, "", "", "Sesíon Multiple", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
                } else if (mensaje.equalsIgnoreCase(textosController.getNombreTexto("pnw.errores.titulo.timeOut", sesionController.getNombreCanal()))) {
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_TIME_OUT, "", "", "Sesión Expirada", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
                } else if (mensaje.equalsIgnoreCase(textosController.getNombreTexto("pnw.errores.titulo.errorGenerico", sesionController.getNombreCanal()))) {
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_ERROR_INESP, "", "", "Error Inesperado", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
                }
            } else {
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_CERRAR_SESION, "", "", "Cierre de Sesión", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
            }
            sesionController.setCerrarSesion(true);
        }
        sesionController.setSesionInvalidada(true);
        sesionController.redireccionHomeBankingExterno();
        return ("");
    }

    /**
     * Redireccion generica de las excepciones en los controladores
     */
    public void redireccionExcpController() {

        try {
            tipoError = textosController.getNombreTexto("pnw.errores.tipo.errorGenerico", BDSUtil.CODIGO_CANAL_MOBILE);
            tituloError = textosController.getNombreTexto("pnw.errores.titulo.errorGenerico", BDSUtil.CODIGO_CANAL_MOBILE);
            msjError = textosController.getNombreTexto("pnw.global.error.generico", BDSUtil.CODIGO_CANAL_MOBILE);
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.global.url.error", BDSUtil.CODIGO_CANAL_MOBILE) + "?faces-redirect=true");

        } catch (IOException e) {
            //la excepcion no puede ser controlada e inicaria error de URL en BD 
            //la misma se manejaria por la pagina de error mapeada en el web.xml
        }

    }

    /**
     * Invalida la session del contexto de JSF SOLO PARA INVOCAR DESDE EL
     * CONTENEDOR
     */
    public void invalidarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }

    /**
     * Otener el tipo de error
     *
     * @return String Tipo de Error
     */
    public String getTipoError() {
        return tipoError;
    }

    /**
     * Asigna el tipo de error
     *
     * @param tipoError String tipo de error
     */
    public void setTipoError(String tipoError) {
        this.tipoError = tipoError;
    }

    /**
     * Obtener titulo error
     *
     * @return
     */
    public String getTituloError() {
        return tituloError;
    }

    /**
     * Asignar titulo error
     *
     * @param tituloError string titulo
     */
    public void setTituloError(String tituloError) {
        this.tituloError = tituloError;
    }

    /**
     * Obtener mensaje de error
     *
     * @return string
     */
    public String getMsjError() {
        return msjError;
    }

    /**
     * Asinar mensaje de error
     *
     * @param msjError string
     */
    public void setMsjError(String msjError) {
        this.msjError = msjError;
    }

    /**
     * metodo para validar el tiempo de sesion entre interaciones al servidor
     */
    public void validarTiempoSesionInteracciones() {
        /*HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest(); 
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse(); 
        IbUsuariosCanalesDTO consultarUsuarioCanalporIdUsuario = ibCanalesDAO.consultarUsuarioCanalporIdUsuario(sesionController.getUsuario(), sesionController.getIdCanal());
        if (!request.getHeader("referer").contains("/ext/") && validarFechaExpiradaBD(parametrosFacade.obtenerFechaBD(sesionController.getNombreCanal()),consultarUsuarioCanalporIdUsuario.getFechaHoraUltimaInteraccionDate(), parametrosController.getValorParametro(BDSUtil.CODIGO_TIME_OUT, sesionController.getNombreCanal()))) {
            sesionController.setMostrarTimeOutDialog(true);
        }*/
    }

    /**
     * Redirecciona a la pagina de login despues de inactividad
     */
    public void redireccionTimeOutPopUp() {

        try {
            tipoError = textosController.getNombreTexto("pnw.errores.tipo.timeOut", BDSUtil.CODIGO_CANAL_MOBILE);
            tituloError = textosController.getNombreTexto("pnw.errores.titulo.timeOut", BDSUtil.CODIGO_CANAL_MOBILE);
            msjError = textosController.getNombreTexto("pnw.errores.texto.timeOut", BDSUtil.CODIGO_CANAL_MOBILE);
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.global.url.error", BDSUtil.CODIGO_CANAL_WEB) + "?faces-redirect=true");
            //this.invalidarSesion();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
