package ejercicio.util;

import java.sql.Connection;
import java.sql.DriverManager;

import io.github.cdimascio.dotenv.Dotenv;


/**
 * Se encarga de gestionar la conexión con la base de datos.
 *
 * @author Fabricio
 * @author Luis
 * @since 1.0
 */
public class ConexionBD {

    /**
     * Realiza la conexión con la base de datos utilizando los datos del archivo .env.
     *
     * @return conexión con la base de datos
     * @throws Exception si ocurre algún error al realizar la conexión
     */
    private static Connection conectar() throws Exception {
        Dotenv dotenv = Dotenv.load();

        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String pass = dotenv.get("DB_PASS");

        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, pass);
    }
    /**
     * Devuelve una conexión con la base de datos.
     *
     * @return conexión con la base de datos, o null si no se puede conectar
     */
    public static Connection getConnection() {
        try {
            return conectar();
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }
    /**
     * Comprueba si la base de datos está disponible.
     *
     * @return true si la conexión está disponible, false si no lo está
     */
    public static boolean estaDisponible() {
        try (Connection conn = conectar()) {
            return conn != null && conn.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }
}
