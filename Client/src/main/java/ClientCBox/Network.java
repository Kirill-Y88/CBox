package ClientCBox;

import CoreCBox.CommandMessage;
import CoreCBox.FileMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class Network {
Thread thread;
SocketChannel sChannel;
    byte [] bArray =  new byte[3072];
    int countPack;
    int indexArray;
    String path;



    public Network (String host, String port){
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
            //LOG.error("e = ", e);
            System.out.println(e);
        }



    }


    public boolean sendFile(){


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
                                new FileMessage(
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
                    new FileMessage(
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




        return true;
    }
    public boolean sendCommand(CommandMessage commandMessage) {
        sChannel.writeAndFlush(commandMessage);
        return true;
    }


    public void disconnect (){
        sChannel.close();
        thread.interrupt();
    }

}
