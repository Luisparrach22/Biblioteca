package ejercicio.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ejercicio.model.Libro;
import ejercicio.util.ConexionBD;

public class LibroRepositoryMySQL implements LibroRepository {

    public LibroRepositoryMySQL() {
        super();
    }

    private Libro mapear(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String titulo = rs.getString("titulo");
        String autor = rs.getString("autor");
        double precio = rs.getDouble("precio");
        int stock = rs.getInt("stock");
        return new Libro(id, titulo, autor, precio, stock);
    }

    @Override
    public List<Libro> obtenerTodos() {
        List<Libro> libros = new ArrayList<>();
        String sql = "select * from libros";

        try (Connection conn = ConexionBD.getConnection()) {
            if (conn == null) {
                return libros;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }

        return libros;
    }

    @Override
    public List<Libro> buscarPorTitulo(String titulo) {
        List<Libro> libros = new ArrayList<>();
        if (titulo == null) return libros;

        String sql = "select * from libros where lower(titulo) like ?";

        try (Connection conn = ConexionBD.getConnection()) {
            if (conn == null) {
                return libros;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + titulo.trim().toLowerCase() + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        libros.add(mapear(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }

        return libros;
    }

    @Override
    public List<Libro> buscarPorAutor(String autor) {
        List<Libro> libros = new ArrayList<>();
        if (autor == null) return libros;

        String sql = "select * from libros where lower(autor) like ?";

        try (Connection conn = ConexionBD.getConnection()) {
            if (conn == null) {
                return libros;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + autor.trim().toLowerCase() + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        libros.add(mapear(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }

        return libros;
    }

    @Override
    public List<Libro> buscarPorRangoPrecio(double precioMin, double precioMax) {
        List<Libro> libros = new ArrayList<>();
        String sql = "select * from libros where precio between ? and ?";

        try (Connection conn = ConexionBD.getConnection()) {
            if (conn == null) {
                return libros;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, precioMin);
                pstmt.setDouble(2, precioMax);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        libros.add(mapear(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }

        return libros;
    }

    @Override
    public List<Libro> buscarPorStockMinimo(int stockMinimo) {
        List<Libro> libros = new ArrayList<>();
        String sql = "select * from libros where stock >= ?";

        try (Connection conn = ConexionBD.getConnection()) {
            if (conn == null) {
                return libros;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, stockMinimo);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        libros.add(mapear(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }

        return libros;
    }

    @Override
    public boolean insertar(Libro libro) {
        if (libro == null) return false;

        String sql = "insert into libros (id, titulo, autor, precio, stock) values (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, libro.getId());
                pstmt.setString(2, libro.getTitulo());
                pstmt.setString(3, libro.getAutor());
                pstmt.setDouble(4, libro.getPrecio());
                pstmt.setInt(5, libro.getStock());

                int filas = pstmt.executeUpdate();
                return filas > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarPorTitulo(String titulo) {
        if (titulo == null) return false;

        String sql = "delete from libros where lower(titulo) = lower(?)";

        try (Connection conn = ConexionBD.getConnection()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, titulo.trim());
                int filas = pstmt.executeUpdate();
                return filas > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarPorId(String id) {
        if (id == null) return false;

        String sql = "delete from libros where id = ?";

        try (Connection conn = ConexionBD.getConnection()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id.trim());
                int filas = pstmt.executeUpdate();
                return filas > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean copiar(LibroRepository destino) {
        if (destino == null) return false;
        List<Libro> misLibros = obtenerTodos();

        if (misLibros.isEmpty()) {
            System.out.println("No hay libros en MySQL para copiar.");
            return false;
        }

        int copiados = 0;
        for (Libro l : misLibros) {
            if (destino.insertar(l)) {
                copiados++;
            }
        }

        System.out.println("Se copiaron " + copiados + " libros al repositorio destino.");
        return copiados > 0;
    }
}
