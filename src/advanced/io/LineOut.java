package advanced.io;

import javax.sound.sampled.*;

public class LineOut implements IOOut{
    private SourceDataLine sourceDataLine;

    public LineOut(AudioFormat format) throws LineUnavailableException {
        sourceDataLine = AudioSystem.getSourceDataLine(format);
        sourceDataLine.open(format);
    }

    public void start() {
        sourceDataLine.start();
    }

    public void write(byte[] buffer, int offset, int length) {
        sourceDataLine.write(buffer, offset, length);
    }

    public void close() {
        sourceDataLine.close();
    }
}
