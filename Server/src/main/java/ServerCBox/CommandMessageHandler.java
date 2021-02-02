package ServerCBox;

import CoreCBox.CommandMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CommandMessageHandler extends SimpleChannelInboundHandler<CommandMessage> {
    ServerController serverController;

    public CommandMessageHandler (ServerController serverController){
        this.serverController = serverController;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CommandMessage msg) throws Exception {

        serverController.log(Integer.toString(msg.getCodeOperation()));
        serverController.log(msg.getStringPath());
        if(msg.getCodeOperation() == 0) {
            serverController.log_in(msg.getStringPath());
            System.out.println("пользователь " +msg.getStringPath()+ " залогинился");
        }
        if(msg.getCodeOperation() == 1) {
            System.out.println("пользователь " +msg.getStringPath()+ " вышел из учетной записи");
        }

        if(msg.getCodeOperation() == 3) {
            serverController.delete_directory(msg.getStringPath());
            System.out.println("директория " +msg.getStringPath()+ " удалена");
        }

        if(msg.getCodeOperation() == 4) {
            System.out.println("начат процесс создания директории");
            serverController.create_directory(msg.getStringPath());
            System.out.println("директория " +msg.getStringPath()+ " создана");
        }

        if(msg.getCodeOperation() == 5){
            System.out.println("начат процесс переименования файла");


            serverController.rename(msg.getStringPath(), msg.getStringPath2());
            System.out.println(" CMH  old " + msg.getStringPath());
            System.out.println(" CMH  new " + msg.getStringPath2());
            System.out.println("закончен процесс переименования файла");
        }




    }
}
