module com.example.encryptfile {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.encryptfile to javafx.fxml;
    exports com.example.encryptfile;
}