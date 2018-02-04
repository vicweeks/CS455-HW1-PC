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
    private InetAddress localIPAddress;
    private int localPortNumber;
    private TCPConnectionsCache connectionCache;
    private RoutingTable routingTable;
    private int sendTracker = 0;
    private int receiveTracker = 0;
    private int relayTracker = 0;
    private long sendSummation = 0L;
    private long receiveSummation = 0L;
    
    public MessagingProtocol(MessagingNode self, TCPConnection registryConnection, int localPortNumber) {
	this.self = self;
	this.localIPAddress = registryConnection.getLocalIP();
	this.localPortNumber = localPortNumber;
	connectionCache = new TCPConnectionsCache();
	connectionCache.addConnection(-1, registryConnection);
    }
    
    // Command: print-counters-and-diagnostics
    public void printDiagnostics() {
	System.out.printf("|%12s|    |%16s|    |%15s|    |%19s|    |%19s|\n",
			  "Packets Sent", "Packets Received", "Packets Relayed",
			  "Sum Values Sent", "Sum Values Received");
	System.out.printf("|%,12d|    |%,16d|    |%,15d|    |%+,19d|    |%+,19d|\n",
			  sendTracker, receiveTracker, relayTracker, sendSummation, receiveSummation);
    }
    
    // Command: exit-overlay
    public void exitOverlay() {
	try {
	    OverlayNodeSendsDeregistration exit =
		new OverlayNodeSendsDeregistration(localIPAddress, localPortNumber, localNodeID);
	    byte[] exitMessage = exit.getBytes();
	    TCPConnection connection = connectionCache.getConnection(-1);
	    connection.sendMessage(exitMessage);
	} catch (UnknownHostException uhe) {
	    System.out.println(uhe.getMessage());
	} catch (IOException ioe) {
	    System.out.println(ioe.getMessage());
	}
    }
	    
    public void onEvent(TCPConnection connection, Event event) {
	int eventType = event.getType();
	switch(eventType)
	    {
	    case 3: onReceivedRegistrationStatus(connection, event);
		break;
	    case 5: onReceivedDeregistrationStatus(connection, event);
		break;
	    case 6: onReceivedNodeManifest(connection, event);
		break;
	    case 8: onReceivedTaskInitiateRequest(connection, event);
		break;
	    case 9: onReceivedData(connection, event);
		break;
	    case 11: onReceivedTrafficSummaryRequest(connection, event);
		break;
	    default: System.out.println("Error in MessagingProtocol: message type " + eventType + " is invalid.");
		System.exit(1);
	}
    }

    // Message Type 3
    private void onReceivedRegistrationStatus(TCPConnection connection, Event event) {
	RegistryReportsRegistrationStatus registrationResponse =
	    (RegistryReportsRegistrationStatus) event;
	localNodeID = registrationResponse.getStatus();
	String infoString = registrationResponse.getInfo();

	System.out.println(infoString);
    }

    // Message Type 5
    private void onReceivedDeregistrationStatus(TCPConnection connection, Event event) {
	RegistryReportsDeregistrationStatus deregistrationResponse =
	    (RegistryReportsDeregistrationStatus) event;
	if (deregistrationResponse.getStatus() == -1)
	    System.out.println(deregistrationResponse.getInfo());
	else
	    self.close(connection);
    }
    
    // Message Type 6
    private void onReceivedNodeManifest(TCPConnection connection, Event event) {
	RegistrySendsNodeManifest nodeManifest = (RegistrySendsNodeManifest) event;
	int routingTableSize = nodeManifest.getRoutingTableSize();
	ArrayList<RoutingEntry> nodesToConnect = nodeManifest.getRoutingNodes();
	int numNodeIDs = nodeManifest.getNumNodes();
	ArrayList<Integer> allNodeIDs = nodeManifest.getAllNodeIDs();
	RoutingEntry localEntry =
	    new RoutingEntry(localNodeID, localIPAddress, localPortNumber);
	routingTable =
	    new RoutingTable(localEntry, routingTableSize, numNodeIDs, allNodeIDs, nodesToConnect);
	initiateConnections(nodesToConnect);
    }

    // Message Type 8
    private void onReceivedTaskInitiateRequest(TCPConnection connection, Event event) {
	RegistryRequestsTaskInitiate taskInitiateRequest = (RegistryRequestsTaskInitiate) event;
	int numPacketsToSend = taskInitiateRequest.getNumPacketsToSend();
        try {
	    for (int i=0; i<numPacketsToSend; i++) {
		sendDataPacket();
	    }
	    reportTaskFinished();
	} catch (IOException ioe) {
	    System.out.println(ioe.getMessage());
	}	
    }

    // Message Type 9
    private void onReceivedData(TCPConnection connection, Event event) {
	OverlayNodeSendsData dataPacket = (OverlayNodeSendsData) event;
	int destID = dataPacket.getDestID();
	try {
	    if (destID != localNodeID) {
		relayDataPacket(dataPacket);
	    } else {
		receiveDataPacket(dataPacket);
	    }
	} catch (IOException ioe) {
	    System.out.println(ioe.getMessage());
	}
    }

    // Message Type 11
    private void onReceivedTrafficSummaryRequest(TCPConnection connection, Event event) {
	System.out.println("Received Request for Traffic Summary Report");
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
		routingConnection.setUpConnection(routingConnection);
		connectionCache.addConnection(nodeToConnectID, routingConnection);
	    }
	} catch (UnknownHostException uhe) {
	    statusMessage = "Node " + localNodeID + " failed to connect to node "
		+ nodeToConnectID;
	    status = -1;
	    System.out.println(uhe.getMessage());
	} catch (IOException ioe) {
	    statusMessage = "Node " + localNodeID + " failed to connect to node "
		+ nodeToConnectID;
	    status = -1;
	    System.out.println(ioe.getMessage());
	}
	reportOverlaySetupStatus(status, statusMessage);
    }

    private void reportOverlaySetupStatus(int status, String statusMessage) {
	try {
	    NodeReportsOverlaySetupStatus setupStatus =
		new NodeReportsOverlaySetupStatus(status, statusMessage);
	    TCPConnection connection = connectionCache.getConnection(-1);
	    byte[] setupStatusMessage = setupStatus.getBytes();
	    connection.sendMessage(setupStatusMessage);
	} catch (IOException ioe) {
	    System.out.println(ioe.getMessage());
	}
    }

    private void reportTaskFinished() throws IOException {
	TCPConnection connection = connectionCache.getConnection(-1);
	OverlayNodeReportsTaskFinished reportTaskFinished =
	    new OverlayNodeReportsTaskFinished(localIPAddress, localPortNumber, localNodeID);
	byte[] reportTaskFinishedMessage = reportTaskFinished.getBytes();
	connection.sendMessage(reportTaskFinishedMessage);
    }
    
    private void sendDataPacket() throws IOException {
	int sinkNodeID = chooseRandomSink();	
	TCPConnection linkConnection = chooseSendingLink(sinkNodeID);
	int payload = generatePayload();
	OverlayNodeSendsData dataPacket = new OverlayNodeSendsData(sinkNodeID, localNodeID, payload);
	byte[] dataPacketMessage = dataPacket.getBytes();	
	
	synchronized(this) {
	    linkConnection.sendMessage(dataPacketMessage);
	    this.sendTracker++;
	    this.sendSummation += payload;
	}	
    }

    private int chooseRandomSink() {
	ArrayList<Integer> allNodeIDs = routingTable.getListIDs();
	int randomID = -1;
	while(randomID < 0) {
	    randomID = allNodeIDs.get(ThreadLocalRandom.current().nextInt(allNodeIDs.size()));
	    if (randomID == localNodeID)
		randomID = -1;
	    else
		return randomID;
	}
	return randomID;
    }

    private TCPConnection chooseSendingLink(int sinkNodeID) {
	// decide about where to send packet
	TCPConnection connection = null;
	int targetDist = getDistBetweenNodes(localNodeID, sinkNodeID);

	for (RoutingEntry entry : routingTable.getConnectedNodes()) {
	    int entryID = entry.getNodeID();      
	    if (entryID == sinkNodeID) { // sink is in routing table
		connection = connectionCache.getConnection(sinkNodeID);
		return connection;
	    } else {
		int entryDist = getDistBetweenNodes(entryID, sinkNodeID);
		if (entryDist < targetDist) { // entry is before sink node
		    connection = connectionCache.getConnection(entryID);
		} else {
		    // do nothing, this would overshoot sink node
		}
	    }
	}
	
	return connection;
    }

    private int getDistBetweenNodes(int srcID, int destID) {
	ArrayList<Integer> allNodeIDs = routingTable.getListIDs();
	int numNodes = allNodeIDs.size();
	int srcIndex = allNodeIDs.indexOf(srcID);
	int destIndex = allNodeIDs.indexOf(destID);
	int distBetween = destIndex - srcIndex;
	if (distBetween < 0)
	    distBetween += numNodes;
	return distBetween;
    }
    
    private int generatePayload() {
	return ThreadLocalRandom.current().nextInt(-2147483648, 2147483647);
    }
    
    private void relayDataPacket(OverlayNodeSendsData dataPacket) throws IOException {
	ArrayList<Integer> dissTrace = dataPacket.getDisseminationTrace();
	dissTrace.add(localNodeID);
	OverlayNodeSendsData relayData =
	    new OverlayNodeSendsData(dataPacket.getDestID(), dataPacket.getSrcID(),
				     dataPacket.getPayload(), dissTrace);
	byte[] relayDataPacket = relayData.getBytes();
	int destID = dataPacket.getDestID();
	TCPConnection linkConnection = chooseSendingLink(destID);
	
	synchronized(this) {
	    linkConnection.sendMessage(relayDataPacket);
	    this.relayTracker++;
	}	
    }

    private void receiveDataPacket(OverlayNodeSendsData dataPacket) throws IOException {
	int srcID = dataPacket.getSrcID();
	int payload = dataPacket.getPayload();

	synchronized(this) {
	    this.receiveTracker++;
	    this.receiveSummation += payload;
	}
    }
    
}
