public class Dijkstra {
    public static void main(String[] args) {
        int[][] graph =
                {
                        {0, 2, 5, 1, Integer.MAX_VALUE, Integer.MAX_VALUE},
                        {2, 0, 3, 2, Integer.MAX_VALUE, Integer.MAX_VALUE},
                        {5, 3, 0, 3, 1, 5},
                        {1, 2, 3, 0, 1, Integer.MAX_VALUE},
                        {Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 1, 0, 2},
                        {Integer.MAX_VALUE, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 2, 0}
                };
        char[] vertex = {'A', 'B', 'C', 'D', 'E', 'F'};
        int[] cost = {0, 2, 5, 1, Integer.MAX_VALUE, Integer.MAX_VALUE};  //记录路径长度
        int[] path = {0, 0, 0, 0, 0, 0};  //记录路径信息
        boolean[] flag = {true, false, false, false, false, false};  //标志位
        int record = 0;
        for(int j = 0; j < cost.length; j++)
        {
            int min = Integer.MAX_VALUE;
            for(int i = 1; i < cost.length; i++)
                if(cost[i] < min && flag[i] == false)
                {
                    min = cost[i];
                    record = i;
                }
            flag[record] = true;
            for(int i = 0; i < cost.length; i++)
            {
                if(cost[record] + graph[record][i] < cost[i] && graph[record][i] != Integer.MAX_VALUE && flag[i] == false)  //防止溢出
                {
                    cost[i] = cost[record] + graph[record][i];
                    path[i] = record;
                }
            }
        }
        for(int i = 0; i < cost.length; i++)
            System.out.print(cost[i] + " ");
        System.out.println();
        for(int i = 0; i < path.length; i++)
            System.out.print(vertex[i] + " ");
        System.out.println();
        for(int i = 0; i < path.length; i++)
            System.out.print(vertex[path[i]] + " ");
    }
}
