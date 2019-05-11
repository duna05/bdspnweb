/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.util;

import com.datapro.dibs.encrypin.GeneraPinBlock;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.log4j.Logger;

/**
 *
 * @author cesar.mujica
 */
public class BDSUtil extends PdfPageEventHelper { 
    
    /**
     * Indica si un registro es o no valido
     */
    public static final String VALIDO = "valido";
    
    /**
    * Constante que almacena el código de persona natural
    */
    public static String CODIGO_CANAL_WEB_PN = "2";
    
    /**
     * Constante que almacena un salto de linea
     */
    public static String NUEVALINEA = System.getProperty("line.separator");

    /**
     * Constante que almacena un salto de linea
     */
    public static String NUEVALINEAEMAIL = " \n ";

    /**
     * Constante que almacena codigo del canal mobile
     */
    public static final String CODIGO_CANAL_MOBILE = "1";

    /**
     * Constante que almacena codigo del canal web
     */
    public static final String CODIGO_CANAL_WEB = "2";
    
    /**
     * Constante que almacena codigo del canal web
     */
    public static final String CODIGO_CANAL_WEB_WSDESLSUR = "01";

    /**
     * Constante que almacena codigo del usario desconectado
     */
    public static final Character CODIGO_USUARIO_DESCONECTADO = '0';

    /**
     * Constante que almacena codigo del usario conectado
     */
    public static final Character CODIGO_USUARIO_CONECTADO = '1';

    /**
     * Constante que almacena codigo del cierre de sesion por multiples
     * conexiones
     */
    public static final Character CODIGO_SESION_SIMULTANEA = '2';

    /**
     * Constante que almacena codigo de respuesta exitoso para los dao
     */
    public static final String CODIGO_RESPUESTA_EXITOSO = "000";

    /**
     * Constante que almacena codigo de respuesta exitoso para los dao
     */
    public static final String CODIGO_RESPUESTA_REGISTRO_RECLAMO_EXISTE = "243";

    /**
     * Constante que almacena codigo de tres intentos fallidos para otp
     */
    public static final String CODIGO_TRES_INTENTOS_FALLIDOS_OTP = "473";

    /**
     * Constante que almacena la descripcion de respuesta exitoso para los dao
     */
    public static final String DESCRIPCION_RESPUESTA_EXITOSO = "OK";

    /**
     * Constante que almacena la descripcion de respuesta exitoso para los dao
     */
    public static final String DESCRIPCION_RESPUESTA_FALLIDA = "FAIL";

    /**
     * Constante que almacena la descripcion de respuesta exitoso para los SP
     * que retorna un codigo de salida
     */
    public static final String DESCRIPCION_RESPUESTA_EXITOSO_SP = "OK";

    /**
     * Constante que almacena codigo de respuesta JPA noResultException
     */
    public static final String CODIGO_SIN_RESULTADOS_JPA = "JPA005";
    
    /**
     * Constante que almacena el codigo enviado por los SP cuando no se
     * consiguen registros
     */
    public static final String CODIGO_SIN_RESULTADOS_SP = "256";

    /**
     * Constante que almacena codigo de respuesta Data Invalida
     */
    public static final String CODIGO_DATA_INVALIDA = "DAO009";

    /**
     * Constante que almacena el jndi de conexion a Oracle 9
     */
    public static final String JNDI_ORACLE_9 = "jdbc/CORE";

    /**
     * Constante que almacena el jndi de conexion a Oracle 11
     */
    public static final String JNDI_ORACLE_11 = "jdbc/IB";
    
    /**
     * Constante que almacena el jndi de conexion a P2P
     */
    public static final String JNDI_P2P = "P2P";

    /**
     * Constante que almacena el codigo de error para una excepcion generica que
     * indica validar el log
     */
    public static final String CODIGO_EXCEPCION_GENERICA = "EXC999";

    /**
     * Constante que almacena el set de caracteres de Oracle 9
     */
    public static final String CHARSET_ORACLE_9 = "Cp1252";

    /**
     * Constante que almacena el codigo del producto cuenta corriente
     */
    public static final String CODIGO_PRODUCTO_CC = "BCC";

    /**
     * Constante que almacena el codigo del producto cuenta ahorro
     */
    public static final String CODIGO_PRODUCTO_CA = "BCA";

    /**
     * Constante que almacena el codigo del producto cuenta en moneda extranjera
     */
    public static final String CODIGO_PRODUCTO_ME = "BDC";

    /**
     * Constante que almacena el codigo del producto tarjetas de credito
     */
    public static final String CODIGO_PRODUCTO_TDC = "BTC";

    /**
     * Constante que almacena el codigo del producto prestamos
     */
    public static final String CODIGO_PRODUCTO_PR = "BPR";

    /**
     * Constante que almacena el codigo del producto depositos a plazo
     */
    public static final String CODIGO_PRODUCTO_DP = "BDP";

    /**
     * Constante que almacena el formato de fecha simple
     */
    public static final String FORMATO_FECHA_SIMPLE = "dd/MM/yyyy";

    /**
     * Constante que almacena el formato de fecha con solo mes y año
     */
    public static final String FORMATO_FECHA_MESANO = "MM/yyyy";
    
    /**
     * Constante que almacena el codigo de bloqueo de acceso de usuarios a un
     * canal
     */
    public static final String CODIGO_BLOQUEO_CANAL = "BL";

    /**
     * Constante que almacena codigo que indica que la session esta activa
     */
    public static final String CODIGO_SESSION_ACIVA = "1";

    /**
     * Constante que almacena codigo del parametro TIMEOUT de la tabla
     * IB_PARAMETROS
     */
    public static final String CODIGO_TIME_OUT = "pnw.global.timeOut.minutos";

    /**
     *
     * Constante que almacena el formato de fecha simple
     */
    public static final String FORMATO_FECHA_COMPLETA = "dd/MM/yyyy hh:mm:ss aa";

    /**
     *
     * Constante que almacena el formato de fecha simple
     */
    public static final String FORMATO_FECHA_HORA_MINUTO = "dd/MM/yyyy hh:mm aa";

    /**
     *
     * Constante que almacena el formato de hora simple
     */
    public static final String FORMATO_HORA = "hh:mm:ss aa";

    /**
     * Constante que almacena codigo de busqueda de modulos de usuarios con
     * perfil de piloto
     */
    public static final Character CODIGO_BUSQUEDA_PILOTO = 'I';

    /**
     * Constante que almacena codigo de busqueda de modulos de usuarios que no
     * poseen perfil de piloto
     */
    public static final Character CODIGO_BUSQUEDA_NO_PILOTO = 'A';

    /**
     * Constante que almacena codigo para el status ACTIVO
     */
    public static final String CODIGO_STS_ACTIVO = "A";

    /**
     * formato de texto estandar manejado
     */
    public static final String UNICODE_FORMAT = "UTF8";

    /**
     * tipo de encriptamiento interno para data sensible
     */
    public static final String DESEDE_ENCRYPTION_SCHEME = "DES";
    
    /**
     * codigo de entrada de canal web requerido por el WS de P2P
     */
    public static final String CODIGO_CANAL_P2P = "02";

    /**
     * Log de sistema
     */
    private static final Logger logger = Logger.getLogger(BDSUtil.class.getName());

    /**
     * Constante que almacena el nombre que indica el inicio de la sesion
     */
    public static final String NOMBRE_TRANSACCION_INICIO_SESION = "pnw.global.sesion.iniciar";

    /**
     * Constante que almacena el nombre que indica el cierre de sesion
     */
    public static final String NOMBRE_TRANSACCION_CERRAR_SESION = "pnw.global.sesion.cerrar";

    /**
     * Constante que almacena el id de transferencias a terceros del sur
     */
    public static final String ID_TRANS_CTAS_TERC_DELSUR = "1";

    /**
     * Constante que almacena el id de pagos tdc a terceros del sur
     */
    public static final String ID_TRANS_TDC_TERC_DELSUR = "5";

    /**
     * Constante que almacena el id de transferencias propias a otros bancos
     */
    public static final String ID_TRANS_CTAS_P_OTROS_BCOS = "7";

    /**
     * Constante que almacena el id de pagos tdc propias a otros bancos
     */
    public static final String ID_TRANS_TDC_P_OTROS_BCOS = "8";

    /**
     * Constante que almacena el id de transferencias a terceros otros bancos
     */
    public static final String ID_TRANS_CTAS_T_OTROS_BCOS = "3";

    /**
     * Constante que almacena el id de pagos tdc terceros a otros bancos
     */
    public static final String ID_TRANS_TDC_T_OTROS_BCOS = "4";

    /**
     * Constante que almacena el id de transferencias a cuentas propias DELSUR
     */
    public static final String ID_TRANS_CTAS_PROPIAS_DELSUR = "6";

    /**
     * Constante que almacena el id de pagos tdc propias DELSUR
     */
    public static final String ID_TRANS_TDC_PROPIAS_DELSUR = "2";

    /**
     * Constante que almacena el id de inversiones en fideicomiso
     */
    public static final String ID_TRANS_INVER_FIDEICOMISO = "6";

    /**
     * Constante que almacena el id de posicion consolidada
     */
    public static final String ID_TRANS_POS_CONSOLIDADA = "190";

    /**
     * Constante que almacena el id de detalle de cuenta
     */
    public static final String ID_TRANS_DET_CTA = "1100";

    /**
     * Constante que almacena el id de detalle de un movimiento de una cuenta
     */
    public static final String ID_TRANS_DET_MOV_CTA = "1101";

    /**
     * Constante que almacena el id de detalle de TDC
     */
    public static final String ID_TRANS_DET_TDC = "1102";

    /**
     * Constante que almacena el id de detalle de PRESTAMO
     */
    public static final String ID_TRANS_DET_PRESTAMO = "1103";

    /**
     * Constante que almacena el id que indica el ingreso al menu
     */
    public static final String ID_TRANS_MENU = "247";

    /**
     * Constante que almacena el id que indica el inicio de sesion
     */
    public static final String ID_TRANS_INICIAR_SESION = "1098";

    /**
     * Constante que almacena el id que indica el cierre de sesion
     */
    public static final String ID_TRANS_CERRAR_SESION = "1099";

    /**
     * Constante que almacena el id que indica modulo de olvidó su clave
     */
    public static final String ID_TRANS_OLVIDO_CLAVE = "221";

    /**
     * Constante que almacena el id que indica modulo de olvidó su clave
     */
    public static final String ID_TRANS_REGISTRO_USUARIO = "261";

    /**
     * Constante que almacena el id que indica modulo de cambio de clave
     */
    public static final String ID_TRANS_CAMBIO_CLAVE = "222";

    /**
     * Constante que almacena el id que indica modulo de cambio pdd
     */
    public static final String ID_TRANS_CAMBIO_PDD = "241";

    /**
     * Constante que almacena el id que indica el cierre de sesion por Error
     * Inesperado
     */
    public static final String ID_TRANS_ERROR_INESP = "1997";

    /**
     * Constante que almacena el id que indica el cierre de sesion por multiple
     * session
     */
    public static final String ID_TRANS_MULT_SESS = "1998";

    /**
     * Constante que almacena el id que indica la agenda de transferencias PDS
     */
    public static final String ID_TRANS_AGENDA_TPDS = "283";

    /**
     * Constante que almacena el id que indica la agenda de transferencias POB
     */
    public static final String ID_TRANS_AGENDA_TPOB = "284";

    /**
     * Constante que almacena el id que indica la agenda de transferencias TOB
     */
    public static final String ID_TRANS_AGENDA_TTOB = "286";

    /**
     * Constante que almacena el id que indica la agenda de transferencias TDS
     */
    public static final String ID_TRANS_AGENDA_TTDS = "285";

    /**
     * Constante que almacena el id que indica la agenda de Pagos TDC PDS
     */
    public static final String ID_TRANS_AGENDA_PPDS = "301";

    /**
     * Constante que almacena el id que indica la agenda de Pagos TDC POB
     */
    public static final String ID_TRANS_AGENDA_PPOB = "302";

    /**
     * Constante que almacena el id que indica la agenda de Pagos TDC TOB
     */
    public static final String ID_TRANS_AGENDA_PTOB = "304";

    /**
     * Constante que almacena el id que indica la agenda de Pagos TDC TDS
     */
    public static final String ID_TRANS_AGENDA_PTDS = "303";

    /**
     * Constante que almacena el id que indica los límites
     */
    public static final String ID_SEGURIDAD_LIMITES = "321";

    /**
     * Constante que almacena el id que indica el histórico de transacciones
     */
    public static final String ID_TRANS_HISTTRANSACCIONES = "322";

    /**
     * Constante que almacena el id que indica lista de beneficiarios
     */
    public static final String ID_TRANS_LISTBENEF = "201";

    /**
     * Constante que almacena el id que indica afiliación de beneficiarios
     */
    public static final String ID_TRANS_AFILIARBENEF = "202";

    /**
     * Constante que almacena el id que indica Actualización de Datos
     */
    public static final String ID_TRANS_ACTUALIZACIONDAT = "365";

    /**
     * Constante que almacena el id que indica Solicitud de Chequeras
     */
    public static final String ID_TRANS_SOL_CHEQUERAS = "381";

    /**
     * Constante que almacena el id que indica Consulta de reclamos
     */
    public static final String ID_TRANS_CONSULTA_RECLAMOS = "402";

    /**
     * Constante que almacena el id que indica Registro de reclamos
     */
    public static final String ID_TRANS_REG_RECLAMOS = "441";

    /**
     * Constante que almacena el id que indica Solicitud de Chequeras
     */
    public static final String ID_TRANS_REF_BANCARIA = "382";

    /**
     * Constante que almacena el id que indica Suspencion de Chequeras
     */
    public static final String ID_TRANS_SUSP_CHEQUERAS = "401";

    /**
     * Constante que almacena el id que indica Suspencion de Chequeras
     */
    public static final String ID_TRANS_SUSCRIP_SMS = "421";

    /**
     * Constante que almacena el id que indica Desbloqueo de Usuario
     */
    public static final String ID_TRANS_DESBL_USR = "481";

    /**
     * Constante que almacena el id que indica el cierre de sesion por time out
     */
    public static final String ID_TRANS_TIME_OUT = "1999";

    /**
     * Constante que almacena el id que indica los pagos de préstamos
     */
    public static final String ID_SERVICIOS_PAGOSPRESTAMO = "341";

    /**
     * Constante que almacena el id que indica Consulta de reclamos
     */
    public static final String ID_TRANS_VER_DETALLE_TRANSACCION = "402";

    /**
     * Constante que almacena el id que indica Consulta de reclamos
     */
    public static final String ID_TRANS_PAGOS_PROGRAMADOS = "462";
    
    /**
     * Constante que almacena el id que indica afiliación de beneficiarios a servicios
     */
    public static final String ID_TRANS_AFILIARSERV = "1821";
    
    /**
     * Constante que almacena el id que indica desafiliación de beneficiarios a servicios
     */
    public static final String ID_TRANS_DESAFILIARSERV = "1822";
    
    /**
     * Constante que almacena el id de pagos de servicios
     */
    public static final String ID_TRANS_PAGOS_SERVICIOS = "1823";
    
    /**
     * Constante que almacena el id que indica afiliación de P2P
     */
    public static final String ID_TRANS_AFILIARP2P = "1861";
    
    /**
     * Constante que almacena el id que indica edición de la afiliación P2P
     */
    public static final String ID_TRANS_EDIT_AFIP2P = "1881";
    
    /**
     * Constante que almacena el id que indica edición de la afiliación P2P
     */
    public static final String ID_TRANS_DESAFILIARP2P = "1901";
    
    /**
     * Constante que almacena el id que indica pago de P2P
     */
    public static final String ID_TRANS_PAGARP2P = "1921";
    
    /**
     * Constante que almacena el id que indica pago de P2C
     */
    public static final String ID_TRANS_PAGARP2C = "1922";
    
    /**
     * Constante que almacena el id que indica modulo de olvidó su clave
     */
    public static final String ID_TRANS_REINICIO_USUARIO = "1961";
    
    /**
     * Constante que almacena el id de recargas DIGITEL
     */
    public static final String ID_TRANS_REC_DIGITEL = "rec.digitel";
    
    /**
     * Constante que almacena el id de avance de efectivo
     */
    public static final String ID_TRANS_AVC_EFECT = "1981";
    
    /**
     * Constante que almacena el id de extra finaciamiento
     */
    public static final String ID_TRANS_EXTRA_FMTO = "2002";
    
    /**
     * Constante que almacena el id de pago de extra finaciamiento
     */
    public static final String ID_TRANS_PAG_EXTRA_FMTO = "2021";
    

    public static final String T_TRANSF_PROPIAS = "TPDS"; //Tipo de transferencias propias
    public static final String T_TRANSF_OTROSBANCOS = "TTOB"; //Tipo de transferencias terceros a otros bancos
    public static final String T_TRANSF_PROPOTROSBANCOS = "TPOB"; //Tipo de transferencias propias otros bancos
    public static final String T_TRANSF_3ROSDELSUR = "TTDS"; //Tipo de transferencia a terceros del banco del sur

    public static final String T_PAG_TDC_PROPIAS = "PPDS"; //Tipo de pago tdc propias
    public static final String T_PAG_TDC_OTROSBANCOS = "PTOB"; //Tipo de pago tdc terceros a otros bancos
    public static final String T_PAG_TDC_PROPOTROSBANCOS = "PPOB"; //Tipo de pago tdc propias otros bancos
    public static final String T_PAG_TDC_3ROSDELSUR = "PTDS"; //Tipo de pago tdc a terceros del banco del sur

    public static final String COD_MOV_TAQUILLA = "CMT";
    public static final String COD_TRANSF_DELSUR = "CTBDS";
    public static final String COD_TRANSF_RECIBIDA = "CTR";
    public static final String COD_TRANSF_ENVIADA = "CTE";
    public static final String COD_TRANSF_P2P = "CTP2P";
    public static final String COD_MOV_ME = "CMME";

    public static final char TIPO_AGENDA_TRANF = '1';  //identificador de tipo de agenda para transferencias
    public static final char TIPO_AGENDA_PAGTDC = '2';  //identificador de tipo de agenda para pagos de TDC
    public static final char TIPO_AGENDA_PAGSRV = '3';  //identificador de tipo de agenda para pagos de Servicios
    public static final int VISA = 4;
    public static final int MASTERCARD = 5;
    
    public static final String CODIGO_EMAIL_TRANSF = "46";  //identificador de plantilla de email de transferencias
    
    public static final String CODIGO_EMAIL_PAG_TDC = "48";  //identificador de plantilla de email de pagos de TDC    
    
    /**
     * Estado de la clave de operaciones especiales
     */
    public static final String ESTADO_CLAVE_OP = "estado";

    /**
     * Variable que maneja el total de registros en archivos pdf
     */
    PdfTemplate total;

    /**
     * Variable que maneja el encabezado de la paginación de pdf
     */
    private String encabezado;
    
    /**
     * Cuatros primeros digitos que identifican la cuentas del banco del sur
     */
    public static final String DIGITOS_INICIALES_BANCO_DEL_SUR = "0157";

    /**
     *
     * Constante que almacena el formato de fecha simple
     */
    public static final String FORMATO_FECHA_COMPLETA_LETRAS = "yyyy-MM-dd";

    /**
     * Convierte un objeto java.util.Date a
     * javax.xml.datatype.XMLGregorianCalendar
     *
     * @param date Date
     * @return XMLGregorianCalendar
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        GregorianCalendar gCalendar = new GregorianCalendar();
        gCalendar.setTime(date);
        XMLGregorianCalendar xmlCalendar = null;
        try {
            xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
        } catch (DatatypeConfigurationException e) {
            logger.error(new StringBuilder("ERROR BDSUTIL EN toXMLGregorianCalendar: ").append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA).append("-EXCP-").append(e.toString()).toString());
        }
        return xmlCalendar;
    }

    /**
     * Convierte un XMLGregorianCalendar a java.util.Date in Java
     *
     * @param calendar XMLGregorianCalendar
     * @return Date
     */
    public static Date toDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().getTime();
    }

    /**
     * Genera ID unicos
     *
     * @return String
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Formatea un monto
     *
     * @param value BigDecimal
     * @return String
     */
    public static String formatearMonto(BigDecimal value) {
        if (value == null) {
            return "";
        }

        DecimalFormatSymbols mySymbols = new DecimalFormatSymbols();
        mySymbols.setDecimalSeparator(',');
        mySymbols.setGroupingSeparator('.');
        DecimalFormat newFormat = new DecimalFormat("#,##0.00", mySymbols);
        return newFormat.format(value.doubleValue());
    }

    public static String formatearMontoPdf(BigDecimal value) {
        if (value == null) {
            return "00,00";
        }

        DecimalFormatSymbols mySymbols = new DecimalFormatSymbols();
        mySymbols.setDecimalSeparator(',');
        mySymbols.setGroupingSeparator('.');
        DecimalFormat newFormat = new DecimalFormat("#,##0.00", mySymbols);
        return newFormat.format(value.doubleValue());
    }

    /**
     * Formatea un monto con valor negativo si es mayor a cero
     *
     * @param value BigDecimal
     * @return String
     */
    public static String formatearMontoNegativo(BigDecimal value) {
        if (value == null) {
            return "";
        }

        DecimalFormatSymbols mySymbols = new DecimalFormatSymbols();
        mySymbols.setDecimalSeparator(',');
        mySymbols.setGroupingSeparator('.');
        DecimalFormat newFormat = new DecimalFormat("#,##0.00", mySymbols);
        String valor = newFormat.format(value.doubleValue());
        if (!valor.equalsIgnoreCase("0,00") && !valor.contains("-")) {
            valor = "-" + valor;
        }
        return valor;
    }

    /**
     * Elimina la coma de un monto y la reemplaza por punto
     *
     * @param value String
     * @return String
     */
    public static String eliminarformatoSimpleMonto(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        value = value.replaceAll("[.]", "");
        return value.replace(',', '.');
    }
    
    /**
     * retorna solo los numeros de un string
     *
     * @param value String
     * @return String
     */
    public static String soloNumeros(String value) {
        value = value.replace(" ", "");
        return value.replace("[^d]", "");
    }
    
    /**
     * retorna el numero de telefono en formato para P2P
     *
     * @param value String
     * @return String
     */
    public static String ajustarTelfP2P(String value) {
        if(value != null && value.trim().length() == 11){
            return "58"+value.substring(1);
        }else{
            return "";
        }        
    }

    /**
     * Formatea la fecha/hora
     *
     * @param value Date
     * @return String
     */
    public static String fecha(Date value) {
        if (value == null) {
            return "";
        }

        SimpleDateFormat f = new SimpleDateFormat("dd/MM/YYYY");

        return f.format(value);
    }

    /**
     * Formatea la fecha/hora
     *
     * @param value Date
     * @return String
     */
    public static String fecha2(Date value) {
        /**/
        DateFormatSymbols sym = DateFormatSymbols.getInstance(new Locale("es", "ar"));
        sym.setMonths(new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"});
        sym.setShortMonths(new String[]{"Ene", "Feb", "Mar", "May", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"});
        SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy", sym);

        SimpleDateFormat fecha = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy hh:mm:ss a", sym);

        return fecha.format(value);

    }

    /**
     * Formatea la fecha/hora
     *
     * @param value Date
     * @return String
     */
    public static String fecha2New(Date value) {

        SimpleDateFormat f = new SimpleDateFormat(FORMATO_FECHA_COMPLETA, new Locale("es_ES"));

//        String fecha = f.format(value);

        /**/
        DateFormatSymbols sym = DateFormatSymbols.getInstance(new Locale("es", "ar"));
        sym.setMonths(new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"});
        sym.setShortMonths(new String[]{"Ene", "Feb", "Mar", "May", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"});
        SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy", sym);

        SimpleDateFormat fecha = new SimpleDateFormat(FORMATO_FECHA_COMPLETA, sym);

        return fecha.format(value);

    }

    /**
     * Formatea la fecha/hora
     *
     * @param fecha Date
     * @param formato
     * @return String
     * @throws java.text.ParseException
     */
    public static Date fechaLargaCompleta(Date fecha, String formato) throws ParseException {

        if (fecha == null) {
            Date date = new Date();
        }

        SimpleDateFormat f = new SimpleDateFormat(formato);

        fecha = f.parse(fecha.toString());

        return fecha;
    }

    /**
     * Formatea la fecha/hora
     *
     * @param value Date
     * @param formato String
     * @return String
     */
    public static String formatearFecha(Date value, String formato) {
        if (value == null) {
            return "";
        }

        SimpleDateFormat f = new SimpleDateFormat(formato);

        return f.format(value);
    }

    /**
     * Formatea la fecha/hora en String segun el formato recibido
     *
     * @param value String
     * @param formato String
     * @return String fecha en String formateada
     */
    public static String formatearFechaString(String value, String formato) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        SimpleDateFormat f = new SimpleDateFormat(formato);

        return f.format(value);
    }

    /**
     * Formatea la fecha/hora en String segun el formato recibido
     *
     * @param value String
     * @param formato String
     * @return fecha en Date formateada
     */
    public static Date formatearFechaStringADate(String value, String formato) {
        Date fechaSalida = new Date();
        if (value == null || value.isEmpty()) {
            return fechaSalida;
        }
        SimpleDateFormat f = new SimpleDateFormat(formato);
        try {
            fechaSalida = f.parse(value);
        } catch (ParseException e) {
            logger.error(new StringBuilder("ERROR BDSUTIL EN formatearFechaStringADate: ").append("-DT-").append(value)
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA).append("-EXCP-").append(e.toString()).toString());
        }
        return fechaSalida;
    }

    /**
     * Calcula los minutos entre dos fechas
     *
     * @param finicio Date
     * @param ffin Date
     * @return long
     */
    public static long calculaMinutosEntreFechas(Date finicio, Date ffin) {
        long milis1;
        long milis2;
        long diff;
        long diffMinutos = 0;
        Calendar cinicio = Calendar.getInstance();
        Calendar cfinal = Calendar.getInstance();
        try {
            cinicio.setTime(finicio);
            cfinal.setTime(ffin);

            milis1 = cinicio.getTimeInMillis();

            milis2 = cfinal.getTimeInMillis();

            diff = milis2 - milis1;

            diffMinutos = Math.abs(diff / (60 * 1000));
        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR BDSUTIL EN calculaMinutosEntreFechas: ").append("-DT-").append(new Date())
                    .append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA).append("-EXCP-").append(e.toString()).toString());
            diffMinutos = 0;
        }
        return diffMinutos;
    }

    /**
     * Metodo para validar un regex parcial dentro de una cadena.
     *
     * @param regex Regex a validar
     * @param value String Valor de la cadena
     * @return boolean true si consigue al menos un patron dentro de la cadena -
     * false en caso contrario
     */
    public boolean validarRegexParcial(String regex, String value) {
        Pattern p = Pattern.compile(regex);
        return p.matcher(value.toString()).find();
    }

    /**
     * Metodo para enmascarar una cuenta o tarjeta de debito o credito. La
     * mascara sustituira cuatro (4) asteriscos (*) por la cantidad de
     * caracteres seleccionados del value
     *
     * @param value String Cadena a enmascarar
     * @param cantidad int Cantidad de caracteres a enmascarar de value
     * @return String enmascarado
     */
    public String mascara(String value, int cantidad) {
        if (value == null || value.isEmpty() || value.equals("-1")) {
            return "";
        }

        String caracter = "**** ";

        value = value.substring(cantidad);

        return caracter.concat(value);
    }

    /**
     * Formatea una cadena, se reemplazara por cuatro asteriscos (****) desde el
     * inicio de la cadena hasta length()-4 de la misma ej: 1234567890 ->
     * ****7890
     *
     * @param value String valor de la cadena a formatear,
     * @return String
     */
    public String numeroCuentaFormato(String value) {
        String x = "";
        if (!value.isEmpty()) {
            x = ("****" + value.substring(value.length() - 4, value.length()));
        }
        return x;
    }

    /**
     * Formatea una cadena, se reemplazara por cuatro asteriscos (****) los
     * caracteres intermedios de un String con longitud mayor a 8 ej: 1234567890
     * -> 1234****7890
     *
     * @param value String valor de la cadena a formatear,
     * @return String
     */
    public static String formatoAsteriscosWeb(String value) {
        if (value != null && value.length() > 8) {
            return (value.substring(0, 4) + "****" + value.substring(value.length() - 4, value.length()));
        } else {
            return (value);
        }
    }

    /**
     * Capitaliza un texto
     *
     * @param textoSinFormato String
     * @return String
     */
    public static String capitalizarTexto(String textoSinFormato) {
        String[] palabras = textoSinFormato.split(" ");
        StringBuilder textoFormateado = new StringBuilder();

        for (String palabra : palabras) {
            if (!palabra.trim().equalsIgnoreCase("")) {
                if (palabra.length() == 1) {
                    textoFormateado.append(palabra.toUpperCase().concat(" "));
                }
                if (palabra.length() > 1) {
                    textoFormateado.append(palabra.substring(0, 1).toUpperCase()
                            .concat(palabra.substring(1, palabra.length())
                                    .toLowerCase()).concat(" "));
                }
            }
        }

        return textoFormateado.toString();
    }

    /**
     * Elimina el primer caracter del tipo de producto para hacerlo mas lagible
     * al cliente
     *
     * @param value String codigo de producto del banco
     * @return String condigo del producto sin el primer caracter en caso de ser
     * B
     */
    public String elimPrimerCaracter(String value) {

        if (value != null && !value.trim().isEmpty() && value.charAt(0) == 'B') {
            return value.substring(1);
        } else {
            return "";
        }
    }

    /**
     * metodo utilitario para limpiar los mensajes del contexto de JSF
     */
    public void limipiarMensajesFacesContext() {
        FacesContext context = FacesContext.getCurrentInstance();
        Iterator<FacesMessage> it = context.getMessages();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    //de momento solo se utiliza el encriptado por JS
    /**
     * metodo que se encarga de encriptar un texto en MD5 sin pasarlo a base 64
     *
     * @param texto
     * @return texto (encriptado)
     */
    public String encriptarMD5(String texto) {
        String resultado = "";
        try {
            MessageDigest md = MessageDigest.getInstance(MessageDigestAlgorithms.MD5);
            md.update(texto.getBytes());
            byte[] digest = md.digest();
            for (byte b : digest) {
                resultado = resultado + Integer.toHexString(0xFF & b);
            }            
        } catch (NoSuchAlgorithmException e) {

        }
        return resultado;
    }

    /**
     * Metodo que redirecciona al destino deseado, colocando la ruta como
     * parametro de entrada
     *
     * @Wilmer Rondon Briceno
     * @param ruta
     */
    public void redirectFacesContext(String ruta) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + ruta);
        } catch (Exception e) {
            logger.error(new StringBuilder("ERROR BDSUtil EN redirectFacesContext: ")
                    .append("-DT-").append(new Date()).append("-STS-").append(DESCRIPCION_RESPUESTA_FALLIDA)
                    .append("-EXCP-").append(e.toString()).toString());
        }

    }

    /**
     * metodo que se encarga de reiniciar el bean de session activo y redirigir
     * a la vista seleccionada
     *
     * @param ruta ruta a redirigir luego de reiniciar los valores del bean de
     * sesion
     */
    public void reiniciarBeanSesion(String ruta) {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        this.redirectFacesContext(ruta);
    }

    /**
     * metodo utilitario que genera un numero aleatorio entre 0 y el
     * (valorTope-1)
     *
     * @param valorTope
     * @return un numero aleatorio entre 0 y el (valorTope-1)
     */
    public int numeroAleatorio(int valorTope) {
        Random rand = new Random();
        return rand.nextInt(valorTope);
    }

    /**
     * Metodo que se encarga de encriptar el pin de la TDD en el cifrado
     * requerido por DATAPRO
     *
     * @param numeroTarjeta numero de TDD a encriptar
     * @param pinEnClaro pin de cajero de la TDD
     * @param llaveEncriptamiento llave que se va a utilizar para encriptar
     * @return el pin encriptado
     */
    public String encriptarPinDATAPRO(String numeroTarjeta, String pinEnClaro, String llaveEncriptamiento) {

        if ((numeroTarjeta != null) && (numeroTarjeta.length() < 19)) {
            for (int p = numeroTarjeta.length(); p < 19; p++) {
                numeroTarjeta = "F" + numeroTarjeta;
            }
        }

        GeneraPinBlock convertidor = new GeneraPinBlock();
        return convertidor.calculapinblock(numeroTarjeta, pinEnClaro, llaveEncriptamiento);

    }

    /**
     * metodo que se encarga de decodificar una cadena en base 64
     *
     * @param valor valor a codificar
     * @return la cadena en base64
     */
    public String cadenaBase64(String valor) {

        Base64 base64 = new Base64();
        return base64.encodeAsString(valor.getBytes());
    }

    /**
     * Método que identifica si una TDC es Visa o MasterCard
     *
     * @author wilmer.rondon
     * @param numeroTarjeta
     * @return String
     */
    public String obtieneTipoTDC(String numeroTarjeta) {
        String tipoTarjeta = null;
        String primerCaracter;

        primerCaracter = numeroTarjeta.substring(0, 1);

        if (VISA == Integer.parseInt(primerCaracter)) {
            tipoTarjeta = "Visa";
        } else if (MASTERCARD == Integer.parseInt(primerCaracter)) {
            tipoTarjeta = "MasterCard";
        }

        return tipoTarjeta;
    }

    /**
     * este metodo se encarga de decirno si la fecha de interaccion supero el
     * limite establecido es estatico para utilizarlo en el filter
     *
     * @param fechaHoraActual
     * @param fechaUltInteraccion date con la fecha a evaluar
     * @param tiempoLimiteMin tiempo limite a evaluar
     * @return
     */
    public static boolean validarFechaExpiradaBD(Date fechaHoraActual, Date fechaUltInteraccion, String tiempoLimiteMin) {
        long transcurrido = ((fechaHoraActual.getTime() - fechaUltInteraccion.getTime()) / 1000);
        long minutos = Integer.parseInt(tiempoLimiteMin);
        long segundos = minutos * 60;
        return (segundos < transcurrido);
    }

    /**
     * Convierte un número entero a su versión en palabras maximo 50 Ej 10 ->
     * diez (10); 23 -> veintien caso contrario retorna el texto "mas de
     * cincuenta (50)." adapatado por cmujica, site de referencia:
     * http://www.javamexico.org/blogs/rodrigo_salado_anaya/convertir_numeros_letras
     *
     * @param numero
     * @return Número en forma de palabras.
     */
    public static String convierteNumeroAPalabras(int numero) {
        /*Mapa con las conversiones de los números*/
        if (numero > 50) {
            return "más de cincuenta (50)";
        } else {
            HashMap numTextos = new HashMap();
            numTextos.put(0, "cero");
            numTextos.put(1, "una");
            numTextos.put(2, "dos");
            numTextos.put(3, "tres");
            numTextos.put(4, "cuatro");
            numTextos.put(5, "cinco");
            numTextos.put(6, "seis");
            numTextos.put(7, "siete");
            numTextos.put(8, "ocho");
            numTextos.put(9, "nueve");
            numTextos.put(10, "diez");
            numTextos.put(11, "once");
            numTextos.put(12, "doce");
            numTextos.put(13, "trece");
            numTextos.put(14, "catorce");
            numTextos.put(15, "quince");
            numTextos.put(16, "dieciseis");
            numTextos.put(17, "diecisiete");
            numTextos.put(18, "dieciocho");
            numTextos.put(19, "diecinueve");
            numTextos.put(20, "veinte");
            numTextos.put(21, "veintiuno");
            numTextos.put(22, "veintidós");
            numTextos.put(23, "veintitrés");
            numTextos.put(24, "veinticuatro");
            numTextos.put(25, "veinticinco");
            numTextos.put(26, "veintiséis");
            numTextos.put(27, "veintisiete");
            numTextos.put(28, "veintocho");
            numTextos.put(29, "veintinueve");
            numTextos.put(30, "treinta");
            numTextos.put(40, "cuarenta");
            numTextos.put(50, "cincuenta");

            //primer digito a leer (desde la izq) en caso de ser mayor a 9 seran decenas
            int primerDigito = 0;
            //en caso de ser mayor a 9 este numero seran unidades
            int segundoDigito = 0;
            //traduccion del numero a texto
            StringBuilder numeroEscrito = new StringBuilder();
            //si el numero no existe en el mapa se arma
            if (numTextos.get(numero) == null) {
                String numeroTXT = String.valueOf(numero);
                char[] numeroChars = new char[2];
                numeroTXT.getChars(0, numeroTXT.length(), numeroChars, 0);
                primerDigito = (Integer.parseInt(String.valueOf(numeroChars[0])));
                if (numeroTXT.length() > 1) {
                    segundoDigito = (Integer.parseInt(String.valueOf(numeroChars[0])));
                    numeroEscrito.append(numTextos.get(primerDigito * 10));
                    numeroEscrito.append(" y ");
                    numeroEscrito.append(numTextos.get(segundoDigito));
                }
                return numeroEscrito.append(" (").append(numero).append(")").toString();
            } else {
                return numeroEscrito.append(numTextos.get(numero)).append(" (").append(numero).append(")").toString();
            }
        }
    }

    public static String dateMonth(int mes) {
        String result = "";

        switch (mes) {
            case 0: {
                result = "Enero";
                break;
            }
            case 1: {
                result = "Febrero";
                break;
            }
            case 2: {
                result = "Marzo";
                break;
            }
            case 3: {
                result = "Abril";
                break;
            }
            case 4: {
                result = "Mayo";
                break;
            }
            case 5: {
                result = "Junio";
                break;
            }
            case 6: {
                result = "Julio";
                break;
            }
            case 7: {
                result = "Agosto";
                break;
            }
            case 8: {
                result = "Septiembre";
                break;
            }
            case 9: {
                result = "Octubre";
                break;
            }
            case 10: {
                result = "Noviembre";
                break;
            }
            case 11: {
                result = "Diciembre";
                break;
            }
            default: {
                result = "Error";
                break;
            }
        }
        return result;
    }

    /**
     * Método que convierte un List<String> en un String separando los valores
     * con una coma (,) author wilmer.rondon
     *
     * @param listString
     * @param separador
     * @return String
     */
    public String convierteListStringEnString(List<String> listString, String separador) {
        StringBuilder listcheques = new StringBuilder();
        if (listString != null) {
            for (String i : listString) {
                if (listcheques.length() > 0) {
                    listcheques.append(separador);
                }
                listcheques.append(i);
            }
        }
        return listcheques.toString();
    }

    /**
     * metodo que cuenta la cantidad de caracteres consecutivos en una cadena la
     * cantCaract debe ser mayor a 1 la longitud de la cadena debe ser mayor a
     * la cantCaract
     *
     * @param cadena cadena a evaluar
     * @param cantCaract cantidad de caracteres consecutivos a ubicar
     * @return
     */
    public int validarCantCaracteresConsecutivos(String cadena, int cantCaract) {
        int contCaracteres = 1;
        if (cadena != null && !cadena.trim().equalsIgnoreCase("") && cadena.length() > cantCaract && cantCaract > 1) {
            //valor ascii del primer caracter
            int valorAsc = cadena.codePointAt(0);
            for (int pos = 1; (pos < cadena.length() && contCaracteres < cantCaract); pos++) {
                //se compara si valor ascii del primer caracter es igual al segundo -1 osea son consecutivos en la tabla ascii
                if (valorAsc == (cadena.codePointAt(pos) - 1)) {
                    contCaracteres++;
                } else {
                    //se resetea el contador en 1 porque es el minimo de caracteres en una secuencia ya que es incluyente
                    contCaracteres = 1;
                }
                //seteamos el valor ascii del siguiente caracter a comparar
                valorAsc = cadena.codePointAt(pos);
            }
        }
        return contCaracteres;
    }

    /**
     * metodo utilitario para encriptar data en SHA256 
     * @param valor encriptado en SHA256 en base64
     */
    public String encSHA256(String valor) {
        StringBuilder hexString = new StringBuilder();
        try { 
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(valor.getBytes());            
            hexString.append( Base64.encodeBase64URLSafeString(digest).replaceAll("-", "")); 
            
        } catch (NoSuchAlgorithmException e) {
            logger.error("ERROR: Ocurrio un error al momento de realizar en encriptamiento con encSHA256"+e);
            hexString.append("ERROR");
        }
        return hexString.toString();
    }
    
    /**
     * funcion que nos indica si el numTest es multiplo del numMultiplo
     * @param numTest
     * @param numMultiplo
     * @return 
     */
    public boolean esMultiploDe(Double numTest, Double numMultiplo){
        if((numTest%numMultiplo) == 0){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * metodo utilitario que se encarga de completar con espacion una cadena
     * @param cadena cadena a completar
     * @param longFinal long a la que se requiere completar con espacios
     * @return la cadena con espacios y la longitud deseada
     */
    public static String completarEspacios(String cadena, int longFinal){
        if(cadena == null || longFinal < 1){
            return "";
        }else{
            int longAct = cadena.length();
            if(longAct > longFinal){
                return cadena;
            }else{
                StringBuilder cadenaFinal = new StringBuilder();
                cadenaFinal.append(cadena);
                int espacios = longFinal - longAct;
                for(int i = 0; i < espacios; i++){
                    cadenaFinal.append("&#160;");
                }
                return cadenaFinal.toString();
            }
        }
    }

    public Map<String, Object> getSessionScope() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    }
    /**
     * Método que sirva para colocar ceros a la izquierda
     * @param value
     * @param cantidad
     * @return 
     */
    public static String cerosIzquierda(String value, int cantidad){
        String cadena = "";
        for(int i=0;i<cantidad-value.length();i++){
            cadena+="0";
        }
        cadena+=value;
        return cadena;
    }
    
    /**
     * <p>
     * Return the {@link UIComponent} (if any) with the specified
     * <code>id</code>, searching recursively starting at the specified
     * <code>base</code>, and examining the base component itself, followed by
     * examining all the base component's facets and children. Unlike
     * findComponent method of {@link UIComponentBase}, which skips recursive
     * scan each time it finds a {@link NamingContainer}, this method examines
     * all components, regardless of their namespace (assuming IDs are unique).
     *
     * @param base Base {@link UIComponent} from which to search
     * @param id Component identifier to be matched
     */
    public static UIComponent findComponent(UIComponent base, String id) {

        // Is the "base" component itself the match we are looking for?
        if (id.equals(base.getId())) {
            return base;
        }

        // Search through our facets and children
        UIComponent kid = null;
        UIComponent result = null;
        Iterator kids = base.getFacetsAndChildren();
        while (kids.hasNext() && (result == null)) {
            kid = (UIComponent) kids.next();
            if (id.equals(kid.getId())) {
                result = kid;
                break;
            }
            result = findComponent(kid, id);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    public static UIComponent findComponentInRoot(String id) {
        UIComponent ret = null;

        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            UIComponent root = context.getViewRoot();
            ret = findComponent(root, id);
        }

        return ret;
    }
}
