package cs455.overlay.node;

import cs455.overlay.wireformats.*;
import cs455.overlay.transport.*;
import java.net.*;
import java.io.*;

public class Registry implements Node {

    public void onEvent(Event event) {

    }
    
    public static void main(String[] args) throws IOException {
        
        if (args.length != 1) {
            System.err.println("Usage: java cs455.overlay.node.Registry <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

	TCPServerThread serverThread = new TCPServerThread(portNumber);
	serverThread.start();
	     
    }
    
}
