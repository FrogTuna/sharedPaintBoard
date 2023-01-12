package Manager;

import java.awt.event.WindowEvent;
import java.net.Socket;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Common.GUIDesign;
import User.UserFrame;


public class ConnectionManager {

	
	
	
	//fields
    public static Map<String, ServerConnection> connectionMap = new HashMap<>();

    
    
    
    //add data
    public static void canvasRepaint(String shape) {
        Common.GUIDesign.listener.update(shape);
    }
    
    
    
    
    //justify if user is allowed to join by manager
    public static void newUser(Socket socket) {
        int message = JOptionPane.showConfirmDialog(null, "Message: A new user want to join in, Do you agree?", "Message", JOptionPane.YES_NO_OPTION);

        if (message == JOptionPane.YES_OPTION) {
            System.out.println("a new user joined to shared whiteboard.");
            GUIDesign.reloadUser();
        } else if (message == JOptionPane.NO_OPTION) {
            for (String userName : connectionMap.keySet()) {
                ServerConnection conn = connectionMap.get(userName);
                if (conn.socket.equals(socket)) {
                    conn.draw("Exit/");
                    connectionMap.remove(userName);
                    break;
                }
            }
        }
    }

    
    
    
    
    public static void boardCast(String status, Socket socket) {
        Collection<ServerConnection> values = connectionMap.values();
        //System.out.println("boardCast: " + values);
        for (ServerConnection conn : values) {
            conn.draw(status);
        }
    }

    
    
    //boardcast draw request and its shape data to all users
    public static void updatePaintingBoardcast(List<String> allData) {

        for (String line : allData) {
            for (ServerConnection conn : connectionMap.values()) {
                conn.draw("Draw/" + line);
            }
        }
    }

    
    
    //boardcast clear request and its data to all users
    public static void clearPaint() {
        for (ServerConnection conn : connectionMap.values()) {
            conn.draw("Clear/");
        }
    }

    
    
    
    //boardcast exit request to all users
    public static void systemOverBoardcast() {
        Set<String> strings = connectionMap.keySet();
        for (String name : strings) {
            connectionMap.get(name).draw("Exit/");
            connectionMap.remove(name);
        }
    }


}

