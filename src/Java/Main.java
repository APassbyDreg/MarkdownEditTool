import File.FileInfo;
import File.ProgramInfo;
import GUI.IntroPage;
import Global.GlobalVariables;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Pattern;

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
        window.setTitle("JMDTool");
        window.getIcons().add(logoPNG);
        window.setScene(new Scene(root, 1050, 600));
        window.setResizable(false);
        window.show();
    }
}