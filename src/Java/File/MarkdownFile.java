package File;

import java.io.*;

public class MarkdownFile extends FileInfo{
    private String[] lines;
    private int lineIndex = 0;

    public MarkdownFile(String address) throws IOException {
        super(address,'a');
        reload();
    }

    // refresh lines
    public void reload() {
        lines = str.split("\n");
        if (lines == null) {
            lines = new String[]{""};
        }
        lineIndex = 0;
    }

    // return is able to read lines
    public boolean isReady() {
        return lineIndex < lines.length;
    }

    // read str line by line
    public String readLine() {
        if (lineIndex < lines.length) {
            lineIndex++;
        }
        else {
            return "\n";
        }
        return lines[lineIndex - 1];
    }

    // mover back one line
    public void moveBack(int lineCount) {
        if (lineIndex - lineCount > 0) {
            lineIndex -= lineCount;
        }
        else {
            lineIndex = 0;
        }
    }

    // move forward one line
    public void moveForward(int lineCount) {
        if (lineIndex + lineCount < lines.length) {
            lineIndex += lineCount;
        }
        else {
            lineIndex = lines.length;
        }
    }

    @Override
    public void save() throws IOException {
        super.save();
        reload();
    }
}
