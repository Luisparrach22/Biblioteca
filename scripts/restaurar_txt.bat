@echo off
rem Copia una copia de seguridad sobre data\libros.txt
rem Uso: scripts\restaurar_txt.bat backups\libros_AAAA-MM-DD_HHMM.txt
setlocal
rem La ruta del archivo se resuelve antes del cd para que valga una ruta relativa
rem a la carpeta desde la que se lanza el script
set "ARCHIVO=%~f1"
cd /d "%~dp0.."

if "%~1"=="" goto :uso
if not exist "%ARCHIVO%" (
    echo ERROR: no existe el archivo "%~1".
    goto :uso
)

rem --- Confirmacion ---
echo AVISO: la aplicacion Biblioteca tiene que estar cerrada.
echo Si esta abierta, puede volver a escribir data\libros.txt al insertar, eliminar o copiar datos
echo y deshacer la restauracion.
echo Se va a copiar "%~1" sobre data\libros.txt. El contenido actual se sobrescribira.
set "RESP="
set /p "RESP=Continuar? (S/N): "
if /i not "%RESP%"=="S" (
    echo Operacion cancelada. No se ha cambiado nada.
    exit /b 1
)

rem --- Restauracion ---
if not exist "data" mkdir "data"
copy /y "%ARCHIVO%" "data\libros.txt" >nul
if errorlevel 1 (
    echo ERROR: no se ha podido copiar "%~1" sobre data\libros.txt.
    exit /b 1
)

echo OK: data\libros.txt restaurado desde "%~1"
exit /b 0

:uso
echo Uso: scripts\restaurar_txt.bat backups\libros_2026-10-25_1730.txt
exit /b 1
