import java.io.*;
import	java.io.InputStreamReader;

public class GetTotalLineNumber {
    public static void main(String[] args) throws IOException {
        final String codeFolderName = "\\src";
        String codeDir = System.getProperty("user.dir") + codeFolderName;
        System.out.println((sum(codeDir)<1500) ? "GO CODING!!!(" + sum(codeDir) + "/1500)" : sum(codeDir) + ", nb");
    }

    private static int sum(String name) throws IOException {
        int num=0;
        File file = new File(name);
        if (file.isDirectory()) {
            String [] list = file.list();
            assert list != null;
            for (String s : list) {
                num += sum(name + "\\" + s);
            }
        }
        else if (!name.endsWith(".ttf")) {
            InputStream in = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(in);
            while (isr.ready()) {
                char c = (char) isr.read();
                if (c == '\n') {
                    num++;
                }
            }
        }
        return num;
    }
}
