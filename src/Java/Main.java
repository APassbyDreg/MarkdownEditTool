import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) throws IOException {
        launch();
    }

    public static void show() {launch(); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/GUI_intro.fxml"));
        Image logoPNG = new Image("Logo.png");
        Stage window = primaryStage;
        Scene scene = new Scene(root, 1050, 600);
        scene.getStylesheets().add("IntroPageDesign.css");
        window.setTitle("MDEditTool");
        window.getIcons().add(logoPNG);
        window.setScene(scene);
        window.setResizable(false);
        window.show();
    }
}