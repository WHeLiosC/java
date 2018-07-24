package server;

import java.net.*;

/**
 * @author lihui
 * @version 2018.7.17
 */

public interface FileStrategy {
    void FileService(Socket socket, String fileName);  // 对传入的socket对象进行处理
}
