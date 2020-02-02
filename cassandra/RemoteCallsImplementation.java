import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class RemoteCallsImplementation extends UnicastRemoteObject implements RemoteCalls {
	private Node myNode;
	private static final int MAX = 1024, m=10;
	
	public RemoteCallsImplementation(Node nd) throws RemoteException{
		myNode = nd;
	}
	
	public int getSuccessor()  throws RemoteException{
		return myNode.getSuccessorId();
	}
	
	public void setSuccessor(int id) throws RemoteException{
		myNode.setSuccessorId(id);
	}
	
	public int getPredecessor() throws RemoteException{
		return myNode.getPredecessorId();
	}
	
	public void setPredecessor(int id) throws RemoteException{
		myNode.setPredecessorId(id);
	}
	
	public int findSuccessor(int id) throws RemoteException{
		System.out.println("Called for node " + Integer.toString(myNode.getId()) + " with id " + Integer.toString(id));
		if(id == myNode.getId())
			return id;
		int n1 = findPredecessor(id);
		System.out.println("Value of n1 is " + Integer.toString(n1));
		try{
		    RemoteCalls obj = ((Server)Naming.lookup(myNode.getURL()+"/"+Integer.toString(n1))).getRemoteCallsStub();
			// Now I need to return n1's successor
			int a = getSuccessor();
			System.out.println("Value which will be returned is " + Integer.toString(a));
			return obj.getSuccessor();
		} catch(Exception e){
			System.out.println(e);
		}
		return -1;
	}
	
	public int findPredecessor(int id) throws RemoteException{
		int low = myNode.getId(), high = myNode.getSuccessorId();
//		System.out.println("Value of low is " + Integer.toString(low));
//		System.out.println("Value of high is " + Integer.toString(high));
//		System.out.println("Value of id is " + Integer.toString(id));
		RemoteCalls obj=null;
		try{

		    System.out.println ("low is " + low);
		    obj = this;
		    System.out.println ("reached here");
		} catch(Exception e){
			System.out.println(e);
		}
		while(!(belongsToSecond(low, high, id))){
			try{
				low = obj.closestPrecedingFinger(id);
				if (low == myNode.getId()) {
				    obj = this;
				}
				else
				    obj = ((Server)Naming.lookup(myNode.getURL()+"/"+Integer.toString(low))).getRemoteCallsStub();
				high = obj.getSuccessor();
//				System.out.println("inside while, Value of low is " + Integer.toString(low));
//				System.out.println("inside while, Value of high is " + Integer.toString(high));
			} catch(Exception e){
				System.out.println(e);
			}
		}
		return low;
	}
	
/*	public int closestPrecedingFinger(int id){
		LinkedHashMap<Integer, Integer> myMap = myNode.getFingerTable();
//		Set set = myMap.entrySet();
		Set<Integer> set = myMap.keySet();
		Integer[] arr = set.toArray(new Integer[set.size()]);
		for(int i=arr.length-1;i>=0;i--){
			Integer val = (Integer)myMap.get(arr[i]);
			// Now check whether val lies inside n,id
			if(belongsToFirst(myNode.getId(), id, val)){
				return val;
			}
		}
		return myNode.getId();
	}
*/	

	public int closestPrecedingFinger(int id) throws RemoteException{
		Integer[] table = myNode.getFingerTable();
//		System.out.println("In closestPrecedingFinger");
//		System.out.println("myNode Id is " + Integer.toString(myNode.getId()));
//		System.out.println("id is " + Integer.toString(id));
		for(int i=m-1;i>=0;i--){
//			System.out.println("table[i] is " + Integer.toString(table[i]));
			if(belongsToFirst(myNode.getId(), id, table[i])){
//				System.out.println("closest preceding finger matched for i " + Integer.toString(i));
				return table[i];
			}
		}
//		System.out.println("did not match any node");
		return myNode.getId();
	}
	
	public void updateFingerTable(int s, int i) throws RemoteException{
		RemoteCalls obj;
		System.out.println("In updateFingerTable function");
		if(belongsToFirst(myNode.getId(), myNode.getFingerTable()[i], s)){
			System.out.println("myNode.getId() " + Integer.toString(myNode.getId()));
			System.out.println("myNode.getFingerTable()[i] " + Integer.toString(myNode.getFingerTable()[i]));
			System.out.println("s "+ Integer.toString(s));
			myNode.setFingerTableEntry(i, s);
			System.out.println("After updating the finger table, printing it");
			printFingerTable();
			int p = myNode.getPredecessorId();
			if(p == s){
				return ;
			}
				
			try{
			    obj = ((Server)Naming.lookup(myNode.getURL()+"/"+Integer.toString(p))).getRemoteCallsStub();
				obj.updateFingerTable(s, i);
				
			} catch(Exception e){
				System.out.println(e);
			}
		}
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
	
	// open-open
	public boolean belongsToFirst(int a, int b, int val){
		if(a == b)
			return true;
		if(b > a){
			if(val > a && val < b){
				return true;
			}
			return false;
		}
		else{
			if((val > a && val <= MAX-1) || (val >= 0 && val < b)){
				return true;
			}
			return false;
		}
	}
	
	// open-closed
	public boolean belongsToSecond(int a, int b, int val){
		if(a == b)			// because consider (n, n.successor] and if both are same, then it must include all the nodes
			return true;
		if(b > a){
			if(val > a && val <= b)
				return true;
			return false;
		}
		else{
			if((val > a && val <= MAX-1) || (val >= 0 && val <= b))
				return true;
			return false;
		}
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
