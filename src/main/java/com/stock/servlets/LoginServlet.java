package com.stock.servlets;

import com.stock.entidades.Usuario;
import com.stock.sesiones.UsuarioFacade;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/Login", "/Logout"})
public class LoginServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private UsuarioFacade usuarioFacade;

    @Override
    protected void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if ("/Logout".equals(request.getServletPath())) {
            HttpSession s = request.getSession(false);
            if (s != null) s.invalidate();
            response.sendRedirect("Login");
            return;
        }

        if (!esPost(request)) {
            mostrar("login.jsp", request, response);
            return;
        }

        String email = request.getParameter("email");
        Usuario u = usuarioFacade.login(email, request.getParameter("password"));
        if (u == null) {
            request.setAttribute("error", "Email o contraseña incorrectos.");
            request.setAttribute("email", email);
            mostrar("login.jsp", request, response);
            return;
        }

        request.getSession(true);
        request.changeSessionId(); // nuevo id de sesión al loguearse (evita robo de sesión)
        u.setPasswordHash(null);   // el hash no hace falta en la sesión
        request.getSession().setAttribute("usuario", u);
        response.sendRedirect("Inicio");
    }
}
