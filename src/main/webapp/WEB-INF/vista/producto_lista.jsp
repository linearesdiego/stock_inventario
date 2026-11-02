<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Productos"/>
<%@ include file="cabecera.jspf" %>

<div class="encabezado">
    <h1>Productos</h1>
    <c:if test="${sessionScope.usuario.admin}">
        <a class="boton" href="AgregarProducto">+ Nuevo producto</a>
    </c:if>
</div>

<form class="filtro" action="ListarProductos" method="get">
    <input type="text" name="q" value="<c:out value='${param.q}'/>" placeholder="Buscar por código o nombre">
    <select name="categoria">
        <option value="">Todas las categorías</option>
        <c:forEach var="c" items="${categorias}">
            <option value="${c.id}" ${param.categoria == c.id ? 'selected' : ''}><c:out value="${c.nombre}"/></option>
        </c:forEach>
    </select>
    <input type="submit" value="Buscar">
</form>

<c:choose>
    <c:when test="${empty lista}">
        <p class="vacio">No hay productos para mostrar.</p>
    </c:when>
    <c:otherwise>
        <table>
            <tr>
                <th>Código</th><th>Nombre</th><th>Categoría</th><th>Proveedor</th>
                <th class="num">Precio</th><th class="num">Stock</th><th class="num">Mínimo</th><th>Acciones</th>
            </tr>
            <c:forEach var="p" items="${lista}">
                <tr>
                    <td><c:out value="${p.codigo}"/></td>
                    <td><c:out value="${p.nombre}"/></td>
                    <td><c:out value="${p.categoria.nombre}"/></td>
                    <td><c:out value="${p.proveedor.razonSocial}"/></td>
                    <td class="num"><fmt:formatNumber value="${p.precio}" type="currency" currencySymbol="$"/></td>
                    <td class="num">
                        <span class="badge ${p.stockActual == 0 ? 'rojo' : (p.enAlerta ? 'naranja' : 'verde')}">${p.stockActual}</span>
                    </td>
                    <td class="num">${p.stockMinimo}</td>
                    <td class="acciones-fila">
                        <a href="RegistrarMovimiento?producto=${p.id}">Mover</a>
                        <a href="ListarMovimientos?producto=${p.id}">Historial</a>
                        <c:if test="${sessionScope.usuario.admin}">
                            <a href="EditarProducto?id=${p.id}">Editar</a>
                            <form class="en-linea" action="EliminarProducto" method="post"
                                  onsubmit="return confirm('¿Eliminar el producto? Si tiene movimientos quedará inactivo.')">
                                <input type="hidden" name="id" value="${p.id}">
                                <button class="link peligro">Eliminar</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<c:if test="${sessionScope.usuario.admin}">
    <c:choose>
        <c:when test="${param.inactivos == '1'}">
            <h2>Productos dados de baja</h2>
            <c:choose>
                <c:when test="${empty inactivos}"><p class="vacio">No hay productos inactivos.</p></c:when>
                <c:otherwise>
                    <table>
                        <tr><th>Código</th><th>Nombre</th><th class="num">Stock</th><th></th></tr>
                        <c:forEach var="p" items="${inactivos}">
                            <tr class="anulado">
                                <td><c:out value="${p.codigo}"/></td>
                                <td><c:out value="${p.nombre}"/></td>
                                <td class="num">${p.stockActual}</td>
                                <td>
                                    <form class="en-linea" action="ReactivarProducto" method="post">
                                        <input type="hidden" name="id" value="${p.id}">
                                        <button class="link">Reactivar</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <p><a href="ListarProductos?inactivos=1">Ver productos dados de baja</a></p>
        </c:otherwise>
    </c:choose>
</c:if>

<%@ include file="pie.jspf" %>
