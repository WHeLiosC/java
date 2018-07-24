package huffman;

import java.io.*;
import java.util.*;

/**
 * @author lihui 201692324
 * @version 2018.7.11
 **/

public class TextZip {

    //ID, 该学号的值需要修改!
    private static final String ID = "201692324";
    private static Map codeMap = new HashMap<Character, String>();  //字符及对应的编码信息

    /**
     * This method generates the huffman tree for the text: "abracadabra!"
     *
     * @return the root of the huffman tree
     */

    public static TreeNode abracadbraTree() {
        TreeNode n0 = new TreeNode(new CharFreq('!', 1));
        TreeNode n1 = new TreeNode(new CharFreq('c', 1));
        TreeNode n2 = new TreeNode(new CharFreq('\u0000', 2), n0, n1);
        TreeNode n3 = new TreeNode(new CharFreq('r', 2));
        TreeNode n4 = new TreeNode(new CharFreq('\u0000', 4), n3, n2);
        TreeNode n5 = new TreeNode(new CharFreq('d', 1));
        TreeNode n6 = new TreeNode(new CharFreq('b', 2));
        TreeNode n7 = new TreeNode(new CharFreq('\u0000', 3), n5, n6);
        TreeNode n8 = new TreeNode(new CharFreq('\u0000', 7), n7, n4);
        TreeNode n9 = new TreeNode(new CharFreq('a', 5));
        TreeNode n10 = new TreeNode(new CharFreq('\u0000', 12), n9, n8);
        return n10;
    }

    /**
     * This method decompresses a huffman compressed text file.  The compressed
     * file must be read one bit at a time using the supplied BitReader, and
     * then by traversing the supplied huffman tree, each sequence of compressed
     * bits should be converted to their corresponding characters.  The
     * decompressed characters should be written to the FileWriter
     *
     * @param br the BitReader which reads one bit at a time from the
     *           compressed file
     *           huffman the huffman tree that was used for compression, and
     *           hence should be used for decompression
     *           fw      a FileWriter for storing the decompressed text file
     */
    public static void decompress(BitReader br, TreeNode huffman, FileWriter fw) throws Exception {

        // IMPLEMENT THIS METHOD
        String result = "";

        TreeNode tempNode = huffman;
        while (br.hasNext()) {
            if (tempNode.isLeaf()) {
                CharFreq temp = (CharFreq) tempNode.getItem();
                result += temp.getChar();
                tempNode = huffman;
            } else {
                if (br.next())
                    tempNode = tempNode.getRight();
                else
                    tempNode = tempNode.getLeft();
            }
        }

        PrintWriter pw = new PrintWriter(fw);
        pw.println(result);
        pw.close();
    }

    /**
     * This method traverses the supplied huffman tree and prints out the
     * codes associated with each character
     *
     * @param t the root of the huffman tree to be traversed
     *          code a String used to build the code for each character as
     *          the tree is traversed recursively
     */
    public static void traverse(TreeNode t, String code) {

        // IMPLEMENT THIS METHOD
        if (t.isLeaf()) {
            CharFreq temp = (CharFreq) t.getItem();
            System.out.println(temp.getChar() + " : " + code);
        }
        if (t.getLeft() != null)
            traverse(t.getLeft(), code + "0");
        if (t.getRight() != null)
            traverse(t.getRight(), code + "1");
    }

    public static void getCodeMap(TreeNode t, String code) {
        if (t.isLeaf()) {
            CharFreq temp = (CharFreq) t.getItem();
            codeMap.put(temp.getChar(), code);
        }
        if (t.getLeft() != null)
            getCodeMap(t.getLeft(), code + "0");
        if (t.getRight() != null)
            getCodeMap(t.getRight(), code + "1");
    }

    /**
     * This method removes the TreeNode, from an ArrayList of TreeNodes,  which
     * contains the smallest item.  The items stored in each TreeNode must
     * implement the Comparable interface.
     * The ArrayList must contain at least one element.
     *
     * @param a an ArrayList containing TreeNode objects
     * @return the TreeNode in the ArrayList which contains the smallest item.
     * This TreeNode is removed from the ArrayList.
     */
    public static TreeNode removeMin(ArrayList a) {
        int minIndex = 0;
        for (int i = 0; i < a.size(); i++) {
            TreeNode ti = (TreeNode)a.get(i);
            TreeNode tmin = (TreeNode) a.get(minIndex);
            if (((Comparable) (ti.getItem())).compareTo(tmin.getItem()) < 0)
                minIndex = i;
        }
        TreeNode n = (TreeNode) a.remove(minIndex);
        return n;
    }

    /**
     * This method counts the frequencies of each character in the supplied
     * FileReader, and produces an output text file which lists (on each line)
     * each character followed by the frequency count of that character.  This
     * method also returns an ArrayList which contains TreeNodes.  The item stored
     * in each TreeNode in the returned ArrayList is a CharFreq object, which
     * stores a character and its corresponding frequency
     *
     * @param fr the FileReader for which the character frequencies are being
     *           counted
     *           pw the PrintWriter which is used to produce the output text file
     *           listing the character frequencies
     * @return the ArrayList containing TreeNodes.  The item stored in each
     * TreeNode is a CharFreq object.
     */
    public static ArrayList countFrequencies(FileReader fr, PrintWriter pw) throws Exception {

        // IMPLEMENT THIS METHOD
        BufferedReader br = new BufferedReader(fr);

        ArrayList treeNodeList = new ArrayList<TreeNode>();
        ArrayList characterList = new ArrayList<Character>();
        ArrayList freqsList = new ArrayList<Integer>();

        int ifEnd = br.read();
        while (ifEnd != -1) {
            char temp = (char) ifEnd;
            if (!characterList.contains(temp)) {
                characterList.add(temp);
                freqsList.add(1);
            } else {
                int freq = (int) freqsList.get(characterList.indexOf(temp));
                freqsList.set(characterList.indexOf(temp), ++freq);
            }
            ifEnd = br.read();
        }
        for (int i = 0; i < characterList.size(); i++) {
            treeNodeList.add(new TreeNode(new CharFreq((char) characterList.get(i), (int) freqsList.get(i))));
            pw.println(characterList.get(i) + " " + freqsList.get(i));
        }

        return treeNodeList;
    }

    /**
     * This method builds a huffman tree from the supplied ArrayList of TreeNodes.
     * Initially, the items in each TreeNode in the ArrayList store a CharFreq object.
     * As the tree is built, the smallest two items in the ArrayList are removed,
     * merged to form a tree with a CharFreq object storing the sum of the frequencies
     * as the root, and the two original CharFreq objects as the children.  The right
     * child must be the second of the two elements removed from the ArrayList (where
     * the ArrayList is scanned from left to right when the minimum element is found).
     * When the ArrayList contains just one element, this will be the root of the
     * completed huffman tree.
     *
     * @param trees the ArrayList containing the TreeNodes used in the algorithm
     *              for generating the huffman tree
     * @return the TreeNode referring to the root of the completed huffman tree
     */
    public static TreeNode buildTree(ArrayList trees) throws IOException {

        // IMPLEMENT THIS METHOD
        while (trees.size() > 1) {
            TreeNode t1 = removeMin(trees);
            CharFreq cf1 = (CharFreq) t1.getItem();
            TreeNode t2 = removeMin(trees);
            CharFreq cf2 = (CharFreq) t2.getItem();
            TreeNode newNode = new TreeNode(new CharFreq('\u0000', cf1.getFreq() + cf2.getFreq()), t1, t2);
            trees.add(newNode);
        }
        return (TreeNode)trees.get(0);
    }

    /**
     * This method compresses a text file using huffman encoding.  Initially, the
     * supplied huffman tree is traversed to generate a lookup table of codes for
     * each character.  The text file is then read one character at a time, and
     * each character is encoded by using the lookup table.  The encoded bits for
     * each character are written one at a time to the specified BitWriter.
     *
     * @param fr the FileReader which contains the text file to be encoded
     *           huffman the huffman tree that was used for compression, and
     *           hence should be used for decompression
     *           bw      the BitWriter used to write the compressed bits to file
     */
    public static void compress(FileReader fr, TreeNode huffman, BitWriter bw) throws Exception {

        // IMPLEMENT THIS METHOD
        getCodeMap(huffman, "");

        BufferedReader br = new BufferedReader(fr);
        int ifEnd = br.read();
        while (ifEnd != -1) {
            String temp = (String) codeMap.get((char) ifEnd);
            for (int i = 0; i < temp.length(); i++)
                bw.writeBit(temp.charAt(i) - 48);
            ifEnd = br.read();
        }

        br.close();
    }

    /**
     * This method reads a frequency file (such as those generated by the
     * countFrequencies() method) and initialises an ArrayList of TreeNodes
     * where the item of each TreeNode is a CharFreq object storing a character
     * from the frequency file and its corresponding frequency.  This method provides
     * the same functionality as the countFrequencies() method, but takes in a
     * frequency file as parameter rather than a text file.
     *
     * @param inputFreqFile the frequency file which stores characters and their
     *                      frequency (one character per line)
     * @return the ArrayList containing TreeNodes.  The item stored in each
     * TreeNode is a CharFreq object.
     */
    public static ArrayList readFrequencies(String inputFreqFile) throws Exception {

        // IMPLEMENT THIS METHOD
        ArrayList treeNodeList = new ArrayList<TreeNode>();

        DataInputStream freqFile = new DataInputStream(new FileInputStream(inputFreqFile));
        int ifEnd = freqFile.read();
        while (ifEnd != -1) {
            char charTemp = (char) ifEnd;
            freqFile.read();  // 跳过空格

            // 读出频次并跳过\r
            String s = "";
            int i = freqFile.read();
            while ((char)i != '\r') {
                s += (char) i;
                i = freqFile.read();
            }
            int freqTemp = Integer.parseInt(s);
            treeNodeList.add(new TreeNode(new CharFreq(charTemp, freqTemp)));

            freqFile.read();  // 跳过\n
            ifEnd = freqFile.read();
        }

        return treeNodeList;
    }

    /**
     * 这一部分实现压缩率的计算
     */
    public static void calcCompressedRatio(String originalFileName, String compressedFileName) {
        File originalFile = new File(originalFileName);
        long originalFileSize = originalFile.length();
        File compressedFile = new File(compressedFileName);
        long compressedFileSize = compressedFile.length();
        double compressedRatio = ((double) compressedFileSize / originalFileSize) * 100;

        System.out.println("Size of the compressed file: " + compressedFileSize + " bytes");
        System.out.println("Size of the original file: " + originalFileSize + " bytes");
        System.out.println("Compressed ratio: " + compressedRatio + "%");
    }

    /**
     * /* This TextZip application should support the following command line flags:
     * <p>
     * QUESTION 2 PART 1
     * =================
     * -a : this uses a default prefix code tree and its compressed
     * file, "a.txz", and decompresses the file, storing the output
     * in the text file, "a.txt".  It should also print out the size
     * of the compressed file (in bytes), the size of the decompressed
     * file (in bytes) and the compression ratio
     * <p>
     * QUESTION 2 PART 2
     * =================
     * -f : given a text file (args[1]) and the name of an output frequency file
     * (args[2]) this should count the character frequencies in the text file
     * and store these in the frequency file (with one character and its
     * frequency per line).  It should then build the huffman tree based on
     * the character frequencies, and then print out the prefix code for each
     * character
     * <p>
     * QUESTION 2 PART 3
     * =================
     * -c : given a text file (args[1]) and the name of an output frequency file
     * (args[2]) and the name of the output compressed file (args[3]), this
     * should compress file
     * <p>
     * QUESTION 2 PART 4
     * =================
     * -d : given a compressed file (args[1]) and its corresponding frequency file
     * (args[2]) and the name of the output decompressed text file (args[3]),
     * this should decompress the file
     */

    public static void main(String[] args) throws Exception {

        if (args[0].equals("-a")) {
            BitReader br = new BitReader("a.txz");
            FileWriter fw = new FileWriter("a.txt");

            // Get the default prefix code tree
            TreeNode tn = abracadbraTree();

            // Decompress the default file "a.txz"
            decompress(br, tn, fw);

            // Close the output file
            fw.close();
            // Output the compression ratio
            // Write your own implementation here.
            calcCompressedRatio("a.txt", "a.txz");

        } else if (args[0].equals("-f")) {  // -f 原文件名 频率文件名
            FileReader fr = new FileReader(args[1]);
            PrintWriter pw = new PrintWriter(new FileWriter(args[2]));

            // Calculate the frequencies
            ArrayList trees = countFrequencies(fr, pw);

            // Close the files
            fr.close();
            pw.close();

            // Build the huffman tree
            TreeNode n = buildTree(trees);

            // Display the codes
            traverse(n, "");
            getCodeMap(n, "");
            Set set = codeMap.keySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Object key = it.next();
                Object value = codeMap.get(key);
                System.out.println(key + " " + value);
            }
        } else if (args[0].equals("-c")) {  // -c 原文件名 频率文件名 压缩后文件名

            FileReader fr = new FileReader(args[1]);
            PrintWriter pw = new PrintWriter(new FileWriter(args[2]));
            ArrayList trees = countFrequencies(fr, pw);

            fr.close();
            pw.close();

            TreeNode n = buildTree(trees);

            // IMPLEMENT NEXT
            // Finish the compress function here
            fr = new FileReader(args[1]);
            BitWriter bw = new BitWriter(new File(args[3]));
            compress(fr, n, bw);
            fr.close();
            bw.close();


            // then output the compression ratio
            // Write your own implementation here.
            System.out.println(args[1]+" 文件压缩完成！压缩后文件名为 "+ args[3]);
            calcCompressedRatio(args[1], args[3]);

        } else if (args[0].equals("-d")) {  // -d 压缩文件名 频率文件名 解压后文件名
            ArrayList a = readFrequencies(args[2]);
            TreeNode tn = buildTree(a);
            BitReader br = new BitReader(args[1]);
            FileWriter fw = new FileWriter(args[3]);
            decompress(br, tn, fw);
            fw.close();

            // Output the compression ratio
            // Write your own implementation here.
            System.out.println(args[1]+" 压缩文件解压完成！解压后文件名为 "+ args[3]);
            calcCompressedRatio(args[3], args[1]);

        }
    }
}
