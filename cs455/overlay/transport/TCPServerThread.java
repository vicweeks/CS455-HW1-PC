package cs455.overlay.transport;

import java.net.*;
import java.io.*;

public class TCPServerThread extends Thread {

    private int portNumber;
    
    public TCPServerThread(int portNumber) {
        this.portNumber = portNumber;  
    }

    public void run() {
	try {
	     ServerSocket serverSocket = new ServerSocket(portNumber);
	     Socket socket = serverSocket.accept();
	     TCPConnection connection = new TCPConnection(socket);
	} catch (IOException e) {
	    System.out.println("Exception caught when setting up serverSocket on port "
			       + portNumber);
	    System.out.println(e.getMessage());
	}
    }

}
