import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Test {

    @org.junit.Test
    public void getProgrammingLanguage() throws IOException, ParseException {
        String f = "D:\\Workplace\\test.txt";
        File file = new File(f);
        FileChannel fileChannel = null;
        try {
            System.out.println("Create file chanel");
            fileChannel = (new FileInputStream(file)).getChannel();
            System.out.println("sleep");
            for (int i=0; i < 50; i++) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            if (fileChannel != null) {
                System.out.println("close");
                fileChannel.close();
            }
        }


    }


    public void getThemes(){

    }
}
