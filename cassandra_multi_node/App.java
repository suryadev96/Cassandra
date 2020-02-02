import java.rmi.*;
import java.rmi.registry.*;

public class App{

    public static void main(String args[]){
	try{

	    Adder stub=new AdderRemote();
	    stub.getStub();
	    Naming.rebind("rmi://localhost:5000/sonoo",stub);

	}catch(Exception e){System.out.println(e);}
    }

}
