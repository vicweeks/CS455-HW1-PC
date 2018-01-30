package cs455.overlay.wireformats;

import cs455.overlay.routing.*;
import java.io.*;
import java.net.*;

public class RegistrySendsNodeManifest implements Event {

    private int type = 6;
    private int routingTableSize;
    private RoutingEntry[] nodesToConnect;
    private int numNodeIDs;
    private int[] allNodeIDs;

    public RegistrySendsNodeManifest(DataInputStream din) throws UnknownHostException, IOException {
	// for receiving
	routingTableSize = din.readInt();
	nodesToConnect = new RoutingEntry[routingTableSize];
	for (int i=0; i<routingTableSize; i++) {
	    int nodeID = din.readInt();
	    int ipLength = din.readInt();
	    byte[] ipAddressRaw = new byte[ipLength];
	    din.readFully(ipAddressRaw);
	    InetAddress ipAddress = convertFromRaw(ipAddressRaw);
	    int portNumber = din.readInt();
	    RoutingEntry entry = new RoutingEntry(nodeID, ipAddress, portNumber);
	    nodesToConnect[i] = entry;
	}
	numNodeIDs = din.readInt();
	allNodeIDs = new int[numNodeIDs];
	for (int i=0; i<numNodeIDs; i++) {
	    allNodeIDs[i] = din.readInt();
	}
    }

    public RegistrySendsNodeManifest(int routingTableSize, RoutingEntry[] nodesToConnect,
				     int numNodeIDs, int[] allNodeIDs)
	throws UnknownHostException {
	// for sending
	this.routingTableSize = routingTableSize;
	this.nodesToConnect = nodesToConnect;
	this.numNodeIDs = numNodeIDs;
	this.allNodeIDs = allNodeIDs;
    }
    
    public int getType() {
	return type;
    }

    public int getRoutingTableSize() {
	return routingTableSize;
    }

    public RoutingEntry[] getRoutingNodes() {
	return nodesToConnect;
    }

    public int getNumNodes() {
	return numNodeIDs;
    }

    private int[] getAllNodeIDs() {
	return allNodeIDs;
    }

    private InetAddress convertFromRaw(byte[] ipAddressRaw) throws UnknownHostException {
	return InetAddress.getByAddress(ipAddressRaw);
    }

    private byte[] convertToRaw(InetAddress ipAddress) throws UnknownHostException {
	return ipAddress.getAddress();
    }
    
    public byte[] getBytes() throws UnknownHostException, IOException {
	byte[] marshalledBytes = null;
	ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
	DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

	dout.writeInt(type);
	dout.writeInt(routingTableSize);
	for (RoutingEntry entry : nodesToConnect) {
	    dout.writeInt(entry.getNodeID());
	    byte[] ipAddressRaw = convertToRaw(entry.getIPAddress());
	    int ipLength = ipAddressRaw.length;
	    dout.writeInt(ipLength);
	    dout.write(ipAddressRaw);
	    dout.writeInt(entry.getPortNumber());
	}
	dout.writeInt(numNodeIDs);
	for (int ID : allNodeIDs) {
	    dout.writeInt(ID);
	}

	dout.flush();
	marshalledBytes = baOutputStream.toByteArray();

	baOutputStream.close();
	dout.close();	
	return marshalledBytes;
    }
    
}
