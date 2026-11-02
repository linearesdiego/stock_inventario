# StockControl

Sistema web de **control de inventario** para un comercio: productos, categorías, proveedores, entradas y salidas de mercadería, alertas de stock mínimo, reportes y usuarios con roles.

Proyecto integrador de **Desarrollo Backend III**, hecho con **Java 17 (Jakarta EE)**, **NetBeans**, **Maven**, **GlassFish** y **MySQL**. Tiene la misma estructura que `gestion-alumnos`: entidades JPA, Session Beans (`@Stateless`) como capa de acceso a datos, Servlets como controladores y JSP + JSTL como vistas.

---

## Tecnologías

| Tecnología | Para qué se usa |
|---|---|
| **Java 17** | Lenguaje |
| **Jakarta EE** (`jakarta.jakartaee-api`) | Servlets, JSP, JPA, EJB, CDI |
| **Servlets** | Controladores: reciben la petición, llaman a los facades y eligen la vista |
| **Filtro (`AuthFilter`)** | Protege las páginas: sin sesión → login; rol insuficiente → 403 |
| **JSP + JSTL** | Vistas sin código Java (`c:forEach`, `c:if`, `fmt:formatNumber`) |
| **JPA (EclipseLink)** | Mapeo objeto-relacional |
| **EJB `@Stateless`** | Reglas de negocio y transacciones manejadas por el contenedor (JTA) |
| **GlassFish** | Servidor donde se despliega el `.war` |
| **MySQL 8** | Base de datos `inventario_db` |
| **HTML + CSS propio** | `webapp/css/estilos.css`, sin frameworks |

---

## Arquitectura (MVC por capas)

```
 Navegador
    │
    ▼
 AuthFilter ──── sin sesión → /Login   ·   EMPLEADO en ruta de ADMIN → 403
    │
    ▼
 Servlets (Controlador)        com.stock.servlets
    │  @EJB
    ▼
 Session Beans / Facades       com.stock.sesiones     ← reglas de negocio + transacción
    │  JPA (EntityManager)
    ▼
 Entidades (Modelo)            com.stock.entidades
    │  JDBC (jdbc/dbinventario)
    ▼
 MySQL: inventario_db

 El Servlet deja los datos con request.setAttribute(...) y hace forward
 a una JSP de WEB-INF/vista/ (no se pueden abrir directo desde el navegador).
```

---

## Estructura del proyecto

```
stockcontrol/
├── pom.xml
├── sql/inventario_db.sql          ← crea la base, las tablas y datos de prueba
└── src/main/
    ├── java/com/stock/
    │   ├── entidades/             ← MODELO
    │   │   ├── Usuario.java, Rol.java (ADMIN / EMPLEADO)
    │   │   ├── Categoria.java, Proveedor.java, Producto.java
    │   │   └── Movimiento.java, TipoMovimiento.java (ENTRADA / SALIDA)
    │   ├── sesiones/              ← SESSION BEANS
    │   │   ├── AbstractFacade.java
    │   │   ├── UsuarioFacade.java      (login, alta con contraseña hasheada)
    │   │   ├── CategoriaFacade.java, ProveedorFacade.java
    │   │   ├── ProductoFacade.java     (código único, baja lógica, alertas, valorización)
    │   │   ├── MovimientoFacade.java   (registrar / anular + reporte más vendidos)
    │   │   └── InicializadorDatos.java (crea admin y empleado si la tabla usuario está vacía)
    │   ├── servlets/              ← CONTROLADORES
    │   │   ├── BaseServlet.java
    │   │   ├── LoginServlet.java, InicioServlet.java (panel y alertas)
    │   │   ├── ProductoServlet.java, CategoriaServlet.java, ProveedorServlet.java
    │   │   ├── MovimientoServlet.java, ReporteServlet.java, UsuarioServlet.java
    │   ├── filtros/AuthFilter.java
    │   └── util/
    │       ├── NegocioException.java   (@ApplicationException(rollback = true))
    │       ├── Seguridad.java          (hash PBKDF2 de contraseñas)
    │       └── Parametros.java         (lectura de parámetros de formularios)
    ├── resources/META-INF/persistence.xml   ← unidad StockControlPU → jdbc/dbinventario
    └── webapp/
        ├── index.jsp              ← redirige al panel
        ├── css/estilos.css
        └── WEB-INF/
            ├── web.xml, beans.xml, glassfish-web.xml
            └── vista/             ← JSP (cabecera.jspf y pie.jspf se incluyen en todas)
```

---

## Modelo de datos

```
categoria 1 ──── N producto N ──── 1 proveedor
                     │ 1
                     │
                     N
usuario 1 ─────── N movimiento
```

| Tabla | Columnas principales |
|---|---|
| `usuario` | `id`, `nombre`, `email` (único), `password_hash`, `rol`, `activo` |
| `categoria` | `id`, `nombre` (único) |
| `proveedor` | `id`, `razon_social`, `cuit` (único), `telefono`, `email` |
| `producto` | `id`, `codigo` (único), `nombre`, `categoria_id`, `proveedor_id`, `precio`, `stock_actual`, `stock_minimo`, `activo` |
| `movimiento` | `id`, `producto_id`, `usuario_id`, `tipo`, `cantidad`, `fecha`, `motivo`, `observacion`, `anulado` |

---

## Reglas de negocio

| # | Regla | Dónde está |
|---|---|---|
| 1 | No se puede sacar más de lo que hay | `MovimientoFacade.registrar()` lanza `NegocioException("Stock insuficiente…")` |
| 2 | Movimiento y stock van juntos | `registrar()` es un método de un `@Stateless`: el contenedor abre **una** transacción JTA; si algo falla hace rollback de todo. El producto se lee con `PESSIMISTIC_WRITE` (`SELECT … FOR UPDATE`) para que dos ventas simultáneas no dejen el stock negativo |
| 3 | Alerta de stock mínimo | `ProductoFacade.findBajoMinimo()` (`stockActual <= stockMinimo`), en el panel y en `/Alertas` |
| 4 | Código de producto único | `ProductoFacade.guardar()` + `UNIQUE` en la tabla |
| 5 | Baja lógica | `ProductoFacade.eliminar()`: si tiene movimientos queda `activo = false`; si nunca se movió se borra |

Además:
- El **stock no se edita a mano**: solo cambia con movimientos. El stock inicial de un producto nuevo se registra como una entrada.
- Un admin puede **anular** un movimiento cargado por error: queda tachado en el historial y el stock se revierte.
- Las contraseñas se guardan con **PBKDF2** (incluido en el JDK), nunca en texto plano.

---

## Roles y rutas

| Ruta | Qué hace | Empleado | Admin |
|---|---|:-:|:-:|
| `/Login`, `/Logout` | Inicio y cierre de sesión | ✔ | ✔ |
| `/Inicio` | Panel: alertas, últimos movimientos, valor del inventario (solo admin) | ✔ | ✔ |
| `/ListarProductos` | Productos con búsqueda y filtro por categoría | ✔ | ✔ |
| `/RegistrarMovimiento`, `/ListarMovimientos` | Entradas y salidas, historial por fechas | ✔ | ✔ |
| `/Alertas` | Productos en o bajo el mínimo | ✔ | ✔ |
| `/AgregarProducto`, `/EditarProducto`, `/EliminarProducto`, `/ReactivarProducto` | ABM de productos (precios) | ✘ | ✔ |
| `/ListarCategorias` … `/EliminarCategoria` | ABM de categorías | ✘ | ✔ |
| `/ListarProveedores` … `/EliminarProveedor` | ABM de proveedores | ✘ | ✔ |
| `/AnularMovimiento` | Anular un movimiento | ✘ | ✔ |
| `/Reportes` | Más vendidos y valorización | ✘ | ✔ |
| `/ListarUsuarios` … `/EliminarUsuario` | Alta de usuarios y roles | ✘ | ✔ |

Las acciones que modifican datos (eliminar, anular, reactivar) se hacen por **POST**, no con un link.

---

## Cómo ejecutarlo

### 1. Base de datos
Abrir `sql/inventario_db.sql` en MySQL Workbench y ejecutarlo completo. Crea `inventario_db` con datos de prueba.

Usuarios de prueba:

| Email | Contraseña | Rol |
|---|---|---|
| `admin@stock.com` | `admin123` | ADMIN |
| `empleado@stock.com` | `empleado123` | EMPLEADO |

### 2. GlassFish
Igual que en gestion-alumnos (el driver `mysql-connector-j` ya tiene que estar en `glassfish/domains/domain1/lib/`). En la consola `http://localhost:4848` → **Resources → JDBC**:

1. **JDBC Connection Pool**: nombre `inventarioPool`, tipo `javax.sql.DataSource`, driver `MySql`. Propiedades: `serverName=localhost`, `portNumber=3306`, `databaseName=inventario_db`, `user=…`, `password=…`, `useSSL=false`, `allowPublicKeyRetrieval=true`. Probar con **Ping**.
2. **JDBC Resource**: JNDI **`jdbc/dbinventario`** → pool `inventarioPool`.

O por consola:
```bash
asadmin create-jdbc-connection-pool --restype javax.sql.DataSource \
  --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource \
  --property "serverName=localhost:portNumber=3306:databaseName=inventario_db:user=root:password=TU_CLAVE:useSSL=false:allowPublicKeyRetrieval=true" \
  inventarioPool
asadmin create-jdbc-resource --connectionpoolid inventarioPool jdbc/dbinventario
```

### 3. NetBeans
**File → Open Project** → `stockcontrol` → **Clean and Build** → **Run**. Se abre `http://localhost:8080/stockcontrol/`.

---

## Problemas comunes

| Error | Solución |
|---|---|
| `Lookup failed for 'jdbc/dbinventario'` | Falta crear el recurso JDBC en GlassFish (paso 2) |
| No puedo entrar con admin | Volver a correr el script SQL. Si la tabla `usuario` está vacía, la app crea sola los usuarios de prueba al desplegarse |
| Los acentos se ven mal | Ejecutar el script completo (empieza con `SET NAMES utf8mb4`) |
| Cambios que no aparecen | **Clean and Build** y volver a **Run** |

---

## Autor

**Diego**: [github.com/linearesdiego](https://github.com/linearesdiego)
