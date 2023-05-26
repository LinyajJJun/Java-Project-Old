package advanced.io;

import javax.sound.sampled.AudioFormat;

public interface IOIn {
    public void start() ;

    public int read(byte[] buffer, int offset, int length) ;

    public void close() ;

    public AudioFormat getFormat() ;

    public void reset();
}
