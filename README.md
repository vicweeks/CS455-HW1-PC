# CS455-HW1-PC
## Intro to Distributed Systems: Homework 1
### Victor J Weeks

## Class Descriptions:

### node
	* MessagingNode.java : This class represents the nodes that will be interacting with each other.
	
	* Node.java : An interface which both Registry and MessagingNode implement.
	
	* Registry.java : This class is the central manager in setting up the overlay.

### routing
	* RoutingEntry.java : Structure that contains info needed to open a connection to a particular node.
	
	* RoutingTable.java : Structure that contains the relevent routing entries and routing table details.

### transport
	* TCPConnection.java : This class handles the creation of threads to run the TCPSender and TCPReceiver.
	
	* TCPConnectionsCache.java : Structure that stores all established TCPConnections in a map with the assigned node ID as the key.

	* TCPReceiver.java : This class handles the receiving of data.

	* TCPSender.java : This class handles the sending of data.

	* TCPServerThread.java : This class sets up the ServerSocket for a node within its own thread.

### util
	* InteractiveCommandParser.java : Ran by a thread to listen for user input commands while program is running.
	
	* StatisticsCollectorAndDisplay.java : Collects the traffic details from each node and prints them in a table to the console.

### wireformats
	* EventFactory.java : This class takes a byte buffer and extracts the type of message from it. It then constructs and returns the corresponding Event.

	* Event.java : An interface which all message types implement.

	* MessagingProtocol.java : Responsible for the logic of the messaging nodes in behavior both with the registry as well as communication between messaging nodes.

	* RegistryProtocol.java : Responsible for the logic of the registry in setting up the overlay and requesting the initiation of communication task for each messaging node.

	* OverlaySetup.java : Handles the logic for setting up an overlay for each messaging node.

	-(11 Message types) : Transforms relevant info into byte arrays for communication between nodes.
	     * NodeReportsOverlaySetupStatus.java

	     * OverlayNodeReportsTaskFinished.java

	     * OverlayNodeReportsTrafficSummary.java

	     * OverlayNodeSendsData.java

	     * OverlayNodeSendsDeregistration.java

	     * OverlayNodeSendsRegistration.java

	     * RegistryReportsDeregistrationStatus.java

	     * RegistryReportsRegistrationStatus.java

	     * RegistryRequestsTaskInitiate.java

	     * RegistryRequestsTrafficSummary.java

	     * RegistrySendsNodeManifest.java