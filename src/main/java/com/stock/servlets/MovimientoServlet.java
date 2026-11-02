package com.stock.servlets;

import com.stock.entidades.Movimiento;
import com.stock.entidades.TipoMovimiento;
import com.stock.sesiones.MovimientoFacade;
import com.stock.sesiones.ProductoFacade;
import com.stock.util.NegocioException;
import com.stock.util.Parametros;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "MovimientoServlet",
            urlPatterns = {"/ListarMovimientos", "/RegistrarMovimiento", "/AnularMovimiento"})
public class MovimientoServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private MovimientoFacade movimientoFacade;

    @EJB
    private ProductoFacade productoFacade;

    @Override
    protected void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        switch (request.getServletPath()) {

            case "/ListarMovimientos": {
                // Por defecto, los últimos 30 días
                LocalDate hasta = Parametros.fecha(request.getParameter("hasta"));
                if (hasta == null) hasta = LocalDate.now();
                LocalDate desde = Parametros.fecha(request.getParameter("desde"));
                if (desde == null) desde = hasta.minusDays(30);
                Integer prod = Parametros.entero(request.getParameter("producto"));

                List<Movimiento> lista = movimientoFacade.findEntreFechas(desde, hasta, prod);
                int entradas = 0, salidas = 0;
                for (Movimiento m : lista) {
                    if (m.isAnulado()) continue;
                    if (m.isEntrada()) entradas += m.getCantidad(); else salidas += m.getCantidad();
                }
                request.setAttribute("lista", lista);
                request.setAttribute("desde", desde);
                request.setAttribute("hasta", hasta);
                request.setAttribute("totalEntradas", entradas);
                request.setAttribute("totalSalidas", salidas);
                request.setAttribute("productos", productoFacade.findActivos());
                mostrar("movimiento_lista.jsp", request, response);
                return;
            }

            case "/RegistrarMovimiento": {
                if (esPost(request)) {
                    try {
                        Integer idProducto = Parametros.entero(request.getParameter("producto"));
                        if (idProducto == null) throw new NegocioException("Elegí un producto.");
                        String tipoTxt = request.getParameter("tipo");
                        TipoMovimiento tipo = Parametros.vacio(tipoTxt) ? null : TipoMovimiento.valueOf(tipoTxt);
                        Integer cant = Parametros.entero(request.getParameter("cantidad"));

                        Movimiento m = movimientoFacade.registrar(idProducto, usuario(request), tipo,
                                cant == null ? 0 : cant, request.getParameter("motivo"),
                                request.getParameter("observacion"));

                        String txt = (m.isEntrada() ? "Entrada" : "Salida") + " registrada: "
                                + m.getCantidad() + " u. de " + m.getProducto().getNombre()
                                + ". Stock actual: " + m.getProducto().getStockActual() + ".";
                        if (m.getProducto().isEnAlerta()) {
                            txt += " ⚠ Quedó en o por debajo del stock mínimo ("
                                    + m.getProducto().getStockMinimo() + ").";
                        }
                        mensaje(request, txt);
                        response.sendRedirect("RegistrarMovimiento");
                        return;
                    } catch (NegocioException e) {
                        // Regla 1: el mensaje "Stock insuficiente..." llega hasta acá
                        request.setAttribute("error", e.getMessage());
                    }
                }
                request.setAttribute("productos", productoFacade.findActivos());
                mostrar("movimiento_form.jsp", request, response);
                return;
            }

            case "/AnularMovimiento":
                if (esPost(request)) {
                    try {
                        movimientoFacade.anular(Parametros.entero(request.getParameter("id")));
                        mensaje(request, "Movimiento anulado y stock revertido.");
                    } catch (NegocioException e) {
                        mensaje(request, e.getMessage());
                    }
                }
                response.sendRedirect("ListarMovimientos");
                return;
        }
    }
}
