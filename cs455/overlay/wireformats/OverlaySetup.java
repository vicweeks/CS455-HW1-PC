package cs455.overlay.wireformats;

import cs455.overlay.routing.*;
import cs455.overlay.transport.*;
import java.util.ArrayList;
import java.io.*;
import java.net.*;

public class OverlaySetup {

    private RoutingTable registryRoutingTable;
    private ArrayList<RoutingTable> allRoutingTables;
    private TCPConnectionsCache connectionCache;
    
    public OverlaySetup(RoutingTable registryRoutingTable, TCPConnectionsCache connectionCache) {
	this.registryRoutingTable = registryRoutingTable;
	this.connectionCache = connectionCache;
	constructNodeRoutingTables();
    }

    public ArrayList<RoutingTable> getAllTables() {
	return allRoutingTables;
    }
    
    private void constructNodeRoutingTables() {
	int numberOfNodes = registryRoutingTable.getNumberOfNodes();
	allRoutingTables = new ArrayList<RoutingTable>(numberOfNodes);
	ArrayList<RoutingEntry> allNodeEntries = registryRoutingTable.getConnectedNodes();
	int tableSize = registryRoutingTable.getTableSize();
	ArrayList<Integer> allNodeIDs = registryRoutingTable.getListIDs();
	
	for(RoutingEntry entry : allNodeEntries) {
	    int nodeID = entry.getNodeID();
	    ArrayList<RoutingEntry> nodesToConnect = calculateRoutingTable(nodeID, tableSize, numberOfNodes, allNodeIDs);
	    RoutingTable nodeTable = new RoutingTable(entry, tableSize, numberOfNodes, allNodeIDs, nodesToConnect);
	    allRoutingTables.add(nodeTable);
	}
    }

    private ArrayList<RoutingEntry> calculateRoutingTable(int nodeID, int tableSize,
							  int numberOfNodes, ArrayList<Integer> allNodeIDs) {
	int idIndex = allNodeIDs.indexOf(nodeID);
	ArrayList<RoutingEntry> nodesToConnect = new ArrayList<RoutingEntry>(tableSize);
	int[] connectedIDs = new int[tableSize];
	for (int i=0; i<tableSize; i++) {
	    int nextIndex = (int) ((idIndex + Math.pow(2, i)) % numberOfNodes);
	    connectedIDs[i] = allNodeIDs.get(nextIndex);
	}
	for (int nextID : connectedIDs) {
	    TCPConnection nextConnection = connectionCache.getConnection(nextID);
	    RoutingEntry node = new RoutingEntry(nextID, nextConnection.getRemoteIP(), nextConnection.getRemotePort());
	    nodesToConnect.add(node);
	}
	return nodesToConnect;
    }

    public byte[] constructNodeManifest(RoutingTable nodeTable)
	throws UnknownHostException, IOException {
	int tableSize = registryRoutingTable.getTableSize();
	int numNodeIDs = registryRoutingTable.getNumberOfNodes();
	ArrayList<Integer> allNodeIDs = registryRoutingTable.getListIDs();
     
	RoutingEntry nodeEntry = nodeTable.getLocalEntry();
	ArrayList<RoutingEntry> nodesToConnect = nodeTable.getConnectedNodes();
	RegistrySendsNodeManifest nodeManifest =
	    new RegistrySendsNodeManifest(tableSize, nodesToConnect, numNodeIDs, allNodeIDs);
	return nodeManifest.getBytes();	
    }
    
}
