package character;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * @author 李辉
 * @version 1.0
 */

public class MaxAmountOfOne {
    public static void main(String[] args) {
        try {
            Scanner in = new Scanner(Paths.get("src\\character\\a.txt"), "UTF-8");
            char[][] c = new char[8][8];
            for (int i = 0; i < 8; i++) {
                String s = in.nextLine();
                for (int j = 0; j < 8; j++)
                    c[i][j] = s.charAt(j);
            }
            countOne(c);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void countOne(char[][] c) {
        int maxAmount = 0;  //记录最多1的个数
        int count;
        String s = "";  //记录最多1的信息

        for (int i = 0; i < 8; i++) {
            count = 0;  //每一行(列)进行计数前，count清零
            //行计算
            for (int j = 0; j < 8; j++) {
                if (c[i][j] == '1')
                    count++;
                if (c[i][j] == '0' || j == 7) {  //根据情况改变s串内容并将count清零
                    if (maxAmount < count) {  //如果当前连续1的个数比之前的记录多，则s串重新赋值
                        maxAmount = count;
                        if (j != 7)
                            s = "第" + i + "行" + (j - count) + "到" + (j - 1) + "列" + "\n";
                        else  //j == 7时，下标不再是(j - count)到j
                            s = "第" + i + "行" + (j - count + 1) + "到" + "7列" + "\n";
                    } else if (maxAmount == count) {  //如果当前连续1的个数和之前的记录一样，则将这种情况加入s串
                        if (j != 7)
                            s = s + "第" + i + "行" + (j - count) + "到" + (j - 1) + "列" + "\n";
                        else  //j == 7时，下标不再是(j - count)到j
                            s = s + "第" + i + "行" + (j - count + 1) + "到" + "7列" + "\n";
                    }
                    count = 0;  //每一行里1不再连续时，count清零
                }
            }

            //列计算
            for (int j = 0; j < 8; j++) {
                if (c[j][i] == '1')
                    count++;
                if (c[j][i] == '0' || j == 7) {  //根据情况改变s串内容并将count清零
                    if (maxAmount < count) {  //如果当前连续1的个数比之前的记录多，则s串重新赋值
                        maxAmount = count;
                        if (j != 7)
                            s = "第" + i + "列" + (j - count) + "到" + (j - 1) + "行" + "\n";
                        else  //j == 7时，下标不再是(j - count)到j
                            s = "第" + i + "列" + (j - count + 1) + "到" + "7行" + "\n";
                    } else if (maxAmount == count) {  //如果当前连续1的个数和之前的记录一样，则将这种情况加入s串
                        if (j != 7)
                            s = s + "第" + i + "列" + (j - count) + "到" + (j - 1) + "行" + "\n";
                        else  //j == 7时，下标不再是(j - count)到j
                            s = s + "第" + i + "列" + (j - count + 1) + "到" + "7行" + "\n";
                    }
                    count = 0;  //每一列里1不再连续时，count清零
                }
            }
        }


        //正对角线计算
        for (int i = maxAmount - 1; i < 8; i++) {
            //第7行maxAmount-1到7所在正对角线检测
            count = 0;
            for (int j = 0; j < 8; j++) {
                if (i - j >= 0 && c[7 - j][i - j] == '1')
                    count++;
                else {
                    if (count > maxAmount) {
                        maxAmount = count;
                        s = "第7行" + i + "列所在正对角线上\n";
                    } else if (count == maxAmount)
                        s += "第7行" + i + "列所在正对角线上\n";
                    count = 0;
                }
            }
        }
        for (int i = 1; i <= 8 - maxAmount; i++) {
            //第0行1到8-maxAmount所在正对角线检测
            count = 0;
            for (int j = 0; j < 8; j++) {
                if (i + j < 8 && c[j][i + j] == '1')
                    count++;
                else {
                    if (count > maxAmount) {
                        maxAmount = count;
                        s = "(0," + i + ")所在正对角线上\n";
                    } else if (count == maxAmount)
                        s = s + "(0," + i + ")所在正对角线上\n";
                    count = 0;
                }
            }
        }

        //副对角线计算
        for (int i = 1; i <= 8 - maxAmount; i++) {
            //第7行1到8 - maxAmount所在副对角线检测
            count = 0;
            for (int j = 0; j < 8; j++) {
                if (7 - j >= 0 && i + j < 8 && c[7 - j][i + j] == '1')
                    count++;
                else {
                    if (count > maxAmount) {
                        maxAmount = count;
                        s = "(7," + i + ")所在副对角线上\n";
                    } else if (count == maxAmount)
                        s = s + "(7," + i + ")所在副对角线上\n";
                    count = 0;
                }
            }
        }
        for (int i = maxAmount - 1; i < 8; i++) {
            //第0行maxAmount-1到8所在副对角线检测
            count = 0;
            for (int j = 0; j < 8; j++) {
                if (i - j >= 0 && c[j][i - j] == '1')
                    count++;
                else {
                    if (count > maxAmount) {
                        maxAmount = count;
                        s = "(0," + i + ")所在副对角线上\n";
                    } else if (count == maxAmount)
                        s = s + "(0," + i + ")所在副对角线上\n";
                    count = 0;
                }
            }

        }
        System.out.print(s);
        System.out.println("maxAmount = " + maxAmount);
    }
}
