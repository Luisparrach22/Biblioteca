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
        if (!archivo.exists()) {
            this.rutaArchivo = "../data/libros.txt";
        }
    }

    private List<Libro> cargarLibros() {
        List<Libro> lista = new ArrayList<>();
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            return lista;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            String linea;

            while ((linea = reader.readLine()) != null) {
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }

                String[] partes = linea.split("\\^");
                if (partes.length == 5) {
                    String id = partes[0];
                    String titulo = partes[1];
                    String autor = partes[2];
                    double precio = Double.parseDouble(partes[3]);
                    int stock = Integer.parseInt(partes[4]);

                    Libro libro = new Libro(id, titulo, autor, precio, stock);
                    lista.add(libro);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return lista;
    }

    private boolean guardarLibros(List<Libro> lista) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo));
            writer.println("# Archivo de libros");
            writer.println("# Formato: id^titulo^autor^precio^stock");

            for (Libro libro : lista) {
                writer.println(libro.getId() + "^" + libro.getTitulo() + "^" + libro.getAutor() + "^" + libro.getPrecio() + "^" + libro.getStock());
            }

            writer.close();
            return true;
        } catch (Exception e) {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Libro> obtenerTodos() {
        return cargarLibros();
    }

    @Override
    public List<Libro> buscarPorTitulo(String titulo) {
        return new ArrayList<>();
    }

    @Override
    public List<Libro> buscarPorAutor(String autor) {
        return new ArrayList<>();
    }

    @Override
    public List<Libro> buscarPorRangoPrecio(double precioMin, double precioMax) {
        return new ArrayList<>();
    }

    @Override
    public List<Libro> buscarPorStockMinimo(int stockMinimo) {
        return new ArrayList<>();
    }

    @Override
    public boolean insertar(Libro libro) {
        return false;
    }

    @Override
    public boolean eliminarPorTitulo(String titulo) {
        return false;
    }

    @Override
    public boolean copiar(LibroRepository destino) {
        return false;
    }
}
