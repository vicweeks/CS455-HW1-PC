package cs455.overlay.node;

import cs455.overlay.wireformats.*;
import cs455.overlay.transport.*;
import cs455.overlay.util.*;
import cs455.overlay.routing.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Registry implements Node {

    private RegistryProtocol protocol;
    
    public void onEvent(TCPConnection connection, Event event) {
	protocol.onEvent(connection, event);
    }
    
    public static void main(String[] args) throws IOException {

	Registry registry = new Registry();
	
        if (args.length != 1) {
            System.err.println("Usage: java cs455.overlay.node.Registry <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

	registry.protocol = new RegistryProtocol();
	
	registry.setUpServerThread(registry, portNumber);
	
	System.out.println("Registry has been started.");
	
	//listen for commands
	registry.runtimeCommands();
	     
    }

    public void setUpServerThread(Node registry, int portNumber) {
	TCPServerThread server = new TCPServerThread(registry, portNumber);
	Thread serverThread = new Thread(server);
	serverThread.start();
    }

    public void runtimeCommands() {
	InteractiveCommandParser icp = new InteractiveCommandParser();
	Thread icpThread = new Thread(icp);
	icpThread.start();
    }
    
}
