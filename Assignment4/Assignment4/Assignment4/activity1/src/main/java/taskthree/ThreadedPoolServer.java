package taskthree;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONObject;

import taskone.JsonUtils;
import taskone.NetworkUtils;
import taskone.Performer;
import taskone.StringList;

public class ThreadedPoolServer {
	
	public static void main(String args[]) throws Exception {
		
		ServerSocket threadedServer;
        StringList strings = new StringList();
        int port = 8000;
        int bound = 10;
        
        if (args.length != 2) {
            // gradle runServer -Pport=9099 -q --console=plain
            System.out.println("Usage: gradle runTask3 -Pport=9099 -Pbound=10");
            System.exit(0);
        }
        try {
            port = Integer.parseInt(args[0]);
            bound = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] and [Bound] must be integers");
            System.exit(0);
        }
        
        threadedServer = new ServerSocket(port);
        System.out.println("SERVER STARTED AT PORT: " + port + " WITH BOUND: " + bound);
        System.out.println("\nNote! You may change bound with: gradle runTask3 -Pbound=[int]");
        System.out.println("or change bound and host with: gradle runTask3 -Pport=[int] -Pbound=[int]\n");
        
        final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(bound);
        
        try {
        	while (true) {
        		System.out.println("WAITING FOR REQUESTS...");
                Socket clientSocket = threadedServer.accept();
                
                if (threadPool.getActiveCount() >= threadPool.getMaximumPoolSize()) {
                	System.out.println("Server too busy for new connection!");
                	System.out.println("max threads available: " + threadPool.getMaximumPoolSize());
                    System.out.println("currently active threads: " + threadPool.getActiveCount());
                    System.out.println("Sending 'BusyServer' error message...");
                    sendBusyErr(clientSocket);
                } else {
                	System.out.println("A CLIENT HAS CONNECTED! STARTING THREAD...");
                    ServerRunnable newClient = new ServerRunnable(clientSocket, strings);
                	threadPool.execute(newClient);
                }
            }	
        } catch (Exception e) {
        	System.out.println("SERVER CLOSING.");
        	threadedServer.close();
        	System.exit(0);
        }
        
	}
	
	private static void sendBusyErr(Socket client) throws IOException {
		OutputStream out = client.getOutputStream();
		// create a new error message
		JSONObject json = new JSONObject();
        json.put("ServerBusy", "Sorry, the server is busy right now. Please try again later.");
		// we are converting the JSON object we have to a byte[]
        byte[] output = JsonUtils.toByteArray(json);
        NetworkUtils.send(out, output);
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