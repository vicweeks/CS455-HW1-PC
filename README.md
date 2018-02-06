# CS455-HW1-PC
## Intro to Distributed Systems: Homework 1
### Victor J Weeks

## Usage:
* Create one registry with the command ```java cs455.overlay.node.Registry <portnum>``` where <portnum> is the desired port for the ServerSocket.
* Create multiple instances of messaging nodes with ```java cs455.overlay.node.MessagingNode <registry-host> <registry-port>``` where <registry-host> is the name of the host on which the registry resides and <registry-port> is the corresponding port number chosen for the registry in the previous command.
* After all messaging nodes have register with the registry (happens upon startup of each messaging node), the registry will accept a number of running commands:
  * ```list-messaging-nodes``` will print information (hostname, port-number, nodeID) about each messaging node currently registered to the console.
  * ```setup-overlay <number-of-routing-table-entries>``` will setup the overlay with the indicated number of entries and send that information to the corresponding messaging node. Upon completion the registry will print that it is ready to initiate tasks.
  * ```list-routing-tables``` when this command is invoked after overlay setup, it will print the individual routing table for each messaging node to the console.
  * ```start <number-of-messages>``` will request each messaging node to send the indicated number of messages to a randomly chosen node (excluding itself). Each node will determine the path for each individual packet to reach the indicated sink node.
* Messaging nodes will also accept the following running commands:
  * ```print-counters-and-diagnostics``` will cause the particular node to print information about traffic at the time of the command to the console. This info will indicate the number of messages the node has sent, received, and relayed, as well as the sums of the messages sent and received.
  * ```exit-overlay``` This command is intended to be called *before* overlay setup. It will cause the particular node to request deregistration from the registry, close any open sockets, and terminate the process.

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

* (11 Message types) : Transforms relevant info into byte arrays for communication between nodes.
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