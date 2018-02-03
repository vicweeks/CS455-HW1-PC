package cs455.overlay.util;

import cs455.overlay.node.*;
import cs455.overlay.transport.*;
import cs455.overlay.wireformats.*;

public class EventThread extends Thread {

    private Node node;
    private TCPConnection self;
    private Event receivedEvent;
    
    public EventThread(Node node, TCPConnection self, Event receivedEvent) {
	this.node = node;
	this.self = self;
	this.receivedEvent = receivedEvent;
    }

    public void run() {
	node.onEvent(self, receivedEvent);
    }
}
