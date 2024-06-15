module com.example.pulsedaily {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    opens com.example.pulsedaily to javafx.fxml;
    exports com.example.pulsedaily;
}