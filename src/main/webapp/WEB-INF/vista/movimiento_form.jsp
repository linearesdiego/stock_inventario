<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Registrar movimiento"/>
<%@ include file="cabecera.jspf" %>

<h1>Registrar movimiento</h1>
<p class="subtitulo">Las entradas suman stock; las salidas lo descuentan. No se puede sacar más de lo que hay.</p>

<c:set var="tipoSel" value="${empty param.tipo ? 'ENTRADA' : param.tipo}"/>

<form class="formulario" action="RegistrarMovimiento" method="post">
    <label>Producto *
        <select name="producto" required>
            <option value="">Elegí un producto…</option>
            <c:forEach var="p" items="${productos}">
                <option value="${p.id}" ${param.producto == p.id ? "selected" : ""}>
                    <c:out value="${p.codigo} - ${p.nombre}"/> (stock: ${p.stockActual})
                </option>
            </c:forEach>
        </select>
    </label>

    <fieldset class="tipo">
        <legend>Tipo *</legend>
        <label class="opcion entrada">
            <input type="radio" name="tipo" value="ENTRADA" ${tipoSel == 'ENTRADA' ? 'checked' : ''}
                   onchange="cambiarMotivos()"> Entrada
        </label>
        <label class="opcion salida">
            <input type="radio" name="tipo" value="SALIDA" ${tipoSel == 'SALIDA' ? 'checked' : ''}
                   onchange="cambiarMotivos()"> Salida
        </label>
    </fieldset>

    <label>Cantidad *
        <input type="number" name="cantidad" value="<c:out value='${param.cantidad}'/>" min="1" required>
    </label>

    <label>Motivo *
        <select name="motivo" id="motivo" required>
            <optgroup label="Entrada" id="motivosEntrada">
                <option value="COMPRA">Compra a proveedor</option>
                <option value="DEVOLUCION">Devolución de cliente</option>
                <option value="AJUSTE">Ajuste de inventario</option>
            </optgroup>
            <optgroup label="Salida" id="motivosSalida">
                <option value="VENTA">Venta</option>
                <option value="ROTURA">Rotura</option>
                <option value="VENCIMIENTO">Vencimiento</option>
                <option value="AJUSTE">Ajuste de inventario</option>
            </optgroup>
        </select>
    </label>

    <label>Observación
        <input type="text" name="observacion" value="<c:out value='${param.observacion}'/>" maxlength="255"
               placeholder="Ej: factura 0001-00012345">
    </label>

    <div class="botones">
        <input type="submit" value="Registrar">
        <a href="ListarMovimientos">Ver movimientos</a>
    </div>
</form>

<script>
    // Muestra solo los motivos que corresponden al tipo elegido
    function cambiarMotivos() {
        var salida = document.querySelector('input[name=tipo]:checked').value === 'SALIDA';
        var ent = document.getElementById('motivosEntrada');
        var sal = document.getElementById('motivosSalida');
        ent.disabled = salida; ent.hidden = salida;
        sal.disabled = !salida; sal.hidden = !salida;
        var sel = document.getElementById('motivo');
        var grupo = salida ? sal : ent;
        var actual = '${fn:escapeXml(param.motivo)}';
        sel.value = '';
        for (var i = 0; i < grupo.children.length; i++) {
            if (grupo.children[i].value === actual) { grupo.children[i].selected = true; return; }
        }
        grupo.children[0].selected = true;
    }
    cambiarMotivos();
</script>

<%@ include file="pie.jspf" %>
