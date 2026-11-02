<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Categorías"/>
<%@ include file="cabecera.jspf" %>

<div class="encabezado">
    <h1>Categorías</h1>
    <a class="boton" href="AgregarCategoria">+ Nueva categoría</a>
</div>

<c:choose>
    <c:when test="${empty lista}">
        <p class="vacio">No hay categorías cargadas.</p>
    </c:when>
    <c:otherwise>
        <table>
            <tr><th>Nombre</th><th class="num">Productos</th><th>Acciones</th></tr>
            <c:forEach var="c" items="${lista}">
                <tr>
                    <td><c:out value="${c.nombre}"/></td>
                    <td class="num">${fn:length(c.productos)}</td>
                    <td class="acciones-fila">
                        <a href="ListarProductos?categoria=${c.id}">Ver productos</a>
                        <a href="EditarCategoria?id=${c.id}">Editar</a>
                        <form class="en-linea" action="EliminarCategoria" method="post"
                              onsubmit="return confirm('¿Eliminar la categoría?')">
                            <input type="hidden" name="id" value="${c.id}">
                            <button class="link peligro">Eliminar</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<%@ include file="pie.jspf" %>
