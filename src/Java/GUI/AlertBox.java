package GUI;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class AlertBox {

    //Create variable
    static char answer;

    public static char display(String fileName) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("FILE NOT SAVE ALERT");
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(fileName + "is changed but NOT saved, do you want to save it before close?");

        //Create two buttons
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");
        Button cancelButton = new Button("Cancel");

        //Clicking will set answer and close window
        yesButton.setOnAction(e -> {
            answer = 'y';
            window.close();
        });
        noButton.setOnAction(e -> {
            answer = 'n';
            window.close();
        });
        cancelButton.setOnAction(event -> {
            answer = 'c';
            window.close();
        });

        VBox layout = new VBox(10);

        //Add buttons
        layout.getChildren().addAll(label, yesButton, noButton, cancelButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        //Make sure to return answer
        return answer;
    }

}