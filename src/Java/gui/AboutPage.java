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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AboutPage {
    private static boolean isOn = false;

    static void display() throws IOException {
        URL url = new URL(Global.aboutUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        StringBuilder content = new StringBuilder();
        String html;
        if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream in = httpConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader buffer = new BufferedReader(isr);
            String line;
            while ((line = buffer.readLine()) != null) {
                content.append(line);
            }
            buffer.close();
        }
        Pattern regex = Pattern.compile("\"(<[\\s\\S]+html>)\"");
        Matcher m = regex.matcher(content.toString());
        if (m.find()) {
            html = m.group(1);
        }
        else {
            html = content.toString();
        }
        System.out.println(html);

        if (!isOn) {
            isOn = true;
            Stage about = new Stage();
            WebView aboutView = new WebView();
            aboutView.setOnKeyPressed(event -> hotKeyHandler(event));
            WebEngine aboutEngine = aboutView.getEngine();
            aboutEngine.loadContent(html);
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

    private static void hotKeyHandler(KeyEvent e) {
        if (e.isControlDown() && e.isAltDown() && e.getCode() == KeyCode.C) {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(Global.aboutUrl);
            clip.setContents(tText, null);
            Notification.display("MarkdownEditTool Message", "URL copied to keyboard");
        }
    }
}
