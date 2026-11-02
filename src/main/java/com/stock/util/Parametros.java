package com.stock.util;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Ayudas para leer parámetros de los formularios sin repetir try/catch en cada Servlet. */
public final class Parametros {

    private Parametros() {
    }

    public static boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static Integer entero(String s) {
        if (vacio(s)) return null;
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            throw new NegocioException("El valor '" + s + "' no es un número entero válido.");
        }
    }

    public static BigDecimal decimal(String s) {
        if (vacio(s)) return null;
        try {
            return new BigDecimal(s.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new NegocioException("El valor '" + s + "' no es un importe válido.");
        }
    }

    public static LocalDate fecha(String s) {
        if (vacio(s)) return null;
        try {
            return LocalDate.parse(s.trim()); // formato yyyy-MM-dd del <input type="date">
        } catch (Exception e) {
            throw new NegocioException("La fecha '" + s + "' no es válida.");
        }
    }
}
