import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 李辉
 * @version 2018.7.18
 */

public class RouterNode {
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(100);
    private String routerNodeID;  // 程序参数指定
    private int port;  // 程序参数指定
    private String configFile;  // 程序参数指定
    private DatagramSocket ds;  // 根据程序参数指定的端口号构造
    Map aliveNodeMap;  // 存储活着的节点<节点，包到达时间>
    RouterTable rt;  // 存储路由器的路由表


    /**
     * 构造函数
     * 初始化DatagramSocket,用来接发UDP包
     * 新建leastCostMap为<String, Double>键值对
     * 新建化路由表
     *
     * @param port 端口号
     */
    public RouterNode(int port, String routerNodeID, String configFile) throws SocketException {
        this.port = port;
        this.routerNodeID = routerNodeID;
        this.configFile = configFile;
        ds = new DatagramSocket(port);
        aliveNodeMap = new HashMap<String, Long>();
        rt = new RouterTable();
    }

    /**
     * @return 路由节点ID
     */
    public String getRouterNodeID() {
        return routerNodeID;
    }

    /**
     * 得到所有直接相连的节点的信息
     *
     * @return 直接相连的节点的信息
     * @throws IOException 配置文件
     */
    public List listConnectedNode() throws IOException {
        List connectedNodeList = new LinkedList();
        Scanner in = new Scanner(Paths.get(configFile));
        in.next();  // 跳过第一行的连接数
        while (in.hasNextLine()) {
            RouterNodeMessage rnm = new RouterNodeMessage(
                    in.next(), Double.parseDouble(in.next()), Integer.parseInt(in.next()));
            connectedNodeList.add(rnm);
        }
        return connectedNodeList;
    }

    /**
     * 打印与节点直接相连的路由节点信息
     */
    public void showConnectedNode() throws IOException {
        for (Object o : listConnectedNode()) {
            ((RouterNodeMessage) o).showRouterNodeMessage();
        }
    }

    /**
     * 根据配置文件初始化路由表
     *
     * @throws IOException listConnectedNode()异常
     */
    public void initialRouterTable() throws IOException {
        rt.add(routerNodeID, listConnectedNode());
    }

    /**
     * 得到最短路径定时输出时间
     *
     * @return 定时输出时间
     * @throws IOException 配置文件
     */
    private long getShowPathTime() throws IOException {
        // 获得路由表定时展示的时间间隔
        Properties p = new Properties();
        InputStreamReader isr = new InputStreamReader(
                new FileInputStream("globalConfigFile.properties"), "GBK");
        p.load(isr);
        return Long.parseLong(p.getProperty("leastCostPathShowTime"));
    }

    /**
     * 打印该节点到其他节点的最短路径信息
     */
    private void showLeastCostPath() {
        fixedThreadPool.execute(() -> {
            long lastShowTime = 0;
            while (true) {
                long startShowTime = System.currentTimeMillis();
                try {
                    if (startShowTime - lastShowTime > getShowPathTime()) {
                        fixedThreadPool.execute(new ShowPathThread(listLeastCostPath()));
                        lastShowTime = startShowTime;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 得到最短路径信息，用map存储
     *
     * @return <String,Double>的最短路径信息map
     */
    private synchronized Map listLeastCostPath() {
        if(aliveNodeMap.size() == 0)  // 如果没有活着的相邻的节点，那么直接返回null
            return null;

        Map leastCostPathMap = new HashMap<String, Double>();

        // 统计当前路由表已知的节点数
        StringBuilder allKnownNode = new StringBuilder();
        Map tableMap = rt.getTable();
        Set set = tableMap.keySet();
        for (Object aSet : set) {
            String rID = (String) aSet;
            if (!allKnownNode.toString().contains(rID))
                allKnownNode.append(rID);
            List rnmList = (List) tableMap.get(rID);
            for (Object arnmList : rnmList) {
                RouterNodeMessage rnm = (RouterNodeMessage) arnmList;
                if (!allKnownNode.toString().contains(rnm.getRouterNodeID()))
                    allKnownNode.append(rnm.getRouterNodeID());
            }
        }

        // 构造二维数组,建立节点ID与数组下标之间的映射关系
        int nodeNumber = allKnownNode.toString().length();
        Map vertexMap = new HashMap<String, Integer>();  // 建立映射关系,根据节点找下标
        for (int i = 0; i < nodeNumber; i++)
            vertexMap.put(allKnownNode.toString().substring(i, i + 1), i);
        Map numberMap = new HashMap<Integer, String>();  // 建立映射关系,根据下标找节点
        for (int i = 0; i < nodeNumber; i++)
            numberMap.put(i, allKnownNode.toString().substring(i, i + 1));
        double[][] graph = new double[nodeNumber][nodeNumber];
        for (int i = 0; i < nodeNumber; i++)
            for (int j = 0; j < nodeNumber; j++) {
                if (i == j)
                    graph[i][j] = 0;
                else
                    graph[i][j] = Double.MAX_VALUE;
            }
        for (Object aSet : set) {
            String rID = (String) aSet;
            List rnmList = (List) tableMap.get(rID);
            for (Object arnmList : rnmList) {
                RouterNodeMessage rnm = (RouterNodeMessage) arnmList;
                graph[(int) vertexMap.get(rID)][(int) vertexMap.get(rnm.getRouterNodeID())] = rnm.getPathCost();
            }
        }

        // 计算最短路径
        double[] cost = graph[(int) vertexMap.get(routerNodeID)];  // 记录路径长度,初始化为估计值
        int[] path = new int[nodeNumber];  // 记录路径信息,初始化为起始节点对应的数组下标
        for (int i = 0; i < nodeNumber; i++)
            path[i] = (int) vertexMap.get(routerNodeID);
        boolean[] flag = new boolean[nodeNumber];  // 标志位，标志着这个节点是否还要考虑
        flag[(int) vertexMap.get(routerNodeID)] = true;  // 把起始节点纳入确定值范围
        int record = 0;
        for (int j = 0; j < cost.length; j++) {  // 找到当前节点到各点的距离最小值
            double min = Double.MAX_VALUE;
            for (int i = 0; i < cost.length; i++)
                if (cost[i] < min && !flag[i]) {
                    min = cost[i];
                    record = i;
                }
            flag[record] = true;  // 将距离最小的节点纳入确定值范围，之后不再考虑
            for (int i = 0; i < cost.length; i++) {  // 根据得到的最小距离的节点去更新其他节点到原节点的距离
                if (cost[record] + graph[record][i] < cost[i] && graph[record][i] != Double.MAX_VALUE && !flag[i])  //防止溢出
                {
                    cost[i] = cost[record] + graph[record][i];
                    path[i] = record;
                }
            }
        }

        // 结果转化为Map
        for (int i = 0; i < nodeNumber; i++) {
            if (i == (int) vertexMap.get(routerNodeID))  // 到本身的最短不考虑
                continue;
            StringBuilder pathStr = new StringBuilder();  // 一直往前头插，从而得到正序的字符串
            pathStr.insert(0, numberMap.get(i));
            for (int j = i; j != (int) vertexMap.get(routerNodeID); ) {
                pathStr.insert(0, numberMap.get(path[j]));
                j = path[j];
            }
            leastCostPathMap.put(pathStr.toString(), cost[i]);
        }

        return leastCostPathMap;
    }

    /**
     * 获得更新包发送时间
     *
     * @return 更新时间
     * @throws IOException 配置文件
     */
    private long getUpdateTime() throws IOException {
        // 获得发送更新包的时间间隔
        Properties p = new Properties();
        InputStreamReader isr = new InputStreamReader(
                new FileInputStream("globalConfigFile.properties"), "GBK");
        p.load(isr);
        return Long.parseLong(p.getProperty("pathUpdateTime"));
    }

    /**
     * 隔一个指定时间发送心跳包和数据更新包
     * 对每一个直接相连的路由节点都有一个线程向它发送心跳包和数据包
     */
    public synchronized void sendPackage() {
        fixedThreadPool.execute(() -> {
            long lastHeartBeatTime = 0;  // 最后一次心跳时间
            long lastDataPackageTime = 0;  // 最后一次发数据包时间
            Iterator it = null;

            while (true) {
                try {
                    it = listConnectedNode().iterator();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long startHeartBeatTime = System.currentTimeMillis();  // 当前时间
                try {
                    if (startHeartBeatTime - lastHeartBeatTime > getHeartBeatTime()) {
                        while (it.hasNext()) {
                            int receiverPort = ((RouterNodeMessage) it.next()).getPort();
                            fixedThreadPool.execute(new SendDatagramPackageThread(ds, routerNodeID,
                                    receiverPort, true, null));
                        }
                        lastHeartBeatTime = startHeartBeatTime;   // 把当前时间设置为最新的心跳时间
                    }

                    long startDataPackageTime = System.currentTimeMillis();  // 当前时间

                    if (startDataPackageTime - lastDataPackageTime > getUpdateTime()) {
                        while (it.hasNext()) {
                            int receiverPort = ((RouterNodeMessage) it.next()).getPort();
                            fixedThreadPool.execute(new SendDatagramPackageThread(ds, routerNodeID,
                                    receiverPort, false, rt));
                        }
                        lastDataPackageTime = startDataPackageTime;  // 把当前时间设置为最新的更新时间
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 负责接收数据，使用另一个线程处理
     */
    public synchronized void receivePackage() {
        // new一个线程只负责接受包，而不对其进行处理
        fixedThreadPool.execute(() -> {
            while (true) {
                byte[] buffer = new byte[1024 * 64];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                try {
                    ds.receive(dp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fixedThreadPool.execute(new HandleDatagramPackageThread(dp, aliveNodeMap, rt));  // 交给另一个线程处理
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获得心跳时间
     *
     * @return 心跳包间隔时间
     * @throws IOException 全局配置文件
     */
    private long getHeartBeatTime() throws IOException {
        // 获得发送心跳包的时间间隔
        Properties p = new Properties();
        InputStreamReader isr = new InputStreamReader(
                new FileInputStream("globalConfigFile.properties"), "GBK");
        p.load(isr);
        return Long.parseLong(p.getProperty("sendHeartBeatPackageTime"));
    }

    /**
     * 检查一个节点是否还活着
     * 如果在上一次发送心跳包后的三个时间间隔内都没有收到心跳包，则认为掉线，并进行清除的操作
     */
    private void checkNodeIsAlive() {
        fixedThreadPool.execute(() -> {
            while (true) {
                String deleteNodeID = null;
                long currentTime = System.currentTimeMillis();
                Set set = aliveNodeMap.keySet();
                for (Object aSet : set) {
                    String rID = (String) aSet;
                    long lastSendTime = (long) aliveNodeMap.get(rID);
                    try {
                        if (currentTime - lastSendTime > 3 * getHeartBeatTime()) {
                            deleteNodeID = rID;
                            System.out.println(rID + "已经GG啦！");
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                aliveNodeMap.remove(deleteNodeID);  // 从活着的节点链表中删除
                rt.remove(deleteNodeID);  // 从路由表中删除
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showRouterTable() throws InterruptedException {
        while (true) {
            rt.showTable();
            Thread.sleep(2000);
        }
    }

    /**
     * test
     * @param args arg[0]routerNodeID args[1]port args[2]configFile
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 3)
            throw new Exception("命令格式不正确！");

        RouterNode rn = new RouterNode(Integer.parseInt(args[1]), args[0], args[2]);
        rn.initialRouterTable();

        System.out.println(rn.getRouterNodeID());
        //rn.showConnectedNode();

        rn.sendPackage();

        rn.receivePackage();

        rn.checkNodeIsAlive();

        //rn.showRouterTable();

        rn.showLeastCostPath();

        // 敲命令输出,输入s立即展示最短路径
        rn.fixedThreadPool.execute(() -> {
            while (true) {
                Scanner in = new Scanner(System.in);
                String order = in.nextLine();
                if (order.equals("s"))
                    rn.fixedThreadPool.execute(new ShowPathThread(rn.listLeastCostPath()));
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}

/**
 * A 2000 configA.txt
 * B 2001 configB.txt
 * C 2002 configC.txt
 * D 2003 configD.txt
 * E 2004 configE.txt
 * F 2005 configF.txt
 */

/**
 * Test
 * A 2000 A
 * B 2001 B
 * C 2002 C
 * D 2003 D
 * E 2004 E
 * F 2005 F
 * G 2006 G
 * H 2007 H
 * I 2008 I
 */
