package cs455.overlay.wireformats;

import java.io.*;

public interface Event {
    
    public int getType();

    public byte[] getBytes() throws IOException;
    
}
