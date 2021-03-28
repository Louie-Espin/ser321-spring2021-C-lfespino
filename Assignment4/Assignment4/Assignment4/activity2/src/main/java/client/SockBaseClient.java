package client;

import java.net.*;
import java.io.*;

import org.json.*;

import buffers.RequestProtos.Request;
import buffers.ResponseProtos.Response;
import buffers.ResponseProtos.Entry;

import java.util.*;
import java.util.stream.Collectors;

class SockBaseClient {
	
	public static void NameRequest(String n, OutputStream out) throws IOException {
		Request req = Request.newBuilder()
                .setOperationType(Request.OperationType.NAME)
                .setName(n).build();
		req.writeDelimitedTo(out);
	}
	
	public static void LeaderRequest(OutputStream out) throws IOException {
		Request req = Request.newBuilder()
				.setOperationType(Request.OperationType.LEADER).build();
		req.writeDelimitedTo(out);
	}
	
	public static void NewRequest(OutputStream out) throws IOException {
		Request req = Request.newBuilder()
				.setOperationType(Request.OperationType.NEW).build();
		req.writeDelimitedTo(out);
	}
	
	public static void AnswerRequest(String a, OutputStream out) throws IOException {
		Request req = Request.newBuilder()
                .setOperationType(Request.OperationType.ANSWER)
                .setAnswer(a).build();
		req.writeDelimitedTo(out);
	}
	
	public static void QuitRequest(OutputStream out) throws IOException {
		Request req = Request.newBuilder()
				.setOperationType(Request.OperationType.QUIT).build();
		req.writeDelimitedTo(out);
	}
	
	public void responseHandler() {
		
	}
	
	public static void menuHandler(BufferedReader stdin, OutputStream out) throws IOException {
		System.out.println("Please type your choice: 1, 2, 3");
		String menuInput = stdin.readLine();
		while (!menuInput.equals("1") && !menuInput.equals("2") && !menuInput.equals("3")) {
			System.out.println("Invalid Answer. Please type your choice");
			menuInput = stdin.readLine();
		}
		switch (menuInput) {
			case ("1"):
				System.out.println("Sending leaderboard request...");
				LeaderRequest(out);
				break;
			case ("2"):
				System.out.println("Sending new game request...");
				NewRequest(out);
				break;
			case ("3"):
				System.out.println("Sending quit request...");
    			QuitRequest(out);
				break;
		}
	}
	
    public static void main (String args[]) throws Exception {
        int i1=0, i2=0;
        int port = 9099; // default port

        // Make sure two arguments are given
        if (args.length != 2) {
            System.out.println("Expected arguments: <host(String)> <port(int)>");
            System.exit(1);
        }
        String host = args[0];
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be integer");
            System.exit(2);
        }
        
        Socket serverSocket = null;
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        OutputStream out = null;
        InputStream in = null;
        String userInput;
        String userName;
        Boolean isRunning = true;

        // Ask user for username
        System.out.println("Please type in your name: ");
        userInput = stdin.readLine();
        userName = userInput;

        try {
            // connect to the server
            serverSocket = new Socket(host, port);

            // write to the server
            out = serverSocket.getOutputStream();
            in = serverSocket.getInputStream();
            
            NameRequest(userName, out);
            
            while (isRunning) {
            	// read from the server
                Response serverResp = Response.parseDelimitedFrom(in);
                
                switch (serverResp.getResponseType()) {
                	case GREETING:
                		System.out.println(serverResp.getGreeting());
                		
                		menuHandler(stdin, out); // Handle menu options
                		break;
                	case LEADER:
                		System.out.println("\n================ LEADERBOARD ================");
                		System.out.println(serverResp.getAllFields());
                		//System.out.println(serverResp.get);
                		System.out.println("================ LEADERBOARD ================\n\n");
                		System.out.println("Type anything and press 'ENTER' to go to the main menu...\n");
                		userInput = stdin.readLine();
                		
                		NameRequest(userName, out);
                		break;
                	case TASK:
                		System.out.println("IMAGE:\n" + serverResp.getImage());
                		System.out.println("TASK:\n" + serverResp.getTask());
                		System.out.println("\nPlease Type An Answer:\n");
                		
                		userInput = stdin.readLine();
                		AnswerRequest(userInput, out); // sends an answer request 
                		break;
                	case WON:
                		System.out.println("\nCONGRATULATIONS! YOU'RE A POKEMON MASTER!\n");
                		System.out.println("Type anything and press 'ENTER' to go to the main menu...\n");
                		userInput = stdin.readLine();
                		
                		NameRequest(userName, out);
                		break;
                	case ERROR:
                		
                		break;
                	case BYE:
                		isRunning = false;
                		break;
                }
            }

        } catch (Exception e) {
        	System.out.println("Couldn't connect to server. Please try again later.");
        } finally {
            if (in != null)   in.close();
            if (out != null)  out.close();
            if (serverSocket != null) serverSocket.close();
        }
    }
}


