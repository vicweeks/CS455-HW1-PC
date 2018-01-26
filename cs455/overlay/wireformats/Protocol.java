package cs455.overlay.wireformats;

public class Protocol {

    public Protocol() {
	
    }

    public void onEvent(Event event) {
	int eventType = event.getType();
	switch(eventType)
	    {
	    case 2: onReceivedRegistrationRequest();
		break;
		//TODO
	    case 3:
		break;
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
	    default: System.out.println("Error in EventFactory: message type " + eventType + " is invalid.");
		System.exit(1);
	}
    }

    private void onReceivedRegistrationRequest() {
	//TODO section 2.1
    }
    
}
