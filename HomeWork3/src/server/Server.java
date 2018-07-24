package server;

/**
 * @author lihui
 * @version 2018.7.17
 */

public class Server {
    public static void main(String[] args) throws Exception {
        if (args.length == 0)
            throw new Exception("参数缺失！无监听端口号...\n");
        int port = Integer.parseInt(args[0]);
        if(args.length<2)
            throw new Exception("参数缺失！无文件上传地址...\n");
        new NwServer(port, new ThreadPoolSupport(new FileUploadProtocol()), args[1]);
    }
}
