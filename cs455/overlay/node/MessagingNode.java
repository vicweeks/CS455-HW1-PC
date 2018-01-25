package cs455.overlay.node;

import cs455.overlay.wireformats.*;
import cs455.overlay.transport.*;
import java.net.*;
import java.io.*;

public class MessagingNode implements Node {

    private TCPConnection registryConnection;
    private byte[] ipAddress;
    private int localPortNumber;
    
    public void onEvent(Event event) {

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

	messagingNode.setUpServerThread();

	byte[] registerMessageBytes = messagingNode.createRegistrationMessage();
	
	messagingNode.connectToRegistry(hostName, registryPortNumber);
	
	messagingNode.registryConnection.sendMessage(registerMessageBytes);
	
    }

    public void setUpServerThread() {
	TCPServerThread serverThread = new TCPServerThread(0);
	localPortNumber = serverThread.getPortNumber();
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
    
    public void connectToRegistry(String hostName, int portNumber) throws UnknownHostException, IOException {
	Socket registrySocket = new Socket(hostName, portNumber);
	registryConnection = new TCPConnection(registrySocket);
    }
    
}
