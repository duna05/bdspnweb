/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.EstadoDAO;
import com.bds.wpn.dao.ClasificacionDAO;
import com.bds.wpn.dao.FichaClienteComplementarioDAO;
import com.bds.wpn.dao.FichaClienteDAO;
import com.bds.wpn.dao.FichaClienteDireccionDAO;
import com.bds.wpn.dao.FichaClienteDireccionPrincipalDAO;
import com.bds.wpn.dao.FichaClienteEmpleosDAO;
import com.bds.wpn.dao.FichaClienteRefBancariasDAO;
import com.bds.wpn.dao.FichaClienteRefComercialDAO;
import com.bds.wpn.dao.IbCanalesDAO;
import com.bds.wpn.dao.IbClaveDAO;
import com.bds.wpn.dao.IbUsuarioDAO;
import com.bds.wpn.dao.IbPaisDAO;
import com.bds.wpn.dao.IbActividadEconomicaDAO;
import com.bds.wpn.dao.MunicipioDAO;
import com.bds.wpn.dao.SectorDAO;
import com.bds.wpn.dao.SubClasificacionDAO;
import com.bds.wpn.dao.SubSubClasificacionDao;
import com.bds.wpn.dao.TddDAO;
import com.bds.wpn.dto.FichaClienteComplementarioDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import com.bds.wpn.dto.FichaClienteDTO;
import com.bds.wpn.dto.FichaClienteDireccionPrincipalDTO;
import java.util.List;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import com.bds.wpn.dao.SubtipoClienteDAO;
import com.bds.wpn.dto.FichaClienteRefBancariasDTO;
import com.bds.wpn.dto.FichaClienteRefTarjetaDTO;
import com.bds.wpn.dao.FichaClienteRefPersonalDAO;
import com.bds.wpn.dao.FichaClienteRefTarjetaDAO;
import com.bds.wpn.dao.FichaClienteUpdateDAO;
import com.bds.wpn.dto.MotivoDTO;
import com.bds.wpn.dao.MotivoDAO;

import com.bds.wpn.dao.FichaClienteDireccionUpdateDAO;
/////////////AUDRA/////////////////////////////////////
import com.bds.wpn.dto.FichaClienteEmpleosUpdateDTO;
import com.bds.wpn.dto.FichaClienteProductosUpdateDTO;
import com.bds.wpn.dto.FichaClienteRefBancariasUpdateDTO;
import com.bds.wpn.dto.FichaClienteRefTarjetaUpdateDTO;
import com.bds.wpn.dto.FichaClienteRefPersonalesUpdateDTO;
import com.bds.wpn.dto.FichaClienteRefComercialesUpdateDTO;


import com.bds.wpn.dao.FichaClienteEmpleosUpdateDAO;
import com.bds.wpn.dao.FichaClienteProductosUpdateDAO;
import com.bds.wpn.dao.FichaClienteRefBancariasUpdateDAO;
import com.bds.wpn.dao.FichaClienteRefTarjetaUpdateDAO;
import com.bds.wpn.dao.FichaClienteRefPersonalesUpdateDAO;
import com.bds.wpn.dao.FichaClienteRefComercialesUpdateDAO;
///////////////////AUDRA//////////////////////////////////////
import com.bds.wpn.dao.MotivoDAO;
import com.bds.wpn.dto.ActividadEconomicaDTO;
import com.bds.wpn.dto.FichaClienteRefPersonalDTO;
import com.bds.wpn.dto.FichaClienteRefComercialesDTO;
import com.bds.wpn.dto.FichaClienteEmpleosDTO;
import com.bds.wpn.dto.FichaClienteUpdateDTO;
import com.bds.wpn.dto.MotivoDTO;
import com.bds.wpn.dto.PaisDTO;
import com.bds.wpn.model.IbFechaBD;
import com.bds.wpn.util.SessionAttributesNames;
//import com.bds.wpn.ws.services.ClasificacionDTO;
//import com.bds.wpn.ws.services.RespuestaDTO;
import static com.sun.faces.facelets.util.Path.context;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author humberto.rojas
 */
@Named("wpnFichaController")
@SessionScoped
public class FichaController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbActividadEconomicaDAO ibActividadEconomica;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbPaisDAO paisDAO;

    @EJB
    SectorDAO sectorDAO;

    @EJB
    ClasificacionDAO clasificacionDAO;

    @EJB
    SubSubClasificacionDao subSubClasificacionDAO;

    @EJB
    MunicipioDAO municipioDAO;

    @EJB
    EstadoDAO estadoDAO;

    @EJB
    SubClasificacionDAO subClasificacionDAO;

    @EJB
    FichaClienteDAO FichaclienteDAO;

    @EJB
    MotivoDAO motivoDAO;

    @EJB
    FichaClienteDireccionPrincipalDAO fichaClienteDireccionPrincipalDAO;

    @EJB
    FichaClienteRefBancariasDAO fichaClienteRefBancariasDAO;

    @EJB
    FichaClienteRefTarjetaDAO fichaClienteRefTarjetaDAO;

    @EJB
    SubtipoClienteDAO subTipoClienteJuridicoDAO;

    @EJB
    FichaClienteDireccionDAO fichaClienteDireccionDAO;

    @EJB
    FichaClienteComplementarioDAO fichaClienteComplementarioDAO;

    @EJB
    FichaClienteRefPersonalDAO fichaClienteRefPersonalDAO;

    @EJB
    FichaClienteRefComercialDAO fichaClienteRefComercialDAO;

    @EJB
    FichaClienteEmpleosDAO fichaClienteEmpleosDAO;
    
    @EJB
    FichaClienteUpdateDAO fichaClienteUpdateDAO;
    
    @EJB
    FichaClienteDireccionUpdateDAO fichaClienteDireccionUpdateDAO;
    
    
    //////////////////////AUDRA////////////////////////////////////
    @EJB
    FichaClienteEmpleosUpdateDAO fichaClienteEmpleosUpdateDAO;
    
    @EJB
    FichaClienteProductosUpdateDAO fichaClienteProductosUpdateDAO;
    
    @EJB
    FichaClienteRefBancariasUpdateDAO fichaClienteRefBancariasUpdateDAO;
    
    @EJB
    FichaClienteRefTarjetaUpdateDAO fichaClienteRefTarjetaUpdateDAO;
    
    @EJB
    FichaClienteRefPersonalesUpdateDAO fichaClienteRefPersonalesUpdateDAO;
    
    @EJB
    FichaClienteRefComercialesUpdateDAO fichaClienteRefComercialesUpdateDAO;
    ////////////////////AUDRA//////////////////////////////////////
    @EJB
    IbUsuarioDAO usuarioDAO;

    @EJB
    TddDAO tddDAO;

    @EJB
    IbCanalesDAO canalesDAO;

    @EJB
    IbClaveDAO claveDAO;

    @EJB
    IbPaisDAO ibPaisDAO;

    @Inject
    TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    @Inject
    CanalController canalController;

    private FichaClienteDTO fichaclienteDTO;

    private PaisDTO paisDTO;

    private FichaClienteRefComercialesDTO fichaClienteRefComercialDTO;

    private FichaClienteComplementarioDTO fichaClienteComplementarioDTO;
    
    private FichaClienteUpdateDTO fichaClienteUpdateDTO;
    
    ////////////////AUDRA//////////////////////////////////////////
    private FichaClienteEmpleosUpdateDTO fichaClienteEmpleosUpdateDTO;
    
    private FichaClienteProductosUpdateDTO fichaClienteProductosUpdateDTO;
    
    private FichaClienteRefBancariasUpdateDTO fichaClienteRefBancariasUpdateDTO;
    
    private FichaClienteRefTarjetaUpdateDTO fichaClienteRefTarjetaUpdateDTO;
    
    private FichaClienteRefPersonalesUpdateDTO fichaClienteRefPersonalesUpdateDTO;
    
    private FichaClienteRefComercialesUpdateDTO fichaClienteRefComercialesUpdateDTO;
    /////////////////AUDRA////////////////////////////////////////
    
    //private List<ClasificacionDTO> clasificacionDTO;
    
    private MotivoDTO motivoDTO;
    
    private List<MotivoDTO> ibMotivoList;    

    private FichaClienteRefBancariasDTO fichaClienteRefBancariasDTO;

    private List<FichaClienteRefBancariasDTO> ibFichaClienteRefBancariaList;

    private List<FichaClienteRefTarjetaDTO> ibFichaClienteRefTarjetasList;

    private List<FichaClienteEmpleosDTO> IbFichaClienteEmpleosList;

    private List<PaisDTO> paisList;

    private List<String> descripTipoDir;

    private LazyDataModel<String> listDirecciones;

    private FichaClienteRefPersonalDTO fichaClienteRefPersonalDTO;

    private List<FichaClienteRefPersonalDTO> listFichaClienteRefPersonalDTO;

    private List<FichaClienteRefComercialesDTO> ibFichaClienteRefComercialesList;

    private String pais;

    private List<SelectItem> paises;

    private List<SelectItem> motivoList;
    
    ////////////HUMBERTO////////////////
    private List<SelectItem> motivoListSI;
    ///////////HUMBERTO////////////////

    private ActividadEconomicaDTO actividadEconomicaDto;

    private MotivoDTO MotivoDTO;

    private String sector;

    private List<SelectItem> sectores;

    private String clasificacion;

    private List<SelectItem> clasificaciones;

    private List<SelectItem> actividadEconomicaList;

    private List<MotivoDTO> motivoListString;

    private List<String> motiListString;
    
    private String salida;

    public String getSalida() {
        return salida;
    }

    //////////////HUMBERTO/////////////////////////////////////
    public List<SelectItem> getMotivoListSI() {
        
        this.motivoListSI = new ArrayList<SelectItem>();
        MotivoDTO motivo = new MotivoDTO();
        for (MotivoDTO pa : motivoDAO.consultarMotivo().getIbMotivoListSI()) {
            String nombre = pa.getNombre();
            SelectItem motivoItem = new SelectItem(nombre, nombre);
            this.motivoListSI.add(motivoItem);
        }        
        return motivoListSI;
    }

    public void setMotivoListSI(List<SelectItem> motivoListSI) {
        this.motivoListSI = motivoListSI;
    }
    ////////////HUMBERTO///////////////////////////////////////  
    
    

    public List<MotivoDTO> getIbMotivoList() {
        return ibMotivoList;
    }

    public void setIbMotivoList(List<MotivoDTO> ibMotivoList) {
        this.ibMotivoList = ibMotivoList;
    }  
    

    public void setSalida(String salida) {
        this.salida = salida;
    }

    public FichaClienteUpdateDTO getFichaClienteUpdateDTO() {
        return fichaClienteUpdateDTO;
    }

    public void setFichaClienteUpdateDTO(FichaClienteUpdateDTO fichaClienteUpdateDTO) {
        this.fichaClienteUpdateDTO = fichaClienteUpdateDTO;
    }
    
    public FichaClienteEmpleosUpdateDTO getFichaClienteEmpleosUpdateDTO() {
        return fichaClienteEmpleosUpdateDTO;
    }

    public void setFichaClienteEmpleosUpdateDTO(FichaClienteEmpleosUpdateDTO fichaClienteEmpleosUpdateDTO) {
        this.fichaClienteEmpleosUpdateDTO = fichaClienteEmpleosUpdateDTO;
    }

    public FichaClienteProductosUpdateDTO getFichaClienteProductosUpdateDTO() {
        return fichaClienteProductosUpdateDTO;
    }

    public void setFichaClienteProductosUpdateDTO(FichaClienteProductosUpdateDTO fichaClienteProductosUpdateDTO) {
        this.fichaClienteProductosUpdateDTO = fichaClienteProductosUpdateDTO;
    }

    public FichaClienteRefBancariasUpdateDTO getFichaClienteRefBancariasUpdateDTO() {
        return fichaClienteRefBancariasUpdateDTO;
    }

    public void setFichaClienteRefBancariasUpdateDTO(FichaClienteRefBancariasUpdateDTO fichaClienteRefBancariasUpdateDTO) {
        this.fichaClienteRefBancariasUpdateDTO = fichaClienteRefBancariasUpdateDTO;
    }

    public FichaClienteRefTarjetaUpdateDTO getFichaClienteRefTarjetaUpdateDTO() {
        return fichaClienteRefTarjetaUpdateDTO;
    }

    public void setFichaClienteRefTarjetaUpdateDTO(FichaClienteRefTarjetaUpdateDTO fichaClienteRefTarjetaUpdateDTO) {
        this.fichaClienteRefTarjetaUpdateDTO = fichaClienteRefTarjetaUpdateDTO;
    }

    public FichaClienteRefPersonalesUpdateDTO getFichaClienteRefPersonalesUpdateDTO() {
        return fichaClienteRefPersonalesUpdateDTO;
    }

    public void setFichaClienteRefPersonalesUpdateDTO(FichaClienteRefPersonalesUpdateDTO fichaClienteRefPersonalesUpdateDTO) {
        this.fichaClienteRefPersonalesUpdateDTO = fichaClienteRefPersonalesUpdateDTO;
    }

    public FichaClienteRefComercialesUpdateDTO getFichaClienteRefComercialesUpdateDTO() {
        return fichaClienteRefComercialesUpdateDTO;
    }

   
    public void setFichaClienteRefComercialesUpdateDTO(FichaClienteRefComercialesUpdateDTO fichaClienteRefComercialesUpdateDTO) {
        this.fichaClienteRefComercialesUpdateDTO = fichaClienteRefComercialesUpdateDTO;
    }

    //////////////////AUDRA/////////////////////////////////////////////////////////
    public List<String> getMotiListString() {
        return motiListString;
    }

    public void setMotiListString(List<String> motiListString) {
        this.motiListString = motiListString;
    }

    public List<MotivoDTO> getMotivoListString() {      
        
        
        return motivoListString;
    }

    public void setMotivoListString(List<MotivoDTO> motivoListString) {
        this.motivoListString = motivoListString;
    }

    public void setMotivoList(List<SelectItem> motivoList) {
        this.motivoList = motivoList;
    }

    public MotivoDTO getMotivoDTO() {

        return MotivoDTO;
    }

    public void setMotivoDTO(MotivoDTO MotivoDTO) {
        this.MotivoDTO = MotivoDTO;
    }

    public ActividadEconomicaDTO getActividadEconomicaDto() {
        return actividadEconomicaDto;
    }

    public void setActividadEconomicaDto(ActividadEconomicaDTO actividadEconomicaDto) {
        this.actividadEconomicaDto = actividadEconomicaDto;
    }

    public List<SelectItem> getActividadEconomicaList() {

        this.actividadEconomicaList = new ArrayList<SelectItem>();
        ActividadEconomicaDTO act = new ActividadEconomicaDTO();
        for (ActividadEconomicaDTO actv : ibActividadEconomica.consultarActividadEconomica().getActividadEconomicaList()) {
            String nombre = actv.getDescripcion();
            BigDecimal value = actv.getCodigoActividadEconomica();

            SelectItem actividadEconomicaItem = new SelectItem(value, nombre);

            this.actividadEconomicaList.add(actividadEconomicaItem);
        }

        return actividadEconomicaList;
    }

    public void setActividadEconomicaList(List<SelectItem> actividadEconomicaList) {
        this.actividadEconomicaList = actividadEconomicaList;
    }

    public List<PaisDTO> getPaisList() {
        return paisList;
    }

    public void setPaisList(List<PaisDTO> paisList) {
        this.paisList = paisList;
    }

   /* public List<ClasificacionDTO> getClasificacionDTO() {
        return clasificacionDTO;
    }

    public void setClasificacionDTO(List<ClasificacionDTO> clasificacionDTO) {
        this.clasificacionDTO = clasificacionDTO;
    }*/

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public List<SelectItem> getClasificaciones() {
        return clasificaciones;
    }

    public void setClasificaciones(List<SelectItem> clasificaciones) {
        this.clasificaciones = clasificaciones;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public List<SelectItem> getSectores() {
        return sectores;
    }

    public void setSectores(List<SelectItem> sectores) {
        this.sectores = sectores;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public List<SelectItem> getPaises() {
        this.paises = new ArrayList<SelectItem>();
        PaisDTO pais = new PaisDTO();
        for (PaisDTO pa : paisDAO.consultarPais().getPaisList()) {
            String nombre = pa.getNombre();
            BigDecimal value = pa.getCodigoPais();

            SelectItem paisItem = new SelectItem(value, nombre);

            this.paises.add(paisItem);
        }
        return paises;
    }

    public void setPaises(List<SelectItem> paises) {
        this.paises = paises;
    }

    public PaisDTO getPaisDTO() {
        return paisDTO;
    }

    public void setPaisDTO(PaisDTO paisDTO) {
        this.paisDTO = paisDTO;
    }

    public List<FichaClienteEmpleosDTO> getIbFichaClienteEmpleosList() {
        return IbFichaClienteEmpleosList;
    }

    public void setIbFichaClienteEmpleosList(List<FichaClienteEmpleosDTO> IbFichaClienteEmpleosList) {
        this.IbFichaClienteEmpleosList = IbFichaClienteEmpleosList;
    }

    public List<FichaClienteRefComercialesDTO> getIbFichaClienteRefComercialesList() {
        return ibFichaClienteRefComercialesList;
    }

    public void setIbFichaClienteRefComercialesList(List<FichaClienteRefComercialesDTO> ibFichaClienteRefComercialesList) {
        this.ibFichaClienteRefComercialesList = ibFichaClienteRefComercialesList;
    }

    public FichaClienteRefComercialesDTO getFichaClienteRefComercialDTO() {
        return fichaClienteRefComercialDTO;
    }

    public void setFichaClienteRefComercialDTO(FichaClienteRefComercialesDTO fichaClienteRefComercialDTO) {
        this.fichaClienteRefComercialDTO = fichaClienteRefComercialDTO;
    }

    public List<FichaClienteRefTarjetaDTO> getIbFichaClienteRefTarjetasList() {
        return ibFichaClienteRefTarjetasList;
    }

    public void setIbFichaClienteRefTarjetasList(List<FichaClienteRefTarjetaDTO> ibFichaClienteRefTarjetasList) {
        this.ibFichaClienteRefTarjetasList = ibFichaClienteRefTarjetasList;
    }

    public FichaClienteRefPersonalDTO getFichaClienteRefPersonalDTO() {
        return fichaClienteRefPersonalDTO;
    }

    public void setFichaClienteRefPersonalDTO(FichaClienteRefPersonalDTO fichaClienteRefPersonalDTO) {
        this.fichaClienteRefPersonalDTO = fichaClienteRefPersonalDTO;
    }

    public List<FichaClienteRefPersonalDTO> getListFichaClienteRefPersonalDTO() {
        return listFichaClienteRefPersonalDTO;
    }

    public void setListFichaClienteRefPersonalDTO(List<FichaClienteRefPersonalDTO> listFichaClienteRefPersonalDTO) {
        this.listFichaClienteRefPersonalDTO = listFichaClienteRefPersonalDTO;
    }

    public List<FichaClienteRefBancariasDTO> getIbFichaClienteRefBancariaList() {
        return ibFichaClienteRefBancariaList;
    }

    public void setIbFichaClienteRefBancariaList(List<FichaClienteRefBancariasDTO> ibFichaClienteRefBancariaList) {
        this.ibFichaClienteRefBancariaList = ibFichaClienteRefBancariaList;
    }

    public FichaClienteRefBancariasDTO getFichaClienteRefBancariasDTO() {
        return fichaClienteRefBancariasDTO;
    }

    public void setFichaClienteRefBancariasDTO(FichaClienteRefBancariasDTO fichaClienteRefBancariasDTO) {
        this.fichaClienteRefBancariasDTO = fichaClienteRefBancariasDTO;
    }
    private FichaClienteDireccionPrincipalDTO fichaClienteDirPpalDTO;

    public FichaClienteDireccionPrincipalDTO getFichaClienteDirPpalDTO() {
        return fichaClienteDirPpalDTO;
    }

    public void setFichaClienteDirPpalDTO(FichaClienteDireccionPrincipalDTO fichaClienteDirPpalDTO) {
        this.fichaClienteDirPpalDTO = fichaClienteDirPpalDTO;
    }

    public LazyDataModel<String> getListDirecciones() {
        return listDirecciones;
    }

    public void setListDirecciones(LazyDataModel<String> listDirecciones) {
        this.listDirecciones = listDirecciones;
    }

    public FichaClienteComplementarioDTO getFichaClienteComplementarioDTO() {
        return fichaClienteComplementarioDTO;
    }

    public void setFichaClienteComplementarioDTO(FichaClienteComplementarioDTO fichaClienteComplementarioDTO) {
        this.fichaClienteComplementarioDTO = fichaClienteComplementarioDTO;
    }

    public List<String> getDescripTipoDir() {
        return descripTipoDir;
    }

    public void setDescripTipoDir(List<String> descripTipoDir) {
        this.descripTipoDir = descripTipoDir;
    }

    public FichaClienteDTO getFichaclienteDTO() {
        return fichaclienteDTO;
    }

    public void setFichaclienteDTO(FichaClienteDTO FichaclienteDTO) {
        this.fichaclienteDTO = FichaclienteDTO;
    }

    public void datosBasicos() {
        setFichaclienteDTO(FichaclienteDAO.consultarDatosCliente(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString()));

        setFichaClienteDirPpalDTO(fichaClienteDireccionPrincipalDAO.consultarDatosDirPpalCliente(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString()));
        RequestContext.getCurrentInstance().update("formFichaCliente");

    }

    public void datosComplementariosJuridico() {
        setFichaClienteComplementarioDTO(fichaClienteComplementarioDAO.consultarDatosComplementariosCliente(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString()));

    }

    public void referenciasJuridico() {
        setIbFichaClienteRefBancariaList(fichaClienteRefBancariasDAO.consultarRefBancarias(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString()).getIbReferenciaList());
        /*SE COMENTO PORQUE ESTA DANDO ERROR CUANDO VIEBE VACIA. VALIDAR EL NULLPOINTEREXCEPTION PARA TODAS LAS LISTAS DE REFERENCIAS*/
        //setIbFichaClienteRefComercialesList(fichaClienteRefComercialDAO.consultarRefComerciales(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString()).getIbReferenciaComercialList());
    }
    
    
    
    
    /*------------------------------------------------------------------------------*/
    
    /*public void guardarEmpleos(CellEditEvent event) throws Exception {        
        FacesMessage msg = new FacesMessage("Rif Editado", ((FichaClienteEmpleosDTO) event.getObject()).getRif());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        System.out.println("msg------------------------->" + msg);
        
    }*/
    
    
    
    
    public void onRowEdit(RowEditEvent event) throws Exception { 
        
        fichaClienteEmpleosUpdateDAO.actualizarDatosEmpleos(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString(),((FichaClienteEmpleosDTO) event.getObject()).getRif(), ((FichaClienteEmpleosDTO) event.getObject()).getRamo(), ((FichaClienteEmpleosDTO) event.getObject()).getDireccion(), ((FichaClienteEmpleosDTO) event.getObject()).getTelefono(), "1");

        
    }
    
    public void onRowEditRefPersonales(RowEditEvent event) throws Exception { 
        
        fichaClienteRefPersonalesUpdateDAO.actualizarRefPersonales(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString(), 
                                           ((FichaClienteRefPersonalesUpdateDTO) event.getObject()).getCodigoTipoIdentificacion(),((FichaClienteRefPersonalesUpdateDTO) event.getObject()).getNumeroIdentificacion(),
                                           ((FichaClienteRefPersonalesUpdateDTO) event.getObject()).getNombre(),((FichaClienteRefPersonalesUpdateDTO) event.getObject()).getPrimerApellido(),
                                           ((FichaClienteRefPersonalesUpdateDTO) event.getObject()).getSegundoApellido(),((FichaClienteRefPersonalesUpdateDTO) event.getObject()).getCodigoReferencia(),
                                           ((FichaClienteRefPersonalesUpdateDTO) event.getObject()).getTelefono(),((FichaClienteRefPersonalesUpdateDTO) event.getObject()).getTelefono2());
        
    }
     
    public void onRowCancel(RowEditEvent event) {
        
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((FichaClienteEmpleosDTO) event.getObject()).getRif());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    
    
    
    
    
    
    
   /*-----------------------------------------------------------------------------*/ 
    
    
    
    
    
    
    public void guardarBasicos(String valor) throws Exception {
         fichaClienteUpdateDAO.insertarDatosCliente(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString());
         fichaClienteDireccionUpdateDAO.insertarDatosDireccion(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString(), valor);
         ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
         context.redirect(context.getRequestContextPath()+"/ext/ficha/paso2_p.xhtml"); /*CREAR URL EN TABLA PARAMETROS*/
         
     }   

    public void guardarReferencias(String motivo) throws Exception {
       
        fichaClienteProductosUpdateDAO.actualizarClienteProductos(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString(), motivo);
        //fichaClienteRefBancariasUpdateDAO.actualizarDatosRefBancarias(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString());
        //fichaClienteRefTarjetaUpdateDAO.actualizarRefTarjeta(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString());
        //fichaClienteRefPersonalesUpdateDAO.actualizarRefPersonales(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString(), codigoTipoIdentificacion, numeroIdentificacion, nombre, primerApellido, segundoApellido, codReferencia, telefono, telefono2);
        //fichaClienteRefComercialesUpdateDAO.actualizarRefComerciales(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString());
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(context.getRequestContextPath()+"/ext/ficha/paso3_p.xhtml");
    }

    public void referenciasNatural() throws Exception {       
        setFichaClienteComplementarioDTO(fichaClienteComplementarioDAO.consultarDatosComplementariosCliente(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString()));
        setListFichaClienteRefPersonalDTO(fichaClienteRefPersonalDAO.consultarRefPersonal(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString()).getIbFichaClienteRefPersonalList());
        setIbFichaClienteRefBancariaList(fichaClienteRefBancariasDAO.consultarRefBancarias(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString()).getIbReferenciaList());
        setIbFichaClienteRefTarjetasList(fichaClienteRefTarjetaDAO.consultarRefTarjeta(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString()).getIbFichaClienteRefTarjetaList());
        setIbFichaClienteRefComercialesList(fichaClienteRefComercialDAO.consultarRefComerciales(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString()).getIbReferenciaComercialList());
        setIbFichaClienteEmpleosList(fichaClienteEmpleosDAO.consultarEmpleos(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString()).getIbEmpleoList());
        setFichaClienteComplementarioDTO(fichaClienteComplementarioDAO.consultarDatosComplementariosCliente(getSessionScope().get(SessionAttributesNames.CODIGO_CLIENTE).toString()));
        setMotiListString(motivoDAO.consultarMotivo().getiMotivoList());
    }

    public void datosAdicionales() {
        
        
        
        
        setPaisList(paisDAO.consultarPais().getPaisList());
    }

    public void datosRegulatorios() {

    }

    public void informacionEconomica() {

    }
    
    
    
      
    
   
    
    
     
    
    
    
    
    

}
