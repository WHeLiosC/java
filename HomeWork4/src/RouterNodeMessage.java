import java.io.Serializable;

/**
 * @author 李辉
 * @version 2018.7.18
 */

public class RouterNodeMessage implements Serializable {
    private String routerNodeID;
    private double pathCost;
    private int port;

    /**
     * 构造函数
     * @param routerNodeID 路由节点ID
     * @param pathCost 路径长度
     * @param port 节点端口
     */
    public RouterNodeMessage(String routerNodeID, double pathCost, int port){
        this.routerNodeID = routerNodeID;
        this.pathCost = pathCost;
        this.port = port;
    }

    /**
     * @return 路由节点ID
     */
    public String getRouterNodeID() {
        return routerNodeID;
    }

    /**
     * @return 路径长度
     */
    public double getPathCost() {
        return pathCost;
    }

    /**
     * @return 节点端口
     */
    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(obj.getClass() == RouterNodeMessage.class){
            RouterNodeMessage rnm = (RouterNodeMessage)obj;
            return routerNodeID.equals(rnm.routerNodeID) &&
                    pathCost == rnm.pathCost && port == rnm.port;
        }
        return false;
    }

    /**
     * 打印路有节点基本信息
     */
    public void showRouterNodeMessage(){
        System.out.printf("路由节点ID: %s   路径长度: %.1f    节点端口: %d\n",
                getRouterNodeID(), getPathCost(), getPort());
    }
}
