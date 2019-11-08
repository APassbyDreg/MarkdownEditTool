package gui;

import global.Global;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AlertBox implements Initializable {

    //Create variable
    static char answer = 'c';
    static Stage window;
    static String msg1;

    @FXML public Button yesButton,noButton,cancelButton;
    @FXML public Label fileNameLabel;

    public static char display(String line) throws IOException {
        msg1 = line;

        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(EditPageController.class.getResource(Global.alertFXMLPath));
        Image logoPNG = new Image(Global.logoRelativePath);
        Scene scene = new Scene(root, 360, 120);
        scene.getStylesheets().add(Global.alertBoxDesignPath);
        window.setTitle("File NOT Save Alert");
        window.getIcons().add(logoPNG);
        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();

        return answer;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        yesButton.setOnAction(event -> {
            answer = 'y';
            window.close();
        });
        noButton.setOnAction(event -> {
            answer = 'n';
            window.close();
        });
        cancelButton.setOnAction(event -> {
            answer = 'c';
            window.close();
        });
        fileNameLabel.setText(msg1);
    }
}