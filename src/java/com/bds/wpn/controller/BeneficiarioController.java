/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.DelSurDAO;
import com.bds.wpn.dao.IbAfiliacionDAO;
import com.bds.wpn.dao.IbCanalesDAO;
import com.bds.wpn.dao.IbMenuDAO;
import com.bds.wpn.dao.TarjetaCreditoDAO;
import com.bds.wpn.dto.BancoDTO;
import com.bds.wpn.dto.DelSurDTO;
import com.bds.wpn.dto.IbAfiliacionesDTO;
import com.bds.wpn.dto.IbModulosDTO;
import com.bds.wpn.dto.IbParametrosDTO;
import com.bds.wpn.dto.IbTransaccionesDTO;
import com.bds.wpn.dto.IbUsuariosCanalesDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.FORMATO_FECHA_COMPLETA;
import static com.bds.wpn.util.BDSUtil.eliminarformatoSimpleMonto;
import com.bds.wpn.util.PaginarPDF;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author juan.faneite
 */
@Named("wpnBeneficiarioController")
@SessionScoped
public class BeneficiarioController extends BDSUtil implements Serializable {

    @EJB
    private TarjetaCreditoDAO tarjetaCreditoDAO;

    @Inject
    InicioSesionController sesionController;
    @EJB
    IbParametrosFacade parametrosFacade;
    @EJB
    IbTextosFacade textosFacade;
    @EJB
    IbAfiliacionDAO afiliacionDAO;
    @EJB
    IbMenuDAO menuDAO;
    @EJB
    DelSurDAO delSurDAO;
    @EJB
    ClienteDAO clienteDAO;
    @EJB
    IbCanalesDAO ibCanalesDAO;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;
    
    @Inject
    CanalController canalController;

    private IbAfiliacionesDTO afiliacionesDTO = new IbAfiliacionesDTO();
    private IbAfiliacionesDTO afiliacionesDTOAfiliar = new IbAfiliacionesDTO();
    private List<IbAfiliacionesDTO> afiliacionesDTOporEliminar;
    private IbTransaccionesDTO transaccionesDTO;
    private List<SelectItem> listSelectTrans;
    private String selectTransSelected;
    private List<IbAfiliacionesDTO> listSelected;
    private String codigoOTP = "";
    private String montoMaximo;
    private BancoDTO bancoSelected;
    private IbAfiliacionesDTO afiliacionSelected;
    private boolean botonDesafiliar;
    private boolean modificaBeneficiario;
    private boolean eliminaBeneficiario;

    private String nombreBanco;
    private String codigoBanco;

    private boolean mantenerDatosForm = false;              //indicador para mantener los datos del formulario

    private boolean insertarBeneficiario;
    private Date fechaEjecucion;

    public boolean isEliminaBeneficiario() {
        return eliminaBeneficiario;
    }

    public void setEliminaBeneficiario(boolean eliminaBeneficiario) {
        this.eliminaBeneficiario = eliminaBeneficiario;
    }

    public boolean isInsertarBeneficiario() {
        return insertarBeneficiario;
    }

    public void setInsertarBeneficiario(boolean insertarBeneficiario) {
        this.insertarBeneficiario = insertarBeneficiario;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        if (nombreBanco != null && nombreBanco.contains("--")) {
            this.nombreBanco = nombreBanco;
            afiliacionesDTOAfiliar.setNombreBanco(nombreBanco);
        }
    }

    public String getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(String codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    public boolean isMantenerDatosForm() {
        return mantenerDatosForm;
    }

    public void setMantenerDatosForm(boolean mantenerDatosForm) {
        this.mantenerDatosForm = mantenerDatosForm;
    }

    public IbAfiliacionesDTO getAfiliacionSelected() {
        return afiliacionSelected;
    }

    public void setAfiliacionSelected(IbAfiliacionesDTO afiliacionSelected) {
        this.afiliacionSelected = afiliacionSelected;
    }

    public BancoDTO getBancoSelected() {
        return bancoSelected;
    }

    public void setBancoSelected(BancoDTO bancoSelected) {
        this.bancoSelected = bancoSelected;
    }

    public String getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(String montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    public boolean isBotonDesafiliar() {
        return botonDesafiliar;
    }

    public void setBotonDesafiliar(boolean botonDesafiliar) {
        this.botonDesafiliar = botonDesafiliar;
    }

    public boolean isModificaBeneficiario() {
        return modificaBeneficiario;
    }

    public void setModificaBeneficiario(boolean modificaBeneficiario) {
        this.modificaBeneficiario = modificaBeneficiario;
    }

    public IbAfiliacionesDTO getAfiliacionesDTOAfiliar() {
        if (afiliacionesDTOAfiliar == null) {
            afiliacionesDTOAfiliar = new IbAfiliacionesDTO();
        }
        return afiliacionesDTOAfiliar;
    }

    public void setAfiliacionesDTOAfiliar(IbAfiliacionesDTO afiliacionesDTOAfiliar) {
        this.afiliacionesDTOAfiliar = afiliacionesDTOAfiliar;
    }

    public String getCodigoOTP() {
        return codigoOTP;
    }

    public void setCodigoOTP(String codigoOTP) {
        this.codigoOTP = codigoOTP;
    }

    public List<IbAfiliacionesDTO> getListSelected() {
        return listSelected;
    }

    public void setListSelected(List<IbAfiliacionesDTO> listSelected) {
        this.listSelected = listSelected;
    }

    public List<IbAfiliacionesDTO> getAfiliacionesDTOporEliminar() {
        return afiliacionesDTOporEliminar;
    }

    public void setAfiliacionesDTOporEliminar(List<IbAfiliacionesDTO> afiliacionesDTOporEliminar) {
        this.afiliacionesDTOporEliminar = afiliacionesDTOporEliminar;
    }

    public List<SelectItem> getListSelectTrans() {
        return listSelectTrans;
    }

    public void setListSelectTrans(List<SelectItem> listSelectTrans) {
        this.listSelectTrans = listSelectTrans;
    }

    public String getSelectTransSelected() {
        return selectTransSelected;
    }

    public void setSelectTransSelected(String selectTransSelected) {
        this.selectTransSelected = selectTransSelected;
    }

    public IbAfiliacionesDTO getAfiliacionesDTO() {
        return afiliacionesDTO;
    }

    public void setAfiliacionesDTO(IbAfiliacionesDTO afiliacionesDTO) {
        this.afiliacionesDTO = afiliacionesDTO;
    }

    public IbTransaccionesDTO getTransaccionesDTO() {
        return transaccionesDTO;
    }

    public void setTransaccionesDTO(IbTransaccionesDTO transaccionesDTO) {
        this.transaccionesDTO = transaccionesDTO;
    }

    public void seleccionarBco() {
        this.codigoBanco = this.afiliacionesDTOAfiliar.getCodBanco();
    }

    public Date getFechaEjecucion() {
        return new Date();
    }

    public void setFechaEjecucion(Date fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }


    /**
     * Metodo para validar el campo de 20 digitos
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validar20Digitos(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        String formulario;
        //Validar si viene de afiliar o modificar afiliado
        if (!modificaBeneficiario) {
            formulario = "formAfiliar2";
        } else {
            formulario = "formModificarBeneficiario1";
        }

        //validamos que tenga seleccionada un tipo de afiliacion
        UIInput tipoAfiliacionField = (UIInput) context.getViewRoot().findComponent(formulario + ":selectTransSelectedaccion");
        if (!modificaBeneficiario) {
            if (tipoAfiliacionField == null) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("Debe Seleccionar el tipo de Afiliacion.", sesionController.getIdCanal()), textosController.getNombreTexto("Debe Seleccionar el Banco.", sesionController.getIdCanal())));
            } else {
                //si el tipo de afiliacion es transferencias a otros bancos o propias o de terceros
                if (tipoAfiliacionField.getValue().toString().equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS) || tipoAfiliacionField.getValue().toString().equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS)) {

                    UIInput codBancoField = (UIInput) context.getViewRoot().findComponent("formAfiliar2:selectBanco");
                    if (codBancoField == null) {
                        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("Debe Seleccionar el Banco.", sesionController.getIdCanal()), textosController.getNombreTexto("Debe Seleccionar el Banco.", sesionController.getIdCanal())));
                    } else {
                        /*if (!value.toString().substring(0, 4).equalsIgnoreCase(codBancoField.getValue().toString())) {
                         throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getIdCanal())));
                         }*/
                    }
                }
                //si el tipo de afiliacion es transferencias a terc del sur
                if (tipoAfiliacionField.getValue().toString().equalsIgnoreCase(ID_TRANS_CTAS_TERC_DELSUR)) {

                    String binDelSur = parametrosController.getNombreParametro("pnw.global.bin.nroCta", sesionController.getIdCanal());
                    if (!value.toString().substring(0, 4).equalsIgnoreCase(binDelSur)) {
                        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getIdCanal())));
                    }

                }
                //esta validacion aplica para num de CTA o de TDC
                if (value == null || value.toString().isEmpty() || value.equals("")) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getIdCanal())));
                } else if (!value.toString().matches("^\\d{20}$")) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getIdCanal())));
                }
            }
        } else {

            //esta validacion aplica para num de CTA o de TDC
            if (value == null || value.toString().isEmpty() || value.equals("")) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getIdCanal())));
            } else if (!value.toString().matches("^\\d{20}$")) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getIdCanal())));
            }
        }

    }
    
    
    /**
     * Metodo para validar identificacion solo numeros
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     * @throws ValidatorException
     */
    public void validarIdentificacion(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null || value.toString().isEmpty() || value.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", BDSUtil.CODIGO_CANAL_WEB_PN)));
        }

        UIInput uiInput = (UIInput) component.findComponent("selectTipoDoc");

        if (uiInput.getLocalValue() == null || uiInput.getLocalValue().toString().isEmpty() || uiInput.getLocalValue().toString().equalsIgnoreCase("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", BDSUtil.CODIGO_CANAL_WEB_PN)));
        }

        String selectTipoDoc = uiInput.getLocalValue().toString();

        if (selectTipoDoc.equalsIgnoreCase("V") || selectTipoDoc.equalsIgnoreCase("E")) {
            if (!value.toString().matches("^[0-9]{5,9}$")) {
                throw new ValidatorException(new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.IdentifInv", BDSUtil.CODIGO_CANAL_WEB_PN)));
            }
        } else {
            if (selectTipoDoc.equalsIgnoreCase("P")) {
                if (!value.toString().matches("^[0-9a-zA-Z]{5,9}$")) {
                    throw new ValidatorException(new FacesMessage(
                            FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.IdentifInv", BDSUtil.CODIGO_CANAL_WEB_PN)));
                }
            } else {
                if (selectTipoDoc.equalsIgnoreCase("J") || selectTipoDoc.equalsIgnoreCase("G") || selectTipoDoc.equalsIgnoreCase("C")) {

                    if (!value.toString().matches("^[0-9]{5,9}$")) {
                        throw new ValidatorException(new FacesMessage(
                                FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.rifInvalido", BDSUtil.CODIGO_CANAL_WEB_PN)));
                    }

                    UtilDTO util = new UtilDTO();

                    util = clienteDAO.validarRif(selectTipoDoc + value, BDSUtil.CODIGO_CANAL_WEB_PN);

                    if (!util.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                        throw new ValidatorException(new FacesMessage(
                                FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", BDSUtil.CODIGO_CANAL_WEB_PN)));
                    } else {
                        if (!util.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                            throw new ValidatorException(new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR, "", util.getRespuestaDTO().getTextoSP()));
                        } else {
                            if (util.getResuladosDTO().get("resultado").toString().trim().equalsIgnoreCase("N")) {
                                throw new ValidatorException(new FacesMessage(
                                        FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.rifInvalido", BDSUtil.CODIGO_CANAL_WEB_PN)));
                            }
                        }
                    }
                }
            }
        }
    }
    
    public List<SelectItem> listTrans() {
        listSelectTrans = new ArrayList<>();
        List<IbModulosDTO> modulos = new ArrayList<>(menuDAO.obtenerModeloTransBeneficiarioFiltrada(sesionController.getIdCanal(), sesionController.getNombreCanal()));

        for (IbModulosDTO m : modulos) {
            //llenado del select
            SelectItemGroup selectGroup = new SelectItemGroup(textosController.getNombreTexto(m.getNombre(), sesionController.getIdCanal()) + ":");

            SelectItem[] selectItemTemp = new SelectItem[m.getIbTransaccionesDTOCollection().size()];
            int cont = 0;
            for (IbTransaccionesDTO t : m.getIbTransaccionesDTOCollection()) {
                selectItemTemp[cont++] = new SelectItem(t.getIdCore().toString(), textosController.getNombreTexto(t.getNombre(), sesionController.getIdCanal()));
            }

            selectGroup.setSelectItems(selectItemTemp);

            listSelectTrans.add(selectGroup);
        }

        return listSelectTrans;
    }

    /**
     *
     * @param beneficiario metodo para redirigir editar beneficiario
     */
    public void editarBeneficiario(IbAfiliacionesDTO beneficiario) {
        afiliacionSelected = new IbAfiliacionesDTO();
        afiliacionSelected = beneficiario;
        this.montoMaximo = formatearMonto(afiliacionSelected.getMontoMaximo());
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
        afiliacionSelected.setNombreBanco(afiliacionSelected.getNombreBanco() + "--" + afiliacionSelected.getNumeroCuenta().substring(0, 4));
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.modificarBeneficiario.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * metodo para redirigir a paso 2
     */
    public void listPaso2() {
        this.mantenerDatosForm = false;
        limpiarBeneficiario();
        sesionController.setValidacionOTP(2);
        sesionController.setValidadorFlujo(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.submenu.servicios.listBeneficiarios", sesionController.getIdCanal()));
    }

    /**
     * metodo para redirigir a paso 2
     */
    public void regresarListPaso2() {
        this.mantenerDatosForm = false;
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() - 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.submenu.servicios.listBeneficiarios", sesionController.getIdCanal()));
    }

    /**
     * Método que redirecciona a modificarBeneficiarioPaso1
     */
    public void modificarBeneficiarioPaso1() {
        modificaBeneficiario = true;
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.modificarBeneficiario.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * Método que redirecciona a modificarBeneficiarioPaso1
     */
    public void regresarModificarBeneficiarioPaso1() {
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() - 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.modificarBeneficiario.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * Método que redirecciona a modificarBeneficiarioPaso2
     */
    public void modificarBeneficiarioPaso2() {
        UtilDTO utilDTO = new UtilDTO();
        int contError = 0;
        int validaciones = this.validarAfiliaciones();
        modificaBeneficiario = true;

        this.afiliacionSelected.setNombreBanco(this.afiliacionSelected.getNombreBancoCompleto());

        if (validaciones == 0) {

            IbAfiliacionesDTO afilDTO = afiliacionDAO.obtenerAfiliacionPorId(this.afiliacionSelected.getId().toString(), sesionController.getNombreCanal());
            if (!afilDTO.getAlias().equalsIgnoreCase(this.afiliacionSelected.getAlias())) {
                utilDTO = afiliacionDAO.verificarAlias(sesionController.getUsuario().getCodUsuario(), this.afiliacionSelected.getAlias(), sesionController.getIdCanal(), sesionController.getNombreCanal());
                if (!utilDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    contError++;
                    //ERROR AL VERIFICAR EL ALIAS
                    FacesContext.getCurrentInstance().addMessage("formModificarBeneficiario1:modificar", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                } else if ((boolean) utilDTO.getResuladosDTO().get("resultado")) {
                    contError++;
                    if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS)) {//7
                        FacesContext.getCurrentInstance().addMessage("formModificarBeneficiario1:txtAliasTransP", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal())));
                    } else {
                        if (selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_P_OTROS_BCOS)) {//8
                            FacesContext.getCurrentInstance().addMessage("formModificarBeneficiario1:txtAliasP", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal())));
                        } else {
                            if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS)) {//3
                                FacesContext.getCurrentInstance().addMessage("formModificarBeneficiario1:txtAliasTOB", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal())));
                            } else {
                                if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_TERC_DELSUR)) {//1
                                    FacesContext.getCurrentInstance().addMessage("formModificarBeneficiario1:txtAliasTDS", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal())));
                                } else {
                                    FacesContext.getCurrentInstance().addMessage("formModificarBeneficiario1:txtAlias", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal())));
                                }
                            }
                        }

                    }
                }
            }

            IbUsuariosCanalesDTO usuarioCanal = ibCanalesDAO.consultarUsuarioCanalporIdUsuario(sesionController.getUsuario(), sesionController.getNombreCanal());
            //si el tipo de afiliacion es transferencias a terc del sur
            if (this.selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_TERC_DELSUR)) {

                utilDTO = clienteDAO.validarIdentificacionCuenta(String.valueOf(this.afiliacionSelected.getTipoDoc()) + this.afiliacionSelected.getDocumento(), this.afiliacionSelected.getNumeroCuenta(), sesionController.getIdCanal(), sesionController.getNombreCanal());
                if (utilDTO.getRespuestaDTO() != null && !utilDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    contError++;
                    //ERROR AL FALLAR EL SERVICIO
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formModificarBeneficiario1:modificar", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                } else {
                    if (utilDTO.getResuladosDTO().get("resultado").equals("F") || utilDTO.getResuladosDTO().get("resultado").equals(false)) {
                        contError++;
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formModificarBeneficiario1:txtNroCtaTDS", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.afiliar.texto.ctaInvl", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.afiliar.texto.ctaInvl", sesionController.getNombreCanal())));
                    }
                }
            }

            // validacion para transacciones terceros otros bancos
            if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS)) {

                boolean validaBanco;

                validaBanco = sesionController.validarCuentaCodigoBanco(afiliacionSelected.getCodBanco(), afiliacionSelected.getNumeroCuenta());
                if (!validaBanco) {
                    contError++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formModificarBeneficiario1:txtNroCtaTOB", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getNombreCanal())));
                }
            }
            // validacion para transacciones propias otros bancos
            if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS)) {

                boolean validaBanco;

                validaBanco = sesionController.validarCuentaCodigoBanco(afiliacionSelected.getCodBanco(), afiliacionSelected.getNumeroCuenta());
                if (!validaBanco) {
                    contError++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formModificarBeneficiario1:txtNroCtaPOB", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getNombreCanal())));
                }
            }

            this.afiliacionSelected.setMontoMaximo(new BigDecimal(eliminarformatoSimpleMonto(montoMaximo)));
            if (contError == 0) {
                sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.modificarBeneficiario.url.paso2", sesionController.getIdCanal()));
            }
        }
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.modificarBeneficiario.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * Método que redirecciona a modificarBeneficiarioPaso3
     */
    public void modificarBeneficiarioPaso3() {
        RespuestaDTO respuesta = new RespuestaDTO();
        int validaciones = this.validarAfiliaciones();
        String selectTransNombre = null;
        if (validaciones == 0) {
            if (this.afiliacionSelected != null && (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS))) {
                respuesta = afiliacionDAO.actualizarAfiliacion(afiliacionSelected.getId().toString(), afiliacionSelected.getAlias(), afiliacionSelected.getTipoDoc(), afiliacionSelected.getDocumento(), afiliacionSelected.getEmail(), afiliacionSelected.getMontoMaximo().toString(), afiliacionSelected.getNombreBeneficiario(), afiliacionSelected.getNombreBanco(), afiliacionSelected.getNumeroCuenta(), sesionController.getIdCanal(), sesionController.getNombreCanal());
            } else if (this.afiliacionSelected != null && selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_P_OTROS_BCOS)) {
                respuesta = afiliacionDAO.actualizarAfiliacion(afiliacionSelected.getId().toString(), afiliacionSelected.getAlias(), afiliacionSelected.getTipoDoc(), null, afiliacionSelected.getEmail(), afiliacionSelected.getMontoMaximo().toString(), null, null, null, sesionController.getIdCanal(), sesionController.getNombreCanal());
            } else if (this.afiliacionSelected != null && (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS))) {
                respuesta = afiliacionDAO.actualizarAfiliacion(afiliacionSelected.getId().toString(), afiliacionSelected.getAlias(), afiliacionSelected.getTipoDoc(), afiliacionSelected.getDocumento(), afiliacionSelected.getEmail(), afiliacionSelected.getMontoMaximo().toString(), afiliacionSelected.getNombreBeneficiario(), afiliacionSelected.getNombreBanco(), afiliacionSelected.getNumeroCuenta(), sesionController.getIdCanal(), sesionController.getNombreCanal());
            } else if (this.afiliacionSelected != null && (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_TERC_DELSUR))) {
                respuesta = afiliacionDAO.actualizarAfiliacion(afiliacionSelected.getId().toString(), afiliacionSelected.getAlias(), afiliacionSelected.getTipoDoc(), afiliacionSelected.getDocumento(), afiliacionSelected.getEmail(), afiliacionSelected.getMontoMaximo().toString(), afiliacionSelected.getNombreBeneficiario(), afiliacionSelected.getNombreBanco(), afiliacionSelected.getNumeroCuenta(), sesionController.getIdCanal(), sesionController.getNombreCanal());
            } else if (this.afiliacionSelected != null && selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_T_OTROS_BCOS) || this.afiliacionSelected != null && selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_TERC_DELSUR)) {
                respuesta = afiliacionDAO.actualizarAfiliacion(afiliacionSelected.getId().toString(), afiliacionSelected.getAlias(), afiliacionSelected.getTipoDoc(), null, afiliacionSelected.getEmail(), afiliacionSelected.getMontoMaximo().toString(), afiliacionSelected.getNombreBeneficiario(), null, null, sesionController.getIdCanal(), sesionController.getNombreCanal());
            }

            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL LISTADO DE PREGUNTAS DE DESAFIO
                FacesContext.getCurrentInstance().addMessage("formModificarBeneficiario2:modificar", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                insertarBeneficiario = false;
                modificaBeneficiario = false;
                eliminaBeneficiario = false;
            } else {
                modificaBeneficiario = true;
                insertarBeneficiario = false;
                eliminaBeneficiario = false;
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_LISTBENEF, "", "", "Modificación de Beneficiarios", "", "", "", "", "", "", "");

                List<SelectItem> listTrans = this.listTrans();

                for (SelectItem i : listTrans) {
                    for (SelectItem j : ((SelectItemGroup) i).getSelectItems()) {
                        if (j.getValue().equals(selectTransSelected)) {
                            selectTransNombre = capitalizarTexto(i.getLabel()) + j.getLabel();
                            break;
                        }
                    }

                }

                /////////NOTIFICACION VIA SMS////////
                String textoSMS = textosController.getNombreTexto("pnw.global.texto.smsModifBenef", sesionController.getNombreCanal());
                String motivoSMS = textosController.getNombreTexto("pnw.menu.modulo.servicios", sesionController.getNombreCanal());
                HashMap<String, String> parametros = new HashMap();
                parametros.put("\\$PFEC", this.formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                parametros.put("\\$PTRANS", selectTransNombre);
                parametros.put("\\$PTELF", parametrosController.getNombreParametro("pnw.afiliar.sms.telf", sesionController.getIdCanal()));
                sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);

                sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.modificarBeneficiario.url.paso3", sesionController.getIdCanal()));
            }
        }
    }

    /**
     * metodo para redirigir a paso 1 de afiliar
     */
    public void afiliarPaso1() {
        modificaBeneficiario = false;
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliar.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * metodo para redirigir a paso 2 de afiliar
     */
    public void afiliarPaso2() {
        this.modificaBeneficiario = false;
        limpiarBeneficiario();
        sesionController.setValidadorFlujo(2);
        sesionController.setValidacionOTP(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliar.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo para redirigir a paso 2 de afiliar
     */
    public void regresarAfiliarPaso2() {
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() - 1);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliar.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo para redirigir a paso 3 de afiliar
     *
     */
    public void afiliarPaso3() {
        if (this.selectTransSelected == null || this.selectTransSelected.equalsIgnoreCase("-1")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliar2:selectTransSelectedaccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliar.url.paso2", sesionController.getIdCanal()));
            return;
        }
        UtilDTO utilDTO = new UtilDTO();
        UtilDTO utilDTO_2 = new UtilDTO();
        int contError = 0;
        String codigoBanco;
        boolean validaBanco;
        int validaciones = this.validarAfiliaciones();
        String campoFormulario = null;

        if (validaciones == 0) {

            utilDTO = afiliacionDAO.verificarAlias(sesionController.getUsuario().getCodUsuario(), this.afiliacionesDTOAfiliar.getAlias(), sesionController.getIdCanal(), sesionController.getNombreCanal());
            utilDTO_2 = afiliacionDAO.verificarProducto(sesionController.getUsuario().getCodUsuario(), this.afiliacionesDTOAfiliar.getNumeroCuenta(), sesionController.getIdCanal(), sesionController.getNombreCanal());
            if ((utilDTO.getRespuestaDTO() != null && !utilDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) || (utilDTO_2.getRespuestaDTO() != null && !utilDTO_2.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO))) {
                contError++;
                //ERROR AL INTENTAR ACTUALIZAR EL LISTADO DE PREGUNTAS DE DESAFIO
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formAfiliar2:afiliacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else if ((boolean) utilDTO.getResuladosDTO().get("resultado")) {
                contError++;
                if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS) || selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_P_OTROS_BCOS)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliar2:txtAliasP", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal())));
                } else {
                    if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS)) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formAfiliar2:txtAlias", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal())));
                    } else {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("formAfiliar2:txtAlias", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.afiliar.texto.aliasExiste", sesionController.getNombreCanal())));
                    }
                }
                /**
                 * validacion para que no se puede registrar un prodicto
                 * previamente registrado.
                 */
            } else if ((boolean) utilDTO_2.getResuladosDTO().get("resultado")) {
                contError++;
                if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliar2:txtNroCtaP", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.gobal.texto.afliliacionDuplicada", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.gobal.texto.afliliacionDuplicada", sesionController.getNombreCanal())));
                } else if (selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_P_OTROS_BCOS)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliar2:txtNroTdcP", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.gobal.texto.afliliacionDuplicada", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.gobal.texto.afliliacionDuplicada", sesionController.getNombreCanal())));
                } else if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS) || selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_TERC_DELSUR)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliar2:txtNroCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.gobal.texto.afliliacionDuplicada", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.gobal.texto.afliliacionDuplicada", sesionController.getNombreCanal())));
                } else if (selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_T_OTROS_BCOS) || selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_TERC_DELSUR)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliar2:txtNroTdc", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.gobal.texto.afliliacionDuplicada", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.gobal.texto.afliliacionDuplicada", sesionController.getNombreCanal())));
                }
            }
            /**
             * validacion para transacciones terceros otros bancos
             */
            if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS) || selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS)) {
//            DESCOMENTAR PARA VALIDAR CUENTA CON BANCO PARA TRANSACCIONES TERCEROS OTROS BANCOS
                codigoBanco = this.afiliacionesDTOAfiliar.getNumeroCuenta().substring(0, 4);

                String binDelSur = parametrosController.getNombreParametro("pnw.global.bin.nroCta", sesionController.getIdCanal());
                if (codigoBanco.equalsIgnoreCase(binDelSur)) {
                    contError++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliar2:txtNroCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.afiliar.texto.ctaInvl", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.afiliar.texto.bancoInvl", sesionController.getNombreCanal())));
                }
            }

            /**
             * validacion para transacciones terceros del sur
             */
            if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_TERC_DELSUR) || selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_TERC_DELSUR)) {
                UtilDTO util;
                if (selectTransSelected.equalsIgnoreCase("1")) {
                    campoFormulario = "formAfiliar2:txtNroCta";
                    utilDTO = clienteDAO.validarIdentificacionCuenta(String.valueOf(this.afiliacionesDTOAfiliar.getTipoDoc()) + this.afiliacionesDTOAfiliar.getDocumento(), this.afiliacionesDTOAfiliar.getNumeroCuenta(), sesionController.getIdCanal(), sesionController.getNombreCanal());
                } else {
                    campoFormulario = "formAfiliar2:txtNroTdc";
                    util = clienteDAO.obtenerCodCliente(String.valueOf(this.afiliacionesDTOAfiliar.getTipoDoc()) + this.afiliacionesDTOAfiliar.getDocumento(), sesionController.getNombreCanal(), sesionController.getIdCanal());

                    if (util != null && util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getResuladosDTO() != null) {
                        utilDTO = tarjetaCreditoDAO.obtenerClienteTarjeta(util.getResuladosDTO().get("codCliente").toString(), this.afiliacionesDTOAfiliar.getNumeroCuenta(), sesionController.getIdCanal(), sesionController.getNombreCanal());
                    } else {
                        utilDTO.setResuladosDTO(new HashMap<String, String>() {
                            {
                                put("resultado", "F");
                            }
                        });
                    }
                }
                if (utilDTO.getRespuestaDTO() != null && !utilDTO.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    contError++;
                    //ERROR AL INTENTAR ACTUALIZAR EL LISTADO DE PREGUNTAS DE DESAFIO
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliar2:afiliacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                } else {
                    if (utilDTO.getResuladosDTO().get("resultado").equals("F") || utilDTO.getResuladosDTO().get("resultado").equals(false)) {
                        contError++;
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage(campoFormulario, new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.afiliar.texto.ctaInvl", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.afiliar.texto.ctaInvl", sesionController.getNombreCanal())));
                    }
                }
                if (sesionController.productosCliente(afiliacionesDTOAfiliar.getNumeroCuenta())) {
                    contError++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage(campoFormulario, new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.productoPropio", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.productoPropio", sesionController.getNombreCanal())));
                }

            }

            if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS)) {
                campoFormulario = "formAfiliar2:txtNroCtaP";
            } else {
                if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS)) {
                    campoFormulario = "formAfiliar2:txtNroCta";
                }
            }

            if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS) || selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS)) {
                validaBanco = sesionController.validarCuentaCodigoBanco(afiliacionesDTOAfiliar.getCodBanco(), afiliacionesDTOAfiliar.getNumeroCuenta());
                if (!validaBanco) {
                    contError++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage(campoFormulario, new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getNombreCanal())));
                    // throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.ctaInvl", sesionController.getIdCanal())));
                }
            }

            this.afiliacionesDTOAfiliar.setMontoMaximo(new BigDecimal(eliminarformatoSimpleMonto(montoMaximo)));
            if (contError == 0) {
                sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliar.url.paso3", sesionController.getIdCanal()));

            }
        }

        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliar.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * Metodo que se encarga de realizar las validaciones de negocio de los
     * afiliaciones de beneficiarios
     *
     * @return int
     */
    public int validarAfiliaciones() {

        FacesMessage fMsg;
        int errores = 0;
        String formulario;
        //monto superior a 1 Bolivar
//        if (Double.valueOf(montoMaximo) < Double.valueOf("1.01")) {
//            fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoInv", sesionController.getNombreCanal()).getMensajeTecnico(),
//                    textosFacade.findByCodigo("pnw.global.texto.mtoInv", sesionController.getNombreCanal()).getMensajeUsuario());
//            FacesContext.getCurrentInstance().addMessage("formAfiliar2:txtMonto", fMsg);
//            errores++;
//        }

        //Validar si viene de afiliar o modificar afiliado
        if (!modificaBeneficiario) {
            formulario = "formAfiliar2";
        } else {
            formulario = "formModificarBeneficiario1";
        }

        //Valida los Límites, Terceros del Sur
        if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_TERC_DELSUR) || selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_TERC_DELSUR)) {
            //Terceros Otros Bancos, Límite de Afiliación
            if (this.montoMaximo != null && this.montoMaximo.compareTo(BigDecimal.ZERO.toString()) > 0) {

                //Terceros del Sur, Límites del Usuario por Canal
                if (sesionController.getUsuarioCanal().getLimiteInternasTerceros().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.montoMaximo))) < 0) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                            textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                    if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_TERC_DELSUR)) {
                        FacesContext.getCurrentInstance().addMessage(formulario + ":txtMontoMax", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                    } else { 
                        FacesContext.getCurrentInstance().addMessage(formulario + ":txtMontoMax", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                    }
                    errores++;
                } else {

                    //Se consulta el limite del banco
                    IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.tercerosdelsur", sesionController.getIdCanal());

                    //Terceros del Sur, Límite del Banco
                    if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.montoMaximo))) < 0) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTDS", sesionController.getNombreCanal()).getMensajeTecnico(),
                                textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTDS", sesionController.getNombreCanal()).getMensajeUsuario());
                        if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_TERC_DELSUR)) {
                            FacesContext.getCurrentInstance().addMessage(formulario + ":txtMontoMaxTDS", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                        } else {
                            FacesContext.getCurrentInstance().addMessage(formulario + ":txtMontoMax", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                        }
                        errores++;
                    }
                }
            }
        }

        //Valida los Límites, Propias Otros Bancos
        if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS) || selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_P_OTROS_BCOS)) {
            //Terceros Otros Bancos, Límite de Afiliación
            if (this.montoMaximo != null && this.montoMaximo.compareTo(BigDecimal.ZERO.toString()) > 0) {

                //Propias Otros Bancos, Límites del Usuario por Canal
                if (sesionController.getUsuarioCanal().getLimiteExternas().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.montoMaximo))) < 0) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                            textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                    if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS)) {
                        FacesContext.getCurrentInstance().addMessage(formulario + ":txtMontoMaxTransP", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                    } else {
                        FacesContext.getCurrentInstance().addMessage(formulario + ":txtMontoMaxP", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                    }
                    errores++;
                } else {

                    //Se consulta el límite del banco
                    IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.propiasotrosbancos", sesionController.getIdCanal());

                    //Propias Otros Bancos, Límite del Banco
                    if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.montoMaximo))) < 0) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPOB", sesionController.getNombreCanal()).getMensajeTecnico(),
                                textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoPOB", sesionController.getNombreCanal()).getMensajeUsuario());
                        if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS)) {
                            FacesContext.getCurrentInstance().addMessage(formulario + ":txtMontoMaxTransP", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                        } else {
                            FacesContext.getCurrentInstance().addMessage(formulario + ":txtMontoMaxP", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                        }
                        errores++;
                    }
                }
            }
        }

        //Valida los Límites, Terceros Otros Bancos
        if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS) || selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_T_OTROS_BCOS)) {
            //Terceros Otros Bancos, Límite de Afiliación
            if (this.montoMaximo != null && this.montoMaximo.compareTo(BigDecimal.ZERO.toString()) > 0) {

                //Terceros Otros Bancos, Límites del Usuario por Canal
                if (sesionController.getUsuarioCanal().getLimiteExternasTerceros().compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.montoMaximo))) < 0) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeTecnico(),
                            textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInv", sesionController.getNombreCanal()).getMensajeUsuario());
                    if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS)) {
                        FacesContext.getCurrentInstance().addMessage(formulario + ":txtMontoMaxTOB", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                    } else {
                        FacesContext.getCurrentInstance().addMessage(formulario + ":txtMontoMax", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                    }
                    errores++;
                } else {

                    //Se consulta el limite del banco
                    IbParametrosDTO valor = parametrosFacade.consultaParametro("pnw.limite.trans.otrosbancos", sesionController.getIdCanal());
                    //Terceros Otros Bancos, Límite del Banco
                    if (new BigDecimal(valor.getIbParametro().getValor()).compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.montoMaximo))) < 0) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTOB", sesionController.getNombreCanal()).getMensajeTecnico(),
                                textosFacade.findByCodigo("pnw.global.texto.mtoLimiteInvBancoTOB", sesionController.getNombreCanal()).getMensajeUsuario());
                        if (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS)) {
                            FacesContext.getCurrentInstance().addMessage(formulario + ":txtMontoMaxTOB", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                        } else {
                            FacesContext.getCurrentInstance().addMessage(formulario + ":txtMontoMax", fMsg);//El monto ingresado supera el límite configurado - LÍMITE DE USUARIO
                        }
                        errores++;
                    }
                }
            }
        }

        return errores;
    }

    /**
     * metodo para redirigir a paso 4 de afiliar
     */
    public void afiliarPaso4() {
        RespuestaDTO respuesta = new RespuestaDTO();
        int validaciones = this.validarAfiliaciones();
        String selectTransNombre = null;

        if (validaciones == 0) {
            if (this.afiliacionesDTOAfiliar != null && (selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS) || selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_P_OTROS_BCOS))) {
                respuesta = afiliacionDAO.insertarAfiliacion(sesionController.getUsuario().getCodUsuario(), this.afiliacionesDTOAfiliar.getAlias(),
                        sesionController.getUsuario().getTipoDoc(),
                        sesionController.getUsuario().getDocumento(),
                        this.afiliacionesDTOAfiliar.getEmail(),
                        selectTransSelected,
                        this.afiliacionesDTOAfiliar.getMontoMaximo().toString(),
                        this.afiliacionesDTOAfiliar.getNombreBanco(),
                        sesionController.getUsuario().getNombre(),
                        this.afiliacionesDTOAfiliar.getNumeroCuenta(),
                        sesionController.getIdCanal(), sesionController.getNombreCanal());
            } else if (this.afiliacionesDTOAfiliar != null && (!selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS) || !selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_P_OTROS_BCOS))) {
                respuesta = afiliacionDAO.insertarAfiliacion(sesionController.getUsuario().getCodUsuario(), this.afiliacionesDTOAfiliar.getAlias(),
                        this.afiliacionesDTOAfiliar.getTipoDoc(),
                        this.afiliacionesDTOAfiliar.getDocumento(),
                        this.afiliacionesDTOAfiliar.getEmail(),
                        selectTransSelected,
                        this.afiliacionesDTOAfiliar.getMontoMaximo().toString(),
                        this.afiliacionesDTOAfiliar.getNombreBanco() == null ? textosController.getNombreTexto("pnw.global.texto.banco", sesionController.getNombreCanal()) : this.afiliacionesDTOAfiliar.getNombreBanco(),
                        this.afiliacionesDTOAfiliar.getNombreBeneficiario(),
                        this.afiliacionesDTOAfiliar.getNumeroCuenta(),
                        sesionController.getIdCanal(), sesionController.getNombreCanal());
            }

            if (respuesta != null && !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL LISTADO DE PREGUNTAS DE DESAFIO
                FacesContext.getCurrentInstance().addMessage("formAfiliar3:afiliacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                insertarBeneficiario = false;
                modificaBeneficiario = false;
                eliminaBeneficiario = false;
            } else {
                //se limpia el formulario de afiliaciones
                //this.afiliacionesDTOAfiliar = new IbAfiliacionesDTO();
                insertarBeneficiario = true;
                modificaBeneficiario = false;
                eliminaBeneficiario = false;
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_AFILIARBENEF, "", "", "Afiliación de Beneficiarios", "", "", "", "", "", "", "");

                List<SelectItem> listTrans = this.listTrans();

                for (SelectItem i : listTrans) {
                    for (SelectItem j : ((SelectItemGroup) i).getSelectItems()) {
                        if (j.getValue().equals(selectTransSelected)) {
                            selectTransNombre = capitalizarTexto(i.getLabel()) + j.getLabel();
                            break;
                        }
                    }

                }

                /////////NOTIFICACION VIA SMS////////
                String textoSMS = textosController.getNombreTexto("pnw.global.texto.smsAfiliacion", sesionController.getNombreCanal());
                String motivoSMS = textosController.getNombreTexto("pnw.menu.modulo.servicios", sesionController.getNombreCanal());
                HashMap<String, String> parametros = new HashMap();
                parametros.put("\\$PFEC", this.formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                parametros.put("\\$PTRANS", selectTransNombre);
                parametros.put("\\$PTELF", parametrosController.getNombreParametro("pnw.afiliar.sms.telf", sesionController.getIdCanal()));
                this.mantenerDatosForm = false;
//                sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);
                sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliar.url.paso4", sesionController.getIdCanal()));
            }
        }
    }

    /**
     * metodo para redirigir a paso 1 de desafiliar
     */
    public void desafiliarPaso1() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliar.url.paso1", sesionController.getIdCanal()));
    }

    /**
     * metodo para redirigir a paso 2 de desafiliar
     *
     * @return
     */
    public void desafiliarPaso2() {

        RespuestaDTO respuesta = new RespuestaDTO();

        respuesta = afiliacionDAO.deshabilitarAfiliacion(obtenerRafagaAfiliaciones(listSelected), "-", sesionController.getNombreCanal());

        if (respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {

            eliminaBeneficiario = true;
            insertarBeneficiario = false;
            modificaBeneficiario = false;
            sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_LISTBENEF, "", "", "Desafiliación de Beneficiarios", "", "", "", "", "", "", "");

            /////////NOTIFICACION VIA SMS////////
            String textoSMS = textosController.getNombreTexto("pnw.global.texto.smsDesafiliacion", sesionController.getNombreCanal());
            String motivoSMS = textosController.getNombreTexto("pnw.menu.modulo.servicios", sesionController.getNombreCanal());
            HashMap<String, String> parametros = new HashMap();
            parametros.put("\\$PFEC", this.formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
            parametros.put("\\$PTELF", parametrosController.getNombreParametro("pnw.afiliar.sms.telf", sesionController.getIdCanal()));
//            sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);

            sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliar.url.paso2", sesionController.getIdCanal()));

        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliar1:divMsjGlobal", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliar.url.paso1", sesionController.getIdCanal()));
        }

    }

    public void iniciarBeneficiario() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.mantenerDatosForm = false;
            if (afiliacionesDTOAfiliar == null) {
                afiliacionesDTOAfiliar = new IbAfiliacionesDTO();
            }
            this.limpiarBeneficiario();
            sesionController.setReiniciarForm(false);
        }
    }

    public void limpiarBeneficiario() {
        //  if (!this.mantenerDatosForm) {
        this.mantenerDatosForm = false;
        this.afiliacionesDTOAfiliar = null;
        this.montoMaximo = null;
        this.bancoSelected = new BancoDTO();
        this.afiliacionesDTO = new IbAfiliacionesDTO();
        this.listSelectTrans = null;
        this.nombreBanco = null;
        this.codigoBanco = null;

        // }
    }

    public void limpiar() {
        if (sesionController.isReiniciarForm()) {
            this.mantenerDatosForm = false;
            this.afiliacionesDTOAfiliar = null;
            this.montoMaximo = null;
            this.bancoSelected = new BancoDTO();
            this.afiliacionesDTO = new IbAfiliacionesDTO();
            this.listSelectTrans = null;
            sesionController.setReiniciarForm(false);
            this.selectTransSelected = "-1";
            this.nombreBanco = null;
            this.botonDesafiliar = false;
        }
    }

    public void borrarAfiliaciones() {
        if (!listSelected.isEmpty()) {
            sesionController.setValidadorFlujo(sesionController.getValidadorFlujo() + 1);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliar.url.paso1", sesionController.getIdCanal()));
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formBeneficiarios:divMsjGlobal", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.errores.texto.seleccioneBenef", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.submenu.servicios.listBeneficiarios", sesionController.getIdCanal()));
        }
    }

    public void obtenerBeneficiariosTransaccion() {
        if (selectTransSelected == null || selectTransSelected.isEmpty() || selectTransSelected.equalsIgnoreCase("-1")) {
            this.afiliacionesDTO = null;
            this.modificaBeneficiario = false;
            this.botonDesafiliar = false;
        } else {
            afiliacionesDTO = afiliacionDAO.obtenerListadoAfiliadosPorOperacion(String.valueOf(sesionController.getUsuario().getId()), selectTransSelected, sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (!afiliacionesDTO.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formBeneficiarios:selectTipoTransaccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                this.botonDesafiliar = false;
            } else {
                if (!afiliacionesDTO.getRespuestaDTO().getCodigoSP().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    this.botonDesafiliar = false;
                } else {
                    this.botonDesafiliar = true;
                }
            }
            modificaBeneficiario = true;
            sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_LISTBENEF, "", "", "Consulta de Beneficiarios", "", "", "", "", "", "", "");
        }

    }

    public List<BancoDTO> listBancos() {
        DelSurDTO delsurDto = new DelSurDTO();

        delsurDto = delSurDAO.listadoBancos(sesionController.getNombreCanal(), sesionController.getIdCanal());

        if (delsurDto.getRespuestaDTO() != null && !delsurDto.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //ERROR AL INTENTAR ACTUALIZAR EL LISTADO DE PREGUNTAS DE DESAFIO
            FacesContext.getCurrentInstance().addMessage("formAfiliar2:afiliacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        }

        return delsurDto.getBancos();
    }

    public String obtenerRafagaAfiliaciones(List<IbAfiliacionesDTO> ibAfiliacionesDTO) {
        StringBuilder rafaga = new StringBuilder();

        for (IbAfiliacionesDTO i : ibAfiliacionesDTO) {
            if (!rafaga.toString().equalsIgnoreCase("")) {
                rafaga.append("-");
            }
            rafaga.append(i.getId());
        }
        return rafaga.toString();
    }

    public void regresarPaso1() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.otp.url.validacionOtp", sesionController.getIdCanal()));
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoAfiliar2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AFILIARBENEF)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoAfiliar3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AFILIARBENEF)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoAfiliar4() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AFILIARBENEF)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoListPaso2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_LISTBENEF)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoDesafiliarPaso1() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_LISTBENEF)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoDesafiliarPaso2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_LISTBENEF)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoModificarPaso1() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_LISTBENEF)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoModificarPaso2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_LISTBENEF)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoModificarPaso3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 5 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_LISTBENEF)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    ////////////////////SECCION  DE REPORTES//////////////////////////
    /**
     * metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoAfiliarBeneficiariosPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 11, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //Variables para el manejo de la data
            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            String nombreBeneficiario;
            String nombreBanco;
            String emailBeneficiarioPDF;
            String numeroTDC;

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(BeneficiarioController.class.getResource("/imgPDF/logoBanner.png"));

            //Transparencia de la imagen
            image.setTransparency(new int[]{0x00, 0x10});
            image.scaleAbsolute(140f, 40f);
            PdfPCell cellImage = new PdfPCell();
            cellImage.setRowspan(2);
            cellImage.setBorder(0);
            cellImage.addElement(new Chunk(image, 2, -2));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas = {0.97f, 2.75f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas);
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setBorderColor(Color.WHITE);
            table.addCell(cellImage);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            //Alineamos fecha y usuario a la derecha
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell fecha = new PdfPCell(new Phrase(fecha2(new Date()), fontTituloBold2));
            PdfPCell usuario = new PdfPCell(new Phrase(sesionController.getUsuario().getNombre(), fontTituloBold2));

            //Establecemos espaciado a la derecha
            fecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
            usuario.setHorizontalAlignment(Element.ALIGN_RIGHT);
            fecha.setBorder(0);
            usuario.setBorder(0);
            table.addCell(fecha);
            table.addCell(usuario);

            pdf.add(new Paragraph(" "));
            pdf.add(table);

            table = new PdfPTable(2);

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas0 = {1.95f, 3.05f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas0);
            //    table.getDefaultCell().setBorder(0);

            //    cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));
            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));

            cell.setColspan(2);
            cell.setBorder(0);

            pdf.add(new Paragraph(" "));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(20f);
            table.addCell(cell);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            pdf.add(new Paragraph(" "));

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(null);
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);
            table.setWidthPercentage(70f);

            //Títulos de las Columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.fecTransaccion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(fecha2(this.getFechaEjecucion()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.aliasBenef", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(afiliacionesDTOAfiliar.getAlias(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nroIdentificacion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            if (this.selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS) || this.selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_P_OTROS_BCOS)) {
                table.addCell(new Phrase(sesionController.getUsuario().getTipoDoc() + "-" + sesionController.getUsuario().getDocumento(), font));
            } else {
                table.addCell(new Phrase(afiliacionesDTOAfiliar.getTipoDoc() + "-" + afiliacionesDTOAfiliar.getDocumento(), font));
            }
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nombreBenef", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            if (this.selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS) || this.selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_P_OTROS_BCOS)) {
                table.addCell(new Phrase(sesionController.getUsuario().getNombre(), font));
            } else {
                table.addCell(new Phrase(afiliacionesDTOAfiliar.getNombreBeneficiario(), font));
            }
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nombreBanco", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.afiliacionesDTOAfiliar.getNombreBanco() == null ? textosController.getNombreTexto("pnw.global.texto.banco", sesionController.getIdCanal()) : this.afiliacionesDTOAfiliar.getNombreBanco(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            if (this.selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_TERC_DELSUR) || this.selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS) || this.selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS)) {
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nroCuenta", sesionController.getIdCanal()), fontBold));
            } else if (this.selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_T_OTROS_BCOS) || this.selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_TERC_DELSUR) || this.selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_P_OTROS_BCOS)) {
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nroTarjeta", sesionController.getIdCanal()), fontBold));
            }

            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.formatoAsteriscosWeb(afiliacionesDTOAfiliar.getNumeroCuenta()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.emailBenef", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(afiliacionesDTOAfiliar.getEmail(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.montoMax", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase((afiliacionesDTOAfiliar.getMontoMaximo() != null ? "Bs. " + this.formatearMonto(this.afiliacionesDTOAfiliar.getMontoMaximo()) : " - "), font));

            //Agregamos la tabla al PDF
            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            //Añadimos la tabla al PDF
            // pdf.setMargins(150f, 20f, 50f, 10f);
            pdf.setMargins(50f, 3f, 22f, 18f);
            pdf.add(new Paragraph(" "));
        } catch (Exception e) {
            //manejar excepciones de reportes
        }
    }

    /**
     * metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoModificarBeneficiariosPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 11, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //Variables para el manejo de la data
            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            String nombreBeneficiario;
            String nombreBanco;
            String emailBeneficiarioPDF;
            String numeroTDC;

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(BeneficiarioController.class.getResource("/imgPDF/logoBanner.png"));

            //Transparencia de la imagen
            image.setTransparency(new int[]{0x00, 0x10});
            image.scaleAbsolute(140f, 40f);
            PdfPCell cellImage = new PdfPCell();
            cellImage.setRowspan(2);
            cellImage.setBorder(0);
            cellImage.addElement(new Chunk(image, 2, -2));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas = {0.97f, 2.75f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas);
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setBorderColor(Color.WHITE);
            table.addCell(cellImage);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            //Alineamos fecha y usuario a la derecha
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell fecha = new PdfPCell(new Phrase(fecha2(new Date()), fontTituloBold2));
            PdfPCell usuario = new PdfPCell(new Phrase(sesionController.getUsuario().getNombre(), fontTituloBold2));

            //Establecemos espaciado a la derecha
            fecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
            usuario.setHorizontalAlignment(Element.ALIGN_RIGHT);
            fecha.setBorder(0);
            usuario.setBorder(0);
            table.addCell(fecha);
            table.addCell(usuario);

            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            table = new PdfPTable(2);

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas0 = {1.95f, 3.05f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas0);
            //    table.getDefaultCell().setBorder(0);

            //    cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.histTransacciones.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));
            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.modificarBenef.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            cell.setBorder(0);

            cell.setPaddingBottom(20f);
            table.addCell(cell);
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(null);
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);
            table.setWidthPercentage(70f);

            //Títulos de las Columnas
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.fecTransaccion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(fecha2(this.getFechaEjecucion()), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.aliasBenef", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(afiliacionSelected.getAlias(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nroIdentificacion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            if (this.selectTransSelected.equalsIgnoreCase("7") || this.selectTransSelected.equalsIgnoreCase("8")) {
                table.addCell(new Phrase(sesionController.getUsuario().getTipoDoc() + "-" + sesionController.getUsuario().getDocumento(), font));
            } else {
                table.addCell(new Phrase(afiliacionSelected.getTipoDoc() + "-" + afiliacionSelected.getDocumento(), font));
            }
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nombreBenef", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            if (this.selectTransSelected.equalsIgnoreCase("7") || this.selectTransSelected.equalsIgnoreCase("8")) {
                table.addCell(new Phrase(sesionController.getUsuario().getNombre(), font));
            } else {
                table.addCell(new Phrase(afiliacionSelected.getNombreBeneficiario(), font));
            }
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nombreBanco", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.afiliacionSelected.getNombreBanco() == null ? textosController.getNombreTexto("pnw.global.texto.banco", sesionController.getIdCanal()) : afiliacionSelected.getNombreBanco(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            if (this.selectTransSelected.equalsIgnoreCase("1") || this.selectTransSelected.equalsIgnoreCase("3") || this.selectTransSelected.equalsIgnoreCase("7")) {
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nroCuenta", sesionController.getIdCanal()), fontBold));
            } else if (this.selectTransSelected.equalsIgnoreCase("4") || this.selectTransSelected.equalsIgnoreCase("5") || this.selectTransSelected.equalsIgnoreCase("8")) {
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nroTarjeta", sesionController.getIdCanal()), fontBold));
            }

            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(afiliacionSelected.getNumeroCuenta(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.emailBenef", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(afiliacionSelected.getEmail(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.montoMax", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase((afiliacionSelected.getMontoMaximo() != null ? "Bs. " + this.afiliacionSelected.getMontoMaximo() : " - "), font));

            //Agregamos la tabla al PDF
            pdf.add(new Paragraph(" "));
            pdf.add(table);
            pdf.add(new Paragraph(" "));

            //Añadimos la tabla al PDF
            // pdf.setMargins(150f, 20f, 50f, 10f);
            pdf.setMargins(50f, 3f, 22f, 18f);
            pdf.add(new Paragraph(" "));
        } catch (Exception e) {
            //manejar excepciones de reportes
        }
    }

    /**
     * metodo encargado de generar el contenido del listado de Perfil BK
     * Usuarios BK en PDF
     *
     * @param pdf hoja de trabajo de PDF
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoEliminarBeneficiariosPDF(Document pdf) throws BadElementException, DocumentException {
        try {
            //Estilos de las Fuentes
            Font fontTituloBold = FontFactory.getFont("Open Sans, sans-serif", 11, Font.NORMAL, new Color(15, 158, 197));
            Font fontTituloBold2 = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL, new Color(15, 158, 197));
            Font fontBold = FontFactory.getFont("Open Sans, sans-serif", 8, Font.BOLD);
            Font font = FontFactory.getFont("Open Sans, sans-serif", 9, Font.NORMAL);

            //Variables para el manejo de la data
            //cantidad de celdas de la tabla, tabla del encabezado
            PdfPTable table = new PdfPTable(2);

            //Alineamos la imagen a la izquierda
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            String nombreBeneficiario;
            String nombreBanco;
            String emailBeneficiarioPDF;
            String numeroTDC;

            //Armamos la ruta donde se encuentra la imagen
            Image image = Image.getInstance(BeneficiarioController.class.getResource("/imgPDF/logoBanner.png"));

            //Transparencia de la imagen
            image.setTransparency(new int[]{0x00, 0x10});
            image.scaleAbsolute(140f, 40f);
            PdfPCell cellImage = new PdfPCell();
            cellImage.setRowspan(2);
            cellImage.setBorder(0);
            cellImage.addElement(new Chunk(image, 2, -2));

            //Armo arreglo con las medidas de cada columna
            float[] medidaCeldas = {0.97f, 2.75f};

            //Asigno las medidas a la tabla (El ancho)
            table.setWidths(medidaCeldas);
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setBorderColor(Color.WHITE);
            table.addCell(cellImage);

            //Espaciado entre el contenido y la celda
            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.getDefaultCell().setPaddingRight(3);
            table.getDefaultCell().setPaddingLeft(3);

            //Alineamos fecha y usuario a la derecha
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell fecha = new PdfPCell(new Phrase(fecha2(new Date()), fontTituloBold2));
            PdfPCell usuario = new PdfPCell(new Phrase(sesionController.getUsuario().getNombre(), fontTituloBold2));

            //Establecemos espaciado a la derecha
            fecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
            usuario.setHorizontalAlignment(Element.ALIGN_RIGHT);
            fecha.setBorder(0);
            usuario.setBorder(0);
            table.addCell(fecha);
            table.addCell(usuario);

            pdf.add(table);

            //Títulos de las Columnas
            table = new PdfPTable(1);
            //Título que contiene el Nombre y el Número del Producto 
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.eliminarBenef.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            cell.setBorder(0);

            cell.setPaddingBottom(20f);
            table.addCell(cell);
            pdf.add(table);
            for (IbAfiliacionesDTO afiliacionEliminar : listSelected) {
                table = new PdfPTable(2);

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

                //Armo arreglo con las medidas de cada columna
                float[] medidaCeldas0 = {1.95f, 3.05f};

                //Asigno las medidas a la tabla (El ancho)
                table.setWidths(medidaCeldas0);
                //    table.getDefaultCell().setBorder(0);

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));

                //Espaciado entre el contenido y la celda
                table.getDefaultCell().setBackgroundColor(null);
                table.getDefaultCell().setPaddingTop(3);
                table.getDefaultCell().setPaddingBottom(3);
                table.getDefaultCell().setPaddingRight(3);
                table.getDefaultCell().setPaddingLeft(3);
                table.setWidthPercentage(70f);

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.fecTransaccion", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(fecha2(this.getFechaEjecucion()), font));

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.aliasBenef", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(afiliacionEliminar.getAlias(), font));

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nroIdentificacion", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                if (this.selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS) || this.selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_P_OTROS_BCOS)) {
                    table.addCell(new Phrase(sesionController.getUsuario().getTipoDoc() + "-" + sesionController.getUsuario().getDocumento(), font));
                } else {
                    table.addCell(new Phrase(afiliacionEliminar.getTipoDoc() + "-" + afiliacionEliminar.getDocumento(), font));
                }
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nombreBenef", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                if (this.selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS) || this.selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_P_OTROS_BCOS)) {
                    table.addCell(new Phrase(sesionController.getUsuario().getNombre(), font));
                } else {
                    table.addCell(new Phrase(afiliacionEliminar.getNombreBeneficiario(), font));
                }
                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nombreBanco", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(afiliacionEliminar.getNombreBanco() == null ? textosController.getNombreTexto("pnw.global.texto.banco", sesionController.getIdCanal()) : afiliacionEliminar.getNombreBanco(), font));

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                if (this.selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_TERC_DELSUR) || this.selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_T_OTROS_BCOS) || this.selectTransSelected.equalsIgnoreCase(ID_TRANS_CTAS_P_OTROS_BCOS)) {
                    table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nroCuenta", sesionController.getIdCanal()), fontBold));
                } else if (this.selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_T_OTROS_BCOS) || this.selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_TERC_DELSUR) || this.selectTransSelected.equalsIgnoreCase(ID_TRANS_TDC_P_OTROS_BCOS)) {
                    table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.nroTarjeta", sesionController.getIdCanal()), fontBold));
                }

                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(afiliacionEliminar.getNumeroCuenta(), font));

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.emailBenef", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase(afiliacionEliminar.getEmail(), font));

                table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
                table.addCell(new Phrase(textosController.getNombreTexto("pnw.afiliarBenef.descargaPdf.montoMax", sesionController.getIdCanal()), fontBold));
                table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
                table.addCell(new Phrase((afiliacionEliminar.getMontoMaximo() != null ? "Bs. " + afiliacionEliminar.getMontoMaximo() : " - "), font));

                pdf.add(new Paragraph(" "));
                pdf.add(table);
            }

            //Agregamos la tabla al PDF
            pdf.add(new Paragraph(" "));

            //Añadimos la tabla al PDF
            // pdf.setMargins(150f, 20f, 50f, 10f);
            pdf.setMargins(50f, 3f, 22f, 18f);
            pdf.add(new Paragraph(" "));
        } catch (Exception e) {
            //manejar excepciones de reportes
        }
    }

    /**
     * metodo encargado de armar el reportes de listado del Perfil
     * PerfilBKUsuarios en PDF
     *
     * @throws java.io.IOException
     * @throws com.lowagie.text.DocumentException
     */
    public void detalleBeneficiarioPDF() throws IOException, DocumentException {

        try {

            String nombreDocumento;

            if (insertarBeneficiario) {
                nombreDocumento = "AfiliacionBeneficiario.pdf";
            } else {
                if (modificaBeneficiario) {
                    nombreDocumento = "ModificacionBeneficiario.pdf";
                } else {
                    nombreDocumento = "EliminarBeneficiario.pdf";
                }
            }

            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public"); 
            response.setContentType("application/octet-stream");
            //response.addHeader("Content-disposition", "attachment;filename=\"AfiliacionBeneficiario.pdf\"");
            response.addHeader("Content-disposition", "attachment;filename=\"" + nombreDocumento + "\"");
            Document document = new Document(PageSize.A4.rotate());//(), 20, 20, 20, 20);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            //Instanciamos la clase utilitaria que realiza la paginación
            PaginarPDF paginar = new PaginarPDF();
            Rectangle headerBox = new Rectangle(22, 45, 1320, 700);

            //Establecemos el tamaño del documento
            writer.setBoxSize("headerBox", headerBox);

            //Pasamos por parámetros los eventos que se producen al generar el PDF
            writer.setPageEvent(paginar);

            document.open();

            //Invocamos el método que genera el PDF
            if (insertarBeneficiario) {
                this.cuerpoAfiliarBeneficiariosPDF(document);
            } else {
                if (modificaBeneficiario) {
                    this.cuerpoModificarBeneficiariosPDF(document);
                } else {
                    this.cuerpoEliminarBeneficiariosPDF(document);
                }
            }

            //ceramos el documento
            document.close();

            response.setContentLength(baos.size());
            ServletOutputStream out = response.getOutputStream();
            baos.writeTo(out);
            baos.flush();
            faces.responseComplete();

        } catch (Exception e) {
        }
    }

}
