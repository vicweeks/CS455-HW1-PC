package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsRegistration implements Event {
        
    private int type = 2;
    private int ipLength;
    private byte[] ipAddress;
    private int portNumber;

    public OverlayNodeSendsRegistration(DataInputStream din) throws IOException {
	ipLength = din.readInt();
	ipAddress = new byte[ipLength];
	din.readFully(ipAddress);
	portNumber = din.readInt();
    }

    public OverlayNodeSendsRegistration(byte[] ipAddress, int portNumber) {
	this.ipAddress = ipAddress;
	ipLength = ipAddress.length;
	this.portNumber = portNumber;
    }
    
    public int getType() {
	return type;
    }

    public byte[] getIPAddress() {
	return ipAddress;
    }

    public int getPortNumber() {
	return portNumber;
    }

    public byte[] getBytes() throws IOException {
	byte[] marshalledBytes = null;
	ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
	DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

	dout.writeInt(type);
	dout.writeInt(ipLength);
	dout.write(ipAddress);
	dout.writeInt(portNumber);

	dout.flush();
	marshalledBytes = baOutputStream.toByteArray();

	baOutputStream.close();
	dout.close();
	return marshalledBytes;
    }
    
}
