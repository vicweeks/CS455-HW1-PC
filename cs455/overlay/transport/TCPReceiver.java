package cs455/overlay/transport;

import java.net.*;
import java.io.*;

public class TCPReceiver {

    private Socket socket;
    private DataInputStream din;

    public TCPReceiver(Socket socket) throws IOException {
	this.socket = socket;
	din = new DataInputStream(socket.getInputStream());
    }

    public void run() {
	int dataLength;
	while (socket != null) {
	    try {
		dataLength = din.readInt();
		byte[] data = new byte[dataLength];
		din.readFully(data, 0, dataLength);
	    } catch (SocketException se) {
		System.out.println(se.getMessage());
		break;
	    } catch (IOException ioe) {
		System.out.println(ioe.getMessage());
		break;
	    }
	}
    }
    
}
