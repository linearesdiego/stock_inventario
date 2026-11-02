package com.stock.servlets;

import com.stock.entidades.Rol;
import com.stock.entidades.Usuario;
import com.stock.sesiones.UsuarioFacade;
import com.stock.util.NegocioException;
import com.stock.util.Parametros;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UsuarioServlet",
            urlPatterns = {"/ListarUsuarios", "/AgregarUsuario", "/EditarUsuario", "/EliminarUsuario"})
public class UsuarioServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private UsuarioFacade usuarioFacade;

    @Override
    protected void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        switch (request.getServletPath()) {

            case "/ListarUsuarios":
                request.setAttribute("lista", usuarioFacade.findAllOrdenado());
                mostrar("usuario_lista.jsp", request, response);
                return;

            case "/EditarUsuario":
                request.setAttribute("u", usuarioFacade.find(Parametros.entero(request.getParameter("id"))));
                request.setAttribute("roles", Rol.values());
                mostrar("usuario_form.jsp", request, response);
                return;

            case "/AgregarUsuario": {
                request.setAttribute("roles", Rol.values());
                if (!esPost(request)) {
                    mostrar("usuario_form.jsp", request, response);
                    return;
                }
                Integer id = Parametros.entero(request.getParameter("id"));
                Usuario u = (id == null) ? new Usuario() : usuarioFacade.find(id);
                u.setNombre(request.getParameter("nombre"));
                u.setEmail(request.getParameter("email"));
                u.setRol(Rol.valueOf(request.getParameter("rol")));
                u.setActivo(request.getParameter("activo") != null);
                try {
                    if (Parametros.vacio(u.getNombre()) || Parametros.vacio(u.getEmail())) {
                        throw new NegocioException("Nombre y email son obligatorios.");
                    }
                    // Un admin no puede sacarse a sí mismo el rol ni desactivarse (se quedaría afuera)
                    if (u.getId() != null && u.getId().equals(usuario(request).getId())
                            && (u.getRol() != Rol.ADMIN || !u.isActivo())) {
                        throw new NegocioException("No podés quitarte el rol de administrador ni desactivarte a vos mismo.");
                    }
                    usuarioFacade.guardar(u, request.getParameter("password"));
                    mensaje(request, "Usuario guardado.");
                    response.sendRedirect("ListarUsuarios");
                } catch (NegocioException e) {
                    request.setAttribute("error", e.getMessage());
                    request.setAttribute("u", u);
                    mostrar("usuario_form.jsp", request, response);
                }
                return;
            }

            case "/EliminarUsuario":
                if (esPost(request)) {
                    Integer id = Parametros.entero(request.getParameter("id"));
                    if (id != null && id.equals(usuario(request).getId())) {
                        mensaje(request, "No podés eliminar tu propio usuario.");
                    } else {
                        usuarioFacade.eliminar(id);
                        mensaje(request, "Usuario eliminado (o desactivado si tenía movimientos).");
                    }
                }
                response.sendRedirect("ListarUsuarios");
                return;
        }
    }
}
