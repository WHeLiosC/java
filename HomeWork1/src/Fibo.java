public class Fibo {
    public static void main(String[] args) {
        Fibo f = new Fibo();

        long startTime1 = System.nanoTime();
        System.out.println(f.fibo1(9)); // 这两种方法哪种效率更高？
        long endTime1 = System.nanoTime();
        System.out.println("递归时间：" + (endTime1-startTime1) + "ns");

        long startTime2 = System.nanoTime();
        System.out.println(f.fibo2(9));
        long endTime2 = System.nanoTime();
        System.out.println("循环时间：" + (endTime2-startTime2) + "ns");
    }

    public int fibo1(int n) { // 使用方法（函数）递归来实现
        if(n <= 0)
            return -1;
        if(n == 1)
            return 1;
        if(n == 2)
            return 1;
        return fibo1(n-1) + fibo1(n - 2);
    }

    public int fibo2(int n) { // 使用循环来实现
        if(n <= 0)
            return -1;
        if(n == 1)
            return 1;
        if(n == 2)
            return 1;
        int a1 = 1;
        int a2 = 1;
        int an = 0;  //要求的第n项
        for(int i = 2; i < n; i++)
        {
            an = a1 + a2;
            a1 = a2;  //a1更新为前两项和
            a2 = an;  //a2更新为前两项和
        }
        return an;
    }
}