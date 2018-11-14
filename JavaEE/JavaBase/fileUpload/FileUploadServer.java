package hawin;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

/**
 *  服务器端，参考IO流关闭，把调用的IO流都关闭，不管是否异常
 */
public class FileUploadServer {

    @SuppressWarnings("InfiniteLoopStatement")
    public void startServer(){
        try (ServerSocket server = new ServerSocket(Constant.FILE_UPLOAD_SERVER_PORT)){
            while(true){
                Socket s = server.accept();// 服务器开启，阻塞，等待客户端连接
                new Thread(()-> fileUpLoadProcess(s)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 连接后，与客户端处理文件上传的过程
     * @param s 客户端的Socket对象
     */
    private void fileUpLoadProcess(Socket s) {
        try(InputStream is = s.getInputStream();OutputStream os = s.getOutputStream()){
            // 1. 接收客户端的文件路径信息，
            byte[] buff = new byte[1024];
            int len = is.read(buff); // 阻塞，等待客户端发消息
            String filePath = new String(buff,0,len);

            // 2.判断文件是否已经上传
            if(checkFileExist(filePath)){
                os.write(Constant.FILE_EXIST.getBytes());
            }else{
                os.write(Constant.FILE_NO_EXIST.getBytes());
                // 这里输is入流被read后，不会再有以前的数据了！！！！所以可以接着给下次调用使用，不影响下次调用的数据
                fileUpLoad(is, filePath);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null!=s){
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 文件上传到服务器本地
     */
    private void fileUpLoad(InputStream is, String filePath) {
        try(BufferedInputStream bis = new BufferedInputStream(is);BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(Constant.SERVER_FILE_UP_LOAD_DIR+File.separator+filePath))){
            byte[] b = new byte[8*1024];
            int newLen;
            while((newLen=bis.read(b))!=-1){
                bos.write(b,0,newLen);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 依据文件名判断文件是否存在
     * @param filePath 客户端传来的文件名
     * @return  true ／ false
     */
    private boolean checkFileExist(String filePath)   {
        Objects.requireNonNull(filePath);
        File clientFiles = new File(Constant.SERVER_FILE_UP_LOAD_DIR);

        if(!clientFiles.exists()){
            clientFiles.mkdirs();
            return false;
        }

        String[] filePaths = clientFiles.list();
        if (filePaths == null || filePaths.length==0) return false;

        for(String path:filePaths){
            if(filePath.equals(path)){
                return true;
            }
        }
        return false;
    }
}
