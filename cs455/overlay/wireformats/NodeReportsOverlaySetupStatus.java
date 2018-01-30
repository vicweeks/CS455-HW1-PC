package cs455.overlay.wireformats;

import java.io.*;

public class NodeReportsOverlaySetupStatus implements Event {

    private int type = 7;
    
    public int getType() {
	return type;
    }

    public byte[] getBytes() throws IOException {
	return null;
    }
    
}
