package cs455.overlay.wireformats;

import cs455.overlay.routing.*;
import cs455.overlay.transport.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;

public class MessagingProtocol {

    private int localNodeID;
    private TCPConnectionsCache connectionCache;
    private RoutingTable routingTable;
    
    public MessagingProtocol(TCPConnection registryConnection) {
        connectionCache = new TCPConnectionsCache();
	connectionCache.addConnection(-1, registryConnection);
	routingTable = new RoutingTable();
    }

    public void onEvent(TCPConnection connection, Event event) {
	int eventType = event.getType();
	switch(eventType)
	    {
	    case 3: onReceivedRegistrationStatus(connection, event);
		break;
	    case 5:
		break;
	    case 6:
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
    
    public void sendMessage(TCPConnection connection, byte[] message) {
	try {
	    connection.sendMessage(message);
	} catch (IOException e) {
	    System.out.println("Failed to send message");
	}
    }
    
}
