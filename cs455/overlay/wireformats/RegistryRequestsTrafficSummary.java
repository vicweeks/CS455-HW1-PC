package cs455.overlay.wireformats;

import java.io.*;

public class RegistryRequestsTrafficSummary implements Event {

    private int type = 11;

    public RegistryRequestsTrafficSummary(DataInputStream din) {

    }
    
    public int getType() {
	return type;
    }

    public byte[] getBytes() throws IOException {
	return null;
    }
    
}
