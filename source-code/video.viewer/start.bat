@echo off
REM Video Viewer — Windows startup script
setlocal

set PROJECT_ROOT=%~dp0..\..
set LIB_DIR=%PROJECT_ROOT%\lib
set CLASS_DIR=%PROJECT_ROOT%\build\classes

set MODULE_PATH=--module-path "%LIB_DIR%\javafx-sdk\lib" --add-modules javafx.controls,javafx.web,javafx.media

java %MODULE_PATH% -cp "%CLASS_DIR%;%LIB_DIR%\*" video.viewer.Launcher %*
