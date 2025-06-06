# 🍹 BarPOSSystem

BarPOSSystem ist ein modernes, einfach zu bedienendes Kassensystem für Bars und kleine Gastronomiebetriebe. Die Anwendung wurde in Java entwickelt und bietet eine intuitive Benutzeroberfläche zur Verwaltung von Produkten, Bestellungen, Benutzern und Transaktionen.

## ✨ Features
- 🖥️ Benutzerfreundliche Oberfläche
- 🛒 Verwaltung von Produkten und Preisen
- 👥 Benutzer- und Rechteverwaltung
- 📒 Kassenbuch und Transaktionsübersicht
- 🔒 PIN-Login für erhöhte Sicherheit

## ⚙️ Installation
1. **Voraussetzungen:**
   - ☕ Java 21 oder höher muss installiert sein.
2. **Kompilieren:**
   - Die .class-Dateien befinden sich bereits im Ordner `bin/`.
   - Alternativ kann das Projekt mit folgendem Befehl neu kompiliert werden:
     ```bash
     javac -d bin src/module-info.java src/com/barpos/*.java
     ```
3. **JAR-Datei erstellen:**
   - Mit folgendem Befehl wird eine ausführbare JAR-Datei erzeugt:
     ```bash
     jar cfm BarPOSSystem.jar manifest.txt -C bin .
     ```

## ⚙️ Technische Voraussetzungen
- ☕ Java 21 oder höher
- 🎨 JavaFX (mindestens Version 21)
  - JavaFX muss separat installiert und dem Klassenpfad hinzugefügt werden.
  - Download: https://gluonhq.com/products/javafx/
  - Beispiel für das Ausführen mit JavaFX:
    ```bash
    java --module-path /pfad/zu/javafx/lib --add-modules javafx.controls,javafx.fxml -jar BarPOSSystem.jar
    ```

## ▶️ Ausführen
Das Programm kann mit folgendem Befehl gestartet werden:
```bash
java -jar BarPOSSystem.jar
```

## 🗂️ Projektstruktur
```
BarPOSSystem/
├── src/           # Quellcode
├── bin/           # Kompilierte .class-Dateien
├── manifest.txt   # Manifest für die JAR-Erstellung
├── BarPOSSystem.jar
```

## 📄 Lizenz
Dieses Projekt ist Open Source. Die Nutzung und Weiterentwicklung ist ausdrücklich erwünscht!

---
**Kontakt:**
Für Fragen oder Anregungen: [Deine E-Mail-Adresse hier einfügen]
