package File;

import Global.Global;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import	java.util.regex.Matcher;
import	java.util.regex.Pattern;
import java.io.IOException;
import java.util.*;

public class ProgramInfo extends FileInfo {
    // in UserSettings.conf
    public String currentTheme = "light(default).css";
    public LinkedList<String> recentFilePaths = new LinkedList<String>(); // new -> old
    public LinkedList<String> themesList = new LinkedList<String>();

    // judge if this program is first open by searching the usersetting file
    public boolean isFirstOpen = false;

    public ProgramInfo() throws IOException {
        super(Global.resourcePath + Global.userConfigName,'r');

        // on first run
        if (str.length() == 0) {
            isFirstOpen = true;
            initialize();
        }

        // read config
        loadConfig();
    }

    // load config from usersetting file
    private void loadConfig() {
        recentFilePaths = new LinkedList<String>();
        themesList = new LinkedList<String>();
        Pattern themeSettingRegex = Pattern.compile("# Theme: (.*)");
        Pattern recentRecordRegex = Pattern.compile("# Recent Files: \\[(.*)\\]");
        Matcher m;
        m = themeSettingRegex.matcher(str);
        if (m.find()) {
            currentTheme = m.group(1);
        }
        m = recentRecordRegex.matcher(str);
        if (m.find()) {
            String recentFiles = m.group(1);
            String[] recentList = recentFiles.split(",");
            for (String s : recentList) {
                if (s.endsWith(".md")) {
                    recentFilePaths.offer(s.replace('/','\\'));
                }
            }
        }

        // get available themes
        File themeFolder = new File(Global.programAbsolutePath + Global.resourcePath + Global.themesFolderPath);
        String[] fileList = themeFolder.list();
        if (fileList != null) {
            for (String s : fileList) {
                if (s.endsWith(".css")) {
                    themesList.add(s);
                }
            }
        }
        else {
            throw new RuntimeException("NO THEME FILE FOUND");
        }
    }

    // set using theme
    public void setTheme(int index) throws IOException {
        if (index < themesList.size()) {
            currentTheme = themesList.get(index);
            saveSettings();
        }
    }

    // add new file to recent file list
    public void addNewRecentFile(String path) throws IOException {
        path = path.replace('/','\\');
        if (path.startsWith("\\")) {
            path = path.substring(1,path.length());
        }
        if (path.endsWith(".md")) {
            recentFilePaths.remove(path);
            recentFilePaths.push(path);
            saveSettings();
        }
    }

    // save whenever settings are changed
    private void saveSettings() throws IOException {
        LinkedList<String> tmp = recentFilePaths;
        StringBuilder settings = new StringBuilder("## This file stores user's configs ##\n\n");
        settings.append("# Theme: ").append(currentTheme).append("\n\n");
        settings.append("# Recent Files: [");
        int i=0;
        while (tmp.size()!=0 && i<Global.MAX_RECENT_FILES_STORED) {
            settings.append(tmp.pop()).append(",");
            i++;
        }
        settings.append("]");
        str = settings.toString();
        save();
        loadConfig();
    }

    // initialize on the program's first run
    private void initialize() throws IOException {
        InputStream rms = Thread.currentThread().getContextClassLoader().getResourceAsStream(Global.readmePath);
        InputStream uss = Thread.currentThread().getContextClassLoader().getResourceAsStream(Global.settingsPath);
        InputStream lts = Thread.currentThread().getContextClassLoader().getResourceAsStream(Global.defaultThemesPaths[0]);
        InputStream dts = Thread.currentThread().getContextClassLoader().getResourceAsStream(Global.defaultThemesPaths[1]);
        InputStream pts = Thread.currentThread().getContextClassLoader().getResourceAsStream(Global.defaultThemesPaths[2]);
        createDefaultFiles(rms, Global.resourcePath + Global.readmeName);
        createDefaultFiles(uss, Global.resourcePath + Global.userConfigName);
        createDefaultFiles(lts, Global.resourcePath + Global.themesFolderPath + Global.defaultThemesNames[0]);
        createDefaultFiles(dts, Global.resourcePath + Global.themesFolderPath + Global.defaultThemesNames[1]);
        createDefaultFiles(pts, Global.resourcePath + Global.themesFolderPath + Global.defaultThemesNames[2]);
        super.load();
        addNewRecentFile(Global.programAbsolutePath +  Global.resourcePath + Global.readmeName);
        loadConfig();
    }

    // create several default files
    private void createDefaultFiles(InputStream is, String relativePath) throws IOException {
        FileInfo newFile = new FileInfo(relativePath,'r');
        InputStreamReader isr = new InputStreamReader(is);
        while (isr.ready()) {
            newFile.str += (char) isr.read();
        }
        is.close();
        isr.close();
        newFile.save();
    }

    @Override
    protected void finalize() throws Throwable {
        saveSettings();
        super.finalize();
    }
}
