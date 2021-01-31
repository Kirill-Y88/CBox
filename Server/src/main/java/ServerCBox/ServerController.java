package ServerCBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerController  {
    private NettyServer nettyServer;


    /*public ServerController(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }*/

    public void log_in(String login){
        File directoryUser = new File("Server/Clients/" + login);
        if (directoryUser.isDirectory()){
            System.out.println("Директория существует");
        }else {
            Path createNewDirectory = Paths.get(directoryUser.getPath());
            try {
                Files.createDirectory(createNewDirectory);
                System.out.println("Создана новая директория");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }







}
