<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Error · StockControl</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body class="pantalla-login">
<div class="login">
    <c:set var="codigo" value="${requestScope['jakarta.servlet.error.status_code']}"/>
    <c:choose>
        <c:when test="${codigo == 403}">
            <h1>Acceso denegado</h1>
            <p>Esta sección es solo para administradores.</p>
        </c:when>
        <c:when test="${codigo == 404}">
            <h1>Página no encontrada</h1>
            <p>La dirección que buscás no existe.</p>
        </c:when>
        <c:otherwise>
            <h1>Ocurrió un error</h1>
            <p><c:out value="${requestScope['jakarta.servlet.error.exception'].message}"
                      default="Algo salió mal al procesar la petición."/></p>
        </c:otherwise>
    </c:choose>
    <p><a class="boton" href="${pageContext.request.contextPath}/Inicio">Volver al panel</a></p>
</div>
</body>
</html>
