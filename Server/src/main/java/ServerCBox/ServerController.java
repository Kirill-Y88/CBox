package ServerCBox;

import CoreCBox.CommandMessage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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


    public void create_directory(String stringNewDirectory) {
        System.out.println(" create " + stringNewDirectory);
        File newDirectory = new File("Server/Clients/" + stringNewDirectory);
        if (newDirectory.isDirectory()) {
            System.out.println("Директория существует");
        } else {
                try {
                    Path createNewDirectory = Paths.get(newDirectory.getPath());
                    Files.createDirectory(createNewDirectory);
                    System.out.println("директория " + newDirectory + " создана");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("директория " + newDirectory + " не создана");
                }

        }
    }

    public void delete_directory (String deleteDirectory) {
        Path pathDelete = Paths.get("Server/Clients/" + deleteDirectory);
        try {
            Files.delete(pathDelete);
            System.out.println("директория удалена инфа 100");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void log (String s){
        System.out.println(s);
    }



}
