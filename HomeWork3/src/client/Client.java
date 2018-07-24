package client;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import java.io.*;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author lihui
 * @version 2018.7.17
 */

import static client.AESencode.initAESEncodeCipher;

public class Client {

    public static void writeFile(String fileName, String sendName, DataOutputStream dos, DataInputStream dis) throws IOException {
        boolean isDirectory = true;

        if (new File(fileName).isDirectory()) {  // 如果是路径，则递归；同时把文件夹的名字发给服务端
            dos.writeBoolean(isDirectory);  // 发送是目录的标志位
            boolean isReceiveName = false;
            dos.writeUTF(sendName);
            while (!isReceiveName) {
                isReceiveName = dis.readBoolean();
            }
            DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(fileName));
            for (Path path : ds)
                writeFile(fileName + File.separator + path.getFileName().toString(),
                        sendName + File.separator + path.getFileName().toString(), dos, dis);
        } else {  // 如果是文件，则发送文件名，发送正文
            dos.writeBoolean(!isDirectory);  // 发送是文件的标志位
            Properties p = new Properties();
            InputStreamReader isr = new InputStreamReader(
                    new FileInputStream("config.properties"), "GBK");
            p.load(isr);

            // 初始化AES密码并创建加密流
            Cipher cipher = initAESEncodeCipher(p.getProperty("PASSWORD"));
            CipherInputStream cis;

            // 建立缓冲区和缓冲区内字节数标志
            byte[] buffer = new byte[1024];
            int num;

            // 传文件名并等待已接受信号
            boolean isReceiveName = false;
            dos.writeUTF(sendName);
            while (!isReceiveName) {  // 服务器返回接收到文件名后在开始传输正文
                isReceiveName = dis.readBoolean();
            }

            // 传文件大小并等待已接受信号
            boolean isReceiveSize = false;
            long fileSize = new File(fileName).length();
            dos.writeLong(fileSize);
            while (!isReceiveSize) {
                isReceiveSize = dis.readBoolean();
            }

            // 显示上传的文件名和大小
            if (fileSize < 1024)
                System.out.printf("上传文件 %s %dB\n", sendName, fileSize);
            if (fileSize > 1024 && fileSize < Math.pow(1024, 2))
                System.out.printf("上传文件 %s %.1fKB\n", sendName, fileSize / (double) 1024);
            if (fileSize > Math.pow(1024, 2) && fileSize < Math.pow(1024, 3))
                System.out.printf("上传文件 %s %.1fMB\n", sendName, fileSize / Math.pow(1024, 2));
            if (fileSize > Math.pow(1024, 3))
                System.out.printf("上传文件 %s %.2fGB\n", sendName, fileSize / Math.pow(1024, 3));

            // 传文件内容
            DataInputStream uploadDis = new DataInputStream(
                    new FileInputStream(Paths.get(fileName).toAbsolutePath().toString()));
            cis = new CipherInputStream(uploadDis, cipher);
            while ((num = cis.read(buffer)) != -1) {
                dos.write(buffer, 0, num);
                dos.flush();
            }
            cis.close();
            uploadDis.close();

            // 等待接受完成信号
            boolean isFinished = false;
            while (!isFinished) {
                isFinished = dis.readBoolean();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        InputStreamReader isr = new InputStreamReader(
                new FileInputStream("config.properties"), "GBK");
        p.load(isr);
        int port = Integer.parseInt(p.getProperty("PORT"));
        String host = p.getProperty("HOST");

        Socket socket = new Socket(host, port);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        // fileName是绝对路径名，用来给函数判断是否是路径/文件
        // sendName发送给服务器的名字不是绝对路径
        String fileName = p.getProperty("FILENAME");
        String sendName = Paths.get(fileName).getFileName().toString();
        writeFile(fileName, sendName, dos, dis);
    }
}