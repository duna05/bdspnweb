package com.bds.wpn.filter;

import com.bds.wpn.controller.ErroresController;
import com.bds.wpn.controller.InicioSesionController;
import com.bds.wpn.controller.ParametrosController;
import com.bds.wpn.controller.TextosController;
import com.bds.wpn.dao.IbCanalesDAO;
import com.bds.wpn.dto.IbUsuariosCanalesDTO;
import com.bds.wpn.dto.RespuestaDTO;
import com.bds.wpn.facade.IbParametrosFacade;
import com.bds.wpn.facade.IbTextosFacade;
import com.bds.wpn.util.BDSUtil;
import com.bds.wpn.util.DAOUtil;
import java.io.IOException;
import java.util.Date;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.log4j.Logger;

/**
 * Filtro que se encarga de proteger la parter interna del portal
 *
 * @author Usuario
 */
//url patterns anterior /sec/* para que ignorara el login en el filtro "/sec/*","/ext/*"
@WebFilter(filterName = "SessionFilter", urlPatterns = {"*.xhtml"})
public class SessionFilter extends BDSUtil implements Filter {

    private static final Logger logger = Logger.getLogger(SessionFilter.class.getName());

    @Inject
    private InicioSesionController inicioSesion;

    @Inject
    private ErroresController erroresController;

    @EJB
    private IbCanalesDAO canalesDAO;

    @EJB
    IbParametrosFacade parametrosFacade;

    @EJB
    IbTextosFacade textosFacade;
    
    @Inject
    TextosController textosController;
    
    @Inject
    ParametrosController parametrosController;

    private static final boolean debug = true;

    public static final String ID_SESSION = "idsessionWpn";

    public SessionFilter() {
    }

    /**
     * Filtro de solicitudes la aplicacion
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        DAOUtil dAOUtil = new DAOUtil();
        try {

            if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
                HttpServletRequest req = (HttpServletRequest) request;
                HttpServletResponse res = (HttpServletResponse) response;
                IbUsuariosCanalesDTO consultarUsuarioCanalporIdUsuario = null;
                String contexto = "";
                boolean sesionExpiradaEnBD = false;

                //fragmentos para determinar la url
                String uri = req.getRequestURI();
                String getProtocol = req.getScheme();
                String getDomain = req.getServerName();
                String getPort = Integer.toString(req.getServerPort());
                String ambiente = parametrosController.getNombreParametro("pnw.global.ambiente.wpn", inicioSesion.getIdCanal());

                //se agregan los headers de seguridad 
                res.setHeader("X-Content-Type-Options", "nosniff");
                res.setHeader("X-Frame-Options", "SAMEORIGIN");
                res.setHeader("X-XSS-Protection", "1; mode=block");
                res.setHeader("Cache-Control", "no-cache");
                res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
                res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
                res.setDateHeader("Expires", 0); // Proxies.
                res.setCharacterEncoding("UTF-8");

                // si encuentra un enlace con JSESSIONID lo limpia
                if (req.isRequestedSessionIdFromURL()) {
                    String url = req.getRequestURL().append(req.getQueryString() != null ? "?"
                            + req.getQueryString() : "").toString();
                    res.setHeader("Location", url);
                    res.sendError(HttpServletResponse.SC_MOVED_PERMANENTLY);
                    return;
                }

                // clase interna para prevenir que se pinten los JSESSIONID rn URLs para cualquier enlace en la app
                HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(res) {
                    @Override
                    public String encodeRedirectUrl(String url) {
                        return url;
                    }

                    @Override
                    public String encodeRedirectURL(String url) {
                        return url;
                    }

                    @Override
                    public String encodeUrl(String url) {
                        return url;
                    }

                    @Override
                    public String encodeURL(String url) {
                        return url;
                    }
                };

               /* if(isAJAXRequest(req)){
                    if(inicioSesion.isSesionInvalidada()){//oo
                        inicioSesion.setCerrarSesion(true);
                        // res.sendRedirect(uri);
                         //res.sendRedirect(uri+parametrosController.getNombreParametro("pnw.global.url.inicio", inicioSesion.getIdCanal())+ "?faces-redirect=true");
                         this.redirectFacesContext("/bdspnweb"+parametrosController.getNombreParametro("pnw.global.url.inicio", inicioSesion.getIdCanal())+ "?faces-redirect=true");
                    }
                }else{
                     if(inicioSesion.isSesionInvalidada()){
                        inicioSesion.setCerrarSesion(true);
                         //res.sendRedirect(uri);
                        this.redirectFacesContext("/bdspnweb"+parametrosController.getNombreParametro("pnw.global.url.inicio", inicioSesion.getIdCanal())+ "?faces-redirect=true");
                        
                     } 
                } 
                */
                if (uri.contains(".css.") || uri.contains(".js.") || uri.contains(".gif.") || uri.contains(".jpg.") || uri.contains(".png.")) {
                    if (uri.contains(".css.")) {
                        res.setHeader("X-Content-Type-Options", "nosniff");
                        res.setHeader("X-Frame-Options", "SAMEORIGIN");
                        res.setContentType("text/css");
                    }
                    chain.doFilter(request, wrappedResponse);
                    return;
                }
                res.setContentType("text/html");

                //validaciones de puerto seguro
                if (ambiente.equalsIgnoreCase("PROD")) {

                    if (getProtocol.toLowerCase().equals("http")) {

                        // Set response content type
                        response.setContentType("text/html");

                        // New location to be redirected
                        String httpsPath = "https" + "://" + getDomain + ":" + getPort
                                + uri;

                        String site = new String(httpsPath);
                        res.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                        res.setHeader("Location", site);
                    }
                    chain.doFilter(request, wrappedResponse);
                    return;
                }
                //validaciones de session ignorando la session para la pagina de loggin

                if (!uri.contains("/ext/")) {
                    if (req.getSession(false) != null) {
                        if (inicioSesion.getUsuario() != null) {//de ser true la session de jsf esta muerta
                            consultarUsuarioCanalporIdUsuario = canalesDAO.consultarUsuarioCanalporIdUsuario(inicioSesion.getUsuario(), inicioSesion.getIdCanal());
                            if (consultarUsuarioCanalporIdUsuario.getRespuestaDTO().getCodigo().equals(CODIGO_RESPUESTA_EXITOSO)) {
                                if (consultarUsuarioCanalporIdUsuario.getInicioSesion().toString().equals("0")) {
                                    //req.getSession(false).invalidate();
                                    sesionExpiradaEnBD = true;
                                }
                                if (!req.getHeader("referer").contains("/ext/") && validarFechaExpiradaBD(parametrosFacade.obtenerFechaBD(inicioSesion.getNombreCanal()),consultarUsuarioCanalporIdUsuario.getFechaHoraUltimaInteraccionDate(), parametrosController.getValorParametro(BDSUtil.CODIGO_TIME_OUT, inicioSesion.getNombreCanal()))) {
                                    erroresController.redireccionTimeOut(wrappedResponse, req);
                                    return;
                                }                                //validamos si la sesion se cerro por sesion simultanea o timeout 
                                if (consultarUsuarioCanalporIdUsuario.getInicioSesion().equals(CODIGO_SESION_SIMULTANEA) || !consultarUsuarioCanalporIdUsuario.getIdSesion().equals(inicioSesion.getIdSesion())) {

                                    if (!consultarUsuarioCanalporIdUsuario.getIdSesion().equals(inicioSesion.getIdSesion())) {
                                        RespuestaDTO respAct = canalesDAO.actualizarUsuarioCanal(inicioSesion.getUsuario(), inicioSesion.getIdCanal(), inicioSesion.getNombreCanal(), String.valueOf(CODIGO_SESION_SIMULTANEA), 0, "0", null, null,
                                                consultarUsuarioCanalporIdUsuario.getLimiteInternas(), consultarUsuarioCanalporIdUsuario.getLimiteExternas(), consultarUsuarioCanalporIdUsuario.getLimiteInternasTerceros(), consultarUsuarioCanalporIdUsuario.getLimiteExternasTerceros());

                                    }

                                    String redirectPage = req.getContextPath() + parametrosController.getNombreParametro("pnw.global.url.error", BDSUtil.CODIGO_CANAL_MOBILE);
                                    if (isAJAXRequest(req)) {
                                        erroresController.setTipoError(textosController.getNombreTexto("pnw.errores.tipo.multiSesion", BDSUtil.CODIGO_CANAL_MOBILE));
                                        erroresController.setTituloError(textosController.getNombreTexto("pnw.errores.titulo.multiSesion", BDSUtil.CODIGO_CANAL_MOBILE));
                                        erroresController.setMsjError(textosController.getNombreTexto("pnw.errores.texto.multiSesion", BDSUtil.CODIGO_CANAL_MOBILE));

                                        res.setHeader("Cache-Control", "no-cache");
                                        res.setHeader("Location", getProtocol + "://" + getDomain + ":" + getPort + res.encodeRedirectURL(redirectPage));
                                        res.setCharacterEncoding("UTF-8");
                                        res.setContentType("text/html");
                                        res.setStatus(res.SC_OK);
                                        //res.flushBuffer();
                                        //OutputStream pw = res.getOutputStream();
                                        //pw.println(sb.toString());
                                        //pw.flush();
                                        chain.doFilter(request, response); //en esta estaba response                                        
                                        return;
                                    } else {
                                        erroresController.redireccionSesionMultiple(wrappedResponse, req);
                                    }
                                    return;
                                }
                            }
                        } else {
                            erroresController.redireccionTimeOut(wrappedResponse, req);
                            return;
                        }

                        //redireccion por ajax
                        if (sesionExpiradaEnBD) {
                            String redirectPage = req.getContextPath() + parametrosController.getNombreParametro("pnw.global.url.error", BDSUtil.CODIGO_CANAL_MOBILE);
                            if (isAJAXRequest(req)) {
                                erroresController.setTipoError(textosController.getNombreTexto("pnw.errores.tipo.multiSesion", BDSUtil.CODIGO_CANAL_MOBILE));
                                erroresController.setTituloError(textosController.getNombreTexto("pnw.errores.titulo.multiSesion", BDSUtil.CODIGO_CANAL_MOBILE));
                                erroresController.setMsjError(textosController.getNombreTexto("pnw.errores.texto.multiSesion", BDSUtil.CODIGO_CANAL_MOBILE));

                                res.setHeader("Cache-Control", "no-cache");
                                res.setHeader("Location", res.encodeRedirectURL(redirectPage));
                                res.setHeader("Cache-Control", "no-cache");
                                res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
                                res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
                                res.setDateHeader("Expires", 0); // Proxies.
                                res.setCharacterEncoding("UTF-8");
                                res.setContentType("text/html");
                                res.setStatus(res.SC_OK);
                                //res.flushBuffer();  
                                //OutputStream pw = res.getOutputStream();
                                //pw.println(sb.toString());
                                //pw.flush();
                                chain.doFilter(request, response); //en esta estaba response  
                                return;
                            }
                            //res.sendRedirect(redirectPage);
                            erroresController.redireccionSesionMultiple(wrappedResponse, req);
                            //req.getSession(false).invalidate();
                            return;
                        }
                        chain.doFilter(request, response);
                        return;
                    } else {
                        erroresController.redireccionGenerica(wrappedResponse, req); //en esta y las anteriores se cambio res por wrappedResponse
                        return;
                    }

                } else {
                    if (!response.isCommitted()) {
                        res.setHeader("X-Content-Type-Options", "nosniff");
                        res.setHeader("X-Frame-Options", "SAMEORIGIN");
                        res.setHeader("Cache-Control", "no-cache");
                        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
                        res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
                        res.setDateHeader("Expires", 0); // Proxies.
                        res.setCharacterEncoding("UTF-8");
                        res.setContentType("text/html");
                        chain.doFilter(request, response); //en esta estaba response
                    }
                    return;
                }

            } else {
                throw new IOException();
            }

        } catch (Exception e) {
            if (!e.toString().contains("javax.servlet.ServletException: Response already committed")
                    && !e.toString().contains("No se pudo restablecer la vista /ext/login/inicioSesion.xhtml.")) {
                logger.error(new StringBuilder("ERROR EN SessionFilter: ")
                        .append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                        .append("-EXCP-").append(e.toString()).toString());
                dAOUtil.notificarErrores(e, inicioSesion.getIdCanal(), inicioSesion.getNombreCanal(),
                        parametrosController.getNombreParametro("pnw.services.wsdl.notificaciones", inicioSesion.getIdCanal()), parametrosController.getNombreParametro("pnw.delsur.soporte.email", inicioSesion.getIdCanal()),
                        textosController.getNombreTexto("pnw.errores.titulo.emailNotificacion", inicioSesion.getIdCanal()), parametrosController.getNombreParametro("pnw.delsur.soporte.tlfs", inicioSesion.getIdCanal()),
                        textosController.getNombreTexto("pnw.sms.motivo.notificacionError", inicioSesion.getIdCanal()));

            }
            e.printStackTrace();
            //error en el filtro
            if (response != null && request != null && (request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
                erroresController.redireccionGenerica((HttpServletResponse) response, (HttpServletRequest) request);
            } else {
                throw new IOException();
            }

        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    /**
     * Determina si es una solicitud de una funcion AJAX
     *
     * @param request HttpServletRequest
     * @return boolean
     */
    private boolean isAJAXRequest(HttpServletRequest request) {
        boolean check = false;
        String facesRequest = request.getHeader("Faces-Request");
        if (facesRequest != null && facesRequest.equals("partial/ajax")) {
            check = true;
        }
        return check;
    }

//    private boolean isSessionControlRequiredForThisResource(HttpServletRequest httpServletRequest) {
//        String requestPath = httpServletRequest.getRequestURI();
//        return !requestPath.contains(getTimeoutPage())
//                && !requestPath.contains(getLoginPage())
//                && !requestPath.contains(resources);
//    }
    /**
     * Determinar si la session es invalida
     *
     * @param httpServletRequest HttpServletRequest
     * @return boolean
     */
    private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
        return (httpServletRequest.getRequestedSessionId() != null)
                && !httpServletRequest.isRequestedSessionIdValid();
    }

}
