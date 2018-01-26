package cs455.overlay.transport;

import cs455.overlay.node.*;
import java.net.*;
import java.io.*;

public class TCPServerThread extends Thread {

    private Node node;
    private ServerSocket serverSocket;
    private TCPConnectionsCache connectionCache;
    
    public TCPServerThread(Node node, int portNumber) {
	this.node = node;
	this.connectionCache = new TCPConnectionsCache();
        try {
	    serverSocket = new ServerSocket(portNumber);
	} catch (IOException e) {
	    System.out.println("Error setting up serverSocket");
	    System.out.println(e.getMessage());
	}
    }

    public int getPortNumber() {
	return serverSocket.getLocalPort();
    }

    public TCPConnectionsCache getConnectionCache() {
	return connectionCache;
    }
    
    public void run() {
	while(serverSocket != null) {
	    try {
		Socket socket = serverSocket.accept();		
		TCPConnection connection = new TCPConnection(node, socket);
		connectionCache.addConnection(connection);
	    } catch (IOException e) {
		System.out.println("Exception caught when setting up serverSocket on port.");
		System.out.println(e.getMessage());
	    }
	}
    }

}
