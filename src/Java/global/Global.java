package global;

import fileio.ProgramInfo;

public class Global {
    public static String programAbsolutePath;
    public static ProgramInfo settings;

    // default values
    public static final int MAX_RECENT_FILES_STORED = 4;
    public static final int NOTIFICATION_TIME_OUT = 5;
    public static final int[] fontSizeList = new int[]{12,14,16,18,20,24,28};
    public static final String programName = "MDEditTool";
    public static final String jarName = "MarkdownEditTool";
    public static final String resourcePath = "resources\\";
    public static final String themesFolderPath = "themes\\";
    public static final String tmpFolderPath = "\\tmp\\";
    public static final String readmeName = "README.md";
    public static final String userConfigName = "UserSettings.config";
    public static final String tmpMDName = "Untitled.md";
    public static final String tmpHTMLName = "pre_render_html.html";
    public static final String[] defaultThemesNames = {"light(default).css","dark.css","page.css"};
    public static final String aboutUrl = "https://raw.githubusercontent.com/APassbyDreg/MarkdownEditTool/master/doc/User_Guide.html";
    public static final String markdownGuide_zhcn = "https://www.rdtoc.com/tutorial/markdown-tutorial.html";
    public static final String markdownGuide_enus = "https://www.markdownguide.org/";
    public static final String customizeThemeGuide = "https://github.com/APassbyDreg/MarkdownEditTool/blob/master/doc/Customize_Themes_Instructions.md  ";

    // resources
    public static final String logoRelativePath = "design/Logo.png";
    public static final String introPageDesignPath = "design/IntroPageDesign.css";
    public static final String editPageDesignPath = "design/EditPageDesign.css";
    public static final String alertBoxDesignPath = "design/AlertBoxDesign.css";
    public static final String[] fontsPath = new String[]{"design/sarasa-monoT-sc-light.ttf", "design/sarasa-monoT-sc-regular.ttf", "design/sarasa-monoT-sc-semibold.ttf"};
    public static final String[] fontsName = new String[]{"light","regular","bold"};
    public static final String introFXMLPath = "/fxml/GUI_intro.fxml";
    public static final String editFXMLPath = "/fxml/GUI_edit.fxml";
    public static final String alertFXMLPath = "/fxml/AlertBox.fxml";
    public static final String readmePath = "README.md";
    public static final String settingsPath = "UserSettings.config";
    public static final String[] defaultThemesPaths = {"themes/light(default).css","themes/dark.css","themes/page.css"};
}
