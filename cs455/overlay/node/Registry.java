package cs455.overlay.node;

import cs455.overlay.wireformats.*;
import cs455.overlay.transport.*;
import java.net.*;
import java.io.*;

public class Registry implements Node {

    public void onEvent(Event event) {

    }
    
    public static void main(String[] args) throws IOException {

	Registry registry = new Registry();
	
        if (args.length != 1) {
            System.err.println("Usage: java cs455.overlay.node.Registry <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

	registry.setUpServerThread(portNumber);
	     
    }

    public void setUpServerThread(int portNumber) {
	TCPServerThread serverThread = new TCPServerThread(portNumber);
	serverThread.start();
    }
    
}
