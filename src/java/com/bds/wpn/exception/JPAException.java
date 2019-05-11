/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.exception;

/**
 *
 * @author renseld.lugo
 */
public class JPAException extends Exception {

    /**
     * Instanciar el objeto JPAException
     */
    public JPAException() {
    }

    /**
     * Instanciar el objeto JPAException
     *
     * @param message String
     */
    public JPAException(String message) {
        super(message);
    }

    /**
     * Instanciar el objeto JPAException
     *
     * @param message String
     * @param cause Throwable
     */
    public JPAException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instanciar el objeto JPAException
     *
     * @param cause Throwable
     */
    public JPAException(Throwable cause) {
        super(cause);
    }

    /**
     * Instanciar el objeto JPAException
     *
     * @param message String
     * @param cause Throwable
     * @param enableSuppression boolean
     * @param writableStackTrace boolean
     */
    public JPAException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
