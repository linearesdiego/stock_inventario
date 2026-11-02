<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Usuario"/>
<%@ include file="cabecera.jspf" %>

<c:set var="nuevo" value="${empty u.id}"/>
<h1>${nuevo ? "Nuevo" : "Editar"} usuario</h1>

<form class="formulario" action="AgregarUsuario" method="post">
    <input type="hidden" name="id" value="${u.id}">
    <label>Nombre *
        <input type="text" name="nombre" value="<c:out value='${u.nombre}'/>" maxlength="80" required>
    </label>
    <label>Email *
        <input type="email" name="email" value="<c:out value='${u.email}'/>" maxlength="120" required>
    </label>
    <label>Contraseña ${nuevo ? "*" : ""}
        <input type="password" name="password" minlength="6" ${nuevo ? "required" : ""}>
        <c:if test="${not nuevo}"><small>Dejala vacía para no cambiarla.</small></c:if>
    </label>
    <label>Rol *
        <select name="rol">
            <c:forEach var="r" items="${roles}">
                <option value="${r}" ${u.rol == r ? "selected" : ""}>${r}</option>
            </c:forEach>
        </select>
    </label>
    <label class="check">
        <input type="checkbox" name="activo" ${nuevo || u.activo ? "checked" : ""}> Activo (puede iniciar sesión)
    </label>
    <div class="botones">
        <input type="submit" value="Guardar">
        <a href="ListarUsuarios">Cancelar</a>
    </div>
</form>

<%@ include file="pie.jspf" %>
