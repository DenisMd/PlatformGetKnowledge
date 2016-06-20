import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Test {

    @org.junit.Test
    public void getProgrammingLanguage() throws IOException, ParseException {
        File utf8File = new File("C:\\Users\\dmarkov\\Desktop\\Trx\\dmarkov_resources\\exceptional\\ExceptionalCards\\recover\\MasterCardFull");
        BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream(utf8File), StandardCharsets.UTF_8));
        String test = reader.readLine();
        System.err.println(test);
        System.err.println(test.charAt(0) == 0xFEFF);
        test = test.substring(1);
        System.err.println(test);
        System.err.println(test.charAt(0) == 0xFEFF);

        String test2 = "A".substring(1);
        System.err.println(test2);
    }


    public void getThemes(){

    }
}
