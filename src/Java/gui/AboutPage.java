package gui;

import fileio.FileInfo;
import global.Global;
import javafx.application.Platform;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AboutPage {
    private static boolean isOn = false;

    static void display() throws IOException {
        if (!isOn) {
            isOn = true;
            Stage about = new Stage();
            WebView aboutView = new WebView();
            WebEngine aboutEngine = aboutView.getEngine();
            aboutEngine.loadContent(Global.aboutLoadingTip);
            Image logoPNG = new Image(Global.logoRelativePath);
            about.setOnCloseRequest(event -> {
                isOn = false;
            });
            about.setTitle(Global.programName + " : About");
            about.getIcons().add(logoPNG);
            about.setScene(new Scene(aboutView,600,750));
            about.show();
            Platform.runLater(() -> {
                try {
                    String content = loadContent();
                    if (!content.equals("")) {
                        aboutEngine.loadContent(content);
                    }
                    else {
                        aboutEngine.loadContent(Global.aboutLoadingErrorTip);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            aboutView.setOnKeyPressed(event -> {
                try {
                    hotKeyHandler(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static String loadContent() throws IOException {
        URL url = new URL(Global.aboutRawUrl);
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
        else {
            return "";
        }
        Pattern regex = Pattern.compile("\"(<[\\s\\S]+html>)\"");
        Matcher m = regex.matcher(content.toString());
        if (m.find()) {
            html = m.group(1);
        }
        else {
            html = content.toString();
        }
        return html;
    }

    private static void hotKeyHandler(KeyEvent e) throws IOException {
        if (e.isControlDown() && e.isAltDown() && e.getCode() == KeyCode.C) {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(Global.aboutUrl);
            clip.setContents(tText, null);
            Notification.display("MarkdownEditTool Message", "URL copied to keyboard");
        }
        if (e.isControlDown() && e.isAltDown() && e.getCode() == KeyCode.E) {
            Runtime.getRuntime().exec("cmd /c start " + Global.aboutUrl);
        }
    }
}
