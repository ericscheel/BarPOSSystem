package com.barpos;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.beans.property.SimpleStringProperty;
import java.util.*;

public class BackendWindow {
    private Map<String, List<Product>> categories;
    private Stage stage;
    private TableView<Product> productTable;
    private ComboBox<String> categoryFilterCombo;
    private TextField nameField;
    private TextField priceField;
    private ComboBox<String> categorySelectCombo;
    private TextField newCategoryField;
    private VBox existingCategoriesBox;
    private Product editingProduct = null; // aktuell zu bearbeitendes Produkt
    
    public BackendWindow(Map<String, List<Product>> categories) {
        this.categories = categories;
        initializeWindow();
    }
    
    private void initializeWindow() {
        stage = new Stage();
        stage.setTitle("Backend - Produktverwaltung");
        stage.setWidth(1200);
        stage.setHeight(800);
        
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #0d1117;");
        
        // Header
        HBox header = createHeader();
        mainLayout.setTop(header);
        
        // Center - Scrollbarer Inhalt
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        VBox centerContent = new VBox(25);
        centerContent.setPadding(new Insets(25));
        
        // Produkttabelle
        VBox tableSection = createTableSection();
        
        // Produktformular
        VBox formSection = createFormSection();
        
        // Kategorie-Management
        VBox categorySection = createCategorySection();
        
        centerContent.getChildren().addAll(tableSection, formSection, categorySection);
        scrollPane.setContent(centerContent);
        mainLayout.setCenter(scrollPane);
        
        Scene scene = new Scene(mainLayout);
        stage.setScene(scene);
    }
    
    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(25));
        header.setStyle("-fx-background-color: linear-gradient(to right, #21262d, #161b22); " +
                       "-fx-border-color: #30363d; -fx-border-width: 0 0 2 0;");
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Produktverwaltung");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: #00ff88; -fx-effect: dropshadow(gaussian, #00ff88, 15, 0, 0, 0);");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button closeButton = createStyledButton("Schließen", "#6c757d", 120, 45);
        closeButton.setOnAction(e -> stage.close());
        
        header.getChildren().addAll(titleLabel, spacer, closeButton);
        return header;
    }
    
    private VBox createTableSection() {
        VBox tableSection = new VBox(15);
        tableSection.setStyle("-fx-background-color: linear-gradient(to bottom, #161b22, #21262d); " +
                             "-fx-padding: 25; -fx-border-radius: 15; -fx-background-radius: 15; " +
                             "-fx-border-color: #30363d; -fx-border-width: 1;");
        
        Label tableLabel = new Label("Aktuelle Produkte");
        tableLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        tableLabel.setStyle("-fx-text-fill: #00ff88;");
        
        // Filter-Box
        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Kategorie filtern:");
        filterLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        categoryFilterCombo = new ComboBox<>();
        categoryFilterCombo.getItems().add("Alle Kategorien");
        categoryFilterCombo.getItems().addAll(categories.keySet());
        categoryFilterCombo.setValue("Alle Kategorien");
        categoryFilterCombo.setPrefWidth(200);
        categoryFilterCombo.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #c9d1d9; " +
                                    "-fx-border-color: #30363d; -fx-border-radius: 8; " +
                                    "-fx-background-radius: 8;");
        categoryFilterCombo.setOnAction(e -> filterProducts());
        
        Button refreshButton = createStyledButton("Aktualisieren", "#3498db", 120, 35);
        refreshButton.setOnAction(e -> refreshTable());
        
        filterBox.getChildren().addAll(filterLabel, categoryFilterCombo, refreshButton);
        
        // Tabelle
        productTable = new TableView<>();
        productTable.setPrefHeight(350);
        productTable.setStyle("-fx-background-color: #0d1117; -fx-border-color: #30363d; " +
                             "-fx-control-inner-background: #0d1117; -fx-selection-bar: #00d488; " +
                             "-fx-selection-bar-non-focused: #00d488;");
        
        // Spalten definieren mit korrekten CellValueFactory
        TableColumn<Product, String> nameCol = new TableColumn<>("Produktname");
        nameCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(250);
        nameCol.setStyle("-fx-text-fill: #c9d1d9;");
        
        TableColumn<Product, String> priceCol = new TableColumn<>("Preis (€)");
        priceCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("%.2f €", cellData.getValue().getPrice())));
        priceCol.setPrefWidth(120);
        priceCol.setStyle("-fx-text-fill: #c9d1d9;");
        
        TableColumn<Product, String> categoryCol = new TableColumn<>("Kategorie");
        categoryCol.setPrefWidth(150);
        
        // Custom CellFactory für bessere Darstellung
        nameCol.setCellFactory(col -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Product product = getTableRow().getItem();
                    setText(product.getName());
                    setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px;");
                }
            }
        });
        
        priceCol.setCellFactory(col -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Product product = getTableRow().getItem();
                    setText(String.format("%.2f €", product.getPrice()));
                    setStyle("-fx-text-fill: #00ff88; -fx-font-size: 14px; -fx-font-weight: bold;");
                }
            }
        });
        
        categoryCol.setCellFactory(col -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Product product = getTableRow().getItem();
                    String category = findProductCategory(product);
                    setText(category);
                    setStyle("-fx-text-fill: #fdcb6e; -fx-font-size: 14px;");
                }
            }
        });
        
        TableColumn<Product, Void> actionCol = new TableColumn<>("Aktionen");
        actionCol.setCellFactory(col -> new TableCell<Product, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Product product = getTableRow().getItem();
                    Button editBtn = createStyledButton("Bearbeiten", "#f39c12", 90, 30);
                    Button deleteBtn = createStyledButton("Löschen", "#e74c3c", 90, 30);

                    editBtn.setOnAction(e -> editProduct(product));
                    deleteBtn.setOnAction(e -> deleteProduct(product));

                    HBox buttons = new HBox(8);
                    buttons.setAlignment(Pos.CENTER);
                    buttons.getChildren().addAll(editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });
        actionCol.setPrefWidth(200);
        actionCol.setStyle("-fx-alignment: CENTER;");
        
        productTable.getColumns().addAll(nameCol, priceCol, categoryCol, actionCol);
        refreshTable();
        
        tableSection.getChildren().addAll(tableLabel, filterBox, productTable);
        return tableSection;
    }
    
    private VBox createFormSection() {
        VBox formSection = new VBox(20);
        formSection.setStyle("-fx-background-color: linear-gradient(to bottom, #161b22, #21262d); " +
                            "-fx-padding: 25; -fx-border-radius: 15; -fx-background-radius: 15; " +
                            "-fx-border-color: #30363d; -fx-border-width: 1;");
        
        Label formLabel = new Label("Neues Produkt hinzufügen / Bearbeiten");
        formLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        formLabel.setStyle("-fx-text-fill: #00ff88;");
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER_LEFT);
        
        // Produktname
        Label nameLabel = new Label("Produktname:");
        nameLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        nameField = new TextField();
        nameField.setPromptText("Name des Produkts eingeben");
        nameField.setPrefWidth(300);
        nameField.setPrefHeight(40);
        nameField.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #c9d1d9; " +
                          "-fx-border-color: #30363d; -fx-border-radius: 8; " +
                          "-fx-background-radius: 8; -fx-padding: 10; " +
                          "-fx-prompt-text-fill: #6e7681; -fx-font-size: 14px;");
        
        // Preis
        Label priceLabel = new Label("Preis (€):");
        priceLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        priceField = new TextField();
        priceField.setPromptText("0.00");
        priceField.setPrefWidth(150);
        priceField.setPrefHeight(40);
        priceField.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #c9d1d9; " +
                           "-fx-border-color: #30363d; -fx-border-radius: 8; " +
                           "-fx-background-radius: 8; -fx-padding: 10; " +
                           "-fx-prompt-text-fill: #6e7681; -fx-font-size: 14px;");
        
        // Kategorie-Auswahl
        Label categorySelectLabel = new Label("Kategorie:");
        categorySelectLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        categorySelectCombo = new ComboBox<>();
        categorySelectCombo.getItems().addAll(categories.keySet());
        categorySelectCombo.setPrefWidth(200);
        categorySelectCombo.setPrefHeight(40);
        categorySelectCombo.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #c9d1d9; " +
                                    "-fx-border-color: #30363d; -fx-border-radius: 8; " +
                                    "-fx-background-radius: 8;");
        
        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(priceLabel, 0, 1);
        formGrid.add(priceField, 1, 1);
        formGrid.add(categorySelectLabel, 0, 2);
        formGrid.add(categorySelectCombo, 1, 2);
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button saveButton = createStyledButton("Speichern", "#27ae60", 120, 45);
        Button clearButton = createStyledButton("Zurücksetzen", "#95a5a6", 120, 45);
        
        saveButton.setOnAction(e -> saveProduct());
        clearButton.setOnAction(e -> clearForm());
        
        buttonBox.getChildren().addAll(saveButton, clearButton);
        
        formSection.getChildren().addAll(formLabel, formGrid, buttonBox);
        return formSection;
    }
    
    private VBox createCategorySection() {
        VBox categorySection = new VBox(20);
        categorySection.setStyle("-fx-background-color: linear-gradient(to bottom, #161b22, #21262d); " +
                                "-fx-padding: 25; -fx-border-radius: 15; -fx-background-radius: 15; " +
                                "-fx-border-color: #30363d; -fx-border-width: 1;");
        
        Label categoryLabel = new Label("Kategorie-Verwaltung");
        categoryLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        categoryLabel.setStyle("-fx-text-fill: #00ff88;");
        
        // Neue Kategorie hinzufügen
        HBox categoryBox = new HBox(15);
        categoryBox.setAlignment(Pos.CENTER_LEFT);
        
        Label newCategoryLabel = new Label("Neue Kategorie:");
        newCategoryLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        newCategoryField = new TextField();
        newCategoryField.setPromptText("Kategoriename eingeben");
        newCategoryField.setPrefWidth(250);
        newCategoryField.setPrefHeight(40);
        newCategoryField.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #c9d1d9; " +
                                 "-fx-border-color: #30363d; -fx-border-radius: 8; " +
                                 "-fx-background-radius: 8; -fx-padding: 10; " +
                                 "-fx-prompt-text-fill: #6e7681; -fx-font-size: 14px;");
        
        Button addCategoryButton = createStyledButton("Hinzufügen", "#8e44ad", 120, 40);
        addCategoryButton.setOnAction(e -> addCategory());
        
        categoryBox.getChildren().addAll(newCategoryLabel, newCategoryField, addCategoryButton);
        
        // Existierende Kategorien
        Label existingLabel = new Label("Existierende Kategorien:");
        existingLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        existingLabel.setStyle("-fx-text-fill: #c9d1d9;");
        
        existingCategoriesBox = new VBox(10);
        updateCategoryList();
        
        categorySection.getChildren().addAll(categoryLabel, categoryBox, existingLabel, existingCategoriesBox);
        return categorySection;
    }
    
    private void updateCategoryList() {
        existingCategoriesBox.getChildren().clear();
        
        for (String category : categories.keySet()) {
            HBox categoryRow = new HBox(15);
            categoryRow.setAlignment(Pos.CENTER_LEFT);
            categoryRow.setPadding(new Insets(10));
            categoryRow.setStyle("-fx-background-color: #0d1117; -fx-border-radius: 8; " +
                                "-fx-background-radius: 8; -fx-border-color: #30363d; -fx-border-width: 1;");
            
            Label catLabel = new Label(category + " (" + categories.get(category).size() + " Produkte)");
            catLabel.setPrefWidth(300);
            catLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px;");
            
            Button deleteCatButton = createStyledButton("Löschen", "#e74c3c", 100, 35);
            deleteCatButton.setOnAction(e -> deleteCategory(category));
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            categoryRow.getChildren().addAll(catLabel, spacer, deleteCatButton);
            existingCategoriesBox.getChildren().add(categoryRow);
        }
    }
    
    private Button createStyledButton(String text, String color, int width, int height) {
        Button btn = new Button(text);
        btn.setPrefWidth(width);
        btn.setPrefHeight(height);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        
        String baseStyle = "-fx-background-color: linear-gradient(to bottom, " + color + ", " + 
                          adjustBrightness(color) + "); " +
                          "-fx-text-fill: white; -fx-border-radius: 8; -fx-background-radius: 8; " +
                          "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);";
        
        btn.setStyle(baseStyle);
        return btn;
    }
    
    private String adjustBrightness(String color) {
        // Einfache Farbabstufungen für Gradient-Effekte
        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("#27ae60", "#229954");
        colorMap.put("#95a5a6", "#7f8c8d");
        colorMap.put("#e74c3c", "#c0392b");
        colorMap.put("#f39c12", "#e67e22");
        colorMap.put("#8e44ad", "#7d3c98");
        colorMap.put("#3498db", "#2980b9");
        colorMap.put("#6c757d", "#5a6268");
        return colorMap.getOrDefault(color, color);
    }
    
    private void refreshTable() {
        productTable.getItems().clear();
        
        String selectedCategory = categoryFilterCombo.getValue();
        if ("Alle Kategorien".equals(selectedCategory)) {
            for (List<Product> products : categories.values()) {
                productTable.getItems().addAll(products);
            }
        } else {
            if (categories.containsKey(selectedCategory)) {
                productTable.getItems().addAll(categories.get(selectedCategory));
            }
        }
    }
    
    private void filterProducts() {
        refreshTable();
    }
    
    private String findProductCategory(Product product) {
        for (Map.Entry<String, List<Product>> entry : categories.entrySet()) {
            if (entry.getValue().contains(product)) {
                return entry.getKey();
            }
        }
        return "Unbekannt";
    }
    
    private void editProduct(Product product) {
        if (product != null) {
            editingProduct = product;
            nameField.setText(product.getName());
            priceField.setText(String.valueOf(product.getPrice()));
            String category = findProductCategory(product);
            categorySelectCombo.setValue(category);
        }
    }
    
    private void deleteProduct(Product product) {
        if (product != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Produkt löschen");
            alert.setHeaderText("Möchten Sie dieses Produkt wirklich löschen?");
            alert.setContentText(product.getName() + " - €" + String.format("%.2f", product.getPrice()));
            
            // Dark Theme für Alert
            styleAlert(alert);
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Produkt aus allen Kategorien entfernen
                for (List<Product> products : categories.values()) {
                    products.remove(product);
                }
                refreshTable();
                updateCategoryCombo();
                updateCategoryList();
                showAlert("Erfolg", "Produkt wurde gelöscht.");
            }
        }
    }
    
    private void saveProduct() {
        try {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            String selectedCategory = categorySelectCombo.getValue();

            if (name.isEmpty() || priceText.isEmpty() || selectedCategory == null) {
                showAlert("Fehler", "Bitte alle Felder ausfüllen und Kategorie auswählen.");
                return;
            }

            double price = Double.parseDouble(priceText);

            if (editingProduct != null) {
                // Produkt bearbeiten
                String oldCategory = findProductCategory(editingProduct);
                if (!oldCategory.equals(selectedCategory)) {
                    // Kategorie gewechselt: Produkt verschieben
                    categories.get(oldCategory).remove(editingProduct);
                    categories.get(selectedCategory).add(editingProduct);
                }
                editingProduct.setName(name);
                editingProduct.setPrice(price);
                showAlert("Erfolg", "Produkt wurde aktualisiert.");
            } else {
                // Neues Produkt hinzufügen
                // Prüfe ob Produktname in Kategorie schon existiert
                for (Product p : categories.get(selectedCategory)) {
                    if (p.getName().equalsIgnoreCase(name)) {
                        showAlert("Fehler", "Produktname existiert bereits in dieser Kategorie.");
                        return;
                    }
                }
                Product newProduct = new Product(name, price);
                categories.get(selectedCategory).add(newProduct);
                showAlert("Erfolg", "Neues Produkt wurde hinzugefügt.");
            }

            clearForm();
            refreshTable();
            updateCategoryCombo();
            updateCategoryList();

        } catch (NumberFormatException e) {
            showAlert("Fehler", "Bitte gültigen Preis eingeben (z.B. 4.50).");
        }
    }
    
    private void clearForm() {
        nameField.clear();
        priceField.clear();
        categorySelectCombo.setValue(null);
        editingProduct = null;
    }
    
    private void addCategory() {
        String newCategory = newCategoryField.getText().trim();
        
        if (newCategory.isEmpty()) {
            showAlert("Fehler", "Bitte Kategoriename eingeben.");
            return;
        }
        
        if (categories.containsKey(newCategory)) {
            showAlert("Fehler", "Kategorie existiert bereits.");
            return;
        }
        
        categories.put(newCategory, new ArrayList<>());
        newCategoryField.clear();
        updateCategoryCombo();
        updateCategoryList();
        showAlert("Erfolg", "Kategorie '" + newCategory + "' wurde hinzugefügt.");
    }
    
    private void deleteCategory(String category) {
        if (categories.get(category).isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Kategorie löschen");
            alert.setHeaderText("Möchten Sie diese Kategorie wirklich löschen?");
            alert.setContentText(category);
            
            styleAlert(alert);
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                categories.remove(category);
                updateCategoryCombo();
                updateCategoryList();
                refreshTable();
                showAlert("Erfolg", "Kategorie wurde gelöscht.");
            }
        } else {
            showAlert("Fehler", "Kategorie kann nicht gelöscht werden - enthält noch " + 
                     categories.get(category).size() + " Produkte.");
        }
    }
    
    private void updateCategoryCombo() {
        String currentSelection = categoryFilterCombo.getValue();
        categoryFilterCombo.getItems().clear();
        categoryFilterCombo.getItems().add("Alle Kategorien");
        categoryFilterCombo.getItems().addAll(categories.keySet());

        if (categories.containsKey(currentSelection)) {
            categoryFilterCombo.setValue(currentSelection);
        } else {
            categoryFilterCombo.setValue("Alle Kategorien");
        }

        categorySelectCombo.getItems().clear();
        categorySelectCombo.getItems().addAll(categories.keySet());
    }
    
    private void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #21262d; -fx-border-color: #30363d; " +
                           "-fx-border-radius: 10; -fx-background-radius: 10;");
        
        Label contentLabel = (Label) dialogPane.lookup(".content.label");
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px;");
        }
        
        Label headerLabel = (Label) dialogPane.lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-weight: bold;");
        }
        
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                             "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 16;");
        }
        
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        if (cancelButton != null) {
            cancelButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; " +
                                 "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 16;");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        styleAlert(alert);
        alert.showAndWait();
    }
    
    public void show() {
        stage.show();
    }
}