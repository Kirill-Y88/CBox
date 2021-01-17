import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.READ;

public class NioServer {

    private final ServerSocketChannel serverChannel = ServerSocketChannel.open();
    private final Selector selector = Selector.open();
    private final ByteBuffer buffer = ByteBuffer.allocate(128);
    private Path serverPath = Paths.get("serverDir");
    private Path pathfile;
    private Path pathCD = Paths.get("E:\\учеба\\CBox\\Server");
    private Path pathPast;

    public NioServer() throws IOException {
        serverChannel.bind(new InetSocketAddress(8189));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);


        /*while (serverChannel.isOpen()) {
            selector.select(); // block
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    handleAccept(key);
                }
                if (key.isReadable()) {
                    handleRead(key);
                }
            }
        }*/
    }

    public static void main(String[] args) throws IOException {
        new NioServer();
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        int read = 0;
        StringBuilder msg = new StringBuilder();
        while ((read = channel.read(buffer)) > 0) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                msg.append((char) buffer.get());
            }
            buffer.clear();
        }

        String command = msg.toString().replaceAll("[\n|\r]", "");
        if (command.equals("ls")) {
            String files = Files.list(serverPath)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.joining(", "));
            files += "\n";
            channel.write(ByteBuffer.wrap(files.getBytes(StandardCharsets.UTF_8)));
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
    }

    // метод для ДЗ
    private void msgHandler(String msg) throws IOException {
        String [] message = msg.toString().replaceAll("[\n|\r]", "").split(" ");
        String command = message[0];

        //CAT
        if(command.equals("cat")) {
            Charset charset = Charset.forName("UTF-8");
            pathfile = Paths.get(message[1]);
            FileChannel fileChannel = FileChannel.open(pathfile, READ);
            int read = 0;
            while ((read = fileChannel.read(buffer))>0){
                buffer.flip();
                while (buffer.hasRemaining()){
                    System.out.print(charset.decode(buffer));
                }
                buffer.clear();
                System.out.println();
            }
            fileChannel.close();
        }
        //MKDIR
        if(command.equals("mkdir")) {
            Path createNewDirectory = Paths.get(message[1]);
            Files.createDirectory(createNewDirectory);
        }
        //TOUCH
        if(command.equals("touch")) {
            Path createNewFile = Paths.get(message[1]);
            Files.createFile(createNewFile);
        }
        //LS
        if (command.equals("ls")) {
            String files = Files.list(serverPath)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.joining("\n"));
            files += "\n";
            System.out.println(files);
        }
        //CD
        if (command.equals("cd")){
            if(message[1]==".."){
                pathPast = Paths.get(serverPath.toString());
                serverPath = Paths.get((serverPath.getParent()).toString());
                msgHandler("ls " + serverPath.toString());
            }
            else if(message[1]=="-"){
                serverPath = Paths.get(pathPast.toString());
                msgHandler("ls " + serverPath.toString());
            }else {
                pathPast = Paths.get(serverPath.toString());
                serverPath = Paths.get(serverPath.toString() + "/" + message[1]);
                msgHandler("ls " + serverPath.toString());
            }

        }



    }


}
