package cs455.overlay.node;

import cs455.overlay.wireformats.*;
import cs455.overlay.transport.*;
import cs455.overlay.util.*;
import java.net.*;
import java.io.*;

public class Registry implements Node {

    private Protocol protocol;
    // arraylist of routing tables
    
    public void onEvent(Event event) {
	protocol.onEvent(event);
    }
    
    public static void main(String[] args) throws IOException {

	Registry registry = new Registry();
	registry.protocol = new Protocol();
	
        if (args.length != 1) {
            System.err.println("Usage: java cs455.overlay.node.Registry <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

	registry.setUpServerThread(registry, portNumber);

	//listen for commands
	registry.runtimeCommands();
	     
    }

    public void setUpServerThread(Node registry, int portNumber) {
	TCPServerThread serverThread = new TCPServerThread(registry, portNumber);
	serverThread.start();
    }

    public void runtimeCommands() {
	InteractiveCommandParser icp = new InteractiveCommandParser();
	icp.start();
    }
    
}
