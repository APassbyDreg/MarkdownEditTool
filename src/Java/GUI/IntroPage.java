package GUI;

import java.io.*;
import java.net.URL;
import java.util.*;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.*;
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
    public static String[] recentFilePath = new String[GlobalVariables.MAX_RECENT_FILES_STORED];

    @FXML
    public void openExistFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Markdown Docs", "*.md");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog((Stage) buttonOpen.getScene().getWindow());
        if (file != null) {
            displayEditWindow();
            EditPage.md = new MarkdownFile(file.getAbsolutePath());
            Stage thisWindow = (Stage) buttonOpen.getScene().getWindow();
            thisWindow.close();
            editor.addNewRecentFile(file.getAbsolutePath());
        }
    }

    @FXML
    public void openNewFile() throws IOException {
        displayEditWindow();
        EditPage.md = new MarkdownFile("");
        Stage thisWindow = (Stage) buttonNew.getScene().getWindow();
        thisWindow.close();
    }

    private void openRecent(int index) throws IOException {
        displayEditWindow();
        EditPage.md = new MarkdownFile(recentFilePath[index]);
        Stage thisWindow = (Stage) buttonOpen.getScene().getWindow();
        thisWindow.close();
        editor.addNewRecentFile(recentFilePath[index]);
    }

    private static void displayEditWindow() throws IOException {
        Stage window = new Stage();
        Parent root = FXMLLoader.load(EditPage.class.getResource("/fxml/GUI_edit.fxml"));
        Image logoPNG = new Image("Logo.png");
        window.setTitle("JMDTool");
        window.getIcons().add(logoPNG);
        window.setScene(new Scene(root, 1200, 720));
        window.show();
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
        LinkedList<String> paths = editor.recentFilePaths;
        for (int i=0; paths.size()>0; i++) {
            recentFilePath[i] = paths.pop();
            try {
                recentFiles[i] = new FileInfo(recentFilePath[i],'a');
            } catch (IOException e) {
                e.printStackTrace();
            }
            recentFileChooser[i] = new Button(recentFiles[i].name);
            recentFileContent[i] = new Label(recentFiles[i].str.split("\n")[0]);
            recentFileAddress[i] = new Label((recentFiles[i].src.length()<50) ? recentFiles[i].src : recentFiles[i].src.substring(0,50)+"...");
            recentFileContainer[i] = new VBox();
            recentFileContainer[i].getChildren().addAll(recentFileChooser[i],recentFileContent[i],recentFileAddress[i]);
            items.add(recentFileContainer[i]);

            int index = i;
            recentFileChooser[i].setOnAction(e -> {
                try {
                    openRecent(index);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}
