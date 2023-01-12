package User;

import java.awt.Canvas;
import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Common.GUIDesign;
import Manager.Tools;

public class UserFrame extends GUIDesign{
	
	//fields
    private String ip;
    private int port;
    private String username;
    public static DataOutputStream outStream;
    public static DataInputStream inStream;
    public JSONParser parser;
    private Socket socket;
    public static JFrame frame;
	
    
    //Constructor
    public UserFrame(String username, String ip, int port) {
        this.username = username;
        this.ip = ip;
        this.port = port;
        
        try {
			socket = new Socket(ip,port);
			
		} catch (UnknownHostException e1) {

            System.out.println("unknown port number, please provide a valid port number.");
            
			
		} catch (IOException e1) {

            System.out.println("failed to set up connection between user and server.");
            return;
            
		}
        
        
        try {
            parser = new JSONParser();
            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {

            System.out.println("Could not use reader and writer on the userframe class.");
            
        }catch (NullPointerException e1) {
	
	        System.out.println("failed to set up connection between user and server.");
            return;
	        
		}
    }
	
	
	//userframe layout
	@SuppressWarnings("unchecked")
	public void createUI() {
		
        frame = new JFrame();
        frame.setSize(800, 550);
        frame.setTitle("JoinWhiteboard: " + username);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
        	
            listener = new Common.UIlistener(frame, socket);
            
        }catch(NullPointerException e1) {
        	
	        System.out.println("failed to set up connection between user and server.");
        }



        JLabel shapeLabel = new JLabel("Shape:");
        shapeLabel.setBounds(20, 20, 50, 20);

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
        
        try {
        	
            listener.setGraphic(canvas.getGraphics());
        
        }catch(NullPointerException e1) {
        	
	        System.out.println("failed to set up connection between user and server.");
        }


        
        //send the request to server for connection
        try {
        	
            JSONObject command = new JSONObject();
            command.put("Name", username);
            outStream.writeUTF("Request/" + command.toJSONString());
            
        } catch (IOException e) {
           
            System.out.println("invoke an error on the (User) side, failed to parse json object or send 'connect/' to server.");
            
        } catch(NullPointerException e1) {
        	
	        System.out.println("failed to set up connection between user and server.");
	        
        }
        
        
        //a new thread for handle the response from server.
        new Thread(() -> {
            try {
                String str;
                while ((str = inStream.readUTF()) != null) {
                    String[] splitStatus = str.split("/");
                    String status = splitStatus[0];   
                    if ("Draw".equals(status)) {
                    	
                        JSONObject object = (JSONObject) parser.parse(splitStatus[1]);
                        listener.redraw(object);
                        
                    } else if ("Clear".equals(status)) {
                    	
                        canvas.repaint();
                        
                    } else if ("Exit".equals(status)) {
                    	System.exit(0);
                    }
                }
            } catch (IOException e) {
            	
                System.out.println("failed to receive the read request from server.");
                
            }catch (ParseException e) {
            	
                System.out.println("failed to parse json object on user side.");
           
            }catch(NullPointerException e1) {
            	
    	        System.out.println("failed to set up connection between user and server.");
            }
        }).start();

		
	}


}

