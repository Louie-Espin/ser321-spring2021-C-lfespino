package whosThatPokemon;

import java.net.*;
import java.awt.image.BufferedImage;
import java.io.*;
import org.json.*;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.json.JSONException;
import java.util.Random;

/**
 * TCP Server class
 */
public class Server {
	
	public static Boolean clientOn;
	
	public static void main (String args[]) {
		
		int port = 8080; // default port
		
		if (args.length != 1 && args.length != 0) {
			System.out.println("Use 'gradle runServerTCP -Pport=[]'");
			System.out.println("Or 'gradle runServerTCP' for port=8080");
			System.exit(0);
		}
		try {
			if (args.length == 1) // change port num if it was provided
				port = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			System.out.println("[Pport] must be an integer!");
			nfe.printStackTrace();
			System.exit(1);
		}
		
		serverStart(port);
	}
	
	public static void serverStart(int p) {
		try {
			// New server socket
			ServerSocket serverSocket = new ServerSocket(p);
			System.out.println("READY AT PORT: " + p);
			int numMatches = 0;
			
			while(true) {
				System.out.println("WAITING FOR CONNECTIONS...");
				Socket clientSocket = serverSocket.accept();
				
				// create an input stream to receive data
				InputStream fromClient = clientSocket.getInputStream();
				// create an output stream to send data
				OutputStream toClient = clientSocket.getOutputStream();
				
				String cName = "unkown"; // client name string
				int numQuestions = 0; // client numQuestions int
				
				System.out.println("SERVER CONNECTED TO CLIENT!");
				
				JSONsend(toClient, JSONtext("Connection established."));
				JSONsend(toClient, JSONtext("Welcome to 'Who's That Pokémon!'"));
				JSONsend(toClient, JSONimage("pokemon-default.jpg")); // send image 'Who's That Pokémon!'
				
				JSONsend(toClient, JSONtext("What is your name?"));
				JSONObject clientName = JSONreceive(fromClient); // receive name
				if (clientName.has("data")) cName = clientName.getString("data");
				
				JSONsend(toClient, JSONtext(cName + "! How many Pokémon would you like to find? Enter a number."));
				JSONObject clientQ = JSONreceive(fromClient); // receive numQuestions
				if (clientName.has("data")) numQuestions = Integer.parseInt((clientQ.getString("data")));
				
				JSONsend(toClient, JSONtext(cName + ", you will have to find " + numQuestions + " Pokémon!"));
				
				// new thread for this client's match
				numMatches++;
				new Thread(new Match(clientSocket, fromClient, toClient, cName, numQuestions, numMatches)).start();
				System.out.println("CLIENT MATCH START!");
			}
		} catch (IOException IOex) {
			System.out.println("IOException: CONNECTION FAILED.");
			System.exit(1);
		} catch (JSONException Jex) {
			System.out.println("JSONException: Bad JSON.");
			System.exit(1);
		} catch (Exception e) {
			System.out.println("SERVER EXCEPTION.");
			System.exit(1);
		}
	}
	
	/**
	 * Match: nested class that Server uses to deal with a 'Who's that Pokemon?' match
	 */
	static class Match implements Runnable {
		/**
		 * Pokemon: nested class to be used in a match.
		 *
		 */
		private static class Pokemon {
			/**
			 * Pokemon properties
			 */
			private String name;
			private String image;
			private int number;
			public enum names {
				MEW, BULBASAUR, BLASTOISE, PIKACHU, NINETALES, DIGLETT, MEOWTH, PSYDUCK, ARCANINE,
				ABRA, PONYTA, GASTLY, GENGAR, RHYDON, SCYTHER, GYARADOS, EEVEE, SNORLAX, MEWTWO
			}
			
			public Pokemon(String name, int num) {
				this.name = name;
				this.number = num;
				this.image = "pokemon-" + number + ".jpg";
			}
			
			public static Pokemon[] allPokes() {
				Pokemon[] pokeArray = new Pokemon[19];
				for (names p: names.values()) {
					pokeArray[p.ordinal()] = new Pokemon(p.toString(), p.ordinal());
					// System.out.println("This pokemon is " + pokeArray[p.ordinal()].name + pokeArray[p.ordinal()].number);
				}
				return pokeArray;
			}
			
			public void setName(String n) {
				this.name = n;
			}
			public String getName() {
				return this.name;
			}
			
			public void setNumber(int n) {
				this.number = n;
			}
			public int setNumber() {
				return this.number;
			}
			
			public void setImage() {
				this.image = "pokemon-" + this.number + ".jpg";
			}
			public String getImage() {
				return this.image;
			}
		}
		/*
		 * properties of the Connect4Match class
		 */
		private Socket client;
		private InputStream fromClient;
		private OutputStream toClient;
		private String clientName;
		private int numQuestions;
		private int matchID;
		private int time;
		Pokemon[] allPokemon;
		Pokemon[] questionPokemon;
		private int totalPokemon = 19;
		
		/**
		 * Match's constructor creates a new match with a socket
		 */
		public Match(Socket c, InputStream in, OutputStream out, String n, int q, int i) {
			this.client = c;
			this.fromClient = in;
			this.toClient = out;
			this.clientName = n;
			this.numQuestions = q;
			this.matchID = i;
			this.time = q * 5;
		}
		
		/**
		 * run: deals with sending and receiving data from client
		 */
		@Override
		public void run() {
			try {
				
				JSONsend(toClient, JSONtext("You will have " + time + " seconds to answer!"));
				JSONsend(toClient, JSONtext("Randomizing pokemon..."));
				
				allPokemon = Pokemon.allPokes();
				questionPokemon = new Pokemon[numQuestions]; 
				
				Random randomizer = new Random();
				
				for (int i = 0; i < numQuestions; i++) {
					questionPokemon[i] = allPokemon[randomizer.nextInt(totalPokemon)];
				}
				
				JSONsend(toClient, JSONtext("Type 'START' to begin match!"));
				String startGame = null;
				JSONObject startSignal = JSONreceive(fromClient); // receive start signal
				if (startSignal.has("data")) startGame = (startSignal.getString("data"));
				
				JSONsend(toClient, JSONtext(Integer.toString(numQuestions)));
				JSONsend(toClient, JSONtext("Match begins!"));
				
				for (int i = 0; i < numQuestions; i++) {
					JSONsend(toClient, JSONimage(questionPokemon[i].getImage()));
					JSONsend(toClient, JSONtext("Number of correct answers: " + i));
					JSONsend(toClient, JSONtext("Who's That Pokémon!"));
					
					String currentAnswer = ""; // stores the current answer
					JSONObject clientAnswer = JSONreceive(fromClient); // receive numQuestions
					if (clientAnswer.has("data")) currentAnswer = clientAnswer.getString("data");
					
					if (currentAnswer.equalsIgnoreCase(questionPokemon[i].getName())) {
						JSONsend(toClient, JSONtext("CORRECT!"));
					}
				}
				
				
			} catch (IOException IOex) {
				System.out.println("CLIENT DISCONNECTED");
			}
		}
	}
	
	/*
	 * request: { "selected": <int: 1=joke, 2=quote, 3=image, 4=random> }
	 * 
	 * response: {"datatype": <int: 1-string, 2-byte array>, "type": <"joke",
	 * "quote", "image">, "data": <thing to return> }
	 * 
	 * error response: {"error": <error string> }
	 */
	public static JSONObject JSONtext(String s) {
		JSONObject json = new JSONObject();
		json.put("datatype", 1);
		json.put("type", "text");
		json.put("data", s);
		return json;
	}
	
	public static JSONObject JSONerror(String err) {
		JSONObject json = new JSONObject();
		json.put("error", err);
		return json;
	}
	
	public static JSONObject JSONimage(String s) throws IOException {
		JSONObject json = new JSONObject();
		json.put("datatype", 2);
		json.put("type", "image");
		
		File imgFile = new File("img/jpg/" + s);
		if (!imgFile.exists()) {
			System.err.println("Cannot find file");
			System.exit(-1);
		}
		
		BufferedImage img = ImageIO.read(imgFile);
		byte[] bytes = null;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			ImageIO.write(img, "jpg", out);
			bytes = out.toByteArray();
		}
		if (bytes != null) {
			Base64.Encoder encoder = Base64.getEncoder();
			json.put("data", encoder.encodeToString(bytes));
			return json;
		} return JSONerror("Unable to save image to byte array");
	}
	
	/** 
	 * JSONsend: Sends server JSON to client
	 * @throws IOException: exception thrown when Input/Output is not found
	 */
	public static void JSONsend(OutputStream out, JSONObject json) throws IOException {
        byte[] outputBytes = JsonUtils.toByteArray(json);
        NetworkUtils.Send(out, outputBytes);
    }
	
	/** 
	 * JSONreceive: Receives client JSON
	 * @throws IOException: exception thrown when Input/Output is not found
	 */
	public static JSONObject JSONreceive(InputStream in) throws IOException {
		byte[] inputBytes = NetworkUtils.Receive(in);
		JSONObject input = JsonUtils.fromByteArray(inputBytes);
		
		if (input.has("error")) {
			System.out.println(input.getString("error"));
		} else if (input.has("data")){
	        System.out.println("Client data received!");
	    }
		return input;
	}
}