/**
  File: Performer.java
  Author: Student in Fall 2020B
  Description: Performer class in package taskone.
*/

package taskone;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 * Class: Performer 
 * Description: Threaded Performer for server tasks.
 */
public class Performer {

    private StringList state;
    private Socket conn;

    public Performer(Socket sock, StringList strings) {
        this.conn = sock;
        this.state = strings;
    }
    
    /**
     * add <string> - adds a string to the list (presently what it does by default now) and
     * displays the list (strings will be added to the end) – already implemented
     * 
     * @param str
     * @return
     */
    public JSONObject add(String str) {
        JSONObject json = new JSONObject();
        
        json.put("datatype", 1);
        json.put("type", "add");
        state.add(str);
        json.put("data", state.toString());
        
        System.out.println("JSON to send: " + json);
        return json;
    }
    
    /**
     * pop - removes the top element of the list and displays it. If the list is empty return "null"
     * 
     * @return
     */
    public JSONObject pop() {
    	JSONObject json = new JSONObject();
    	
        json.put("datatype", 2);
        json.put("type", "pop");
        if (this.state.strings.isEmpty()) {
        	json.put("data", "null");
    	} else {
    		json.put("data", this.state.strings.get(0));
    		this.state.strings.remove(0);
    	}
        System.out.println("JSON to send: " + json);
    	return json;
    }
    
    /**
     * display - displays the current list
     */
    public JSONObject display() {
    	JSONObject json = new JSONObject();
    	
    	json.put("datatype", 3);
        json.put("type", "display");
        json.put("data", this.state.toString());
    	
        System.out.println("JSON to send: " + json);
        return json;
    }
    
    /**
     * count - returns the number of elements in your list and displays the number
     */
    public JSONObject count() {
    	JSONObject json = new JSONObject();
    	
    	json.put("datatype", 4);
        json.put("type", "count");
        json.put("data", String.valueOf(this.state.size()));
    	
        System.out.println("JSON to send: " + json);
    	return json;
    }
    
    /**
     * switch <int int> - switch the elements at the given indexes. If one of the indexes is
	 * invalid return "null" (list does not change)
     */
    public JSONObject switching(int index1, int index2) {
    	JSONObject json = new JSONObject();
    	json.put("datatype", 5);
        json.put("type", "switch");
    	
    	try {
    	    this.state.strings.get(index1);
    	    this.state.strings.get(index2);
    	} catch ( IndexOutOfBoundsException e ) {
            json.put("data", "null: invalid index.");
            System.out.println("JSON to send: " + json);
    		return json;
    	}
    	
    	String temp1 = this.state.strings.get(index1);
    	String temp2 = this.state.strings.get(index2);
    	
    	this.state.strings.set(index1, temp2);
    	this.state.strings.set(index2, temp1);
    	
    	
        json.put("data", "items successfully switched.");
    	
        System.out.println("JSON to send: " + json);
        return json;
    }
    
    /**
     * quit - quits
     */
    public JSONObject quit() {
    	JSONObject json = new JSONObject();
    	
    	json.put("datatype", 0);
        json.put("type", "quit");
        json.put("data", "bye!");
    	
        System.out.println("JSON to send: " + json);
        return json;
    }
    
    public static JSONObject error(String err) {
        JSONObject json = new JSONObject();
        json.put("error", err);
        return json;
    }

    public void doPerform() {
        boolean quit = false;
        OutputStream out = null;
        InputStream in = null;
        try {
            out = conn.getOutputStream();
            in = conn.getInputStream();
            System.out.println("Server connected to client:");
            while (!quit) {
                byte[] messageBytes = NetworkUtils.receive(in);
                JSONObject message = JsonUtils.fromByteArray(messageBytes);
                JSONObject returnMessage = new JSONObject();
   
                int choice = message.getInt("selected");
                    switch (choice) {
                    	case (0):
                    		System.out.println("Client would like to quit.");
                    		returnMessage = quit();
                    		break;
                        case (1):
                        	System.out.println("Client would like to add.");
                            String inStr = (String) message.get("data");
                            returnMessage = add(inStr);
                            break;
                        case (2):
                        	System.out.println("Client would like to pop.");
                        	returnMessage = pop();
                        	break;
                        case (3):
                        	System.out.println("Client would like to display.");
                        	returnMessage = display();
                        	break;
                        case (4):
                        	System.out.println("Client would like to count.");
                        	returnMessage = count();
                        	break;
                        case (5):
                        	System.out.println("Client would like to switch.");
                        	String dataStr = (String) message.get("data");
                        	String[] dataParams = dataStr.split("\\s+");
                        	try {
                        		returnMessage = switching(Integer.parseInt(dataParams[0]),Integer.parseInt(dataParams[1]));
                        	} catch (NumberFormatException ne) {
                        		returnMessage = error("Invalid: not an int.");
                        	}
                        	break;
                        default:
                            returnMessage = error("Invalid selection: " + choice 
                                    + " is not an option");
                            break;
                    }
                // we are converting the JSON object we have to a byte[]
                byte[] output = JsonUtils.toByteArray(returnMessage);
                NetworkUtils.send(out, output);
            }
            // close the resource
            System.out.println("close the resources of client ");
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
