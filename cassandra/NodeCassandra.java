import java.rmi.*;
import java.rmi.registry.*;

public class NodeCassandra{
    public static void main (String args[]) {
	try{
	    int id = Integer.parseInt (args[0]);
	    Server stub = new ServerRemote(id, Integer.parseInt(args[1]));
	    System.out.println ("done");	    
	    Naming.rebind("rmi://localhost:5000/"+id,stub);
	    System.out.println ("done");
	}
	catch (Exception e)
	    {
		System.out.println (e);
	    }
    }
}
