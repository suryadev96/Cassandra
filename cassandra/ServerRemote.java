import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class ServerRemote extends UnicastRemoteObject implements Server{

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

    int k = 2;
    RemoteCalls R;
    
    int myId;
    private Node myNode;
    private String regURL;
    private static final int MAX = 1024;
    private static final int m = 10;		// m = log(MAX)

    public RemoteCalls getRemoteCallsStub () throws RemoteException {
	return R;
    }
    
    ServerRemote (int _myId, int knownNodeNo) throws RemoteException {

	super();
	myId = _myId;
	myNode = new Node(myId, "rmi://localhost:5000", knownNodeNo);
	R = new RemoteCallsImplementation (myNode);
	regURL = "rmi://localhost:5000";
	join(myNode.getStartingNodeId());

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
		Runtime CommandPrompt = Runtime.getRuntime();

		Process powerShell = CommandPrompt.exec("mkdir -p " + table + "/" + key);
		powerShell.waitFor();
		powerShell = Runtime.getRuntime().exec("touch " + table + "/" + key + "/" + superColumn);
		powerShell.waitFor();
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
	System.out.println ("Insert Called");
	try {
	    RemoteCalls R_obj = R;
	    int coordinator = R_obj.findSuccessor(Integer.parseInt (key));
	    if (coordinator != myId) {
		System.out.println ("Transferring insert to " + coordinator);
		Server stub = (Server) Naming.lookup(regURL + "/" + Integer.toString (coordinator));
		System.out.println ("This is okay");
		stub.insert (table, key, supercolumn, column);
		System.out.println ("Done Transferring");
		return;
	    }

	    System.out.println ("Inserting myself");
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

	    System.out.println ("Done Inserting");

	    System.out.println ("Starting Replication");
	    int succId = myNode.getSuccessorId ();
	    System.out.println ("My successor is " + succId);
	    Server stub = (Server) Naming.lookup(regURL + "/" + Integer.toString (succId));
	    stub.replicate (table, key, supercolumn, column, k-1);
	    System.out.println ("Done Replicating");
	}
	catch (Exception e) {
	    System.out.println (e);
	}
    }

    public void replicate (String table, String key, String supercolumn, String column, int replicas_left) throws RemoteException{
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

	    System.out.println ("Done Inserting");

	    System.out.println ("Starting Replication");
	    replicas_left--;
	    if (replicas_left != 0) {
		int succId = myNode.getSuccessorId ();
		System.out.println ("My successor is " + succId);
		Server stub = (Server) Naming.lookup(regURL + "/" + Integer.toString (succId));
		stub.replicate (table, key, supercolumn, column, replicas_left);
		System.out.println ("Done Replicating");
		return;

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
	    RemoteCalls R_obj = R;
	    int coordinator = R_obj.findSuccessor(Integer.parseInt (key));
	    if (coordinator != myId) {
		System.out.println ("Transferring get to " + coordinator);
		Server stub = (Server) Naming.lookup(regURL + "/" + Integer.toString (coordinator));
		System.out.println ("This is okay");
		String [] valReturned = stub.get (table, key, supercolumn);
		for (String v : valReturned) {
		    System.out.println (v);
		}
		return valReturned;
	    }

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
		System.out.println ("Key  Found");		
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
	System.out.println ("Return Null");
	return null;
    }

    public void join(int id){
	if(id!= -1){
	    // i.e. there exists a node which it can contact
	    System.out.println("Inside join function");
	    initFingerTable(id);
	    System.out.println("After calling initFingerTable");
	    updateOthers();
	    System.out.println("After calling updateOthers");
	    printFingerTable();
	}
	else{
	    /*LinkedHashMap<Integer, Integer> tempFingerTable = new LinkedHashMap<Integer, Integer>();
	      for(int i=1;i<=m;i++){
	      tempFingerTable.put((myNode.getId()+2^(i-1)) % MAX, myNode.getId());
	      }
	      myNode.setFingerTable(tempFingerTable);
	      myNode.setPredecessorId(myNode.getId());*/
			
	    Integer[] table = new Integer[m];
	    for(int i=0;i<m;i++){
		table[i] = myNode.getId();
	    }
	    myNode.setFingerTable(table);
	    myNode.setPredecessorId(myNode.getId());
	    //			myNode.setSuccessorId(myNode.getId());
	    printFingerTable();
	}
    }
	
    public void initFingerTable(int id){
	RemoteCalls obj;
	System.out.println ("here");
	Integer[] ft = myNode.getFingerTable();
	//		System.out.println("inside initFingerTableFunction");
	try {
	    System.out.println ("id is " + id);
	    obj = ((Server)Naming.lookup(regURL+"/"+Integer.toString(id))).getRemoteCallsStub();
	    System.out.println ("here1");
	    //			System.out.println("Calling findSuccessor");
	    ft[0] = obj.findSuccessor(myNode.getId()+1);
	    //			System.out.println("Caling setSuccessor");
	    //myNode.setSuccessorId(ft[0]);
	    //			System.out.println("Trying to lookup in the finger table");
	    //			System.out.println(id);
	    //			System.out.println(myNode.getId());
	    //			System.out.println(ft[0]);
	    obj = ((Server)Naming.lookup(regURL+"/"+Integer.toString(ft[0]))).getRemoteCallsStub();
	    //			System.out.println("Calling myNode.setPredecessor");
	    myNode.setPredecessorId(obj.getPredecessor());
	    //			System.out.println("Calling obj.setPredecessor");
	    obj.setPredecessor(myNode.getId());
	} catch (Exception e) {
	    System.out.println("Exception in initFingerTable");
	    System.out.println(e);
	}
		
	for (int i = 1; i < m; i++) {
	    int d = (int)(Math.pow(2,  i));
	    int start = (myNode.getId() + d) % MAX;
	    if(belongsToClosedOpen(myNode.getId(), ft[i-1], start)){
		ft[i] = ft[i-1];
	    }else{
		try {
		    obj = ((Server)Naming.lookup(regURL+"/"+Integer.toString(id))).getRemoteCallsStub();
		    ft[i] = obj.findSuccessor(start); 
		} catch (Exception e) {
		    System.out.println(e);
		}
	    }
	}
	myNode.setFingerTable(ft);
	printFingerTable();
    }	
	
    public void updateOthers(){
	RemoteCalls obj;
	for(int i=0;i<m;i++){
	    try{
		int d = (int)(Math.pow(2, i));
		int temp = (myNode.getId() - d);
		temp = mod(temp, MAX);
		System.out.println("In updateOthers, temp is " + Integer.toString(temp));
		System.out.println ("id is " + myNode.getId());
		// obj = ((Server)Naming.lookup(regURL+"/"+Integer.toString(myNode.getId()))).getRemoteCallsStub();
		int p = R.findPredecessor(temp);
		System.out.println ("p is " + p);
		if (p == myNode.getId()) {
		    obj = R;
		}
		else {
		    obj = ((Server)Naming.lookup(regURL+"/"+Integer.toString(p))).getRemoteCallsStub();
		}
		obj.updateFingerTable(myNode.getId(), i);
	    } catch(Exception e){
		System.out.println(e);
	    }
	}
    }
	
    public int mod(int x, int y){
	int result = x%y;
	while(result<0){
	    result+=y;
	}
	return result;
    }
	
    public void printFingerTable(){
	System.out.println("The finger table for node " + Integer.toString(myNode.getId()) + " is :");
	for(int i=0;i<m;i++){
	    System.out.print(myNode.getId() + (int) (Math.pow(2, i)));
	    System.out.print(" --> ");
	    System.out.println(myNode.getFingerTable()[i]);
	}
	System.out.println(" ");
    }
	
    public boolean belongsToClosedOpen(int a, int b, int val){
	if(a == b)
	    return true;
	if(b > a){
	    if(val >= a && val < b)
		return true;
	    return false;
	}
	else{
	    if((val >= a && val <= MAX-1) || (val >= 0 && val < b))
		return true;
	    return false;
	}
    }
	
}
