/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.IbAfiliacionDAO;
import com.bds.wpn.dao.IbMenuDAO;
import com.bds.wpn.dao.IbUsuariosP2PDAO;
import com.bds.wpn.dto.IbAfiliacionesDTO;
import com.bds.wpn.dto.IbModulosDTO;
import com.bds.wpn.dto.IbParametrosDTO;
import com.bds.wpn.dto.IbTransaccionesDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author wilmer.rondon
 */
@Named("wpnMenuController")
@SessionScoped
public class MenuController extends BDSUtil implements Serializable {

    @EJB
    IbMenuDAO menuDAO;

    @EJB
    IbAfiliacionDAO afiliacionDAO;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbUsuariosP2PDAO ibUsuariosP2PDAO;

    @Inject
    InicioSesionController sesionController;

    @Inject
    TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    @Inject
    OTPController oTPController;

    private IbModulosDTO modulosDTO;
    private Collection<IbModulosDTO> listModulosDTO;
    private IbParametrosDTO parametrosDTO;
    private int activoTab = -1;
    private final String P2P_AFILIACION = "pnw.submenu.transfP2P.afiliacion";
    private final String P2P_DESAFILIACION = "pnw.submenu.transfP2P.desafiliacion";
    private final String P2P_EDICION = "pnw.submenu.transfP2P.editarAfi";
    private final String P2P_PAGO = "pnw.submenu.transfP2P.pago";

    private final String TRANSF_TERCEROS_DEL_SUR = "pnw.submenu.transf.tercdelsur";
    private final String TRANSF_TERCEROS_OTROS_BCOS = "pnw.submenu.transf.tercotrobanc";
    private final String PAGO_TDC_DEL_SUR_2 = "pnw.submenu.pagos.tercdelsur";
    private final String PAGO_TDC_OTROS_BCOS = "pnw.submenu.pagos.tercotrobanc";
                                                      
    private final String ACTUALIZA_DATOS_SENSIBLES = "pnw.submenu.seguridad.actualizacionDatos"; 
    
    private final String OPCION_MENU = "Ingreso a la opción de menú: ";

    /**
     * Obtener los parametros
     *
     * @return IbParametrosDTO
     */
    public IbParametrosDTO getParametrosDTO() {
        return parametrosDTO;
    }

    /**
     * Asignar los parametros
     *
     * @param parametrosDTO
     */
    public void setParametrosDTO(IbParametrosDTO parametrosDTO) {
        this.parametrosDTO = parametrosDTO;
    }

    /**
     * Obtener el listado de modulos
     *
     * @return Collection
     */
    public Collection<IbModulosDTO> getListModulosDTO() {
        if (this.listModulosDTO == null) {
            this.cargarMenu();
        }
        return listModulosDTO;
    }

    /**
     * Asignar el listado de modulos
     *
     * @param listModulosDTO
     */
    public void setListModulosDTO(Collection<IbModulosDTO> listModulosDTO) {
        this.listModulosDTO = listModulosDTO;
    }

    /**
     * Obntener el modulo
     *
     * @return IbModulosDTO
     */
    public IbModulosDTO getModulosDTO() {
        return modulosDTO;
    }

    /**
     * Asignar el modulo
     *
     * @param modulosDTO IbModulosDTO
     */
    public void setModulosDTO(IbModulosDTO modulosDTO) {
        this.modulosDTO = modulosDTO;
    }

    /**
     * Metodo que redirecciona a la pagina configurada por defecto
     *
     * @param actionEvent
     * @throws java.io.IOException
     */
    public void actionURLInicial(ActionEvent actionEvent) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro("pnw.global.url.inicial", sesionController.getIdCanal()));
    }

    /**
     * Metodo para cargar el menu y submenu de manera dinamica
     *
     * @return Collection<IbModulosDTO>
     */
    public Collection<IbModulosDTO> cargarMenu() {
        // sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), ID_TRANS_MENU, "", "", "Carga de Menú", "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
        listModulosDTO = menuDAO.consultarModulosPorUsuario(sesionController.getUsuario().getId().toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        return listModulosDTO;
    }

    /**
     * Obtener el tab activo
     *
     * @return Integer
     */
    public int getActivoTab() {
        return activoTab;
    }

    /**
     * Asignar el tab activo
     *
     * @param activoTab
     */
    public void setActivoTab(int activoTab) {
        this.activoTab = activoTab;
    }

    /**
     * Redirecciona a la pagina asociada al tab, cuando se hace clic sobre el
     * elemento
     */
    public void enlanceTab() {
        try {
            if (listModulosDTO != null) {
                if (listModulosDTO.size() > 0) {
                    IbModulosDTO modulo = (IbModulosDTO) listModulosDTO.toArray()[activoTab];
                    if (modulo.getIbTransaccionesDTOCollection().size() == 1) {
                        FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + parametrosController.getNombreParametro(
                                ((IbTransaccionesDTO) modulo.getIbTransaccionesDTOCollection().toArray()[0]).getNombre(), sesionController.getIdCanal()));
                    }

                }
            }

        } catch (Exception e) {

        }
    }

    public String style(boolean valor) {
        if (valor) {
            return "imgIconoSignoMas";
        } else {
            return "";
        }
    }

    /**
     * metodo para redireccionar a Otp o al modulo correspondiente
     *
     * @param modulo modulo seleccionado
     * @param transaccion transaccion seleccionada
     */
    public void redirectModulo(IbModulosDTO modulo, IbTransaccionesDTO transaccion) {
        //SE AGREGAN LAS LINEAS PARA REGISTRAR EN BITACORA Y ACTUALIZAR LA FECHA DE LA ULTIMA INTERACCION EN BD
        //DE MANERA DE EVITAR EL VENCIMIENTO DE LA SESSION ANTES DE TIEMPO
        String nombreTransaccionMenu = OPCION_MENU + textosController.getNombreTexto(transaccion.getNombre(), sesionController.getIdCanal());
        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), transaccion.getId().toString(), "", "", nombreTransaccionMenu, "", "", "", "", "", "", CODIGO_RESPUESTA_EXITOSO);
        if (transaccion.getNombre().equals(P2P_AFILIACION)
                || transaccion.getNombre().equals(P2P_DESAFILIACION)
                || transaccion.getNombre().equals(P2P_EDICION)
                || transaccion.getNombre().equals(P2P_PAGO)) {
            sesionController.setValidarUsuarioPiloto(true);
            oTPController.setUsuarioHabilitadoP2P(false);
        } else {
            sesionController.setValidarUsuarioPiloto(false);
        }

        if (transaccion.getNombre().equals(P2P_AFILIACION)) {
            sesionController.setModuloAfiliacionP2P(true);
        } else {
            sesionController.setModuloAfiliacionP2P(false);
        }

        IbAfiliacionesDTO afiliacionCliente = null;
        sesionController.setReiniciarForm(true);
        sesionController.setValidadorFlujo(1);
        sesionController.setIdModuloAdm(transaccion.getId().toString());
        sesionController.setNombreModulo(textosController.getNombreTexto(modulo.getNombre(), sesionController.getNombreCanal()));
        sesionController.setNombreTransaccion(textosController.getNombreTexto(transaccion.getNombre(), sesionController.getNombreCanal()));
        if (transaccion.getIdCore().toString().equalsIgnoreCase("0")) {
            sesionController.setIdTransaccion(transaccion.getId().toString());
        } else {//si la transaccion tiene id de core como pagos TDC o Transf se manejan esos IDs
            sesionController.setIdTransaccion(transaccion.getIdCore().toString());
        }
        this.limpiarMenu();
        if ((transaccion.getPoseeBeneficiario() != null && transaccion.getPoseeBeneficiario().toString().equalsIgnoreCase("1"))
                && (!transaccion.getIdCore().toString().equalsIgnoreCase(ID_TRANS_CTAS_PROPIAS_DELSUR) && !transaccion.getIdCore().toString().equalsIgnoreCase(ID_TRANS_TDC_PROPIAS_DELSUR))) {

            //IB_AFILIACIONES
            afiliacionCliente = afiliacionDAO.obtenerListadoAfiliadosPorOperacion(sesionController.getUsuario().getId().toString(),
                    transaccion.getIdCore().toString(),
                    sesionController.getIdCanal(),
                    sesionController.getNombreCanal());
            if (afiliacionCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                //se consultan las tdc propias para dicha transaccion
                if (afiliacionCliente.getAfiliaciones().isEmpty()) {
                    sesionController.setValidadorFlujo(2);
                    this.redirectFacesContext(parametrosController.getNombreParametro(transaccion.getNombre(), sesionController.getIdCanal()));
                    return;
                }
            } else {
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.global.url.error", sesionController.getIdCanal()));
                return;
            }

        }
        sesionController.setCodigoTransaccion(transaccion.getNombre());
        if (transaccion.getNombre().equals(TRANSF_TERCEROS_DEL_SUR)
                || transaccion.getNombre().equals(TRANSF_TERCEROS_OTROS_BCOS)
                || transaccion.getNombre().equals(PAGO_TDC_DEL_SUR_2)
                || transaccion.getNombre().equals(PAGO_TDC_OTROS_BCOS)
                || transaccion.getNombre().equals(ACTUALIZA_DATOS_SENSIBLES)) {
            this.redirectFacesContext("/sec/seguridad/validacion_clave_especial.xhtml?faces-redirect=true");
        } else {
            if (transaccion.getPoseeOtp().toString().equalsIgnoreCase("1") && sesionController.getValidacionOTP() == 0) {
                sesionController.setValidadorFlujo(1);
                sesionController.setUrlRedireccionOtp(parametrosController.getNombreParametro(transaccion.getNombre(), sesionController.getIdCanal()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.otp.url.validacionOtp", sesionController.getIdCanal()));
                return;
            } else {
                if (transaccion.getPoseeOtp().toString().equalsIgnoreCase("1") && sesionController.getValidacionOTP() != 0) {
                    sesionController.setValidadorFlujo(2);
                    this.redirectFacesContext(parametrosController.getNombreParametro(transaccion.getNombre(), sesionController.getIdCanal()));
                    return;
                }
            }
        }
        if (!transaccion.getNombre().equals(TRANSF_TERCEROS_DEL_SUR)) {
            this.redirectFacesContext(parametrosController.getNombreParametro(transaccion.getNombre(), sesionController.getIdCanal()));
        } else if (!transaccion.getNombre().equals(TRANSF_TERCEROS_OTROS_BCOS)) {
            this.redirectFacesContext(parametrosController.getNombreParametro(transaccion.getNombre(), sesionController.getIdCanal()));
        } else if (!transaccion.getNombre().equals(PAGO_TDC_DEL_SUR_2)) {
            this.redirectFacesContext(parametrosController.getNombreParametro(transaccion.getNombre(), sesionController.getIdCanal()));
        } else if (!transaccion.getNombre().equals(PAGO_TDC_OTROS_BCOS)) {
            this.redirectFacesContext(parametrosController.getNombreParametro(transaccion.getNombre(), sesionController.getIdCanal()));
        } else {
            this.redirectFacesContext(parametrosController.getNombreParametro(transaccion.getNombre(), sesionController.getIdCanal()));
        }
        return;
    }

    /**
     * metodo que se encarga de asignar el menu padre activo
     *
     * @param tabActivo
     */
    public void moduloActivo(int tabActivo) {
        this.activoTab = tabActivo;
        sesionController.setReiniciarForm(true);
        sesionController.setValidacionOTP(2);
    }

    /**
     * metodo que se encarga de anular el menu para recargar la consulta en el
     * prerender de la vista
     */
    public void limpiarMenu() {
        this.listModulosDTO = null;
    }

    /**
     * reinicia el Menu
     *
     */
    public void reiniciarMenu() {
        this.activoTab = 0;
        sesionController.setIdModuloAdm("-1");
    }

    public String verificarTransaccionNueva(String key) {
        if (key.equals("pnw.submenu.seguridad.crearClaveOpeEsp") || key.equals("pnw.submenu.seguridad.modificarClaveOpeEsp")) {
            return "NUEVO";
        } else {
            return "";
        }
    }

}
