package cs455.overlay.transport;

import java.net.*;
import java.io.*;

public class TCPConnection {

    private TCPReceiver receiverThread;
    private TCPSender senderThread;
    
    public TCPConnection(Socket socket) {
	try {
	    this.receiverThread = new TCPReceiver(socket);
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
