package GUI;

import Convert.Converter;
import Global.GlobalVariables;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.* ;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import File.*;

import javax.sound.midi.spi.MidiDeviceProvider;
import java.util.concurrent.atomic.AtomicBoolean;

public class EditPageController implements Initializable{
    public static MarkdownFile md;
    public static WebFile web;
    public static Converter converter;

    @FXML public TextArea editPane;
    @FXML public WebView previewPane;
    public WebEngine previewEngine;


    public static void displayEditWindow(String mdsrc) throws IOException {
        md = new MarkdownFile(mdsrc);
        web = new WebFile(GlobalVariables.programAbsolutePath + "\\tmp\\pre_render_html.html");
        converter = new Converter(md, web, new ProgramInfo());


        Stage window = new Stage();
        Parent root = FXMLLoader.load(EditPageController.class.getResource("/fxml/GUI_edit.fxml"));
        Image logoPNG = new Image("Logo.png");
        window.setTitle("MDTool");
        window.getIcons().add(logoPNG);
        window.setScene(new Scene(root, 1200, 720));
        window.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editPane.setText(md.str);
        previewEngine = previewPane.getEngine();

        try {
            previewEngine.loadContent(converter.getHTML());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
