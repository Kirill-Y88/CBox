import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.READ;


public class MainClientNIO {

    public static void main(String[] args) {
        SocketChannel socketChannel;
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        Path path = Paths.get("E:\\учеба\\CBox\\Client\\src\\main\\java\\NIOTEXT.txt");
        FileChannel fileChannel;

        try {
            socketChannel = SocketChannel.open();
            fileChannel = FileChannel.open(path, READ);
            socketChannel = SocketChannel.open(new InetSocketAddress("localhost",8189));

            while (fileChannel.read(byteBuffer) !=-1){
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()){
                    socketChannel.write(byteBuffer);
                }
                byteBuffer.clear();
            }

            fileChannel.close();
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
