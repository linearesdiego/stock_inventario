<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Alertas"/>
<%@ include file="cabecera.jspf" %>

<h1>Alertas de stock mínimo</h1>
<p class="subtitulo">Productos con stock actual menor o igual al stock mínimo.</p>

<c:choose>
    <c:when test="${empty alertas}">
        <p class="vacio">Todo en orden: ningún producto está en el mínimo.</p>
    </c:when>
    <c:otherwise>
        <table>
            <tr><th>Código</th><th>Producto</th><th>Categoría</th><th>Proveedor</th><th>Stock</th><th>Mínimo</th><th></th></tr>
            <c:forEach var="p" items="${alertas}">
                <tr>
                    <td><c:out value="${p.codigo}"/></td>
                    <td><c:out value="${p.nombre}"/></td>
                    <td><c:out value="${p.categoria.nombre}"/></td>
                    <td><c:out value="${p.proveedor.razonSocial}"/>
                        <c:if test="${not empty p.proveedor.telefono}"><br><small>☎ <c:out value="${p.proveedor.telefono}"/></small></c:if>
                    </td>
                    <td><span class="badge ${p.stockActual == 0 ? 'rojo' : 'naranja'}">${p.stockActual}</span></td>
                    <td>${p.stockMinimo}</td>
                    <td><a href="RegistrarMovimiento?tipo=ENTRADA&producto=${p.id}">Reponer</a></td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<%@ include file="pie.jspf" %>
