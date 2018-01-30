package cs455.overlay.wireformats;

import java.io.*;
import java.net.*;

public class OverlayNodeSendsRegistration implements Event {
        
    private int type = 2;
    private int ipLength;
    private InetAddress ipAddress;
    private byte[] ipAddressRaw;
    private int portNumber;

    public OverlayNodeSendsRegistration(DataInputStream din) throws UnknownHostException, IOException {
	ipLength = din.readInt();
	ipAddressRaw = new byte[ipLength];
	din.readFully(ipAddressRaw);
	ipAddress = convertFromRaw(ipAddressRaw);
	portNumber = din.readInt();
    }

    public OverlayNodeSendsRegistration(InetAddress ipAddress, int portNumber) throws UnknownHostException {
	this.ipAddress = ipAddress;
	this.ipAddressRaw = convertToRaw(ipAddress);
	ipLength = ipAddressRaw.length;
	this.portNumber = portNumber;
    }
    
    public int getType() {
	return type;
    }

    public InetAddress getIPAddress() {
	return ipAddress;
    }

    public int getPortNumber() {
	return portNumber;
    }

    private InetAddress convertFromRaw(byte[] ipAddressRaw) throws UnknownHostException {
	return InetAddress.getByAddress(ipAddressRaw);
    }

    private byte[] convertToRaw(InetAddress ipAddress) throws UnknownHostException {
	return ipAddress.getAddress();
    }
    
    public byte[] getBytes() throws IOException {
	byte[] marshalledBytes = null;
	ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
	DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

	dout.writeInt(type);
	dout.writeInt(ipLength);
	dout.write(ipAddressRaw);
	dout.writeInt(portNumber);

	dout.flush();
	marshalledBytes = baOutputStream.toByteArray();

	baOutputStream.close();
	dout.close();
	return marshalledBytes;
    }
    
}
