package cs455.overlay.wireformats;

import cs455.overlay.routing.*;
import cs455.overlay.transport.*;
import cs455.overlay.node.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.net.*;

public class MessagingProtocol {

    private MessagingNode self;
    private int localNodeID;
    private TCPConnectionsCache connectionCache;
    private RoutingTable routingTable;
    
    public MessagingProtocol(MessagingNode self, TCPConnection registryConnection) {
	this.self = self;
	connectionCache = new TCPConnectionsCache();
	connectionCache.addConnection(-1, registryConnection);
    }

    // Command: print-counters-and-diagnostics
    public void printDiagnostics() {
	// TODO
	System.out.println("This command will print info about messages this node has processed.");
    }
    
    // Command: exit-overlay
    public void exitOverlay() {
	// TODO
	System.out.println("This command will cause the node to leave the overlay.");
    }
	    
    public void onEvent(TCPConnection connection, Event event) {
	int eventType = event.getType();
	switch(eventType)
	    {
	    case 3: onReceivedRegistrationStatus(connection, event);
		break;
	    case 5: 
		break;
	    case 6: onReceivedNodeManifest(connection, event);
		break;
	    case 8:
		break;
	    case 9:
		break;
	    case 11:
		break;
	    default: System.out.println("Error in MessagingProtocol: message type " + eventType + " is invalid.");
		System.exit(1);
	}
    }

    // Message Type 3
    private void onReceivedRegistrationStatus(TCPConnection connection, Event event) {
	RegistryReportsRegistrationStatus registrationResponse = (RegistryReportsRegistrationStatus) event;
	localNodeID = registrationResponse.getStatus();
	String infoString = registrationResponse.getInfo();

	System.out.println(infoString);
    }
    
    // Message Type 6
    private void onReceivedNodeManifest(TCPConnection connection, Event event) {
	RegistrySendsNodeManifest nodeManifest = (RegistrySendsNodeManifest) event;
	int routingTableSize = nodeManifest.getRoutingTableSize();
	ArrayList<RoutingEntry> nodesToConnect = nodeManifest.getRoutingNodes();
	int numNodeIDs = nodeManifest.getNumNodes();
	ArrayList<Integer> allNodeIDs = nodeManifest.getAllNodeIDs();
	RoutingEntry localEntry = new RoutingEntry(localNodeID, connection.getLocalIP(), connection.getLocalPort());
	routingTable = new RoutingTable(localEntry, routingTableSize, numNodeIDs, allNodeIDs, nodesToConnect);
	initiateConnections(nodesToConnect);
    }

    private void initiateConnections(ArrayList<RoutingEntry> nodesToConnect) {
	int status = localNodeID;
	String statusMessage = "";
	int nodeToConnectID = -1;
	try {
	    for (RoutingEntry entry : nodesToConnect) {
		nodeToConnectID = entry.getNodeID();
		Socket socket = new Socket(entry.getIPAddress(), entry.getPortNumber());
		TCPConnection routingConnection = new TCPConnection(self, socket);
		connectionCache.addConnection(nodeToConnectID, routingConnection);
	    }
	} catch (UnknownHostException uhe) {
	    statusMessage = "Node " + localNodeID + " failed to connect to node "
		+ nodeToConnectID;
	    status = -1;
	} catch (IOException ioe) {
	    statusMessage = "Node " + localNodeID + " failed to connect to node "
		+ nodeToConnectID;
	    status = -1;
	}
	reportOverlaySetupStatus(status, statusMessage);
    }

    private void reportOverlaySetupStatus(int status, String statusMessage) {
	try {
	    NodeReportsOverlaySetupStatus setupStatus = new NodeReportsOverlaySetupStatus(status, statusMessage);
	    TCPConnection connection = connectionCache.getConnection(-1);
	    byte[] setupStatusMessage = setupStatus.getBytes();
	    sendMessage(connection, setupStatusMessage);
	} catch (IOException ioe) {
	    System.out.println(ioe.getMessage());
	}
    }
    
    public void sendMessage(TCPConnection connection, byte[] message) throws IOException {
	connection.sendMessage(message);
    }
    
}
