
package Manager;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Server {
	
	//fields
    public int port;
    public String username;

    
    //constructor
    public Server(String username, int port) {
        this.port = port;
        this.username = username;
        createServer();
    }

    
    //set up a tcp connection
    @SuppressWarnings("resource")
    public void createServer() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("server failed to connect, you are running on same port number network.");
            return;
        }
        System.out.println("Server is running!");
        
        //listen client requests (set up connection)
        while (true) {
            try {
                Socket socket = server.accept();
                ServerConnection serverConnect = new ServerConnection(socket);
                serverConnect.start();
                System.out.println("Server handled a new requests");
            } catch (IOException e1) {
                System.out.println("Server connect failed");
                return;
            } catch (NullPointerException e1) {
                System.out.println("server is null");
                return;
            }
        }
    }
}