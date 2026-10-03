package ejercicio.util;

import java.util.List;

import ejercicio.model.Libro;
import ejercicio.repository.LibroRepository;

/**
 * Clase para mantener sincronizados los dos repositorios (Archivo local y MySQL).
 * Si falla la conexion con la BD, se omiten las operaciones.
 * 
 * @author Luis Parra
 * @author Fabricio
 */
public class Sincronizador {

    // Repositorio principal/activo y el secundario con el que nos sincronizamos
    private final LibroRepository activo;
    private final LibroRepository otro;

    // Constructor donde le pasamos los dos repositorios
    public Sincronizador(LibroRepository activo, LibroRepository otro) {
        this.activo = activo;
        this.otro = otro;
    }

    /**
     * Hace una sincronizacion completa bidireccional entre ambos repositorios.
     * Pasa los libros que faltan de un sitio al otro y viceversa.
     */
    public void sincronizar() {
        // Si MySQL no esta levantada o disponible, salimos sin hacer nada
        if (!listo()) return;

        System.out.println("Sincronizando archivo <-> MySQL...");

        List<Libro> librosActivo = activo.obtenerTodos();
        List<Libro> librosOtro = otro.obtenerTodos();

        int sincronizados = 0;

        // Pasamos los libros del repo activo al otro repo si no existen ya
        for (Libro libro : librosActivo) {
            if (!contieneLibro(librosOtro, libro.getId())) {
                if (otro.insertar(libro)) {
                    sincronizados++;
                }
            }
        }

        // Y ahora al reves: pasamos lo del otro repo al activo si faltan
        for (Libro libro : librosOtro) {
            if (!contieneLibro(librosActivo, libro.getId())) {
                if (activo.insertar(libro)) {
                    sincronizados++;
                }
            }
        }

        // Informamos de cuantos libros se han copiado
        if (sincronizados > 0) {
            System.out.println("Sincronizados " + sincronizados + " libros.");
        } else {
            System.out.println("Ambas fuentes ya estan sincronizadas.");
        }
    }

    /**
     * Copia un libro recién insertado en el repo activo hacia el otro repo.
     */
    public void trasInsertar(Libro libro) {
        if (libro == null || !listo()) return;

        // Comprobamos si ya estaba en el otro destino para no duplicar
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

    /**
     * Elimina el libro del otro repositorio para mantenerlos iguales al borrar.
     */
    public void trasEliminar(String id) {
        // Validamos que el ID tenga sentido y que la BD responda
        if (id == null || id.isBlank() || !listo()) return;

        if (otro.eliminarPorId(id)) {
            System.out.println("Borrado propagado al otro repositorio.");
        } else {
            System.out.println("El libro no existia en el otro repositorio.");
        }
    }

    /**
     * Helper para comprobar si la base de datos MySQL esta activa.
     */
    private boolean listo() {
        if (ConexionBD.estaDisponible()) {
            return true;
        }
        System.out.println("Aviso: MySQL no disponible, se omite la sincronizacion.");
        return false;
    }

    /**
     * Comprueba si un libro con ese ID ya esta en la lista (sin distinguir mayusculas/minusculas).
     */
    private boolean contieneLibro(List<Libro> lista, String id) {
        for (Libro libro : lista) {
            if (libro.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
}
