package com.example.encryptfile;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static final String KEY_FILE = "secret.key";
    private static final String DATA_FILE = "data.enc";
    private SecretKey secretKey;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load or generate secret key
            if (Files.exists(Paths.get(KEY_FILE))) {
                String keyString = new String(Files.readAllBytes(Paths.get(KEY_FILE)));
                secretKey = EncryptionUtil.loadSecretKey(keyString);
            } else {
                secretKey = EncryptionUtil.generateSecretKey();
                String keyString = EncryptionUtil.saveSecretKey(secretKey);
                Files.write(Paths.get(KEY_FILE), keyString.getBytes());
            }

            // Load data
            List<String> data;
            if (Files.exists(Paths.get(DATA_FILE))) {
                String encryptedData = EncryptionUtil.decrypt(DATA_FILE, secretKey);
                data = List.of(encryptedData.split("\n"));
            } else {
                data = List.of("Sample Data");
            }

            // Your UI code
            primaryStage.setTitle("JavaFX App");
            primaryStage.setOnCloseRequest(event -> {
                try {
                    String dataToSave = String.join("\n", data);
                    EncryptionUtil.encrypt(dataToSave, DATA_FILE, secretKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Platform.exit();
                System.exit(0);
            });

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
