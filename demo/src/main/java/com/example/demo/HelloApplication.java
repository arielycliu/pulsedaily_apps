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

import java.io.IOException;
import java.net.URL;


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

        Text scenetitle = new Text("Do you have all the necessary tools you need to do your best work?");
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

//                double ratingValue = ratingSlider.getValue();
                RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
                int ratingValue = 0; // Default value if no radio button is selected
                if (selectedRadioButton == null) {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Must indicate a rating.");
                    return;
                } else {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Form successfully submitted.");
                    ratingValue = Integer.parseInt(selectedRadioButton.getText());
                }
                String notes = userTextField.getText();

                System.out.println("Rating: " + Integer.toString(ratingValue));
                System.out.println("Additional Notes: " + notes);
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