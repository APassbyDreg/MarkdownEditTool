import Global.GlobalVariables;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;

public class Main extends Application {

    public static void main(String[] args) throws IOException, URISyntaxException {
        // ini program info
        String src = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        GlobalVariables.programAbsolutePath = src.substring(0, src.length() - GlobalVariables.jarName.length() - ".jar".length());

        launch();
    }

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