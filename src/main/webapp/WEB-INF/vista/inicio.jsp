<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Panel"/>
<%@ include file="cabecera.jspf" %>

<h1>Hola, <c:out value="${sessionScope.usuario.nombre}"/></h1>

<div class="tarjetas">
    <div class="tarjeta-dato">
        <span class="valor">${cantProductos}</span>
        <span class="etiqueta">Productos activos</span>
    </div>
    <a class="tarjeta-dato ${empty alertas ? '' : 'alerta'}" href="Alertas">
        <span class="valor">${fn:length(alertas)}</span>
        <span class="etiqueta">En stock mínimo</span>
    </a>
    <c:if test="${sessionScope.usuario.admin}">
        <a class="tarjeta-dato" href="Reportes">
            <span class="valor"><fmt:formatNumber value="${valorizacion}" type="currency" currencySymbol="$"/></span>
            <span class="etiqueta">Valor del inventario</span>
        </a>
    </c:if>
</div>

<div class="acciones">
    <a class="boton" href="RegistrarMovimiento?tipo=ENTRADA">+ Registrar entrada</a>
    <a class="boton peligro" href="RegistrarMovimiento?tipo=SALIDA">− Registrar salida</a>
</div>

<h2>⚠ Productos en o bajo el stock mínimo</h2>
<c:choose>
    <c:when test="${empty alertas}">
        <p class="vacio">Todo en orden: ningún producto está en el mínimo.</p>
    </c:when>
    <c:otherwise>
        <table>
            <tr><th>Código</th><th>Producto</th><th>Stock</th><th>Mínimo</th><th></th></tr>
            <c:forEach var="p" items="${alertas}" end="4">
                <tr>
                    <td><c:out value="${p.codigo}"/></td>
                    <td><c:out value="${p.nombre}"/></td>
                    <td><span class="badge ${p.stockActual == 0 ? 'rojo' : 'naranja'}">${p.stockActual}</span></td>
                    <td>${p.stockMinimo}</td>
                    <td><a href="RegistrarMovimiento?tipo=ENTRADA&producto=${p.id}">Reponer</a></td>
                </tr>
            </c:forEach>
        </table>
        <c:if test="${fn:length(alertas) > 5}"><p><a href="Alertas">Ver todas (${fn:length(alertas)})</a></p></c:if>
    </c:otherwise>
</c:choose>

<h2>Últimos movimientos</h2>
<c:choose>
    <c:when test="${empty ultimos}">
        <p class="vacio">Todavía no hay movimientos.</p>
    </c:when>
    <c:otherwise>
        <table>
            <tr><th>Fecha</th><th>Tipo</th><th>Producto</th><th>Cant.</th><th>Motivo</th><th>Usuario</th></tr>
            <c:forEach var="m" items="${ultimos}">
                <tr class="${m.anulado ? 'anulado' : ''}">
                    <td>${m.fechaTexto}</td>
                    <td><span class="badge ${m.entrada ? 'verde' : 'rojo'}">${m.tipo}</span></td>
                    <td><c:out value="${m.producto.nombre}"/></td>
                    <td>${m.cantidad}</td>
                    <td>${m.motivo}</td>
                    <td><c:out value="${m.usuario.nombre}"/></td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<%@ include file="pie.jspf" %>
