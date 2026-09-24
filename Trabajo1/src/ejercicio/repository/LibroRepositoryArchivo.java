package ejercicio.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ejercicio.model.Libro;

public class LibroRepositoryArchivo implements LibroRepository {

    private String rutaArchivo = "data/libros.txt";

    public LibroRepositoryArchivo() {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists() && new File("../data/libros.txt").exists()) {
            this.rutaArchivo = "../data/libros.txt";
        }
        asegurarDatosIniciales();
    }

    public LibroRepositoryArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        asegurarDatosIniciales();
    }

    private void asegurarDatosIniciales() {
        File archivo = new File(rutaArchivo);
        File directorio = archivo.getParentFile();
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

        int copiados = 0;
        for (Libro l : misLibros) {
            if (destino.insertar(l)) {
                copiados++;
            }
        }

        System.out.println("Se copiaron " + copiados + " libros al repositorio destino.");
        return copiados > 0;
    }
}
