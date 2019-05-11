/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.exception;

/**
 *
 * @author cesar.mujica
 */
public class ServiceException extends Exception {

    /**
     * Instanciar el objeto ServiceException
     */
    public ServiceException() {
    }

    /**
     * Instanciar el objeto ServiceException
     *
     * @param message String
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Instanciar el objeto ServiceException
     *
     * @param message String
     * @param cause Throwable
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instanciar el objeto ServiceException
     *
     * @param cause Throwable
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Instanciar el objeto ServiceException
     *
     * @param message String
     * @param cause Throwable
     * @param enableSuppression boolean
     * @param writableStackTrace boolean
     */
    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
