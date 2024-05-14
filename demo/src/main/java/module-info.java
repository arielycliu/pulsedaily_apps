module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires aws.java.sdk.sqs;
    requires aws.java.sdk.core;
    requires org.json;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}