package ejercicio;

import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import ejercicio.model.Libro;
import ejercicio.repository.LibroRepository;
import ejercicio.repository.LibroRepositoryArchivo;
import ejercicio.repository.LibroRepositoryMySQL;
import ejercicio.util.ConexionBD;
import ejercicio.util.Sincronizador;

/**
 * Clase principal de la aplicacion de Gestion de Biblioteca.
 * Controla el arranque de Docker para MySQL, el menu interactivo por consola
 * y la sincronizacion entre fuentes de datos.
 * 
 * @author Luis Parra
 * @author Fabricio
 */
public class Main {

    /**
     * Ejecuta un comando en la terminal (cmd.exe en Windows o sh en Mac/Linux).
     * Devuelve true si el comando finalizo correctamente (exit value 0).
     */
    private static boolean ejecutarComando(String comando) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String[] cmd;
            if (os.contains("win")) {
                // Windows: ejecuta el comando mediante el intérprete cmd.exe.
                cmd = new String[]{"cmd.exe", "/c", comando};
            } else {
                // macOS y otros sistemas Unix/Linux: ejecuta el comando mediante sh.
                cmd = new String[]{"sh", "-c", comando};
            }
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            pb.redirectError(ProcessBuilder.Redirect.DISCARD);
            Process p = pb.start();
            p.waitFor();
            return p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Revisa en bucle durante un maximo de 60 segundos si MySQL ya acepta conexiones.
     */
    private static void esperarMySQL() {
        System.out.print("Esperando a que MySQL este disponible");
        long limite = System.currentTimeMillis() + 60_000L;

        while (System.currentTimeMillis() < limite) {
            if (ConexionBD.estaDisponible()) {
                System.out.println(" OK");
                return;
            }
            System.out.print(".");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println();
        System.out.println("Aviso: MySQL no responde, se continuara sin el.");
    }

    public static void main(String[] args) {
        
        System.out.println("Verificando Docker...");

        // Comprobamos si Docker esta instalado en el equipo
        if (!ejecutarComando("docker --version")) {
            System.out.println("ERROR: Docker no esta instalado.");
            System.out.println("Instala Docker Desktop desde: https://www.docker.com/products/docker-desktop/");
            return;
        }

        // Comprobamos si Docker Desktop esta abierto y funcionando
        if (!ejecutarComando("docker info")) {
            System.out.println("ERROR: Docker no esta ejecutandose.");
            System.out.println("Abre Docker Desktop y vuelve a intentarlo.");
            return;
        }

        System.out.println("Docker detectado correctamente.");

        // Obtenemos la ruta del archivo docker-compose.yml en la raiz
        String rutaCompose = Paths.get(System.getProperty("user.dir"), "docker-compose.yml").toString();

        // Levantamos el contenedor de MySQL en segundo plano
        System.out.println("Iniciando contenedor MySQL...");
        boolean levantado = ejecutarComando("docker compose -f \"" + rutaCompose + "\" up -d");

        // Probamos con la sintaxis antigua con guion por si acaso
        if (!levantado) {
            levantado = ejecutarComando("docker-compose -f \"" + rutaCompose + "\" up -d");
        }

        if (!levantado) {
            System.out.println("ERROR: No se pudo iniciar docker-compose.");
            System.out.println("Asegurate de que docker-compose.yml esta en la raiz del proyecto.");
            return;
        }

        System.out.println("Contenedor MySQL iniciado correctamente.");
        esperarMySQL();
        System.out.println();

        Scanner sc = new Scanner(System.in);

        // Elegimos cual sera la fuente principal de datos
        System.out.println("--- Gestion Biblioteca  ---");
        System.out.println("1. Archivo (.txt)");
        System.out.println("2. MySQL");
        System.out.print("Seleccione repositorio: ");

        int opcionRepo = Integer.parseInt(sc.nextLine());

        LibroRepository repo;
        LibroRepository repoDestino;

        // Segun la eleccion, asignamos cual es el activo y cual es el secundario
        if (opcionRepo == 2) {
            repo = new LibroRepositoryMySQL();
            repoDestino = new LibroRepositoryArchivo();
        } else {
            repo = new LibroRepositoryArchivo();
            repoDestino = new LibroRepositoryMySQL();
        }

        // Ejecutamos la sincronizacion inicial al arrancar el programa
        Sincronizador sincronizador = new Sincronizador(repo, repoDestino);
        sincronizador.sincronizar();
        System.out.println();

        int opcion = 0;

        // Bucle principal del menu hasta que el usuario elija salir (opcion 9)
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
                    // Opcion 1: Listar todo el catalogo
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
                    // Opcion 2: Filtro por titulo
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
                    // Opcion 3: Filtro por autor
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
                    // Opcion 4: Busqueda entre precio minimo y maximo
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
                    // Opcion 5: Filtrar por stock mayor o igual al indicado
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
                    // Opcion 6: Alta de un nuevo libro y propagacion al otro repo
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
                            // Notificamos al sincronizador para replicarlo en el otro origen
                            sincronizador.trasInsertar(nuevo);
                        } else {
                            System.out.println("Error al insertar el libro.");
                        }
                    }
                    break;

                case 7:
                    // Opcion 7: Eliminar libro por titulo o ID especifico si hay repetidos
                    System.out.print("Título del libro a eliminar: ");
                    String tEliminar = sc.nextLine();
                    List<Libro> encontrados = repo.buscarPorTitulo(tEliminar);

                    if (encontrados.isEmpty()) {
                        System.out.println("No existe ningún libro con ese título.");
                    } else if (encontrados.size() == 1) {
                        // Si solo hay uno, lo borramos directamente
                        Libro l = encontrados.get(0);
                        if (repo.eliminarPorId(l.getId())) {
                            System.out.println("Libro eliminado.");
                            // Propagamos la baja al otro repositorio
                            sincronizador.trasEliminar(l.getId());
                        } else {
                            System.out.println("Error al eliminar.");
                        }
                    } else {
                        // Si coinciden varios por titulo, pedimos desempatar por ID
                        System.out.println("Hay varios libros con ese título:");
                        for (Libro l : encontrados) {
                            System.out.println("ID: " + l.getId() + " - " + l.getTitulo() + " (" + l.getAutor() + ")");
                        }
                        System.out.print("Introduce el ID exacto a eliminar: ");
                        String idEliminar = sc.nextLine();
                        if (repo.eliminarPorId(idEliminar)) {
                            System.out.println("Libro eliminado.");
                            sincronizador.trasEliminar(idEliminar);
                        } else {
                            System.out.println("Error al eliminar.");
                        }
                    }
                    break;

                case 8:
                    // Opcion 8: Copiado masivo del repo activo al repo destino
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
        
        // Al terminar el programa, apagamos el contenedor de MySQL
        System.out.println("Deteniendo contenedor MySQL...");
        String rutaCompose2 = Paths.get(System.getProperty("user.dir"), "docker-compose.yml").toString();
        if (!ejecutarComando("docker compose -f \"" + rutaCompose2 + "\" down")) {
            ejecutarComando("docker-compose -f \"" + rutaCompose2 + "\" down");
        }
        System.out.println("Contenedor detenido. Hasta luego!");
    }
}
