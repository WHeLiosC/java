package primenumber;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author 李辉
 * @version 1.0
 */

public class PrimeNumber {
    public static void main(String[] args) throws IOException {
        PrintWriter fw = new PrintWriter("src\\primenumber\\primeNumber.txt");

        for (int i = 0; i < 10000; i++)
            if (isPrimeNumber(i)) fw.println(i);

        fw.close();
    }

    public static boolean isPrimeNumber(int number) {
        if (number < 2)
            return false;
        for (int i = 2; i <= Math.sqrt(number); i++)
            if (number % i == 0)
                return false;
        return true;
    }
}
