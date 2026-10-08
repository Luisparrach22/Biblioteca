

DROP DATABASE IF EXISTS libreria_serena;
CREATE DATABASE libreria_serena CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE libreria_serena;

-- 1. TABLAS PRINCIPALES (SIN DEPENDENCIAS)

-- Tiendas físicas
CREATE TABLE tienda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    direccion VARCHAR(150) NOT NULL,
    ciudad VARCHAR(50) NOT NULL,
    telefono VARCHAR(15) NOT NULL
);

 -- Editoriales
CREATE TABLE editorial (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    pais VARCHAR(50) NOT NULL,
    telefono_contacto VARCHAR(15) NOT NULL
);

-- Autores
CREATE TABLE autor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    nacionalidad VARCHAR(50) NOT NULL,
    anio_nacimiento INT
);

--  Clientes (Socios y habituales)
CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(15),
    es_socio BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_alta DATE NOT NULL
);


--  2. TABLAS CON DEPENDENCIAS DE PRIMER NIVEL

--  Catálogo de libros
CREATE TABLE libro (
    isbn CHAR(13) PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    anio_publicacion INT NOT NULL,
    num_paginas INT NOT NULL CHECK (num_paginas > 0),
    precio_catalogo DECIMAL(6, 2) NOT NULL CHECK (precio_catalogo >= 0),
    editorial_id INT NOT NULL,
    FOREIGN KEY (editorial_id) REFERENCES editorial(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

--  Empleados por tienda
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


--  3. TABLAS INTERMEDIAS Y TRANSACCIONALES


--  Relación Libros - Autores (N:M con rol)
CREATE TABLE libro_autor (
    libro_isbn CHAR(13) NOT NULL,
    autor_id INT NOT NULL,
    rol ENUM('principal', 'colaborador') NOT NULL DEFAULT 'principal',
    PRIMARY KEY (libro_isbn, autor_id),
    FOREIGN KEY (libro_isbn) REFERENCES libro(isbn) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (autor_id) REFERENCES autor(id) ON UPDATE CASCADE ON DELETE CASCADE
);

--  Inventario / Stock de libros por tienda (N:M con fecha de recuento)
CREATE TABLE inventario (
    tienda_id INT NOT NULL,
    libro_isbn CHAR(13) NOT NULL,
    stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    fecha_ultimo_recuento DATE NOT NULL,
    PRIMARY KEY (tienda_id, libro_isbn),
    FOREIGN KEY (tienda_id) REFERENCES tienda(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (libro_isbn) REFERENCES libro(isbn) ON UPDATE CASCADE ON DELETE CASCADE
);

--  Pedidos / Ventas realizadas
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

--  Líneas de detalle del pedido (guarda precio histórico cobrado)
CREATE TABLE linea_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT NOT NULL,
    libro_isbn CHAR(13) NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_cobrado DECIMAL(6, 2) NOT NULL CHECK (precio_cobrado >= 0),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (libro_isbn) REFERENCES libro(isbn) ON UPDATE CASCADE ON DELETE RESTRICT
);

