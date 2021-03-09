package whosThatPokemon;

import java.io.*;
import java.net.*;
import org.json.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.net.Socket;
import java.net.UnknownHostException;
import org.json.JSONObject;
import java.util.Base64;

/**
 * TCP Client class
 */
public class Client {
	
	/**
	 * Properties of the Client class
	 */
	private static boolean Waiting = true;
	private static boolean Running = false;
	
	/**
	 * main: Client's main method
	 * @param args: string args array
	 */
	public static void main(String[] args) {
		int port = 8080; // default port
		String host = "localhost"; // default host
		
		if (args.length != 2 && args.length != 0) {
			System.out.println("Use 'gradle runClientTCP -Pport=[] -Phost=[]'");
			System.out.println("Or 'gradle runClientTCP' for port=8080 & localhost");
			System.exit(0);
		}
		try {
			if (args.length == 2) { // change port num if it was provided
				port = Integer.parseInt(args[1]);
				host = args[0];
			}	
		} catch (NumberFormatException nfe) {
			System.out.println("[Pport] must be an integer!");
			nfe.printStackTrace();
			System.exit(1);
		}
		
		setRunning(false);
		connectToServer(port, host); // connect to the Server
		Application.launch(ClientGui.class); // run the GUI class
	}
	
	/**
	 * connectToServer: handles the client's connection to the server
	 */
	private static void connectToServer(int p, String h) {
		
		new Thread(() -> { //Control the server connection on a separate thread
			try {
				//create a socket to connect to the server
				Socket serverSocket = new Socket(h, p);
				//create an input stream to receive data
				InputStream fromServer = serverSocket.getInputStream();
				//create an output stream to send data
				OutputStream toServer = serverSocket.getOutputStream();
				
				waitForRunning(); // wait for ClientGui to tell us it is running
				while(getRunning()) {
					
					JSONreceive(fromServer); // receive text "Server connection established."
					JSONreceive(fromServer); // receive text "Welcome to 'Who's That Pokémon!'"
					JSONreceive(fromServer); // receive image 'Who's That Pokémon!'
					
					JSONreceive(fromServer); // receive text "What is your name?"
					setWaiting(true); waitForPlayerAction();
					JSONsend(toServer); // responds
					
					JSONreceive(fromServer); // receive text "Hello name! How many Pokémon would you like to find?"
					setWaiting(true); waitForPlayerAction();
					JSONsend(toServer); // responds
					
					JSONreceive(fromServer); // receive text '[name], you will have to find [num] Pokémon!'
					JSONreceive(fromServer); // receive text "You will have [time] seconds to answer!"
					JSONreceive(fromServer); // Randomizing pokemon...
					
					JSONreceive(fromServer); // Type 'START' to begin match!
					setWaiting(true); waitForPlayerAction();
					JSONsend(toServer); // responds
					
					JSONreceive(fromServer); // receive number of turns
					JSONreceive(fromServer); // receive text "Match begins!"
					
					Boolean gameDone = false;
					while (!gameDone) {
						JSONreceive(fromServer); // receive new pokemon image
						JSONreceive(fromServer); // receive text "Number of correct answers:"
						JSONreceive(fromServer); // receive text "Who's That Pokémon!"
						
						setWaiting(true); waitForPlayerAction();
						JSONsend(toServer); // responds
						
						JSONreceive(fromServer); // receive text "CORRECT!"
					}
					
				}
				serverSocket.close();
				System.exit(0);
				
			} catch (UnknownHostException UHex) {
				Platform.runLater(() -> ClientGui.setOutputText("UnknownHostException: Couldn't connect to server. Try a different host name."));
				// UHex.printStackTrace();
			} catch (ConnectException CNex) {
				Platform.runLater(() -> ClientGui.setOutputText("ConnectException: Couldn't connect to server. Ensure the server is running and try again."));
				// CNex.printStackTrace();
			} catch (IOException IOex) {
				Platform.runLater(() -> ClientGui.setOutputText("IOException: Please close the program and try again."));
				// IOex.printStackTrace();
			} catch (InterruptedException IPex) {
				Platform.runLater(() -> ClientGui.setOutputText("InterruptedException: Please close the program and try again."));
				// IPex.printStackTrace();
			} catch (Exception ex) {
				Platform.runLater(() -> ClientGui.setOutputText("Exception: Please close the program and try again."));
				// ex.printStackTrace();
			}
		}).start();
	}
	
	/*
	 * getWaiting: gets Bool Waiting
	 */
	public static Boolean getWaiting() {
		return Waiting;
	}
	
	/*
	 * setWaiting: sets Bool Waiting
	 */
	public static void setWaiting(Boolean b) {
		Waiting = b;
	}
	
	/*
	 * getRunning: gets Bool Running
	 */
	public static Boolean getRunning() {
		return Running;
	}
	
	/*
	 * setRunning: sets Bool Running
	 */
	public static void setRunning(Boolean b) {
		Running = b;
	}
	
	/** 
	 * waitForPlayerAction: wait for the player to make a submission
	 * @throws InterruptedException: exception thrown when sleep is interrupted
	 */
	private static void waitForPlayerAction() throws InterruptedException {
		while (Waiting) {
			Thread.sleep(100);
		}
		Waiting = true;
	}
	
	/** 
	 * waitForRunning: wait for the program to start running
	 * @throws InterruptedException: exception thrown when sleep is interrupted
	 */
	private static void waitForRunning() throws InterruptedException {
		while (!Running) {
			Thread.sleep(100);
		}
		Waiting = false;
	}
	
	private static void stringToGUI(String s) {
		Platform.runLater(() -> ClientGui.setOutputText(s));
	}
	
	private static void imageToGUI(Image img) {
		Platform.runLater(() -> ClientGui.setNewImage(img));
	}
	
	/** 
	 * JSONtext: Creates a JSON Object based on text parameter
	 */
	public static JSONObject JSONtext(String text) {
        JSONObject request = new JSONObject();
        request.put("data", text);
        return request;
    }

	/** 
	 * JSONsend: Sends client's answer to the server
	 * @throws IOException: exception thrown when Input/Output is not found
	 */
	public static void JSONsend(OutputStream out) throws IOException {
		String answer = ClientGui.getAnswer();
        stringToGUI("You:");
        stringToGUI(answer);
        stringToGUI("");
        byte[] outputBytes = JsonUtils.toByteArray(JSONtext(answer));
        NetworkUtils.Send(out, outputBytes);
    }
	
	/** 
	 * receiveJSON: Receives JSON data from the server
	 * @throws IOException: exception thrown when Input/Output is not found
	 */
	public static void JSONreceive(InputStream in) throws IOException {
		
		byte[] inputBytes = NetworkUtils.Receive(in);
		JSONObject input = JsonUtils.fromByteArray(inputBytes);

		if (input.has("error")) {
			stringToGUI("Server: " + input.getString("error"));
			stringToGUI("");
		}
		else {
			switch (input.getInt("datatype")) {
				case (1): {
					stringToGUI("Server:");
					stringToGUI(input.getString("data"));
					stringToGUI("");
					break;
				}
				case (2): {
					stringToGUI("Server sent an image");
					stringToGUI("");
					Base64.Decoder decoder = Base64.getDecoder();
					byte[] bytes = decoder.decode(input.getString("data"));
					Image img = null;
					
					try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
						img = new Image(bais);
					}
					if (img != null) imageToGUI(img);
					break;
				}
            }
        }
	}
}
