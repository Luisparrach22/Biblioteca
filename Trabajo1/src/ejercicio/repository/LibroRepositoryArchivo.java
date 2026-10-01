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
 * Repositorio de acceso a datos para la entidad Libro utilizando un archivo de texto como persistencia.
 * 
 * @author Luis Parra
 * @version 1.0
 */
public class LibroRepositoryArchivo implements LibroRepository {

    private String rutaArchivo = "data/libros.txt";

    public LibroRepositoryArchivo() {
        File archivoPredeterminado = new File(rutaArchivo);
        File archivoAlternativo = new File("../data/libros.txt");

        if (!archivoPredeterminado.exists() && archivoAlternativo.exists()) {
            rutaArchivo = archivoAlternativo.getPath();
        }

        asegurarDatosIniciales();
    }

    public LibroRepositoryArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        asegurarDatosIniciales();
    }

    private void asegurarDatosIniciales() {
        File directorio = new File(rutaArchivo).getParentFile();
        if (directorio != null && !directorio.exists()) {
            directorio.mkdirs();
        }
    }

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

    @Override
    public List<Libro> obtenerTodos() {
        return cargarLibros();
    }

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

    private boolean contieneId(List<Libro> lista, String id) {
        for (Libro libro : lista) {
            if (libro.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
}
