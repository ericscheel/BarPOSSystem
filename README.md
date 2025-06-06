# 🍹 BarPOSSystem

BarPOSSystem ist ein modernes, einfach zu bedienendes Kassensystem für Bars und kleine Gastronomiebetriebe. Die Anwendung wurde in Java entwickelt und bietet eine intuitive JavaFX-Benutzeroberfläche zur Verwaltung von Produkten, Bestellungen, Benutzern und Transaktionen.

## ✨ Features
- 🖥️ Benutzerfreundliche Oberfläche
- 🛒 Verwaltung von Produkten und Preisen
- 👥 Benutzer- und Rechteverwaltung
- 📒 Kassenbuch und Transaktionsübersicht
- 🔒 PIN-Login für erhöhte Sicherheit

## ⚙️ Technische Voraussetzungen
- ☕ Java 21 oder höher
- 🎨 JavaFX (mindestens Version 21)
  - JavaFX muss separat installiert und dem Klassenpfad hinzugefügt werden.
  - Download: https://gluonhq.com/products/javafx/

## ⚙️ Installation & Ausführung
1. **Kompilieren:**
   - Die .class-Dateien befinden sich bereits im Ordner `bin/`.
   - Alternativ kann das Projekt mit folgendem Befehl neu kompiliert werden:
     ```bash
     javac -d bin src/module-info.java src/com/barpos/*.java
     ```
2. **JAR-Datei erstellen:**
   - Mit folgendem Befehl wird eine ausführbare JAR-Datei erzeugt:
     ```bash
     jar cfm BarPOSSystem.jar manifest.txt -C bin .
     ```
3. **Starten:**
   - Am einfachsten per Doppelklick auf die Datei `start-BarPOSSystem.bat` (Windows).
   - Alternativ im Terminal (JavaFX-Pfad anpassen!):
     ```bat
     java --module-path C:\Pfad\zu\javafx\lib --add-modules javafx.controls,javafx.fxml -jar BarPOSSystem.jar
     ```

## 🗂️ Projektstruktur
```
BarPOSSystem/
├── src/           # Quellcode
├── bin/           # Kompilierte .class-Dateien
├── manifest.txt   # Manifest für die JAR-Erstellung
├── BarPOSSystem.jar
├── start-BarPOSSystem.bat  # Windows-Startdatei
```

## 📦 Release
**BarPOSSystem 1.0 – Erstes offizielles Release**

Mit BarPOSSystem 1.0 veröffentlichen wir die erste stabile Version unseres modernen Kassensystems für Bars und kleine Gastronomiebetriebe! Die Anwendung bietet eine intuitive JavaFX-Oberfläche, Produkt- und Benutzerverwaltung, ein Kassenbuch sowie einen sicheren PIN-Login. Das System ist einfach zu installieren und kann flexibel erweitert werden. Java 21 und JavaFX werden benötigt.

Wir freuen uns über Feedback, Anregungen und Beiträge aus der Community!

## 📄 Lizenz
Dieses Projekt ist Open Source. Die Nutzung und Weiterentwicklung ist ausdrücklich erwünscht!

---
**Kontakt:**
Für Fragen oder Anregungen: [Deine E-Mail-Adresse hier einfügen]
