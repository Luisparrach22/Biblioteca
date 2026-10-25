-- =====================================================================
-- init.sql - Base de datos de la aplicación Biblioteca
--
-- AVISO: este script BORRA la tabla "libros" y todos sus datos y la
-- vuelve a crear con los libros de ejemplo. Se puede ejecutar varias
-- veces seguidas sin error.
--
-- Uso (cmd de Windows, desde la raíz del proyecto):
--   mysql -u root -p < sql\init.sql
-- =====================================================================

-- El archivo está en UTF-8: así las tildes y la ñ se guardan bien
-- aunque la consola de Windows use otra codificación.
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS biblioteca
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE biblioteca;

DROP TABLE IF EXISTS libros;

-- Mismas columnas y tipos que usa LibroRepositoryMySQL y db/schema.sql
CREATE TABLE libros (
    id VARCHAR(50) PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    precio DOUBLE NOT NULL,
    stock INT NOT NULL
) DEFAULT CHARSET = utf8mb4;

-- L001-L007 son los mismos libros que data/libros.txt
INSERT INTO libros (id, titulo, autor, precio, stock) VALUES
('L001', 'Don Quijote de la Mancha', 'Miguel de Cervantes', 19.95, 12),
('L002', 'Cien años de soledad', 'Gabriel García Márquez', 15.50, 8),
('L003', '1984', 'George Orwell', 12.99, 15),
('L004', 'El Principito', 'Antoine de Saint-Exupéry', 9.95, 20),
('L005', 'Fahrenheit 451', 'Ray Bradbury', 11.80, 5),
('L006', 'El Señor de los Anillos', 'J.R.R. Tolkien', 25.00, 10),
('L007', 'Orgullo y Prejuicio', 'Jane Austen', 10.50, 7),
('L008', 'El amor en los tiempos del cólera', 'Gabriel García Márquez', 18.90, 6),
('L009', 'Crónica de una muerte anunciada', 'Gabriel García Márquez', 8.75, 14),
('L010', 'Novelas ejemplares', 'Miguel de Cervantes', 13.20, 3),
('L011', 'La sombra del viento', 'Carlos Ruiz Zafón', 22.45, 9),
('L012', 'Rebelión en la granja', 'George Orwell', 7.60, 18);
