package CoreCBox;

import java.io.Serializable;
import java.time.LocalDateTime;

public class FileMessage implements Serializable {

    private String pathname;
    private final String messageContent;
    private final LocalDateTime sendAt;
    byte [] byteArr;
    int part;
    int indexArray;
    boolean finish;

    public FileMessage(String pathname, String messageContent, LocalDateTime sendAt, byte[] byteArr, int part, int indexArray, boolean finish) {
        this.pathname = pathname;
        this.messageContent = messageContent;
        this.sendAt = sendAt;
        this.byteArr = byteArr;
        this.part = part;
        this.indexArray = indexArray;
        this.finish = finish;
    }

    public byte[] getByteArr() {
        return byteArr;
    }

    public int getIndexArray() {
        return indexArray;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public LocalDateTime getSendAt() {
        return sendAt;
    }

    public int getPart() {
        return part;
    }

    public boolean isFinish() {
        return finish;
    }

    @Override
    public String toString() {
        return String.format("[%d-%d-%d %d:%d] %s: %s",
                sendAt.getYear(), sendAt.getMonthValue(), sendAt.getDayOfMonth(),
                sendAt.getHour(), sendAt.getMinute(), pathname, messageContent);
    }
}
