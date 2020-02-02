import java.rmi.*;
public interface Adder extends Remote{

    public int add(int x,int y)throws RemoteException;

    //Code added by SuryaDev Reddy
    public int login(String username, String password) throws RemoteException;
    public int logout(String username) throws RemoteException;
    public String [] search_chat(String username, String username2) throws RemoteException;
    public void getStub () throws RemoteException;
    public String [] search_keyword(String username, String keyword) throws RemoteException;
    public int send(String username, String msg, String username2) throws RemoteException;
}
