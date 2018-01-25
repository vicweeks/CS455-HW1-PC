CS455 HW1-PC
Victor Weeks
January 19, 2018

Class Descriptions:

wireformats
	-Protocol.java
	
	-Event.java : An interface which all message types implement.

	-EventFactory.java : This class takes a byte buffer and extracts the type of message from it. It then constructs the corresponding Event.

	-(11 Message types, each with their own class)

routing
	-RoutingTable.java
	-RoutingEntry.java

util
	-InteractiveCommandParser.java
	-StatisticsCollectorAndDisplay.java

transport
	-TCPServerThread.java : This class sets up the ServerSocket for a node within its own thread.
	
	-TCPConnection.java : This class handles the creation of threads to run the TCPSender and TCPReceiver.
	
	-TCPSender.java : This class handles the sending of data.
	
	-TCPReceiver.java : This class handles the receiving of data.
	
	-TCPConnectionsCache.java

node
	-Node.java : An interface which both Registry and MessagingNode implement.
	
	-Registry.java : This class is the central manager in setting up the overlay.

	-MessagingNode.java : This class represents the nodes that will be interacting with each other.
