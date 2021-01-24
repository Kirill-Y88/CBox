package Poligon;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ServerCbox {
    private static final int PORT = 8189;
    public static SocketChannel channel;

    public static void main(String[] args) {


        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();  //объект для преднастроки сервера
            b.group(workerGroup)                     // команда для подключения потоков
                    .channel(NioServerSocketChannel.class)      //канал для подключения клиентов
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {    //настройка взаимодействия с подключенным клиентом
                           channel = socketChannel;
                             socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                     new ObjectEncoder(),
                                     new MessageHandler());  //добавление в конец конвеера (pipeline) различных хендлеров
                        }
                    });

            ChannelFuture future = b.bind(PORT).sync();  //старт сервака, future - хранит состояние сервака


            future.channel().closeFuture().sync();  // блокирующая операция - ожидание остановки сервака

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();   //закрытие пула потоков после остановки сервака
        }

    }

}
