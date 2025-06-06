@echo off
title BarPOS Installer Builder - Fixed Paths
color 0A

echo ========================================
echo       Bar POS Installer Builder
echo ========================================
echo.

REM Expliziter Pfad zur JAR-Datei
set "JAR_PATH=%~dp0BarPOS.jar"
set "WORK_DIR=%~dp0"

echo Arbeitsverzeichnis: %WORK_DIR%
echo JAR-Pfad: %JAR_PATH%
echo.

REM Prüfungen mit absoluten Pfaden
if not exist "%JAR_PATH%" (
    echo ❌ FEHLER: BarPOS.jar nicht gefunden!
    echo Gesucht in: %JAR_PATH%
    echo.
    echo LÖSUNG:
    echo 1. BarPOS.jar ins gleiche Verzeichnis wie dieses Script legen
    echo 2. Oder aus Eclipse neu exportieren nach: %WORK_DIR%
    echo.
    echo Aktuelle Dateien im Verzeichnis:
    dir /b "%WORK_DIR%"
    echo.
    pause
    exit /b 1
)

echo ✓ BarPOS.jar gefunden: %JAR_PATH%

if not exist "C:\javafx-sdk-21.0.7\lib" (
    echo ❌ FEHLER: JavaFX SDK nicht gefunden!
    echo Gesucht in: C:\javafx-sdk-21\lib
    pause
    exit /b 1
)

echo ✓ JavaFX SDK gefunden
echo.

REM WiX testen
candle /? >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ FEHLER: WiX Tools nicht gefunden!
    echo PATH-Variable korrekt gesetzt?
    pause
    exit /b 1
)

echo ✓ WiX Tools gefunden
echo.

echo Erstelle Installer...
echo JAR-Datei: %JAR_PATH%
echo Zielverzeichnis: %WORK_DIR%installer\
echo.

REM Alte Builds löschen
if exist "%WORK_DIR%installer" rmdir /s /q "%WORK_DIR%installer"

REM JPackage mit absoluten Pfaden
jpackage ^
  --input "%WORK_DIR%" ^
  --name "BarPOS" ^
  --main-jar "BarPOS.jar" ^
  --main-class com.barpos.ModernPOSSystem ^
  --type exe ^
  --dest "%WORK_DIR%installer" ^
  --app-version 1.0 ^
  --vendor "Eric Scheel" ^
  --description "Modernes Kassensystem" ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --win-menu-group "Bar POS System" ^
  --module-path "C:\javafx-sdk-21.0.7\lib" ^
  --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics ^
  --java-options "-Xmx1024m" ^
  --java-options "-Dfile.encoding=UTF-8"

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo           ✅ ERFOLGREICH!
    echo ========================================
    echo.
    echo Installer erstellt: %WORK_DIR%installer\BarPOS-1.0.exe
    echo.
    start explorer "%WORK_DIR%installer"
) else (
    echo.
    echo ❌ FEHLER beim Erstellen des Installers!
    echo Fehlercode: %errorlevel%
)

pause