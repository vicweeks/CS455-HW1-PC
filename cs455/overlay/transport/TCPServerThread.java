package cs455.overlay.transport;

import java.net.*;
import java.io.*;

public class TCPServerThread extends Thread {

    private ServerSocket serverSocket;
    
    public TCPServerThread(int portNumber) {
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
		TCPConnection connection = new TCPConnection(socket);
	    } catch (IOException e) {
		System.out.println("Exception caught when setting up serverSocket on port.");
		System.out.println(e.getMessage());
	    }
	}
    }

}
