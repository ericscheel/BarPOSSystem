package com.barpos;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ModernPOSSystem extends Application {
    
    // Datenstrukturen
    private Map<String, List<Product>> categories = new HashMap<>();
    private List<OrderItem> currentOrder = new ArrayList<>();
    private Map<String, List<OrderItem>> parkedOrders = new HashMap<>();
    private List<Transaction> cashBook = new ArrayList<>();
    private double totalAmount = 0.0;
    
    // UI Komponenten
    private VBox orderDisplay;
    private Label totalLabel;
    private Label receivedLabel;
    private Label changeLabel;
    private TextField paymentField;
    private ComboBox<String> parkedOrdersCombo;
    private GridPane categoryButtons;
    private GridPane quickButtonsGrid;
    private Label quickButtonsLabel;
    private RadioButton cashPayment;
    private RadioButton cardPayment;
    
    @Override
    public void start(Stage primaryStage) {
        // PIN-Login-Dialog anzeigen
        PINLoginDialog pinLoginDialog = new PINLoginDialog();
        if (!pinLoginDialog.showAndWait()) {
            // Login abgebrochen oder fehlgeschlagen
            System.exit(0);
            return;
        }
        
        initializeData();
        
        primaryStage.setTitle("Gifthütte - Kassensystem - Touch Edition");
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(false);
        
        // Hauptlayout mit modernem Dark Theme
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #1a1a1a;");
        
        // Linke Seite - Kategorien und Produkte
        VBox leftPanel = createLeftPanel();
        
        // Mitte - Aktuelle Bestellung
        VBox centerPanel = createCenterPanel();
        
        // Rechte Seite - Zahlung und Funktionen
        VBox rightPanel = createRightPanel();
        
        mainLayout.setLeft(leftPanel);
        mainLayout.setCenter(centerPanel);
        mainLayout.setRight(rightPanel);
        
        // Top - Navigation und geparkte Bestellungen
        HBox topPanel = createTopPanel();
        mainLayout.setTop(topPanel);
        
        Scene scene = new Scene(mainLayout, 1600, 900);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void initializeData() {
        // Beispiel-Kategorien und Produkte für eine Bar
        List<Product> drinks = Arrays.asList(
            new Product("Bier 0,5L", 4.50),
            new Product("Weizen 0,5L", 4.80),
            new Product("Cola 0,3L", 3.20),
            new Product("Wasser 0,3L", 2.50)
        );
        
        List<Product> cocktails = Arrays.asList(
            new Product("Mojito", 8.50),
            new Product("Caipirinha", 7.80),
            new Product("Long Island", 9.50),
            new Product("Cuba Libre", 6.90)
        );
        
        List<Product> shots = Arrays.asList(
            new Product("Vodka", 3.50),
            new Product("Tequila", 4.00),
            new Product("Jägermeister", 3.80),
            new Product("Sambuca", 4.20)
        );
        
        List<Product> snacks = Arrays.asList(
            new Product("Erdnüsse", 2.50),
            new Product("Chips", 3.00),
            new Product("Oliven", 4.50),
            new Product("Käsewürfel", 5.00)
        );
        
        categories.put("Getränke", drinks);
        categories.put("Cocktails", cocktails);
        categories.put("Shots", shots);
        categories.put("Snacks", snacks);
    }
    
    private HBox createTopPanel() {
        HBox topPanel = new HBox(30);
        topPanel.setPadding(new Insets(20));
        topPanel.setStyle("-fx-background-color: linear-gradient(to right, #0d1117, #161b22); " +
                         "-fx-border-color: #30363d; -fx-border-width: 0 0 2 0;");
        topPanel.setAlignment(Pos.CENTER_LEFT);
        topPanel.setPrefHeight(80);
        
        // Benutzer-Info anzeigen
        PINManager pinManager = PINManager.getInstance();
        User currentUser = pinManager.getCurrentUser();
        
        VBox userInfo = new VBox(2);
        Label titleLabel = new Label("Gifthütte - Kassensystem");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: #00ff88; -fx-effect: dropshadow(gaussian, #00ff88, 15, 0, 0, 0);");
        
        Label userLabel = new Label("👤 " + currentUser.getName() + " (" + currentUser.getRole().getDisplayName() + ")");
        userLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        userLabel.setStyle("-fx-text-fill: #c9d1d9;");
        
        userInfo.getChildren().addAll(titleLabel, userLabel);
        
        // Geparkte Bestellungen
        Label parkedLabel = new Label("Geparkte Bestellungen:");
        parkedLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 16px;");
        
        parkedOrdersCombo = new ComboBox<>();
        parkedOrdersCombo.setPromptText("Bestellung auswählen");
        parkedOrdersCombo.setPrefWidth(250);
        parkedOrdersCombo.setPrefHeight(50);
        parkedOrdersCombo.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; " +
                                  "-fx-border-color: #30363d; -fx-border-radius: 12; " +
                                  "-fx-background-radius: 12; -fx-font-size: 14px;");
        
        Button loadParkedButton = createTouchButton("Laden", "#00d488", "#00ff88", 120, 50);
        loadParkedButton.setOnAction(e -> loadParkedOrder());
        
        // Geschützte Buttons basierend auf Berechtigungen
        HBox protectedButtons = new HBox(15);
        
        if (pinManager.canAccessBackend()) {
            Button backendButton = createTouchButton("Backend", "#6c5ce7", "#a29bfe", 120, 50);
            backendButton.setOnAction(e -> openBackend());
            protectedButtons.getChildren().add(backendButton);
        }
        
        if (pinManager.canAccessCashBook()) {
            Button cashBookButton = createTouchButton("Kassenbuch", "#fdcb6e", "#f39c12", 140, 50);
            cashBookButton.setOnAction(e -> showCashBook());
            protectedButtons.getChildren().add(cashBookButton);
        }
        
        if (pinManager.canManageUsers()) {
            Button userMgmtButton = createTouchButton("Benutzer", "#8e44ad", "#9b59b6", 120, 50);
            userMgmtButton.setOnAction(e -> openUserManagement());
            protectedButtons.getChildren().add(userMgmtButton);
        }
        
        // Logout-Button
        Button logoutButton = createTouchButton("Abmelden", "#e74c3c", "#ff6b6b", 120, 50);
        logoutButton.setOnAction(e -> logout());
        protectedButtons.getChildren().add(logoutButton);
        
        topPanel.getChildren().addAll(userInfo, new Region(), parkedLabel, 
                                     parkedOrdersCombo, loadParkedButton, protectedButtons);
        HBox.setHgrow(topPanel.getChildren().get(1), Priority.ALWAYS);
        
        return topPanel;
    }
    
    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(20);
        leftPanel.setPadding(new Insets(25));
        leftPanel.setPrefWidth(450);
        leftPanel.setStyle("-fx-background-color: linear-gradient(to bottom, #161b22, #0d1117); " +
                          "-fx-border-color: #30363d; -fx-border-width: 0 2 0 0; " +
                          "-fx-border-radius: 0 15 15 0; -fx-background-radius: 0 15 15 0;");
        
        Label categoryLabel = new Label("Kategorien");
        categoryLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        categoryLabel.setStyle("-fx-text-fill: #00ff88; -fx-padding: 0 0 15 0;");
        
        // Kategorie-Buttons
        VBox categoryBox = new VBox(15);
        for (String category : categories.keySet()) {
            Button categoryBtn = createTouchCategoryButton(category);
            categoryBox.getChildren().add(categoryBtn);
        }
        
        // Produkt-Grid
        categoryButtons = new GridPane();
        categoryButtons.setHgap(20);
        categoryButtons.setVgap(20);
        categoryButtons.setPadding(new Insets(30, 0, 0, 0));
        
        // Standard: Erste Kategorie anzeigen
        showCategoryProducts(categories.keySet().iterator().next());
        
        leftPanel.getChildren().addAll(categoryLabel, categoryBox, categoryButtons);
        return leftPanel;
    }
    
    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(25);
        centerPanel.setPadding(new Insets(25));
        centerPanel.setPrefWidth(500);
        centerPanel.setStyle("-fx-background-color: #0d1117;");
        
        Label orderLabel = new Label("Aktuelle Bestellung");
        orderLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        orderLabel.setStyle("-fx-text-fill: #00ff88; -fx-padding: 0 0 15 0;");
        
        orderDisplay = new VBox(12);
        orderDisplay.setStyle("-fx-background-color: linear-gradient(to bottom, #161b22, #21262d); " +
                             "-fx-padding: 25; -fx-border-color: #30363d; -fx-border-radius: 15; " +
                             "-fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");
        orderDisplay.setPrefHeight(500);
        
        ScrollPane orderScroll = new ScrollPane(orderDisplay);
        orderScroll.setFitToWidth(true);
        orderScroll.setPrefHeight(500);
        orderScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; " +
                            "-fx-border-color: transparent;");
        
        // Bestellungs-Aktionen
        HBox orderActions = new HBox(20);
        orderActions.setAlignment(Pos.CENTER);
        
        Button clearButton = createTouchButton("Bestellung löschen", "#e74c3c", "#ff6b6b", 180, 55);
        clearButton.setOnAction(e -> clearOrder());
        
        TextField parkNameField = new TextField();
        parkNameField.setPromptText("Name für geparkte Bestellung");
        parkNameField.setPrefWidth(250);
        parkNameField.setPrefHeight(55);
        parkNameField.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; " +
                              "-fx-border-color: #30363d; -fx-border-radius: 12; " +
                              "-fx-background-radius: 12; -fx-padding: 15; " +
                              "-fx-font-size: 14px; -fx-prompt-text-fill: #6e7681;");
        
        Button parkButton = createTouchButton("Parken", "#f39c12", "#fdcb6e", 120, 55);
        parkButton.setOnAction(e -> parkOrder(parkNameField.getText()));
        
        orderActions.getChildren().addAll(clearButton, parkNameField, parkButton);
        
        centerPanel.getChildren().addAll(orderLabel, orderScroll, orderActions);
        return centerPanel;
    }
    
    private VBox createRightPanel() {
        VBox rightPanel = new VBox(30);
        rightPanel.setPadding(new Insets(25));
        rightPanel.setPrefWidth(450); // Breiter für Schnelltasten
        rightPanel.setStyle("-fx-background-color: linear-gradient(to bottom, #161b22, #0d1117); " +
                           "-fx-border-color: #30363d; -fx-border-width: 0 0 0 2; " +
                           "-fx-border-radius: 15 0 0 15; -fx-background-radius: 15 0 0 15;");
        
        // Gesamtbetrag
        VBox totalBox = new VBox(20);
        totalBox.setStyle("-fx-background-color: linear-gradient(to bottom, #21262d, #161b22); " +
                         "-fx-padding: 30; -fx-border-radius: 20; -fx-background-radius: 20; " +
                         "-fx-border-color: #30363d; -fx-border-width: 2; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,255,136,0.2), 25, 0, 0, 0);");
        
        Label totalHeaderLabel = new Label("Gesamtbetrag");
        totalHeaderLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));
        totalHeaderLabel.setStyle("-fx-text-fill: #8b949e;");
        
        totalLabel = new Label("€0.00");
        totalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 40));
        totalLabel.setStyle("-fx-text-fill: #00ff88;");
        
        totalBox.getChildren().addAll(totalHeaderLabel, totalLabel);
        
        // Zahlung mit Schnelltasten
        VBox paymentBox = new VBox(20);
        paymentBox.setStyle("-fx-background-color: linear-gradient(to bottom, #21262d, #161b22); " +
                           "-fx-padding: 30; -fx-border-radius: 20; -fx-background-radius: 20; " +
                           "-fx-border-color: #30363d; -fx-border-width: 2;");
        
        Label paymentLabel = new Label("Zahlung");
        paymentLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        paymentLabel.setStyle("-fx-text-fill: #00ff88;");
        
        // Zahlungsart-Auswahl
        Label paymentTypeLabel = new Label("Zahlungsart:");
        paymentTypeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        paymentTypeLabel.setStyle("-fx-text-fill: #c9d1d9;");
        
        HBox paymentTypeBox = new HBox(15);
        paymentTypeBox.setAlignment(Pos.CENTER);
        
        ToggleGroup paymentTypeGroup = new ToggleGroup();
        
        cashPayment = new RadioButton("💵 Bar");
        cashPayment.setToggleGroup(paymentTypeGroup);
        cashPayment.setSelected(true); // Standard: Bar
        cashPayment.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        cashPayment.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 16px;");
        
        cardPayment = new RadioButton("💳 Karte");
        cardPayment.setToggleGroup(paymentTypeGroup);
        cardPayment.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        cardPayment.setStyle("-fx-text-fill: #3498db; -fx-font-size: 16px;");
        
        // Event-Handler für Zahlungsart-Wechsel
        cashPayment.setOnAction(e -> {
            if (cashPayment.isSelected()) {
                // Bei Bar-Zahlung: Schnelltasten und Rückgeld-Berechnung aktivieren
                quickButtonsGrid.setDisable(false);
                quickButtonsLabel.setDisable(false);
                changeLabel.setVisible(true);
                receivedLabel.setText("Erhalten: €0.00");
                changeLabel.setText("Rückgeld: €0.00");
                paymentField.setPromptText("Betrag €");
                calculateChange();
            }
        });
        
        cardPayment.setOnAction(e -> {
            if (cardPayment.isSelected()) {
                // Bei Karten-Zahlung: Exakten Betrag setzen, Schnelltasten deaktivieren
                quickButtonsGrid.setDisable(true);
                quickButtonsLabel.setDisable(true);
                changeLabel.setVisible(false);
                paymentField.setText(String.format("%.2f", totalAmount));
                receivedLabel.setText("Kartenzahlung: €" + String.format("%.2f", totalAmount));
                paymentField.setPromptText("Kartenbetrag €");
            }
        });
        
        paymentTypeBox.getChildren().addAll(cashPayment, cardPayment);
        
        // Eingabefeld mit Touch-Tastatur
        HBox paymentInputBox = new HBox(15);
        paymentInputBox.setAlignment(Pos.CENTER);
        
        paymentField = new TextField();
        paymentField.setPromptText("Betrag €");
        paymentField.setFont(Font.font("Segoe UI", 18));
        paymentField.setPrefHeight(60);
        paymentField.setPrefWidth(200);
        paymentField.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #c9d1d9; " +
                             "-fx-border-color: #30363d; -fx-border-radius: 15; " +
                             "-fx-background-radius: 15; -fx-padding: 15; " +
                             "-fx-prompt-text-fill: #6e7681; -fx-font-size: 18px;");
        paymentField.textProperty().addListener((obs, old, newVal) -> calculateChange());
        
        Button clearPaymentBtn = createTouchButton("C", "#e74c3c", "#ff6b6b", 50, 60);
        clearPaymentBtn.setOnAction(e -> paymentField.clear());
        
        paymentInputBox.getChildren().addAll(paymentField, clearPaymentBtn);
        
        // Schnelltasten für Geldbeträge
        quickButtonsLabel = new Label("Schnelleingabe:");
        quickButtonsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        quickButtonsLabel.setStyle("-fx-text-fill: #c9d1d9;");
        
        quickButtonsGrid = new GridPane();
        quickButtonsGrid.setHgap(10);
        quickButtonsGrid.setVgap(10);
        quickButtonsGrid.setAlignment(Pos.CENTER);
        
        String[] amounts = {"5", "10", "20", "50", "100"};
        int col = 0;
        for (String amount : amounts) {
            Button quickBtn = createTouchButton(amount + "€", "#6c5ce7", "#a29bfe", 95, 55);
            quickBtn.setOnAction(e -> {
                paymentField.setText(amount + ".00");
                calculateChange(); // Sofort Rückgeld berechnen
            });
            quickButtonsGrid.add(quickBtn, col, 0);
            col++;
        }
        
        // Exakt-Button
        Button exactBtn = createTouchButton("Exakt", "#f39c12", "#fdcb6e", 380, 50);
        exactBtn.setOnAction(e -> {
            if (totalAmount > 0) {
                paymentField.setText(String.format("%.2f", totalAmount));
                calculateChange(); // Sofort Rückgeld berechnen
            }
        });
        quickButtonsGrid.add(exactBtn, 0, 1, 5, 1); // Span über alle Spalten
        
        receivedLabel = new Label("Erhalten: €0.00");
        receivedLabel.setFont(Font.font("Segoe UI", 16));
        receivedLabel.setStyle("-fx-text-fill: #c9d1d9;");
        
        changeLabel = new Label("Rückgeld: €0.00");
        changeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        changeLabel.setStyle("-fx-text-fill: #00ff88;");
        
        Button completeButton = createTouchButton("Zahlung abschließen", "#00d488", "#00ff88", 360, 70);
        completeButton.setStyle(completeButton.getStyle() + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        completeButton.setOnAction(e -> completeTransaction());
        
        paymentBox.getChildren().addAll(paymentLabel, paymentTypeLabel, paymentTypeBox, paymentInputBox, quickButtonsLabel, 
                                       quickButtonsGrid, receivedLabel, changeLabel, completeButton);
        
        rightPanel.getChildren().addAll(totalBox, paymentBox);
        return rightPanel;
    }
    
    private Button createTouchCategoryButton(String category) {
        Button btn = new Button(category);
        btn.setPrefWidth(400);
        btn.setPrefHeight(65);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        btn.setStyle("-fx-background-color: linear-gradient(to right, #21262d, #30363d); " +
                    "-fx-text-fill: #c9d1d9; -fx-border-color: #30363d; " +
                    "-fx-border-radius: 15; -fx-background-radius: 15; " +
                    "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
        
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: linear-gradient(to right, #00d488, #00ff88); " +
            "-fx-text-fill: #000000; -fx-border-color: #00ff88; " +
            "-fx-border-radius: 15; -fx-background-radius: 15; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,255,136,0.5), 20, 0, 0, 5); " +
            "-fx-font-weight: bold; -fx-font-size: 16px;"));
        
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: linear-gradient(to right, #21262d, #30363d); " +
            "-fx-text-fill: #c9d1d9; -fx-border-color: #30363d; " +
            "-fx-border-radius: 15; -fx-background-radius: 15; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);"));
        
        btn.setOnAction(e -> showCategoryProducts(category));
        return btn;
    }
    
    private Button createTouchButton(String text, String baseColor, String hoverColor, int width, int height) {
        Button btn = new Button(text);
        btn.setPrefWidth(width);
        btn.setPrefHeight(height);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btn.setMinWidth(width);
        btn.setMaxWidth(width);
        
        String baseStyle = "-fx-background-color: linear-gradient(to bottom, " + baseColor + ", " + 
                          adjustBrightness(baseColor, -0.2) + "); " +
                          "-fx-text-fill: white; -fx-border-radius: 12; -fx-background-radius: 12; " +
                          "-fx-padding: 15 25; -fx-cursor: hand; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3); " +
                          "-fx-text-alignment: center; -fx-alignment: center;";
        
        String hoverStyle = "-fx-background-color: linear-gradient(to bottom, " + hoverColor + ", " + 
                           adjustBrightness(hoverColor, -0.1) + "); " +
                           "-fx-text-fill: white; -fx-border-radius: 12; -fx-background-radius: 12; " +
                           "-fx-padding: 15 25; -fx-cursor: hand; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5); " +
                           "-fx-text-alignment: center; -fx-alignment: center;";
        
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        
        return btn;
    }
    
    private String adjustBrightness(String hexColor, double factor) {
        // Einfache Helligkeitsanpassung für Hover-Effekte
        if (hexColor.equals("#00d488")) return "#00b574";
        if (hexColor.equals("#00ff88")) return "#00e077";
        if (hexColor.equals("#6c5ce7")) return "#5b4bd1";
        if (hexColor.equals("#a29bfe")) return "#8b82f7";
        if (hexColor.equals("#fdcb6e")) return "#f5b942";
        if (hexColor.equals("#f39c12")) return "#e67e22";
        if (hexColor.equals("#e74c3c")) return "#c0392b";
        if (hexColor.equals("#ff6b6b")) return "#ff5252";
        return hexColor;
    }
    
    private void showCategoryProducts(String category) {
        categoryButtons.getChildren().clear();
        List<Product> products = categories.get(category);
        
        int col = 0, row = 0;
        for (Product product : products) {
            Button productBtn = new Button(product.getName() + "\n€" + String.format("%.2f", product.getPrice()));
            productBtn.setPrefWidth(130);
            productBtn.setPrefHeight(110);
            productBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            
            String baseStyle = "-fx-background-color: linear-gradient(to bottom, #00d488, #00b574); " +
                              "-fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15; " +
                              "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3); " +
                              "-fx-padding: 10;";
            
            String hoverStyle = "-fx-background-color: linear-gradient(to bottom, #00ff88, #00e077); " +
                               "-fx-text-fill: #000000; -fx-border-radius: 15; -fx-background-radius: 15; " +
                               "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,255,136,0.5), 20, 0, 0, 5); " +
                               "-fx-font-weight: bold; -fx-padding: 10;";
            
            productBtn.setStyle(baseStyle);
            productBtn.setOnMouseEntered(e -> productBtn.setStyle(hoverStyle));
            productBtn.setOnMouseExited(e -> productBtn.setStyle(baseStyle));
            productBtn.setOnAction(e -> addToOrder(product));
            
            categoryButtons.add(productBtn, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }
    
    private void addToOrder(Product product) {
        // Prüfen ob Produkt bereits in Bestellung
        for (OrderItem item : currentOrder) {
            if (item.getProduct().getName().equals(product.getName())) {
                item.setQuantity(item.getQuantity() + 1);
                updateOrderDisplay();
                return;
            }
        }
        
        currentOrder.add(new OrderItem(product, 1));
        updateOrderDisplay();
    }
    
    private void updateOrderDisplay() {
        orderDisplay.getChildren().clear();
        totalAmount = 0.0;
        
        for (OrderItem item : currentOrder) {
            HBox itemBox = new HBox(15);
            itemBox.setAlignment(Pos.CENTER_LEFT);
            itemBox.setPadding(new Insets(15));
            itemBox.setStyle("-fx-background-color: linear-gradient(to right, #21262d, #30363d); " +
                            "-fx-border-color: #30363d; -fx-border-radius: 12; -fx-background-radius: 12; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);");
            
            Label nameLabel = new Label(item.getProduct().getName());
            nameLabel.setPrefWidth(170);
            nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            nameLabel.setStyle("-fx-text-fill: #c9d1d9;");
            
            Spinner<Integer> quantitySpinner = new Spinner<>(1, 99, item.getQuantity());
            quantitySpinner.setPrefWidth(80);
            quantitySpinner.setPrefHeight(45);
            quantitySpinner.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #c9d1d9; " +
                                   "-fx-border-color: #30363d; -fx-border-radius: 8; " +
                                   "-fx-font-size: 14px;");
            quantitySpinner.valueProperty().addListener((obs, old, newVal) -> {
                item.setQuantity(newVal);
                updateOrderDisplay();
            });
            
            TextField discountField = new TextField(String.format("%.2f", item.getDiscount()));
            discountField.setPromptText("Rabatt €");
            discountField.setPrefWidth(90);
            discountField.setPrefHeight(45);
            discountField.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #fdcb6e; " +
                                  "-fx-border-color: #30363d; -fx-border-radius: 8; " +
                                  "-fx-prompt-text-fill: #6e7681; -fx-font-size: 14px; " +
                                  "-fx-padding: 8;");
            discountField.textProperty().addListener((obs, old, newVal) -> {
                try {
                    item.setDiscount(Double.parseDouble(newVal));
                    updateOrderDisplay();
                } catch (NumberFormatException ignored) {}
            });
            
            double itemTotal = (item.getProduct().getPrice() - item.getDiscount()) * item.getQuantity();
            Label priceLabel = new Label("€" + String.format("%.2f", itemTotal));
            priceLabel.setPrefWidth(90);
            priceLabel.setAlignment(Pos.CENTER_RIGHT);
            priceLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            priceLabel.setStyle("-fx-text-fill: #00ff88;");
            
            Button removeBtn = new Button("×");
            removeBtn.setPrefSize(40, 40);
            removeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                              "-fx-border-radius: 20; -fx-background-radius: 20; " +
                              "-fx-font-size: 18; -fx-font-weight: bold; -fx-cursor: hand;");
            removeBtn.setOnMouseEntered(e -> removeBtn.setStyle(
                "-fx-background-color: #ff6b6b; -fx-text-fill: white; " +
                "-fx-border-radius: 20; -fx-background-radius: 20; " +
                "-fx-font-size: 18; -fx-font-weight: bold; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.4), 10, 0, 0, 3);"));
            removeBtn.setOnMouseExited(e -> removeBtn.setStyle(
                "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-border-radius: 20; -fx-background-radius: 20; " +
                "-fx-font-size: 18; -fx-font-weight: bold; -fx-cursor: hand;"));
            removeBtn.setOnAction(e -> {
                currentOrder.remove(item);
                updateOrderDisplay();
            });
            
            itemBox.getChildren().addAll(nameLabel, quantitySpinner, discountField, priceLabel, removeBtn);
            orderDisplay.getChildren().add(itemBox);
            
            totalAmount += itemTotal;
        }
        
        totalLabel.setText("€" + String.format("%.2f", totalAmount));
        calculateChange();
    }
    
    private void calculateChange() {
        try {
            String paymentText = paymentField.getText().trim();
            if (paymentText.isEmpty()) {
                receivedLabel.setText("Erhalten: €0.00");
                changeLabel.setText("Rückgeld: €0.00");
                changeLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-weight: bold; -fx-font-size: 22px;");
                return;
            }
            
            // Deutsche und internationale Zahlenformate unterstützen
            String normalizedPayment = paymentText.replace(",", ".");
            double received = Double.parseDouble(normalizedPayment);
            
            receivedLabel.setText("Erhalten: €" + String.format("%.2f", received));
            double change = received - totalAmount;
            changeLabel.setText("Rückgeld: €" + String.format("%.2f", change));
            
            // Farbe je nach Rückgeld-Status
            if (change >= 0) {
                changeLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold; -fx-font-size: 22px;");
            } else {
                changeLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold; -fx-font-size: 22px;");
            }
            
        } catch (NumberFormatException e) {
            receivedLabel.setText("Erhalten: €0.00");
            changeLabel.setText("Rückgeld: €0.00");
            changeLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-weight: bold; -fx-font-size: 22px;");
        }
    }
    
    private void parkOrder(String name) {
        if (name.isEmpty() || currentOrder.isEmpty()) {
            showAlert("Fehler", "Bitte Namen eingeben und Bestellung erstellen.");
            return;
        }
        
        parkedOrders.put(name, new ArrayList<>(currentOrder));
        parkedOrdersCombo.getItems().add(name);
        clearOrder();
        showAlert("Erfolg", "Bestellung '" + name + "' wurde geparkt.");
    }
    
    private void loadParkedOrder() {
        String selectedOrder = parkedOrdersCombo.getValue();
        if (selectedOrder != null) {
            currentOrder.clear();
            currentOrder.addAll(parkedOrders.get(selectedOrder));
            updateOrderDisplay();
            parkedOrders.remove(selectedOrder);
            parkedOrdersCombo.getItems().remove(selectedOrder);
        }
    }
    
    private void clearOrder() {
        currentOrder.clear();
        paymentField.clear();
        
        // Zahlungsart auf Bar zurücksetzen - mit Null-Check
        if (cashPayment != null) {
            cashPayment.setSelected(true);
        }
        if (quickButtonsGrid != null) {
            quickButtonsGrid.setDisable(false);
        }
        if (quickButtonsLabel != null) {
            quickButtonsLabel.setDisable(false);
        }
        if (changeLabel != null) {
            changeLabel.setVisible(true);
        }
        
        receivedLabel.setText("Erhalten: €0.00");
        changeLabel.setText("Rückgeld: €0.00");
        paymentField.setPromptText("Betrag €");
        
        updateOrderDisplay();
    }
    
    private void completeTransaction() {
        if (currentOrder.isEmpty()) {
            showAlert("Fehler", "Keine Artikel in der Bestellung.");
            return;
        }
        
        String paymentText = paymentField.getText().trim();
        if (paymentText.isEmpty()) {
            showAlert("Fehler", "Bitte Zahlungsbetrag eingeben.");
            return;
        }
        
        // Zahlungsart ermitteln - mit Null-Check
        boolean isCashPayment = (cashPayment != null && cashPayment.isSelected());
        String paymentMethod = isCashPayment ? "Bar" : "Karte";
        
        try {
            // Deutsche und internationale Zahlenformate unterstützen
            String normalizedPayment = paymentText.replace(",", ".");
            double received = Double.parseDouble(normalizedPayment);
            
            if (received <= 0) {
                showAlert("Fehler", "Betrag muss größer als 0 sein.");
                return;
            }
            
            if (isCashPayment) {
                // Bei Bar-Zahlung: Rückgeld-Prüfung
                if (received < totalAmount) {
                    showAlert("Fehler", "Erhaltener Betrag ist zu gering.\n\n" +
                             "💰 Benötigt: €" + String.format("%.2f", totalAmount) + "\n" +
                             "💵 Eingegeben: €" + String.format("%.2f", received) + "\n" +
                             "❌ Fehlbetrag: €" + String.format("%.2f", totalAmount - received));
                    return;
                }
            } else {
                // Bei Karten-Zahlung: Exakter Betrag erwartet
                if (Math.abs(received - totalAmount) > 0.01) {
                    showAlert("Hinweis", "Bei Kartenzahlung wird der exakte Betrag abgebucht.\n\n" +
                             "💳 Betrag: €" + String.format("%.2f", totalAmount));
                    received = totalAmount; // Exakter Betrag für Karte
                }
            }
            
            // Transaktion zum Kassenbuch hinzufügen
            Transaction transaction = new Transaction(
                LocalDateTime.now(),
                new ArrayList<>(currentOrder),
                totalAmount,
                received,
                received - totalAmount,
                paymentMethod
            );
            cashBook.add(transaction);
            
            double change = received - totalAmount;
            String successMessage = "✅ Transaktion erfolgreich abgeschlossen!\n\n" +
                                   "💰 Gesamtbetrag: €" + String.format("%.2f", totalAmount) + "\n" +
                                   "💳 Zahlungsart: " + paymentMethod + "\n" +
                                   "💵 " + (isCashPayment ? "Erhalten" : "Abgebucht") + ": €" + String.format("%.2f", received);
            
            if (isCashPayment && change > 0) {
                successMessage += "\n🔄 Rückgeld: €" + String.format("%.2f", change);
            } else if (isCashPayment && change == 0) {
                successMessage += "\n✅ Passend bezahlt - Kein Rückgeld";
            }
            
            showAlert("Erfolg", successMessage);
            clearOrder();
            
        } catch (NumberFormatException e) {
            showAlert("Fehler", "Ungültiger Zahlungsbetrag!\n\n" +
                     "✅ Richtig: 15.50 oder 15,50\n" +
                     "❌ Falsch: " + paymentText + "\n\n" +
                     "Bitte nur Zahlen mit Punkt oder Komma eingeben.");
        }
    }
    
    private void openBackend() {
        PINManager pinManager = PINManager.getInstance();
        if (!pinManager.canAccessBackend()) {
            showAlert("Zugriff verweigert", 
                     "Sie haben keine Berechtigung für das Backend.\n" +
                     "Benötigte Rolle: Techniker");
            return;
        }
        
        BackendWindow backend = new BackendWindow(categories);
        backend.show();
    }
    
    private void showCashBook() {
        PINManager pinManager = PINManager.getInstance();
        if (!pinManager.canAccessCashBook()) {
            showAlert("Zugriff verweigert", 
                     "Sie haben keine Berechtigung für das Kassenbuch.\n" +
                     "Benötigte Rolle: Manager oder Techniker");
            return;
        }
        
        CashBookWindow cashBookWindow = new CashBookWindow(cashBook);
        cashBookWindow.show();
    }
    
    private void openUserManagement() {
        PINManager pinManager = PINManager.getInstance();
        if (!pinManager.canManageUsers()) {
            showAlert("Zugriff verweigert", 
                     "Sie haben keine Berechtigung für die Benutzerverwaltung.\n" +
                     "Benötigte Rolle: Techniker");
            return;
        }
        
        UserManagementDialog userMgmt = new UserManagementDialog();
        userMgmt.show();
    }
    
    private void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Abmelden");
        alert.setHeaderText("Möchten Sie sich wirklich abmelden?");
        alert.setContentText("Sie müssen sich danach erneut anmelden.");
        
        // Dark Theme für Alert Dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #21262d; -fx-border-color: #30363d; " +
                           "-fx-border-radius: 10; -fx-background-radius: 10;");
        
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px;");
        dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: #c9d1d9; -fx-font-weight: bold;");
        
        dialogPane.lookupButton(ButtonType.OK).setStyle(
            "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
            "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 16;");
        
        dialogPane.lookupButton(ButtonType.CANCEL).setStyle(
            "-fx-background-color: #6c757d; -fx-text-fill: white; " +
            "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 16;");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            PINManager.getInstance().logout();
            
            // Aktuelle Stage schließen und neu starten
            Stage currentStage = (Stage) totalLabel.getScene().getWindow();
            currentStage.close();
            
            // Neue Instanz starten
            Platform.runLater(() -> {
                try {
                    new ModernPOSSystem().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Dark Theme für Alert Dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #21262d; -fx-border-color: #30363d; " +
                           "-fx-border-radius: 10; -fx-background-radius: 10;");
        
        // Text-Styling
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px;");
        
        // Button-Styling
        dialogPane.lookupButton(ButtonType.OK).setStyle(
            "-fx-background-color: linear-gradient(to bottom, #00d488, #00b574); " +
            "-fx-text-fill: white; -fx-border-radius: 6; -fx-background-radius: 6; " +
            "-fx-padding: 8 16; -fx-font-weight: bold;");
        
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

// Datenklassen
class Product {
    private String name;
    private double price;
    
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
    
    public String getName() { return name; }
    public double getPrice() { return price; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
}

class OrderItem {
    private Product product;
    private int quantity;
    private double discount = 0.0;
    
    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getDiscount() { return discount; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setDiscount(double discount) { this.discount = discount; }
}

class Transaction {
    private LocalDateTime timestamp;
    private List<OrderItem> items;
    private double total;
    private double received;
    private double change;
    private String paymentMethod;
    
    public Transaction(LocalDateTime timestamp, List<OrderItem> items, double total, double received, double change, String paymentMethod) {
        this.timestamp = timestamp;
        this.items = items;
        this.total = total;
        this.received = received;
        this.change = change;
        this.paymentMethod = paymentMethod;
    }
    
    // Konstruktor für Rückwärtskompatibilität
    public Transaction(LocalDateTime timestamp, List<OrderItem> items, double total, double received, double change) {
        this(timestamp, items, total, received, change, "Bar");
    }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<OrderItem> getItems() { return items; }
    public double getTotal() { return total; }
    public double getReceived() { return received; }
    public double getChange() { return change; }
    public String getPaymentMethod() { return paymentMethod; }
}