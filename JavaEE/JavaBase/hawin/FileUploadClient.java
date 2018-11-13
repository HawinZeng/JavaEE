package hawin;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class FileUploadClient {

    public static void main(String[] args) {
        FileUploadClient client = new FileUploadClient();
        client.startClient();
    }

    public FileUploadClient(){

    }

    public void startClient(){

        try (Socket s = new Socket(Constant.SERVER_IP,Constant.FILE_UPLOAD_SERVER_PORT)){
            // 上传图片给服务器
            // 1. 录入文件，通过键盘输入录入文件,且文件存在
            File upFile = getExistFile();

            String[] paths = upFile.getPath().split(File.separator);

            // 2. 使用IO流给服务器发文件名
            OutputStream os = s.getOutputStream();
            os.write(paths[paths.length-1].getBytes());

            // 3. 接受服务器返回的信息，若已上传，则停止上传，若没有，则上传
            InputStream is = s.getInputStream();
            byte[] buff = new byte[1024];
            int len = is.read(buff);
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
            is.close();
            os.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取待上传的文件，且是存在的文件
     * @return
     */
    private File getExistFile() {

        String filePath = getUpLoadFilePath();
        File file = new File(filePath);

        // 1.  首先判断文件夹是否正确
        File parentFile = file.getParentFile();
        if(parentFile==null || !parentFile.exists()){
            System.out.println("输入的文件路目录不存在！");
            getUpLoadFilePath();// 递归，直到输入正确为止
        }

        // 2. 录入的文件是否存在
        if(!file.exists()){ //  提示输入路径有误，需要重新输入
            System.out.println("输入的文件不存在！");
            getUpLoadFilePath();// 递归，直到输入正确为止
        }

        return file;
    }

    /**
     *
     * @return
     */
    private String getUpLoadFilePath() {
        Scanner sr = new Scanner(System.in);
        System.out.println("请输入上传文件正确的完整路径：");
        return sr.nextLine();
    }


}
