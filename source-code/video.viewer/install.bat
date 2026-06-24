@echo off
REM Video Viewer — Windows install script
echo ============================================
echo  Video Viewer - Windows Install
echo ============================================
echo.

set SCRIPT_DIR=%~dp0
set PROJECT_ROOT=%SCRIPT_DIR%..\..
set LIB_DIR=%PROJECT_ROOT%\lib

echo [1/3] Checking Java...
java -version 2>NUL
if errorlevel 1 (
    echo ERROR: Java not found. Install Java 11+ and add to PATH.
    exit /b 1
)
echo.

echo [2/3] Checking JavaFX SDK...
if not exist "%LIB_DIR%\javafx-sdk" (
    echo WARNING: JavaFX SDK not found at %LIB_DIR%\javafx-sdk
    echo Download from https://openjfx.io and extract to %LIB_DIR%\javafx-sdk
) else (
    echo JavaFX SDK found.
)
echo.

echo [3/3] Setting up database...
call "%SCRIPT_DIR%database.bat"
echo.

echo ============================================
echo  Video Viewer install complete.
echo ============================================
