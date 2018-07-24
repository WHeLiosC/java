// 13/17小数点后第100位的数字是几？
public class FractionalDigit {
    public static void main(String[] args) {
        int d = 13;
        int q = 17;
        int a = 0;

        for(int i = 0; i < 100; i++)
        {
            d *= 10; //被除数乘10作为被除数再与除数做运算
            a = d / q; //小数位
            d = d % q; //新的被除数
        }

        System.out.println(a);
    }

}
