package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeReportsTaskFinished implements Event {

    private int type = 10;
    
    public int getType() {
	return type;
    }

    public byte[] getBytes() throws IOException {
	return null;
    }
    
}
