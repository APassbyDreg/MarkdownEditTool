package convert;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockBuilder {

    /* naming rules:
    ** xxx Maker: intake blockinfo and output string
    ** xxx Builder: intake string and pass blockinfo to maker
     */

    public String[] lineRegex = {
            "^(#+)\\s*(.*)", // head
            "^&gt; (.*)", // blockquote
            "^-{3,}$", // hr
            "^`<code></code>", // code lines
            "^(\\s*)- (.*)", // ul
            "^(\\s*)\\d*\\. (.*)" // li
    };
    public String[] spanRegex = {
            "(.*)!\\[(.*?)\\]\\((.*?)\\)", // img
            "(.*)\\[(.*?)\\]\\((.*?)\\)", // a
            "(.*)\\*\\*(.*?)\\*\\*", // b
            "(.*)\\*(.*?)\\*", // em
    };
    public Pattern[] linePatterns = new Pattern[lineRegex.length], spanPatterns = new Pattern [spanRegex.length];
    public String[] linePattenNames = {"HEADER", "QUOTE", "SEPARATE_LINE", "CODE_LINES", "UNORDERED_LIST", "ORDERED_LIST"};
    public String[] spanPattenNames = {"IMAGE", "LINK", "BOLD", "EMPHASISE"};

    BlockBuilder() {
        for (int i=0; i<lineRegex.length; i++) {
            linePatterns[i] = Pattern.compile(lineRegex[i]);
        }
        for (int i=0; i<spanRegex.length; i++) {
            spanPatterns[i] = Pattern.compile(spanRegex[i]);
        }
    }

    public int defineLineType(String line) {
        for (int i=0; i<linePatterns.length; i++) {
            Matcher m = linePatterns[i].matcher(line);
            if (m.find()) {
//                System.out.println(m.group(1));
                return i;
            }
        }
        return -1;
    }

    // make most of the blocks
    private String blockMaker(BlockInfo block) {
        switch (block.type) {
            case "HEADER":
                return "<h" + block.level + ">" + block.content + "</h" + block.level + ">";
            case "CODE_LINES":
                return "<pre><code>" + block.content + "</code></pre>";
            case "QUOTE":
                return "<blockquote>" + block.content + "</blockquote>";
//            case "SEPARATE_LINE":
//                return "</ hr>\n";
            case "EMPHASISE":
                return "<em>" + block.content + "</em>";
            case "BOLD":
                return "<b>" + block.content + "</b>";
            case "INLINE_CODE":
                return "<code>" + block.content + "</code>";
            case "LINK":
                return "<a href='" + block.url + "' target='_blank'>" + block.content + "</a>";
            case "IMAGE":
                return "<img src='" + block.url + "' alt='" + block.content + "'>";
        }
        return block.content;
    }

    // builde headers
    public String headerBuilder(String source) {
        BlockInfo blockInfo = new BlockInfo(linePattenNames[0]);
        Matcher m = linePatterns[0].matcher(source);
        if (m.find()) {
            int l = m.group(1).length();
            l = Math.min(l, 6);
            blockInfo.setLevel(l);
            blockInfo.setContent(m.group(2));
        }
        return blockMaker(blockInfo);
    }

    // build quotes
    public String quoteBuilder(String source) {
        BlockInfo blockInfo = new BlockInfo(linePattenNames[1]);
        Matcher m = linePatterns[1].matcher(source);
        if (m.find()) {
            blockInfo.setContent(m.group(1));
        }
        return blockMaker(blockInfo);
    }

    // build code lines
    public String codeLinesBuilder(String source) {
        BlockInfo blockInfo = new BlockInfo(linePattenNames[3]);
        String codeLinesRegex = "^```(.*)\n([\\s\\S]*\n)```";
        Pattern p = Pattern.compile(codeLinesRegex);
        Matcher m = p.matcher(source);
        if (m.find()) {
            blockInfo.setContent(m.group(2));
        }
        return blockMaker(blockInfo);
    }

    // make lists
    // use stack to push/pop using lists
    private String listMaker(BlockInfo[] lists) {
        Stack<BlockInfo> blockStack = new Stack<BlockInfo>();
        StringBuilder str= new StringBuilder();
        for (BlockInfo list : lists) {
            if (blockStack.empty() || list.level > blockStack.peek().level) {
                str.append("<").append(list.type).append("><li>").append(list.content).append("</li>");
                blockStack.push(list);
            }
            else if (list.level == blockStack.peek().level && list.type == blockStack.peek().type) {
                str.append("<li>").append(list.content).append("</li>");
            }
            else {
                while (!blockStack.empty() && (list.level <= blockStack.peek().level)) {
                    BlockInfo previous = blockStack.pop();
                    if (list.type == previous.type && list.level == previous.level) {
                        str.append("<li>").append(list.content).append("</li>");
                        blockStack.push(list);
                        break;
                    } else {
                        str.append("</").append(previous.type.substring(0, 2)).append(">");
                    }
                }
                if (blockStack.empty() || list.level != blockStack.peek().level) {
                    str.append("<").append(list.type).append("><li>").append(list.content).append("</li>");
                    blockStack.push(list);
                }
            }
        }
        while (!blockStack.empty()) {
            BlockInfo previous = blockStack.pop();
            str.append("</").append(previous.type.substring(0,2)).append(">");
        }
        return str.toString();
    }

    // build lists
    public String listBuilder(String source) {
        source.replace("\t","  ");
        String[] listsStr = source.split("\n");
        BlockInfo[] lists = new BlockInfo[listsStr.length];
        String listRegex = "^([ ]*)(-|\\d*\\.) (.*)";
        Pattern p = Pattern.compile(listRegex);
        for (int i=0; i<listsStr.length; i++) {
            Matcher m = p.matcher(listsStr[i]);
            if (m.find()) {
                lists[i] = new BlockInfo("LIST");
                lists[i].setLevel(m.group(1).length());
                if (m.group(2).equals("-")) {
                    lists[i].setType("ul");
                }
                else {
                    lists[i].setType("ol start=''");
                }
                lists[i].setContent(m.group(3));
            }
        }
        return listMaker(lists);
    }

    // build inline html content
    public String spanBuilder(String line) {
        // for code: replace special characters first:
        Matcher codeMatcher = Pattern.compile("(.*)`(.*?)`").matcher(line);
        while (codeMatcher.find()) {
            BlockInfo block = new BlockInfo("INLINE_CODE");
            block.setContent(codeMatcher.group(2).replace("[","&0x5B;").replace("*","&0x2A;"));
            line = codeMatcher.replaceFirst(codeMatcher.group(1) + blockMaker(block));
            codeMatcher = Pattern.compile("(.*)`(.*?)`").matcher(line);
        }

        for (int i=0; i<spanRegex.length; i++) {
            Matcher m = spanPatterns[i].matcher(line);
            while (m.find()) {
                BlockInfo block = new BlockInfo(spanPattenNames[i]);
                block.setContent(m.group(2));
                if (m.groupCount() == 3) {
                    block.setUrl(m.group(3));
                }
                line = m.replaceFirst(m.group(1) + blockMaker(block));
                m = spanPatterns[i].matcher(line);
            }
        }

        // restore changes
        return line.replace("&0x5B;","[").replace("&0x2A;","*");
    }
}
