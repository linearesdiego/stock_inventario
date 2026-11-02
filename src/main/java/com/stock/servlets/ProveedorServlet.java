package com.stock.servlets;

import com.stock.entidades.Proveedor;
import com.stock.sesiones.ProveedorFacade;
import com.stock.util.NegocioException;
import com.stock.util.Parametros;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ProveedorServlet",
            urlPatterns = {"/ListarProveedores", "/AgregarProveedor", "/EditarProveedor", "/EliminarProveedor"})
public class ProveedorServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProveedorFacade proveedorFacade;

    @Override
    protected void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        switch (request.getServletPath()) {

            case "/ListarProveedores":
                request.setAttribute("lista", proveedorFacade.findAllOrdenado());
                mostrar("proveedor_lista.jsp", request, response);
                return;

            case "/EditarProveedor":
                request.setAttribute("proveedor", proveedorFacade.find(Parametros.entero(request.getParameter("id"))));
                mostrar("proveedor_form.jsp", request, response);
                return;

            case "/AgregarProveedor": {
                if (!esPost(request)) {
                    mostrar("proveedor_form.jsp", request, response);
                    return;
                }
                Integer id = Parametros.entero(request.getParameter("id"));
                Proveedor p = (id == null) ? new Proveedor() : proveedorFacade.find(id);
                p.setRazonSocial(limpio(request.getParameter("razonSocial")));
                p.setCuit(limpio(request.getParameter("cuit")));
                p.setTelefono(limpio(request.getParameter("telefono")));
                p.setEmail(limpio(request.getParameter("email")));
                try {
                    if (p.getRazonSocial() == null) {
                        throw new NegocioException("La razón social es obligatoria.");
                    }
                    if (p.getCuit() != null && !p.getCuit().matches("\\d{2}-?\\d{8}-?\\d")) {
                        throw new NegocioException("El CUIT debe tener 11 dígitos (ej: 20-12345678-9).");
                    }
                    if (p.getId() == null) proveedorFacade.create(p); else proveedorFacade.edit(p);
                    mensaje(request, "Proveedor guardado.");
                    response.sendRedirect("ListarProveedores");
                } catch (NegocioException | EJBException e) {
                    request.setAttribute("error", e instanceof NegocioException ? e.getMessage()
                            : "No se pudo guardar. ¿Ya existe un proveedor con ese CUIT?");
                    request.setAttribute("proveedor", p);
                    mostrar("proveedor_form.jsp", request, response);
                }
                return;
            }

            case "/EliminarProveedor":
                if (esPost(request)) {
                    try {
                        proveedorFacade.eliminar(Parametros.entero(request.getParameter("id")));
                        mensaje(request, "Proveedor eliminado.");
                    } catch (NegocioException e) {
                        mensaje(request, e.getMessage());
                    }
                }
                response.sendRedirect("ListarProveedores");
                return;
        }
    }

    private static String limpio(String s) {
        return Parametros.vacio(s) ? null : s.trim();
    }
}
