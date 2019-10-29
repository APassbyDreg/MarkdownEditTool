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
import javafx.scene.web.WebView;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import File.*;

import javax.sound.midi.spi.MidiDeviceProvider;
import java.util.concurrent.atomic.AtomicBoolean;

public class EditPage implements Initializable{
    public static MarkdownFile md;
    public static WebFile web;
    public static Converter converter;

    @FXML public static TextArea editPane;
    @FXML public static WebView previewPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(111);
    }
}
