package File;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import	java.util.regex.Matcher;
import	java.util.regex.Pattern;

import java.io.IOException;
import java.util.*;

public class ProgramInfo extends FileInfo {
    // in UserSettings.conf
    public String currentTheme;
    public LinkedList<String> recentFilePaths = new LinkedList<String>(); // new -> old
    private ArrayList<String> themesList = new ArrayList<String>();

    // default values
    public final String jarName = "test";
    public final String themesFolderRelativePath = "\\resources\\themes\\";

    public ProgramInfo() throws IOException {
        super("\\resources\\UserSettings.config",'r');

        // on first run
        if (str.length() == 0) {
            initialize();
        }

        // read config
        loadConfig();

        // get available themes
        File themeFolder = new File(programAbsolutePath + themesFolderRelativePath);
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

    private void loadConfig() {
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
    }

    public String getProgramAbsolutePath () {
        System.out.println(System.getProperty("java.class.path"));
        Pattern pathPattern = Pattern.compile("(.+)\\\\" + jarName + "\\.jar");
        Matcher pathMatcher = pathPattern.matcher(System.getProperty("java.class.path"));
        assert pathMatcher.find() : "exec path error";
        if (pathMatcher.find()) {
            return pathMatcher.group(1);
        }
        else {
            return System.getProperty("user.dir");
        }
    }

    public void setTheme(int index) throws IOException {
        if (index < themesList.size()) {
            currentTheme = themesList.get(index);
            saveSettings();
        }
    }

    public void addNewRecentFile(String path) throws IOException {
        path = path.replace('/','\\');
        if (path.endsWith(".md")) {
            recentFilePaths.remove(path);
            recentFilePaths.push(path);
            saveSettings();
        }
    }

    private void saveSettings() throws IOException {
        LinkedList<String> tmp = recentFilePaths;
        StringBuilder settings = new StringBuilder("## This file stores user's configs ##\n\n");
        settings.append("# Theme: ").append(currentTheme).append("\n\n");
        settings.append("# Recent Files: [");
        int i=0;
        while (tmp.size()!=0 && i<MAX_RECENT_FILES_STORED) {
            settings.append(tmp.pop()).append(",");
            i++;
        }
        settings.append("]");
        str = settings.toString();
        save();
    }

    private void initialize() throws IOException {
        InputStream rms = Thread.currentThread().getContextClassLoader().getResourceAsStream("README.md");
        InputStream uss = Thread.currentThread().getContextClassLoader().getResourceAsStream("UserSettings.config");
        InputStream lts = Thread.currentThread().getContextClassLoader().getResourceAsStream("light(default).css");
        InputStream dts = Thread.currentThread().getContextClassLoader().getResourceAsStream("dark.css");
        InputStream pts = Thread.currentThread().getContextClassLoader().getResourceAsStream("page.css");
        createDefaultFiles(rms, "resources\\README.md");
        createDefaultFiles(uss, "resources\\UserSettings.config");
        createDefaultFiles(lts, "resources\\themes\\light(default).css");
        createDefaultFiles(dts, "resources\\themes\\dark.css");
        createDefaultFiles(pts, "resources\\themes\\page.css");
        super.load();
        loadConfig();
        addNewRecentFile(programAbsolutePath +  "resources\\README.md");
    }

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
