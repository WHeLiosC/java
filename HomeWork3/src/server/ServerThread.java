package server;

import java.net.Socket;

/**
 * @author lihui
 * @version 2018.7.17
 */

public class ServerThread extends Thread {
    private Socket socket = null;
    private FileStrategy fs;
    private String fileName;

    public ServerThread(FileStrategy fs) {
        this.fs = fs;
        this.fileName = null;
    }

    public boolean isIdle() {
        // 如果socket变量为空，那么这个线程当然是空闲的
        return socket == null;
    }

    public synchronized void setSocket(Socket socket, String fileName) {
        this.socket = socket;
        this.fileName = fileName;
        notify();
    }

    public synchronized void run() {
        while (true) {
            try {
                wait();
                fs.FileService(socket, fileName);
                fileName = null;
                socket = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
