import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class ServerRemote extends UnicastRemoteObject implements Server{

    int myId;
    private Node myNode;
    private String regURL;
    private static final int MAX = 1024;
    private static final int m = 10;		// m = log(MAX)
	
    ServerRemote () throws RemoteException {
	super();
    }
    
    public static void main (String[] args) throws IOException{
	// table names are given as parameters
	// we create directories for the tables
	for (String s : args) {
	    Runtime.getRuntime().exec("mkdir -p " + s);
	}
	// Server S = new Server ();
	// S.insert ("term", "1", "hello", "hello! hi, there");
	// S.get ("term", "1", "hello");
    }

    public void insert (String table, String key, String supercolumn, String column) throws RemoteException{
	try {
	    Runtime.getRuntime().exec("mkdir -p " + table + "/" + key);
	    Runtime.getRuntime().exec("touch " + table + "/" + key + "/" + supercolumn);
	    FileWriter fw = new FileWriter (table + "/" + key + "/" + supercolumn, true);
	    int size = column.length();
	    fw.write (column);
	    // fix three characters for size
	    if (size < 10) {
		fw.write ("00" + size);
	    }
	    else if (size < 100) {
		fw.write ("0" + size);
	    }
	    else if (size < 1000){
		fw.write (size);
	    }
	    else {
		// message too long
	    }

	    fw.close();
	}
	catch (Exception e) {
	    System.out.println (e);
	}
    }

    String [] parse (String str) {
	ArrayList <String> msg = new ArrayList <String> ();
	int size = str.length();
	int start = size;
	while (start > 0) {
	    int len = Integer.parseInt (str.substring (start-3, start));
	    msg.add (str.substring (start-3-len, start-3));
	    start = start-3-len;
	}
	String [] msgString = new String[msg.size()];
	msg.toArray(msgString);
	return msgString;
    }
    
    public String [] get (String table, String key, String supercolumn) throws RemoteException{
	try {
	    String allMessages = new String (Files.readAllBytes(Paths.get(table + "/" + key + "/" + supercolumn)));
	    System.out.println (allMessages);
	    String [] message = parse (allMessages);
	    for (String s : message) {
		System.out.println (s);
	    }
	    return message;
	}
	catch (Exception e) {
	    System.out.println (e);
	}
	return null;
    }

	
}
