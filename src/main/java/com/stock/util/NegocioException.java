package com.stock.util;

import jakarta.ejb.ApplicationException;

/**
 * Error de una regla de negocio (ej: "no hay stock suficiente").
 * Con rollback = true el contenedor deshace la transacción y la excepción
 * llega tal cual al Servlet (no envuelta en EJBException), así se muestra el mensaje.
 */
@ApplicationException(rollback = true)
public class NegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
