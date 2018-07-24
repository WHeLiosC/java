package server;

import java.net.Socket;
import java.util.ArrayList;

/**
 * @author lihui
 * @version 2018.7.17
 */

public class ThreadPoolSupport implements FileStrategy {
    public ArrayList threads = new ArrayList();
    private final int INIT_THREADS = 50;
    private FileStrategy fs;
    private String fileName;

    public ThreadPoolSupport(FileStrategy fs) {
        this.fs = fs;
        this.fileName = null;
        for (int i = 0; i < INIT_THREADS; i++) {
            ServerThread t = new ServerThread(fs);
            t.start();
            threads.add(t);
        }

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void FileService(Socket socket, String fileName) {
        ServerThread t = null;
        boolean found = false;
        for (int i = 0; i < threads.size(); i++) {
            t = (ServerThread) threads.get(i);
            if (t.isIdle()) {
                found = true;
                break;
            }
        }

        if (!found) {
            t = new ServerThread(fs);
            t.start();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threads.add(t);
        }

        t.setSocket(socket, fileName);
    }
}
