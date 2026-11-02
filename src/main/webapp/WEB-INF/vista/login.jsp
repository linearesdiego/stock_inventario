<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Ingresar · StockControl</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body class="pantalla-login">
<form class="login" action="Login" method="post">
    <h1>📦 StockControl</h1>
    <p class="subtitulo">Control de inventario</p>
    <c:if test="${not empty error}">
        <p class="aviso error"><c:out value="${error}"/></p>
    </c:if>
    <label>Email
        <input type="email" name="email" value="<c:out value='${email}'/>" required autofocus>
    </label>
    <label>Contraseña
        <input type="password" name="password" required>
    </label>
    <input type="submit" value="Ingresar">
</form>
</body>
</html>
