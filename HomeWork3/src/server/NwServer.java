package server;

import java.net.*;

/**
 * @author lihui
 * @version 2018.7.17
 */

public class NwServer {  // NwServer.java，负责接受连接请求，并将创建的Socket对象
    public NwServer(int port, FileStrategy fs, String fileName) {  // 这个方法将在主线程中执行
        try {
            ServerSocket ss = new ServerSocket(port);
            System.out.println("服务器准备就绪！等待连接...");
            while (true) {
                Socket socket = ss.accept();  // 负责接受连接请求
                System.out.println("与客户端" + socket.getInetAddress() + "连接");
                fs.FileService(socket, fileName);  // 将服务器端的socket对象传递给ThreadPoolSupport对象
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
