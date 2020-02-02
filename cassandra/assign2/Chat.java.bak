import java.rmi.*;
public interface Chat extends Remote{

    public void setIdIp(String id, String ip) throws RemoteException;

    public void insertIdIp(String id, String ip) throws RemoteException;

    public void notifyIdIp (String id, String ip) throws RemoteException;
    
    public int return_seq(String id, String ip, String msg, int m_id, int status) throws RemoteException;

    public void set_deliver(String id, String ip_addr, int m_id, int max_seq, int status) throws RemoteException;

    public void reply(String msg) throws RemoteException;

    public void leave() throws RemoteException;

    public void replyTo(String id, String msg) throws RemoteException;

    public void enqueue_self_message(String id, String ip, String msg, int m_id, int seq, int status) throws RemoteException;    
}
