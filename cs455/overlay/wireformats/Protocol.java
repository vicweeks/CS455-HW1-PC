package cs455.overlay.wireformats;

import cs455.overlay.routing.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;

public class Protocol {

    private ArrayList<RoutingEntry> allRoutingEntries;
    private RoutingTable nodeRoutingTable;
    private ArrayList<Integer> assignedIDs;
    
    public Protocol() {
	allRoutingEntries = null;
	assignedIDs = null;
    }

    public Protocol(boolean isRegistry) {
	allRoutingEntries = new ArrayList<RoutingEntry>();
	assignedIDs = new ArrayList<Integer>();
    }

    public void onEvent(Event event) {
	int eventType = event.getType();
	switch(eventType)
	    {
	    case 2: onReceivedRegistrationRequest(event);
		break;
		//TODO
	    case 3:
		break;
	    case 4:
		break;
	    case 5:
		break;
	    case 6:
		break;
	    case 7:
		break;
	    case 8:
		break;
	    case 9:
		break;
	    case 10:
		break;
	    case 11:
		break;
	    case 12:
		break;
	    default: System.out.println("Error in EventFactory: message type " + eventType + " is invalid.");
		System.exit(1);
	}
    }

    private void onReceivedRegistrationRequest(Event event) {
	//TODO section 2.1
	OverlayNodeSendsRegistration registrationEvent = (OverlayNodeSendsRegistration) event;
	byte[] ipAddress = registrationEvent.getIPAddress();
	int portNumber = registrationEvent.getPortNumber();
	int status = 0;
	String errorMessage = "";
	byte[] registrationStatus = null;
	int nodeID = -1;
	// check if node had previously registered
	for (RoutingEntry rEntry : allRoutingEntries) {
	    if (rEntry.getIPAddress().equals(ipAddress) && rEntry.getPortNumber() == portNumber) {
		errorMessage = "Error Registering Node: Node already registered.";
		status = -1;
	    }
	}
	
	// ensure that the IP address in the message matches where the request originated
	//TODO
	try {
	if (status != -1) {
	    // Generate unique node ID
	    nodeID = generateNodeID();
	    String infoString = "Registration request successful. The number of messaging nodes currently constituting the overlay is (" + allRoutingEntries.size() + ")";
	
	    RoutingEntry registerNode = new RoutingEntry(nodeID, ipAddress, portNumber);
	    allRoutingEntries.add(registerNode);
	    registrationStatus = createRegistrationStatus(nodeID, infoString); 
	} else {
	    registrationStatus = createRegistrationStatus(status, errorMessage);
	}
	} catch (IOException e) {
	    //TODO handle this
	}
	System.out.println("Added node with id: " + nodeID);
    }

    private int generateNodeID() {
	int uNodeID = -1;
	int randomID;
	while (uNodeID < 0) {
	    randomID = ThreadLocalRandom.current().nextInt(0, 128);
	    if (!assignedIDs.contains(randomID))
		uNodeID = randomID;
	}
	return uNodeID;
    }

    private byte[] createRegistrationStatus(int status, String infoString) throws IOException {
	RegistryReportsRegistrationStatus statusMessage = new RegistryReportsRegistrationStatus(status, infoString);
	return statusMessage.getBytes();
    }
    
}
