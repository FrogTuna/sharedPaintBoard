package User;

import javax.swing.*;

import Manager.ManagerFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class UserApp extends Thread {

	
	//fields
    public String ip;
    public int port;
    public String username;

    
    
    //constructor
    public UserApp(String username, String ip, int port) {
    	this.username = username;
    	this.ip = ip;
    	this.port = port;
    }

    
    
    //new thread
    public void run() {
        User();
    }

    
    
    //create a user frame
    public void User() {
    	
        try {
        	
            UserFrame user = new UserFrame(username, ip, port);
            user.createUI();
            
        }catch(NullPointerException e1) {
        	
	        System.out.println("failed to set up connection between user and server.");
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
    		
    		//create a new user frame when port is valid
            try{
                int port = Integer.parseInt(args[1]);
                UserApp user = new UserApp(args[2], args[0],port);
                user.start();
            }
            catch (NumberFormatException ex){
        		System.out.println("invailded port number, please try again");
        		return;
            }

    	}

    }
}