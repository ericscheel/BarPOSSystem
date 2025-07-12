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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CashBookWindow {
    private List<Transaction> transactions;
    private Stage stage;
    private TableView<Transaction> transactionTable;
    private Label totalSalesLabel;
    private Label transactionCountLabel;
    private Label averageTransactionLabel;
    
    public CashBookWindow(List<Transaction> transactions) {
        this.transactions = transactions;
        initializeWindow();
    }
    
    private void initializeWindow() {
        stage = new Stage();
        stage.setTitle("Kassenbuch - Übersicht");
        stage.setWidth(1000);
        stage.setHeight(700);
        
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");
        
        // Header
        HBox header = new HBox();
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2c3e50;");
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Kassenbuch");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        header.getChildren().add(titleLabel);
        mainLayout.setTop(header);
        
        // Center Content
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(20));
        
        // Statistiken
        VBox statsSection = createStatsSection();
        
        // Transaktions-Tabelle
        VBox tableSection = createTableSection();
        
        centerContent.getChildren().addAll(statsSection, tableSection);
        
        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setFitToWidth(true);
        mainLayout.setCenter(scrollPane);
        
        Scene scene = new Scene(mainLayout);
        stage.setScene(scene);
        
        updateStatistics();
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");
        card.setPrefWidth(200);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        // Rückgabe des Cards und ValueLabels als Referenz
        card.setUserData(valueLabel);
        return card;
    }

    private VBox createStatsSection() {
        VBox statsSection = new VBox(15);
        statsSection.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-radius: 5;");

        Label statsLabel = new Label("Tagesstatistiken");
        statsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        HBox statsCards = new HBox(20);
        statsCards.setAlignment(Pos.CENTER);

        VBox totalCard = createStatCard("Gesamtumsatz", "€0.00", "#27ae60");
        totalSalesLabel = (Label) totalCard.getUserData();

        VBox countCard = createStatCard("Transaktionen", "0", "#3498db");
        transactionCountLabel = (Label) countCard.getUserData();

        VBox avgCard = createStatCard("Ø Bonwert", "€0.00", "#f39c12");
        averageTransactionLabel = (Label) avgCard.getUserData();

        statsCards.getChildren().addAll(totalCard, countCard, avgCard);

        HBox exportBox = new HBox(10);
        exportBox.setAlignment(Pos.CENTER_LEFT);
        exportBox.setPadding(new Insets(10, 0, 0, 0));

        Button exportTodayButton = createStyledButton("Heute exportieren", "#8e44ad");
        Button exportAllButton = createStyledButton("Alles exportieren", "#34495e");
        Button clearButton = createStyledButton("Kassenbuch leeren", "#e74c3c");

        exportTodayButton.setOnAction(e -> exportToday());
        exportAllButton.setOnAction(e -> exportAll());
        clearButton.setOnAction(e -> clearCashBook());

        exportBox.getChildren().addAll(exportTodayButton, exportAllButton, clearButton);

        statsSection.getChildren().addAll(statsLabel, statsCards, exportBox);
        return statsSection;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox(10);
        tableSection.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-radius: 5;");

        Label tableLabel = new Label("Transaktionsverlauf");
        tableLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        transactionTable = new TableView<>();
        transactionTable.setPrefHeight(400);

        TableColumn<Transaction, String> timeCol = new TableColumn<>("Zeit");
        timeCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
        );
        timeCol.setPrefWidth(80);

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Datum");
        dateCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
        );
        dateCol.setPrefWidth(100);

        TableColumn<Transaction, String> itemsCol = new TableColumn<>("Artikel");
        itemsCol.setCellValueFactory(cellData -> {
            StringBuilder items = new StringBuilder();
            for (OrderItem item : cellData.getValue().getItems()) {
                if (items.length() > 0) items.append(", ");
                items.append(item.getQuantity()).append("x ").append(item.getProduct().getName());
            }
            return new SimpleStringProperty(items.toString());
        });
        itemsCol.setPrefWidth(300);

        TableColumn<Transaction, Double> totalCol = new TableColumn<>("Gesamt");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setCellFactory(col -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("€" + String.format("%.2f", item));
                }
            }
        });
        totalCol.setPrefWidth(100);

        TableColumn<Transaction, Double> receivedCol = new TableColumn<>("Erhalten");
        receivedCol.setCellValueFactory(new PropertyValueFactory<>("received"));
        receivedCol.setCellFactory(col -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("€" + String.format("%.2f", item));
                }
            }
        });
        receivedCol.setPrefWidth(100);

        TableColumn<Transaction, Double> changeCol = new TableColumn<>("Rückgeld");
        changeCol.setCellValueFactory(new PropertyValueFactory<>("change"));
        changeCol.setCellFactory(col -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("€" + String.format("%.2f", item));
                }
            }
        });
        changeCol.setPrefWidth(100);

        TableColumn<Transaction, Void> detailCol = new TableColumn<>("Details");
        detailCol.setCellFactory(col -> new TableCell<Transaction, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Transaction transaction = getTableRow().getItem();
                    Button detailBtn = createStyledButton("Details", "#3498db");
                    detailBtn.setOnAction(e -> showTransactionDetails(transaction));
                    setGraphic(detailBtn);
                }
            }
        });
        detailCol.setPrefWidth(80);

        transactionTable.getColumns().addAll(timeCol, dateCol, itemsCol, totalCol, receivedCol, changeCol, detailCol);

        refreshTable();

        tableSection.getChildren().addAll(tableLabel, transactionTable);
        return tableSection;
    }
    
    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 12; -fx-background-radius: 5; -fx-padding: 5 10;");
        return btn;
    }
    
    private void refreshTable() {
        transactionTable.getItems().clear();
        transactions.stream()
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .forEach(transactionTable.getItems()::add);
    }
    
    private void updateStatistics() {
        double totalSales = transactions.stream()
                .mapToDouble(Transaction::getTotal)
                .sum();
        
        int transactionCount = transactions.size();
        
        double averageTransaction = transactionCount > 0 ? totalSales / transactionCount : 0.0;
        
        totalSalesLabel.setText("€" + String.format("%.2f", totalSales));
        transactionCountLabel.setText(String.valueOf(transactionCount));
        averageTransactionLabel.setText("€" + String.format("%.2f", averageTransaction));
    }
    
    private void showTransactionDetails(Transaction transaction) {
        if (transaction == null) return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transaktionsdetails");
        alert.setHeaderText("Transaktion vom " +
            transaction.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

        StringBuilder details = new StringBuilder();
        details.append("Artikel:\n");

        for (OrderItem item : transaction.getItems()) {
            details.append(String.format("• %dx %s - €%.2f",
                item.getQuantity(),
                item.getProduct().getName(),
                (item.getProduct().getPrice() - item.getDiscount()) * item.getQuantity()));

            if (item.getDiscount() > 0) {
                details.append(String.format(" (Rabatt: €%.2f)", item.getDiscount() * item.getQuantity()));
            }
            details.append("\n");
        }

        details.append(String.format("\nGesamt: €%.2f", transaction.getTotal()));
        details.append(String.format("\nErhalten: €%.2f", transaction.getReceived()));
        details.append(String.format("\nRückgeld: €%.2f", transaction.getChange()));

        alert.setContentText(details.toString());
        alert.getDialogPane().setPrefWidth(400);
        alert.showAndWait();
    }
    
    private void exportToday() {
        // Hier würde die Export-Logik für heute implementiert
        showAlert("Export", "Tagesabschluss wurde exportiert (Funktion wird implementiert).");
    }
    
    private void exportAll() {
        // Hier würde die Export-Logik für alle Daten implementiert
        showAlert("Export", "Alle Daten wurden exportiert (Funktion wird implementiert).");
    }
    
    private void clearCashBook() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Kassenbuch leeren");
        alert.setHeaderText("Möchten Sie wirklich alle Transaktionen löschen?");
        alert.setContentText("Diese Aktion kann nicht rückgängig gemacht werden!");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            transactions.clear();
            refreshTable();
            updateStatistics();
            showAlert("Erfolg", "Kassenbuch wurde geleert.");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void show() {
        stage.show();
    }
}