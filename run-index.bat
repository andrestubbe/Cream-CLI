@echo off
setlocal enabledelayedexpansion

set "TARGET_DIR=%~1"
set "SEARCH_QUERY=%~2"

if "%TARGET_DIR%"=="" set "TARGET_DIR=C:\Users\andre\Documents\2026-06-14-Work-FastJava"
if "%SEARCH_QUERY%"=="" set "SEARCH_QUERY=FastTUI"

call mvn -q compile exec:java -Dexec.mainClass="cream.cli.test.CreamIndexTest" -Dexec.args="\"%TARGET_DIR%\" \"%SEARCH_QUERY%\""

echo.
pause
