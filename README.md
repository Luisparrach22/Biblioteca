# Biblioteca

Sistema de Gestión de Biblioteca en Java (consola). Los libros se guardan en un archivo de texto (`data/libros.txt`) y en MySQL, y al arrancar se sincronizan los dos.

## Requisitos

- JDK 17 o superior
- Git
- Eclipse IDE for Enterprise Java and Web Developers
- MySQL, de una de estas dos formas:
  - Ruta A: Docker Desktop (la aplicación levanta MySQL sola)
  - Ruta B: MySQL Server 8 instalado en Windows

## Instalación

1. Clonar el repositorio:

   ```cmd
   git clone https://github.com/Luisparrach22/Biblioteca.git
   ```

2. Copiar `.env.example` como `.env` y rellenarlo según la ruta (ver abajo).
3. En Eclipse: File → Import → Existing Projects into Workspace y elegir la carpeta clonada. Las librerías de `lib/` ya están añadidas.
4. Ejecutar con Run → `Biblioteca`.

En Eclipse no se ven los archivos que empiezan por punto, como el `.env`. Para verlos: en el Package Explorer, Filters y quitar `.* resources`.

### Ruta A: Docker

- Abrir Docker Desktop antes de ejecutar.
- En el `.env` dejar `DB_URL` y `DB_USER=root` como están y poner una contraseña en `DB_PASS`.
- La aplicación arranca el contenedor y la base de datos se crea sola con `sql/init.sql`. Al salir, lo para.
- El puerto 3306 tiene que estar libre (sin otro MySQL encendido).
- Para borrar los datos o cambiar la contraseña: `docker compose down -v`

### Ruta B: MySQL instalado

- Crear la base de datos desde cmd (no PowerShell), en la carpeta del proyecto (sin probar):

  ```cmd
  mysql -u root -p < sql\init.sql
  ```

  Si no encuentra `mysql`, usar la ruta completa: `"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"`. El script borra los datos que hubiera en la tabla `libros`.
- En el `.env` poner tu usuario y tu contraseña de MySQL.

## Variables del .env

| Variable | Ruta A | Ruta B |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/biblioteca` | La de tu servidor |
| `DB_USER` | `root` | Tu usuario |
| `DB_PASS` | La que quieras | Tu contraseña |

`DB_HOST`, `DB_PORT`, `DB_NAME` y `DB_PASSWORD` no se usan.

## Generar el .jar

En Eclipse: Export → Runnable JAR file → `dist\Biblioteca.jar`, marcando "Copy required libraries into a sub-folder next to the generated JAR". Se ejecuta desde la carpeta del proyecto, no desde `dist` (sin probar):

```cmd
java -jar dist\Biblioteca.jar
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

## Administración

Scripts para cmd (no PowerShell). Se pueden lanzar desde cualquier carpeta. Los de base de datos usan Docker si el contenedor `mysql` está en marcha y, si no, el MySQL instalado. Las copias se guardan en `backups\`.

```cmd
scripts\backup_bd.bat
scripts\restaurar_bd.bat backups\biblioteca_2026-10-25_1730.sql
scripts\reiniciar_bd.bat
scripts\backup_txt.bat
scripts\restaurar_txt.bat backups\libros_2026-10-25_1730.txt
```

- `reiniciar_bd.bat` hace antes una copia con `backup_bd.bat` y después vuelve a cargar `sql\init.sql`.
- Antes de `restaurar_txt.bat`, cerrar la aplicación.

## Documentación

- Manual de despliegue: pendiente
- JavaDoc: [doc/index.html](doc/index.html)
