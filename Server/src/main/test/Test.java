import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Test {

    @org.junit.Test
    public void getProgrammingLanguage() throws IOException {
        InputStream is = null;
        try {
            try {
                is = new FileInputStream(new File("dasda"));
                is.read();
            } catch (IOException e) {
                throw  new RuntimeException("wwwwwwwwww");
            }

        } finally {

        }

    }


    public void getThemes(){

    }
}
