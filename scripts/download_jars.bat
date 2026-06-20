@echo off
set JARS_DIR=%~dp0..\jars

echo Downloading JDSP 3.1.1 (Digital Signal Processing)...
curl.exe -L -s -o "%JARS_DIR%\jdsp-3.1.1.jar" "https://repo1.maven.org/maven2/com/github/psambit9791/jdsp/3.1.1/jdsp-3.1.1.jar"

echo Downloading jSciPy 3.1.7 (Scientific Computing / Signal Processing)...
curl.exe -L -s -o "%JARS_DIR%\jscipy-3.1.7.jar" "https://repo1.maven.org/maven2/io/github/hissain/jscipy/3.1.7/jscipy-3.1.7.jar"

echo Downloading TarsosDSP Core 2.5 (Audio/Real-Time Signal Processing)...
curl.exe -L -s -o "%JARS_DIR%\TarsosDSP-core-2.5.jar" "https://mvn.0110.be/releases/be/tarsos/dsp/core/2.5/core-2.5.jar"

echo Downloading TarsosDSP JVM 2.5 (Audio I/O for JVM)...
curl.exe -L -s -o "%JARS_DIR%\TarsosDSP-jvm-2.5.jar" "https://mvn.0110.be/releases/be/tarsos/dsp/jvm/2.5/jvm-2.5.jar"

echo Downloading Apache Commons Math 3.6.1 (Math/FFT utilities)...
curl.exe -L -s -o "%JARS_DIR%\commons-math3-3.6.1.jar" "https://repo1.maven.org/maven2/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar"

echo.
echo Done. JARs downloaded to %JARS_DIR%
dir "%JARS_DIR%\*.jar"
