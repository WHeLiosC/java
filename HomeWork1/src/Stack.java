
//下面的程序为一个整数栈，请实现整数出栈功能
public class Stack {

    private SingleLinkedList list = new SingleLinkedList();

    public boolean empty() {
        return list.getSize()==0;
    }

    public int size() {
        return list.getSize();
    }

    //请实现pop函数，从栈顶返回数据(弹出数据)，要求必须是先进后出，FILO结构
    public int pop() {
        if(empty())  //栈空则返回-1
            return -1;
        if(list.head == list.tail)  //如果就一个数据，返回数据值，链表清空
        {
            int temp = list.head.data;
            list.clear();
            return temp;
        }
        Node n = list.head;  //不止一个元素，进行下列操作
        while(n != null)
        {
            if(n.next == list.tail)
                break;
            n = n.next;
        }
        int temp = list.tail.data;
        list.tail = n;
        list.size--;
        return temp;
    }

    //数据进栈操作
    public void push(int data) {// 向链表末尾追加元素，append操作
        list.add(data);
    }

    //栈数据复制
    public Stack clone() {
        Stack s = new Stack();
        for(int i=0;i<list.getSize();i++) {
            s.list.add(list.get(i));
        }
        return s;
    }

    public static void main(String[] args) {
        Stack s1 = new Stack();
        s1.push(1);
        s1.push(2);
        s1.push(3);

        Stack s2 = s1.clone();

        System.out.println(s1.pop());
        System.out.println(s1.pop());
        System.out.println(s1.pop());

        System.out.println(s1.size());

    }
}
