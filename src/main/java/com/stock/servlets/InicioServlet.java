package com.stock.servlets;

import com.stock.sesiones.MovimientoFacade;
import com.stock.sesiones.ProductoFacade;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Panel principal: alertas de stock mínimo, últimos movimientos y (para admin) la valorización. */
@WebServlet(name = "InicioServlet", urlPatterns = {"/Inicio", "/Alertas"})
public class InicioServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProductoFacade productoFacade;

    @EJB
    private MovimientoFacade movimientoFacade;

    @Override
    protected void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("alertas", productoFacade.findBajoMinimo());

        if ("/Alertas".equals(request.getServletPath())) {
            mostrar("alertas.jsp", request, response);
            return;
        }

        request.setAttribute("cantProductos", productoFacade.findActivos().size());
        request.setAttribute("ultimos", movimientoFacade.findUltimos(8));
        if (usuario(request).isAdmin()) {
            request.setAttribute("valorizacion", productoFacade.valorizacionTotal());
        }
        mostrar("inicio.jsp", request, response);
    }
}
