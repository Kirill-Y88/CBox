package ServerCBox;

import CoreCBox.ChatUnitMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import model.UserConstants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatUnitHandler extends SimpleChannelInboundHandler<ChatUnitMessage> {
    List<ChatUnitMessage> fileList = new ArrayList<>();
    private static final ConcurrentLinkedDeque<ChannelHandlerContext> clients = new ConcurrentLinkedDeque<>();

    private String name;
    private String pathFile;
    private static int cnt = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        /*clients.add(ctx);
        cnt++;
        name = "user#" + cnt;*/
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatUnitMessage msg) throws Exception {

        if (msg.getPathname().equals(UserConstants.DEFAULT_SENDER_NAME)) {
            msg.setPathname(name);
        }

        for (ChannelHandlerContext client : clients) {
            client.writeAndFlush(msg);
        }

        System.out.println(" часть " + msg.getPart() + " метка о завершении " + msg.isFinish());

        fileList.add( msg.getPart(),msg);
        if (msg.isFinish()) {
            writeToFile(msg.getPathname());}

    }

    private void writeToFile (String path){
        try (FileOutputStream fos = new FileOutputStream(path)) {
            for ( ChatUnitMessage bmsg: fileList) {
                fos.write(bmsg.getByteArr(), 0, bmsg.getIndexArray());
                System.out.println("Write part " + bmsg.getPart());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        fileList.clear();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clients.remove(ctx);
    }
}
