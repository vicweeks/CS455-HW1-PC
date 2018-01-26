package cs455.overlay.wireformats;

import java.io.*;

public class EventFactory {

    private static EventFactory instance = null;

    protected EventFactory() {
	//defeat instantiation
    }

    public static EventFactory getInstance() {
	if (instance == null)
	    instance = new EventFactory();
	return instance;
    }

    public Event constructEvent(byte[] marshalledBytes) throws IOException {
	int type;
	Event event = null;
	
	ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
	DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

	type = din.readInt();

	switch (type) {
	    case 2: event = new OverlayNodeSendsRegistration(din);
		break;
	    case 3: event = new RegistryReportsRegistrationStatus(din);
		break;
		//TODO
	    case 4: 
		break;
	    case 5:
		break;
	    case 6:
		break;
	    case 7:
		break;
	    case 8:
		break;
	    case 9: 
		break;
	    case 10: 
		break;
	    case 11:
		break;
	    case 12:
		break;
	    default: System.out.println("Error in EventFactory: message type " + type + " is invalid.");
		System.exit(1);
	}

	return event;
    } 
    
}
