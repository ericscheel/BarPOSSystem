module barpos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.junit.jupiter.api; // Für JUnit 5 Tests
    requires java.sql;
    
    exports com.barpos;
}