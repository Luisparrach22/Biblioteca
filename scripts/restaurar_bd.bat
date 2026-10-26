@echo off
rem Restaura una copia de seguridad de la base de datos
rem Uso: scripts\restaurar_bd.bat backups\biblioteca_AAAA-MM-DD_HHMM.sql
setlocal
rem La ruta del archivo se resuelve antes del cd para que valga una ruta relativa
rem a la carpeta desde la que se lanza el script
set "ARCHIVO=%~f1"
cd /d "%~dp0.."

set "DB_NAME=biblioteca"

if "%~1"=="" goto :uso
if not exist "%ARCHIVO%" (
    echo ERROR: no existe el archivo "%~1".
    goto :uso
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
echo Se va a restaurar "%~1" en la base de datos %DB_NAME%.
echo Los datos actuales se sobrescribiran.
set "RESP="
set /p "RESP=Continuar? (S/N): "
if /i not "%RESP%"=="S" (
    echo Operacion cancelada. No se ha cambiado nada.
    exit /b 1
)

rem --- Restauracion ---
if "%RUTA%"=="A" (
    echo Restaurando con Docker...
    docker compose exec -T -e "MYSQL_PWD=%DB_PASS%" mysql mysql "--user=%DB_USER%" --default-character-set=utf8mb4 < "%ARCHIVO%"
) else (
    echo Restaurando con el MySQL instalado...
    set "MYSQL_PWD=%DB_PASS%"
    mysql "--user=%DB_USER%" --default-character-set=utf8mb4 < "%ARCHIVO%"
)
if errorlevel 1 (
    echo ERROR: ha fallado la restauracion de "%~1".
    exit /b 1
)

echo OK: base de datos %DB_NAME% restaurada desde "%~1"
exit /b 0

:uso
echo Uso: scripts\restaurar_bd.bat backups\biblioteca_2026-10-25_1730.sql
exit /b 1
