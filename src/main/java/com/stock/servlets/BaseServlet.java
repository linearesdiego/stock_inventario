package com.stock.servlets;

import com.stock.entidades.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Base de todos los controladores: GET y POST van al mismo método procesar(),
 * igual que en gestion-alumnos, y trae dos ayudas que se usan en todos lados.
 */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    protected static final String VISTAS = "/WEB-INF/vista/";

    protected abstract void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesar(request, response);
    }

    /** Usuario logueado (el AuthFilter garantiza que existe). */
    protected Usuario usuario(HttpServletRequest request) {
        return (Usuario) request.getSession().getAttribute("usuario");
    }

    /** Mensaje que se muestra una vez en la próxima página (sobrevive al redirect). */
    protected void mensaje(HttpServletRequest request, String texto) {
        request.getSession().setAttribute("mensaje", texto);
    }

    protected boolean esPost(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod());
    }

    protected void mostrar(String vista, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VISTAS + vista).forward(request, response);
    }
}
