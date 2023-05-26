package advanced.io;

import javax.sound.sampled.*;
import java.io.*;

public class FileOut implements IOOut{
    private String fileName;
    private AudioFormat format;
    private ByteArrayOutputStream byteArrayOutputStream;
    public FileOut(String filename, AudioFormat format) throws IOException {
        this.format = format;
        this.fileName = filename;
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    public void start() {
        // 不需要實作此方法，僅為相容性考量
    }

    public void write(byte[] buffer, int offset, int length) {
        try {
            byteArrayOutputStream.write(buffer, offset, length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            byte[] audioData = byteArrayOutputStream.toByteArray();
            AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioData), format, audioData.length);
            File file = new File(fileName);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
            audioInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
                System.out.println(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}