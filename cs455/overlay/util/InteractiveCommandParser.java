package cs455.overlay.util;

import java.io.*;

public class InteractiveCommandParser implements Runnable {

    public InteractiveCommandParser() {

    }

    public void run() {

	while(true) {
	    try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String input = reader.readLine();
		System.out.println("Command was: " + input);
	    } catch (IOException e) {
		System.out.println("Error occured when parsing command");
		System.exit(1);
	    }
	}
    }

    private void parseCommand() {

    }
    
}
