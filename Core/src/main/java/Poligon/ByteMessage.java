package Poligon;

import java.io.Serializable;

public class ByteMessage implements Serializable {
    String fileName;
    int numberPackage;
    byte [] bytesArray = new byte[1024];

    boolean finish = false;

    public ByteMessage(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getNumberPackage() {
        return numberPackage;
    }

    public void setNumberPackage(int numberPackage) {
        this.numberPackage = numberPackage;
    }

    public byte[] getBytesArray() {
        return bytesArray;
    }

    public void setBytesArray(byte[] bytesArray) {
        this.bytesArray = bytesArray;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

}