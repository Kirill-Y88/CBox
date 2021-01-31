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

        if(msg.getCodeOperation() == 0) {
            serverController.log_in(msg.getLogin());

        }





    }
}
