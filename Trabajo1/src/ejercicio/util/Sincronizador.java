package ejercicio.util;

import java.util.List;

import ejercicio.model.Libro;
import ejercicio.repository.LibroRepository;
import ejercicio.repository.LibroRepositoryArchivo;
import ejercicio.repository.LibroRepositoryMySQL;

public class Sincronizador {

    private LibroRepository archivo;
    private LibroRepository mysql;

    public Sincronizador() {
        this.archivo = new LibroRepositoryArchivo();
        this.mysql = new LibroRepositoryMySQL();
    }

    public void sincronizar() {
        System.out.println("Sincronizando archivo <-> MySQL...");

        List<Libro> librosArchivo = archivo.obtenerTodos();
        List<Libro> librosMySQL = mysql.obtenerTodos();

        int sincronizados = 0;

        for (Libro libro : librosArchivo) {
            if (!contieneLibro(librosMySQL, libro.getId())) {
                if (mysql.insertar(libro)) {
                    sincronizados++;
                }
            }
        }

        for (Libro libro : librosMySQL) {
            if (!contieneLibro(librosArchivo, libro.getId())) {
                if (archivo.insertar(libro)) {
                    sincronizados++;
                }
            }
        }

        if (sincronizados > 0) {
            System.out.println("Sincronizados " + sincronizados + " libros.");
        } else {
            System.out.println("Ambas fuentes ya estan sincronizados.");
        }
    }

    private boolean contieneLibro(List<Libro> lista, String id) {
        for (Libro libro : lista) {
            if (libro.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
}
