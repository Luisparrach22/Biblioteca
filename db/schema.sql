CREATE DATABASE IF NOT EXISTS biblioteca;
USE biblioteca;

DROP TABLE IF EXISTS libros;
CREATE TABLE libros (
    id VARCHAR(50) PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    precio DOUBLE NOT NULL,
    stock INT NOT NULL
);

INSERT INTO libros (id, titulo, autor, precio, stock) VALUES
('L001', 'Don Quijote de la Mancha', 'Miguel de Cervantes', 19.95, 12),
('L002', 'Cien años de soledad', 'Gabriel García Márquez', 15.50, 8),
('L003', '1984', 'George Orwell', 12.99, 15),
('L004', 'El Principito', 'Antoine de Saint-Exupéry', 9.95, 20),
('L005', 'Fahrenheit 451', 'Ray Bradbury', 11.80, 5),
('L006', 'El Señor de los Anillos', 'J.R.R. Tolkien', 25.00, 10),
('L007', 'Orgullo y Prejuicio', 'Jane Austen', 10.50, 7);
