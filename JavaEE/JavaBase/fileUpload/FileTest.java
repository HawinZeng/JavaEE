package hawin;

import java.io.File;

public class FileTest {

    public static void main(String[] args) throws Exception {
        File file = new File("test/mm.txt");
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getCanonicalPath());
        System.out.println(file.getPath()); // mm.txt
        System.out.println(file.getName());
        if (!file.exists()) {
            file.mkdirs();
        }


    }

    static void getAllFile(File dir) { //传入的肯定是目录
        File[] fileList = dir.listFiles(); // 若目录不存在，则fileList是null
        if (fileList == null || fileList.length == 0) {
            return;
        }
        for (File f : fileList) {
            if (f.isDirectory()) {
                System.out.println(f.getAbsoluteFile());
                getAllFile(f);
            } else {
                System.out.println(f.getAbsoluteFile());
            }
        }
    }

    static void printDir(File file) {
        String pathName = file.getAbsolutePath();
        if (pathName.endsWith(".java")) {
            System.out.println(pathName);
            return;
        }

        if (file.isFile()) return;

        File[] fileList = file.listFiles();
        if (null != fileList) {
            for (File f : fileList) {
                printDir(f);
            }
        }
    }

}
