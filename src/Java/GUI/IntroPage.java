package GUI;
import	java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import	java.util.Properties;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import Global.GlobalVariables;
import File.*;

public class IntroPage implements Initializable{
    private static ProgramInfo editor;

    @FXML public Button buttonOpen,buttonNew;
    @FXML public ListView<VBox> recentList;

    public static VBox[] recentFileContainer = new VBox[GlobalVariables.MAX_RECENT_FILES_STORED];
    public static Button[] recentFileChooser = new Button[GlobalVariables.MAX_RECENT_FILES_STORED];
    public static Label[] recentFileContent = new Label[GlobalVariables.MAX_RECENT_FILES_STORED];
    public static Label[] recentFileAddress = new Label[GlobalVariables.MAX_RECENT_FILES_STORED];

    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Markdown Docs", "*.md");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(GlobalVariables.introWindow);
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            editor = new ProgramInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileInfo[] recentFiles = new FileInfo[GlobalVariables.MAX_RECENT_FILES_STORED];

        ObservableList<VBox> items = recentList.getItems();
        LinkedList<String> recentFilePaths = editor.recentFilePaths;
        for (int i=0; recentFilePaths.size()>0; i++) {
            try {
                recentFiles[i] = new FileInfo(recentFilePaths.pop(),'a');
            } catch (IOException e) {
                e.printStackTrace();
            }
            recentFileChooser[i] = new Button(recentFiles[i].name);
            recentFileContent[i] = new Label(recentFiles[i].str.split("\n")[0]);
            recentFileAddress[i] = new Label((recentFiles[i].src.length()<50) ? recentFiles[i].src : recentFiles[i].src.substring(0,50)+"...");
            recentFileContainer[i] = new VBox();
            recentFileContainer[i].getChildren().addAll(recentFileChooser[i],recentFileContent[i],recentFileAddress[i]);
            items.add(recentFileContainer[i]);
        }
    }
}
