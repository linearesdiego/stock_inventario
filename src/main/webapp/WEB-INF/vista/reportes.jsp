<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Reportes"/>
<%@ include file="cabecera.jspf" %>

<h1>Reportes</h1>

<form class="filtro" action="Reportes" method="get">
    <label>Desde <input type="date" name="desde" value="${desde}"></label>
    <label>Hasta <input type="date" name="hasta" value="${hasta}"></label>
    <input type="submit" value="Actualizar">
    <a href="ListarMovimientos?desde=${desde}&hasta=${hasta}">Ver movimientos del período</a>
</form>

<h2>Productos más vendidos</h2>
<c:choose>
    <c:when test="${empty masVendidos}">
        <p class="vacio">No hubo ventas en el período.</p>
    </c:when>
    <c:otherwise>
        <c:set var="max" value="${masVendidos[0][2]}"/>
        <table>
            <tr><th>#</th><th>Código</th><th>Producto</th><th class="num">Unidades</th><th class="barra-col"></th></tr>
            <c:forEach var="fila" items="${masVendidos}" varStatus="st">
                <tr>
                    <td>${st.count}</td>
                    <td><c:out value="${fila[0]}"/></td>
                    <td><c:out value="${fila[1]}"/></td>
                    <td class="num">${fila[2]}</td>
                    <td class="barra-col"><div class="barra-dato" style="width:${fila[2] * 100 / max}%"></div></td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<h2>Valorización del inventario</h2>
<div class="tarjetas">
    <div class="tarjeta-dato">
        <span class="valor"><fmt:formatNumber value="${valorizacion}" type="currency" currencySymbol="$"/></span>
        <span class="etiqueta">Valor total (precio × stock actual)</span>
    </div>
</div>
<table>
    <tr><th>Código</th><th>Producto</th><th>Categoría</th><th class="num">Precio</th><th class="num">Stock</th><th class="num">Valorizado</th></tr>
    <c:forEach var="p" items="${productos}">
        <tr>
            <td><c:out value="${p.codigo}"/></td>
            <td><c:out value="${p.nombre}"/></td>
            <td><c:out value="${p.categoria.nombre}"/></td>
            <td class="num"><fmt:formatNumber value="${p.precio}" type="currency" currencySymbol="$"/></td>
            <td class="num">${p.stockActual}</td>
            <td class="num"><fmt:formatNumber value="${p.valorizado}" type="currency" currencySymbol="$"/></td>
        </tr>
    </c:forEach>
    <tr class="total">
        <td colspan="5">Total</td>
        <td class="num"><fmt:formatNumber value="${valorizacion}" type="currency" currencySymbol="$"/></td>
    </tr>
</table>

<%@ include file="pie.jspf" %>
