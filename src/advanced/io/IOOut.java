package advanced.io;

public interface IOOut {
    public void start() ;

    public void write(byte[] buffer, int offset, int length) ;

    public void close() ;
}
