import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Test {

    @org.junit.Test
    public void getProgrammingLanguage() throws IOException, ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Date date = format.parse("171");
        System.out.println(date.toString());
    }


    public void getThemes(){

    }
}
