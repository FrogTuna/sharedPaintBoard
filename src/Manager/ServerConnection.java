package Manager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import User.UserFrame;

public class ServerConnection extends Thread {

    //Fields
    public Socket socket = null;
    public JSONParser parser;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    public JSONObject allObject;

    
    
    //constructor
    public ServerConnection(Socket socket) {
        this.socket = socket;
        try {
            parser = new JSONParser();
            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Could not use reader and writer on the serverconnection class.");
        }
    }

    
    
    //receive the request from users
    public void run() {
        try {
            String str;
            while ((str = inStream.readUTF()) != null) {
                String[] splitStatus = str.split("/");
                String status = splitStatus[0];
                JSONObject object = (JSONObject) parser.parse(splitStatus[1]);

                switch (status) {
                    case "Request":
                        String name = object.get("Name").toString();
                        if (ConnectionManager.connectionMap.containsKey(name)) {
                            return;
                        } else {
                            ConnectionManager.connectionMap.put(name, this);
                            ConnectionManager.newUser(socket);
                            ConnectionManager.updatePaintingBoardcast(Tools.allData);
                        }
                        break;
                        
                    case "Draw":
                        ManagerFrame.listener.redraw(object);
                        ConnectionManager.boardCast(str, socket);
                        break;
                }
            }

        } catch (IOException e) {

            System.out.println("failed to read request from users.");

        } catch (ParseException e) {

            System.out.println("failed to parse json object on the server side.");
        }
    }

    
    //boardcast the draw information to all users
    public void draw(String status) {
        //System.out.println(outStream);
        //System.out.println("draw...." + status);
        try {
            outStream.writeUTF(status);
            outStream.flush();
        } catch (IOException e) {
            System.out.println("failed to send status to manager from server");
        }
    }
}


