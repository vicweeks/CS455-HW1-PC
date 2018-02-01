package cs455.overlay.transport;

import cs455.overlay.node.*;
import java.net.*;
import java.io.*;

public class TCPServerThread implements Runnable {

    private Node node;
    private ServerSocket serverSocket;
    
    public TCPServerThread(Node node, int portNumber) {
	this.node = node;
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

    public void run() {
	while(serverSocket != null) {
	    try {
		Socket socket = serverSocket.accept();		
		TCPConnection connection = new TCPConnection(node, socket);
		connection.setUpConnection(connection);
	    } catch (IOException e) {
		System.out.println("Exception caught when setting up serverSocket on port.");
		System.out.println(e.getMessage());
	    }
	}
    }

}
