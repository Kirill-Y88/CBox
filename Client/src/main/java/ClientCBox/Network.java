package ClientCBox;

import CoreCBox.CommandMessage;
import CoreCBox.FileMessage;

import io.netty.handler.codec.serialization.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

public class Network {
Thread thread;


//версия 2
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;
    public ListView<String> listView;


    String path;
    byte [] bArray =  new byte[3072];
    int countPack = 0;
    int indexArray = 0;

    public Network (String host, String port, Controller controller){

        //версия 2
        try {
            Socket socket = new Socket(host, Integer.parseInt(port) );
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());

            new Thread(() -> {
                while (true) {
                    try {
                        CommandMessage message = (CommandMessage) is.readObject();
                        if(message.getCodeOperation() == 0 && message.isAutorization() == true){
                            controller.log_in_auth(message.getStringPath());
                        } else if(message.isAutorization() == false){
                            System.out.println("Неверный логин пароль");
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Не верный логин / пароль", ButtonType.OK);
                            alert.showAndWait();
                        }


                        listView.getItems().add(message.toString());
                    } catch (Exception e) {
                        System.out.println(e);
                        break;
                    }
                }
            }).start();
        } catch (Exception e) {
            System.out.println(e);
        }


    }


    public boolean sendFile(String filePath){
        LocalDateTime sendAt = LocalDateTime.now();
        //вариант 2
        try (FileInputStream fis = new FileInputStream("Client/Clients/" + filePath)) {
            indexArray = 0;
            countPack = 0;
            while ( (indexArray = fis.read(bArray)) > 0){
                os.writeObject(
                        new FileMessage(
                                filePath,
                                "fish",
                                sendAt,
                                bArray,
                                countPack,
                                indexArray,
                                false)
                );
                os.flush();
                countPack++;
            }
            indexArray = 0;
            countPack++;
            os.writeObject(
                    new FileMessage(
                            filePath,
                            "fish",
                            sendAt,
                            new byte[]{},
                            countPack,
                            indexArray,
                            true)
            );
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    return true;
    }


    public boolean sendCommand(CommandMessage commandMessage) {
        try {
            os.writeObject(commandMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    public void disconnect (){
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }

}
