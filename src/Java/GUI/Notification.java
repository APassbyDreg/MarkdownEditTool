package GUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Notification {
    public static void display(String title,String msg) {
        Stage window = new Stage();
        Label content = new Label(msg);
        VBox container = new VBox();

        content.setStyle("-fx-font-size: 16");
        container.setAlignment(Pos.CENTER);
        container.getChildren().add(content);
        Image logoPNG = new Image("Logo.png");
        window.setTitle(title);
        window.getIcons().add(logoPNG);
        window.setScene(new Scene(container,320,40));
        window.setResizable(false);
        window.showAndWait();
    }
}
