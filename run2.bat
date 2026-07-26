@echo off
chcp 65001 >nul

if not exist "%JAVA_HOME%\bin\javac.exe" (
    if exist "C:\Program Files\Java\jdk-21.0.12\bin\javac.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12"
    if exist "C:\Program Files\Java\latest\bin\javac.exe" set "JAVA_HOME=C:\Program Files\Java\latest"
    if exist "C:\Program Files\Java\jdk-25.0.3\bin\javac.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-25.0.3"
)
set "PATH=%JAVA_HOME%\bin;%PATH%"

if not exist "cp.txt" (
    echo [INFO] cp.txt not found. Generating classpath once...
    call mvn dependency:build-classpath "-Dmdep.outputFile=cp.txt" "-DincludeScope=runtime" -q
)

for /f "usebackq delims=" %%i in ("cp.txt") do set "CP=%%i"
java -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 --enable-native-access=ALL-UNNAMED -cp "target\classes;%CP%" cream.cli.Client
