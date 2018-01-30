package cs455.overlay.wireformats;

import cs455.overlay.routing.*;
import cs455.overlay.transport.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.net.*;

public class RegistryProtocol {

    private TCPConnectionsCache connectionCache;
    private RoutingTable registryRoutingTable;
    private ArrayList<RoutingTable> allRoutingTables; 
    
    public RegistryProtocol() {
	connectionCache = new TCPConnectionsCache();
	registryRoutingTable = new RoutingTable();
    }

    public TCPConnectionsCache getConnectionCache() {
	return connectionCache;
    }

    // command list-messaging-nodes
    public void listMessagingNodes() {
	// TODO
	System.out.println("This command will make me list all the messaging nodes.");
    }

    // command: setup-overlay number-of-routing-table-entries
    public void setupOverlay(int numRoutingTableEntries) {
	// TODO
	registryRoutingTable.setTableSize(numRoutingTableEntries);
	constructNodeRoutingTables();
	try {
	    constructNodeManifest();
	} catch (UnknownHostException uhe) {
	    System.out.println(uhe.getMessage());
	} catch (IOException ioe) {
	    System.out.println(ioe.getMessage());
	}
	System.out.println("This command will make me setup the overlay with "
			   + numRoutingTableEntries + " routing table entries");
    }

    // command: list-routing-tables
    public void listRoutingTables() {
	// TODO
	System.out.println("This command will make me list the routing tables for each node.");
    }

    // command: start number-of-messages
    public void initiateTask(int numMessages) {
	System.out.println("This command will cause me to tell the nodes to start sending " + numMessages
			   + " messages.");
    }

    private void constructNodeRoutingTables() {
	int numberOfNodes = registryRoutingTable.getNumberOfNodes();
	allRoutingTables = new ArrayList<RoutingTable>(numberOfNodes);
	ArrayList<RoutingEntry> allNodeEntries= registryRoutingTable.getConnectedNodes();
	int tableSize = registryRoutingTable.getTableSize();
	ArrayList<Integer> allNodeIDs = registryRoutingTable.getListIDs();
	
	for(RoutingEntry entry : allNodeEntries) {
	    RoutingTable nodeTable = new RoutingTable(entry, tableSize, numberOfNodes, allNodeIDs);
	    int nodeID = entry.getNodeID();
	    calculateRoutingTable(nodeID, nodeTable, numberOfNodes, allNodeIDs);
	    allRoutingTables.add(nodeTable);
	}
    }

    private void calculateRoutingTable(int nodeID, RoutingTable nodeTable, int numberOfNodes,
				       ArrayList<Integer> allNodeIDs) {
	int idIndex = allNodeIDs.indexOf(nodeID);
	int tableSize = nodeTable.getTableSize();
	int[] connectedIDs = new int[tableSize];
	for (int i=0; i<tableSize; i++) {
	    int nextIndex = (int) ((idIndex + Math.pow(2, i)) % numberOfNodes);
	    connectedIDs[i] = allNodeIDs.get(nextIndex);
	}
	for (int nextID : connectedIDs) {
	    TCPConnection nextConnection = connectionCache.getConnection(nextID);
	    nodeTable.addEntry(nodeID, nextConnection.getRemoteIP(), nextConnection.getRemotePort());
	}
    }

    private void constructNodeManifest() throws UnknownHostException, IOException {
	int tableSize = registryRoutingTable.getTableSize();
	int numNodeIDs = registryRoutingTable.getNumberOfNodes();
	int[] allNodeIDs = registryRoutingTable.getAllIDs();
     
	for (RoutingTable nodeTable : allRoutingTables) {
	    RoutingEntry nodeEntry = nodeTable.getLocalEntry();
	    TCPConnection connection = connectionCache.getConnection(nodeEntry.getNodeID());
	    RoutingEntry[] nodesToConnect = new RoutingEntry[tableSize];
	    nodeTable.getConnectedNodes().toArray(nodesToConnect);
	    RegistrySendsNodeManifest nodeManifest =
		new RegistrySendsNodeManifest(tableSize, nodesToConnect, numNodeIDs, allNodeIDs);
	    byte[] nodeManifestMessage = nodeManifest.getBytes();
	    sendNodeManifest(connection, nodeManifestMessage);
	}
	
    }
    
    public void onEvent(TCPConnection connection, Event event) {
	int eventType = event.getType();
	switch(eventType)
	    {
	    case 2: onReceivedRegistrationRequest(connection, event);
		break;
	    case 4:
		break;
	    case 7:
		break;
	    case 10:
		break;
	    case 12:
		break;
	    default: System.out.println("Error in RegistryProtocol: message type " + eventType + " is invalid.");
		System.exit(1);
	}
    }

    // Message Type 2
    private void onReceivedRegistrationRequest(TCPConnection connection, Event event) {
	OverlayNodeSendsRegistration registrationEvent = (OverlayNodeSendsRegistration) event;
	InetAddress ipAddress = registrationEvent.getIPAddress();
	int portNumber = registrationEvent.getPortNumber();

	int status = 0;
	int nodeID = -1;
	String statusMessage = "";
	byte[] registrationStatus = null;
	
        statusMessage = checkForRegistrationError(connection, ipAddress, portNumber);
	if (!statusMessage.equals(""))
	    status = -1;
	
	try {
	    if (status != -1) {
		// Generate unique node ID
		nodeID = generateNodeID();
		registryRoutingTable.addEntry(nodeID, ipAddress, portNumber);
		statusMessage = "Registration request successful. The number of messaging nodes currently constituting the overlay is (" + registryRoutingTable.getConnectedNodes().size() + ")";
		registrationStatus = createRegistrationStatus(nodeID, statusMessage);
		registerNode(nodeID, connection);
		//Debug
		System.out.println("Added node with id: " + nodeID);
	    } else {
		registrationStatus = createRegistrationStatus(status, statusMessage);
		//Debug
		System.out.println("Failed to add node");
	    }
	} catch (IOException e) {
	    System.out.println(e.getMessage());
	}

	sendRegistrationStatus(connection, registrationStatus);
    }

    private String checkForRegistrationError(TCPConnection connection, InetAddress ipAddress, int portNumber) {
        ArrayList<RoutingEntry> allRoutingEntries = registryRoutingTable.getConnectedNodes();
	
	// check if node had previously registered
	for (RoutingEntry rEntry : allRoutingEntries) {
	    if (rEntry.getIPAddress().equals(ipAddress) && rEntry.getPortNumber() == portNumber) {
		return "Error Registering Node: Node already registered.";
	    }
	}

	// ensure that the IP address in the message matches where the request originated
        if (!connection.getRemoteIP().equals(ipAddress)) {
	    return "Error Registering Node: Originating IP address does not match IP address in message.";
	}
	return "";
    }
    
    private int generateNodeID() {
	int uNodeID = -1;
	int randomID;
	while (uNodeID < 0) {
	    randomID = ThreadLocalRandom.current().nextInt(0, 128);
	    if (!registryRoutingTable.getListIDs().contains(randomID))
		uNodeID = randomID;
	}
	return uNodeID;
    }

    private byte[] createRegistrationStatus(int status, String infoString) throws IOException {
	RegistryReportsRegistrationStatus statusMessage = new RegistryReportsRegistrationStatus(status, infoString);
	return statusMessage.getBytes();
    }

    private void registerNode(int nodeID, TCPConnection connection) {
	connectionCache.addConnection(nodeID, connection);
	InetAddress nodeIPAddress = connection.getRemoteIP();
	int nodePortNumber = connection.getRemotePort();
	registryRoutingTable.registerEntry(nodeID, nodeIPAddress, nodePortNumber);
    }
    
    private void sendRegistrationStatus(TCPConnection connection, byte[] registrationStatus) {
	try {
	    sendMessage(connection, registrationStatus);
	} catch (IOException e) {
	    System.out.println(e.getMessage());
	}
    }

    private void sendNodeManifest(TCPConnection connection, byte[] nodeManifest) throws IOException {
	try {
	    sendMessage(connection, nodeManifest);
	} catch (UnknownHostException uhe) {
	    System.out.println(uhe.getMessage());
	}
    }
    
    private void sendMessage(TCPConnection connection, byte[] message) throws IOException {
	connection.sendMessage(message);
    }
    
}
