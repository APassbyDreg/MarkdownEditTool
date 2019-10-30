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

public class EditPageController implements Initializable{
    public static Stage window;
    public static MarkdownFile md;
    public static WebFile web;
    public static ProgramInfo settings;
    public static boolean isChanged = false;
    public static boolean isTemp = false;

    public static TextArea editor;
    public static WebView preview;
    public static WebEngine previewEngine;
    public static Menu themeSelector;
    public static RadioMenuItem[] themesToggleGroupItems;
    public static ToggleGroup themesToggleGroup = new ToggleGroup();

    @FXML public TextArea editPane;
    @FXML public WebView previewPane;
    @FXML public Menu themeMenu;
    @FXML public MenuItem newButton,openButton,closeButton,saveButton;

    public static void displayEditWindow(String mdPath) throws IOException {
        if (mdPath.equals("")) {
            mdPath = GlobalVariables.programAbsolutePath + "\\tmp\\Untitled.md";
            isTemp = true;
        }

        settings = new ProgramInfo();
        themesToggleGroupItems = new RadioMenuItem[settings.themesList.size()];
        md = new MarkdownFile(mdPath);
        web = new WebFile(GlobalVariables.programAbsolutePath + "\\tmp\\pre_render_html.html");

        window = new Stage();
        Parent root = FXMLLoader.load(EditPageController.class.getResource("/fxml/GUI_edit.fxml"));
        Image logoPNG = new Image("Logo.png");

        window.setOnCloseRequest(e -> {
            if (isChanged == true) {
                try {
                    e.consume();
                    closeProgram();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
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

    public static boolean save() throws IOException {
        boolean isSuccessful = true;

        window.setTitle("MDEditTool : " + md.name);
        if (isTemp) {
            String content = md.str;
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Markdown Docs", "*.md");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(window);
            if (file != null) {
                isTemp = false;
                md = new MarkdownFile(file.getAbsolutePath());
                md.str = content;
                md.save();
                window.close();
                settings.addNewRecentFile(file.getAbsolutePath());
                displayEditWindow(file.getAbsolutePath());
            }
        }
        else if (isChanged == true) {
            md.save();
        }

        return isSuccessful;
    }

    public static void chooseTheme(int index) throws IOException {
        String name = themesToggleGroupItems[index].getText();
        int i = settings.themesList.indexOf(name);
        settings.setTheme(i);
        refresh();
    }

    public static void closeProgram() throws IOException {
        boolean isClosing = true;
        char usrConfirm = 'c';
        if (isTemp) {
            usrConfirm = AlertBox.display("This new file has NOT been saved");
        }
        else {
            usrConfirm = AlertBox.display(md.name + " is changed but NOT saved");
        }
        switch (usrConfirm){
            case 'y':
                if (!save()) {
                    isClosing = false;
                }
                break;
            case 'n':
                break;
            case 'c':
                isClosing = false;
                break;
        }
        if (isClosing) {
            window.close();
        }
    }

    public void openNewFile() throws IOException {
        save();
        window.close();
        displayEditWindow("");
    }

    public void openExistedFile() throws IOException {
        save();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Markdown Docs", "*.md");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            window.close();
            md = new MarkdownFile(file.getAbsolutePath());
            settings.addNewRecentFile(file.getAbsolutePath());
            displayEditWindow(file.getAbsolutePath());
        }
    }

    public void setSaveButton() throws IOException {
        save();
    }

    public void setCloseButton() throws IOException {
        closeProgram();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        themeSelector = themeMenu;
        editor = editPane;
        preview = previewPane;
        previewEngine = previewPane.getEngine();

        // ini edit board
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

        // ini themes select list
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
    }
}
