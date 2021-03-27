package tasktwo;

import java.net.ServerSocket;
import java.net.Socket;

import taskone.Performer;
import taskone.StringList;

public class ThreadedServer {
	
	public static void main(String args[]) throws Exception {
		
		ServerSocket threadedServer;
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
        
        threadedServer = new ServerSocket(port);
        System.out.println("SERVER STARTED AT PORT: " + port);
        
        try {
        	while (true) {
                System.out.println("WAITING FOR REQUESTS...");
                Socket clientSocket = threadedServer.accept();
                System.out.println("A CLIENT HAS CONNECTED! STARTING THREAD...");
                ServerRunnable newClient = new ServerRunnable(clientSocket, strings);
                new Thread(newClient).start();
            }	
        } catch (Exception e) {
        	System.out.println("SERVER CLOSING.");
        	threadedServer.close();
        	System.exit(0);
        }
        
	}
	
	private static class ServerRunnable implements Runnable {
		
		private Socket clientSocket;
		private StringList strings;
		
		/**
		 * ServerRunnable: constructor for ServerRunnable
		 * @param c
		 * @param str
		 */
		ServerRunnable(Socket c, StringList str) {
			this.clientSocket = c;
			this.strings = str;
		}
		
		public void run() {
			try {
				Performer performer = new Performer(clientSocket, strings);
	            synchronized (performer) {
	            	performer.doPerform();
	            }
	            
			} catch (Exception e) {
				System.out.println("CLIENT DISCONNECTED");
			}
		}
	}
}
