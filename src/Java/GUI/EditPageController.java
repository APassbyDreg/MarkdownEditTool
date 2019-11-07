package GUI;

import Convert.Converter;
import Global.Global;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import File.*;

public class EditPageController implements Initializable{
    public static MarkdownFile md;
    private static WebFile web;
    private static ProgramInfo settings;
    private static Stage window;
    private static TextArea editor;
    private static WebView preview;
    private static WebEngine previewEngine;
    private static RadioMenuItem[] themesToggleGroupItems, fontSizeToggleGroupItems, fontWeightToggleGroupItems;
    private static RadioMenuItem previewOnlySwitch, editorOnlySwitch, autoSaveSwitch, autoScrollSwitch;
    private static ToggleGroup themesToggleGroup = new ToggleGroup(), fontSizeToggleGroup = new ToggleGroup(), FontWeightToggleGroup = new ToggleGroup();
    private static Label totalCharNumIndicator, lastSaveTimeIndicator;
    private static SplitPane splitView;
    private static boolean isScrollSyncing = true, autoScroll = true;

    @FXML public SplitPane splitPane;
    @FXML public TextArea editPane;
    @FXML public WebView previewPane;
    @FXML public Menu themeMenu,fontSizeMenu,fontWeightMenu;
    @FXML public MenuItem newButton,openButton,closeButton,saveButton,returnButton;
    @FXML public Label charNumLabel,lastSaveTimeLabel;
    @FXML public VBox mainPane;
    @FXML public RadioMenuItem previewOnlyButton,editorOnlyButton,autoSaveButton,autoScrollButton;

    public static void displayEditWindow(String mdPath, ProgramInfo editor) throws IOException {
        if (mdPath.equals("")) {
            mdPath = Global.programAbsolutePath + Global.tmpFolderPath + Global.tmpMDName;
            md = new MarkdownFile(mdPath);
            md.makeTemp();
        }
        else {
            md = new MarkdownFile(mdPath);
        }

        // create basic files
        settings = editor;
        web = new WebFile(Global.programAbsolutePath + Global.tmpFolderPath + Global.tmpHTMLName);
        web.makeTemp();

        // load window
        window = new Stage();
        Parent root = FXMLLoader.load(EditPageController.class.getResource(Global.editFXMLPath));
        Image logoPNG = new Image(Global.logoRelativePath);
        window.setTitle(Global.programName + " : " + md.name);
        window.getIcons().add(logoPNG);
        Scene scene = new Scene(root, 1200, 720);
        scene.getStylesheets().add(Global.editPageDesignPath);
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
        window.widthProperty().addListener((obs, oldVal, newVal)->{
            changePreviewStatus();
        });
        window.heightProperty().addListener((obs, oldVal, newVal)->{
            changePreviewStatus();
        });
        window.show();
        setEditorStyle(settings.currentTheme);

        if (mdPath.equals("")) {
            md.makeTemp();
        }
    }

    private static void refresh() throws IOException {
        md.str = editor.getText().replace("\t","  ");
        Converter converter = new Converter(md, web, settings);
        previewEngine.loadContent(converter.getHTML());
        if (md.isChanged()) {
            window.setTitle(Global.programName + " : " + md.name + "*");
        }
        else {
            window.setTitle(Global.programName + " : " + md.name);
        }
        updateCharNum();
        if (settings.getAutoSaveStatus()) {
            save();
        }
    }

    private static boolean save() throws IOException {
        boolean isSuccessful = true;
        if (md.isTemp()) {
            String content = md.str;
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Markdown Docs", "*.md");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(window);
            if (file != null) {
                md = new MarkdownFile(file.getAbsolutePath());
                md.str = content;
                md.save();
                window.close();
                settings.addNewRecentFile(file.getAbsolutePath());
                displayEditWindow(file.getAbsolutePath(), settings);
            }
            else {
                isSuccessful = false;
            }
        }
        else if (md.isChanged()) {
            md.save();
            updateLastSaveTime();
            window.setTitle(Global.programName + " : " + md.name);
        }
        return isSuccessful;
    }

    private static void chooseTheme(int index) throws IOException {
        String name = themesToggleGroupItems[index].getText() + ".css";
        int i = settings.themesList.indexOf(name);
        settings.setTheme(i);
        setEditorStyle(name);
        refresh();
    }

    private static void setEditorStyle(String name) throws IOException {
        FileInfo style = new FileInfo(Global.resourcePath + Global.themesFolderPath + name,'r');
        Pattern bg = Pattern.compile("body *\\{[\\s\\S]*?background-color:(.*?);[\\s\\S]*");
        Pattern text = Pattern.compile("body *\\{[\\s\\S]*?[^-]color: (#.*?);[\\s\\S]*");
        Matcher bgMatcher = bg.matcher(style.str);
        Matcher textMatcher = text.matcher(style.str);
        String editorStyle = "";
        if (bgMatcher.find()) {
            editorStyle += "-fx-control-inner-background: " + bgMatcher.group(1) + ";";
            editor.lookup(".track").setStyle("-fx-background-color : " + bgMatcher.group(1) + ";");
        }
        if (textMatcher.find()) {
            editorStyle += "-fx-text-fill:" + textMatcher.group(1) + ";";
            editor.lookup(".thumb").setStyle("-fx-background-color :derive(" + textMatcher.group(1) + ",90.0%);");
        }
        editor.setStyle(editorStyle);
    }

    private static void setEditorFont() {
        fontSizeToggleGroupItems[settings.getFontSize()].setSelected(true);
        fontWeightToggleGroupItems[settings.getFontWeight()].setSelected(true);
        editor.setFont(Font.loadFont(Thread.currentThread().getContextClassLoader().getResourceAsStream(Global.fontsPath[settings.getFontWeight()]),Global.fontSizeList[settings.getFontSize()]));
    }

    private static boolean closeProgram() throws IOException {
        boolean isClosing = true;
        char usrConfirm = 'c';
        if (md.isChanged()){
            if (md.isTemp()) {
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
            if (web.isTemp()) {
                web.delete();
            }
            if (md.isTemp()) {
                md.delete();
            }
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

    private static void changeAutoSaveStatus() throws IOException {
        settings.setAutoSave(autoSaveSwitch.isSelected());
    }

    private static void syncScroll() {
        double yPos = editor.getScrollTop();
        double height = editor.getHeight();
        double editorHeight = ((Text)editor.lookup(".text")).getBoundsInLocal().getHeight();
        int previewHeight = (Integer) previewEngine.executeScript("document.body.scrollHeight");
        previewEngine.executeScript("window.scrollTo(0," + (previewHeight-height)*(yPos/(editorHeight-height)) + ")");
    }

    private static void changePreviewStatus() {
        if (previewOnlySwitch.isSelected()) {
            editor.setEditable(false);
            editor.setVisible(false);
            splitView.setDividerPositions(0);
            splitView.lookupAll(".split-pane-divider").forEach(div ->  div.setMouseTransparent(true));
        }
        else if (editorOnlySwitch.isSelected()) {
            editor.setEditable(true);
            editor.setVisible(true);
            splitView.setDividerPositions(1);
            splitView.lookupAll(".split-pane-divider").forEach(div ->  div.setMouseTransparent(true));
        }
        else {
            editor.setEditable(true);
            editor.setVisible(true);
            splitView.setDividerPositions(0.4);
            splitView.lookupAll(".split-pane-divider").forEach(div ->  div.setMouseTransparent(false));
        }
    }

    private void hotKeyHandler(KeyEvent e) throws IOException {
        if (e.isControlDown() && e.getCode() == KeyCode.S) {
            if (e.isAltDown()) {
                saveAsMarkdown();
            }
            save();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.N) {
            openNewFile();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.O) {
            openExistedFile();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.H) {
            openAboutPage();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.R) {
            returnIndex();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.Q) {
            closeProgram();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.E) {
            editorOnlySwitch.setSelected(!editorOnlySwitch.isSelected());
            previewOnlySwitch.setSelected(false);
            changePreviewStatus();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.P) {
            previewOnlySwitch.setSelected(!previewOnlySwitch.isSelected());
            editorOnlySwitch.setSelected(false);
            changePreviewStatus();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.EQUALS) {
            if (e.isShiftDown()) {
                settings.setFontWeight((settings.getFontWeight()+1)%Global.fontsName.length);
            }
            else {
                settings.setFontSize((settings.getFontSize()+1)%Global.fontSizeList.length);
            }
            setEditorFont();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.MINUS) {
            if (e.isShiftDown()) {
                settings.setFontWeight((settings.getFontWeight()+Global.fontsName.length-1)%Global.fontsName.length);
            }
            else {
                settings.setFontSize((settings.getFontSize()+Global.fontSizeList.length-1)%Global.fontSizeList.length);
            }
            setEditorFont();
        }
    }

    public void openNewFile() throws IOException {
        if (closeProgram()) {
            displayEditWindow("",settings);
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
                displayEditWindow(file.getAbsolutePath(),settings);
            }
        }
    }

    public void setSaveButton() throws IOException {
        save();
    }

    public void setCloseButton() throws IOException {
        closeProgram();
    }

    public void saveAsMarkdown() throws IOException {
        save();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Markdown", "*.md");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(window);
        if (file !=  null) {
            String content = md.str;
            FileInfo newMD = new FileInfo(file.getAbsolutePath(), 'a');
            newMD.str = content;
            newMD.save();
            settings.addNewRecentFile(file.getAbsolutePath());
            displayEditWindow(file.getAbsolutePath(),settings);
        }
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
            Parent root = FXMLLoader.load(getClass().getResource(Global.introFXMLPath));
            Image logoPNG = new Image(Global.logoRelativePath);
            Stage index = new Stage();
            Scene scene = new Scene(root, 1050, 600);
            scene.getStylesheets().add(Global.introPageDesignPath);
            index.setTitle(Global.programName);
            index.getIcons().add(logoPNG);
            index.setScene(scene);
            index.setResizable(false);
            index.show();
        }
    }

    public void openAboutPage() {
        AboutPage.display();
    }

    public void switchAutoScroll() {
        autoScroll = autoScrollSwitch.isSelected();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editor = editPane;
        preview = previewPane;
        splitView = splitPane;
        previewEngine = previewPane.getEngine();
        totalCharNumIndicator = charNumLabel;
        lastSaveTimeIndicator = lastSaveTimeLabel;
        previewOnlySwitch = previewOnlyButton;
        editorOnlySwitch = editorOnlyButton;
        autoSaveSwitch = autoSaveButton;
        autoScrollSwitch = autoScrollButton;

        // ini preview pane
        try {
            Converter converter = new Converter(md, web, settings);
            previewEngine.loadContent(converter.getHTML());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ini themes select list
        themesToggleGroupItems = new RadioMenuItem[settings.themesList.size()];
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
            themeMenu.getItems().add(themesToggleGroupItems[i]);
        }

        // ini font size select list
        fontSizeToggleGroupItems = new RadioMenuItem[Global.fontSizeList.length];
        for (int i=0; i<Global.fontSizeList.length; i++) {
            fontSizeToggleGroupItems[i] = new RadioMenuItem(String.valueOf(Global.fontSizeList[i]));
            fontSizeToggleGroupItems[i].setToggleGroup(fontSizeToggleGroup);
            int fontSizeIndex = i;
            fontSizeToggleGroupItems[i].setOnAction(event -> {
                try {
                    settings.setFontSize(fontSizeIndex);
                    setEditorFont();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            if (Global.fontSizeList[i] == settings.getFontSize()) {
                fontSizeToggleGroupItems[i].setSelected(true);
            }
            fontSizeMenu.getItems().add(fontSizeToggleGroupItems[i]);
        }

        // ini font weight select list
        fontWeightToggleGroupItems = new RadioMenuItem[Global.fontsName.length];
        for (int i=0; i<Global.fontsName.length; i++) {
            fontWeightToggleGroupItems[i] = new RadioMenuItem(Global.fontsName[i]);
            fontWeightToggleGroupItems[i].setToggleGroup(FontWeightToggleGroup);
            int fontWeightIndex = i;
            fontWeightToggleGroupItems[i].setOnAction(event -> {
                try {
                    settings.setFontWeight(fontWeightIndex);
                    setEditorFont();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            if (i == settings.getFontWeight()) {
                fontWeightToggleGroupItems[i].setSelected(true);
            }
            fontWeightMenu.getItems().add(fontWeightToggleGroupItems[i]);
        }

        // ini switches
        previewOnlyButton.setOnAction(event -> {
            editorOnlySwitch.setSelected(false);
            changePreviewStatus();
        });
        editorOnlyButton.setOnAction(event -> {
            previewOnlySwitch.setSelected(false);
            changePreviewStatus();
        });
        autoSaveButton.setSelected(settings.getAutoSaveStatus());
        autoSaveButton.setOnAction(event -> {
            try {
                changeAutoSaveStatus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        autoScrollButton.setSelected(true);

        // ini edit pane
        editor.setText(md.str);
        editor.setOnKeyReleased(e -> {
            try {
                isScrollSyncing = true;
                refresh();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        editor.scrollTopProperty().addListener((obj, start, end) -> {isScrollSyncing=true;});
        previewPane.setOnScroll(e->{isScrollSyncing=false;});
        setEditorFont();

        // ini hot key listener
        mainPane.setOnKeyPressed(e -> {
            try {
                hotKeyHandler(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // ini bottom bar
        updateCharNum();

        // sync scroll bars
//        editor.scrollTopProperty().addListener((obj,start,end) -> {syncScroll();});
        Timer t = new Timer();
        t.schedule(new task(),0,1);
    }

    static class task extends TimerTask {

        /**
         * The action to be performed by this timer task.
         */
        @Override
        public void run() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (isScrollSyncing && autoScroll) {
                        syncScroll();
                    }
                }
            });
        }
    }
}
