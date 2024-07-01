package com.example.pulsedaily;

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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Client email TODO
        String clientEmail = "alice@techinnovators.com";

        // Import fonts
        URL fontUrl = getClass().getResource("/fonts/ReadexPro.ttf");
        Font.loadFont(fontUrl.toExternalForm(), 12);
        fontUrl = getClass().getResource("/fonts/WorkSans.ttf");
        Font.loadFont(fontUrl.toExternalForm(), 12);
        fontUrl = getClass().getResource("/fonts/WorkSans-Italic.ttf");
        Font.loadFont(fontUrl.toExternalForm(), 12);
        fontUrl = getClass().getResource("/fonts/Courgette.ttf");
        Font.loadFont(fontUrl.toExternalForm(), 12);

        // Create grid
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10); // gap between col
        grid.setVgap(10); // gap between rows
        grid.setPadding(new Insets(25, 25, 25, 25)); // top right bottom left

        // Read from zenquotes api to pull quote of the day
        URL quote_url = new URL("https://zenquotes.io/api/random");
        HttpURLConnection quote_connection = (HttpURLConnection) quote_url.openConnection(); // make api call
        quote_connection.setRequestMethod("GET");

        // Read api response
        BufferedReader reader = new BufferedReader(new InputStreamReader(quote_connection.getInputStream()));
        StringBuilder quote_response = new StringBuilder();
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            quote_response.append(inputLine);
        }
        reader.close();
        quote_connection.disconnect();
        String quote_response_string = quote_response.toString();

        // Convert to json
        JSONArray jsonArray = new JSONArray(quote_response_string);
        JSONObject jsonObject = jsonArray.getJSONObject(0); // Assuming API returns an array with one object

        // Parse out author and quote
        String quoteText = jsonObject.getString("q");
        String quoteAuthor = jsonObject.getString("a");

        System.out.println("Quote: " + quoteText);
        System.out.println("Author: " + quoteAuthor);

        // Add quote banner
        Text quotetarget = new Text(quoteText + "\n - " + quoteAuthor);
        quotetarget.setFill(Color.rgb(74, 63, 43));
        quotetarget.setFont(Font.font("Courgette", FontWeight.BOLD, 12));
        quotetarget.wrappingWidthProperty().bind(grid.widthProperty().subtract(50)); // set to size based on window
        grid.add(quotetarget, 0, 0, 5, 1);

        // Connect to rds api endpoint
        String requestBody = "{\"email\": \"" + clientEmail + "\" }";
        int questionID = 0;
        String question = "";

        try {
            URL url = new URL("https://xzrnwqkv35.execute-api.us-east-1.amazonaws.com/questions");
            HttpURLConnection question_connection = (HttpURLConnection) url.openConnection();
            question_connection.setRequestMethod("POST");
            question_connection.setRequestProperty("Content-Type", "application/json");
            question_connection.setDoOutput(true);

            // Add request body to stream
            DataOutputStream outputStream = new DataOutputStream(question_connection.getOutputStream());
            outputStream.writeBytes(requestBody);
            outputStream.flush();
            outputStream.close();

            // Read response
            reader = new BufferedReader(new InputStreamReader(question_connection.getInputStream()));
            String cur_line;
            StringBuffer question_response = new StringBuffer();
            while ((cur_line = reader.readLine()) != null) {
                question_response.append(cur_line);
            }
            reader.close();
            question_connection.disconnect();
            String question_response_string = question_response.toString();
            JSONObject quoteJSON = new JSONObject(question_response_string);
            questionID = quoteJSON.getInt("question_id");
            question = quoteJSON.getString("content");

            System.out.println("questionID: " + questionID);
            System.out.println("question: " + question);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        // Add question title to frontend
        Text scenetitle = new Text(question);
        scenetitle.setFont(Font.font("Readex Pro", FontWeight.NORMAL, 18));
        scenetitle.wrappingWidthProperty().bind(grid.widthProperty().subtract(50));
        grid.add(scenetitle, 0, 1, 5, 1); // col index  |  row index  |  cols spanned  |  rows spanned
        GridPane.setMargin(scenetitle, new Insets(0, 0, 5, 0)); // add margin

        // Add radio buttons for 1-5
        ToggleGroup group = new ToggleGroup(); // radio buttons 1-5
        for (int i = 1; i <= 5; i++) {
            RadioButton radioButton = new RadioButton();
            radioButton.setText(Integer.toString(i));
            radioButton.setToggleGroup(group);
            grid.add(radioButton, i-1, 2, 1, 1);
            ColumnConstraints column = new ColumnConstraints(); // set radio buttons to be spread out
            column.setPercentWidth(100 / 5);
            grid.getColumnConstraints().add(column);
        }

        // Add heading for additional comments
        Text detailsHeading = new Text("Additional comments:");
        detailsHeading.setFont(Font.font("Readex Pro", FontWeight.NORMAL, 15));
        detailsHeading.wrappingWidthProperty().bind(grid.widthProperty().subtract(50)); // set to size based on window
        grid.add(detailsHeading, 0, 3, 5, 1);
        GridPane.setMargin(detailsHeading, new Insets(10, 0, 0, 0)); // add margin

        // Add text field for additional comments
        TextField userTextField = new TextField();
        userTextField.setFont(Font.font("Work Sans", FontWeight.NORMAL, 15));
        grid.add(userTextField, 0, 4, 5, 1);
        GridPane.setMargin(userTextField, new Insets(0, 0, 5, 0)); // add margin

        // Add submit button
        Button btn = new Button("Submit");
        btn.setFont(Font.font("Work Sans", FontWeight.BOLD, 14));
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 0, 5, 5, 1);

        // Add confirm text
        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 6, 5, 1);
        actiontarget.setFont(Font.font("Work Sans", FontWeight.NORMAL, 10));

        int finalQuestionID = questionID;
        btn.setOnAction(new EventHandler<ActionEvent>() { // handle form submission
            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);

                RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
                int ratingValue = 0; // Default value if no radio button is selected
                if (selectedRadioButton == null) {
                    actiontarget.setText("Must indicate a rating."); // error message
                    return;
                } else {
                    actiontarget.setText("Thanks for your input."); // success message
                    ratingValue = Integer.parseInt(selectedRadioButton.getText());
                }
                String notes = userTextField.getText();

                // Post the response data to rds
                try {
                    // Connect to rds api endpoint
                    URL url = new URL("https://xzrnwqkv35.execute-api.us-east-1.amazonaws.com/respond");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Add request body
                    String requestBody = "{\"email\": \"" + clientEmail + "\", \"question_id\": " + Integer.toString(finalQuestionID) + ", \"rating\": " + Integer.toString(ratingValue) + ", \"details\": \"" + notes +"\" }";
                    System.out.println(requestBody);
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(requestBody);
                    outputStream.flush();
                    outputStream.close();

                    // Read response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuffer api_response = new StringBuffer();
                    while ((line = reader.readLine()) != null) {
                        api_response.append(line);
                    }
                    reader.close();
//                    System.out.println("API response: " + api_response.toString());
                    connection.disconnect();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                // Hold the scene for a second then close
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> {
                    stage.close(); // Close the stage (application window)
                });
                pause.play();
            }
        });

        // Launch scene
        Scene scene = new Scene(grid, 350, 350);
        scene.getStylesheets().add(getClass().getResource("/form.css").toExternalForm()); // add css
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}