package Convert;

public class BlockInfo {
    public String content = "";
    public String url = "";
    public String type = "TEXT";
    public int level = 0;

    BlockInfo(String blockType) {
        type = blockType;
    }

    public void setType(String blockType) {
        type = blockType;
    }

    public void setLevel(int blockLevel) {
        level = blockLevel;
    }

    public void setContent (String blockContent) {
        content = blockContent;
    }

    public void setUrl(String blockUrl) {
        url = blockUrl;
    }
}
