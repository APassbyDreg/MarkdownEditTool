package gui;

import global.Global;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class Notification {

    public static void display(String title,String msg) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        Label content = new Label(msg);
        content.setTextAlignment(TextAlignment.CENTER);
        VBox container = new VBox();

        content.setStyle("-fx-font-size: 15");
        container.setAlignment(Pos.CENTER);
        container.getChildren().add(content);
        Image logoPNG = new Image(Global.logoRelativePath);
        window.setTitle(title);
        window.getIcons().add(logoPNG);
        window.setScene(new Scene(container,320,40));
        window.setResizable(false);

        Timer timer = new Timer();
        timer.schedule(new task(window,content), 0, 1000);

        window.showAndWait();
    }

    public static class task extends TimerTask {
        private int runTimes = 0;
        private Stage window;
        private Label label;

        task(Stage w, Label l) {
            runTimes = 0;
            window = w;
            label = l;
        }

        @Override
        public void run() {
            runTimes++;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (runTimes >= Global.NOTIFICATION_TIME_OUT) {
                        window.close();
                    }
                    else {
                        String timeInfo = "(automatically closing in " + (Global.NOTIFICATION_TIME_OUT - runTimes) + "s)";
                        label.setText(label.getText().split("\n")[0] + "\n" + timeInfo);
                    }
                }
            });
        }
    }
}
