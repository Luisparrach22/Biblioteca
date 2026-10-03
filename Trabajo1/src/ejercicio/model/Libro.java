package ejercicio.model;
import java.util.Objects;
import java.util.regex.Pattern;
/**
 * Representa un libro de la biblioteca.
 *
 * @author Fabricio
 * @author Luis
 * @since 1.0
 */
public class Libro {
    private String id;
    private String titulo;
    private String autor;
    private double precio;
    private int stock;

    /**
     * Creamos constructor vacio,sin inicializar sus datos.
     */
    public Libro() {
        super();
    }

    /**
     * Crea un libro con los datos indicados.
     *
     * @param id identificador del libro
     * @param titulo título del libro
     * @param autor autor del libro
     * @param precio precio del libro
     * @param stock cantidad de ejemplares disponibles
     */
    public Libro(String id, String titulo, String autor, double precio, int stock) {
        this.id = id != null ? id.trim() : "";
        this.titulo = titulo != null ? titulo.trim() : "";
        this.autor = autor != null ? autor.trim() : "";
        this.precio = precio;
        this.stock = stock;
    }
    /**
     * Valida los datos proporcionados para un libro.
     *
     * @param id identificador del libro
     * @param titulo título del libro
     * @param autor autor del libro
     * @param precio precio del libro
     * @param stock cantidad de ejemplares disponibles
     * @return true si todos los datos son válidos; false en caso contrario
     */
    public boolean validarDatos(String id, String titulo, String autor, double precio, int stock) {
        if (id == null || id.isBlank()) {
            System.out.println("El id es obligatorio.");
            return false;
        }
        if (titulo == null || titulo.isBlank()) {
            System.out.println("El título es obligatorio.");
            return false;
        }
        if (autor == null || autor.isBlank()) {
            System.out.println("El autor es obligatorio.");
            return false;
        }
        if (precio < 0) {
            System.out.println("El precio no puede ser negativo.");
            return false;
        }
        if (stock < 0) {
            System.out.println("El stock no puede ser negativo.");
            return false;
        }
        return true;
    }

    /**
     * Convierte los datos del libro en una línea de texto.
     * Los datos se separan utilizando el carácter "^".
     *
     * @return una línea con los datos del libro separados por "^"
     */
    public String toCSV() {
        return id + "^" + titulo + "^" + autor + "^" + precio + "^" + stock;
    }


/**
 * Crea un libro a partir de una línea de texto.
 * La línea debe tener los datos separados por "^".
 *
 * @param lineaCadena línea de texto con los datos del libro
 * @return un libro creado con los datos de la línea, o null si la línea no es válida
 */
    public static Libro fromCSV(String lineaCadena) {
        if (lineaCadena == null || lineaCadena.isBlank() || lineaCadena.startsWith("#")) {
            return null;
        }
        //Dividir la línea en partes usando el carácter '^' como delimitador
        String[] partes = lineaCadena.split(Pattern.quote("^"));
        if (partes.length == 5) {
            try {
                String id = partes[0].trim();
                String titulo = partes[1].trim();
                String autor = partes[2].trim();
                double precio = Double.parseDouble(partes[3].trim());
                int stock = Integer.parseInt(partes[4].trim());
                return new Libro(id, titulo, autor, precio, stock);
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear línea CSV: " + lineaCadena);
            }
        }
        return null;
    }
    
    /**
     * Devuelve el identificador del libro.
     *
     * @return identificador del libro
     */
    public String getId() {
        return id;
    }
    /**
     * Asigna un identificador al libro.
     *
     * @param id identificador que se asignará al libro
     */
    public void setId(String id) {
        this.id = id != null ? id.trim() : "";
    }

    /**
     * Devuelve el título del libro.
     *
     * @return título del libro
     */
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo != null ? titulo.trim() : "";
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor != null ? autor.trim() : "";
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "id=" + id + ", titulo=" + titulo + ", autor=" + autor
                + ", precio=" + precio + ", stock=" + stock;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Libro otroLibro)) {
            return false;
        }
        return Objects.equals(id, otroLibro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
