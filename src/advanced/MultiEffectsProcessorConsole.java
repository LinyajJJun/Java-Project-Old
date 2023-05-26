package advanced;

import advanced.io.FileIn;
import advanced.io.FileOut;
import advanced.io.IOIn;
import advanced.io.IOOut;

import javax.sound.sampled.AudioFormat;

public class MultiEffectsProcessorConsole {
    private final static int BUFFER_SIZE = 1024;
    public static void main(String[] args) {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);

            IOIn lineIn = new FileIn("test.wav", format);
            IOOut lineOut = new FileOut("output.wav", format);
//            advanced.io.IOOut lineOut = new advanced.io.LineOut(format);
            byte[] buffer = new byte[BUFFER_SIZE];

            lineIn.start();
            lineOut.start();

            int bytesRead;
            while ((bytesRead = lineIn.read(buffer, 0, buffer.length)) != -1) {
                lineOut.write(buffer, 0, bytesRead);
            }

            lineIn.close();
            lineOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
