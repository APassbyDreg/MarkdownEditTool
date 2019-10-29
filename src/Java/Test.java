import Convert.Converter;
import File.*;
import Global.GlobalVariables;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test {

    public static void main(String[] args) throws IOException {
        GlobalVariables gbv = new GlobalVariables();
        MarkdownFile md = new MarkdownFile(GlobalVariables.programAbsolutePath + "\\README.md");
        md.str = "11111";
        md.save();
        WebFile web = new WebFile(GlobalVariables.programAbsolutePath + "\\tmp\\tmp.html");
        web.str = "1111";
        web.save();
    }
}
