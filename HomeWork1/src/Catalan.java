
public class Catalan {

    public static int answers = 0;

    //请实现go函数
    public static void go(Deque from, Deque to, Stack s) {
        if (from.size() == 0 && s.empty()) {  //如果from队列和s栈都为空，那么结果队列满，为一种解答
            while (to.size() != 0) {
                System.out.print(to.getFirst());
                to.removeFirst();
            }
            System.out.println();
            answers++;
        } else {  //否则只有两种操作选择，入栈或出栈
            if (from.size() > 0 /* && s.size() < 4 */)  //入栈(注释内为栈大小为4的情况)
            {
                Stack sCopy = s.clone();
                Deque toCopy = to.clone();
                Deque fromCopy = from.clone();

                sCopy.push(fromCopy.getFirst());
                fromCopy.removeFirst();

                go(fromCopy, toCopy, sCopy);
            }

            if (!s.empty())  //出栈
            {
                Stack sCopy = s.clone();
                Deque toCopy = to.clone();
                Deque fromCopy = from.clone();

                toCopy.addLast(sCopy.pop());

                go(fromCopy, toCopy, sCopy);
            }
        }

    }

    public static void main(String[] args) {
        Deque from = new Deque();
        Deque to = new Deque();
        Stack s = new Stack();

        for (int i = 1; i <= 7; i++) {
            from.addLast(i);
        }

        go(from, to, s);

        System.out.println(answers);
    }

}
