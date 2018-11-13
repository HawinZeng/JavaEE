package hawin;

import java.io.File;

public class FileTest {

    public static void main(String[] args) throws Exception{
        File file = new File("/Volumes/C/Pic/me.jpg");
        System.out.println(file.getPath());
//        File parentFile  =  file.getParentFile();
//        System.out.println(parentFile.getAbsolutePath());
//        if(parentFile.exists()){
//            System.out.println(true);
//        }else{
//            System.out.println(false);
//        }
    }
}
