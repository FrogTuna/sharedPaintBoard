package Common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Manager.Tools;

public class UIlistener implements ActionListener, MouseListener, MouseMotionListener {
	
	//fields
    public Graphics2D graphic;
    public JFrame frame;
    int startx, starty, endx, endy;
    public Color color = Color.BLACK;
    private String shape = "";
    private String rgb = "0 0 0";
    private Socket socket;
    private int fontSize = 2;
    public static DataOutputStream outStream;
    public static DataInputStream inStream;

    
    //constructor
    public UIlistener(JFrame frame, Socket socket) {
        this.frame = frame;
        this.socket = socket;
        try {
            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            System.out.println("failed to read or write data on the UIlistener");
            
        } catch (NullPointerException e1) {
        	
            System.out.println("failed to read or write data on the UIlistener");
        }
        
    }


    //draw method
    public void setGraphic(Graphics g) {
        this.graphic = (Graphics2D) g;
        //System.out.println(g);
    }


    
	//button listener
    @Override
    public void actionPerformed(ActionEvent e) {
    	
    	//button listener
        String actionCommand = e.getActionCommand();
        Cursor cursor;
        System.out.println(actionCommand);
        switch (actionCommand) {
        
            case "Color":
                JFrame colorFrame = new JFrame();
                colorFrame.setSize(300, 200);
                colorFrame.setLocationRelativeTo(null);
                colorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                Color cursorColor = JColorChooser.showDialog(colorFrame, "Choose a color", null);
                if (cursorColor != null) {
                    color = cursorColor;
                }
                break;
                
            case "Line":
            case "Rectangle":
            case "Circle":
            case "Triangle":
                shape = actionCommand;
                cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                frame.setCursor(cursor);
                break;
                
            case "Pen":
            case "Text":
                shape = actionCommand;
                cursor = new Cursor(Cursor.DEFAULT_CURSOR);
                frame.setCursor(cursor);
                break;
                
            default:
                System.out.println("actionCommand: " + actionCommand);
        }
    }

    
    //justify if curcor is pressed, if cursor
    //presses, then it will invole two mouserelease and dragg methods
    //this method is to record the first data of all shapes 
    @SuppressWarnings("unchecked")
	@Override
    public void mousePressed(MouseEvent e) {
        startx = e.getX();
        starty = e.getY();
        if (!graphic.getColor().equals(color)) {
            graphic.setColor(color);
        }
        if (shape.equals("Pen")) {
            rgb = getColor(color);
            graphic.setColor(color);
            graphic.setStroke(new BasicStroke(fontSize));
            graphic.drawLine(startx, starty, startx, starty);
            JSONObject command = new JSONObject();
            command.put("Shape", "Pen-start");
            command.put("Color", rgb);
            command.put("FontSize", fontSize);
            command.put("StartX",startx);
            command.put("StartY",starty);
            Tools.addData(command.toJSONString());
            sendShape(command);
        }
    }

    
    
    //justify if curcor is released, if cursor
    //releases, then call drawing method
    @SuppressWarnings("unlikely-arg-type")
    @Override
    public void mouseReleased(MouseEvent e) {
        endx = e.getX();
        endy = e.getY();
        switch (shape) {
        
            case "Line":
                drawing();
                System.out.println("mouseReleased - line");
                break;
                
            case "Rectangle":
                drawing();
                System.out.println("mouseReleased - rectangle");
                break;
                
            case "Triangle":
                drawing();
                System.out.println("mouseReleased - Triangle");
                break;
                
            case "Circle":
                drawing();
                System.out.println("mouseReleased - Circle");
                break;
                
            case "Text":
                drawing();
                System.out.println("mouseReleased - Text");
                break;
                
        }

    }

    
    
    
    //This method is to record the pen moving path,
    //also send the shape's information to server
    //in json format.
    @SuppressWarnings("unchecked")
	@Override
    public void mouseDragged(MouseEvent e) {
        endx = e.getX();
        endy = e.getY();
        if (shape.equals("Pen")) {
            graphic.setColor(color);
            graphic.setStroke(new BasicStroke(fontSize));
            graphic.drawLine(endx, endy, endx, endy);
            JSONObject command = new JSONObject();
            command.put("Shape", "Pen-end");
            command.put("Color", rgb);
            command.put("FontSize", fontSize);
            command.put("EndX",endx);
            command.put("EndY",endy);
            Tools.addData(command.toJSONString());
            sendShape(command);
        }
    }
    
    
    
    //justify shape, 
    //then parse json data to readable data, 
    //finall repaint this readable data
    public void redraw(JSONObject command) {
    	
    	//shape and fontsize
        String shape = command.get("Shape").toString();
        int fontSize = Integer.parseInt(command.get("FontSize").toString());
        
        
        //color
        String[] colorStr = command.get("Color").toString().split(" ");
        int red = Integer.parseInt(colorStr[0]); 
        int green = Integer.parseInt(colorStr[1]);  
        int blue = Integer.parseInt(colorStr[2]);
        Color reColor = new Color(red,green,blue);

        
        //parse json shape
        switch (shape) {
 
            case "Circle":
                graphic.setColor(reColor);
                graphic.setStroke(new BasicStroke(fontSize));
                int startx = Integer.parseInt(command.get("StartX").toString());
                int starty = Integer.parseInt(command.get("StartY").toString());
                int endx = Integer.parseInt(command.get("EndX").toString());
                int endy = Integer.parseInt(command.get("EndY").toString());
                if (startx < endx && starty < endy) {
                    graphic.drawOval(startx, starty, Math.abs(endx - startx), Math.abs(endx - startx));
                }
                if (startx > endx && starty < endy) {
                    graphic.drawOval(endx, starty, Math.abs(endx - startx), Math.abs(endx - startx));
                }
                if (startx < endx && starty > endy) {
                    graphic.drawOval(startx, endy, Math.abs(endx - startx), Math.abs(endx - startx));
                }
                if (startx > endx && starty > endy) {
                    graphic.drawOval(endx, endy, Math.abs(endx - startx), Math.abs(endx - startx));
                }
                break;
                
            case "Line":
                graphic.setColor(reColor);
                graphic.setStroke(new BasicStroke(fontSize));
                int startxL = Integer.parseInt(command.get("StartX").toString());
                int startyL = Integer.parseInt(command.get("StartY").toString());
                int endxL = Integer.parseInt(command.get("EndX").toString());
                int endyL = Integer.parseInt(command.get("EndY").toString());
                graphic.drawLine(startxL, startyL, endxL, endyL);
                break;
                
            case "Rectangle":
                graphic.setColor(reColor);
                graphic.setStroke(new BasicStroke(fontSize));
                int startxR = Integer.parseInt(command.get("StartX").toString());
                int startyR = Integer.parseInt(command.get("StartY").toString());
                int endxR = Integer.parseInt(command.get("EndX").toString());
                int endyR = Integer.parseInt(command.get("EndY").toString());
                graphic.drawRect(Math.min(startxR, endxR), Math.min(startyR, endyR), Math.abs(startxR - endxR), Math.abs(startyR - endyR));
                break;
                
            case "Triangle":
                graphic.setColor(reColor);
                graphic.setStroke(new BasicStroke(fontSize));
                int startxT = Integer.parseInt(command.get("StartX").toString());
                int startyT = Integer.parseInt(command.get("StartY").toString());
                int endxT = Integer.parseInt(command.get("EndX").toString());
                int endyT = Integer.parseInt(command.get("EndY").toString());
                graphic.drawLine(startxT, endyT, endxT, endyT);
                graphic.drawLine(startxT, endyT, (startxT + endxT) / 2, startyT);
                graphic.drawLine(endxT, endyT, (startxT + endxT) / 2, startyT);
                break;
                
            case "Text":
                graphic.setColor(reColor);
                graphic.setStroke(new BasicStroke(fontSize));
                String text = (String) command.get("Text".toString());
                int endxText = Integer.parseInt(command.get("EndX").toString());
                int endyText = Integer.parseInt(command.get("EndY").toString());
                graphic.drawString(text, endxText, endyText);
                break;
                
            case "Pen-start":
                graphic.setColor(reColor);
                graphic.setStroke(new BasicStroke(fontSize));
                int startxP = Integer.parseInt(command.get("StartX").toString());
                int startyP = Integer.parseInt(command.get("StartY").toString());
                graphic.drawLine(startxP, startyP, startxP, startyP);
                break;
                
            case "Pen-end":
                graphic.setColor(reColor);
                graphic.setStroke(new BasicStroke(fontSize));
                int endxP = Integer.parseInt(command.get("EndX").toString());
                int endyP = Integer.parseInt(command.get("EndY").toString());
                graphic.drawLine(endxP, endyP, endxP, endyP);
                break;
                
        }
    }

    
    
    
    
    //drawing shape method based on button selected, once the action finished,
    //send the shape's information to server in json format.
    @SuppressWarnings("unchecked")
    public void drawing() {
        JSONObject command = new JSONObject();
        switch (shape) {
        	
            case "Circle":
                rgb = getColor(color);
                graphic.setColor(color);
                graphic.setStroke(new BasicStroke(fontSize));
                if (startx < endx && starty < endy) {
                    graphic.drawOval(startx, starty, Math.abs(endx - startx), Math.abs(endx - startx));
                }
                if (startx > endx && starty < endy) {
                    graphic.drawOval(endx, starty, Math.abs(endx - startx), Math.abs(endx - startx));
                }
                if (startx < endx && starty > endy) {
                    graphic.drawOval(startx, endy, Math.abs(endx - startx), Math.abs(endx - startx));
                }
                if (startx > endx && starty > endy) {
                    graphic.drawOval(endx, endy, Math.abs(endx - startx), Math.abs(endx - startx));
                }
                command.put("Shape", "Circle");
                command.put("Color", rgb);
                command.put("FontSize", fontSize);
                command.put("StartX", startx);
                command.put("StartY", starty);
                command.put("EndX", endx);
                command.put("EndY", endy);
                Tools.addData(command.toJSONString());
                sendShape(command);
                break;

            case "Line":
                rgb = getColor(color);
                graphic.setColor(color);
                graphic.setStroke(new BasicStroke(fontSize));
                graphic.drawLine(startx, starty, endx, endy);
                command.put("Shape", "Line");
                command.put("Color", rgb);
                command.put("FontSize", fontSize);
                command.put("StartX", startx);
                command.put("StartY", starty);
                command.put("EndX", endx);
                command.put("EndY", endy);
                Tools.addData(command.toJSONString());
                sendShape(command);
                break;
                
            case "Rectangle":
                rgb = getColor(color);
                graphic.setColor(color);
                graphic.setStroke(new BasicStroke(fontSize));
                graphic.drawRect(Math.min(startx, endx), Math.min(starty, endy), Math.abs(startx - endx), Math.abs(starty - endy));
                command.put("Shape", "Rectangle");
                command.put("Color", rgb);
                command.put("FontSize", fontSize);
                command.put("StartX", startx);
                command.put("StartY", starty);
                command.put("EndX", endx);
                command.put("EndY", endy);
                Tools.addData(command.toJSONString());
                sendShape(command);
                break;
                
            case "Triangle":
                rgb = getColor(color);
                graphic.setColor(color);
                graphic.setStroke(new BasicStroke(fontSize));
                graphic.drawLine(startx, endy, endx, endy);
                graphic.drawLine(startx, endy, (startx + endx) / 2, starty);
                graphic.drawLine(endx, endy, (startx + endx) / 2, starty);
                command.put("Shape", "Triangle");
                command.put("Color", rgb);
                command.put("FontSize", fontSize);
                command.put("StartX", startx);
                command.put("StartY", starty);
                command.put("EndX", endx);
                command.put("EndY", endy);
                Tools.addData(command.toJSONString());
                sendShape(command);
                break;  
                
            case "Text":
                rgb = getColor(color);
                graphic.setColor(color);
                graphic.setStroke(new BasicStroke(fontSize));
                String str = JOptionPane.showInputDialog("input a text:");	
                if(str != null) {
                    graphic.drawString(str, endx, endy);
                    command.put("Shape", "Text");
                    command.put("Color", rgb);
                    command.put("FontSize", fontSize);
                    command.put("Text", str);
                    command.put("EndX", endx);
                    command.put("EndY", endy);
                    Tools.addData(command.toJSONString());
                    sendShape(command);
                }
                else {	
                	JOptionPane.showMessageDialog(this.frame,"cancel adding text frame","Message",JOptionPane.CANCEL_OPTION);
                }      
                break;
            	
        }
    }

    
    
    
    
    
    

    //send shape's information to server in json format
    @SuppressWarnings("unchecked")
    public void sendShape(JSONObject command) {
        try {
            outStream.writeUTF("Draw/" + command.toJSONString());
            outStream.flush();
        } catch (IOException e) {
            	System.out.println("System failed to send shape's information to server");
        }
    }

    
    //get color elements (red, green, blue)
    private String getColor(Color color) {
        return color.getRed() + " " + color.getGreen() + " " + color.getBlue();
    }

    
    //add new shape's information to list
    public void update(String line) {
        Tools.addData(line);
    }

    //clear all shape's information from list
    public void clearAllData() {
        Tools.allData.clear();
    }

    @Override
    public void mouseEntered(MouseEvent e) {


    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {


    }


    @Override
    public void mouseMoved(MouseEvent e) {


    }


}