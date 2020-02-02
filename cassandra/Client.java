import java.rmi.*;
import java.util.Scanner;
import java.io.*;

public class Client{

    public static void main(String args[]){
	try{

	    Adder stub=(Adder)Naming.lookup("rmi://localhost:5000/sonoo");
	    //System.out.println(stub.add(34,4));

	    //Code added by Rohan Gyani
	    /*
	     * User is given the following options
	     * 1. Chat with another person
	     * 2. Open a chat history (interaction) with another person
	     * 3. Search a keyword in the chat
	     */
	    Scanner in = new Scanner(System.in);
	    int choice=0, login_status=0;	//0: not logged in
	    boolean repeat=true;
	    String[] st;
	    String choiceToContinue;
	    String username="", password="";
	    String username2="", msg="";
	    int ret, n_users, n_msgs, itr1, itr2, ind=0;

	    //Reading Random Sentences.txt into an array of strings
	    String[] rnd_msg=new String[100];
	    String filename="Random Sentences.txt";
	    String line=null;

	    FileReader fl=new FileReader(filename);
	    BufferedReader br=new BufferedReader(fl);
	    while((line=br.readLine())!=null)
		{
		    rnd_msg[ind]=line;
		    ind++;
		}


	    //Login
	    while(login_status==0)
		{
		    System.out.println("Please enter your login credentials:");
		    System.out.println("Username:");
		    username=in.nextLine();
		    System.out.println("Password:");
		    password=in.nextLine();
		    login_status=stub.login(username, password);
		    if(login_status==0)	System.out.println("Wrong Credentials!");
		}

	    while(repeat)
		{
		    System.out.println("Which of the following do you want to choose:");
		    System.out.println("1. Chat with another person");
		    System.out.println("2. Open a chat history (interaction) with another person");
		    System.out.println("3. Search a keyword in the chat");
		    System.out.println("4. Run scripts to populate data");
		    System.out.println("Please enter one of the above (1, 2, 3 or 4):");
		    choice=in.nextInt();
	
		    if(choice==1)
			{
			    System.out.println("Enter the username of the person you want to chat with:");
			    in.nextLine();
			    username2=in.nextLine();
			    System.out.println("Enter the message:");
			    msg=in.nextLine();
			    ret=stub.send(username, msg, username2);
			    System.out.println ("username 2 = " + username2);
			    System.out.println ("message is " + msg);
			    if(ret<=0)	System.out.println("Message not sent successfully!");
			    else System.out.println("Message delivered");
			}
		    else if(choice==2)
			{
			    System.out.println("Enter the username of the person you want to open chat history of:");
			    in.nextLine();
			    username2=in.nextLine();
			    st=stub.search_chat(username, username2);
			    if(st==null)	System.out.println("Operation Unsuccessful");
			    else
				{
				    //Process the string
				    for (String s : st) {
					System.out.println (s);
				    }
				    
				}
			}
		    else if(choice==3)
			{
			    System.out.println("Enter the keyword you want to search:");
			    in.nextLine();
			    msg=in.nextLine();
			    st=stub.search_keyword(username, msg);
			    if(st==null)	System.out.println("Operation Unsuccessful");
			    else
				{
				    //Process the string
				    for (String s : st) {
					System.out.println (s);
				    }
				}
			}
		    // else if(choice==4)
		    // {
		
		
		    // 	System.out.println("Enter the number of users: ");
		    // 	n_users=in.nextInt();
		
		    // 	System.out.println("Enter the number of messages (max 50): ");
		    // 	n_msgs=in.nextInt();
		
		    // 	for(itr1=1;itr1<=n_users;itr1++)
		    // 	{
		    // 		for(itr2=itr1+1;itr2<=n_users;itr2++)
		    // 		{
		    // 			for(ind=0;ind<n_msgs/2;ind++)
		    // 			{
		    // 				ret=stub.send(itr1, msg, itr2);
		    // 				if(ret<=0)	System.out.println("Message not sent successfully!");
		    // 			//else System.out.println("Message delivered");
		    // 			}
		    // 			for(;ind<n_msgs;ind++)
		    // 			{
		    // 				ret=stub.send(itr2, msg, itr1);
		    // 				if(ret<=0)	System.out.println("Message not sent successfully!");
			
		    // 			}
		    // 		}
		    // 	}
		    // }
		    else
			System.out.println("Invalid choice entered");
	
		    System.out.println("You want to continue?(Y/N)");
		    choiceToContinue=in.nextLine();
		    repeat=choiceToContinue.equalsIgnoreCase("Y");
		    if(!repeat)
			{	System.out.println("You are logged out");
			    login_status=0;
			}
		}
	}catch(Exception e){System.out.println(e);}
    }

}
