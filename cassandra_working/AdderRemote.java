import java.rmi.*;
import java.rmi.server.*;
import java.io.*;

public class AdderRemote extends UnicastRemoteObject implements Adder{

//stub1: Interaction
//stub2: keyword
    Server stub;
String table1="interaction";
String table2="term";

AdderRemote()throws RemoteException{
super();
}

public int add(int x,int y){return x+y;}


public int login(String username, String password)
{
	//filename: login_cred
	String filename="login_cred.txt";
	String line=null;
	
	try{
		FileReader fl=new FileReader(filename);
		BufferedReader br=new BufferedReader(fl);
		while((line=br.readLine())!=null)
		{
			String[] parts=line.split(",");
			if(parts[0].equals(username))
			{
				if(parts[1].equals(password))
					return 1;
				return 0;	//Wrong Password
			}
		}
		return 0;	//Invalid Creds
	}
	catch(Exception ex)
	{
		System.out.println(ex);
	}
	return 0;
}

public int logout(String username)
{
	//All changes made on the client side
	return 1;
}

public String [] search_chat(String username, String username2)
{
	try{
		String [] s=stub.get(table1, username, username2);
		return s;	//Need to process s at client
	}
	catch(Exception ex)
	{
		System.out.println(ex);
		return null;
	}
}

public String [] search_keyword(String username, String keyword)
{
	try{
		String [] s=stub.get(table2, username, keyword);
		return s;	//Need to process s at client
	}
	catch(Exception ex)
	{
		System.out.println(ex);
		return null;
	}
}

public int send(String username, String msg, String username2)
{
	
	try{
		//Inserting Interaction
		stub.insert(table1, username, username2, msg);
		String[] parts=msg.split(" ");
	
		//Inserting keyword
		int i;
		for(i=0;i<parts.length;i++)
		{
			stub.insert(table2, username, parts[i], msg);
		}
	}
	catch(Exception ex)
	{
		System.out.println(ex);
		return -1;
	}
	return 1;
}

    public void getStub () throws RemoteException{
	try {
	    stub = (Server) Naming.lookup("rmi://localhost:5000/cassandra");
	}
	catch (Exception e) {
	    System.out.println (e);
	}
    }
    
}
