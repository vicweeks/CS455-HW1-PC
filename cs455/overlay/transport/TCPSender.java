package cs455.overlay.transport;

import cs455.overlay.wireformats.*;
import java.net.*;
import java.io.*;

public class TCPSender {

    private Socket socket;
    private DataOutputStream dout;

    public TCPSender(Socket socket) throws IOException {
	this.socket = socket;
	dout = new DataOutputStream(socket.getOutputStream());
    }
    
    public void sendData(byte[] dataToSend) throws IOException {
	int dataLength = dataToSend.length;
	dout.writeInt(dataLength);
	dout.write(dataToSend, 0, dataLength);
	dout.flush();
    }
    
}
