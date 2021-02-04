package ClientCBox;

import CoreCBox.CommandMessage;
import CoreCBox.FileMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

public class Network {
Thread thread;
SocketChannel sChannel;

//версия 2
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;
    public ListView<String> listView;


    String path;
    byte [] bArray =  new byte[3072];
    int countPack = 0;
    int indexArray = 0;

    public Network (String host, String port, Controller controller){

        /*//версия 1
        try {
            thread = new Thread(() -> {
                NioEventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    Bootstrap b = new Bootstrap();
                    b.group(workerGroup)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    sChannel = socketChannel;
                                    socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                            new ObjectEncoder()
                                    );
                                }
                            });
                    ChannelFuture future = b.connect(host, Integer.parseInt(port)).sync();
                    future.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    workerGroup.shutdownGracefully();
                }
            });
            thread.start();

        } catch (Exception e) {
            System.out.println(e);
        }*/

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
      //  byte [] bArray =  new byte[3072];
        LocalDateTime sendAt = LocalDateTime.now();
        /*//вариант 1
        int countPack = 0;
        int indexArray = 0;

        File file = new File("Client/Clients/" + filePath);

        long fileSize = file.length();
        int quantity = (int)fileSize/3072 +1;

        try (FileInputStream fis = new FileInputStream("Client/Clients/" + filePath)) {

            while ( (indexArray = fis.read(bArray)) != -1){
                sChannel.writeAndFlush
                        (
                                new FileMessage(
                                        filePath,
                                        "fish",
                                        sendAt,
                                        bArray,
                                        countPack,
                                        indexArray,
                                        false,
                                        quantity)
                        );
                countPack++;
            }
            indexArray = 0;
            sChannel.writeAndFlush(
                    new FileMessage(
                            filePath,
                            "Fish",
                            sendAt,
                            new byte[]{},
                            countPack,
                            indexArray,
                            true,
                            quantity)
            );
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            indexArray = 0;
            countPack = 0;
            return false;
        }*/


        //вариант 2
       // path = "cloud-server/src/main/java/photo.jpg";
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
       // sChannel.writeAndFlush(commandMessage);
        return true;
    }


    public void disconnect (){

        sChannel.close();
        thread.interrupt();
    }

}
