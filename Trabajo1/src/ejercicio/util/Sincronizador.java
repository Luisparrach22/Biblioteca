package ejercicio.util;

import java.util.List;

import ejercicio.model.Libro;
import ejercicio.repository.LibroRepository;

public class Sincronizador {

    private final LibroRepository activo;
    private final LibroRepository otro;

    public Sincronizador(LibroRepository activo, LibroRepository otro) {
        this.activo = activo;
        this.otro = otro;
    }

    public void sincronizar() {
        if (!listo()) return;

        System.out.println("Sincronizando archivo <-> MySQL...");

        List<Libro> librosActivo = activo.obtenerTodos();
        List<Libro> librosOtro = otro.obtenerTodos();

        int sincronizados = 0;

        for (Libro libro : librosActivo) {
            if (!contieneLibro(librosOtro, libro.getId())) {
                if (otro.insertar(libro)) {
                    sincronizados++;
                }
            }
        }

        for (Libro libro : librosOtro) {
            if (!contieneLibro(librosActivo, libro.getId())) {
                if (activo.insertar(libro)) {
                    sincronizados++;
                }
            }
        }

        if (sincronizados > 0) {
            System.out.println("Sincronizados " + sincronizados + " libros.");
        } else {
            System.out.println("Ambas fuentes ya estan sincronizadas.");
        }
    }

    public void trasInsertar(Libro libro) {
        if (libro == null || !listo()) return;

        if (contieneLibro(otro.obtenerTodos(), libro.getId())) {
            System.out.println("El libro ya estaba sincronizado en el otro repositorio.");
            return;
        }

        if (otro.insertar(libro)) {
            System.out.println("Libro sincronizado en el otro repositorio.");
        } else {
            System.out.println("No se pudo sincronizar el libro en el otro repositorio.");
        }
    }

    public void trasEliminar(String id) {
        if (id == null || id.isBlank() || !listo()) return;

        if (otro.eliminarPorId(id)) {
            System.out.println("Borrado propagado al otro repositorio.");
        } else {
            System.out.println("El libro no existia en el otro repositorio.");
        }
    }

    private boolean listo() {
        if (ConexionBD.estaDisponible()) {
            return true;
        }
        System.out.println("Aviso: MySQL no disponible, se omite la sincronizacion.");
        return false;
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
