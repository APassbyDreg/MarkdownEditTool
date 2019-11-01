import GUI.IntroPageController;
import Global.Global;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends Application {

    public static void main(String[] args) throws IOException, URISyntaxException {
        // ini program info
        String src = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        if (src.endsWith(".jar")) {
            Global.programAbsolutePath = src.substring(0, src.length() - Global.jarName.length() - ".jar".length());
        }
        else {
            Global.programAbsolutePath = src;
        }
        // launch program
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        IntroPageController.display(primaryStage);
    }
}