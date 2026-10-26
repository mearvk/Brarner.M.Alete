@echo off
REM Build script for Brarner.M.Alete — Windows
REM Produces: Brarner.M.Alete.jar (the signal-processing engine + servers)
REM
REM Scope matches build.sh: builds the core engine only (bma\, Main, the master
REM and per-module BaseServers), excludes the optional ai\ and video\ trees, and
REM puts only jars\signal-processing on the classpath.

setlocal enabledelayedexpansion

set JAR_NAME=Brarner.M.Alete.jar
set SRC_DIR=source-code
set OUT_DIR=build\classes
set LIB_DIR=jars\signal-processing
set MAIN_CLASS=Main

if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"
if not exist build mkdir build

REM Collect engine sources, excluding ai\ and video\.
if exist build\sources.txt del /q build\sources.txt
for /r "%SRC_DIR%" %%F in (*.java) do (
    set "FP=%%F"
    echo !FP! | findstr /i /c:"\%SRC_DIR%\ai\" /c:"\%SRC_DIR%\video\" >nul
    if errorlevel 1 echo %%F>> build\sources.txt
)

REM Classpath: recurse through jars\signal-processing (build a ;-separated list).
set CLASSPATH=
for /r "%LIB_DIR%" %%J in (*.jar) do set "CLASSPATH=!CLASSPATH!%%J;"

REM Compile
javac -d "%OUT_DIR%" -cp "%CLASSPATH%" @build\sources.txt
if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)

REM Package with a runnable manifest
jar cfe "%JAR_NAME%" "%MAIN_CLASS%" -C "%OUT_DIR%" .
if errorlevel 1 (
    echo Packaging failed.
    exit /b 1
)

echo Built %JAR_NAME% (main class: %MAIN_CLASS%)
