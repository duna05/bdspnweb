

/* global CryptoJS */

$(document).ready(function () {
    document.onkeydown = function () {
        switch (event.keyCode) {
            case 116 : //F5 button
                event.returnValue = false;
                event.keyCode = 0;
                return false;
            case 82 : //R button
                if (event.ctrlKey) {
                    event.returnValue = false;
                    event.keyCode = 0;
                    return false;
                }
        }

        if (event.keyCode != 116 && event.keyCode != 82) {
            restarTimeOut();
        }
    }

    $("#menuLateral").mouseover(function (event) {
        $("#menuLateral").removeClass("imgIconoMenuDesplegado").addClass("imgIconoMenuDesplegadoF2");
    });

    $("#menuLateral").mouseout(function (event) {
        $("#menuLateral").removeClass("imgIconoMenuDesplegadoF2").addClass("imgIconoMenuDesplegado");
    });

    /*
     * Permite remover el estilo del div botoncerrarsession en menu.xhtml
     */

    $("#botoncerrarsession").mouseover(function (event) {
        $("#botoncerrarsession").removeClass("interna_menu").addClass("interna_menu_hover");
    });

    /*
     * Permite remover el estilo del div botoncerrarsession en menu.xhtml
     */

    $("#botoncerrarsession").mouseout(function (event) {
        $("#botoncerrarsession").removeClass("interna_menu_hover").addClass("interna_menu");
    });

    $('.solo-numero').keyup(function () {
        this.value = this.value.replace(/[^0-9]/g, '');
    });

    $('.txtMonto').keypress(function () {
        strtemp = this.value;
        strfinal = '';

        if (this.value == null || this.value == '') {
            return this.value = '0,00';
        }
        this.value = this.value.replace(/[^0-9]/g, '');

        if (this.value.length < 3) {
            while (this.value.length < 3) {
                this.value = '0' + this.value;
            }
        } else {
            if (this.value.substring(0, 1) == '0') {
                this.value = this.value.substring(1, this.value.length);
            }
        }
        decimales = this.value.length - 2;
        strdeci = this.value.substring(decimales);

        strmili = this.value.substring(0, decimales);

        i = 0;
        y = 3;
        strmilifinal = '';
        strmiliaux = strmili;
        while (i < strmili.length) {

            if (strmiliaux.length > 3) {
                strmiliaux2 = strmiliaux.substring(strmiliaux.length - y);
                strmiliaux = strmiliaux.substring(0, strmiliaux.length - y);


                if (strmilifinal == '') {
                    strconstruc = strmiliaux2;
                    strmilifinal = strconstruc;
                } else {
                    strconstruc = strmiliaux2;
                    strmilifinal = strconstruc + '.' + strmilifinal;
                }
                i += 3;
            } else {
                if (strmilifinal == '') {
                    strmilifinal = strmiliaux.substring(0, strmiliaux.length);
                } else {
                    strmilifinal = strmiliaux.substring(0, strmiliaux.length) + '.' + strmilifinal;
                }
                i += 3;
            }
        }

        strfinal = strmilifinal + ',' + strdeci;

        $(this).val(strfinal);
    });

    $('.txtMonto').keyup(function () {
        strtemp = this.value;
        strfinal = '';

        if (this.value == null || this.value == '') {
            return this.value = '0,00';
        }
        this.value = this.value.replace(/[^0-9]/g, '');

        if (this.value.length < 3) {
            while (this.value.length < 3) {
                this.value = '0' + this.value;
            }
        } else {
            if (this.value.substring(0, 1) == '0') {
                this.value = this.value.substring(1, this.value.length);
            }
        }
        decimales = this.value.length - 2;
        strdeci = this.value.substring(decimales);

        strmili = this.value.substring(0, decimales);

        i = 0;
        y = 3;
        strmilifinal = '';
        strmiliaux = strmili;
        while (i < strmili.length) {

            if (strmiliaux.length > 3) {
                strmiliaux2 = strmiliaux.substring(strmiliaux.length - y);
                strmiliaux = strmiliaux.substring(0, strmiliaux.length - y);


                if (strmilifinal == '') {
                    strconstruc = strmiliaux2;
                    strmilifinal = strconstruc;
                } else {
                    strconstruc = strmiliaux2;
                    strmilifinal = strconstruc + '.' + strmilifinal;
                }
                i += 3;
            } else {
                if (strmilifinal == '') {
                    strmilifinal = strmiliaux.substring(0, strmiliaux.length);
                } else {
                    strmilifinal = strmiliaux.substring(0, strmiliaux.length) + '.' + strmilifinal;
                }
                i += 3;
            }
        }

        strfinal = strmilifinal + ',' + strdeci;

        $(this).val(strfinal);
    });

    /**
     * funcion para invalidar boton derecho del mouse
     */
    $(this).bind("contextmenu", function (e) {
        e.preventDefault();
    });

    /**
     * funcion para invalidar crtl + c, crtl + x, crtl + v
     */
    $(this).keydown(function (event) {
        var forbiddenKeys = new Array('c', 'x', 'v');
        var keyCode = (event.keyCode) ? event.keyCode : event.which;
        var isCtrl;
        isCtrl = event.ctrlKey
        if (isCtrl) {
            for (i = 0; i < forbiddenKeys.length; i++) {
                if (forbiddenKeys[i] == String.fromCharCode(keyCode).toLowerCase()) {
                    return false;
                }
            }
        }
        return true;
    });


    /*
     PF('dialogerror').show(function (){
     
     alert("prueba"); 
     });
     
     */
});

function salirTimeOut() {
    PF('otpDialog').hide();
    PF('statusDialog').hide();
    PF('timeDialog').show();
}

PrimeFaces.locales["es"] = {
    closeText: "Cerrar",
    prevText: "Anterior",
    nextText: "Siguiente",
    monthNames: ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"],
    monthNamesShort: ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"],
    dayNames: ["Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"],
    dayNamesShort: ["Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab"],
    dayNamesMin: ["Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa"],
    weekHeader: "Semana",
    firstDay: 0,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: "",
    timeOnlyTitle: "Solo hora",
    timeText: "Tiempo",
    hourText: "Hora",
    minuteText: "Minuto",
    secondText: "Segundo",
    currentText: "Fecha actual",
    ampm: false,
    month: "Mes",
    week: "Semana",
    day: "Día",
    allDayText: "Todo el día"
};

function asignarValorOculto(valor, componente) {
    document.getElementById(componente).value = valor;
}

function salirError() {
    var context = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
    window.location.href = context + "/ext/errores/error.xhtml";
}

function redireccionLogin() {
    var context = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
    window.location.href = context;
}

/**
 * Metodo para formatear el monto insertado en un input ej 000.000,00
 * @param {type} obj input del cual se extrae el monto a formatear
 * @returns {formatoMonto.obj.value|String}
 */
function formatoMonto(obj) {
    strtemp = obj.value;
    strfinal = '';

    if (obj.value == null || obj.value == '') {
        return obj.value = '0,00';
    }
    obj.value = obj.value.replace(/[^0-9]/g, '');

    if (obj.value.length < 3) {
        while (obj.value.length < 3) {
            obj.value = '0' + obj.value;
        }
    } else {
        if (obj.value.substring(0, 1) == '0') {
            obj.value = obj.value.substring(1, obj.value.length);
        }
    }
    decimales = obj.value.length - 2;
    strdeci = obj.value.substring(decimales);

    strmili = obj.value.substring(0, decimales);

    i = 0;
    y = 3;
    strmilifinal = '';
    strmiliaux = strmili;
    while (i < strmili.length) {

        if (strmiliaux.length > 3) {
            strmiliaux2 = strmiliaux.substring(strmiliaux.length - y);
            strmiliaux = strmiliaux.substring(0, strmiliaux.length - y);


            if (strmilifinal == '') {
                strconstruc = strmiliaux2;
                strmilifinal = strconstruc;
            } else {
                strconstruc = strmiliaux2;
                strmilifinal = strconstruc + '.' + strmilifinal;
            }
            i += 3;
        } else {
            if (strmilifinal == '') {
                strmilifinal = strmiliaux.substring(0, strmiliaux.length);
            } else {
                strmilifinal = strmiliaux.substring(0, strmiliaux.length) + '.' + strmilifinal;
            }
            i += 3;
        }
    }

    strfinal = strmilifinal + ',' + strdeci;

    $(obj).val(strfinal);
}

function soloNumero(campo) {
    campo.value = campo.value.replace(/[^0-9]/g, '');
}

function reiniciarDatos() {
    if (document.getElementById('formPagarP2P:selectAlias').value != '-1') {
        document.getElementById('formPagarP2P:selectAlias').value = "-1";
        document.getElementById('formPagarP2P:selectTelfDest').value = "0412";
        document.getElementById('formPagarP2P:txtNroTelfDest').value = "";
        document.getElementById('formPagarP2P:selectTipoDoc').value = "V";
        document.getElementById('formPagarP2P:txtIdentificacion').value = "";
        document.getElementById('formPagarP2P:selectBanco').value = "-1";
        document.getElementById('formPagarP2P:nombreAlias').value = "";
    }
}

function reiniciarDatosP2C() {
    if (document.getElementById('formPagarP2P:selectAliasP2C').value != '-1') {
        document.getElementById('formPagarP2P:selectAliasP2C').value = "-1";
        document.getElementById('formPagarP2P:selectTelfDestP2C').value = "0412";
        document.getElementById('formPagarP2P:txtNroTelfDestP2C').value = "";
        document.getElementById('formPagarP2P:selectTipoDocP2C').value = "V";
        document.getElementById('formPagarP2P:txtIdentificacionP2C').value = "";
        document.getElementById('formPagarP2P:selectBancoP2C').value = "-1";
        document.getElementById('formPagarP2P:nombreAliasP2C').value = "";
    }
}

function reiniciarAlias() {
    if (document.getElementById('formPagarP2P:selectAlias').value != '-1') {
        document.getElementById('formPagarP2P:alias').style.display = "none";
        document.getElementById('formPagarP2P:campoAlias').style.display = "none";
        document.getElementById('formPagarP2P:validatorAlias').style.display = "none";
    }
}

function reiniciarAliasP2C() {
    if (document.getElementById('formPagarP2P:selectAliasP2C').value != '-1') {
        document.getElementById('formPagarP2P:aliasP2C').style.display = "none";
        document.getElementById('formPagarP2P:campoAliasP2C').style.display = "none";
        document.getElementById('formPagarP2P:validatorAliasP2C').style.display = "none";
    }
}

function ocultarMostrarFrecuente() {
    if (document.getElementById('formPagarP2P:selectAlias').value != '-1') {
        $('.checkFrecuente').css('display', 'none');
    } else {
        $('.checkFrecuente').css('display', 'block');
    }
}

function ocultarMostrarFrecuenteP2C() {
    if (document.getElementById('formPagarP2P:selectAliasP2C').value != '-1') {
        $('.checkFrecuenteP2C').css('display', 'none');
    } else {
        $('.checkFrecuenteP2C').css('display', 'block');
    }
}


function ocultarMostrarA() {
    if (document.getElementById('formPagarP2P:selectAlias').value === '-1') {
        $('.alias1').css('display', 'none');
        $('.campoAlias1').css('display', 'none');
        $('.validatorAlias1').css('display', 'none');
    } else {
        $('.alias1').css('display', 'block');
        $('.campoAlias1').css('display', 'block');
        $('.validatorAlias1').css('display', 'block');
    }
}





function showMonthYear(calendar) {

    $('.ui-datepicker-calendar').css('display', 'none');
    $('.ui-datepicker .ui-datepicker-next').css('display', 'none');
    $('.ui-datepicker .ui-datepicker-prev').css('display', 'none');
    $('.ui-datepicker').css('top', $('.ui-datepicker').offset().top + 0);

    var month = '';
    var year = '';

    if ($(calendar).val()) {
        month = $(calendar).val().split('/')[0];
        year = $(calendar).val().split('/')[1];
    } else {
        month = $.datepicker.formatDate("mm", new Date());
        year = $.datepicker.formatDate("yy", new Date());
    }

    var exists = 0 != $('.ui-datepicker-year option[value=' + year + ']').length;
    if (!exists) {

        $(".ui-datepicker-year").empty();
        var initYear = parseInt(year) - 10;
        var endYear = parseInt(year) + 10;
        while (initYear < endYear) {

            $(".ui-datepicker-year").append($('<option>', {value: initYear, text: initYear}));
            initYear++;
        }

    }

    $('.ui-datepicker-month').val(parseInt(month) - 1);
    $('.ui-datepicker-year').val(year);
    $(".ui-datepicker-month").change(function () {
        changeMonthYear(calendar);
    });
    $(".ui-datepicker-year").change(function () {
        changeMonthYear(calendar);
    });

}

function changeMonthYear(calendar) {

    console.log('changeMonthYear');
    $('.ui-datepicker-calendar').css('display', 'none');
    $(".ui-datepicker-month").change(function () {
        changeMonthYear(calendar);
    });
    $(".ui-datepicker-year").change(function () {
        changeMonthYear(calendar);
    });

    var month = parseInt($('.ui-datepicker-month').val()) + 1;
    if (month < 10) {
        month = '0' + month;
    }
    $(calendar).val(month + "/" + parseInt($('.ui-datepicker-year').val()));

}

/**
 * metodo para validar pop up. Si no es un pop up redirecciona a una pagina de error
 * @returns VOID
 */
function validarpopup() {
    if (!(window.opener && window.opener !== window)) {
//        SE DEBE COLOCAR LA RUTA CORRECTA DE LA PAGINA DE ERROR
//        
//        window.location.href = 'http://localhost:7001/bdspnweb/sec/menu/menu.xhtml';
    }
}

var isMobile = {
    Android: function () {
        return navigator.userAgent.match(/Android/i);
    },
    BlackBerry: function () {
        return navigator.userAgent.match(/BlackBerry/i);
    },
    iOS: function () {
        return navigator.userAgent.match(/iPhone|iPad|iPod/i);
    },
    Opera: function () {
        return navigator.userAgent.match(/Opera Mini/i);
    },
    Windows: function () {
        return navigator.userAgent.match(/IEMobile/i);
    },
    Mozilla: function () {
        return navigator.userAgent.match(/Mozilla/i);
    },
    Safari: function () {
        return navigator.userAgent.match(/Mobile Safari/i);
    },
    any: function () {
        return (isMobile.Android() || isMobile.BlackBerry() || isMobile.iOS() || isMobile.Opera() || isMobile.Windows());
    }
};


function isMobileDevice() {
    if (!isMobile.any()) {
        if (~window.location.pathname.indexOf("/ext/errores/errorMovil.xhtml") === 0) {
            var context = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
            window.location.href = context + "/ext/errores/errorMovil.xhtml";
        }
    }
}
/*
 function redireccionarSitioSeguro() {
 
 //if (window.location.protocol != "https:") {
 //window.location.href = "https:" + window.location.href.substring(window.location.protocol.length, window.location.href.length);
 
 
 
 //* var context = window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
 // window.location.href = context+"/ext/errores/error.xhtml"
 //}
 }*/

function encriptarPin(componente) {
    if (document.getElementById(componente).value.length < 4) {
        document.getElementById(componente).value = "";
    } else {
        document.getElementById(componente).value = hex_md5(document.getElementById(componente).value);
    }

}

/*funcion que mantiene la opcion actual y el menu padre activos*/
function menuMarcar() {
    var element = $('.menuCategoria');
    var opcion = "_" + $('#formMenu\\:opcionMenu').val();
    element.each(function () {
        $(this).parent().addClass("oculto");
    });
    element = $('.menuOpc');
    element.each(function () {
        if ($(this).attr("id") === 'formMenu:menuPanel:' + opcion) {
            $(this).removeClass("menuOpc").addClass("menuVisitado");
            $(this).parent().parent().addClass('visible');
        }
    });
}

/*funcion que mantiene la opcion actual y el menu padre activos*/
function menuReiniciar() {
    var element = $('.menuCategoria');
    var opcion = "_" + $('#formMenu\\:opcionMenu').val();
    element.each(function () {
        $(this).parent().addClass("oculto");
    }
    );
}

/**
 * funcion para asignar la mascara del pin luego de encriptarlo
 */
function asignarPin(componente, pin) {
    if (document.getElementById(componente).value.length === 4) {
        document.getElementById(componente).value = pin;
    }
}

/**
 * funcion para asignar la mascara del password luego de encriptarlo
 */
function asignarPass(componente, pin) {
    if (document.getElementById(componente).value.length >= 8 && document.getElementById(componente).value.length <= 12) {
        document.getElementById(componente).value = pin;
    }
}

/**
 * funcion para asignar la mascara del password luego de encriptarlo en clave de operaciones especiales
 */
function asignarPassOP(componente, pin) {
    document.getElementById(componente).value = pin;
}

/**
 * funcion para asignar la mascara de las respuestas de desafio luego de encriptarlas
 */
function asignarRespuestas(componente, mascara, cantidadRespuestas) {
    for (i = 0; i < cantidadRespuestas; i++) {
        if (document.getElementById(componente + i).value.length > 0) {
            document.getElementById(componente + i).value = mascara;
        }
    }
}

function obtenerCodBanco(componente) {
    document.getElementById("formAfiliar2:txtCodBanco").value = componente.value.toString().split('--')[1];

}


function limpiarMes() {
    document.getElementById("formDetCta:mesFiltroEdoCta").selectedIndex = 0;

}

//importante variable para alamcernar el id del time out de interna no borrar
var timeOutSs = 45454;

//funcion para hashear en s
function sha256(valor) {
    try {
        if (document.getElementById(componente).value.length > 0) {
            document.getElementById(componente).value = pin;
        }
        var hashObj = new jsSHA("SHA-256", "TEXT", 1);
        hashObj.update(valor);
        alert('valor: ' + hashObj.getHash("HEX"));
    } catch (e) {
        //alert("ERROR: " + e.message);
    }

}

function showPassword(idCampo) {
    document.getElementById(idCampo).setAttribute("type", "input");
}

function hidePassword(idCampo) {
    document.getElementById(idCampo).setAttribute("type", "password");
}

function evitarIERender() {
    alert("UE: " + navigator.userAgent);
    return navigator.userAgent.match(/ MSIE /i);
}

function mostrarOcultarPassword(_elementTxt, _elementPwrd) {
    var element = document.getElementById(_elementTxt);

    if (element.style.display === 'none') {
        element.style.display = 'block';
    } else {
        element.style.display = 'none';
    }

    element = document.getElementById(_elementPwrd);

    if (element.style.display === 'none') {
        element.style.display = 'block';
    } else {
        element.style.display = 'none';
    }
}

function copiarValorSustituirXAstericos(_elementTxtOculto, _elementPwrd, e) {
    var elementPwrd = document.getElementById(_elementPwrd);
    var elementTxtOculto = document.getElementById(_elementTxtOculto);

    //se identifica la tecla que se pulso
    tecla = (document.all) ? e.keyCode : e.which;

    //SI NO ES LA TECLA DE BACK 
    if (tecla !== 8) {
        var elementPwrdString;
        //SE REEMPLAZA EL CARACTER ■ POR ESPACIOS EN BLANCO
        elementPwrdString = elementPwrd.value.replace(/■/g, '');

        if (elementPwrdString === undefined) {
            elementPwrdString = elementPwrd.value;
        }
        
        elementTxtOculto.value = encriptar(desencriptar(elementTxtOculto.value)+elementPwrdString);
        
        var tamanio = desencriptar(elementTxtOculto.value).length;
          
        //SE AGREGA EL CARACTER ■ PARA QUE APARESCA COMO UN ASTERICOS EN EL CAMPO 
        //VISIBLE DE LA CLAVE
        for (var i = 0; i < tamanio; i++) {
            if (i === 0) {
                elementPwrd.value = '■';
            } else {
                elementPwrd.value += '■';
            }
        }
    } else {
        //SI SE PULSO LA TECLA BORRAR
        elementTxtOculto.value = '';
        elementPwrd.value = '';
    }
}

function encriptar(str) {
    return Base64.encode(str);
}

function desencriptar(str) {
    return Base64.decode(str);
}

function copiarValorXAstericos(_elementTxtOculto, _elementPwrd, e) {
    var elementPwrd = document.getElementById(_elementPwrd);
    var elementTxtOculto = document.getElementById(_elementTxtOculto);
    
    //se identifica la tecla que se pulso
    tecla = (document.all) ? e.keyCode : e.which;
    
    //SI NO ES LA TECLA DE BACK 
    if (tecla !== 8) {
        var elementPwrdString;
        //SE REEMPLAZA EL CARACTER ■ POR ESPACIOS EN BLANCO
        elementPwrdString = elementPwrd.value.replace(/■/g, '').trim();

        if (elementPwrdString === undefined) {
            elementPwrdString = elementPwrd.value.trim();
        }
        elementTxtOculto.value = elementTxtOculto.value + elementPwrdString;

        var tamanio = elementTxtOculto.value.length;

        //SE AGREGA EL CARACTER ■ PARA QUE APARESCA COMO UN ASTERICOS EN EL CAMPO 
        //VISIBLE DE LA CLAVE
        for (var i = 0; i < tamanio; i++) {
            if (i === 0) {
                elementPwrd.value = '■';
            } else {
                elementPwrd.value += '■';
            }
        }
    } else {
        //SI SE PULSO LA TECLA BORRAR
        elementTxtOculto.value = '';
        elementPwrd.value = '';
    }
}