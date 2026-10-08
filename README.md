# Biblioteca

Sistema de Gestión de Biblioteca en Java (consola) con dos repositorios intercambiables: **archivo de texto plano** y **MySQL**.

## Requisitos

- JDK 17 o superior
- Docker Desktop (para el contenedor MySQL)
- Jars de dependencias en `~/Downloads/librerias/`:
  - `dotenv-java-3.2.0.jar`
  - `mysql-connector-j-26.7.0.jar`

## Estructura

```
Trabajo1/src/ejercicio/
├── Main.java                        Menú de opciones y arranque
├── model/Libro.java                 Modelo (id, titulo, autor, precio, stock)
├── repository/
│   ├── LibroRepository.java         Interfaz de consultas, inserción y copia
│   ├── LibroRepositoryArchivo.java  Persistencia en data/libros.txt
│   └── LibroRepositoryMySQL.java    Persistencia en MySQL
└── util/
    ├── ConexionBD.java              Conexión JDBC vía variables de entorno
    └── Sincronizador.java           Sincronización automática entre repositorios

data/libros.txt   Fichero de texto (separador ^, un libro por línea)
db/schema.sql     Esquema y datos iniciales de MySQL
docker-compose.yml  Contenedor MySQL 8.0
```

## Ejecución

1. Copia `.env.example` a `.env` y completa `DB_URL`, `DB_USER` y `DB_PASS`.
2. Compila desde la raíz del proyecto:

   ```bash
   mkdir -p out
   javac -d out -cp "$HOME/Downloads/librerias/dotenv-java-3.2.0.jar:$HOME/Downloads/librerias/mysql-connector-j-26.7.0.jar" $(find Trabajo1/src -name "*.java")
   ```

3. Ejecuta (la aplicación arranca y detiene el contenedor MySQL sola):

   ```bash
   java -cp "out:$HOME/Downloads/librerias/dotenv-java-3.2.0.jar:$HOME/Downloads/librerias/mysql-connector-j-26.7.0.jar" ejercicio.Main
   ```

## Uso

Al iniciar se elige el repositorio activo (archivo o MySQL) y se sincronizan ambos.

| Opción | Descripción |
|---|---|
| 1 | Listado de todos los libros |
| 2 | Buscar por título |
| 3 | Buscar por autor |
| 4 | Buscar por rango de precios |
| 5 | Buscar por stock mínimo |
| 6 | Insertar nuevo libro |
| 7 | Eliminar libro (por título; si hay varios, se elige por ID) |
| 8 | Copiar datos al otro repositorio |
| 9 | Salir |

Tras cada inserción o eliminación los cambios se sincronizan automáticamente con el otro repositorio.
