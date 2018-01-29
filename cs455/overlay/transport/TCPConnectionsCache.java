package cs455.overlay.transport;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class TCPConnectionsCache {

    private SortedMap<Integer, TCPConnection> connections;
    
    public TCPConnectionsCache() {
	connections = new TreeMap<Integer, TCPConnection>();
    }

    public void addConnection(int nodeID, TCPConnection incomingConnection) {
	connections.put(new Integer(nodeID), incomingConnection);
    }

    public SortedMap<Integer, TCPConnection> getConnections() {
	return connections;
    }
    
}
