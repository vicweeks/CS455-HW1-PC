package cs455.overlay.wireformats;

import java.util.ArrayList;
import java.io.*;

public class OverlayNodeSendsData implements Event {

    private int type = 9;
    private int destID;
    private int srcID;
    private int payload;
    private int disseminationTraceLength;
    private ArrayList<Integer> disseminationTrace;
    
    public OverlayNodeSendsData(DataInputStream din) throws IOException {
	// for receiving
	destID = din.readInt();
	srcID = din.readInt();
	payload = din.readInt();
	disseminationTraceLength = din.readInt();
	for (int i=0; i< disseminationTraceLength; i++) {
	    disseminationTrace.add(din.readInt());
	}
    }

    public OverlayNodeSendsData(int destID, int srcID, int payload) {
	// for sending
	this.destID = destID;
	this.srcID = srcID;
	this.payload = payload;
	disseminationTraceLength = 0;
	disseminationTrace = new ArrayList<Integer>();
    }
    
    public int getType() {
	return type;
    }

    public int getDestID() {
	return destID;
    }

    public int getSrcID() {
	return srcID;
    }

    public int getPayload() {
	return payload;
    }

    public byte[] addTraversed(int nodeID) throws IOException {
	disseminationTrace.add(nodeID);
	return getBytes();
    }
    
    public int[] getDisseminationTrace() {
	int[] dissTrace = new int[disseminationTraceLength];
	for (int i=0; i<disseminationTraceLength; i++) {
	    dissTrace[i] = disseminationTrace.get(i);
	}
	return dissTrace;
    }

    public byte[] getBytes() throws IOException {
	byte[] marshalledBytes = null;
	ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
	DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

	dout.writeInt(type);
	dout.writeInt(destID);
	dout.writeInt(srcID);
	dout.writeInt(payload);
	dout.writeInt(disseminationTraceLength);

	for (int nodeID : disseminationTrace) {
	    dout.writeInt(nodeID);
	}

	dout.flush();
	marshalledBytes = baOutputStream.toByteArray();

	baOutputStream.close();
	dout.close();
	return marshalledBytes;
    }
    
}
