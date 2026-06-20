@echo off
echo --- Setting Classpath ---
set JARS_DIR=%~dp0..\jars
set CP=.

for %%f in ("%JARS_DIR%\*.jar") do (
    set CP=!CP!;%%f
)

setlocal enabledelayedexpansion
set CP=.
for %%f in ("%JARS_DIR%\*.jar") do (
    set CP=!CP!;%%f
)

echo CLASSPATH=%CP%
endlocal & set CLASSPATH=%CP%

echo.
echo To use in this session, run:
echo   set CLASSPATH=%CP%
echo.
echo Or add to system environment via:
echo   setx CLASSPATH "%CP%"
echo.

REM Write a helper file that can be called to set CP in other scripts
echo @echo off > "%~dp0set_classpath.bat"
echo set CLASSPATH=. >> "%~dp0set_classpath.bat"

setlocal enabledelayedexpansion
for %%f in ("%JARS_DIR%\*.jar") do (
    echo set CLASSPATH=%%CLASSPATH%%;%%f >> "%~dp0set_classpath.bat"
)
endlocal

echo Generated set_classpath.bat for reuse.
