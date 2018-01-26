package cs455.overlay.routing;

public class RoutingTable {

    private RoutingEntry localEntry;
    private int tableSize;
    private int numberOfNodes;
    private int[] allNodeIDs;
    private RoutingEntry[] connectedNodes;

    public RoutingTable(RoutingEntry localEntry, int tableSize, int numberOfNodes, int[] allNodeIDs) {
	this.localEntry = localEntry;
	this.tableSize = tableSize;
	this.numberOfNodes = numberOfNodes;
	this.allNodeIDs = allNodeIDs;
	this.connectedNodes = new RoutingEntry[tableSize];
    }
    
}
