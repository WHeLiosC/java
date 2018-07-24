import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author 李辉
 * @version 2018.7.23
 */

public class ShowPathThread extends Thread {
    private Map showMap;

    public ShowPathThread(Map showMap) {
        this.showMap = showMap;
    }

    private void show() {
        if(showMap == null){
            System.out.println("没有其他节点活着");
            System.out.println("*****************************************************");
            return;
        }
        Set set = showMap.keySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String pathStr = (String) it.next();
            double cost = (double) showMap.get(pathStr);
            char nodeID = pathStr.charAt(pathStr.length() - 1);
            if(cost != Double.MAX_VALUE)  // 如果为Double.MAX_VALUE, 说明此时已经不可达了
                System.out.printf("least-cost path to node %c: %s and the cost is %.1f\n",
                    nodeID, pathStr, cost);
        }
        System.out.println("*****************************************************");
    }

    @Override
    public void run() {
        show();
    }
}
