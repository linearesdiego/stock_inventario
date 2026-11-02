-- =========================================================
--  StockControl - base de datos inventario_db (MySQL 8)
--  Ejecutar completo en MySQL Workbench (Ctrl+Shift+Enter)
-- =========================================================
SET NAMES utf8mb4;
DROP DATABASE IF EXISTS inventario_db;
CREATE DATABASE inventario_db CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE inventario_db;

CREATE TABLE usuario (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(80)  NOT NULL,
    email         VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    rol           VARCHAR(20)  NOT NULL,          -- ADMIN / EMPLEADO
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_usuario_rol CHECK (rol IN ('ADMIN', 'EMPLEADO'))
);

CREATE TABLE categoria (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE proveedor (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    razon_social VARCHAR(120) NOT NULL,
    cuit         VARCHAR(13)  UNIQUE,
    telefono     VARCHAR(30),
    email        VARCHAR(120)
);

CREATE TABLE producto (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    codigo       VARCHAR(30)    NOT NULL UNIQUE,   -- regla 4: código único
    nombre       VARCHAR(120)   NOT NULL,
    categoria_id INT,
    proveedor_id INT,
    precio       DECIMAL(12, 2) NOT NULL DEFAULT 0,
    stock_actual INT            NOT NULL DEFAULT 0,
    stock_minimo INT            NOT NULL DEFAULT 0,
    activo       BOOLEAN        NOT NULL DEFAULT TRUE,  -- regla 5: baja lógica
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT fk_producto_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor (id),
    CONSTRAINT chk_producto_stock CHECK (stock_actual >= 0 AND stock_minimo >= 0),
    CONSTRAINT chk_producto_precio CHECK (precio >= 0)
);

CREATE TABLE movimiento (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    producto_id INT          NOT NULL,
    usuario_id  INT          NOT NULL,
    tipo        VARCHAR(10)  NOT NULL,            -- ENTRADA / SALIDA
    cantidad    INT          NOT NULL,
    fecha       DATETIME     NOT NULL,
    motivo      VARCHAR(30)  NOT NULL,
    observacion VARCHAR(255),
    anulado     BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (producto_id) REFERENCES producto (id),
    CONSTRAINT fk_movimiento_usuario  FOREIGN KEY (usuario_id)  REFERENCES usuario (id),
    CONSTRAINT chk_movimiento_tipo CHECK (tipo IN ('ENTRADA', 'SALIDA')),
    CONSTRAINT chk_movimiento_cantidad CHECK (cantidad > 0)
);

CREATE INDEX idx_movimiento_fecha ON movimiento (fecha);

-- ---------------------------------------------------------
--  Datos de prueba
-- ---------------------------------------------------------
-- Contraseñas: admin123 y empleado123 (hash PBKDF2, igual que el que genera la app)
INSERT INTO usuario (nombre, email, password_hash, rol) VALUES
('Administrador', 'admin@stock.com',
 '65536:Xw8uMuly99dhKUYv+cNZPg==:po2GErVbIZMtWgU7+mcmTvaDyRI6MMQO8GEo3TDlcyo=', 'ADMIN'),
('Empleado', 'empleado@stock.com',
 '65536:3bEaMx6PISm7Toe6PQVgaA==:ls4oh+ubPpjymjEjhUDMf1DH59ZRzWFM5cmHVSflmes=', 'EMPLEADO');

INSERT INTO categoria (nombre) VALUES ('Bebidas'), ('Almacén'), ('Limpieza'), ('Lácteos');

INSERT INTO proveedor (razon_social, cuit, telefono, email) VALUES
('Distribuidora Norte SRL', '30-71234567-8', '381 455-1020', 'ventas@distnorte.com'),
('Mayorista El Sol SA',     '30-70987654-3', '381 421-7788', 'pedidos@elsol.com'),
('Limpiamax',               '20-28765432-1', '381 156-334455', 'limpiamax@gmail.com');

-- stock_actual = entradas - salidas de los movimientos de abajo
INSERT INTO producto (codigo, nombre, categoria_id, proveedor_id, precio, stock_actual, stock_minimo) VALUES
('BEB-001', 'Gaseosa cola 2,25 L',     1, 1, 2500.00,  6, 10),
('BEB-002', 'Agua mineral 1,5 L',      1, 1, 1200.00, 40, 12),
('ALM-001', 'Yerba mate 1 kg',         2, 2, 4800.00,  5,  8),
('ALM-002', 'Fideos spaghetti 500 g',  2, 2, 1500.00, 52, 15),
('ALM-003', 'Aceite de girasol 1,5 L', 2, 2, 3900.00,  6,  6),
('LIM-001', 'Lavandina 1 L',           3, 3,  900.00, 31, 10),
('LIM-002', 'Detergente 750 ml',       3, 3, 1700.00,  0,  8),
('LAC-001', 'Leche entera 1 L',        4, 1, 1400.00, 28, 20);

INSERT INTO movimiento (producto_id, usuario_id, tipo, cantidad, fecha, motivo, observacion) VALUES
(1, 1, 'ENTRADA', 48, NOW() - INTERVAL 20 DAY, 'COMPRA', 'Factura A 0001-00004512'),
(2, 1, 'ENTRADA', 60, NOW() - INTERVAL 20 DAY, 'COMPRA', 'Factura A 0001-00004512'),
(8, 1, 'ENTRADA', 72, NOW() - INTERVAL 19 DAY, 'COMPRA', 'Factura A 0001-00004530'),
(3, 1, 'ENTRADA', 30, NOW() - INTERVAL 18 DAY, 'COMPRA', 'Remito 0002-00001177'),
(4, 1, 'ENTRADA', 80, NOW() - INTERVAL 18 DAY, 'COMPRA', 'Remito 0002-00001177'),
(5, 1, 'ENTRADA', 20, NOW() - INTERVAL 18 DAY, 'COMPRA', 'Remito 0002-00001177'),
(6, 1, 'ENTRADA', 40, NOW() - INTERVAL 17 DAY, 'COMPRA', NULL),
(7, 1, 'ENTRADA', 24, NOW() - INTERVAL 17 DAY, 'COMPRA', NULL),
(1, 2, 'SALIDA',  30, NOW() - INTERVAL 12 DAY, 'VENTA', NULL),
(2, 2, 'SALIDA',  20, NOW() - INTERVAL 11 DAY, 'VENTA', NULL),
(3, 2, 'SALIDA',  18, NOW() - INTERVAL 10 DAY, 'VENTA', NULL),
(4, 2, 'SALIDA',  25, NOW() - INTERVAL 9 DAY,  'VENTA', NULL),
(4, 2, 'SALIDA',   3, NOW() - INTERVAL 8 DAY,  'ROTURA', 'Paquetes rotos en el depósito'),
(5, 2, 'SALIDA',  14, NOW() - INTERVAL 7 DAY,  'VENTA', NULL),
(8, 2, 'SALIDA',  40, NOW() - INTERVAL 6 DAY,  'VENTA', NULL),
(8, 2, 'SALIDA',   4, NOW() - INTERVAL 5 DAY,  'VENCIMIENTO', 'Lote vencido'),
(6, 2, 'SALIDA',   9, NOW() - INTERVAL 4 DAY,  'VENTA', NULL),
(7, 2, 'SALIDA',  24, NOW() - INTERVAL 3 DAY,  'VENTA', NULL),
(1, 2, 'SALIDA',  12, NOW() - INTERVAL 2 DAY,  'VENTA', NULL),
(3, 2, 'SALIDA',   7, NOW() - INTERVAL 1 DAY,  'VENTA', NULL);
