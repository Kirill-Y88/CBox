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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class Network {
Thread thread;
SocketChannel sChannel;

    String path;
    byte [] bArray =  new byte[3072];
    int countPack = 0;
    int indexArray = 0;

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
            System.out.println(e);
        }
    }


    public boolean sendFile(String filePath){
      //  byte [] bArray =  new byte[3072];
        int countPack = 0;
        int indexArray = 0;

        LocalDateTime sendAt = LocalDateTime.now();

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
        }



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
