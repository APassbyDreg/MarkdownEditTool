package File;
import	java.io.FileInputStream;

import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class WebFile extends FileInfo {

    public WebFile(String address) throws IOException {
        super(address, 'a');
    }



}
