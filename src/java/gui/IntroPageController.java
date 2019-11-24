package gui;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import global.Global;
import fileio.*;
import javafx.util.Duration;

public class IntroPageController implements Initializable{
    private static int introWindowsNum = 0;

    @FXML
    private Button buttonOpen,buttonNew;
    @FXML
    private ListView<VBox> recentList;
    @FXML
    private BorderPane mainWindow;

    private Stage window;
    private VBox[] recentFileContainer = new VBox[Global.MAX_RECENT_FILES_STORED];
    private Button[] recentFileChooser = new Button[Global.MAX_RECENT_FILES_STORED];
    private Label[] recentFileContent = new Label[Global.MAX_RECENT_FILES_STORED];
    private Label[] recentFileAddress = new Label[Global.MAX_RECENT_FILES_STORED];
    private FileInfo[] recentFiles = new FileInfo[Global.MAX_RECENT_FILES_STORED];

    public void display(Stage stage) throws IOException {
        introWindowsNum++;
        window = stage;
        if (window != null) {
            FXMLLoader loader = new FXMLLoader(IntroPageController.class.getResource(Global.introFXMLPath));
            loader.setController(this);
            Parent root = loader.load();
            Image logoPNG = new Image(Global.logoRelativePath);
            Scene scene = new Scene(root, 1050, 600);
            scene.getStylesheets().add(Global.introPageDesignPath);
            window.setOnCloseRequest(e->closeWindow());
            window.setTitle(Global.programName);
            window.getIcons().add(logoPNG);
            window.setScene(scene);
            window.setResizable(false);
            window.show();
        }
    }

    private void closeWindow() {
        window.close();
        introWindowsNum--;
        if (introWindowsNum==0 && EditPageController.editorWindowsNum==0) {
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    private void openExistFile() throws Throwable {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Markdown Docs", "*.md");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog((Stage) buttonOpen.getScene().getWindow());
        if (file != null) {
            Global.settings.addNewRecentFile(file.getAbsolutePath());
            EditPageController newEditPage = new EditPageController();
            newEditPage.display(file.getAbsolutePath());
            closeWindow();
        }
    }

    @FXML
    private void openNewFile() throws Throwable {
        EditPageController newEditPage = new EditPageController();
        newEditPage.display("");
        closeWindow();
    }

    @FXML
    private void openRecent(int index) throws Throwable {
        Global.settings.addNewRecentFile(recentFiles[index].src);
        EditPageController newEditPage = new EditPageController();
        newEditPage.display(recentFiles[index].src);
        closeWindow();
    }

    @FXML
    private void introButtonAnimationIn(MouseEvent event) {
        Button button = (Button) event.getSource();
        final Animation animation = new Transition() {
            double currentRadius = 10;

            {
                setCycleDuration(Duration.millis(250));
                setInterpolator(Interpolator.EASE_OUT);
                Pattern p = Pattern.compile("-radius: (.+?);");
                Matcher m = p.matcher(button.getStyle());
                if (m.find()) {
                    currentRadius = Double.parseDouble(m.group(1));
                }
            }

            @Override
            protected void interpolate(double frac) {
                double radius = currentRadius + (30-currentRadius)*frac;
                button.setStyle("-fx-border-radius: " + radius + ";-fx-background-radius: " + radius + ";");
            }
        };
        animation.play();
    }

    @FXML
    private void introButtonAnimationOut(MouseEvent event) {
        Button button = (Button) event.getSource();
        final Animation animation = new Transition() {
            double currentRadius = 30;

            {
                setCycleDuration(Duration.millis(250));
                setInterpolator(Interpolator.EASE_OUT);
                Pattern p = Pattern.compile("-radius: (.+?);");
                Matcher m = p.matcher(button.getStyle());
                if (m.find()) {
                    currentRadius = Double.parseDouble(m.group(1));
                }
            }

            @Override
            protected void interpolate(double frac) {
                double radius = currentRadius + (10-currentRadius)*frac;
                button.setStyle("-fx-border-radius: " + radius + ";-fx-background-radius: " + radius + ";");
            }
        };
        animation.play();
    }

    private void chooseButtonAnimationIn(MouseEvent event) {
        Button button = (Button) event.getSource();
        final Animation animation = new Transition() {
            double opacity = 1;

            {
                setCycleDuration(Duration.millis(250));
                setInterpolator(Interpolator.EASE_OUT);

                opacity = ((Color) button.getBackground().getFills().get(0).getFill()).getOpacity();
            }

            @Override
            protected void interpolate(double frac) {
                double newVal = opacity * (1 - frac);
                if (newVal < 1e-3) {
                    newVal = 0;
                }
                button.setStyle("-fx-background-color: rgba(235,243,253," + newVal + ")");
            }
        };
        animation.play();
    }

    private void chooseButtonAnimationOut(MouseEvent event) {
        Button button = (Button) event.getSource();
        final Animation animation = new Transition() {
            double opacity = 0;

            {
                setCycleDuration(Duration.millis(250));
                setInterpolator(Interpolator.EASE_OUT);

                opacity = ((Color) button.getBackground().getFills().get(0).getFill()).getOpacity();
            }

            @Override
            protected void interpolate(double frac) {
                double newVal = (1 - opacity)*frac;
                if (newVal < 1e-3) {
                    newVal = 0;
                }
                button.setStyle("-fx-background-color: rgba(235,243,253," + newVal + ")");
            }
        };
        animation.play();
    }

    private void chooseContentAnimationIn(MouseEvent event) {
        Label label = (Label) event.getSource();
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(250));
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double frac) {
                double rate = (1 - frac*0.8);
                double r=70*rate,g=74*rate,b=92*rate;
                if (r<1e-3) {
                    r=0;
                }
                if (g<1e-3) {
                    g=0;
                }
                if (b<1e-3) {
                    b=0;
                }
                label.setStyle("-fx-background-color: rgba("+r+","+g+","+b+",1)");
            }
        };
        animation.play();
    }

    private void chooseContentAnimationOut(MouseEvent event) {
        Label label = (Label) event.getSource();
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(250));
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double frac) {
                double rate = 0.2 + frac*0.8;
                double r=70*rate,g=74*rate,b=92*rate;
                if (r<1e-3) {
                    r=0;
                }
                if (g<1e-3) {
                    g=0;
                }
                if (b<1e-3) {
                    b=0;
                }
                label.setStyle("-fx-background-color: rgba("+r+","+g+","+b+",1)");
            }
        };
        animation.play();
    }

    private void copyFilePath(int index) {
        String path = recentFiles[index].src;
        path = path.substring(0,path.length() - (recentFiles[index].name + ".md").length());
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(path);
        clip.setContents(tText, null);
        Notification.display("MarkdownEditTool Message", "file path copied to keyboard");
    }

    private void hotKeyHandler(KeyEvent e) throws Throwable {
        if (e.isControlDown() && e.getCode() == KeyCode.N) {
            openNewFile();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.O) {
            openExistFile();
        }
        else if (e.isControlDown() && e.getCode() == KeyCode.H) {
            AboutPage.display();
        }
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
        buttonOpen.getStyleClass().add("mainButtons");
        buttonNew.getStyleClass().add("mainButtons");

        ObservableList<VBox> items = recentList.getItems();
        LinkedList<String> paths = Global.settings.recentFilePaths;
        for (int i=0; i < paths.size(); i++) {
            int index = i;
            try {
                recentFiles[i] = new FileInfo(paths.get(i),'a');
            } catch (IOException e) {
                e.printStackTrace();
            }
            recentFileChooser[i] = new Button(recentFiles[i].name);
            recentFileContent[i] = new Label(" " + recentFiles[i].str.split("\n")[0].trim());
            recentFileAddress[i] = new Label(" " + ((recentFiles[i].src.length()<60) ? recentFiles[i].src : recentFiles[i].src.substring(0,60)+"..."));
            recentFileChooser[i].getStyleClass().add("recentViewChooser");
            recentFileContent[i].getStyleClass().add("recentViewContent");
            recentFileAddress[i].getStyleClass().add("recentViewAddress");
            recentFileChooser[i].setOnAction(e -> {
                try {
                    openRecent(index);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
            recentFileChooser[i].setOnMouseEntered(event -> chooseButtonAnimationIn(event));
            recentFileChooser[i].setOnMouseExited(event -> chooseButtonAnimationOut(event));
            recentFileAddress[i].setOnMouseEntered(event -> chooseContentAnimationIn(event));
            recentFileAddress[i].setOnMouseExited(event -> chooseContentAnimationOut(event));
            recentFileAddress[i].setOnMouseClicked(e -> copyFilePath(index));
            recentFileContainer[i] = new VBox();
            recentFileContainer[i].getChildren().addAll(recentFileChooser[i],recentFileContent[i],recentFileAddress[i]);
            items.add(recentFileContainer[i]);
        }
        mainWindow.setOnKeyPressed(event -> {
            try {
                hotKeyHandler(event);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }
}
