# English README (Short Version)

## 🍹 BarPOSSystem
BarPOSSystem is a modern, easy-to-use POS system for bars and small hospitality businesses. Built with Java and JavaFX, it offers an intuitive interface for managing products, orders, users, and transactions.

### Features
- User-friendly interface
- Product and price management
- User and permission management
- Cash book and transaction overview
- PIN login for security

### Requirements
- Java 21 or higher
- JavaFX 21 or higher (must be installed separately)

### Getting Started
1. Compile the project:
   ```bash
   javac -d bin src/module-info.java src/com/barpos/*.java
   ```
2. Create the JAR:
   ```bash
   jar cfm BarPOSSystem.jar manifest.txt -C bin .
   ```
3. Start the app (adjust JavaFX path!):
   ```bash
   java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar BarPOSSystem.jar
   ```

### License
MIT License

---
For questions or suggestions: [Your email here]
