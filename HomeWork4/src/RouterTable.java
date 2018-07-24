import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author 李辉
 * @version 2018.7.21
 */
public class RouterTable implements Serializable {
    private HashMap<String, List<RouterNodeMessage>> table;

    /**
     * 构造函数
     */
    public RouterTable(){
        table = new HashMap<>();
    }

    public HashMap<String, List<RouterNodeMessage>> getTable() {
        return table;
    }

    /**
     * 在路由表中添加一个节点的信息
     * @param routerNodeID 要添加的节点ID
     * @param rnmList 添加的节点ID的有关信息
     */
    public void add(String routerNodeID, List<RouterNodeMessage> rnmList){
        table.put(routerNodeID, rnmList);
    }

    /**
     * 删除某个节点及其所有相关的信息
     * @param routerNodeID 要删除的节点ID
     */
    public void remove(String routerNodeID){
        // 删除路由表中这个节点
        table.remove(routerNodeID);

        // 删除其他节点中所有与该节点有关的信息
        Set set = table.keySet();
        for (Object aSet : set) {
            String rID = (String) (aSet);
            List<RouterNodeMessage> rnmList = table.get(rID);
            Iterator it = rnmList.iterator();
            while(it.hasNext()) {
                RouterNodeMessage rnm = (RouterNodeMessage)it.next();
                if (rnm.getRouterNodeID().equals(routerNodeID))
                    // rnmList.remove(rnm); 会出现ConcurrentModificationException
                    it.remove();
            }
        }
    }

    /**
     * 判断路由表中是否存在给定的节点
     * @param routerNodeID 给定的节点
     * @return Boolean
     */
    public boolean contains(String routerNodeID){
        Set set = table.keySet();
        for (Object aSet : set) {
            String rID = (String) aSet;
            if (rID.equals(routerNodeID))
                return true;
        }
        return false;
    }

    /**
     * 打印路由表
     */
    public void showTable(){
        Set set = table.keySet();
        for (Object aSet : set) {
            String rID = (String) aSet;
            System.out.println(rID);
            List rnmList = table.get(rID);
            for (Object aRnmList : rnmList) {
                RouterNodeMessage rnm = (RouterNodeMessage) aRnmList;
                rnm.showRouterNodeMessage();
            }
        }
    }

    /**
     * 判断某个节点里有没有某条给定的记录
     */
    public boolean hasRecordInNode(String routerNodeID, RouterNodeMessage rnm){
        Set set = table.keySet();
        for(Object aSet:set){
            String rID = (String) aSet;
            if(rID.equals(routerNodeID)){
                List rnmList = table.get(rID);
                for(Object arnmList: rnmList)
                    if (rnm.equals(arnmList))
                        return true;
            }
        }
        return false;
    }

    /**
     * 返回给定节点的直接相连路径信息
     * @param routerNodeID 给定节点
     * @return 相连节点链表
     */
    public List<RouterNodeMessage> getListOfNode(String routerNodeID){
        Set set = table.keySet();
        for(Object aSet: set){
            String rID = (String)aSet;
            if(rID.equals(routerNodeID))
                return table.get(rID);
        }
        return null;
    }
}