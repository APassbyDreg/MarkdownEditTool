package fileio;

import java.io.IOException;

public class WebFile extends FileInfo {

    public WebFile(String address) throws IOException {
        super(address, 'a');
    }

    /*
    so embarrassing to have no new method here...
     */
}
