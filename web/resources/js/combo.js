                    function prueba(){
                    
                        var buscar = 'PENSIONADO';                   
                        if (document.getElementById('formFichaCliente:subtipocliente') ){ 
                           var sel = document.getElementById("formFichaCliente:subtipocliente");   
                             for (var i = 0; i < sel.length; i++) {                                 
                                  if(sel.options[i].text==buscar)
                                     {                                      
                                       sel.selectedIndex=i;
                                     } 
                             }                             
                         }   
                    }
                    
                    function subtipocliente(buscar){
                    
                        var buscar = buscar;  
                       // alert(buscar);
                        if (document.getElementById('formFichaCliente:subtipocliente') ){ 
                           var sel = document.getElementById("formFichaCliente:subtipocliente");   
                             for (var i = 0; i < sel.length; i++) {                                 
                                  if(sel.options[i].value==buscar)
                                     {                                      
                                       sel.selectedIndex=i;
                                     } 
                             }                             
                         }   
                    }