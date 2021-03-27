/**
  File: Server.java
  Author: Student in Fall 2020B
  Description: Server class in package taskone.
*/

package taskone;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.JSONObject;

/**
 * Class: Server
 * Description: Server tasks.
 */
class Server {

    public static void main(String[] args) throws Exception {
        
    	ServerSocket server;
        StringList strings = new StringList();
        int port = 8000;
        
        if (args.length != 1) {
            // gradle runServer -Pport=9099 -q --console=plain
            System.out.println("Usage: gradle runServer -Pport=9099 -q --console=plain");
            System.exit(0);
        }
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be an integer");
            System.exit(1);
        }
        
        server = new ServerSocket(port);
        System.out.println("SERVER STARTED AT PORT: " + port);
        
        try {
        	while (true) {
                System.out.println("WAITING FOR REQUESTS...");
                
                try (Socket clientSocket = server.accept()) {
                	System.out.println("CLIENT CONNECTED. STARTING PERFORMER");
                	Performer performer = new Performer(clientSocket, strings);
                    performer.doPerform();
                } catch (Exception e){
                	System.out.println("CLIENT DISCONNECTED. RESTARTING...");
                }
            }	
        } catch (Exception e) {
        	System.out.println("SERVER CLOSING.");
        	server.close();
        	System.exit(0);
        }
    }
}
