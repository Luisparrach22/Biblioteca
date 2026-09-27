package ejercicio.model;

import java.util.Objects;
import java.util.regex.Pattern;
public class Libro {

    private String id;
    private String titulo;
    private String autor;
    private double precio;
    private int stock;

    public Libro() {
        super();
    }

    public Libro(String id, String titulo, String autor, double precio, int stock) {
        this.id = id != null ? id.trim() : "";
        this.titulo = titulo != null ? titulo.trim() : "";
        this.autor = autor != null ? autor.trim() : "";
        this.precio = precio;
        this.stock = stock;
    }

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

    public String toCSV() {
        return id + "^" + titulo + "^" + autor + "^" + precio + "^" + stock;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id != null ? id.trim() : "";
    }

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
