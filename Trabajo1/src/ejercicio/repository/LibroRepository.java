package ejercicio.repository;


import java.util.List;

import ejercicio.model.Libro;

public interface LibroRepository {

    List<Libro> obtenerTodos();

    List<Libro> buscarPorTitulo(String titulo);

    List<Libro> buscarPorAutor(String autor);

    List<Libro> buscarPorRangoPrecio(double precioMin, double precioMax);

    List<Libro> buscarPorStockMinimo(int stockMinimo);

    boolean insertar(Libro libro);

    boolean eliminarPorTitulo(String titulo);

    boolean copiar(LibroRepository destino);
}