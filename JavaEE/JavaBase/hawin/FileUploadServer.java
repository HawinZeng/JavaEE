package hawin;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class FileUploadServer {

    public static void main(String[] args) {
        // 1. 创建服务器对象,并开启服务器
        FileUploadServer server = new FileUploadServer();
        server.startServer();

    }


    public FileUploadServer(){

    }

    public void startServer(){

        try {
            ServerSocket server = new ServerSocket(Constant.FILE_UPLOAD_SERVER_PORT);

            while(true){
                Socket s = server.accept();

                // 1. 接收客户端的文件路径信息，
                InputStream is = s.getInputStream();
                byte[] buff = new byte[1024];
                int len = is.read(buff);
                String filePath = new String(buff,0,len);


                // 2.判断文件是否已经上传
                OutputStream os = s.getOutputStream();
                boolean isExistFile = judgeFileExists(filePath);
                if(isExistFile){
                    os.write(Constant.FILE_EXIST.getBytes());
                }else{
                    os.write(Constant.FILE_NO_EXIST.getBytes());

                    BufferedInputStream bis = new BufferedInputStream(is); // ??
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("clientFiles"+File.separator+filePath));
                    byte[] b = new byte[8*1024];
                    int newLen;
                    while((newLen=bis.read(b))!=-1){
                        bos.write(b,0,newLen);
                    }

                    bos.close();
                    bis.close();
                }

                //
                os.close();
                is.close();
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean judgeFileExists(String filePath)   {
        Objects.requireNonNull(filePath);

        File clientFiles = new File("clientFiles");
        if(!clientFiles.exists()){
            clientFiles.mkdirs();
        }

        String[] filePaths = clientFiles.list();
        if (filePaths == null) return false;
        for(String path:filePaths){
            if(filePath.equals(path)){
                return true;
            }
        }
        return false;
    }


}
