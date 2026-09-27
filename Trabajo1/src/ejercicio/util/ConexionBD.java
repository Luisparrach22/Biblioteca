package ejercicio.util;

import java.sql.Connection;
import java.sql.DriverManager;

import io.github.cdimascio.dotenv.Dotenv;

public class ConexionBD {

    private static Connection conectar() throws Exception {
        Dotenv dotenv = Dotenv.load();

        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String pass = dotenv.get("DB_PASS");

        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, pass);
    }

    public static Connection getConnection() {
        try {
            return conectar();
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }

    public static boolean estaDisponible() {
        try (Connection conn = conectar()) {
            return conn != null && conn.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }
}
