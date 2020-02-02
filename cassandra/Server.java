import java.rmi.*;
public interface Server extends Remote{
    void insert (String table, String key, String supercolumn, String column) throws RemoteException;
    String [] get (String table, String key, String supercolumn) throws RemoteException;
    RemoteCalls getRemoteCallsStub () throws RemoteException;
    void replicate (String table, String key, String supercolumn, String column, int replicas_left) throws RemoteException;
}
