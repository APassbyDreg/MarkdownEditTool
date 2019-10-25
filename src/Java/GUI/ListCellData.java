package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.*;
import javafx.scene.control.*;

import java.io.IOException;

public class ListCellData {
    @FXML
    private VBox vBox;
    @FXML
    private Button fileChooser;
    @FXML
    private Label fileContent;

    public ListCellData()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/GUI_listCellItem.fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setInfo(String name, String content)
    {
        fileChooser.setText(name);
        fileContent.setText(content);
    }

    public VBox getBox()
    {
        return vBox;
    }
}
