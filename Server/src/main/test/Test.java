import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Test {

    @org.junit.Test
    public void getProgrammingLanguage(){
        File file = new File("C:\\Users\\Alena\\Desktop\\project\\different\\den\\PlatformGetKnowledge\\Server\\src\\main\\webapp\\resources\\bower_components\\codemirror\\mode");
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        for (String der : directories){
            System.out.println(der);
        }
    }


    public void getThemes(){
        File file = new File("C:\\Users\\Alena\\Desktop\\project\\different\\den\\PlatformGetKnowledge\\Server\\src\\main\\webapp\\resources\\bower_components\\codemirror\\theme");
        String[] files = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isFile();
            }
        });
        for (String f : files){
            System.out.println(f.replace(".css",""));
        }
    }
}
