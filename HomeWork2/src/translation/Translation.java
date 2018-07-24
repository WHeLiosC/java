package translation;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author 李辉
 * @version 1.0
 */

public class Translation {
    static Map libMap = new HashMap<String, String>();  //用Map来实现重复单词的覆盖和后期数据的追加

    public static void createLib() throws IOException {
        System.out.println("请输入要单词及解释，输入#结束输入");

        String record;

        FileOutputStream fos = new FileOutputStream("src\\translation\\lib.txt");
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        PrintWriter pwr = new PrintWriter(osw);

        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            record = in.nextLine();
            if (record.equals("#"))
                break;
            libMap.put(record.split(" ")[0], record.split(" ")[1]);
        }

        Set set = libMap.keySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = (String) libMap.get(key);
            pwr.println(key + " " + value);
        }

        pwr.flush();
        pwr.close();
        osw.close();
        fos.close();
    }

    public static void Menu() throws IOException {
        int choice;
        while (true) {
            System.out.println("输入词库结束\n查询单词请按1，更新词库请按2，退出程序请按3");
            Scanner choiceIn = new Scanner(System.in);
            choice = choiceIn.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("输入要查询的单词");
                    Scanner key = new Scanner(System.in);
                    System.out.println(Seek(key.next()));
                    break;
                case 2:
                    createLib();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("输入错误，请重新输入");
                    break;
            }
        }
    }

    public static String Seek(String key) throws IOException {
        Scanner lib = new Scanner(Paths.get("src\\translation\\lib.txt"), "UTF-8");
        while (lib.hasNextLine()) {
            String s = lib.nextLine();
            if (s.contains(key))
                return s.split(" ")[1];
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        createLib();

        Menu();
    }
}
