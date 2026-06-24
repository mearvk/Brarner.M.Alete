@echo off
REM Video Viewer — Compile script (Windows)
setlocal

set SCRIPT_DIR=%~dp0
set PROJECT_ROOT=%SCRIPT_DIR%..\..
set LIB_DIR=%PROJECT_ROOT%\lib
set OUT_DIR=%PROJECT_ROOT%\build\classes

if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

echo Compiling Video Viewer...
javac -d "%OUT_DIR%" -cp "%LIB_DIR%\*;%LIB_DIR%\javafx-sdk\lib\*" "%SCRIPT_DIR%Launcher.java" "%SCRIPT_DIR%VideoViewer.java"
if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)
echo Video Viewer compiled successfully.
