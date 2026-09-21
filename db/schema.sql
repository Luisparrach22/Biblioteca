-- Script de creación de la base de datos y tabla para el Sistema de Gestión de Biblioteca

CREATE DATABASE IF NOT EXISTS biblioteca;
USE biblioteca;

-- Tabla de libros
DROP TABLE IF EXISTS libros;
CREATE TABLE libros (
    id VARCHAR(50) PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    precio DOUBLE NOT NULL,
    stock INT NOT NULL
);

