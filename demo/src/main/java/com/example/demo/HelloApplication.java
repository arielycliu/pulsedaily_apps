package com.example.demo;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        URL fontUrl = getClass().getResource("/fonts/ReadexPro.ttf");
        Font.loadFont(fontUrl.toExternalForm(), 12);
        fontUrl = getClass().getResource("/fonts/WorkSans.ttf");
        Font.loadFont(fontUrl.toExternalForm(), 12);
        fontUrl = getClass().getResource("/fonts/WorkSans-Italic.ttf");
        Font.loadFont(fontUrl.toExternalForm(), 12);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10); // gap between col
        grid.setVgap(10); // gap between rows
        grid.setPadding(new Insets(25, 25, 25, 25)); // top right bottom left

        // QUOTES
        URL url = new URL("https://zenquotes.io/api/quotes/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String response_string = response.toString();
        System.out.println(response_string);
        JSONArray jsonArray = new JSONArray(response_string);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        System.out.println("Quote: " + jsonObject.getString("q"));


        // QUESTIONS
        url = new URL("https://qjg3v3vdqa.execute-api.us-east-1.amazonaws.com/default/LambdaReadFromPulse");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        // encrypt in binary or single sign on
        connection.setRequestProperty("x-api-key", "rFvFLeKmxlaOTugyndyqq6chKSn7HkLQ1rSQPlax");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setDoOutput(true);

        responseCode = connection.getResponseCode();
//        System.out.println("Response Code: " + responseCode);
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        response_string = response.toString();
        System.out.println("Response: " + response_string);

        // save questions
        String[] parts = response_string.substring(2, response_string.length() - 2).split(", ");
        try (FileWriter writer = new FileWriter("api_questions.json")) {
            for (int i = 0; i < parts.length; i++) {
                for (int j = 0; j < parts[i].length(); j++) {
                    if (j != ']' && j != '[') {
//                        System.out.println(parts[i].charAt(j));
                        writer.write(parts[i].charAt(j));
                    }
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parse response
        int questionId = Integer.parseInt(parts[0]);
        String questionString = parts[1].substring(1, parts[1].length() - 1); // Remove quotes
        String questionType = parts[2].substring(1, parts[2].length() - 1); // Remove quotes
        System.out.println("Question 1: " + questionString);

        connection.disconnect();

        Text scenetitle = new Text(questionString);
        scenetitle.setFont(Font.font("Readex Pro", FontWeight.NORMAL, 18));
        scenetitle.wrappingWidthProperty().bind(grid.widthProperty().subtract(50));
        grid.add(scenetitle, 0, 0, 5, 1); // col index  |  row index  |  cols spanned  |  rows spanned
        GridPane.setMargin(scenetitle, new Insets(0, 0, 5, 0)); // add margin under title

//        Slider ratingSlider = new Slider(1, 5, 3); // Range from 1 to 5, default value 3
//        ratingSlider.setShowTickMarks(false);
//        ratingSlider.setShowTickLabels(true);
//        ratingSlider.setMajorTickUnit(1);
//        ratingSlider.setBlockIncrement(1);
//        grid.add(ratingSlider, 0, 1, 5, 1);

        ToggleGroup group = new ToggleGroup(); // radio buttons 1-5
        for (int i = 1; i <= 5; i++) {
            RadioButton radioButton = new RadioButton();
            radioButton.setText(Integer.toString(i));
            radioButton.setToggleGroup(group);
//            VBox box = new VBox();
//            box.setAlignment(Pos.CENTER);
//            box.getChildren().addAll(radioButton, new javafx.scene.control.Label(Integer.toString(i)));
//            grid.add(box, i - 1, 1);
            grid.add(radioButton, i-1, 1);
            // space around
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100 / 5);
            grid.getColumnConstraints().add(column);
        }

        TextField userTextField = new TextField(); // textfield
        userTextField.setFont(Font.font("Work Sans", FontWeight.NORMAL, 15));
        userTextField.setText("Additional notes");
        grid.add(userTextField, 0, 2, 5, 1);
        GridPane.setMargin(userTextField, new Insets(0, 0, 5, 0)); // add margin under title

        Button btn = new Button("Submit"); // submit button
        btn.setFont(Font.font("Work Sans", FontWeight.BOLD, 14));
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 0, 3, 5, 1);

        final Text actiontarget = new Text(); // text to confirm submission
        grid.add(actiontarget, 0, 4, 5, 1);
        actiontarget.setFont(Font.font("Work Sans", FontWeight.NORMAL, 10));

        btn.setOnAction(new EventHandler<ActionEvent>() { // handle form submission
            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Form successfully submitted.");

//              double ratingValue = ratingSlider.getValue();
                RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
                int ratingValue = 0; // Default value if no radio button is selected
                if (selectedRadioButton == null) {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Must indicate a rating.");
                    return;
                } else {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Thanks for your input.");
                    ratingValue = Integer.parseInt(selectedRadioButton.getText());
                }
                String notes = userTextField.getText();

                System.out.println("Rating: " + Integer.toString(ratingValue));
                System.out.println("Additional Notes: " + notes);

                // API starts here:
                try {
                    URL url = new URL("https://jh7gbs9u87.execute-api.us-east-1.amazonaws.com/default/LambdaWriteToPulse");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("x-api-key", "fn4Wpaiw3n1qew4dpHNkJ6cPiKDHdrHU5ehKQi3k");
                    connection.setRequestProperty("Content-Type", "application/json");

                    connection.setDoOutput(true);
                    String requestBody = "{\"ClientID\": 256, \"QuestionID\": " + Integer.toString(questionId) + ", \"ResponseNum\": " + Integer.toString(ratingValue) + ", \"Details\": \"" + notes +"\" }";
                    System.out.println(requestBody);
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(requestBody);
                    outputStream.flush();
                    outputStream.close();

        //          int responseCode = connection.getResponseCode();
        //          System.out.println("Response Code: " + responseCode);
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    System.out.println("Response: " + response.toString());

                    connection.disconnect();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> {
                    stage.close(); // Close the stage (application window)
                });
                pause.play();
            }
        });

        Scene scene = new Scene(grid, 300, 275);
        scene.getStylesheets().add(getClass().getResource("/form.css").toExternalForm()); // add css
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}