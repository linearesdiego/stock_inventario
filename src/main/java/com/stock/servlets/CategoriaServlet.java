package com.stock.servlets;

import com.stock.entidades.Categoria;
import com.stock.sesiones.CategoriaFacade;
import com.stock.util.NegocioException;
import com.stock.util.Parametros;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "CategoriaServlet",
            urlPatterns = {"/ListarCategorias", "/AgregarCategoria", "/EditarCategoria", "/EliminarCategoria"})
public class CategoriaServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private CategoriaFacade categoriaFacade;

    @Override
    protected void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        switch (request.getServletPath()) {

            case "/ListarCategorias":
                request.setAttribute("lista", categoriaFacade.findAllOrdenado());
                mostrar("categoria_lista.jsp", request, response);
                return;

            case "/EditarCategoria":
                request.setAttribute("categoria", categoriaFacade.find(Parametros.entero(request.getParameter("id"))));
                mostrar("categoria_form.jsp", request, response);
                return;

            case "/AgregarCategoria": {
                if (!esPost(request)) {
                    mostrar("categoria_form.jsp", request, response);
                    return;
                }
                Integer id = Parametros.entero(request.getParameter("id"));
                Categoria c = (id == null) ? new Categoria() : categoriaFacade.find(id);
                c.setNombre(request.getParameter("nombre") == null ? "" : request.getParameter("nombre").trim());
                try {
                    if (c.getNombre().isEmpty()) {
                        throw new NegocioException("El nombre es obligatorio.");
                    }
                    if (c.getId() == null) categoriaFacade.create(c); else categoriaFacade.edit(c);
                    mensaje(request, "Categoría guardada.");
                    response.sendRedirect("ListarCategorias");
                } catch (NegocioException | EJBException e) {
                    request.setAttribute("error", e instanceof NegocioException ? e.getMessage()
                            : "No se pudo guardar. ¿Ya existe una categoría con ese nombre?");
                    request.setAttribute("categoria", c);
                    mostrar("categoria_form.jsp", request, response);
                }
                return;
            }

            case "/EliminarCategoria":
                if (esPost(request)) {
                    try {
                        categoriaFacade.eliminar(Parametros.entero(request.getParameter("id")));
                        mensaje(request, "Categoría eliminada.");
                    } catch (NegocioException e) {
                        mensaje(request, e.getMessage());
                    }
                }
                response.sendRedirect("ListarCategorias");
                return;
        }
    }
}
