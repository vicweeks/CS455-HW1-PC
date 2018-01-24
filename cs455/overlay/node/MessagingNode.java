package cs455.overlay.node;

import cs455.overlay.wireformats.*;
import cs455.overlay.transport.*;
import java.net.*;
import java.io.*;

public class MessagingNode implements Node {

    public void onEvent(Event event) {

    }
    
    public static void main(String[] args) throws IOException {
        
        if (args.length != 2) {
            System.err.println(
                "Usage: java cs455.overlay.node.MessagingNode <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

	Socket registrySocket = new Socket(hostName, portNumber);
	TCPConnection registryConnection = new TCPConnection(registrySocket);
	
    }
    
}
