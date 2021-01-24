package Poligon;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public  class MessageHandler extends SimpleChannelInboundHandler<ByteMessage> {
    List<ByteMessage> fileList = new ArrayList<>();
    Path path = Paths.get("C:\\Users\\benbe\\OneDrive\\книги\\Java\\Проекты\\geek-chat(1)\\geek-chat\\geek-chat-server\\src\\main\\java\\com\\geekbrains\\geek\\chat\\server\\cbox\\NIOTEXT.txt");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //super.channelActive(ctx);
        System.out.println(" active!!! " + ctx.toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteMessage msg) throws Exception {
        //////При одключении пользователя срабатывает channelActive, но не работает channelRead0, не могу увидеть причину...
        System.out.println("channelRead Start");
        fileList.add(msg.getNumberPackage(),msg);
        System.out.println("channelRead");
        if (msg.isFinish()) {
            writeToFile();}

    }
    public void writeToFile (){
        System.out.println("Write");
        try (FileOutputStream fos = new FileOutputStream(path.toString())) {
            for ( ByteMessage bmsg: fileList) {
                fos.write(bmsg.bytesArray);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
