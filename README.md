# Biblioteca

Aplicación Java de consola para gestionar libros. Guarda los datos en dos repositorios intercambiables: un archivo de texto (`data/libros.txt`) y una base de datos MySQL. Al arrancar se elige uno y los dos se sincronizan.

## Elige cómo desplegar

| | Ruta A: MySQL en Docker | Ruta B: MySQL instalado |
|---|---|---|
| Necesita | Docker Desktop | MySQL Server 8.x en Windows |
| Base de datos | La crea el contenedor sola con `sql/init.sql` | La creas tú con `sql/init.sql` |
| Usuario | `root` | El tuyo |
| Cuándo conviene | Tienes Docker o puedes instalarlo | No tienes Docker o ya tienes MySQL |

El código y el repositorio son los mismos. Al arrancar, la aplicación mira si hay Docker: si lo hay, levanta el contenedor (Ruta A); si no, usa el MySQL que diga el `.env` (Ruta B).

## Requisitos comunes

- JDK 17 o superior (probado con JDK 24)
- Git
- Eclipse IDE for Enterprise Java and Web Developers (2026-09)

## Pasos comunes

1. Clona el repositorio:

   ```cmd
   git clone https://github.com/Luisparrach22/Biblioteca.git
   cd Biblioteca
   ```

2. Copia la plantilla de configuración:

   ```cmd
   copy .env.example .env
   notepad .env
   ```

   Los valores dependen de la ruta (ver [Variables del .env](#variables-del-env)).

3. Abre el proyecto en Eclipse: File → Import → General → Existing Projects into Workspace → Select root directory: la carpeta clonada → Finish.

   El proyecto `Biblioteca` ya viene configurado: código fuente en `Trabajo1/src`, librerías de `lib/` y la configuración de ejecución `Biblioteca`. No hay que añadir nada al Build Path. Eclipse necesita un JDK 17 o superior (Window → Preferences → Java → Installed JREs).

Para ejecutar la aplicación (en las dos rutas): Run → Run Configurations → Java Application → `Biblioteca` → Run. Después ya aparece en el desplegable del botón Run. La consola de Eclipse es donde se escriben las opciones del menú.

## Ruta A: MySQL en Docker

1. Instala Docker Desktop y ábrelo.
2. En el `.env` pon `DB_USER=root` y en `DB_PASS` la contraseña que quieras para root. Deja `DB_URL` como está.
3. En Eclipse ejecuta la configuración `Biblioteca` (ver [Pasos comunes](#pasos-comunes)).

La aplicación levanta el contenedor, espera a MySQL y la base de datos se carga sola con `sql/init.sql`. Al salir, para el contenedor.

`sql/init.sql` solo se ejecuta la primera vez que se crea el volumen. Para cambiar la contraseña de root o volver a los datos iniciales, desde la raíz del proyecto (**sin comprobar**):

```cmd
docker compose down -v
```

## Ruta B: MySQL instalado

1. Instala MySQL Server 8.x. Deja el servicio arrancado y apunta la contraseña de root.
2. Crea la base de datos. Usa **cmd**, no PowerShell (en PowerShell no funciona `<`). Desde la raíz del proyecto (**sin comprobar**):

   ```cmd
   mysql -u root -p < sql\init.sql
   ```

   Si `mysql` no se reconoce, usa la ruta completa (cambia `8.0` por tu versión):

   ```cmd
   "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < sql\init.sql
   ```

   El script crea la base de datos `biblioteca` y la tabla `libros` con libros de ejemplo. **Borra los datos que hubiera en la tabla.** Se puede ejecutar varias veces.

3. En el `.env` pon tu usuario y tu contraseña de MySQL, y `DB_URL` apuntando a tu servidor.
4. En Eclipse ejecuta la configuración `Biblioteca` (ver [Pasos comunes](#pasos-comunes)).

Si además tienes Docker instalado y tu MySQL ya ocupa el puerto 3306, la aplicación avisará de que no pudo iniciar el contenedor y seguirá con tu MySQL.

## Variables del .env

El archivo `.env` va en la raíz del proyecto. No se sube a git.

| Variable | Para qué sirve | Ruta A (Docker) | Ruta B (MySQL instalado) |
|---|---|---|---|
| `DB_URL` | URL JDBC: servidor, puerto y base de datos | `jdbc:mysql://localhost:3306/biblioteca` | `jdbc:mysql://<servidor>:<puerto>/biblioteca` |
| `DB_USER` | Usuario de MySQL | `root` | Tu usuario |
| `DB_PASS` | Contraseña del usuario | La que tendrá root en el contenedor | Tu contraseña |

## Ejecutar con el .jar

Para generar el .jar en Eclipse:

1. Clic derecho en el proyecto → Export → Java → Runnable JAR file.
2. Launch configuration: `Biblioteca - Biblioteca`.
3. Export destination: `dist\Biblioteca.jar`.
4. Library handling: **Copy required libraries into a sub-folder next to the generated JAR**.

Eclipse crea `dist\Biblioteca.jar` y la carpeta `dist\Biblioteca_lib\`. Ejecuta desde la **raíz del proyecto**, no desde `dist\`, porque ahí se buscan `.env`, `docker-compose.yml` y `data\libros.txt` (**sin comprobar**):

```cmd
java -jar dist\Biblioteca.jar
```

## Estructura

```
Trabajo1/src/ejercicio/   Código fuente (clase principal: ejercicio.Main)
data/libros.txt           Repositorio de texto (campos separados por ^)
sql/init.sql              Script de la base de datos (las dos rutas)
docker-compose.yml        Contenedor MySQL (solo Ruta A)
lib/                      Librerías (.jar), ya incluidas
.project, .classpath      Proyecto de Eclipse
Biblioteca.launch         Configuración de ejecución de Eclipse
doc/                      JavaDoc generado
.env.example              Plantilla de configuración
```

## Documentación

- Manual de despliegue (PDF): _pendiente_ <!-- enlace al PDF -->
- JavaDoc: [doc/index.html](doc/index.html)
