package ServerCBox;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NettyServer {

    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);
    private Path pathUserRoot = null;
    private Path pathFull;
    private int countPathRoot;
    SocketChannel sChannel;


    public Path getPathFull() {
        return pathFull;
    }

    public int getCountPathRoot() {
        return countPathRoot;
    }

    public NettyServer() {
        pathFull = Paths.get(".").toAbsolutePath().resolve("Clients");
        countPathRoot = pathFull.getNameCount();
        System.out.println(pathFull);
        System.out.println(countPathRoot);

       // new ServerController(this);
       // SqlHandler sqlHandler = new SqlHandler();


        SqlHandler.connect();
       /*String login = SqlHandler.getLogin("user1", "u1");
       SqlHandler.setLogin("prepar", "2");
        System.out.println(SqlHandler.getLogin("prepar", "2"));
        System.out.println(login);*/


        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            sChannel = channel;
                            channel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new FileMessageHandler(),
                                    new CommandMessageHandler(new ServerController(sChannel)));
                        }
                    });
            ChannelFuture future = bootstrap.bind(8189).sync();
            System.out.println("сервер включен");
            System.out.println(future.channel().isActive());
            LOG.debug("server started on PORT = 8189!");
            future.channel().closeFuture().sync(); // block
            SqlHandler.disconnect();
        } catch (InterruptedException e) {
            LOG.error("e=", e);
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyServer();
    }

}
