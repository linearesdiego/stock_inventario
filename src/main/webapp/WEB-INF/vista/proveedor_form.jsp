<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Proveedor"/>
<%@ include file="cabecera.jspf" %>

<h1>${empty proveedor.id ? "Nuevo" : "Editar"} proveedor</h1>

<form class="formulario" action="AgregarProveedor" method="post">
    <input type="hidden" name="id" value="${proveedor.id}">
    <label>Razón social *
        <input type="text" name="razonSocial" value="<c:out value='${proveedor.razonSocial}'/>" maxlength="120" required>
    </label>
    <label>CUIT
        <input type="text" name="cuit" value="<c:out value='${proveedor.cuit}'/>" maxlength="13" placeholder="20-12345678-9">
    </label>
    <label>Teléfono
        <input type="text" name="telefono" value="<c:out value='${proveedor.telefono}'/>" maxlength="30">
    </label>
    <label>Email
        <input type="email" name="email" value="<c:out value='${proveedor.email}'/>" maxlength="120">
    </label>
    <p class="nota">Los productos se asocian al proveedor desde el formulario de cada producto.</p>
    <div class="botones">
        <input type="submit" value="Guardar">
        <a href="ListarProveedores">Cancelar</a>
    </div>
</form>

<%@ include file="pie.jspf" %>
