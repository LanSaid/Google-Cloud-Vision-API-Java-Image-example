/**
 * Created by АленкаиВова on 08.06.2016.
 */

import java.io.*;
import java.util.Date;


public class FileWorker {
    public static void write(String fileName, String text) {
        File file = new File(fileName);
        try {

            if (!file.exists()) {
                file.createNewFile();
            }


            FileWriter out = new FileWriter(file.getAbsoluteFile(), true);

            try {
                Date now = new Date();
                out.append("\n" + now + "" + text);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
