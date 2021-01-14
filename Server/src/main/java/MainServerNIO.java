import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class MainServerNIO {


    public static void main(String[] args) {
        ServerSocketChannel serverSocketChannel;
        SocketChannel socketChannel;
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        Path path = Paths.get("E:\\учеба\\CBox\\Server\\src\\main\\java\\NIOTEXT.txt");
        FileChannel fileChannel;

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(8189));
            fileChannel = FileChannel.open(path, WRITE, CREATE );
            while (true){
                socketChannel = serverSocketChannel.accept();
                System.out.println("Accept");
                break;
            }
            while (socketChannel.read(byteBuffer) != -1){
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()){
                    fileChannel.write(byteBuffer);
                }
                byteBuffer.clear();
            }


            fileChannel.close();
            socketChannel.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


}
