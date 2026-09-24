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

	@Override
	public List<Libro> obtenerTodos() {

	    List<Libro> libros = new ArrayList<>();

	    String sql = "SELECT id, titulo, autor, precio, stock FROM libros";

	    try (Connection conn = ConexionBD.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {

	            libros.add(mapear(rs));

	        }

	    } catch (SQLException e) {

	        System.err.println("Error SQL al obtener todos los libros: " + e.getMessage());

	    }

	    return libros;
	}

	private Libro mapear(ResultSet rs) throws SQLException {

	    Libro libro = new Libro();

	    libro.setId(rs.getString("id"));
	    libro.setTitulo(rs.getString("titulo"));
	    libro.setAutor(rs.getString("autor"));
	    libro.setPrecio(rs.getDouble("precio"));
	    libro.setStock(rs.getInt("stock"));

	    return libro;
	}
	
	
	
	
	
	

	@Override
	public List<Libro> buscarPorTitulo(String titulo) {
	
		return null;
	}

	@Override
	public List<Libro> buscarPorAutor(String autor) {
		
		return null;
	}

	@Override
	public List<Libro> buscarPorRangoPrecio(double precioMin, double precioMax) {
	
		return null;
	}

	@Override
	public List<Libro> buscarPorStockMinimo(int stockMinimo) {
		
		return null;
	}

	@Override
	public boolean insertar(Libro libro) {
		
		return false;
	}

	@Override
	public boolean eliminarPorTitulo(String titulo) {
		
		return false;
	}

	@Override
	public boolean copiar(LibroRepository destino) {
		
		return false;
	}

}
