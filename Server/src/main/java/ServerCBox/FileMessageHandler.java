package ServerCBox;

import CoreCBox.FileMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import model.UserConstants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FileMessageHandler extends SimpleChannelInboundHandler<FileMessage> {
    List<FileMessage> fileList = new ArrayList<>(1000);
    Stack<FileMessage> fileMessageQueue = new Stack<>();
    private static final ConcurrentLinkedDeque<ChannelHandlerContext> clients = new ConcurrentLinkedDeque<>();

   // FileMessage [] fileMessages

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
    protected void channelRead0(ChannelHandlerContext ctx, FileMessage msg) throws Exception {

        if (msg.getPathname().equals(UserConstants.DEFAULT_SENDER_NAME)) {
            msg.setPathname(name);
        }
        for (ChannelHandlerContext client : clients) {
            client.writeAndFlush(msg);
        }
        System.out.println(" часть " + msg.getPart() + " метка о завершении " + msg.isFinish());

      //  recapacity(msg.getQuantity());

       // fileList.add( msg.getPart(),msg);
       // fileList.add( msg);
        fileMessageQueue.add(msg);
        if (msg.isFinish()) {
            writeToFile("Server/Clients/" + msg.getPathname());}

    }

    private void writeToFile (String path){
        try (FileOutputStream fos = new FileOutputStream(path)) {
            for ( FileMessage bmsg: fileMessageQueue) {
                fos.write(bmsg.getByteArr(), 0, bmsg.getIndexArray());
                System.out.println("Write part " + bmsg.getPart());
            }
/*

            for (int i = 0 ; i < fileList.size() ; i++) {
                fos.write((fileList.get(i)).getByteArr(), 0,(fileList.get(i)).getIndexArray());
                System.out.println("Write part " + (fileList.get(i)).getPart());
            }
*/


        }catch (IOException e){
            e.printStackTrace();
        }
        fileList.clear();
    }

    private void recapacity( int quantity){
        if(fileList.size() < quantity){
            fileList = new ArrayList<>(quantity);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clients.remove(ctx);
    }
}
