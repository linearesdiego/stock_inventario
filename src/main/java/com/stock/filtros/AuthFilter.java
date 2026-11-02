package com.stock.filtros;

import com.stock.entidades.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

/**
 * Intercepta todas las peticiones:
 *  - Si no hay usuario en sesión, redirige al login (salvo el login y los estilos).
 *  - Si la ruta es solo para administradores y el usuario es EMPLEADO, responde 403.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter extends HttpFilter {

    private static final long serialVersionUID = 1L;

    /** Rutas que se pueden abrir sin iniciar sesión. */
    private static final Set<String> PUBLICAS = Set.of("/Login");

    /** Rutas reservadas al rol ADMIN. */
    private static final Set<String> SOLO_ADMIN = Set.of(
            "/AgregarProducto", "/EditarProducto", "/EliminarProducto", "/ReactivarProducto",
            "/ListarCategorias", "/AgregarCategoria", "/EditarCategoria", "/EliminarCategoria",
            "/ListarProveedores", "/AgregarProveedor", "/EditarProveedor", "/EliminarProveedor",
            "/AnularMovimiento", "/Reportes",
            "/ListarUsuarios", "/AgregarUsuario", "/EditarUsuario", "/EliminarUsuario");

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");
        String ruta = req.getServletPath();

        if (PUBLICAS.contains(ruta) || ruta.startsWith("/css/")) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession sesion = req.getSession(false);
        Usuario usuario = sesion == null ? null : (Usuario) sesion.getAttribute("usuario");

        if (usuario == null) {
            res.sendRedirect(req.getContextPath() + "/Login");
            return;
        }

        if (SOLO_ADMIN.contains(ruta) && !usuario.isAdmin()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Esta sección es solo para administradores.");
            return;
        }

        // Evita que el navegador muestre páginas privadas desde la caché después del logout
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        chain.doFilter(req, res);
    }
}
