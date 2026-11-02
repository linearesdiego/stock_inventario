package com.stock.servlets;

import com.stock.entidades.Producto;
import com.stock.sesiones.CategoriaFacade;
import com.stock.sesiones.ProductoFacade;
import com.stock.sesiones.ProveedorFacade;
import com.stock.util.NegocioException;
import com.stock.util.Parametros;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ProductoServlet",
            urlPatterns = {"/ListarProductos", "/AgregarProducto", "/EditarProducto",
                           "/EliminarProducto", "/ReactivarProducto"})
public class ProductoServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProductoFacade productoFacade;

    @EJB
    private CategoriaFacade categoriaFacade;

    @EJB
    private ProveedorFacade proveedorFacade;

    @Override
    protected void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        switch (request.getServletPath()) {

            case "/ListarProductos": {
                String q = request.getParameter("q");
                Integer cat = Parametros.entero(request.getParameter("categoria"));
                request.setAttribute("lista", productoFacade.buscarActivos(q, cat));
                request.setAttribute("categorias", categoriaFacade.findAllOrdenado());
                if (usuario(request).isAdmin() && "1".equals(request.getParameter("inactivos"))) {
                    request.setAttribute("inactivos", productoFacade.findInactivos());
                }
                mostrar("producto_lista.jsp", request, response);
                return;
            }

            case "/EditarProducto": {
                request.setAttribute("producto", productoFacade.find(Parametros.entero(request.getParameter("id"))));
                mostrarFormulario(request, response);
                return;
            }

            case "/AgregarProducto": {
                if (!esPost(request)) {
                    mostrarFormulario(request, response);
                    return;
                }
                // Se envió el formulario: guardar (nuevo o editado)
                Integer id = Parametros.entero(request.getParameter("id"));
                Producto p = (id == null) ? new Producto() : productoFacade.find(id);
                try {
                    p.setCodigo(request.getParameter("codigo"));
                    p.setNombre(request.getParameter("nombre"));
                    p.setCategoria(categoriaFacade.find(Parametros.entero(request.getParameter("categoria"))));
                    p.setProveedor(proveedorFacade.find(Parametros.entero(request.getParameter("proveedor"))));
                    p.setPrecio(Parametros.decimal(request.getParameter("precio")));
                    Integer minimo = Parametros.entero(request.getParameter("stockMinimo"));
                    p.setStockMinimo(minimo == null ? 0 : minimo);

                    if (id == null) {
                        Integer inicial = Parametros.entero(request.getParameter("stockInicial"));
                        productoFacade.crear(p, inicial == null ? 0 : inicial, usuario(request));
                        mensaje(request, "Producto " + p.getNombre() + " creado.");
                    } else {
                        // El stock NO se edita acá: solo cambia con movimientos
                        productoFacade.guardar(p);
                        mensaje(request, "Producto " + p.getNombre() + " actualizado.");
                    }
                    response.sendRedirect("ListarProductos");
                } catch (NegocioException e) {
                    request.setAttribute("error", e.getMessage());
                    request.setAttribute("producto", p);
                    request.setAttribute("stockInicial", request.getParameter("stockInicial"));
                    mostrarFormulario(request, response);
                }
                return;
            }

            case "/EliminarProducto": {
                if (esPost(request)) {
                    boolean borrado = productoFacade.eliminar(Parametros.entero(request.getParameter("id")));
                    mensaje(request, borrado
                            ? "Producto eliminado."
                            : "El producto tiene movimientos: se dio de baja (inactivo) para no perder el historial.");
                }
                response.sendRedirect("ListarProductos");
                return;
            }

            case "/ReactivarProducto": {
                if (esPost(request)) {
                    productoFacade.reactivar(Parametros.entero(request.getParameter("id")));
                    mensaje(request, "Producto reactivado.");
                }
                response.sendRedirect("ListarProductos");
                return;
            }
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("categorias", categoriaFacade.findAllOrdenado());
        request.setAttribute("proveedores", proveedorFacade.findAllOrdenado());
        mostrar("producto_form.jsp", request, response);
    }
}
