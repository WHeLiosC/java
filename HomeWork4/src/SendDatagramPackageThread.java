import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author 李辉
 * @version 2017.7.19
 */

public class SendDatagramPackageThread extends Thread {
    private int receiverPort;
    private String routerNodeID;
    private DatagramSocket ds;
    private boolean isHeartBeatPackage;
    private RouterTable rt;

    /**
     * 构造函数
     *
     * @param ds                 路由节点的套接字
     * @param routerNodeID       发送端的路由节点ID
     * @param receiverPort       接收端的端口号
     * @param isHeartBeatPackage 发送心跳包还是数据包
     * @param rt                 路由表，用于构建数据更新包
     */
    public SendDatagramPackageThread(
            DatagramSocket ds, String routerNodeID,
            int receiverPort, boolean isHeartBeatPackage, RouterTable rt) {
        this.routerNodeID = routerNodeID;
        this.receiverPort = receiverPort;
        this.ds = ds;
        this.isHeartBeatPackage = isHeartBeatPackage;
        this.rt = rt;
    }

    /**
     * 发送UDP心跳包，内容为发送端的路由节点ID及端口号
     */
    public void sendHeartBeatPackage() throws IOException {

        // 构建UDP心跳包内容，即发送端的路由节点ID和心跳包发送的时间
        long sendTime = System.currentTimeMillis();  // 心跳包发送出去的时间
        String str = "#" + routerNodeID + sendTime;  // 用#来标志心跳包
        byte[] buffer = str.getBytes();
        DatagramPacket dp = new DatagramPacket(buffer,
                buffer.length, InetAddress.getByName("127.0.0.1"), receiverPort);
        ds.send(dp);
        //System.out.println("我向"+receiverPort+"发了一个心跳包！");
    }

    /**
     * 发送UDP数据包
     */
    public void sendDataPackage() throws IOException {
        // 构建UDP数据包，内容为路由表
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(1024 * 64);
        ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
        os.flush();
        os.writeObject(rt);
        os.flush();
        byte[] buffer = byteStream.toByteArray();
        DatagramPacket dp = new DatagramPacket(buffer,
                buffer.length, InetAddress.getByName("127.0.0.1"), receiverPort);
        ds.send(dp);
        //System.out.println("我向"+receiverPort+"发了一个数据包！");
        os.close();
    }

    @Override
    public void run() {
        if (isHeartBeatPackage) {
            try {
                sendHeartBeatPackage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                sendDataPackage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
