package cs455.overlay.transport;

import cs455.overlay.node.*;
import java.net.*;
import java.io.*;

public class TCPConnection {

    //Identifier info for cache
    private InetAddress remoteIP;
    private int remotePort;
    
    private Node node;
    private Socket socket;
    private TCPReceiver receiverThread;
    private TCPSender senderThread;
    
    public TCPConnection(Node node, Socket socket) {
	this.node = node;
	this.socket = socket;
	remoteIP = socket.getInetAddress();
	remotePort = socket.getPort();

	try {
	    this.receiverThread = new TCPReceiver(node, socket);
	    this.senderThread = new TCPSender(socket);
	    receiverThread.start();
        } catch (SocketException se) {
	    System.out.println(se.getMessage());
	} catch (IOException ioe) {
	    System.out.println(ioe.getMessage());
	}
    }
    
    public void sendMessage(byte[] dataToSend) throws IOException {
	senderThread.sendData(dataToSend);
    }
    
}
