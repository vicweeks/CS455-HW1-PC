package cs455.overlay.wireformats;

import cs455.overlay.routing.*;
import cs455.overlay.transport.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;

public class RegistryProtocol {

    private TCPConnectionsCache connectionCache;
    private RoutingTable registryRoutingTable;
    
    public RegistryProtocol() {
	connectionCache = new TCPConnectionsCache();
	registryRoutingTable = new RoutingTable();
    }

    public TCPConnectionsCache getConnectionCache() {
	return connectionCache;
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
	byte[] ipAddress = registrationEvent.getIPAddress();
	int portNumber = registrationEvent.getPortNumber();
	int status = 0;
	String errorMessage = "";
	byte[] registrationStatus = null;
	int nodeID = -1;

	ArrayList<RoutingEntry> allRoutingEntries = registryRoutingTable.getConnectedNodes();
	
	// check if node had previously registered
	for (RoutingEntry rEntry : allRoutingEntries) {
	    if (rEntry.getIPAddress().equals(ipAddress) && rEntry.getPortNumber() == portNumber) {
		errorMessage = "Error Registering Node: Node already registered.";
		status = -1;
	    }
	}

	/*TODO
	// ensure that the IP address in the message matches where the request originated
        if (!connection.getLocalIP().equals(ipAddress)) {
	    System.out.println("Originating IP: " + connection.getRemoteIP());
	    System.out.println("Requested IP: " + ipAddress);
	    errorMessage = "Error Registering Node: Originating IP address does not match IP address in message.";
	    status = -1;
	}
	*/
	try {
	    if (status != -1) {
		// Generate unique node ID
		nodeID = generateNodeID();
		registryRoutingTable.addEntry(nodeID, ipAddress, portNumber);
		String infoString = "Registration request successful. The number of messaging nodes currently constituting the overlay is (" + registryRoutingTable.getConnectedNodes().size() + ")";
		registrationStatus = createRegistrationStatus(nodeID, infoString);
		connectionCache.addConnection(nodeID, connection);
		System.out.println("Added node with id: " + nodeID);
	    } else {
		registrationStatus = createRegistrationStatus(status, errorMessage);
		System.out.println("Failed to add node");
	    }
	} catch (IOException e) {
	    System.out.println(e.getMessage());
	}
	

	try {
	    sendMessage(connection, registrationStatus);
	} catch (IOException e) {
		System.out.println(e.getMessage());
	}
    }

    private int generateNodeID() {
	int uNodeID = -1;
	int randomID;
	while (uNodeID < 0) {
	    randomID = ThreadLocalRandom.current().nextInt(0, 128);
	    if (!registryRoutingTable.getAllIDs().contains(randomID))
		uNodeID = randomID;
	}
	return uNodeID;
    }

    private byte[] createRegistrationStatus(int status, String infoString) throws IOException {
	RegistryReportsRegistrationStatus statusMessage = new RegistryReportsRegistrationStatus(status, infoString);
	return statusMessage.getBytes();
    }

    public void sendMessage(TCPConnection connection, byte[] message) throws IOException {
	connection.sendMessage(message);
        
    }
    
}
