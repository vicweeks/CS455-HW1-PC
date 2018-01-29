package cs455.overlay.node;

import cs455.overlay.wireformats.*;
import cs455.overlay.transport.*;
import java.net.*;
import java.io.*;

public class MessagingNode implements Node {

    private TCPConnection registryConnection;
    private byte[] ipAddress;
    private int localPortNumber;
    private MessagingProtocol protocol;
    
    public void onEvent(TCPConnection connection, Event event) {
	protocol.onEvent(connection, event);
    }
    
    public static void main(String[] args) throws IOException {

	MessagingNode messagingNode = new MessagingNode();
	
        if (args.length != 2) {
            System.err.println(
                "Usage: java cs455.overlay.node.MessagingNode <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int registryPortNumber = Integer.parseInt(args[1]);

	messagingNode.setUpServerThread(messagingNode);
	
	System.out.println("Messaging Node has been started.");

	byte[] registerMessageBytes = messagingNode.createRegistrationMessage();
	
	messagingNode.connectToRegistry(messagingNode, hostName, registryPortNumber);
	messagingNode.protocol = new MessagingProtocol(messagingNode.registryConnection);
	messagingNode.registryConnection.sendMessage(registerMessageBytes);
	
    }

    public void setUpServerThread(Node messagingNode) {
	TCPServerThread server = new TCPServerThread(messagingNode, 0);
	localPortNumber = server.getPortNumber();
	Thread serverThread = new Thread(server);
	serverThread.start();
    }
    
    public byte[] createRegistrationMessage() throws IOException {
	try {
	    InetAddress localHost = InetAddress.getLocalHost();
	    ipAddress = localHost.getAddress();
	} catch (UnknownHostException e) {
	    System.out.println(e.getMessage());
	    System.exit(1);
	}
	OverlayNodeSendsRegistration registerMessage = new OverlayNodeSendsRegistration(ipAddress, localPortNumber);
	return registerMessage.getBytes();
    }
    
    public void connectToRegistry(Node messagingNode, String hostName, int portNumber) throws UnknownHostException, IOException {
	Socket registrySocket = new Socket(hostName, portNumber);	
	registryConnection = new TCPConnection(messagingNode, registrySocket);
	registryConnection.setUpConnection(registryConnection);
    }
    
}
