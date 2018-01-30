package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeReportsTrafficSummary implements Event {

    private int type = 12;
    
    public int getType() {
	return type;
    }

    public byte[] getBytes() throws IOException {
	return null;
    }
    
}
