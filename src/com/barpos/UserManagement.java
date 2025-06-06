package com.barpos;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.*;

// Benutzer-Rollen Enum
enum UserRole {
    NUTZER("Nutzer", "Grundlegende Kassenfunktionen"),
    MANAGER("Manager", "Kassenfunktionen + Kassenbuch"),
    TECHNIKER("Techniker", "Vollzugriff auf alle Funktionen");
    
    private final String displayName;
    private final String description;
    
    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}

// Benutzer-Klasse mit PIN
class User {
    private String name;
    private String pin;
    private UserRole role;
    private boolean isActive;
    
    public User(String name, String pin, UserRole role) {
        this.name = name;
        this.pin = pin;
        this.role = role;
        this.isActive = true;
    }
    
    public boolean verifyPIN(String inputPin) {
        return pin.equals(inputPin);
    }
    
    // Getter und Setter
    public String getName() { return name; }
    public String getPIN() { return pin; }
    public UserRole getRole() { return role; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    public void setRole(UserRole role) { this.role = role; }
    public void setPIN(String pin) { this.pin = pin; }
}

// PIN-Manager
class PINManager {
    private static PINManager instance;
    private Map<String, User> pinToUserMap;
    private User currentUser;
    
    private PINManager() {
        pinToUserMap = new HashMap<>();
        initializeDefaultUsers();
    }
    
    public static PINManager getInstance() {
        if (instance == null) {
            instance = new PINManager();
        }
        return instance;
    }
    
    private void initializeDefaultUsers() {
        // Standard-Benutzer mit PINs
        addUser(new User("Eric", "1234", UserRole.NUTZER));
        addUser(new User("Sarah", "5678", UserRole.MANAGER));
        addUser(new User("Admin", "0000", UserRole.TECHNIKER));
        addUser(new User("Lisa", "9999", UserRole.NUTZER));
        addUser(new User("Max", "1111", UserRole.MANAGER));
    }
    
    private void addUser(User user) {
        pinToUserMap.put(user.getPIN(), user);
    }
    
    public boolean loginWithPIN(String pin) {
        User user = pinToUserMap.get(pin);
        if (user != null && user.isActive()) {
            currentUser = user;
            return true;
        }
        return false;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    // Berechtigungsprüfungen
    public boolean canAccessBackend() {
        return isLoggedIn() && currentUser.getRole() == UserRole.TECHNIKER;
    }
    
    public boolean canAccessCashBook() {
        return isLoggedIn() && 
               (currentUser.getRole() == UserRole.MANAGER || 
                currentUser.getRole() == UserRole.TECHNIKER);
    }
    
    public boolean canManageUsers() {
        return isLoggedIn() && currentUser.getRole() == UserRole.TECHNIKER;
    }
    
    // Benutzerverwaltung
    public boolean addNewUser(String name, String pin, UserRole role) {
        if (pinToUserMap.containsKey(pin)) {
            return false; // PIN bereits vergeben
        }
        addUser(new User(name, pin, role));
        return true;
    }
    
    public boolean removeUser(String pin) {
        if (pin.equals("0000")) {
            return false; // Admin kann nicht gelöscht werden
        }
        return pinToUserMap.remove(pin) != null;
    }
    
    public Collection<User> getAllUsers() {
        return pinToUserMap.values();
    }
    
    public User getUserByPIN(String pin) {
        return pinToUserMap.get(pin);
    }
}

// PIN-Login-Dialog mit Touch-Tastatur
class PINLoginDialog {
    private Stage stage;
    private TextField pinField;
    private boolean loginSuccessful = false;
    private String currentPIN = "";
    
    public PINLoginDialog() {
        createDialog();
    }
    
    private void createDialog() {
        stage = new Stage();
        stage.setTitle("Bar POS - PIN Anmeldung");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        
        VBox mainLayout = new VBox(30);
        mainLayout.setPadding(new Insets(40));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #0d1117, #161b22);");
        
        // Logo/Titel
        Label titleLabel = new Label("🍺 Gifthütte Kassensystem");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: #00ff88; -fx-effect: dropshadow(gaussian, #00ff88, 15, 0, 0, 0);");
        
        // PIN-Eingabe
        VBox pinBox = new VBox(20);
        pinBox.setAlignment(Pos.CENTER);
        pinBox.setStyle("-fx-background-color: linear-gradient(to bottom, #21262d, #161b22); " +
                       "-fx-padding: 40; -fx-border-radius: 25; -fx-background-radius: 25; " +
                       "-fx-border-color: #30363d; -fx-border-width: 2;");
        pinBox.setPrefWidth(500);
        
        Label pinLabel = new Label("PIN eingeben");
        pinLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        pinLabel.setStyle("-fx-text-fill: #c9d1d9;");
        
        // PIN-Anzeige
        pinField = new TextField();
        pinField.setPromptText("••••");
        pinField.setPrefHeight(70);
        pinField.setPrefWidth(300);
        pinField.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        pinField.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #00ff88; " +
                         "-fx-border-color: #30363d; -fx-border-radius: 15; " +
                         "-fx-background-radius: 15; -fx-padding: 20; " +
                         "-fx-prompt-text-fill: #6e7681; -fx-alignment: center;");
        pinField.setEditable(false); // Nur über Touch-Tastatur
        
        // Touch-Tastatur
        GridPane keypad = createTouchKeypad();
        
        // Aktions-Buttons
        HBox actionButtons = new HBox(20);
        actionButtons.setAlignment(Pos.CENTER);
        
        Button loginButton = createStyledButton("Anmelden", "#00d488", 150, 60);
        Button exitButton = createStyledButton("Beenden", "#e74c3c", 150, 60);
        
        loginButton.setOnAction(e -> performLogin());
        exitButton.setOnAction(e -> System.exit(0));
        
        actionButtons.getChildren().addAll(loginButton, exitButton);
        
        pinBox.getChildren().addAll(pinLabel, pinField, keypad, actionButtons);
        
        // Nur die PIN-Box, keine Test-User-Info mehr
        mainLayout.getChildren().addAll(titleLabel, pinBox);
        
        Scene scene = new Scene(mainLayout, 600, 650); // Kleiner ohne Info-Box
        stage.setScene(scene);
    }
    
    private GridPane createTouchKeypad() {
        GridPane keypad = new GridPane();
        keypad.setHgap(15);
        keypad.setVgap(15);
        keypad.setAlignment(Pos.CENTER);
        keypad.setPadding(new Insets(20, 0, 20, 0));
        
        // Zahlen 1-9
        for (int i = 1; i <= 9; i++) {
            Button numBtn = createKeypadButton(String.valueOf(i));
            final String number = String.valueOf(i);
            numBtn.setOnAction(e -> addDigit(number));
            
            int row = (i - 1) / 3;
            int col = (i - 1) % 3;
            keypad.add(numBtn, col, row);
        }
        
        // Null, Löschen
        Button clearBtn = createKeypadButton("C");
        clearBtn.setStyle(clearBtn.getStyle().replace("#6c5ce7", "#e74c3c"));
        clearBtn.setOnAction(e -> clearPIN());
        keypad.add(clearBtn, 0, 3);
        
        Button zeroBtn = createKeypadButton("0");
        zeroBtn.setOnAction(e -> addDigit("0"));
        keypad.add(zeroBtn, 1, 3);
        
        Button deleteBtn = createKeypadButton("⌫");
        deleteBtn.setStyle(deleteBtn.getStyle().replace("#6c5ce7", "#f39c12"));
        deleteBtn.setOnAction(e -> deleteLastDigit());
        keypad.add(deleteBtn, 2, 3);
        
        return keypad;
    }
    
    private Button createKeypadButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(80, 80);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        btn.setStyle("-fx-background-color: linear-gradient(to bottom, #6c5ce7, #5b4bd1); " +
                    "-fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15; " +
                    "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);");
        
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #a29bfe, #8b82f7); " +
            "-fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(108,92,231,0.4), 12, 0, 0, 3);"));
        
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #6c5ce7, #5b4bd1); " +
            "-fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);"));
        
        return btn;
    }
    
    private void addDigit(String digit) {
        if (currentPIN.length() < 4) {
            currentPIN += digit;
            updatePINDisplay();
        }
    }
    
    private void deleteLastDigit() {
        if (!currentPIN.isEmpty()) {
            currentPIN = currentPIN.substring(0, currentPIN.length() - 1);
            updatePINDisplay();
        }
    }
    
    private void clearPIN() {
        currentPIN = "";
        updatePINDisplay();
    }
    
    private void updatePINDisplay() {
        String display = "";
        for (int i = 0; i < currentPIN.length(); i++) {
            display += "•";
        }
        for (int i = currentPIN.length(); i < 4; i++) {
            display += "○";
        }
        pinField.setText(display);
    }
    
    private VBox createInfoBox() {
        VBox infoBox = new VBox(15);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setStyle("-fx-background-color: rgba(33, 38, 45, 0.8); " +
                        "-fx-padding: 25; -fx-border-radius: 20; -fx-background-radius: 20;");
        
        Label infoLabel = new Label("Beispiel-PINs:");
        infoLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        infoLabel.setStyle("-fx-text-fill: #fdcb6e;");
        
        String[] credentials = {
            "Eric (Kassier): 1234",
            "Sarah (Manager): 5678", 
            "Admin (Techniker): 0000",
            "Lisa (Kassier): 9999",
            "Max (Manager): 1111"
        };
        
        VBox credBox = new VBox(8);
        for (String cred : credentials) {
            Label credLabel = new Label("🔑 " + cred);
            credLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 14px;");
            credBox.getChildren().add(credLabel);
        }
        
        infoBox.getChildren().addAll(infoLabel, credBox);
        return infoBox;
    }
    
    private Button createStyledButton(String text, String color, int width, int height) {
        Button btn = new Button(text);
        btn.setPrefWidth(width);
        btn.setPrefHeight(height);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        
        String baseStyle = "-fx-background-color: linear-gradient(to bottom, " + color + ", " + 
                          adjustBrightness(color) + "); " +
                          "-fx-text-fill: white; -fx-border-radius: 12; -fx-background-radius: 12; " +
                          "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);";
        
        btn.setStyle(baseStyle);
        return btn;
    }
    
    private String adjustBrightness(String color) {
        switch (color) {
            case "#00d488": return "#00b574";
            case "#e74c3c": return "#c0392b";
            default: return color;
        }
    }
    
    private void performLogin() {
        if (currentPIN.length() != 4) {
            showAlert("Fehler", "Bitte geben Sie eine 4-stellige PIN ein.");
            return;
        }
        
        PINManager pinManager = PINManager.getInstance();
        if (pinManager.loginWithPIN(currentPIN)) {
            loginSuccessful = true;
            stage.close();
        } else {
            showAlert("Anmeldung fehlgeschlagen", 
                     "Ungültige PIN.\n\nBitte versuchen Sie es erneut.");
            clearPIN();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Dark Theme für Alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #21262d; -fx-border-color: #30363d;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #c9d1d9;");
        
        alert.showAndWait();
    }
    
    public boolean showAndWait() {
        stage.showAndWait();
        return loginSuccessful;
    }
}

// Benutzerverwaltungs-Dialog für PIN-System
class UserManagementDialog {
    private Stage stage;
    private TableView<User> userTable;
    private PINManager pinManager;
    
    public UserManagementDialog() {
        this.pinManager = PINManager.getInstance();
        createDialog();
    }
    
    private void createDialog() {
        stage = new Stage();
        stage.setTitle("PIN-Benutzerverwaltung");
        stage.setWidth(900);
        stage.setHeight(700);
        
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #0d1117;");
        
        // Header
        HBox header = new HBox();
        header.setPadding(new Insets(25));
        header.setStyle("-fx-background-color: #21262d; -fx-border-color: #30363d; -fx-border-width: 0 0 2 0;");
        
        Label titleLabel = new Label("PIN-Benutzerverwaltung");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: #00ff88;");
        
        header.getChildren().add(titleLabel);
        mainLayout.setTop(header);
        
        // Center - Tabelle
        VBox centerContent = new VBox(25);
        centerContent.setPadding(new Insets(25));
        
        // Benutzer-Tabelle mit verbesserter Lesbarkeit
        userTable = new TableView<>();
        userTable.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; " +
                          "-fx-control-inner-background: #161b22; -fx-selection-bar: #00d488; " +
                          "-fx-selection-bar-non-focused: #00d488;");
        userTable.setPrefHeight(450);
        
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(200);
        nameCol.setStyle("-fx-text-fill: #c9d1d9;");
        
        TableColumn<User, String> pinCol = new TableColumn<>("PIN");
        pinCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty("••••"));
        pinCol.setPrefWidth(100);
        pinCol.setStyle("-fx-text-fill: #c9d1d9;");
        
        TableColumn<User, String> roleCol = new TableColumn<>("Rolle");
        roleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole().getDisplayName()));
        roleCol.setPrefWidth(150);
        roleCol.setStyle("-fx-text-fill: #c9d1d9;");
        
        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().isActive() ? "Aktiv" : "Inaktiv"));
        statusCol.setPrefWidth(100);
        statusCol.setStyle("-fx-text-fill: #c9d1d9;");
        
        // Column-Header styling
        nameCol.setStyle("-fx-background-color: #30363d; -fx-text-fill: #c9d1d9; -fx-font-weight: bold;");
        pinCol.setStyle("-fx-background-color: #30363d; -fx-text-fill: #c9d1d9; -fx-font-weight: bold;");
        roleCol.setStyle("-fx-background-color: #30363d; -fx-text-fill: #c9d1d9; -fx-font-weight: bold;");
        statusCol.setStyle("-fx-background-color: #30363d; -fx-text-fill: #c9d1d9; -fx-font-weight: bold;");
        
        TableColumn<User, Void> actionCol = new TableColumn<>("Aktionen");
        actionCol.setStyle("-fx-background-color: #30363d; -fx-text-fill: #c9d1d9; -fx-font-weight: bold;");
        actionCol.setCellFactory(col -> new TableCell<User, Void>() {
            private final Button editBtn = new Button("Bearbeiten");
            private final Button deleteBtn = new Button("Löschen");
            
            {
                editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 8 16;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 16;");
                
                editBtn.setOnAction(e -> editUser(getTableRow().getItem()));
                deleteBtn.setOnAction(e -> deleteUser(getTableRow().getItem()));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10);
                    buttons.getChildren().addAll(editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });
        actionCol.setPrefWidth(200);
        
        userTable.getColumns().addAll(nameCol, pinCol, roleCol, statusCol, actionCol);
        refreshUserTable();
        
        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        Button addUserBtn = new Button("Benutzer hinzufügen");
        addUserBtn.setStyle("-fx-background-color: #00d488; -fx-text-fill: white; " +
                           "-fx-padding: 12 24; -fx-font-size: 14px; -fx-border-radius: 8; " +
                           "-fx-background-radius: 8;");
        addUserBtn.setOnAction(e -> addNewUser());
        
        Button closeBtn = new Button("Schließen");
        closeBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; " +
                         "-fx-padding: 12 24; -fx-font-size: 14px; -fx-border-radius: 8; " +
                         "-fx-background-radius: 8;");
        closeBtn.setOnAction(e -> stage.close());
        
        buttonBox.getChildren().addAll(addUserBtn, closeBtn);
        
        centerContent.getChildren().addAll(userTable, buttonBox);
        mainLayout.setCenter(centerContent);
        
        Scene scene = new Scene(mainLayout);
        stage.setScene(scene);
    }
    
    private void refreshUserTable() {
        userTable.getItems().clear();
        userTable.getItems().addAll(pinManager.getAllUsers());
    }
    
    private void addNewUser() {
        // Dialog für neue Benutzer mit verbesserter Lesbarkeit
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Neuen Benutzer hinzufügen");
        dialog.setHeaderText("Benutzerdaten eingeben");
        
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #21262d; -fx-border-color: #30363d; " +
                           "-fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        // Header-Text Style
        Label headerLabel = (Label) dialogPane.lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        // Labels mit besserer Sichtbarkeit
        Label nameLabel = new Label("Name:");
        nameLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label pinLabel = new Label("PIN:");
        pinLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label roleLabel = new Label("Rolle:");
        roleLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Name eingeben");
        nameField.setPrefWidth(200);
        nameField.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #c9d1d9; " +
                          "-fx-border-color: #30363d; -fx-border-radius: 8; " +
                          "-fx-background-radius: 8; -fx-padding: 8; " +
                          "-fx-prompt-text-fill: #6e7681; -fx-font-size: 14px;");
        
        TextField pinField = new TextField();
        pinField.setPromptText("4-stellige PIN");
        pinField.setPrefWidth(200);
        pinField.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #c9d1d9; " +
                         "-fx-border-color: #30363d; -fx-border-radius: 8; " +
                         "-fx-background-radius: 8; -fx-padding: 8; " +
                         "-fx-prompt-text-fill: #6e7681; -fx-font-size: 14px;");
        
        ComboBox<UserRole> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(UserRole.values());
        roleCombo.setValue(UserRole.NUTZER);
        roleCombo.setPrefWidth(200);
        roleCombo.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #c9d1d9; " +
                          "-fx-border-color: #30363d; -fx-border-radius: 8; " +
                          "-fx-background-radius: 8;");
        
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(pinLabel, 0, 1);
        grid.add(pinField, 1, 1);
        grid.add(roleLabel, 0, 2);
        grid.add(roleCombo, 1, 2);
        
        dialogPane.setContent(grid);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Button-Styling
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setStyle("-fx-background-color: #00d488; -fx-text-fill: white; " +
                         "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 16; " +
                         "-fx-font-weight: bold;");
        
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; " +
                             "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 16;");
        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText().trim();
            String pin = pinField.getText().trim();
            UserRole role = roleCombo.getValue();
            
            if (!name.isEmpty() && pin.length() == 4 && pin.matches("\\d{4}")) {
                if (pinManager.addNewUser(name, pin, role)) {
                    refreshUserTable();
                    showAlert("Erfolg", "Benutzer '" + name + "' wurde hinzugefügt.");
                } else {
                    showAlert("Fehler", "PIN bereits vergeben. Bitte andere PIN wählen.");
                }
            } else {
                showAlert("Fehler", "Bitte gültigen Namen und 4-stellige PIN eingeben.");
            }
        }
    }
    
    private void editUser(User user) {
        if (user != null) {
            showAlert("Info", "Benutzer: " + user.getName() + 
                     "\nPIN: ••••\nRolle: " + user.getRole().getDisplayName() +
                     "\n\nBearbeitung wird in einer zukünftigen Version implementiert.");
        }
    }
    
    private void deleteUser(User user) {
        if (user != null && !user.getPIN().equals("0000")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Benutzer löschen");
            alert.setHeaderText("Bestätigung erforderlich");
            alert.setContentText("Möchten Sie den Benutzer '" + user.getName() + "' wirklich löschen?");
            
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: #21262d; -fx-border-color: #30363d;");
            dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #c9d1d9;");
            dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: #c9d1d9;");
            
            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                pinManager.removeUser(user.getPIN());
                refreshUserTable();
                showAlert("Erfolg", "Benutzer '" + user.getName() + "' wurde gelöscht.");
            }
        } else if (user != null && user.getPIN().equals("0000")) {
            showAlert("Fehler", "Admin-Benutzer kann nicht gelöscht werden.");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #21262d; -fx-border-color: #30363d; " +
                           "-fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        // Content-Text lesbar machen
        Label contentLabel = (Label) dialogPane.lookup(".content.label");
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px;");
        }
        
        // OK-Button stylen
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setStyle("-fx-background-color: #00d488; -fx-text-fill: white; " +
                             "-fx-border-radius: 6; -fx-background-radius: 6; " +
                             "-fx-padding: 8 16; -fx-font-weight: bold;");
        }
        
        alert.showAndWait();
    }
    
    public void show() {
        stage.show();
    }
}