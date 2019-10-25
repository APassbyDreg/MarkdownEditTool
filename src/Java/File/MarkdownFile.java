package File;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class MarkdownFile extends FileInfo{
    private String[] lines;
    private int lineIndex = 0;

    public MarkdownFile(String address) throws IOException {
        super(address,'a');
        reload();
    }

    public void reload() {
        lines = str.split("\n");
        if (lines == null) {
            lines = new String[]{""};
        }
        lineIndex = 0;
    }

    public boolean isReady() {
        return lineIndex < lines.length;
    }

    public String readLine() {
        if (lineIndex < lines.length) {
            lineIndex++;
        }
        else {
            return "\n";
        }
        return lines[lineIndex - 1];
    }

    public void moveBack(int lineCount) {
        if (lineIndex - lineCount > 0) {
            lineIndex -= lineCount;
        }
        else {
            lineIndex = 0;
        }
    }

    public void moveForward(int lineCount) {
        if (lineIndex + lineCount < lines.length) {
            lineIndex += lineCount;
        }
        else {
            lineIndex = lines.length;
        }
    }

    @Override
    public void writeNew(String newStr) {
        super.writeNew(newStr);
        reload();
    }

    @Override
    public void save() throws IOException {
        super.save();
        reload();
    }
}
