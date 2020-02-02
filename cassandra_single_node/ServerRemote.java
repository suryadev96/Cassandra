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

    class KeySuperColumn {
	public String table;
	public String key;
	public String superColumn;

	@Override
	public int hashCode () {
	    return table.hashCode() + key.hashCode() + superColumn.hashCode();
	}

	@Override
	public boolean equals (Object obj) {
	    KeySuperColumn other = (KeySuperColumn) obj;
	    if (!table.equals (other.table)) {
		// System.out.println (table + " != " + other.table);
		return false;
	    }
	    if (!key.equals (other.key)) {
		// System.out.println (key + " != " + other.key);
		return false;
	    }
	    if (!superColumn.equals (other.superColumn)) {
		// System.out.println (superColumn + " != " + other.superColumn);
		return false;
	    }
	    return true;
	}

	@Override
	public String toString () {
	    return table + " : " + key + " : " + superColumn;
	}
    }
    
    Map<KeySuperColumn, List <String>> records;

    int numRecordsMem;
    
    ServerRemote () throws RemoteException {
	super();
	numRecordsMem = 0;
	records = new HashMap <KeySuperColumn, List <String>> ();
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

    void writeToDisc () {
	try {
	    	System.out.println ("Writing to Disc");
		    for (KeySuperColumn K : records.keySet()) {
			String table = K.table;
			String key = K.key;
			String superColumn = K.superColumn;

			List <String> columnList = records.get (K);
			Runtime.getRuntime().exec("mkdir -p " + table + "/" + key);
			Runtime.getRuntime().exec("touch " + table + "/" + key + "/" + superColumn);
			FileWriter fw = new FileWriter (table + "/" + key + "/" + superColumn, true);

			for (String column : columnList) {

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

			}
			fw.close();
		    }
	}
	catch (Exception e) {
	    System.out.println (e);
	}

	numRecordsMem = 0;
	records = new HashMap <KeySuperColumn, List <String>> ();

	System.out.println ("New Hash Map is ");
	for (KeySuperColumn s : records.keySet()) {
	    System.out.println (s.toString());
	}
    }
    
    public void insert (String table, String key, String supercolumn, String column) throws RemoteException{
	try {
	    if (numRecordsMem == 3) {
		writeToDisc();
	    }
	    else {
		KeySuperColumn K = new KeySuperColumn ();
		K.table = table;
		K.key = key;
		K.superColumn = supercolumn;

		if (records.containsKey (K)) {
		    List <String> columnList = records.get (K);
		    columnList.add (column);
		    records.put (K, columnList);
		}
		else {
		    List <String> columnList = new ArrayList <String> ();
		    columnList.add (column);
		    records.put (K, columnList);
		}
		numRecordsMem++;
	    }
	}
	catch (Exception e) {
	    System.out.println (e);
	}
    }

    ArrayList <String> parse (String str) {
		ArrayList <String> msg = new ArrayList <String> ();
		int size = str.length();
		int start = size;
		while (start > 0) {
		    int len = Integer.parseInt (str.substring (start-3, start));
		    msg.add (str.substring (start-3-len, start-3));
		    start = start-3-len;
		}
		return msg;
    }
    
    public String [] get (String table, String key, String supercolumn) throws RemoteException{
	try {
	    String allMessages = new String (Files.readAllBytes(Paths.get(table + "/" + key + "/" + supercolumn)));
	    System.out.println (allMessages);
	    ArrayList <String> message = parse (allMessages);
	    KeySuperColumn K = new KeySuperColumn();
	    K.table = table;
	    K.key = key;
	    K.superColumn = supercolumn;

	    for (String s : message) {
	    	System.out.println (s);
	    }

	    List <String> MessagesInMem = new ArrayList <String> ();

	    // print records
	    for (KeySuperColumn KT : records.keySet ()) {
	    	System.out.println (KT.toString());
	    }

	    System.out.println ("Searching for Key " + K.toString());
	    
	    if (records.containsKey (K)) {
		System.out.println ("Key Found");
		MessagesInMem = records.get (K);
		for (String s : MessagesInMem) {
		    System.out.println (s);
		}
		MessagesInMem.addAll (message);
	    }
	    else {
		System.out.println ("Key Not Found");		
		MessagesInMem = message;
	    }
	    for (String s : message) {
	    	System.out.println (s);
	    }
	    String [] msgString = new String[MessagesInMem.size()];
	    MessagesInMem.toArray(msgString);
	    for (String s : msgString) {
	    	System.out.println (s);
	    }
	    
	    return msgString;
	}
	catch (Exception e) {
	    System.out.println (e);
	}
	return null;
    }

	
}
