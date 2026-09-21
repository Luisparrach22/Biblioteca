package ejercicio.model;

import java.util.Objects;

public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private double precio;
    private int stock;

    public Libro(int id, String titulo, String autor, double precio, int stock) {
        this.id = id;
        this.titulo = titulo.trim();
        this.autor = autor.trim();
        this.precio = precio;
        this.stock = stock;
    }

    public boolean validarDatos(int id, String titulo, String autor, double precio, int stock){

        if (id <= 0){
            System.out.println("El id debe ser mayor que cero.");
            return false;
        }

        if (titulo == null || titulo.isBlank()){
            System.out.println("El titulo es obligatorio.");
            return false;
        }

        if (autor == null || autor.isBlank()){
            System.out.println("El autor es obligatorio.");
            return false;
        }

        if (precio < 0){
            System.out.println("El precio no puede ser negativo.");
            return false;
        }

        if (stock < 0){
            System.out.println("El stock no puede ser negativo.");
            return false;
        }

        return true;
    }

    // Getters

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    // toString
    @Override
    public java.lang.String toString() {
        return "Libro:" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", precio=" + precio +
                ", stock=" + stock;
    }

    // equals & hascode

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }

        if (!(objeto instanceof Libro otroLibro)) {
            return false;
        }

        return id == otroLibro.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
