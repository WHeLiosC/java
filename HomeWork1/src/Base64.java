import java.util.Vector;

public class Base64 {
    static public String Base64Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" + "0123456789" + "+/";

    public static String encode(byte[] binaryData) {
        String base64 = "";
        int count = binaryData.length;
        for(int i = 0; count >= 3; i = i + 3, count = count - 3)
        {   //所有逻辑右移运算操作数与0xff相与(类型转换问题)
            byte temp = (byte)((binaryData[i] & 0xff) >>> 2);  //第一个原字节逻辑右移2位得到第一个字节
            base64 += Base64Alphabet.charAt(temp);
            //第一个原字节&0x03左移4位 或上 第二个原字节逻辑右移4位得到第二个字节
            temp = (byte)(((binaryData[i] & 0x03) << 4) | (((binaryData[i + 1])  & 0xff) >>> 4));
            base64 += Base64Alphabet.charAt(temp);
            //第二个原字节&0x0f左移2位 或上 第三个原字节逻辑右移6位得到第三个字节
            temp = (byte)(((binaryData[i + 1] & 0x0f) << 2) | (binaryData[i + 2] & 0xff) >>> 6);
            base64 += Base64Alphabet.charAt(temp);
            //第三个原字节&0x3f得到第四个字节
            temp = (byte)(binaryData[i + 2] & 0x3f);
            base64 += Base64Alphabet.charAt(temp);
        }
        if(count == 1)  //多余一个字节，拆分为两个字节，加两个等号
        {
            byte temp = (byte)((binaryData[binaryData.length - 1]  & 0xff) >>> 2);  //多余的原字节逻辑右移2位得到第一个字节
            base64 += Base64Alphabet.charAt(temp);
            temp = (byte)(((binaryData[binaryData.length - 1] & 0x03) << 4));  //多余的原字节&0x03左移4位
            base64 += Base64Alphabet.charAt(temp);
            base64 += "==";
        }
        if(count == 2)  //多余两个字节，拆分为3个字节，加一个等号
        {
            byte temp = (byte)((binaryData[binaryData.length - 2]  & 0xff) >>> 2);  //多余的第一个原字节逻辑右移2位得到第一个字节
            base64 += Base64Alphabet.charAt(temp);
            //多余的第一个原字节&0x03左移4位 或上 多余的第二个原字节逻辑右移4位得到第二个字节
            temp = (byte)((((binaryData[binaryData.length - 2] & 0x03) << 4) | (binaryData[binaryData.length - 1]  & 0xff) >>> 4));
            base64 += Base64Alphabet.charAt(temp);
            //多余的第二个原字节&0x0f左移2位
            temp = (byte)(((binaryData[binaryData.length - 1] & 0x0f) << 2));
            base64 += Base64Alphabet.charAt(temp);
            base64 += "=";
        }
        return base64;
    }

    public static byte[] decode(String s) {
        int size = 0;  //原字节数组长度
        if(s.charAt(s.length() - 1) == '=' && s.charAt(s.length() - 2) != '=')  //一个等号声明数组长度为
            size = 3 * s.length() / 4 - 1;
        else if(s.charAt(s.length() - 1) == '=' && s.charAt(s.length() - 2) == '=')  //两个等号声明数组长度为
            size = 3 * s.length() / 4 - 2;
        else
            size = 3 * s.length() / 4;
        byte[] src = new byte[size];

        for(int i = 0, j = 0; i < s.length(); i = i + 4)
        {
            //第一个字节左移2位 或上 第二个字节逻辑右移4位,得到第一个原字节
            byte temp = (byte)((Base64Alphabet.indexOf(s.charAt(i)) << 2) | (Base64Alphabet.indexOf(s.charAt(i + 1)) >>> 4));
            src[j++] = temp;

            if(s.charAt(i + 2) == '=')  //如果有两个等号
                break;

            //第二个字节左移4位 或上 第三个字节逻辑右移2位，得到第二个原字节
            temp = (byte)((Base64Alphabet.indexOf(s.charAt(i + 1)) << 4) | (Base64Alphabet.indexOf(s.charAt(i + 2)) >>> 2));
            src[j++] = temp;

            if(s.charAt(i + 3) == '=')  //如果有一个等号
                break;

            //第三个字节左移6位 或上 第四个字节，得到第三个原字节
            temp = (byte)((Base64Alphabet.indexOf(s.charAt(i + 2)) << 6) | Base64Alphabet.indexOf(s.charAt(i + 3)));
            src[j++] = temp;
        }
        return src;
    }
    public static void main(String[] args) {
        byte[] a = {1};
        //byte[] a = {-5, 3, 6, 7, -117, -34, 90, 6, 1};
        //byte[] a = "中国梦".getBytes();
        String s = encode(a);
        System.out.println(s);
        byte[] b = decode(s);
        for (int i = 0; i < b.length; i++) {
            System.out.print(b[i] + " ");
        }
        System.out.println();
    }

}

/*
无符号右移问题：
    遇到问题byte temp = (byte)(-7 >>> 2);
    期望得到0x3e(0011 1110)
    结果得到0xfe(1111 1110)
    原因是右移运算时byte类型的-7被强制转换成int型，再无符号右移2位赋值给byte，截取的是1111 1110
 */
