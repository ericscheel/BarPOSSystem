
@echo off
REM Starte BarPOSSystem mit JavaFX-Unterstützung
setlocal
set JAVAFX_PATH=C:\Pfad\zu\javafx\lib

java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -jar BarPOSSystem.jar

pause
