package cs455.overlay.routing;

import java.util.ArrayList;

public class RoutingTable {

    private RoutingEntry localEntry;
    private int tableSize;
    private int numberOfNodes;
    private ArrayList<Integer> allNodeIDs;
    private ArrayList<RoutingEntry> connectedNodes;

    public RoutingTable(RoutingEntry localEntry, int tableSize, int numberOfNodes, ArrayList<Integer> allNodeIDs) {
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

    public void setTableSize(int tableSize) {
	this.tableSize = tableSize;
    }

    public int getTableSize() {
	return tableSize;
    }

    public int getNumberOfNodes() {
	return numberOfNodes;
    }
    
    public ArrayList<Integer> getAllIDs() {
	return allNodeIDs;
    }

    public ArrayList<RoutingEntry> getConnectedNodes() {
	return connectedNodes;
    }
    
    public void addEntry(int nodeID, byte[] ipAddress, int portNumber) {
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
