package ejercicio.dao;

import java.util.List;

public interface GenericDAO<T> {
	boolean insertar(T objeto);
	List<T> obtenerTodos();
	T obtenerPorId(int id);
	boolean actualizar(T objeto);
	boolean eliminar(int id);

}
