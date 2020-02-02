import java.io.*;

public class Input implements Runnable{
    private Thread t;
    Chat stub;
    
    BufferedReader br;

    Input (Chat _stub) {
	stub = _stub;
    }
    
    public void run () {
	try {
	    br = new BufferedReader (new InputStreamReader(System.in));
	    while (true) {
		String line = br.readLine();
		if (line.startsWith ("#Control")) {
		    if (line.contains ("join")) {
			System.out.println("Already Joined -_-");
		    }
		    else if (line.contains ("leave")) {
			stub.leave();
			// System.exit(0);
		    }
		}
		else if (line.startsWith("#ReplyTo")) {
		    int startIndex = 8;
		    while (line.charAt(++startIndex) != ' ') {
		    }
		    String id = line.substring(9, startIndex);
		    startIndex++;
		    String msg = line.substring (startIndex, line.length() - 1);
		    stub.replyTo (id, msg);
		}
		else if (line.startsWith("#Reply")) {
		    String msg = line.substring(7, line.length() - 1);
		    stub.reply (msg);
		}
		else {
		    System.out.println ("Command Not Found");
		}
	    }
	} catch (Exception e) {
	    System.out.println (e);
	}
    }

    public void start () {
	t = new Thread (this, "inputThread");
	t.start();
    }
}
