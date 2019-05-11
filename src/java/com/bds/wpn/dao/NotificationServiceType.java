/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.dao;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 * Interfaz de implementacion para diferenciar el uso de distintas
 * implementaciones de una misma interfaz, el proposito de esta es etiquetar o
 * agrupar las implementaciones referentes a los servicios SOAP y MOCK de
 * sistema, sin embargo pueden agregarse mas
 *
 * @author Yaher Carrillo
 */
@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface NotificationServiceType {

    ServiceType value();

    public enum ServiceType {

        SERVICES, MOCK;
    }
}
