package Manager;

import java.awt.Canvas;
import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Common.GUIDesign;

public class ManagerFrame extends GUIDesign {
	
	
	//fields
    private String ip = "localhost";
    private int port = 8000;
    private String username;
    private Socket socket;
    public static JFrame frame;
    
	
    
    //constructor 
    public ManagerFrame(String username, String ip, int port) {
        this.username = username;
        this.ip = ip;
        this.port = port;
        
        try {
			socket = new Socket(ip,port);
		} catch (UnknownHostException e1) {

            System.out.println("unknown port number, please provide a valid port number.");
            
		} catch (IOException e1) {

            System.out.println("failed to set up connection between manager and server.");
            
		}
        
    }
	
	
	//manager layout
	@SuppressWarnings("unchecked")
	public void createUI() {
		
		
        frame = new JFrame();
        frame.setSize(800, 550);
        frame.setTitle("CreateWhiteboard: " + username);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
        	
            listener = new Common.UIlistener(frame, socket);
            
        }catch(NullPointerException e1) {
        	
	        System.out.println("failed to set up connection between manager and server.");
        }



        
        
        JLabel shapeLabel = new JLabel("Shape:");
        shapeLabel.setBounds(20, 20, 50, 50);
        JLabel colorLabel = new JLabel("Color:");
        colorLabel.setBounds(20, 220, 50, 20);
        JLabel textLabel = new JLabel("Text:");
        textLabel.setBounds(20, 310, 50, 20);
        


        canvas = new Canvas();
        canvas.setBackground(Color.WHITE);
        canvas.setBounds(140, 50, 550, 450);
        canvas.addMouseListener(listener);
        canvas.addMouseMotionListener(listener);

        
        
        frame.add(shapeLabel);
        frame.add(line());
        frame.add(rectangle());
        frame.add(triangle());
        frame.add(circle());
        frame.add(pen());
        frame.add(colorLabel);
        frame.add(colorSelection());
        frame.add(textLabel);
        frame.add(addText());
        frame.add(canvas);
        listener.setGraphic(canvas.getGraphics());
        frame.add(newCanvas());
        frame.add(saveCanvas());
        frame.add(openCanvas());
        frame.add(exitButton());
        frame.add(displayUser());
        frame.add(refreshButton());
        

		
	}
	


}
