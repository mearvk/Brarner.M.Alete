@echo off
echo --- Port Check ---
echo.

echo Checking port 3306 (MySQL)...
netstat -an | findstr ":3306 " >nul 2>&1
if %errorlevel%==0 (
    echo   [OPEN] Port 3306 is in use
    netstat -ano | findstr ":3306 "
) else (
    echo   [FREE] Port 3306 is available
)
echo.

echo Checking port 8080 (HTTP/App Server)...
netstat -an | findstr ":8080 " >nul 2>&1
if %errorlevel%==0 (
    echo   [OPEN] Port 8080 is in use
    netstat -ano | findstr ":8080 "
) else (
    echo   [FREE] Port 8080 is available
)
echo.

echo Checking port 443 (HTTPS)...
netstat -an | findstr ":443 " >nul 2>&1
if %errorlevel%==0 (
    echo   [OPEN] Port 443 is in use
) else (
    echo   [FREE] Port 443 is available
)
echo.

echo Checking port 8443 (HTTPS Alt)...
netstat -an | findstr ":8443 " >nul 2>&1
if %errorlevel%==0 (
    echo   [OPEN] Port 8443 is in use
) else (
    echo   [FREE] Port 8443 is available
)
echo.

echo Checking port 1099 (RMI/JMX)...
netstat -an | findstr ":1099 " >nul 2>&1
if %errorlevel%==0 (
    echo   [OPEN] Port 1099 is in use
) else (
    echo   [FREE] Port 1099 is available
)
