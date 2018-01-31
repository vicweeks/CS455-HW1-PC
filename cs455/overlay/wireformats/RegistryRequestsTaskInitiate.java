package cs455.overlay.wireformats;

import java.io.*;

public class RegistryRequestsTaskInitiate implements Event {

    private int type = 8;

    public RegistryRequestsTaskInitiate(DataInputStream din) {

    }
    
    public int getType() {
	return type;
    }

    public byte[] getBytes() throws IOException {
	return null;
    }
    
}
