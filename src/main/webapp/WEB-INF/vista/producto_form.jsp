<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Producto"/>
<%@ include file="cabecera.jspf" %>

<c:set var="nuevo" value="${empty producto.id}"/>
<h1>${nuevo ? "Nuevo" : "Editar"} producto</h1>

<form class="formulario" action="AgregarProducto" method="post">
    <input type="hidden" name="id" value="${producto.id}">

    <label>Código *
        <input type="text" name="codigo" value="<c:out value='${producto.codigo}'/>" maxlength="30" required>
    </label>
    <label>Nombre *
        <input type="text" name="nombre" value="<c:out value='${producto.nombre}'/>" maxlength="120" required>
    </label>
    <label>Categoría
        <select name="categoria">
            <option value="">(sin categoría)</option>
            <c:forEach var="c" items="${categorias}">
                <option value="${c.id}" ${producto.categoria.id == c.id ? "selected" : ""}><c:out value="${c.nombre}"/></option>
            </c:forEach>
        </select>
    </label>
    <label>Proveedor
        <select name="proveedor">
            <option value="">(sin proveedor)</option>
            <c:forEach var="pr" items="${proveedores}">
                <option value="${pr.id}" ${producto.proveedor.id == pr.id ? "selected" : ""}><c:out value="${pr.razonSocial}"/></option>
            </c:forEach>
        </select>
    </label>
    <label>Precio *
        <input type="number" name="precio" value="${producto.precio}" min="0" step="0.01" required>
    </label>
    <label>Stock mínimo *
        <input type="number" name="stockMinimo" value="${empty producto ? 0 : producto.stockMinimo}" min="0" required>
    </label>
    <c:choose>
        <c:when test="${nuevo}">
            <label>Stock inicial
                <input type="number" name="stockInicial" value="${empty stockInicial ? 0 : stockInicial}" min="0">
                <small>Se registra como una entrada en el historial.</small>
            </label>
        </c:when>
        <c:otherwise>
            <p class="nota">Stock actual: <strong>${producto.stockActual}</strong>.
                Para cambiarlo, <a href="RegistrarMovimiento?producto=${producto.id}">registrá un movimiento</a>.</p>
        </c:otherwise>
    </c:choose>

    <div class="botones">
        <input type="submit" value="Guardar">
        <a href="ListarProductos">Cancelar</a>
    </div>
</form>

<%@ include file="pie.jspf" %>
