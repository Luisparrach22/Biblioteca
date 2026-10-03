package ejercicio.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ejercicio.model.Libro;

/**
 * Clase que gestiona la lectura y escritura de libros en un archivo de texto (.txt).
 * Implementa la interfaz LibroRepository.
 * 
 * @author Luis Parra
 * @version 1.0
 */
public class LibroRepositoryArchivo implements LibroRepository {

    private String rutaArchivo = "data/libros.txt";

    /**
     * Constructor por defecto. Comprueba la ruta del archivo de libros.
     */
    public LibroRepositoryArchivo() {
        File archivoPredeterminado = new File(rutaArchivo);
        File archivoAlternativo = new File("../data/libros.txt");

        if (!archivoPredeterminado.exists() && archivoAlternativo.exists()) {
            rutaArchivo = archivoAlternativo.getPath();
        }

        asegurarDatosIniciales();
    }

    /**
     * Constructor con ruta personalizada para el archivo de libros.
     * 
     * @param rutaArchivo Ruta del archivo de texto
     */
    public LibroRepositoryArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        asegurarDatosIniciales();
    }

    /**
     * Crea las carpetas necesarias si la ruta del archivo no existe.
     */
    private void asegurarDatosIniciales() {
        File directorio = new File(rutaArchivo).getParentFile();
        if (directorio != null && !directorio.exists()) {
            directorio.mkdirs();
        }
    }

    /**
     * Lee todos los libros guardados en el archivo de texto.
     * 
     * @return Lista con los libros cargados desde el archivo
     */
    private List<Libro> cargarLibros() {
        List<Libro> lista = new ArrayList<>();
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            return lista;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                Libro libro = Libro.fromCSV(linea);
                if (libro != null) {
                    lista.add(libro);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al leer el archivo de libros (" + rutaArchivo + "): " + e.getMessage());
        }

        return lista;
    }

    /**
     * Escribe la lista completa de libros en el archivo de texto.
     * 
     * @param lista Lista de libros a guardar
     * @return true si se guardó correctamente, false en caso contrario
     */
    private boolean guardarLibros(List<Libro> lista) {
        File archivo = new File(rutaArchivo);
        File directorio = archivo.getParentFile();
        if (directorio != null && !directorio.exists()) {
            directorio.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
            writer.println("# Archivo de persistencia de libros");
            writer.println("# Formato por línea: id^titulo^autor^precio^stock");

            for (Libro libro : lista) {
                writer.println(libro.toCSV());
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar el archivo de libros (" + rutaArchivo + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene la lista completa de libros leyendo del archivo.
     * 
     * @return Lista de libros
     */
    @Override
    public List<Libro> obtenerTodos() {
        return cargarLibros();
    }

    /**
     * Busca libros cuyo título contenga el texto indicado.
     * 
     * @param titulo Texto a buscar en el título del libro
     * @return Lista de libros encontrados
     */
    @Override
    public List<Libro> buscarPorTitulo(String titulo) {
        List<Libro> lista = cargarLibros();
        List<Libro> resultado = new ArrayList<>();
        if (titulo == null) return resultado;

        String filtro = titulo.trim().toLowerCase();
        for (Libro libro : lista) {
            if (libro.getTitulo().toLowerCase().contains(filtro)) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    /**
     * Busca libros cuyo autor contenga el texto indicado.
     * 
     * @param autor Nombre del autor a buscar
     * @return Lista de libros que coinciden con el autor
     */
    @Override
    public List<Libro> buscarPorAutor(String autor) {
        List<Libro> lista = cargarLibros();
        List<Libro> resultado = new ArrayList<>();
        if (autor == null) return resultado;

        String filtro = autor.trim().toLowerCase();
        for (Libro libro : lista) {
            if (libro.getAutor().toLowerCase().contains(filtro)) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    /**
     * Busca libros con un precio comprendido entre un mínimo y un máximo.
     * 
     * @param precioMin Precio mínimo
     * @param precioMax Precio máximo
     * @return Lista de libros en ese rango de precio
     */
    @Override
    public List<Libro> buscarPorRangoPrecio(double precioMin, double precioMax) {
        List<Libro> lista = cargarLibros();
        List<Libro> resultado = new ArrayList<>();

        for (Libro libro : lista) {
            if (libro.getPrecio() >= precioMin && libro.getPrecio() <= precioMax) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    /**
     * Busca libros que tengan una cantidad de stock igual o superior a la indicada.
     * 
     * @param stockMinimo Cantidad mínima de stock
     * @return Lista de libros con stock suficiente
     */
    @Override
    public List<Libro> buscarPorStockMinimo(int stockMinimo) {
        List<Libro> lista = cargarLibros();
        List<Libro> resultado = new ArrayList<>();

        for (Libro libro : lista) {
            if (libro.getStock() >= stockMinimo) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    /**
     * Inserta un nuevo libro en el archivo si su ID no existe previamente.
     * 
     * @param libro Libro que se quiere insertar
     * @return true si se insertó con éxito, false si ya existía o hubo un error
     */
    @Override
    public boolean insertar(Libro libro) {
        if (libro == null) return false;
        List<Libro> lista = cargarLibros();

        for (Libro l : lista) {
            if (l.getId().equalsIgnoreCase(libro.getId())) {
                System.out.println("El libro con ID '" + libro.getId() + "' ya existe.");
                return false;
            }
        }

        lista.add(libro);
        return guardarLibros(lista);
    }

    /**
     * Elimina un libro del archivo según su título.
     * 
     * @param titulo Título del libro a borrar
     * @return true si se borró con éxito, false si no se encontró o falló
     */
    @Override
    public boolean eliminarPorTitulo(String titulo) {
        if (titulo == null) return false;
        List<Libro> lista = cargarLibros();
        List<Libro> nuevaLista = new ArrayList<>();
        boolean encontrado = false;

        for (Libro l : lista) {
            if (l.getTitulo().equalsIgnoreCase(titulo.trim())) {
                encontrado = true;
            } else {
                nuevaLista.add(l);
            }
        }

        if (encontrado) {
            return guardarLibros(nuevaLista);
        }
        return false;
    }

    /**
     * Elimina un libro del archivo buscando por su ID único.
     * 
     * @param id Identificador del libro a borrar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    @Override
    public boolean eliminarPorId(String id) {
        if (id == null) return false;
        List<Libro> lista = cargarLibros();
        List<Libro> nuevaLista = new ArrayList<>();
        boolean encontrado = false;

        for (Libro l : lista) {
            if (l.getId().equalsIgnoreCase(id.trim())) {
                encontrado = true;
            } else {
                nuevaLista.add(l);
            }
        }

        if (encontrado) {
            return guardarLibros(nuevaLista);
        }
        return false;
    }

    /**
     * Copia todos los libros de este archivo hacia otro repositorio de destino.
     * 
     * @param destino Repositorio de destino donde se copiarán los libros
     * @return true si la copia se completó correctamente
     */
    @Override
    public boolean copiar(LibroRepository destino) {
        if (destino == null) return false;
        List<Libro> misLibros = cargarLibros();

        if (misLibros.isEmpty()) {
            System.out.println("No hay libros en el archivo para copiar.");
            return false;
        }

        List<Libro> existentes = destino.obtenerTodos();
        int copiados = 0;
        int yaExisten = 0;

        for (Libro l : misLibros) {
            if (contieneId(existentes, l.getId())) {
                yaExisten++;
            } else if (destino.insertar(l)) {
                existentes.add(l);
                copiados++;
            }
        }

        System.out.println("Copia realizada: " + copiados + " insertados, " + yaExisten + " ya existian.");
        return copiados + yaExisten == misLibros.size();
    }

    /**
     * Comprueba si un ID de libro ya existe en una lista.
     * 
     * @param lista Lista de libros donde buscar
     * @param id ID del libro a comprobar
     * @return true si el ID ya existe en la lista, false si no
     */
    private boolean contieneId(List<Libro> lista, String id) {
        for (Libro libro : lista) {
            if (libro.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
}
