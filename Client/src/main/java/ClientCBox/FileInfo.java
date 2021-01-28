package ClientCBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo {
    public String file_name;
    public long file_size;

   public FileInfo(Path path) {
       try {
           this.file_name = path.getFileName().toString();
           this.file_size = Files.size(path);
       } catch (IOException e) {
           e.printStackTrace();
           System.out.println("Файл не найден");
       }
   }

    public String getFile_name() { return file_name;  }

    public long getFile_size() { return file_size; }
}
