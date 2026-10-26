@echo off
rem Vuelve a los datos iniciales ejecutando sql\init.sql, despues de hacer una copia de seguridad
rem Uso: scripts\reiniciar_bd.bat
setlocal
cd /d "%~dp0.."

set "DB_NAME=biblioteca"

if not exist "sql\init.sql" (
    echo ERROR: no existe el archivo sql\init.sql.
    exit /b 1
)

rem --- Copia de seguridad previa ---
call "%~dp0backup_bd.bat"
if errorlevel 1 (
    echo ERROR: no se ha podido hacer la copia de seguridad. No se ha cambiado nada.
    exit /b 1
)

rem --- Credenciales del .env ---
if not exist ".env" (
    echo ERROR: no existe el archivo .env en la raiz del proyecto. Copia .env.example como .env y rellenalo.
    exit /b 1
)
set "DB_USER="
set "DB_PASS="
for /f "usebackq eol=# tokens=1,* delims==" %%a in (".env") do (
    if "%%a"=="DB_USER" set "DB_USER=%%b"
    if "%%a"=="DB_PASS" set "DB_PASS=%%b"
)
if not defined DB_USER (
    echo ERROR: falta DB_USER en el archivo .env.
    exit /b 1
)
if not defined DB_PASS (
    echo ERROR: falta DB_PASS en el archivo .env.
    exit /b 1
)

rem --- Ruta A (Docker) o Ruta B (MySQL instalado) ---
set "RUTA="
for /f "delims=" %%s in ('docker compose ps --status running --services 2^>nul') do (
    if "%%s"=="mysql" set "RUTA=A"
)
if not defined RUTA (
    where mysql >nul 2>&1 && set "RUTA=B"
)
if not defined RUTA (
    echo ERROR: no se encuentra MySQL. Abre Docker Desktop y arranca el contenedor con "docker compose up -d",
    echo        o instala MySQL y anade su carpeta bin al PATH.
    exit /b 1
)

rem --- Confirmacion ---
echo Se va a ejecutar sql\init.sql: se borraran todos los libros de la base de datos %DB_NAME%
echo y se cargaran los libros de ejemplo.
set "RESP="
set /p "RESP=Continuar? (S/N): "
if /i not "%RESP%"=="S" (
    echo Operacion cancelada. No se ha cambiado nada.
    exit /b 1
)

rem --- Reinicio ---
if "%RUTA%"=="A" (
    echo Reiniciando con Docker...
    docker compose exec -T -e "MYSQL_PWD=%DB_PASS%" mysql mysql "--user=%DB_USER%" --default-character-set=utf8mb4 < "sql\init.sql"
) else (
    echo Reiniciando con el MySQL instalado...
    set "MYSQL_PWD=%DB_PASS%"
    mysql "--user=%DB_USER%" --default-character-set=utf8mb4 < "sql\init.sql"
)
if errorlevel 1 (
    echo ERROR: ha fallado la ejecucion de sql\init.sql.
    exit /b 1
)

echo OK: base de datos %DB_NAME% reiniciada con los datos de sql\init.sql
exit /b 0
