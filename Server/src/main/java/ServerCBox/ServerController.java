package ServerCBox;

import CoreCBox.CommandMessage;
import io.netty.channel.socket.SocketChannel;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerController  {
   // private NettyServer nettyServer;
   SocketChannel sChannel;

    public ServerController(SocketChannel sChannel) {
        this.sChannel= sChannel;
    }

    public void log_in(String login, String password){

        if((SqlHandler.getLogin(login,password)).equals(login)) {

            sChannel.writeAndFlush(new CommandMessage(0,login, true));
            System.out.println("логин пароль  верно");
            File directoryUser = new File("Server/Clients/" + login);
            if (directoryUser.isDirectory()) {
                System.out.println("Директория существует");
            } else {
                Path createNewDirectory = Paths.get(directoryUser.getPath());
                try {
                    Files.createDirectory(createNewDirectory);
                    System.out.println("Создана новая директория");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if((SqlHandler.getLogin(login,password)).equals(null)){

            sChannel.writeAndFlush(new CommandMessage(0,login,false));
            System.out.println("логин пароль НЕ верно");
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
            System.out.println("директория Не удалена инфа 100");
        }
    }

    public void rename (String oldNameDirectory, String newNameDirectory){
        File oldNameDir = new File("Server/Clients/" +oldNameDirectory);
        File newNameDir = new File("Server/Clients/" +newNameDirectory);
        System.out.println("old " + oldNameDir);
        System.out.println("new " + newNameDir);
        oldNameDir.renameTo(newNameDir);
        System.out.println("переименование на стороне сервера");
    }



    public void log (String s){
        System.out.println(s);
    }




}
