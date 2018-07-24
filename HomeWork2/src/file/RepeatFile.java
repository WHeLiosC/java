package file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.io.File;

public class RepeatFile {
    private static List fileList = new ArrayList<MyFile>();

    public void getAllFile(Path filePath) throws IOException {
        Stream<Path> fileStream = Files.list(filePath);

        fileStream.forEach(path -> {
            File file = path.toFile();
            if(file.isFile())
                fileList.add(new MyFile(file.getName(), file.getAbsolutePath(), file.length()));
            else if(file.isDirectory()) {
                try {
                    getAllFile(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public List<MyFile> getRepeatFile(){
        List repeatFileList = new ArrayList<MyFile>();
        List fileListCopy = new ArrayList<MyFile>(fileList);

        for(Object file: fileList)
        {
            fileListCopy.remove(file);
            if(fileListCopy.contains(file) || repeatFileList.contains(file))
                repeatFileList.add(file);
        }
        return repeatFileList;
    }

    public static void main(String[] args)throws IOException{
        RepeatFile rf = new RepeatFile();
        rf.getAllFile(Paths.get(args[0]));
        List repeatFileList = rf.getRepeatFile();
        if(repeatFileList.size() == 0)
            System.out.println("没有重复文件");
        else
            for(Object file: repeatFileList)
                ((MyFile)file).show();
    }
}
