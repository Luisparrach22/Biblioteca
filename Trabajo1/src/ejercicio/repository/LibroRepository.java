package ejercicio.repository;

import java.util.List;
import ejercicio.model.Libro;
/**
 * Define las operaciones que se pueden realizar con los libros.
 *
 * @author Fabricio
 * @author Luis
 * @since 1.0
 */
public interface LibroRepository {
	  /**
     * Devuelve todos los libros.
     *
     * @return lista con todos los libros
     */
    List<Libro> obtenerTodos();
    /**
     * Busca libros por su título.
     *
     * @param titulo título que se quiere buscar
     * @return lista de libros que coinciden con el título
     */
    List<Libro> buscarPorTitulo(String titulo);
    /**
     * Busca libros por su autor.
     *
     * @param autor autor que se quiere buscar
     * @return lista de libros que coinciden con el autor
     */
    List<Libro> buscarPorAutor(String autor);
    /**
     * Busca libros dentro de un rango de precios.
     *
     * @param precioMin precio mínimo
     * @param precioMax precio máximo
     * @return lista de libros que están dentro del rango
     */
    List<Libro> buscarPorRangoPrecio(double precioMin, double precioMax);
    /**
     * Busca libros que tengan un stock mínimo.
     *
     * @param stockMinimo cantidad mínima de stock
     * @return lista de libros que cumplen el stock mínimo
     */
    List<Libro> buscarPorStockMinimo(int stockMinimo);
    /**
     * Inserta un libro en el repositorio.
     *
     * @param libro libro que se quiere insertar
     * @return true si se ha insertado correctamente, false en caso contrario
     */
    boolean insertar(Libro libro);
    /**
     * Elimina un libro por su título.
     *
     * @param titulo título del libro que se quiere eliminar
     * @return true si se ha eliminado correctamente, false en caso contrario
     */
    boolean eliminarPorTitulo(String titulo);
    /**
     * Elimina un libro por su id.
     *
     * @param id identificador del libro que se quiere eliminar
     * @return true si se ha eliminado correctamente, false en caso contrario
     */
    boolean eliminarPorId(String id);
    /**
     * Copia los libros de este repositorio en otro repositorio.
     *
     * @param destino repositorio donde se copiarán los libros
     * @return true si la copia se realiza correctamente, false en caso contrario
     */
    boolean copiar(LibroRepository destino);
}