import java.rmi.*;
import java.rmi.registry.*;

public class Node{
    public static void main (String args[]) {
	try{
	    Server stub = new ServerRemote();
	    Naming.rebind("rmi://localhost:5000/cassandra",stub);
	}
	catch (Exception e)
	    {
		System.out.println (e);
	    }
    }
}
