/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.controller;

import com.bds.wpn.dao.ClienteDAO;
import com.bds.wpn.dao.CuentaDAO;
import com.bds.wpn.dao.DelSurDAO;
import com.bds.wpn.dao.IBAcumuladoTransaccionDAO;
import com.bds.wpn.dao.IbCanalesDAO;
import com.bds.wpn.dao.IbPrefijosOperadorasDAO;
import com.bds.wpn.dao.IbPreguntasDesafioDAO;
import com.bds.wpn.dao.IbUsuarioDAO;
import com.bds.wpn.dao.IbUsuariosP2PDAO;
import com.bds.wpn.dao.NotificacionesDAO;
import com.bds.wpn.dao.P2CDAO;
import com.bds.wpn.dao.P2PDAO;
import com.bds.wpn.dao.PreguntasAutenticacionDAO;
import com.bds.wpn.dto.BancoDTO;
import com.bds.wpn.dto.ClienteDTO;
import com.bds.wpn.dto.CuentaDTO;
import com.bds.wpn.dto.EMailDTO;
import com.bds.wpn.dto.IbAcumuladoTransaccionDTO;
import com.bds.wpn.dto.IbBeneficiariosP2CDTO;
import com.bds.wpn.dto.IbBeneficiariosP2PDTO;
import com.bds.wpn.dto.IbPrefijosOperadorasDTO;
import com.bds.wpn.dto.IbPregDesafioUsuarioDTO;
import com.bds.wpn.dto.IbPreguntasDesafioDTO;
import com.bds.wpn.dto.IbUsuariosDTO;
import com.bds.wpn.dto.IbUsuariosP2PDTO;
import com.bds.wpn.dto.PreguntaAutenticacionDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.dto.TipoPagoDTO;
import com.bds.wpn.dto.UtilDTO;
import com.bds.wpn.enumerated.TipoPagoEnum;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import static com.bds.wpn.util.BDSUtil.CODIGO_RESPUESTA_EXITOSO;
import static com.bds.wpn.util.BDSUtil.CODIGO_SIN_RESULTADOS_JPA;
import static com.bds.wpn.util.BDSUtil.FORMATO_FECHA_COMPLETA;
import static com.bds.wpn.util.BDSUtil.NUEVALINEA;
import static com.bds.wpn.util.BDSUtil.fecha2;
import static com.bds.wpn.util.BDSUtil.formatearFecha;
import com.bds.wpn.util.DESedeEncryption;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author cesar.mujica
 */
@Named("wpnGestionP2PController")
@SessionScoped
public class GestionP2PController extends BDSUtil implements Serializable {

    @Inject
    InicioSesionController sesionController;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;

    @EJB
    IbUsuariosP2PDAO ibUsuariosP2PDAO;

    @EJB
    ClienteDAO clienteDAO;

    @EJB
    NotificacionesDAO notificacionesDAO;

    @EJB
    IbPrefijosOperadorasDAO prefijosOperadorasDAO;

    @EJB
    IBAcumuladoTransaccionDAO acumuladoTransaccionDAO;

    @EJB
    IbPreguntasDesafioDAO ibPreguntasDesafioDAO;

    @EJB
    PreguntasAutenticacionDAO preguntasAutenticacionDAO;

    @EJB
    IbUsuarioDAO usuarioDAO;

    @EJB
    IbCanalesDAO canalesDAO;

    @EJB
    CuentaDAO cuentaDAO;

    @EJB
    DelSurDAO delSurDAO;

    @EJB
    P2PDAO p2pDAO;
    
    @EJB
    P2CDAO p2cDAO;

    @Inject
    TextosController textosController;

    @Inject
    ParametrosController parametrosController;

    private boolean usuarioHabilitadoP2P = false;

//    @Inject
//    TextosController textosController;
//
//    @Inject
//    ParametrosController parametrosController;
    private IbUsuariosP2PDTO usuarioP2P;                    //datos del usuario afiliado al servicio P2P
    private List<CuentaDTO> cuentasCliente;                 //las cuentas asociadas al cliente
    private List<CuentaDTO> cuentasClienteEditar;           //las cuentas asociadas al cliente para editar la afiliacion
    private String ctaSeleccionada;                         //cuenta selesscionada para afiliar al servicio
    private CuentaDTO datosCuentaSeleccionada;              //datos completos de la cuenta seleccionada
    private boolean mantenerDatosForm = false;              //indicador para mantener los datos del formulario
    private boolean usuarioAfiliado = false;                //indicador para saber si el usuario esta afiliado al servicio P2P
    private boolean checkContrato = false;
    private List<IbUsuariosP2PDTO> afiliacionesP2P;         //lista de numeros afiliados al servicio P2P
    private String afiliacionSeleccionada;                  //identificador de la afiliacion seleccionada
    private String codTelfDestino;                          //codigo de operadora de telf destino
    private String telfDestino;                             //Numero de telefono de destino para pago P2P
    private List<IbPrefijosOperadorasDTO> operadoras;       //Prefijos de Operadoras telefónicas
    private String tipoDocBeneficiario;                     //tipo de doc del beneficiario
    private String nroDocBeneficiario;                      //numero de doc del beneficiario
    private String montoPago;                               //monto del pago P2P
    private String descripcionPago;                         //descripcion del pago P2P
    private List<BancoDTO> bancos;                          //listado de bancos venezolanos
    private String infoBanco;                               //informacion del banco seleccionado para P2P separado por "NOMBRE--CODIGO"
    private boolean mostrarDatosAfiliacionP2P = false;      //indicador para mostrar o no la informacion relacionada a la afiliacion P2P
    private BigDecimal saldoDisponibleP2P;                  //saldo disponible para la transferencia de P2P
    private BigDecimal saldoDisponibleP2C;                  //saldo disponible para la transferencia de P2C
    private String saldoDisponibleAfiliacion;               //saldo disponible de la cta afiliada 
    private BigDecimal mtoMaxDiaBco;                        //monto maximo de transacciones acumuladas diarias permitidas por el BCO segun parametro de BD
    private BigDecimal mtoMaxDiaBcoP2C;                     //monto maximo de transacciones acumuladas diarias permitidas por el BCO segun parametro de BD para P2C
    private BigDecimal mtoAcumulado = BigDecimal.ZERO;;                        //monto maximo de transacciones acumuladas diarias por el cliente
    private BigDecimal mtoAcumuladoP2C = BigDecimal.ZERO;;                     //monto maximo de transacciones acumuladas diarias por el cliente en P2C
    private String nombreBanco;                             //nombre de banco seleccionado para pago de P2P
    private String codBanco;                                //código de banco seleccionado para pago de P2P
    private String infoAfiliacion;                          //datos de afilaicion selecconada para pago de P2P
    private String nroReferencia;                           //numero de referencia del pago P2P
    private String numeroCuenta;                            //numero de cuenta para generar las preguntas de seguridad
    private String[] respuestasDD;
    private String[] respuestasDDEnc;
    private ArrayList<IbPreguntasDesafioDTO> preguntasDD = null;
    private int cantPreguntasDesafio = 0;
    private String[] respuestasDA;
    private ArrayList<PreguntaAutenticacionDTO> preguntasDA = null;
    private int cantPreguntasAutenticacion = 0;
    private boolean mostrarPreguntas = false;
    private final String CODIGO_AREA = "0212";
    private final String codigoPlantilla = "52";
    private final String AFILIACION = "afiliacion";
    private final String DESAFILIACION = "desafiliacion";
    private final String EDICION = "edicion";
    private boolean frecuente;
    private boolean panelAlias = false;
    private boolean validFrecuente;
    private String mostrarAlias = "none";
    private String mostrarAlias2 = "none";
    private String mostrarFrecuente = "hidden";
    private List<IbBeneficiariosP2PDTO> beneficiariosP2P;
    private List<IbBeneficiariosP2CDTO> beneficiariosP2C;
    private String aliasSeleccionado;
    private String alias;
    private String alias2;
    private static String aliasAux = "";

    private boolean horarioHabilitado = true;
    private boolean validaCierreOperaciones = true;
    private boolean mostrarMensajeCierre = false;

    private int tipoPagoSelect = 0; 
    private List<TipoPagoDTO> listaTipoPago;
    private boolean showPanel = false; 
    private boolean showPanelP2C = false;
    private String aliasSeleccionadoP2C;
    private boolean frecuenteP2C;
    private boolean validFrecuenteP2C;
    private String mostrarAliasP2C = "none";
    private String mostrarAliasP2C2 = "none";
    private String mostrarFrecuenteP2C = "hidden";
    private String aliasP2CSeleccionado;
    private String aliasP2C;
    private String aliasP2C2;
    private static String aliasAuxP2C = "";

    //////////////GETTERS Y SETTERS////////
    public boolean isCheckContrato() {
        return checkContrato;
    }

    public void setCheckContrato(boolean checkContrato) {
        this.checkContrato = checkContrato;
    }

    public boolean isMostrarPreguntas() {
        return mostrarPreguntas;
    }

    public void setMostrarPreguntas(boolean mostrarPreguntas) {
        this.mostrarPreguntas = mostrarPreguntas;
    }

    public boolean isMantenerDatosForm() {
        return mantenerDatosForm;
    }

    public void setMantenerDatosForm(boolean mantenerDatosForm) {
        this.mantenerDatosForm = mantenerDatosForm;
    }

    public IbUsuariosP2PDTO getUsuarioP2P() {
        return usuarioP2P;
    }

    public void setUsuarioP2P(IbUsuariosP2PDTO usuarioP2P) {
        this.usuarioP2P = usuarioP2P;
    }

    public List<CuentaDTO> getCuentasCliente() {
        return cuentasCliente;
    }

    public List<CuentaDTO> getCuentasClienteEditar() {
        ClienteDTO datosCliente = clienteDAO.listadoCuentasClientes(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (!datosCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeTecnico(),
                    textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeUsuario());
            FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:selectCtaAfiliar", fMsg);
        } else {
            //se combinan las cuentas y se setea la semilla para encriptar la data sensible
            if (datosCliente.getCuentasAhorroCorrienteDTO().size() > 0) {
                if (datosCliente.getCuentasAhorroDTO() != null) {
                    for (int i = 0; i < datosCliente.getCuentasAhorroDTO().size(); i++) {
                        datosCliente.getCuentasAhorroDTO().get(i).setSemilla(sesionController.getSemilla());
                    }
                }
                if (datosCliente.getCuentasCorrienteDTO() != null) {
                    for (int i = 0; i < datosCliente.getCuentasCorrienteDTO().size(); i++) {
                        datosCliente.getCuentasCorrienteDTO().get(i).setSemilla(sesionController.getSemilla());
                    }
                }
                this.cuentasClienteEditar = datosCliente.getCuentasAhorroCorrienteDTO();
            }
        }
        //se filtran las cuentas que no esten afiliadas menos la ya asociada
        List<CuentaDTO> cuentasTemp = new ArrayList<>();
        cuentasTemp.addAll(this.cuentasClienteEditar);
        for (CuentaDTO cta : cuentasTemp) {
            if (!cta.getNumeroCuenta().equalsIgnoreCase(usuarioP2P.getNroCuenta())) {
                for (IbUsuariosP2PDTO usr : afiliacionesP2P) {
                    if (usr.getNroCuenta().equalsIgnoreCase(cta.getNumeroCuenta())) {
                        cuentasClienteEditar.remove(cta);
                        break;
                    }
                }
            }
        }
        if (cuentasClienteEditar == null || cuentasClienteEditar.isEmpty()) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.usuarioYaAfiliadoP2P", sesionController.getNombreCanal())));
        }
        return cuentasClienteEditar;
    }

    public void setCuentasClienteEditar(List<CuentaDTO> cuentasClienteEditar) {
        this.cuentasClienteEditar = cuentasClienteEditar;
    }

    public void setCuentasCliente(List<CuentaDTO> cuentasCliente) {
        this.cuentasCliente = cuentasCliente;
    }

    public String getCtaSeleccionada() {
        return ctaSeleccionada;
    }

    public void setCtaSeleccionada(String ctaSeleccionada) {
        this.ctaSeleccionada = ctaSeleccionada;
        if (this.ctaSeleccionada.equalsIgnoreCase("-1")) {
            datosCuentaSeleccionada = null;
        } else {
            if (sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AFILIARP2P)) {
                for (CuentaDTO cuenta : cuentasCliente) {
                    if (cuenta.getNumeroCuentaEnc().equals(ctaSeleccionada)) {
                        datosCuentaSeleccionada = cuenta;
                        break;
                    }
                }
            }
            if (sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_EDIT_AFIP2P)) {
                for (CuentaDTO cuenta : cuentasClienteEditar) {
                    if (cuenta.getNumeroCuentaEnc().equals(ctaSeleccionada)) {
                        datosCuentaSeleccionada = cuenta;
                        break;
                    }
                }
            }

        }
    }

    public CuentaDTO getDatosCuentaSeleccionada() {
        return datosCuentaSeleccionada;
    }

    public void setDatosCuentaSeleccionada(CuentaDTO datosCuentaSeleccionada) {
        this.datosCuentaSeleccionada = datosCuentaSeleccionada;
    }

    public boolean isUsuarioAfiliado() {
        return usuarioAfiliado;
    }

    public void setUsuarioAfiliado(boolean usuarioAfiliado) {
        this.usuarioAfiliado = usuarioAfiliado;
    }

    public List<IbUsuariosP2PDTO> getAfiliacionesP2P() {
        return afiliacionesP2P;
    }

    public void setAfiliacionesP2P(List<IbUsuariosP2PDTO> afiliacionesP2P) {
        this.afiliacionesP2P = afiliacionesP2P;
    }

    public String getAfiliacionSeleccionada() {
        return afiliacionSeleccionada;
    }

    public void setAfiliacionSeleccionada(String afiliacionSeleccionada) {
        this.afiliacionSeleccionada = afiliacionSeleccionada;
        String mtoTemp;
        if (this.afiliacionSeleccionada.equalsIgnoreCase("-1")) {
            this.usuarioP2P = null;
        } else {
            for (IbUsuariosP2PDTO usr : afiliacionesP2P) {
                if (usr.getId().toString().equalsIgnoreCase(afiliacionSeleccionada)) {
                    this.usuarioP2P = new IbUsuariosP2PDTO();
                    this.usuarioP2P.setEmail(usr.getEmail());
                    this.usuarioP2P.setId(usr.getId());
                    this.usuarioP2P.setIdUsuario(usr.getIdUsuario());
                    this.usuarioP2P.setMtoMaxDiario(formatearMonto(new BigDecimal(usr.getMtoMaxDiario())));
                    this.usuarioP2P.setMtoMaxTransaccion(formatearMonto(new BigDecimal(usr.getMtoMaxTransaccion())));
                    this.usuarioP2P.setNroCuenta(usr.getNroCuenta());
                    this.usuarioP2P.setNroDocumento(usr.getNroDocumento());
                    this.usuarioP2P.setNroTelefono(usr.getNroTelefono());
                    this.usuarioP2P.setRespuestaDTO(usr.getRespuestaDTO());
                    this.usuarioP2P.setTipoDocumento(usr.getTipoDocumento());
                    this.usuarioP2P.setUsuariosP2p(usr.getUsuariosP2p());
                    this.getCuentasClienteEditar();
                    for (CuentaDTO cta : cuentasClienteEditar) {
                        if (cta.getNumeroCuenta().equalsIgnoreCase(usr.getNroCuenta())) {
                            this.ctaSeleccionada = cta.getNumeroCuentaEnc();
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public String getCodTelfDestino() {
        return codTelfDestino;
    }

    public void setCodTelfDestino(String codTelfDestino) {
        this.codTelfDestino = codTelfDestino;
    }

    public String getTelfDestino() {
        return telfDestino;
    }

    public void setTelfDestino(String telfDestino) {
        this.telfDestino = telfDestino;
    }

    public List<IbPrefijosOperadorasDTO> getOperadoras() {
        return operadoras;
    }

    public void setOperadoras(List<IbPrefijosOperadorasDTO> operadoras) {
        this.operadoras = operadoras;
    }

    public String getTipoDocBeneficiario() {
        return tipoDocBeneficiario;
    }

    public void setTipoDocBeneficiario(String tipoDocBeneficiario) {
        this.tipoDocBeneficiario = tipoDocBeneficiario;
    }

    public String getNroDocBeneficiario() {
        return nroDocBeneficiario;
    }

    public void setNroDocBeneficiario(String nroDocBeneficiario) {
        this.nroDocBeneficiario = nroDocBeneficiario;
    }

    public String getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(String montoPago) {
        this.montoPago = montoPago;
    }

    public String getDescripcionPago() {
        return descripcionPago;
    }

    public void setDescripcionPago(String descripcionPago) {
        this.descripcionPago = descripcionPago;
    }

    public String getInfoBanco() {
        return infoBanco;
    }

    public void setInfoBanco(String infoBanco) {
        this.infoBanco = infoBanco;
    }

    public List<BancoDTO> getBancos() {
        return bancos;
    }

    public void setBancos(List<BancoDTO> bancos) {
        this.bancos = bancos;
    }

    public BigDecimal getMtoMaxDiaBco() {
        return mtoMaxDiaBco;
    }

    public void setMtoMaxDiaBco(BigDecimal mtoMaxDiaBco) {
        this.mtoMaxDiaBco = mtoMaxDiaBco;
    }

    public BigDecimal getMtoAcumulado() {
        return mtoAcumulado;
    }

    public void setMtoAcumulado(BigDecimal mtoAcumulado) {
        this.mtoAcumulado = mtoAcumulado;
    }

    public boolean isMostrarDatosAfiliacionP2P() {
        return mostrarDatosAfiliacionP2P;
    }

    public void setMostrarDatosAfiliacionP2P(boolean mostrarDatosAfiliacionP2P) {
        this.mostrarDatosAfiliacionP2P = mostrarDatosAfiliacionP2P;
    }

    public BigDecimal getSaldoDisponibleP2P() {
        return saldoDisponibleP2P;
    }

    public void setSaldoDisponibleP2P(BigDecimal saldoDisponibleP2P) {
        this.saldoDisponibleP2P = saldoDisponibleP2P;
    }

    public String getSaldoDisponibleAfiliacion() {
        return saldoDisponibleAfiliacion;
    }

    public void setSaldoDisponibleAfiliacion(String saldoDisponibleAfiliacion) {
        this.saldoDisponibleAfiliacion = saldoDisponibleAfiliacion;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getCodBanco() {
        return codBanco;
    }

    public void setCodBanco(String codBanco) {
        this.codBanco = codBanco;
    }

    public String getInfoAfiliacion() {
        return infoAfiliacion;
    }

    public void setInfoAfiliacion(String infoAfiliacion) {
        this.infoAfiliacion = infoAfiliacion;
    }

    public String getNroReferencia() {
        return nroReferencia;
    }

    public void setNroReferencia(String nroReferencia) {
        this.nroReferencia = nroReferencia;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String[] getRespuestasDD() {
        return respuestasDD;
    }

    public void setRespuestasDD(String[] respuestasDD) {
        this.respuestasDD = respuestasDD;
    }

    public String[] getRespuestasDDEnc() {
        return respuestasDDEnc;
    }

    public void setRespuestasDDEnc(String[] respuestasDDEnc) {
        this.respuestasDDEnc = respuestasDDEnc;
    }

    public ArrayList<IbPreguntasDesafioDTO> getPreguntasDD() {
        return preguntasDD;
    }

    public void setPreguntasDD(ArrayList<IbPreguntasDesafioDTO> preguntasDD) {
        this.preguntasDD = preguntasDD;
    }

    public int getCantPreguntasDesafio() {
        return cantPreguntasDesafio;
    }

    public void setCantPreguntasDesafio(int cantPreguntasDesafio) {
        this.cantPreguntasDesafio = cantPreguntasDesafio;
    }

    public String[] getRespuestasDA() {
        return respuestasDA;
    }

    public void setRespuestasDA(String[] respuestasDA) {
        this.respuestasDA = respuestasDA;
    }

    public ArrayList<PreguntaAutenticacionDTO> getPreguntasDA() {
        return preguntasDA;
    }

    public void setPreguntasDA(ArrayList<PreguntaAutenticacionDTO> preguntasDA) {
        this.preguntasDA = preguntasDA;
    }

    public int getCantPreguntasAutenticacion() {
        return cantPreguntasAutenticacion;
    }

    public void setCantPreguntasAutenticacion(int cantPreguntasAutenticacion) {
        this.cantPreguntasAutenticacion = cantPreguntasAutenticacion;
    }

    //////////METODOS FUNCIONALES///////////
    /**
     * metodo generico que se encarga de limpiar el formulario
     */
    public void limpiarContrato() {
        this.checkContrato = false;
    }

    /**
     * metodo generico que se encarga de limpiar el formulario
     */
    public void limpiar() {
        if (sesionController.isReiniciarForm()) {
            this.mostrarPreguntas = false;
            this.checkContrato = false;
            this.cantPreguntasDesafio = 0;
            this.numeroCuenta = null;
            this.respuestasDD = null;
            this.preguntasDD = null;
            this.preguntasDA = null;
            this.respuestasDA = null;
            this.usuarioP2P = new IbUsuariosP2PDTO();
            this.usuarioP2P.setNroTelefono(sesionController.getUsuario().getCelular());
            this.usuarioP2P.setEmail(sesionController.getUsuario().getEmail());
            this.usuarioP2P.setTipoDocumento(String.valueOf(sesionController.getUsuario().getTipoDoc()));
            this.usuarioP2P.setNroDocumento(sesionController.getUsuario().getDocumento());
            this.mantenerDatosForm = false;
            sesionController.setReiniciarForm(false);
            this.datosCuentaSeleccionada = null;
            this.ctaSeleccionada = "-1";
            this.afiliacionSeleccionada = "-1";

            afiliacionesP2P = null;
            IbUsuariosP2PDTO userP2PTemp = ibUsuariosP2PDAO.consultarUsuarioP2P(sesionController.getUsuario().getId().toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
            if (userP2PTemp != null && userP2PTemp.getRespuestaDTO() != null && userP2PTemp.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                    && userP2PTemp.getUsuariosP2p() != null && !userP2PTemp.getUsuariosP2p().isEmpty()) {
                afiliacionesP2P = userP2PTemp.getUsuariosP2p();
            }
            if (afiliacionesP2P == null || afiliacionesP2P.isEmpty()) {
                //EDITAR MSJ ERROR SIN AFIL
                if (sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_EDIT_AFIP2P)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.usuarioNoAfiliadoP2P", sesionController.getNombreCanal())));
                }//DESAFILIAR MSJ ERROR SIN AFIL
                if (sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_DESAFILIARP2P)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formDesafiliarServ2:divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.usuarioNoAfiliadoP2P", sesionController.getNombreCanal())));
                }
            }

            if (sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AFILIARP2P)) {
                //AFILIAR FILTRO DE CTAS DISPONIBLES
                ClienteDTO datosCliente = clienteDAO.listadoCuentasClientes(sesionController.getUsuario().getCodUsuario(), sesionController.getIdCanal(), sesionController.getNombreCanal());
                if (!datosCliente.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:selectCtaAfiliar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosFacade.findByCodigo("pnw.global.error.generico", sesionController.getNombreCanal()).getMensajeUsuario()));
                } else {
                    //se combinan las cuentas y se setea la semilla para encriptar la data sensible
                    if (datosCliente.getCuentasAhorroCorrienteDTO().size() > 0) {
                        if (datosCliente.getCuentasAhorroDTO() != null) {
                            for (int i = 0; i < datosCliente.getCuentasAhorroDTO().size(); i++) {
                                datosCliente.getCuentasAhorroDTO().get(i).setSemilla(sesionController.getSemilla());
                            }
                        }
                        if (datosCliente.getCuentasCorrienteDTO() != null) {
                            for (int i = 0; i < datosCliente.getCuentasCorrienteDTO().size(); i++) {
                                datosCliente.getCuentasCorrienteDTO().get(i).setSemilla(sesionController.getSemilla());
                            }
                        }
                        this.cuentasCliente = datosCliente.getCuentasAhorroCorrienteDTO();
                    }
                }
                //se filtran las cuentas que no esten afiliadas
                if (afiliacionesP2P != null && !afiliacionesP2P.isEmpty()) {
                    List<CuentaDTO> cuentasTemp = new ArrayList<>();
                    cuentasTemp.addAll(this.cuentasCliente);
                    //se elimina de la lista de cuentas disponibles las cuentas ya afiliadas                
                    for (CuentaDTO cta : cuentasTemp) {
                        for (IbUsuariosP2PDTO usr : afiliacionesP2P) {
                            if (usr.getNroCuenta().equalsIgnoreCase(cta.getNumeroCuenta())) {
                                cuentasCliente.remove(cta);
                                break;
                            }
                        }
                    }
                }
                if (cuentasCliente == null || cuentasCliente.isEmpty()) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.usuarioYaAfiliadoP2P", sesionController.getNombreCanal())));
                }
            }
        }
    }

    /**
     * metodo generico que se encarga de limpiar el formulario
     */
    public void iniciarPagos() {
        validarUsuarioPiloto();

        if (usuarioHabilitadoP2P) {
            mostrarDatosAfiliacionP2P = false;
            //se consultan las afiliaciones de P2P de no poseer afiliaciones no se muestra el formulario de pagos
            this.afiliacionesP2P = ibUsuariosP2PDAO.consultarUsuarioP2P(sesionController.getUsuario().getId().toString(), sesionController.getIdCanal(), sesionController.getNombreCanal()).getUsuariosP2p();

            if (this.afiliacionesP2P != null && !this.afiliacionesP2P.isEmpty()) {
                this.setBeneficiariosP2P(afiliacionesP2P.get(0).getBeneficiariosP2P());
                this.setBeneficiariosP2C(afiliacionesP2P.get(0).getBeneficiariosP2C());
                //se consultan los acumulados para validar el de P2P
                IbAcumuladoTransaccionDTO acumulado = acumuladoTransaccionDAO.consultarAcumuladoUsuario(sesionController.getUsuario().getId().toString(), sesionController.getNombreCanal(), sesionController.getIdCanal());
                this.mtoAcumulado = BigDecimal.ZERO;
                if (acumulado != null && acumulado.getRespuestaDTO() != null && acumulado.getRespuestaDTO().getCodigo() != null && acumulado.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    if (acumulado.getAcumuladoP2P() != null && acumulado.getAcumuladoP2P().compareTo(BigDecimal.ZERO) == 1) {
                        this.mtoAcumulado = acumulado.getAcumuladoP2P();
                    }

                    if (acumulado.getAcumuladoP2C() != null && acumulado.getAcumuladoP2C().compareTo(BigDecimal.ZERO) == 1) {
                        this.mtoAcumuladoP2C = acumulado.getAcumuladoP2C();
                    }

                } else {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formPagarP2P:divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                }
                //se obtiene el valor de referencia del parametro del acumulado max por dia para P2P del bco
                this.mtoMaxDiaBco = new BigDecimal(parametrosController.getValorParametro("pnw.limite.p2p.maxDiario", sesionController.getIdCanal()));
                this.mtoMaxDiaBcoP2C = new BigDecimal(parametrosController.getValorParametro("limite.p2c.maxDiario", sesionController.getIdCanal()));
//                this.mtoMaxDiaBco = new BigDecimal(this.afiliacionesP2P.get(0).getMtoMaxDiario());
                //se obtiene el saldo disponible para pago P2P
                this.saldoDisponibleP2P = mtoMaxDiaBco.subtract(mtoAcumulado);
                //se obtiene el saldo disponible para pago P2C
                this.saldoDisponibleP2C = mtoMaxDiaBcoP2C.subtract(mtoAcumuladoP2C);
                if (this.saldoDisponibleP2P.compareTo(BigDecimal.ZERO) == 1 || this.saldoDisponibleP2C.compareTo(BigDecimal.ZERO) == 1) {
                    //si el cliente posee saldo disponible mayor a 0 bolivares entonces se muestra el formulario de pago
                    mostrarDatosAfiliacionP2P = true;

                    this.bancos = delSurDAO.listadoBancosSwitch7B(sesionController.getNombreCanal(), sesionController.getIdCanal()).getBancos();
                    asignarNombreBanco(this.getBeneficiariosP2P());
                    this.operadoras = prefijosOperadorasDAO.listaPrefijosOperadoras(sesionController.getNombreCanal()).getIbPrefijosOperadorasDTO();
                    this.operadoras.removeIf(p -> p.getOperadora().equals(CODIGO_AREA));
                    if (sesionController.isReiniciarForm()) {
                        this.afiliacionSeleccionada = "-1";
                        this.codTelfDestino = "";
                        this.telfDestino = "";
                        this.tipoDocBeneficiario = "";
                        this.nroDocBeneficiario = "";
                        this.montoPago = "";
                        this.descripcionPago = "";
                        this.infoBanco = "";
                        this.saldoDisponibleAfiliacion = "";
                        this.aliasSeleccionado = "";
                        this.frecuente = false;
                        this.alias = "";
                        this.mostrarAlias = "none";
                        this.mostrarFrecuente = "hidden";

                        this.aliasSeleccionadoP2C = "";
                        this.frecuenteP2C = false;
                        this.aliasP2C = "";
                        this.mostrarAliasP2C = "none";
                        this.mostrarFrecuenteP2C = "hidden";
                        sesionController.setReiniciarForm(false);
                    }
                } else {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formPagarP2P:divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.limiteMaxDiaP2PSuperado", sesionController.getNombreCanal())));
                }
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formPagarP2P:divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.usuarioNoAfiliadoP2P", sesionController.getNombreCanal())));
            }
        }
    }

    /**
     *
     */
    public void obtenerDatosAlias() {
        boolean sw = false;
        for (IbBeneficiariosP2PDTO ibBeneficiariosP2PDTO : this.beneficiariosP2P) {
            if (ibBeneficiariosP2PDTO.getId().toString().equals(this.aliasSeleccionado)) {
                for (BancoDTO banco : this.getBancos()) {
                    if (banco.getCodigoBanco().equals(BDSUtil.cerosIzquierda(String.valueOf(ibBeneficiariosP2PDTO.getCodigoBanco()), 4))) {
                        this.setInfoBanco(banco.getNombreBanco() + "--" + banco.getCodigoBanco());
                        break;
                    }
                    if (BDSUtil.cerosIzquierda(String.valueOf(ibBeneficiariosP2PDTO.getCodigoBanco()), 4).equals(DIGITOS_INICIALES_BANCO_DEL_SUR)) {
                        this.setInfoBanco("DELSUR BANCO UNIVERSAL C.A.--" + DIGITOS_INICIALES_BANCO_DEL_SUR);
                        break;
                    }
                }
                this.setTipoDocBeneficiario(ibBeneficiariosP2PDTO.getTipoDocumento());
                this.setNroDocBeneficiario(ibBeneficiariosP2PDTO.getNroDocumento());
                this.setTelfDestino(ibBeneficiariosP2PDTO.getNroTelefono().substring(4, ibBeneficiariosP2PDTO.getNroTelefono().length()));
                this.setCodTelfDestino(ibBeneficiariosP2PDTO.getNroTelefono().substring(0, 4));
                this.aliasAux = ibBeneficiariosP2PDTO.getAliasBeneficiario();
                this.mostrarFrecuente = "none";
                this.setFrecuente(false);
                sw = true;
                break;
            }
        }
        if (!sw) {
            this.setTipoDocBeneficiario("V");
            this.setNroDocBeneficiario("");
            this.setTelfDestino("");
            this.setCodTelfDestino("");
            this.setFrecuente(false);
            this.setInfoBanco("-1");
        }

    }

    public void obtenerDatosAliasP2C() {
        boolean sw = false;
        for (IbBeneficiariosP2CDTO ibBeneficiariosP2CDTO : this.beneficiariosP2C) {
            if (ibBeneficiariosP2CDTO.getId().toString().equals(this.aliasSeleccionadoP2C)) {
                for (BancoDTO banco : this.getBancos()) {
                    if (banco.getCodigoBanco().equals(BDSUtil.cerosIzquierda(String.valueOf(ibBeneficiariosP2CDTO.getCodigoBanco()), 4))) {
                        this.setInfoBanco(banco.getNombreBanco() + "--" + banco.getCodigoBanco());
                        break;
                    }
                    if (BDSUtil.cerosIzquierda(String.valueOf(ibBeneficiariosP2CDTO.getCodigoBanco()), 4).equals(DIGITOS_INICIALES_BANCO_DEL_SUR)) {
                        this.setInfoBanco("DELSUR BANCO UNIVERSAL C.A.--" + DIGITOS_INICIALES_BANCO_DEL_SUR);
                        break;
                    }
                }
                this.setTipoDocBeneficiario(ibBeneficiariosP2CDTO.getTipoDocumento());
                this.setNroDocBeneficiario(ibBeneficiariosP2CDTO.getNroDocumento());
                this.setTelfDestino(ibBeneficiariosP2CDTO.getNroTelefono().substring(4, ibBeneficiariosP2CDTO.getNroTelefono().length()));
                this.setCodTelfDestino(ibBeneficiariosP2CDTO.getNroTelefono().substring(0, 4));
                this.aliasAuxP2C = ibBeneficiariosP2CDTO.getAliasBeneficiario();
                this.mostrarFrecuenteP2C = "none";
                this.setFrecuenteP2C(false);
                sw = true;
                break;
            }
        }
        if (!sw) {
            this.setTipoDocBeneficiario("V");
            this.setNroDocBeneficiario("");
            this.setTelfDestino("");
            this.setCodTelfDestino("");
            this.setFrecuenteP2C(false);
            this.setInfoBanco("-1");
        }

    }

    /**
     * metodo generico que se encarga de limpiar el formulario
     */
    public void obtenerDatosAfiliacionP2P() {
        //se consulta en linea el acumulado y disponibles por si se realizo otra transaccion 
        IbAcumuladoTransaccionDTO acumulado = acumuladoTransaccionDAO.consultarAcumuladoUsuario(sesionController.getUsuario().getId().toString(), sesionController.getNombreCanal(), sesionController.getIdCanal());
        this.mtoAcumulado = BigDecimal.ZERO;
        if (acumulado != null && acumulado.getRespuestaDTO() != null && acumulado.getRespuestaDTO().getCodigo() != null && acumulado.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {

            if (acumulado.getAcumulados() != null && !acumulado.getAcumulados().isEmpty() && acumulado.getAcumulados().get(0).getAcumuladoP2P() != null && acumulado.getAcumulados().get(0).getAcumuladoP2P().compareTo(BigDecimal.ZERO) == 1) {
                this.mtoAcumulado = acumulado.getAcumulados().get(0).getAcumuladoP2P();
            }
        } else {
            this.afiliacionSeleccionada = "-1";
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formPagarP2P:selectTelfOrig", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        }
        this.mtoMaxDiaBco = new BigDecimal(parametrosController.getValorParametro("pnw.limite.p2p.maxDiario", sesionController.getIdCanal()));
        this.saldoDisponibleP2P = mtoMaxDiaBco.subtract(mtoAcumulado);
        if (this.afiliacionSeleccionada != null && !this.afiliacionSeleccionada.equalsIgnoreCase("-1")) {
            //se consulta en linea el acumulado y disponibles por si se realizo otra transaccion            
            this.afiliacionesP2P = ibUsuariosP2PDAO.consultarUsuarioP2P(sesionController.getUsuario().getId().toString(), sesionController.getIdCanal(), sesionController.getNombreCanal()).getUsuariosP2p();
            for (IbUsuariosP2PDTO afiliacion : this.afiliacionesP2P) {
                if (afiliacion.getId().toString().equalsIgnoreCase(this.afiliacionSeleccionada)) {
                    this.usuarioP2P = afiliacion;
                    break;
                }
            }
            UtilDTO util = cuentaDAO.obtenerSaldoDisponibleCta(this.usuarioP2P.getNroCuenta(), sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (util != null && util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                if (util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    this.saldoDisponibleAfiliacion = util.getResuladosDTO().get("saldo").toString();
                } else {
                    this.saldoDisponibleAfiliacion = util.getResuladosDTO().get("saldo").toString();
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formPagarP2P:txtSaldoDispCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", util.getRespuestaDTO().getDescripcionSP()));
                }
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formPagarP2P:txtSaldoDispCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            }
        } else {
            this.saldoDisponibleAfiliacion = "0,00";
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoPagar2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_PAGARP2P)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPagarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoPagar3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_PAGARP2P)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoPagar4() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_PAGARP2P)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoAfiliar2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AFILIARP2P)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
        validarUsuarioPiloto();
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoAfiliar3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AFILIARP2P)) {
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
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AFILIARP2P)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoAfiliar5() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 5 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_AFILIARP2P)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoEditarAfiliacion2() {
        if (!FacesContext.getCurrentInstance().isPostback() && sesionController.isReiniciarForm()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_EDIT_AFIP2P)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
        validarUsuarioPiloto();
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoEditarAfiliacion3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_EDIT_AFIP2P)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoEditarAfiliacion4() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_EDIT_AFIP2P)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoDesafiliar2() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 2 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_DESAFILIARP2P)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
        validarUsuarioPiloto();
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoDesafiliar3() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 3 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_DESAFILIARP2P)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo generico que valida el flujo del pasos y si no se cumple
     * redireeciona al paso 1
     */
    public void validarFlujoDesafiliar4() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (sesionController.getValidadorFlujo() != 4 || !sesionController.getIdTransaccion().equals(BDSUtil.ID_TRANS_DESAFILIARP2P)) {
                sesionController.setValidadorFlujo(1);
                this.regresarPaso1();
            }
        }
    }

    /**
     * metodo utilitario que redirecciona al OTP
     */
    public void regresarPagarPaso1() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.otp.url.validacionOtp", sesionController.getIdCanal()));
    }

    /**
     * metodo para redirigir a paso 2 de Pagar P2P
     */
    public void regresarPagarPaso2() {
        if (!this.getAliasSeleccionado().equals("-1")) {
            this.codTelfDestino = "";
            this.telfDestino = "";
            this.tipoDocBeneficiario = "";
            this.nroDocBeneficiario = "";
            this.infoBanco = "";
            this.aliasSeleccionado = "-1";
            this.frecuente = false;
            this.alias = "";
            this.mostrarAlias = "none";
            this.mostrarFrecuente = "hidden";
        }
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagarP2P.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo que se encargar de redireccionar al paso 3 de la pagar P2P
     */
    public void pagarPaso3() {
        int cantErrores = 0;
        IbUsuariosP2PDTO afiliacion = null;

        UtilDTO util = delSurDAO.verificarBloqueBanco(this.infoBanco.split("--")[1], sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (util.getRespuestaDTO() != null && util.getResuladosDTO() != null || util.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)
                || util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            if ((Boolean) util.getResuladosDTO().get("bancoBloqueado")) {
                cantErrores++;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formPagarP2P:divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.bancoBloqueado", sesionController.getNombreCanal())));
            }
        } else {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formPagarP2P:divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.bcoExecpcion", sesionController.getNombreCanal())));
        }
        for (IbUsuariosP2PDTO afilP2P : this.afiliacionesP2P) {
            if (afilP2P.getId().toString().equalsIgnoreCase(this.afiliacionSeleccionada)) {
                afiliacion = afilP2P;
                break;
            }
        }

        //validacion de monto Max por transaccion
        if (afiliacion != null) {

            if (this.isFrecuente() && this.alias.equals("") && this.getAliasSeleccionado().equals("-1")) {
                cantErrores++;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formPagarP2P:nombreAlias", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pjw.global.texto.requerido", sesionController.getNombreCanal())));
            }
            if (this.infoBanco.split("--")[1].equalsIgnoreCase("0157")) {
                if (afiliacion.getNroTelefono().equalsIgnoreCase(this.codTelfDestino + this.telfDestino)) {
                    cantErrores++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formPagarP2P:txtNroTelfDest", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.ctasDiferentes", sesionController.getNombreCanal())));
                }
            }

            BigDecimal mtoPag = new BigDecimal(eliminarformatoSimpleMonto(this.getMontoPago()));
            BigDecimal mtoTrs = new BigDecimal(afiliacion.getMtoMaxTransaccion());
            if (tipoPagoSelect == TipoPagoEnum.P2P.getId()) {
                if (mtoPag.compareTo(mtoTrs) == 1) {
                    //MONTO SUPERIOR AL LIMITE POR TRANSACCION CONFIGURADO
                    cantErrores++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formPagarP2P:txtMtoPago", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoPagoLimiteTrsP2P", sesionController.getNombreCanal())));
                }

                if (mtoPag.compareTo(BigDecimal.ONE) == -1) {
                    //MONTO DEBE SER SUPERIOR A 1 BF
                    cantErrores++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formPagarP2P:txtMtoPago", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoInv", sesionController.getNombreCanal())));
                }

                if (mtoPag.compareTo(this.saldoDisponibleP2P) == 1) {
                    //MONTO SUPERIOR AL LIMITE DISPONIBLE MAXIMO DIARIO PERMITIDO POR EL BANCO
                    cantErrores++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formPagarP2P:txtMtoPago", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoPagoLimiteDiaP2P", sesionController.getNombreCanal())));
                }
                if (mtoPag.compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.saldoDisponibleAfiliacion))) == 1) {
                    //MONTO SUPERIOR AL LIMITE DISPONIBLE MAXIMO DIARIO PERMITIDO POR EL BANCO
                    cantErrores++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formPagarP2P:txtMtoPago", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoPagoSinFondosP2P", sesionController.getNombreCanal())));
                }
            } else { //P2C
                if (mtoPag.compareTo(mtoTrs) == 1) {
                    //MONTO SUPERIOR AL LIMITE POR TRANSACCION CONFIGURADO
                    cantErrores++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formPagarP2P:txtMtoPagoP2C", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoPagoLimiteTrsP2P", sesionController.getNombreCanal())));
                }

                if (mtoPag.compareTo(BigDecimal.ONE) == -1) {
                    //MONTO DEBE SER SUPERIOR A 1 BF
                    cantErrores++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formPagarP2P:txtMtoPagoP2C", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoInv", sesionController.getNombreCanal())));
                }

                if (mtoPag.compareTo(this.saldoDisponibleP2P) == 1) {
                    //MONTO SUPERIOR AL LIMITE DISPONIBLE MAXIMO DIARIO PERMITIDO POR EL BANCO
                    cantErrores++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formPagarP2P:txtMtoPagoP2C", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoPagoLimiteDiaP2P", sesionController.getNombreCanal())));
                }
                if (mtoPag.compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.saldoDisponibleAfiliacion))) == 1) {
                    //MONTO SUPERIOR AL LIMITE DISPONIBLE MAXIMO DIARIO PERMITIDO POR EL BANCO
                    cantErrores++;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formPagarP2P:txtMtoPagoP2C", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoPagoSinFondosP2P", sesionController.getNombreCanal())));
                }
            }
        } else {
            //error al intentar obtener los datos de la afiliacion P2P
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formPagarP2P:pago", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        }
        if (cantErrores > 0) {
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagarP2P.url.paso2", sesionController.getIdCanal()));
        } else {
            this.nombreBanco = this.infoBanco.split("--")[0];
            this.codBanco = this.infoBanco.split("--")[1];
            this.infoAfiliacion = this.usuarioP2P.getNroTelefono() + " " + this.formatoAsteriscosWeb(this.usuarioP2P.getNroCuenta());
            sesionController.setValidadorFlujo(3);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagarP2P.url.paso3", sesionController.getIdCanal()));
            if (tipoPagoSelect == TipoPagoEnum.P2P.getId()) {
                if (!this.aliasAux.equals("") && !this.getAliasSeleccionado().equals("-1")) {
                    this.setAlias2(aliasAux);
                    this.setMostrarAlias2("hidden");

                } else if (!this.alias.equals("") && this.isFrecuente() && this.getAliasSeleccionado().equals("-1")) {
                    this.setAlias2(this.getAlias());
                    this.setMostrarAlias2("hidden");
                } else {
                    this.setAlias2("");
                    this.setMostrarAlias2("none");
                }
            } else { //P2C
                if (!this.aliasAux.equals("") && !this.getAliasSeleccionadoP2C().equals("-1")) {
                    this.setAlias2(aliasAux);
                    this.setMostrarAlias2("hidden");

                } else if (!this.aliasP2C.equals("") && this.isFrecuenteP2C() && this.getAliasSeleccionadoP2C().equals("-1")) {
                    this.setAlias2(this.getAliasP2C());
                    this.setMostrarAlias2("hidden");
                } else {
                    this.setAlias2("");
                    this.setMostrarAlias2("none");
                }
            }
        }
    }

    /**
     * metodo que se encargar de redireccionar al paso 4 de la pagar P2P
     */
    public void pagarPaso4() {
        if (!this.getAliasSeleccionado().equals("-1")) { //valida que solo inserte cuando haya un alias nuevo
            this.setFrecuente(false);
        }
        UtilDTO utilP2P;

        if (tipoPagoSelect == TipoPagoEnum.P2P.getId()) {
            if (this.codBanco.equalsIgnoreCase(parametrosController.getValorParametro("pnw.global.bin.nroCta", sesionController.getIdCanal()))) {
                //pago P2P DELSUR
                utilP2P = p2pDAO.realizarPagoP2PTercerosDelsur(CODIGO_CANAL_P2P, ajustarTelfP2P(this.usuarioP2P.getNroTelefono()), ajustarTelfP2P(this.codTelfDestino + this.telfDestino),
                        soloNumeros(this.nroDocBeneficiario), eliminarformatoSimpleMonto(this.montoPago), this.descripcionPago, sesionController.getUsuario().getTipoDoc() + soloNumeros(sesionController.getUsuario().getDocumento()),
                        parametrosController.getNombreParametro("roas.wsdl.p2p", sesionController.getIdCanal()), this.isFrecuente(), this.getAlias(), this.usuarioP2P.getIdUsuario(), this.tipoDocBeneficiario, sesionController.getIdCanal(), sesionController.getNombreCanal());
            } else {
                //pago P2P OTROS BANCOS
                utilP2P = p2pDAO.realizarPagoP2PTercerosOtrosBancos(CODIGO_CANAL_P2P, ajustarTelfP2P(this.usuarioP2P.getNroTelefono()), ajustarTelfP2P(this.codTelfDestino + this.telfDestino),
                        soloNumeros(this.nroDocBeneficiario), eliminarformatoSimpleMonto(this.montoPago), this.descripcionPago, "0000000" + this.codBanco, sesionController.getUsuario().getTipoDoc() + soloNumeros(sesionController.getUsuario().getDocumento()),
                        parametrosController.getNombreParametro("roas.wsdl.p2p", sesionController.getIdCanal()), this.isFrecuente(), this.getAlias(), this.usuarioP2P.getIdUsuario(), this.tipoDocBeneficiario, sesionController.getIdCanal(), sesionController.getNombreCanal());
            }
        } else { //P2C
            if (this.codBanco.equalsIgnoreCase(parametrosController.getValorParametro("pnw.global.bin.nroCta", sesionController.getIdCanal()))) {
                //pago P2P DELSUR
                utilP2P = p2cDAO.realizarPagoP2CTercerosDelsur(CODIGO_CANAL_P2P, ajustarTelfP2P(this.usuarioP2P.getNroTelefono()), ajustarTelfP2P(this.codTelfDestino + this.telfDestino),
                        soloNumeros(this.nroDocBeneficiario), eliminarformatoSimpleMonto(this.montoPago), this.descripcionPago, sesionController.getUsuario().getTipoDoc() + soloNumeros(sesionController.getUsuario().getDocumento()),
                        parametrosController.getNombreParametro("roas.wsdl.p2c", sesionController.getIdCanal()), this.isFrecuente(), this.getAlias(), this.usuarioP2P.getIdUsuario(), this.tipoDocBeneficiario, sesionController.getIdCanal(), sesionController.getNombreCanal());
            } else {
                //pago P2P OTROS BANCOS
                utilP2P = p2cDAO.realizarPagoP2CTercerosOtrosBancos(CODIGO_CANAL_P2P, ajustarTelfP2P(this.usuarioP2P.getNroTelefono()), ajustarTelfP2P(this.codTelfDestino + this.telfDestino),
                        soloNumeros(this.nroDocBeneficiario), eliminarformatoSimpleMonto(this.montoPago), this.descripcionPago, "0000000" + this.codBanco, sesionController.getUsuario().getTipoDoc() + soloNumeros(sesionController.getUsuario().getDocumento()),
                        parametrosController.getNombreParametro("roas.wsdl.p2c", sesionController.getIdCanal()), this.isFrecuente(), this.getAlias(), this.usuarioP2P.getIdUsuario(), this.tipoDocBeneficiario, sesionController.getIdCanal(), sesionController.getNombreCanal());
            }
        }

        //INVOCAR AL WS DE PAGO P2P
        if (utilP2P.getRespuestaDTO() != null && utilP2P.getRespuestaDTO().getCodigo() != null && utilP2P.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            if (utilP2P.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {

                this.nroReferencia = utilP2P.getResuladosDTO().get("referencia").toString();
                sesionController.setValidadorFlujo(4);
                acumuladoTransaccionDAO.insertarAcumuladoTransaccion(sesionController.getUsuario().getId().toString(), this.montoPago, this.getTipoPagoSelect() == TipoPagoEnum.P2P.getId() ? ID_TRANS_PAGARP2P : ID_TRANS_PAGARP2C, sesionController.getNombreCanal(), sesionController.getIdCanal());
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), sesionController.getIdTransaccion(), formatoAsteriscosWeb(this.usuarioP2P.getNroTelefono()),
                        formatoAsteriscosWeb(this.codTelfDestino + this.telfDestino), "Pago P2P - BCO: " + this.codBanco, this.montoPago, this.nroReferencia, "", this.tipoDocBeneficiario, this.nroDocBeneficiario, "", "");
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagarP2P.url.paso4", sesionController.getIdCanal()));
                /////////NOTIFICACION VIA SMS//////// 
                String texto = "Bs." + this.montoPago + " el dia " + formatearFecha(new Date(), FORMATO_FECHA_HORA_MINUTO) + " Banco destino " + this.nombreBanco + "REF-" + this.nroReferencia;
                HashMap<String, String> parametros = new HashMap();
                parametros.put("\\$POTP", texto);

                String textoSMS = textosController.getNombreTexto("pnw.global.sms.cuerpo.pago.p2p", sesionController.getNombreCanal());
                String motivoSMS = textosController.getNombreTexto("pnw.global.sms.motivo.pago.p2p", sesionController.getNombreCanal());
                String numeroTefl = sesionController.getUsuario().getCelular();
                sesionController.enviarSMS(numeroTefl, textoSMS, parametros, motivoSMS);

                String parametrosCorreo = sesionController.getUsuario().getNombre() + "~"
                        + this.formatoAsteriscosWeb(this.codTelfDestino + this.telfDestino) + "~"
                        + this.tipoDocBeneficiario + this.nroDocBeneficiario + "~"
                        + this.nombreBanco + "~"
                        + this.montoPago + "~"
                        + this.formatoAsteriscosWeb(usuarioP2P.getNroTelefono()) + "~"
                        + formatearFecha(new Date(), FORMATO_FECHA_HORA_MINUTO) + "~"
                        + this.nroReferencia + "~" + this.descripcionPago;

                notificacionesDAO.enviarEmailPN(codigoPlantilla, usuarioP2P.getEmail(), parametrosCorreo, sesionController.getIdCanal(), sesionController.getNombreCanal());
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formPagarP2P:divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_WARN, "", utilP2P.getRespuestaDTO().getDescripcionSP()));
                sesionController.setValidadorFlujo(3);
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagarP2P.url.paso3", sesionController.getIdCanal()));
            }
        } else if (utilP2P.getRespuestaDTO() != null && utilP2P.getRespuestaDTO().getDescripcion() != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formPagarP2P:divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", utilP2P.getRespuestaDTO().getDescripcion()));
            sesionController.setValidadorFlujo(3);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagarP2P.url.paso3", sesionController.getIdCanal()));
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formPagarP2P:divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            sesionController.setValidadorFlujo(3);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.pagarP2P.url.paso3", sesionController.getIdCanal()));
        }
    }

    public void regresarPasoPosCon() {
        this.redirectFacesContext("/sec/noticias/noticias.xhtml?faces-redirect=true");
        //this.redirectFacesContext(parametrosController.getNombreParametro("pnw.otp.url.validacionOtp", sesionController.getIdCanal()));
    }

    /**
     * metodo utilitario que redirecciona al OTP
     */
    public void regresarPaso1() {
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.otp.url.validacionOtp", sesionController.getIdCanal()));
    }

    /**
     * metodo para redirigir a paso 2 de afiliar P2P
     */
    public void regresarAfiliarPaso2() {
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo para redirigir a paso 3 de afiliar P2P
     */
    public void regresarAfiliarPaso3() {
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(3);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso3", sesionController.getIdCanal()));
    }

    /**
     * metodo action para ir a paso 2 de afiliar
     *
     */
    public void afiliarPaso2() {
        if (this.isCheckContrato()) {
            sesionController.setValidadorFlujo(2);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:checkContrato", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosFacade.textoParametro("pnw.errores.texto.checkBoxInv", sesionController.getIdCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.pasoContrato", sesionController.getIdCanal()));
        }
    }

    /**
     * metodo action para ir a paso 3 de afiliar
     *
     */
    public void afiliarPaso3() {
        int errorValidacion = 0;
        String retorno = "";
        desencriptarRespuestasWeb();

        if (preguntasDD.size() != countResp(respuestasDDEnc) || preguntasDA.size() != countResp(respuestasDA)) {

            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.campos.requeridos", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));

        } else {
            if (!validarAlfaNumericoYEspacio(respuestasDDEnc) && !validarAlfaNumericoYEspacio(respuestasDA)) {

                UtilDTO util = ibPreguntasDesafioDAO.validarPreguntaDD(sesionController.getUsuario().getId().toString(), obtenerRespuestasDesafio(), "-", sesionController.getNombreCanal(), sesionController.getIdCanal());

                if (util == null || util.getRespuestaDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
                    respuestasDD = null;
                    respuestasDA = null;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
                    return;
                } else if (util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    respuestasDD = null;
                    respuestasDA = null;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
                    return;
                } else {
                    if (util.getResuladosDTO() != null && (Boolean) util.getResuladosDTO().get("resultado")) {
                        util = preguntasAutenticacionDAO.validarPDAporCliente(sesionController.getUsuario().getTdd(), this.numeroCuenta, obtenerRespuestasAutenticacion(), "-", sesionController.getNombreCanal(), sesionController.getIdCanal());

                        if (util == null || util.getRespuestaDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
                            respuestasDD = null;
                            respuestasDA = null;
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
                            return;
                        } else if (util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                            respuestasDD = null;
                            respuestasDA = null;
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
                            return;
                        } else {
                            if ((Boolean) util.getResuladosDTO().get("resultado")) {
                                //SE RESETEA EL CONTADOR DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
                                RespuestaDTO respuesta = usuarioDAO.actualizarPregSegFallidas(sesionController.getUsuario().getId().toString(), 0, sesionController.getNombreCanal(), sesionController.getIdCanal());

                                if (respuesta == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                    //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
                                    respuestasDD = null;
                                    respuestasDA = null;
                                    FacesContext context = FacesContext.getCurrentInstance();
                                    context.getExternalContext().getFlash().setKeepMessages(true);
                                    FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
                                    return;
                                } else if (respuesta != null && respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                                    respuestasDD = null;
                                    respuestasDA = null;
                                    FacesContext context = FacesContext.getCurrentInstance();
                                    context.getExternalContext().getFlash().setKeepMessages(true);
                                    FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getTextoSP()));
                                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
                                    return;
                                }

                                //////////////////////////////////////////
                                //EN ESTE RENGLÓN SE PROCEDE A REALIZAR LA CARGA DE LA PÁGINA SIGUIENTE
                                sesionController.setValidadorFlujo(3);
                                sesionController.setReiniciarForm(true);
                                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso3", sesionController.getIdCanal()));
                                return;
                                //////////////////////////////////////////
                            } else {
                                //INDICAMOS QUE SE ENCONTRO UNA INCOSISTENCIA EN LAS RESPUESTAS
                                errorValidacion++;
                            }
                        }
                    } else {
                        //INDICAMOS QUE SE ENCONTRO UNA INCOSISTENCIA EN LAS RESPUESTAS
                        errorValidacion++;
                    }
                }
                if (errorValidacion > 0) {
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_SEGURIDAD_LIMITES, "", "", "Intento Fallido de Validacion de Preguntas de Seguridad", "", "", "", "", "", "", "");
                    retorno = validarIntentosFallidosPregSeg();
                    if (sesionController.isUsuarioBloqueado()) {
                        sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_SEGURIDAD_LIMITES, "", "", "Bloqueo de Usuario por Intentos Fallidos de Preguntas de Seguridad", "", "", "", "", "", "", "");
                        sesionController.mostrarDialogo(textosController.getNombreTexto("pnw.login.texto.bloqueo", sesionController.getNombreCanal()));
                        sesionController.setTextoAlerta(textosController.getNombreTexto("pnw.login.texto.bloqueoPopUp", sesionController.getNombreCanal()));
                        sesionController.setSesionInvalidada(true);
                        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.global.url.inicio", sesionController.getIdCanal()) + "?faces-redirect=true");
                        return;
                    } else {
                        if (retorno.equalsIgnoreCase(CODIGO_EXCEPCION_GENERICA)) {
                            //SI SE GENERO UN ERROR EN LAS CONSULTAS SE ENVIA EL MSJ DE ERROR DEFINIDO EN EL METODO DE VALIDACION
                            respuestasDD = null;
                            respuestasDA = null;
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
                            return;
                        } else {
                            //CASO CONTRARIO SE INDICA QUE SE REALIZO UN INTENTO FALLIDO DE VALIDACION EN LAS PREGUNTAS DE SEGURIDAD
                            respuestasDD = null;
                            respuestasDA = null;
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, textosController.getNombreTexto("pnw.global.texto.respuestasInv", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.respuestasInv", sesionController.getNombreCanal())));
                            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
                            return;
                        }
                    }
                }
            }
        }
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));

    }

    /**
     * metodo que se encargar de redireccionar al paso 3 de la afiliacion de P2P
     */
    public void afiliarPaso4() {
        int cantErrores = 0;

        //validacion de NroTelefono vacio
        if (this.usuarioP2P.getNroTelefono() == null || this.usuarioP2P.getNroTelefono().trim().equalsIgnoreCase("")) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:txtNroTelf", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        } else {
            //validacion de NroTelefono invalido
            if (!this.usuarioP2P.getNroTelefono().matches("^\\d{11}$")) {
                cantErrores++;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:txtNroTelf", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.tlfInv", sesionController.getNombreCanal())));
            }
        }
        //validacion de NroDocumento vacia
        if (this.usuarioP2P.getNroDocumento() == null || this.usuarioP2P.getNroDocumento().equals("")) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:txtIdentificacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }
        //validacion de Email vacia
        if (this.usuarioP2P.getEmail() == null || this.usuarioP2P.getEmail().equals("")) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:txtEmail", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }
        /*else {
            if (!this.usuarioP2P.getEmail().matches("^\\S+@\\S+\\.\\S+$")) {
                cantErrores++;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:txtEmail", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.emailInvl", sesionController.getNombreCanal())));
            }
        }*/
        //validacion de cuenta debito vacia
        if (this.ctaSeleccionada.equalsIgnoreCase("-1")) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:selectCtaAfiliar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }
        //validacion de monto Max Diario vacio
        /*if (this.usuarioP2P.getMtoMaxDiario() == null || this.usuarioP2P.getMtoMaxDiario().trim().equalsIgnoreCase("")) {
         cantErrores++;
         FacesContext context = FacesContext.getCurrentInstance();
         context.getExternalContext().getFlash().setKeepMessages(true);
         FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:txtMtoMaxDia", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
         }*/

        //validacion de monto Max Transaccion vacio
        if (this.usuarioP2P.getMtoMaxTransaccion() == null || this.usuarioP2P.getMtoMaxTransaccion().trim().equalsIgnoreCase("")) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:txtMtoMaxAcum", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }
        //validacion de monto Max Diario superior a limite monto Max Diario
        /*if (this.usuarioP2P.getMtoMaxDiario() != null && (new BigDecimal(eliminarformatoSimpleMonto(this.usuarioP2P.getMtoMaxDiario())).compareTo(new BigDecimal(parametrosController.getValorParametro("pnw.limite.p2p.maxDiario", sesionController.getNombreCanal()))) == 1)) {
         cantErrores++;
         FacesContext context = FacesContext.getCurrentInstance();
         context.getExternalContext().getFlash().setKeepMessages(true);
         FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:txtMtoMaxDia", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoDiaSupLimiteDS", sesionController.getNombreCanal()) + parametrosController.getValorParametro("pnw.limite.p2p.maxDiario", sesionController.getNombreCanal())));
         }*/
        //validacion de monto Max Transaccion superior a limite monto Max Transaccion
        if (this.usuarioP2P.getMtoMaxTransaccion() != null && (new BigDecimal(eliminarformatoSimpleMonto(this.usuarioP2P.getMtoMaxTransaccion())).compareTo(new BigDecimal(parametrosController.getValorParametro("pnw.limite.p2p.maxTransaccion", sesionController.getNombreCanal()))) == 1)) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:txtMtoMaxAcum", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoTranSupLimiteDS", sesionController.getNombreCanal()) + parametrosController.getValorParametro("pnw.limite.p2p.maxTransaccion", sesionController.getNombreCanal())));
        }
        //validacion de monto Max Transaccion superior a monto Max Diario
        /*if (this.usuarioP2P.getMtoMaxTransaccion() != null && this.usuarioP2P.getMtoMaxDiario() != null && (new BigDecimal(eliminarformatoSimpleMonto(this.usuarioP2P.getMtoMaxDiario())).compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.usuarioP2P.getMtoMaxTransaccion()))) == -1)) {
         cantErrores++;
         FacesContext context = FacesContext.getCurrentInstance();
         context.getExternalContext().getFlash().setKeepMessages(true);
         FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:txtMtoMaxAcum", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoTranSupMtoDia", sesionController.getNombreCanal())));
         }*/
        if (cantErrores > 0) {
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso3", sesionController.getIdCanal()));
        } else {
            this.usuarioP2P.setIdUsuario(sesionController.getUsuario().getId());
            this.usuarioP2P.setNroCuenta(datosCuentaSeleccionada.getNumeroCuenta());
            sesionController.setValidadorFlujo(4);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso4", sesionController.getIdCanal()));
        }
    }

    /**
     * metodo que se encarga de redireccionar al paso 4 de la afiliacion de P2P
     */
    public void afiliarPaso5() {
        //se valida previamente si el telf o la cuenta estan afiliados
        UtilDTO util = ibUsuariosP2PDAO.validarAfiliacionP2P(usuarioP2P.getNroTelefono(), usuarioP2P.getNroCuenta(), sesionController.getUsuario().getId().toString(), sesionController.getNombreCanal());
        if (util != null && util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                && util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getResuladosDTO() != null && ((boolean) util.getResuladosDTO().get(1))) {
            //de no ser asi se registra la afiliacion
            RespuestaDTO respuesta = ibUsuariosP2PDAO.insertarUsuarioP2P(usuarioP2P, sesionController.getIdCanal(), sesionController.getNombreCanal());
            if (respuesta == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                if (respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_SIN_RESULTADOS_JPA)) {
                    FacesContext.getCurrentInstance().addMessage("formAfiliarServ3:afiliacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("error.p2p.telfDuplicado", sesionController.getNombreCanal())));
                } else {
                    FacesContext.getCurrentInstance().addMessage("formAfiliarServ3:afiliacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                }
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso4", sesionController.getIdCanal()));
            } else {
                if (respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliarServ3:afiliacion", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getDescripcionSP()));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso4", sesionController.getIdCanal()));
                } else {
                    //si todo salio bien se procede a registrar la bitacora y notificar al cliente
                    sesionController.setValidadorFlujo(5);
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), sesionController.getIdTransaccion(), "", "", "Afiliación de Servicio P2P",
                            "", "", "", "", "", "", "");
                    String textoSMS = textosController.getNombreTexto("pnw.sms.cuerpo.registroP2P", sesionController.getNombreCanal());
                    String motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.registroP2P", sesionController.getNombreCanal());
                    HashMap<String, String> parametros = new HashMap();
                    parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                    parametros.put("\\$PTELF", this.usuarioP2P.getNroTelefono());
                    parametros.put("\\$PCTAORIG", this.numeroCuentaFormato(this.usuarioP2P.getNroCuenta()));
                    sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);
                    this.notificarEmailRegistroP2P(sesionController.getUsuario(), AFILIACION);
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso5", sesionController.getIdCanal()));
                }
            }
        } else {
            if (util != null && util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                    && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ3:afiliacion", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getDescripcionSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso4", sesionController.getIdCanal()));
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ3:afiliacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso4", sesionController.getIdCanal()));
            }
        }
    }

    /**
     * metodo para redirigir a paso 2 de afiliar P2P
     */
    public void regresarEditarAfiPaso2() {
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.editAfiP2P.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo que se encargar de redireccionar al paso 3 de la edicion de la
     * afiliacion de P2P
     */
    public void editarAfiPaso3() {
        int cantErrores = 0;
        if (this.afiliacionSeleccionada == null || this.afiliacionSeleccionada.equalsIgnoreCase("") || this.afiliacionSeleccionada.equalsIgnoreCase("-1")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:selectAfiEditar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.editAfiP2P.url.paso2", sesionController.getIdCanal()));
            return;
        }

        //validacion de NroTelefono vacio
        if (this.usuarioP2P.getNroTelefono() == null || this.usuarioP2P.getNroTelefono().trim().equalsIgnoreCase("")) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:txtNroTelf", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        } else {
            //validacion de NroTelefono invalido
            if (!this.usuarioP2P.getNroTelefono().matches("^\\d{11}$")) {
                cantErrores++;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:txtNroTelf", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.tlfInv", sesionController.getNombreCanal())));
            }
        }
        //validacion de NroDocumento vacia
        if (this.usuarioP2P.getNroDocumento() == null || this.usuarioP2P.getNroDocumento().equals("")) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:txtIdentificacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }
        //validacion de Email vacia
        if (this.usuarioP2P.getEmail() == null || this.usuarioP2P.getEmail().equals("")) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:txtEmail", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        } else {
            if (!this.usuarioP2P.getEmail().matches("^\\S+@\\S+\\.\\S+$")) {
                cantErrores++;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:txtEmail", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.emailInvl", sesionController.getNombreCanal())));
            }
        }
        //validacion de cuenta debito vacia
        if (this.ctaSeleccionada.equalsIgnoreCase("-1")) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:selectCtaAfiliar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }
        //validacion de monto Max Diario vacio
        /*if (this.usuarioP2P.getMtoMaxDiario() == null || this.usuarioP2P.getMtoMaxDiario().trim().equalsIgnoreCase("")) {
         cantErrores++;
         FacesContext context = FacesContext.getCurrentInstance();
         context.getExternalContext().getFlash().setKeepMessages(true);
         FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:txtMtoMaxDia", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
         }*/

        //validacion de monto Max Transaccion vacio
        if (this.usuarioP2P.getMtoMaxTransaccion() == null || this.usuarioP2P.getMtoMaxTransaccion().trim().equalsIgnoreCase("")) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:txtMtoMaxAcum", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
        }
        //validacion de monto Max Diario superior a limite monto Max Diario
        /*if (this.usuarioP2P.getMtoMaxDiario() != null && (new BigDecimal(eliminarformatoSimpleMonto(this.usuarioP2P.getMtoMaxDiario())).compareTo(new BigDecimal(parametrosController.getValorParametro("pnw.limite.p2p.maxDiario", sesionController.getNombreCanal()).getIbParametro().getValor())) == 1)) {
         cantErrores++;
         FacesContext context = FacesContext.getCurrentInstance();
         context.getExternalContext().getFlash().setKeepMessages(true);
         FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:txtMtoMaxDia", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoDiaSupLimiteDS", sesionController.getNombreCanal()) + parametrosController.getValorParametro("pnw.limite.p2p.maxDiario", sesionController.getNombreCanal()).getIbParametro().getValor()));
         }*/
        //validacion de monto Max Transaccion superior a limite monto Max Transaccion
        if (this.usuarioP2P.getMtoMaxTransaccion() != null && (new BigDecimal(eliminarformatoSimpleMonto(this.usuarioP2P.getMtoMaxTransaccion())).compareTo(new BigDecimal(parametrosController.getValorParametro("pnw.limite.p2p.maxTransaccion", sesionController.getNombreCanal()))) == 1)) {
            cantErrores++;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:txtMtoMaxAcum", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoTranSupLimiteDS", sesionController.getNombreCanal()) + parametrosController.getValorParametro("pnw.limite.p2p.maxTransaccion", sesionController.getNombreCanal())));
        }
        //validacion de monto Max Transaccion superior a monto Max Diario
        /*if (this.usuarioP2P.getMtoMaxTransaccion() != null && this.usuarioP2P.getMtoMaxDiario() != null && (new BigDecimal(eliminarformatoSimpleMonto(this.usuarioP2P.getMtoMaxDiario())).compareTo(new BigDecimal(eliminarformatoSimpleMonto(this.usuarioP2P.getMtoMaxTransaccion()))) == -1)) {
         cantErrores++;
         FacesContext context = FacesContext.getCurrentInstance();
         context.getExternalContext().getFlash().setKeepMessages(true);
         FacesContext.getCurrentInstance().addMessage("formEditAfilServ2:txtMtoMaxAcum", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.mtoTranSupMtoDia", sesionController.getNombreCanal())));
         }*/
        if (cantErrores > 0) {
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.editAfiP2P.url.paso2", sesionController.getIdCanal()));
        } else {
            this.usuarioP2P.setIdUsuario(sesionController.getUsuario().getId());
            this.usuarioP2P.setNroCuenta(datosCuentaSeleccionada.getNumeroCuenta());
            sesionController.setValidadorFlujo(3);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.editAfiP2P.url.paso3", sesionController.getIdCanal()));
        }
    }

    /**
     * metodo que se encarga de redireccionar al paso 4 de la afiliacion de P2P
     */
    public void editarAfiPaso4() {
        //se valida previamente si el telf o la cuenta estan afiliados
        UtilDTO util = ibUsuariosP2PDAO.validarEdicionAfiliacionP2P(usuarioP2P.getId().toString(), usuarioP2P.getNroTelefono(), usuarioP2P.getNroCuenta(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (util != null && util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                && util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && util.getResuladosDTO() != null && ((boolean) util.getResuladosDTO().get(1))) {
            //de no ser asi se registra la afiliacion
            RespuestaDTO respuesta = ibUsuariosP2PDAO.editarUsuarioP2P(usuarioP2P, sesionController.getIdCanal(), sesionController.getNombreCanal());
            if (respuesta == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formEditAfilServ3:afiliacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.editAfiP2P.url.paso3", sesionController.getIdCanal()));
            } else {
                if (respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formEditAfilServ3:afiliacion", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getDescripcionSP()));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.editAfiP2P.url.paso3", sesionController.getIdCanal()));
                } else {
                    //si todo salio bien se procede a registrar la bitacora y notificar al cliente

                    //PENDIENTE SMS Y CORREO DE EDICION DE AFILIACION
                    sesionController.setValidadorFlujo(4);
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), sesionController.getIdTransaccion(), "", "", "Edición de Afiliación de Servicio P2P",
                            "", "", "", "", "", "", "");
                    String textoSMS = textosController.getNombreTexto("pnw.sms.cuerpo.registroP2P", sesionController.getNombreCanal());
                    String motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.registroP2P", sesionController.getNombreCanal());
                    HashMap<String, String> parametros = new HashMap();
                    parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                    parametros.put("\\$PTELF", this.usuarioP2P.getNroTelefono());
                    parametros.put("\\$PCTAORIG", this.numeroCuentaFormato(this.usuarioP2P.getNroCuenta()));
                    sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);
                    this.notificarEmailRegistroP2P(sesionController.getUsuario(), EDICION);
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.editAfiP2P.url.paso4", sesionController.getIdCanal()));
                }
            }
        } else {
            if (util != null && util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                    && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formEditAfilServ3:afiliacion", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getDescripcionSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.editAfiP2P.url.paso3", sesionController.getIdCanal()));
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formEditAfilServ3:afiliacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.editAfiP2P.url.paso3", sesionController.getIdCanal()));
            }
        }
    }

    /**
     * metodo para redirigir a paso 2 de desafiliar P2P
     */
    public void regresarDesafiliarPaso2() {
        this.mantenerDatosForm = true;
        sesionController.setValidadorFlujo(2);
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliarP2P.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo que se encargar de redireccionar al paso 3 de la desafiliacion de
     * P2P
     */
    public void desafiliarPaso3() {
        int cantErrores = 0;
        if (this.afiliacionSeleccionada == null || this.afiliacionSeleccionada.equalsIgnoreCase("") || this.afiliacionSeleccionada.equalsIgnoreCase("-1")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formDesafiliarServ2:selectAfiEditar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliarP2P.url.paso2", sesionController.getIdCanal()));
            return;
        }
        if (cantErrores > 0) {
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliarP2P.url.paso2", sesionController.getIdCanal()));
        } else {
            sesionController.setValidadorFlujo(3);
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliarP2P.url.paso3", sesionController.getIdCanal()));
        }
    }

    /**
     * metodo que se encarga de redireccionar al paso 4 de la desafiliacion de
     * P2P
     */
    public void desafiliarPaso4() {
        RespuestaDTO respuesta = ibUsuariosP2PDAO.desafiliarUsuarioP2P(this.usuarioP2P.getId().toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (respuesta == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formDesafiliarServ3:desafiliacion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliarP2P.url.paso3", sesionController.getIdCanal()));
        } else {
            if (respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formDesafiliarServ3:desafiliacion", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getDescripcionSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliarP2P.url.paso3", sesionController.getIdCanal()));
            } else {
                //si todo salio bien se procede a registrar la bitacora y notificar al cliente

                //PENDIENTE SMS Y CORREO DE EDICION DE DESAFILIACION
                sesionController.setValidadorFlujo(4);
                sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), sesionController.getIdTransaccion(), "", "", "Desafiliación de Servicio P2P",
                        "", "", "", "", "", "", "");
                String textoSMS = textosController.getNombreTexto("pnw.sms.cuerpo.desafiliarP2P", sesionController.getNombreCanal());
                String motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.desafiliarP2P", sesionController.getNombreCanal());
                HashMap<String, String> parametros = new HashMap();
                parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
                parametros.put("\\$PTELF", this.usuarioP2P.getNroTelefono());
                parametros.put("\\$PCTAORIG", this.numeroCuentaFormato(this.usuarioP2P.getNroCuenta()));
                sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);
                this.notificarEmailRegistroP2P(sesionController.getUsuario(), DESAFILIACION);
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.desafiliarP2P.url.paso4", sesionController.getIdCanal()));
            }
        }
    }

    /**
     * metodo utilitario que se encarga de ejecutar la validacion previa del
     * registro del usuario al servicio P2P
     */
    public void validarEstatusRegistro() {
        IbUsuariosP2PDTO userTemp = this.ibUsuariosP2PDAO.consultarUsuarioP2P(sesionController.getUsuario().getId().toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        if (userTemp.getRespuestaDTO() != null && userTemp.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)
                && userTemp.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && userTemp.getUsuariosP2p() != null
                && !userTemp.getUsuariosP2p().isEmpty() && userTemp.getUsuariosP2p().get(0).getIdUsuario().compareTo(sesionController.getUsuario().getId()) == 0) {
            this.usuarioAfiliado = true;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divMsjSinAfiliaciones", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.texto.usuarioYaAfiliadoP2P", sesionController.getNombreCanal())));
        }
    }

    /**
     * metodo para notificar al usuario el registro de un numero al servicio P2P
     *
     * @param usuarioDTO el objeto con los datos del usuario
     */
    public void notificarEmailRegistroP2P(IbUsuariosDTO usuarioDTO, String procedencia) {

        //REDACTAR TEXTO
        String asunto = null;
        StringBuilder texto = new StringBuilder("");

        texto.append(parametrosController.getNombreParametro("pnw.global.texto.nombreBanco", sesionController.getIdCanal()));
        if (procedencia.equals(AFILIACION)) {
            asunto = "Notificacion de Registro al Servicio Pago D-2, DELSUR";
            texto.append(" le informa que, hemos registrado una afiliación al servicio Pago D-2 de su Número ");
        }
        if (procedencia.equals(DESAFILIACION)) {
            asunto = "Notificacion de Desafiliación al Servicio Pago D-2, DELSUR";
            texto.append(" le informa que, hemos registrado una desafiliación al servicio Pago D-2 de su Número ");
        }
        if (procedencia.equals(EDICION)) {
            asunto = "Notificacion de Modificación al Servicio Pago D-2, DELSUR";
            texto.append(" le informa que, hemos registrado una modificación al servicio Pago D-2 de su Número ");
        }

        texto.append(formatoAsteriscosWeb(this.usuarioP2P.getNroTelefono()));
        texto.append(" a su cuenta ");
        texto.append(formatoAsteriscosWeb(this.usuarioP2P.getNroCuenta()));
        texto.append(NUEVALINEA);
        texto.append(NUEVALINEA);
        texto.append("Gracias por su preferencia,");
        texto.append(NUEVALINEA);
        texto.append(NUEVALINEA);
        texto.append(parametrosController.getNombreParametro("pnw.global.texto.nombreBanco", sesionController.getIdCanal()));
        texto.append(NUEVALINEA);
        texto.append("Informacion Confidencial.");

        //notificacion al area de seguridad
        //EMailDTO emailDTO = notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("pnw.global.texto.emailBanco", sesionController.getIdCanal()), parametrosController.getNombreParametro("pnw.global.texto.emailBanco", sesionController.getIdCanal()), asunto, texto.toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        //si el usuario posee correo asociado se le envia el email notificando el bloqueo por intentos fallidos
        if (usuarioDTO != null && usuarioDTO.getEmail() != null && !usuarioDTO.getEmail().trim().equalsIgnoreCase("")) {
            EMailDTO emailDTO = notificacionesDAO.enviarEmail(parametrosController.getNombreParametro("pnw.global.texto.emailBanco", sesionController.getIdCanal()), usuarioDTO.getEmail(), asunto, texto.toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        }
    }

    //////////////SECCION  DE PREG DE SEGURIDAD//////////////////////////
    public String obtenerRespuestasDesafio() {
        StringBuilder rafagaDesafio = new StringBuilder();
        for (int i = 0; i < cantPreguntasDesafio; i++) {
            if (!rafagaDesafio.toString().equalsIgnoreCase("")) {
                rafagaDesafio.append("-");
            }
            rafagaDesafio.append(preguntasDD.get(i).getId()).append("-").append(this.encSHA256(respuestasDDEnc[i]));
        }
        return rafagaDesafio.toString();
    }

    public String obtenerRespuestasAutenticacion() {
        StringBuilder rafagaAutenticacion = new StringBuilder();
        for (int i = 0; i < cantPreguntasAutenticacion; i++) {
            if (!rafagaAutenticacion.toString().equalsIgnoreCase("")) {
                rafagaAutenticacion.append("-");
            }
            rafagaAutenticacion.append(preguntasDA.get(i).getCodigoPregunta()).append("-").append(respuestasDA[i]);
        }
        return rafagaAutenticacion.toString();
    }

    private boolean validarAlfaNumericoYEspacio(String[] value) {
        int valid = 0;
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        for (String s : value) {
            if (!s.matches("^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ ]*$")) {
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.AlfaNumEspacio", sesionController.getIdCanal()), textosController.getNombreTexto("pnw.global.texto.AlfaNumEspacio", sesionController.getIdCanal())));
                valid++;
            }
        }
        return valid > 0;
    }

    /*
     * Metodo para contar las cantidad de respuestas que no se encuentran vacias
     */
    private int countResp(String[] args) {
        int count = 0;
        for (String s : args) {
            if (s != null) {
                if (!s.isEmpty()) {
                    count++;
                }
            }

        }
        return count;
    }

    public void limpiarRespuestas() {
        respuestasDA = new String[cantPreguntasAutenticacion];
        respuestasDD = new String[cantPreguntasDesafio];
    }

    /**
     * metodo para encriptar texto en la capa web en 3des con una semilla de BD
     * para el ocultamiento de la data en la rafaga del request
     */
    public void encriptarRespuestasWeb() {
        if (this.respuestasDD != null && this.respuestasDD.length > 0) {
            respuestasDDEnc = new String[cantPreguntasDesafio];
            DESedeEncryption desEnc = new DESedeEncryption();
            desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal())));
            for (int i = 0; i < cantPreguntasDesafio; i++) {
                respuestasDDEnc[i] = desEnc.encriptar(this.respuestasDD[i]);
            }
        }
    }

    /**
     * metodo para desencriptar el texto en la capa web en 3des con una semilla
     * de BD
     */
    public void desencriptarRespuestasWeb() {
        DESedeEncryption desEnc = new DESedeEncryption();
        desEnc.setSemilla(System.getProperty(parametrosController.getNombreParametro("pnw.global.semilla.postilium", sesionController.getIdCanal())));
        for (int i = 0; i < cantPreguntasDesafio; i++) {
            respuestasDDEnc[i] = desEnc.desencriptar(this.respuestasDDEnc[i]);
        }
    }

    /**
     * metodo valida y generar las preguntas de Seguridad
     */
    public void generarPreguntasSeg() {
        String binCta = parametrosController.getNombreParametro("pnw.global.bin.nroCta", sesionController.getIdCanal());
        int longBin = binCta.length();

        if (this.numeroCuenta == null || this.numeroCuenta.isEmpty() || this.numeroCuenta.equals("")) {
            mostrarPreguntas = false;
            cantPreguntasDesafio = 0;
            cantPreguntasAutenticacion = 0;
            respuestasDD = null;
            preguntasDD = null;
            respuestasDA = null;
            preguntasDA = null;
            sesionController.setValidadorFlujo(2);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:txtNroCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.requerido", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
            return;
        } else if (!this.numeroCuenta.matches("^\\d{20}$") || !this.numeroCuenta.substring(0, longBin).equalsIgnoreCase(binCta)) {
            mostrarPreguntas = false;
            cantPreguntasDesafio = 0;
            cantPreguntasAutenticacion = 0;
            respuestasDD = null;
            preguntasDD = null;
            respuestasDA = null;
            preguntasDA = null;
            sesionController.setValidadorFlujo(2);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:txtNroCta", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.texto.ctaInvalida", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.texto.ctaInvalida", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
            return;
        }
        //Validación positiva
        this.mostrarPreguntas = false;
        cantPreguntasDesafio = Integer.parseInt(parametrosFacade.consultaParametro("pnw.global.validacionPositiva.cantPDD", sesionController.getIdCanal()).getIbParametro().getValor());
        respuestasDD = new String[cantPreguntasDesafio];
        preguntasDD = new ArrayList<>();
        IbPregDesafioUsuarioDTO preguntasDesafUsr = ibPreguntasDesafioDAO.listadoPreguntasDesafioUsuario(sesionController.getUsuario().getId().toString(), sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (!preguntasDesafUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) || preguntasDesafUsr.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_SIN_RESULTADOS_JPA)) {
            //MSJ ERROR CONSULTA DE PREGUNTAS DESAFIO
            mostrarPreguntas = false;
            cantPreguntasDesafio = 0;
            cantPreguntasAutenticacion = 0;
            respuestasDD = null;
            preguntasDD = null;
            respuestasDA = null;
            preguntasDA = null;
            sesionController.setValidadorFlujo(2);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
            return;
        } else {
            if (preguntasDesafUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !preguntasDesafUsr.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //MSJ ERROR CONTROLADO CONSULTA DE PREGUNTAS
                mostrarPreguntas = false;
                cantPreguntasDesafio = 0;
                cantPreguntasAutenticacion = 0;
                respuestasDD = null;
                preguntasDD = null;
                respuestasDA = null;
                preguntasDA = null;
                sesionController.setValidadorFlujo(2);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divTDD", new FacesMessage(FacesMessage.SEVERITY_WARN, preguntasDesafUsr.getRespuestaDTO().getTextoSP(), preguntasDesafUsr.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
                return;
            } else {
                if (preguntasDesafUsr.getPreguntasDesafioUsr() == null || preguntasDesafUsr.getPreguntasDesafioUsr().isEmpty() || preguntasDesafUsr.getPreguntasDesafioUsr().size() < cantPreguntasDesafio) {
                    //MSJ DE ERROR CANT DE PREGUNTAS 0 ó MENOR A LA CANT REQUERIDA
                    mostrarPreguntas = false;
                    cantPreguntasDesafio = 0;
                    cantPreguntasAutenticacion = 0;
                    respuestasDD = null;
                    preguntasDD = null;
                    respuestasDA = null;
                    preguntasDA = null;
                    sesionController.setValidadorFlujo(2);
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
                    return;
                } else {
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_AFILIARP2P, "", "", "Generacion de Preguntas Desafio Ora11", "", "", "", "", "", "", "");
                    ArrayList<IbPreguntasDesafioDTO> preguntasDesaTotales = new ArrayList<>(preguntasDesafUsr.getPreguntasDesafioUsr());
                    int posicionPregAleatoria = 0;
                    for (int i = 0; i < cantPreguntasDesafio; i++) {
                        posicionPregAleatoria = numeroAleatorio(preguntasDesaTotales.size());
                        preguntasDD.add(preguntasDesaTotales.get(posicionPregAleatoria));
                        preguntasDesaTotales.remove(posicionPregAleatoria);
                    }
                }
            }
        }

        cantPreguntasAutenticacion = Integer.parseInt(parametrosController.getValorParametro("pnw.global.validacionPositiva.cantPDA", sesionController.getIdCanal()));
        respuestasDA = new String[cantPreguntasAutenticacion];
        preguntasDA = new ArrayList<>();
        //SE COLOCAN 0 PREGUNTAS PARA QUE EL SERVICIO RETORNE TODO EL POOL DE PDA
        PreguntaAutenticacionDTO preguntasAutUsr = preguntasAutenticacionDAO.listPDAporCliente(sesionController.getUsuario().getTdd(), this.numeroCuenta, cantPreguntasAutenticacion, sesionController.getNombreCanal(), sesionController.getIdCanal());
        if (!preguntasAutUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) || preguntasDesafUsr.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_SIN_RESULTADOS_JPA)) {
            //MSJ ERROR CONSULTA DE PREGUNTAS DESAFIO
            mostrarPreguntas = false;
            cantPreguntasDesafio = 0;
            cantPreguntasAutenticacion = 0;
            respuestasDD = null;
            preguntasDD = null;
            respuestasDA = null;
            preguntasDA = null;
            sesionController.setValidadorFlujo(2);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
            return;
        } else {
            if (preguntasAutUsr.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !preguntasAutUsr.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //MSJ ERROR CONTROLADO CONSULTA DE PREGUNTAS
                mostrarPreguntas = false;
                cantPreguntasDesafio = 0;
                cantPreguntasAutenticacion = 0;
                respuestasDD = null;
                preguntasDD = null;
                respuestasDA = null;
                preguntasDA = null;
                sesionController.setValidadorFlujo(2);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divTDD", new FacesMessage(FacesMessage.SEVERITY_WARN, preguntasAutUsr.getRespuestaDTO().getTextoSP(), preguntasAutUsr.getRespuestaDTO().getTextoSP()));
                this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
                return;
            } else {
                if (preguntasAutUsr.getPreguntasAutenticacion() == null || preguntasAutUsr.getPreguntasAutenticacion().isEmpty() || preguntasAutUsr.getPreguntasAutenticacion().size() < cantPreguntasAutenticacion) {
                    //MSJ DE ERROR CANT DE PREGUNTAS 0 ó MENOR A LA CANT REQUERIDA
                    mostrarPreguntas = false;
                    cantPreguntasDesafio = 0;
                    cantPreguntasAutenticacion = 0;
                    respuestasDD = null;
                    preguntasDD = null;
                    respuestasDA = null;
                    preguntasDA = null;
                    sesionController.setValidadorFlujo(2);
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divTDD", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                    this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
                    return;
                } else {
                    sesionController.registroBitacora(sesionController.getUsuario().getId().toString(), BDSUtil.ID_TRANS_AFILIARP2P, "", "", "Generacion de Preguntas Autenticacion Ora9", "", "", "", "", "", "", "");
                    ArrayList<PreguntaAutenticacionDTO> preguntasAutTotales = new ArrayList<>(preguntasAutUsr.getPreguntasAutenticacion());
                    int posicionPregAleatoria = 0;
                    for (int i = 0; i < cantPreguntasAutenticacion; i++) {
                        posicionPregAleatoria = numeroAleatorio(preguntasAutTotales.size());
                        preguntasDA.add(preguntasAutTotales.get(posicionPregAleatoria));
                        preguntasAutTotales.remove(posicionPregAleatoria);
                    }
                }
            }
        }
        this.mostrarPreguntas = true;
        this.redirectFacesContext(parametrosController.getNombreParametro("pnw.afiliarP2P.url.paso2", sesionController.getIdCanal()));
    }

    /**
     * metodo que se encarga de valida la cantidad de intentos fallidos de un
     * usuario al modulo de preguntas de seguridad en CAMBIO DE CLAVE (PDA Y
     * PDD)
     *
     * @return indicador de error en consulta o si viene vacio el proceso se
     * concreto sin problemas
     */
    public String validarIntentosFallidosPregSeg() {
        int limitePregSegFallidasPermitidas = Integer.parseInt(parametrosController.getNombreParametro("pnw.global.validacionPositiva.cantIntentosFallidos", sesionController.getIdCanal()));
        int cantPregSegFallidas = 0;
        RespuestaDTO respuesta;
        UtilDTO util = usuarioDAO.cantidadPregSegFallidas(sesionController.getUsuario().getId().toString(), sesionController.getUsuario().getTdd(), sesionController.getNombreCanal(), sesionController.getIdCanal());

        if (util == null || util.getRespuestaDTO() == null || !util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
        } else if (util.getRespuestaDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !util.getRespuestaDTO().getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", util.getRespuestaDTO().getTextoSP()));
        }

        if (util != null && util.getRespuestaDTO() != null && util.getResuladosDTO() != null && util.getRespuestaDTO().getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
            cantPregSegFallidas = Integer.parseInt(util.getResuladosDTO().get("intentosFallidos").toString());
        } else {
            //ERROR EN DAO AL INTENTAR OBTENER LA CANTIDAD DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
            FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal()), textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            return CODIGO_EXCEPCION_GENERICA;
        }
        if (cantPregSegFallidas < limitePregSegFallidasPermitidas) {
            cantPregSegFallidas++;
            //SE INCREMENTA EL CONTADOR DE INTENTOS FALLIDOS DE PREGUNTAS DE SEGURIDAD
            respuesta = usuarioDAO.actualizarPregSegFallidas(sesionController.getUsuario().getId().toString(), cantPregSegFallidas, sesionController.getNombreCanal(), sesionController.getIdCanal());

            if (respuesta == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else if (respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getTextoSP()));
            }

        }
        /////////NOTIFICACION VIA SMS ULTIMO INTENTO RESTANTE////////
        if (cantPregSegFallidas == (limitePregSegFallidasPermitidas - 1)) {
            String textoSMS = textosController.getNombreTexto("pnw.global.sms.cuerpo.preBloqueo", sesionController.getNombreCanal());
            String motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.preBloqueoLogin", sesionController.getNombreCanal());
            HashMap<String, String> parametros = new HashMap();
            parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
            sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);
            ////////NOTIFICACIÓN VÍA EMAIL AL CORREO DE SEGURIDAD//////
            sesionController.notificarEmailUltimoIntento(sesionController.getUsuario());
        }
        //BLOQUEO DEL USUARIO POR LIMITE DE INTENTOS FALLIDOS SUPERADOS
        if (cantPregSegFallidas >= limitePregSegFallidasPermitidas) {
            respuesta = canalesDAO.bloquearAccesoCanal(sesionController.getUsuario().getId().toString(), sesionController.getIdCanal(), sesionController.getNombreCanal());

            if (respuesta == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR EN DAO AL INTENTAR VALIDAR LAS PREGUNTAS
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
                return CODIGO_EXCEPCION_GENERICA;
            } else if (respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getTextoSP()));
            }
            respuesta = canalesDAO.bloquearAccesoCanal(sesionController.getUsuario().getId().toString(), CODIGO_CANAL_MOBILE, sesionController.getNombreCanal());

            if (respuesta == null || respuesta.getCodigo() == null || !respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                //ERROR AL INTENTAR ACTUALIZAR EL CONTADOR DE INTENTOS FALLIDOS
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", textosController.getNombreTexto("pnw.global.error.generico", sesionController.getNombreCanal())));
            } else if (respuesta.getCodigoSP() != null && respuesta.getCodigo().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO) && !respuesta.getCodigoSP().equalsIgnoreCase(CODIGO_RESPUESTA_EXITOSO)) {
                FacesContext.getCurrentInstance().addMessage("formAfiliarServ2:divPreguntas", new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getTextoSP()));
                return CODIGO_EXCEPCION_GENERICA;
            }

            sesionController.setUsuarioBloqueado(true);
            /////////NOTIFICACION VIA SMS BLOQUEO DE USUARIO POR INTENTOS FALLIDOS////////
            String textoSMS = textosController.getNombreTexto("pnw.login.texto.bloqueo", sesionController.getNombreCanal());
            String motivoSMS = textosController.getNombreTexto("pnw.sms.motivo.bloqueoLogin", sesionController.getNombreCanal());
            HashMap<String, String> parametros = new HashMap();
            parametros.put("\\$PFEC", formatearFecha(new Date(), FORMATO_FECHA_COMPLETA));
            sesionController.enviarSMS(sesionController.getUsuario().getCelular(), textoSMS, parametros, motivoSMS);

            ////////NOTIFICACIÓN VÍA EMAIL AL CORREO DE SEGURIDAD//////
            sesionController.notificarEmail(sesionController.getUsuario());
            sesionController.notificarEmailBloqueo(sesionController.getUsuario());
            sesionController.setTextoAlerta(textosController.getNombreTexto("pnw.login.texto.bloqueoPopUp", sesionController.getNombreCanal()));
        }
        return "";
    }

    ////////////////////SECCION  DE REPORTES//////////////////////////
    /**
     * metodo encargado de generar el contenido del Pago P2P en PDF
     *
     * @param pdf hoja de trabajo de PDF
     * @throws com.lowagie.text.BadElementException
     */
    public void cuerpoPagoP2PPDF(Document pdf) throws BadElementException, DocumentException {
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
            PdfPCell cell = new PdfPCell((new Phrase(textosController.getNombreTexto("pnw.pagarP2P.descargaPdf.titulo", sesionController.getIdCanal()), fontTituloBold)));

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
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagarP2P.descargaPdf.telfOrig", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.getUsuarioP2P().getNroTelefono(), font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagarP2P.descargaPdf.telfDest", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.codTelfDestino + this.telfDestino, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagarP2P.descargaPdf.docBenef", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.tipoDocBeneficiario + this.nroDocBeneficiario, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagarP2P.descargaPdf.bancoDest", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.nombreBanco, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagarP2P.descargaPdf.monto", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.montoPago, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagarP2P.descargaPdf.descripcion", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.descripcionPago, font));

            table.getDefaultCell().setBackgroundColor(new Color(230, 230, 230));
            table.addCell(new Phrase(textosController.getNombreTexto("pnw.pagarP2P.descargaPdf.nroReferencia", sesionController.getIdCanal()), fontBold));
            table.getDefaultCell().setBackgroundColor(new Color(255, 255, 255));
            table.addCell(new Phrase(this.nroReferencia, font));

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
            e.printStackTrace();
        }
    }

    /**
     * metodo encargado de armar el reportes de pago P2P en PDF
     *
     * @throws java.io.IOException
     * @throws com.lowagie.text.DocumentException
     */
    public void detallePagoPDF() throws IOException, DocumentException {

        try {

            String nombreDocumento;

            nombreDocumento = "DetallePagoP2P.pdf";

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
            this.cuerpoPagoP2PPDF(document);

            //ceramos el documento
            document.close();

            response.setContentLength(baos.size());
            ServletOutputStream out = response.getOutputStream();
            baos.writeTo(out);
            baos.flush();
            faces.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doRegresar() {
        this.redirectFacesContext("/sec/noticias/noticias.xhtml");
    }

    public boolean isUsuarioHabilitadoP2P() {
        return usuarioHabilitadoP2P;
    }

    public void setUsuarioHabilitadoP2P(boolean usuarioHabilitadoP2P) {
        this.usuarioHabilitadoP2P = usuarioHabilitadoP2P;
    }

    /**
     * METODO UTILIZADO PARA VALIDAR UN USUARIO PILOTO
     */
    public void validarUsuarioPiloto() {
        if (sesionController.isValidarUsuarioPiloto()) {
            FacesContext.getCurrentInstance().
                    addMessage("formOTPController:SinPermisoP2P",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
                                    textosController.getNombreTexto("pnw.global.texto.sinPermisoP2P",
                                            sesionController.getIdCanal())));

            usuarioHabilitadoP2P = delSurDAO.validarUsuarioP2P(sesionController.getUsuario().getTipoDoc() + sesionController.getUsuario().getDocumento(), sesionController.getIdCanal(), sesionController.getNombreCanal());
        }
    }

    public List<IbBeneficiariosP2PDTO> asignarNombreBanco(List<IbBeneficiariosP2PDTO> beneficiarioP2P) {
        int i = 0;
        if (beneficiarioP2P != null) {
            for (IbBeneficiariosP2PDTO ibBeneficiariosP2PDTO : beneficiarioP2P) {
                for (BancoDTO banco : this.bancos) {
                    if (banco.getCodigoBanco().equals(BDSUtil.cerosIzquierda(String.valueOf(beneficiarioP2P.get(i).getCodigoBanco()), 4))) {
                        beneficiarioP2P.get(i).setNombreBanco(banco.getNombreBanco());
                        break;
                    }
                    if (BDSUtil.cerosIzquierda(String.valueOf(beneficiarioP2P.get(i).getCodigoBanco()), 4).equals(DIGITOS_INICIALES_BANCO_DEL_SUR)) {
                        beneficiarioP2P.get(i).setNombreBanco("DELSUR BANCO UNIVERSAL C.A.");
                        break;
                    }
                }
                i++;
            }
        }

        return beneficiarioP2P;
    }
    
    public List<IbBeneficiariosP2CDTO> asignarNombreBancoP2C(List<IbBeneficiariosP2CDTO> beneficiarioP2C) {
        int i = 0;
        if (beneficiarioP2C != null) {
            for (IbBeneficiariosP2CDTO ibBeneficiariosP2CDTO : beneficiarioP2C) {
                for (BancoDTO banco : this.bancos) {
                    if (banco.getCodigoBanco().equals(BDSUtil.cerosIzquierda(String.valueOf(beneficiarioP2C.get(i).getCodigoBanco()), 4))) {
                        beneficiarioP2C.get(i).setNombreBanco(banco.getNombreBanco());
                        break;
                    }
                    if (BDSUtil.cerosIzquierda(String.valueOf(beneficiarioP2C.get(i).getCodigoBanco()), 4).equals(DIGITOS_INICIALES_BANCO_DEL_SUR)) {
                        beneficiarioP2C.get(i).setNombreBanco("DELSUR BANCO UNIVERSAL C.A.");
                        break;
                    }
                }
                i++;
            }
        }

        return beneficiarioP2C;
    }

    public void reiniciarDatos() {
        this.codTelfDestino = "";
        this.telfDestino = "";
        this.tipoDocBeneficiario = "";
        this.nroDocBeneficiario = "";
        this.infoBanco = "";
    }

    public boolean isFrecuente() {
        return frecuente;
    }

    public void setFrecuente(boolean frecuente) {
        this.frecuente = frecuente;
    }

    public boolean isValidFrecuente() {
        return validFrecuente;
    }

    public void setValidFrecuente(boolean validFrecuente) {
        this.validFrecuente = validFrecuente;
    }

    public void validarFrecuente() {
        if (frecuente) {
            this.panelAlias = true;
            this.frecuente = true;
            this.mostrarAlias = "hidden";
        } else {
            this.panelAlias = false;
            this.frecuente = false;
            this.mostrarAlias = "none";
        }
    }

    public void validarFrecuenteP2C() {
        if (frecuenteP2C) {
            this.frecuenteP2C = true;
            this.mostrarAliasP2C = "hidden";
        } else {
            this.frecuenteP2C = false;
            this.mostrarAliasP2C = "none";
        }
    }

    public String getAliasSeleccionadoP2C() {
        return aliasSeleccionadoP2C;
    }

    public void setAliasSeleccionadoP2C(String aliasSeleccionadoP2C) {
        this.aliasSeleccionadoP2C = aliasSeleccionadoP2C;
    }

    public String getMostrarAlias() {
        return mostrarAlias;
    }

    public void setMostrarAlias(String mostrarAlias) {
        this.mostrarAlias = mostrarAlias;
    }

    public String getAlias2() {
        return alias2;
    }

    public void setAlias2(String alias2) {
        this.alias2 = alias2;
    }

    public String getMostrarAlias2() {
        return mostrarAlias2;
    }

    public void setMostrarAlias2(String mostrarAlias2) {
        this.mostrarAlias2 = mostrarAlias2;
    }

    public List<IbBeneficiariosP2PDTO> getBeneficiariosP2P() {
        return beneficiariosP2P;
    }

    public void setBeneficiariosP2P(List<IbBeneficiariosP2PDTO> beneficiariosP2P) {
        this.beneficiariosP2P = beneficiariosP2P;
    }

    public String getAliasSeleccionado() {
        return aliasSeleccionado;
    }

    public void setAliasSeleccionado(String aliasSeleccionado) {
        this.aliasSeleccionado = aliasSeleccionado;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getMostrarFrecuente() {
        return mostrarFrecuente;
    }

    public void setMostrarFrecuente(String mostrarFrecuente) {
        this.mostrarFrecuente = mostrarFrecuente;
    }

    public boolean isValidaCierreOperaciones() {

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date horaActualSistema = new Date();
        Calendar calendarHoraInicio = Calendar.getInstance();
        Calendar calendarHoraCierre = Calendar.getInstance();
        Date horaInicioCierre, horaFinCierre;

        try {
            horaInicioCierre = dateFormat.parse(sesionController.getHoraInicio());
            horaFinCierre = dateFormat.parse(sesionController.getHoraFin());

            calendarHoraInicio.setTime(horaInicioCierre);
            calendarHoraCierre.setTime(horaFinCierre);

            if (calendarHoraInicio.DAY_OF_YEAR != calendarHoraCierre.DAY_OF_YEAR) {
                calendarHoraCierre.add(calendarHoraCierre.DAY_OF_YEAR, 1);
            }

            String horaActualFormateada = dateFormat.format(horaActualSistema);

            if ((calendarHoraInicio.getTime().compareTo(dateFormat.parse(horaActualFormateada)) <= 0)
                    && (calendarHoraCierre.getTime().compareTo(dateFormat.parse(horaActualFormateada)) >= 0)) {
                validaCierreOperaciones = false;
            } else {
                validaCierreOperaciones = true;
            }
        } catch (ParseException parseException) {

        }
        return validaCierreOperaciones;
    }

    public boolean isMostrarMensajeCierre() {
        if (validaCierreOperaciones == false) {
            mostrarMensajeCierre = true;
        }
        return mostrarMensajeCierre;
    }

    public List<TipoPagoDTO> getTipoPago() {

        listaTipoPago = new ArrayList<>();
        TipoPagoDTO tipoPagoDTO = new TipoPagoDTO();
        tipoPagoDTO.setCodigo(TipoPagoEnum.P2P.getId());
        tipoPagoDTO.setDescripcion(TipoPagoEnum.P2P.getDescripcion());
        listaTipoPago.add(tipoPagoDTO);
        tipoPagoDTO = new TipoPagoDTO();
        tipoPagoDTO.setCodigo(TipoPagoEnum.P2C.getId());
        tipoPagoDTO.setDescripcion(TipoPagoEnum.P2C.getDescripcion());
        listaTipoPago.add(tipoPagoDTO);
        return listaTipoPago;
    }

    public void actualizaPanel() {
        this.setTipoDocBeneficiario("V");
        this.setNroDocBeneficiario("");
        this.setTelfDestino("");
        this.setCodTelfDestino("");
        this.setFrecuente(false);
        this.setFrecuenteP2C(false);
        this.setInfoBanco("-1");
        HtmlSelectOneMenu tipoAcceso = (HtmlSelectOneMenu) findComponentInRoot("selectAlias");
        tipoAcceso.setValue("-1");
        HtmlSelectOneMenu tipoAccesoP2C = (HtmlSelectOneMenu) findComponentInRoot("selectAliasP2C");
        tipoAccesoP2C.setValue("-1");
        this.mostrarAliasP2C = "none";
        this.mostrarAlias = "none";
        if (tipoPagoSelect == TipoPagoEnum.P2P.getId()) {
            showPanel = true;
            showPanelP2C = false;
        } else if (tipoPagoSelect == TipoPagoEnum.P2C.getId()) {
            showPanel = false;
            showPanelP2C = true;
        }
    }

    public List<TipoPagoDTO> getListaTipoPago() {
        return listaTipoPago;
    }

    public void setListaTipoPago(List<TipoPagoDTO> listaTipoPago) {
        this.listaTipoPago = listaTipoPago;
    }

    public int getTipoPagoSelect() {
        return tipoPagoSelect;
    }

    public void setTipoPagoSelect(int tipoPagoSelect) {
        this.tipoPagoSelect = tipoPagoSelect;
    }

    public boolean isShowPanel() {
        return showPanel;
    }

    public void setShowPanel(boolean showPanel) {
        this.showPanel = showPanel;
    }

    public boolean isShowPanelP2C() {
        return showPanelP2C;
    }

    public void setShowPanelP2C(boolean showPanelP2C) {
        this.showPanelP2C = showPanelP2C;
    }

    public List<IbBeneficiariosP2CDTO> getBeneficiariosP2C() {
        return beneficiariosP2C;
    }

    public void setBeneficiariosP2C(List<IbBeneficiariosP2CDTO> beneficiariosP2C) {
        this.beneficiariosP2C = beneficiariosP2C;
    }

    public boolean isFrecuenteP2C() {
        return frecuenteP2C;
    }

    public void setFrecuenteP2C(boolean frecuenteP2C) {
        this.frecuenteP2C = frecuenteP2C;
    }

    public String getMostrarAliasP2C() {
        return mostrarAliasP2C;
    }

    public void setMostrarAliasP2C(String mostrarAliasP2C) {
        this.mostrarAliasP2C = mostrarAliasP2C;
    }

    public String getMostrarAliasP2C2() {
        return mostrarAliasP2C2;
    }

    public void setMostrarAliasP2C2(String mostrarAliasP2C2) {
        this.mostrarAliasP2C2 = mostrarAliasP2C2;
    }

    public String getMostrarFrecuenteP2C() {
        return mostrarFrecuenteP2C;
    }

    public void setMostrarFrecuenteP2C(String mostrarFrecuenteP2C) {
        this.mostrarFrecuenteP2C = mostrarFrecuenteP2C;
    }

    public String getAliasP2C() {
        return aliasP2C;
    }

    public void setAliasP2C(String aliasP2C) {
        this.aliasP2C = aliasP2C;
    }

    public String getAliasP2C2() {
        return aliasP2C2;
    }

    public void setAliasP2C2(String aliasP2C2) {
        this.aliasP2C2 = aliasP2C2;
    }

    public static String getAliasAuxP2C() {
        return aliasAuxP2C;
    }

    public static void setAliasAuxP2C(String aliasAuxP2C) {
        GestionP2PController.aliasAuxP2C = aliasAuxP2C;
    }

    public BigDecimal getSaldoDisponibleP2C() {
        return saldoDisponibleP2C;
    }

    public void setSaldoDisponibleP2C(BigDecimal saldoDisponibleP2C) {
        this.saldoDisponibleP2C = saldoDisponibleP2C;
    }

    public boolean isPanelAlias() {
        return panelAlias;
    }

    public void setPanelAlias(boolean panelAlias) {
        this.panelAlias = panelAlias;
    }
}
