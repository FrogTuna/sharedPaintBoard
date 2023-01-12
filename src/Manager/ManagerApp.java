package Manager;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import User.UserApp;

public class ManagerApp extends Thread {
	
	//fields
    public String ip;
    public static int port;
    public String username;


    
    
    //constructor
    public ManagerApp(String username, String ip, int port) {
    	this.username = username;
    	this.ip = ip;
    	this.port = port;
    }
    
    
    //new thread
    public void run() {
    	
    	Manager();
    }
    
    //create a manager frame
    public void Manager() {
    	
        try {
        	
    		ManagerFrame manager = new ManagerFrame (username, ip, port);
            manager.createUI();
            
        }catch(NullPointerException e1) {
        	
	        System.out.println("failed to set up connection between manager and server.");
        }


        
    }


    
    //main
    public static void main(String args[]) {

    	
    	//check arguments 
    	if(args.length > 3) {
    		
    		System.out.println("user is only allowed to input 3 arguments, please follow <ip> <port> username");
    		
    	}
    	else if(args.length < 3) {
    		
    		System.out.println("user is allowed to input 3 arguments, please follow <ip> <port> username");
    		
    	}
    	else {
    		
    		//create a new manager frame when port is valid
            try{
                port = Integer.parseInt(args[1]);
                ManagerApp gui = new ManagerApp(args[2],args[0],port);
                gui.start();
                
                //new server
                Server server = new Server(args[2], port);
            	  	

            }
            catch (NumberFormatException ex){
        		System.out.println("invailded port number, please try again");
        		return;
            }
            


    	}
      

    }
}

