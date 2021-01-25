package tempFile;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.ChatUnitMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);

    public ListView<String> listView;
    byte [] bArray;
    int countPack;
    int indexArray;
    String path;

    public ChatController() {
        bArray = new byte[3072];
        countPack = 0;
        indexArray = 0;
    }

    public TextField text;

    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;

    public void sendMessage(ActionEvent event) throws IOException {
        String messageContent = text.getText();
        LocalDateTime sendAt = LocalDateTime.now();
        text.clear();
        path = "cloud-server/src/main/java/photo.jpg";
        try (FileInputStream fis = new FileInputStream("cloud-client/src/main/java/photo.jpg")) {
            indexArray = 0;
            while ( (indexArray = fis.read(bArray)) > 0){
                os.writeObject(
                        new ChatUnitMessage(
                                path,
                                messageContent,
                                sendAt,
                                bArray,
                                countPack,
                                indexArray,
                                false)
                );
                os.flush();
                countPack++;
            }
            indexArray = 0;
            os.writeObject(
                    new ChatUnitMessage(
                            path,
                            messageContent,
                            sendAt,
                            new byte[]{},
                            countPack,
                            indexArray,
                            true)
            );
            os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8188);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());

            new Thread(() -> {
                while (true) {
                    try {
                        ChatUnitMessage message = (ChatUnitMessage) is.readObject();
                        listView.getItems().add(message.toString());
                    } catch (Exception e) {
                        LOG.error("e = ", e);
                        break;
                    }
                }
            }).start();
        } catch (Exception e) {
            LOG.error("e = ", e);
        }

    }
}
