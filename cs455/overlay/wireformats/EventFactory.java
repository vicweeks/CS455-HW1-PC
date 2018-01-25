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

	switch (type)
	    {
	    case 2: event = new OverlayNodeSendsRegistration(din);
		break;
	    case 3: event = registryReportsRegistrationStatus(din);
		break;
	    case 4: event = overlayNodeSendsDeregistration(din);
		break;
	    case 5: event = registryReportsDeregistrationStatus(din);
		break;
	    case 6: event = registrySendsNodeManifest(din);
		break;
	    case 7: event = nodeReportsOverlaySetupStatus(din);
		break;
	    case 8: event = registryRequestsTaskInitiate(din);
		break;
	    case 9: event = overlayNodeSendsData(din);
		break;
	    case 10: event = overlayNodeReportsTaskFinished(din);
		break;
	    case 11: event = registryRequestsTrafficSummary(din);
		break;
	    case 12: event = overlayNodeReportsTrafficSummary(din);
		break;
	    default: System.out.println("Error in EventFactory: message type " + type + " is invalid.");
		System.exit(1);
	    }

	return event;
    }

    private Event overlayNodeSendsRegistration(DataInputStream din) {
	return null;
    }

    private Event registryReportsRegistrationStatus(DataInputStream din) {
	return null;
    }

    private Event overlayNodeSendsDeregistration(DataInputStream din) {
	return null;
    }

    private Event registryReportsDeregistrationStatus(DataInputStream din) {
	return null;
    }

    private Event registrySendsNodeManifest(DataInputStream din) {
	return null;
    }

    private Event nodeReportsOverlaySetupStatus(DataInputStream din) {
	return null;
    }

    private Event registryRequestsTaskInitiate(DataInputStream din) {
	return null;
    }

    private Event overlayNodeSendsData(DataInputStream din) {
	return null;
    }

    private Event overlayNodeReportsTaskFinished(DataInputStream din) {
	return null;
    }

    private Event registryRequestsTrafficSummary(DataInputStream din) {
	return null;
    }

    private Event overlayNodeReportsTrafficSummary(DataInputStream din) {
	return null;
    }
    
}
