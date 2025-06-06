
@echo off
REM Starte BarPOSSystem mit JavaFX-Unterstützung
setlocal
set JAVAFX_PATH=C:\javafx-sdk-21.0.7\lib

java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -jar BarPOSSystem.jar

pause
