package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsRegistration implements Event {
    //testing marshalling example
    
    private int type;
    private long timestamp;
    private String identifier;
    private int tracker;

    public OverlayNodeSendsRegistration(byte[] marshalledBytes) throws IOException {
	ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
	DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

	type = din.readInt();
	timestamp = din.readLong();

	int identifierLength = din.readInt();
	byte[] identifierBytes = new byte[identifierLength];
	din.readFully(identifierBytes);

	identifier = new String(identifierBytes);

	tracker = din.readInt();

	baInputStream.close();
	din.close();
    }
    
    public void getType() {

    }

    public byte[] getBytes() throws IOException {
	byte[] marshalledBytes = null;
	ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
	DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

	dout.writeInt(type);
	dout.writeLong(timestamp);

	byte[] identifierBytes = identifier.getBytes();
	int elementLength = identifierBytes.length;
	dout.writeInt(elementLength);
	dout.write(identifierBytes);

	dout.writeInt(tracker);

	dout.flush();
	marshalledBytes = baOutputStream.toByteArray();

	baOutputStream.close();
	dout.close();
	return marshalledBytes;
    }
    
}
