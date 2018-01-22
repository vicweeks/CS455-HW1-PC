package cs455.overlay.wireformats;

public interface Event {

    protected int type;
    
    public void getType();

    public void getBytes();
    
}
