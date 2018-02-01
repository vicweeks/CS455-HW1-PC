package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsData implements Event {

    private int type = 9;

    public OverlayNodeSendsData(DataInputStream din) {

    }
    
    public int getType() {
	return type;
    }

    public byte[] getBytes() throws IOException {
	return null;
    }
    
}
