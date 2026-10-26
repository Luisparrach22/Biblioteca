@echo off
rem Copia de seguridad de la base de datos en backups\biblioteca_AAAA-MM-DD_HHMM.sql
rem Uso: scripts\backup_bd.bat
setlocal
cd /d "%~dp0.."

set "DB_NAME=biblioteca"

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
    where mysqldump >nul 2>&1 && set "RUTA=B"
)
if not defined RUTA (
    echo ERROR: no se encuentra MySQL. Abre Docker Desktop y arranca el contenedor con "docker compose up -d",
    echo        o instala MySQL y anade su carpeta bin al PATH.
    exit /b 1
)

rem --- Nombre del archivo ---
set "FECHA="
for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyy-MM-dd_HHmm"') do set FECHA=%%i
if not defined FECHA (
    echo ERROR: no se ha podido obtener la fecha.
    exit /b 1
)
if not exist "backups" mkdir "backups"
if not exist "backups" (
    echo ERROR: no se ha podido crear la carpeta backups.
    exit /b 1
)
set "ARCHIVO=backups\%DB_NAME%_%FECHA%.sql"

rem --- Volcado ---
if "%RUTA%"=="A" (
    echo Haciendo copia de seguridad con Docker...
    docker compose exec -T -e "MYSQL_PWD=%DB_PASS%" mysql mysqldump "--user=%DB_USER%" --single-transaction --no-tablespaces --set-gtid-purged=OFF --default-character-set=utf8mb4 --databases %DB_NAME% > "%ARCHIVO%"
) else (
    echo Haciendo copia de seguridad con el MySQL instalado...
    set "MYSQL_PWD=%DB_PASS%"
    mysqldump "--user=%DB_USER%" --single-transaction --no-tablespaces --set-gtid-purged=OFF --default-character-set=utf8mb4 --databases %DB_NAME% "--result-file=%ARCHIVO%"
)
if errorlevel 1 (
    if exist "%ARCHIVO%" del "%ARCHIVO%"
    echo ERROR: ha fallado la copia de seguridad de la base de datos %DB_NAME%.
    exit /b 1
)

echo OK: copia de seguridad guardada en %ARCHIVO%
exit /b 0
