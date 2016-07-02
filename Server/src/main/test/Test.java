import java.io.*;
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
        StringBuilder stringBuilder = new StringBuilder();
        Locale ruL  = new Locale("ru");
        Formatter formatter = new Formatter(stringBuilder, Locale.US);

        int a = 1;
        double b = Math.PI;
        char c = 'g';
        String str = "ttt";

        //System.out.println(formatter.format("Hello : %4$s , %3$c , %2$f , %1$d" , a,b,c,str).toString());
        //System.out.println(formatter.format("Char c : %1s%1s%1s" , str,str,str).toString());

        //System.out.println(formatter.format(Locale.FRANCE,"Math pi : %+40.20f" ,b).toString());

        System.out.format(ruL,"Local time: %tc", Calendar.getInstance());
    }


    public void getThemes(){

    }
}
