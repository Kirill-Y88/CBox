package ServerCBox;

import CoreCBox.FileMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


public class FileMessageHandler extends SimpleChannelInboundHandler<FileMessage> {
    List<FileMessage> fileList = new ArrayList<>();


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileMessage msg) throws Exception {

        System.out.println(" часть " + msg.getPart() + " метка о завершении " + msg.isFinish());


        if (msg.isFinish()) {
            writeToFile("Server/Clients/" + msg.getPathname());

        }
        fileList.add( msg.getPart(),msg);


    }


    private void writeToFile (String path){
        try (FileOutputStream fos = new FileOutputStream(path)) {
            FileMessage fm;

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


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }
}
