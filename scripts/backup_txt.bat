@echo off
rem Copia data\libros.txt en backups\libros_AAAA-MM-DD_HHMM.txt
rem Uso: scripts\backup_txt.bat
setlocal
cd /d "%~dp0.."

if not exist "data\libros.txt" (
    echo ERROR: no existe el archivo data\libros.txt.
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
set "ARCHIVO=backups\libros_%FECHA%.txt"

rem --- Copia ---
copy /y "data\libros.txt" "%ARCHIVO%" >nul
if errorlevel 1 (
    echo ERROR: no se ha podido copiar data\libros.txt en %ARCHIVO%.
    exit /b 1
)

echo OK: copia de seguridad guardada en %ARCHIVO%
exit /b 0
