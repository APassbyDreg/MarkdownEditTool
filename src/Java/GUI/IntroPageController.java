package GUI;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import Global.Global;
import File.*;
import javafx.util.Duration;

public class IntroPageController implements Initializable{
    private static ProgramInfo editor;

    @FXML public Button buttonOpen,buttonNew;
    @FXML public ListView<VBox> recentList;
    @FXML public BorderPane mainWindow;

    public static VBox[] recentFileContainer = new VBox[Global.MAX_RECENT_FILES_STORED];
    public static Button[] recentFileChooser = new Button[Global.MAX_RECENT_FILES_STORED];
    public static Label[] recentFileContent = new Label[Global.MAX_RECENT_FILES_STORED];
    public static Label[] recentFileAddress = new Label[Global.MAX_RECENT_FILES_STORED];
    public static String[] recentFilePath = new String[Global.MAX_RECENT_FILES_STORED];

    public static void display(Stage primaryStage) throws IOException {
        editor = new ProgramInfo();
        Parent root = FXMLLoader.load(IntroPageController.class.getResource("/fxml/GUI_intro.fxml"));
        Image logoPNG = new Image(Global.logoRelativePath);
        Stage window = primaryStage;
        Scene scene = new Scene(root, 1050, 600);
        scene.getStylesheets().add(Global.introPageDesignPath);
        window.setTitle(Global.programName);
        window.getIcons().add(logoPNG);
        window.setScene(scene);
        window.setResizable(false);
        window.show();

        // for a strange bug: config reset when image load
        if (editor.isFirstOpen) {
            editor.save();
        }
    }

    @FXML
    public void openExistFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Markdown Docs", "*.md");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog((Stage) buttonOpen.getScene().getWindow());
        if (file != null) {
            EditPageController.md = new MarkdownFile(file.getAbsolutePath());
            Stage thisWindow = (Stage) buttonOpen.getScene().getWindow();
            thisWindow.close();
            editor.addNewRecentFile(file.getAbsolutePath());
            EditPageController.displayEditWindow(file.getAbsolutePath(),editor);
        }
    }

    @FXML
    public void openNewFile() throws IOException {
        Stage thisWindow = (Stage) buttonNew.getScene().getWindow();
        thisWindow.close();
        EditPageController.displayEditWindow("",editor);
    }

    private void openRecent(int index) throws IOException {
        Stage thisWindow = (Stage) buttonOpen.getScene().getWindow();
        thisWindow.close();
        EditPageController.displayEditWindow(recentFilePath[index],editor);
        editor.addNewRecentFile(recentFilePath[index]);
    }

    public void introButtonAnimationIn(MouseEvent event) {
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

    public void introButtonAnimationOut(MouseEvent event) {
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

    public void chooseButtonAnimationIn(MouseEvent event) {
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

    public void chooseButtonAnimationOut(MouseEvent event) {
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

    public void chooseContentAnimationIn(MouseEvent event) {
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

    public void chooseContentAnimationOut(MouseEvent event) {
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

    private void hotKeyHandler(KeyEvent e) throws IOException {
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
        FileInfo[] recentFiles = new FileInfo[Global.MAX_RECENT_FILES_STORED];

        buttonOpen.getStyleClass().add("mainButtons");
        buttonNew.getStyleClass().add("mainButtons");

        ObservableList<VBox> items = recentList.getItems();
        LinkedList<String> paths = editor.recentFilePaths;
        for (int i=0; i < paths.size(); i++) {
            recentFilePath[i] = paths.get(i);
            try {
                recentFiles[i] = new FileInfo(recentFilePath[i],'a');
            } catch (IOException e) {
                e.printStackTrace();
            }
            recentFileChooser[i] = new Button(recentFiles[i].name);
            recentFileContent[i] = new Label(" " + recentFiles[i].str.split("\n")[0].trim());
            recentFileAddress[i] = new Label(" " + ((recentFiles[i].src.length()<60) ? recentFiles[i].src : recentFiles[i].src.substring(0,60)+"..."));
            recentFileChooser[i].getStyleClass().add("recentViewChooser");
            recentFileContent[i].getStyleClass().add("recentViewContent");
            recentFileAddress[i].getStyleClass().add("recentViewAddress");
            recentFileChooser[i].setOnMouseEntered(event -> chooseButtonAnimationIn(event));
            recentFileChooser[i].setOnMouseExited(event -> chooseButtonAnimationOut(event));
            recentFileContent[i].setOnMouseEntered(event -> chooseContentAnimationIn(event));
            recentFileContent[i].setOnMouseExited(event -> chooseContentAnimationOut(event));
            recentFileAddress[i].setOnMouseEntered(event -> chooseContentAnimationIn(event));
            recentFileAddress[i].setOnMouseExited(event -> chooseContentAnimationOut(event));
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

            mainWindow.setOnKeyPressed(event -> {
                try {
                    hotKeyHandler(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
