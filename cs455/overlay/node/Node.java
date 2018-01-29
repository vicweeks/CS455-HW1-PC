package cs455.overlay.node;

import cs455.overlay.wireformats.*;
import cs455.overlay.transport.*;

public interface Node {
    
    public void onEvent(TCPConnection connection, Event event);
    
}
