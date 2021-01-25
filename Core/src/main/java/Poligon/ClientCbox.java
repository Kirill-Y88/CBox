package Poligon;

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
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientCbox {
    private static SocketChannel channel;

    private static final String HOST = "localhost";
    private static final int PORT = 8189;


    public static void main(String[] args)  {
        Path path = Paths.get("C:\\Users\\benbe\\OneDrive\\книги\\Java\\Проекты\\geek-chat(1)\\geek-chat\\geek-chat-client\\src\\main\\java\\cbox\\NIOTEXT.txt");

        ClientCbox.startNewThread();

        ByteMessage byteMessage = new ByteMessage(path.toString());

        System.out.println(" part 1");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(" part 2");
        try (FileInputStream fis = new FileInputStream(path.toString())) {
            int countPack = 0;
            System.out.println(" part 3");
            while ( fis.read(byteMessage.bytesArray) > 0){
                byteMessage.setNumberPackage(countPack);
                byteMessage.setFinish(false);
                channel.writeAndFlush( byteMessage);   //////Периодически выскакивает NullPointer
                countPack++;
                System.out.println(" счетчик" + countPack + "   канал " + channel.isActive());
            }
            System.out.println(" part 4");
            byteMessage.setNumberPackage(countPack);
            byteMessage.setFinish(true);
            channel.writeAndFlush(byteMessage);
            System.out.println(" part 5");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void startNewThread() {
        Thread t = new Thread(() -> {
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;
                                socketChannel.pipeline().addLast(
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)), new ObjectEncoder()
                                );
                            }
                        });
                ChannelFuture future = b.connect(HOST, PORT).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        t.start();
    }


}
