import java.rmi.registry.LocateRegistry;


public class Reg{
	
	public static void main(String[] args){
		try{
//			System.out.println(20);
			LocateRegistry.createRegistry(5000);
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
}