package cs455.overlay.wireformats;

import java.io.*;

public interface Event {
    
    public void getType();

    public byte[] getBytes() throws IOException;
    
}
