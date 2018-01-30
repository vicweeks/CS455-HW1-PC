package cs455.overlay.routing;

import java.util.ArrayList;
import java.net.*;

public class RoutingTable {

    private RoutingEntry localEntry;
    private int tableSize;
    private int numberOfNodes;
    private ArrayList<Integer> allNodeIDs;
    private ArrayList<RoutingEntry> connectedNodes;

    public RoutingTable(RoutingEntry localEntry, int tableSize, int numberOfNodes,
			ArrayList<Integer> allNodeIDs) {
	this.localEntry = localEntry;
	this.tableSize = tableSize;
	this.numberOfNodes = numberOfNodes;
	this.allNodeIDs = allNodeIDs;
	this.connectedNodes = new ArrayList<RoutingEntry>(tableSize);
    }

    public RoutingTable() {
	this.localEntry = null;
	this.tableSize = 0;
	this.numberOfNodes = 0;
	this.allNodeIDs = new ArrayList<Integer>();
	this.connectedNodes = new ArrayList<RoutingEntry>();
    }

    public RoutingEntry getLocalEntry() {
	return localEntry;
    }
    
    public void setTableSize(int tableSize) {
	this.tableSize = tableSize;
    }

    public int getTableSize() {
	return tableSize;
    }

    public int getNumberOfNodes() {
	return numberOfNodes;
    }

    public ArrayList<Integer> getListIDs() {
	return allNodeIDs;
    }
    
    public int[] getAllIDs() {
	int[] idArray = new int[numberOfNodes];
	for (int i=0; i<idArray.length; i++) {
	    idArray[i] = allNodeIDs.get(i);
	}
	return idArray;
    }

    public ArrayList<RoutingEntry> getConnectedNodes() {
	return connectedNodes;
    }

    public void addEntry(int nodeID, InetAddress ipAddress, int portNumber) {
	connectedNodes.add(new RoutingEntry(nodeID, ipAddress, portNumber));
    }
    
    public void registerEntry(int nodeID, InetAddress ipAddress, int portNumber) {
	numberOfNodes += 1;
	allNodeIDs.add(nodeID);
	connectedNodes.add(new RoutingEntry(nodeID, ipAddress, portNumber));
    }

    public void removeEntry(int nodeID) {
	if (allNodeIDs.contains(nodeID)) {
	    allNodeIDs.remove(nodeID);
	    for (RoutingEntry entry : connectedNodes) {
		if (entry.getNodeID() == nodeID) {
		    connectedNodes.remove(entry);
		    return;
		}
	    }
	} else {
	    System.out.println("Error removing node: node is not registered.");
	}
    }
    
}
