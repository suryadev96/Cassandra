import java.io.*;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Node{
	private static final int MAX = 1024, m = 10;
	private int nodeId;
	private String url;
	private int successorId, predecessorId, startingNodeId;
//	private LinkedHashMap<Integer, Integer> fingerTable;
	private Integer[] fingerTable;
	private boolean isConnected;
	
	public Node(int id, String url, int nd){
		nodeId = id % MAX;
		this.url = url;
		startingNodeId = nd % MAX;
		isConnected = false;
//		fingerTable = new LinkedHashMap<Integer, Integer>();
		fingerTable = new Integer[m];
	}
	
	public int getId(){
		return nodeId;
	}
	
	public int getStartingNodeId(){
		return startingNodeId;
	}
	
	public String getURL(){
		return url;
	}
	
	public int getSuccessorId(){
		return fingerTable[0];
	}
	
	public int getPredecessorId(){
		return predecessorId;
	}
	
/*	public LinkedHashMap<Integer, Integer> getFingerTable(){
		return fingerTable;
	}
	
	public void setFingerTable(LinkedHashMap<Integer, Integer>) table){
		fingerTable = table;
	}
*/	
	public Integer[] getFingerTable(){
		return fingerTable;
	}
	
	public void setFingerTable(Integer[] tbl){
		fingerTable = tbl;
	}
	
	public void setFingerTableEntry(int index, int val){
		fingerTable[index] = val;
	}
	
	public boolean checkConnected(){
		return isConnected;
	}
	
	public void setSuccessorId(int id){
		successorId = id;
	}
	
	public void setPredecessorId(int id){
		predecessorId = id;
	}
}