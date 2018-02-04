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
    private int finishedNodes;
    private SortedMap<Integer, RoutingEntry> sortedEntries;
    
    public RegistryProtocol() {
	connectionCache = new TCPConnectionsCache();
	readyNodes = 0;
	finishedNodes = 0;
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
		connection.sendMessage(nodeManifest);
	    }
	} catch (UnknownHostException uhe) {
	    System.out.println(uhe.getMessage());
	} catch (IOException ioe) {
	    System.out.println(ioe.getMessage());
	}
    }

    // command: list-routing-tables
    public void listRoutingTables() {
	for (RoutingTable nodeTable : allRoutingTables) {
	    RoutingEntry localEntry = nodeTable.getLocalEntry();
	    ArrayList<RoutingEntry> connectedNodes = nodeTable.getConnectedNodes();
	    System.out.println("Node table for node with \nID " + localEntry.getNodeID()
			       + ", IP_Address " + localEntry.getIPAddress()
			       + ", and Port_Number " + localEntry.getPortNumber() + ":");
	    System.out.println("    Node    IP_Address         Port_Number");
	    for (RoutingEntry entry : connectedNodes) {
		System.out.printf("    |%3s|   |%14s|   |%5s|\n",
				  entry.getNodeID(), entry.getIPAddress(), entry.getPortNumber());
	    }
	    System.out.println("\n\n\n");
	}
    }

    // command: start number-of-messages
    public void initiateTask(int numMessages) {
	if (readyNodes != sortedEntries.size())
	    System.out.println("Error: cannot initiate setup; not all messaging nodes are ready.");
	else {
	    try {
		RegistryRequestsTaskInitiate initiateTask =
		    new RegistryRequestsTaskInitiate(numMessages);
		byte[] initiateTaskMessage = initiateTask.getBytes();
		for (TCPConnection connection : connectionCache.getConnectionList())
		    connection.sendMessage(initiateTaskMessage);
	    } catch (IOException ioe) {
		System.out.println(ioe.getMessage());
	    }
	}
    } 
    
    public void onEvent(TCPConnection connection, Event event) {
	int eventType = event.getType();
	switch(eventType)
	    {
	    case 2: onReceivedRegistrationRequest(connection, event);
		break;
	    case 4: onReceivedDeregistrationRequest(connection, event);
		break;
	    case 7: onReceivedSetupStatus(connection, event);
		break;
	    case 10: onReceivedTaskFinishedReport(connection, event);
		break;
	    case 12: onReceivedTrafficSummary(connection, event);
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
		System.out.println("Added node with ID: " + nodeID);
	    } else {
		registrationStatus = createRegistrationStatus(status, statusMessage);
		//Debug
		System.out.println("Failed to add node");
	    }
	    connection.sendMessage(registrationStatus);
	} catch (IOException ioe) {
	    System.out.println(ioe.getMessage());
	    System.out.println("Lost connection to node. It has been removed from the overlay.");
	    sortedEntries.remove(nodeID);
	    connectionCache.removeConnection(nodeID);
	}
    }

    // Message Type 4
    private void onReceivedDeregistrationRequest(TCPConnection connection, Event event) {
	OverlayNodeSendsDeregistration deregistrationRequest =
	    (OverlayNodeSendsDeregistration) event;
	InetAddress ipAddress = deregistrationRequest.getIPAddress();
	int portNumber = deregistrationRequest.getPortNumber();
	int nodeID = deregistrationRequest.getNodeID();
	int status = 0;
	String statusMessage = "";
	byte[] deregistrationStatus = null;

	statusMessage = checkForDeregistrationError(connection, ipAddress, portNumber, nodeID);
	if (!statusMessage.equals(""))
	    status = -1;
	
	try {
	    if (status != -1) {
		// deregister node
		sortedEntries.remove(nodeID);
		connectionCache.removeConnection(nodeID);
		statusMessage = "Deregistration request successful. The number of messaging nodes currently constituting the overlay is (" + sortedEntries.size() + ")";
		deregistrationStatus = createDeregistrationStatus(nodeID, statusMessage);
		//Debug
		System.out.println("Removed node with ID: " + nodeID);
	    } else {
		deregistrationStatus = createDeregistrationStatus(status, statusMessage);
		//Debug
		System.out.println("Failed to remove node with ID: " + nodeID);	
	    }
	    connection.sendMessage(deregistrationStatus);	    
	} catch (IOException ioe) {
	    System.out.println(ioe.getMessage());
	}
    }
    
    // Message Type 7
    private void onReceivedSetupStatus(TCPConnection connection, Event event) {
	NodeReportsOverlaySetupStatus setupStatus = (NodeReportsOverlaySetupStatus) event;
	int status = setupStatus.getStatus();
	if (status == -1) {
	    System.out.println(setupStatus.getInfo());
	} else {
	    readyNodes++;
	    if (readyNodes == sortedEntries.size())
		System.out.println("Registry is now ready to initiate tasks.");
	}
    }

    // Message Type 10
    private void onReceivedTaskFinishedReport(TCPConnection connection, Event event) {
	OverlayNodeReportsTaskFinished taskFinishedReport = (OverlayNodeReportsTaskFinished) event;
	finishedNodes++;
	if (finishedNodes == sortedEntries.size()) {
	    requestTrafficSummary();
	}
    }

    // Message Type 12
    private void onReceivedTrafficSummary(TCPConnection connection, Event event) {
	
    }
    
    private String checkForRegistrationError(TCPConnection connection,
					     InetAddress ipAddress, int portNumber) {
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

    private String checkForDeregistrationError(TCPConnection connection, InetAddress ipAddress,
					       int portNumber, int nodeID) {
	
	// check if previously registered
	if (!sortedEntries.containsKey(nodeID)) {
	    return "Error Deregistering Node: Node " + nodeID + " not previously registered.";
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
	RegistryReportsRegistrationStatus statusMessage =
	    new RegistryReportsRegistrationStatus(status, infoString);
	return statusMessage.getBytes();
    }

    private byte[] createDeregistrationStatus(int status, String infoString) throws IOException {
	RegistryReportsDeregistrationStatus statusMessage =
	    new RegistryReportsDeregistrationStatus(status, infoString);
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

    private void requestTrafficSummary() {
	try {
	    RegistryRequestsTrafficSummary requestTrafficSummary =
		new RegistryRequestsTrafficSummary();
	    byte[] requestTrafficSummaryMessage = requestTrafficSummary.getBytes();
	    for (TCPConnection connection : connectionCache.getConnectionList()) {
		connection.sendMessage(requestTrafficSummaryMessage);
		//Debug
		//System.out.println("Sent traffic summary request");
	    }
	} catch (IOException ioe) {
	    System.out.println(ioe.getMessage());
	}
    }
}
