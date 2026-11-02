<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Usuarios"/>
<%@ include file="cabecera.jspf" %>

<div class="encabezado">
    <h1>Usuarios</h1>
    <a class="boton" href="AgregarUsuario">+ Nuevo usuario</a>
</div>

<table>
    <tr><th>Nombre</th><th>Email</th><th>Rol</th><th>Estado</th><th>Acciones</th></tr>
    <c:forEach var="u" items="${lista}">
        <tr class="${u.activo ? '' : 'anulado'}">
            <td><c:out value="${u.nombre}"/></td>
            <td><c:out value="${u.email}"/></td>
            <td><span class="badge ${u.admin ? 'azul' : 'gris'}">${u.rol}</span></td>
            <td>${u.activo ? 'Activo' : 'Inactivo'}</td>
            <td class="acciones-fila">
                <a href="EditarUsuario?id=${u.id}">Editar</a>
                <c:if test="${u.id != sessionScope.usuario.id}">
                    <form class="en-linea" action="EliminarUsuario" method="post"
                          onsubmit="return confirm('¿Eliminar el usuario?')">
                        <input type="hidden" name="id" value="${u.id}">
                        <button class="link peligro">Eliminar</button>
                    </form>
                </c:if>
            </td>
        </tr>
    </c:forEach>
</table>

<%@ include file="pie.jspf" %>
