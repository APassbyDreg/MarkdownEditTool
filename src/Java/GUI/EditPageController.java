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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.* ;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;
import File.*;

import javax.sound.midi.spi.MidiDeviceProvider;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EditPageController implements Initializable{
    public static Stage window;
    public static MarkdownFile md;
    public static WebFile web;
    public static ProgramInfo settings;
    public static boolean isChanged = false;

    public static TextArea editor;
    public static WebView preview;
    public static WebEngine previewEngine;
    public static Menu themeSelector;
    public static RadioMenuItem[] themesToggleGroupItems;
    public static ToggleGroup themesToggleGroup = new ToggleGroup();

    @FXML public TextArea editPane;
    @FXML public WebView previewPane;
    @FXML public Menu themeMenu;

    public static void displayEditWindow(String mdsrc) throws IOException {
        settings = new ProgramInfo();
        themesToggleGroupItems = new RadioMenuItem[settings.themesList.size()];
        md = new MarkdownFile(mdsrc);
        web = new WebFile(GlobalVariables.programAbsolutePath + "\\tmp\\pre_render_html.html");

        window = new Stage();
        Parent root = FXMLLoader.load(EditPageController.class.getResource("/fxml/GUI_edit.fxml"));
        Image logoPNG = new Image("Logo.png");
        window.setTitle("MDEditTool : " + md.name);
        window.getIcons().add(logoPNG);
        window.setScene(new Scene(root, 1200, 720));
        window.show();
    }

    public static void refresh() throws IOException {
        md.str = editor.getText().replace("\t","  ");
        Converter converter = new Converter(md, web, settings);
        previewEngine.loadContent(converter.getHTML());
    }

    public static void save() throws IOException {
        window.setTitle("MDEditTool : " + md.name);
        if (isChanged = true) {
            md.save();
        }
    }

    public static void chooseTheme(int index) throws IOException {
        String name = themesToggleGroupItems[index].getText();
        int i = settings.themesList.indexOf(name);
        settings.setTheme(i);
        refresh();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        themeSelector = themeMenu;
        editor = editPane;
        preview = previewPane;
        previewEngine = previewPane.getEngine();

        LinkedList<String> themeList = settings.themesList;
        for (int i=0; i<themeList.size(); i++) {
            String name = themeList.get(i);
            themesToggleGroupItems[i] = new RadioMenuItem(name);
            themesToggleGroupItems[i].setToggleGroup(themesToggleGroup);
            int themeIndex = i;
            themesToggleGroupItems[i].setOnAction(e->{
                try {
                    chooseTheme(themeIndex);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            if (name.equals(settings.currentTheme)) {
                themesToggleGroupItems[i].setSelected(true);
            }
            themeSelector.getItems().add(themesToggleGroupItems[i]);
        }

        editor.setText(md.str);
        editor.setOnKeyReleased( e-> {
            try {
                isChanged = true;
                window.setTitle("MDEditTool : " + md.name + "*");
                refresh();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        try {
            Converter converter = new Converter(md, web, settings);
            previewEngine.loadContent(converter.getHTML());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
