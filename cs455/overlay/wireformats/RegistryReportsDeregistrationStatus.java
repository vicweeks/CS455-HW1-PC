package cs455.overlay.wireformats;

import java.io.*;

public class RegistryReportsDeregistrationStatus implements Event {

    public int getType() {
	return -1;
    }

    public byte[] getBytes() throws IOException {
	return null;
    }
    
}
