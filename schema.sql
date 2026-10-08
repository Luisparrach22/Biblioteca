
 Base de Datos: Páginas de Villa Serena
Trabajo: Nº1 - Documentación del sistema (Fase 3)


DROP DATABASE IF EXISTS libreria_serena;
CREATE DATABASE libreria_serena CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE libreria_serena;

--1. TABLAS PRINCIPALES (SIN DEPENDENCIAS)

--Tiendas físicas
CREATE TABLE tienda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    direccion VARCHAR(150) NOT NULL,
    ciudad VARCHAR(50) NOT NULL,
    telefono VARCHAR(15) NOT NULL
);

 --Editoriales
CREATE TABLE editorial (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    pais VARCHAR(50) NOT NULL,
    telefono_contacto VARCHAR(15) NOT NULL
);

--Autores
CREATE TABLE autor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    nacionalidad VARCHAR(50) NOT NULL,
    anio_nacimiento INT
);

-- Clientes (Socios y habituales)
CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(15),
    es_socio BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_alta DATE NOT NULL
);


-- 2. TABLAS CON DEPENDENCIAS DE PRIMER NIVEL

-- Catálogo de libros
CREATE TABLE libro (
    isbn CHAR(13) PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    anio_publicacion INT NOT NULL,
    num_paginas INT NOT NULL CHECK (num_paginas > 0),
    precio_catalogo DECIMAL(6, 2) NOT NULL CHECK (precio_catalogo >= 0),
    editorial_id INT NOT NULL,
    FOREIGN KEY (editorial_id) REFERENCES editorial(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- Empleados por tienda
CREATE TABLE empleado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dni CHAR(9) NOT NULL UNIQUE,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    cargo ENUM('librero', 'cajero', 'encargado') NOT NULL,
    fecha_contratacion DATE NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    tienda_id INT NOT NULL,
    FOREIGN KEY (tienda_id) REFERENCES tienda(id) ON UPDATE CASCADE ON DELETE RESTRICT
);


-- 3. TABLAS INTERMEDIAS Y TRANSACCIONALES


-- Relación Libros - Autores (N:M con rol)
CREATE TABLE libro_autor (
    libro_isbn CHAR(13) NOT NULL,
    autor_id INT NOT NULL,
    rol ENUM('principal', 'colaborador') NOT NULL DEFAULT 'principal',
    PRIMARY KEY (libro_isbn, autor_id),
    FOREIGN KEY (libro_isbn) REFERENCES libro(isbn) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (autor_id) REFERENCES autor(id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Inventario / Stock de libros por tienda (N:M con fecha de recuento)
CREATE TABLE inventario (
    tienda_id INT NOT NULL,
    libro_isbn CHAR(13) NOT NULL,
    stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    fecha_ultimo_recuento DATE NOT NULL,
    PRIMARY KEY (tienda_id, libro_isbn),
    FOREIGN KEY (tienda_id) REFERENCES tienda(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (libro_isbn) REFERENCES libro(isbn) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Pedidos / Ventas realizadas
CREATE TABLE pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_pedido VARCHAR(20) NOT NULL UNIQUE,
    fecha DATETIME NOT NULL,
    tienda_id INT NOT NULL,
    empleado_id INT NOT NULL,
    cliente_id INT NOT NULL,
    forma_pago ENUM('efectivo', 'tarjeta', 'bizum') NOT NULL,
    estado ENUM('preparado', 'entregado', 'cancelado') NOT NULL DEFAULT 'preparado',
    FOREIGN KEY (tienda_id) REFERENCES tienda(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (empleado_id) REFERENCES empleado(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- Líneas de detalle del pedido (guarda precio histórico cobrado)
CREATE TABLE linea_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT NOT NULL,
    libro_isbn CHAR(13) NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_cobrado DECIMAL(6, 2) NOT NULL CHECK (precio_cobrado >= 0),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (libro_isbn) REFERENCES libro(isbn) ON UPDATE CASCADE ON DELETE RESTRICT
);


-- 4. DATOS DE PRUEBA (POBLACIÓN DE LA BD)


-- Tiendas
INSERT INTO tienda (id, nombre, direccion, ciudad, telefono) VALUES
(1, 'Centro', 'Calle Mayor 14', 'Villa Serena', '976000111'),
(2, 'Ribera', 'Avenida del Río 5', 'Aldeaverde', '976000222'),
(3, 'Universidad', 'Plaza de las Ciencias 2', 'Villa Serena', '976000333');

-- Editoriales
INSERT INTO editorial (id, nombre, pais, telefono_contacto) VALUES
(1, 'Alfaguara', 'España', '915001001'),
(2, 'Alianza', 'España', '915002002'),
(3, 'Plaza & Janés', 'España', '934003003'),
(4, 'Fondo de Cultura Económica', 'México', '555004004');

-- Autores
INSERT INTO autor (id, nombre, nacionalidad, anio_nacimiento) VALUES
(1, 'Julio Cortázar', 'Argentina', 1914),
(2, 'Jorge Luis Borges', 'Argentina', 1899),
(3, 'Isabel Allende', 'Chile', 1942),
(4, 'Juan Rulfo', 'México', 1917),
(5, 'Gabriel García Márquez', 'Colombia', 1927);

-- Libros
INSERT INTO libro (isbn, titulo, anio_publicacion, num_paginas, precio_catalogo, editorial_id) VALUES
('9788420412146', 'Rayuela', 1963, 600, 16.50, 1),
('9788420658872', 'Ficciones', 1944, 224, 12.00, 2),
('9788401343056', 'Cuentos de Eva Luna', 1989, 320, 14.90, 3),
('9788420689999', 'Antología del cuento', 1980, 410, 18.00, 2),
('9788437604947', 'Pedro Páramo', 1955, 130, 10.00, 4),
('9788497592208', 'Cien años de soledad', 1967, 496, 15.00, 1);

-- Libro - Autor (con autores principales y colaboradores)
INSERT INTO libro_autor (libro_isbn, autor_id, rol) VALUES
('9788420412146', 1, 'principal'),
('9788420658872', 2, 'principal'),
('9788401343056', 3, 'principal'),
('9788420689999', 1, 'principal'),
('9788420689999', 2, 'colaborador'),
('9788437604947', 4, 'principal'),
('9788497592208', 5, 'principal');

-- Empleados
INSERT INTO empleado (id, dni, nombre, apellidos, cargo, fecha_contratacion, email, tienda_id) VALUES
(1, '12345678A', 'Marta', 'López Gómez', 'cajera', '2023-01-15', 'marta.lopez@villasere.es', 1),
(2, '23456789B', 'Carlos', 'García Ruiz', 'encargado', '2021-06-01', 'carlos.garcia@villasere.es', 1),
(3, '34567890C', 'Lucía', 'Fernández Sanz', 'librero', '2024-03-10', 'lucia.fernandez@villasere.es', 2),
(4, '45678901D', 'Javier', 'Martín Soler', 'librero', '2022-09-20', 'javier.martin@villasere.es', 3);

-- Clientes
INSERT INTO cliente (id, nombre_completo, email, telefono, es_socio, fecha_alta) VALUES
(1, 'Andrés Pérez', 'andres.p@correo.es', '600111222', TRUE, '2025-02-10'),
(2, 'Beatriz Morales', 'beatriz.m@correo.es', '600333444', TRUE, '2025-05-18'),
(3, 'David Navarro', 'david.n@correo.es', NULL, FALSE, '2026-01-12'),
(4, 'Elena Domínguez', 'elena.d@correo.es', '600555666', TRUE, '2024-11-04');

-- Inventario de libros por tienda
INSERT INTO inventario (tienda_id, libro_isbn, stock, fecha_ultimo_recuento) VALUES
-- Tienda 1 (Centro)
(1, '9788420412146', 5, '2026-03-01'),
(1, '9788420658872', 3, '2026-03-01'),
(1, '9788437604947', 2, '2026-03-01'),
(1, '9788497592208', 0, '2026-03-01'), -- Agotado en Centro
-- Tienda 2 (Ribera)
(2, '9788420412146', 2, '2026-02-28'),
(2, '9788401343056', 4, '2026-02-28'),
-- Tienda 3 (Universidad - Hoja de Carmen §8)
(3, '9788420412146', 4, '2026-03-02'),
(3, '9788420658872', 2, '2026-03-02'),
(3, '9788401343056', 0, '2026-03-02'),
(3, '9788420689999', 6, '2026-02-28'),
(3, '9788497592208', 3, '2026-03-02'); -- Disponible en Universidad

-- Pedidos
INSERT INTO pedido (id, numero_pedido, fecha, tienda_id, empleado_id, cliente_id, forma_pago, estado) VALUES
(1, 'PED-10482', '2026-03-12 11:30:00', 1, 1, 1, 'tarjeta', 'entregado'),
(2, 'PED-10483', '2026-03-12 12:45:00', 1, 1, 2, 'bizum', 'entregado'),
(3, 'PED-20101', '2026-03-13 17:15:00', 2, 3, 1, 'efectivo', 'entregado'),
(4, 'PED-30055', '2026-03-14 10:00:00', 3, 4, 3, 'tarjeta', 'entregado'),
(5, 'PED-10484', '2026-03-15 18:20:00', 1, 2, 1, 'tarjeta', 'entregado');

-- Líneas de pedido (Ticket real §6 y otros)
INSERT INTO linea_pedido (pedido_id, libro_isbn, cantidad, precio_cobrado) VALUES
-- Pedido 1 (Ticket nº 10482 del caso §6: Rayuela x1, Ficciones x2, Pedro Páramo x1)
(1, '9788420412146', 1, 16.50),
(1, '9788420658872', 2, 12.00),
(1, '9788437604947', 1, 10.00),
-- Pedido 2
(2, '9788401343056', 1, 14.90),
-- Pedido 3
(3, '9788420689999', 1, 18.00),
-- Pedido 4
(4, '9788420412146', 2, 16.50),
(4, '9788497592208', 1, 15.00),
-- Pedido 5
(5, '9788420658872', 1, 12.00);
