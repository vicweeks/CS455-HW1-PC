package cs455.overlay.transport;

import java.util.ArrayList;

public class TCPConnectionsCache {

    private ArrayList<TCPConnection> connections;
    
    public TCPConnectionsCache() {
	connections = new ArrayList<TCPConnection>();
    }

    public void addConnection(TCPConnection incomingConnection) {
	connections.add(incomingConnection);
    }
    
}
