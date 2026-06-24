@echo off
REM Video Viewer — Database setup (Windows)
set SCRIPT_DIR=%~dp0

echo Setting up Video Viewer database...
where mysql >NUL 2>&1
if errorlevel 1 (
    echo MySQL not found. Install MySQL and re-run, or run database.sql manually.
    exit /b 1
)
mysql -u root -p < "%SCRIPT_DIR%database.sql"
