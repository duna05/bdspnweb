/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.IbClavesOperacionesEspecialesDAO;
import com.bds.wpn.dto.FlujoExternaUsuarioDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.enumerated.ClavesOperacionesEspecialesEnum;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.ESTADO_CLAVE_OP;
import com.bds.wpn.util.DESedeEncryption;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author luis.perez
 */
@Named("wpnClaveEspecialModificarIngresoClaveController")
@SessionScoped
public class ClaveEspecialModificarIngresoClaveController extends BDSUtil implements Serializable {

    private FlujoExternaUsuarioDTO flujoExternaUsuarioDTO = new FlujoExternaUsuarioDTO();
    private String claveOP;
    private String confirmacionClaveOP;

    @Inject
    InicioSesionController sesionController;

    @Inject
    TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    @EJB
    IbClavesOperacionesEspecialesDAO ibClavesOperacionesEspecialesDAO;

    public void validarFlujo() {
        //EL USUARIO ESTA INTENTANDO INGRESAR DIRECTO A LA URL
        if (sesionController.getValidadorFlujo() != 2) {
            redirectFacesContext("/sec/seguridad/clave_especial_mo_vali_posi.xhtml");
        }
    }

    public void doContinuar() {
        UtilDTO utilDTO = new UtilDTO();
        boolean valida = false;
        int errores = 0;
//        this.encriptarClaveWeb(); // no va aqui
        this.desencriptarClaveWeb();
        if (validarAlfaNumerico(this.flujoExternaUsuarioDTO.getClaveOP())) {
            if ((validarFormaClave(this.flujoExternaUsuarioDTO.getClaveOP(), "formIngresoDeClaveOpEspeciales:txtClave")) && (validarFormaClave(this.flujoExternaUsuarioDTO.getConfirmacionClaveOP(), "formIngresoDeClaveOpEspeciales:txtConfirClave"))) {

                if (!validarClavesIgualesCambClv()) {
                    errores++;
                } else {

                    this.flujoExternaUsuarioDTO.setClaveOP(this.encSHA256(this.flujoExternaUsuarioDTO.getClaveOP()));
                    utilDTO = ibClavesOperacionesEspecialesDAO.insertarActualizarClaveOperacionesEspeciales(sesionController.getUsuario().getId().toString(), this.flujoExternaUsuarioDTO.getClaveOP(), sesionController.getUsuario().getId().toString(), false, sesionController.getIdCanal(), sesionController.getNombreCanal());

                    if (ClavesOperacionesEspecialesEnum.YA_REGISTRADO.getDescripcion().equals(utilDTO.getResuladosDTO().get(ESTADO_CLAVE_OP))) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formIngresoDeClaveOpEspeciales:txtConfirClave", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.claves.OP.3ultimas", sesionController.getIdCanal())));
                        errores++;
                    } else if (ClavesOperacionesEspecialesEnum.NO_REGISTRADO.getDescripcion().equals(utilDTO.getResuladosDTO().get(ESTADO_CLAVE_OP))) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formIngresoDeClaveOpEspeciales:txtConfirClave", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.claves.OP.Noregistrado", sesionController.getIdCanal())));
                        errores++;
                    } else if (!utilDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO) || !utilDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formIngresoDeClaveOpEspeciales:txtConfirClave", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("error.generico.msj", sesionController.getIdCanal())));
                        errores++;

                    }
                }
            } else {
                errores++;
            }
            if (errores > 0) {
                this.redirectFacesContext("/sec/seguridad/clave_especial_mo_ingreso_datos.xhtml");
                return;
            }
            redirectFacesContext("/sec/seguridad/clave_especial_mo_ejecutar_pro.xhtml");
            return;
        }else {
            this.redirectFacesContext("/sec/seguridad/clave_especial_mo_ingreso_datos.xhtml");
            return;
        }
    }

    private boolean validarAlfaNumerico(String value) {
        int valid = 0;
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (!value.matches("^[a-zA-Z0-9]*$")) {
            FacesContext.getCurrentInstance().addMessage("formIngresoDeClaveOpEspeciales:txtConfirClave", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.AlfaNumerico", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.AlfaNumerico", sesionController.getIdCanal())));
            valid++;
        }
        return valid == 0;
    }

    public void doRegresar() {
        redirectFacesContext("/sec/seguridad/clave_especial_mo_vali_posi.xhtml");
    }

    /**
     * Metodo para validar el la composicion del password
     *
     * @param name String
     * @param value Object
     * @return
     *
     */
    public boolean validarFormaClave(Object value, String name) {
        boolean valid = true;

        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            FacesContext.getCurrentInstance().addMessage("formIngresoDeClaveOpEspeciales:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getIdCanal())));
            valid = false;
        } else {
            if (value.toString().length() < 8) {//validacion de Longitud   
                FacesContext.getCurrentInstance().addMessage(name, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvOP", sesionController.getIdCanal())));
                valid = false;
            } else {
                if (!validarRegexParcial("([a-z]+)", value.toString())) {//La clave debe contener al menos una letra minúscula
                    FacesContext.getCurrentInstance().addMessage(name, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvMin", sesionController.getIdCanal())));
                    valid = false;
                } else {
                    if (!validarRegexParcial("([A-Z]+)", value.toString())) {//La clave debe contener al menos una letra mayúscula
                        FacesContext.getCurrentInstance().addMessage(name, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvMay", sesionController.getIdCanal())));
                        valid = false;
                    } else {
                        if (!validarRegexParcial("([0-9]+)", value.toString())) {//La clave debe contener al menos un número
                            FacesContext.getCurrentInstance().addMessage(name, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvNumero", sesionController.getIdCanal())));
                            valid = false;
                        }
                    }
                }
            }
        }
        return valid;
    }

    /**
     * Metodo para validar el campo motivo del formulario de transferencias.
     *
     *
     *
     * @return
     */
    public boolean validarClavesIgualesCambClv() {
        boolean valid = true;
        String value = flujoExternaUsuarioDTO.getConfirmacionClaveOP();
        String passwordField = flujoExternaUsuarioDTO.getClaveOP();
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        if (!passwordField.equalsIgnoreCase(value)) {
            FacesContext.getCurrentInstance().addMessage("formIngresoDeClaveOpEspeciales:txtConfirClave", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.claveDesigual", sesionController.getIdCanal())));
            valid = false;
        }
//else {
//            //Validacion de datos personales
//            UtilDTO utilDTO = clienteDAO.validarClaveFondo(ibUsuariosDTO.getCodUsuario(), value, sesionController.getNombreCanal(), sesionController.getIdCanal());
//            if (utilDTO == null || utilDTO.getRespuestaDTO() == null || utilDTO.getResuladosDTO() == null || !utilDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
//                FacesContext.getCurrentInstance().addMessage("formIngresoDeClaveOpEspeciales:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getIdCanal())));
//                valid = false;
//            } else {
//                if (utilDTO == null || utilDTO.getRespuestaDTO() == null || utilDTO.getResuladosDTO() == null || !utilDTO.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
//                    FacesContext.getCurrentInstance().addMessage("formIngresoDeClaveOpEspeciales:divMensaje", new FacesMessage(FacesMessage.SEVERITY_WARN, "", utilDTO.getRespuestaDTO().getTextoSP()));
//                    valid = false;
//                } else {
//                    if (!(Boolean) utilDTO.getResuladosDTO().get("resultado")) {
//                        FacesContext.getCurrentInstance().addMessage("formIngresoDeClaveOpEspeciales:divMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.clvDatosPers", sesionController.getIdCanal())));
//                        valid = false;
//                    }
//                }
//            }
//        }
        return valid;
    }

    /**
     * metodo para encriptar texto en la capa web en 3des con una semilla de BD
     * para el ocultamiento de la data en la rafaga del request
     */
    public void encriptarClaveWeb() {
        if (this.getClaveOP() != null && this.getConfirmacionClaveOP() != null) {
            DESedeEncryption desEnc = new DESedeEncryption();
            desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal())));
            this.flujoExternaUsuarioDTO.setClaveOP(desEnc.encriptar(this.getClaveOP()));
            this.flujoExternaUsuarioDTO.setConfirmacionClaveOP(desEnc.encriptar(this.getConfirmacionClaveOP()));
        }
    }

    /**
     * metodo para desencriptar el texto en la capa web en 3des con una semilla
     * de BD
     */
    public void desencriptarClaveWeb() {
        DESedeEncryption desEnc = new DESedeEncryption();
        desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal())));
        this.flujoExternaUsuarioDTO.setClaveOP(desEnc.desencriptar(this.flujoExternaUsuarioDTO.getClaveOP()));
        this.flujoExternaUsuarioDTO.setConfirmacionClaveOP(desEnc.desencriptar(this.flujoExternaUsuarioDTO.getConfirmacionClaveOP()));
    }

    public FlujoExternaUsuarioDTO getFlujoExternaUsuarioDTO() {
        return flujoExternaUsuarioDTO;
    }

    public void setFlujoExternaUsuarioDTO(FlujoExternaUsuarioDTO flujoExternaUsuarioDTO) {
        this.flujoExternaUsuarioDTO = flujoExternaUsuarioDTO;
    }

    public String getClaveOP() {
        return claveOP;
    }

    public void setClaveOP(String claveOP) {
        this.claveOP = claveOP;
    }

    public String getConfirmacionClaveOP() {
        return confirmacionClaveOP;
    }

    public void setConfirmacionClaveOP(String confirmacionClaveOP) {
        this.confirmacionClaveOP = confirmacionClaveOP;
    }
}
