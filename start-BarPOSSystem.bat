@echo off
REM Verbesserte Startdatei für BarPOSSystem
echo ============================================
echo    BarPOSSystem Startup Script
echo ============================================
echo.

REM Java-Version prüfen
echo Pruefe Java-Installation...
java -version 2>nul
if errorlevel 1 (
    echo FEHLER: Java ist nicht installiert oder nicht im PATH verfuegbar!
    echo Bitte installieren Sie Java 11 oder hoeher.
    echo Download: https://adoptium.net/
    pause
    exit /b 1
)

REM JavaFX-Pfad setzen (anpassbar)
set JAVAFX_PATH=C:\javafx-sdk-21.0.7\lib

REM Prüfen ob JavaFX vorhanden ist
if not exist "%JAVAFX_PATH%" (
    echo WARNUNG: JavaFX-Pfad nicht gefunden: %JAVAFX_PATH%
    echo.
    echo Versuche ohne expliziten JavaFX-Pfad...
    echo Wenn das nicht funktioniert:
    echo 1. JavaFX von https://gluonhq.com/products/javafx/ herunterladen
    echo 2. Pfad in dieser Datei anpassen
    echo.
    
    REM Fallback: Ohne Module-Path versuchen
    echo Starte ohne Module-Path...
    java -jar BarPOSSystem.jar
    goto :end
)

REM JavaFX-Bibliotheken zum Klassenpfad hinzufügen
set JAVAFX_LIBS=%JAVAFX_PATH%\javafx.base.jar;%JAVAFX_PATH%\javafx.controls.jar;%JAVAFX_PATH%\javafx.fxml.jar;%JAVAFX_PATH%\javafx.graphics.jar;%JAVAFX_PATH%\javafx.media.jar;%JAVAFX_PATH%\javafx.swing.jar;%JAVAFX_PATH%\javafx.web.jar

echo Starte BarPOSSystem...
echo JavaFX-Pfad: %JAVAFX_PATH%
echo.

REM Zuerst mit Module-Path versuchen (Java 9+)
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -jar BarPOSSystem.jar 2>nul
if errorlevel 1 (
    echo Module-System nicht verfuegbar, versuche mit Classpath...
    
    REM Fallback: Mit Classpath (Java 8+)
    java -cp "BarPOSSystem.jar;%JAVAFX_LIBS%" com.barpos.ModernPOSSystem
    if errorlevel 1 (
        echo.
        echo FEHLER: Start fehlgeschlagen!
        echo.
        echo Moegliche Loesungen:
        echo 1. Java 11 oder hoeher installieren
        echo 2. JavaFX separat installieren und Pfad anpassen
        echo 3. Oracle JDK verwenden (enthaelt JavaFX bis Version 10)
        echo.
        echo Technische Details:
        echo - Java Version sollte 11+ sein
        echo - JavaFX muss separat installiert werden
        echo - Pfad muss korrekt gesetzt sein
    )
)

:end
echo.
echo ============================================
pause