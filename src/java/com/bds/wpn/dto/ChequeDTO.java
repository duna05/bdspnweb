package com.bds.wpn.dto;

/**
 * Clase ChequeDTO
 * @author rony.rodriguez
 */
public class ChequeDTO {
    
    private String numeroCheque;
    private String estado;
    private String conformado;
    private String textoEstado;
    private RespuestaDTO respuestaDTO;

    /**
     * obtiene numero cheque
     * @return numero cheque
     */
    public String getNumeroCheque() {
        return numeroCheque;
    }

    /**
     * inserta numero cheque
     * @param numeroCheque numero cheque
     */
    public void setNumeroCheque(String numeroCheque) {
        this.numeroCheque = numeroCheque;
    }
    
    /**
     * obtiene estado del cheque
     * @return estado del cheque
     */
    public String getEstado() {
        return estado;
    }

    /**
     * inserta estado del cheque
     * @param estado estado del cheque
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * obtiene estado de conformado de cheque
     * @return conformado de cheque
     */
    public String getConformado() {
        return conformado;
    }

    /**
     * inserta estado de conformado de cheque
     * @param conformado conformado de cheque
     */
    public void setConformado(String conformado) {
        this.conformado = conformado;
    }

    /**
     * obtiene texto estado de cheque
     * @return texto estado
     */
    public String getTextoEstado() {
        return textoEstado;
    }

    /**
     * inserta texto estado de cheque
     * @param textoEstado texto estado
     */
    public void setTextoEstado(String textoEstado) {
        this.textoEstado = textoEstado;
    }

    public RespuestaDTO getRespuestaDTO() {
        return respuestaDTO;
    }

    public void setRespuestaDTO(RespuestaDTO respuesta) {
        this.respuestaDTO = respuesta;
    }

    
}
