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
public class DAOException extends Exception {

    public DAOException() {
    }

    /**
     * Iniciar el objeto DAOException
     *
     * @param message String
     */
    public DAOException(String message) {
        super(message);
    }

    /**
     * Iniciar el objeto DAOException
     *
     * @param message String
     * @param cause Throwable
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Iniciar el objeto DAOException
     *
     * @param cause Throwable
     */
    public DAOException(Throwable cause) {
        super(cause);
    }

    /**
     * Iniciar el objeto DAOException
     *
     * @param message String
     * @param cause Throwable
     * @param enableSuppression boolean
     * @param writableStackTrace boolean
     */
    public DAOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
