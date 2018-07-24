import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.*;

/**
 * @author 李辉
 * @version 2017.7.19
 */

// 只负责对心跳包和数据包进行处理，不负责接收
public class HandleDatagramPackageThread extends Thread {
    private DatagramPacket dp;
    public Map aliveNodeMap;
    RouterTable rt;

    /**
     * 构造函数
     *
     * @param dp           要处理的包
     * @param aliveNodeMap 存所有活着的节点
     */
    public HandleDatagramPackageThread(DatagramPacket dp, Map aliveNodeMap, RouterTable rt) {
        this.dp = dp;
        this.aliveNodeMap = aliveNodeMap;
        this.rt = rt;
    }

    private boolean isHeartBeatPackage() {
        byte[] buffer = dp.getData();
        String str = new String(buffer);
        if (str.startsWith("#"))
            return true;
        return false;
    }

    @Override
    public synchronized void run() {
        if (isHeartBeatPackage()) {
            byte[] buffer = dp.getData();
            String str = new String(buffer);
            String routerNodeID = str.substring(1, 2);
            long sendTime = Long.parseLong(str.substring(2).trim());
            aliveNodeMap.put(routerNodeID, sendTime);
            //System.out.println("收到" + routerNodeID + " " + sendTime + "的心跳包");
        } else {
            byte[] buffer = dp.getData();
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bis));
                RouterTable receiveRT = (RouterTable) ois.readObject();
                ois.close();

                /**
                 *对接收到的路由表中的每一个节点
                 *如果原路由表中没有这个节点，直接调用RouterTable类的add()方法加进去
                 *如果原始路由表中有这个节点，就对其中List的每一项进行比对
                 *如果完全一致，那么continue；
                 *如果不一致，那么找到不一致的那一条，
                 * 把这条记录包含的节点从路由表中删除
                 * （这个删除方法是从路由表Map中删除这个key值，同时对于其他的节点，对其中包含这个key值的记录也进行删除）
                 */
                HashMap receiveRTMap = receiveRT.getTable();
                Set receiveRTSet = receiveRTMap.keySet();
                Iterator receiveIt = receiveRTSet.iterator();
                while (receiveIt.hasNext()) {
                    String rID = (String) receiveIt.next();
                    List rnmList = (List<RouterNodeMessage>) receiveRTMap.get(rID);
                    if (!rt.contains(rID)) {
                        rt.add(rID, rnmList);
                    } else {
                        // 接收的路由表的某一节点的记录数比原路由表这一节点的记录数相等
                        if(rnmList.size() == rt.getListOfNode(rID).size())
                            continue;
                        // 接收的路由表的某一节点的记录数比原路由表这一节点的记录数多
                        if (rnmList.size() > rt.getListOfNode(rID).size()) {
                            for (Object arnmList : rnmList) {
                                RouterNodeMessage extraRNM = (RouterNodeMessage) arnmList;
                                if (!rt.hasRecordInNode(rID, extraRNM)) {
                                    rt.remove(extraRNM.getRouterNodeID());
                                    break;
                                }
                            }
                        }
                        // 接收的路由表的某一节点的记录数比原路由表这一节点的记录数少
                        else if (rnmList.size() < rt.getListOfNode(rID).size()) {
                            List originList = rt.getListOfNode(rID);
                            for (Object aoriginList : originList) {
                                RouterNodeMessage extraRNM = (RouterNodeMessage) aoriginList;
                                if (!receiveRT.hasRecordInNode(rID, extraRNM)) {
                                    rt.remove(extraRNM.getRouterNodeID());
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
