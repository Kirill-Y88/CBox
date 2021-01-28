package ClientCBox;

import CoreCBox.ChatUnitMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javax.xml.bind.SchemaOutputResolver;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    TextField host;
    @FXML
    TextField port;
    @FXML
    TextField login;
    @FXML
    TextField password;
    @FXML
     TextField pathClient;
    @FXML
     TableView<FileInfo> tableViewClient;
    @FXML
     TextField pathServer;
    @FXML
    TableView<FileInfo> tableViewServer;

    byte [] bArray =  new byte[3072];
    int countPack;
    int indexArray;
    String path;
    SocketChannel sChannel;
    Thread t;

    public TextField text;
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

            


    }

    public void connect(ActionEvent actionEvent) {
        try {
            t = new Thread(() -> {
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
                    ChannelFuture future = b.connect(host.getText(), Integer.parseInt(port.getText())).sync();
                    future.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    workerGroup.shutdownGracefully();
                }
            });
            t.start();

        } catch (Exception e) {
            //LOG.error("e = ", e);
            System.out.println(e);
        }
    }

    public void disconnect(ActionEvent actionEvent) {
        sChannel.close();
        //is.close();

        try {
            t.interrupt();
        }catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void upClient(ActionEvent actionEvent) {
    }

    public void upServer(ActionEvent actionEvent) {
    }

    public void copy(ActionEvent actionEvent) {


        //String messageContent = text.getText();
        LocalDateTime sendAt = LocalDateTime.now();
        //text.clear();
        path = "Server/src/main/java/ServerCBox/photo.jpg";
        try (FileInputStream fis = new FileInputStream("Client/src/main/java/ClientCBox/photo.jpg")) {
            indexArray = 0;
            while ( (indexArray = fis.read(bArray)) > 0){
                //os.writeObject
                sChannel.writeAndFlush
                        (
                        new ChatUnitMessage(
                                path,
                                "fish",
                                sendAt,
                                bArray,
                                countPack,
                                indexArray,
                                false)
                );
               // os.flush();
                countPack++;
            }
            indexArray = 0;
            //os.writeObject
            sChannel.writeAndFlush(
                    new ChatUnitMessage(
                            path,
                            "Fish",
                            sendAt,
                            new byte[]{},
                            countPack,
                            indexArray,
                            true)
            );
           // os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void delete(ActionEvent actionEvent) {
    }

    public void rename(ActionEvent actionEvent) {
    }

    public void create_directory(ActionEvent actionEvent) {
    }

    public void exitAction(){
        sChannel.close();
        //is.close();

        try {
            t.interrupt();
        }catch (Exception e)
        {
            System.out.println(e);
        }
        Platform.exit();
    }
}
