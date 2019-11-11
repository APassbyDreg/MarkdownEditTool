package gui;

import convert.Converter;
import global.Global;
import javafx.application.Platform;
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
import fileio.*;

public class EditPageController implements Initializable{
    private MarkdownFile md;
    private WebFile web;

    private Stage window;
    private RadioMenuItem[] themesToggleGroupItems, fontSizeToggleGroupItems, fontWeightToggleGroupItems;
    private ToggleGroup themesToggleGroup = new ToggleGroup(), fontSizeToggleGroup = new ToggleGroup(), FontWeightToggleGroup = new ToggleGroup();
    private boolean isScrollSyncing = true, autoScroll = true;

    @FXML public SplitPane splitPane;
    @FXML public TextArea editPane;
    @FXML public WebView previewPane;
    @FXML public Menu themeMenu,fontSizeMenu,fontWeightMenu;
    @FXML public Label charNumLabel,lastSaveTimeLabel;
    @FXML public VBox mainPane;
    @FXML public RadioMenuItem previewOnlyButton,editorOnlyButton,autoSaveButton,autoScrollButton;

    public EditPageController() throws Exception {
        window = new Stage();
    }

    public void display(String mdPath) throws Throwable {
        if (mdPath.equals("")) {
            mdPath = Global.programAbsolutePath + Global.tmpFolderPath + Global.tmpMDName;
            md = new MarkdownFile(mdPath);
            md.makeTemp();
        }
        else {
            md = new MarkdownFile(mdPath);
        }
        if (mdPath.equals("")) {
            md.makeTemp();
        }

        // create basic files
        web = new WebFile(Global.programAbsolutePath + Global.tmpFolderPath + Global.tmpHTMLName);
        web.makeTemp();

        FXMLLoader loader = new FXMLLoader(EditPageController.class.getResource(Global.editFXMLPath));
        loader.setController(this);
        Parent root = loader.load();

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
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
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
        setEditorStyle(Global.settings.currentTheme);
    }

    private void refresh() throws Throwable {
        md.str = editPane.getText().replace("\t","  ");
        Converter converter = new Converter(md, web, Global.settings);
        previewPane.getEngine().loadContent(converter.getHTML());
        if (md.isChanged()) {
            window.setTitle(Global.programName + " : " + md.name + "*");
        }
        else {
            window.setTitle(Global.programName + " : " + md.name);
        }
        updateCharNum();
        if (Global.settings.getAutoSaveStatus()) {
            save();
        }
    }

    private boolean save() throws Throwable {
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
                Global.settings.addNewRecentFile(file.getAbsolutePath());
                EditPageController newEditPage = new EditPageController();
                newEditPage.display(file.getAbsolutePath());
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

    private void chooseTheme(int index) throws Throwable {
        String name = themesToggleGroupItems[index].getText() + ".css";
        int i = Global.settings.themesList.indexOf(name);
        Global.settings.setTheme(i);
        setEditorStyle(name);
        refresh();
    }

    private void setEditorStyle(String name) throws IOException {
        FileInfo style = new FileInfo(Global.resourcePath + Global.themesFolderPath + name,'r');
        Pattern bg = Pattern.compile("body *\\{[\\s\\S]*?background-color:(.*?);[\\s\\S]*");
        Pattern text = Pattern.compile("body *\\{[\\s\\S]*?[^-]color: (#.*?);[\\s\\S]*");
        Matcher bgMatcher = bg.matcher(style.str);
        Matcher textMatcher = text.matcher(style.str);
        String editorStyle = "";
        if (bgMatcher.find()) {
            editorStyle += "-fx-control-inner-background: " + bgMatcher.group(1) + ";";
            editPane.lookup(".track").setStyle("-fx-background-color : " + bgMatcher.group(1) + ";");
        }
        if (textMatcher.find()) {
            editorStyle += "-fx-text-fill:" + textMatcher.group(1) + ";";
            editPane.lookup(".thumb").setStyle("-fx-background-color :derive(" + textMatcher.group(1) + ",90.0%);");
        }
        editPane.setStyle(editorStyle);
    }

    private void setEditorFont() {
        fontSizeToggleGroupItems[Global.settings.getFontSize()].setSelected(true);
        fontWeightToggleGroupItems[Global.settings.getFontWeight()].setSelected(true);
        editPane.setFont(Font.loadFont(Thread.currentThread().getContextClassLoader().getResourceAsStream(Global.fontsPath[Global.settings.getFontWeight()]),Global.fontSizeList[Global.settings.getFontSize()]));
    }

    private boolean closeProgram() throws Throwable {
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

    private void updateCharNum() {
        int charNum = editPane.getText().trim().length();
        charNumLabel.setText("Total characters: " + charNum);
    }

    private void updateLastSaveTime() {
        String t = md.lastSaveTime;
        lastSaveTimeLabel.setText("Last save time: " + t);
    }

    private void changeAutoSaveStatus() throws IOException {
        Global.settings.setAutoSave(autoSaveButton.isSelected());
    }

    private void syncScroll() {
        double yPos = editPane.getScrollTop();
        double height = editPane.getHeight();
        double editorHeight = ((Text) editPane.lookup(".text")).getBoundsInLocal().getHeight();
        int previewHeight = (Integer) previewPane.getEngine().executeScript("document.body.scrollHeight");
        double scrollTo = (previewHeight-height)*(yPos/(editorHeight-height));
        previewPane.getEngine().executeScript("window.scrollTo(0," + scrollTo + ")");
    }

    private void changePreviewStatus() {
        if (previewOnlyButton.isSelected()) {
            editPane.setEditable(false);
            editPane.setVisible(false);
            splitPane.setDividerPositions(0);
            splitPane.lookupAll(".split-pane-divider").forEach(div ->  div.setMouseTransparent(true));
        }
        else if (editorOnlyButton.isSelected()) {
            editPane.setEditable(true);
            editPane.setVisible(true);
            splitPane.setDividerPositions(1);
            splitPane.lookupAll(".split-pane-divider").forEach(div ->  div.setMouseTransparent(true));
        }
        else {
            editPane.setEditable(true);
            editPane.setVisible(true);
            splitPane.setDividerPositions(0.4);
            splitPane.lookupAll(".split-pane-divider").forEach(div ->  div.setMouseTransparent(false));
        }
    }

    private void hotKeyHandler(KeyEvent e) throws Throwable {
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
           previewOnlyButton.setSelected(previewOnlyButton.isSelected());
            previewOnlyButton.setSelected(false);
            changePreviewStatus();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.P) {
            previewOnlyButton.setSelected(!previewOnlyButton.isSelected());
           previewOnlyButton.setSelected(false);
            changePreviewStatus();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.EQUALS) {
            if (e.isShiftDown()) {
                Global.settings.setFontWeight((Global.settings.getFontWeight()+1)%Global.fontsName.length);
            }
            else {
                Global.settings.setFontSize((Global.settings.getFontSize()+1)%Global.fontSizeList.length);
            }
            setEditorFont();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.MINUS) {
            if (e.isShiftDown()) {
                Global.settings.setFontWeight((Global.settings.getFontWeight()+Global.fontsName.length-1)%Global.fontsName.length);
            }
            else {
                Global.settings.setFontSize((Global.settings.getFontSize()+Global.fontSizeList.length-1)%Global.fontSizeList.length);
            }
            setEditorFont();
        }
    }

    public void openNewFile() throws Throwable {
        EditPageController newEditPage = new EditPageController();
        newEditPage.display("");
    }

    public void openExistedFile() throws Throwable {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Markdown Docs", "*.md");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(window);
        if (file !=  null) {
            md = new MarkdownFile(file.getAbsolutePath());
            Global.settings.addNewRecentFile(file.getAbsolutePath());
            EditPageController newEditPage = new EditPageController();
            newEditPage.display(file.getAbsolutePath());
        }
    }

    public void setSaveButton() throws Throwable {
        save();
    }

    public void setCloseButton() throws Throwable {
        closeProgram();
    }

    public void saveAsMarkdown() throws Throwable {
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
            Global.settings.addNewRecentFile(file.getAbsolutePath());
            EditPageController newEditPage = new EditPageController();
            newEditPage.display(file.getAbsolutePath());
        }
    }

    public void saveAsTxt() throws Throwable {
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

    public void saveAsHtml() throws Throwable {
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

    public void returnIndex() throws Throwable {
        if (closeProgram()) {
            IntroPageController index = new IntroPageController();
            index.display();
        }
    }

    public void openAboutPage() {
        AboutPage.display();
    }

    public void switchAutoScroll() {
        autoScroll = autoScrollButton.isSelected();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ini themes select list
        themesToggleGroupItems = new RadioMenuItem[Global.settings.themesList.size()];
        LinkedList<String> themeList = Global.settings.themesList;
        for (int i=0; i<themeList.size(); i++) {
            String name = themeList.get(i);
            themesToggleGroupItems[i] = new RadioMenuItem(name.substring(0, name.length()-4));
            themesToggleGroupItems[i].setToggleGroup(themesToggleGroup);
            int themeIndex = i;
            themesToggleGroupItems[i].setOnAction(e->{
                try {
                    chooseTheme(themeIndex);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
            if (name.equals(Global.settings.currentTheme)) {
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
                    Global.settings.setFontSize(fontSizeIndex);
                    setEditorFont();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            if (Global.fontSizeList[i] == Global.settings.getFontSize()) {
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
                    Global.settings.setFontWeight(fontWeightIndex);
                    setEditorFont();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            if (i == Global.settings.getFontWeight()) {
                fontWeightToggleGroupItems[i].setSelected(true);
            }
            fontWeightMenu.getItems().add(fontWeightToggleGroupItems[i]);
        }

        // ini switches
        previewOnlyButton.setOnAction(event -> {
            editorOnlyButton.setSelected(false);
            changePreviewStatus();
        });
        editorOnlyButton.setOnAction(event -> {
            previewOnlyButton.setSelected(false);
            changePreviewStatus();
        });
        autoSaveButton.setSelected(Global.settings.getAutoSaveStatus());
        autoSaveButton.setOnAction(event -> {
            try {
                changeAutoSaveStatus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        autoScrollButton.setOnAction(event -> {
            switchAutoScroll();
        });
        autoScrollButton.setSelected(true);

        // ini edit pane
        try {
            Converter converter = new Converter(md, web, Global.settings);
            previewPane.getEngine().loadContent(converter.getHTML());
        } catch (IOException e) {
            e.printStackTrace();
        }
        editPane.setOnKeyReleased(e -> {
            try {
                isScrollSyncing = true;
                refresh();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        });
        editPane.scrollTopProperty().addListener((obj, start, end) -> {isScrollSyncing = true;});
        previewPane.setOnScroll(e->{isScrollSyncing=false;});
        editPane.setText(md.str);
        setEditorFont();

        // ini hot key listener
        mainPane.setOnKeyPressed(e -> {
            try {
                hotKeyHandler(e);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        // ini bottom bar
        updateCharNum();

        // sync scroll bars
        Timer t = new Timer();
        t.schedule(new task(),0,1);
    }

    class task extends TimerTask {
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
