/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.IbClavesOperacionesEspecialesDAO;
import com.bds.wpn.enumerated.ClavesOperacionesEspecialesEnum;
import com.bds.wpn.util.BDSUtil;
import com.bds.wpn.util.DESedeEncryption;
import java.io.Serializable;
import java.util.Base64;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Logger;

/**
 *
 * @author jose.perez
 */
@Named("wpnClaveOperacioneEspecialesController")
@SessionScoped
public class ClaveOperacioneEspecialesController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;

    @Inject
    TextosController textosController;

    @EJB
    IbClavesOperacionesEspecialesDAO ibClavesOperacionesEspecialesDAO;

    @Inject
    ParametrosController parametrosController;

    private String claveOperacionesEspeciales;

    private final String TRANSF_TERCEROS_DEL_SUR = "pnw.submenu.transf.tercdelsur";
    private final String TRANSF_TERCEROS_OTROS_BCOS = "pnw.submenu.transf.tercotrobanc";
    private final String PAGO_TDC_DEL_SUR_2 = "pnw.submenu.pagos.tercdelsur";
    private final String PAGO_TDC_OTROS_BCOS = "pnw.submenu.pagos.tercotrobanc";
    private final String ACTUALIZA_DATOS_USUARIOS = "pnw.submenu.seguridad.actualizacionDatos";

    private String claveEnc;          //Respuesta codigo de operaciones especiales... al hacer submit se enmascaran
    private String respuestaClaveOP;
    private String claveEncEnv;

    /**
     * Log de sistema.
     */
    private static final Logger logger = Logger.getLogger(ClaveOperacioneEspecialesController.class.getName());
    
    public void initController() {
       this.claveEnc = "";
    }
    
    public String getClaveEnc() {
        return claveEnc;
    }

    public void setClaveEnc(String claveEnc) {
        this.claveEnc = claveEnc;
    }

    public String getClaveEncEnv() {
        return claveEncEnv;
    }

    public void setClaveEncEnv(String claveEncEnv) {
        this.claveEncEnv = claveEncEnv;
    }

    public String getClaveOperacionesEspeciales() {
        return claveOperacionesEspeciales;
    }

    public void setClaveOperacionesEspeciales(String claveOperacionesEspeciales) {
        this.claveOperacionesEspeciales = claveOperacionesEspeciales;
    }

    /**
     * metodo que se encarga de validar el codigo de operaciones especiales
     */
    public void doContinuar() {

        validarClaveOE();
        if (respuestaClaveOP.equals(ClavesOperacionesEspecialesEnum.ACTIVO.getDescripcion())) {
            sesionController.setValidadorFlujo(2);//Se coloca 2 para hacer la validacion de la url y verificar que el usuario no ingreso directo
            if (sesionController.getCodigoTransaccion().equals(TRANSF_TERCEROS_DEL_SUR)) {
                this.redirectFacesContext("/sec/transferencias/tdsPaso2.xhtml?faces-redirect=true");
            } else if (sesionController.getCodigoTransaccion().equals(TRANSF_TERCEROS_OTROS_BCOS)) {
                this.redirectFacesContext("/sec/transferencias/tobPaso2.xhtml?faces-redirect=true");
            } else if (sesionController.getCodigoTransaccion().equals(PAGO_TDC_DEL_SUR_2)) {
                this.redirectFacesContext("/sec/pagosTDC/tdsPaso2.xhtml?faces-redirect=true");
            } else if (sesionController.getCodigoTransaccion().equals(PAGO_TDC_OTROS_BCOS)) {
                this.redirectFacesContext("/sec/pagosTDC/tobPaso2.xhtml?faces-redirect=true");            
            } else if (sesionController.getCodigoTransaccion().equals(ACTUALIZA_DATOS_USUARIOS)) {
                this.redirectFacesContext("/sec/seguridad/actualizacionDatosPaso2.xhtml?faces-redirect=true");
            }
        } else if (respuestaClaveOP.equals(ClavesOperacionesEspecialesEnum.BLOQUEADO.getDescripcion())) {
            FacesContext.getCurrentInstance().addMessage("formOtp:clave", new FacesMessage(FacesMessage.SEVERITY_WARN, textosController.getNombreTexto("pnw.global.texto.claveOPBloqueado", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.claveOPBloqueado", sesionController.getNombreCanal())));
        } else if (respuestaClaveOP.equals(ClavesOperacionesEspecialesEnum.INCORRECTO.getDescripcion())) {
            FacesContext.getCurrentInstance().addMessage("formOtp:clave", new FacesMessage(FacesMessage.SEVERITY_WARN, textosController.getNombreTexto("pnw.global.texto.claveOPIncorrecta", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.claveOPIncorrecta", sesionController.getNombreCanal())));
        } else if (respuestaClaveOP.equals(ClavesOperacionesEspecialesEnum.NO_REGISTRADO.getDescripcion())) {
            FacesContext.getCurrentInstance().addMessage("formOtp:clave", new FacesMessage(FacesMessage.SEVERITY_WARN, textosController.getNombreTexto("pnw.global.texto.claveOPNoRegistrado", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.claveOPNoRegistrado", sesionController.getNombreCanal())));
        }
    }

    public String validarClaveOE() {
        desencriptarClaveWeb();
        if (!claveEncEnv.equals("")) {
            respuestaClaveOP = ibClavesOperacionesEspecialesDAO.validarClaveOperacionesEspeciales(
                    sesionController.getUsuario().getId().toString(),
                    this.encSHA256(claveEncEnv),
                    sesionController.getIdCanal(),
                    sesionController.getNombreCanal());
        }
        return respuestaClaveOP;
    }

    /**
     * metodo para encriptar texto en la capa web en 3des con una semilla de BD
     * para el ocultamiento de la data en la rafaga del request
     */
    public void encriptarClaveWeb() {
        //SE DESENCRIPTA LA CLAVE LA CUAL FUE CODIFICADA POR CADA CLIC
        //EN EL inicioSession.xhml configurado en el jqueryfull
        setClaveEnc(new String(Base64.getDecoder().decode(this.claveEnc)));
        if (this.claveEnc != null && !this.claveEnc.equals("")) {
            claveEncEnv = claveEnc;
            DESedeEncryption desEnc = new DESedeEncryption();
            desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal())));
            claveEncEnv = desEnc.encriptar(this.claveEncEnv);
        }
    }

    /**
     * metodo para desencriptar el texto en la capa web en 3des con una semilla
     * de BD
     */
    public void desencriptarClaveWeb() {
        DESedeEncryption desEnc = new DESedeEncryption();
        desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal())));
        this.setClaveEncEnv(desEnc.desencriptar(this.getClaveEncEnv()));
    }

    public boolean consultaExisteClaveOP() {
        boolean resultado = false;
        resultado = ibClavesOperacionesEspecialesDAO.consultarClaveOperacionesEspeciales(
                sesionController.getUsuario().getId().toString(),
                sesionController.getIdCanal(),
                sesionController.getNombreCanal());
        return resultado;
    }

    public void redirecCrearClaveOP() {
        sesionController.setNombreModulo(textosController.getNombreTexto("pnw.menu.modulo.seguridad", sesionController.getNombreCanal()));
        sesionController.setNombreTransaccion(textosController.getNombreTexto("pnw.submenu.seguridad.crearClaveOpeEsp", sesionController.getNombreCanal()));
        this.redirectFacesContext("/sec/seguridad/clave_especial_cre_vali_posi.xhtml");
    }

    public String getRespuestaClaveOP() {
        return respuestaClaveOP;
    }

    public void setRespuestaClaveOP(String respuestaClaveOP) {
        this.respuestaClaveOP = respuestaClaveOP;
    }
}
