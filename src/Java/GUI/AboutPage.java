package GUI;

import Global.Global;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class AboutPage {
    public static boolean isOn = false;

    public static void display() {
        if (!isOn) {
            isOn = true;
            Stage about = new Stage();
            WebView aboutView = new WebView();
            WebEngine aboutEngine = aboutView.getEngine();
            aboutEngine.load(Global.aboutUrl);
            Image logoPNG = new Image(Global.logoRelativePath);
            about.setOnCloseRequest(event -> {
                isOn = false;
            });
            about.setTitle(Global.programName + " : About");
            about.getIcons().add(logoPNG);
            about.setScene(new Scene(aboutView,1200,800));
            about.show();
        }
    }
}
