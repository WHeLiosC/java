package server;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.net.Socket;

/**
 * @author lihui
 * @version 2018.7.17
 */
import static server.AESdecode.initAESDecodeCipher;

public class FileUploadProtocol implements FileStrategy {
    static final String password = "1234567890123456";

    @Override
    public void FileService(Socket socket, String fileName) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            while (true) {
                // 读标志位，看是文件还是目录
                Boolean isDirectory = dis.readBoolean();

                if (isDirectory) {  // 如果是目录，则建立目录
                    String clientFileName = dis.readUTF();
                    dos.writeBoolean(true);
                    File f = new File(fileName + File.separator + clientFileName);
                    System.out.println("创建文件夹 " + f.getName());
                    f.mkdir();
                } else {
                    // 初始化AES密码并创建解密流
                    Cipher cipher = initAESDecodeCipher(password);
                    CipherOutputStream cos;

                    // 建立缓冲区和缓冲区内字节数标志
                    byte[] buffer = new byte[1024];
                    int num;

                    // 接收文件名并返回已接受信号
                    String clientFileName = dis.readUTF();
                    dos.writeBoolean(true);

                    // 接收文件大小并返回已接收信号
                    long fileSize = dis.readLong();
                    dos.writeBoolean(true);

                    // 接受正文
                    // 服务器指定位置和用户的文件名构成路径
                    String relativeFileName = fileName + File.separator + clientFileName;
                    if (fileSize < 1024)
                        System.out.printf("接收文件 %s %dB\n", relativeFileName, fileSize);
                    if (fileSize > 1024 && fileSize < Math.pow(1024, 2))
                        System.out.printf("接收文件 %s %.1fKB\n", relativeFileName, fileSize / (double) 1024);
                    if (fileSize > Math.pow(1024, 2) && fileSize < Math.pow(1024, 3))
                        System.out.printf("接收文件 %s %.1fMB\n", relativeFileName, fileSize / Math.pow(1024, 2));
                    if (fileSize > Math.pow(1024, 3))
                        System.out.printf("接收文件 %s %.2fGB\n", relativeFileName, fileSize / Math.pow(1024, 3));
                    FileOutputStream fos = new FileOutputStream(relativeFileName);
                    DataOutputStream fileDos = new DataOutputStream(fos);
                    cos = new CipherOutputStream(fileDos, cipher);
                    while (fileSize >= 0) {
                        num = dis.read(buffer);
                        cos.write(buffer, 0, num);
                        cos.flush();
                        fileSize -= num;
                    }

                    cos.close();
                    fos.close();
                    fileDos.close();

                    // 发送接收完成信号
                    dos.writeBoolean(true);
                }
            }

        } catch (IOException e) {
            System.out.println("与客户端失去连接...");
        }

    }
}
