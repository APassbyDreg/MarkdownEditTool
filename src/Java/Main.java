import GUI.IntroPageController;
import Global.GlobalVariables;
import javafx.application.Application;
import javafx.collections.ObservableList;
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
        if (src.endsWith(".jar")) {
            GlobalVariables.programAbsolutePath = src.substring(0, src.length() - GlobalVariables.jarName.length() - ".jar".length());
        }
        else {
            GlobalVariables.programAbsolutePath = src;
        }

        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        IntroPageController.display(primaryStage);
    }
}