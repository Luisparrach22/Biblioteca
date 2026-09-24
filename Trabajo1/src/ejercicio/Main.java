package ejercicio;

import java.util.List;
import java.util.Scanner;

import ejercicio.model.Libro;
import ejercicio.repository.LibroRepository;
import ejercicio.repository.LibroRepositoryArchivo;
import ejercicio.repository.LibroRepositoryMySQL;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("--- Gestion Biblioteca  ---");
        System.out.println("1. Archivo (.txt)");
        System.out.println("2. MySQL");
        System.out.print("Seleccione repositorio: ");

        int opcionRepo = Integer.parseInt(sc.nextLine());

        LibroRepository repo;
        LibroRepository repoDestino;

        if (opcionRepo == 2) {
            repo = new LibroRepositoryMySQL();
            repoDestino = new LibroRepositoryArchivo();
        } else {
            repo = new LibroRepositoryArchivo();
            repoDestino = new LibroRepositoryMySQL();
        }

        int opcion = 0;

        do {
            System.out.println("--- Menu ---");
            System.out.println("1. Listado de todos los libros");
            System.out.println("2. Buscar por título");
            System.out.println("3. Buscar por autor");
            System.out.println("4. Buscar por rango de precio");
            System.out.println("5. Buscar por stock mínimo");
            System.out.println("6. Insertar nuevo libro");
            System.out.println("7. Eliminar libro");
            System.out.println("8. Copiar datos");
            System.out.println("9. Salir");
            System.out.print("Opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcion = 0;
            }

            switch (opcion) {
                case 1:
                    List<Libro> todos = repo.obtenerTodos();
                    if (todos.isEmpty()) {
                        System.out.println("No hay libros.");
                    } else {
                        for (Libro l : todos) {
                            System.out.println(l);
                        }
                    }
                    break;

                case 2:
                    System.out.print("Título a buscar: ");
                    String t = sc.nextLine();
                    List<Libro> porTitulo = repo.buscarPorTitulo(t);
                    if (porTitulo.isEmpty()) {
                        System.out.println("No se encontraron resultados.");
                    } else {
                        for (Libro l : porTitulo) {
                            System.out.println(l);
                        }
                    }
                    break;

                case 3:
                    System.out.print("Autor a buscar: ");
                    String a = sc.nextLine();
                    List<Libro> porAutor = repo.buscarPorAutor(a);
                    if (porAutor.isEmpty()) {
                        System.out.println("No se encontraron resultados.");
                    } else {
                        for (Libro l : porAutor) {
                            System.out.println(l);
                        }
                    }
                    break;

                case 4:
                    System.out.print("Precio mínimo: ");
                    double pMin = Double.parseDouble(sc.nextLine());
                    System.out.print("Precio máximo: ");
                    double pMax = Double.parseDouble(sc.nextLine());

                    List<Libro> porPrecio = repo.buscarPorRangoPrecio(pMin, pMax);
                    if (porPrecio.isEmpty()) {
                        System.out.println("No se encontraron resultados.");
                    } else {
                        for (Libro l : porPrecio) {
                            System.out.println(l);
                        }
                    }
                    break;

                case 5:
                    System.out.print("Stock mínimo: ");
                    int sMin = Integer.parseInt(sc.nextLine());

                    List<Libro> porStock = repo.buscarPorStockMinimo(sMin);
                    if (porStock.isEmpty()) {
                        System.out.println("No hay resultados.");
                    } else {
                        for (Libro l : porStock) {
                            System.out.println(l);
                        }
                    }
                    break;

                case 6:
                    System.out.print("ID: ");
                    String id = sc.nextLine();
                    System.out.print("Título: ");
                    String titulo = sc.nextLine();
                    System.out.print("Autor: ");
                    String autor = sc.nextLine();
                    System.out.print("Precio: ");
                    double precio = Double.parseDouble(sc.nextLine());
                    System.out.print("Stock: ");
                    int stock = Integer.parseInt(sc.nextLine());

                    Libro nuevo = new Libro(id, titulo, autor, precio, stock);
                    if (nuevo.validarDatos(id, titulo, autor, precio, stock)) {
                        if (repo.insertar(nuevo)) {
                            System.out.println("Libro insertado correctamente.");
                        } else {
                            System.out.println("Error al insertar el libro.");
                        }
                    }
                    break;

                case 7:
                    System.out.print("Título del libro a eliminar: ");
                    String tEliminar = sc.nextLine();
                    List<Libro> encontrados = repo.buscarPorTitulo(tEliminar);

                    if (encontrados.isEmpty()) {
                        System.out.println("No existe ningún libro con ese título.");
                    } else if (encontrados.size() == 1) {
                        Libro l = encontrados.get(0);
                        if (repo.eliminarPorId(l.getId())) {
                            System.out.println("Libro eliminado.");
                        } else {
                            System.out.println("Error al eliminar.");
                        }
                    } else {
                        System.out.println("Hay varios libros con ese título:");
                        for (Libro l : encontrados) {
                            System.out.println("ID: " + l.getId() + " - " + l.getTitulo() + " (" + l.getAutor() + ")");
                        }
                        System.out.print("Introduce el ID exacto a eliminar: ");
                        String idEliminar = sc.nextLine();
                        if (repo.eliminarPorId(idEliminar)) {
                            System.out.println("Libro eliminado.");
                        } else {
                            System.out.println("Error al eliminar.");
                        }
                    }
                    break;

                case 8:
                    if (repo.copiar(repoDestino)) {
                        System.out.println("Copia de datos realizada.");
                    } else {
                        System.out.println("Error al realizar la copia.");
                    }
                    break;

                case 9:
                    System.out.println("Saliendo del programa...");
                    break;

                default:
                    System.out.println("Opción no válida.");
                    break;
            }

        } while (opcion != 9);

        sc.close();
    }
}
