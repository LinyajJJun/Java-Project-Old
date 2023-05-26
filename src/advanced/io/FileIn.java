package advanced.io;

import javax.sound.sampled.*;
import java.io.*;

public class FileIn implements IOIn{
    private AudioInputStream audioInputStream;

    public FileIn(String filename, AudioFormat format) throws UnsupportedAudioFileException, IOException {
        audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
        audioInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);
    }
    public FileIn(File file, AudioFormat format) throws UnsupportedAudioFileException, IOException {
        audioInputStream = AudioSystem.getAudioInputStream(file);
        audioInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);
    }

    public void start() {
        // 不需要實作此方法，僅為相容性考量
    }

    public int read(byte[] buffer, int offset, int length){
        try {
            return audioInputStream.read(buffer, offset, length);
        } catch (IOException e) {
            return -1;
        }
    }

    public void close(){
        try {
            audioInputStream.close();
        } catch (IOException e) {

        }
    }

    public AudioFormat getFormat() {
        return audioInputStream.getFormat();
    }
    public void reset(){
        try {
            audioInputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
