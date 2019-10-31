package File;

import java.net.URLDecoder;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import Global.*;
import sun.awt.AWTCharset;

public class FileInfo extends GlobalVariables {
    public String str = "";
    public String src;
    public String name;
    public String type;
    public String lastSaveTime;
    public String lastSaveStr;
    public boolean isTemp = false;
    private File file;

    public FileInfo(String address, char addressType) throws IOException {
        super();
        address = address.replace('/','\\');
        if (addressType == 'r') {
            address = programAbsolutePath + address;
        }
        file = new File(address);
        src = file.getAbsolutePath();

        load();

        Pattern pattern = Pattern.compile(".+\\\\(.+)\\.(.+)");
        Matcher matcher = pattern.matcher(src);
        if (matcher.find()) {
            name = matcher.group(1);
            type = matcher.group(2);
        }
        lastSaveStr = str;
    }

    public void load() throws IOException {
        if (file.exists()) {
            InputStream ipStream = new FileInputStream(file);
            InputStreamReader ipsReader = new InputStreamReader(ipStream,StandardCharsets.UTF_8);
            while (ipsReader.ready()) {
                str += (char) ipsReader.read();
            }
            ipsReader.close();
            ipStream.close();
        }
        else {
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            if (!file.createNewFile()) {
                System.out.println("access denied: failed to create file");
            }
        }
    }

    public boolean isChanged() {
        return !str.equals(lastSaveStr);
    }

    public void save() throws IOException {
        OutputStream opStream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(opStream, StandardCharsets.UTF_8);
        writer.flush();
        writer.append(str);
        writer.close();
        markTime();
        lastSaveStr = str;
    }

    public void markTime() {
        Date d = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        lastSaveTime = ft.format(d);
    }

    public void makeTemp() throws IOException {
        isTemp = true;
    }

    public void unmarkTemp() throws IOException {
        isTemp = false;
    }

    public void delete() {
        if (!file.delete()) {
            System.out.println("error: delete failed");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        save();
        if (isTemp) {
            delete();
        }
    }
}
