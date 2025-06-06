@echo off
title BarPOS Installer Builder - Fixed
color 0A
echo ========================================
echo       Bar POS Installer Builder
echo          (JPackage + WiX - FIXED)
echo ========================================
echo.

REM Arbeitsverzeichnis
if not exist "C:\BarPOS-Build" mkdir "C:\BarPOS-Build"
cd /d "C:\BarPOS-Build"

echo 1. Pruefungen...

REM Alle Prüfungen...
if not exist "BarPOS.jar" (
    echo FEHLER: BarPOS.jar nicht gefunden!
    pause
    exit /b 1
)

if not exist "C:\javafx-sdk-21.0.7\lib" (
    echo FEHLER: JavaFX SDK nicht gefunden in C:\javafx-sdk-21\lib
    pause
    exit /b 1
)

echo Alle Dateien gefunden!
echo.
echo 2. Erstelle Installer mit korrigierten JavaFX-Parametern...
echo.

REM Alte Builds löschen
if exist "installer" rmdir /s /q "installer"

REM KORRIGIERTES JPackage mit expliziten JavaFX-Modulen
jpackage ^
  --input . ^
  --name "BarPOS" ^
  --main-jar BarPOS.jar ^
  --main-class com.barpos.ModernPOSSystem ^
  --type exe ^
  --dest installer ^
  --app-version 1.0 ^
  --vendor "Eric-Marcell Scheel" ^
  --description "Modernes Kassensystem fuer Bars und Restaurants" ^
  --copyright "Copyright (c) 2025" ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --win-menu-group "Bar POS System" ^
  --module-path "C:\javafx-sdk-21.0.7\lib" ^
  --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics,javafx.media,javafx.swing,javafx.web ^
  --java-options "-Xms256m" ^
  --java-options "-Xmx1024m" ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --java-options "-Djava.awt.headless=false" ^
  --java-options "--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED" ^
  --java-options "--add-exports=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED" ^
  --java-options "--add-opens=javafx.controls/javafx.scene.control=ALL-UNNAMED"

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo           ERFOLGREICH!
    echo ========================================
    echo.
    echo Installer: installer\BarPOS-1.0.exe
    echo.
    echo WICHTIG: Teste den Installer jetzt!
    echo.
    start explorer "installer"
) else (
    echo.
    echo FEHLER beim Erstellen!
    echo Pruefe:
    echo - JavaFX SDK Pfad korrekt?
    echo - JAR-Datei funktionsfaehig?
    echo - Genug Speicherplatz?
)

pause