<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Categoría"/>
<%@ include file="cabecera.jspf" %>

<h1>${empty categoria.id ? "Nueva" : "Editar"} categoría</h1>

<form class="formulario" action="AgregarCategoria" method="post">
    <input type="hidden" name="id" value="${categoria.id}">
    <label>Nombre *
        <input type="text" name="nombre" value="<c:out value='${categoria.nombre}'/>" maxlength="60" required autofocus>
    </label>
    <div class="botones">
        <input type="submit" value="Guardar">
        <a href="ListarCategorias">Cancelar</a>
    </div>
</form>

<%@ include file="pie.jspf" %>
