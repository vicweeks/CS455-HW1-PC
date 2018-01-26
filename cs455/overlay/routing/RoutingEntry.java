package cs455.overlay.routing;

public class RoutingEntry {

    private int nodeID;
    private byte[] ipAddress;
    private int portNumber;

    public RoutingEntry(int nodeID, byte[] ipAddress, int portNumber) {
	this.nodeID = nodeID;
	this.ipAddress = ipAddress;
	this.portNumber = portNumber;
    }

    public int getNodeID() {
	return this.nodeID;
    }

    public byte[] getIPAddress() {
	return this.ipAddress;
    }

    public int getPortNumber() {
	return this.portNumber;
    }

}
