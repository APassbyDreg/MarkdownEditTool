package Convert;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import File.*;

public class Converter extends BlockBuilder {
    private MarkdownFile md;
    private WebFile html;
    private ProgramInfo editor;

    public Converter(MarkdownFile src, WebFile dest, ProgramInfo info) {
        super();
        md = src;
        html = dest;
        editor = info;
    }

    public void convert() throws IOException {
        html.str = "<!doctype html><html>" + genHead().replaceAll("\\s+"," ") + genBody() + "</html>";
        html.save();
    }

    public String getHTML() throws IOException {
        convert();
        return html.str;
    }

    // main convert function (editor info to html head)
    private String genHead() throws IOException {
        FileInfo style = new FileInfo(editor.themesFolderRelativePath + editor.currentTheme, 'r');
        String headContent = "<head>\n<meta charset='UTF-8'>\n";
        headContent += "<title>" + md.name + "</title>\n";
        headContent += "<style>\n" + style.str + "</style>";
        return headContent + "</head>";
    }

    // main convert function (md to html body)
    private String genBody() {
        StringBuilder converted = new StringBuilder();
        int type = -1, previousType = -1;
        md.reload();
        while (md.isReady()) { // line by line convert
            StringBuilder line = new StringBuilder(md.readLine());
            StringBuilder content = new StringBuilder(spanBuilder(replaceSpecialCharacters(line.toString())));
            previousType = type;
            type = defineLineType(content.toString());
            switch (type) {
                case -1:
                    if (!line.toString().equals("")) {
                        converted.append(content);
                    }
                    else if (previousType == -1){
                        converted.append("<br /><br />");
                    }
                    break;
                case 0:
                    converted.append(headerBuilder(content.toString()));
                    break;
                case 1:
                    converted.append(quoteBuilder(content.toString()));
                    break;
                case 2:
                    converted.append("<hr />");
                    break;
                case 3:
                    line.append("\n");
                    while (md.isReady()) {
                        String next = replaceSpecialCharacters(md.readLine());
                        Matcher m1 = Pattern.compile("^```").matcher(next);
                        line.append(next).append("\n");
                        if (m1.find()) {
                            break;
                        }
                    }
                    converted.append(codeLinesBuilder(line.toString()));
                    break;
                case 4:
                case 5:
                    content.append("\n");
                    while (md.isReady()) {
                        String next = new String(spanBuilder(replaceSpecialCharacters(md.readLine())));
                        int nextType = defineLineType(next);
                        if (nextType == 5 || nextType == 4) {
                            content.append(next).append("\n");
                        }
                        else if (!next.equals("")) {
                            md.moveBack(1);
                            break;
                        }
                    }
                    converted.append(listBuilder(content.toString()));
                    break;
            }
        }
        md.reload();
        return "<body>" + converted.toString() + "</body>";
    }

    // replace html specific strings
    public String replaceSpecialCharacters(String text) {
        text = text.replaceAll("&", "&amp;");
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
//        text.replace(" ", "&nbsp;");
        return text;
    }

    // save to file
    public void toFile() throws IOException {
        md.save();
        html.save();
    }
}
