package ServerCBox;

import CoreCBox.FileMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import model.UserConstants;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FileMessageHandler extends SimpleChannelInboundHandler<FileMessage> {
    List<FileMessage> fileList = new ArrayList<>();
    Stack<FileMessage> fileMessageQueue = new Stack<>();
    private static final ConcurrentLinkedDeque<ChannelHandlerContext> clients = new ConcurrentLinkedDeque<>();
    FileMessage fileMessage = null;
   // FileMessage [] fileMessages

    private String name;
    private String pathFile;
    private static int cnt = 0;
    private FileOutputStream fos;

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


        if (msg.isFinish()) {
            writeToFile("Server/Clients/" + msg.getPathname());
            //closeFileStream();
        }


        fileList.add( msg.getPart(),msg);






       // fileList.add( msg);
        //fileMessageQueue.add(msg);

        // 2 еще вариантик
       /* if(!(fileMessage.getPathname()).equals(msg.getPathname())) {
            fileMessage = msg;
            openFileStream("Server/Clients/" + fileMessage.getPathname());
            fos.write(fileMessage.getByteArr(), 0, fileMessage.getIndexArray());
        }else {
            fos.write(fileMessage.getByteArr(), 0, fileMessage.getIndexArray());
        }*/





    }

    private void openFileStream(String path){
        try {
            fos = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void closeFileStream(){
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile (String path){
        try (FileOutputStream fos = new FileOutputStream(path)) {
            FileMessage fm;

            /*for ( FileMessage bmsg: fileList) {
                fos.write(bmsg.getByteArr(), 0, bmsg.getIndexArray());
                System.out.println("Write part " + bmsg.getPart());
            }*/



           /* for ( FileMessage bmsg: fileList) {
                fos.write(bmsg.getByteArr(), 0, bmsg.getIndexArray());
                System.out.println("Write part " + bmsg.getPart());
            }*/





           /* for (int i = 0 ; i < fileList.size() ; i++) {
                fm = fileList.get(i);
                fos.write(fm.getByteArr(), 0,fm.getIndexArray());
                System.out.println("Write part " + fm.getPart());
            }*/
            System.out.println( "размер file list " + fileList.size() );
            while (fileList.size()>0){
                fm = fileList.remove(0);
                fos.write(fm.getByteArr(), 0,fm.getIndexArray());
                System.out.println("Write part " + fm.getPart());
            }




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
