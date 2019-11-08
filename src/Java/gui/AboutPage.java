package gui;

import global.Global;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class AboutPage {
    public static boolean isOn = false;

    public static void display() {
        if (!isOn) {
            isOn = true;
            Stage about = new Stage();
            WebView aboutView = new WebView();
            aboutView.setOnKeyPressed(event -> hotKeyHandler(event));
            WebEngine aboutEngine = aboutView.getEngine();
            aboutEngine.load(Global.aboutUrl);
            Image logoPNG = new Image(Global.logoRelativePath);
            about.setOnCloseRequest(event -> {
                isOn = false;
            });
            about.setTitle(Global.programName + " : About");
            about.getIcons().add(logoPNG);
            about.setScene(new Scene(aboutView,600,750));
            about.show();
        }
    }

    public static void hotKeyHandler(KeyEvent e) {
        if (e.isControlDown() && e.isAltDown() && e.getCode() == KeyCode.C) {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(Global.aboutUrl);
            clip.setContents(tText, null);
            Notification.display("MarkdownEditTool Message", "URL copied to keyboard");
        }
    }
}
