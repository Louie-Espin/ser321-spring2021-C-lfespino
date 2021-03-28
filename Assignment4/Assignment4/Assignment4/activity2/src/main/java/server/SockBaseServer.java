package server;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.*;
import java.lang.*;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Logs;
import buffers.RequestProtos.Message;
import buffers.ResponseProtos.Response;
import buffers.ResponseProtos.Entry;

class SockBaseServer {
	
	static String logFilename = "logs.txt";
	
	public static void main (String args[]) throws Exception {
        if (args.length != 2) {
            System.out.println("Expected arguments: <port(int)> <delay(int)>");
            System.exit(1);
        }
        int port = 9099; // default port
        int sleepDelay = 10000; // default delay

        try {
            port = Integer.parseInt(args[0]);
            sleepDelay = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port|sleepDelay] must be an integer");
            System.exit(2);
        }
        
        Socket clientSocket = null;
        ServerSocket serverSock = null;
        Game game = new Game();
        
        try {
            serverSock = new ServerSocket(port);
            System.out.println("SERVER STARTED AT PORT: " + port);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
        
        try {
        	while (true) {
        		System.out.println("WAITING FOR REQUESTS...");
        		clientSocket = serverSock.accept();
        		System.out.println("A CLIENT HAS CONNECTED! STARTING THREAD...");
        		ServerRunnable newGame = new ServerRunnable(clientSocket, game);
        		new Thread(newGame).start();
        	}
        } catch (Exception e) {
        	System.out.println("SERVER CLOSING.");
        	serverSock.close();
        	System.exit(0);
        }
    }
	
	/**
     * Reading the current log file
     * @return Logs.Builder a builder of a logs entry from protobuf
     */
    public static Logs.Builder readLogFile() throws Exception {
        Logs.Builder logs = Logs.newBuilder();

        try {
            // just read the file and put what is in it into the logs object
            return logs.mergeFrom(new FileInputStream(logFilename));
        } catch (FileNotFoundException e) {
            System.out.println(logFilename + ": File not found.  Creating a new file.");
            return logs;
        }
    }
    
    public static void leaderWriterJSON(String name, int win, int login) {
    	try {
    		String jsonDir = "txtFiles/leaderboard.json";
        	File jsonFile = new File(jsonDir);
        	if (jsonFile.exists()) {
        		InputStream inS = new FileInputStream(jsonDir);
        		JSONObject jsonObj = (JSONObject) new JSONTokener(inS).nextValue();
        		JSONArray entries = jsonObj.getJSONArray("entries");
        		
        		for (int i = 0; i < entries.length(); i++) {
        			JSONObject entry = entries.getJSONObject(i);
        			if (entry.getString("name").equals(name)) {
        				
        				int entryWins = entry.getInt("wins");
        				int entryLogins = entry.getInt("logins");
        				entryWins += win; entryLogins += login;
        				entries.remove(i); // remove the old entry
        				
        				JSONObject newEntry = new JSONObject();
        				newEntry.put("name", name);
        				newEntry.put("wins", entryWins);
        				newEntry.put("logins", entryLogins);
        				
        				entries.put(newEntry);
        				try {
                			FileWriter file = new FileWriter("txtFiles/leaderboard.json");
                			file.write(jsonObj.toString());
                			file.close();
                		} catch (IOException io) {
                			System.out.println("Couldn't write new JSON file.");
                		}
        				return;
        			}
        		}
        		
        		JSONObject newEntry = new JSONObject();
    			newEntry.put("name", name);
				newEntry.put("wins", win);
				newEntry.put("logins", login);
				entries.put(newEntry);
				try {
        			FileWriter file = new FileWriter("txtFiles/leaderboard.json");
        			file.write(jsonObj.toString());
        			file.close();
        		} catch (IOException io) {
        			System.out.println("Couldn't write new JSON file.");
        		}
        	} else {
        		System.out.println("Error: file does not exist!");
        	}
    	} catch (Exception e) {
        	System.out.println("JSON file could not be read!");
        	e.printStackTrace();
    	}
    }
    
    public static void leaderReaderJSON(OutputStream out) {
    	// Entries and Leader Response
    	Response.Builder leaderResp = Response.newBuilder()
          .setResponseType(Response.ResponseType.LEADER);
    	List<Entry> protoEntries = new ArrayList<Entry>();
    	
    	try {
        	String jsonDir = "txtFiles/leaderboard.json";
        	File jsonFile = new File(jsonDir);
        	if (jsonFile.exists()) {
        		InputStream inS = new FileInputStream(jsonDir);
        		JSONObject jsonObj = (JSONObject) new JSONTokener(inS).nextValue();
        		JSONArray entries = jsonObj.getJSONArray("entries");
        		
        		System.out.println("PRINTING JSON LEADERBOARD ENTRIES");
        		for (int i = 0; i < entries.length(); i++) {
        			JSONObject entry = entries.getJSONObject(i);
        			String entryName = entry.getString("name");
        			int entryWins = entry.getInt("wins");
        			int entryLogins = entry.getInt("logins");
        			protoEntries.add(entryBuilder(entryName, entryWins, entryLogins));
        		}
        		leaderResp.addAllLeader(protoEntries);
        		Response fullResponse = leaderResp.build();
        		fullResponse.writeDelimitedTo(out);
        	} else {
        		System.out.println("Couldn't find JSON leaderboard file! Making a new one...");
        		JSONObject jsonObj = new JSONObject();
        		JSONArray entries = new JSONArray();
        		jsonObj.put("entries", entries);
        		try {
        			FileWriter file = new FileWriter("txtFiles/leaderboard.json");
        			file.write(jsonObj.toString());
        			file.close();
        		} catch (IOException io) {
        			System.out.println("Couldn't write new JSON file.");
        		}
        	}
        } catch (Exception e) {
        	System.out.println("JSON file could not be read!");
        	e.printStackTrace();
        }
    }
    
    /**
     * Replaces num characters in the image. I used it to turn more than one x when the task is fulfilled
     * @param num -- number of x to be turned
     * @return String of the new hidden image
     */
    public String replace(int num, Game game){
        for (int i = 0; i < num; i++){
            if (game.getIdx()< game.getIdxMax())
                game.replaceOneCharacter();
        }
        return game.getImage();
    }
    
    /**
     * Writing a new entry to our log
     * @param name - Name of the person logging in
     * @param message - type Message from Protobuf which is the message to be written in the log (e.g. Connect) 
     * @return String of the new hidden image
     */
    public static void writeToLog(String name, Message message){
        try {
            // read old log file 
            Logs.Builder logs = readLogFile();

            // get current time and data
            Date date = java.util.Calendar.getInstance().getTime();

            // we are writing a new log entry to our log
            // add a new log entry to the log list of the Protobuf object
            logs.addLog(date.toString() + ": " +  name + " - " + message);

            // open log file
            FileOutputStream output = new FileOutputStream(logFilename);
            Logs logsObj = logs.build();

            // This is only to show how you can iterate through a Logs object which is a protobuf object
            // which has a repeated field "log"

            for (String log: logsObj.getLogList()){

                System.out.println(log);
            }

            // write to log file
            logsObj.writeTo(output);
        } catch (Exception e) {
            System.out.println("Issue while trying to save");
        }
    }
	
	private static class ServerRunnable implements Runnable {
		
		private List<String> taskQuestions = new ArrayList<String>();
		private List<String> taskAnswers = new ArrayList<String>();
		ServerSocket serv = null;
	    InputStream in = null;
	    OutputStream out = null;
	    Socket clientSocket = null;
	    int port = 9099; // default port
	    Game game;


	    public ServerRunnable(Socket sock, Game game) {
	        this.clientSocket = sock;
	        this.game = game;
	        try {
	            in = clientSocket.getInputStream();
	            out = clientSocket.getOutputStream();
	        } catch (Exception e){
	            System.out.println("Error in constructor: " + e);
	        }
	        
	        taskQuestions.add("What type is super effective against fire?");
	        taskAnswers.add("WATER");
	        
	        taskQuestions.add("What type is super effective against ghost?");
	        taskAnswers.add("DARK");
	        
	        taskQuestions.add("What move poisons your Pokemon?");
	        taskAnswers.add("TOXIC");
	        
	        taskQuestions.add("What Pokemon does Pikachu evolve to?");
	        taskAnswers.add("RAICHU");
	        
	        taskQuestions.add("What's the most effective Poke Ball in the game?");
	        taskAnswers.add("MASTER BALL");
	        
	        taskQuestions.add("How many Gym Badges must a trainer collect before challenging the Elite Four?");
	        taskAnswers.add("EIGHT");
	        
	        taskQuestions.add("What's the device trainers use to keep a record of their Pokemon encounters?");
	        taskAnswers.add("POKEDEX");
	        
	        taskQuestions.add("If you need to buy supplies in the Pokemon world, where do you go?");
	        taskAnswers.add("POKE MART");
	        
	        taskQuestions.add("If you need to revive your fainted Pokemon to full health, where do you go?");
	        taskAnswers.add("POKEMON CENTER");
	        
	        taskQuestions.add("What type of attacks are Normal type Pokemon immune to?");
	        taskAnswers.add("GHOST");
	        
	        taskQuestions.add("Who is your rival in the Pokemon 'Red' and 'Blue' games?");
	        taskAnswers.add("BLUE");
	    }
	    
	    @Override
		public void run() {
			String name = "";
			int randomNum = 0;
		    String gameQuestion = "";
		    String gameAnswer = "";
		    String userAnswer = "";
		    int revealSegments = 3; // number of segments needed to reveal the full image

	        System.out.println("Ready...");
	        try {
	        	while (true) {
		            Request clientReq = Request.parseDelimitedFrom(in); // read the proto object and put into new objct
		            Random randGenerator = new Random(); // Picks a random number to choose from
		            
		            switch (clientReq.getOperationType()) {
			            case NAME:
			            	name = clientReq.getName(); // get name from proto object
			            	System.out.println("Got a name request: " + name);
			            	
			            	// LEADERBOARD LOGGING
		            		leaderWriterJSON(name, 0, 1); // This user now has +1 login
			            	
			            	// writing a connect message to the log with name and CONNENCT
			            	// writeToLog(name, Message.CONNECT);
			                greetResponse(name, out);
			            	break;
			            	
			            case LEADER:
			            	System.out.println("Got a leaderboard request");
			            	leaderReaderJSON(out);
			            	break;
			            	
			            case NEW:
			            	System.out.println("Got a new game request");
			            	randomNum = randGenerator.nextInt(taskQuestions.size());
			            	gameQuestion = taskQuestions.get(randomNum);
			            	gameAnswer = taskAnswers.get(randomNum);
			            	game.newGame();
			            	
			            	System.out.println("Question sent: " + gameQuestion);
			            	System.out.println("Answer expected: " + gameAnswer);
			            	
			            	taskResponse(game, gameQuestion, out);
			            	break;
			            	
			            case ANSWER:
			            	System.out.println("Got an answer request");
			            	userAnswer = clientReq.getAnswer();
			            	
			            	// check if the game has been won
			            	if (game.getIdx() == game.getIdxMax() || game.getWon()) {
			            		game.setWon();
			            		wonResponse(out);
			            		
			            		// LEADERBOARD LOGGING
			            		leaderWriterJSON(name, 1, 0);
			            		break;
			            	}
			            	
			            	if (userAnswer.equalsIgnoreCase(gameAnswer)) {
			            		System.out.println("Answer request: Correct");
			            		
			            		// replace a certain number of X's
			            		for (int i = 0; i < (game.getIdxMax() / revealSegments); i++) {
			            			if (game.getIdxMax() > game.getIdx()) // replace only if its within bounds
			            				game.replaceOneCharacter();
			            		}
			            		
			            		// randomize and send new task
			            		randomNum = randGenerator.nextInt(taskQuestions.size());
			            		gameQuestion = taskQuestions.get(randomNum);
				            	gameAnswer = taskAnswers.get(randomNum);
				            	
			            		System.out.println("Question sent: " + gameQuestion);
				            	System.out.println("Answer expected: " + gameAnswer);
			            		taskResponse(game, "CORRECT!\n" + gameQuestion, out);
			            	} else {
			            		System.out.println("Answer request: Incorrect");
			            		
			            		// send the same task
			            		System.out.println("Question sent: " + gameQuestion);
				            	System.out.println("Answer expected: " + gameAnswer);
			            		taskResponse(game, "WRONG! Try Again.\n" + gameQuestion, out);
			            	}
			            	
			            	break;
			            	
			            case QUIT:
			            	System.out.println("Got a quit request");
			                byeResponse(out);
			            	break;
		            }
	        	}
	        } catch (Exception ex) {
	            System.out.println("Client disconnected: " + name);	
	            // ex.printStackTrace();
	        }
	        finally {
	        	try {
	        		if (out != null)  out.close();
		        	if (in != null)   in.close();
		        	if (clientSocket != null) clientSocket.close();
	        	} catch (Exception ex) {
	        		System.out.println("Error while closing client connection...");
	        	}
	        }
	    }
	}
	
	public static void greetResponse(String n, OutputStream out) throws IOException {
		Response rep = Response.newBuilder()
				.setResponseType(Response.ResponseType.GREETING)
				.setGreeting( "                                  ,'\\\n" + 
						"    _.----.        ____         ,'  _\\   ___    ___     ____\n" + 
						"_,-'       `.     |    |  /`.   \\,-'    |   \\  /   |   |    \\  |`.\n" + 
						"\\      __    \\    '-.  | /   `.  ___    |    \\/    |   '-.   \\ |  |\n" + 
						" \\.    \\ \\   |  __  |  |/    ,','_  `.  |          | __  |    \\|  |\n" + 
						"   \\    \\/   /,' _`.|      ,' / / / /   |          ,' _`.|     |  |\n" + 
						"    \\     ,-'/  /   \\    ,'   | \\/ / ,`.|         /  /   \\  |     |\n" + 
						"     \\    \\ |   \\_/  |   `-.  \\    `'  /|  |    ||   \\_/  | |\\    |\n" + 
						"      \\    \\ \\      /       `-.`.___,-' |  |\\  /| \\      /  | |   |\n" + 
						"       \\    \\ `.__,'|  |`-._    `|      |__| \\/ |  `.__,'|  | |   |\n" + 
						"        \\_.-'       |__|    `-._ |              '-.|     '-.| |   |\n" + 
						"                                `'                            '-._|\n" + 
						""
						+ "Hello " + n + "! Welcome to Pokemon Command Line."
						+ "\nWhat would you like to do?"
						+ "\n 1: Leaderboard \n 2: Enter A Game \n 3: Quit")
                .build();
		rep.writeDelimitedTo(out);
	}
	
	// TODO
	public static void leaderResponse(OutputStream out) throws IOException {
		Response rep = Response.newBuilder()
				.setResponseType(Response.ResponseType.LEADER)
				.build();
		rep.writeDelimitedTo(out);
	}
	
	public static Entry entryBuilder(String n, int win, int login) {
        return Entry.newBuilder()
        		.setName(n)
        		.setWins(win)
        		.setLogins(login)
        		.build();
	}
	
	public static void taskResponse(Game g, String t, OutputStream out) throws IOException {
		Response rep = Response.newBuilder()
				.setResponseType(Response.ResponseType.TASK)
				.setImage(g.getImage())
				.setTask(t)
				.build();
		rep.writeDelimitedTo(out);
	}
	
	public static void wonResponse(OutputStream out) throws IOException {
		Response rep = Response.newBuilder()
				.setResponseType(Response.ResponseType.WON)
				.build();
		rep.writeDelimitedTo(out);
	}
	
	// TODO
	public static void errorResponse(OutputStream out) throws IOException {
		Response rep = Response.newBuilder()
				.setResponseType(Response.ResponseType.ERROR)
				.build();
		rep.writeDelimitedTo(out);
	}
	
	public static void byeResponse(OutputStream out) throws IOException {
		Response rep = Response.newBuilder()
				.setResponseType(Response.ResponseType.BYE)
				.build();
		rep.writeDelimitedTo(out);
	}
    
    // Handles the communication right now it just accepts one input and then is done you should make sure the server stays open
    // can handle multiple requests and does not crash when the server crashes
    // you can use this server as based or start a new one if you prefer. 
//    public void start() throws IOException {
//        String name = "";
//
//
//        System.out.println("Ready...");
//        try {
//            // read the proto object and put into new objct
//            Request op = Request.parseDelimitedFrom(in);
//            String result = null;
//            
//            // if the operation is NAME (so the beginning then say there is a commention and greet the client)
//            if (op.getOperationType() == Request.OperationType.NAME) {
//            	// get name from proto object
//            	name = op.getName();
//            	
//            	// writing a connect message to the log with name and CONNENCT
//            	writeToLog(name, Message.CONNECT);
//                System.out.println("Got a connection and a name: " + name);
//                Response response = Response.newBuilder()
//                        .setResponseType(Response.ResponseType.GREETING)
//                        .setGreeting("Hello " + name + " and welcome. \nWhat would you like to do? \n 1: Leaderboard \n 2: Enter A Game \n 3: Quit")
//                        .build();
//                response.writeDelimitedTo(out);
//            }

            // Example how to start a new game and how to build a response with the image which you could then send to the server
            // LINE 67-108 are just an example for Protobuf and how to work with the differnt types. They DO NOT
            // belong into this code. 
//            game.newGame(); // starting a new game
//
//            // adding the String of the game to 
//            Response response2 = Response.newBuilder()
//                .setResponseType(Response.ResponseType.TASK)
//                .setImage(game.getImage())
//                .setTask("Great task goes here")
//                .build();
//
//            // On the client side you would receive a Response object which is the same as the one in line 70, so now you could read the fields
//            System.out.println("Task: " + response2.getResponseType());
//            System.out.println("Image: \n" + response2.getImage());
//            System.out.println("Task: \n" + response2.getTask());
//
//            // Creating Entry and Leader response
//            Response.Builder res = Response.newBuilder()
//                .setResponseType(Response.ResponseType.LEADER);
//
//            // building and Entry
//            Entry leader = Entry.newBuilder()
//                .setName("name")
//                .setWins(0)
//                .setLogins(0)
//                .build();
//
//            // building and Entry
//            Entry leader2 = Entry.newBuilder()
//                .setName("name2")
//                .setWins(1)
//                .setLogins(1)
//                .build();
//
//            res.addLeader(leader);
//            res.addLeader(leader2);
//
//            Response response3 = res.build();
//
//            for (Entry lead: response3.getLeaderList()){
//                System.out.println(lead.getName() + ": " + lead.getWins());
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//        	if (out != null)  out.close();
//        	if (in != null)   in.close();
//        	if (clientSocket != null) clientSocket.close();
//        }
//    }
}

