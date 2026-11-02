<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Proveedores"/>
<%@ include file="cabecera.jspf" %>

<div class="encabezado">
    <h1>Proveedores</h1>
    <a class="boton" href="AgregarProveedor">+ Nuevo proveedor</a>
</div>

<c:choose>
    <c:when test="${empty lista}">
        <p class="vacio">No hay proveedores cargados.</p>
    </c:when>
    <c:otherwise>
        <table>
            <tr><th>Razón social</th><th>CUIT</th><th>Contacto</th><th>Productos que entrega</th><th>Acciones</th></tr>
            <c:forEach var="p" items="${lista}">
                <tr>
                    <td><c:out value="${p.razonSocial}"/></td>
                    <td><c:out value="${p.cuit}"/></td>
                    <td>
                        <c:out value="${p.telefono}"/>
                        <c:if test="${not empty p.email}"><br><small><c:out value="${p.email}"/></small></c:if>
                    </td>
                    <td>
                        <c:forEach var="pr" items="${p.productos}" varStatus="st">
                            <c:out value="${pr.nombre}"/>${st.last ? "" : ", "}
                        </c:forEach>
                        <c:if test="${empty p.productos}"><span class="suave">—</span></c:if>
                    </td>
                    <td class="acciones-fila">
                        <a href="EditarProveedor?id=${p.id}">Editar</a>
                        <form class="en-linea" action="EliminarProveedor" method="post"
                              onsubmit="return confirm('¿Eliminar el proveedor?')">
                            <input type="hidden" name="id" value="${p.id}">
                            <button class="link peligro">Eliminar</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<%@ include file="pie.jspf" %>
