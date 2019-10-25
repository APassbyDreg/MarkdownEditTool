package Global;
import javafx.stage.Stage;

import	java.util.regex.Matcher;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

public class GlobalVariables {
    public static String jarName = "test";
    public static String programAbsolutePath;
    public static final int MAX_RECENT_FILES_STORED = 5;
    public static Stage introWindow;
    public static Stage editWindow;

    public GlobalVariables() throws UnsupportedEncodingException {
        programAbsolutePath = getRuntimeFolder();
    }

    private String getRuntimeFolder() throws UnsupportedEncodingException {
        String src = System.getProperty("java.class.path");
        if (src.contains(jarName+".jar")) {
            return src.substring(0,src.length() - jarName.length() - ".jar".length());
        }
        else {
            return URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("").getPath(),"UTF-8");
        }
    }
}
