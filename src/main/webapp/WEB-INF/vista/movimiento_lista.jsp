<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Movimientos"/>
<%@ include file="cabecera.jspf" %>

<div class="encabezado">
    <h1>Movimientos</h1>
    <a class="boton" href="RegistrarMovimiento">+ Registrar movimiento</a>
</div>

<form class="filtro" action="ListarMovimientos" method="get">
    <label>Desde <input type="date" name="desde" value="${desde}"></label>
    <label>Hasta <input type="date" name="hasta" value="${hasta}"></label>
    <select name="producto">
        <option value="">Todos los productos</option>
        <c:forEach var="p" items="${productos}">
            <option value="${p.id}" ${param.producto == p.id ? 'selected' : ''}><c:out value="${p.nombre}"/></option>
        </c:forEach>
    </select>
    <input type="submit" value="Filtrar">
</form>

<div class="tarjetas">
    <div class="tarjeta-dato"><span class="valor">${fn:length(lista)}</span><span class="etiqueta">Movimientos</span></div>
    <div class="tarjeta-dato"><span class="valor verde">+${totalEntradas}</span><span class="etiqueta">Unidades que entraron</span></div>
    <div class="tarjeta-dato"><span class="valor rojo">−${totalSalidas}</span><span class="etiqueta">Unidades que salieron</span></div>
</div>

<c:choose>
    <c:when test="${empty lista}">
        <p class="vacio">No hay movimientos en ese período.</p>
    </c:when>
    <c:otherwise>
        <table>
            <tr>
                <th>Fecha</th><th>Tipo</th><th>Producto</th><th class="num">Cant.</th>
                <th>Motivo</th><th>Observación</th><th>Usuario</th>
                <c:if test="${sessionScope.usuario.admin}"><th></th></c:if>
            </tr>
            <c:forEach var="m" items="${lista}">
                <tr class="${m.anulado ? 'anulado' : ''}">
                    <td>${m.fechaTexto}</td>
                    <td><span class="badge ${m.entrada ? 'verde' : 'rojo'}">${m.tipo}</span></td>
                    <td><c:out value="${m.producto.codigo} - ${m.producto.nombre}"/></td>
                    <td class="num">${m.cantidad}</td>
                    <td>${m.motivo}</td>
                    <td><c:out value="${m.observacion}"/></td>
                    <td><c:out value="${m.usuario.nombre}"/></td>
                    <c:if test="${sessionScope.usuario.admin}">
                        <td>
                            <c:choose>
                                <c:when test="${m.anulado}"><span class="suave">Anulado</span></c:when>
                                <c:otherwise>
                                    <form class="en-linea" action="AnularMovimiento" method="post"
                                          onsubmit="return confirm('¿Anular este movimiento? Se revierte el stock.')">
                                        <input type="hidden" name="id" value="${m.id}">
                                        <button class="link peligro">Anular</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<%@ include file="pie.jspf" %>
