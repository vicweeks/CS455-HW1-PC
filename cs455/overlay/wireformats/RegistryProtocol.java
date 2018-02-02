package cs455.overlay.wireformats;

import cs455.overlay.routing.*;
import cs455.overlay.transport.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.net.*;

public class RegistryProtocol {

    private TCPConnectionsCache connectionCache;
    private ArrayList<RoutingTable> allRoutingTables;
    private int readyNodes;
    private SortedMap<Integer, RoutingEntry> sortedEntries;
    
    public RegistryProtocol() {
	connectionCache = new TCPConnectionsCache();
	readyNodes = 0;
	sortedEntries = new TreeMap<Integer, RoutingEntry>();
    }

    public TCPConnectionsCache getConnectionCache() {
	return connectionCache;
    }

    // command list-messaging-nodes
    public void listMessagingNodes() {
	for (RoutingEntry entry : sortedEntries.values()) {
	    System.out.println("Messaging node with ID " + entry.getNodeID()
			       + " has IP Address " + entry.getIPAddress()
			       + " and Port Number " + entry.getPortNumber());
	}
    }

    // command: setup-overlay number-of-routing-table-entries
    public void setupOverlay(int numRoutingTableEntries) {
	OverlaySetup setup = new OverlaySetup(sortedEntries, numRoutingTableEntries);
	allRoutingTables = setup.getAllTables();
	try {
	    for (RoutingTable nodeTable : allRoutingTables) {
		RoutingEntry nodeEntry = nodeTable.getLocalEntry();
		TCPConnection connection = connectionCache.getConnection(nodeEntry.getNodeID());
		byte[] nodeManifest = setup.constructNodeManifest(nodeTable);
		sendNodeManifest(connection, nodeManifest);
	    }
	} catch (UnknownHostException uhe) {
	    System.out.println(uhe.getMessage());
	} catch (IOException ioe) {
	    System.out.println(ioe.getMessage());
	}
    }

    // command: list-routing-tables
    public void listRoutingTables() {
	// TODO
	System.out.println("This command will make me list the routing tables for each node.");
    }

    // command: start number-of-messages
    public void initiateTask(int numMessages) {
	if (readyNodes != sortedEntries.size())
	    System.out.println("Error: cannot initiate setup; not all messaging nodes are ready.");
	else {
	    //TODO
	    System.out.println("This command will cause me to tell the nodes to start sending "
			       + numMessages  + " messages.");
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
	    case 7: onReceivedSetupStatus(connection, event);
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
		registerNode(nodeID, connection, ipAddress, portNumber);
		statusMessage = "Registration request successful. The number of messaging nodes currently constituting the overlay is (" + sortedEntries.size() + ")";
		registrationStatus = createRegistrationStatus(nodeID, statusMessage);
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

    // Message Type 7
    private void onReceivedSetupStatus(TCPConnection connection, Event event) {
	NodeReportsOverlaySetupStatus setupStatus = (NodeReportsOverlaySetupStatus) event;
	int status = setupStatus.getStatus();
	if (status == -1) {
	    System.out.println(setupStatus.getInfo());
	} else
	    readyNodes++;
    }
    
    private String checkForRegistrationError(TCPConnection connection, InetAddress ipAddress, int portNumber) {
        ArrayList<RoutingEntry> allRoutingEntries =
	    new ArrayList<RoutingEntry>(sortedEntries.values());
	
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
	    if (!sortedEntries.containsKey(randomID))
		uNodeID = randomID;
	}
	return uNodeID;
    }

    private byte[] createRegistrationStatus(int status, String infoString) throws IOException {
	RegistryReportsRegistrationStatus statusMessage = new RegistryReportsRegistrationStatus(status, infoString);
	return statusMessage.getBytes();
    }

    private void registerNode(int nodeID, TCPConnection connection, InetAddress nodeIPAddress,
			      int nodePortNumber) {
	connectionCache.addConnection(nodeID, connection);
	sortedEntries.put(new Integer(nodeID),
			  new RoutingEntry(nodeID, nodeIPAddress, nodePortNumber));
    }

    private void deregisterNode(int nodeID, TCPConnection connection) {
	RoutingEntry status = sortedEntries.remove(nodeID);
	if (status == null) {
	    System.out.println("Error removing node: node is not currently registered.");
	}
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
