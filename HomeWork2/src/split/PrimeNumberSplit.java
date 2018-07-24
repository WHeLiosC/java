package split;

/**
 * @author 李辉
 * @version 1.0
 */

public class PrimeNumberSplit {
    //把一个数分割成两部分，第一部分为前n位数字，第二部分是剩余的部分数字
    public static int[] SegmentWithDigit(int number, int n) {
        String stringNumber = Integer.toString(number);
        String stringFrontNumber = stringNumber.substring(0, n);
        String stringLastNumber = stringNumber.substring(n);
        //如果后一部分的数字字符串以0开头，把0移到字符串末尾。以0结尾肯定不是素数，也不影响后续的分解
        while (stringLastNumber.startsWith("0")) {
            if (Integer.parseInt(stringLastNumber) == 0)
                break;
            stringLastNumber = stringLastNumber.substring(1, stringLastNumber.length()) + "0";
        }
        int[] result = {Integer.parseInt(stringFrontNumber), Integer.parseInt(stringLastNumber)};
        return result;
    }

    public static boolean isPrimeNumber(int number) {
        //判断一个数是否为素数
        if (number < 2)
            return false;
        for (int i = 2; i <= Math.sqrt(number); i++)
            if (number % i == 0)
                return false;
        return true;
    }

    public static boolean isSplitPrimeNumber(int number1, int number2) {
        boolean flag;
        if (isPrimeNumber(number1) && isPrimeNumber(number2))  //两个数都是素数则返回true
            return true;
        else if (number1 / 10 == 0 && number2 / 10 == 0)  //两个数有一个不是素数，并且两个数都已经是个位数则返回false
            return false;
        else if (number1 / 10 == 0 && number2 / 10 != 0) {
            for (int j = 1; j < Integer.toString(number2).length(); j++) {
                flag = isPrimeNumber(number1) &&
                        isSplitPrimeNumber(SegmentWithDigit(number2, j)[0], SegmentWithDigit(number2, j)[1]);
                if(flag == true)
                    return true;
            }
            return false;
        } else if (number1 / 10 != 0 && number2 / 10 == 0) {
            for (int i = 1; i < Integer.toString(number1).length(); i++){
                flag = isSplitPrimeNumber(SegmentWithDigit(number1, i)[0], SegmentWithDigit(number1, i)[1]) &&
                        isPrimeNumber(number2);
                if(flag == true)
                    return true;
            }
            return false;
        } else if (number1 / 10 != 0 && number2 / 10 != 0) {
            for (int i = 1; i < Integer.toString(number1).length(); i++)
                for (int j = 1; j < Integer.toString(number2).length(); j++){
                    flag = (isSplitPrimeNumber(SegmentWithDigit(number1, i)[0], SegmentWithDigit(number1, i)[1]) &&
                            isSplitPrimeNumber(SegmentWithDigit(number2, j)[0], SegmentWithDigit(number2, j)[1])) ||
                            (isPrimeNumber(number1) &&
                                    isSplitPrimeNumber(SegmentWithDigit(number2, j)[0], SegmentWithDigit(number2, j)[1])) ||
                            (isSplitPrimeNumber(SegmentWithDigit(number1, i)[0], SegmentWithDigit(number1, i)[1]) &&
                                    isPrimeNumber(number2));
                    if(flag == true)
                        return flag;
            }
            return false;
        }
        return true;  //没什么用，不加还报错
    }


    public static void main(String[] args) {
        int count = 0;
        for (int i = 11; i < 10000; i++) {
            if (isPrimeNumber(i)) {
                for (int j = 1; j < Integer.toString(i).length(); j++) {
                    boolean flag = isSplitPrimeNumber(SegmentWithDigit(i, j)[0], SegmentWithDigit(i, j)[1]);
                    if (flag == true) {
                        count++;
                        //System.out.print(i + " ");
                        break;
                    }
                }
            }
        }
        System.out.println(count);
    }
}
