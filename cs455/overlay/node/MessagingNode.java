package cs455.overlay.node;

import cs455.overlay.wireformats.*;
import cs455.overlay.transport.*;
import cs455.overlay.util.*;
import java.net.*;
import java.io.*;

public class MessagingNode implements Node {

    private TCPConnection registryConnection;
    private MessagingProtocol protocol;
    
    public void onEvent(TCPConnection connection, Event event) {
	protocol.onEvent(connection, event);
    }
    
    public static void main(String[] args) throws IOException {

	MessagingNode m = new MessagingNode();
	
        if (args.length != 2) {
            System.err.println(
                "Usage: java cs455.overlay.node.MessagingNode <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int registryPortNumber = Integer.parseInt(args[1]);

	int localPortNumber = m.setUpServerThread(m);
	
	System.out.println("Messaging Node has been started.");
	
	try {
	    byte[] registerMessageBytes = m.createRegistrationMessage(localPortNumber);
	    m.connectToRegistry(m, hostName, registryPortNumber);
	    m.protocol = new MessagingProtocol(m.registryConnection);
	    m.registryConnection.sendMessage(registerMessageBytes);
	} catch(UnknownHostException uhe) {
	    System.out.println(uhe.getMessage());
	} catch(IOException ioe) {
	    System.out.println(ioe.getMessage());
	}

	m.runtimeCommands(m.protocol);
	
    }

    public int setUpServerThread(Node m) {
	int portNumber = -1;
	TCPServerThread server = new TCPServerThread(m, 0);
	portNumber = server.getPortNumber();
	Thread serverThread = new Thread(server);
	serverThread.start();
	return portNumber;
    }
    
    public byte[] createRegistrationMessage(int localPortNumber) throws UnknownHostException, IOException {
	InetAddress ipAddress = InetAddress.getLocalHost();
	OverlayNodeSendsRegistration registerMessage = new OverlayNodeSendsRegistration(ipAddress, localPortNumber);
	return registerMessage.getBytes();
    }
    
    public void connectToRegistry(Node messagingNode, String hostName, int portNumber) throws UnknownHostException, IOException {
	Socket registrySocket = new Socket(hostName, portNumber);	
	registryConnection = new TCPConnection(messagingNode, registrySocket);
	registryConnection.setUpConnection(registryConnection);
    }

    public void runtimeCommands(MessagingProtocol protocol) {
	InteractiveCommandParser icp = new InteractiveCommandParser(false, protocol);
	Thread icpThread = new Thread(icp);
	icpThread.start();
    }
    
}
