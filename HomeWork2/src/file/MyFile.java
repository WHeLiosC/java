package file;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MyFile {
    private String fileName;
    private Path filePath;
    private long fileLength;

    public MyFile(String fileName, String filePath, long fileLength){
        this.fileName = fileName;
        this.filePath = Paths.get(filePath);
        this.fileLength = fileLength;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(this.getClass() == obj.getClass()){
            MyFile f = (MyFile) obj;
            return f.getName().equals(this.getName()) &&
                    f.getFileLength() == this.getFileLength();
        }
        return false;
    }

    public String getName() {
        return fileName;
    }

    public String getPath() {
        return filePath.toAbsolutePath().toString();
    }

    public long getFileLength() {
        return fileLength;
    }

    public void show(){
        System.out.println("file name:"+getName() +";file path:"+getPath()+";file size:"+getFileLength());
    }
}
