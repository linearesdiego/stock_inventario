package com.stock.servlets;

import com.stock.sesiones.MovimientoFacade;
import com.stock.sesiones.ProductoFacade;
import com.stock.util.Parametros;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/** Reportes (solo admin): más vendidos en un período y valorización del inventario. */
@WebServlet(name = "ReporteServlet", urlPatterns = {"/Reportes"})
public class ReporteServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private MovimientoFacade movimientoFacade;

    @EJB
    private ProductoFacade productoFacade;

    @Override
    protected void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Por defecto: los últimos 30 días
        LocalDate hasta = Parametros.fecha(request.getParameter("hasta"));
        if (hasta == null) hasta = LocalDate.now();
        LocalDate desde = Parametros.fecha(request.getParameter("desde"));
        if (desde == null) desde = hasta.minusDays(30);

        request.setAttribute("desde", desde);
        request.setAttribute("hasta", hasta);
        request.setAttribute("masVendidos", movimientoFacade.masVendidos(desde, hasta, 10));
        request.setAttribute("productos", productoFacade.findActivos());
        request.setAttribute("valorizacion", productoFacade.valorizacionTotal());
        mostrar("reportes.jsp", request, response);
    }
}
