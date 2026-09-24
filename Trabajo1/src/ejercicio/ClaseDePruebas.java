package ejercicio;



import java.util.List;

import ejercicio.model.Libro;
import ejercicio.repository.LibroRepositoryMySQL;

public class ClaseDePruebas {

	public static void main(String[] args) {
	
		   LibroRepositoryMySQL repositorio = new LibroRepositoryMySQL();

	        List<Libro> libros = repositorio.obtenerTodos();

	        for (Libro libro : libros) {
	            System.out.println(libro);
	        }

	}

}
