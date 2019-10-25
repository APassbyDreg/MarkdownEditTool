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

    public static Stage window;
    private static int MAX_RECENT_FILES_STORED;

    public static void main(String[] args) throws IOException {
        launch();
    }

    public static void show() {launch(); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GlobalVariables.introWindow = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("GUI_intro.FXML"));
        Image logoPNG = new Image("Logo.png");
        window = primaryStage;
        window.setTitle("JMDTool");
        window.getIcons().add(logoPNG);
        window.setScene(new Scene(root, 1050, 600));
        window.setResizable(false);
        window.show();

//        window = primaryStage;
//        window.setTitle("JMDTool");
//
//        // outside framework
//        BorderPane borderPane = new BorderPane();
//        borderPane.setPadding(new Insets(40));
//
//        // left side
//        VBox left = new VBox();
//        Button buttonNEW = new Button("NEW");
//        Button buttonOPEN = new Button("OPEN");
//        setMainButtonStyle(buttonNEW);
//        setMainButtonStyle(buttonOPEN);
//
//        Image logoPNG = new Image("Logo.png");
//        ImageView logo = new ImageView();
//        logo.setImage(logoPNG);
//        logo.setFitWidth(200);
//        logo.setFitHeight(200);
//        logo.setStyle("-fx-opacity: 0.6;-fx-label-padding: 20");
//        left.getChildren().addAll(buttonNEW, buttonOPEN, logo);
//
//        // right side
//        VBox right = new VBox();
//        Button[] recentDocChooser = new Button[GlobalVariables.MAX_RECENT_FILES_STORED];
//        Label[] recentDocBrief = new Label[GlobalVariables.MAX_RECENT_FILES_STORED];
//        FileInfo[] recentDocContent = new FileInfo[GlobalVariables.MAX_RECENT_FILES_STORED];
//        LinkedList<String> recents = editor.recentFilePaths;
//        for (int i = 0; i<GlobalVariables.MAX_RECENT_FILES_STORED && recents.size()!=0; i++) {
//            recentDocContent[i] = new FileInfo(recents.pop(),'a');
//            recentDocChooser[i] = new Button(recentDocContent[i].name);
//            recentDocBrief[i] = new Label(recentDocContent[i].str.split("\n")[0]);
//            setRecentStyle(recentDocChooser[i], recentDocBrief[i]);
//            right.getChildren().addAll(recentDocChooser[i], recentDocBrief[i]);
//        }
//
//        borderPane.setLeft(left);
//        borderPane.setRight(right);
//
//        Scene scene = new Scene(borderPane, 1050, 600);
//        window.getIcons().add(logoPNG);
//        window.setScene(scene);
//        window.setResizable(false);
//        window.show();
//    }
//
//    private void setMainButtonStyle(Button bt) {
//        bt.setPrefSize(300,75);
//        bt.setStyle("-fx-font-size: 30;-fx-start-margin: 50");
//    }
//
//    private void setRecentStyle(Button bt, Label content) {
//        bt.setPrefSize(450,50);
//        bt.setStyle("-fx-font-size: 30; -fx-alignment: CENTER_LEFT;");
//        content.setStyle("-fx-font-size: 18;");
    }

}