package hawin;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class FileUploadClient {

    public void startClient(){

        try (Socket s = new Socket(Constant.SERVER_IP,Constant.FILE_UPLOAD_SERVER_PORT);
             OutputStream os = s.getOutputStream();InputStream is = s.getInputStream()){
            // 上传图片给服务器
            // 1. 录入文件，通过键盘输入录入文件,且文件存在
            File upFile = getExistFile();

            // 2. 使用IO流给服务器发文件名
            os.write(upFile.getName().getBytes());

            // 3. 接受服务器返回的信息，若已上传，则停止上传，若没有，则上传
            byte[] buff = new byte[1024];
            int len = is.read(buff); // 阻塞，等待服务器回应
            String result = new String(buff,0,len);

            if(Constant.FILE_EXIST.equals(result)){
                System.out.println(upFile+":已经上传了！");
            }else{
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(upFile));
                BufferedOutputStream bos = new BufferedOutputStream(os);

                byte[] b = new byte[8*1024];
                int upLen;
                while((upLen=bis.read(b))!=-1){
                    bos.write(b,0,upLen);
                }

                bos.close();
                bis.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取待上传的文件，且是存在的文件，通过递归法，确切找到存在文件
     * @return 存在的file
     */
    private File getExistFile() {
        File file = new File(getUpLoadFilePath());

        // 1. 录入的文件是否存在  文件目录不存在，那么文件就一定不存在
        if(!file.exists()){ //  提示输入路径有误，需要重新输入
            System.out.println("输入的文件不存在！");
           return getExistFile();//  递归进去层层找存在的file，然后通过return 层层返回存在的file；直到输入正确为止
        }

        // 2. 排除文件夹
        if(file.isDirectory()){
            System.out.println("输入的是一个文件夹，需要排除！");
            return getExistFile();
        }

        return file;
    }

    /**
     * 获取输入的文件路径
     * @return 文件路径
     */
    private String getUpLoadFilePath() {
        Scanner sr = new Scanner(System.in);
        System.out.println("请输入上传文件正确的完整路径：");
        return sr.nextLine();
    }

}
