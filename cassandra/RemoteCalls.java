import java.rmi.*;

public interface RemoteCalls extends Remote{
	
	public int findSuccessor(int id) throws RemoteException;
	public int findPredecessor(int id) throws RemoteException;
	public int closestPrecedingFinger(int id) throws RemoteException;
	public int getSuccessor() throws RemoteException;
	public int getPredecessor() throws RemoteException;
	public void setSuccessor(int id) throws RemoteException;
	public void setPredecessor(int id) throws RemoteException;
	// node join operations
	public void updateFingerTable(int s, int i) throws RemoteException;
}