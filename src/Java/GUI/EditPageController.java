package GUI;

import Convert.Converter;
import Global.GlobalVariables;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import File.*;

public class EditPageController implements Initializable{
    public static MarkdownFile md;
    private static WebFile web;
    private static ProgramInfo settings;
    private static Stage window;
    private static boolean isTemp = false;

    private static TextArea editor;
    private static WebView preview;
    private static WebEngine previewEngine;
    private static RadioMenuItem[] themesToggleGroupItems;
    private static ToggleGroup themesToggleGroup = new ToggleGroup();
    private static Label totalCharNumIndicator, lastSaveTimeIndicator;

    @FXML public TextArea editPane;
    @FXML public WebView previewPane;
    @FXML public Menu themeMenu;
    @FXML public MenuItem newButton,openButton,closeButton,saveButton,returnButton;
    @FXML public Label charNumLabel,lastSaveTimeLabel;
    @FXML public VBox mainPane;

    public static void displayEditWindow(String mdPath) throws IOException {
        if (mdPath.equals("")) {
            mdPath = GlobalVariables.programAbsolutePath + "\\tmp\\Untitled.md";
            isTemp = true;
        }

        // create basic files
        settings = new ProgramInfo();
        themesToggleGroupItems = new RadioMenuItem[settings.themesList.size()];
        md = new MarkdownFile(mdPath);
        web = new WebFile(GlobalVariables.programAbsolutePath + "\\tmp\\pre_render_html.html");
        web.makeTemp();

        // load window
        window = new Stage();
        Parent root = FXMLLoader.load(EditPageController.class.getResource("/fxml/GUI_edit.fxml"));
        Image logoPNG = new Image("Logo.png");
        window.setTitle("MDEditTool : " + md.name);
        window.getIcons().add(logoPNG);
        Scene scene = new Scene(root, 1200, 720);
        scene.getStylesheets().add("EditPageDesign.css");
        window.setScene(scene);
        window.setOnCloseRequest(e -> {
            if (md.isChanged()) {
                try {
                    e.consume();
                    closeProgram();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        window.show();

        if (mdPath.equals("")) {
            md.makeTemp();
        }
    }

    public static void refresh() throws IOException {
        md.str = editor.getText().replace("\t","  ");
        Converter converter = new Converter(md, web, settings);
        previewEngine.loadContent(converter.getHTML());
        if (md.isChanged()) {
            window.setTitle("MDEditTool : " + md.name + "*");
        }
        else {
            window.setTitle("MDEditTool : " + md.name);
        }
        updateCharNum();
    }

    public static boolean save() throws IOException {
        boolean isSuccessful = true;
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
            else {
                isSuccessful = false;
            }
        }
        else if (md.isChanged()) {
            md.save();
            updateLastSaveTime();
            window.setTitle("MDEditTool : " + md.name);
        }
        return isSuccessful;
    }

    public static void chooseTheme(int index) throws IOException {
        String name = themesToggleGroupItems[index].getText() + ".css";
        int i = settings.themesList.indexOf(name);
        settings.setTheme(i);
        setEditorStyle(name);
        refresh();
    }

    public static void setEditorStyle(String name) throws IOException {
        FileInfo style = new FileInfo(settings.themesFolderRelativePath + name,'r');
        Pattern bg = Pattern.compile("body *\\{[\\s\\S]*?background-color:(.*?);[\\s\\S]*");
        Pattern text = Pattern.compile("body *\\{[\\s\\S]*?[^-]color: (#.*?);[\\s\\S]*");
        Matcher bgMatcher = bg.matcher(style.str);
        Matcher textMatcher = text.matcher(style.str);
        String editorStyle = "";
        if (bgMatcher.find()) {
            editorStyle += "-fx-control-inner-background: " + bgMatcher.group(1) + ";";
        }
        if (textMatcher.find()) {
            editorStyle += "-fx-text-fill:" + textMatcher.group(1) + ";";
        }
        editor.setStyle(editorStyle);
    }

    public static boolean closeProgram() throws IOException {
        boolean isClosing = true;
        char usrConfirm = 'c';
        if (md.isChanged()){
            if (isTemp) {
                usrConfirm = AlertBox.display("This new file has NOT been saved");
            }
            else {
                usrConfirm = AlertBox.display(md.name + " is changed but NOT saved");
            }
            switch (usrConfirm){
                case 'y': // yes
                    if (!save()) {
                        isClosing = false;
                    }
                    break;
                case 'n': // no
                    break;
                case 'c': // cancel
                    isClosing = false;
                    break;
            }
        }
        if (isClosing) {
            window.close();
        }
        return isClosing;
    }

    private static void updateCharNum() {
        int charNum = editor.getText().trim().length();
        totalCharNumIndicator.setText("Total characters: " + charNum);
    }

    private static void updateLastSaveTime() {
        String t = md.lastSaveTime;
        lastSaveTimeIndicator.setText("Last save time: " + t);
    }

    public void openNewFile() throws IOException {
        if (closeProgram()) {
            displayEditWindow("");
        }
    }

    public void openExistedFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Markdown Docs", "*.md");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(window);
        if (file !=  null) {
            if (closeProgram()) {
                md = new MarkdownFile(file.getAbsolutePath());
                settings.addNewRecentFile(file.getAbsolutePath());
                displayEditWindow(file.getAbsolutePath());
            }
        }
    }

    public void setSaveButton() throws IOException {
        save();
    }

    public void setCloseButton() throws IOException {
        closeProgram();
    }

    public void saveAsTxt() throws IOException {
        save();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Plain Text", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(window);
        if (file !=  null) {
            String content = md.str;
            FileInfo txt = new FileInfo(file.getAbsolutePath(), 'a');
            txt.str = content;
            txt.save();
        }
    }

    public void saveAsHtml() throws IOException {
        save();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Web Docs", "*.html");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(window);
        if (file !=  null) {
            String content = web.str;
            FileInfo html = new FileInfo(file.getAbsolutePath(), 'a');
            html.str = content;
            html.save();
        }
        Notification.display("Notification","plz pay attention to your image references!");
    }

    public void returnIndex() throws IOException {
        if (closeProgram()) {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/GUI_intro.fxml"));
            Image logoPNG = new Image("Logo.png");
            Stage index = new Stage();
            Scene scene = new Scene(root, 1050, 600);
            scene.getStylesheets().add("IntroPageDesign.css");
            index.setTitle("MDEditTool");
            index.getIcons().add(logoPNG);
            index.setScene(scene);
            index.setResizable(false);
            index.show();
        }
    }

    public void openAboutPage() {
        Stage about = new Stage();
        WebView aboutView = new WebView();
        WebEngine aboutEngine = aboutView.getEngine();
        aboutEngine.load("https://apassbydreg.work/2019/10/31/codemarkdownedittool-user-guide/");
        Image logoPNG = new Image("Logo.png");
        about.setTitle("MDEditTool : About");
        about.getIcons().add(logoPNG);
        about.setScene(new Scene(aboutView,1200,1200));
        about.show();
    }

    private void hotKeyHandler(KeyEvent e) throws IOException {
        if (e.isControlDown() && e.getCode() == KeyCode.S) {
            save();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.N) {
            openNewFile();
        }
        else if (e.getCode() == KeyCode.F1) {
            openAboutPage();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Menu themeSelector = themeMenu;
        editor = editPane;
        preview = previewPane;
        previewEngine = previewPane.getEngine();
        totalCharNumIndicator = charNumLabel;
        lastSaveTimeIndicator = lastSaveTimeLabel;

        // ini preview pane
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
            themesToggleGroupItems[i] = new RadioMenuItem(name.substring(0, name.length()-4));
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

        // ini edit pane
        editor.setText(md.str);
        editor.setOnKeyReleased(e -> {
            try {
                refresh();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        try {
            setEditorStyle(settings.currentTheme);
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.setFont(Font.loadFont(Thread.currentThread().getContextClassLoader().getResourceAsStream("sarasa-monoT-sc-semibold.ttf"),16));

        // ini hot key listener
        mainPane.setOnKeyPressed(e -> {
            try {
                hotKeyHandler(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        updateCharNum();
    }
}
